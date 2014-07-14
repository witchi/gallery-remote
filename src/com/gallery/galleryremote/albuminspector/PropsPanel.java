package com.gallery.galleryremote.albuminspector;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import com.gallery.galleryremote.util.GRI18n;

class PropsPanel extends JPanel {

	private static final long serialVersionUID = -4646300634406502185L;
	private static final String MODULE = "AlbmInspec";

	private JLabel jNameLabel;
	private JLabel jTitleLabel;
	private JLabel jPictureLabel;
	private JLabel jSummaryLabel;

	private FieldPanel jTitle;
	private AlbumFieldTextArea jPictures;
	private FieldPanel jName;
	private FieldPanel jSummary;

	private JLabel jApply;
	private JButton jMove;

	private GridBagConstraints nameLabelConstraints;
	private GridBagConstraints applyLabelConstraints;
	private GridBagConstraints moveButtonConstraints;
	private GridBagConstraints titleLabelConstraints;
	private GridBagConstraints summaryLabelConstraints;
	private GridBagConstraints pictureLabelConstraints;
	private GridBagConstraints summaryFieldConstraints;
	private GridBagConstraints titleFieldConstraints;
	private GridBagConstraints nameFieldConstraints;
	private GridBagConstraints pictureTextAreaConstraints;

	PropsPanel() {
		initUI();
	}

	void setEnabledContent(boolean enabled) {
		getNameField().setEnabledContent(enabled);
		getTitleField().setEnabledContent(enabled);
		getSummaryField().setEnabledContent(enabled);
		getPictureTextArea().setEnabled(enabled);
		getApplyLabel().setEnabled(enabled);
		getMoveButton().setEnabled(enabled);
	}

	private void initUI() {
		setLayout(new GridBagLayout());
		setBorder(getPanelBorder());

		add(getNameLabel(), getNameLabelConstraints());
		add(getNameField(), getNameFieldConstraints());
		add(getTitleLabel(), getTitleLabelConstraints());
		add(getTitleField(), getTitleFieldConstraints());
		add(getSummaryLabel(), getSummaryLabelConstraints());
		add(getSummaryField(), getSummaryFieldConstraints());
		add(getPictureLabel(), getPictureLabelConstraints());
		add((Component) getPictureTextArea(), getPictureTextAreaConstraints());

		add(getApplyLabel(), getApplyLabelConstraints());
		add(getMoveButton(), getMoveButtonConstraints());
	}

	private GridBagConstraints getPictureTextAreaConstraints() {
		if (pictureTextAreaConstraints == null) {
			pictureTextAreaConstraints = new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.HORIZONTAL, new Insets(0, 0, 3, 0), 0, 0);
		}
		return pictureTextAreaConstraints;
	}

	private GridBagConstraints getSummaryFieldConstraints() {
		if (summaryFieldConstraints == null) {
			summaryFieldConstraints = new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0);
		}
		return summaryFieldConstraints;
	}

	private GridBagConstraints getTitleFieldConstraints() {
		if (titleFieldConstraints == null) {
			titleFieldConstraints = new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 3, 0), 0, 0);
		}
		return titleFieldConstraints;
	}

	private GridBagConstraints getNameFieldConstraints() {
		if (nameFieldConstraints == null) {
			nameFieldConstraints = new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 3, 0), 0, 0);
		}
		return nameFieldConstraints;
	}

	private GridBagConstraints getNameLabelConstraints() {
		if (nameLabelConstraints == null) {
			nameLabelConstraints = new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
					new Insets(2, 0, 0, 5), 2, 0);
		}
		return nameLabelConstraints;
	}

	private GridBagConstraints getTitleLabelConstraints() {
		if (titleLabelConstraints == null) {
			titleLabelConstraints = new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
					new Insets(2, 0, 0, 5), 2, 0);
		}
		return titleLabelConstraints;
	}

	private GridBagConstraints getSummaryLabelConstraints() {
		if (summaryLabelConstraints == null) {
			summaryLabelConstraints = new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
					new Insets(2, 0, 0, 5), 0, 0);
		}
		return summaryLabelConstraints;
	}

	private GridBagConstraints getPictureLabelConstraints() {
		if (pictureLabelConstraints == null) {
			pictureLabelConstraints = new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
					new Insets(0, 0, 2, 5), 2, 0);
		}
		return pictureLabelConstraints;
	}

	private GridBagConstraints getApplyLabelConstraints() {
		if (applyLabelConstraints == null) {
			applyLabelConstraints = new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
					new Insets(5, 0, 0, 0), 0, 0);
		}
		return applyLabelConstraints;
	}

	private GridBagConstraints getMoveButtonConstraints() {
		if (moveButtonConstraints == null) {
			moveButtonConstraints = new GridBagConstraints(0, 5, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
					new Insets(5, 0, 0, 0), 0, 0);
		}
		return moveButtonConstraints;
	}

	private Border getPanelBorder() {
		Border etched = BorderFactory.createEtchedBorder(Color.white, new Color(148, 145, 140));
		return new TitledBorder(etched, GRI18n.getString(MODULE, "Props"));
	}

	private JLabel getTitleLabel() {
		if (jTitleLabel == null) {
			jTitleLabel = new JLabel();
			jTitleLabel.setText(GRI18n.getString(MODULE, "Title"));
		}
		return jTitleLabel;
	}

	private JLabel getNameLabel() {
		if (jNameLabel == null) {
			jNameLabel = new JLabel();
			jNameLabel.setText(GRI18n.getString(MODULE, "Name"));
		}
		return jNameLabel;
	}

	private JLabel getSummaryLabel() {
		if (jSummaryLabel == null) {
			jSummaryLabel = new JLabel();
			jSummaryLabel.setText(GRI18n.getString(MODULE, "Summary"));
		}
		return jSummaryLabel;
	}

	private JLabel getPictureLabel() {
		if (jPictureLabel == null) {
			jPictureLabel = new JLabel();
			jPictureLabel.setText(GRI18n.getString(MODULE, "Pictures"));
		}
		return jPictureLabel;
	}

	AlbumFieldTextArea getPictureTextArea() {
		if (jPictures == null) {
			jPictures = new AlbumFieldTextAreaImpl();
			jPictures.setFont(UIManager.getFont("Label.font"));
			jPictures.setEditable(false);
			jPictures.setBackground(UIManager.getColor("TextField.inactiveBackground"));
		}
		return jPictures;
	}

	FieldPanel getSummaryField() {
		if (jSummary == null) {
			jSummary = new FieldPanel();
		}
		return jSummary;
	}

	FieldPanel getTitleField() {
		if (jTitle == null) {
			jTitle = new FieldPanel();
		}
		return jTitle;
	}

	FieldPanel getNameField() {
		if (jName == null) {
			jName = new FieldPanel();
		}
		return jName;
	}

	JLabel getApplyLabel() {
		if (jApply == null) {
			jApply = new JLabel();
			jApply.setText(GRI18n.getString(MODULE, "Apply"));
		}
		return jApply;
	}

	AbstractButton getMoveButton() {
		if (jMove == null) {
			jMove = new JButton();
			jMove.setText(GRI18n.getString(MODULE, "Move"));
		}
		return jMove;
	}

}
