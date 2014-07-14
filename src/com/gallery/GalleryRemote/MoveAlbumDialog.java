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
package com.gallery.GalleryRemote;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import com.gallery.GalleryRemote.albuminspector.AlbumInspectorModel;
import com.gallery.GalleryRemote.model.Album;
import com.gallery.GalleryRemote.util.DialogUtil;
import com.gallery.GalleryRemote.util.GRI18n;

/**
 * Description of the Class
 * 
 * @author paour
 * @created October 18, 2002
 */
public class MoveAlbumDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = -5217703609161413941L;
	public final static String MODULE = "MoveAlbum";

	AlbumInspectorModel model = null;

	JLabel jLabel2 = new JLabel();
	JLabel jLabel3 = new JLabel();
	JLabel jLabel4 = new JLabel();
	JLabel jLabel5 = new JLabel();
	JLabel jLabel1 = new JLabel();
	JLabel jAlbumName = new JLabel();
	JComboBox<Album> jAlbum = null;
	JPanel jPanel2 = new JPanel();
	JButton jOk = new JButton();
	JButton jCancel = new JButton();

	Album newParent = null;

	/**
	 * Constructor for the NewAlbumDialog object
	 * 
	 * @param owner
	 *           Description of Parameter
	 * @param gallery
	 *           Description of Parameter
	 * @param defaultAlbum
	 *           Description of Parameter
	 */
	public MoveAlbumDialog(Frame owner, AlbumInspectorModel albumModel) {
		super(owner, true);

		this.model = albumModel;

		jbInit();

		pack();
		DialogUtil.center(this, owner);

		setVisible(true);
	}

	private void jbInit() {
		this.getContentPane().setLayout(new GridBagLayout());
		this.setModal(true);
		this.setTitle(GRI18n.getString(MODULE, "title"));

		Vector<Album> albums = new Vector<Album>(model.getGallery().getFlatAlbumList());
		// rootAlbum = new Album(gallery);
		// rootAlbum.setSuppressEvents(true);
		// rootAlbum.setTitle(GRI18n.getString(MODULE, "rootAlbmTitle"));
		// rootAlbum.setName("root.root");
		Album rootAlbum = (Album) model.getGallery().getRoot();
		Album album = model.getAlbum();
		if (album.getParentAlbum() == rootAlbum) {
			albums.remove(rootAlbum);
		}
		removeAlbumFromList(albums, album, true);

		jAlbum = new JComboBox<Album>(albums);
		jAlbum.setFont(UIManager.getFont("Label.font"));

		jCancel.setText(GRI18n.getString("Common", "Cancel"));
		jCancel.setActionCommand("Cancel");
		jOk.setText(GRI18n.getString("Common", "OK"));
		jOk.setActionCommand("OK");
		jAlbumName.setText(GRI18n.getString(MODULE, "moveAlbm", new String[] { album.getName() }));

		jLabel2.setText(GRI18n.getString(MODULE, "parentAlbm"));

		this.getContentPane().add(jLabel2,
				new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 4));
		this.getContentPane().add(
				jAlbumName,
				new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0,
						0));
		this.getContentPane().add(
				jAlbum,
				new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5),
						0, 0));
		this.getContentPane().add(jPanel2,
				new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

		jPanel2.setLayout(new GridLayout(1, 2, 5, 0));
		jPanel2.add(jCancel, null);
		jPanel2.add(jOk, null);

		jOk.addActionListener(this);
		jCancel.addActionListener(this);

		getRootPane().setDefaultButton(jOk);
	}

	/**
	 * Description of the Method
	 * 
	 * @param e
	 *           Description of Parameter
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		Log.log(Log.LEVEL_INFO, MODULE, "Command selected " + command);

		if (command.equals("Cancel")) {
			setVisible(false);
		} else if (command.equals("OK")) {
			newParent = (Album) jAlbum.getSelectedItem();
			// if (newParent == rootAlbum) {
			// newParent = null;
			// }

			setVisible(false);
		}
	}

	public Album getNewParent() {
		return newParent;
	}

	private void removeAlbumFromList(Vector<Album> list, Album album, boolean deep) {
		if (!deep) {
			list.remove(album);
		}
		for (Enumeration<Album> it = album.children(); it.hasMoreElements();) {
			Album subAlbum = it.nextElement();
			removeAlbumFromList(list, subAlbum, true);
		}
	}
}
