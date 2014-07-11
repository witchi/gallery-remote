package com.gallery.GalleryRemote.albuminspector;

import java.util.Vector;

import com.gallery.GalleryRemote.GalleryCommCapabilities;
import com.gallery.GalleryRemote.model.Album;
import com.gallery.GalleryRemote.model.Gallery;
import com.gallery.GalleryRemote.statusbar.StatusUpdate;

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
	
	String getName();
	
	boolean hasParent(Album parent);
	
	Vector<Album> removeAlbumFromList(Vector<Album> list, boolean deep);
}
