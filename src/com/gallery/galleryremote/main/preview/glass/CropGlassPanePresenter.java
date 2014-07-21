package com.gallery.galleryremote.main.preview.glass;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;

import javax.swing.SwingConstants;

import com.gallery.galleryremote.util.ImageUtils;

public class CropGlassPanePresenter implements MouseListener, MouseMotionListener {

	private final CropGlassPaneModel model;
	private final CropGlassPane view;

	private int movingEdge = 0; // use CropGlassConstants
	
	
	public CropGlassPanePresenter(CropGlassPane view, CropGlassPaneModel model) {
		view.addMouseListener(this);
		view.addMouseMotionListener(this);
		this.model = model;
		this.view = view;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (loader.pictureShowNow == null || !model.hasCurrentRect() || loader.pictureShowNow.isOnline()) {
			return;
		}
		if (!model.hasCachedRect()) {
			movingEdge = CropGlassConstants.NONE;
		}
		model.startDragging(movingEdge);
		mouseDragged(e);

		CropGlassDTO dto = new CropGlassDTO();
		view.refreshUI(dto);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		model.stopDragging();
		CropGlassDTO dto = new CropGlassDTO();
		dto.setCursor(Cursor.DEFAULT_CURSOR);
		view.refreshUI(dto);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (!model.hasCurrentRect()) {
			return;
		}

		if (!model.hasCachedRect()) {
			movingEdge = CropGlassConstants.NONE;
		}

		int modifiers = e.getModifiersEx();

		Point2D p;
		switch (movingEdge) {
		case CropGlassConstants.NORTH:
			p = makeValid(new Point(cacheRect.x, (int) (e.getPoint().getY())));
			modifiers = 0;
			break;

		case CropGlassConstants.SOUTH:
			p = makeValid(new Point(cacheRect.x + cacheRect.width, (int) (e.getPoint().getY())));
			modifiers = 0;
			break;

		case CropGlassConstants.WEST:
			p = makeValid(new Point((int) (e.getPoint().getX()), cacheRect.y));
			modifiers = 0;
			break;

		case CropGlassConstants.EAST:
			p = makeValid(new Point((int) (e.getPoint().getX()), cacheRect.y + cacheRect.height));
			modifiers = 0;
			break;

		case CropGlassConstants.NORTH_WEST:
		case CropGlassConstants.NORTH_EAST:
		case CropGlassConstants.SOUTH_WEST:
		case CropGlassConstants.SOUTH_EAST:
			p = makeValid(e.getPoint());
			modifiers = modifiers & ~InputEvent.ALT_DOWN_MASK;
			break;

		case CropGlassConstants.INSIDE:
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
		end = makeValid(p);
		
		model.setCenterMode((modifiers & InputEvent.ALT_DOWN_MASK) == InputEvent.ALT_DOWN_MASK);

		updateRect();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (loader.pictureShowNow == null || loader.imageShowNow == null || loader.pictureShowNow.isOnline() || cacheRect == null) {
			movingEdge = 0;
			
			CropGlassDTO dto = new CropGlassDTO();
			dto.setCursor(Cursor.DEFAULT_CURSOR);
			
			view.refreshUI(dto);	// AR: added to refresh cursor type.
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
				movingEdge = CropGlassConstants.WEST;
				setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
				canMove = true;
			} else if (Math.abs(px - cacheRect.x - cacheRect.width) < TOLERANCE) {
				movingEdge = CropGlassConstants.EAST;
				setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
				canMove = true;
			}
		}

		if (Math.abs(px - cacheRect.x) < TOLERANCE && Math.abs(py - cacheRect.y) < TOLERANCE) {
			movingEdge = CropGlassConstants.NORTH_WEST;
			setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
			canMove = true;
		}

		if (Math.abs(px - cacheRect.x - cacheRect.width) < TOLERANCE && Math.abs(py - cacheRect.y) < TOLERANCE) {
			movingEdge = CropGlassConstants.NORTH_EAST;
			setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
			canMove = true;
		}

		if (Math.abs(px - cacheRect.x) < TOLERANCE && Math.abs(py - cacheRect.y - cacheRect.height) < TOLERANCE) {
			movingEdge = CropGlassConstants.SOUTH_WEST;
			setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
			canMove = true;
		}

		if (Math.abs(px - cacheRect.x - cacheRect.width) < TOLERANCE && Math.abs(py - cacheRect.y - cacheRect.height) < TOLERANCE) {
			movingEdge = CropGlassConstants.SOUTH_EAST;
			setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
			canMove = true;
		}

		if (px >= cacheRect.x + TOLERANCE && px <= cacheRect.x + cacheRect.width - TOLERANCE && py >= cacheRect.y + TOLERANCE
				&& py <= cacheRect.y + cacheRect.height - TOLERANCE) {
			movingEdge = CropGlassConstants.INSIDE;
			setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			canMove = true;
		}

		if (!canMove) {
			movingEdge = CropGlassConstants.NONE;
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
