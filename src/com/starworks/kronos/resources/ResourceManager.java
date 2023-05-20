package com.starworks.kronos.resources;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.locks.StampedLock;

import javax.net.ssl.HttpsURLConnection;

import com.starworks.kronos.files.FileSystem;
import com.starworks.kronos.logging.Logger;

/*
 * Resource URL is expected to be resources/[category]/[sub_directories*]/[resource_name]. This way
 * this will directly line up to the local resources directory.
 */
public enum ResourceManager {

	INSTANCE;

	private final Logger LOGGER = Logger.getLogger(ResourceManager.class);

	private static final String WEB_DIRECTORY = "https://kronosengine.com/resources/";
	private static final String WORKING_DIRECTORY_EXT = "resources/";

	private final Map<String, byte[]> m_cache;
	private final StampedLock m_lock;

	private ResourceManager() {
		this.m_cache = new WeakHashMap<String, byte[]>();
		this.m_lock = new StampedLock();
	}

	/*
	 * Validates the local file system and ensures required root folders exist
	 */
	public void validate() {
		String workingDirectory = FileSystem.INSTANCE.getWorkingDirectory();
		
		// data folder
		File file = new File(workingDirectory + "data/");
		if (!file.exists()) {
			file.mkdir();
		}
	}
	
	public void load(ResourceLoader... loaders) {
		for (var loader : loaders) {
			loader.load();
		}
	}

	public CompletableFuture<InputStream> fetchWebResource(String resourcePath) {
		return fetchWebResource(WEB_DIRECTORY, resourcePath);
	}

	public CompletableFuture<InputStream> fetchWebResource(String directoryURL, String resourceURL) {
		return CompletableFuture.supplyAsync(() -> {
			long stamp = m_lock.readLock();
			try {
				if (m_cache.containsKey(resourceURL)) {
					byte[] cached = m_cache.get(resourceURL);
					if (cached != null) {
						return new ByteArrayInputStream(cached);
					}
				}
			} finally {
				m_lock.unlockRead(stamp);
			}

			try {
				URL url = new URL(fetchWebResourceURL(resourceURL));
				HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
				String localDirectory = getLocalResourceDirectory();

				File file = new File(localDirectory + resourceURL);
				if (!file.exists()) {
					file.getParentFile().mkdirs();
				}
				byte[] buffer = new byte[1024];
				int bytesRead;
				ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

				try (InputStream input = connection.getInputStream(); FileOutputStream output = new FileOutputStream(localDirectory + resourceURL)) {
					while ((bytesRead = input.read(buffer)) != -1) {
						output.write(buffer, 0, bytesRead);
						byteStream.write(buffer, 0, bytesRead);
					}
				}
				LOGGER.info("Downloading '{0}{1}'", directoryURL, resourceURL);

				byte[] resource = byteStream.toByteArray();

				stamp = m_lock.writeLock();
				try {
					m_cache.put(resourceURL, resource);
				} finally {
					m_lock.unlockWrite(stamp);
				}

				return new ByteArrayInputStream(resource);
			} catch (IOException e) {
				LOGGER.error("Unable to download resource '{0}{1}", directoryURL, resourceURL);
				throw new CompletionException(e);
			}
		});
	}

	public String fetchLocalResourcePath(String resourcePath) {
		StringBuilder sb = new StringBuilder(getLocalResourceDirectory());
		sb.append(resourcePath);
		return sb.toString();
	}

	public String fetchWebResourceURL(String resourcePath) {
		StringBuilder sb = new StringBuilder(WEB_DIRECTORY);
		sb.append(resourcePath);
		return sb.toString();
	}

	public String getLocalResourceDirectory() {
		StringBuilder sb = new StringBuilder();
		sb.append(FileSystem.INSTANCE.getWorkingDirectory());
		sb.append(WORKING_DIRECTORY_EXT);
		return sb.toString();
	}

	public String getWebResourceDirectory() {
		return WEB_DIRECTORY;
	}
}
