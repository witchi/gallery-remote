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
package com.gallery.galleryremote;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import javax.swing.JList;

import com.gallery.galleryremote.model.Picture;

/**
 * Picture model
 * 
 * @author paour
 * @created 11 août 2002
 */
public class PictureSelection extends ArrayList<Picture> implements Transferable, ClipboardOwner {

	private static final long serialVersionUID = -2189909427632491648L;
	public static final DataFlavor[] flavors = { new DataFlavor(Picture.class, "Gallery Picture object (local)") };
	private static final java.util.List<DataFlavor> flavorList = Arrays.asList(flavors);

	public PictureSelection() {
	}

	public PictureSelection(JList<Picture> list, boolean clone) {
		int[] selIndices = list.getSelectedIndices();
		for (int i = 0; i < selIndices.length; i++) {
			int selIndex = selIndices[i];
			if (selIndex != -1) {
				Picture p = list.getModel().getElementAt(selIndex);
				if (clone) {
					p = (Picture) p.clone();
				}
				add(p);
			}
		}
	}

	/* Transferable interface */
	@Override
	public synchronized DataFlavor[] getTransferDataFlavors() {
		return flavors;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return (flavorList.contains(flavor));
	}

	@Override
	public synchronized Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {

		if (isDataFlavorSupported(flavor)) {
			ArrayList<Picture> deepClone = new ArrayList<Picture>(size());
			for (Iterator<Picture> it = iterator(); it.hasNext();) {
				deepClone.add((Picture) it.next().clone());
			}
			return deepClone;
		}
		throw new UnsupportedFlavorException(flavor);
	}

	/* ClipboardOwner interface */
	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
	}
}