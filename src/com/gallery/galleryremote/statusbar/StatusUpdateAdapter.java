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
package com.gallery.galleryremote.statusbar;

import com.gallery.galleryremote.model.Picture;

/**
 * This is an event adapter for the StatusUpdateListener class.
 * 
 * @author <a href="mailto:tim_miller@users.sourceforge.net">Tim Miller</a>
 * @author <a href="mailto:andre.rothe@phosco.info">Andr&eacute Rothe</a>
 * 
 * @version $id$
 */
public class StatusUpdateAdapter implements StatusUpdate {
	/* level-bound methods */
	@Override
	public void startProgress(StatusLevel level, int min, int max, String message, boolean undetermined) {
	}

	@Override
	public void updateProgressValue(StatusLevel level, int value) {
	}

	@Override
	public void updateProgressValue(StatusLevel level, int value, int maxValue) {
	}

	@Override
	public void setUndetermined(StatusLevel level, boolean undetermined) {
	}

	@Override
	public void updateProgressStatus(StatusLevel level, String message) {
	}

	@Override
	public void stopProgress(StatusLevel level, String message) {
	}

	/* level-independant methods */
	@Override
	public void setInProgress(boolean inProgress) {
	}

	@Override
	public void setStatus(String message) {
	}

	@Override
	public void error(String message) {
	}

	@Override
	public int getProgressValue(StatusLevel level) {
		return 0;
	}

	@Override
	public int getProgressMinValue(StatusLevel level) {
		return 0;
	}

	@Override
	public int getProgressMaxValue(StatusLevel level) {
		return 0;
	}

	@Override
	public void doneUploading(String newItemName, Picture picture) {
	}
}
