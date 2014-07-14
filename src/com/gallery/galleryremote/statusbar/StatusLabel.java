package com.gallery.galleryremote.statusbar;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

class StatusLabel extends JPanel {

	private static final long serialVersionUID = -8417286688626011990L;
	private JLabel label;
	private GridBagConstraints labelConstraints;

	StatusLabel() {
		initUI();
	}

	private void initUI() {
		setLayout(new GridBagLayout());
		add(getLabel(), getLabelConstraints());
		setBorder(BorderFactory.createEtchedBorder());
	}

	private JLabel getLabel() {
		if (label == null) {
			label = new JLabel();
			label.setMinimumSize(new Dimension(100, 20));
			label.setPreferredSize(new Dimension(100, 20));
		}
		return label;
	}

	void setText(String logMessage) {
		getLabel().setText(logMessage);
	}

	private GridBagConstraints getLabelConstraints() {
		if (labelConstraints == null) {
			labelConstraints = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(
					0, 3, 1, 5), 0, 0);
		}
		return labelConstraints;
	}
}
