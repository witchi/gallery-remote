package com.gallery.galleryremote.album.create;

import com.gallery.galleryremote.model.Album;
import com.gallery.galleryremote.model.Gallery;
import com.gallery.galleryremote.util.AbstractModel;
import com.gallery.galleryremote.util.log.Logger;

public class NewAlbumModelImpl extends AbstractModel implements NewAlbumModel {

	private static final Logger LOGGER = Logger.getLogger(NewAlbumModelImpl.class);
	private static final String ILLEGAL_CHARACTERS = "[ \\\\/*?\"'&|.+#]";

	private final Gallery gallery;
	private final Album defaultAlbum;
	private Album newAlbum = null;
	private Album parentAlbum = null;

	public NewAlbumModelImpl(Gallery gallery, Album defaultAlbum) {
		LOGGER.fine("Creating class instance...");
		this.gallery = gallery;
		this.defaultAlbum = defaultAlbum;
	}

	public Album getNewAlbum() {
		return newAlbum;
	}

	public Album getParentAlbum() {
		return parentAlbum;
	}

	static String getDefaultName(final String text) {
		String r = text.toLowerCase();
		r = r.replaceAll("[&]", "and");
		r = r.replaceAll(ILLEGAL_CHARACTERS, "_");
		return r.replaceAll("_{2,}", "_");
	}
}
