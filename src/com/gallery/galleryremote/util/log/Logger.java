package com.gallery.galleryremote.util.log;

import java.util.logging.Level;

public class Logger {

	private java.util.logging.Logger logger;

	private Logger(java.util.logging.Logger logger) {
		this.logger = logger;
	}

	public static Logger getLogger(Class<?> clazz) {
		return new Logger(java.util.logging.Logger.getLogger(clazz.getName()));
	}

	public void fine(StackTraceElement[] stack) {
		String message = "";
		for (StackTraceElement elem : stack) {
			message += elem.toString() + "\n";
		}
		fine(message);
	}

	public void fine(String msg) {
		logger.fine(msg);
	}

	public void severe(String msg) {
		logger.severe(msg);
	}

	public void throwing(Throwable thrown) {
		logger.log(Level.FINE, thrown.getMessage(), thrown);
	}

	public void info(String msg) {
		logger.info(msg);
	}
	
}
