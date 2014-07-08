package com.gallery.GalleryRemote.prictureinspect;

import java.awt.Color;
import java.awt.Font;

import javax.swing.border.Border;
import javax.swing.text.Document;

interface PictureFieldTextArea {

// called by controller
	void addKeyboardListener(PictureInspectorPresenter listener);

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
	
	void setBorder(Border border);

}
