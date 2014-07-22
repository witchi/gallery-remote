package com.gallery.galleryremote.main.preview.glass;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import com.gallery.galleryremote.model.Picture;
import com.gallery.galleryremote.util.AbstractModel;
import com.gallery.galleryremote.util.ImageLoaderUtil;
import com.gallery.galleryremote.util.ImageUtils;

public class CropGlassPaneModelImpl  extends AbstractModel implements CropGlassPaneModel {

	private Rectangle currentRect = null;
	private long dragStartTime;
	private Rectangle oldRect = null;
	private Rectangle cacheRect = null;
	private boolean centerMode = false;
	private Picture localCurrentPicture = null;
	private boolean updateRectOnce = false;

	private boolean inDrag;
	private Point2D start = null;
	private Point2D end = null;
	private Point2D moveCropStart = null;

	public static final int TOLERANCE = 5;

	private final ImageLoaderUtil loader;

	// TODO: use an interface for the loader class
	public CropGlassPaneModelImpl(ImageLoaderUtil loader) {
		this.loader = loader;
	}

	public void startDragging(int movingEdge, Point mousecoord) {
		dragStartTime = System.currentTimeMillis();
		inDrag = true;

		switch (movingEdge) {
		case CropGlassConstants.NORTH:
			// keep bottom-right
			start = makeValidPoint(new Point(cacheRect.x + cacheRect.width, cacheRect.y + cacheRect.height));
			break;

		case CropGlassConstants.SOUTH:
			// keep top-left
			start = makeValidPoint(new Point(cacheRect.x, cacheRect.y));
			break;

		case CropGlassConstants.WEST:
			// keep bottom-right
			start = makeValidPoint(new Point(cacheRect.x + cacheRect.width, cacheRect.y + cacheRect.height));
			break;

		case CropGlassConstants.EAST:
			// keep top-left
			start = makeValidPoint(new Point(cacheRect.x, cacheRect.y));
			break;

		case CropGlassConstants.NORTH_WEST:
			// keep bottom-right
			start = makeValidPoint(new Point(cacheRect.x + cacheRect.width, cacheRect.y + cacheRect.height));
			break;

		case CropGlassConstants.NORTH_EAST:
			// keep bottom-left
			start = makeValidPoint(new Point(cacheRect.x, cacheRect.y + cacheRect.height));
			break;

		case CropGlassConstants.SOUTH_WEST:
			// keep top-right
			start = makeValidPoint(new Point(cacheRect.x + cacheRect.width, cacheRect.y));
			break;

		case CropGlassConstants.SOUTH_EAST:
			// keep top-left
			start = makeValidPoint(new Point(cacheRect.x, cacheRect.y));
			break;

		case CropGlassConstants.INSIDE:
			// moving: just remember transitionStart for offset
			moveCropStart = makeValidPoint(mousecoord);
			break;

		default:
			// new rectangle
			start = makeValidPoint(mousecoord);
			break;
		}

		loader.pictureShowNow.setCropTo(null);
		updateRectOnce = true;
	}

	public void stopDragging() {
		inDrag = false;
		centerMode = false;

		if (loader.pictureShowNow == null || oldRect == null || loader.pictureShowNow.isOnline()) {
			return;
		}

		AffineTransform t = ImageUtils.createTransform(getBounds(), currentRect, loader.pictureShowNow.getDimension(),
				loader.pictureShowNow.getAngle(), loader.pictureShowNow.isFlipped());
		// pictureShowNow.setCropTo(getRect(t.transform(transitionStart,
		// null), t.transform(end, null)));

		Rectangle tmpRect = new Rectangle();
		tmpRect.setFrameFromDiagonal(t.transform(oldRect.getLocation(), null),
				t.transform(new Point(oldRect.x + oldRect.width, oldRect.y + oldRect.height), null));
		loader.pictureShowNow.setCropTo(tmpRect);

	}

	public boolean hasCachedRect() {
		return cacheRect != null;
	}

	public boolean hasCurrentRect() {
		return currentRect != null;
	}

	public void setCenterMode(boolean center) {
		this.centerMode = center;
	}

	public Point2D makeValidPoint(Point2D p) {
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

	public Rectangle getRect(Point2D p1, Point2D p2) {
		Rectangle r = new Rectangle();
		if (centerMode) {
			r.setFrameFromCenter(p1, p2);
			p1 = new Point2D.Double(r.getMinX(), r.getMinY());
			p2 = new Point2D.Double(r.getMaxX(), r.getMaxY());
		}

		r.setFrameFromDiagonal(makeValidPoint(p1), makeValidPoint(p2));

		return r;
	}
	
	@Override
	public void removeCrop() {
		loader.pictureShowNow.setCropTo(null);
		cacheRect = null;
		fireRefreshEvent();
	}
	
	private void fireRefreshEvent() {
		notifyListeners(new ActionEvent(this, 0, CropGlassPaneActions.REFRESH.name()));
	}
}
