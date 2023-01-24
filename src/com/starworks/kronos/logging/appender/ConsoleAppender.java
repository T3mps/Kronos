package com.starworks.kronos.logging.appender;

import java.io.IOException;
import java.io.OutputStream;

import com.starworks.kronos.toolkit.Ansi;

public final class ConsoleAppender implements Appender {

	private final OutputStream m_stream;

	private String m_reusableString;

	public ConsoleAppender() {
		this.m_stream = new SystemOutStream();
		this.m_reusableString = null;
	}

	@Override
	public void append(Message message) throws IOException {
		m_reusableString = Ansi.colorize(message.msg(), message.ansiTraits());
		m_stream.write(m_reusableString.getBytes());
	}

	@Override
	public void close() throws IOException {
		m_stream.flush();
		m_stream.close();
	}

	private static class SystemOutStream extends OutputStream {

		public SystemOutStream() {}

		@Override
		public void write(final int b) throws IOException {
			System.out.write(b);
		}

		@Override
		public void write(final byte[] b) throws IOException {
			System.out.write(b);
		}

		@Override
		public void write(final byte[] b, final int off, final int len) throws IOException {
			System.out.write(b, off, len);
		}

		@Override
		public void flush() {
			System.out.flush();
		}

		@Override
		public void close() {
			// do not close System.out
		}
	}
}
