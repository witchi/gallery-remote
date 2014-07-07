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
import com.gallery.GalleryRemote.util.GRI18n;

public class PictureInspectorModelImpl extends AbstractModel implements PictureInspectorModel {

	private static final String MODULE = "PictInspec";
	private MainFrame mainFrame;
	private List<Picture> pictureList = null;
	private final HashMap<String, Document> extraFieldDocuments;
	private final HashMap<String, Document> fieldDocuments;

	public PictureInspectorModelImpl(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		this.pictureList = new ArrayList<Picture>();
		this.extraFieldDocuments = new HashMap<String, Document>();
		this.fieldDocuments = new HashMap<String, Document>();
	}

	@Override
	public void setPictureList(List<Picture> pictureList) {
		if (pictureList == null) {
			this.pictureList = new ArrayList<Picture>();
		} else {
			this.pictureList = pictureList;
		}
		fireRefreshEvent();
	}

	@Override
	public void rotatePictureLeft() {
		for (Picture p : pictureList) {
			p.rotateLeft();
		}
		fireRefreshEvent();
		mainFrame.repaint();
		mainFrame.previewFrame.repaint();
	}

	@Override
	public void rotatePictureRight() {
		for (Picture p : pictureList) {
			p.rotateRight();
		}
		fireRefreshEvent();
		mainFrame.repaint();
		mainFrame.previewFrame.repaint();
	}

	@Override
	public void flipPicture() {
		for (Picture p : pictureList) {
			p.flip();
		}
		fireRefreshEvent();
		mainFrame.repaint();
		mainFrame.previewFrame.repaint();
	}

	@Override
	public void deleteSelectedPictures() {
		mainFrame.deleteSelectedPictures();
	}

	@Override
	public void movePicturesUp() {
		mainFrame.movePicturesUp();
	}

	@Override
	public void movePicturesDown() {
		mainFrame.movePicturesDown();
	}

	@Override
	public List<Picture> getPictureList() {
		return new ArrayList<Picture>(pictureList);
	}

	private String getDocumentText(Document document) {
		try {
			return document.getText(0, document.getLength());
		} catch (BadLocationException e) {
			return "";
		}
	}

	@Override
	public void documentUpdate(Document doc) {
		if (pictureList == null || pictureList.size() != 1) {
			return;
		}

		Picture p = pictureList.get(0);

		if (doc == fieldDocuments.get("Caption")) {
			p.setCaption(getDocumentText(fieldDocuments.get("Caption")));
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

	@Override
	public Image getThumbnail(Picture p) {
		return mainFrame.getThumbnail(p);
	}

	private void fireRefreshEvent() {
		notifyListeners(new ActionEvent(this, 0, PictureInspectorActions.REFRESH.name()));
	}

	private void fireRemoveExtraFieldsEvent() {
		notifyListeners(new ActionEvent(this, 0, PictureInspectorActions.REMOVE_EXTRA_FIELDS.name()));
	}

	@Override
	public void removeExtraFields() {
		extraFieldDocuments.clear();
		fireRemoveExtraFieldsEvent();
	}

	@Override
	public Map<String, Document> getExtraFieldDocuments(Picture p) {
		ArrayList<String> newExtraFields = p.getParentAlbum().getExtraFields();

		if (newExtraFields == null) {
			removeExtraFields();
			return Collections.unmodifiableMap(extraFieldDocuments);
		}

		if (newExtraFields.equals(extraFieldDocuments.keySet())) {
			return Collections.unmodifiableMap(extraFieldDocuments);
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
		return Collections.unmodifiableMap(extraFieldDocuments);
	}

	@Override
	public Map<String, Document> getFieldDocuments() {
		if (fieldDocuments.isEmpty()) {
			fieldDocuments.put("Path", new PlainDocument());
			fieldDocuments.put("Album", new PlainDocument());
			fieldDocuments.put("Size", new PlainDocument());
			fieldDocuments.put("Caption", new PlainDocument());
			Document d = new PlainDocument();
			try {
				d.remove(0, d.getLength());
				d.insertString(0, GRI18n.getString(MODULE, "icon"), null);
			} catch (BadLocationException e) {
				// do nothing
			}
			fieldDocuments.put("Icon", d);
		}
		return Collections.unmodifiableMap(fieldDocuments);
	}

	@Override
	public boolean hasCapability(Picture p, int capability) {
		return p.getParentAlbum().getGallery().getComm(mainFrame.jStatusBar).hasCapability(mainFrame.jStatusBar, capability);
	}

	@Override
	public void setDocumentText(String name, String text) {
		Document d = fieldDocuments.get(name);
		try {
			d.remove(0, d.getLength());
			d.insertString(0, text, null);
		} catch (BadLocationException e) {
			// do nothing
		}
	}
}
