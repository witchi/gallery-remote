package com.gallery.galleryremote.album.inspector;

import java.awt.event.ActionEvent;

import com.gallery.galleryremote.main.MainFrame;
import com.gallery.galleryremote.model.Album;
import com.gallery.galleryremote.model.Gallery;
import com.gallery.galleryremote.statusbar.StatusUpdate;
import com.gallery.galleryremote.util.AbstractModel;
import com.gallery.galleryremote.util.log.Logger;
import com.gallery.galleryremote.GalleryCommCapabilities;

public class AlbumInspectorModelImpl extends AbstractModel implements AlbumInspectorModel {

	private static final Logger LOGGER = Logger.getLogger(AlbumInspectorModelImpl.class); 
	
	// TODO: remove the direct reference to mainframe by listeners
	private MainFrame mainFrame;
	private Album album;

	public AlbumInspectorModelImpl(MainFrame mainFrame) {
		LOGGER.fine("Creating class instance...");
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
		return album.getGallery().getComm(mainFrame.getStatusBar()).hasCapability(mainFrame.getStatusBar(), capability);
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
