package com.gallery.galleryremote.statusbar;

/**
 * This thread animates the progressbar from left to right and right to the
 * left.
 * 
 * @author arothe
 * 
 */
public class UndeterminedThread extends Thread {
	private StatusUpdate su;
	private StatusLevel level;

	public UndeterminedThread(StatusUpdate su, StatusLevel level) {
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