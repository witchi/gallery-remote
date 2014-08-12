package com.gallery.galleryremote.progress;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.gallery.galleryremote.model.Picture;
import com.gallery.galleryremote.statusbar.StatusLevel;
import com.gallery.galleryremote.statusbar.StatusUpdate;

public class UploadProgressPresenterImpl implements UploadProgressPresenter, StatusUpdate, ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setStatus(String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startProgress(StatusLevel level, int min, int max, String message, boolean undetermined) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateProgressValue(StatusLevel level, int value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateProgressValue(StatusLevel level, int value, int maxValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateProgressStatus(StatusLevel level, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setUndetermined(StatusLevel level, boolean undetermined) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getProgressValue(StatusLevel level) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getProgressMinValue(StatusLevel level) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getProgressMaxValue(StatusLevel level) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void stopProgress(StatusLevel level, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setInProgress(boolean inProgress) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doneUploading(String newItemName, Picture picture) {
		// TODO Auto-generated method stub
		
	}

}
