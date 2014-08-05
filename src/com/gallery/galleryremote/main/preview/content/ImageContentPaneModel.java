package com.gallery.galleryremote.main.preview.content;

import java.awt.Image;
import java.awt.Color;
import com.gallery.galleryremote.model.Picture;

public interface ImageContentPaneModel {

	void setDefaultBackgroundColor(Color background);
	
	Color getBackgroundColor();
	
	Image getImage();
	
	Picture getPicture();
	
	// FIXME: is it right or is it the nextPicture()
	Picture getExpectedPicture();
	
	boolean hasPicture();
	
}
