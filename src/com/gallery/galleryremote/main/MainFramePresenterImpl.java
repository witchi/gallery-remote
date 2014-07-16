package com.gallery.galleryremote.main;

import java.applet.Applet;

import javax.swing.DefaultComboBoxModel;

import com.gallery.galleryremote.GRApplet;
import com.gallery.galleryremote.GalleryRemote;
import com.gallery.galleryremote.model.Gallery;
import com.gallery.galleryremote.prefs.PropertiesFile;
import com.gallery.galleryremote.util.log.Logger;

public class MainFramePresenterImpl implements MainFramePresenter {

	private static final Logger LOGGER = Logger.getLogger(MainFramePresenterImpl.class);

	private final MainFrameModel model;
	private final MainFrame view;
	
	public MainFramePresenterImpl(MainFrameModel model, MainFrame view) {
		LOGGER.fine("Creating class instance...");
		this.model = model;
		this.view = view;
	}

	/**
	 * called by the GalleryRemoteMainFrame class just after the instantiation of
	 * MainFrame
	 */
	@Override
	public void init() {
		macOSXRegistration();

		PropertiesFile p = GalleryRemote.instance().properties;

		// load galleries
		galleries = new DefaultComboBoxModel<Gallery>();
		if (!GalleryRemote.instance().isAppletMode()) {
			int i = 0;
			while (true) {
				try {
					Gallery g = Gallery.readFromProperties(p, i++, getStatusBar());
					if (g == null) {
						break;
					}
					galleries.addElement(g);
				} catch (Exception e) {
					LOGGER.warning("Error trying to load Gallery profile " + i);
					LOGGER.throwing(e);
				}
			}
		} else {
			Applet applet = GalleryRemote.instance().getApplet();
			GRApplet.AppletInfo info = ((GRApplet) applet).getGRAppletInfo();
			info.gallery.addTreeModelListener(this);
			galleries.addElement(info.gallery);
		}

		setIconImage(iconImage);
	}

}
