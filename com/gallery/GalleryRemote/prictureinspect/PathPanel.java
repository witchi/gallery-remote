package com.gallery.GalleryRemote.prictureinspect;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

public class PathPanel extends JScrollPane {
	private static final long serialVersionUID = -5578520363849492620L;
	private JTextArea jPath;

	public PathPanel() {
		setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		setBorder(null);
		getViewport().add(getPath(), null);
	}

	public JTextArea getPath() {
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
