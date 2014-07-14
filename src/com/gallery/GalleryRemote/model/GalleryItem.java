/*
 * Created by IntelliJ IDEA.
 * User: paour
 * Date: Feb 25, 2004
 * Time: 7:26:45 PM
 */
package com.gallery.GalleryRemote.model;

import javax.swing.tree.DefaultMutableTreeNode;

import com.gallery.GalleryRemote.util.HTMLEscaper;

public abstract class GalleryItem extends DefaultMutableTreeNode implements Cloneable {

	private static final long serialVersionUID = 3219127395262782249L;
	public static final String MODULE = "GalleryItem";

	transient Gallery gallery = null;
	String caption = null;

	transient String escapedCaption = null;

	public GalleryItem(Gallery gallery) {
		this.gallery = gallery;
	}

	@Override
	public Object clone() {
		GalleryItem newGalleryItem = null;

		newGalleryItem = (GalleryItem) super.clone();

		newGalleryItem.caption = caption;
		newGalleryItem.gallery = gallery;

		return newGalleryItem;
	}

	public Album getParentAlbum() {
		return (Album) getParent();
	}

	public void setCaption(String caption) {
		this.caption = caption;
		this.escapedCaption = null;
	}

	public String getCaption() {
		return caption;
	}

	/**
	 * Cache the escapedCaption because the escaping is lengthy and this is
	 * called by a frequent UI method
	 * 
	 * @return the HTML escaped version of the caption
	 */
	public String getEscapedCaption() {
		if (escapedCaption == null) {
			if (caption != null) {
				escapedCaption = HTMLEscaper.escape(caption);
			}
		}

		return escapedCaption;
	}
}