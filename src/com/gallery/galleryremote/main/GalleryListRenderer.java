package com.gallery.galleryremote.main;

import java.awt.Component;
import java.awt.Font;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import com.gallery.galleryremote.model.Gallery;

class GalleryListRenderer extends DefaultListCellRenderer {
	private static final long serialVersionUID = -2625342262305949248L;

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		Gallery gallery = null;

		if (value instanceof Gallery && value != null) {
			gallery = (Gallery) value;
		}

		if (gallery != null && gallery.getRoot() != null) {
			Font font = getFont().deriveFont(Font.BOLD);
			setFont(font);
		} else {
			Font font = getFont().deriveFont(Font.PLAIN);
			setFont(font);
		}

		return this;
	}
}
