package com.gallery.galleryremote.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gallery.galleryremote.CancellableTransferListener;
import com.gallery.galleryremote.GalleryRemote;
import com.gallery.galleryremote.imageloader.ImageLoaderListener;
import com.gallery.galleryremote.imageloader.ImageLoaderThread;
import com.gallery.galleryremote.imageloader.WrapInfo;
import com.gallery.galleryremote.model.Picture;
import com.gallery.galleryremote.prefs.PreferenceNames;
import com.gallery.galleryremote.util.log.Logger;

/**
 * This class holds the current visible picture and a little cache of already
 * displayed images. It stores also the next visible picture of the slideshow.
 * The class notifies a listener (called ImageLoaderUser) on defined points
 * (like start downloading or already loaded - PictureReady).
 */
public class ImageCache implements PreferenceNames, ImageProcessor {
	private static final Logger LOGGER = Logger.getLogger(ImageCache.class);

	private final SmartHashtable images;

	// next visible picture (TODO: is it the next picture or the expected Picture
	// with angle and flip?)
	private Picture nextPicture = null;

	// current visible Picture (meta data)
	private Picture currentPicture = null;

	// current visible Image (image data)
	private Image currentImage = null;

	private final ImageLoaderThread imageLoaderThread;

	public static Color[] darkGray = new Color[11];
	public static Pattern breaker = Pattern.compile("<(br|BR)\\s?\\/?>");
	public static Pattern stripper = Pattern.compile("<[^<>]*>");

	private int cacheSize;
	private boolean ignoreIMFailure = false;
	private CancellableTransferListener transferListener;
	private ImageLoaderListener loadListener;

	public ImageCache(int cacheSize, ImageLoaderListener imageLoadListener) {
		LOGGER.fine("Creating class instance...");

		this.imageLoaderThread = new ImageLoaderThread(this);
		this.cacheSize = cacheSize;
		this.images = new SmartHashtable(cacheSize);
		this.loadListener = imageLoadListener;
		this.transferListener = null;
		this.cacheSize = 10;
	}

	public Image getImage() {
		return currentImage;
	}

	public Picture getPicture() {
		return currentPicture;
	}

	public Picture getNextPicture() {
		return nextPicture;
	}

	public void setTransferListener(CancellableTransferListener transferListener) {
		this.transferListener = transferListener;
	}

	public void flushMemory() {
		images.clear();
		if (currentPicture != null) {
			nextPicture = null;
			currentImage = null;
			preparePicture(currentPicture, true, true);
			currentPicture = null;
		}
	}

	public void reduceMemory() {
		LOGGER.fine("Free memory before reduction: " + Runtime.getRuntime().freeMemory());
		LOGGER.fine("Current image cache: " + images.size() + " - cache size " + cacheSize);

		if (images.size() > 1 && cacheSize > 1) {
			cacheSize = images.size() - 1;
		}

		images.shrink(cacheSize);
		LOGGER.fine("Free memory after reduction: " + Runtime.getRuntime().freeMemory());
	}

	@Override
	public void pictureReady(Image image, Picture picture) {
		if (!loadListener.blockPictureReady(image, picture)) {
			currentImage = image;
			currentPicture = picture;
			loadListener.pictureReady();
		}
	}

	private void firePictureReady(boolean notify, Image image, Picture picture) {
		if (notify) {
			pictureReady(image, picture);
		}
	}

	@Override
	public Image getSizedIconForce(Picture picture) {
		Image r = (Image) images.get(picture);
		if (r == null) {
			return null;
		}

		synchronized (picture) {

			// REFACTOR: move this into a processor oder loader class

			if (picture.isOnline()) {
				loadListener.pictureStartDownloading(picture);

				File f = ImageUtils.download(picture, loadListener.getImageSize(), GalleryRemote.instance().getCore().getMainStatusUpdate(),
						transferListener);

				loadListener.pictureStartProcessing(picture);

				if (f == null) {
					return null;
				}

				r = ImageUtils.load(f.getPath(), loadListener.getImageSize(), ImageUtils.PREVIEW, ignoreIMFailure);

			} else {
				loadListener.pictureStartProcessing(picture);
				r = ImageUtils.load(picture.getSource().getPath(), loadListener.getImageSize(), ImageUtils.PREVIEW, ignoreIMFailure);
			}

			if (r == null) {
				loadListener.pictureLoadError(picture);
			}

			LOGGER.fine("Adding to cache: " + picture);
			images.put(picture, r);
		}

		return r;
	}

	public void preparePicture(Picture picture, boolean async, boolean notify) {
		if (picture == null) {
			nextPicture = null;
			loadListener.nullRect();
			firePictureReady(notify, null, null);
			return;
		}

		if (picture == nextPicture) {
			return;
		}

		nextPicture = picture;
		Image r = (Image) images.get(picture);
		if (r != null) {
			LOGGER.fine("Cache hit: " + picture);
			firePictureReady(notify, r, picture);
			return;
		}

		LOGGER.fine("Cache miss: " + picture);
		if (async) {
			imageLoaderThread.loadPictureAsync(picture, notify);
			return;
		}

		Image sizedIcon = imageLoaderThread.loadPicture(picture);
		if (sizedIcon != null) {
			firePictureReady(notify, sizedIcon, picture);
		}
	}

