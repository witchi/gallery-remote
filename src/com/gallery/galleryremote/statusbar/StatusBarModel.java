package com.gallery.galleryremote.statusbar;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class StatusBarModel {

	private HashMap<StatusLevel, StatusLevelData> data;
	private Map<StatusLevel, UndeterminedThread> threads;
	private StatusLevel currentLevel;

	public StatusBarModel() {
		initStatusLevel();
	}

	private void initStatusLevel() {
		data = new HashMap<StatusLevel, StatusLevelData>();
		threads = Collections.synchronizedMap(new HashMap<StatusLevel, UndeterminedThread>());

		for (StatusLevel l : StatusLevel.values()) {
			data.put(l, new StatusLevelData(l));
		}

		data.get(StatusLevel.GENERIC).setActive(true);
		currentLevel = StatusLevel.GENERIC;
	}

	boolean raiseLevel(StatusLevel level) {
		if (level.ordinal() < currentLevel.ordinal()) {
			return false;
		}
		if (level.ordinal() > currentLevel.ordinal()) {
			currentLevel = level;
		}
		return true;
	}

	StatusLevelData getCurrentLevelData() {
		return new StatusLevelData(data.get(currentLevel));
	}

	StatusLevel getCurrentStatusLevel() {
		return currentLevel;
	}

	StatusLevelData getStatusLevelData(StatusLevel level) {
		return new StatusLevelData(data.get(level));
	}

	void setStatusLevelData(StatusLevelData dto) {
		StatusLevelData sld = data.get(dto.getLevel());
		sld.setMinValue(dto.getMinValue());
		sld.setValue(dto.getValue());
		sld.setMaxValue(dto.getMaxValue());
		sld.setMessage(dto.getMessage());
		sld.setActive(dto.isActive());
		sld.setUndetermined(dto.isUndetermined());
	}

	synchronized void undetermineLevel(StatusLevel level, StatusUpdate listener) {
		if (threads.get(level) == null) {
			UndeterminedThread t = new UndeterminedThread(listener, level);
			threads.put(level, t);
			t.start();
		}
	}

	synchronized void determineLevel(StatusLevel level) {
		Thread t = threads.remove(level);
		if (t == null) {
			return;
		}
		t.interrupt();
	}

	StatusLevel getPrevStatusLevel(StatusLevel level) {
		StatusLevel sl = level;
		while (sl.ordinal() > StatusLevel.GENERIC.ordinal() && data.get(sl).isActive() == false) {
			sl = StatusLevel.values()[sl.ordinal() - 1];
		}
		return sl;
	}

	void setCurrentStatusLevel(StatusLevel level) {
		if (level == null) {
			this.currentLevel = StatusLevel.GENERIC;
		} else {
			this.currentLevel = level;
		}
	}
}
