package com.gallery.galleryremote.album.create;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import com.gallery.galleryremote.model.Album;

class AlbumListRenderer extends DefaultListCellRenderer {

	private static final long serialVersionUID = -1231757007725402230L;

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

		if (((Album) value).getCanCreateSubAlbum()) {
			if (!isSelected) {
				setForeground(Color.BLACK);
			}
		} else {
			setForeground(Color.GRAY);
		}

		return this;
	}
}