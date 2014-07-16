package com.gallery.galleryremote.cache;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URLConnection;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import com.gallery.galleryremote.GalleryRemote;
import com.gallery.galleryremote.model.Picture;
import com.gallery.galleryremote.util.ImageUtils;
import com.gallery.galleryremote.util.log.Logger;

class ThumbnailLoader extends Thread implements Loader {

	private static final Logger LOGGER = Logger.getLogger(ThumbnailLoader.class);

	private volatile boolean cancel;
	private final ThumbnailCache cache;
	private int id;

	public ThumbnailLoader(ThumbnailCache cache) {
		LOGGER.fine("Creating class instance...");
		this.cancel = false;
		this.cache = cache;
	}

	/**
	 * Main processing method for the ThumbnailLoader object
	 * 
	 */
	@Override
	public void run() {

		Thread.yield();

		LOGGER.fine("Thumbnail thread starting...");

		id = 0;
		cache.beforeLoad(this);

		while (!cancel) {

			Picture p = cache.getNextToLoad();
			if (p == null) {
				break; // queue is empty
			}

			if (cache.containsThumbnail(p)) {
				continue;
			}

			Image img = null;
			if (p.isOnline()) {

				img = fetchThumbnailImage(p);
				if (img != null) {

					Dimension newD = ImageUtils.getSizeKeepRatio(
							new Dimension(((BufferedImage) img).getWidth(), ((BufferedImage) img).getHeight()),
							GalleryRemote.instance().properties.getThumbnailSize(), true);

					if (newD != null) {
						Image scaled = img.getScaledInstance(newD.width, newD.height, Image.SCALE_FAST);
						img.flush();
						img = scaled;
					}
				} else {
					img = ImageUtils.unrecognizedThumbnail;
				}
			} else {
				img = ImageUtils.load(p.getSource().getPath(), GalleryRemote.instance().properties.getThumbnailSize(), ImageUtils.THUMB);
			}

			cache.load(p, img, ++id);
			LOGGER.fine("update progress " + id + "/" + (id + cache.getCountToLoad()));
		}

		cache.afterLoad(this);
		LOGGER.fine("Thumbnail thread ending");
	}

	@Override
	public void cancel() {
		cancel = true;
		this.interrupt();

		try {
			this.join();
		} catch (InterruptedException e) {
			// do nothing
		}
	}

	private Image fetchThumbnailImage(Picture p) {

		LOGGER.fine("Fetching thumbnail " + p.getUrlThumbnail());
		Image img = null;
		try {
			URLConnection conn = ImageUtils.openUrlConnection(p.getUrlThumbnail(), p);
			conn.connect();

			ImageReader reader = ImageIO.getImageReadersByFormatName("jpeg").next();
			ImageInputStream inputStream = ImageIO.createImageInputStream(conn.getInputStream());
			reader.setInput(inputStream);
			img = reader.read(0);
			reader.dispose();

		} catch (IIOException e) {
			LOGGER.throwing(e);
		} catch (IOException e) {
			LOGGER.throwing(e);
		}

		return img;
	}
}
