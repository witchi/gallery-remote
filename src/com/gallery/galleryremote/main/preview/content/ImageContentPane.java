package com.gallery.galleryremote.main.preview.content;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.JPanel;

import com.gallery.galleryremote.GalleryRemote;
import com.gallery.galleryremote.prefs.PreferenceNames;
import com.gallery.galleryremote.util.ImageUtils;

public class ImageContentPane extends JPanel {
	private static final long serialVersionUID = 6465140694468227338L;

	private volatile Image image;
	private volatile Color color;

	public void setColor(Color color) {
		this.color = color;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		// TODO: move that to presenter and use setColor()
		Color c = GalleryRemote.instance().properties.getColorProperty(PreferenceNames.SLIDESHOW_COLOR);
		
		
		if (this.color != null) {
			g.setColor(this.color);
		} else {
			g.setColor(getBackground());
		}

		g.fillRect(0, 0, getSize().width, getSize().height);

		if (this.image == null) {
			return;
		}

		if (loader.imageShowNow != null && loader.pictureShowWant != null) {
			// Log.log(Log.LEVEL_TRACE, MODULE, "New image: " +
			// loader.imageShowNow);

			Image tmpImage = ImageUtils.rotateImage(loader.imageShowNow, loader.pictureShowWant.getAngle(),
					loader.pictureShowWant.isFlipped(), this);

			currentRect = new Rectangle(getLocation().x + (getWidth() - tmpImage.getWidth(this)) / 2, getLocation().y
					+ (getHeight() - tmpImage.getHeight(this)) / 2, tmpImage.getWidth(this), tmpImage.getHeight(this));

			g2.drawImage(this.image, currentRect.x, currentRect.y, getContentPane());
		}
	}
}
