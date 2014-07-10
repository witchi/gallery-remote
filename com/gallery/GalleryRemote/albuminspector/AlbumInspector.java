package com.gallery.GalleryRemote.albuminspector;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;

import com.gallery.GalleryRemote.prefs.UploadPanel.ResizeSize;

public interface AlbumInspector {

	void setEnabled(boolean enabled);

	AlbumFieldTextArea getSummaryTextArea();

	AlbumFieldTextArea getTitleTextArea();

	AlbumFieldTextArea getNameTextArea();

	AbstractButton getSlideshowButton();

	AbstractButton getFetchButton();

	AbstractButton getMoveButton();

	JComboBox<ResizeSize> getResizeTo();

	AbstractButton getResizeToForce();

	AbstractButton getResizeToDefault();

	AbstractButton getBeginning();

	AbstractButton getResizeBeforeUpload();

	void refresh(AlbumInspectorDTO dto);

}
