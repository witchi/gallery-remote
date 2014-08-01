package com.gallery.galleryremote.imageloader;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.gallery.galleryremote.model.Picture;
import com.gallery.galleryremote.util.ImageProcessor;
import com.gallery.galleryremote.util.log.Logger;

/**
 * This thread runs always, till someone calls the kill() method. It gets some
 * ImageLoaderWorkUnit instances from its workList. If the list is empty, the
 * thread will check the content of the list every 150 ms. If there is no new
 * work, the thread will sleep.
 * 
 * @author arothe
 * 
 */
public class ImageLoaderThread extends Thread {

	private static final Logger LOGGER = Logger.getLogger(ImageLoaderThread.class);

	private final List<ImageLoaderWorkUnit> workList;
	private volatile boolean killed;
	private final ImageProcessor processor;

	public ImageLoaderThread(ImageProcessor processor) {
		LOGGER.fine("Creating class instance...");

		this.processor = processor;
		this.workList = Collections.synchronizedList(new ArrayList<ImageLoaderWorkUnit>());
		this.killed = false;
		start();
	}

	@Override
	public void run() {
		LOGGER.fine("Starting image loading thread");

		while (!Thread.currentThread().isInterrupted()) {

			ImageLoaderWorkUnit unit = getLastWorkUnit();

			if (unit == null) {
				hibernate(unit);
				continue;
			}

			unit.setImage(processor.getSizedIconForce(unit.getPicture()));

			if (unit == getLastWorkUnit()) {
				firePictureReady(unit);
				flushWorkList(unit);
				hibernate(unit);
			}
		}
		LOGGER.fine("Ending image loading thread");
	}

	private void flushWorkList(ImageLoaderWorkUnit unit) {
		if (unit == null) {
			return;
		}
		int idx = workList.indexOf(unit);
		for (int i = 0; i < idx; i++) {
			workList.remove(i);
		}
	}

	private ImageLoaderWorkUnit getLastWorkUnit() {
		return workList.isEmpty() ? null : workList.get(workList.size() - 1);
	}

	private void hibernate(ImageLoaderWorkUnit unit) {
		try {
			while (unit == getLastWorkUnit()) {
				Thread.sleep(150);
			}
		} catch (InterruptedException ex) {
			if (killed) {
				Thread.currentThread().interrupt();
			}
		}
	}

	private void firePictureReady(ImageLoaderWorkUnit unit) {
		if (!unit.isNotification() || unit.getImage() == null) {
			return;
		}
		processor.pictureReady(unit.getImage(), unit.getPicture());
	}

	public void kill() {
		LOGGER.fine("Killing image loading thread");
		this.killed = true;
		interrupt();
	}

	public Image loadPicture(Picture picture) {
		LOGGER.fine("synchronized load picture " + picture);
		return processor.getSizedIconForce(picture);
	}

	public void loadPictureAsync(Picture picture, boolean notify) {
		LOGGER.fine("async load picture " + picture);
		workList.add(new ImageLoaderWorkUnit(picture, notify));
	}

}
