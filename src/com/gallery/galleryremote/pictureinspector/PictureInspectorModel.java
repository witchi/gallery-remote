package com.gallery.galleryremote.pictureinspector;

import java.awt.Image;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

import javax.swing.text.Document;

import com.gallery.galleryremote.model.Picture;
import com.gallery.galleryremote.GalleryCommCapabilities;

public interface PictureInspectorModel {

	// called by MainFrame
	void setPictureList(List<Picture> pictureList);

	// called by presenter
	void deleteSelectedPictures();

	void movePicturesUp();

	void movePicturesDown();

	void rotatePictureLeft();

	void rotatePictureRight();

	void flipPicture();

	Map<String, Document> getExtraFieldDocuments(Picture p);

	Map<String, Document> getFieldDocuments();

	void documentUpdate(Document document);

	List<Picture> getPictureList();

	void removeExtraFields();

	Image getThumbnail(Picture picture);

	void addActionListener(ActionListener l);

	boolean hasCapability(Picture picture, GalleryCommCapabilities capability);

	void setDocumentText(String name, String text);
}
