package com.gallery.GalleryRemote.prictureinspect;

import java.awt.Dimension;
import java.awt.Image;

class PictureInspectorDTO {

	private Image thumbnail;
	private boolean hasCapability;
	private Dimension iconSize;
	private boolean viewEnabled;

	PictureInspectorDTO() {
	}

	public boolean isViewEnabled() {
		return viewEnabled;
	}

	public void setViewEnabled(boolean viewEnabled) {
		this.viewEnabled = viewEnabled;
	}

	public Dimension getIconSize() {
		return iconSize;
	}

	public void setIconSize(Dimension iconSize) {
		this.iconSize = iconSize;
	}

	boolean hasCapability() {
		return hasCapability;
	}

	void setCapability(boolean hasCapability) {
		this.hasCapability = hasCapability;
	}

	void setThumbnail(Image thumbnail) {
		this.thumbnail = thumbnail;
	}

	Image getThumbnail() {
		return thumbnail;
	}
}
