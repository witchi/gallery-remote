package com.gallery.galleryremote.pictureinspector;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import com.gallery.galleryremote.util.log.Logger;

class PictureFieldTextAreaImpl extends JTextArea implements PictureFieldTextArea {

	private static final long serialVersionUID = 6219172601326439093L;
	private static final Logger LOGGER = Logger.getLogger(PictureFieldTextAreaImpl.class);

	public PictureFieldTextAreaImpl() {
		LOGGER.fine("Creating class instance...");
	}

	@Override
	public void addKeyboardListener(PictureInspectorPresenter listener) {
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), listener.getNextFocusAction().getValue(Action.NAME));
		getInputMap()
				.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_MASK), listener.getPrevFocusAction().getValue(Action.NAME));
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), listener.getNextPictureAction().getValue(Action.NAME));
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), listener.getPrevPictureAction().getValue(Action.NAME));
		getActionMap().put(listener.getNextFocusAction().getValue(Action.NAME), listener.getNextFocusAction());
		getActionMap().put(listener.getPrevFocusAction().getValue(Action.NAME), listener.getPrevFocusAction());
		getActionMap().put(listener.getNextPictureAction().getValue(Action.NAME), listener.getNextPictureAction());
		getActionMap().put(listener.getPrevPictureAction().getValue(Action.NAME), listener.getPrevPictureAction());
	}

}
