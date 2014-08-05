package com.gallery.galleryremote.album.create;

import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;

import com.gallery.galleryremote.model.Album;

public class AlbumComboBoxModel extends DefaultComboBoxModel<Album> {

	private static final long serialVersionUID = 2007843443636065698L;
	
	public void setAlbumList(ArrayList<Album> list) {
		removeAllElements();
		for (Album a : list) {
			addElement(a);
		}
		
		// TODO: do we loose some listeners
		// TODO: do we fire some obsolete events? 
	}

}
