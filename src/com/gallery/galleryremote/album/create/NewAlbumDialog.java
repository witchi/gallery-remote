package com.gallery.galleryremote.album.create;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.gallery.galleryremote.model.Album;

public interface NewAlbumDialog {
	
	void resetUI(NewAlbumDTO dto);

	void setVisible(boolean visible);

	// TODO: maybe it is better to return interfaces instead of the full Swing UI
	// objects
	JButton getCancelButton();

	JButton getOkButton();

	JTextField getNameField();

	JTextField getTitleField();

	JTextArea getDescriptionArea();

	JComboBox<Album> getAlbumComboBox();
}
