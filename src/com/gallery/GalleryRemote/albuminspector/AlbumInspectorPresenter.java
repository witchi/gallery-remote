package com.gallery.GalleryRemote.albuminspector;

import java.awt.Component;

import javax.swing.Action;

public interface AlbumInspectorPresenter {

	// called indirect by view
	Action getNextFocusAction();

	Action getPrevFocusAction();

	Action getNextPictureAction();

	Action getPrevPictureAction();

	// called by MainFrame
	Component getView();

	void setEnabled(boolean enabled);
}
