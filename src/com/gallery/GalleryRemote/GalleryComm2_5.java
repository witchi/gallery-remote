/*
 * Created by IntelliJ IDEA.
 * User: paour
 * Date: Mar 17, 2004
 * Time: 4:20:46 PM
 */
package com.gallery.GalleryRemote;

import java.io.IOException;
import java.util.Arrays;

import ch.innovation.httpclient.ModuleException;
import ch.innovation.httpclient.NVPair;

import com.gallery.GalleryRemote.model.Gallery;
import com.gallery.GalleryRemote.model.Picture;
import com.gallery.GalleryRemote.statusbar.StatusUpdate;
import com.gallery.GalleryRemote.util.GRI18n;

public class GalleryComm2_5 extends GalleryComm2 {
	private static final String MODULE = "GalComm2";

	/** Remote scriptname that provides version 2 of the protocol on the server. */
	public static final String SCRIPT_NAME = "main.php?g2_controller=remote.GalleryRemote&g2_form[cmd]=no-op";

	public static final boolean ZEND_DEBUG = false;

	private static GalleryCommCapabilities[] capabilities2;
	private static GalleryCommCapabilities[] capabilities3;
	private static GalleryCommCapabilities[] capabilities4;
	private static GalleryCommCapabilities[] capabilities6;
	private static GalleryCommCapabilities[] capabilities7;
	private static GalleryCommCapabilities[] capabilities8;
	private static GalleryCommCapabilities[] capabilities9;

	protected GalleryComm2_5(Gallery g) {
		super(g);

		scriptName = "main.php";

		capabilities2 = new GalleryCommCapabilities[] { GalleryCommCapabilities.CAPA_UPLOAD_FILES, GalleryCommCapabilities.CAPA_FETCH_ALBUMS,
				GalleryCommCapabilities.CAPA_UPLOAD_CAPTION, GalleryCommCapabilities.CAPA_FETCH_HIERARCHICAL,
				GalleryCommCapabilities.CAPA_FETCH_ALBUMS_PRUNE };
		capabilities3 = new GalleryCommCapabilities[] { GalleryCommCapabilities.CAPA_UPLOAD_FILES, GalleryCommCapabilities.CAPA_FETCH_ALBUMS,
				GalleryCommCapabilities.CAPA_UPLOAD_CAPTION, GalleryCommCapabilities.CAPA_FETCH_HIERARCHICAL,
				GalleryCommCapabilities.CAPA_NEW_ALBUM, GalleryCommCapabilities.CAPA_FETCH_ALBUMS_PRUNE };
		capabilities4 = new GalleryCommCapabilities[] { GalleryCommCapabilities.CAPA_UPLOAD_FILES, GalleryCommCapabilities.CAPA_FETCH_ALBUMS,
				GalleryCommCapabilities.CAPA_UPLOAD_CAPTION, GalleryCommCapabilities.CAPA_FETCH_HIERARCHICAL,
				GalleryCommCapabilities.CAPA_NEW_ALBUM, GalleryCommCapabilities.CAPA_FETCH_ALBUMS_PRUNE,
				GalleryCommCapabilities.CAPA_FETCH_ALBUM_IMAGES, GalleryCommCapabilities.CAPA_FORCE_FILENAME };
		capabilities6 = new GalleryCommCapabilities[] { GalleryCommCapabilities.CAPA_UPLOAD_FILES, GalleryCommCapabilities.CAPA_FETCH_ALBUMS,
				GalleryCommCapabilities.CAPA_UPLOAD_CAPTION, GalleryCommCapabilities.CAPA_FETCH_HIERARCHICAL,
				GalleryCommCapabilities.CAPA_ALBUM_INFO, GalleryCommCapabilities.CAPA_IMAGE_MAX_SIZE, GalleryCommCapabilities.CAPA_NEW_ALBUM,
				GalleryCommCapabilities.CAPA_FETCH_ALBUMS_PRUNE, GalleryCommCapabilities.CAPA_FETCH_ALBUM_IMAGES,
				GalleryCommCapabilities.CAPA_FORCE_FILENAME };
		capabilities7 = new GalleryCommCapabilities[] { GalleryCommCapabilities.CAPA_UPLOAD_FILES, GalleryCommCapabilities.CAPA_FETCH_ALBUMS,
				GalleryCommCapabilities.CAPA_UPLOAD_CAPTION, GalleryCommCapabilities.CAPA_FETCH_HIERARCHICAL,
				GalleryCommCapabilities.CAPA_ALBUM_INFO, GalleryCommCapabilities.CAPA_IMAGE_MAX_SIZE, GalleryCommCapabilities.CAPA_NEW_ALBUM,
				GalleryCommCapabilities.CAPA_FETCH_ALBUMS_PRUNE, GalleryCommCapabilities.CAPA_FETCH_ALBUM_IMAGES,
				GalleryCommCapabilities.CAPA_FORCE_FILENAME, GalleryCommCapabilities.CAPA_INCREMENT_VIEW_COUNT };
		capabilities8 = new GalleryCommCapabilities[] { GalleryCommCapabilities.CAPA_UPLOAD_FILES, GalleryCommCapabilities.CAPA_FETCH_ALBUMS,
				GalleryCommCapabilities.CAPA_UPLOAD_CAPTION, GalleryCommCapabilities.CAPA_FETCH_HIERARCHICAL,
				GalleryCommCapabilities.CAPA_ALBUM_INFO, GalleryCommCapabilities.CAPA_IMAGE_MAX_SIZE, GalleryCommCapabilities.CAPA_NEW_ALBUM,
				GalleryCommCapabilities.CAPA_FETCH_ALBUMS_PRUNE, GalleryCommCapabilities.CAPA_FETCH_ALBUM_IMAGES,
				GalleryCommCapabilities.CAPA_FORCE_FILENAME, GalleryCommCapabilities.CAPA_INCREMENT_VIEW_COUNT,
				GalleryCommCapabilities.CAPA_FETCH_ALBUMS_TOO };
		capabilities9 = new GalleryCommCapabilities[] { GalleryCommCapabilities.CAPA_UPLOAD_FILES, GalleryCommCapabilities.CAPA_FETCH_ALBUMS,
				GalleryCommCapabilities.CAPA_UPLOAD_CAPTION, GalleryCommCapabilities.CAPA_FETCH_HIERARCHICAL,
				GalleryCommCapabilities.CAPA_ALBUM_INFO, GalleryCommCapabilities.CAPA_IMAGE_MAX_SIZE, GalleryCommCapabilities.CAPA_NEW_ALBUM,
				GalleryCommCapabilities.CAPA_FETCH_ALBUMS_PRUNE, GalleryCommCapabilities.CAPA_FETCH_ALBUM_IMAGES,
				GalleryCommCapabilities.CAPA_FORCE_FILENAME, GalleryCommCapabilities.CAPA_INCREMENT_VIEW_COUNT,
				GalleryCommCapabilities.CAPA_FETCH_ALBUMS_TOO, GalleryCommCapabilities.CAPA_FETCH_RANDOM };

		Arrays.sort(capabilities2);
		Arrays.sort(capabilities3);
		Arrays.sort(capabilities4);
		Arrays.sort(capabilities6);
		Arrays.sort(capabilities7);
		Arrays.sort(capabilities8);
		Arrays.sort(capabilities9);

		g.setGalleryVersion(2);
	}

