package com.gallery.galleryremote.main.preview.content;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;

public interface ImageContentPane {

	Color getBackground();

	void setColor(Color color);

	void setImage(Image image, Rectangle imageArea);

	void repaint();

	Point getLocation();
	
	int getWidth();
	
	int getHeight();
}
