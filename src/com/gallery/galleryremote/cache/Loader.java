package com.gallery.galleryremote.cache;

public interface Loader {

	void addProcessListener(LoaderListener listener);

	void removeProcessListener(LoaderListener listener);
	
	void start();
	
	void cancel();
}
