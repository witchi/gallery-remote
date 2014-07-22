package com.gallery.galleryremote.util;

import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.gallery.galleryremote.Log;

public class SmartHashtable extends HashMap<Object, Object> {
	private static final long serialVersionUID = -5521200540999583214L;
	ArrayList<Object> touchOrder = new ArrayList<Object>();

	@Override
	public Object put(Object key, Object value) {
		touch(key);
		super.put(key, value);

		// Log.log(Log.LEVEL_TRACE, MODULE,
		// Runtime.getRuntime().freeMemory() + " - " +
		// Runtime.getRuntime().totalMemory());
		if (cacheSize > 0 && touchOrder.size() > cacheSize) {
			shrink();
		}
		// Log.log(Log.LEVEL_TRACE, MODULE,
		// Runtime.getRuntime().freeMemory() + " - " +
		// Runtime.getRuntime().totalMemory());

		return value;
	}

	@Override
	public Object get(Object key) {
		return get(key, true);
	}

	public Object get(Object key, boolean touch) {
		Object result = super.get(key);

		if (result != null && touch) {
			touch(key);
		}

		return result;
	}

	@Override
	public void clear() {
		// Log.log(Log.LEVEL_TRACE, MODULE,
		// Runtime.getRuntime().freeMemory() + " - " +
		// Runtime.getRuntime().totalMemory());

		// flush images before clearing hastables for quicker deletion
		Iterator<Object> it = values().iterator();
		while (it.hasNext()) {
			Image i = (Image) it.next();
			if (i != null) {
				i.flush();
			}
		}

		super.clear();
		touchOrder.clear();

		System.runFinalization();
		System.gc();

		// Log.log(Log.LEVEL_TRACE, MODULE,
		// Runtime.getRuntime().freeMemory() + " - " +
		// Runtime.getRuntime().totalMemory());
	}

	public void touch(Object key) {
		Log.log(Log.LEVEL_TRACE, MODULE, "touch " + key);
		int i = touchOrder.indexOf(key);

		if (i != -1) {
			touchOrder.remove(i);
		}

		touchOrder.add(key);
	}

	public void shrink() {
		if (touchOrder.size() == 0) {
			Log.log(Log.LEVEL_ERROR, MODULE, "Empty SmartHashtable");
			return;
		}

		Object key = touchOrder.get(0);
		touchOrder.remove(0);

		Image i = (Image) get(key, false);
		if (i != null) {
			i.flush();
		}

		remove(key);

		Log.log(Log.LEVEL_TRACE, MODULE, "Shrunk " + key);
		if (cacheSize > 0 && size() > cacheSize) {
			shrink();
		} else {
			System.runFinalization();
			System.gc();
		}
	}
}
