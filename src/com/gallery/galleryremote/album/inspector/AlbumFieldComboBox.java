package com.gallery.galleryremote.album.inspector;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;

import com.gallery.galleryremote.prefs.UploadPanel.ResizeSize;

public interface AlbumFieldComboBox {

	String getSelectedItemAsString();

	String getEditedItem();

	void addKeyboardListener(KeyListener listener);

	void addActionListener(ActionListener l);

	void setEnabled(boolean enabled);

	void setBackground(Color color);

	ComboBoxModel<ResizeSize> getModel();

	void setSelectedIndex(int i);

	void setSelectedItem(ResizeSize s);

	void addItem(ResizeSize s);

	void setRenderer(DefaultListCellRenderer aRenderer);

	void setEditable(boolean editable);

	void setToolTipText(String text);

}
