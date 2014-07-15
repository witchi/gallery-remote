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
package com.gallery.galleryremote.cache;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.gallery.galleryremote.GalleryRemote;
import com.gallery.galleryremote.model.Picture;
import com.gallery.galleryremote.util.log.Logger;

/**
 * Thumbnail cache loads and resizes images in the background for display in the
 * list of Pictures
 * 
 * @author paour
 * @author arothe
 */
public class ThumbnailCache implements LoaderListener {

	private static final Logger LOGGER = Logger.getLogger(ThumbnailCache.class);

	private final List<ThumbnailLoader> loaders;
	private final Stack<Picture> toLoad;
	private final Map<Picture, Image> thumbnails;

	public ThumbnailCache() {
		LOGGER.fine("Creating class instance...");
		this.loaders = new ArrayList<ThumbnailLoader>();
		this.toLoad = new Stack<Picture>();
		this.thumbnails = Collections.synchronizedMap(new HashMap<Picture, Image>());
	}

	/**
	 * Ask for the thumbnail to be loaded as soon as possible
	 */
	public void preloadThumbnailFirst(Picture p) {
		LOGGER.fine("preloadThumbnailFirst " + p);

		if (!thumbnails.containsKey(p)) {
			toLoad.push(p);
			rerun();
		}
	}

	/**
	 * Ask for several thumnails to be loaded
	 * 
	 * @param pictures
	 *           enumeration of Picture objects that should be loaded
	 */
	public void preloadThumbnails(Iterator<Picture> pictures) {
		LOGGER.fine("preloadThumbnails");

		while (pictures.hasNext()) {
			Picture p = pictures.next();
			if (!thumbnails.containsKey(p)) {
				toLoad.add(0, p);
			}
		}
		rerun();
	}

	public void reload() {
		Iterator<Picture> it = new HashMap<Picture, Image>(thumbnails).keySet().iterator();
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

	private void rerun() {
		if (GalleryRemote.instance().properties.getShowThumbnails()) {
			ThumbnailLoader l = new ThumbnailLoader();
			l.addProcessListener(this);
			loaders.add(l);
			l.start();
		}
	}

	public void cancelLoad() {
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

	@Override
	public void endOfProcess(Loader loader) {
		loader.removeProcessListener(this);
		loaders.remove(loader);
	}
}
