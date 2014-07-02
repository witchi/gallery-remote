package com.gallery.GalleryRemote;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;
import java.util.Random;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.jdesktop.jdic.screensaver.ScreensaverContext;
import org.jdesktop.jdic.screensaver.ScreensaverSettings;

import com.gallery.GalleryRemote.model.Album;
import com.gallery.GalleryRemote.model.Gallery;
import com.gallery.GalleryRemote.model.Picture;
import com.gallery.GalleryRemote.prefs.PreferenceNames;
import com.gallery.GalleryRemote.prefs.PropertiesFile;
import com.gallery.GalleryRemote.util.ImageLoaderUtil;
import com.gallery.GalleryRemote.util.ImageUtils;

/**
 * Created by IntelliJ IDEA. User: paour Date: Jan 14, 2004
 */
public class GalleryRemoteScreenSaver extends GalleryRemote implements
		GalleryRemoteCore, PreferenceNames, ListDataListener,
		ImageLoaderUtil.ImageLoaderUser {

	DefaultComboBoxModel<Gallery> galleries = null;
	Gallery gallery;
	Album album;
	DroppableList jPicturesList;
	StatusUpdateAdapter statusUpdate = new StatusUpdateAdapter();
	ScreensaverContext context;
	Picture currentPicture = null;
	File currentImage = null;
	ImageLoaderUtil loader = new ImageLoaderUtil(3, this);
	Dimension size = null;
	boolean newImage = false;
	ArrayList<Picture> picturesList = null;
	int delay = 5000;
	boolean hasSettings = true;

	@Override
	protected void initializeGR() {
		super.initializeGR();

		CoreUtils.initCore();

		Log.startLog(instance().properties.getIntProperty(PreferenceNames.LOG_LEVEL),
				instance().properties.getBooleanProperty("toSysOut"));

		startup();
	}

	public void setContext(ScreensaverContext context) {
		this.context = context;
	}

	@Override
	public void createProperties() {
		super.createProperties();

		File f = new File(System.getProperty("user.home") + File.separator
				+ ".GalleryRemote" + File.separator);

		f.mkdirs();

		File pf = new File(f, "GalleryRemoteScreenSaver.properties");

		if (!pf.exists()) {
			try {
				pf.createNewFile();
			} catch (IOException e) {
				Log.logException(Log.LEVEL_ERROR, MODULE, e);
			}
		}

		properties = new PropertiesFile(properties, pf.getPath(), "user");
	}

	@Override
	public Frame getMainFrame() {
		return null;
	}

	@Override
	public GalleryRemoteCore getCore() {
		return this;
	}

	@Override
	protected void loadIcons() {
	}

	@Override
	public void startup() {
		ScreensaverSettings settings = context.getSettings();
		// settings.loadSettings("Gallery");
		Log.log(Log.LEVEL_TRACE, MODULE, "Screensaver settings: "
				+ settings.getProperties().toString());

		Properties p = settings.getProperties();

		galleries = new DefaultComboBoxModel<Gallery>();

		gallery = new Gallery(GalleryRemote.instance().getCore().getMainStatusUpdate());
		String curl = settings.getProperty("curl");

		if (curl != null) {
			try {
				p.load(new URL(curl).openStream());
			} catch (IOException e) {
				Log.log(Log.LEVEL_CRITICAL, MODULE,
						"Error trying to get configuration file: " + curl);
				Log.logException(Log.LEVEL_CRITICAL, MODULE, e);
			}

			Log.log(Log.LEVEL_TRACE, MODULE, "Fetched settings: "
					+ settings.getProperties().toString());
		}

		String url = p.getProperty("url");
		if (url != null) {
			gallery.setStUrlString(url);
			if (p.getProperty("username") == null
					|| p.getProperty("username").trim().length() == 0) {
				gallery.cookieLogin = true;
			} else {
				gallery.setUsername(p.getProperty("username"));
				gallery.setPassword(p.getProperty("password"));
			}
			gallery.setType(Gallery.TYPE_STANDALONE);

			properties.setBooleanProperty(SLIDESHOW_RECURSIVE,
					p.getProperty("recursive") != null);
			properties.setBooleanProperty(SLIDESHOW_LOWREZ,
					p.getProperty("hires") == null);
			properties.setBooleanProperty(SLIDESHOW_NOSTRETCH,
					p.getProperty("stretch") == null);
			delay = Integer.parseInt(p.getProperty("delay")) * 1000;
			String albums = p.getProperty("album");
			String[] albumsA = albums.split(",");

			galleries.addElement(gallery);
			ImageUtils.deferredTasks();

			album = new Album(gallery);
			album.setName(albumsA[new Random().nextInt(albumsA.length)]);
			album.addListDataListener(this);

			album.fetchAlbumImages(statusUpdate, GalleryRemote.instance().properties
					.getBooleanProperty(SLIDESHOW_RECURSIVE), 200, true);
		} else {
			hasSettings = false;
		}
	}

	public void nextPicture() {
		if (GalleryRemote.instance() == null) {
			return;
		}

		if (picturesList == null || picturesList.size() == 0) {
			picturesList = new ArrayList<Picture>(album.getPicturesList());
		}

		Picture p = (Picture) picturesList.get((int) Math.floor(Math.random()
				* picturesList.size()));
		picturesList.remove(p);

		loader.preparePicture(p, true, true);
	}

	@Override
	public void shutdown() {
		if (GalleryRemote.instance() != null) {
			GalleryRemote.shutdownInstance();
		}
	}

	@Override
	public void shutdown(boolean shutdownOs) {
		shutdown();
	}

	@Override
	public void flushMemory() {
	}

	@Override
	public void preloadThumbnails(Iterator<Picture> pictures) {
	}

	@Override
	public Image getThumbnail(Picture p) {
		return null;
	}

	@Override
	public StatusUpdate getMainStatusUpdate() {
		return statusUpdate;
	}

	@Override
	public DefaultComboBoxModel<Gallery> getGalleries() {
		return galleries;
	}

	@Override
	public void thumbnailLoadedNotify() {
	}

	@Override
	public void setInProgress(boolean inProgress) {
	}

	@Override
	public void addPictures(File[] files, int index, boolean select) {
		album.addPictures(files, index);
	}

	@Override
	public void addPictures(Picture[] pictures, int index, boolean select) {
		album.addPictures(Arrays.asList(pictures), index);
	}

	@Override
	public Album getCurrentAlbum() {
		return album;
	}

	@Override
	public JList<Picture> getPicturesList() {
		return jPicturesList;
	}

	@Override
	public void contentsChanged(ListDataEvent e) {
		if (album.isHasFetchedImages()) {
			Log.log(Log.LEVEL_TRACE, MODULE, "Done downloading album info");

			nextPicture();
		}
	}

	@Override
	public void intervalAdded(ListDataEvent e) {
	}

	@Override
	public void intervalRemoved(ListDataEvent e) {
	}

	@Override
	public void pictureReady() {
		Log.log(Log.LEVEL_TRACE, MODULE,
				"PictureReady, letting screensaver thread update");
		newImage = true;

		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				nextPicture();
			}
		}.start();
	}

	@Override
	public boolean blockPictureReady(Image image, Picture picture) {
		return false;
	}

	@Override
	public Dimension getImageSize() {
		if (size == null) {
			size = context.getComponent().getBounds().getSize();
		}

		return size;
	}

	@Override
	public void nullRect() {
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
