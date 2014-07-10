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
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.UIManager;

import com.gallery.GalleryRemote.GalleryRemote;
import com.gallery.GalleryRemote.Log;
import com.gallery.GalleryRemote.model.Album;
import com.gallery.GalleryRemote.prefs.PreferenceNames;
import com.gallery.GalleryRemote.prefs.UploadPanel.ResizeSize;
import com.gallery.GalleryRemote.util.GRI18n;

/**
 * Bean inspector for Pictures
 * 
 * @author paour
 * @author arothe
 * @created August 16, 2002
 */
public class AlbumInspectorImpl extends JPanel implements PreferenceNames, AlbumInspector {

	private static final long serialVersionUID = 8406883123834053126L;
	private static final String MODULE = "AlbmInspec";

	private JPanel jSpacer;

	private JButton jFetch;
	private JButton jSlideshow;

	private OverridePanel jOverridePanel;
	private PropsPanel jPanelProps;

	private GridBagConstraints jPropsPanelConstraints;
	private GridBagConstraints jOverridePanelConstraints;
	private GridBagConstraints jFetchButtonConstraints;
	private GridBagConstraints jSlideshowButtonConstraints;
	private GridBagConstraints jSpacerConstraints;

	GridBagLayout gridBagLayout1 = new GridBagLayout();
	Album album = null;

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
		if (jFetch == null) {
			jFetch = new JButton();
			jFetch.setText(GRI18n.getString(MODULE, "Fetch"));
		}
		return jFetch;
	}

	@Override
	public AbstractButton getSlideshowButton() {
		if (jSlideshow == null) {
			jSlideshow = new JButton();
			jSlideshow.setText(GRI18n.getString(MODULE, "Slideshow"));
		}
		return jSlideshow;
	}

	private OverridePanel getOverridePanel() {
		if (jOverridePanel == null) {
			jOverridePanel = new OverridePanel();
		}
		return jOverridePanel;
	}

	private PropsPanel getPropsPanel() {
		if (jPanelProps == null) {
			jPanelProps = new PropsPanel();
		}
		return jPanelProps;
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
	public JComboBox<ResizeSize> getResizeTo() {
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

	// ---- factor below

	private void readResizeTo(String text) {
		if (ignoreItemChanges) {
			return;
		}

		try {
			int overrideDimension = album.getOverrideResizeDimension();

			// if (text.length() > 0) {
			int newOverrideDimension = Integer.parseInt(text);

			if (overrideDimension != -1 || (newOverrideDimension != GalleryRemote.instance().properties.getIntDimensionProperty(RESIZE_TO))) {
				Log.log(Log.LEVEL_TRACE, MODULE, "Overriding dimension to " + newOverrideDimension);
				album.setOverrideResizeDimension(newOverrideDimension);
			}
			// }
		} catch (NumberFormatException ee) {
			Log.logException(Log.LEVEL_ERROR, MODULE, ee);
		}
	}

	@Override
	public void refresh(AlbumInspectorDTO dto) {
		boolean oldIgnoreItemChanges = ignoreItemChanges;
		ignoreItemChanges = true;

		if (album != null && jResizeBeforeUpload.isSelected()) {
			jResizeToDefault.setEnabled(true);
			jResizeToForce.setEnabled(true);

			if (jResizeToForce.isSelected()) {
				jResizeTo.setEnabled(true);
				jResizeTo.setBackground(UIManager.getColor("TextField.background"));
			} else {
				jResizeTo.setEnabled(false);
				jResizeTo.setBackground(UIManager.getColor("TextField.inactiveBackground"));
			}
		} else {
			jResizeToDefault.setEnabled(false);
			jResizeToForce.setEnabled(false);
			jResizeTo.setEnabled(false);
			jResizeTo.setBackground(UIManager.getColor("TextField.inactiveBackground"));
		}

		ignoreItemChanges = oldIgnoreItemChanges;
	}
}
