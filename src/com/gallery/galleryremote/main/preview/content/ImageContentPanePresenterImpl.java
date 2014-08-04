package com.gallery.galleryremote.main.preview.content;

import java.awt.Component;
import java.awt.Image;
import java.awt.Rectangle;

import com.gallery.galleryremote.GalleryRemote;
import com.gallery.galleryremote.prefs.PreferenceNames;
import com.gallery.galleryremote.util.ImageUtils;
import com.gallery.galleryremote.util.log.Logger;

public class ImageContentPanePresenterImpl implements ImageContentPanePresenter {

	private static final Logger LOGGER = Logger.getLogger(ImageContentPanePresenterImpl.class);

	private final ImageContentPaneModel model;
	private final ImageContentPane view;

	public ImageContentPanePresenterImpl(ImageContentPane view, ImageContentPaneModel model) {
		LOGGER.fine("Creating class instance...");
		this.model = model;
		this.view = view;
	}

	@Override
	public void resetUI() {

		ImageContentPaneDTO dto = new ImageContentPaneDTO();
		dto.setColor(GalleryRemote.instance().properties.getColorProperty(PreferenceNames.SLIDESHOW_COLOR));

		if (model.hasPicture()) {

			Image tmpImage = ImageUtils.rotateImage(model.getImage(), model.getExpectedPicture().getAngle(), model.getExpectedPicture()
					.isFlipped(), (Component) view);

			Rectangle currentRect = new Rectangle(view.getLocation().x + (view.getWidth() - tmpImage.getWidth(view)) / 2, view.getLocation().y
					+ (view.getHeight() - tmpImage.getHeight(view)) / 2, tmpImage.getWidth(view), tmpImage.getHeight(view));

			dto.setRectangle(currentRect);
			dto.setImage(tmpImage);
		}

		view.refresh(dto);
	}

}
