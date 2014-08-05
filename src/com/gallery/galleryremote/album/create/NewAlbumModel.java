package com.gallery.galleryremote.album.create;

import java.util.ArrayList;

import javax.swing.text.Document;

import com.gallery.galleryremote.model.Album;

public interface NewAlbumModel {

	Album getNewAlbum();

	ArrayList<Album> getAlbumList();

	Album getSelectedAlbum();

	void setSelectedAlbum(Album album);

	String getGalleryUri();

	void setDefaultName(Document d);
	
	Document getTitle();

	Document getName();

	Document getDescription();
	
	Album getParentAlbum();
}
