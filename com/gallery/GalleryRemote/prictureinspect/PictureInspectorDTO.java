package com.gallery.GalleryRemote.prictureinspect;

import java.awt.Image;

import com.gallery.GalleryRemote.model.Picture;

class PictureInspectorDTO {

	private Picture picture;
	private Image thumbnail;
	private int pictureListSize;
	private int fileSize;
	private boolean hasCapability;

	PictureInspectorDTO() {
	}

	boolean hasCapability() {
		return hasCapability;
	}

	void setCapability(boolean hasCapability) {
		this.hasCapability = hasCapability;
	}

	void setPicture(Picture p) {
		this.picture = p;
	}

	void setThumbnail(Image thumbnail) {
		this.thumbnail = thumbnail;
	}

	void setPictureListSize(int size) {
		this.pictureListSize = size;
	}

	void setFileSize(int size) {
		this.fileSize = size;
	}

	Picture getPicture() {
		return picture;
	}

	Image getThumbnail() {
		return thumbnail;
	}

	int getPictureListSize() {
		return pictureListSize;
	}

	int getFileSize() {
		return fileSize;
	}
}
