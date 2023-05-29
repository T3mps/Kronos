package com.starworks.kronos.locale;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.StampedLock;

import com.starworks.kronos.exception.KronosRuntimeException;
import com.starworks.kronos.logging.Logger;

public enum StringTable {
	INSTANCE;

	private static final String EMPTY_STRING = "";
	private static final String PROPERTIES_EXT = ".properties";
	
	private final Logger LOGGER = Logger.getLogger(StringTable.class);
	private final ConcurrentMap<String, ConcurrentMap<String, String>> m_stringTables;
	private final ConcurrentMap<String, Path> m_tablePaths;
	private final WatchService m_watcher;
	private final Thread m_watchThread;
	private final Path m_dirPath;
	private final StampedLock m_lock;
	private Locale m_currentLocale;
	private volatile boolean m_isRunning;

	private StringTable() {
		this.m_dirPath = Paths.get("data/locale/");
		this.m_currentLocale = Locale.getDefault();

		if (!Files.exists(m_dirPath)) {
			try {
				Files.createDirectories(m_dirPath);
			} catch (IOException e) {
				LOGGER.error("Unsuccessfully attempted to generate data folder");
			}
		}

		this.m_stringTables = new ConcurrentHashMap<String, ConcurrentMap<String, String>>();
		this.m_tablePaths = new ConcurrentHashMap<String, Path>();
		this.m_isRunning = true;
		this.m_lock = new StampedLock();

		try {
			loadTables(m_dirPath);
			this.m_watcher = FileSystems.getDefault().newWatchService();
			registerDirectories(m_dirPath);
			this.m_watchThread = new Thread(this::startWatching);
			this.m_watchThread.start();
		} catch (IOException e) {
			e.printStackTrace();
			throw new KronosRuntimeException(e);
		}
	}

	private void registerDirectories(final Path start) throws IOException {
		Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				dir.register(m_watcher, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_CREATE);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	private void startWatching() {
		while (m_isRunning) {
			WatchKey key;
			try {
				key = m_watcher.take();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return;
			}

			for (var event : key.pollEvents()) {
				WatchEvent.Kind<?> kind = event.kind();
				Path eventPath = ((Path) key.watchable()).resolve((Path) event.context());

				if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
					if (eventPath.toString().endsWith(getSuffix())) {
		                String tableName = eventPath.getFileName().toString().replace(getSuffix(), EMPTY_STRING);
		                m_stringTables.remove(tableName);
		                m_tablePaths.remove(tableName);
		                loadTablesFromPath(eventPath);
		            }
				} else if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
					if (Files.isDirectory(eventPath)) {
						try {
							registerDirectories(eventPath);
						} catch (IOException e) {
							LOGGER.error("Error registering new directory for watching", e);
						}
					}
				}
			}

			boolean valid = key.reset();
			if (!valid) {
				break;
			}
		}
	}

	public void stopWatching() {
		m_isRunning = false;
		m_watchThread.interrupt();
		try {
			m_watcher.close();
		} catch (IOException e) {
			LOGGER.error("Error closing file watcher", e);
		}
	}

	public void addString(String table, String key, String value) {
		var stringTable = m_stringTables.computeIfAbsent(table, k -> new ConcurrentHashMap<String, String>());
		stringTable.put(key, value);
		savePropertiesToFile(table, stringTable);
	}

	public String getString(String table, String key) {
	    var stringTable = m_stringTables.getOrDefault(table, null);
	    if (stringTable == null) return EMPTY_STRING;
	    return stringTable.getOrDefault(key, EMPTY_STRING);
	}

	public void removeString(String table, String key) {
		var stringTable = m_stringTables.get(table);
		if (stringTable != null) {
			stringTable.remove(key);
		}
	}

	public void modifyString(String table, String key, String newValue) {
		var stringTable = m_stringTables.get(table);
		if (stringTable == null) {
			return;
		}
		stringTable.put(key, newValue);
		savePropertiesToFile(table, stringTable);
	}

	public void changeLocale(Locale newLocale) {
		long stamp = m_lock.writeLock();
		try {
			m_stringTables.clear();
			this.m_currentLocale = newLocale;
			try {
				loadTables(m_dirPath);
			} catch (IOException e) {
				LOGGER.error("Error while changing locale", e);
			}
		} finally {
			m_lock.unlockWrite(stamp);
		}
	}

	private void savePropertiesToFile(String table, ConcurrentMap<String, String> stringTable) {
	    Properties prop = new Properties();

	    for (var entry : stringTable.entrySet()) {
	        prop.setProperty(entry.getKey(), entry.getValue());
	    }

	    Path filePath = m_tablePaths.getOrDefault(table, buildTableFilePath(table));
	    try (OutputStream output = new FileOutputStream(filePath.toFile())) {
	        prop.store(output, null);
	    } catch (IOException ex) {
	        LOGGER.error("Error saving properties to file", ex);
	    }
	}
	
	private void loadTables(Path dir) throws IOException {
		Files.walk(dir).filter(Files::isRegularFile)
					   .filter(p -> p.toString().endsWith(getSuffix()))
					   .forEach(this::loadTablesFromPath);
	}

	private void loadTablesFromPath(Path path) {
		Path normalizedPath = path.normalize();
	    if (!normalizedPath.startsWith(m_dirPath)) {
	        LOGGER.warn("Invalid file access attempt: " + normalizedPath);
	        return;
	    }
		try (InputStream input = new FileInputStream(path.toFile())) {
			Properties prop = new Properties();
			prop.load(input);

			ConcurrentMap<String, String> table = new ConcurrentHashMap<String, String>();
			for (var key : prop.stringPropertyNames()) {
				table.put(key, prop.getProperty(key));
			}

			String tableName = path.getFileName().toString().replace(getSuffix(), EMPTY_STRING);
		    m_stringTables.put(tableName, table);
		    m_tablePaths.put(tableName, path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Path buildTableFilePath(String table) {
	    String fileName = table + getSuffix();
	    return m_dirPath.resolve(fileName);
	}
	
	public void shutdown() {
		stopWatching();
	}

	private String getSuffix() {
		StringBuilder sb = new StringBuilder();
		sb.append('_');
		sb.append(m_currentLocale.toString());
		sb.append(PROPERTIES_EXT);
		return sb.toString();
	}

	public Map<String, String> getTable(String name) {
		return Collections.unmodifiableMap(m_stringTables.get(name));
	}

	public Set<String> getTables() {
		return Collections.unmodifiableSet(m_stringTables.keySet());
	}

	public Set<String> getKeys(String table) {
		ConcurrentMap<String, String> stringTable = m_stringTables.get(table);
		if (stringTable != null) {
			return Collections.unmodifiableSet(stringTable.keySet());
		}
		return Collections.emptySet();
	}
}
