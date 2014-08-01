package com.gallery.galleryremote.util;

import java.awt.Image;

import com.gallery.galleryremote.model.Picture;

public interface ImageProcessor {

	Image getSizedIconForce(Picture picture);
	
	void pictureReady(Image image, Picture picture);
}
