package com.gallery.galleryremote.imageloader;

import java.awt.Image;

import com.gallery.galleryremote.model.Picture;

class ImageLoaderWorkUnit {

	private Image image;
	private final Picture picture;
	private final boolean notify;

	public ImageLoaderWorkUnit(Picture picture) {
		this(picture, false);
	}

	public ImageLoaderWorkUnit(Picture picture, boolean notify) {
		this.picture = picture;
		this.notify = notify;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public Picture getPicture() {
		return this.picture;
	}

	public Image getImage() {
		return this.image;
	}

	public boolean isNotification() {
		return this.notify;
	}

}
