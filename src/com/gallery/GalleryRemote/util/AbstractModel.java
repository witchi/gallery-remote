package com.gallery.GalleryRemote.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractModel {

	private final List<ActionListener> listeners = new CopyOnWriteArrayList<ActionListener>();

	public void addActionListener(ActionListener l) {
		listeners.add(l);
	}

	public synchronized void removeActionListener(ActionListener l) {
		listeners.remove(l);
	}

	protected void notifyListeners(ActionEvent e) {
		for (ActionListener l : listeners) {
			l.actionPerformed(e); // perform the action
		}
	}

}
