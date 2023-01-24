package com.starworks.kronos.exception;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class Exceptions {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("com.starworks.kronos.exception.messages");

	private Exceptions() {
	}

	public static String getMessage(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
