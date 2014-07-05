package com.gallery.GalleryRemote.prictureinspect;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.gallery.GalleryRemote.GalleryRemote;
import com.gallery.GalleryRemote.Log;
import com.gallery.GalleryRemote.util.GRI18n;
import com.gallery.GalleryRemote.util.ImageUtils;

class IconAreaPanel extends JPanel {
	private static final long serialVersionUID = -5451979859370784290L;
	private static final String MODULE = "PictInspec";

	private JLabel jIcon;
	private GridBagConstraints jIconConstraints;
	private JButton jRotateLeftButton;
	private GridBagConstraints jRotateLeftButtonConstraints;
	private JButton jFlipButton;
	private GridBagConstraints jFlipButtonConstraints;
	private JButton jRotateRightButton;
	private GridBagConstraints jRotateRightButtonConstraints;

	private int emptyIconHeight;

	IconAreaPanel() {
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
		Log.log(Log.LEVEL_TRACE, MODULE, "emptyIconHeight: " + getEmptyIconHeight());
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
			jRotateLeftButton.setToolTipText(GRI18n.getString(MODULE, "rotLtTip"));
			jRotateLeftButton.setActionCommand(PictureInspectorActions.ROTATE_LEFT.name());
			jRotateLeftButton.setIcon(GalleryRemote.iLeft);
		}
		return jRotateLeftButton;
	}

	JButton getRotateRightButton() {
		if (jRotateRightButton == null) {
			jRotateRightButton = new JButton();
			jRotateRightButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			jRotateRightButton.setToolTipText(GRI18n.getString(MODULE, "rotRtTip"));
			jRotateRightButton.setActionCommand(PictureInspectorActions.ROTATE_RIGHT.name());
			jRotateRightButton.setIcon(GalleryRemote.iRight);
		}
		return jRotateRightButton;
	}

	JButton getFlipButton() {
		if (jFlipButton == null) {
			jFlipButton = new JButton();
			jFlipButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			jFlipButton.setToolTipText(GRI18n.getString(MODULE, "flipTip"));
			jFlipButton.setActionCommand(PictureInspectorActions.FLIP.name());
			jFlipButton.setIcon(GalleryRemote.iFlip);
		}
		return jFlipButton;
	}

	JLabel getIcon() {
		if (jIcon == null) {
			jIcon = new JLabel();
			jIcon.setHorizontalAlignment(SwingConstants.CENTER);
			jIcon.setHorizontalTextPosition(SwingConstants.CENTER);
			jIcon.setText(GRI18n.getString(MODULE, "icon"));
			jIcon.setVerticalTextPosition(SwingConstants.BOTTOM);
		}
		return jIcon;
	}

	int getEmptyIconHeight() {
		return emptyIconHeight;
	}
}
