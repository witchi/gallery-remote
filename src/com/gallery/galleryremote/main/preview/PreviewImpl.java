package com.gallery.galleryremote.main.preview;

import java.awt.Dimension;

import javax.swing.JFrame;

import com.gallery.galleryremote.GalleryRemote;
import com.gallery.galleryremote.main.preview.content.ImageContentPaneImpl;
import com.gallery.galleryremote.main.preview.glass.CropGlassPaneImpl;
import com.gallery.galleryremote.util.GRI18n;
import com.gallery.galleryremote.util.log.Logger;

public class PreviewImpl extends JFrame implements Preview {

	private static final long serialVersionUID = 3498443714616453620L;
	private static final Logger LOGGER = Logger.getLogger(PreviewImpl.class);

	private ImageContentPaneImpl imageContentPane;
	private CropGlassPaneImpl cropGlassPane;

	public PreviewImpl() {
		LOGGER.fine("Creating class instance...");
		initComponents();
	}

	private void initComponents() {
		setTitle(GRI18n.getString(this.getClass(), "title"));
		setIconImage(GalleryRemote.instance().getMainFrame().getIconImage());
		setBounds(GalleryRemote.instance().properties.getPreviewBounds());
		setContentPane(getImageContentPane());
		setGlassPane(getCropGlassPane());
		getCropGlassPane().setVisible(true);
	}

	private ImageContentPaneImpl getImageContentPane() {
		if (imageContentPane == null) {
			imageContentPane = new ImageContentPaneImpl();
		}
		return imageContentPane;
	}

	private CropGlassPaneImpl getCropGlassPane() {
		if (cropGlassPane == null) {
			cropGlassPane = new CropGlassPaneImpl();
		}
		return cropGlassPane;
	}

	@Override
	public Dimension getGlassPaneSize() {
		return new Dimension(getCropGlassPane().getWidth(), getCropGlassPane().getHeight());
	}
}
