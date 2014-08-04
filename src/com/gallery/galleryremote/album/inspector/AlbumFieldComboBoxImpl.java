package com.gallery.galleryremote.album.inspector;

import java.awt.event.KeyListener;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;

import com.gallery.galleryremote.prefs.UploadPanel.ResizeSize;

public class AlbumFieldComboBoxImpl extends JComboBox<ResizeSize> implements AlbumFieldComboBox {

	private static final long serialVersionUID = -4594464883786453342L;

	public AlbumFieldComboBoxImpl(Vector<ResizeSize> defaultSizes) {
		super(defaultSizes);
	}

	@Override
	public String getEditedItem() {
		return getEditor().getItem().toString();
	}

	@Override
	public String getSelectedItemAsString() {
		return getSelectedItem().toString();
	}

	@Override
	public void addKeyboardListener(KeyListener listener) {
		getEditor().getEditorComponent().addKeyListener(listener);
	}

	@Override
	public void setSelectedItem(ResizeSize s) {
		super.setSelectedItem(s);
	}

	@Override
	public void setRenderer(DefaultListCellRenderer aRenderer) {
		super.setRenderer(aRenderer);
	}
}
