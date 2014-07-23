package com.gallery.galleryremote.util;

import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.gallery.galleryremote.util.log.Logger;

public class SmartHashtable extends HashMap<Object, Object> {

	private static final Logger LOGGER = Logger.getLogger(SmartHashtable.class);
	private static final long serialVersionUID = -5521200540999583214L;

	private final ArrayList<Object> touchOrder;
	private int cacheSize;

	public SmartHashtable(int cacheSize) {
		this.touchOrder = new ArrayList<Object>();
		this.cacheSize = cacheSize;
	}

	@Override
	public Object put(Object key, Object value) {
		touch(key);
		super.put(key, value);

		LOGGER.fine(Runtime.getRuntime().freeMemory() + " - " + Runtime.getRuntime().totalMemory());
		if (cacheSize > 0 && touchOrder.size() > cacheSize) {
			shrink();
		}
		LOGGER.fine(Runtime.getRuntime().freeMemory() + " - " + Runtime.getRuntime().totalMemory());
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
		LOGGER.fine(Runtime.getRuntime().freeMemory() + " - " + Runtime.getRuntime().totalMemory());

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

		LOGGER.fine(Runtime.getRuntime().freeMemory() + " - " + Runtime.getRuntime().totalMemory());
	}

	public void touch(Object key) {
		LOGGER.fine("touch " + key);
		int i = touchOrder.indexOf(key);

		if (i != -1) {
			touchOrder.remove(i);
		}

		touchOrder.add(key);
	}

	private void shrink() {
		if (touchOrder.size() == 0) {
			LOGGER.fine("Empty SmartHashtable");
			return;
		}

		Object key = touchOrder.remove(0);

		Image i = (Image) get(key, false);
		if (i != null) {
			i.flush();
		}

		remove(key);

		LOGGER.fine("Shrunk " + key);
		if (cacheSize > 0 && size() > cacheSize) {
			shrink();
		} else {
			System.runFinalization();
			System.gc();
		}
	}

	public void shrink(int cacheSize) {
		this.cacheSize = cacheSize;
		shrink();
	}
}
