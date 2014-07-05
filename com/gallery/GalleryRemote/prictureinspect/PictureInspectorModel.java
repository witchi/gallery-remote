package com.gallery.GalleryRemote.prictureinspect;

import java.awt.Image;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

import javax.swing.text.Document;

import com.gallery.GalleryRemote.model.Picture;

public interface PictureInspectorModel {

	// called by MainFrame (TODO: should we change that to presenter)
	void setPictureList(List<Picture> pictureList);

	// called by presenter
	void deleteSelectedPictures();

	void movePicturesUp();

	void movePicturesDown();

	void rotatePictureLeft();

	void rotatePictureRight();

	void flipPicture();

	Map<String, Document> getExtraFields();

	void documentUpdate(Document document);

	List<Picture> getPictureList();

	void removeExtraFields();

	Image getThumbnail(Picture picture);

	void setExtraFieldsForPicture(Picture picture);

	void addActionListener(ActionListener l);
	
	boolean hasCapability(Picture picture, int capability);
}
