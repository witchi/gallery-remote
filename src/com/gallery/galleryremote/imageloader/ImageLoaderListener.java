package com.gallery.galleryremote.imageloader;

import java.awt.Dimension;
import java.awt.Image;

import com.gallery.galleryremote.model.Picture;

public interface ImageLoaderListener {
	void pictureReady();

	boolean blockPictureReady(Image image, Picture picture);

	Dimension getImageSize();

	void nullRect();

	void pictureStartDownloading(Picture picture);

	void pictureStartProcessing(Picture picture);

	void pictureLoadError(Picture picture);
}