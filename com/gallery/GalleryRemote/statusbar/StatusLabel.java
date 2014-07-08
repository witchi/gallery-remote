package com.gallery.GalleryRemote.statusbar;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

class StatusLabel extends JPanel {

	private static final long serialVersionUID = -8417286688626011990L;
	private JLabel label;

	StatusLabel() {
		initUI();
	}

	private void initUI() {
		add(getLabel());
		setBorder(BorderFactory.createEtchedBorder());
	}

	private JLabel getLabel() {
		if (label == null) {
			JLabel label = new JLabel();
			label.setMinimumSize(new Dimension(100, 20));
			label.setPreferredSize(new Dimension(100, 20));
		}
		return label;
	}

	void setText(String logMessage) {
		getLabel().setText(logMessage);
	}

}
