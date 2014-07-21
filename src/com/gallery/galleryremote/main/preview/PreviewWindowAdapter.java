package com.gallery.galleryremote.main.preview;

import java.awt.Frame;
import java.awt.event.WindowAdapter;

import com.gallery.galleryremote.GalleryRemote;
import com.gallery.galleryremote.main.MainFrame;

class PreviewWindowAdapter extends WindowAdapter {

	public PreviewWindowAdapter() {
	}

	@Override
	public void windowActivated(java.awt.event.WindowEvent e) {
		MainFrame mainFrame = (MainFrame) GalleryRemote.instance().getMainFrame();
		Preview previewFrame = (Preview) e.getWindow();

		if (mainFrame.getActivating() == previewFrame) {
			mainFrame.setActivating(null);
			return;
		}

		if (mainFrame.getActivating() == null && mainFrame.isVisible()) {
			/*
			 * WindowListener mfWindowListener = mainFrame.getWindowListeners()[0];
			 * WindowListener pWindowListener = getWindowListeners()[0];
			 * removeWindowListener(pWindowListener);
			 * mainFrame.removeWindowListener(mfWindowListener);
			 */

			mainFrame.setActivating((Frame) previewFrame);
			mainFrame.toFront();
			previewFrame.toFront();

			/*
			 * addWindowListener(pWindowListener);
			 * mainFrame.addWindowListener(mfWindowListener);
			 */
		}
	}

}
