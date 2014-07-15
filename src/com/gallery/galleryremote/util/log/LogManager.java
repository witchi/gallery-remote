package com.gallery.galleryremote.util.log;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;

import com.gallery.galleryremote.Log;

public final class LogManager {

	private static volatile boolean initialized = false;
	private static volatile Level level = Level.INFO;
	private static volatile boolean toConsole = false;

	public synchronized static void init(int level, boolean toConsole) throws IOException {
		if (initialized) {
			return;
		}

		LogManager.initialized = true;
		LogManager.level = translate(level);
		LogManager.toConsole = toConsole;

		initExistingLoggers(LogManager.class.getPackage().getName());

		// the parent logger for all application loggers
		java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Logger.class.getName());
		logger.setUseParentHandlers(false);

		FileHandler fh = new FileHandler(new File(System.getProperty("java.io.tmpdir"), "GalleryRemoteLog2.txt").getPath());
		Formatter formatter = new GalleryLogFormatter();
		fh.setFormatter(formatter);
		fh.setLevel(Level.ALL);
		logger.addHandler(fh);
	}

	private static void removeConsoleHandlerFromLogger(java.util.logging.Logger logger) {
		Handler[] handlers = logger.getHandlers();
		if (handlers.length == 0) {
			return;
		}
		for (Handler h : handlers) {
			if (h instanceof ConsoleHandler) {
				logger.removeHandler(h);
			}
		}
	}

	private static void initExistingLoggers(String pkg) {

		List<String> list = Collections.list(java.util.logging.LogManager.getLogManager().getLoggerNames());
		for (String name : list) {
			if (name.startsWith(pkg)) {
				java.util.logging.Logger logger = java.util.logging.Logger.getLogger(name);
				logger.setLevel(LogManager.level);
				if (!toConsole) {
					removeConsoleHandlerFromLogger(logger);
				}
			}
		}

		int idx = pkg.lastIndexOf('.');
		if (idx > -1) {
			initExistingLoggers(pkg.substring(0, idx));
		}
	}

	private static Level translate(int level) {
		Level res;
		// map the levels from the GalleryRemote properties file
		// TODO: remove that, if we have only Java-Logging values.
		switch (level) {
		case Log.LEVEL_CRITICAL:
			res = Level.SEVERE;
			break;
		case Log.LEVEL_ERROR:
			res = Level.WARNING;
			break;
		case Log.LEVEL_INFO:
			res = Level.INFO;
			break;
		case Log.LEVEL_TRACE:
			res = Level.FINE;
			break;
		default:
			res = Level.parse("" + level);
		}
		return res;
	}
}
