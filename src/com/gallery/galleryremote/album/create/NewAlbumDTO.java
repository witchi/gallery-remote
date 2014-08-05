package com.gallery.galleryremote.album.create;

import java.util.ArrayList;

import com.gallery.galleryremote.model.Album;

class NewAlbumDTO {

	private boolean enabled;
	private ArrayList<Album> albumList;
	private Album selectedAlbum;
	private String galleryUri;
	

	public NewAlbumDTO() {
		this.setEnabled(false);
		this.setAlbumList(new ArrayList<Album>());
		this.setSelectedAlbum(null);
		this.setGalleryUri("");
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public ArrayList<Album> getAlbumList() {
		return albumList;
	}

	public void setAlbumList(ArrayList<Album> albumList) {
		this.albumList = albumList;
	}

	public Album getSelectedAlbum() {
		return selectedAlbum;
	}

	public void setSelectedAlbum(Album selectedAlbum) {
		this.selectedAlbum = selectedAlbum;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getGalleryUri() {
		return galleryUri;
	}

	public void setGalleryUri(String galleryUri) {
		this.galleryUri = galleryUri;
	}
}
