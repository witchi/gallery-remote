package com.gallery.galleryremote;

import com.gallery.galleryremote.model.Picture;

/**
 * Created by IntelliJ IDEA.
 * User: paour
 * Date: Jan 20, 2004
 */
public interface CancellableTransferListener {
	public boolean dataTransferred(int transferred, int overall, double kbPerSecond, Picture picture);
}
