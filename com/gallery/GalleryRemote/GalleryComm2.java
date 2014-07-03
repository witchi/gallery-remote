/*
 * Gallery Remote - a File Upload Utility for Gallery
 *
 * Gallery - a web based photo album viewer and editor
 * Copyright (C) 2000-2001 Bharat Mediratta
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package com.gallery.GalleryRemote;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import javax.swing.JOptionPane;

import HTTPClient.Codecs;
import HTTPClient.HTTPConnection;
import HTTPClient.HTTPResponse;
import HTTPClient.ModuleException;
import HTTPClient.NVPair;
import HTTPClient.ParseException;
import HTTPClient.TransferListener;

import com.gallery.GalleryRemote.model.Album;
import com.gallery.GalleryRemote.model.Gallery;
import com.gallery.GalleryRemote.model.Picture;
import com.gallery.GalleryRemote.prefs.GalleryProperties;
import com.gallery.GalleryRemote.prefs.PreferenceNames;
import com.gallery.GalleryRemote.util.GRI18n;
import com.gallery.GalleryRemote.util.HTMLEscaper;
import com.gallery.GalleryRemote.util.NaturalOrderComparator;
import com.gallery.GalleryRemote.util.UrlMessageDialog;

/**
 * The GalleryComm2 class implements the client side of the Gallery remote
 * protocol <a href=
 * "http://svn.sourceforge.net/viewvc/gallery/trunk/gallery/gallery_remote.php?view=markup"
 * > version 2</a>.
 * 
 * @author jackodog
 * @author paour
 * @author <a href="mailto:tim_miller@users.sourceforge.net">Tim Miller</a>
 */
public class GalleryComm2 extends GalleryComm implements GalleryComm2Consts, GalleryCommCapabilities, PreferenceNames {
	/*
	 * Implementation notes: One GalleryComm2 instance is needed per Gallery
	 * server (since the protocol only logs into each server once). So the
	 * constructor requires a Gallery instance and is immutable with respect to
	 * it.
	 */

	/*
	 * -------------------------------------------------------------------------
	 * CLASS CONSTANTS
	 */

	/** Module name for logging. */
	private static final String MODULE = "GalComm2";

	/** Remote scriptname that provides version 2 of the protocol on the server. */
	public static final String SCRIPT_NAME = "gallery_remote2.php";

	protected String scriptName = SCRIPT_NAME;

	/*
	 * -------------------------------------------------------------------------
	 * INSTANCE VARIABLES
	 */

	/** The gallery this GalleryComm2 instance is attached to. */
	protected final Gallery g;

	/**
	 * The minor revision of the server (2.x) Use this to decide whether some
	 * functionality should be disabled (because the server would not understand.
	 */
	protected int serverMinorVersion = 0;

	private static int[] capabilities1;
	private static int[] capabilities2;
	private static int[] capabilities5;
	private static int[] capabilities7;
	private static int[] capabilities9;
	private static int[] capabilities13;
	private static int[] capabilities14;
	private static int[] capabilities15;

	/*
	 * -------------------------------------------------------------------------
	 * CONSTRUCTION
	 */

	/**
	 * Create an instance of GalleryComm2 by supplying an instance of Gallery.
	 */
	protected GalleryComm2(Gallery g) {
		if (g == null) {
			throw new IllegalArgumentException("Must supply a non-null gallery.");
		}

		this.g = g;

		/*
		 * Initialize the capabilities array with what protocol 2.0 supports. Once
		 * we're logged in and we know what the minor revision of the protocol is,
		 * we'll be able to add more capabilities, such as CAPA_NEW_ALBUM (since
		 * 2.1)
		 */
		capabilities = new int[] { CAPA_UPLOAD_FILES, CAPA_FETCH_ALBUMS, CAPA_UPLOAD_CAPTION, CAPA_FETCH_HIERARCHICAL, CAPA_ALBUM_INFO };
		capabilities1 = new int[] { CAPA_UPLOAD_FILES, CAPA_FETCH_ALBUMS, CAPA_UPLOAD_CAPTION, CAPA_FETCH_HIERARCHICAL, CAPA_ALBUM_INFO,
				CAPA_NEW_ALBUM };
		capabilities2 = new int[] { CAPA_UPLOAD_FILES, CAPA_FETCH_ALBUMS, CAPA_UPLOAD_CAPTION, CAPA_FETCH_HIERARCHICAL, CAPA_ALBUM_INFO,
				CAPA_NEW_ALBUM, CAPA_FETCH_ALBUMS_PRUNE };
		capabilities5 = new int[] { CAPA_UPLOAD_FILES, CAPA_FETCH_ALBUMS, CAPA_UPLOAD_CAPTION, CAPA_FETCH_HIERARCHICAL, CAPA_ALBUM_INFO,
				CAPA_NEW_ALBUM, CAPA_FETCH_ALBUMS_PRUNE, CAPA_FORCE_FILENAME };
		capabilities7 = new int[] { CAPA_UPLOAD_FILES, CAPA_FETCH_ALBUMS, CAPA_UPLOAD_CAPTION, CAPA_FETCH_HIERARCHICAL, CAPA_ALBUM_INFO,
				CAPA_NEW_ALBUM, CAPA_FETCH_ALBUMS_PRUNE, CAPA_FORCE_FILENAME, CAPA_MOVE_ALBUM };
		capabilities9 = new int[] { CAPA_UPLOAD_FILES, CAPA_FETCH_ALBUMS, CAPA_UPLOAD_CAPTION, CAPA_FETCH_HIERARCHICAL, CAPA_ALBUM_INFO,
				CAPA_NEW_ALBUM, CAPA_FETCH_ALBUMS_PRUNE, CAPA_FORCE_FILENAME, CAPA_MOVE_ALBUM, CAPA_FETCH_ALBUM_IMAGES };
		capabilities13 = new int[] { CAPA_UPLOAD_FILES, CAPA_FETCH_ALBUMS, CAPA_UPLOAD_CAPTION, CAPA_FETCH_HIERARCHICAL, CAPA_ALBUM_INFO,
				CAPA_NEW_ALBUM, CAPA_FETCH_ALBUMS_PRUNE, CAPA_FORCE_FILENAME, CAPA_MOVE_ALBUM, CAPA_FETCH_ALBUM_IMAGES, CAPA_FETCH_ALBUMS_TOO,
				CAPA_FETCH_NON_WRITEABLE_ALBUMS };
		capabilities14 = new int[] { CAPA_UPLOAD_FILES, CAPA_FETCH_ALBUMS, CAPA_UPLOAD_CAPTION, CAPA_FETCH_HIERARCHICAL, CAPA_ALBUM_INFO,
				CAPA_NEW_ALBUM, CAPA_FETCH_ALBUMS_PRUNE, CAPA_FORCE_FILENAME, CAPA_MOVE_ALBUM, CAPA_FETCH_ALBUM_IMAGES, CAPA_FETCH_ALBUMS_TOO,
				CAPA_FETCH_NON_WRITEABLE_ALBUMS, CAPA_FETCH_HONORS_HIDDEN };
		capabilities15 = new int[] { CAPA_UPLOAD_FILES, CAPA_FETCH_ALBUMS, CAPA_UPLOAD_CAPTION, CAPA_FETCH_HIERARCHICAL, CAPA_ALBUM_INFO,
				CAPA_NEW_ALBUM, CAPA_FETCH_ALBUMS_PRUNE, CAPA_FORCE_FILENAME, CAPA_MOVE_ALBUM, CAPA_FETCH_ALBUM_IMAGES, CAPA_FETCH_ALBUMS_TOO,
				CAPA_FETCH_NON_WRITEABLE_ALBUMS, CAPA_FETCH_HONORS_HIDDEN, CAPA_IMAGE_MAX_SIZE };

		// the algorithm for search needs the ints to be sorted.
		Arrays.sort(capabilities);
		Arrays.sort(capabilities1);
		Arrays.sort(capabilities2);
		Arrays.sort(capabilities5);
		Arrays.sort(capabilities7);
		Arrays.sort(capabilities9);
		Arrays.sort(capabilities13);
		Arrays.sort(capabilities14);
		Arrays.sort(capabilities15);
	}

