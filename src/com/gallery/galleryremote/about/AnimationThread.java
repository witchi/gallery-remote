package com.gallery.galleryremote.about;

import com.gallery.galleryremote.util.GRI18n;

/**
 * Animation thread
 * 
 * @author paour
 */
class AnimationThread extends Thread {
	private static final String MODULE = "About";
	private volatile boolean running = true;
	private final AnimationModel model;

	AnimationThread(AnimationModel model) {
		super(GRI18n.getString(MODULE, "aboutAnim"));
		setPriority(Thread.MIN_PRIORITY);
		this.model = model;
	}

	public void kill() {
		running = false;
		this.interrupt();
	}

	@Override
	public void run() {

		long start = System.currentTimeMillis();

		while (running) {
			int scrollPosition = model.getInitialPosition()
					+ (int) ((System.currentTimeMillis() - start) / 40);

			if (scrollPosition > model.getMaxTextHeight()) {
				scrollPosition = model.getInitialPosition();
				start = System.currentTimeMillis();
			}

			try {
				Thread.sleep(1);
			} catch (Exception e) {
				// do nothing
			}

			model.paintAnimation(scrollPosition);
		}
	}
}