	// FIXME: move this into another class, it should be part of a UI class
	public static void paintAlignedOutline(Graphics g, String s, int textX, int textY, int thickness, int position, int wrapWidth) {
		paintAlignedOutline(g, textX, textY, thickness, position, wrap((Graphics2D) g, s, wrapWidth), false);
	}

	// FIXME: move this into another class, it should be part of a UI class
	public static Point paintAlignedOutline(Graphics g, int textX, int textY, int thickness, int position, WrapInfo wrapInfo,
			boolean paintAtOrigin) {
		FontMetrics fm = g.getFontMetrics();

		Point boxPos = new Point();

		switch (position / 10) {
		case 1:
		default:
			boxPos.y = textY;
			break;

		case 2:
			boxPos.y = textY - wrapInfo.height / 2;
			break;

		case 3:
			boxPos.y = textY - wrapInfo.height;
			break;
		}

		switch (position % 10) {
		case 2:
		default:
			boxPos.x = textX;
			break;

		case 0:
			boxPos.x = textX - wrapInfo.width / 2;
			break;

		case 4:
			boxPos.x = textX - wrapInfo.width;
			break;
		}

		for (int i = 0; i < wrapInfo.lines.length; i++) {
			Rectangle2D bounds = fm.getStringBounds(wrapInfo.lines[i], g);
			Point linePos = new Point(boxPos);

			if (paintAtOrigin) {
				linePos.x = thickness;
				linePos.y = thickness;
			}

			switch (position % 10) {
			case 2:
			default:
				// nothing to do
				break;

			case 0:
				linePos.x += (wrapInfo.width - bounds.getWidth()) / 2;
				break;

			case 4:
				linePos.x += wrapInfo.width - bounds.getWidth();
				break;
			}

			paintOutline(g, wrapInfo.lines[i], linePos.x, (int) (linePos.y + fm.getAscent() + bounds.getHeight() * i), thickness);
		}

		return boxPos;
	}

	// FIXME: move this into another class, it should be part of a UI class	
	public static WrapInfo wrap(Graphics2D g, String s, int wrapWidth) {
		WrapInfo wrapInfo = new WrapInfo();

		FontMetrics fm = g.getFontMetrics();
		Rectangle2D bounds = fm.getStringBounds(s, g);

		String[] ss = s.split("\n");

		ArrayList<String> lines = new ArrayList<String>(ss.length);

		for (int i = 0; i < ss.length; i++) {
			int linebreak = ss[i].length() - 1;
			while (linebreak != -1 && fm.getStringBounds(ss[i].substring(0, linebreak), g).getWidth() > wrapWidth) {
				linebreak = ss[i].lastIndexOf(' ', linebreak - 1);
			}
			if (linebreak != -1 && linebreak != ss[i].length() - 1) {
				lines.add(ss[i].substring(0, linebreak));
				ss[i] = ss[i].substring(linebreak + 1);
				i--;
			} else {
				lines.add(ss[i]);
			}

			int width = (int) fm.getStringBounds(lines.get(lines.size() - 1), g).getWidth();
			if (width > wrapInfo.width) {
				wrapInfo.width = width;
			}
		}

		wrapInfo.lines = lines.toArray(new String[lines.size()]);
		wrapInfo.height = (int) bounds.getHeight() * ss.length;

		return wrapInfo;
	}

	// FIXME: move this into another class, it should be part of a UI class	
	public static void paintOutline(Graphics g, String s, int textX, int textY, int thickness) {
		if (thickness > 10) {
			thickness = 10;
		}

		if (thickness > 0) {
			g.setColor(getDarkGray(thickness));

			for (int i = -thickness; i <= thickness; i++) {
				for (int j = -thickness; j <= thickness; j++) {
					if (i != 0 || j != 0) {
						g.drawString(s, textX + i, textY + j);
					}
				}
			}
		}

		g.setColor(Color.white);
		g.drawString(s, textX, textY);
	}

	// FIXME: move this into another class, it should be part of a UI class	
	private static Color getDarkGray(int thickness) {
		if (darkGray[thickness] == null) {
			darkGray[thickness] = new Color(64, 64, 64, 255 / thickness / thickness);
		}

		return darkGray[thickness];
	}

	// FIXME: move this into another class, it should be part of a UI class	
	public static void setSlideshowFont(Component c) {
		String fontName = GalleryRemote.instance().properties.getProperty(SLIDESHOW_FONTNAME);
		int defaultFontSize = (int) DialogUtil.findParentWindow(c).getGraphicsConfiguration().getBounds().getHeight() / 40;
		int fontSize = GalleryRemote.instance().properties.getIntProperty(SLIDESHOW_FONTSIZE, defaultFontSize);
		Font f = null;
		if (fontName != null) {
			f = new Font(fontName, 0, fontSize);
		} else if (fontSize != c.getFont().getSize()) {
			f = c.getFont().deriveFont(fontSize + 0.0f);
		}

		if (f != null) {
			c.setFont(f);
		}
	}

	
	// FIXME: move this into another class, it should be part of an utility class	
	public static String stripTags(String text) {
		if (text == null) {
			return null;
		}

		Matcher m = breaker.matcher(text);
		text = m.replaceAll("\n");

		m = stripper.matcher(text);
		text = m.replaceAll("");

		return text;
	}

}
