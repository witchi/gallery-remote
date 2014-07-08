package com.gallery.GalleryRemote.statusbar;

import java.awt.Component;

import javax.swing.JOptionPane;

import com.gallery.GalleryRemote.GalleryRemote;
import com.gallery.GalleryRemote.Log;
import com.gallery.GalleryRemote.model.Picture;
import com.gallery.GalleryRemote.util.DialogUtil;
import com.gallery.GalleryRemote.util.GRI18n;
import com.gallery.GalleryRemote.util.log.Logger;

public class StatusBarPresenterImpl implements StatusBarPresenter {

	public static final String MODULE = "StatusBar";
	private static final Logger LOGGER = Logger.getLogger(StatusBar.class);

	private StatusBar view;
	private StatusBarModel model;

	public StatusBarPresenterImpl(StatusBarModel model, StatusBar view) {
		this.model = model;
		this.view = view;
	}

	private void resetUIState() {

		StatusLevelData data = model.getCurrentLevelData();

		LOGGER.fine("level: " + data.getLevel() + " - " + data.getMessage() + " - " + data.getValue());
		Log.log(Log.LEVEL_TRACE, MODULE, "level: " + data.getLevel() + " - " + data.getMessage() + " - " + data.getValue());

		try {
			view.setIndeterminateProgress(data.isUndetermined());
		} catch (Throwable t) {
			// we end up here if the method is not implemented and we don't
			// have indeterminate progress bars: come up with our own...
			if (data.isUndetermined()) {
				model.undetermineLevel(data.getLevel(), this);
			} else {
				model.determineLevel(data.getLevel());
			}
		}

		view.resetUIState(data);
	}

	@Override
	public void startProgress(StatusLevel level, int minValue, int maxValue, String message, boolean undetermined) {

		StatusLevelData dto = new StatusLevelData(level);
		dto.setMinValue(minValue);
		dto.setMaxValue(maxValue);
		dto.setValue(0);
		dto.setMessage(message);
		dto.setUndetermined(undetermined);
		dto.setActive(true);

		model.setStatusLevelData(dto);
		if (model.raiseLevel(level)) {
			resetUIState();
		}
	}

	@Override
	public void setStatus(String message) {
		updateProgressStatus(StatusLevel.GENERIC, message);
	}

	@Override
	public void updateProgressValue(StatusLevel level, int value) {
		StatusLevelData dto = model.getStatusLevelData(level);
		dto.setValue(value);

		if (level == model.getCurrentStatusLevel() && dto.isActive()) {
			resetUIState();
			return;
		}

		LOGGER.fine("Trying to use updateProgressValue when not progressOn or with wrong level");
		LOGGER.fine(Thread.currentThread().getStackTrace());

		// Log.log(Log.TRACE, MODULE,
		// "Trying to use updateProgressValue when not progressOn or with wrong level");
		// Log.logStack(Log.TRACE, MODULE);
	}

	@Override
	public void updateProgressValue(StatusLevel level, int value, int maxValue) {
		StatusLevelData dto = model.getStatusLevelData(level);
		dto.setValue(value);
		dto.setMaxValue(maxValue);

		if (level == model.getCurrentStatusLevel() && dto.isActive()) {
			resetUIState();
			return;
		}

		LOGGER.fine("Trying to use updateProgressValue when not progressOn or with wrong level");
		LOGGER.fine(Thread.currentThread().getStackTrace());

		// Log.log(Log.TRACE, MODULE,
		// "Trying to use updateProgressValue when not progressOn or with wrong level");
		// Log.logStack(Log.TRACE, MODULE);
	}

	@Override
	public void updateProgressStatus(StatusLevel level, String message) {
		StatusLevelData dto = model.getStatusLevelData(level);
		dto.setMessage(message);

		if (level == model.getCurrentStatusLevel() && dto.isActive()) {
			resetUIState();
			return;
		}

		LOGGER.fine("Trying to use updateProgressValue when not progressOn or with wrong level");
		LOGGER.fine(Thread.currentThread().getStackTrace());

		// Log.log(Log.TRACE, MODULE,
		// "Trying to use updateProgressValue when not progressOn or with wrong level");
		// Log.logStack(Log.TRACE, MODULE);
	}

	@Override
	public void setUndetermined(StatusLevel level, boolean undetermined) {
		// TODO: ??
	}

	@Override
	public int getProgressValue(StatusLevel level) {
		return model.getStatusLevelData(level).getValue();
	}

	@Override
	public int getProgressMinValue(StatusLevel level) {
		return model.getStatusLevelData(level).getMinValue();
	}

	@Override
	public int getProgressMaxValue(StatusLevel level) {
		return model.getStatusLevelData(level).getMaxValue();
	}

	@Override
	public void stopProgress(StatusLevel level, String message) {
		
		StatusLevelData dto = model.getStatusLevelData(StatusLevel.GENERIC);
		dto.setMessage(message);
		model.setStatusLevelData(dto);

		LOGGER.fine("stop level: " + dto.getLevel() + " - " + dto.getMessage() + " - " + dto.getValue());
		Log.log(Log.LEVEL_TRACE, MODULE, "level: " + dto.getLevel() + " - " + dto.getMessage() + " - " + dto.getValue());
		
		dto = model.getStatusLevelData(level);

		LOGGER.fine("stop level: " + dto.getLevel() + " - " + dto.getMessage() + " - " + dto.getValue());
		Log.log(Log.LEVEL_TRACE, MODULE, "level: " + dto.getLevel() + " - " + dto.getMessage() + " - " + dto.getValue());

		if (level == model.getCurrentStatusLevel() && dto.isActive()) {
			model.determineLevel(level);

			dto.setMinValue(0);
			dto.setMaxValue(0);
			dto.setValue(0);
			dto.setUndetermined(false);

			if (level.ordinal() > StatusLevel.GENERIC.ordinal()) {
				dto.setActive(false);
			}

			// find the previous active level
			model.setCurrentStatusLevel(model.getPrevStatusLevel(model.getCurrentStatusLevel()));
			resetUIState();
		}
	}

	@Override
	public void setInProgress(boolean inProgress) {
		GalleryRemote.instance().getCore().setInProgress(inProgress);
	}

	@Override
	public void error(String message) {
		JOptionPane.showMessageDialog(DialogUtil.findParentWindow(view), message, GRI18n.getString(MODULE, "Error"),
				JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void doneUploading(String newItemName, Picture picture) {
		// TODO: ??
	}

	@Override
	public Component getView() {
		return view;
	}

}
