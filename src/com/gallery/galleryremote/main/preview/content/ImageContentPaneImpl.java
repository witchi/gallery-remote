package com.gallery.galleryremote.main.preview.content;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.JPanel;

import com.gallery.galleryremote.util.log.Logger;

public class ImageContentPaneImpl extends JPanel implements ImageContentPane {
	private static final long serialVersionUID = 6465140694468227338L;
	private static final Logger LOGGER = Logger.getLogger(ImageContentPaneImpl.class);

	private volatile Image image;
	private volatile Rectangle imageArea;
	private volatile Color color;

	public ImageContentPaneImpl() {
		LOGGER.fine("Creating class instance...");
	}

	@Override
	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	public void setImage(Image image, Rectangle imageArea) {
		this.image = image;
		this.imageArea = imageArea;
	}

	private void clearPane(Graphics g) {
		g.setColor(this.color);
		g.fillRect(0, 0, getSize().width, getSize().height);
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		clearPane(g);

		if (this.image == null) {
			return;
		}

		g2.drawImage(this.image, this.imageArea.x, this.imageArea.y, this);
	}
}
