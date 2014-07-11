/*
 *  Gallery Remote - a File Upload Utility for Gallery
 *
 *  Gallery - a web based photo album viewer and editor
 *  Copyright (C) 2000-2001 Bharat Mediratta
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or (at
 *  your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.gallery.GalleryRemote.albuminspector;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import com.gallery.GalleryRemote.prefs.PreferenceNames;
import com.gallery.GalleryRemote.util.GRI18n;

/**
 * Bean inspector for albums
 * 
 * @author paour
 * @author arothe
 * @created August 16, 2002
 */
public class AlbumInspectorImpl extends JPanel implements PreferenceNames, AlbumInspector {

	private static final long serialVersionUID = 8406883123834053126L;
	private static final String MODULE = "AlbmInspec";

	private JPanel jSpacer;

	private JButton jFetchButton;
	private JButton jSlideshowButton;

	private OverridePanel jOverridePanel;
	private PropsPanel jPropsPanel;

	private GridBagConstraints jPropsPanelConstraints;
	private GridBagConstraints jOverridePanelConstraints;
	private GridBagConstraints jFetchButtonConstraints;
	private GridBagConstraints jSlideshowButtonConstraints;
	private GridBagConstraints jSpacerConstraints;

	public AlbumInspectorImpl() {
		initUI();
	}

	private JPanel getSpacer() {
		if (jSpacer == null) {
			jSpacer = new JPanel();
		}
		return jSpacer;
	}

	@Override
	public AbstractButton getFetchButton() {
		if (jFetchButton == null) {
			jFetchButton = new JButton();
			jFetchButton.setText(GRI18n.getString(MODULE, "Fetch"));
		}
		return jFetchButton;
	}

	@Override
	public AbstractButton getSlideshowButton() {
		if (jSlideshowButton == null) {
			jSlideshowButton = new JButton();
			jSlideshowButton.setText(GRI18n.getString(MODULE, "Slideshow"));
		}
		return jSlideshowButton;
	}

	private OverridePanel getOverridePanel() {
		if (jOverridePanel == null) {
			jOverridePanel = new OverridePanel();
		}
		return jOverridePanel;
	}

	private PropsPanel getPropsPanel() {
		if (jPropsPanel == null) {
			jPropsPanel = new PropsPanel();
		}
		return jPropsPanel;
	}

	private GridBagConstraints getPropsPanelConstraints() {
		if (jPropsPanelConstraints == null) {
			jPropsPanelConstraints = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0);
		}
		return jPropsPanelConstraints;
	}

	private GridBagConstraints getOverridePanelConstraints() {
		if (jOverridePanelConstraints == null) {
			jOverridePanelConstraints = new GridBagConstraints(0, 1, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0);
		}
		return jOverridePanelConstraints;
	}

	private GridBagConstraints getFetchButtonConstraints() {
		if (jFetchButtonConstraints == null) {
			jFetchButtonConstraints = new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
					new Insets(5, 0, 0, 0), 0, 0);
		}
		return jFetchButtonConstraints;
	}

	private GridBagConstraints getSpacerConstraints() {
		if (jSpacerConstraints == null) {
			jSpacerConstraints = new GridBagConstraints(0, 5, 1, 1, 1.0, 0.1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
					0, 0, 0, 0), 0, 0);
		}
		return jSpacerConstraints;
	}

	private GridBagConstraints getSlideshowButtonConstraints() {
		if (jSlideshowButtonConstraints == null) {
			jSlideshowButtonConstraints = new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
					new Insets(5, 0, 0, 0), 0, 0);
		}
		return jSlideshowButtonConstraints;
	}

	private void initUI() {
		setLayout(new GridBagLayout());

		add(getPropsPanel(), getPropsPanelConstraints());
		add(getOverridePanel(), getOverridePanelConstraints());
		add(getFetchButton(), getFetchButtonConstraints());
		add(getSlideshowButton(), getSlideshowButtonConstraints());
		add(getSpacer(), getSpacerConstraints());

		this.setMinimumSize(new Dimension(150, 0));
	}

	void setEnabledContent(boolean enabled) {
		getPropsPanel().setEnabledContent(enabled);
		getOverridePanel().setEnabledContent(enabled);
		getFetchButton().setEnabled(enabled);
		getSlideshowButton().setEnabled(enabled);
	}

	@Override
	public void setEnabled(boolean enabled) {
		setEnabledContent(enabled);
		super.setEnabled(enabled);
	}

	@Override
	public AlbumFieldTextArea getSummaryTextArea() {
		return getPropsPanel().getSummaryField().getField();
	}

	@Override
	public AlbumFieldTextArea getTitleTextArea() {
		return getPropsPanel().getTitleField().getField();
	}

	@Override
	public AlbumFieldTextArea getNameTextArea() {
		return getPropsPanel().getNameField().getField();
	}

	@Override
	public AbstractButton getMoveButton() {
		return getPropsPanel().getMoveButton();
	}

	@Override
	public AlbumFieldComboBox getResizeTo() {
		return getOverridePanel().getResizeToComboBox();
	}

	@Override
	public AbstractButton getResizeToForce() {
		return getOverridePanel().getResizeToForceRadioButton();
	}

	@Override
	public AbstractButton getResizeToDefault() {
		return getOverridePanel().getResizeToDefaultRadioButton();
	}

	@Override
	public AbstractButton getBeginning() {
		return getOverridePanel().getBeginningCheckBox();
	}

	@Override
	public AbstractButton getResizeBeforeUpload() {
		return getOverridePanel().getResizeBeforeUploadCheckBox();
	}

	private JLabel getApplyLabel() {
		return getPropsPanel().getApplyLabel();
	}
	
	private AlbumFieldTextArea getPictureTextArea() {
		return getPropsPanel().getPictureTextArea();
	}
	
	@Override
	public void refresh(AlbumInspectorDTO dto) {

		// TODO: can we move the if-statements into the presenter?
		// TODO: add more settings, use dto
		if (dto.hasAlbum() && getResizeBeforeUpload().isSelected()) {
			getResizeToDefault().setEnabled(true);
			getResizeToForce().setEnabled(true);

			if (getResizeToForce().isSelected()) {
				getResizeTo().setEnabled(true);
				getResizeTo().setBackground(UIManager.getColor("TextField.background"));
			} else {
				getResizeTo().setEnabled(false);
				getResizeTo().setBackground(UIManager.getColor("TextField.inactiveBackground"));
			}
		} else {
			getResizeToDefault().setEnabled(false);
			getResizeToForce().setEnabled(false);
			getResizeTo().setEnabled(false);
			getResizeTo().setBackground(UIManager.getColor("TextField.inactiveBackground"));
		}

		// todo: protocol support
		getApplyLabel().setEnabled(false);
		getNameTextArea().setEditable(false);
		getNameTextArea().setBackground(UIManager.getColor("TextField.inactiveBackground"));
		getTitleTextArea().setEditable(false);
		getTitleTextArea().setBackground(UIManager.getColor("TextField.inactiveBackground"));
		getSummaryTextArea().setEditable(false);
		getSummaryTextArea().setBackground(UIManager.getColor("TextField.inactiveBackground"));
		getPictureTextArea().setEditable(false);
		getPictureTextArea().setBackground(UIManager.getColor("TextField.inactiveBackground"));
	}
}
