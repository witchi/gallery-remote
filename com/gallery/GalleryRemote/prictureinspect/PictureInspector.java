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
package com.gallery.GalleryRemote.prictureinspect;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import com.gallery.GalleryRemote.GalleryCommCapabilities;
import com.gallery.GalleryRemote.GalleryRemote;
import com.gallery.GalleryRemote.model.Album;
import com.gallery.GalleryRemote.model.Picture;
import com.gallery.GalleryRemote.util.GRI18n;
import com.gallery.GalleryRemote.util.ImageUtils;

/**
 * Bean inspector for Pictures (the right column in the main frame)
 * 
 * @author paour
 * @author arothe
 */
public class PictureInspector extends JPanel implements PictureInspectorActions {

	private static final long serialVersionUID = -5594312109149362431L;
	private static final String MODULE = "PictInspec";
	private static final int FIRST_ROW_EXTRA = 8;

	private HashMap<String, JLabel> extraLabels = new HashMap<String, JLabel>();
	private HashMap<String, PictureFieldTextArea> extraTextAreas = new HashMap<String, PictureFieldTextArea>();
	private ArrayList<String> currentExtraFields = null;
	private PictureInspectorModel model;
	
	private JLabel jLabel5;
	private GridBagConstraints jLabel5Constraints;

	private JLabel jLabel6;
	private GridBagConstraints jLabel6Constraints;

	private JLabel jLabel4;
	private GridBagConstraints jLabel4Constraints;

	private JLabel jLabel8;
	private GridBagConstraints jLabel8Constraints;

	private JLabel jLabel1;
	private GridBagConstraints jLabel1Constraints;

	private JLabel jLabel2;
	private GridBagConstraints jLabel2Constraints;

	private JPanel jSpacer;
	private GridBagConstraints jSpacerConstraints;

	private JButton jDeleteButton;
	private GridBagConstraints jDeleteButtonConstraints;

	private JButton jUpButton;
	private GridBagConstraints jUpButtonConstraints;

	private JButton jDownButton;
	private GridBagConstraints jDownButtonConstraints;

	private IconAreaPanel jIconAreaPanel;
	private GridBagConstraints jIconAreaPanelConstraints;

	private JTextArea jAlbum;
	private GridBagConstraints jAlbumConstraints;

	private JTextArea jSize;
	private GridBagConstraints jSizeConstraints;

	private PathPanel jPathPanel;
	private GridBagConstraints jPathPanelConstraints;

	private CaptionPanel jCaptionPanel;
	private GridBagConstraints jCaptionPanelConstraints;

	/**
	 * Constructor for the PictureInspector object
	 */
	public PictureInspector(PictureInspectorModel model) {
		this.model = model;
		initUI();
	}

	private void initUI() {
		setLayout(new GridBagLayout());

		add(getPathLabel(), getPathLabelConstraints());
		add(getAlbumLabel(), getAlbumLabelConstraints());
		add(getCaptionLabel(), getCaptionLabelConstraints());
		add(getMoveLabel(), getMoveLabelConstraints());
		add(getSizeLabel(), getSizeLabelConstraints());
		add(getDeleteLabel(), getDeleteLabelConstraints());
		add(getSpacer(), getSpacerConstraints());
		add(getIconAreaPanel(), getIconAreaPanelConstraints());
		add(getAlbumTextArea(), getAlbumTextAreaConstraints());
		add(getSizeTextArea(), getSizeTextAreaConstraints());
		add(getUpButton(), getUpButtonConstraints());
		add(getDownButton(), getDownButtonConstraints());
		add(getDeleteButton(), getDeleteButtonConstraints());
		add(getPathPanel(), getPathPanelConstraints());
		add(getCaptionPanel(), getCaptionPanelConstraints());

		this.setMinimumSize(new Dimension(150, 0));
		replaceIcon(getIcon(), ImageUtils.defaultThumbnail);
	}

