package com.gallery.galleryremote;

import java.awt.Image;
import java.io.File;
import java.util.Iterator;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JList;

import com.gallery.galleryremote.model.Album;
import com.gallery.galleryremote.model.Gallery;
import com.gallery.galleryremote.model.Picture;
import com.gallery.galleryremote.statusbar.StatusUpdate;

/**
 * @author paour
 * @version Jan 14, 2004
 */
public interface GalleryRemoteCore {
	public void startup();

	public void shutdown();
	public void shutdown(boolean shutdownOs);

	public void flushMemory();
	public void preloadThumbnails(Iterator<Picture> pictures);
	public Image getThumbnail(Picture p);
	public StatusUpdate getMainStatusUpdate();

	public void thumbnailLoadedNotify();
	public void setInProgress(boolean inProgress);

	public void addPictures(File[] files, int index, boolean select);
	public void addPictures(Picture[] pictures, int index, boolean select);

	public DefaultComboBoxModel<Gallery> getGalleries();
	public Album getCurrentAlbum();

	public JList<Picture> getPicturesList();
}
