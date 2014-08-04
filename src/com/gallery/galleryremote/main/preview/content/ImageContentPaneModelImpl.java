package com.gallery.galleryremote.main.preview.content;

import java.awt.Image;

import com.gallery.galleryremote.model.Picture;
import com.gallery.galleryremote.util.ImageCache;
import com.gallery.galleryremote.util.log.Logger;

public class ImageContentPaneModelImpl implements ImageContentPaneModel {

	private final static Logger LOGGER = Logger.getLogger(ImageContentPaneModelImpl.class);
	private final ImageCache cache;

	public ImageContentPaneModelImpl(ImageCache cache) {
		LOGGER.fine("Creating class instance...");
		this.cache = cache;
	}

	@Override
	public boolean hasPicture() {
		return cache.getImage() != null && cache.getNextPicture() != null;
	}

	@Override
	public Picture getExpectedPicture() {
		return cache.getNextPicture();
	}

	@Override
	public Image getImage() {
		return cache.getImage();
	}

	@Override
	public Picture getPicture() {
		return cache.getPicture();
	}

}
