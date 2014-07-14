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

/**
 * This interface includes capacity keys for various versions of the Gallery
 * Remote protocols.
 * 
 * @author Pierre-Luc Paour
 * @author arothe
 * @version $id$
 */
public enum GalleryCommCapabilities {
	CAPA_UPLOAD_FILES, CAPA_FETCH_ALBUMS, CAPA_UPLOAD_CAPTION, CAPA_FETCH_HIERARCHICAL, CAPA_ALBUM_INFO, 
	CAPA_NEW_ALBUM, CAPA_FETCH_ALBUMS_PRUNE, CAPA_FORCE_FILENAME, CAPA_FETCH_ALBUM_IMAGES, CAPA_MOVE_ALBUM,
	CAPA_FETCH_ALBUMS_TOO, CAPA_FETCH_NON_WRITEABLE_ALBUMS, CAPA_FETCH_HONORS_HIDDEN, CAPA_IMAGE_MAX_SIZE, 
	CAPA_INCREMENT_VIEW_COUNT, CAPA_FETCH_RANDOM;
}
