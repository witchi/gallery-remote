package com.gallery.galleryremote.main.preview.content;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;

import com.gallery.galleryremote.GalleryRemote;
import com.gallery.galleryremote.prefs.PreferenceNames;
import com.gallery.galleryremote.util.AbstractModel;
import com.gallery.galleryremote.util.ImageLoaderUtil;
import com.gallery.galleryremote.util.ImageUtils;
import com.gallery.galleryremote.util.log.Logger;

public class ImageContentPaneModelImpl extends AbstractModel implements ImageContentPaneModel {

	private static final Logger LOGGER = Logger.getLogger(ImageContentPaneModelImpl.class);

	private final ImageLoaderUtil loader;
	private Color defaultBackgroundColor;

	public ImageContentPaneModelImpl(ImageLoaderUtil loader) {
		this.loader = loader;
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

	@Override
	public Image getImage(Component c) {
		Image image = null;
		if (loader.imageShowNow != null && loader.pictureShowWant != null) {
			LOGGER.fine("New image: " + loader.imageShowNow);
			image = ImageUtils.rotateImage(loader.imageShowNow, loader.pictureShowWant.getAngle(), loader.pictureShowWant.isFlipped(), c);
		}
		return image;
	}
}
