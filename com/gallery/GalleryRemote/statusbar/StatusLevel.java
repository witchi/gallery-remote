package com.gallery.GalleryRemote.statusbar;

public enum StatusLevel {
	NONE(-1), LEVEL_GENERIC(0), LEVEL_BACKGROUND(1), LEVEL_CACHE(2), LEVEL_UPLOAD_ONE(3), LEVEL_UPLOAD_ALL(4), LEVEL_UNINTERUPTIBLE(5);
	
	private int code;
	
	private StatusLevel(int code) {
		this.code = code;
	}
	
	public int code() {
		return code;
	}
}
