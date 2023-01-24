package com.starworks.kronos.logging.appender;

import java.io.Closeable;
import java.io.IOException;

import com.starworks.kronos.toolkit.Ansi.AnsiTrait;

public interface Appender extends Closeable {

    void append(Message message) throws IOException;

	void close() throws IOException;
	
	public static record Message(String msg, AnsiTrait... ansiTraits) {
	}
}