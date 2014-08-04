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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

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

import com.gallery.galleryremote.Log;
import com.gallery.galleryremote.model.Album;
import com.gallery.galleryremote.model.Gallery;
import com.gallery.galleryremote.util.DialogUtil;
import com.gallery.galleryremote.util.GRI18n;

/**
 * @author paour
 * @created October 18, 2002
 */
public class NewAlbumDialogImpl extends JDialog implements NewAlbumDialog {

	private static final long serialVersionUID = -7008531987250343265L;

	private JLabel jLabel2;
	private JLabel jLabel3;
	JLabel jLabel4 = new JLabel();
	JLabel jLabel5 = new JLabel();
	JTextField jTitle = new JTextField();
	JTextField jName = new JTextField();
	JTextArea jDescription = new JTextArea();
	JLabel jLabel1 = new JLabel();
	JLabel jGalleryName = new JLabel();
	JComboBox<Album> jAlbum = null;
	JPanel jPanel2 = new JPanel();
	private JButton jOk = new JButton();
	private JButton jCancel;
	GridLayout gridLayout1 = new GridLayout();

	public NewAlbumDialogImpl(Frame owner) {
		super(owner, true);
		initUI();
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

	private JButton getCancelButton() {
		if (jCancel == null) {
			jCancel = new JButton();
			jCancel.setText(GRI18n.getString("Common", "Cancel"));
			jCancel.setActionCommand("Cancel");
		}
		return jCancel;
	}

	private JButton getOkButton() {
		if (jOk == null) {
			jOk = new JButton();
			jOk.setText(GRI18n.getString("Common", "OK"));
			jOk.setActionCommand("OK");
		}
		return jOk;
	}

	private void initUI() {
		this.getContentPane().setLayout(new GridBagLayout());
		this.setModal(true);
		this.setTitle(GRI18n.getString(NewAlbumDialogImpl.class.getName(), "title"));

		Vector<Album> albums = new Vector<Album>(gallery.getFlatAlbumList());

		jAlbum = new JComboBox<Album>(albums);
		jAlbum.setRenderer(new AlbumListRenderer());

		if (defaultAlbum == null) {
			jAlbum.setSelectedItem(gallery.getRoot());
		} else {
			jAlbum.setSelectedItem(defaultAlbum);
		}

		
		jDescription
				.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, Color.white, Color.lightGray, Color.darkGray, Color.gray));
		jDescription.setLineWrap(true);
		jDescription.setWrapStyleWord(true);
		JScrollPane descriptionArea = new JScrollPane(jDescription, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		descriptionArea.setPreferredSize(new Dimension(250, 100));
		jAlbum.setFont(UIManager.getFont("Label.font"));
		jGalleryName.setText(GRI18n.getString(MODULE, "createAlbm", new String[] { gallery.toString() }));
		jName.setFont(UIManager.getFont("Label.font"));
		jName.setToolTipText(GRI18n.getString(MODULE, "albmNameTip"));
		
		jTitle.setFont(UIManager.getFont("Label.font"));

		gridLayout1.setColumns(2);
		gridLayout1.setHgap(5);

		jLabel4.setText(GRI18n.getString(MODULE, "albmName"));
		jLabel5.setText(GRI18n.getString(MODULE, "albmDesc"));
		jPanel2.setLayout(gridLayout1);

		this.getContentPane().add(getLabel2(),
				new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 4));
		this.getContentPane().add(getLabel3(),
				new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 5, 0, 5), 0, 4));
		this.getContentPane().add(jLabel4,
				new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 5, 0, 5), 0, 4));
		this.getContentPane().add(
				jLabel5,
				new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0,
						3));
		this.getContentPane().add(
				jTitle,
				new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5),
						0, 0));
		this.getContentPane().add(
				jName,
				new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5),
						0, 0));
		this.getContentPane().add(descriptionArea,
				new GridBagConstraints(1, 4, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));
		this.getContentPane().add(
				jGalleryName,
				new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0,
						0));
		this.getContentPane().add(
				jAlbum,
				new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5),
						0, 0));
		this.getContentPane().add(jPanel2,
				new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		jPanel2.add(getCancelButton(), null);
		jPanel2.add(getOkButton(), null);

		getRootPane().setDefaultButton(getOkButton());

		pack();
		DialogUtil.center(this, getOwner());
	}

	public void resetUI(NewAlbumDTO dto) {
		jOk.setEnabled(dto.isEnabled());
		jName.setEnabled(dto.isEnabled());
		jTitle.setEnabled(dto.isEnabled());
		jDescription.setEnabled(dto.isEnabled());
	}

}