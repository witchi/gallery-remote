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
	private volatile Color color;
	private volatile Rectangle rectangle;

	public ImageContentPaneImpl() {
		LOGGER.fine("Creating class instance...");
	}

	@Override
	public void refreshUI(ImageContentPaneDTO dto) {
		image = dto.getImage();
		color = dto.getColor();
		rectangle = dto.getRectangle();
		repaint();
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

		g2.drawImage(this.image, rectangle.x, rectangle.y, this);
	}
}
