package com.gallery.galleryremote.main.preview;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import com.gallery.galleryremote.util.ImageLoaderUtil;

public class PreviewComponentAdapter extends ComponentAdapter {

	private final ImageLoaderUtil loader;

	public PreviewComponentAdapter(ImageLoaderUtil loader) {
		this.loader = loader;
	}

	@Override
	public void componentResized(ComponentEvent e) {
		loader.flushMemory();
	}

}
