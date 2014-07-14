package com.gallery.GalleryRemote.prictureinspect;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

class ExtraFieldPanel extends JScrollPane {
	private static final long serialVersionUID = -5578520363769492620L;
	private PictureFieldTextArea jField;

	ExtraFieldPanel() {
		setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		setBorder(null);
		getViewport().add((Component) getField(), null);
		setBorder(BorderFactory.createEtchedBorder());
	}

	PictureFieldTextArea getField() {
		if (jField == null) {
			jField = new PictureFieldTextAreaImpl();
			jField.setLineWrap(true);
			jField.setWrapStyleWord(true);
			jField.setEditable(false);
			jField.setFont(UIManager.getFont("Label.font"));
			jField.setBackground(UIManager.getColor("TextField.inactiveBackground"));
		}
		return jField;
	}
}
