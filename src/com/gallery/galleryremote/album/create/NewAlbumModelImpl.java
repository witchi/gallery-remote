package com.gallery.galleryremote.album.create;

import java.util.ArrayList;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import com.gallery.galleryremote.model.Album;
import com.gallery.galleryremote.model.Gallery;
import com.gallery.galleryremote.util.AbstractModel;
import com.gallery.galleryremote.util.log.Logger;

public class NewAlbumModelImpl extends AbstractModel implements NewAlbumModel {

	private static final Logger LOGGER = Logger.getLogger(NewAlbumModelImpl.class);
	private static final String ILLEGAL_CHARACTERS = "[ \\\\/*?\"'&|.+#]";

	private final Gallery gallery;
	private Album selectedAlbum;

	private Document title;
	private Document name;
	private Document description;

	private Album newAlbum;

	public NewAlbumModelImpl(Gallery gallery, Album defaultAlbum) {
		LOGGER.fine("Creating class instance...");
		
		this.gallery = gallery;
		this.selectedAlbum = defaultAlbum == null ? (Album) gallery.getRoot() : defaultAlbum;

		this.title = new PlainDocument();
		this.name = new PlainDocument();
		this.description = new PlainDocument();
	}
	
	private String getDocumentText(Document d) {
		try {
			return d.getText(0, d.getLength());
		} catch (BadLocationException e) {
			return "";
		}
	}
	
	@Override
	public Album getNewAlbum() {
		if (newAlbum == null) {
			newAlbum = new Album(gallery);
			newAlbum.setName(getDocumentText(name));
			newAlbum.setTitle(getDocumentText(title));
			newAlbum.setCaption(getDocumentText(description));
			selectedAlbum.getGallery().insertNodeInto(newAlbum, selectedAlbum, selectedAlbum.getChildCount());
		}
		return newAlbum;
	}

	@Override
	public Document getTitle() {
		return this.title;
	}
	
	@Override
	public Document getName() {
		return this.name;
	}

	@Override
	public void setDefaultName(Document d) {
		try {
			name.insertString(0, getLegalString(getDocumentText(d)), null);
		} catch (BadLocationException e) {
			// do nothing
		}
	}
	
	@Override
	public Document getDescription() {
		return this.description;
	}

	@Override
	public String getGalleryUri() {
		return gallery.toString();
	}

	@Override
	public ArrayList<Album> getAlbumList() {
		return gallery.getFlatAlbumList();
	}

	@Override
	public Album getSelectedAlbum() {
		return selectedAlbum;
	}

	@Override
	public void setSelectedAlbum(Album album) {
		this.selectedAlbum = album;
	}

	@Override
	public Album getParentAlbum() {
		return selectedAlbum;
	}

	private String getLegalString(final String text) {
		String r = text.toLowerCase();
		r = r.replaceAll("[&]", "and");
		r = r.replaceAll(ILLEGAL_CHARACTERS, "_");
		return r.replaceAll("_{2,}", "_");
	}
}