	private void refreshWithNoPicture() {
		getIcon().setText(GRI18n.getString(MODULE, "noPicSel"));
		replaceIcon(getIcon(), ImageUtils.defaultThumbnail);
		getPath().setText("");
		jAlbum.setText("");

		getCaption().setText("");
		getCaption().setEditable(false);
		getCaption().setBackground(UIManager.getColor("TextField.inactiveBackground"));

		jSize.setText("");
		jUpButton.setEnabled(false);
		jDownButton.setEnabled(false);
		jDeleteButton.setEnabled(false);
		getRotateLeftButton().setEnabled(false);
		getRotateRightButton().setEnabled(false);
		getFlipButton().setEnabled(false);

		removeExtraFields();
	}

	private void refreshWithOnePicture() {
		Picture p = model.getPictureList().get(0);
		replaceIcon(getIcon(), mf.getThumbnail(p));
		if (p.isOnline()) {
			getPath().setText(GRI18n.getString(MODULE, "onServer"));
			getIcon().setText(p.getName());
		} else {
			getIcon().setText(p.getSource().getName());
			getPath().setText(p.getSource().getParent());
		}
		jAlbum.setText(p.getParentAlbum().getTitle());
		if (p.getParentAlbum().getGallery().getComm(mf.jStatusBar).hasCapability(mf.jStatusBar, GalleryCommCapabilities.CAPA_UPLOAD_CAPTION)) {
			getCaption().setText(p.getCaption());
			getCaption().setEditable(true);
			getCaption().setBackground(UIManager.getColor("TextField.background"));
		}
		jSize.setText(NumberFormat.getInstance().format((int) p.getFileSize()) + " bytes");
		jUpButton.setEnabled(isEnabled());
		jDownButton.setEnabled(isEnabled());
		jDeleteButton.setEnabled(isEnabled());
		getRotateLeftButton().setEnabled(isEnabled());
		getRotateRightButton().setEnabled(isEnabled());
		getFlipButton().setEnabled(isEnabled());

		addExtraFields(p);
	}

	private void refreshWithMultiplePictures() {
		Picture p = model.getPictureList().get(0);
		Object[] params = { new Integer(model.getPictureList().size()) };
		getIcon().setText(GRI18n.getString(MODULE, "countElemSel", params));
		replaceIcon(getIcon(), ImageUtils.defaultThumbnail);
		getPath().setText("");
		jAlbum.setText(p.getParentAlbum().getTitle());
		getCaption().setText("");
		getCaption().setEditable(false);
		getCaption().setBackground(UIManager.getColor("TextField.inactiveBackground"));
		jSize.setText(NumberFormat.getInstance().format(Album.getObjectFileSize(model.getPictureList())) + " bytes");

		jUpButton.setEnabled(isEnabled());
		jDownButton.setEnabled(isEnabled());
		jDeleteButton.setEnabled(isEnabled());
		getRotateLeftButton().setEnabled(isEnabled());
		getRotateRightButton().setEnabled(isEnabled());
		getFlipButton().setEnabled(isEnabled());

		removeExtraFields();
	}

	public void refresh() {
		getIcon().setPreferredSize(
				new Dimension(0, GalleryRemote.instance().properties.getThumbnailSize().height + getIconAreaPanel().getEmptyIconHeight()
						+ getIcon().getIconTextGap()));

		int count = model.getPictureList().size();

		if (count == 0) {
			refreshWithNoPicture();
			return;
		}

		if (count == 1) {
			refreshWithOnePicture();
			return;
		}

		refreshWithMultiplePictures();
	}

	public List<String> getExtraFieldNames() {
		return new ArrayList<String>(extraTextAreas.keySet());
	}

	public PictureFieldTextArea getExtraField(String name) {
		return extraTextAreas.get(name);
	}

