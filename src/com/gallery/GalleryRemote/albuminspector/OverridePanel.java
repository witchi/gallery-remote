package com.gallery.GalleryRemote.albuminspector;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import com.gallery.GalleryRemote.prefs.UploadPanel;
import com.gallery.GalleryRemote.util.GRI18n;

public class OverridePanel extends JPanel {

	private static final long serialVersionUID = 4874157104146489889L;
	private static final String MODULE = "AlbmInspec";

	private GridBagConstraints jResizeBeforeUploadConstraints;
	private GridBagConstraints jResizeToDefaultConstraints;
	private GridBagConstraints jResizeToForceConstraints;
	private GridBagConstraints jResizeToConstraints;
	private GridBagConstraints jBeginningConstraints;

	private JCheckBox jResizeBeforeUpload;
	private JRadioButton jResizeToDefault;
	private JRadioButton jResizeToForce;
	private AlbumFieldComboBoxImpl jResizeTo;
	private JCheckBox jBeginning;

	private ButtonGroup buttonGroup;

	public OverridePanel() {
		initUI();
	}

	private void initUI() {
		setLayout(new GridBagLayout());
		setBorder(getPanelBorder());

		add(getResizeBeforeUploadCheckBox(), getResizeBeforeUploadConstraints());
		add(getResizeToDefaultRadioButton(), getResizeToDefaultConstraints());
		add(getResizeToForceRadioButton(), getResizeToForceConstraints());
		add((Component) getResizeToComboBox(), getResizeToConstraints());
		add(getBeginningCheckBox(), getBeginningConstraints());

		getButtonGroup().add(getResizeToDefaultRadioButton());
		getButtonGroup().add(getResizeToForceRadioButton());
	}

	void setEnabledContent(boolean enabled) {
		getResizeToDefaultRadioButton().setEnabled(enabled);
		getResizeToForceRadioButton().setEnabled(enabled);
		getResizeBeforeUploadCheckBox().setEnabled(enabled);
		getResizeToComboBox().setEnabled(enabled);
		getBeginningCheckBox().setEnabled(enabled);

		if (enabled) {
			getResizeToComboBox().setBackground(UIManager.getColor("TextField.background"));
		} else {
			getResizeToComboBox().setBackground(UIManager.getColor("TextField.inactiveBackground"));
		}
	}

	private ButtonGroup getButtonGroup() {
		if (buttonGroup == null) {
			buttonGroup = new ButtonGroup();
		}
		return buttonGroup;
	}

	private Border getPanelBorder() {
		Border etched = BorderFactory.createEtchedBorder(Color.white, new Color(148, 145, 140));
		return new TitledBorder(etched, GRI18n.getString(MODULE, "Override"));
	}

	AlbumFieldComboBox getResizeToComboBox() {
		if (jResizeTo == null) {
			jResizeTo = new AlbumFieldComboBoxImpl(UploadPanel.defaultSizes);
			jResizeTo.setToolTipText(GRI18n.getString(MODULE, "res2W"));
			jResizeTo.setEditable(true);
			jResizeTo.setRenderer(new UploadPanel.SizeListRenderer()); // TODO:
																							// refactor
																							// it ?
		}
		return jResizeTo;
	}

	JRadioButton getResizeToForceRadioButton() {
		if (jResizeToForce == null) {
			jResizeToForce = new JRadioButton();
			jResizeToForce.setToolTipText(GRI18n.getString(MODULE, "res2FrcTip"));
			jResizeToForce.setText(GRI18n.getString(MODULE, "res2Frc"));
		}
		return jResizeToForce;
	}

	JRadioButton getResizeToDefaultRadioButton() {
		if (jResizeToDefault == null) {
			jResizeToDefault = new JRadioButton();
			jResizeToDefault.setToolTipText(GRI18n.getString(MODULE, "res2Def"));
			jResizeToDefault.setText(GRI18n.getString(MODULE, "res2Def"));
		}
		return jResizeToDefault;
	}

	JCheckBox getBeginningCheckBox() {
		if (jBeginning == null) {
			jBeginning = new JCheckBox();
			jBeginning.setText(GRI18n.getString(MODULE, "Beginning"));
		}
		return jResizeBeforeUpload;
	}

	JCheckBox getResizeBeforeUploadCheckBox() {
		if (jResizeBeforeUpload == null) {
			jResizeBeforeUpload = new JCheckBox();
			jResizeBeforeUpload.setToolTipText(GRI18n.getString(MODULE, "resBfrUpldTip"));
			jResizeBeforeUpload.setText(GRI18n.getString(MODULE, "resBfrUpld"));
		}
		return jResizeBeforeUpload;
	}

	private GridBagConstraints getResizeBeforeUploadConstraints() {
		if (jResizeBeforeUploadConstraints == null) {
			jResizeBeforeUploadConstraints = new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
					new Insets(0, 0, 0, 0), 0, 0);
		}
		return jResizeBeforeUploadConstraints;
	}

	private GridBagConstraints getResizeToDefaultConstraints() {
		if (jResizeToDefaultConstraints == null) {
			jResizeToDefaultConstraints = new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
					new Insets(0, 20, 0, 0), 0, 0);
		}
		return jResizeToDefaultConstraints;
	}

	private GridBagConstraints getResizeToForceConstraints() {
		if (jResizeToForceConstraints == null) {
			jResizeToForceConstraints = new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
					new Insets(0, 20, 0, 0), 0, 0);
		}
		return jResizeToForceConstraints;
	}

	private GridBagConstraints getResizeToConstraints() {
		if (jResizeToConstraints == null) {
			jResizeToConstraints = new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
					new Insets(0, 0, 0, 0), 0, 0);
		}
		return jResizeToConstraints;
	}

	private GridBagConstraints getBeginningConstraints() {
		if (jBeginningConstraints == null) {
			jBeginningConstraints = new GridBagConstraints(0, 3, 4, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
					new Insets(0, 0, 0, 0), 0, 0);
		}
		return jBeginningConstraints;
	}
}
