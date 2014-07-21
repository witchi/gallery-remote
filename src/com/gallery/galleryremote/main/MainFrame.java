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
package com.gallery.galleryremote.main;

import java.awt.Frame;
import java.awt.Image;
import java.io.File;

import com.gallery.galleryremote.main.preview.Preview;
import com.gallery.galleryremote.model.Album;
import com.gallery.galleryremote.model.Gallery;
import com.gallery.galleryremote.model.Picture;
import com.gallery.galleryremote.statusbar.StatusBarPresenter;

/**
 * 
 * @author arothe
 */
public interface MainFrame {

	void initMainFrame();

	boolean isVisible();

	void toFront();

	void addPictures(Album album, File[] files, boolean select);

	void addPictures(Album album, Picture[] pictures, boolean select);

	void repaint();

	void movePicturesDown();

	void movePicturesUp();

	void deleteSelectedPictures();

	Image getThumbnail(Picture p);

	void fetchAlbumImages();
	
	StatusBarPresenter getStatusBar();

	Preview getPreview();

	void removeGallery(Gallery g);

	Frame getActivating();

	void setActivating(Frame frame);
	
	void slideshow();

}
