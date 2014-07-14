package com.gallery.galleryremote.albuminspector;

import java.awt.event.ActionEvent;

import com.gallery.galleryremote.model.Album;
import com.gallery.galleryremote.model.Gallery;
import com.gallery.galleryremote.statusbar.StatusUpdate;
import com.gallery.galleryremote.util.AbstractModel;
import com.gallery.galleryremote.GalleryCommCapabilities;
import com.gallery.galleryremote.MainFrame;

public class AlbumInspectorModelImpl extends AbstractModel implements AlbumInspectorModel {

	// TODO: remove the direct reference to mainframe by listeners
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

	@Override
	public void fetchAlbumImages() {
		mainFrame.fetchAlbumImages();
	}

	@Override
	public void setOverrideAddToBeginning(boolean selected) {
		album.setOverrideAddToBeginning(selected);
	}

	@Override
	public void setOverrideResize(boolean selected) {
		album.setOverrideResize(selected);
	}

	@Override
	public void setOverrideResizeDefault(boolean selected) {
		album.setOverrideResizeDefault(selected);
	}

	@Override
	public void moveAlbumTo(StatusUpdate listener, Album newParent) {
		album.moveAlbumTo(listener, newParent);
	}

	@Override
	public Gallery getGallery() {
		return this.album.getGallery();
	}

	@Override
	public Album getAlbum() {
		return this.album;
	}

	@Override
	public void startSlideshow() {
		mainFrame.slideshow();
	}

	@Override
	public boolean hasCapability(GalleryCommCapabilities capability) {
		return album.getGallery().getComm(mainFrame.jStatusBar).hasCapability(mainFrame.jStatusBar, capability);
	}

	@Override
	public int getOverrideResizeDimension() {
		return album.getOverrideResizeDimension();
	}

	@Override
	public void setOverrideResizeDimension(int dimension) {
		album.setOverrideResizeDimension(dimension);

	}
}
