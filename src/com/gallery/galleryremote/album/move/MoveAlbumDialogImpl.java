package com.gallery.galleryremote.album.move;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import com.gallery.galleryremote.album.create.AlbumComboBoxModel;
import com.gallery.galleryremote.model.Album;
import com.gallery.galleryremote.util.DialogUtil;
import com.gallery.galleryremote.util.GRI18n;

public class MoveAlbumDialogImpl extends JDialog implements MoveAlbumDialog {

	private static final long serialVersionUID = 5264253604680824765L;

	private JLabel jLabel2;
	private GridBagConstraints jLabel2Constraints;

	private JLabel jAlbumName;
	private GridBagConstraints jAlbumNameConstraints;
	private AlbumComboBoxModel jAlbumModel;

	private JComboBox<Album> jAlbum;
	private GridBagConstraints jAlbumConstraints;

	private JPanel jButtonPanel;
	private GridBagConstraints jButtonPanelConstraints;

	private JButton jOk;
	private JButton jCancel;

	public MoveAlbumDialogImpl(Frame owner) {
		super(owner, true);
		initUI();
	}

	private void initUI() {
		setModal(true);
		setTitle(GRI18n.getString(MoveAlbumDialogImpl.class, "title"));

		getContentPane().setLayout(new GridBagLayout());
		getContentPane().add(getLabel2(), getLabel2Constraints());
		getContentPane().add(getAlbumNameLabel(), getAlbumNameConstraints());
		getContentPane().add(getAlbumComboBox(), getAlbumConstraints());
		getContentPane().add(getButtonPanel(), getButtonPanelConstraints());
		
		pack();
		DialogUtil.center(this, getOwner());

		getRootPane().setDefaultButton(getOkButton());
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

	private GridBagConstraints getAlbumNameConstraints() {
		if (jAlbumNameConstraints == null) {
			jAlbumNameConstraints = new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
					new Insets(5, 5, 5, 5), 0, 0);
		}
		return jAlbumNameConstraints;
	}

	private GridBagConstraints getAlbumConstraints() {
		if (jAlbumConstraints == null) {
			jAlbumConstraints = new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
					new Insets(0, 0, 0, 5), 0, 0);
		}
		return jAlbumConstraints;
	}

	private GridBagConstraints getButtonPanelConstraints() {
		if (jButtonPanelConstraints == null) {
			jButtonPanelConstraints = new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0);
		}
		return jButtonPanelConstraints;
	}

	private JPanel getButtonPanel() {
		if (jButtonPanel == null) {
			jButtonPanel = new JPanel();
			jButtonPanel.setLayout(new GridLayout(1, 2, 5, 0));
			jButtonPanel.add(getCancelButton(), null);
			jButtonPanel.add(getOkButton(), null);
		}
		return jButtonPanel;
	}

	private JLabel getAlbumNameLabel() {
		if (jAlbumName == null) {
			jAlbumName = new JLabel();
		}
		return jAlbumName;
	}

	@Override
	public JButton getCancelButton() {
		if (jCancel == null) {
			jCancel = new JButton();
			jCancel.setText(GRI18n.getString(MoveAlbumDialog.class, "Cancel"));
			jCancel.setActionCommand("Cancel");
		}
		return jCancel;
	}

	@Override
	public JButton getOkButton() {
		if (jOk == null) {
			jOk = new JButton();
			jOk.setText(GRI18n.getString(MoveAlbumDialog.class, "OK"));
			jOk.setActionCommand("OK");
		}
		return jOk;
	}

	private JLabel getLabel2() {
		if (jLabel2 == null) {
			jLabel2 = new JLabel();
			jLabel2.setText(GRI18n.getString(MoveAlbumDialog.class, "parentAlbm"));
		}
		return jLabel2;
	}

	@Override
	public void resetUI(MoveAlbumDTO dto) {
		getAlbumComboBoxModel().setAlbumList(dto.getAlbumList());
		getAlbumNameLabel().setText(GRI18n.getString(MoveAlbumDialog.class, "moveAlbm", new String[] { dto.getAlbumName() }));
	}

}
