package com.gallery.galleryremote.main.preview;

import java.awt.Dimension;
import java.awt.Image;

import com.gallery.galleryremote.GalleryRemote;
import com.gallery.galleryremote.main.preview.glass.CropGlassPane;
import com.gallery.galleryremote.main.preview.glass.CropGlassPaneModelImpl;
import com.gallery.galleryremote.main.preview.glass.CropGlassPanePresenter;
import com.gallery.galleryremote.main.preview.glass.CropGlassPanePresenterImpl;
import com.gallery.galleryremote.model.Picture;
import com.gallery.galleryremote.util.ImageLoaderUtil;
import com.gallery.galleryremote.util.log.Logger;

public class PreviewPresenterImpl implements PreviewPresenter, ImageLoaderUtil.ImageLoaderListener {

	private static final Logger LOGGER = Logger.getLogger(PreviewPresenterImpl.class);

	private final PreviewModel model;
	private final Preview view;
	private final ImageLoaderUtil loader;

	private final CropGlassPanePresenter glassPresenter;
	private final CropGlassPaneModelImpl glassModel;

	public PreviewPresenterImpl(PreviewModel model, Preview view) {
		LOGGER.fine("Creating class instance...");
		this.model = model;
		this.view = view;
		this.loader = new ImageLoaderUtil(GalleryRemote.instance().properties.getIntProperty("cacheSize", 10), this);

		this.glassModel = new CropGlassPaneModelImpl(this.loader);
		this.glassPresenter = new CropGlassPanePresenterImpl(this.glassModel, (CropGlassPane) view.getGlassPane());

		this.imageModel = new ImageContentPaneModel(this.loader);
		this.imagePresenter = new ImageContentPanePresenter(this.imageModel, view.getContentPane());

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
