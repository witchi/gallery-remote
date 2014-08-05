package com.gallery.galleryremote.main.preview.content;

import java.awt.Color;
import java.awt.Image;

import com.gallery.galleryremote.GalleryRemote;
import com.gallery.galleryremote.model.Picture;
import com.gallery.galleryremote.prefs.PreferenceNames;
import com.gallery.galleryremote.util.AbstractModel;
import com.gallery.galleryremote.util.ImageCache;
import com.gallery.galleryremote.util.log.Logger;

public class ImageContentPaneModelImpl extends AbstractModel implements ImageContentPaneModel {

	private final static Logger LOGGER = Logger.getLogger(ImageContentPaneModelImpl.class);
	private final ImageCache cache;
	private Color defaultBackgroundColor;
	
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
	
		@Override
	public void setDefaultBackgroundColor(Color background) {
		this.defaultBackgroundColor = background;
	}

	@Override
	public Color getBackgroundColor() {
		Color c = GalleryRemote.instance().properties.getColorProperty(PreferenceNames.SLIDESHOW_COLOR);
		return (c == null ? this.defaultBackgroundColor : c);
	}
	
}
