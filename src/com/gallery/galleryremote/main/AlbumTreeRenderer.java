package com.gallery.galleryremote.main;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import com.gallery.galleryremote.model.Album;

class AlbumTreeRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = 1255639764969953072L;
	Album album = null;

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {

		// Swing incorrectly passes selection state in some cases
		TreePath selectionPath = tree.getSelectionPath();
		if (selectionPath != null && selectionPath.getLastPathComponent() == value) {
			sel = true;
		}

		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

		if (value instanceof Album) {
			album = (Album) value;
		} else {
			album = null;
		}

		Font font = getFont();
		if (font != null) {
			if (album != null && album.getSize() > 0) {
				setFont(font.deriveFont(Font.BOLD));
			} else {
				setFont(font.deriveFont(Font.PLAIN));
			}
		}

		if (album != null && album.isHasFetchedImages()) {
			setForeground(Color.green);
		}

		String name = getText();
		if (name != null) {
			name = name.trim();
		}

		setText(name);
		setToolTipText(name);

		return this;
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension retDimension = super.getPreferredSize();

		// account for the fact that albums with added pictures are drawn in
		// bold
		if (retDimension != null) {
			retDimension = new Dimension((int) (retDimension.width * 1.5 + 15), retDimension.height);
		}

		return retDimension;
	}
}
