package com.gallery.galleryremote.statusbar;

public enum StatusLevel {
	NONE(-1), GENERIC(0), BACKGROUND(1), CACHE(2), UPLOAD_ONE(3), UPLOAD_ALL(4), UNINTERRUPTIBLE(5);
	
	private int code;
	
	private StatusLevel(int code) {
		this.code = code;
	}
	
	public int code() {
		return code;
	}
}
