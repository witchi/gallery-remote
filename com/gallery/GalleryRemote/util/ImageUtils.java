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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import HTTPClient.Cookie;
import HTTPClient.CookieModule;

import com.gallery.GalleryRemote.CancellableTransferListener;
import com.gallery.GalleryRemote.GalleryFileFilter;
import com.gallery.GalleryRemote.GalleryRemote;
import com.gallery.GalleryRemote.Log;
import com.gallery.GalleryRemote.StatusUpdate;
import com.gallery.GalleryRemote.model.ExifData;
import com.gallery.GalleryRemote.model.Picture;
import com.gallery.GalleryRemote.prefs.GalleryProperties;
import com.gallery.GalleryRemote.prefs.PreferenceNames;
import com.gallery.GalleryRemote.prefs.PropertiesFile;

/**
 * Interface to common image manipulation routines
 * 
 * @author paour
 */

public class ImageUtils {
	public static final String MODULE = "ImageUtils";

	static ArrayList<File> toDelete = new ArrayList<File>();
	static long totalTime = 0;
	static int totalIter = 0;

	public static boolean useIM = false;
	static String imPath = null;
	static int jpegQuality = 75;
	static boolean imIgnoreErrorCode = false;

	public static boolean useJpegtran = false;
	public static boolean useJpegtranCrop = false;
	static String jpegtranPath = null;
	static boolean jpegtranIgnoreErrorCode = false;

	static File tmpDir = null;

	public static final int THUMB = 0;
	public static final int PREVIEW = 1;
	public static final int UPLOAD = 2;

	static String[] filterName = new String[3];
	static String[] format = new String[3];

	public final static String DEFAULT_RESOURCE = "/default.gif";
	public final static String UNRECOGNIZED_RESOURCE = "/default.gif";

	public static Image defaultThumbnail = null;
	public static Image unrecognizedThumbnail = null;

	public static boolean deferredStopUsingIM = false;
	public static boolean deferredStopUsingJpegtran = false;
	public static boolean deferredStopUsingJpegtranCrop = false;
	public static boolean deferredOutOfMemory = false;

	public static ImageObserver observer = new ImageObserver() {
		public boolean imageUpdate(Image img, int infoflags, int x, int y,
				int width, int height) {
			return false;
		}
	};
	public static final String APERTURE_TO_GALLERY_SCPT = "ApertureToGallery.applescript";

	public static Image load(String filename, Dimension d, int usage) {
		return load(filename, d, usage, false);
	}

	public static Image load(String filename, Dimension d, int usage,
			boolean ignoreFailure) {
		if (!new File(filename).exists()) {
			return null;
		}

		Image r = null;
		long start = System.currentTimeMillis();

		if (!GalleryFileFilter.canManipulate(filename)) {
			return unrecognizedThumbnail;
		}

		if (useIM) {
			ArrayList<String> cmd = new ArrayList<String>();
			cmd.add(imPath);

			if (filterName[usage] != null && filterName[usage].length() > 0) {
				// cmdline.append(" -filter ").append(filterName[usage]);
				cmd.add("-filter");
				cmd.add(filterName[usage]);
			}

			cmd.add(filename);

			cmd.add("-resize");
			if (GalleryRemote.instance().properties
					.getBooleanProperty(PreferenceNames.SLIDESHOW_NOSTRETCH)) {
				cmd.add(d.width + "x" + d.height + ">");
			} else {
				cmd.add(d.width + "x" + d.height);
			}

			cmd.add("+profile");
			cmd.add("*");

			File temp = deterministicTempFile("thumb", "." + format[usage],
					tmpDir, filename + d);

			if (!temp.exists()) {
				toDelete.add(temp);

				cmd.add(temp.getPath());

				int exitValue = exec((String[]) cmd.toArray(new String[0]));

				if ((exitValue != 0 && !imIgnoreErrorCode && !ignoreFailure)
						|| !temp.exists()) {
					if (exitValue != -1 && !temp.exists()) {
						// don't kill IM if it's just an InterruptedException
						Log.log(Log.LEVEL_CRITICAL, MODULE,
								"ImageMagick doesn't seem to be working. Disabling");
						stopUsingIM();
					}
				} else {
					try {
						r = ImageIO.read(temp);
					} catch (IOException e) {
						Log.logException(Log.LEVEL_ERROR, MODULE, e);
					} catch (Throwable e) {
						Log.logException(Log.LEVEL_ERROR, MODULE, e);
						Log.log(Log.LEVEL_ERROR, MODULE,
								"Out of memory while loading image " + temp);
						outOfMemoryError();
					}
				}
			} else {
				try {
					r = ImageIO.read(temp);
				} catch (IOException e) {
					Log.logException(Log.LEVEL_ERROR, MODULE, e);
				} catch (Throwable e) {
					Log.logException(Log.LEVEL_ERROR, MODULE, e);
					Log.log(Log.LEVEL_ERROR, MODULE,
							"Out of memory while loading image " + temp);
					outOfMemoryError();
				}
			}
		}

		if (!useIM && r == null) {
			r = loadJava(
					filename,
					d,
					GalleryRemote.instance().properties
							.getBooleanProperty(PreferenceNames.SLIDESHOW_NOSTRETCH));
		}

		long time = System.currentTimeMillis() - start;
		totalTime += time;
		totalIter++;
		Log.log(Log.LEVEL_TRACE, MODULE, "Time: " + time + " - Avg: "
				+ (totalTime / totalIter));

		return r;
	}

