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
package com.gallery.galleryremote.album.create;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import com.gallery.galleryremote.model.Album;
import com.gallery.galleryremote.util.DialogUtil;
import com.gallery.galleryremote.util.GRI18n;
import com.gallery.galleryremote.util.log.Logger;

/**
 * @author paour
 * @author arothe
 * @created October 18, 2002
 */
public class NewAlbumDialogImpl extends JDialog implements NewAlbumDialog {

	private static final long serialVersionUID = -7008531987250343265L;
	private static final Logger LOGGER = Logger.getLogger(NewAlbumDialogImpl.class);

	private JComboBox<Album> jAlbum;
	private GridBagConstraints jAlbumConstraints;
	private AlbumComboBoxModel jAlbumModel;

	private JLabel jLabel2;
	private GridBagConstraints jLabel2Constraints;

	private JLabel jLabel3;
	private GridBagConstraints jLabel3Constraints;

	private JLabel jLabel4;
	private GridBagConstraints jLabel4Constraints;

	private JLabel jLabel5;
	private GridBagConstraints jLabel5Constraints;

	private JTextField jTitle;
	private GridBagConstraints jTitleConstraints;

	private JTextField jName;
	private GridBagConstraints jNameConstraints;

	private JTextArea jDescription;
	private JScrollPane jDescriptionScrollPane;
	private GridBagConstraints jDescriptionConstraints;

	private JLabel jGalleryName;
	private GridBagConstraints jGalleryNameConstraints;

	private JPanel jPanel2;
	private GridBagConstraints jPanel2Constraints;

	private JButton jOk;
	private JButton jCancel;

	public NewAlbumDialogImpl(Frame owner) {
		super(owner, true);

		LOGGER.fine("Creating class instance...");
		initUI();
	}

	private JLabel getGalleryName() {
		if (jGalleryName == null) {
			jGalleryName = new JLabel();
		}
		return jGalleryName;
	}

	private JLabel getLabel2() {
		if (jLabel2 == null) {
			jLabel2 = new JLabel();
			jLabel2.setText(GRI18n.getString(NewAlbumDialogImpl.class.getName(), "parentAlbm"));
		}
		return jLabel2;
	}

	private JLabel getLabel3() {
		if (jLabel3 == null) {
			jLabel3 = new JLabel();
			jLabel3.setText(GRI18n.getString(NewAlbumDialogImpl.class.getName(), "albmTitle"));
		}
		return jLabel3;
	}

	private JLabel getLabel4() {
		if (jLabel4 == null) {
			jLabel4 = new JLabel();
			jLabel4.setText(GRI18n.getString(NewAlbumDialogImpl.class.getName(), "albmName"));
		}
		return jLabel4;
	}

	private JLabel getLabel5() {
		if (jLabel5 == null) {
			jLabel5 = new JLabel();
			jLabel5.setText(GRI18n.getString(NewAlbumDialogImpl.class.getName(), "albmDesc"));
		}
		return jLabel5;
	}

	@Override
	public JButton getCancelButton() {
		if (jCancel == null) {
			jCancel = new JButton();
			jCancel.setText(GRI18n.getString("Common", "Cancel"));
			jCancel.setActionCommand("Cancel");
		}
		return jCancel;
	}

	@Override
	public JButton getOkButton() {
		if (jOk == null) {
			jOk = new JButton();
			jOk.setText(GRI18n.getString("Common", "OK"));
			jOk.setActionCommand("OK");
		}
		return jOk;
	}

	private JPanel getButtonPanel() {
		if (jPanel2 == null) {
			GridLayout layout = new GridLayout();
			layout.setColumns(2);
			layout.setHgap(5);

			jPanel2 = new JPanel();
			jPanel2.setLayout(layout);
			jPanel2.add(getCancelButton(), null);
			jPanel2.add(getOkButton(), null);
		}
		return jPanel2;
	}

	@Override
	public JTextField getTitleField() {
		if (jTitle == null) {
			jTitle = new JTextField();
			jTitle.setFont(UIManager.getFont("Label.font"));
		}
		return jTitle;
	}

	@Override
	public JTextField getNameField() {
		if (jName == null) {
			jName = new JTextField();
			jName.setFont(UIManager.getFont("Label.font"));
			jName.setToolTipText(GRI18n.getString(NewAlbumDialogImpl.class.getName(), "albmNameTip"));
		}
		return jName;
	}

	@Override
	public JTextArea getDescriptionArea() {
		if (jDescription == null) {
			jDescription = new JTextArea();
			jDescription.setBorder(getDescriptionBorder());
			jDescription.setLineWrap(true);
			jDescription.setWrapStyleWord(true);
		}
		return jDescription;
	}

	private Border getDescriptionBorder() {
		return BorderFactory.createBevelBorder(BevelBorder.LOWERED, Color.white, Color.lightGray, Color.darkGray, Color.gray);
	}

