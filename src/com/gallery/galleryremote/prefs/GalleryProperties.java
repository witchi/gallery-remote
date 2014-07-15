/*
 *  Gallery Remote - a File Upload Utility for Gallery
 *
 *  Gallery - a web based photo album viewer and editor
 *  Copyright (C) 2000-2001 Bharat Mediratta
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or (at
 *  your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.gallery.galleryremote.prefs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;

import com.gallery.galleryremote.util.Base64;
import com.gallery.galleryremote.util.log.Logger;

/**
 * GalleryProperties: access property data with a higher level of abstraction
 * 
 * @author paour
 * @author arothe
 */
public class GalleryProperties extends Properties implements PreferenceNames {

	private static final long serialVersionUID = 1347760811688657295L;
	private static final Logger LOGGER = Logger.getLogger(GalleryProperties.class);
	public static final String MODULE = "GalProps";

	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

	// caches
	protected Dimension thumbnailSize = null;
	protected Rectangle mainBounds = null;
	protected Rectangle previewBounds = null;

	// from Properties
	private static final String keyValueSeparators = "=: \t\r\n\f";
	private static final String strictKeyValueSeparators = "=:";
	private static final String whiteSpaceChars = " \t\r\n\f";

	public GalleryProperties(Properties p) {
		super(p);
	}

	public GalleryProperties(Map<String, String> p) {
		super();

		Iterator<String> names = p.keySet().iterator();
		while (names.hasNext()) {
			String name = names.next();
			super.setProperty(name, p.get(name));
		}
	}

	public GalleryProperties() {
	}

	@Override
	public String getProperty(String key) {
		return getProperty(key, false);
	}

	public String getProperty(String key, boolean emptySignificant) {
		Object oval = super.get(key);
		String sval = (oval instanceof String) ? (String) oval : null;
		String value = ((sval == null || (sval.length() == 0 && !emptySignificant)) && (defaults != null)) ? defaults.getProperty(key) : sval;

		if (value == null || (value.length() == 0 && !emptySignificant)) {
			return null;
		}
		return value;
	}

