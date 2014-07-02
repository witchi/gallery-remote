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
package com.gallery.GalleryRemote.model;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.ListModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import com.gallery.GalleryRemote.GalleryCommCapabilities;
import com.gallery.GalleryRemote.GalleryRemote;
import com.gallery.GalleryRemote.Log;
import com.gallery.GalleryRemote.StatusUpdate;
import com.gallery.GalleryRemote.StatusUpdateAdapter;
import com.gallery.GalleryRemote.prefs.PreferenceNames;
import com.gallery.GalleryRemote.util.GRI18n;
import com.gallery.GalleryRemote.util.ImageUtils;
import com.gallery.GalleryRemote.util.NaturalOrderComparator;

/**
 * Album model
 * 
 * @author paour
 */

public class Album extends GalleryItem implements ListModel<Picture>, Serializable, PreferenceNames {

	private static final long serialVersionUID = -448524246006100127L;

	/*
	 * -------------------------------------------------------------------------
	 * CONSTANTS
	 */
	public static final String MODULE = "Album";

	/*
	 * -------------------------------------------------------------------------
	 * LOCAL STORAGE
	 */
	ArrayList<Picture> pictures = new ArrayList<Picture>();

	/*
	 * -------------------------------------------------------------------------
	 * SERVER INFO
	 */
	// Gallery gallery = null;
	// ArrayList subAlbums = new ArrayList();

	// Album parent; // parent Album
	String title = GRI18n.getString(MODULE, "title");
	String name;
	ArrayList<String> extraFields;
	String summary;

	Boolean overrideResize = null;
	Boolean overrideResizeDefault = null;
	int overrideResizeDimension = -1;
	Boolean overrideAddToBeginning = null;

	int autoResize = 0;
	// permissions -- default to true for the sake of old protocols ...
	boolean canRead = true;
	boolean canAdd = true;
	boolean canWrite = true;
	boolean canDeleteFrom = true;
	boolean canDeleteThisAlbum = true;
	boolean canCreateSubAlbum = true;

	boolean hasFetchedInfo = false;
	boolean hasFetchedImages = false;

	transient private Long pictureFileSize;
	transient private Integer albumDepth;
	transient private boolean suppressEvents = false;

	public static List<String> extraFieldsNoShow = Arrays.asList(new String[] { "Capture date", "Upload date", "Description" });
	public static List<String> extraFieldsNoShowG2 = Arrays.asList(new String[] { "Capture date", "Upload date" });

	public Album(Gallery gallery) {
		super(gallery);

		setAllowsChildren(true);
	}

	/**
	 * Retrieves the album properties from the server.
	 */
	public void fetchAlbumProperties(StatusUpdate su) {
		if (!hasFetchedInfo && getGallery().getComm(su).hasCapability(su, GalleryCommCapabilities.CAPA_ALBUM_INFO)) {
			if (su == null) {
				su = new StatusUpdateAdapter() {
				};
			}

			try {
				gallery.getComm(su).albumProperties(su, this, false);
			} catch (RuntimeException e) {
				Log.log(Log.LEVEL_INFO, MODULE, "Server probably doesn't support album-properties");
				Log.logException(Log.LEVEL_INFO, MODULE, e);
			}
		}
	}

	public void fetchAlbumImages(StatusUpdate su, boolean recursive, int maxPictures) {
		fetchAlbumImages(su, recursive, maxPictures, false);
	}

	public void fetchAlbumImages(StatusUpdate su, boolean recursive, int maxPictures, boolean random) {
		if (getGallery().getComm(su).hasCapability(su, GalleryCommCapabilities.CAPA_FETCH_ALBUM_IMAGES)) {
			if (su == null) {
				su = new StatusUpdateAdapter() {
				};
			}

			try {
				removeRemotePictures();

				gallery.getComm(su).fetchAlbumImages(su, this, recursive, true, maxPictures, random);
			} catch (RuntimeException e) {
				Log.log(Log.LEVEL_INFO, MODULE, "Server probably doesn't support album-fetch-images");
				Log.logException(Log.LEVEL_INFO, MODULE, e);
			}
		}
	}

	public void removeRemotePictures() {
		int l = pictures.size();
		for (Iterator<Picture> it = pictures.iterator(); it.hasNext();) {
			Picture picture = (Picture) it.next();
			if (picture.isOnline()) {
				it.remove();
			}
		}

		fireContentsChanged(this, 0, l - 1);
	}

