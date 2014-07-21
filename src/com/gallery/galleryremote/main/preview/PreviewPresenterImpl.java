package com.gallery.galleryremote.main.preview;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;

import com.gallery.galleryremote.GalleryRemote;
import com.gallery.galleryremote.model.Picture;
import com.gallery.galleryremote.util.ImageLoaderUtil;
import com.gallery.galleryremote.util.log.Logger;

public class PreviewPresenterImpl implements PreviewPresenter, ImageLoaderUtil.ImageLoaderUser {

	private static final Logger LOGGER = Logger.getLogger(PreviewPresenterImpl.class);

	private final PreviewModel model;
	private final Preview view;

	
	// AR: public added
	public ImageLoaderUtil loader;

	public PreviewPresenterImpl(PreviewModel model, Preview view) {
		LOGGER.fine("Creating class instance...");
		this.model = model;
		this.view = view;

		loader = new ImageLoaderUtil(GalleryRemote.instance().properties.getIntProperty("cacheSize", 10), this);
		initEvents(); // TODO: should we call that here?
	}

	public void resetUI() {
	
	}

	private void initEvents() {
		view.addComponentListener(new PreviewComponentAdapter(loader));
		view.addWindowListener(new PreviewWindowAdapter());
	}

	public void setVisible(boolean visible) {
		if (visible) {
			view.setVisible(true);
			return;
		}
		// release memory if no longer necessary
		loader.flushMemory();
		view.setVisible(false);
		loader.preparePicture(null, false, false);
	}

	@Override
	public void pictureReady() {
		view.repaint();
	}

	@Override
	public boolean blockPictureReady(Image image, Picture picture) {
		return false;
	}

	@Override
	public Dimension getImageSize() {
		return view.getGlassPaneSize();
	}

	@Override
	public void nullRect() {
		currentRect = null;
	}

	@Override
	public void pictureStartDownloading(Picture picture) {
	}

	@Override
	public void pictureStartProcessing(Picture picture) {
	}

	@Override
	public void pictureLoadError(Picture picture) {
	}

}
