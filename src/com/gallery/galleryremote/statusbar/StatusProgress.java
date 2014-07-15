package com.gallery.galleryremote.statusbar;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import com.sun.istack.internal.logging.Logger;

class StatusProgress extends JPanel {

	private static final long serialVersionUID = 5584670140758675762L;
	private static final Logger LOGGER = Logger.getLogger(StatusProgress.class);

	private JProgressBar bar;
	private GridBagConstraints barConstraints;
	private final int progressWidth;

	StatusProgress(int progressWidth) {
		LOGGER.fine("Creating class instance...");
		this.progressWidth = progressWidth;
		initUI();
	}

	private void initUI() {
		setLayout(new GridBagLayout());
		add(getProgressBar(), getBarConstraints());
		setBorder(BorderFactory.createEtchedBorder());
	}

	private JProgressBar getProgressBar() {
		if (bar == null) {
			bar = new JProgressBar();
			bar.setMinimumSize(new Dimension(10, 20));
			bar.setPreferredSize(new Dimension(progressWidth, 20));
			bar.setMaximumSize(new Dimension(progressWidth, 20));
			bar.setStringPainted(false);
			bar.setBorderPainted(false);
		}
		return bar;
	}

	private GridBagConstraints getBarConstraints() {
		if (barConstraints == null) {
			barConstraints = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
					new Insets(0, 5, 1, 5), 0, 0);
		}
		return barConstraints;
	}

	void setIndeterminate(boolean indeterminated) {
		getProgressBar().setIndeterminate(indeterminated);
	}

	boolean isIndeterminate() {
		return getProgressBar().isIndeterminate();
	}

	void setMinimum(int n) {
		getProgressBar().setMinimum(n);
	}

	int getMinimum() {
		return getProgressBar().getMinimum();
	}

	void setMaximum(int n) {
		getProgressBar().setMaximum(n);
	}

	void setValue(int n) {
		getProgressBar().setValue(n);
	}

}