	public void moveAlbumTo(StatusUpdate su, Album newParent) {
		if (getGallery().getComm(su).hasCapability(su, GalleryCommCapabilities.CAPA_MOVE_ALBUM)) {
			if (su == null) {
				su = new StatusUpdateAdapter() {
				};
			}

			try {
				if (gallery.getComm(su).moveAlbum(su, this, newParent, false)) {
					gallery.removeNodeFromParent(this);
					gallery.insertNodeInto(this, newParent, newParent.getChildCount());
				}

				// gallery.fetchAlbums(su);
			} catch (RuntimeException e) {
				Log.log(Log.LEVEL_INFO, MODULE, "Server probably doesn't support move-album");
				Log.logException(Log.LEVEL_INFO, MODULE, e);
			}
		}
	}

	/**
	 * Sets the server auto resize dimension.
	 * 
	 * @param autoResize
	 *           the server's resize dimension
	 */
	public void setServerAutoResize(int autoResize) {
		this.autoResize = autoResize;
		hasFetchedInfo = true;
	}

	/**
	 * Gets the server auto resize dimension.
	 * 
	 * @return the server's resize dimension for this album
	 */
	public int getServerAutoResize() {
		fetchAlbumProperties(null);

		return autoResize;
	}

	/**
	 * Sets the gallery attribute of the Album object
	 * 
	 * @param gallery
	 *           The new gallery
	 */
	/*
	 * public void setGallery(Gallery gallery) { this.gallery = gallery; }
	 */

	/**
	 * Gets the gallery attribute of the Album object
	 * 
	 * @return The gallery
	 */
	public Gallery getGallery() {
		return gallery;
	}

	/**
	 * Gets the pictures inside the album
	 * 
	 * @return The pictures value
	 */
	public Iterator<Picture> getPictures() {
		return pictures.iterator();
	}

	/**
	 * Adds a picture to the album
	 * 
	 * @param p
	 *           the picture to add. This will change its parent album
	 */
	public void addPicture(Picture p) {
		p.setParent(this);
		addPictureInternal(p);

		int index = pictures.indexOf(p);
		fireIntervalAdded(this, index, index);
	}

	/**
	 * Adds a picture to the album
	 * 
	 * @param file
	 *           the file to create the picture from
	 */
	public Picture addPicture(File file) {
		Picture p = new Picture(gallery, file);
		p.setParent(this);
		addPictureInternal(p);

		int index = pictures.indexOf(p);
		fireIntervalAdded(this, index, index);

		return p;
	}

	/**
	 * Adds pictures to the album
	 * 
	 * @param files
	 *           the files to create the pictures from
	 */
	public ArrayList<Picture> addPictures(File[] files) {
		return addPictures(files, -1);
	}

	/**
	 * Adds pictures to the album at a specified index
	 * 
	 * @param files
	 *           the files to create the pictures from
	 * @param index
	 *           the index in the list at which to begin adding
	 */
	public ArrayList<Picture> addPictures(File[] files, int index) {
		List<File> expandedFiles = Arrays.asList(files);

		Log.log(Log.LEVEL_TRACE, MODULE, "addPictures: " + expandedFiles);

		try {
			expandedFiles = ImageUtils.expandDirectories(Arrays.asList(files));
		} catch (IOException e) {
			Log.logException(Log.LEVEL_ERROR, MODULE, e);
		}

		Log.log(Log.LEVEL_TRACE, MODULE, "addPictures (expanded): " + expandedFiles);

		ArrayList<Picture> pictures = new ArrayList<Picture>(expandedFiles.size());

		for (Iterator<File> it = expandedFiles.iterator(); it.hasNext();) {
			File f = it.next();

			Picture p = new Picture(gallery, f);
			p.setParent(this);
			if (index == -1) {
				addPictureInternal(p);
			} else {
				addPictureInternal(index++, p);
			}

			pictures.add(p);
		}

		fireContentsChanged(this, 0, pictures.size() - 1);

		return pictures;
	}

	/**
	 * Adds picturesA to the album
	 */
	public void addPictures(List<Picture> picturesL) {
		addPictures(picturesL, -1);
	}

