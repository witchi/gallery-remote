package com.gallery.galleryremote.main.preview.content;

import java.awt.Image;

import com.gallery.galleryremote.model.Picture;

public interface ImageContentPaneModel {

	Image getImage();
	
	Picture getPicture();
	
	// FIXME: is it right or is it the nextPicture()
	Picture getExpectedPicture();
	
	boolean hasPicture();
	
}
