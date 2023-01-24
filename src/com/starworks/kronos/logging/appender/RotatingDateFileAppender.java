package com.starworks.kronos.logging.appender;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class RotatingDateFileAppender extends RotatingFileAppender {

	private static final DateFormat s_dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	private Date m_date;
	
	public RotatingDateFileAppender(String directory, int maxLines) {
		super(directory + s_dateFormat.format(new Date()), maxLines);
		this.m_date = new Date();
	}
	
	public boolean validate() {
		final Date date = new Date();
		if (date.after(m_date)) {
			m_date = date;
			m_baseName = m_handle.getFileDirectory() + s_dateFormat.format(date);
			m_currentFileCount.set(0);
			return false;
		}
		return true;
	}

	@Override
	public void append(Message message) throws IOException {
		if (!validate()) {
			rotate();
		}
		super.append(message);
	}
}
