package com.gallery.galleryremote;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import javax.swing.JApplet;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import ch.innovation.httpclient.Cookie;
import ch.innovation.httpclient.CookieModule;

import com.gallery.galleryremote.model.Gallery;
import com.gallery.galleryremote.util.DialogUtil;
import com.gallery.galleryremote.util.GRI18n;
import com.gallery.galleryremote.util.ImageUtils;

/**
 * Created by IntelliJ IDEA. User: paour Date: Oct 30, 2003
 */
public class GRApplet extends JApplet {

	private static final long serialVersionUID = -6958755323698104679L;
	public static final String MODULE = "GRApplet";

	protected JLabel jLabel;
	boolean hasStarted = false;
	String coreClass = "com.gallery.galleryremote.GalleryRemoteMainFrame";

	@Override
	public void init() {
		System.out.println("Applet init");
	}

	@Override
	public void start() {
		System.out.println("Applet transitionStart");
		initGalleryRemote();

		if (hasStarted) {
			GalleryRemote.instance().initializeGR();

			String localeString = getParameter("gr_locale");
			if (localeString != null && localeString.length() > 1) {
				Locale locale = GRI18n.parseLocaleString(localeString);
				GRI18n.setLocale(locale.getLanguage(), locale.getCountry());
				Log.log(Log.LEVEL_INFO, MODULE, "Set locale to: " + locale);
			}

			initUI();

			new Thread() {
				@Override
				public void run() {
					GalleryRemote.instance().runGR();
				}
			}.start();
		} else {
			initDummyUI();
		}
	}

	protected void initUI() {
		jLabel = new JLabel(
				"<HTML><CENTER>The Gallery Remote applet is running. Please don't close this window or navigate away!</CENTER></HTML>");
		getContentPane().add(jLabel);
	}

	protected void initDummyUI() {
		jLabel = new JLabel(
				"<HTML><CENTER>The Gallery Remote applet is not running because another is running in the same browser</CENTER></HTML>");
		getContentPane().add(jLabel);
	}

	protected void initGalleryRemote() {
		if (!GalleryRemote.createInstance(coreClass, this)) {
			JOptionPane
					.showMessageDialog(
							DialogUtil.findParentWindow(this),
							"Only one instance of the Gallery Remote can run at the same time...",
							"Error", JOptionPane.ERROR_MESSAGE);
		} else {
			hasStarted = true;
		}
	}

	@Override
	public void stop() {
		System.out.println("Applet stop");
		// don't shutdown the applet if it didn't transitionStart...
		if (hasStarted && GalleryRemote.instance() != null) {
			GalleryRemote.instance().getCore().shutdown();
		}

		ImageUtils.purgeTemp();

		Log.log(Log.LEVEL_INFO, MODULE, "Shutting down log");
		Log.shutdown();
	}

	public void hasShutdown() {
		jLabel.setText("<HTML><CENTER>The Gallery Remote applet has stopped, you can navigate away or close the window</CENTER></HTML>");
	}

