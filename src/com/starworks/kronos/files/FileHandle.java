package com.starworks.kronos.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.concurrent.locks.StampedLock;

public final class FileHandle {

	private final File m_file;
	private FileInputStream m_fis;
	private FileOutputStream m_fos;
	private FileChannel m_fileChannel;
	private FileLock m_fileLock;
	private final String m_fileDirectory;
	private final String m_fileName;
	private final String m_fileExtension;
	private boolean m_closed;
	private final StampedLock m_lock;
	private boolean m_generated;

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
		this.m_fileDirectory = parent.replace(File.separator, FileSystem.separator) + FileSystem.separator;
		this.m_fileName = m_file.getName().substring(0, m_file.getName().lastIndexOf("."));
		int lastIndex = fileName.lastIndexOf(".");
		this.m_fileExtension = (lastIndex == -1) ? "" : fileName.substring(lastIndex);
		this.m_fileLock = null;
		this.m_closed = false;
		this.m_lock = new StampedLock();
	}

	public void write(String message) throws IOException {
		long stamp = m_lock.writeLock();
		try {
			m_fos = new FileOutputStream(m_file, true);
			m_fileChannel = m_fos.getChannel();
			try {
				m_fileLock = m_fileChannel.lock();
				m_fos.write(message.getBytes());
				m_fos.flush();
			} finally {
				if (m_fileLock != null) {
					m_fileLock.release();
				}
				m_fos.close();
			}
		} finally {
			m_lock.unlockWrite(stamp);
		}
	}

	public void write(FileHandle handle) throws IOException {
		long stamp = m_lock.writeLock();
		try {
			m_fos = new FileOutputStream(m_file, true);
			m_fileChannel = m_fos.getChannel();
			try {
				m_fileLock = m_fileChannel.lock();
				m_fos.write(handle.readContents());
				m_fos.flush();
			} finally {
				if (m_fileLock != null) {
					m_fileLock.release();
				}
				m_fos.close();
			}
		} finally {
			m_lock.unlockWrite(stamp);
		}
	}

	private byte[] readContents() throws IOException {
		byte[] data = new byte[(int) m_file.length()];
		m_fis = new FileInputStream(m_file);
		m_fis.read(data);
		m_fis.close();
		return data;
	}

	public void clearContents() throws IOException {
		long stamp = m_lock.writeLock();
		try {
			m_fos = new FileOutputStream(m_file);
			m_fos.close();
		} finally {
			m_lock.unlock(stamp);
		}
	}

	public void delete() throws IOException {
		m_file.delete();
	}

	void shutdown() throws IOException {
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
