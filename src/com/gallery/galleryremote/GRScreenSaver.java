// Copyright (c) 2004-2005 Sun Microsystems, Inc. All rights reserved. Use is
// subject to license terms.
// 
// This program is free software; you can redistribute it and/or modify
// it under the terms of the Lesser GNU General Public License as
// published by the Free Software Foundation; either version 2 of the
// License, or (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
// USA

package com.gallery.galleryremote;

import org.jdesktop.jdic.screensaver.SimpleScreensaver;

import java.awt.*;

import com.gallery.galleryremote.util.ImageLoaderUtil;
import com.gallery.galleryremote.util.HTMLEscaper;
import com.gallery.galleryremote.prefs.PreferenceNames;

/**
 * Screensaver that shows a Gallery slideshow
 * 
 * @author Pierre-Luc Paour
 */
public class GRScreenSaver extends SimpleScreensaver implements PreferenceNames {
	public static final String MODULE = "ScreenSaver";
	String coreClass = "com.gallery.galleryremote.GalleryRemoteScreenSaver";
	boolean hasStarted = false;
	GalleryRemoteScreenSaver grss;
	int paintPass = 0;
	int thickness = 1;

	/**
	 * Initialize this screen saver
	 */
	@Override
	public void init() {
		if (!hasStarted) {
			GalleryRemote.createInstance(coreClass, null);
			hasStarted = true;
			grss = (GalleryRemoteScreenSaver) GalleryRemote.instance().getCore();
			grss.setContext(getContext());
			GalleryRemote.instance().initializeGR();

			thickness = GalleryRemote.instance().properties.getIntProperty(SLIDESHOW_FONTTHICKNESS, 1);
		}
	}

	/**
	 * Paint the next frame
	 */
	@Override
	public void paint(Graphics g) {
		// Log.log(Log.LEVEL_TRACE, MODULE, "Paint");
		if (paintPass < 10) {
			paintPass++;

			Component c = getContext().getComponent();
			ImageLoaderUtil.setSlideshowFont(c);
			g.setFont(c.getFont());
			String message = grss.hasSettings ? "Preparing slideshow..." : "Please pick the Settings for your Gallery";

			int width = (int) c.getBounds().getWidth();
			int height = (int) c.getBounds().getHeight();
			ImageLoaderUtil.paintAlignedOutline(g, message, width / 2, height / 2, thickness, 20, width);

			return;
		}

		if (grss.newImage) {
			/*
			 * if (firstPaint) { firstPaint = false; Component c =
			 * getContext().getComponent(); ImageLoaderUtil.setSlideshowFont(c);
			 * g.setFont(c.getFont()); }
			 */

			grss.newImage = false;

			Log.log(Log.LEVEL_TRACE, MODULE, "Really Paint!");
			Component c = getContext().getComponent();
			Image img = grss.loader.imageShowNow;

			int width = (int) c.getBounds().getWidth();
			int height = (int) c.getBounds().getHeight();
			int dx = (width - img.getWidth(c)) / 2;
			int dy = (height - img.getHeight(c)) / 2;
			g.drawImage(img, dx, dy, c);

			g.setColor(GalleryRemote.instance().properties.getColorProperty(PreferenceNames.SLIDESHOW_COLOR));
			g.fillRect(0, 0, dx, height);
			g.fillRect(width - dx, 0, width, height);
			g.fillRect(dx, 0, width - dx, dy);
			g.fillRect(dx, height - dy, width - dx, height);

			String caption = ImageLoaderUtil.stripTags(HTMLEscaper.unescape(grss.loader.pictureShowNow.getCaption()));

			if (caption != null && caption.length() != 0) {
				ImageLoaderUtil.paintAlignedOutline(g, caption, width / 2, height - 10, thickness, 30, width);
			}
		}
	}

	@Override
	public void destroy() {
		GalleryRemote.instance().getCore().shutdown();

		// for the screensaver, it makes sense to keep the temp files around,
		// they'll come useful again...
		// ImageUtils.purgeTemp();

		Log.log(Log.LEVEL_INFO, MODULE, "Shutting down log");
		Log.shutdown();

		super.destroy();
	}
}
