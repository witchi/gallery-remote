package com.gallery.galleryremote.albuminspector;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;

import com.gallery.galleryremote.model.Album;
import com.gallery.galleryremote.prefs.PreferenceNames;
import com.gallery.galleryremote.prefs.UploadPanel;
import com.gallery.galleryremote.util.DialogUtil;
import com.gallery.galleryremote.util.log.Logger;
import com.gallery.galleryremote.CoreUtils;
import com.gallery.galleryremote.GalleryCommCapabilities;
import com.gallery.galleryremote.GalleryRemote;
import com.gallery.galleryremote.MoveAlbumDialog;

public class AlbumInspectorPresenterImpl implements ActionListener, ItemListener, KeyListener, AlbumInspectorPresenter {

	private static final Logger LOGGER = Logger.getLogger(AlbumInspectorPresenterImpl.class);

	private AlbumInspectorModel model;
	private AlbumInspector view;

	private Action nextFocusAction;
	private Action prevFocusAction;
	private Action nextPictureAction;
	private Action prevPictureAction;

	private boolean ignoreItemChanges;
	private boolean ignoreNextComboBoxChanged;

	public AlbumInspectorPresenterImpl(AlbumInspectorModel model, AlbumInspector view) {
		this.model = model;
		this.view = view;
		initDocuments();
		initEvents();
	}

	private void initEvents() {
		ignoreItemChanges = false;
		ignoreNextComboBoxChanged = false;

		view.getNameTextArea().addKeyboardListener(this);
		view.getTitleTextArea().addKeyboardListener(this);
		view.getSummaryTextArea().addKeyboardListener(this);
		view.getFetchButton().addActionListener(this);
		view.getSlideshowButton().addActionListener(this);
		view.getMoveButton().addActionListener(this);

		view.getBeginning().addItemListener(this);
		view.getResizeBeforeUpload().addItemListener(this);
		view.getResizeToDefault().addItemListener(this);
		view.getResizeToForce().addItemListener(this);
		view.getResizeTo().addActionListener(this);
		view.getResizeTo().addKeyboardListener(this);
	}

	private void initDocuments() {

	}

	@Override
	public Component getView() {
		return (Component) view;
	}

	@Override
	public void setEnabled(boolean enabled) {
		view.setEnabled(enabled);
	}

	@Override
	public Action getNextFocusAction() {
		if (nextFocusAction == null) {
			nextFocusAction = new AbstractAction("Move Focus Forwards") {
				private static final long serialVersionUID = -7481079099741609449L;

				@Override
				public void actionPerformed(ActionEvent evt) {
					((Component) evt.getSource()).transferFocus();
				}
			};
		}
		return nextFocusAction;
	}

	@Override
	public Action getPrevFocusAction() {
		if (prevFocusAction == null) {
			prevFocusAction = new AbstractAction("Move Focus Backwards") {
				private static final long serialVersionUID = -4207478878462672331L;

				@Override
				public void actionPerformed(ActionEvent evt) {
					((Component) evt.getSource()).transferFocusBackward();
				}
			};
		}
		return prevFocusAction;
	}

	@Override
	public Action getNextPictureAction() {
		if (nextPictureAction == null) {
			nextPictureAction = new AbstractAction("Select Next Picture") {
				private static final long serialVersionUID = -3058236737204149574L;

				@Override
				public void actionPerformed(ActionEvent evt) {
					CoreUtils.selectNextPicture();
				}
			};
		}
		return nextPictureAction;
	}

	@Override
	public Action getPrevPictureAction() {
		if (prevPictureAction == null) {
			prevPictureAction = new AbstractAction("Select Prev Picture") {
				private static final long serialVersionUID = 5771498665345070313L;

				@Override
				public void actionPerformed(ActionEvent evt) {
					CoreUtils.selectPrevPicture();
				}
			};
		}
		return prevPictureAction;
	}

	/**
	 * Invoked when an item has been selected or deselected by the user. The code
	 * written for this method performs the operations that need to occur when an
	 * item is selected (or deselected).
	 */
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (ignoreItemChanges) {
			return;
		}

