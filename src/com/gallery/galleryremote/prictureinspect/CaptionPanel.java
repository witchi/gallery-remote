package com.gallery.galleryremote.prictureinspect;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import com.gallery.galleryremote.util.log.Logger;

class CaptionPanel extends JScrollPane {

	private static final long serialVersionUID = -5578520363869492620L;
	private static final Logger LOGGER = Logger.getLogger(CaptionPanel.class);
	private PictureFieldTextArea jCaption;

	CaptionPanel() {
		LOGGER.fine("Creating class instance...");
		setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		setBorder(null);
		getViewport().add((Component) getCaption(), null);
		setBorder(BorderFactory.createEtchedBorder());
	}

	PictureFieldTextArea getCaption() {
		if (jCaption == null) {
			jCaption = new PictureFieldTextAreaImpl();
			jCaption.setLineWrap(true);
			jCaption.setWrapStyleWord(true);
			jCaption.setEditable(false);
			jCaption.setFont(UIManager.getFont("Label.font"));
			jCaption.setBackground(UIManager.getColor("TextField.inactiveBackground"));
		}
		return jCaption;
	}
}