	/*
	 * -------------------------------------------------------------------------
	 * PUBLIC INSTANCE METHODS
	 */

	/**
	 * Causes the GalleryComm2 instance to upload the pictures in the associated
	 * Gallery to the server.
	 * 
	 * @param su
	 *           an instance that implements the StatusUpdate interface.
	 */
	@Override
	public void uploadFiles(StatusUpdate su, boolean async) {
		UploadTask uploadTask = new UploadTask(su);
		doTask(uploadTask, async);
	}

	/**
	 * Causes the GalleryComm2 instance to fetch the albums contained by
	 * associated Gallery from the server.
	 * 
	 * @param su
	 *           an instance that implements the StatusUpdate interface.
	 */
	@Override
	public void fetchAlbums(StatusUpdate su, boolean async) {
		doTask(new AlbumListTask(su), async);
	}

	/**
	 * Causes the GalleryComm2 instance to fetch the album properties for the
	 * given Album.
	 * 
	 * @param su
	 *           an instance that implements the StatusUpdate interface.
	 */
	@Override
	public void albumProperties(StatusUpdate su, Album a, boolean async) {
		doTask(new AlbumPropertiesTask(su, a), async);
	}

	/**
	 * Causes the GalleryComm instance to create a new album as a child of the
	 * specified album (or at the root if album is null)
	 */
	@Override
	public String newAlbum(StatusUpdate su, Album parentAlbum, String newAlbumName, String newAlbumTitle, String newAlbumDesc, boolean async) {
		NewAlbumTask newAlbumTask = new NewAlbumTask(su, parentAlbum, newAlbumName, newAlbumTitle, newAlbumDesc);
		doTask(newAlbumTask, async);

		return newAlbumTask.getNewAlbumName();
	}

	@Override
	public void fetchAlbumImages(StatusUpdate su, Album a, boolean recusive, boolean async, int maxPictures, boolean random) {
		FetchAlbumImagesTask fetchAlbumImagesTask = new FetchAlbumImagesTask(su, a, recusive, maxPictures, random);
		doTask(fetchAlbumImagesTask, async);
	}

	@Override
	public boolean moveAlbum(StatusUpdate su, Album a, Album newParent, boolean async) {
		MoveAlbumTask moveAlbumTask = new MoveAlbumTask(su, a, newParent);
		doTask(moveAlbumTask, async);

		return moveAlbumTask.getSuccess();
	}

	@Override
	public void login(StatusUpdate su) {
		LoginTask loginTask = new LoginTask(su);
		doTask(loginTask, false);
	}

	/*
	 * -------------------------------------------------------------------------
	 * UTILITY METHODS
	 */
	void doTask(GalleryTask task, boolean async) {
		if (async) {
			Thread t = new Thread(task);
			t.start();
		} else {
			task.run();
		}
	}

	void status(StatusUpdate su, int level, String message) {
		Log.log(Log.LEVEL_INFO, MODULE, message);
		su.updateProgressStatus(level, message);
	}

	void error(StatusUpdate su, String message) {
		status(su, StatusUpdate.LEVEL_GENERIC, message);
		su.error(message);
	}

	void trace(String message) {
		Log.log(Log.LEVEL_TRACE, MODULE, message);
	}

	/*
	 * -------------------------------------------------------------------------
	 * INNER CLASSES
	 */

	/**
	 * This class serves as the base class for each GalleryComm2 task.
	 */
	abstract class GalleryTask implements Runnable {

		StatusUpdate su;
		HTTPConnection mConnection;
		volatile boolean interrupt = false;
		volatile boolean terminated = false;
		Thread thread = null;

		public GalleryTask(StatusUpdate su) {
			if (su == null) {
				this.su = new StatusUpdateAdapter() {
				};
			} else {
				this.su = su;
			}
		}

		@Override
		public void run() {

			thread = Thread.currentThread();
			su.setInProgress(true);
			if (!isLoggedIn) {
				if (!login()) {
					Log.log(Log.LEVEL_TRACE, MODULE, "Failed to log in to " + g.toString());
					su.setInProgress(false);
					return;
				}

				isLoggedIn = true;
			} else {
				Log.log(Log.LEVEL_TRACE, MODULE, "Still logged in to " + g.toString());
			}

			runTask();

			cleanUp();
		}

		public void interrupt() {
			thread.interrupt();
			interrupt = true;
		}

		public void cleanUp() {
			su.setInProgress(false);
			terminated = true;
		}

		abstract void runTask();

		private boolean login() {
			Object[] params = { g.toString() };
			status(su, StatusUpdate.LEVEL_GENERIC, GRI18n.getString(MODULE, "logIn", params));

			if (g.getType() != Gallery.TYPE_STANDALONE && g.getType() != Gallery.TYPE_APPLET) {
				try {
					requestResponse(null, null, g.getLoginUrl(scriptName), false, su, this, null);
				} catch (IOException ioe) {
					Log.logException(Log.LEVEL_ERROR, MODULE, ioe);
					Object[] params2 = { ioe.toString() };
					error(su, GRI18n.getString(MODULE, "error", params2));
				} catch (ModuleException me) {
					Log.logException(Log.LEVEL_ERROR, MODULE, me);
					Object[] params2 = { me.getMessage() };
					error(su, GRI18n.getString(MODULE, "errReq", params2));
				}
			}

			// setup protocol parameters
			String username = g.getUsername();
			String password = g.getPassword();

			if ((username == null || username.length() == 0) && !g.cookieLogin) {
				username = (String) JOptionPane.showInputDialog(GalleryRemote.instance().getMainFrame(),
						GRI18n.getString(MODULE, "usernameLbl"), GRI18n.getString(MODULE, "username"), JOptionPane.PLAIN_MESSAGE, null, null,
						null);

				if (username != null) {
					g.setUsername(username);
				}
			}

			if (username != null && (password == null || password.length() == 0)) {
				password = (String) JOptionPane.showInputDialog(GalleryRemote.instance().getMainFrame(), GRI18n.getString(MODULE, "passwdLbl"),
						GRI18n.getString(MODULE, "passwd"), JOptionPane.PLAIN_MESSAGE, null, null, null);

				g.setPassword(password);
			}

			NVPair form_data[] = { new NVPair("cmd", "login"), new NVPair("protocol_version", PROTOCOL_VERSION), null, null };

			if (username != null) {
				form_data[2] = new NVPair("uname", username);
				Log.log(Log.LEVEL_TRACE, MODULE, "login parameters: " + Arrays.asList(form_data));
				form_data[3] = new NVPair("password", password);
			} else {
				Log.log(Log.LEVEL_TRACE, MODULE, "login parameters: " + Arrays.asList(form_data));
			}

			form_data = fudgeFormParameters(form_data);

			// make the request
			try {
				triedLogin = true;

				// load and validate the response
				Properties p = requestResponse(form_data, null, g.getGalleryUrl(scriptName), true, su, this, true, null);
				if (GR_STAT_SUCCESS.equals(p.getProperty("status")) || GR_STAT_LOGIN_MISSING.equals(p.getProperty("status"))) {
					status(su, StatusUpdate.LEVEL_GENERIC, GRI18n.getString(MODULE, "loggedIn"));
					try {
						String serverVersion = p.getProperty("server_version");
						int i = serverVersion.indexOf(".");
						serverMinorVersion = Integer.parseInt(serverVersion.substring(i + 1));

						Log.log(Log.LEVEL_TRACE, MODULE, "Server minor version: " + serverMinorVersion);

						handleCapabilities();
					} catch (Exception e) {
						Log.log(Log.LEVEL_ERROR, MODULE, "Malformed server_version: " + p.getProperty("server_version"));
						Log.logException(Log.LEVEL_ERROR, MODULE, e);
					}
					return true;
				} else if (GR_STAT_PASSWD_WRONG.equals(p.getProperty("status"))) {
					error(su, GRI18n.getString(MODULE, "usrpwdErr"));
					return false;
				} else {
					Object[] params2 = { p.getProperty("status_text") };
					error(su, GRI18n.getString(MODULE, "loginErr", params2));
					return false;
				}
			} catch (GR2Exception gr2e) {
				Log.logException(Log.LEVEL_ERROR, MODULE, gr2e);
				Object[] params2 = { gr2e.getMessage() };
				error(su, GRI18n.getString(MODULE, "error", params2));
			} catch (IOException ioe) {
				Log.logException(Log.LEVEL_ERROR, MODULE, ioe);
				Object[] params2 = { ioe.toString() };
				error(su, GRI18n.getString(MODULE, "error", params2));
			} catch (ModuleException me) {
				Log.logException(Log.LEVEL_ERROR, MODULE, me);
				Object[] params2 = { me.getMessage() };
				error(su, GRI18n.getString(MODULE, "errReq", params2));
			}

			return false;
		}
	}

