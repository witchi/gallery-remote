package com.gallery.galleryremote.pictureinspector;

import java.awt.Component;

import javax.swing.Action;

public interface PictureInspectorPresenter {

	// called by view
	Action getNextFocusAction();

	Action getPrevFocusAction();

	Action getNextPictureAction();

	Action getPrevPictureAction();

	// called by MainFrame
	void setEnabled(boolean enabled);

	Component getView();
}