	public void copyProperties(Properties source) {
		Enumeration<?> names = source.propertyNames();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			String value = source.getProperty(name);
			String currentValue = getProperty(name);

			// don't override existing property with empty one
			if ((currentValue == null || currentValue.length() == 0) || (value != null && value.length() != 0)) {
				super.setProperty(name, value);
			}
		}
	}

	/* Adapted from Properties to support UTF-8 */
	public void load(String data) throws IOException {
		BufferedReader in = new BufferedReader(new StringReader(data));
		while (true) {
			// Get next line
			String line = in.readLine();
			if (line == null)
				return;

			if (line.length() > 0) {
				// Continue lines that end in slashes if they are not comments
				char firstChar = line.charAt(0);
				if ((firstChar != '#') && (firstChar != '!')) {
					// Find start of key
					int len = line.length();
					int keyStart;
					for (keyStart = 0; keyStart < len; keyStart++) {
						if (whiteSpaceChars.indexOf(line.charAt(keyStart)) == -1)
							break;
					}

					// Blank lines are ignored
					if (keyStart == len)
						continue;

					// Find separation between key and value
					int separatorIndex;
					for (separatorIndex = keyStart; separatorIndex < len; separatorIndex++) {
						char currentChar = line.charAt(separatorIndex);
						if (currentChar == '\\')
							separatorIndex++;
						else if (keyValueSeparators.indexOf(currentChar) != -1)
							break;
					}

					// Skip over whitespace after key if any
					int valueIndex;
					for (valueIndex = separatorIndex; valueIndex < len; valueIndex++)
						if (whiteSpaceChars.indexOf(line.charAt(valueIndex)) == -1)
							break;

					// Skip over one non whitespace key value separators if any
					if (valueIndex < len)
						if (strictKeyValueSeparators.indexOf(line.charAt(valueIndex)) != -1)
							valueIndex++;

					// Skip over white space after other separators if any
					while (valueIndex < len) {
						if (whiteSpaceChars.indexOf(line.charAt(valueIndex)) == -1)
							break;
						valueIndex++;
					}
					String key = line.substring(keyStart, separatorIndex);
					String value = (separatorIndex < len) ? line.substring(valueIndex, len) : "";

					// Convert then store key and value
					put(loadConvert(key), loadConvert(value));
				}
			}
		}
	}

	public String loadConvert(String theString) {
		char aChar;
		int len = theString.length();
		StringBuffer outBuffer = new StringBuffer(len);

		for (int x = 0; x < len;) {
			aChar = theString.charAt(x++);
			if (aChar == '\\') {
				aChar = theString.charAt(x++);
				if (aChar == 'u') {
					// Read the xxxx
					int value = 0;
					for (int i = 0; i < 4; i++) {
						aChar = theString.charAt(x++);
						switch (aChar) {
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							value = (value << 4) + aChar - '0';
							break;
						case 'a':
						case 'b':
						case 'c':
						case 'd':
						case 'e':
						case 'f':
							value = (value << 4) + 10 + aChar - 'a';
							break;
						case 'A':
						case 'B':
						case 'C':
						case 'D':
						case 'E':
						case 'F':
							value = (value << 4) + 10 + aChar - 'A';
							break;
						default:
							throw new IllegalArgumentException("Malformed \\uxxxx encoding.");
						}
					}
					outBuffer.append((char) value);
				} else {
					if (aChar == 't')
						aChar = '\t';
					else if (aChar == 'r')
						aChar = '\r';
					else if (aChar == 'n')
						aChar = '\n';
					else if (aChar == 'f')
						aChar = '\f';
					outBuffer.append(aChar);
				}
			} else
				outBuffer.append(aChar);
		}
		return outBuffer.toString();
	}

	public File getCurrentDirectory() {
		String currentDirectory = getProperty("filedialogPath");
		if (currentDirectory != null) {
			return new File(currentDirectory);
		}
		return null;
	}

	public void setCurrentDirectory(File currentDirectory) {
		setProperty("filedialogPath", currentDirectory.getPath());
	}

	public boolean getShowPreview() {
		return getBooleanProperty("showPreview");
	}

	public void setShowPreview(boolean showPreview) {
		setProperty("showPreview", String.valueOf(showPreview));
	}

	public boolean getShowPath() {
		return getBooleanProperty("showPath");
	}

	public void setShowPath(boolean showPath) {
		setProperty("showPath", String.valueOf(showPath));
	}

	public boolean getShowThumbnails() {
		return getBooleanProperty(SHOW_THUMBNAILS);
	}

	public void setShowThumbnails(boolean showThumbnails) {
		setProperty(SHOW_THUMBNAILS, String.valueOf(showThumbnails));
	}

	public Dimension getThumbnailSize() {
		if (thumbnailSize == null) {
			thumbnailSize = getDimensionProperty(THUMBNAIL_SIZE);
		}

		return thumbnailSize;
	}

	public Rectangle getMainBounds() {
		if (mainBounds == null) {
			mainBounds = getRectangleProperty("mainBounds");
		}

		return mainBounds;
	}

	public Rectangle getPreviewBounds() {
		if (previewBounds == null) {
			previewBounds = getRectangleProperty("previewBounds");
		}

		return previewBounds;
	}

	public void setMainBounds(Rectangle r) {
		setRectangleProperty("mainBounds", r);
	}

	public void setPreviewBounds(Rectangle r) {
		setRectangleProperty("previewBounds", r);
	}

	public void setThumbnailSize(Dimension size) {
		thumbnailSize = size;
		setDimensionProperty(THUMBNAIL_SIZE, size);
	}

	public int getAutoCaptions() {
		// first, clean up legacy preference names
		if (getBooleanProperty("setCaptionsWithMetadataComment", false)) {
			setAutoCaptions(AUTO_CAPTIONS_COMMENT);
			remove("setCaptionsWithMetadataComment");
		}
		if (getBooleanProperty("setCaptionsWithFilenames", false)) {
			setAutoCaptions(AUTO_CAPTIONS_FILENAME);
			remove("setCaptionsWithFilenames");
		}
		if (getBooleanProperty("setCaptionsNone", false)) {
			setAutoCaptions(AUTO_CAPTIONS_NONE);
			remove("setCaptionsNone");
		}

		return getIntProperty(AUTO_CAPTIONS, AUTO_CAPTIONS_FILENAME);
	}

	public void setAutoCaptions(int mode) {
		setIntProperty(AUTO_CAPTIONS, mode);
	}

	public Dimension getDimensionProperty(String key) {
		String value = getProperty(key);
		if (value == null)
			return null;

		StringTokenizer st;
		if ((st = new StringTokenizer(value, ",")).countTokens() == 2) {
			return new Dimension(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
		}
		LOGGER.warning("Parameter " + key + " = " + value + " is missing or malformed (should be width,height)");
		// Log.log(Log.LEVEL_ERROR, MODULE, "Parameter " + key + " = " + value +
		// " is missing or malformed (should be width,height)");
		return null;
	}

	public void setDimensionProperty(String key, Dimension d) {
		setProperty(key, ((int) d.getWidth()) + "," + ((int) d.getHeight()));
	}

	public int getIntDimensionProperty(String key) {
		int i = getIntProperty(key + "1", -1);

		if (i == -1) {
			Dimension d = getDimensionProperty(key);
			if (d != null) {
				i = d.width;
			} else {
				i = 0;
			}

			setIntDimensionProperty(key, i);
		}

		return i;
	}

	public void setIntDimensionProperty(String key, int i) {
		setProperty(key + "1", String.valueOf(i));
	}

	public Color getColorProperty(String key) {
		String value = getProperty(key);
		if (value == null)
			return null;

		StringTokenizer st;
		if ((st = new StringTokenizer(value, ",")).countTokens() == 3) {
			return new Color(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
		}
		// Log.log(Log.LEVEL_ERROR, MODULE, "Parameter " + key + " = " + value +
		// " is missing or malformed (should be red,green,blue)");
		LOGGER.warning("Parameter " + key + " = " + value + " is missing or malformed (should be red,green,blue)");
		return null;
	}

	public void setColorProperty(String key, Color c) {
		setProperty(key, c.getRed() + "," + c.getGreen() + "," + c.getBlue());
	}

	public Rectangle getRectangleProperty(String key) {
		String value = getProperty(key);
		if (value == null)
			return null;

		StringTokenizer st;
		if ((st = new StringTokenizer(value, ",")).countTokens() == 4) {
			return new Rectangle(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()),
					Integer.parseInt(st.nextToken()));
		}
		// Log.log(Log.LEVEL_ERROR, MODULE, "Parameter " + key +
		// " is missing or malformed (should be x,y,width,height)");
		LOGGER.warning("Parameter " + key + " is missing or malformed (should be x,y,width,height)");
		return null;
	}

	public void setRectangleProperty(String key, Rectangle rect) {
		setProperty(key, ((int) rect.getX()) + "," + ((int) rect.getY()) + "," + ((int) rect.getWidth()) + "," + ((int) rect.getHeight()));
	}

	public boolean getBooleanProperty(String key) {
		String booleanS = getProperty(key);

		if (booleanS != null) {
			if (booleanS.equalsIgnoreCase("yes") || booleanS.equalsIgnoreCase("true")) {
				return true;
			} else if (booleanS.equalsIgnoreCase("no") || booleanS.equalsIgnoreCase("false")) {
				return false;
			}
		}

		throw new NumberFormatException("Parameter " + key + " = " + booleanS + " is missing or malformed (should be true/yes or false/no)");
	}

	public boolean getBooleanProperty(String key, boolean defaultValue) {
		try {
			return getBooleanProperty(key);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public void setBooleanProperty(String key, boolean value) {
		setProperty(key, value ? "true" : "false");
	}

	public int getIntProperty(String key) {
		String intS = getProperty(key);
		try {
			return Integer.valueOf(intS).intValue();
		} catch (Exception e) {
			throw new NumberFormatException("Parameter " + key + " = " + intS + " is missing or malformed (should be an integer value)");
		}
	}

	public int getIntProperty(String key, int defaultValue) {
		try {
			return getIntProperty(key);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public void setIntProperty(String key, int value) {
		setProperty(key, String.valueOf(value));
	}

	public String getBase64Property(String key) {
		String base64S = getProperty(key);
		if (base64S == null)
			return null;

		try {
			return Base64.decode(base64S);
		} catch (Error e) {
			throw new NumberFormatException("Parameter " + key + " = " + base64S + " is missing or malformed (should be a Base64 value)");
		}
	}

	public void setBase64Property(String key, String value) {
		setProperty(key, Base64.encode(value));
	}

	public Date getDateProperty(String key) {
		String dateS = getProperty(key);
		if (dateS == null)
			return null;

		try {
			return dateFormat.parse(dateS);
		} catch (ParseException e) {
			throw new NumberFormatException("Parameter " + key + " = " + dateS
					+ " is missing or malformed (should be a Date value (yyyy/mm/dd))");
		}
	}

	public void setDateProperty(String key, Date date) {
		setProperty(key, dateFormat.format(date));
	}

	@Override
	public String getProperty(String key, String defaultValue) {
		String tmp = getProperty(key);

		if (tmp == null) {
			return defaultValue;
		}
		return tmp;
	}

	public void logProperties(Level level, String module) {
		if (module == null) {
			module = MODULE;
		}

		ArrayList<String> names = new ArrayList<String>(100);
		Enumeration<?> e = propertyNames();
		while (e.hasMoreElements()) {
			names.add((String) e.nextElement());
		}

		Object[] namesArray = names.toArray();
		Arrays.sort(namesArray);

		for (int i = 0; i < namesArray.length; i++) {
			String name = (String) namesArray[i];
			LOGGER.log(level, logPropertiesHelper(name));
			// Log.log(level, module, logPropertiesHelper(name));
		}
	}

	public String logPropertiesHelper(String name) {
		return name + "= |" + getProperty(name) + "|";
	}

	public void uncache() {
		thumbnailSize = null;
		mainBounds = null;
		previewBounds = null;
	}
}
