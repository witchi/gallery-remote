package com.gallery.GalleryRemote.albuminspector;

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
import javax.swing.UIManager;

import com.gallery.GalleryRemote.CoreUtils;
import com.gallery.GalleryRemote.GalleryCommCapabilities;
import com.gallery.GalleryRemote.GalleryRemote;
import com.gallery.GalleryRemote.Log;
import com.gallery.GalleryRemote.MoveAlbumDialog;
import com.gallery.GalleryRemote.model.Album;
import com.gallery.GalleryRemote.prefs.UploadPanel;

public class AlbumInspectorPresenterImpl implements ActionListener, ItemListener, KeyListener, AlbumInspectorPresenter {

	private static final String MODULE = "AlbmInspec";

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

		// TODO: should we implement our own interface+class ???
		view.getResizeTo().getEditor().getEditorComponent().addKeyListener(this);
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
		Log.log(Log.LEVEL_TRACE, MODULE, "Item state changed " + source);

		if (source == jBeginning) {
			album.setOverrideAddToBeginning(new Boolean(jBeginning.isSelected()));
		} else if (source == jResizeBeforeUpload) {
			album.setOverrideResize(new Boolean(jResizeBeforeUpload.isSelected()));

			resetUIState();
		} else if (source == jResizeToDefault || source == jResizeToForce) {
			album.setOverrideResizeDefault(new Boolean(jResizeToDefault.isSelected()));

			resetUIState();
		} else {
			Log.log(Log.LEVEL_TRACE, MODULE, "Unknown source " + source);
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
		Log.log(Log.LEVEL_TRACE, MODULE, "Action selected " + command);

		if (source == jFetch) {
			mf.fetchAlbumImages();
		} else if (source == jNew) {
			mf.newAlbum();
		} else if (source == jApply) {
			// todo
		} else if (source == jMove) {
			MoveAlbumDialog mad = new MoveAlbumDialog(mf, album.getGallery(), album);
			Album newParent = mad.getNewParent();

			if (newParent != null) {
				album.moveAlbumTo(GalleryRemote.instance().getCore().getMainStatusUpdate(), newParent);

				// todo: this is too drastic...
				album.getGallery().reload();

				// album.moveAlbumTo(null, null);
			}
		} else if (source == jSlideshow) {
			mf.slideshow();
		} else if (source == jResizeTo) {
			if ("comboBoxChanged".equals(command)) {
				if (ignoreNextComboBoxChanged) {
					ignoreNextComboBoxChanged = false;
				} else {
					readResizeTo(jResizeTo.getSelectedItem().toString());
				}
			}
		} else {
			Log.log(Log.LEVEL_TRACE, MODULE, "Unknown source " + source);
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		readResizeTo(jResizeTo.getEditor().getItem().toString());
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	void refresh(AlbumInspectorDTO dto) {

		boolean oldIgnoreItemChanges = ignoreItemChanges;
		ignoreItemChanges = true;

		if (album == null) {
			jName.setText("");
			jTitle.setText("");
			jSummary.setText("");
			jPictures.setText("");

			setEnabledContent(false);
		} else {
			setEnabledContent(true);

			// setActive(jName, true);
			jName.setText(album.getName());

			// setActive(jTitle, true);
			jTitle.setText(album.getTitle());

			// setActive(jSummary, true);
			jSummary.setText(album.getSummary());

			jPictures.setText("" + album.getSize());

			jResizeBeforeUpload.setSelected(album.getResize());
			jResizeToDefault.setSelected(album.getResizeDefault());
			jResizeToForce.setSelected(!album.getResizeDefault());
			UploadPanel.setupComboValue(album.getResizeDimension(), jResizeTo);
			// hack: the JComboBox will fire an action when the value is changed
			ignoreNextComboBoxChanged = true;
			jBeginning.setSelected(album.getAddToBeginning());

			jFetch.setEnabled(album.getGallery().getComm(mf.jStatusBar)
					.hasCapability(mf.jStatusBar, GalleryCommCapabilities.CAPA_FETCH_ALBUM_IMAGES));
			jMove.setEnabled(album.getGallery().getComm(mf.jStatusBar).hasCapability(mf.jStatusBar, GalleryCommCapabilities.CAPA_MOVE_ALBUM));

			jSlideshow.setEnabled(album.getSize() > 0);
		}

		// todo: protocol support
		jApply.setEnabled(false);
		jName.setEditable(false);
		jName.setBackground(UIManager.getColor("TextField.inactiveBackground"));
		jTitle.setEditable(false);
		jTitle.setBackground(UIManager.getColor("TextField.inactiveBackground"));
		jSummary.setEditable(false);
		jSummary.setBackground(UIManager.getColor("TextField.inactiveBackground"));

		jPictures.setEditable(false);
		jPictures.setBackground(UIManager.getColor("TextField.inactiveBackground"));

		view.resetUI(dto);

		ignoreItemChanges = oldIgnoreItemChanges;
	}

}
