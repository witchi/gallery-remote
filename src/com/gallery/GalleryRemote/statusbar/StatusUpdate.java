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
package com.gallery.GalleryRemote.statusbar;

import com.gallery.GalleryRemote.model.Picture;

/**
 * This interface decouples the status updating methods from MainFrame.
 * 
 * @author <a href="mailto:tim_miller@users.sourceforge.net">Tim Miller</a>
 * @version $id$
 */
public interface StatusUpdate {
	/* level-bound methods */
	public void setStatus(String message);

	public void startProgress(StatusLevel level, int min, int max, String message, boolean undetermined);

	public void updateProgressValue(StatusLevel level, int value);

	public void updateProgressValue(StatusLevel level, int value, int maxValue);

	public void updateProgressStatus(StatusLevel level, String message);

	public void setUndetermined(StatusLevel level, boolean undetermined);

	public int getProgressValue(StatusLevel level);

	public int getProgressMinValue(StatusLevel level);

	public int getProgressMaxValue(StatusLevel level);

	public void stopProgress(StatusLevel level, String message);

	/* level-independant methods */
	public void setInProgress(boolean inProgress);

	public void error(String message);

	public void doneUploading(String newItemName, Picture picture);
}
