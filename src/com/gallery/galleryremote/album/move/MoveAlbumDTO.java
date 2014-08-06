package com.gallery.galleryremote.album.move;

import java.util.ArrayList;

import com.gallery.galleryremote.model.Album;

class MoveAlbumDTO {

	private ArrayList<Album> albumList;
	private String albumName;
	
	public MoveAlbumDTO() {
		this.setAlbumList(new ArrayList<Album>());
		this.setAlbumName("");
	}

	public ArrayList<Album> getAlbumList() {
		return albumList;
	}

	public void setAlbumList(ArrayList<Album> albumList) {
		this.albumList = albumList;
	}

	public String getAlbumName() {
		return albumName;
	}

	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}
}