	private void addExtraFields(Picture p) {
		ArrayList<String> newExtraFields = p.getParentAlbum().getExtraFields();

		if (newExtraFields == null) {
			removeExtraFields();
		} else {
			if (!newExtraFields.equals(currentExtraFields)) {
				removeExtraFields();

				int i = 0;
				Iterator<String> it = newExtraFields.iterator();
				while (it.hasNext()) {
					String name = it.next();
					JLabel label = new JLabel(name);
					extraLabels.put(name, label);
					add(label, new GridBagConstraints(0, FIRST_ROW_EXTRA + i, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST,
							GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 2, 0));

					PictureFieldTextArea field = new PictureFieldTextArea();
					extraTextAreas.put(name, field);
					field.setFont(UIManager.getFont("Label.font"));
					field.setLineWrap(true);
					field.setWrapStyleWord(true);
					add(field, new GridBagConstraints(1, FIRST_ROW_EXTRA + i, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
							GridBagConstraints.BOTH, new Insets(5, 5, 0, 5), 0, 0));
					
					
					// we must move both routines into the controller, but we can notify it?
					field.getDocument().addDocumentListener(model);
					field.addKeyboardListener(model);

					i++;
				}

				currentExtraFields = newExtraFields;
			}

			Iterator<String> it = newExtraFields.iterator();
			while (it.hasNext()) {
				String name = it.next();
				String value = p.getExtraField(name);
				JTextArea field = extraTextAreas.get(name);
				if (value == null) {
					field.setText("");
				} else {
					field.setText(value);
				}
			}
		}
	}

	private void removeExtraFields() {
		Iterator<JLabel> it = extraLabels.values().iterator();
		while (it.hasNext()) {
			remove(it.next());
		}
		Iterator<PictureFieldTextArea> i = extraTextAreas.values().iterator();
		while (i.hasNext()) {
			remove(i.next());
		}
		extraLabels.clear();
		extraTextAreas.clear();
		currentExtraFields = null;
	}

	@Override
	public void setEnabled(boolean enabled) {
		// Log.log(Log.TRACE, MODULE, "setEnabled " + enabled);
		getIcon().setEnabled(enabled);
		jUpButton.setEnabled(enabled);
		jDownButton.setEnabled(enabled);
		jDeleteButton.setEnabled(enabled);
		getRotateLeftButton().setEnabled(enabled);
		getRotateRightButton().setEnabled(enabled);
		getFlipButton().setEnabled(enabled);
		getCaption().setEnabled(enabled);
		super.setEnabled(enabled);
	}

	// Focus traversal actions

	public void replaceIcon(JLabel label, Image icon) {
		Icon i = label.getIcon();

		if (i == null || !(i instanceof ImageIcon)) {
			i = new ImageIcon();
			label.setIcon(i);
		}

		((ImageIcon) i).setImage(icon);
	}

	private JLabel getPathLabel() {
		if (jLabel5 == null) {
			jLabel5 = new JLabel();
			jLabel5.setText(GRI18n.getString(MODULE, "Path"));
		}
		return jLabel5;
	}

