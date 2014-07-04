package com.gallery.GalleryRemote.prictureinspect;

import javax.swing.Action;

public interface PictureInspectorController {

	// called by view
	Action getNextFocusAction();
	Action getPrevFocusAction();
	Action getNextPictureAction();
	Action getPrevPictureAction();
}