	public void addPictures(List<Picture> picturesL, int index) {
		for (Iterator<Picture> it = picturesL.iterator(); it.hasNext();) {
			Picture p = it.next();
			p.setParent(this);
			if (index == -1) {
				pictures.add(p);
			} else {
				pictures.add(index++, p);
			}
		}

		gallery.setDirty(true);

		fireContentsChanged(this, 0, pictures.size() - 1);
	}

	private void addPictureInternal(Picture p) {
		addPictureInternal(-1, p);
	}

	private void addPictureInternal(int index, Picture p) {
		// handle EXIF
		if (GalleryRemote.instance().properties.getBooleanProperty(EXIF_AUTOROTATE) && p.getExifData() != null) {
			ImageUtils.AngleFlip af = p.getExifData().getTargetOrientation();

			if (af != null) {
				p.setFlipped(af.flip);
				p.setAngle(af.angle);
				p.setSuppressServerAutoRotate(true);
			}
		}

		if (index == -1) {
			pictures.add(p);
		} else {
			pictures.add(index, p);
		}

		gallery.setDirty(true);
	}

	public void sortPicturesAlphabetically() {
		Collections.sort(pictures, new NaturalOrderComparator<Picture>());
		fireContentsChanged(this, 0, pictures.size() - 1);
	}

	/**
	 * Sorts pictures based on the EXIF Date Created. If no date, then fall to
	 * the bottom.
	 */
	public void sortPicturesCreated() {
		Collections.sort(pictures, new Comparator<Picture>() {

			@Override
			public int compare(Picture o1, Picture o2) {
				Date d1 = null, d2 = null;

				Picture p1 = (Picture) o1;
				Picture p2 = (Picture) o2;

				if (p1.getExifData() != null) {
					d1 = p1.getExifData().getCreationDate();
				}
				if (p2.getExifData() != null) {
					d2 = p2.getExifData().getCreationDate();
				}

				if (d1 != null && d2 == null) {
					return 1;
				}

				if (d1 == null && d2 != null) {
					return -1;
				}

				if (d1 == null && d2 == null) {
					return 0;
				}

				return d1.compareTo(d2);
			}
		});
		fireContentsChanged(this, 0, pictures.size() - 1);
	}

	@SuppressWarnings("unchecked")
	public void sortSubAlbums() {
		if (children != null) {
			Collections.sort((Vector<Album>) children, new NaturalOrderComparator<Album>());
		}

		fireContentsChanged(this, 0, pictures.size() - 1);
	}

	/**
	 * Number of pictures in the album
	 * 
	 * @return Number of pictures in the album
	 */
	public int sizePictures() {
		return pictures.size();
	}

	/**
	 * Remove all the pictures
	 */
	public void clearPictures() {
		int l = pictures.size() - 1;

		pictures.clear();

		fireIntervalRemoved(this, 0, l);
	}

	/**
	 * Remove a picture
	 * 
	 * @param n
	 *           item number of the picture to remove
	 */
	public void removePicture(int n) {
		pictures.remove(n);

		fireIntervalRemoved(this, n, n);
	}

	public void removePicture(Picture p) {
		removePicture(pictures.indexOf(p));
	}

	/**
	 * Remove pictures
	 * 
	 * @param indices
	 *           list of indices of pictures to remove
	 */
	public void removePictures(int[] indices) {
		int min, max;
		min = max = indices[0];

		for (int i = indices.length - 1; i >= 0; i--) {
			pictures.remove(indices[i]);
			if (indices[i] > max)
				max = indices[i];
			if (indices[i] < min)
				min = indices[i];
		}

		fireIntervalRemoved(this, min, max);
	}

	/**
	 * Get a picture from the album
	 * 
	 * @param n
	 *           index of the picture to retrieve
	 * @return The Picture
	 */
	public Picture getPicture(int n) {
		return (Picture) pictures.get(n);
	}

	/**
	 * Set a picture in the album
	 * 
	 * @param n
	 *           index of the picture
	 * @param p
	 *           The new picture
	 */
	public void setPicture(int n, Picture p) {
		pictures.set(n, p);

		fireContentsChanged(this, n, n);
	}

