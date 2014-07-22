package com.gallery.galleryremote.imageloader;

import java.awt.Image;

import com.gallery.galleryremote.Log;
import com.gallery.galleryremote.model.Picture;

public class ImageLoaderThread implements Runnable {
	Picture picture;
	boolean stillRunning = false;
	boolean notify = false;

	@Override
	public void run() {
		Log.log(Log.LEVEL_TRACE, MODULE, "Starting " + picture);
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

		Log.log(Log.LEVEL_TRACE, MODULE, "Ending");
	}

	public void loadPicture(Picture picture, boolean notify) {
		Log.log(Log.LEVEL_TRACE, MODULE, "loadPicture " + picture);

		this.picture = picture;

		if (notify) {
			this.notify = true;
		}

		if (!stillRunning) {
			stillRunning = true;
			Log.log(Log.LEVEL_TRACE, MODULE, "Calling Start");
			new Thread(this).start();
		}
	}
}
