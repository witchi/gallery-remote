package com.gallery.galleryremote.pictureinspector;

import java.util.Collection;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.text.Document;

/**
 * This interface is used by the PictureInspectorPresenter to access the view.
 * 
 * @author arothe
 */
interface PictureInspector {

	void setEnabled(boolean enabled);

	boolean isEnabled();

	void refresh(PictureInspectorDTO dto);

	int getEmptyIconHeight();

	int getIconTextGap();

	void setFieldDocuments(Map<String, Document> map);

	void setExtraFields(Map<String, Document> docList);

	Collection<PictureFieldTextArea> getExtraFields();

	void removeExtraFields();

	PictureFieldTextArea getCaption();

	AbstractButton getDeleteButton();

	AbstractButton getUpButton();

	AbstractButton getDownButton();

	AbstractButton getRotateLeftButton();

	AbstractButton getRotateRightButton();

	AbstractButton getFlipButton();
}