	class LoginTask extends GalleryTask {
		LoginTask(StatusUpdate su) {
			super(su);
		}

		@Override
		void runTask() {
		}
	}

	/**
	 * An extension of GalleryTask to handle uploading photos.
	 */
	class UploadTask extends GalleryTask {
		MyTransferListener transferListener;

		UploadTask(StatusUpdate su) {
			super(su);
		}

		private long getTotalFileSize(ArrayList<Picture> pictureList) {
			long totalSize = 0;
			for (Picture p : pictureList) {
				totalSize += p.getFileSize();
			}
			return totalSize;
		}

		private void setCancelUploadListener(StatusUpdate su) {
			if (!(su instanceof UploadProgress)) {
				return;
			}

			final UploadProgress up = (UploadProgress) su;
			up.setCancelListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					up.updateProgressStatus(StatusUpdate.LEVEL_UPLOAD_ALL, GRI18n.getString(MODULE, "upStop"));
					up.setUndetermined(StatusUpdate.LEVEL_UPLOAD_ALL, true);

					interrupt();

					try {
						thread.join(60000L);
					} catch (InterruptedException ex) {
						// do nothing
					}

					if (!terminated) {
						Log.log(Log.LEVEL_ERROR, "Thread would not terminate after 60 seconds.");
						// since we killed the thread, it's not going to
						// clean up after itself
						cleanUp();
					}

					up.done();
				}
			});
		}

		@Override
		void runTask() {
			ArrayList<Picture> pictures = g.getAllUploadablePictures();

			transferListener = new MyTransferListener(su);
			transferListener.sizeAllFiles = getTotalFileSize(pictures);
			transferListener.numberAllFiles = pictures.size();

			su.startProgress(StatusUpdate.LEVEL_UPLOAD_ALL, 0, 100, GRI18n.getString(MODULE, "upPic"), false);

			setCancelUploadListener(su);

			// upload each file, one at a time
			boolean allGood = true;
			Iterator<Picture> iter = pictures.iterator();

			while (iter.hasNext() && !interrupt) {
				Picture p = iter.next();
				allGood = uploadPicture(p, p);
				if (allGood) {
					p.getParentAlbum().removePicture(p);
				}
			}

			if (allGood) {
				su.stopProgress(StatusUpdate.LEVEL_UPLOAD_ALL, GRI18n.getString(MODULE, "upComplete"));

				if (su instanceof UploadProgress) {
					if (((UploadProgress) su).isShutdown()) {
						GalleryRemote.instance().getCore().shutdown(true);
					}
				}

				g.setDirty(false);

				GalleryRemote.instance().getCore().flushMemory();
			} else {
				su.stopProgress(StatusUpdate.LEVEL_UPLOAD_ALL, GRI18n.getString(MODULE, "upFailed"));
			}
		}

		boolean uploadPicture(Picture p, Picture picture) {
			try {
				boolean utf8 = p.getParentAlbum().getGallery().getGalleryVersion() == 2;
				boolean escapeCaptions = !utf8 && GalleryRemote.instance().properties.getBooleanProperty(HTML_ESCAPE_CAPTIONS);
				transferListener.currentFile = p.toString();

				if (utf8) {
					Log.log(Log.LEVEL_INFO, MODULE, "Will upload using UTF-8 for text data");
				}

				status(su, StatusUpdate.LEVEL_UPLOAD_ONE, GRI18n.getString(MODULE, "upPrep"));

				// can't set null as an NVPair value
				String caption = p.getCaption();
				caption = (caption == null) ? "" : caption;

				if (escapeCaptions) {
					caption = HTMLEscaper.escape(caption);
				}

				// setup the protocol parameters
				NVPair[] opts = { new NVPair("cmd", "add-item"), new NVPair("protocol_version", PROTOCOL_VERSION),
						new NVPair("set_albumName", p.getParentAlbum().getName()), new NVPair("caption", caption, utf8 ? "UTF-8" : null),
						new NVPair("force_filename", p.getSource().getName()), null };

				// set auto-rotate only if we do the rotation in GR, otherwise
				// we'd be overriding the server setting
				if (p.getAngle() != 0) {
					opts[5] = new NVPair("auto_rotate", "no");
				}

				// set up extra fields
				if (p.getExtraFieldsMap() != null && p.getExtraFieldsMap().size() > 0) {
					ArrayList<NVPair> optsList = new ArrayList<NVPair>(Arrays.asList(opts));

					Iterator<String> it = p.getExtraFieldsMap().keySet().iterator();
					while (it.hasNext()) {
						String name = it.next();
						String value = p.getExtraField(name);

						if (value != null) {
							if (escapeCaptions) {
								value = HTMLEscaper.escape(value);
							}

							optsList.add(new NVPair("extrafield." + name, value, utf8 ? "UTF-8" : null));
						}
					}

					opts = optsList.toArray(opts);
				}

				Log.log(Log.LEVEL_TRACE, MODULE, "add-item parameters: " + Arrays.asList(opts));

				// setup the multipart form data
				NVPair[] afile = { new NVPair("userfile", p.getUploadSource().getAbsolutePath()) };
				NVPair[] hdrs = new NVPair[1];
				byte[] data = Codecs.mpFormDataEncode(fudgeFormParameters(opts), fudgeParameters(afile), hdrs);

				// load and validate the response
				Properties props = requestResponse(hdrs, data, g.getGalleryUrl(scriptName), true, su, this, transferListener);
				if (props.getProperty("status").equals(GR_STAT_SUCCESS)) {
					status(su, StatusUpdate.LEVEL_UPLOAD_ONE, GRI18n.getString(MODULE, "upSucc"));
					String newItemName = props.getProperty("item_name");
					if (newItemName != null) {
						su.doneUploading(newItemName, picture);
					}
					return true;
				}
				Object[] params = { props.getProperty("status_text") };
				error(su, GRI18n.getString(MODULE, "upErr", params));
				return false;
			} catch (GR2Exception gr2e) {
				Log.logException(Log.LEVEL_ERROR, MODULE, gr2e);
				Object[] params = { gr2e.getMessage() };
				error(su, p.toString() + ": " + GRI18n.getString(MODULE, "error", params));
			} catch (SocketException swe) {
				Log.logException(Log.LEVEL_ERROR, MODULE, swe);
				Object[] params = { swe.toString() };
				error(su, p.toString() + ": " + GRI18n.getString(MODULE, "confErr", params));
			} catch (IOException ioe) {
				Log.logException(Log.LEVEL_ERROR, MODULE, ioe);
				Object[] params = { ioe.toString() };
				error(su, p.toString() + ": " + GRI18n.getString(MODULE, "error", params));
			} catch (ModuleException me) {
				Log.logException(Log.LEVEL_ERROR, MODULE, me);
				Object[] params = { me.getMessage() };
				error(su, p.toString() + ": " + GRI18n.getString(MODULE, "errReq", params));
			}

			return false;
		}
	}

	/**
	 * An extension of GalleryTask to handle fetching albums.
	 */
	class AlbumListTask extends GalleryTask {

		AlbumListTask(StatusUpdate su) {
			super(su);
		}

		@Override
		void runTask() {
			su.startProgress(StatusUpdate.LEVEL_BACKGROUND, 0, 10, GRI18n.getString(MODULE, "albmFtchng", new Object[] { g.toString() }), true);

			try {
				long startTime = System.currentTimeMillis();

				if (serverMinorVersion < 2) {
					list20();
				} else {
					list22();
				}

				// tell the tree to reload
				g.reload();

				Log.log(Log.LEVEL_INFO, MODULE, "execution time for AlbumList: " + (System.currentTimeMillis() - startTime));
			} catch (GR2Exception gr2e) {
				Log.logException(Log.LEVEL_ERROR, MODULE, gr2e);
				Object[] params2 = { gr2e.getMessage() };
				error(su, GRI18n.getString(MODULE, "error", params2));
			} catch (IOException ioe) {
				Log.logException(Log.LEVEL_ERROR, MODULE, ioe);
				Object[] params2 = { ioe.toString() };
				error(su, GRI18n.getString(MODULE, "error", params2));
			} catch (ModuleException me) {
				Log.logException(Log.LEVEL_ERROR, MODULE, me);
				Object[] params2 = { me.toString() };
				error(su, GRI18n.getString(MODULE, "error", params2));
			}

			su.stopProgress(StatusUpdate.LEVEL_BACKGROUND, GRI18n.getString(MODULE, "fetchComplete"));
		}

		private void list20() throws IOException, ModuleException {
			// setup the protocol parameters
			NVPair form_data[] = { new NVPair("cmd", "fetch-albums"), new NVPair("protocol_version", PROTOCOL_VERSION) };
			Log.log(Log.LEVEL_TRACE, MODULE, "fetchAlbums parameters: " + Arrays.asList(form_data));

			form_data = fudgeFormParameters(form_data);

			// load and validate the response
			Properties p = requestResponse(form_data, su, this);
			if (p.getProperty("status").equals(GR_STAT_SUCCESS)) {
				ArrayList<Album> mAlbumList = new ArrayList<Album>();

				// parse and store the data
				int albumCount = Integer.parseInt(p.getProperty("album_count"));

				HashMap<String, String> ref2parKey = new HashMap<String, String>();
				HashMap<String, Album> ref2album = new HashMap<String, Album>();

				Album rootAlbum = g.createRootAlbum();

				for (int i = 1; i < albumCount + 1; i++) {
					Album a = new Album(g);
					a.setSuppressEvents(true);

					String nameKey = "album.name." + i;
					String titleKey = "album.title." + i;
					String parentKey = "album.parent." + i;
					String permsAddKey = "album.perms.add." + i;
					String permsWriteKey = "album.perms.write." + i;
					String permsDelItemKey = "album.perms.del_item." + i;
					String permsDelAlbKey = "album.perms.del_alb." + i;
					String permsCreateSubKey = "album.perms.create_sub." + i;
					String infoExtraFieldsKey = "album.info.extrafields." + i;

					a.setCanAdd(isTrue(p.getProperty(permsAddKey)));
					a.setCanWrite(isTrue(p.getProperty(permsWriteKey)));
					a.setCanDeleteFrom(isTrue(p.getProperty(permsDelItemKey)));
					a.setCanDeleteThisAlbum(isTrue(p.getProperty(permsDelAlbKey)));
					a.setCanCreateSubAlbum(isTrue(p.getProperty(permsCreateSubKey)));
					a.setExtraFieldsString(HTMLEscaper.unescape(p.getProperty(infoExtraFieldsKey)));

					a.setName(p.getProperty(nameKey));
					a.setTitle(HTMLEscaper.unescape(p.getProperty(titleKey)));

					a.setSuppressEvents(false);
					mAlbumList.add(a);

					// map album ref nums to albums
					ref2album.put("" + i, a);

					// map album refs to parent refs
					String parentRefS = p.getProperty(parentKey);
					int parentRef = Integer.parseInt(parentRefS);
					if (parentRef != 0) {
						ref2parKey.put("" + i, parentRefS);
					} else {
						rootAlbum.add(a);
					}
				}

				// link albums to parents
				for (int i = 1; i < albumCount + 1; i++) {
					String parentKey = ref2parKey.get("" + i);
					if (parentKey != null) {
						Album a = ref2album.get("" + i);
						if (a != null) {
							Album pa = ref2album.get(parentKey);
							pa.add(a);
						}
					}
				}

				status(su, StatusUpdate.LEVEL_BACKGROUND, GRI18n.getString(MODULE, "ftchdAlbms"));

				// g.setAlbumList(mAlbumList);
			} else {
				Object[] params = { p.getProperty("status_text") };
				error(su, GRI18n.getString(MODULE, "error", params));
			}
		}

		private void list22() throws IOException, ModuleException {
			// setup the protocol parameters
			NVPair form_data[] = { new NVPair("cmd", "fetch-albums-prune"), new NVPair("protocol_version", PROTOCOL_VERSION) };
			Log.log(Log.LEVEL_TRACE, MODULE, "fetchAlbums parameters: " + Arrays.asList(form_data));

			form_data = fudgeFormParameters(form_data);

			// load and validate the response
			GalleryProperties p = requestResponse(form_data, su, this);
			if (p.getProperty("status").equals(GR_STAT_SUCCESS)) {
				ArrayList<Album> albums = new ArrayList<Album>();

				// parse and store the data
				int albumCount = Integer.parseInt(p.getProperty("album_count"));
				// System.err.println( "### albumCount = " + albumCount );
				HashMap<String, String> name2parentName = new HashMap<String, String>();
				HashMap<String, Album> name2album = new HashMap<String, Album>();

				Album rootAlbum = g.createRootAlbum();

				for (int i = 1; i < albumCount + 1; i++) {
					Album a = new Album(g);
					a.setSuppressEvents(true);

					String nameKey = "album.name." + i;
					String titleKey = "album.title." + i;
					String parentKey = "album.parent." + i;
					String permsAddKey = "album.perms.add." + i;
					String permsWriteKey = "album.perms.write." + i;
					String permsDelItemKey = "album.perms.del_item." + i;
					String permsDelAlbKey = "album.perms.del_alb." + i;
					String permsCreateSubKey = "album.perms.create_sub." + i;
					String infoExtraFieldKey = "album.info.extrafields." + i;

					a.setCanAdd(isTrue(p.getProperty(permsAddKey)));
					a.setCanWrite(isTrue(p.getProperty(permsWriteKey)));
					a.setCanDeleteFrom(isTrue(p.getProperty(permsDelItemKey)));
					a.setCanDeleteThisAlbum(isTrue(p.getProperty(permsDelAlbKey)));
					a.setCanCreateSubAlbum(isTrue(p.getProperty(permsCreateSubKey)));

					String name = p.getProperty(nameKey);
					a.setName(name);
					a.setTitle(HTMLEscaper.unescape(p.getProperty(titleKey)));
					a.setExtraFieldsString(HTMLEscaper.unescape(p.getProperty(infoExtraFieldKey)));

					a.setSuppressEvents(false);

					albums.add(a);

					// map album names to albums
					name2album.put(name, a);

					// map album refs to parent refs
					String parentName = p.getProperty(parentKey);

					if (parentName == null) {
						Log.log(Log.LEVEL_ERROR, MODULE, "Gallery error: the album " + name
								+ " doesn't have a parent. You should delete it, the album database " + "is corrupted because of it.");

					} else if (parentName.equals(name)) {
						Log.log(Log.LEVEL_ERROR, MODULE, "Gallery error: the album " + name
								+ " is its own parent. You should delete it, the album database " + "is corrupted because of it.");

						parentName = null;
					}

					if (parentName != null && parentName.length() > 0 && !parentName.equals("0")) {
						name2parentName.put(name, parentName);
					} else {
						rootAlbum.add(a);
					}
				}

				Log.log(Log.LEVEL_TRACE, MODULE, "Created " + albums.size() + " albums");

				// link albums to parents
				Iterator<String> itp = name2parentName.keySet().iterator();
				while (itp.hasNext()) {
					String name = itp.next();
					String parentName = name2parentName.get(name);
					Album child = name2album.get(name);
					Album parent = name2album.get(parentName);

					if (child != null && parent != null) {
						parent.add(child);
					}
				}

				Log.log(Log.LEVEL_TRACE, MODULE, "Linked " + name2parentName.size() + " albums to their parents");

				// reorder
				Collections.sort(albums, new NaturalOrderComparator<Album>());
				Collections.reverse(albums);
				ArrayList<Album> orderedAlbums = new ArrayList<Album>();
				int depth = 0;
				while (!albums.isEmpty()) {
					Iterator<Album> ita = albums.iterator();
					while (ita.hasNext()) {
						Album a = ita.next();

						try {
							if (a.getAlbumDepth() == depth) {
								ita.remove();
								a.sortSubAlbums();

								Album parentAlbum = a.getParentAlbum();
								if (parentAlbum == null) {
									orderedAlbums.add(0, a);
								} else {
									int i = orderedAlbums.indexOf(parentAlbum);
									orderedAlbums.add(i + 1, a);
								}
							}
						} catch (IllegalArgumentException e) {
							ita.remove();
							Log.log(Log.LEVEL_TRACE, MODULE, "Gallery server album list is corrupted: " + "album " + a.getName()
									+ " has a bad containment hierarchy.");

							if (!GalleryRemote.instance().properties.getBooleanProperty(SUPPRESS_WARNING_CORRUPTED)) {
								UrlMessageDialog md = new UrlMessageDialog(GRI18n.getString(MODULE, "fixCorrupted", new String[] { a.getTitle(),
										a.getGallery().getGalleryUrl(a.getName()).toString() }), a.getGallery()
										.getGalleryUrl(GRI18n.getString(MODULE, "fixCorruptedUrl", new String[] { a.getName() })).toString(), null);

								if (md.dontShow()) {
									GalleryRemote.instance().properties.setBooleanProperty(SUPPRESS_WARNING_CORRUPTED, true);
								}
							}

							// This doesn't work: there's a problem with the
							// connection (maybe not re-entrant...)
							// if (answer == JOptionPane.YES_OPTION) {
							// a.moveAlbumTo(su, null);
							// moveAlbum(su, a, null, true);
							// }
						}
					}

					depth++;
				}

				rootAlbum.setCanCreateSubAlbum(p.getBooleanProperty("can_create_root", false));

				Log.log(Log.LEVEL_TRACE, MODULE, "Ordered " + orderedAlbums.size() + " albums");

				status(su, StatusUpdate.LEVEL_BACKGROUND, GRI18n.getString(MODULE, "ftchdAlbms"));

				// g.setAlbumList(orderedAlbums);
			} else {
				Object[] params = { p.getProperty("status_text") };
				error(su, GRI18n.getString(MODULE, "error", params));
			}
		}
	}

	/**
	 * An extension of GalleryTask to handle getting album information.
	 */
	class AlbumPropertiesTask extends GalleryTask {
		Album a;

		AlbumPropertiesTask(StatusUpdate su, Album a) {
			super(su);
			this.a = a;
		}

		@Override
		void runTask() {
			status(su, StatusUpdate.LEVEL_GENERIC, GRI18n.getString(MODULE, "getAlbmInfo", new String[] { a.toString() }));

			try {
				// setup the protocol parameters
				NVPair form_data[] = { new NVPair("cmd", "album-properties"), new NVPair("protocol_version", PROTOCOL_VERSION),
						new NVPair("set_albumName", a.getName()) };
				Log.log(Log.LEVEL_TRACE, MODULE, "album-info parameters: " + Arrays.asList(form_data));

				form_data = fudgeFormParameters(form_data);

				// load and validate the response
				GalleryProperties p = requestResponse(form_data, su, this);
				if (p.getProperty("status").equals(GR_STAT_SUCCESS)) {
					// parse and store the data
					int autoResize = p.getIntProperty("auto_resize");
					int maxSize = p.getIntProperty("max_size", 0);

					// use larger of intermediate and max size
					a.setServerAutoResize(autoResize > maxSize ? autoResize : maxSize);

					String extrafields = p.getProperty("extrafields");
					a.setExtraFieldsString(extrafields);

					status(su, StatusUpdate.LEVEL_GENERIC, GRI18n.getString(MODULE, "ftchdAlbmProp"));

				} else {
					error(su, "Error: " + p.getProperty("status_text"));
				}

			} catch (GR2Exception gr2e) {
				Log.logException(Log.LEVEL_ERROR, MODULE, gr2e);
				Object[] params2 = { gr2e.getMessage() };
				error(su, GRI18n.getString(MODULE, "error", params2));
			} catch (IOException ioe) {
				Log.logException(Log.LEVEL_ERROR, MODULE, ioe);
				Object[] params2 = { ioe.toString() };
				error(su, GRI18n.getString(MODULE, "error", params2));
			} catch (ModuleException me) {
				Log.logException(Log.LEVEL_ERROR, MODULE, me);
				Object[] params2 = { me.toString() };
				error(su, GRI18n.getString(MODULE, "error", params2));
			}
		}
	}

	/**
	 * An extension of GalleryTask to handle creating a new album.
	 */
	class NewAlbumTask extends GalleryTask {
		Album parentAlbum;
		String albumName;
		String albumTitle;
		String albumDesc;
		private String newAlbumName;

		NewAlbumTask(StatusUpdate su, Album parentAlbum, String albumName, String albumTitle, String albumDesc) {
			super(su);
			this.parentAlbum = parentAlbum;
			this.albumName = albumName;
			this.albumTitle = albumTitle;
			this.albumDesc = albumDesc;
		}

		@Override
		void runTask() {
			status(su, StatusUpdate.LEVEL_GENERIC, GRI18n.getString(MODULE, "newAlbm", new Object[] { albumName, g.toString() }));

			boolean escapeCaptions = GalleryRemote.instance().properties.getBooleanProperty(HTML_ESCAPE_CAPTIONS);
			boolean utf8 = !escapeCaptions && parentAlbum.getGallery().getGalleryVersion() == 2;

			if (utf8) {
				Log.log(Log.LEVEL_INFO, MODULE, "Will upload using UTF-8 for text data");
			}

			// if the parent is null (top-level album), set the album name to an
			// illegal name so it's set to null
			// by Gallery. Using an empty string doesn't work, because then the
			// HTTP parameter is not
			// parsed, and the session album is kept the same as before (from
			// the cookie).
			String parentAlbumName = (parentAlbum == null) ? "hack_null_albumName" : parentAlbum.getName();

			if (escapeCaptions) {
				albumTitle = HTMLEscaper.escape(albumTitle);
				albumDesc = HTMLEscaper.escape(albumDesc);
			}

			try {
				// setup the protocol parameters
				NVPair form_data[] = { new NVPair("cmd", "new-album"), new NVPair("protocol_version", PROTOCOL_VERSION),
						new NVPair("set_albumName", parentAlbumName), new NVPair("newAlbumName", albumName),
						new NVPair("newAlbumTitle", albumTitle, utf8 ? "UTF-8" : null),
						new NVPair("newAlbumDesc", albumDesc, utf8 ? "UTF-8" : null) };

				form_data = fudgeFormParameters(form_data);

				Log.log(Log.LEVEL_TRACE, MODULE, "new-album parameters: " + Arrays.asList(form_data));

				Properties p;
				if (utf8) {
					// force using mime-multipart so we can use UTF-8
					NVPair[] hdrs = new NVPair[1];
					byte[] data = Codecs.mpFormDataEncode(form_data, new NVPair[0], hdrs);

					// load and validate the response
					p = requestResponse(hdrs, data, g.getGalleryUrl(scriptName), true, su, this, null);
				} else {
					// normal request
					p = requestResponse(form_data, su, this);
				}

				if (p.getProperty("status").equals(GR_STAT_SUCCESS)) {
					status(su, StatusUpdate.LEVEL_GENERIC, GRI18n.getString(MODULE, "crateAlbmOk"));
					newAlbumName = p.getProperty("album_name");
				} else {
					Object[] params2 = { p.getProperty("status_text") };
					error(su, GRI18n.getString(MODULE, "error", params2));
				}

			} catch (GR2Exception gr2e) {
				Log.logException(Log.LEVEL_ERROR, MODULE, gr2e);
				Object[] params2 = { gr2e.getMessage() };
				error(su, GRI18n.getString(MODULE, "error", params2));
			} catch (IOException ioe) {
				Log.logException(Log.LEVEL_ERROR, MODULE, ioe);
				Object[] params2 = { ioe.toString() };
				error(su, GRI18n.getString(MODULE, "error", params2));
			} catch (ModuleException me) {
				Log.logException(Log.LEVEL_ERROR, MODULE, me);
				Object[] params2 = { me.toString() };
				error(su, GRI18n.getString(MODULE, "error", params2));
			}
		}

		public String getNewAlbumName() {
			return newAlbumName;
		}
	}

	/**
	 * An extension of GalleryTask to handle getting album information.
	 */
	class FetchAlbumImagesTask extends GalleryTask {
		Album a;
		boolean recursive = false;
		int maxPictures = 0;
		boolean random = false;

		FetchAlbumImagesTask(StatusUpdate su, Album a, boolean recursive, int maxPictures, boolean random) {
			super(su);

			this.a = a;
			this.recursive = recursive;
			this.maxPictures = maxPictures;
			this.random = random;
		}

		@Override
		void runTask() {
			su.startProgress(StatusUpdate.LEVEL_GENERIC, 0, 10, GRI18n.getString(MODULE, "fetchAlbImages", new String[] { a.getName() }), true);

			try {
				ArrayList<Picture> newPictures = new ArrayList<Picture>();
				fetch(a, a.getName(), newPictures);

				a.setHasFetchedImages(true);
				boolean isDirty = a.getGallery().isDirty();
				a.addPictures(newPictures);
				a.getGallery().setDirty(isDirty);
				GalleryRemote.instance().getCore().preloadThumbnails(newPictures.iterator());

				su.stopProgress(StatusUpdate.LEVEL_GENERIC,
						GRI18n.getString(MODULE, "fetchAlbImagesDone", new String[] { "" + newPictures.size() }));
			} catch (GR2Exception e) {
				error(su, GRI18n.getString(MODULE, "error", new String[] { e.getMessage() }));
				su.stopProgress(StatusUpdate.LEVEL_GENERIC, e.getMessage());
			}

		}

		/**
		 * @throws GR2Exception
		 */
		private void fetch(Album a, String albumName, ArrayList<Picture> newPictures) throws GR2Exception {
			su.updateProgressStatus(StatusUpdate.LEVEL_GENERIC, GRI18n.getString(MODULE, "fetchAlbImages", new String[] { albumName }));

			try {
				// setup the protocol parameters
				NVPair[] form_data = new NVPair[] { new NVPair("cmd", "fetch-album-images"), new NVPair("protocol_version", PROTOCOL_VERSION),
						new NVPair("set_albumName", albumName), new NVPair("albums_too", recursive ? "yes" : "no"),
						new NVPair("random", random ? "yes" : "no"), new NVPair("limit", maxPictures + ""), new NVPair("extrafields", "yes") };

				Log.log(Log.LEVEL_TRACE, MODULE, "fetch-album-images parameters: " + Arrays.asList(form_data));

				form_data = fudgeFormParameters(form_data);

				// load and validate the response
				GalleryProperties p = requestResponse(form_data, su, this);
				if (!(p.getProperty("status").equals(GR_STAT_SUCCESS))) {
					throw new GR2Exception(p.getProperty("error"));
				}

				// parse and store the data
				int numImages = p.getIntProperty("image_count");
				String baseUrl = p.getProperty("baseurl");

				try {
					if (baseUrl == null) {
						Log.log(Log.LEVEL_TRACE, MODULE, "Gallery root, baseurl is null");
					} else {
						// verify that baseUrl is a valid URL (don't remove)
						new URL(baseUrl);
					}
				} catch (MalformedURLException e) {
					Log.log(Log.LEVEL_TRACE, MODULE, "baseurl is relative, tacking on Gallery URL (only works for standalone)");
					URL tmpUrl = new URL(g.getStUrlString());
					baseUrl = new URL(tmpUrl.getProtocol(), tmpUrl.getHost(), tmpUrl.getPort(), baseUrl).toString();
				}

				String caption = p.getProperty("album.caption");
				if (caption != null) {
					a.setCaption(caption);
				}

				String extraFieldsString = p.getProperty("album.extrafields");
				if (extraFieldsString != null) {
					a.setExtraFieldsString(extraFieldsString);
				}

				int width;
				int height;
				ArrayList<String> extraFields = a.getExtraFields();
				for (int i = 1; i <= numImages; i++) {
					if (maxPictures > 0 && newPictures.size() >= maxPictures) {
						Log.log(Log.LEVEL_TRACE, MODULE, "Fetched maximum of " + maxPictures + " pictures: stopping.");
						break;
					}

					String subAlbumName = p.getProperty("album.name." + i);
					boolean subAlbumHidden = p.getBooleanProperty("album.hidden." + i, false);

					if (subAlbumName != null) {
						if (!subAlbumHidden) {
							fetch(a, subAlbumName, newPictures);
						}
					} else {
						Picture picture = new Picture(g);
						picture.setOnline(true);

						String rawName = p.getProperty("image.name." + i);
						if (rawName != null) {
							picture.setUrlFull(new URL(baseUrl + rawName));
							width = p.getIntProperty("image.raw_width." + i, 0);
							height = p.getIntProperty("image.raw_height." + i, 0);
							picture.setSizeFull(new Dimension(width, height));
							picture.setFileSize(p.getIntProperty("image.raw_filesize." + i));

							picture.setUniqueId(a.getName() + '_' + rawName);
							picture.setItemId(rawName);
						}

						String forceExtension = p.getProperty("image.forceExtension." + i);
						if (forceExtension != null) {
							picture.setForceExtension(forceExtension);
						}

						String resizedName = p.getProperty("image.resizedName." + i);
						if (resizedName != null) {
							picture.setUrlResized(new URL(baseUrl + resizedName));
							width = p.getIntProperty("image.resized_width." + i);
							height = p.getIntProperty("image.resized_height." + i);
							picture.setSizeResized(new Dimension(width, height));
						}

						picture.setUrlThumbnail(new URL(baseUrl + p.getProperty("image.thumbName." + i)));
						width = p.getIntProperty("image.thumb_width." + i);
						height = p.getIntProperty("image.thumb_height." + i);
						picture.setSizeThumbnail(new Dimension(width, height));

						int resizedNum = p.getIntProperty("image.resizedNum." + i, 0);
						for (int j = 1; j <= resizedNum; j++) {
							resizedName = p.getProperty("image.resized." + j + ".name." + i);
							width = p.getIntProperty("image.resized." + j + ".width." + i);
							height = p.getIntProperty("image.resized." + j + ".height." + i);
							picture.addResizedDerivative(new URL(baseUrl + resizedName), new Dimension(width, height));
						}

						picture.setCaption(p.getProperty("image.caption." + i));

						String title = p.getProperty("image.title." + i);
						if (title != null) {
							picture.setName(title);
						}

						if (extraFields != null) {
							for (Iterator<String> it = extraFields.iterator(); it.hasNext();) {
								String name = it.next();
								String value = p.getProperty("image.extrafield." + name + "." + i);

								if (value != null) {
									picture.setExtraField(name, value);
								}
							}
						}

						picture.setHidden(p.getBooleanProperty("image.hidden." + i, false));

						picture.setAlbumOnServer(a);
						picture.setIndexOnServer(i - 1);

						if (!picture.isHidden() && !(rawName == null && resizedName == null)) {
							// don't add the picture if the current user
							// can't get access to it
							newPictures.add(picture);
						}
					}
				}

			} catch (IOException ioe) {
				Log.logException(Log.LEVEL_ERROR, MODULE, ioe);
				Object[] params2 = { ioe.toString() };
				error(su, GRI18n.getString(MODULE, "error", params2));
			} catch (ModuleException me) {
				Log.logException(Log.LEVEL_ERROR, MODULE, me);
				Object[] params2 = { me.toString() };
				error(su, GRI18n.getString(MODULE, "error", params2));
			}
		}
	}

	/**
	 * An extension of GalleryTask to handle moving an album.
	 */
	class MoveAlbumTask extends GalleryTask {
		Album a;
		Album newParent;
		boolean success = false;

		MoveAlbumTask(StatusUpdate su, Album a, Album newParent) {
			super(su);
			this.a = a;
			this.newParent = newParent;
		}

		@Override
		void runTask() {
			String newParentName;
			String destAlbumName;

			if (newParent != null) {
				newParentName = destAlbumName = newParent.getName();
			} else {
				newParentName = GRI18n.getString(MODULE, "rootAlbum");
				destAlbumName = "0";
			}

			status(su, StatusUpdate.LEVEL_GENERIC, GRI18n.getString(MODULE, "moveAlbum", new String[] { a.getName(), newParentName }));

			try {
				// setup the protocol parameters
				NVPair form_data[] = { new NVPair("cmd", "move-album"), new NVPair("protocol_version", PROTOCOL_VERSION),
						new NVPair("set_albumName", a.getName()), new NVPair("set_destalbumName", destAlbumName) };
				Log.log(Log.LEVEL_TRACE, MODULE, "move-album parameters: " + Arrays.asList(form_data));

				form_data = fudgeFormParameters(form_data);

				// load and validate the response
				GalleryProperties p = requestResponse(form_data, su, this);
				if (p.getProperty("status").equals(GR_STAT_SUCCESS)) {
					status(su, StatusUpdate.LEVEL_GENERIC, GRI18n.getString(MODULE, "moveAlbumDone"));

					success = true;
				} else {
					error(su, "Error: " + p.getProperty("status_text"));
				}

			} catch (GR2Exception gr2e) {
				Log.logException(Log.LEVEL_ERROR, MODULE, gr2e);
				Object[] params2 = { gr2e.getMessage() };
				error(su, GRI18n.getString(MODULE, "error", params2));
			} catch (IOException ioe) {
				Log.logException(Log.LEVEL_ERROR, MODULE, ioe);
				Object[] params2 = { ioe.toString() };
				error(su, GRI18n.getString(MODULE, "error", params2));
			} catch (ModuleException me) {
				Log.logException(Log.LEVEL_ERROR, MODULE, me);
				Object[] params2 = { me.toString() };
				error(su, GRI18n.getString(MODULE, "error", params2));
			}
		}

		public boolean getSuccess() {
			return success;
		}
	}

	boolean isTrue(String s) {
		return s != null && s.equals("true");
	}

	/**
	 * POSTSs a request to the Gallery server with the given form data.
	 */
	GalleryProperties requestResponse(NVPair form_data[], StatusUpdate su, GalleryTask task) throws ModuleException, IOException {
		return requestResponse(form_data, null, g.getGalleryUrl(scriptName), true, su, task, null);
	}

	GalleryProperties requestResponse(NVPair form_data[], URL galUrl, StatusUpdate su, GalleryTask task) throws ModuleException, IOException {
		return requestResponse(form_data, null, galUrl, true, su, task, null);
	}

	GalleryProperties requestResponse(NVPair form_data[], byte[] data, URL galUrl, boolean checkResult, StatusUpdate su, GalleryTask task,
			MyTransferListener transferListener) throws ModuleException, IOException {
		return requestResponse(form_data, data, galUrl, checkResult, su, task, false, transferListener);
	}

	/**
	 * POSTs a request to the Gallery server with the given form data. If data is
	 * not null, a multipart MIME post is performed.
	 */
	GalleryProperties requestResponse(NVPair form_data[], byte[] data, URL galUrl, boolean checkResult, StatusUpdate su,
			GalleryComm2.GalleryTask task, boolean alreadyRetried, MyTransferListener transferListener) throws ModuleException, IOException {
		// assemble the URL
		String urlPath = galUrl.getFile();
		Log.log(Log.LEVEL_TRACE, MODULE, "Connecting to: " + galUrl);
		Log.log(Log.LEVEL_TRACE, MODULE, "Path: " + urlPath);

		if (data != null) {
			su.startProgress(StatusUpdate.LEVEL_UPLOAD_ONE, 0, 0, GRI18n.getString(MODULE, "upStart"), false);
		}

		// create a connection
		HTTPConnection mConnection = new HTTPConnection(galUrl);

		// set the user-agent for all requests
		// also try to disable pipelining, to help with uploading large numbers
		// of files
		ArrayList<NVPair> nvPairs = new ArrayList<NVPair>();

		nvPairs.add(new NVPair("Connection", "close"));

		// If Gallery specified the user agent, then we'll use that. If not,
		// we'll let
		// HTTPClient choose a default agent.
		String userAgent = g.getUserAgent();
		if (userAgent != null) {
			nvPairs.add(new NVPair("User-Agent", userAgent));
		}

		mConnection.setDefaultHeaders(nvPairs.toArray(new NVPair[nvPairs.size()]));
		Log.log(Log.LEVEL_TRACE, MODULE, "Extra headers: " + nvPairs);

		if (g.getForceProtocolEncoding() != null) {
			mConnection.setForceCharset(g.getForceProtocolEncoding());
		}

		// Markus Cozowicz (mc@austrian-mint.at) 2003/08/24
		HTTPResponse rsp;

		// post multipart if there is data
		if (data == null) {
			if (form_data == null) {
				rsp = mConnection.Get(urlPath);
			} else {
				rsp = mConnection.Post(urlPath, form_data);
			}
		} else {
			rsp = mConnection.Post(urlPath, data, form_data, transferListener);
		}

		// Log.log(Log.LEVEL_TRACE, MODULE, "Request body: " + new
		// String(rsp.request.getData()));

		// handle 30x redirects
		if (rsp.getStatusCode() >= 300 && rsp.getStatusCode() < 400) {
			// retry, the library will have fixed the URL
			status(su, StatusUpdate.LEVEL_UPLOAD_ONE, GRI18n.getString(MODULE, "redirect"));
			if (data == null) {
				if (form_data == null) {
					rsp = mConnection.Get(urlPath);
				} else {
					rsp = mConnection.Post(urlPath, form_data);
				}
			} else {
				rsp = mConnection.Post(urlPath, data, form_data, transferListener);
			}
		}

		// handle response
		if (rsp.getStatusCode() >= 300) {
			Object[] params = { new Integer(rsp.getStatusCode()), rsp.getReasonLine() };
			throw new GR2Exception(GRI18n.getString(MODULE, "httpPostErr", params));
		}
		Log.log(Log.LEVEL_TRACE, MODULE, "Content-type: " + rsp.getHeader("Content-type"));

		// load response
		String response;
		try {
			response = rsp.getText().trim();
		} catch (ParseException e) {
			Log.log(Log.LEVEL_ERROR, MODULE, "HTTPClient failed to parse response, getting data instead of text");
			response = new String(rsp.getData()).trim();
		}
		Log.log(Log.LEVEL_TRACE, MODULE, response);

		if (checkResult) {
			// validate response
			int i = response.indexOf(PROTOCOL_MAGIC);

			if (i == -1) {
				if (alreadyRetried) {
					// failed one time too many
					Object[] params = { galUrl.toString() };
					throw new GR2Exception(GRI18n.getString(MODULE, "gllryNotFound", params));
				}
				// try again
				Log.log(Log.LEVEL_INFO, MODULE, "Request failed the first time: trying again...");
				return requestResponse(form_data, data, galUrl, checkResult, su, task, true, transferListener);
			} else if (i > 0) {
				response = response.substring(i);
				Log.log(Log.LEVEL_TRACE, MODULE, "Short response: " + response);
			}

			GalleryProperties p = new GalleryProperties();
			p.load(response);

			// catch session expiration problems
			if (!alreadyRetried && !g.cookieLogin && g.getUsername() != null && g.getUsername().length() != 0
					&& p.getProperty("debug_user_already_logged_in") != null && !"1".equals(p.getProperty("debug_user_already_logged_in"))) {
				Log.log(Log.LEVEL_INFO, MODULE, "The session seems to have expired: trying to login and retry...");

				if (task.login()) {
					return requestResponse(form_data, data, galUrl, checkResult, su, task, true, transferListener);
				}
				Log.log(Log.LEVEL_INFO, MODULE, "Login attempt unsuccessful");
			}

			// mConnection.stop();

			su.stopProgress(StatusUpdate.LEVEL_UPLOAD_ONE, GRI18n.getString(MODULE, "addImgOk"));

			g.setAuthToken(p.getProperty("auth_token"));

			return p;
		}
		su.stopProgress(StatusUpdate.LEVEL_UPLOAD_ONE, GRI18n.getString(MODULE, "addImgErr"));
		return null;
	}

	public NVPair[] fudgeFormParameters(NVPair[] form_data) {
		return form_data;
	}

	public NVPair[] fudgeParameters(NVPair[] data) {
		return data;
	}

	void handleCapabilities() {
		if (serverMinorVersion >= 15) {
			capabilities = capabilities15;
		} else if (serverMinorVersion >= 14) {
			capabilities = capabilities14;
		} else if (serverMinorVersion >= 13) {
			capabilities = capabilities13;
		} else if (serverMinorVersion >= 9) {
			capabilities = capabilities9;
		} else if (serverMinorVersion >= 7) {
			capabilities = capabilities7;
		} else if (serverMinorVersion >= 5) {
			capabilities = capabilities5;
		} else if (serverMinorVersion >= 2) {
			capabilities = capabilities2;
		} else if (serverMinorVersion == 1) {
			capabilities = capabilities1;
		}
	}

	class MyTransferListener implements TransferListener {
		StatusUpdate su;
		java.text.DecimalFormat df = new java.text.DecimalFormat("##,##0");
		java.text.DecimalFormat ff = new java.text.DecimalFormat("##,##0.0");

		String currentFile;

		long sizeAllFiles;
		long transferredFilesDone = 0;
		long transferredThisFile = 0;

		int numberAllFiles;
		int numberFilesDone = 0;

		long timeStarted = 0;
		double kbPerSecond = 0;

		MyTransferListener(StatusUpdate su) {
			this.su = su;
		}

		@Override
		public void dataTransferred(int transferredThisFile, int sizeThisFile, double kbPerSecond) {
			this.transferredThisFile = transferredThisFile;

			Object[] params = { df.format(transferredThisFile / 1024), df.format(sizeThisFile / 1024), ff.format(kbPerSecond / 1024.0) };

			su.updateProgressStatus(StatusUpdate.LEVEL_UPLOAD_ONE, GRI18n.getString(MODULE, "trnsfrStat", params));
			su.updateProgressValue(StatusUpdate.LEVEL_UPLOAD_ONE, transferredThisFile);

			params = new Object[] { currentFile, new Integer(numberFilesDone + 1), new Integer(numberAllFiles),
					new Integer((int) ((transferredFilesDone + transferredThisFile) / 1024 / 1024)),
					new Integer((int) (sizeAllFiles / 1024 / 1024)), getProjectedTimeLeft() };
			su.updateProgressStatus(StatusUpdate.LEVEL_UPLOAD_ALL, GRI18n.getString(MODULE, "upStatus", params));
			su.updateProgressValue(StatusUpdate.LEVEL_UPLOAD_ALL, (int) ((transferredFilesDone + transferredThisFile) * 100 / sizeAllFiles));
		}

		@Override
		public void transferStart(int sizeThisFile) {
			this.transferredThisFile = sizeThisFile;
			if (timeStarted == 0) {
				timeStarted = System.currentTimeMillis();
			}

			su.updateProgressValue(StatusUpdate.LEVEL_UPLOAD_ONE, 0, sizeThisFile);
		}

		@Override
		public void transferEnd(int sizeThisFile) {
			transferredFilesDone += sizeThisFile;
			this.transferredThisFile = 0;
			numberFilesDone++;

			su.updateProgressStatus(StatusUpdate.LEVEL_UPLOAD_ONE, GRI18n.getString(MODULE, "upCompSrvrProc"));
			su.setUndetermined(StatusUpdate.LEVEL_UPLOAD_ONE, true);
		}

		public String getProjectedTimeLeft() {
			int secondsLeft = getProjectedSecondsLeft();

			if (secondsLeft == -1) {
				return "";
			} else if (secondsLeft >= 120) {
				Object[] params = { new Integer(secondsLeft / 60) };
				return GRI18n.getString(MODULE, "minutesLeft", params);
			} else {
				Object[] params = { new Integer(secondsLeft) };
				return GRI18n.getString(MODULE, "secondsLeft", params);
			}
		}

		public int getProjectedSecondsLeft() {
			long timeNow = System.currentTimeMillis();

			if (timeStarted == 0 || timeNow - timeStarted < 500) {
				// just starting, unknown average speed
				return -1;
			}

			// average the current speed and the speed since start
			double denom = (kbPerSecond + ((transferredFilesDone + transferredThisFile) / (timeNow - timeStarted) * 1000)) / 2;

			if (denom == 0) {
				return -1;
			}

			return (int) ((sizeAllFiles - transferredFilesDone - transferredThisFile) / denom);
		}
	}

	/*
	 * -------------------------------------------------------------------------
	 * MAIN METHOD (test only)
	 */
	/*
	 * public static void main( String [] args ) {
	 * 
	 * try { StatusUpdate su = new StatusUpdateAdapter(){}; Gallery g = new
	 * Gallery(); g.setStUrlString( "http://www.deathcult.com/gallery/" );
	 * g.setUsername( "ted" ); g.setPassword( "1qwe2asd" ); g.setStatusUpdate( su
	 * ); GalleryComm2 gc = new GalleryComm2( g ); gc.fetchAlbums( su, false );
	 * 
	 * //try { Thread.sleep( 10000 ); } catch ( InterruptedException ie ) {}
	 * 
	 * ArrayList albumList = g.getAlbumList(); System.err.println(
	 * "albumList size = " + albumList.size() ); for ( int i = 0; i <
	 * albumList.size(); i++ ) { Album a = (Album)albumList.get( i );
	 * a.fetchAlbumProperties( su ); //try { Thread.sleep( 500 ); } catch (
	 * InterruptedException ie ) {} }
	 * 
	 * //try { Thread.sleep( 10000 ); } catch ( InterruptedException ie ) {}
	 * 
	 * ArrayList albumList2 = g.getAlbumList(); System.err.println(
	 * "albumList2 size = " + albumList2.size() ); for ( int i = 0; i <
	 * albumList2.size(); i++ ) { Album a = (Album)albumList2.get( i );
	 * System.err.println( a.getName() + "   srv_rsz = " +
	 * a.getServerAutoResize() ); }
	 * 
	 * } catch ( MalformedURLException mue ) {
	 * 
	 * } }
	 */
}
