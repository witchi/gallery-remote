package com.gallery.galleryremote.main.preview.glass;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public interface CropGlassPane {

	void refreshUI(CropGlassDTO dto);

	void addMouseListener(MouseListener listener);

	void addMouseMotionListener(MouseMotionListener listener);
}