	@Override
	public void incrementViewCount(StatusUpdate su, Picture p) {
		doTask(new IncrementViewCountTask(su, p), true);
	}

	/**
	 * An extension of GalleryTask to handle moving an album.
	 */
	class IncrementViewCountTask extends GalleryTask {
		Picture p;

		IncrementViewCountTask(StatusUpdate su, Picture p) {
			super(su);
			this.p = p;
		}

		@Override
		void runTask() {
			try {
				// setup the protocol parameters
				NVPair form_data[] = { new NVPair("cmd", "increment-view-count"), new NVPair("protocol_version", PROTOCOL_VERSION),
						new NVPair("itemId", p.getItemId()), };
				Log.log(Log.LEVEL_TRACE, MODULE, "increment-view-count parameters: " + Arrays.asList(form_data));

				form_data = fudgeFormParameters(form_data);

				// load and validate the response
				requestResponse(form_data, su, this);

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

	@Override
	public NVPair[] fudgeParameters(NVPair[] data) {
		NVPair[] data_modified = new NVPair[data.length];
		for (int i = 0; i < data.length; i++) {
			NVPair nvPair = data[i];
			data_modified[i] = new NVPair("g2_" + nvPair.getName(), nvPair.getValue(), nvPair.safeGetEncoding());
		}

		return data_modified;
	}

	@Override
	public NVPair[] fudgeFormParameters(NVPair form_data[]) {
		NVPair[] form_data_modified;
		if (ZEND_DEBUG) {
			form_data_modified = new NVPair[form_data.length + 7];
		} else {
			form_data_modified = new NVPair[form_data.length + 2];
		}

		for (int i = 0; i < form_data.length; i++) {
			if (form_data[i] != null) {
				form_data_modified[i] = new NVPair("g2_form[" + form_data[i].getName() + "]", form_data[i].getValue(),
						form_data[i].safeGetEncoding());
			} else {
				form_data_modified[i] = null;
			}
		}

		form_data_modified[form_data.length] = new NVPair("g2_controller", "remote.GalleryRemote");
		if (g.getAuthToken() != null) {
			form_data_modified[form_data.length + 1] = new NVPair("g2_authToken", g.getAuthToken());
		}

		if (ZEND_DEBUG) {
			form_data_modified[form_data.length + 2] = new NVPair("start_debug", "1");
			form_data_modified[form_data.length + 3] = new NVPair("debug_port", "10000");
			form_data_modified[form_data.length + 4] = new NVPair("debug_host", "172.16.1.35,127.0.0.1");
			form_data_modified[form_data.length + 5] = new NVPair("send_sess_end", "1");
			// form_data_modified[form_data.length + 5] = new
			// NVPair("debug_no_cache", "1077182887875");
			// form_data_modified[form_data.length + 6] = new
			// NVPair("debug_stop", "1");
			form_data_modified[form_data.length + 6] = new NVPair("debug_url", "1");
		}

		Log.log(Log.LEVEL_TRACE, MODULE, "Overriding form data: " + Arrays.asList(form_data_modified));

		return form_data_modified;
	}

	@Override
	void handleCapabilities() {
		if (serverMinorVersion >= 9) {
			capabilities = capabilities9;
		} else if (serverMinorVersion >= 8) {
			capabilities = capabilities8;
		} else if (serverMinorVersion >= 7) {
			capabilities = capabilities7;
		} else if (serverMinorVersion >= 6) {
			capabilities = capabilities6;
		} else if (serverMinorVersion >= 4) {
			capabilities = capabilities4;
		} else if (serverMinorVersion >= 3) {
			capabilities = capabilities3;
		} else {
			capabilities = capabilities2;
		}
	}

}
