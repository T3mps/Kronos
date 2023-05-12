package com.starworks.kronos.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.concurrent.locks.StampedLock;

public final class FileHandle {

	private final File m_file;
	private final URI m_fileURI;
	private final String m_fileDirectory;
	private final String m_fileName;
	private final String m_fileExtension;
	private boolean m_closed;
	private final StampedLock m_lock;
	private boolean m_generated;

	FileHandle(String fileName, boolean generateIfNotExist) throws IOException {
		this.m_file = new File(fileName);
		this.m_fileURI = m_file.toURI();
		if (generateIfNotExist) {
			try {
				this.m_generated = m_file.createNewFile();
			} catch (IOException e) {
				this.m_generated = false;
			}
		}
		String parent = m_file.getParent();
		this.m_fileDirectory = (parent != null ? parent.replace(File.separator, FileSystem.separator) + FileSystem.separator : "");
		int lastIndex = m_file.getName().lastIndexOf(".");
		if (lastIndex == -1) {
			this.m_fileName = m_file.getName();
			this.m_fileExtension = "";
		} else {
			this.m_fileName = m_file.getName().substring(0, lastIndex);
			this.m_fileExtension = m_file.getName().substring(lastIndex);
		}
		this.m_closed = false;
		this.m_lock = new StampedLock();
	}

	public void write(String message) throws IOException {
		long stamp = m_lock.writeLock();
		try {
			try (FileOutputStream fos = new FileOutputStream(m_file, true)) {
				fos.write(message.getBytes());
				fos.flush();
			}
		} finally {
			m_lock.unlockWrite(stamp);
		}
	}

	public void write(FileHandle handle) throws IOException {
		long stamp = m_lock.writeLock();
		try {
			try (FileOutputStream fos = new FileOutputStream(m_file, true)) {
				fos.write(handle.readContents());
				fos.flush();
			}
		} finally {
			m_lock.unlockWrite(stamp);
		}
	}

	private byte[] readContents() throws IOException {
		long stamp = m_lock.readLock();
		try {
			try (FileInputStream fis = new FileInputStream(m_file)) {
				byte[] data = new byte[(int) m_file.length()];
				fis.read(data);
				return data;
			}
		} finally {
			m_lock.unlockRead(stamp);
		}
	}

	public OutputStream writeStream(boolean append) throws FileNotFoundException {
		if (m_closed) {
			throw new IllegalStateException("FileHandle is closed");
		}
		return new FileOutputStream(m_file, append);
	}

	public InputStream readStream() throws FileNotFoundException {
		if (m_closed) {
			throw new IllegalStateException("FileHandle is closed");
		}
		long stamp = m_lock.readLock();
		try {
			return new FileInputStream(m_file);
		} finally {
			m_lock.unlockRead(stamp);
		}
	}

	public void clearContents() throws IOException {
		long stamp = m_lock.writeLock();
		try {
			try (FileOutputStream fos = new FileOutputStream(m_file)) {
				// do nothing here, just overwriting the file with an empty one
			}
		} finally {
			m_lock.unlock(stamp);
		}
	}

	public void delete() throws IOException {
		if (!m_file.delete()) {
			throw new IOException("File '" + m_fileName + m_fileExtension + "' failed to delete");
		}
	}

	void shutdown() throws IOException {
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

	public URI getFileURI() {
		return m_fileURI;
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
