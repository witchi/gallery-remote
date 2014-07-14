package com.gallery.GalleryRemote.albuminspector;

class AlbumInspectorDTO {

	private boolean hasAlbum;
	private String name;
	private String summary;
	private String title;
	private int numberOfPictures;
	private boolean enabled;
	private boolean selectedResizeBeforeUpload;
	private boolean selectedResizeToDefault;
	private boolean selectedResizeToForce;
	private boolean selectedAddBeginning;
	private boolean enabledFetch;
	private boolean enabledMove;
	private boolean enabledSlideShow;

	public AlbumInspectorDTO() {
		setHasAlbum(false);
		name = "";
		summary = "";
		title = "";
		numberOfPictures = 0;
		enabled = false;
		selectedResizeBeforeUpload = false;
		selectedResizeToDefault = false;
		selectedResizeToForce = false;
		selectedAddBeginning = false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getNumberOfPictures() {
		return numberOfPictures;
	}

	public void setNumberOfPictures(int numberOfPictures) {
		this.numberOfPictures = numberOfPictures;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isSelectedResizeToDefault() {
		return selectedResizeToDefault;
	}

	public void setSelectedResizeToDefault(boolean selected) {
		this.selectedResizeToDefault = selected;
	}

	public boolean isSelectedResizeToForce() {
		return selectedResizeToForce;
	}

	public void setSelectedResizeToForce(boolean selected) {
		this.selectedResizeToForce = selected;
	}

	public boolean isSelectedAddBeginning(boolean selected) {
		return this.selectedAddBeginning;
	}

	public void setSelectedAddBeginning(boolean selected) {
		this.selectedAddBeginning = selected;
	}

	public boolean isSelectedResizeBeforeUpload() {
		return selectedResizeBeforeUpload;
	}

	public void setSelectedResizeBeforeUpload(boolean selected) {
		this.selectedResizeBeforeUpload = selected;
	}

	public void setEnabledFetch(boolean enabled) {
		this.enabledFetch = enabled;
	}

	public void setEnabledMove(boolean enabled) {
		this.enabledMove = enabled;
	}

	public void setEnabledSlideshow(boolean enabled) {
		this.enabledSlideShow = enabled;
	}

	public boolean isEnabledSlideShow() {
		return enabledSlideShow;
	}

	public void setEnabledSlideShow(boolean enabledSlideShow) {
		this.enabledSlideShow = enabledSlideShow;
	}

	public boolean isEnabledFetch() {
		return enabledFetch;
	}

	public boolean isEnabledMove() {
		return enabledMove;
	}

	public boolean hasAlbum() {
		return hasAlbum;
	}

	public void setHasAlbum(boolean hasAlbum) {
		this.hasAlbum = hasAlbum;
	}
}