	private GridBagConstraints getPathLabelConstraints() {
		if (jLabel5Constraints == null) {
			jLabel5Constraints = new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
					new Insets(0, 0, 0, 0), 2, 0);
		}
		return jLabel5Constraints;
	}

	private JLabel getAlbumLabel() {
		if (jLabel6 == null) {
			jLabel6 = new JLabel();
			jLabel6.setText(GRI18n.getString(MODULE, "Album"));
		}
		return jLabel6;
	}

	private GridBagConstraints getAlbumLabelConstraints() {
		if (jLabel6Constraints == null) {
			jLabel6Constraints = new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,
					0, 0, 0), 2, 0);
		}
		return jLabel6Constraints;
	}

	private JLabel getCaptionLabel() {
		if (jLabel4 == null) {
			jLabel4 = new JLabel();
			jLabel4.setText(GRI18n.getString(MODULE, "Caption"));
		}
		return jLabel4;
	}

	private GridBagConstraints getCaptionLabelConstraints() {
		if (jLabel4Constraints == null) {
			jLabel4Constraints = new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
					new Insets(5, 0, 0, 0), 2, 0);
		}
		return jLabel4Constraints;
	}

	private JLabel getMoveLabel() {
		if (jLabel8 == null) {
			jLabel8 = new JLabel();
			jLabel8.setText(GRI18n.getString(MODULE, "Move"));
		}
		return jLabel8;
	}

	private GridBagConstraints getMoveLabelConstraints() {
		if (jLabel8Constraints == null) {
			jLabel8Constraints = new GridBagConstraints(0, 4, 1, 2, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,
					0, 0, 0), 2, 0);
		}
		return jLabel8Constraints;
	}

	private JLabel getSizeLabel() {
		if (jLabel1 == null) {
			jLabel1 = new JLabel();
			jLabel1.setText(GRI18n.getString(MODULE, "Size"));
		}
		return jLabel1;
	}

	private GridBagConstraints getSizeLabelConstraints() {
		if (jLabel1Constraints == null) {
			jLabel1Constraints = new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,
					0, 0, 0), 2, 0);
		}
		return jLabel1Constraints;
	}

	private JLabel getDeleteLabel() {
		if (jLabel2 == null) {
			jLabel2 = new JLabel();
			jLabel2.setText(GRI18n.getString(MODULE, "Delete"));
		}
		return jLabel2;
	}

	private GridBagConstraints getDeleteLabelConstraints() {
		if (jLabel2Constraints == null) {
			jLabel2Constraints = new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,
					0, 0, 0), 2, 0);
		}
		return jLabel2Constraints;
	}

	private JPanel getSpacer() {
		if (jSpacer == null) {
			jSpacer = new JPanel();
		}
		return jSpacer;
	}

	private GridBagConstraints getSpacerConstraints() {
		if (jSpacerConstraints == null) {
			jSpacerConstraints = new GridBagConstraints(0, 99, 2, 1, 1.0, 0.1, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
					new Insets(0, 0, 0, 0), 0, 0);
		}
		return jSpacerConstraints;
	}

	private IconAreaPanel getIconAreaPanel() {
		if (jIconAreaPanel == null) {
			jIconAreaPanel = new IconAreaPanel();
			jIconAreaPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		}
		return jIconAreaPanel;
	}

	private GridBagConstraints getIconAreaPanelConstraints() {
		if (jIconAreaPanelConstraints == null) {
			jIconAreaPanelConstraints = new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
					new Insets(5, 5, 5, 5), 0, 0);
		}
		return jIconAreaPanelConstraints;
	}

	private JTextArea getAlbumTextArea() {
		if (jAlbum == null) {
			jAlbum = new JTextArea();
			jAlbum.setRows(0);
			jAlbum.setEditable(false);
			jAlbum.setFont(UIManager.getFont("Label.font"));
			jAlbum.setBackground(UIManager.getColor("TextField.inactiveBackground"));
		}
		return jAlbum;
	}

	private GridBagConstraints getAlbumTextAreaConstraints() {
		if (jAlbumConstraints == null) {
			jAlbumConstraints = new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
					new Insets(0, 5, 0, 5), 0, 0);
		}
		return jAlbumConstraints;
	}

	private JTextArea getSizeTextArea() {
		if (jSize == null) {
			jSize = new JTextArea();
			jSize.setRows(0);
			jSize.setEditable(false);
			jSize.setFont(UIManager.getFont("Label.font"));
			jSize.setBackground(UIManager.getColor("TextField.inactiveBackground"));
		}
		return jSize;
	}

	private GridBagConstraints getSizeTextAreaConstraints() {
		if (jSizeConstraints == null) {
			jSizeConstraints = new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
					new Insets(0, 5, 0, 5), 0, 0);
		}
		return jSizeConstraints;
	}

	JButton getUpButton() {
		if (jUpButton == null) {
			jUpButton = new JButton();
			jUpButton.setMaximumSize(new Dimension(120, 23));
			jUpButton.setMinimumSize(new Dimension(120, 23));
			jUpButton.setPreferredSize(new Dimension(120, 23));
			jUpButton.setToolTipText(GRI18n.getString(MODULE, "upBtnTip"));
			jUpButton.setText(GRI18n.getString(MODULE, "upBtn"));
			jUpButton.setActionCommand("Up");
			jUpButton.setHorizontalAlignment(SwingConstants.LEFT);
			jUpButton.setIcon(GalleryRemote.iUp);
		}
		return jUpButton;
	}

	private GridBagConstraints getUpButtonConstraints() {
		if (jUpButtonConstraints == null) {
			jUpButtonConstraints = new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
					new Insets(2, 0, 0, 0), 0, 0);
		}
		return jUpButtonConstraints;
	}

	JButton getDownButton() {
		if (jDownButton == null) {
			jDownButton = new JButton();
			jDownButton.setMaximumSize(new Dimension(120, 23));
			jDownButton.setMinimumSize(new Dimension(120, 23));
			jDownButton.setPreferredSize(new Dimension(120, 23));
			jDownButton.setToolTipText(GRI18n.getString(MODULE, "dnBtnTip"));
			jDownButton.setText(GRI18n.getString(MODULE, "dnBtn"));
			jDownButton.setActionCommand("Down");
			jDownButton.setHorizontalAlignment(SwingConstants.LEFT);
			jDownButton.setIcon(GalleryRemote.iDown);
		}
		return jDownButton;
	}

	private GridBagConstraints getDownButtonConstraints() {
		if (jDownButtonConstraints == null) {
			jDownButtonConstraints = new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
					new Insets(0, 0, 0, 0), 0, 0);
		}
		return jDownButtonConstraints;
	}

	JButton getDeleteButton() {
		if (jDeleteButton == null) {
			jDeleteButton = new JButton();
			jDeleteButton.setMaximumSize(new Dimension(120, 23));
			jDeleteButton.setMinimumSize(new Dimension(120, 23));
			jDeleteButton.setPreferredSize(new Dimension(120, 23));
			jDeleteButton.setToolTipText(GRI18n.getString(MODULE, "delBtnTip"));
			jDeleteButton.setActionCommand("Delete");
			jDeleteButton.setHorizontalAlignment(SwingConstants.LEFT);
			jDeleteButton.setText(GRI18n.getString(MODULE, "Delete"));
			jDeleteButton.setIcon(GalleryRemote.iDelete);
		}
		return jDeleteButton;
	}

	private GridBagConstraints getDeleteButtonConstraints() {
		if (jDeleteButtonConstraints == null) {
			jDeleteButtonConstraints = new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
					new Insets(2, 0, 0, 0), 0, 0);
		}
		return jDeleteButtonConstraints;
	}

	JButton getRotateLeftButton() {
		return getIconAreaPanel().getRotateLeftButton();
	}

	JButton getRotateRightButton() {
		return getIconAreaPanel().getRotateRightButton();
	}

	JButton getFlipButton() {
		return getIconAreaPanel().getFlipButton();
	}

	private JLabel getIcon() {
		return getIconAreaPanel().getIcon();
	}

	private PathPanel getPathPanel() {
		if (jPathPanel == null) {
			jPathPanel = new PathPanel();
		}
		return jPathPanel;
	}

	private GridBagConstraints getPathPanelConstraints() {
		if (jPathPanelConstraints == null) {
			jPathPanelConstraints = new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 5, 0, 5), 0, 0);
		}
		return jPathPanelConstraints;
	}

	private CaptionPanel getCaptionPanel() {
		if (jCaptionPanel == null) {
			jCaptionPanel = new CaptionPanel();
		}
		return jCaptionPanel;
	}

	private GridBagConstraints getCaptionPanelConstraints() {
		if (jCaptionPanelConstraints == null) {
			jCaptionPanelConstraints = new GridBagConstraints(1, 7, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH,
					new Insets(5, 5, 0, 5), 0, 0);
		}
		return jCaptionPanelConstraints;
	}

	PictureFieldTextArea getCaption() {
		return getCaptionPanel().getCaption();
	}

	private JTextArea getPath() {
		return getPathPanel().getPath();
	}
}