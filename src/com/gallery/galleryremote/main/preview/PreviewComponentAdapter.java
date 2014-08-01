package com.gallery.galleryremote.main.preview;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import com.gallery.galleryremote.util.ImageCache;

public class PreviewComponentAdapter extends ComponentAdapter {

	private final ImageCache loader;

	public PreviewComponentAdapter(ImageCache loader) {
		this.loader = loader;
	}

	@Override
	public void componentResized(ComponentEvent e) {
		loader.flushMemory();
	}

}