		JComponent source = (JComponent) e.getSource();
		LOGGER.fine("Item state changed " + source);

		if (source == view.getBeginning()) {

			model.setOverrideAddToBeginning(view.getBeginning().isSelected());

		} else if (source == view.getResizeBeforeUpload()) {

			model.setOverrideResize(view.getResizeBeforeUpload().isSelected());
			refresh();

		} else if (source == view.getResizeToDefault() || source == view.getResizeToForce()) {

			model.setOverrideResizeDefault(view.getResizeToDefault().isSelected());
			refresh();

		} else {
			LOGGER.fine("Unknown source " + source);
		}

		ignoreNextComboBoxChanged = false;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ignoreItemChanges) {
			return;
		}

		String command = e.getActionCommand();
		JComponent source = (JComponent) e.getSource();
		LOGGER.fine("Action selected " + command);

		if (source == view.getFetchButton()) {
			model.fetchAlbumImages();

		} else if (source == view.getMoveButton()) {

			MoveAlbumDialog mad = new MoveAlbumDialog(DialogUtil.findParentWindow((Component) view), model);
			Album newParent = mad.getNewParent();

			if (newParent != null) {
				model.moveAlbumTo(GalleryRemote.instance().getCore().getMainStatusUpdate(), newParent);

				// TODO: this is too drastic...
				model.getGallery().reload();
			}

		} else if (source == view.getSlideshowButton()) {
			model.startSlideshow();

		} else if (source == view.getResizeTo()) {
			if ("comboBoxChanged".equals(command)) {
				if (ignoreNextComboBoxChanged) {
					ignoreNextComboBoxChanged = false;
				} else {
					readResizeTo(view.getResizeTo().getSelectedItemAsString());
				}
			}
		} else {
			LOGGER.fine("Unknown source " + source);
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// do nothing
	}

	@Override
	public void keyReleased(KeyEvent e) {
		readResizeTo(view.getResizeTo().getEditedItem());
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// do nothing
	}

	void refresh() {

		boolean oldIgnoreItemChanges = ignoreItemChanges;
		ignoreItemChanges = true;

		AlbumInspectorDTO dto = new AlbumInspectorDTO();
		Album album = model.getAlbum();

		if (album != null) {
			dto.setHasAlbum(true);
			dto.setName(album.getName());
			dto.setTitle(album.getTitle());
			dto.setSummary(album.getSummary());
			dto.setNumberOfPictures(album.getSize());
			dto.setEnabled(true);
			dto.setSelectedResizeBeforeUpload(album.getResize());
			dto.setSelectedResizeToDefault(album.getResizeDefault());
			dto.setSelectedResizeToForce(!album.getResizeDefault());

			UploadPanel.setupComboValue(album.getResizeDimension(), view.getResizeTo());

			// hack: the JComboBox will fire an action when the value is changed
			// TODO add a method to the ResizeTo class, which consumes the action
			// event
			// why is it placed after the setupComboValue(), which changes the
			// value?

			ignoreNextComboBoxChanged = true;

			dto.setSelectedAddBeginning(album.getAddToBeginning());
			dto.setEnabledFetch(model.hasCapability(GalleryCommCapabilities.CAPA_FETCH_ALBUM_IMAGES));
			dto.setEnabledMove(model.hasCapability(GalleryCommCapabilities.CAPA_MOVE_ALBUM));
			dto.setEnabledSlideshow(album.getSize() > 0);
		}

		view.refresh(dto);
		ignoreItemChanges = oldIgnoreItemChanges;
	}

	private void readResizeTo(String text) {
		if (ignoreItemChanges) {
			return;
		}

		try {
			int overrideDimension = model.getOverrideResizeDimension();
			int newOverrideDimension = Integer.parseInt(text);

			if (overrideDimension == -1
					|| (newOverrideDimension == GalleryRemote.instance().properties.getIntDimensionProperty(PreferenceNames.RESIZE_TO))) {
				return;
			}

			LOGGER.fine("Overriding dimension to " + newOverrideDimension);
			model.setOverrideResizeDimension(newOverrideDimension);

		} catch (NumberFormatException ee) {
			LOGGER.throwing(ee);
		}
	}
}
