package com.gallery.GalleryRemote.prictureinspect;

import java.awt.Component;

import javax.swing.JScrollPane;
import javax.swing.UIManager;

public class CaptionPanel extends JScrollPane {
	private static final long serialVersionUID = -5578520363869492620L;

	PictureFieldTextArea jCaption;

	public CaptionPanel() {
		setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		setBorder(null);
		getViewport().add((Component) jCaption, null);
	}

	public PictureFieldTextArea getCaption() {
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