	private JScrollPane getDescriptionScroller() {
		if (jDescriptionScrollPane != null) {
			jDescriptionScrollPane = new JScrollPane(getDescriptionArea(), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			jDescriptionScrollPane.setPreferredSize(new Dimension(250, 100));
		}
		return jDescriptionScrollPane;
	}

	private AlbumComboBoxModel getAlbumComboBoxModel() {
		if (jAlbumModel == null) {
			jAlbumModel = new AlbumComboBoxModel();
		}
		return jAlbumModel;
	}

	@Override
	public JComboBox<Album> getAlbumComboBox() {
		if (jAlbum == null) {
			jAlbum = new JComboBox<Album>(getAlbumComboBoxModel());
			jAlbum.setRenderer(new AlbumListRenderer());
			jAlbum.setFont(UIManager.getFont("Label.font"));
		}
		return jAlbum;
	}

	private GridBagConstraints getLabel2Constraints() {
		if (jLabel2Constraints == null) {
			jLabel2Constraints = new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,
					5, 0, 5), 0, 4);
		}
		return jLabel2Constraints;
	}

	private GridBagConstraints getLabel3Constraints() {
		if (jLabel3Constraints == null) {
			jLabel3Constraints = new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2,
					5, 0, 5), 0, 4);
		}
		return jLabel3Constraints;
	}

	private GridBagConstraints getLabel4Constraints() {
		if (jLabel4Constraints == null) {
			jLabel4Constraints = new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2,
					5, 0, 5), 0, 4);
		}
		return jLabel4Constraints;
	}

	private GridBagConstraints getLabel5Constraints() {
		if (jLabel5Constraints == null) {
			jLabel5Constraints = new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
					new Insets(0, 5, 0, 5), 0, 3);
		}
		return jLabel5Constraints;
	}

	private GridBagConstraints getTitleConstraints() {
		if (jTitleConstraints == null) {
			jTitleConstraints = new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
					new Insets(0, 0, 0, 5), 0, 0);
		}
		return jTitleConstraints;
	}

	private GridBagConstraints getNameConstraints() {
		if (jNameConstraints == null) {
			jNameConstraints = new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
					new Insets(0, 0, 0, 5), 0, 0);
		}
		return jNameConstraints;
	}

	private GridBagConstraints getButtonPanelConstraints() {
		if (jPanel2Constraints == null) {
			jPanel2Constraints = new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5,
					5, 5, 5), 0, 0);
		}
		return jPanel2Constraints;
	}

	private GridBagConstraints getAlbumComboboxConstraints() {
		if (jAlbumConstraints == null) {
			jAlbumConstraints = new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
					new Insets(0, 0, 0, 5), 0, 0);
		}
		return jAlbumConstraints;
	}

	private GridBagConstraints getGalleryNameConstraints() {
		if (jGalleryNameConstraints == null) {
			jGalleryNameConstraints = new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
					new Insets(5, 5, 5, 5), 0, 0);
		}
		return jGalleryNameConstraints;
	}

	private GridBagConstraints getDescriptionConstraints() {
		if (jDescriptionConstraints == null) {
			jDescriptionConstraints = new GridBagConstraints(1, 4, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 5), 0, 0);
		}
		return jDescriptionConstraints;
	}

	private void initUI() {
		setModal(true);
		setTitle(GRI18n.getString(NewAlbumDialogImpl.class.getName(), "title"));

		getContentPane().setLayout(new GridBagLayout());
		getContentPane().add(getLabel2(), getLabel2Constraints());
		getContentPane().add(getLabel3(), getLabel3Constraints());
		getContentPane().add(getLabel4(), getLabel4Constraints());
		getContentPane().add(getLabel5(), getLabel5Constraints());
		getContentPane().add(getTitleField(), getTitleConstraints());
		getContentPane().add(getNameField(), getNameConstraints());
		getContentPane().add(getDescriptionScroller(), getDescriptionConstraints());
		getContentPane().add(getGalleryName(), getGalleryNameConstraints());
		getContentPane().add(getAlbumComboBox(), getAlbumComboboxConstraints());
		getContentPane().add(getButtonPanel(), getButtonPanelConstraints());

		getRootPane().setDefaultButton(getOkButton());

		pack();
		DialogUtil.center(this, getOwner());
	}

	@Override
	public void resetUI(NewAlbumDTO dto) {
		getOkButton().setEnabled(dto.isEnabled());
		getNameField().setEnabled(dto.isEnabled());
		getTitleField().setEnabled(dto.isEnabled());
		getDescriptionArea().setEnabled(dto.isEnabled());
		getAlbumComboBoxModel().setAlbumList(dto.getAlbumList());
		getAlbumComboBox().setSelectedItem(dto.getSelectedAlbum());
		getGalleryName().setText(GRI18n.getString(NewAlbumDialogImpl.class.getName(), "createAlbm", new String[] { dto.getGalleryUri() }));
	}

}