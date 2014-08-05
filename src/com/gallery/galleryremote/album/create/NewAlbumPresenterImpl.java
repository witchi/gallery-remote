package com.gallery.galleryremote.album.create;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JTextField;

import com.gallery.galleryremote.model.Album;
import com.gallery.galleryremote.util.log.Logger;

public class NewAlbumPresenterImpl implements ActionListener, ItemListener, FocusListener, NewAlbumPresenter {

	private static final Logger LOGGER = Logger.getLogger(NewAlbumPresenterImpl.class);
	private final NewAlbumModel model;
	private final NewAlbumDialog view;

	public NewAlbumPresenterImpl(NewAlbumModel model, NewAlbumDialog view) {
		LOGGER.fine("Creating class instance...");
		
		this.view = view;
		this.model = model;
		
		initEvents();
		view.setVisible(true);
	}

	private void initEvents() {
		jOk.addActionListener(this);
		jCancel.addActionListener(this);
		jAlbum.addItemListener(this);

		jName.addFocusListener(this);
		jTitle.addFocusListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		LOGGER.info("Command selected " + command);

		if (command.equals("Cancel")) {
			view.setVisible(false);
		} else if (command.equals("OK")) {
			newAlbum = new Album(gallery);
			// newAlbum.setSuppressEvents(true);
			newAlbum.setName(jName.getText());
			newAlbum.setTitle(jTitle.getText());
			newAlbum.setCaption(jDescription.getText());

			// newAlbum.setSuppressEvents(false);

			parentAlbum = (Album) jAlbum.getSelectedItem();
			parentAlbum.getGallery().insertNodeInto(newAlbum, parentAlbum, parentAlbum.getChildCount());

			view.setVisible(false);
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			resetUI();
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
		((JTextField) e.getSource()).selectAll();
	}

	@Override
	public void focusLost(FocusEvent e) {
		if (e.getSource() == jTitle && jName.getText().length() == 0) {
			jName.setText(getDefaultName(jTitle.getText()));
		}
	}

	private void resetUI() {
		Album a = (Album) jAlbum.getSelectedItem();
		boolean canCreateSubAlbum = a.getCanCreateSubAlbum();
		view.resetUI(new NewAlbumDTO(canCreateSubAlbum));
	}

}
