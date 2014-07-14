package com.gallery.GalleryRemote.albuminspector;

import javax.swing.AbstractButton;

public interface AlbumInspector {

	void setEnabled(boolean enabled);

	AlbumFieldTextArea getSummaryTextArea();

	AlbumFieldTextArea getTitleTextArea();

	AlbumFieldTextArea getNameTextArea();

	AbstractButton getSlideshowButton();

	AbstractButton getFetchButton();

	AbstractButton getMoveButton();

	AlbumFieldComboBox getResizeTo();

	AbstractButton getResizeToForce();

	AbstractButton getResizeToDefault();

	AbstractButton getBeginning();

	AbstractButton getResizeBeforeUpload();

	void refresh(AlbumInspectorDTO dto);

}
