package com.gallery.galleryremote.main.preview.content;

import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;

public class ImageContentPaneDTO {

	private Image image;
	private Rectangle rectangle;
	private Color color;

	public ImageContentPaneDTO() {
		image = null;
		color = null;
		rectangle = null;
	}

	public Image getImage() {
		return image;
	}

	public Rectangle getRectangle() {
		return rectangle;
	}

	public Color getColor() {
		return color;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public void setRectangle(Rectangle rectangle) {
		this.rectangle = rectangle;
	}

	public void setColor(Color color) {
		this.color = color;
	}

}
