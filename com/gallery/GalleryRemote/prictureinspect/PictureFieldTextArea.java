package com.gallery.GalleryRemote.prictureinspect;

import java.awt.Color;
import java.awt.Font;

import javax.swing.text.Document;

public interface PictureFieldTextArea {

	// called by controller
	void addKeyboardListener(PictureInspectorController listener);

	Document getDocument();

	// called by view
	void setText(String text);

	void setEditable(boolean editable);

	void setBackground(Color background);

	void setEnabled(boolean enabled);

	void setLineWrap(boolean wrap);

	void setWrapStyleWord(boolean wrapStyle);

	void setFont(Font font);

	void setDocument(Document doc);

}
