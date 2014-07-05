package com.gallery.GalleryRemote.prictureinspect;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import com.gallery.GalleryRemote.MainFrame;
import com.gallery.GalleryRemote.model.Picture;
import com.gallery.GalleryRemote.util.AbstractModel;

public class PictureInspectorModel extends AbstractModel {

	private MainFrame mainFrame;
	private List<Picture> pictureList = null;
	private HashMap<String, Document> extraFieldDocuments;
	private Document captionDocument;

	public PictureInspectorModel(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		this.pictureList = new ArrayList<Picture>();
		this.extraFieldDocuments = new HashMap<String, Document>();
	}

	// called by MainFrame
	public void setPictureList(List<Picture> pictureList) {
		if (pictureList == null) {
			this.pictureList = new ArrayList<Picture>();
		} else {
			this.pictureList = pictureList;
		}
		fireRefreshEvent();
	}

	void rotatePictureLeft() {
		for (Picture p : pictureList) {
			p.rotateLeft();
		}
		fireRefreshEvent();
		mainFrame.repaint();
		mainFrame.previewFrame.repaint();
	}

	void rotatePictureRight() {
		for (Picture p : pictureList) {
			p.rotateRight();
		}
		fireRefreshEvent();
		mainFrame.repaint();
		mainFrame.previewFrame.repaint();
	}

	void flipPicture() {
		for (Picture p : pictureList) {
			p.flip();
		}
		fireRefreshEvent();
		mainFrame.repaint();
		mainFrame.previewFrame.repaint();
	}

	void deleteSelectedPictures() {
		mainFrame.deleteSelectedPictures();
	}

	void movePicturesUp() {
		mainFrame.movePicturesUp();
	}

	void movePicturesDown() {
		mainFrame.movePicturesDown();
	}

	List<Picture> getPictureList() {
		return new ArrayList<Picture>(pictureList);
	}

	private String getDocumentText(Document document) {
		try {
			return document.getText(0, document.getLength());
		} catch (BadLocationException e) {
			return "";
		}
	}

	void documentUpdate(Document doc) {
		if (pictureList == null || pictureList.size() != 1) {
			return;
		}

		Picture p = pictureList.get(0);

		if (doc == captionDocument) {
			p.setCaption(getDocumentText(captionDocument));
			return;
		}

		for (String name : extraFieldDocuments.keySet()) {
			if (doc != extraFieldDocuments.get(name)) {
				continue;
			}

			if (doc.getLength() == 0) {
				p.removeExtraField(name);
			} else {
				p.setExtraField(name, getDocumentText(doc));
			}
			break; // extra field was found
		}
	}

	Image getThumbnail(Picture p) {
		return mainFrame.getThumbnail(p);
	}

	private void fireRefreshEvent() {
		notifyListeners(new ActionEvent(this, 0, PictureInspectorActions.REFRESH.name()));
	}

	private void fireRemoveExtraFieldsEvent() {
		notifyListeners(new ActionEvent(this, 0, PictureInspectorActions.REMOVE_EXTRA_FIELDS.name()));
	}

	private void fireSetExtraFieldsEvent() {
		notifyListeners(new ActionEvent(this, 0, PictureInspectorActions.SET_EXTRA_FIELDS.name()));
	}

	void removeExtraFields() {
		extraFieldDocuments.clear();
		fireRemoveExtraFieldsEvent();
	}

	void setExtraFieldsForPicture(Picture p) {
		ArrayList<String> newExtraFields = p.getParentAlbum().getExtraFields();

		if (newExtraFields == null) {
			removeExtraFields();
			return;
		}

		if (newExtraFields.equals(extraFieldDocuments.keySet())) {
			return;
		}

		removeExtraFields();
		for (String name : newExtraFields) {
			Document d = new PlainDocument();
			try {
				d.remove(0, d.getLength());
				d.insertString(0, p.getExtraField(name), null);
			} catch (BadLocationException e) {
				// do nothing
			}
			extraFieldDocuments.put(name, d);
		}
		fireSetExtraFieldsEvent();
	}

	Map<String, Document> getExtraFields() {
		return Collections.unmodifiableMap(extraFieldDocuments);
	}

	boolean hasCapability(Picture p, int capability) {
		return p.getParentAlbum().getGallery().getComm(mainFrame.jStatusBar).hasCapability(mainFrame.jStatusBar, capability);
	}
}
