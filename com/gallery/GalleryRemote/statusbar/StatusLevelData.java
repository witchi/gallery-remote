package com.gallery.GalleryRemote.statusbar;

class StatusLevelData {
	private StatusLevel level = StatusLevel.NONE;
	private boolean active = false;
	private String message = "";
	private int minValue = 0;
	private int maxValue = Integer.MAX_VALUE;
	private int value = 0;
	private boolean undetermined = false;

	StatusLevelData(StatusLevel level) {
		this.level = level;
	}

	StatusLevelData(StatusLevelData data) {
		this.level = data.getLevel();
		this.active = data.isActive();
		this.message = data.getMessage();
		this.minValue = data.getMinValue();
		this.maxValue = data.getMaxValue();
		this.value = data.getValue();
		this.undetermined = data.isUndetermined();
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getMinValue() {
		return minValue;
	}

	public void setMinValue(int minValue) {
		this.minValue = minValue;
	}

	public int getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public boolean isUndetermined() {
		return undetermined;
	}

	public void setUndetermined(boolean undetermined) {
		this.undetermined = undetermined;
	}

	public StatusLevel getLevel() {
		return level;
	}
}
