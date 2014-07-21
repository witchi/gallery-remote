package com.gallery.galleryremote.main.preview.glass;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import javax.swing.JComponent;

import com.gallery.galleryremote.GalleryRemote;
import com.gallery.galleryremote.Log;
import com.gallery.galleryremote.model.Picture;
import com.gallery.galleryremote.prefs.PreferenceNames;
import com.gallery.galleryremote.util.GRI18n;
import com.gallery.galleryremote.util.ImageLoaderUtil;
import com.gallery.galleryremote.util.ImageUtils;

public class CropGlassPane extends JComponent {

	private static final long serialVersionUID = 6033021304558249809L;
	Color background = new Color(100, 100, 100, 150);
	boolean inDrag;
	Point2D start = null, end = null, moveCropStart = null;
	long dragStartTime;
	Rectangle oldRect = null;
	Rectangle cacheRect = null;
	boolean centerMode = false;
	Picture localCurrentPicture = null;
	boolean updateRectOnce = false;

	int movingEdge = 0; // use SwingConstants
	static final int INSIDE = 9;
	public static final int TOLERANCE = 5;

	@Override
	public void paint(Graphics g) {
		oldRect = null;

		if (localCurrentPicture != loader.pictureShowNow) {
			cacheRect = null;
			localCurrentPicture = loader.pictureShowNow;
		}

		if (loader.pictureShowNow == null || loader.imageShowNow == null || loader.pictureShowNow.isOnline()) {
			cacheRect = null;
			return;
		}

		if (currentRect != null && start != null && end != null) {
			Rectangle ct = loader.pictureShowNow.getCropTo();
			if (ct != null) {
				AffineTransform t = ImageUtils.createTransform(getBounds(), currentRect, loader.pictureShowNow.getDimension(),
						loader.pictureShowNow.getAngle(), loader.pictureShowNow.isFlipped());

				try {
					cacheRect = getRect(t.inverseTransform(ct.getLocation(), null),
							t.inverseTransform(new Point(ct.x + ct.width, ct.y + ct.height), null));

					g.setColor(background);
					g.setClip(currentRect);
					g.fillRect(0, 0, cacheRect.x, getHeight());
					g.fillRect(cacheRect.x, 0, getWidth() - cacheRect.x, cacheRect.y);
					g.fillRect(cacheRect.x, cacheRect.y + cacheRect.height, getWidth() - cacheRect.x, getHeight() - cacheRect.y
							- cacheRect.height);
					g.fillRect(cacheRect.x + cacheRect.width, cacheRect.y, getWidth() - cacheRect.x - cacheRect.width, cacheRect.height);

					g.setColor(Color.black);
					g.drawRect(cacheRect.x, cacheRect.y, cacheRect.width, cacheRect.height);

					g.setColor(background);
					drawThirds(g, cacheRect);

					g.setClip(null);
				} catch (NoninvertibleTransformException e) {
					Log.logException(Log.LEVEL_ERROR, MODULE, e);
				}
			} else {
				if (movingEdge == 0) {
					// only blank the cacheRect if we're not busy modifying
					// it
					cacheRect = null;
				}
			}
		} else {
			cacheRect = null;
		}

		paintInfo(g);

		if (updateRectOnce) {
			updateRectOnce = false;
			updateRect(g);
		}
	}

	public void paintInfo(Graphics g) {
		String message = null;

		Rectangle cropTo = loader.pictureShowNow.getCropTo();
		if (!inDrag) {
			if (cropTo == null) {
				message = GRI18n.getString(this.getClass(), "noCrop");
			} else {
				message = GRI18n.getString(this.getClass(), "crop");
			}
		} else {
			if (movingEdge == 0) {
				message = GRI18n.getString(this.getClass(), "inCrop");
			} else {
				message = GRI18n.getString(this.getClass(), "inModify");
			}
		}

		g.setFont(g.getFont());
		ImageLoaderUtil.paintOutline(g, message, 5, getBounds().height - 5, 1);
	}

	public void updateRect() {
		updateRect(getGraphics());
	}

	public void updateRect(Graphics g) {
		if (updateRectOnce) {
			return;
		}

		g.setXORMode(Color.cyan);
		g.setColor(Color.black);
		if (oldRect != null) {
			g.drawRect(oldRect.x, oldRect.y, oldRect.width, oldRect.height);
			drawThirds(g, oldRect);
		}

		if (inDrag) {
			oldRect = getRect(start, end);
			g.drawRect(oldRect.x, oldRect.y, oldRect.width, oldRect.height);
			drawThirds(g, oldRect);
		}
	}

	private void drawThirds(Graphics g, Rectangle r) {
		if (GalleryRemote.instance().properties.getBooleanProperty(PreferenceNames.PREVIEW_DRAW_THIRDS, false)
				|| "thirds".equals(GalleryRemote.instance().properties.getProperty(PreferenceNames.PREVIEW_DRAW_THIRDS))) {
			g.drawLine(r.x + r.width / 3, r.y, r.x + r.width / 3, r.y + r.height);
			g.drawLine(r.x + r.width * 2 / 3, r.y, r.x + r.width * 2 / 3, r.y + r.height);
			g.drawLine(r.x, r.y + r.height / 3, r.x + r.width, r.y + r.height / 3);
			g.drawLine(r.x, r.y + r.height * 2 / 3, r.x + r.width, r.y + r.height * 2 / 3);
		} else if ("golden".equals(GalleryRemote.instance().properties.getProperty(PreferenceNames.PREVIEW_DRAW_THIRDS))) {
			double p = 1.6180339887499;
			double gw = r.width * p / (2 * p + 1);
			double gh = r.height * p / (2 * p + 1);
			g.drawLine((int) (r.x + gw), r.y, (int) (r.x + gw), r.y + r.height);
			g.drawLine((int) (r.x + r.width - gw), r.y, (int) (r.x + r.width - gw), r.y + r.height);
			g.drawLine(r.x, (int) (r.y + gh), r.x + r.width, (int) (r.y + gh));
			g.drawLine(r.x, (int) (r.y + r.height - gh), r.x + r.width, (int) (r.y + r.height - gh));
		}
	}

	public Rectangle getRect(Point2D p1, Point2D p2) {
		Rectangle r = new Rectangle();
		if (centerMode) {
			r.setFrameFromCenter(p1, p2);
			p1 = new Point2D.Double(r.getMinX(), r.getMinY());
			p2 = new Point2D.Double(r.getMaxX(), r.getMaxY());
		}

		r.setFrameFromDiagonal(makeValid(p1), makeValid(p2));

		return r;
	}

	public Point2D makeValid(Point2D p) {
		double px = p.getX();
		double py = p.getY();

		if (px < currentRect.x) {
			px = currentRect.x;
		}

		if (py < currentRect.y) {
			py = currentRect.y;
		}

		if (px > currentRect.x + currentRect.width - 1) {
			px = currentRect.x + currentRect.width - 1;
		}

		if (py > currentRect.y + currentRect.height - 1) {
			py = currentRect.y + currentRect.height - 1;
		}

		return new Point2D.Double(px, py);
	}

	public boolean isValid(Point2D p) {
		double px = p.getX();
		double py = p.getY();

		return px >= currentRect.x && py >= currentRect.y && px <= currentRect.x + currentRect.width - 1
				&& py <= currentRect.y + currentRect.height - 1;
	}
}
