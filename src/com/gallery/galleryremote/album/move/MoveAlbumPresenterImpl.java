package com.gallery.galleryremote.album.move;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.gallery.galleryremote.model.Album;
import com.gallery.galleryremote.util.log.Logger;

public class MoveAlbumPresenterImpl implements MoveAlbumPresenter, ActionListener {

	private static final Logger LOGGER = Logger.getLogger(MoveAlbumPresenterImpl.class);
	private final MoveAlbumDialog view;
	private final MoveAlbumModel model;

	public MoveAlbumPresenterImpl(MoveAlbumDialog view, MoveAlbumModel model) {
		LOGGER.fine("Creating class instance...");
		this.model = model;
		this.view = view;

		initEvents();
		resetUI();

		view.setVisible(true);
	}

	private void initEvents() {
		view.getOkButton().addActionListener(this);
		view.getCancelButton().addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		LOGGER.info("Command selected " + command);

		if (command.equals("Cancel")) {
			view.setVisible(false);
		} else if (command.equals("OK")) {
			model.setSelectedAlbum((Album) view.getAlbumComboBox().getSelectedItem());
			view.setVisible(false);
		}
	}

	private void resetUI() {
		MoveAlbumDTO dto = new MoveAlbumDTO();
		dto.setAlbumName(model.getAlbumName());
		dto.setAlbumList(model.getAlbumList());
		view.resetUI(dto);
	}
}
