package com.gallery.galleryremote.util.log;

import java.util.logging.Level;

public class Logger {

	private java.util.logging.Logger logger;

	private Logger(java.util.logging.Logger logger) {
		this.logger = logger;
	}

	public void fine(StackTraceElement[] stack) {
		String message = "";
		for (StackTraceElement elem : stack) {
			message += elem.toString() + "\n";
		}
		fine(message);
	}

	public static Logger getLogger(Class<?> clazz) {
		return new Logger(java.util.logging.Logger.getLogger(clazz.getName()));
	}

	public void fine(String string) {
		logger.fine(string);
	}

	public void severe(String string) {
		logger.severe(string);
	}

	public void throwing(Throwable thrown) {
		logger.log(Level.FINE, thrown.getMessage(), thrown);
	}

	
	
}
