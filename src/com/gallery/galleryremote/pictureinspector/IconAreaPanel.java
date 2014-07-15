package com.gallery.galleryremote.pictureinspector;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.gallery.galleryremote.GalleryRemote;
import com.gallery.galleryremote.util.GRI18n;
import com.gallery.galleryremote.util.ImageUtils;
import com.gallery.galleryremote.util.log.Logger;

class IconAreaPanel extends JPanel {

	private static final long serialVersionUID = -5451979859370784290L;
	private static final Logger LOGGER = Logger.getLogger(IconAreaPanel.class);

	private PictureInspectorIcon jIcon;
	private GridBagConstraints jIconConstraints;
	private JButton jRotateLeftButton;
	private GridBagConstraints jRotateLeftButtonConstraints;
	private JButton jFlipButton;
	private GridBagConstraints jFlipButtonConstraints;
	private JButton jRotateRightButton;
	private GridBagConstraints jRotateRightButtonConstraints;

	private int emptyIconHeight;

	IconAreaPanel() {
		LOGGER.fine("Creating class instance...");
		initUI();
	}

	private void initUI() {
		setLayout(new GridBagLayout());
		add(getIcon(), getIconConstraints());

		if (ImageUtils.useJpegtran) {
			add(getRotateLeftButton(), getRotateLeftButtonConstraints());
			add(getFlipButton(), getFlipButtonConstraints());
			add(getRotateRightButton(), getRotateRightButtonConstraints());
		}
		setEmptyIconHeight(getIcon().getPreferredSize().getHeight());
	}

	private void setEmptyIconHeight(double height) {
		this.emptyIconHeight = (int) height;
		LOGGER.fine("emptyIconHeight: " + getEmptyIconHeight());
	}

	private GridBagConstraints getIconConstraints() {
		if (jIconConstraints == null) {
			jIconConstraints = new GridBagConstraints(0, 1, 3, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
					new Insets(0, 0, 0, 0), 0, 0);
		}
		return jIconConstraints;
	}

	private GridBagConstraints getRotateLeftButtonConstraints() {
		if (jRotateLeftButtonConstraints == null) {
			jRotateLeftButtonConstraints = new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
					new Insets(0, 0, 0, 0), 0, 0);
		}
		return jRotateLeftButtonConstraints;
	}

	private GridBagConstraints getFlipButtonConstraints() {
		if (jFlipButtonConstraints == null) {
			jFlipButtonConstraints = new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
					new Insets(0, 0, 0, 0), 0, 0);
		}
		return jFlipButtonConstraints;
	}

	private GridBagConstraints getRotateRightButtonConstraints() {
		if (jRotateRightButtonConstraints == null) {
			jRotateRightButtonConstraints = new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
					new Insets(0, 0, 0, 0), 0, 0);
		}
		return jRotateRightButtonConstraints;
	}

	JButton getRotateLeftButton() {
		if (jRotateLeftButton == null) {
			jRotateLeftButton = new JButton();
			jRotateLeftButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			jRotateLeftButton.setToolTipText(GRI18n.getString(this.getClass().getPackage().getName(), "rotLtTip"));
			jRotateLeftButton.setActionCommand(PictureInspectorActions.ROTATE_LEFT.name());
			jRotateLeftButton.setIcon(GalleryRemote.iLeft);
		}
		return jRotateLeftButton;
	}

	JButton getRotateRightButton() {
		if (jRotateRightButton == null) {
			jRotateRightButton = new JButton();
			jRotateRightButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			jRotateRightButton.setToolTipText(GRI18n.getString(this.getClass().getPackage().getName(), "rotRtTip"));
			jRotateRightButton.setActionCommand(PictureInspectorActions.ROTATE_RIGHT.name());
			jRotateRightButton.setIcon(GalleryRemote.iRight);
		}
		return jRotateRightButton;
	}

	JButton getFlipButton() {
		if (jFlipButton == null) {
			jFlipButton = new JButton();
			jFlipButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			jFlipButton.setToolTipText(GRI18n.getString(this.getClass().getPackage().getName(), "flipTip"));
			jFlipButton.setActionCommand(PictureInspectorActions.FLIP.name());
			jFlipButton.setIcon(GalleryRemote.iFlip);
		}
		return jFlipButton;
	}

	PictureInspectorIcon getIcon() {
		if (jIcon == null) {
			jIcon = new PictureInspectorIcon();
			jIcon.setHorizontalAlignment(SwingConstants.CENTER);
			jIcon.setHorizontalTextPosition(SwingConstants.CENTER);
			jIcon.setVerticalTextPosition(SwingConstants.BOTTOM);
		}
		return jIcon;
	}

	int getEmptyIconHeight() {
		return emptyIconHeight;
	}
}
