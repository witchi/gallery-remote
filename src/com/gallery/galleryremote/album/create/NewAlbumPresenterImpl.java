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

		initDocuments();
		initEvents();
		resetUI();
		
		view.setVisible(true);
	}

	private void initDocuments() {
		view.getTitleField().setDocument(model.getTitle());
		view.getNameField().setDocument(model.getName());
		view.getDescriptionArea().setDocument(model.getDescription());
	}
	
	private void initEvents() {
		view.getOkButton().addActionListener(this);
		view.getCancelButton().addActionListener(this);
		view.getAlbumComboBox().addItemListener(this);
		view.getNameField().addFocusListener(this);
		view.getTitleField().addFocusListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		LOGGER.info("Command selected " + command);

		if (command.equals("Cancel")) {
			view.setVisible(false);
		} else if (command.equals("OK")) {
			model.getNewAlbum();
			view.setVisible(false);
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			model.setSelectedAlbum((Album) e.getItem());
			resetUI();
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
		((JTextField) e.getSource()).selectAll();
	}

	@Override
	public void focusLost(FocusEvent e) {
		if (e.getSource() == view.getTitleField() && model.getName().getLength() == 0) {
			model.setDefaultName(model.getTitle());
		}
	}

	private void resetUI() {
		NewAlbumDTO dto = new NewAlbumDTO();
		dto.setAlbumList(model.getAlbumList());
		dto.setSelectedAlbum(model.getSelectedAlbum());
		dto.setEnabled(model.getSelectedAlbum().getCanCreateSubAlbum());
		dto.setGalleryUri(model.getGalleryUri());
		view.resetUI(dto);
	}

}
