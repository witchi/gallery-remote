package com.gallery.galleryremote.album.move;

import javax.swing.JButton;
import javax.swing.JComboBox;

import com.gallery.galleryremote.model.Album;

public interface MoveAlbumDialog {

	void setVisible(boolean visible);

	void resetUI(MoveAlbumDTO dto);

	JButton getOkButton();

	JButton getCancelButton();

	JComboBox<Album> getAlbumComboBox();
}
