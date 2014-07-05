package com.gallery.GalleryRemote.prictureinspect;

import java.util.Collection;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.text.Document;

public interface PictureInspector {

	// called by view
	Collection<PictureFieldTextArea> setExtraFields(Map<String, Document> docList);

	void removeExtraFields();
	
	AbstractButton getDeleteButton();
	
	AbstractButton getUpButton();
	
	AbstractButton getDownButton();
	
	AbstractButton getRotateLeftButton();
	
	AbstractButton getRotateRightButton();
	
	AbstractButton getFlipButton();
	
	PictureFieldTextArea getCaption(); 
	
	// called by controller
	void setEnabled(boolean enabled);
	void refreshWithOnePicture(PictureInspectorDTO dto);
	void refreshWithoutPicture();
	void refreshWithMultiplePictures(PictureInspectorDTO dto);
	
}
