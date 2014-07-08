package com.gallery.GalleryRemote.statusbar;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

class StatusProgress extends JPanel {

	private static final long serialVersionUID = 5584670140758675762L;
	private JProgressBar bar;
	private final int progressWidth;

	StatusProgress(int progressWidth) {
		this.progressWidth = progressWidth;

		add(getProgressBar());
		setBorder(BorderFactory.createEtchedBorder());
	}

	private JProgressBar getProgressBar() {
		if (bar == null) {
			JProgressBar bar = new JProgressBar();
			bar.setMinimumSize(new Dimension(10, 20));
			bar.setPreferredSize(new Dimension(progressWidth, 20));
			bar.setMaximumSize(new Dimension(progressWidth, 20));
			bar.setStringPainted(false);
			bar.setBorderPainted(false);
		}
		return bar;
	}

	void setIndeterminate(boolean indeterminated) {
		getProgressBar().setIndeterminate(indeterminated);
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
