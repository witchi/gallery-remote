package com.gallery.galleryremote.main.preview.content;

import java.awt.Component;
import java.awt.Image;
import java.awt.Rectangle;

public class ImageContentPanePresenterImpl implements ImageContentPanePresenter {

	private final ImageContentPaneModel model;
	private final ImageContentPane view;

	public ImageContentPanePresenterImpl(ImageContentPaneModel model, ImageContentPane view) {
		this.model = model;
		this.view = view;
		model.setDefaultBackgroundColor(view.getBackground());
	}

	private void refreshUI() {
		view.setColor(model.getBackgroundColor());

		Image tmpImage = model.getImage((Component) view);
		if (tmpImage == null) {
			view.setImage(null, null);
		} else {
			Rectangle currentRect = new Rectangle(view.getLocation().x + (view.getWidth() - tmpImage.getWidth(this)) / 2, view.getLocation().y
					+ (view.getHeight() - tmpImage.getHeight(this)) / 2, tmpImage.getWidth(this), tmpImage.getHeight(this));
			view.setImage(tmpImage, currentRect);
		}

		view.repaint();
	}
}
