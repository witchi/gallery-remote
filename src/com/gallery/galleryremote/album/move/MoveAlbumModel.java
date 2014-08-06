package com.gallery.galleryremote.album.move;

import java.util.ArrayList;

import com.gallery.galleryremote.model.Album;

public interface MoveAlbumModel {

	ArrayList<Album> getAlbumList();
	
	Album getNewParent();
	
	void setSelectedAlbum(Album album);
	
	String getAlbumName();
}