	public static Image loadJava(Object reference, Dimension d,
			boolean noStretch) {
		try {
			Image r, scaled;

			// System.gc();

			if (reference instanceof String) {
				r = ImageIO.read(new File((String) reference));
			} else if (reference instanceof URL) {
				r = ImageIO.read((URL) reference);
			} else {
				throw new IllegalArgumentException(
						"loadJava can only be called with a URL or a filename");
			}

			Dimension newD = getSizeKeepRatio(
					new Dimension(r.getWidth(observer), r.getHeight(observer)),
					d, noStretch);
			if (newD == null) {
				return r;
			}

			scaled = createResizedCopy(r, newD.width, newD.height,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			r.flush();
			r = null;

			// System.runFinalization();
			// System.gc();

			return scaled;
		} catch (IOException e) {
			Log.logException(Log.LEVEL_ERROR, MODULE, e);
			return null;
		} catch (Throwable e) {
			Log.logException(Log.LEVEL_ERROR, MODULE, e);
			Log.log(Log.LEVEL_ERROR, MODULE,
					"Out of memory while loading image " + reference);
			outOfMemoryError();
			return null;
		}
	}

	public static File resize(String filename, Dimension d) {
		return resize(filename, d, null, -1);
	}

	public static File resize(String filename, Dimension d, Rectangle cropTo,
			int resizeJpegQuality) {
		File r = null;
		long start = System.currentTimeMillis();

		if (!GalleryFileFilter.canManipulate(filename)) {
			return new File(filename);
		}

		if (useIM) {
			ArrayList<String> cmd = new ArrayList<String>();
			cmd.add(imPath);

			if (filterName[UPLOAD] != null && filterName[UPLOAD].length() > 0) {
				cmd.add("-filter");
				cmd.add(filterName[UPLOAD]);
			}

			cmd.add(filename);

			if (cropTo != null) {
				cmd.add("-crop");
				cmd.add(cropTo.width + "x" + cropTo.height + "+" + cropTo.x
						+ "+" + cropTo.y);
			}

			if (d != null) {
				cmd.add("-resize");
				cmd.add(d.width + "x" + d.height + ">");
			}

			cmd.add("-quality");
			cmd.add(""
					+ ((resizeJpegQuality == -1) ? jpegQuality
							: resizeJpegQuality));

			r = deterministicTempFile("res",
					"." + GalleryFileFilter.getExtension(filename), tmpDir,
					filename + d + cropTo);
			toDelete.add(r);

			cmd.add(r.getPath());

			int exitValue = exec((String[]) cmd.toArray(new String[0]));

			if ((exitValue != 0 && !imIgnoreErrorCode) || !r.exists()) {
				if (exitValue != -1 && !r.exists()) {
					// don't kill IM if it's just an InterruptedException
					Log.log(Log.LEVEL_CRITICAL, MODULE,
							"ImageMagick doesn't seem to be working. Disabling");
					stopUsingIM();
				}
				r = null;
			}
		}

		if (!useIM && r == null) {
			r = resizeJava(filename, d);

			if (r == null) {
				Log.log(Log.LEVEL_TRACE, MODULE,
						"All methods of resize failed: sending original file");
				r = new File(filename);
			}
		}

		long time = System.currentTimeMillis() - start;
		totalTime += time;
		totalIter++;
		Log.log(Log.LEVEL_TRACE, MODULE, "Time: " + time + " - Avg: "
				+ (totalTime / totalIter));

		return r;
	}

	public static File resizeJava(String filename, Dimension d) {
		File r;

		if (!GalleryRemote.instance().properties
				.getBooleanProperty(PreferenceNames.USE_JAVA_RESIZE)) {
			return null;
		}

		if (!GalleryRemote.instance().properties
				.getBooleanProperty(PreferenceNames.SUPPRESS_WARNING_JAVA)) {
			if (stopUsingJavaResize()) {
				return null;
			}
		}

		try {
			// read the image
			ImageInputStream iis = ImageIO.createImageInputStream(new File(
					filename));

			Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
			if (!iter.hasNext()) {
				return null;
			}

			ImageReader reader = (ImageReader) iter.next();
			ImageReadParam param = reader.getDefaultReadParam();
			reader.setInput(iis, true, false);

			// Java bug with thumbnails
			// IIOImage image = reader.readAll(0, param);
			BufferedImage rim = (BufferedImage) reader.readAsRenderedImage(0,
					param);

			iis.close();
			reader.dispose();

			// resize the image
			// BufferedImage rim = (BufferedImage) image.getRenderedImage();

			Dimension newD = getSizeKeepRatio(
					new Dimension(rim.getWidth(), rim.getHeight()), d, true);

			if (newD != null) {
				BufferedImage scaled = createResizedCopy(rim, newD.width,
						newD.height, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
				// ImageObserver imageObserver =
				// GalleryRemote._().getMainFrame();
				// ImageObserver imageObserver = null;
				// BufferedImage scaledB = new
				// BufferedImage(scaled.getWidth(imageObserver),
				// scaled.getHeight(imageObserver), rim.getType());
				// scaledB.getGraphics().drawImage(scaled, 0, 0, imageObserver);

				/*
				 * System.out.println("*** Original"); IIOMetadata metadata =
				 * image.getMetadata(); String names[] =
				 * metadata.getMetadataFormatNames(); for (int i = 0; i <
				 * names.length; i++) {
				 * displayMetadata(metadata.getAsTree(names[i])); }
				 * 
				 * Node root =
				 * metadata.getAsTree(metadata.getNativeMetadataFormatName());
				 * Node markerSequence = root.getFirstChild().getNextSibling();
				 * markerSequence
				 * .removeChild(markerSequence.getFirstChild().getNextSibling
				 * ().getNextSibling().getNextSibling());
				 * markerSequence.removeChild(markerSequence.getLastChild());
				 * markerSequence.removeChild(markerSequence.getLastChild());
				 * markerSequence.removeChild(markerSequence.getLastChild());
				 * markerSequence.removeChild(markerSequence.getLastChild());
				 * markerSequence.removeChild(markerSequence.getLastChild());
				 * markerSequence.removeChild(markerSequence.getLastChild());
				 * markerSequence.removeChild(markerSequence.getLastChild());
				 * 
				 * System.out.println("*** Modified root");
				 * displayMetadata(root);
				 */

				// metadata.setFromTree(metadata.getNativeMetadataFormatName(),
				// root);

				// System.out.println("*** Modified metadata");
				// displayMetadata(metadata.getAsTree(metadata.getNativeMetadataFormatName()));

				// todo: despite my best efforts, I can't get the ImageIO
				// library to keep the metadata.
				IIOImage image = new IIOImage(scaled, null, null);

				// image.getMetadata().mergeTree(metadata.getNativeMetadataFormatName(),
				// root);

				// image.setRaster(scaledB.getRaster());

				// write the image
				r = deterministicTempFile("jres",
						"." + GalleryFileFilter.getExtension(filename), tmpDir,
						filename + d);
				toDelete.add(r);

				ImageWriter writer = ImageIO.getImageWriter(reader);

				if (writer == null) {
					Log.log(Log.LEVEL_ERROR,
							MODULE,
							"No writer to write out "
									+ filename
									+ " ImageIO probably doesn't support it. Resize aborted.");
					return new File(filename);
				}

				ImageOutputStream ios;
				try {
					r.delete();
					ios = ImageIO.createImageOutputStream(r);
				} catch (IOException e) {
					throw new IIOException("Can't create output stream!", e);
				}

				writer.setOutput(ios);
				ImageWriteParam iwp = writer.getDefaultWriteParam();
				iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				iwp.setCompressionQuality(jpegQuality / 100.0F);
				// metadata = writer.getDefaultStreamMetadata(null);
				// System.out.println("*** Default metadata");
				// displayMetadata(metadata.getAsTree(metadata.getNativeMetadataFormatName()));
				writer.write(null, image, iwp);

				// image.getMetadata().mergeTree(metadata.getNativeMetadataFormatName(),
				// root);

				ios.flush();
				ios.close();
				writer.dispose();

				Log.log(Log.LEVEL_TRACE, MODULE, "Java resized " + filename
						+ " to " + r.getPath());
			} else {
				return new File(filename);
			}
		} catch (IOException e) {
			Log.logException(Log.LEVEL_ERROR, MODULE, e);
			return new File(filename);
		}

		return r;
	}

	public static File rotate(String filename, int angle, boolean flip,
			boolean resetExifOrientation) {
		File r = null;

		if (!GalleryFileFilter.canManipulateJpeg(filename)) {
			Log.log(Log.LEVEL_TRACE, MODULE,
					"jpegtran doesn't support rotating anything but jpeg");
			return new File(filename);
		}

		if (useJpegtran) {
			File orig = null;
			File dest = null;
			try {
				if (GalleryRemote.IS_MAC_OS_X) {
					orig = new File(filename);
					dest = deterministicTempFile("tmp",
							"." + GalleryFileFilter.getExtension(filename),
							tmpDir, filename + angle + flip);

					orig.renameTo(dest);
					filename = dest.getPath();
				}

				if (flip) {
					r = jpegtranExec(filename, "-flip", "horizontal", false);
					filename = r.getPath();
				}

				if (angle != 0) {
					r = jpegtranExec(filename, "-rotate", "" + (angle * 90),
							false);
				}

				/*
				 * if (resetExifOrientation) { resetExifOrientation(filename); }
				 */
			} finally {
				if (orig != null && dest != null) {
					dest.renameTo(orig);
				}
			}
		}

		if (!useJpegtran && r == null) {
			throw new UnsupportedOperationException(
					"jpegtran must be installed for this operation");
		}

		return r;
	}

	public static File losslessCrop(String filename, Rectangle cropTo) {
		File r = null;

		if (!GalleryFileFilter.canManipulateJpeg(filename)) {
			throw new UnsupportedOperationException(
					"jpegtran doesn't support cropping anything but jpeg");
		}

		if (useJpegtran) {
			File orig = null;
			File dest = null;
			try {
				if (GalleryRemote.IS_MAC_OS_X) {
					orig = new File(filename);
					dest = deterministicTempFile("tmp",
							"." + GalleryFileFilter.getExtension(filename),
							tmpDir, filename + cropTo);

					orig.renameTo(dest);
					filename = dest.getPath();
				}

				r = jpegtranExec(filename, "-crop", cropTo.width + "x"
						+ cropTo.height + "+" + cropTo.x + "+" + cropTo.y, true);
			} finally {
				if (orig != null && dest != null) {
					dest.renameTo(orig);
				}
			}
		}

		if (!useJpegtran && r == null) {
			throw new UnsupportedOperationException(
					"jpegtran with CROP PATCH must be installed for this operation");
		}

		return r;
	}

	private static File jpegtranExec(String filename, String arg1, String arg2,
			boolean crop) {
		File r;
		ArrayList<String> cmd = new ArrayList<String>();
		cmd.add(jpegtranPath);

		cmd.add("-copy");
		cmd.add("all");
		// cmd.add("-debug");

		cmd.add(arg1);
		cmd.add(arg2);

		r = deterministicTempFile(crop ? "crop" : "rot", "."
				+ GalleryFileFilter.getExtension(filename), tmpDir, filename
				+ arg1 + arg2);
		toDelete.add(r);

		cmd.add("-outfile");
		cmd.add(r.getPath());

		cmd.add(filename);

		int exitValue = exec((String[]) cmd.toArray(new String[0]));

		if ((exitValue != 0 && !jpegtranIgnoreErrorCode) || !r.exists()) {
			if (exitValue != -1 && !r.exists()) {
				// don't kill jpegtran if it's just an InterruptedException
				if (crop) {
					Log.log(Log.LEVEL_CRITICAL, MODULE,
							"jpegtran doesn't seem to be working for cropping. Disabling");
					stopUsingJpegtranCrop();
				} else {
					Log.log(Log.LEVEL_CRITICAL, MODULE,
							"jpegtran doesn't seem to be working. Disabling");
					stopUsingJpegtran();
				}
			}

			r = null;
		}

		return r;
	}

	public static Image rotateImage(Image thumb, int angle, boolean flipped,
			Component c) {
		if (angle != 0 || flipped) {
			int width;
			int height;
			int width1;
			int height1;

			width = thumb.getWidth(c);
			height = thumb.getHeight(c);

			if (angle % 2 == 0) {
				width1 = width;
				height1 = height;
			} else {
				width1 = height;
				height1 = width;
			}

			BufferedImage vImg = (BufferedImage) c.createImage(width1, height1);

			Graphics2D g = (Graphics2D) vImg.getGraphics();

			AffineTransform transform = getRotationTransform(width, height,
					angle, flipped);

			g.drawImage(thumb, transform, c);

			thumb = vImg;
		}

		return thumb;
	}

	public static AffineTransform getRotationTransform(int width, int height,
			int angle, boolean flipped) {
		if (angle != 0 || flipped) {
			int width1;
			int height1;

			if (angle % 2 == 0) {
				width1 = width;
				height1 = height;
			} else {
				width1 = height;
				height1 = width;
			}

			AffineTransform transform = AffineTransform.getTranslateInstance(
					width / 2, height / 2);
			if (angle != 0) {
				transform.rotate(angle * Math.PI / 2);
			}
			if (flipped) {
				transform.scale(-1, 1);
			}
			transform.translate(-width1 / 2 - (angle == 3 ? width - width1 : 0)
					+ (flipped ? width - width1 : 0) * (angle == 1 ? -1 : 1),
					-height1 / 2 - (angle == 1 ? height - height1 : 0));

			return transform;
		}

		return null;
	}

	public static AffineTransform createTransform(Rectangle container,
			Rectangle contentResized, Dimension content, int angle,
			boolean flipped) {
		double scale = Math.sqrt(1.0F * content.width * content.height
				/ contentResized.width / contentResized.height);

		AffineTransform transform = new AffineTransform();
		transform.translate(content.width / 2, content.height / 2);

		if (flipped) {
			transform.scale(-scale, scale);
		} else {
			transform.scale(scale, scale);
		}

		if (angle != 0) {
			transform.rotate(-angle * Math.PI / 2);
		}

		transform.translate(-container.width / 2, -container.height / 2);

		return transform;
	}

	public static LocalInfo getLocalFilenameForPicture(Picture p, boolean full) {
		URL u;
		Dimension d;

		if (!full && p.getSizeResized() == null) {
			// no resized version
			return null;
		}

		if (full) {
			u = p.safeGetUrlFull();
			d = p.safeGetSizeFull();
		} else {
			u = p.getUrlResized();
			d = p.getSizeResized();
		}

		String uid = p.getUniqueId();
		String ext = p.getForceExtension();

		if (uid == null || ext == null) {
			uid = u.getPath();

			int i = uid.lastIndexOf('/');
			uid = uid.substring(i + 1);

			i = uid.lastIndexOf('.');
			ext = uid.substring(i + 1);
			uid = uid.substring(0, i);
		}

		String filename = uid + "." + ext;

		return new LocalInfo(ext, filename, deterministicTempFile("server", "."
				+ ext, tmpDir, uid + d), u, d);
	}

	static class LocalInfo {
		String ext;
		String filename;
		File file;
		URL url;
		Dimension size;

		public LocalInfo(String ext, String filename, File file, URL url,
				Dimension size) {
			this.ext = ext;
			this.filename = filename;
			this.file = file;
			this.url = url;
			this.size = size;
		}
	}

	public static File download(Picture p, Dimension d, StatusUpdate su,
			CancellableTransferListener tl) {
		if (!p.isOnline()) {
			return p.getSource();
		}

		URL pictureUrl;
		File f;
		String filename;
		LocalInfo fullInfo = getLocalFilenameForPicture(p, true);
		boolean stop = false;

		if (p.getSizeResized() != null) {
			LocalInfo resizedInfo = getLocalFilenameForPicture(p, false);

			if ((d.width > p.getSizeResized().width
					|| d.height > p.getSizeResized().height || fullInfo.file
						.exists())
					&& !GalleryRemote.instance().properties
							.getBooleanProperty(PreferenceNames.SLIDESHOW_LOWREZ)) {
				pictureUrl = fullInfo.url;
				// pictureDimension = p.getSizeFull();
				f = fullInfo.file;
				filename = fullInfo.filename;
			} else {
				pictureUrl = resizedInfo.url;
				// pictureDimension = p.getSizeResized();
				f = resizedInfo.file;
				filename = resizedInfo.filename;
			}
		} else {
			pictureUrl = fullInfo.url;
			// pictureDimension = p.getSizeFull();
			f = fullInfo.file;
			filename = fullInfo.filename;
		}

		Log.log(Log.LEVEL_TRACE, MODULE, "Going to download " + pictureUrl);

		try {
			synchronized (p) {
				// don't download the same picture twice
				if (f.exists()) {
					Log.log(Log.LEVEL_TRACE, MODULE, filename
							+ " already existed: no need to download it again");
					return f;
				}

				long start = System.currentTimeMillis();

				URLConnection conn = openUrlConnection(pictureUrl, p);

				int size = conn.getContentLength();

				su.startProgress(StatusUpdate.LEVEL_BACKGROUND, 0, size, GRI18n
						.getString(MODULE, "down.start",
								new Object[] { filename }), false);

				Log.log(Log.LEVEL_TRACE, MODULE,
						"Saving " + p + " to " + f.getPath());

				BufferedInputStream in = new BufferedInputStream(
						conn.getInputStream());
				BufferedOutputStream out = new BufferedOutputStream(
						new FileOutputStream(f));

				byte[] buffer = new byte[2048];
				int l;
				int dl = 0;
				long t = -1;
				while (!stop && (l = in.read(buffer)) != -1) {
					out.write(buffer, 0, l);
					dl += l;

					long now = System.currentTimeMillis();
					if (t != -1 && now - t > 1000) {
						su.updateProgressValue(StatusUpdate.LEVEL_BACKGROUND,
								dl);
						int speed = (int) (dl / (now - start) * 1000 / 1024);
						su.updateProgressStatus(StatusUpdate.LEVEL_BACKGROUND,
								GRI18n.getString(MODULE, "down.progress",
										new Object[] { filename,
												new Integer(dl / 1024),
												new Integer(size / 1024),
												new Integer(speed) }));

						t = now;
					}

					if (t == -1) {
						t = now;
					}

					if (tl != null) {
						stop = !tl.dataTransferred(dl, size, 0, p);
					}
				}

				in.close();
				out.flush();
				out.close();

				if (stop) {
					Log.log(Log.LEVEL_TRACE, MODULE, "Stopped downloading " + p);
					f.delete();
				} else {
					Log.log(Log.LEVEL_TRACE, MODULE, "Downloaded " + p + " ("
							+ dl + ") in "
							+ ((System.currentTimeMillis() - start) / 1000)
							+ "s");
					toDelete.add(f);
				}
			}

			su.stopProgress(StatusUpdate.LEVEL_BACKGROUND, GRI18n.getString(
					MODULE, "down.end", new Object[] { filename }));
		} catch (IOException e) {
			Log.logException(Log.LEVEL_ERROR, MODULE, e);
			f = null;

			su.stopProgress(StatusUpdate.LEVEL_BACKGROUND, "Downloading " + p
					+ " failed");
		}

		if (stop) {
			return null;
		}

		return f;
	}

	public static URLConnection openUrlConnection(URL pictureUrl, Picture p)
			throws IOException {
		URLConnection conn = pictureUrl.openConnection();
		conn.setDefaultUseCaches(false);
		// conn.addRequestProperty("Connection", "Dont-keep-alive");
		String userAgent = p.getAlbumOnServer().getGallery().getUserAgent();
		if (userAgent != null) {
			conn.setRequestProperty("User-Agent", userAgent);
		}
		conn.addRequestProperty("Referer", p.getAlbumOnServer().getGallery()
				.getGalleryUrl("").toString());
		Cookie[] cookies = CookieModule.listAllCookies();
		for (int i = 0; i < cookies.length; i++) {
			conn.addRequestProperty("Cookie", cookies[i].toString());
		}

		conn.connect();
		return conn;
	}

	public static Dimension getPictureDimension(Picture p) {
		if (p.isOnline()) {
			// can't find out size without downloading
			return null;
		}

		ImageInputStream iis;
		try {
			iis = ImageIO.createImageInputStream(p.getSource());

			Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
			if (!iter.hasNext()) {
				return null;
			}

			ImageReader reader = (ImageReader) iter.next();
			reader.setInput(iis, true, false);
			Dimension d = new Dimension(reader.getWidth(0), reader.getHeight(0));

			iis.close();
			reader.dispose();
			return d;
		} catch (IOException e) {
			Log.logException(Log.LEVEL_ERROR, MODULE, e);
			return null;
		}
	}

	public static ArrayList<String> importApertureSelection() {
		File script = new File(System.getProperty("java.io.tmpdir"),
				APERTURE_TO_GALLERY_SCPT);

		if (!script.exists()) {
			// copy the script file from our bundle to /tmp
			InputStream scriptResource = GalleryRemote.class
					.getResourceAsStream("/" + APERTURE_TO_GALLERY_SCPT);
			if (scriptResource == null) {
				try {
					scriptResource = new FileInputStream(
							APERTURE_TO_GALLERY_SCPT);
				} catch (FileNotFoundException e) {
					Log.logException(Log.LEVEL_ERROR, MODULE, e);
				}

				if (scriptResource == null) {
					Log.log(Log.LEVEL_ERROR, MODULE, "Can't find "
							+ APERTURE_TO_GALLERY_SCPT);
					return null;
				}
			}

			BufferedReader scriptInStream = new BufferedReader(
					new InputStreamReader(scriptResource));
			BufferedWriter scriptOutStream = null;

			try {
				String line;
				scriptOutStream = new BufferedWriter(new FileWriter(script));
				while ((line = scriptInStream.readLine()) != null) {
					scriptOutStream.write(line);
					scriptOutStream.write("\n");
				}
			} catch (IOException e) {
				Log.logException(Log.LEVEL_ERROR, MODULE, e);
			} finally {
				try {
					scriptInStream.close();
					if (scriptOutStream != null) {
						scriptOutStream.close();
					}
				} catch (IOException f) {
				}
			}

			toDelete.add(script);
		}

		// run script
		if (exec("osascript " + script.getAbsolutePath()) != 0) {
			return null;
		}

		// load results
		File resultFile = new File(System.getProperty("java.io.tmpdir"),
				"ApertureToGallery.txt");

		if (!resultFile.exists()) {
			resultFile = new File("/tmp/ApertureToGallery.txt");
		}

		ArrayList<String> resultList = new ArrayList<String>();

		if (!resultFile.exists()) {
			return null;
		} else {
			BufferedReader resultReader = null;
			try {
				resultReader = new BufferedReader(new FileReader(resultFile));
				String line = null;

				while ((line = resultReader.readLine()) != null) {
					resultList.add(line);
				}
			} catch (IOException e) {
				Log.logException(Log.LEVEL_ERROR, MODULE, e);
			} finally {
				if (resultReader != null) {
					try {
						resultReader.close();
						resultFile.delete();
					} catch (IOException e) {
					}
				}
			}
		}

		return resultList;
	}

	public static void addToDelete(File f) {
		toDelete.add(f);
	}

	static {
		tmpDir = new File(System.getProperty("java.io.tmpdir"), "thumbs-"
				+ System.getProperty("user.name"));

		if (!tmpDir.exists()) {
			tmpDir.mkdirs();
		}

		Log.log(Log.LEVEL_INFO, MODULE, "tmpDir: " + tmpDir.getPath());

		// Making sure ImageMagick works
		try {
			PropertiesFile pt = null;

			if (new File("imagemagick/im.properties").exists()) {
				pt = new PropertiesFile("imagemagick/im");
			} else if (new File("im.properties").exists()) {
				pt = new PropertiesFile("im");
			}

			GalleryProperties p = null;

			if (pt != null) {
				pt.read();

				// allow overriding from main property file
				p = new GalleryProperties(pt);
				GalleryRemote.instance().defaults.copyProperties(p);
			} else {
				p = GalleryRemote.instance().defaults;
			}

			p = GalleryRemote.instance().properties;

			useIM = p.getBooleanProperty("im.enabled");
			Log.log(Log.LEVEL_INFO, MODULE, "im.enabled: " + useIM);
			if (useIM) {
				imPath = p.getProperty("im.convertPath");
				Log.log(Log.LEVEL_INFO, MODULE, "im.convertPath: " + imPath);

				imIgnoreErrorCode = p.getBooleanProperty("im.ignoreErrorCode",
						imIgnoreErrorCode);
				Log.log(Log.LEVEL_INFO, MODULE, "im.ignoreErrorCode: "
						+ imIgnoreErrorCode);

				if (imPath.indexOf('/') == -1 && imPath.indexOf('\\') == -1) {
					// unqualified path, let's investigate

					if (System.getProperty("os.name").toLowerCase()
							.indexOf("windows") != -1) {
						// we're on Windows with an abbreviated path: look up IM
						// in the registry

						StringBuffer output = new StringBuffer();
						int retval = exec(
								"reg query HKLM\\Software\\ImageMagick\\Current /v BinPath",
								output);

						if (retval == 0) {
							Pattern pat = Pattern.compile(
									"^\\s*BinPath\\s*REG_SZ\\s*(.*)",
									Pattern.MULTILINE
											| Pattern.CASE_INSENSITIVE);
							// Pattern pat = Pattern.compile("BinPath",
							// Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
							Matcher m = pat.matcher(output.toString());
							if (m.find()) {
								imPath = m.group(1) + "\\" + imPath;

								if (!imPath.endsWith(".exe")) {
									imPath += ".exe";
								}

								Log.log(Log.LEVEL_INFO, MODULE,
										"Found ImageMagick in registry. imPath is now "
												+ imPath);
							}
						} else {
							// most likely, we don't have reg.exe, try
							// regedit.exe

							File tempFile = File.createTempFile("gr_regdump",
									null);

							retval = exec(
									"regedit /E \""
											+ tempFile.getPath()
											+ "\" \"HKEY_LOCAL_MACHINE\\Software\\ImageMagick\\Current\"",
									output);

							if (retval == 0) {
								BufferedReader br = new BufferedReader(
										new InputStreamReader(
												new FileInputStream(tempFile),
												"UTF-16"));
								String line;
								Pattern pat = Pattern.compile(
										"^\\\"BinPath\\\"=\\\"(.*)\\\"",
										Pattern.CASE_INSENSITIVE);
								while ((line = br.readLine()) != null) {
									Matcher m = pat.matcher(line);
									if (m.find()) {
										imPath = m.group(1) + "\\" + imPath;

										if (!imPath.endsWith(".exe")) {
											imPath += ".exe";
										}

										Log.log(Log.LEVEL_INFO, MODULE,
												"Found ImageMagick in registry. imPath is now "
														+ imPath);

										break;
									}
								}

								br.close();
							}

							tempFile.delete();
						}
					}

					// try to validate that IM works
					int exitValue = exec(new String[] { imPath, "-version" });

					if (exitValue == -2) {
						Log.log(Log.LEVEL_CRITICAL, MODULE,
								"Can't find ImageMagick in the system Path. Disabling");
						stopUsingIM();
					}
				} else if (!new File(imPath).exists()) {
					Log.log(Log.LEVEL_CRITICAL, MODULE,
							"Can't find ImageMagick Convert at the above path. Disabling");
					stopUsingIM();
				}
			}

			if (useIM) {
				filterName[THUMB] = p.getProperty("im.thumbnailResizeFilter");
				filterName[PREVIEW] = p.getProperty("im.previewResizeFilter");
				filterName[UPLOAD] = p.getProperty("im.uploadResizeFilter");
				if (filterName[UPLOAD] == null) {
					filterName[UPLOAD] = filterName[PREVIEW];
				}

				format[THUMB] = p
						.getProperty("im.thumbnailResizeFormat", "gif");
				format[PREVIEW] = p
						.getProperty("im.previewResizeFormat", "jpg");
				format[UPLOAD] = null;
			}

			// read quality even if useIM is false for java-based resize
			jpegQuality = p.getIntProperty("im.jpegQuality", jpegQuality);
		} catch (Exception e) {
			Log.logException(Log.LEVEL_CRITICAL, MODULE, e);
			stopUsingIM();
		}

		defaultThumbnail = loadJava(
				ImageUtils.class.getResource(DEFAULT_RESOURCE),
				GalleryRemote.instance().properties.getThumbnailSize(), true);

		unrecognizedThumbnail = loadJava(
				ImageUtils.class.getResource(UNRECOGNIZED_RESOURCE),
				GalleryRemote.instance().properties.getThumbnailSize(), true);

		// Making sure jpegtran works
		try {
			PropertiesFile pt = null;

			if (new File("jpegtran/jpegtran.properties").exists()) {
				pt = new PropertiesFile("jpegtran/jpegtran");
			} else if (new File("jpegtran.properties").exists()) {
				pt = new PropertiesFile("jpegtran");
			}

			GalleryProperties p = null;

			if (pt != null) {
				pt.read();

				// allow overriding from main property file
				p = new GalleryProperties(pt);
				GalleryRemote.instance().defaults.copyProperties(p);
			} else {
				p = GalleryRemote.instance().defaults;
			}

			p = GalleryRemote.instance().properties;

			useJpegtran = p.getBooleanProperty("jp.enabled");
			useJpegtranCrop = p.getBooleanProperty("jp.crop.enabled");
			Log.log(Log.LEVEL_INFO, MODULE, "jp.enabled: " + useJpegtran);
			if (useJpegtran) {
				jpegtranPath = p.getProperty("jp.path");
				Log.log(Log.LEVEL_INFO, MODULE, "jp.path: " + jpegtranPath);

				if (System.getProperty("os.name").toLowerCase()
						.indexOf("windows") != -1
						&& !jpegtranPath.endsWith(".exe")) {
					jpegtranPath += ".exe";
				}

				jpegtranIgnoreErrorCode = p.getBooleanProperty(
						"jp.ignoreErrorCode", jpegtranIgnoreErrorCode);
				Log.log(Log.LEVEL_INFO, MODULE, "jp.ignoreErrorCode: "
						+ jpegtranIgnoreErrorCode);

				if (jpegtranPath.indexOf('/') == -1
						&& jpegtranPath.indexOf('\\') == -1) {
					Log.log(Log.LEVEL_CRITICAL, MODULE,
							"jpegtran path is not fully qualified, "
									+ "presence won't be tested until later");
				} else if (!new File(jpegtranPath).exists()) {
					Log.log(Log.LEVEL_CRITICAL, MODULE,
							"Can't find jpegtran at the above path");
					stopUsingJpegtran();
				}
			}
		} catch (Exception e) {
			Log.logException(Log.LEVEL_CRITICAL, MODULE, e);
			stopUsingJpegtran();
		}
	}

	public static void purgeTemp() {
		for (Iterator<File> it = toDelete.iterator(); it.hasNext();) {
			File file = it.next();
			file.delete();
		}
	}

	public static Dimension getSizeKeepRatio(Dimension source,
			Dimension target, boolean noStretch) {
		if (noStretch && target.width > source.width
				&& target.height > source.height) {
			return null;
		}

		Dimension result = new Dimension();

		float sourceRatio = Math.abs((float) source.width / source.height);
		float targetRatio = Math.abs((float) target.width / target.height);

		if (Math.abs(targetRatio) > Math.abs(sourceRatio)) {
			result.height = target.height;
			result.width = (int) (target.height * sourceRatio * (target.height
					* target.width > 0 ? 1 : -1));
		} else {
			result.width = target.width;
			result.height = (int) (target.width / sourceRatio * (target.height
					* target.width > 0 ? 1 : -1));
		}

		return result;
	}

	public static float getRatio(Dimension source, Dimension target) {
		float widthRatio = (float) target.width / source.width;
		float heightRatio = (float) target.height / source.height;

		if (heightRatio > widthRatio) {
			return widthRatio;
		} else {
			return heightRatio;
		}
	}

	public static class AngleFlip {
		public int angle = 0;
		public boolean flip = false;

		public AngleFlip(int angle, boolean flip) {
			this.angle = angle;
			this.flip = flip;
		}
	}

	public static File deterministicTempFile(String prefix, String suffix,
			File directory, String hash) {
		if (directory == null) {
			directory = new File(System.getProperty("java.io.tmpdir"));
		}

		return new File(directory, prefix + hash.hashCode() + suffix);
	}

	public static ExifData getExifData(String filename) {
		try {
			Class<?> c = GalleryRemote
					.secureClassForName("com.gallery.GalleryRemote.util.ExifImageUtils");
			Method m = c.getMethod("getExifData", new Class[] { String.class });
			return (ExifData) m.invoke(null, new Object[] { filename });
		} catch (Throwable e) {
			Log.log(Log.LEVEL_ERROR, MODULE, "Exif library is not installed.");
			// Log.logException(Log.LEVEL_ERROR, MODULE, e);
			return null;
		}
	}

	/*
	 * public static ImageUtils.AngleFlip getExifTargetOrientation(String
	 * filename) { try { Class c =
	 * Class.forName("com.gallery.GalleryRemote.util.ExifImageUtils"); Method m
	 * = c.getMethod("getExifTargetOrientation", new Class[]{String.class});
	 * return (AngleFlip) m.invoke(null, new Object[]{filename}); } catch
	 * (Throwable e) { Log.log(Log.LEVEL_TRACE, MODULE,
	 * "Exif library is not installed."); return null; } }
	 * 
	 * public static Date getExifDateCreated(String filename) { try { Class c =
	 * Class.forName("com.gallery.GalleryRemote.util.ExifImageUtils"); Method m
	 * = c.getMethod("getExifDateCreated", new Class[]{String.class}); return
	 * (Date) m.invoke(null, new Object[]{filename}); } catch (Throwable e) {
	 * Log.log(Log.LEVEL_TRACE, MODULE, "Exif library is not installed.");
	 * return null; } }
	 */

	static Boolean exifAvailable = null;

	public static boolean isExifAvailable() {
		if (exifAvailable == null) {
			try {
				Class<?> c = GalleryRemote
						.secureClassForName("com.gallery.GalleryRemote.util.ExifImageUtils");
				c.getMethod("getExifData", new Class[] { String.class });
				exifAvailable = Boolean.TRUE;
			} catch (Throwable e) {
				Log.log(Log.LEVEL_ERROR, MODULE,
						"Exif library is not installed.");
				// Log.logException(Log.LEVEL_ERROR, MODULE, e);
				exifAvailable = Boolean.FALSE;
			}
		}

		return exifAvailable.booleanValue();
	}

	/* ********* Utilities ********** */
	public static List<File> expandDirectories(List<File> filesAndFolders)
			throws IOException {
		ArrayList<File> allFilesList = new ArrayList<File>();

		Iterator<File> iter = filesAndFolders.iterator();
		while (iter.hasNext()) {
			File f = iter.next();
			if (f.isDirectory()) {
				allFilesList.addAll(listFilesRecursive(f));
			} else {
				allFilesList.add(f);
			}
		}

		return allFilesList;
	}

	public static java.util.List<File> listFilesRecursive(File dir)
			throws IOException {
		ArrayList<File> ret = new ArrayList<File>();

		/*
		 * File.listFiles: stupid call returns null if there's an i/o exception
		 * *or* if the file is not a directory, making a mess.
		 * http://java.sun.com/j2se/1.4/docs/api/java/io/File.html#listFiles()
		 */
		File[] fileArray = dir.listFiles();
		if (fileArray == null) {
			if (dir.isDirectory()) {
				/* convert to exception */
				throw new IOException("i/o exception listing directory: "
						+ dir.getPath());
			} else {
				/* this method should only be called on a directory */
				Log.log(Log.LEVEL_CRITICAL, MODULE,
						"assertion failed: listFilesRecursive called on a non-dir file");
				return ret;
			}
		}

		java.util.List<File> files = Arrays.asList(fileArray);

		Iterator<File> iter = files.iterator();
		while (iter.hasNext()) {
			File f = iter.next();
			if (f.isDirectory()) {
				ret.addAll(listFilesRecursive(f));
			} else {
				ret.add(f);
			}
		}

		return ret;
	}

	public static int exec(String cmdline) {
		return exec(cmdline, null);
	}

	public static int exec(String cmdline, StringBuffer output) {
		Log.log(Log.LEVEL_TRACE, MODULE, "Executing " + cmdline);

		try {
			Process p = Runtime.getRuntime().exec(cmdline);

			return pumpExec(p, output);
		} catch (InterruptedException e) {
			Log.logException(Log.LEVEL_ERROR, MODULE, e);
			return -1;
		} catch (IOException e) {
			Log.logException(Log.LEVEL_ERROR, MODULE, e);
		}

		return 1;
	}

	public static int exec(String[] cmd) {
		return exec(cmd, null);
	}

	public static int exec(String[] cmd, StringBuffer output) {
		Log.log(Log.LEVEL_TRACE, MODULE, "Executing " + Arrays.asList(cmd));

		try {
			Process p = Runtime.getRuntime().exec(cmd);

			return pumpExec(p, output);
		} catch (InterruptedException e) {
			Log.logException(Log.LEVEL_ERROR, MODULE, e);
			return -1;
		} catch (IOException e) {
			Log.logException(Log.LEVEL_ERROR, MODULE, e);
			return -2;
		}
	}

	private static int pumpExec(Process p, StringBuffer output)
			throws InterruptedException, IOException {

		BufferedReader out = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		BufferedReader err = new BufferedReader(new InputStreamReader(
				p.getErrorStream()));

		int exitValue = p.waitFor();

		String line;
		while ((line = out.readLine()) != null) {
			Log.log(Log.LEVEL_TRACE, MODULE, "Out: " + line);
			if (output != null) {
				output.append(line).append("\n");
			}
		}

		while ((line = err.readLine()) != null) {
			Log.log(Log.LEVEL_TRACE, MODULE, "Err: " + line);
		}

		Log.log(Log.LEVEL_TRACE, MODULE, "Returned with value " + exitValue);

		return exitValue;
	}

	public static void deferredTasks() {
		if (deferredStopUsingIM) {
			deferredStopUsingIM = false;

			stopUsingIM();
		}

		if (deferredStopUsingJpegtran) {
			deferredStopUsingJpegtran = false;

			stopUsingJpegtran();
		}

		if (deferredStopUsingJpegtranCrop) {
			deferredStopUsingJpegtranCrop = false;

			stopUsingJpegtranCrop();
		}

		if (deferredOutOfMemory) {
			deferredOutOfMemory = false;

			outOfMemoryError();
		}
	}

	static void stopUsingIM() {
		useIM = false;

		if (!GalleryRemote.instance().properties.getBooleanProperty(
				PreferenceNames.SUPPRESS_WARNING_IM, false)) {
			if (GalleryRemote.instance().getMainFrame() != null
					&& GalleryRemote.instance().getMainFrame().isVisible()) {
				UrlMessageDialog md = new UrlMessageDialog(GRI18n.getString(
						MODULE, "warningTextIM"), GRI18n.getString(MODULE,
						"warningUrlIM"), GRI18n.getString(MODULE,
						"warningUrlTextIM"));

				if (md.dontShow()) {
					GalleryRemote.instance().properties.setBooleanProperty(
							PreferenceNames.SUPPRESS_WARNING_IM, true);
				}
			} else {
				deferredStopUsingIM = true;
			}
		}
	}

	static boolean stopUsingJavaResize() {
		UrlMessageDialog md = new UrlMessageDialog(GRI18n.getString(MODULE,
				"warningTextJava"), null, null, GRI18n.getString(MODULE,
				"useJava"), GRI18n.getString(MODULE, "dontUseJava"));

		boolean useJavaResize = (md.getButtonChosen() == 1);

		if (md.dontShow()) {
			GalleryRemote.instance().properties.setBooleanProperty(
					PreferenceNames.SUPPRESS_WARNING_JAVA, true);
			GalleryRemote.instance().properties.setBooleanProperty(
					PreferenceNames.USE_JAVA_RESIZE, useJavaResize);
		}

		return !useJavaResize;
	}

	static void stopUsingJpegtran() {
		useJpegtran = false;

		if (!GalleryRemote.instance().properties.getBooleanProperty(
				PreferenceNames.SUPPRESS_WARNING_JPEGTRAN, false)) {
			if (GalleryRemote.instance().getMainFrame() != null
					&& GalleryRemote.instance().getMainFrame().isVisible()) {
				UrlMessageDialog md = new UrlMessageDialog(GRI18n.getString(
						MODULE, "warningTextJpegtran"), GRI18n.getString(
						MODULE, "warningUrlJpegtran"), GRI18n.getString(MODULE,
						"warningUrlTextJpegtran"));

				if (md.dontShow()) {
					GalleryRemote.instance().properties.setBooleanProperty(
							PreferenceNames.SUPPRESS_WARNING_JPEGTRAN, true);
				}
			} else {
				deferredStopUsingJpegtran = true;
			}
		}
	}

	static void stopUsingJpegtranCrop() {
		useJpegtranCrop = false;

		if (!GalleryRemote.instance().properties.getBooleanProperty(
				PreferenceNames.SUPPRESS_WARNING_JPEGTRAN_CROP, false)) {
			if (GalleryRemote.instance().getMainFrame() != null
					&& GalleryRemote.instance().getMainFrame().isVisible()) {
				UrlMessageDialog md = new UrlMessageDialog(GRI18n.getString(
						MODULE, "warningTextJpegtranCrop"), GRI18n.getString(
						MODULE, "warningUrlJpegtranCrop"), GRI18n.getString(
						MODULE, "warningUrlTextJpegtranCrop"));

				if (md.dontShow()) {
					GalleryRemote.instance().properties.setBooleanProperty(
							PreferenceNames.SUPPRESS_WARNING_JPEGTRAN_CROP,
							true);
				}
			} else {
				deferredStopUsingJpegtranCrop = true;
			}
		}
	}

	static void outOfMemoryError() {
		if (!GalleryRemote.instance().properties.getBooleanProperty(
				PreferenceNames.SUPPRESS_WARNING_OUT_OF_MEMORY, false)) {
			if (GalleryRemote.instance().getMainFrame() != null
					&& GalleryRemote.instance().getMainFrame().isVisible()) {
				UrlMessageDialog md = new UrlMessageDialog(GRI18n.getString(
						MODULE, "warningTextOutOfMemory"), GRI18n.getString(
						MODULE, "warningUrlOutOfMemory"), GRI18n.getString(
						MODULE, "warningUrlTextOutOfMemory"));

				if (md.dontShow()) {
					GalleryRemote.instance().properties.setBooleanProperty(
							PreferenceNames.SUPPRESS_WARNING_OUT_OF_MEMORY,
							true);
				}
			} else {
				deferredOutOfMemory = true;
			}
		}
	}

	public static void displayMetadata(Node root) {
		displayMetadata(root, 0);
	}

	static void indent(int level) {
		for (int i = 0; i < level; i++) {
			System.out.print("  ");
		}
	}

	static void displayMetadata(Node node, int level) {
		indent(level); // emit open tag
		System.out.print("<" + node.getNodeName());
		NamedNodeMap map = node.getAttributes();
		if (map != null) { // print attribute values
			int length = map.getLength();
			for (int i = 0; i < length; i++) {
				Node attr = map.item(i);
				System.out.print(" " + attr.getNodeName() + "=\""
						+ attr.getNodeValue() + "\"");
			}
		}

		Node child = node.getFirstChild();
		if (child != null) {
			System.out.println(">"); // close current tag
			while (child != null) { // emit child tags recursively
				displayMetadata(child, level + 1);
				child = child.getNextSibling();
			}
			indent(level); // emit close tag
			System.out.println("</" + node.getNodeName() + ">");
		} else {
			System.out.println("/>");
		}
	}

	public static BufferedImage createResizedCopy(Image originalImage,
			int scaledWidth, int scaledHeight, Object hint) {
		BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight,
				BufferedImage.TYPE_INT_ARGB);

		Graphics2D g = scaledBI.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
		g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
		g.dispose();

		return scaledBI;
	}
}
