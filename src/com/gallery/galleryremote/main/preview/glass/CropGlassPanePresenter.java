package com.gallery.galleryremote.main.preview.glass;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import javax.swing.SwingConstants;

import com.gallery.galleryremote.util.ImageUtils;

public class CropGlassPanePresenter implements MouseListener, MouseMotionListener {

	
	public CropGlassPanePresenter(CropGlassPane view) {
		view.addMouseListener(this);
		view.addMouseMotionListener(this);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (loader.pictureShowNow == null || currentRect == null || loader.pictureShowNow.isOnline()) {
			return;
		}

		if (cacheRect == null) {
			movingEdge = 0;
		}

		dragStartTime = System.currentTimeMillis();

		inDrag = true;

		switch (movingEdge) {
		case SwingConstants.NORTH:
			// keep bottom-right
			start = makeValid(new Point(cacheRect.x + cacheRect.width, cacheRect.y + cacheRect.height));
			break;

		case SwingConstants.SOUTH:
			// keep top-left
			start = makeValid(new Point(cacheRect.x, cacheRect.y));
			break;

		case SwingConstants.WEST:
			// keep bottom-right
			start = makeValid(new Point(cacheRect.x + cacheRect.width, cacheRect.y + cacheRect.height));
			break;

		case SwingConstants.EAST:
			// keep top-left
			start = makeValid(new Point(cacheRect.x, cacheRect.y));
			break;

		case SwingConstants.NORTH_WEST:
			// keep bottom-right
			start = makeValid(new Point(cacheRect.x + cacheRect.width, cacheRect.y + cacheRect.height));
			break;

		case SwingConstants.NORTH_EAST:
			// keep bottom-left
			start = makeValid(new Point(cacheRect.x, cacheRect.y + cacheRect.height));
			break;

		case SwingConstants.SOUTH_WEST:
			// keep top-right
			start = makeValid(new Point(cacheRect.x + cacheRect.width, cacheRect.y));
			break;

		case SwingConstants.SOUTH_EAST:
			// keep top-left
			start = makeValid(new Point(cacheRect.x, cacheRect.y));
			break;

		case INSIDE:
			// moving: just remember transitionStart for offset
			moveCropStart = makeValid(e.getPoint());
			break;

		default:
			// new rectangle
			start = makeValid(e.getPoint());
			break;
		}

		loader.pictureShowNow.setCropTo(null);
		updateRectOnce = true;
		mouseDragged(e);
		repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
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

		setCursor(Cursor.getDefaultCursor());

		repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (currentRect == null) {
			return;
		}

		if (cacheRect == null) {
			movingEdge = 0;
		}

		int modifiers = e.getModifiersEx();

		Point2D p;
		switch (movingEdge) {
		case SwingConstants.NORTH:
			p = makeValid(new Point(cacheRect.x, (int) (e.getPoint().getY())));
			modifiers = 0;
			break;

		case SwingConstants.SOUTH:
			p = makeValid(new Point(cacheRect.x + cacheRect.width, (int) (e.getPoint().getY())));
			modifiers = 0;
			break;

		case SwingConstants.WEST:
			p = makeValid(new Point((int) (e.getPoint().getX()), cacheRect.y));
			modifiers = 0;
			break;

		case SwingConstants.EAST:
			p = makeValid(new Point((int) (e.getPoint().getX()), cacheRect.y + cacheRect.height));
			modifiers = 0;
			break;

		case SwingConstants.NORTH_WEST:
		case SwingConstants.NORTH_EAST:
		case SwingConstants.SOUTH_WEST:
		case SwingConstants.SOUTH_EAST:
			p = makeValid(e.getPoint());
			modifiers = modifiers & ~InputEvent.ALT_DOWN_MASK;
			break;

		case INSIDE:
			// move
			double dx = e.getPoint().getX() - moveCropStart.getX();
			double dy = e.getPoint().getY() - moveCropStart.getY();

			start = makeValid(new Point((int) (cacheRect.x + dx), (int) (cacheRect.y + dy)));
			p = new Point((int) (start.getX() + cacheRect.width), (int) (start.getY() + cacheRect.height));

			if (!isValid(p)) {
				p = makeValid(p);
				start = new Point((int) (p.getX() - cacheRect.width), (int) (p.getY() - cacheRect.height));
			}
			modifiers = 0;
			break;

		default:
			// new rectangle
			p = makeValid(e.getPoint());
			break;
		}

		double px = p.getX();
		double py = p.getY();
		if ((modifiers & InputEvent.SHIFT_DOWN_MASK) == InputEvent.SHIFT_DOWN_MASK) {
			// constrain to a square
			double dx = px - start.getX();
			double dy = py - start.getY();

			if (Math.abs(dx) < Math.abs(dy)) {
				py = start.getY() + (dy * dx > 0 ? dx : -dx);
			} else {
				px = start.getX() + (dx * dy > 0 ? dy : -dy);
			}

			p.setLocation(px, py);
		} else if ((modifiers & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK) {
			// constrain to same aspect ratio
			int dx = (int) (px - start.getX());
			int dy = (int) (py - start.getY());

			// reverse rectangle
			Dimension target;
			int sameOrientation = (Math.abs(dx) - Math.abs(dy)) * (currentRect.width - currentRect.height);
			if (sameOrientation > 0) {
				target = new Dimension(dx, dy);
			} else {
				target = new Dimension(dy, dx);
			}

			Dimension d = ImageUtils.getSizeKeepRatio(currentRect.getSize(), target, false);

			if (sameOrientation > 0) {
				p.setLocation(start.getX() + d.width, start.getY() + d.height);
			} else {
				p.setLocation(start.getX() + d.height, start.getY() + d.width);
			}
		}

		centerMode = (modifiers & InputEvent.ALT_DOWN_MASK) == InputEvent.ALT_DOWN_MASK;

		end = makeValid(p);

		updateRect();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (loader.pictureShowNow == null || loader.imageShowNow == null || loader.pictureShowNow.isOnline() || cacheRect == null) {
			movingEdge = 0;
			setCursor(Cursor.getDefaultCursor());
			return;
		}

		double px = e.getPoint().getX();
		double py = e.getPoint().getY();

		boolean canMove = false;

		if (px >= cacheRect.x + TOLERANCE && px <= cacheRect.x + cacheRect.width - TOLERANCE) {
			if (Math.abs(py - cacheRect.y) < TOLERANCE) {
				movingEdge = SwingConstants.NORTH;
				setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
				canMove = true;
			} else if (Math.abs(py - cacheRect.y - cacheRect.height) < TOLERANCE) {
				movingEdge = SwingConstants.SOUTH;
				setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
				canMove = true;
			}
		}

		if (py >= cacheRect.y + TOLERANCE && py <= cacheRect.y + cacheRect.height - TOLERANCE) {
			if (Math.abs(px - cacheRect.x) < TOLERANCE) {
				movingEdge = SwingConstants.WEST;
				setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
				canMove = true;
			} else if (Math.abs(px - cacheRect.x - cacheRect.width) < TOLERANCE) {
				movingEdge = SwingConstants.EAST;
				setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
				canMove = true;
			}
		}

		if (Math.abs(px - cacheRect.x) < TOLERANCE && Math.abs(py - cacheRect.y) < TOLERANCE) {
			movingEdge = SwingConstants.NORTH_WEST;
			setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
			canMove = true;
		}

		if (Math.abs(px - cacheRect.x - cacheRect.width) < TOLERANCE && Math.abs(py - cacheRect.y) < TOLERANCE) {
			movingEdge = SwingConstants.NORTH_EAST;
			setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
			canMove = true;
		}

		if (Math.abs(px - cacheRect.x) < TOLERANCE && Math.abs(py - cacheRect.y - cacheRect.height) < TOLERANCE) {
			movingEdge = SwingConstants.SOUTH_WEST;
			setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
			canMove = true;
		}

		if (Math.abs(px - cacheRect.x - cacheRect.width) < TOLERANCE && Math.abs(py - cacheRect.y - cacheRect.height) < TOLERANCE) {
			movingEdge = SwingConstants.SOUTH_EAST;
			setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
			canMove = true;
		}

		if (px >= cacheRect.x + TOLERANCE && px <= cacheRect.x + cacheRect.width - TOLERANCE && py >= cacheRect.y + TOLERANCE
				&& py <= cacheRect.y + cacheRect.height - TOLERANCE) {
			movingEdge = INSIDE;
			setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			canMove = true;
		}

		if (!canMove) {
			movingEdge = 0;
			setCursor(Cursor.getDefaultCursor());
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (loader.pictureShowNow == null) {
			return;
		}

		if (System.currentTimeMillis() - dragStartTime < 500) {
			// don't drop the crop if this took longer than a real click
			loader.pictureShowNow.setCropTo(null);
			cacheRect = null;
			repaint();
		}
	}


}
