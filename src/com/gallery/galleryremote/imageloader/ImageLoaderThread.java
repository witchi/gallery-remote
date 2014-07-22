package com.gallery.galleryremote.imageloader;

import java.awt.Image;

import com.gallery.galleryremote.Log;
import com.gallery.galleryremote.model.Picture;
import com.gallery.galleryremote.util.log.Logger;

public class ImageLoaderThread implements Runnable {
	
	private static final Logger LOGGER = Logger.getLogger(ImageLoaderThread.class);
	
	private Picture picture;
	private boolean stillRunning = false;
	private boolean notify = false;

	@Override
	public void run() {
		LOGGER.fine("Start loading image " + picture);
		
		Picture tmpPicture = null;
		Image tmpImage = null;
		while (picture != null) {
			synchronized (this) {
				tmpPicture = picture;
				picture = null;
			}

			tmpImage = getSizedIconForce(tmpPicture);

			if (tmpImage == null) {
				notify = false;
			}
		}

		stillRunning = false;

		if (notify) {
			pictureReady(tmpImage, tmpPicture);
			notify = false;
		}

		LOGGER.fine("Ending image loading");
	}

	public void loadPicture(Picture picture, boolean notify) {
		LOGGER.fine("loadPicture " + picture);

		this.picture = picture;

		if (notify) {
			this.notify = true;
		}

		if (!stillRunning) {
			stillRunning = true;
			LOGGER.fine("Calling Start for load picture");
			new Thread(this).start();
		}
	}
}
