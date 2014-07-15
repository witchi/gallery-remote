package com.gallery.galleryremote.cache;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import com.gallery.galleryremote.GalleryRemote;
import com.gallery.galleryremote.model.Picture;
import com.gallery.galleryremote.statusbar.StatusLevel;
import com.gallery.galleryremote.util.GRI18n;
import com.gallery.galleryremote.util.ImageUtils;

class ThumbnailLoader extends Thread implements Loader {

	private volatile boolean cancel;
	private final List<LoaderListener> listeners;

	public ThumbnailLoader() {
		cancel = false;
		listeners = Collections.synchronizedList(new ArrayList<LoaderListener>());
	}

	/**
	 * Main processing method for the ThumbnailLoader object
	 * 
	 */
	@Override
	public void run() {

		Thread.yield();
		int loaded = 0;

		GalleryRemote
				.instance()
				.getCore()
				.getMainStatusUpdate()
				.startProgress(StatusLevel.CACHE, 0, toLoad.size(), GRI18n.getString(this.getClass().getPackage().getName(), "loadThmb"), false);

		while (!toLoad.isEmpty() && !cancel) {
			Picture p = toLoad.pop();
			Image i = null;

			LOGGER.fine("Thumbnail thread starting...");

			if (!thumbnails.containsKey(p)) {
				if (p.isOnline()) {
					LOGGER.fine("Fetching thumbnail " + p.getUrlThumbnail());
					try {
						URLConnection conn = ImageUtils.openUrlConnection(p.getUrlThumbnail(), p);
						conn.connect();

						ImageReader reader = ImageIO.getImageReadersByFormatName("jpeg").next();
						ImageInputStream inputStream = ImageIO.createImageInputStream(conn.getInputStream());
						reader.setInput(inputStream);

						i = reader.read(0);

						reader.dispose();
					} catch (IIOException e) {
						LOGGER.throwing(e);
					} catch (IOException e) {
						LOGGER.throwing(e);
					}

					if (i != null) {
						Image scaled;
						Dimension newD = ImageUtils.getSizeKeepRatio(
								new Dimension(((BufferedImage) i).getWidth(), ((BufferedImage) i).getHeight()),
								GalleryRemote.instance().properties.getThumbnailSize(), true);
						if (newD != null) {
							scaled = i.getScaledInstance(newD.width, newD.height, Image.SCALE_FAST);
							i.flush();
							i = scaled;
						}
					} else {
						i = ImageUtils.unrecognizedThumbnail;
					}
				} else {
					i = ImageUtils.load(p.getSource().getPath(), GalleryRemote.instance().properties.getThumbnailSize(), ImageUtils.THUMB);
				}

				thumbnails.put(p, i);

				loaded++;

				LOGGER.fine("update progress " + loaded + "/" + (loaded + toLoad.size()));
				GalleryRemote.instance().getCore().getMainStatusUpdate().updateProgressValue(StatusLevel.CACHE, loaded, loaded + toLoad.size());
				GalleryRemote.instance().getCore().thumbnailLoadedNotify();
			}
		}

		GalleryRemote.instance().getCore().getMainStatusUpdate()
				.stopProgress(StatusLevel.CACHE, GRI18n.getString(this.getClass().getPackage().getName(), "thmbLoaded"));

		fireProcessEndEvent();

		LOGGER.fine("Thumbnail thread ending");
	}

	private void fireProcessEndEvent() {
		for (LoaderListener listener : listeners) {
			listener.endOfProcess(this);
		}
	}

	@Override
	public void addProcessListener(LoaderListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public void removeProcessListener(LoaderListener listener) {
		this.listeners.remove(listener);
	}

	@Override
	public void cancel() {
		cancel = true;
	}
}
