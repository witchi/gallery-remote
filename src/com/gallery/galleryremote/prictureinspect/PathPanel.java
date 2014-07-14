package com.gallery.galleryremote.prictureinspect;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

class PathPanel extends JScrollPane {
	private static final long serialVersionUID = -5578520363849492620L;
	private JTextArea jPath;

	PathPanel() {
		setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		setBorder(null);
		getViewport().add(getPath(), null);
	}

	JTextArea getPath() {
		if (jPath == null) {
			jPath = new JTextArea();
			jPath.setBackground(UIManager.getColor("TextField.inactiveBackground"));
			jPath.setFont(UIManager.getFont("Label.font"));
			jPath.setEditable(false);
			jPath.setLineWrap(true);
		}
		return jPath;
	}
}
