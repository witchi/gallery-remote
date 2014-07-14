package com.gallery.galleryremote;

import com.gallery.galleryremote.util.DialogUtil;
import com.gallery.galleryremote.util.log.LogManager;
import com.gallery.galleryremote.util.log.Logger;
import com.gallery.galleryremote.prefs.PropertiesFile;
import com.gallery.galleryremote.prefs.PreferenceNames;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * @author paour
 * @author arothe
 * @version Jan 14, 2004
 */
public class GalleryRemoteMini extends GalleryRemote {

	private static final Logger LOGGER = Logger.getLogger(GalleryRemoteMini.class);

	@Override
	protected void initializeGR() {
		super.initializeGR();

		CoreUtils.initCore();

		try {
			LogManager.setup(instance().properties.getIntProperty(PreferenceNames.LOG_LEVEL), instance().properties.getBooleanProperty("toSysOut"));
		} catch (IOException e) {
			throw new RuntimeException("Problems with creating the log files");
		}
		Log.startLog(instance().properties.getIntProperty(PreferenceNames.LOG_LEVEL), instance().properties.getBooleanProperty("toSysOut"));
	}

	@Override
	public void createProperties() {
		super.createProperties();

		properties = getAppletOverrides(properties, "GRDefault_");

		File f = new File(System.getProperty("user.home") + File.separator + ".GalleryRemote" + File.separator);

		f.mkdirs();

		File pf = new File(f, "GalleryRemoteApplet.properties");

		if (!pf.exists()) {
			try {
				pf.createNewFile();
			} catch (IOException e) {
				LOGGER.throwing(e);
				//Log.logException(Log.LEVEL_ERROR, MODULE, e);
			}
		}

		properties = new PropertiesFile(properties, pf.getPath(), "user");
		properties = getAppletOverrides(properties, "GROverride_");
	}

	@Override
	public Frame getMainFrame() {
		return DialogUtil.findParentWindow(applet);
	}

	@Override
	public GalleryRemoteCore getCore() {
		return (GalleryRemoteCore) applet;
	}
}