	protected AppletInfo getGRAppletInfo() {
		AppletInfo info = new AppletInfo();

		info.gallery = new Gallery(GalleryRemote.instance().getCore()
				.getMainStatusUpdate());
		info.gallery.setBlockWrites(true);

		String url = getParameter("gr_url");
		String urlFull = getParameter("gr_url_full");
		String urlOverride = getParameter("gr_url_override");
		String cookieName = getParameter("gr_cookie_name");
		String cookieValue = getParameter("gr_cookie_value");
		String cookieDomain = getParameter("gr_cookie_domain");
		String cookiePath = getParameter("gr_cookie_path");
		String userAgent = getParameter("gr_user_agent");
		String galleryVersion = getParameter("gr_gallery_version");
		String slideshowFrom = getParameter("gr_slideshow_start_from");

		info.albumName = getParameter("gr_album");

		Log.log(Log.LEVEL_INFO, MODULE, "Applet parameters:");
		Log.log(Log.LEVEL_INFO, MODULE, "gr_url: " + url);
		Log.log(Log.LEVEL_INFO, MODULE, "gr_url_full: " + urlFull);
		Log.log(Log.LEVEL_INFO, MODULE, "gr_url_override: " + urlOverride);
		Log.log(Log.LEVEL_INFO, MODULE, "gr_cookie_name: " + cookieName);
		Log.log(Log.LEVEL_INFO, MODULE, "gr_cookie_domain: " + cookieDomain);
		Log.log(Log.LEVEL_INFO, MODULE, "gr_cookie_path: " + cookiePath);
		Log.log(Log.LEVEL_INFO, MODULE, "gr_album: " + info.albumName);
		Log.log(Log.LEVEL_INFO, MODULE, "gr_user_agent: " + userAgent);
		Log.log(Log.LEVEL_INFO, MODULE, "gr_gallery_version: " + galleryVersion);
		Log.log(Log.LEVEL_INFO, MODULE, "gr_slideshow_start_from: "
				+ slideshowFrom);

		if (cookieDomain == null || cookieDomain.length() < 1) {
			try {
				cookieDomain = new URL(url).getHost();
			} catch (MalformedURLException e) {
				URL documentBase = getDocumentBase();
				cookieDomain = documentBase.getHost();

				Log.log(Log.LEVEL_INFO,
						MODULE,
						"URL probably doesn't have a host part because the Gallery "
								+ "is in (unsupported) relative mode. Using the Applet documentBase: "
								+ cookieDomain);

				try {
					url = new URL(documentBase.getProtocol(),
							documentBase.getHost(), documentBase.getPort(), url)
							.toString();
				} catch (MalformedURLException e1) {
					Log.logException(Log.LEVEL_ERROR, MODULE, e1);
				}
			}
		}

		if (urlOverride != null) {
			info.gallery.setType(Gallery.TYPE_APPLET);
			info.gallery.setApUrlString(urlOverride);
			urlFull = urlOverride;
		} else if (urlFull != null) {
			// the server specified a full URL, we're probably in embedded mode
			// and we have to recreate the URL

			URL documentBase = getDocumentBase();

			// if urlFull is indeed a full URL, use it
			try {
				URL urlFullU = new URL(urlFull);

				if (urlFullU.getHost().equals(documentBase.getHost())
						&& urlFullU.getPort() == documentBase.getPort()) {
					info.gallery.setType(Gallery.TYPE_APPLET);
					info.gallery.setApUrlString(urlFull);

					Log.log(Log.LEVEL_TRACE, MODULE, "Full URL: " + urlFull);
				} else {
					Log.log(Log.LEVEL_TRACE, MODULE,
							"urlFull doesn't match documentBase for important data");

					throw new MalformedURLException();
				}
			} catch (MalformedURLException e) {
				Log.log(Log.LEVEL_TRACE, MODULE,
						"urlFull is not a valid URL: recomposing it from documentBase");

				try {
					String path = null;

					if (urlFull.startsWith("/")) {
						path = urlFull;
					} else {
						path = documentBase.getPath();
						int i = path.lastIndexOf("/");
						if (i != -1) {
							path = path.substring(0, i);
						}

						path += "/" + urlFull;
					}

					urlFull = new URL(documentBase.getProtocol(),
							documentBase.getHost(), documentBase.getPort(),
							path).toString();

					info.gallery.setType(Gallery.TYPE_APPLET);
					info.gallery.setApUrlString(urlFull);

					Log.log(Log.LEVEL_TRACE, MODULE, "Full URL: " + urlFull);
				} catch (MalformedURLException ee) {
					Log.logException(Log.LEVEL_ERROR, MODULE, ee);
					urlFull = null;
				}
			}
		}

		if (urlFull == null) {
			// old versions of Gallery, or bad urlFull
			info.gallery.setType(Gallery.TYPE_STANDALONE);
			info.gallery.setStUrlString(url);
		}

		info.gallery.setUserAgent(userAgent);
		info.gallery.cookieLogin = true;
		if (galleryVersion != null) {
			try {
				info.gallery.forceGalleryVersion = Integer
						.parseInt(galleryVersion);
			} catch (RuntimeException e) {
				Log.logException(Log.LEVEL_ERROR, MODULE, e);
			}
		}
		info.slideshowFrom = slideshowFrom;

		CookieModule.discardAllCookies();
		if (cookieName != null) {
			Cookie cookie = new Cookie(cookieName, cookieValue, cookieDomain,
					cookiePath, null, false);
			Log.log(Log.LEVEL_TRACE, MODULE, "Adding cookie: " + cookie);
			CookieModule.addCookie(cookie);
		}

		int cookieNum = 1;
		String name = null;
		while ((name = getParameter("gr_cookie" + cookieNum + "_name")) != null) {
			String value = getParameter("gr_cookie" + cookieNum + "_value");
			Cookie cookie1 = new Cookie(name, value, cookieDomain, cookiePath,
					null, false);
			Log.log(Log.LEVEL_TRACE, MODULE, "Adding cookie: " + cookie1);
			CookieModule.addCookie(cookie1);
			cookieNum++;
		}

		return info;
	}

	class AppletInfo {
		String albumName;
		Gallery gallery;
		String slideshowFrom;
	}
}
