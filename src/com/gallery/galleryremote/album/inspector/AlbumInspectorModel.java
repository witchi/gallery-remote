package com.gallery.galleryremote.album.inspector;

import com.gallery.galleryremote.model.Album;
import com.gallery.galleryremote.model.Gallery;
import com.gallery.galleryremote.statusbar.StatusUpdate;
import com.gallery.galleryremote.GalleryCommCapabilities;

public interface AlbumInspectorModel {

	void setAlbum(Album album);

	void setOverrideAddToBeginning(boolean selected);

	void setOverrideResize(boolean selected);

	void setOverrideResizeDefault(boolean selected);

	void moveAlbumTo(StatusUpdate listener, Album newParent);

	Gallery getGallery();

	Album getAlbum();

	void fetchAlbumImages();

	// TODO: maybe it is better to send an event to a listener (of class
	// MainFrame)
	void startSlideshow();

	boolean hasCapability(GalleryCommCapabilities capability);

	int getOverrideResizeDimension();

	void setOverrideResizeDimension(int dimension);
}
