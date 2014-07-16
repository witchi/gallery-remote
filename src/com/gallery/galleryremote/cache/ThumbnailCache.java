package com.gallery.galleryremote.cache;

import java.awt.Image;
import java.util.Iterator;

import com.gallery.galleryremote.model.Picture;

public interface ThumbnailCache {

	int getCountToLoad();

	boolean containsThumbnail(Picture p);

	Picture getNextToLoad();

	void beforeLoad(Loader loader);
	
	void load(Picture p, Image img, int id);
	
	void afterLoad(Loader loader);
	
	void preloadThumbnailFirst(Picture p);
	
	void preloadThumbnails(Iterator<Picture> pictures);
	
	void cancelLoad();
	
	Image getThumbnail(Picture p);
	
	void reload();
	
	void flushMemory();
}
