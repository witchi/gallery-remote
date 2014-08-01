package com.gallery.galleryremote.main.preview.content;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;

public interface ImageContentPaneModel {

	public void setDefaultBackgroundColor(Color background);

	public Color getBackgroundColor();
	
	public Image getImage(Component c);

}
