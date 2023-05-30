package com.starworks.kronos.resources;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.locks.StampedLock;

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
	private final Map<String, String> etagCache;
	private final Map<String, String> lastModifiedCache;
	private final StampedLock m_lock;

	private ResourceManager() {
		this.m_cache = new WeakHashMap<String, byte[]>();
		this.etagCache = new HashMap<String, String>();
		this.lastModifiedCache = new HashMap<String, String>();
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

	            long writeStamp = m_lock.tryConvertToWriteLock(stamp);
	            if (writeStamp != 0L) {
	                stamp = writeStamp;
	                return fetchAndCacheWebResource(directoryURL, resourceURL, stamp);
	            } else {
	                m_lock.unlockRead(stamp);
	                stamp = m_lock.writeLock();
	                try {
	                    if (m_cache.containsKey(resourceURL)) {
	                        byte[] cached = m_cache.get(resourceURL);
	                        if (cached != null) {
	                            return new ByteArrayInputStream(cached);
	                        }
	                    }
	                    return fetchAndCacheWebResource(directoryURL, resourceURL, stamp);
	                } finally {
	                    m_lock.unlockWrite(stamp);
	                }
	            }
	        } finally {
	            if (m_lock.validate(stamp)) {
	                m_lock.unlockRead(stamp);
	            }
	        }
	    });
	}

	private InputStream fetchAndCacheWebResource(String directoryURL, String resourceURL, long stamp) {
		try {
            URL url = new URL(fetchWebResourceURL(resourceURL));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            if (etagCache.containsKey(resourceURL)) {
                connection.setRequestProperty("If-None-Match", etagCache.get(resourceURL));
            }
            if (lastModifiedCache.containsKey(resourceURL)) {
                connection.setRequestProperty("If-Modified-Since", lastModifiedCache.get(resourceURL));
            }

            String localDirectory = getLocalResourceDirectory();
            File file = new File(localDirectory + resourceURL);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
            }

            int responseCode = connection.getResponseCode();

            // If the server responds with 304 Not Modified, use the cached resource
            if (responseCode == HttpURLConnection.HTTP_NOT_MODIFIED) {
                LOGGER.info("Resource '{0}{1}' not modified, using cached version", directoryURL, resourceURL);
                byte[] cachedResource = m_cache.get(resourceURL);
                return new ByteArrayInputStream(cachedResource);
            }

            byte[] buffer = new byte[1024];
            int bytesRead;
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

            try (var input = connection.getInputStream(); var output = new FileOutputStream(localDirectory + resourceURL)) {
                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                    byteStream.write(buffer, 0, bytesRead);
                }
            }

            String etag = connection.getHeaderField("ETag");
            String lastModified = connection.getHeaderField("Last-Modified");

            // If the server doesn't support ETag or Last-Modified, cache the resource unconditionally
            if (etag == null && lastModified == null) {
                LOGGER.warn("Resource '{0}{1}' does not support ETag or Last-Modified, caching unconditionally", directoryURL, resourceURL);
            } else {
                etagCache.put(resourceURL, etag);
                lastModifiedCache.put(resourceURL, lastModified);
            }

            LOGGER.info("Downloading '{0}{1}'", directoryURL, resourceURL);

            byte[] resource = byteStream.toByteArray();

            m_cache.put(resourceURL, resource);

            return new ByteArrayInputStream(resource);
		} catch (IOException e) {
	        LOGGER.error("Unable to download resource '{0}{1}", directoryURL, resourceURL);
	        throw new CompletionException(e);
	    } finally {
	        m_lock.unlockWrite(stamp);
	    }
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
