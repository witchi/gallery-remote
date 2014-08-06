package com.gallery.galleryremote.album.move;

import java.util.ArrayList;
import java.util.Enumeration;

import com.gallery.galleryremote.album.inspector.AlbumInspectorModel;
import com.gallery.galleryremote.model.Album;
import com.gallery.galleryremote.util.AbstractModel;

public class MoveAlbumModelImpl extends AbstractModel implements MoveAlbumModel {

	private final AlbumInspectorModel albumModel;
	private Album newParent;

	public MoveAlbumModelImpl(AlbumInspectorModel albumModel) {
		this.albumModel = albumModel;
		this.newParent = null;
	}

	/**
	 * Returns a list of all Albums of the current Gallery, but removes the
	 * current album from the list. So we cannot chose the current album as new parent (circle).
	 */
	@Override
	public ArrayList<Album> getAlbumList() {
		ArrayList<Album> list = albumModel.getGallery().getFlatAlbumList();

		Album rootAlbum = (Album) albumModel.getGallery().getRoot();
		Album album = albumModel.getAlbum();

		if (album.getParentAlbum() == rootAlbum) {
			list.remove(rootAlbum);
		}

		removeAlbumFromList(list, album, true);
		return list;
	}

	@Override
	public Album getNewParent() {
		return newParent;
	}

	private void removeAlbumFromList(ArrayList<Album> list, Album album, boolean deep) {
		if (!deep) {
			list.remove(album);
			return;
		}
		for (Enumeration<Album> it = album.children(); it.hasMoreElements();) {
			Album subAlbum = it.nextElement();
			removeAlbumFromList(list, subAlbum, true);
		}
	}

	@Override
	public void setSelectedAlbum(Album album) {
		this.newParent = album;
	}

	@Override
	public String getAlbumName() {
		return albumModel.getAlbum().getName();
	}
}
