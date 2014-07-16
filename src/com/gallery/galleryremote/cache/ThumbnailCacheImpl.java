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
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.gallery.galleryremote.GalleryRemote;
import com.gallery.galleryremote.model.Picture;
import com.gallery.galleryremote.statusbar.StatusLevel;
import com.gallery.galleryremote.util.GRI18n;
import com.gallery.galleryremote.util.log.Logger;

/**
 * Thumbnail cache loads and resizes images in the background for display in the
 * list of Pictures
 * 
 * @author paour
 * @author arothe
 */
public class ThumbnailCacheImpl implements ThumbnailCache {

	private static final Logger LOGGER = Logger.getLogger(ThumbnailCacheImpl.class);

	private final List<ThumbnailLoader> loaders;
	private final Map<Picture, Image> thumbnails;
	private final Stack<Picture> toLoad;

	public ThumbnailCacheImpl() {
		LOGGER.fine("Creating class instance...");
		loaders = new ArrayList<ThumbnailLoader>();
		thumbnails = Collections.synchronizedMap(new HashMap<Picture, Image>());
		toLoad = new Stack<Picture>();
	}

	@Override
	public void preloadThumbnailFirst(Picture p) {
		LOGGER.fine("preloadThumbnailFirst " + p);
		if (!thumbnails.containsKey(p)) {
			toLoad.push(p);
			rerun();
		}
	}

	@Override
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

	@Override
	public void reload() {
		Iterator<Picture> it = new HashMap<Picture, Image>(thumbnails).keySet().iterator();
		thumbnails.clear();
		preloadThumbnails(it);
	}

	@Override
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
			ThumbnailLoader l = new ThumbnailLoader(this);
			loaders.add(l);
			l.start();
		}
	}

	@Override
	public void cancelLoad() {
		toLoad.clear();
		for (Loader loader : loaders) {
			loader.cancel();
		}
	}

	@Override
	public Image getThumbnail(Picture p) {
		return thumbnails.get(p);
	}

	@Override
	public void afterLoad(Loader loader) {
		loaders.remove(loader);

		GalleryRemote.instance().getCore().getMainStatusUpdate()
				.stopProgress(StatusLevel.CACHE, GRI18n.getString(this.getClass(), "thmbLoaded"));
	}

	@Override
	public int getCountToLoad() {
		return toLoad.size();
	}

	@Override
	public boolean containsThumbnail(Picture p) {
		return thumbnails.containsKey(p);
	}

	@Override
	public Picture getNextToLoad() {
		try {
			return toLoad.pop();
		} catch (EmptyStackException e) {
			return null;
		}
	}

	@Override
	public void load(Picture p, Image img, int loaded) {
		thumbnails.put(p, img);
		GalleryRemote.instance().getCore().getMainStatusUpdate().updateProgressValue(StatusLevel.CACHE, loaded, loaded + toLoad.size());
		GalleryRemote.instance().getCore().thumbnailLoadedNotify();
	}

	@Override
	public void beforeLoad(Loader loader) {
		GalleryRemote.instance().getCore().getMainStatusUpdate()
				.startProgress(StatusLevel.CACHE, 0, toLoad.size(), GRI18n.getString(this.getClass(), "loadThmb"), false);
	}

	@Override
	public String[] acceptedImageFormats() {
		return new String[] {"jpeg", "png", "bmp", "gif"};
	}
}
