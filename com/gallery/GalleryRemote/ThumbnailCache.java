/*
 *  Gallery Remote - a File Upload Utility for Gallery
 *
 *  Gallery - a web based photo album viewer and editor
 *  Copyright (C) 2000-2001 Bharat Mediratta
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or (at
 *  your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.gallery.GalleryRemote;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import com.gallery.GalleryRemote.model.Picture;
import com.gallery.GalleryRemote.util.GRI18n;
import com.gallery.GalleryRemote.util.ImageUtils;

/**
 * Thumbnail cache loads and resizes images in the background for display in the
 * list of Pictures
 * 
 * @author paour
 */
public class ThumbnailCache implements Runnable {
	public static final String MODULE = "ThumbCache";

	boolean stillRunning = false;
	Stack<Picture> toLoad = new Stack<Picture>();
	HashMap<Picture, Image> thumbnails = new HashMap<Picture, Image>();

	/**
	 * Main processing method for the ThumbnailLoader object
	 */
	@Override
	public void run() {
		Thread.yield();
		int loaded = 0;
		GalleryRemote
				._()
				.getCore()
				.getMainStatusUpdate()
				.startProgress(StatusUpdate.LEVEL_CACHE, 0, toLoad.size(),
						GRI18n.getString(MODULE, "loadThmb"), false);
		// Log.log(Log.TRACE, MODULE, "Starting " + iFilename);
		while (!toLoad.isEmpty()) {
			Picture p = toLoad.pop();
			Image i = null;

			if (!thumbnails.containsKey(p)) {
				if (p.isOnline()) {
					Log.log(Log.LEVEL_TRACE, MODULE,
							"Fetching thumbnail " + p.getUrlThumbnail());
					try {
						URLConnection conn = ImageUtils.openUrlConnection(
								p.getUrlThumbnail(), p);
						conn.connect();

						ImageReader reader = (ImageReader) ImageIO
								.getImageReadersByFormatName("jpeg").next();
						ImageInputStream inputStream = ImageIO
								.createImageInputStream(conn.getInputStream());
						reader.setInput(inputStream);

						i = reader.read(0);

						reader.dispose();
					} catch (IIOException e) {
						Log.logException(Log.LEVEL_ERROR, MODULE, e);
					} catch (IOException e) {
						Log.logException(Log.LEVEL_ERROR, MODULE, e);
					}

					if (i != null) {
						Image scaled;
						Dimension newD = ImageUtils
								.getSizeKeepRatio(new Dimension(
										((BufferedImage) i).getWidth(),
										((BufferedImage) i).getHeight()),
										GalleryRemote._().properties
												.getThumbnailSize(), true);
						if (newD != null) {
							scaled = i.getScaledInstance(newD.width,
									newD.height, Image.SCALE_FAST);
							i.flush();
							i = scaled;
						}
					} else {
						i = ImageUtils.unrecognizedThumbnail;
					}
				} else {
					i = ImageUtils.load(p.getSource().getPath(),
							GalleryRemote._().properties.getThumbnailSize(),
							ImageUtils.THUMB);
				}

				thumbnails.put(p, i);

				loaded++;

				Log.log(Log.LEVEL_TRACE, MODULE, "update progress " + loaded
						+ "/" + (loaded + toLoad.size()));
				GalleryRemote
						._()
						.getCore()
						.getMainStatusUpdate()
						.updateProgressValue(StatusUpdate.LEVEL_CACHE, loaded,
								loaded + toLoad.size());
				GalleryRemote._().getCore().thumbnailLoadedNotify();
			}
		}
		stillRunning = false;

		GalleryRemote
				._()
				.getCore()
				.getMainStatusUpdate()
				.stopProgress(StatusUpdate.LEVEL_CACHE,
						GRI18n.getString(MODULE, "thmbLoaded"));

		// Log.log(Log.TRACE, MODULE, "Ending");
	}

	/**
	 * Ask for the thumbnail to be loaded as soon as possible
	 */
	public void preloadThumbnailFirst(Picture p) {
		Log.log(Log.LEVEL_TRACE, MODULE, "preloadThumbnailFirst " + p);

		if (!thumbnails.containsKey(p)) {
			toLoad.push(p);

			rerun();
		}
	}

	/**
	 * Ask for several thumnails to be loaded
	 * 
	 * @param pictures
	 *            enumeration of Picture objects that should be loaded
	 */
	public void preloadThumbnails(Iterator<Picture> pictures) {
		Log.log(Log.LEVEL_TRACE, MODULE, "preloadThumbnails");

		while (pictures.hasNext()) {
			Picture p = pictures.next();

			if (!thumbnails.containsKey(p)) {
				toLoad.add(0, p);
			}
		}

		rerun();
	}

	@SuppressWarnings("unchecked")
	public void reload() {
		Iterator<Picture> it = ((HashMap<Picture, Image>) thumbnails.clone()).keySet().iterator();
		thumbnails.clear();
		preloadThumbnails(it);
	}

	public void flushMemory() {
		Iterator<Image> it = thumbnails.values().iterator();
		while (it.hasNext()) {
			Image i = it.next();
			if (i != null) {
				i.flush();
			}
		}

		thumbnails.clear();
	}

	void rerun() {
		if (!stillRunning && GalleryRemote._().properties.getShowThumbnails()) {
			stillRunning = true;
			Log.log(Log.LEVEL_TRACE, MODULE, "Calling Start");
			new Thread(this).start();
		}
	}

	void cancelLoad() {
		toLoad.clear();
	}

	/**
	 * Retrieves a thumbnail from the thumbnail cache
	 * 
	 * @return The thumbnail object
	 */
	public Image getThumbnail(Picture p) {
		return thumbnails.get(p);
	}
}
