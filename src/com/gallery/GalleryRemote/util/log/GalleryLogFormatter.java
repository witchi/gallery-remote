package com.gallery.GalleryRemote.util.log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class GalleryLogFormatter extends Formatter {

	@Override
	public String format(LogRecord record) {
		StringBuffer buf = new StringBuffer(1000);
		buf.append(calcDate(record.getMillis()));
		buf.append(" | ");
		buf.append(record.getLevel().getName());
		buf.append(" | ");
		buf.append(record.getSourceClassName());
		buf.append(" | ");
		buf.append(record.getMessage());
		return buf.toString();
	}
	
	private String calcDate(long millisecs) {
	    SimpleDateFormat date_format = new SimpleDateFormat("MMM dd,yyyy HH:mm");
	    Date resultdate = new Date(millisecs);
	    return date_format.format(resultdate);
	  }
}
