package com.gallery.galleryremote.util.log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public final class GalleryLogFormatter extends Formatter {

	@Override
	public String format(LogRecord record) {
		StringBuffer buf = new StringBuffer(1000);
		buf.append(calcDate(record.getMillis()));
		buf.append(" | ");
		buf.append(record.getLevel().getName());
		buf.append(" | ");
		buf.append(record.getLoggerName());
		buf.append(format(record.getMessage(), buf.length()));
		buf.append("\n");
		return buf.toString();
	}

	private StringBuffer format(String msg, int margin) {
		StringBuffer res = new StringBuffer();
		StringTokenizer tok = new StringTokenizer(msg, "\n");
		while (tok.hasMoreTokens()) {
			res.append(" | ");
			res.append(tok.nextToken());
			if (tok.hasMoreTokens()) {
				res.append("\n");
				res.append(getSpaces(margin));
			}
		}
		return res;
	}
	
	private String getSpaces(int count) {
		String res = "";
		for (int i = 0; i<count; i++) {
			res += " ";
		}
		return res;
	}
	
	private String calcDate(long millisecs) {
	    SimpleDateFormat date_format = new SimpleDateFormat("dd.MMM.yyyy HH:mm:ss");
	    Date resultdate = new Date(millisecs);
	    return date_format.format(resultdate);
	  }
}
