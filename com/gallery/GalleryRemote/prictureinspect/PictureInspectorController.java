package com.gallery.GalleryRemote.prictureinspect;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.gallery.GalleryRemote.CoreUtils;
import com.gallery.GalleryRemote.Log;
import com.gallery.GalleryRemote.MainFrame;
import com.gallery.GalleryRemote.model.Picture;

public class PictureInspectorController implements ActionListener, DocumentListener {

	private static final String MODULE = "PictInspec";

	private List<Picture> pictureList = null;
	private MainFrame mainFrame;
	private PictureInspector view;

	private Action nextFocusAction;
	private Action prevFocusAction;
	private Action nextPictureAction;
	private Action prevPictureAction;

	public PictureInspectorController(MainFrame mainFrame, PictureInspector view) {
		this.pictureList = new ArrayList<Picture>();
		this.mainFrame = mainFrame;
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
	}
	
	public void setPictureList(List<Picture> pictureList) {
		// Log.log(Log.TRACE, MODULE, "setPictures " + pictures);
		// Log.logStack(Log.TRACE, MODULE);
		if (pictureList == null) {
			this.pictureList = new ArrayList<Picture>();
		} else {
			this.pictureList = pictureList;
		}
		view.refresh();
	}

	public List<Picture> getPictureList() {
		return new ArrayList<Picture>(pictureList);
	}

	// Event handling
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		Log.log(Log.LEVEL_INFO, MODULE, "Command selected " + command);

		if (command.equals(PictureInspectorActions.ACTION_DELETE)) {
			// We must call the MainFrame to delete pictures
			// so that it can know that the document is now dirty.
			mainFrame.deleteSelectedPictures();
		} else if (command.equals(PictureInspectorActions.ACTION_UP)) {
			// We must call the MainFrame to move pictures
			// so that it can know that the document is now dirty.
			mainFrame.movePicturesUp();
		} else if (command.equals(PictureInspectorActions.ACTION_DOWN)) {
			// We must call the MainFrame to move pictures
			// so that it can know that the document is now dirty.
			mainFrame.movePicturesDown();
		} else if (command.equals(IconAreaActions.ACTION_ROTATE_LEFT)) {
			rotatePictureLeft();
		} else if (command.equals(IconAreaActions.ACTION_ROTATE_RIGHT)) {
			rotatePictureRight();
		} else if (command.equals(IconAreaActions.ACTION_FLIP)) {
			flipPicture();
		}
	}

	private void rotatePictureLeft() {
		for (Picture p : pictureList) {
			p.rotateLeft();
		}
		view.refresh();
		mainFrame.repaint();
		mainFrame.previewFrame.repaint();
	}

	private void rotatePictureRight() {
		for (Picture p : pictureList) {
			p.rotateRight();
		}
		view.refresh();
		mainFrame.repaint();
		mainFrame.previewFrame.repaint();
	}

	private void flipPicture() {
		for (Picture p : pictureList) {
			p.flip();
		}
		view.refresh();
		mainFrame.repaint();
		mainFrame.previewFrame.repaint();
	}

	/**
	 * Caption JTextArea events.
	 */
	@Override
	public void insertUpdate(DocumentEvent e) {
		textUpdate(e);
	}

	/**
	 * Caption JTextArea events.
	 */
	@Override
	public void removeUpdate(DocumentEvent e) {
		textUpdate(e);
	}

	/**
	 * Caption JTextArea events.
	 */
	@Override
	public void changedUpdate(DocumentEvent e) {
		textUpdate(e);
	}

	private void textUpdate(DocumentEvent e) {

		if (pictureList == null || pictureList.size() != 1) {
			return;
		}

		Picture p = pictureList.get(0);

		if (e.getDocument() == view.getCaption().getDocument()) {
			p.setCaption(view.getCaption().getText());
			return;
		}

		for (String name : view.getExtraFieldNames()) {

			PictureFieldTextArea field = view.getExtraField(name);
			if (e.getDocument() == field.getDocument()) {
				String text = field.getText();
				if (text.length() == 0) {
					p.removeExtraField(name);
				} else {
					p.setExtraField(name, text);
				}
				break; // extra field was found
			}
		}
	}

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

}
