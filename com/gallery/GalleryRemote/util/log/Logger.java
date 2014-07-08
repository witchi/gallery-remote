package com.gallery.GalleryRemote.util.log;

import java.io.File;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
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

	public static void setup(int level, boolean toConsole) throws IOException {

		java.util.logging.Logger logger = java.util.logging.Logger.getLogger(java.util.logging.Logger.GLOBAL_LOGGER_NAME);

		if (!toConsole) {
			java.util.logging.Logger rootLogger = java.util.logging.Logger.getLogger("");
			Handler[] handlers = rootLogger.getHandlers();

			if (handlers[0] instanceof ConsoleHandler) {
				rootLogger.removeHandler(handlers[0]);
			}
		}

		// map the levels from the GalleryRemote properties file
		// TODO: remove that, if we have only Java-Logging values.
		switch (level) {
		case 0:
			logger.setLevel(Level.SEVERE);
			break;
		case 1:
			logger.setLevel(Level.WARNING);
			break;
		case 2:
			logger.setLevel(Level.INFO);
			break;
		case 3:
			logger.setLevel(Level.FINE);
			break;
		default:
			logger.setLevel(Level.parse("" + level));
		}

		FileHandler fileTxt = new FileHandler(new File(System.getProperty("java.io.tmpdir"), "GalleryRemoteLog2.txt").getPath());
		Formatter formatter = new GalleryLogFormatter();
		fileTxt.setFormatter(formatter);
		logger.addHandler(fileTxt);
	}
}
