package com.starworks.kronos.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.StampedLock;

public final class FileHandle implements AutoCloseable {

	private final File m_file;
	private final FileInputStream m_fis;
	private final FileOutputStream m_fos;
	private final FileChannel m_fileChannel;
	private FileLock m_fileLock;
	private final String m_fileDirectory;
	private final String m_fileName;
	private final String m_fileExtension;
	private boolean m_closed;
	private final StampedLock m_lock;
	private boolean m_generated;
	final AtomicInteger m_consumers;

	FileHandle(String fileName) throws IOException {
		this.m_file = new File(fileName);
		String parent = m_file.getParent();
		if (parent == null) {
			parent = "";
		}
		File directory = new File(parent);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		if (m_generated = !m_file.exists()) {
			m_file.createNewFile();
		}
		this.m_fis = new FileInputStream(m_file);
		this.m_fos = new FileOutputStream(m_file, true);
		this.m_fileDirectory = parent.replace(File.separator, FileSystem.separator) + FileSystem.separator;
		this.m_fileName = m_file.getName().substring(0, m_file.getName().lastIndexOf("."));
		int lastIndex = fileName.lastIndexOf(".");
		this.m_fileExtension = (lastIndex == -1) ? "" : fileName.substring(lastIndex);
		this.m_fileChannel = m_fos.getChannel();
		this.m_fileLock = null;
		this.m_closed = false;
		this.m_lock = new StampedLock();
		this.m_consumers = new AtomicInteger(0);
	}

	public void write(String message) throws IOException {
		long stamp = m_lock.writeLock();
		try {
			try {
				m_fileLock = m_fileChannel.lock();
				m_fos.write(message.getBytes());
				m_fos.flush();
			} finally {
				if (m_fileLock != null) {
					m_fileLock.release();
				}
			}
		} finally {
			m_lock.unlockWrite(stamp);
		}
	}

	public void write(FileHandle handle) throws IOException {
		long stamp = m_lock.writeLock();
		try {
			try {
				m_fileLock = m_fileChannel.lock();
				m_fos.write(handle.readContents());
				m_fos.flush();
			} finally {
				if (m_fileLock != null) {
					m_fileLock.release();
				}
			}
		} finally {
			m_lock.unlockWrite(stamp);
		}
	}

	private byte[] readContents() throws IOException {
		byte[] data = new byte[(int) m_file.length()];
		m_fis.read(data);
		m_fis.close();
		return data;
	}

	public void clearContents() throws IOException {
		long stamp = m_lock.writeLock();
		try {
			FileOutputStream temp = new FileOutputStream(m_file);
			FileLock lock = temp.getChannel().lock();
			if (lock != null) {
				lock.release();
				temp.close();
			}
		} finally {
			m_lock.unlock(stamp);
		}
	}

	public void delete() throws IOException {
		m_consumers.set(0);
		m_fos.close();
		m_file.delete();
	}

	public void close() throws IOException {
		long stamp = m_lock.writeLock();
		try {
			if (m_consumers.getAndDecrement() == 0) {
				m_fos.close();
				m_closed = true;
			}
		} finally {
			m_lock.unlock(stamp);
		}
	}

	void shutdown() throws IOException {
		m_consumers.set(0);
		m_fos.close();
		m_closed = true;
	}
	
	public File getFile() {
		return m_file;
	}

	public String getPath() {
		StringBuilder sb = new StringBuilder(m_fileDirectory);
		sb.append(m_fileName);
		sb.append(m_fileExtension);
		return sb.toString();
	}

	public String getFileDirectory() {
		return m_fileDirectory;
	}

	public String getFileName() {
		return m_fileName;
	}

	public String getFileExtension() {
		return m_fileExtension;
	}

	public boolean isClosed() {
		return m_closed;
	}
	
	public boolean wasGenerated() {
		return m_generated;
	}
}
