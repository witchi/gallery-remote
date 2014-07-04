package com.gallery.GalleryRemote.prictureinspect;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import com.gallery.GalleryRemote.CoreUtils;
import com.gallery.GalleryRemote.Log;

public class PictureInspectorControllerImpl implements ActionListener, DocumentListener, PictureInspectorController {

	private static final String MODULE = "PictInspec";

	private PictureInspectorModel model;
	private PictureInspector view;

	private Action nextFocusAction;
	private Action prevFocusAction;
	private Action nextPictureAction;
	private Action prevPictureAction;

	public PictureInspectorControllerImpl(PictureInspectorModel model, PictureInspector view) {
		this.model = model;
		this.view = view;
		initEvents();
	}

	private void initEvents() {
		view.getDeleteButton().addActionListener(this);
		view.getUpButton().addActionListener(this);
		view.getDownButton().addActionListener(this);
		view.getRotateLeftButton().addActionListener(this);
		view.getRotateRightButton().addActionListener(this);
		view.getFlipButton().addActionListener(this);
		view.getCaption().getDocument().addDocumentListener(this);
		view.getCaption().addKeyboardListener(this);
		model.addActionListener(this);
	}

	// Event handling
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		Log.log(Log.LEVEL_INFO, MODULE, "Command selected " + command);

		if (command.equals(PictureInspectorActions.ACTION_DELETE)) {
			model.deleteSelectedPictures();
		} else if (command.equals(PictureInspectorActions.ACTION_UP)) {
			model.movePicturesUp();
		} else if (command.equals(PictureInspectorActions.ACTION_DOWN)) {
			model.movePicturesDown();
		} else if (command.equals(PictureInspectorActions.ACTION_ROTATE_LEFT)) {
			model.rotatePictureLeft();
		} else if (command.equals(PictureInspectorActions.ACTION_ROTATE_RIGHT)) {
			model.rotatePictureRight();
		} else if (command.equals(PictureInspectorActions.ACTION_FLIP)) {
			model.flipPicture();
		} else if (command.equals(PictureInspectorActions.ACTION_REFRESH)) {
			view.refresh();
		} else if (command.equals(PictureInspectorActions.ACTION_REMOVE_EXTRA_FIELDS)) {
			view.removeExtraFields();
		} else if (command.equals(PictureInspectorActions.ACTION_SET_EXTRA_FIELDS)) {
			setExtraFieldsForPicture(/* need parameters */);
		}
	}

	/**
	 * Caption JTextArea events.
	 */
	@Override
	public void insertUpdate(DocumentEvent e) {
		model.textUpdate(e);
	}

	/**
	 * Caption JTextArea events.
	 */
	@Override
	public void removeUpdate(DocumentEvent e) {
		model.textUpdate(e);
	}

	/**
	 * Caption JTextArea events.
	 */
	@Override
	public void changedUpdate(DocumentEvent e) {
		model.textUpdate(e);
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

	// called by an action of the model
	private void setExtraFieldsForPicture(Map<String, Document> docList) {
		Collection<PictureFieldTextAreaImpl> fieldSet = view.setExtraFields(docList);
		for (PictureFieldTextAreaImpl field : fieldSet) {
			field.getDocument().addDocumentListener(this);
			field.addKeyboardListener(this);
		}
	}

}
