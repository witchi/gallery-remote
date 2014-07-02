package com.gallery.GalleryRemote.about;

import com.gallery.GalleryRemote.util.GRI18n;

/**
 * Animation thread
 * 
 * @author paour
 */
class AnimationThread extends Thread {
	private static final String MODULE = "About";
	private volatile boolean running = true;
	private final AboutModel model;

	AnimationThread(AboutModel model) {
		super(GRI18n.getString(MODULE, "aboutAnim"));
		setPriority(Thread.MIN_PRIORITY);
		this.model = model;
	}

	/**
	 * Description of the Method
	 */
	public void kill() {
		running = false;
		this.interrupt();
	}

	/**
	 * Main processing method for the AnimationThread object
	 */
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
				Thread.sleep(100 / 60);
			} catch (Exception e) {
				// do nothing
			}

			model.paintAnimation(scrollPosition);
		}
	}
}