	/**
	 * Get the list of files that contain the pictures
	 * 
	 * @return The fileList value
	 */
	/*
	 * public ArrayList getFileList() { ArrayList l = new ArrayList(
	 * pictures.size() );
	 * 
	 * Enumeration e = pictures.elements(); while ( e.hasMoreElements() ) {
	 * l.add( ( (Picture) e.nextElement() ).getSource() ); }
	 * 
	 * return l; }
	 */

	/**
	 * Sets the name attribute of the Album object
	 * 
	 * @param name
	 *           The new name value
	 */
	public void setName(String name) {
		this.name = removeOffendingChars(name);
	}

	static final String offendingChars = "\\/*?\"\'&<>|.+# ()";

	static String removeOffendingChars(String in) {
		StringBuffer out = new StringBuffer();

		int l = in.length();
		for (int i = 0; i < l; i++) {
			char c = in.charAt(i);
			if (offendingChars.indexOf(c) == -1) {
				out.append(c);
			}
		}

		return out.toString();
	}

	/**
	 * Gets the name attribute of the Album object
	 * 
	 * @return The name value
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the title attribute of the Album object
	 * 
	 * @param title
	 *           The new title
	 */
	public void setTitle(String title) {
		this.title = title;

		if (!suppressEvents) {
			gallery.nodeChanged(this);
		}
	}

	/**
	 * Gets the title attribute of the Album object
	 * 
	 * @return The title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Gets the aggregated file size of all the pictures in the album
	 * 
	 * @return The file size (bytes)
	 */
	public long getPictureFileSize() {
		if (pictureFileSize == null) {
			pictureFileSize = new Long(getPictureFileSize((Picture[]) pictures.toArray(new Picture[0])));
		}

		return pictureFileSize.longValue();
	}

	/**
	 * Gets the aggregated file size of a list of pictures
	 * 
	 * @param pictures
	 *           the list of Pictures
	 * @return The file size (bytes)
	 */
	public static long getPictureFileSize(Picture[] pictures) {
		return getObjectFileSize(Arrays.asList(pictures));
	}

	/**
	 * Gets the aggregated file size of a list of pictures Unsafe, the Objects
	 * will be cast to Pictures.
	 * 
	 * @param pictures
	 *           the list of Pictures
	 * @return The file size (bytes)
	 */
	public static long getObjectFileSize(List<Picture> pictures) {
		long total = 0;

		for (Picture p : pictures) {
			total += p.getFileSize();
		}

		return total;
	}

	@Override
	public String toString() {
		StringBuffer ret = new StringBuffer();
		ret.append(indentHelper(""));
		ret.append(title);

		if (pictures.size() != 0) {
			ret.append(" (" + pictures.size() + ")");
		}

		// using canAdd here, since that's the only operation we perform
		// currently. eventually, when we start changing things
		// on the server, permission support will get more ... interesting.
		if (!canAdd) {
			ret.append(" ").append(GRI18n.getString(MODULE, "ro"));
		}

		return ret.toString();
	}

	@Override
	public boolean equals(Object o) {
		return (o != null && o instanceof Album && ((Album) o).getGallery() == getGallery() && getName() != null
				&& ((Album) o).getName() != null && ((Album) o).getName().equals(getName()));
	}

	/*
	 * -------------------------------------------------------------------------
	 * ListModel Implementation
	 */

	/**
	 * Gets the size attribute of the Album object
	 * 
	 * @return The size value
	 */
	@Override
	public int getSize() {
		return pictures.size();
	}

	/**
	 * Gets the elementAt attribute of the Album object
	 * 
	 * @param index
	 *           Description of Parameter
	 * @return The elementAt value
	 */
	@Override
	public Picture getElementAt(int index) {
		return pictures.get(index);
	}

	/**
	 * Description of the Method
	 */
	/*
	 * public void setParent(Album a) { // take care of a Gallery bug... if (a ==
	 * this) { Log.log(Log.LEVEL_ERROR, MODULE, "Gallery error: the album " +
	 * name + " is its own parent. You should delete it, the album database " +
	 * "is corrupted because of it.");
	 * 
	 * a = (Album) getRoot(); }
	 * 
	 * super.setParent(a); }
	 */

	public ArrayList<String> getExtraFields() {
		return extraFields;
	}

