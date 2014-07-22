package com.gallery.galleryremote.imageloader;

import java.util.Arrays;

public class WrapInfo {
	public String[] lines;
	public int width;
	public int height;

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer();
		sb.append("WrapInfo");
		sb.append("{lines=").append(lines == null ? "null" : Arrays.asList(lines).toString());
		sb.append(", width=").append(width);
		sb.append(", height=").append(height);
		sb.append('}');
		return sb.toString();
	}
}
