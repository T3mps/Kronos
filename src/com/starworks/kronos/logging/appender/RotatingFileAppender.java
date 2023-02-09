package com.starworks.kronos.logging.appender;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.starworks.kronos.Configuration;
import com.starworks.kronos.files.FileSystem;

public sealed class RotatingFileAppender extends FileAppender permits RotatingDateFileAppender {

	private final String SEPARATOR = "_";
	
	protected String m_baseName;
	protected String m_extension;
	protected int m_maxLines;
	protected AtomicInteger m_currentFileCount;

	public RotatingFileAppender(String path, int maxLines) {
		this.m_maxLines = maxLines;
		int idx = path.lastIndexOf('.');
		if (idx == -1) {
			this.m_baseName = path;
			this.m_extension = Configuration.logging.extension();
		} else {
			this.m_baseName = path.substring(0, idx);
			this.m_extension = path.substring(idx);
		}
		this.m_currentFileCount = new AtomicInteger(countFiles());
		try {
			String p = buildName();
			this.m_handle = FileSystem.getFileHandle(p);
			this.m_lines = new AtomicLong(lines(p));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private final int countFiles() {
		int count = 0;
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			String path = m_baseName + i + m_extension;
			if (new File(path).exists()) {
				count = i;
			} else {
				break;
			}
		}
		return count;
	}

	@Override
	public void append(Message message) throws IOException {
		if (m_lines.get() >= m_maxLines) {
			m_currentFileCount.incrementAndGet();
			rotate();
		}
		super.append(message);
	}

	protected void rotate() throws IOException {
		m_handle.close();
		String path = buildName();
		m_handle = FileSystem.getFileHandle(path);
		m_lines.set(lines(path));
		;
	}
	
	protected final String buildName() {
		StringBuilder sb = new StringBuilder(m_baseName);
		sb.append(SEPARATOR);
		sb.append(m_currentFileCount);
		sb.append(m_extension);
		return sb.toString();
	}
}
