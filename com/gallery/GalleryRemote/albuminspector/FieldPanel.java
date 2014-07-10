package com.gallery.GalleryRemote.albuminspector;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

class FieldPanel extends JScrollPane {
	private static final long serialVersionUID = -5578520365769492620L;
	private AlbumFieldTextArea jField;

	FieldPanel() {
		setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		setBorder(null);
		getViewport().add((Component) getField(), null);
		setBorder(BorderFactory.createEtchedBorder());
	}

	AlbumFieldTextArea getField() {
		if (jField == null) {
			jField = new AlbumFieldTextAreaImpl();
			jField.setLineWrap(true);
			jField.setWrapStyleWord(true);
			jField.setEditable(false);
			jField.setFont(UIManager.getFont("Label.font"));
			jField.setBackground(UIManager.getColor("TextField.inactiveBackground"));
		}
		return jField;
	}

	void setEnabledContent(boolean enabled) {
		getField().setEnabled(enabled);
	}
}
