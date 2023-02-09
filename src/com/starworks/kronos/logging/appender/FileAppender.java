package com.starworks.kronos.logging.appender;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicLong;

import com.starworks.kronos.Configuration;
import com.starworks.kronos.exception.Exceptions;
import com.starworks.kronos.exception.KronosRuntimeException;
import com.starworks.kronos.files.FileHandle;
import com.starworks.kronos.files.FileSystem;

public sealed class FileAppender implements Appender permits RotatingFileAppender {

	protected static final String s_newLine = System.lineSeparator();
	protected static final byte[] s_newLineBytes = s_newLine.getBytes();

	protected FileHandle m_handle;
	protected AtomicLong m_lines;

	private String m_reusableString;

	public FileAppender() {
	}

	public FileAppender(String path) {
		try {
			String p = path + (path.endsWith(Configuration.logging.extension()) ? "" : Configuration.logging.extension());
			this.m_handle = FileSystem.getFileHandle(p);
			this.m_lines = new AtomicLong(lines(p));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected final long lines(String filename) {
		Path path = Paths.get(filename);
		try {
			return Files.lines(path).count();
		} catch (IOException e) {
			e.printStackTrace();
		}
		throw new KronosRuntimeException(Exceptions.getMessage("logging.appender.fileAppender.lines"));
	}

	@Override
	public void append(Message message) throws IOException {
		m_reusableString = message.msg();
		m_handle.write(m_reusableString);
		m_lines.addAndGet(m_reusableString.split(s_newLine).length);
	}

	@Override
	public void close() throws IOException {
		m_handle.close();
	}

	public FileHandle getFileHandle() {
		return m_handle;
	}

	public long lines() {
		return m_lines.get();
	}
}
