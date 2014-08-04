package com.gallery.galleryremote.main.preview.content;

import java.awt.Point;
import java.awt.image.ImageObserver;

public interface ImageContentPane extends ImageObserver {

	void refreshUI(ImageContentPaneDTO dto);

	int getHeight();

	int getWidth();

	Point getLocation();
}