	public void setExtraFieldsString(String extraFieldsString) {
		if (extraFieldsString != null && extraFieldsString.length() > 0) {
			extraFields = new ArrayList<String>();
			StringTokenizer st = new StringTokenizer(extraFieldsString, ",");
			List<String> noShow = null;
			if (getGallery().getGalleryVersion() == 1) {
				noShow = extraFieldsNoShow;
			} else {
				noShow = extraFieldsNoShowG2;
			}
			while (st.hasMoreTokens()) {
				String name = st.nextToken();

				if (!noShow.contains(name) && !extraFields.contains(name)) {
					extraFields.add(name);
				}
			}
		} else {
			extraFields = null;
		}
	}

	public void setCanRead(boolean b) {
		canRead = b;
	}

	public boolean getCanRead() {
		return canRead;
	}

	public void setCanAdd(boolean b) {
		canAdd = b;
	}

	public boolean getCanAdd() {
		return canAdd;
	}

	public void setCanWrite(boolean b) {
		canWrite = b;
	}

	public boolean getCanWrite() {
		return canWrite;
	}

	public void setCanDeleteFrom(boolean b) {
		canDeleteFrom = b;
	}

	public boolean getCanDeleteFrom() {
		return canDeleteFrom;
	}

	public void setCanDeleteThisAlbum(boolean b) {
		canDeleteThisAlbum = b;
	}

	public boolean getCanDeleteThisAlbum() {
		return canDeleteThisAlbum;
	}

	public void setCanCreateSubAlbum(boolean b) {
		canCreateSubAlbum = b;
	}

