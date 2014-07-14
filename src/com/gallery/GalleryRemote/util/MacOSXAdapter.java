/*
 *  Gallery Remote - a File Upload Utility for Gallery
 *
 *  Gallery - a web based photo album viewer and editor
 *  Copyright (C) 2000-2001 Bharat Mediratta
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or (at
 *  your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package com.gallery.GalleryRemote.util;

import java.lang.reflect.Method;

import javax.swing.JFrame;

import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;
import com.gallery.GalleryRemote.Log;

/**
 * Single class with hooks to handle existing functionality "about", "quit" and
 * "preferences" within the MacOSX application menu
 * 
 * @author iluvatar
 * @version Aug 27, 2003
 */
public class MacOSXAdapter extends ApplicationAdapter {
	private static MacOSXAdapter theAdapter;
	private static com.apple.eawt.Application theApplication;

	private JFrame mainApplication;

	private static String quitMethod;
	private static String preferencesMethod;
	private static String aboutMethod;

	public static final String MODULE = "MacOSX";

	public static void registerMacOSXApplication(JFrame srcApp, String about, String quit, String pref) {
		if (theApplication == null) {
			theApplication = new com.apple.eawt.Application();
		}

		if (theAdapter == null) {
			theAdapter = new MacOSXAdapter(srcApp);
		}
		theApplication.addApplicationListener(theAdapter);

		aboutMethod = about;
		quitMethod = quit;
		if (!pref.equalsIgnoreCase("")) {
			theApplication.setEnabledPreferencesMenu(true);
			preferencesMethod = pref;
		}
	}

	@Override
	public void handleAbout(ApplicationEvent ae) {
		if (mainApplication != null) {
			try {
				ae.setHandled(true);
				// this may happen outside of main application flow
				Class<?> caller = Class.forName(mainApplication.getClass().getName());
				Method callMethod = caller.getDeclaredMethod(aboutMethod, (Class<?>[]) null);
				if (callMethod != null) {
					callMethod.invoke(mainApplication, (Object[]) null);
				}

			} catch (NoClassDefFoundError e) {
				Log.log(Log.LEVEL_ERROR, MODULE,
						"This version of Mac OS X does not support the Apple EAWT.  Application Menu handling has been disabled (" + e + ")");
			} catch (ClassNotFoundException e) {
				Log.log(Log.LEVEL_ERROR, MODULE,
						"This version of Mac OS X does not support the Apple EAWT.  Application Menu handling has been disabled (" + e + ")");
			} catch (Exception e) {
				Log.log(Log.LEVEL_ERROR, MODULE, "Exception while loading the MacOSXAdapter:");
				e.printStackTrace();
			}

		} else {
			throw new IllegalStateException("handleAbout: mainApplication instance detached");
		}
	}

	@Override
	public void handleQuit(ApplicationEvent ae) {
		if (mainApplication != null) {
			try {
				ae.setHandled(false);
				// this may happen outside of main application flow
				Class<?> caller = Class.forName(mainApplication.getClass().getName());
				Method callMethod = caller.getDeclaredMethod(quitMethod, (Class<?>[]) null);
				if (callMethod != null) {
					callMethod.invoke(mainApplication, (Object[]) null);
				}

			} catch (NoClassDefFoundError e) {
				Log.log(Log.LEVEL_ERROR, MODULE,
						"This version of Mac OS X does not support the Apple EAWT.  Application Menu handling has been disabled (" + e + ")");
			} catch (ClassNotFoundException e) {
				Log.log(Log.LEVEL_ERROR, MODULE,
						"This version of Mac OS X does not support the Apple EAWT.  Application Menu handling has been disabled (" + e + ")");
			} catch (Exception e) {
				Log.log(Log.LEVEL_ERROR, MODULE, "Exception while loading the MacOSXAdapter:");
				e.printStackTrace();
			}

		} else {
			throw new IllegalStateException("handleQuit: mainApplication instance detached");
		}
	}

	@Override
	public void handlePreferences(ApplicationEvent ae) {
		if (mainApplication != null) {
			try {
				// this may happen outside of main application flow
				Class<?> caller = Class.forName(mainApplication.getClass().getName());
				Method callMethod = caller.getDeclaredMethod(preferencesMethod, (Class<?>[]) null);
				if (callMethod != null) {
					callMethod.invoke(mainApplication, (Object[]) null);
				}
				ae.setHandled(true);

			} catch (NoClassDefFoundError e) {
				Log.log(Log.LEVEL_ERROR, MODULE,
						"This version of Mac OS X does not support the Apple EAWT.  Application Menu handling has been disabled (" + e + ")");
			} catch (ClassNotFoundException e) {
				Log.log(Log.LEVEL_ERROR, MODULE,
						"This version of Mac OS X does not support the Apple EAWT.  Application Menu handling has been disabled (" + e + ")");
			} catch (Exception e) {
				Log.log(Log.LEVEL_ERROR, MODULE, "Exception while loading the MacOSXAdapter:");
				e.printStackTrace();
			}

		} else {
			throw new IllegalStateException("handlePreferences: mainApplication instance detached");
		}
	}

	private MacOSXAdapter(JFrame srcApp) {
		mainApplication = srcApp;
	}
}
