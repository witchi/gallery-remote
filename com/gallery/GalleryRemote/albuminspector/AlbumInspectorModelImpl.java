package com.gallery.GalleryRemote.albuminspector;

import java.awt.event.ActionEvent;

import com.gallery.GalleryRemote.MainFrame;
import com.gallery.GalleryRemote.model.Album;
import com.gallery.GalleryRemote.util.AbstractModel;

public class AlbumInspectorModelImpl extends AbstractModel implements AlbumInspectorModel {

	private MainFrame mainFrame;
	private Album album;

	public AlbumInspectorModelImpl(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

	@Override
	public void setAlbum(Album album) {
		this.album = album;
		fireRefreshEvent();
	}

	private void fireRefreshEvent() {
		notifyListeners(new ActionEvent(this, 0, AlbumInspectorActions.REFRESH.name()));
	}
}
