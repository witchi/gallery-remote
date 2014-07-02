package com.gallery.GalleryRemote.about;

import java.awt.FontMetrics;

import com.gallery.GalleryRemote.util.GRI18n;

/**
 * Animation thread
 * 
 * @author paour
 */
class AnimationThread extends Thread {
	private static final String MODULE = "About";
	private volatile boolean running = true;

	AnimationThread() {
		super(GRI18n.getString(MODULE, "aboutAnim"));
		setPriority(Thread.MIN_PRIORITY);
	}

	/**
	 * Description of the Method
	 */
	public void kill() {
		running = false;
	}

	/**
	 * Main processing method for the AnimationThread object
	 */
	@Override
	public void run() {
		FontMetrics fm = getFontMetrics(getFont());
		int max = (text.size() * fm.getHeight());
		long start = System.currentTimeMillis();

		while (running) {
			scrollPosition = initialPosition
					+ (int) ((System.currentTimeMillis() - start) / 40);

			if (scrollPosition > max) {
				scrollPosition = initialPosition;
				start = System.currentTimeMillis();
			}

			try {
				Thread.sleep(100 / 60);
			} catch (Exception e) {
			}

			repaint(getWidth() / 2 - maxWidth, TOP, maxWidth * 2,
					getHeight() - TOP - BOTTOM);
		}
	}
}