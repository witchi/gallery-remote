package com.gallery.galleryremote.main;

class SortType {
	int type;
	String text;

	public SortType(int type, String text) {
		this.type = type;
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}
}
