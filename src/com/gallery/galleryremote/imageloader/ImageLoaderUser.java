package com.gallery.galleryremote.imageloader;

import java.awt.Dimension;
import java.awt.Image;

import com.gallery.galleryremote.model.Picture;

public interface ImageLoaderUser {
	public void pictureReady();

	public boolean blockPictureReady(Image image, Picture picture);

	public Dimension getImageSize();

	public void nullRect();

	public void pictureStartDownloading(Picture picture);

	public void pictureStartProcessing(Picture picture);

	public void pictureLoadError(Picture picture);
}