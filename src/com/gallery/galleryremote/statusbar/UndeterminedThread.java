package com.gallery.galleryremote.statusbar;

import com.sun.istack.internal.logging.Logger;

/**
 * This thread animates the ProgressBar from left to right and right to the
 * left. It is only necessary, if the UI won't provide an own ProgressBar
 * (JProgressBar throws an exception on setUndetermined()).
 * 
 * @author arothe
 * 
 */
public class UndeterminedThread extends Thread {

	private static final Logger LOGGER = Logger.getLogger(UndeterminedThread.class);

	private StatusUpdate su;
	private StatusLevel level;

	public UndeterminedThread(StatusUpdate su, StatusLevel level) {
		LOGGER.fine("Creating class instance...");
		this.su = su;
		this.level = level;
	}

	@Override
	public void run() {
		boolean forward = true;

		while (!isInterrupted()) {
			if (su.getProgressValue(level) >= su.getProgressMaxValue(level)) {
				forward = false;
			} else if (su.getProgressValue(level) <= su.getProgressMinValue(level)) {
				forward = true;
			}

			su.updateProgressValue(level, su.getProgressValue(level) + (forward ? 1 : -1));

			try {
				sleep(500);
			} catch (InterruptedException e) {
			}
		}
	}
}