	public boolean getCanCreateSubAlbum() {
		return canCreateSubAlbum;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public ArrayList<Picture> getPicturesList() {
		return pictures;
	}

	ArrayList<Picture> getUploadablePicturesList() {
		ArrayList<Picture> uploadable = new ArrayList<Picture>();

		for (Iterator<Picture> it = pictures.iterator(); it.hasNext();) {
			Picture picture = (Picture) it.next();
			if (!picture.isOnline()) {
				uploadable.add(picture);
			}
		}

		return uploadable;
	}

	void setPicturesList(ArrayList<Picture> pictures) {
		this.pictures = pictures;

		for (Iterator<Picture> e = pictures.iterator(); e.hasNext();) {
			((Picture) e.next()).setParent(this);
		}

		fireContentsChanged(this, 0, pictures.size() - 1);
	}

	public int getAlbumDepth() throws IllegalArgumentException {
		if (albumDepth == null) {
			albumDepth = new Integer(depthHelper(0));
		}

		return albumDepth.intValue();
	}

	int depthHelper(int depth) throws IllegalArgumentException {
		if (getParentAlbum() == this || depth > 20) {
			throw new IllegalArgumentException("Circular containment hierarchy. Gallery corrupted!");
		}

		if (getParentAlbum() != null) {
			return getParentAlbum().depthHelper(depth + 1);
		} else {
			return depth;
		}
	}

	public static final String INDENT_QUANTUM = "     ";

	String indentHelper(String indent) {
		if (getParentAlbum() != null) {
			return getParentAlbum().indentHelper(indent + INDENT_QUANTUM);
		} else {
			return indent;
		}
	}

	/*
	 * void notifyListeners() { if (!suppressEvents) { fireContentsChanged(this,
	 * 0, pictures.size()); if (gallery != null) { gallery.albumChanged(this); }
	 * } }
	 */

	/*
	 * public ArrayList getSubAlbums() { return subAlbums; }
	 */

	/*
	 * public void addSubAlbum(Album a) { subAlbums.add(a);
	 * 
	 * if (!suppressEvents) { //gallery.fireTreeNodesInserted(this,
	 * gallery.getObjectArrayForAlbum(this), // new int[] { subAlbums.indexOf(a)
	 * }, // new Object[] { a }); gallery.fireTreeStructureChanged(gallery,
	 * gallery.getPathForAlbum(this)); } }
	 * 
	 * public void removeSubAlbum(Album a) { int index = subAlbums.indexOf(a); if
	 * (index != -1) { subAlbums.remove(a);
	 * 
	 * if (!suppressEvents) { //gallery.fireTreeNodesRemoved(this,
	 * gallery.getObjectArrayForAlbum(this), // new int[] { index }, // new
	 * Object[] { a }); gallery.fireTreeStructureChanged(gallery,
	 * gallery.getPathForAlbum(this)); //gallery.fireTreeStructureChanged(this,
	 * new TreePath(gallery.root)); } } }
	 */

	public Boolean getOverrideResize() {
		return overrideResize;
	}

	public void setOverrideResize(Boolean overrideResize) {
		this.overrideResize = overrideResize;
	}

	public Boolean getOverrideResizeDefault() {
		return overrideResizeDefault;
	}

	public void setOverrideResizeDefault(Boolean overrideResizeDefault) {
		this.overrideResizeDefault = overrideResizeDefault;
	}

	public int getOverrideResizeDimension() {
		return overrideResizeDimension;
	}

	public void setOverrideResizeDimension(int overrideResizeDimension) {
		this.overrideResizeDimension = overrideResizeDimension;
	}

	public Boolean getOverrideAddToBeginning() {
		return overrideAddToBeginning;
	}

	public void setOverrideAddToBeginning(Boolean overrideAddToBeginning) {
		this.overrideAddToBeginning = overrideAddToBeginning;
	}

	public boolean getResize() {
		if (overrideResize != null) {
			return overrideResize.booleanValue();
		} else {
			return GalleryRemote.instance().properties.getBooleanProperty(RESIZE_BEFORE_UPLOAD);
		}
	}

	public boolean getResizeDefault() {
		if (overrideResizeDefault != null) {
			return overrideResizeDefault.booleanValue();
		} else {
			return GalleryRemote.instance().properties.getIntDimensionProperty(RESIZE_TO) == 0;
		}
	}

	public int getResizeDimension() {
		if (overrideResizeDimension != -1) {
			return overrideResizeDimension;
		} else {
			return GalleryRemote.instance().properties.getIntDimensionProperty(RESIZE_TO);
		}
	}

	public boolean getAddToBeginning() {
		if (overrideAddToBeginning != null) {
			return overrideAddToBeginning.booleanValue();
		} else {
			// todo
			return false;
		}
	}

	public boolean isHasFetchedImages() {
		return hasFetchedImages;
	}

	public void setHasFetchedImages(boolean hasFetchedImages) {
		this.hasFetchedImages = hasFetchedImages;
	}

	public void setSuppressEvents(boolean suppressEvents) {
		this.suppressEvents = suppressEvents;
	}

	/*
	 * ****************** LIST HANDLING (FOR PICTURES) ***************
	 */
	@Override
	public void addListDataListener(ListDataListener l) {
		if (listenerList == null)
			listenerList = new EventListenerList();
		listenerList.add(ListDataListener.class, l);
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		if (listenerList == null)
			listenerList = new EventListenerList();
		listenerList.remove(ListDataListener.class, l);
	}

	public void fireContentsChanged(Object source, int index0, int index1) {
		if (listenerList == null)
			listenerList = new EventListenerList();
		Object[] listeners = listenerList.getListenerList();
		ListDataEvent e = null;

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ListDataListener.class) {
				if (e == null) {
					e = new ListDataEvent(source, ListDataEvent.CONTENTS_CHANGED, index0, index1);
				}
				((ListDataListener) listeners[i + 1]).contentsChanged(e);
			}
		}
	}

	public void fireIntervalAdded(Object source, int index0, int index1) {
		if (listenerList == null)
			listenerList = new EventListenerList();
		Object[] listeners = listenerList.getListenerList();
		ListDataEvent e = null;

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ListDataListener.class) {
				if (e == null) {
					e = new ListDataEvent(source, ListDataEvent.INTERVAL_ADDED, index0, index1);
				}
				((ListDataListener) listeners[i + 1]).intervalAdded(e);
			}
		}
	}

	public void fireIntervalRemoved(Object source, int index0, int index1) {
		if (listenerList == null)
			listenerList = new EventListenerList();
		Object[] listeners = listenerList.getListenerList();
		ListDataEvent e = null;

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ListDataListener.class) {
				if (e == null) {
					e = new ListDataEvent(source, ListDataEvent.INTERVAL_REMOVED, index0, index1);
				}
				((ListDataListener) listeners[i + 1]).intervalRemoved(e);
			}
		}
	}

	transient protected EventListenerList listenerList = new EventListenerList();

	@SuppressWarnings("unchecked")
	@Override
	public Enumeration<Album> children() {
		return (Enumeration<Album>) super.children();
	}
}
