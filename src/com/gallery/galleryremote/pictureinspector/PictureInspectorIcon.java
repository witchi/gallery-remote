package com.gallery.galleryremote.pictureinspector;

import javax.swing.JLabel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import com.gallery.galleryremote.util.log.Logger;

public class PictureInspectorIcon extends JLabel implements DocumentListener {

	private static final long serialVersionUID = 5502733913250753072L;
	private static final Logger LOGGER = Logger.getLogger(PictureInspectorIcon.class);
	private Document document;

	public PictureInspectorIcon() {
		LOGGER.fine("Creating class instance...");
		document = new PlainDocument();
	}

	public void setDocument(Document doc) {
		this.document = doc;
		doc.addDocumentListener(this);
	}

	@Override
	public String getText() {
		try {
			return document.getText(0, document.getLength());
		} catch (Exception e) {
			return "";
		}
	}

	@Override
	public void setText(String text) {
		super.setText(text);
		try {
			document.remove(0, document.getLength());
			document.insertString(0, text, null);
		} catch (Exception e) {
			// do nothing
		}
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		try {
			Document d = e.getDocument();
			super.setText(d.getText(0, d.getLength()));
		} catch (BadLocationException e1) {
			// do nothing
		}
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		try {
			Document d = e.getDocument();
			super.setText(d.getText(0, d.getLength()));
		} catch (BadLocationException e1) {
			// do nothing
		}
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		try {
			Document d = e.getDocument();
			super.setText(d.getText(0, d.getLength()));
		} catch (BadLocationException e1) {
			// do nothing
		}
	}

}
