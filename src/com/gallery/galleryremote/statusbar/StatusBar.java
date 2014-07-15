package com.gallery.galleryremote.statusbar;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

import com.sun.istack.internal.logging.Logger;

/**
 * @author paour
 * @author arothe
 * 
 * @version Sep 17, 2003
 */
public class StatusBar extends JPanel {

	private static final long serialVersionUID = -3346018784723463138L;
	private static final Logger LOGGER = Logger.getLogger(StatusBar.class);

	private StatusProgress jProgressBar;
	private GridBagConstraints jProgressConstraints;
	private StatusLabel jStatusLabel;
	private GridBagConstraints jStatusLabelConstraints;
	private final int progressWidth;

	public StatusBar(int progressWidth) {
		LOGGER.fine("Creating class instance...");
		this.progressWidth = progressWidth;
		initUI();
	}

	public StatusBar() {
		this(150);
	}

	private void initUI() {
		setLayout(new GridBagLayout());
		add(getStatusLabel(), getStatusLabelConstraints());
		add(getProgressBar(), getProgressConstraints());
	}

	private GridBagConstraints getProgressConstraints() {
		if (jProgressConstraints == null) {
			jProgressConstraints = new GridBagConstraints(1, 0, 1, 1, 0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
					0, 1, 1, 5), 0, 0);
		}
		return jProgressConstraints;
	}

	private StatusProgress getProgressBar() {
		if (jProgressBar == null) {
			jProgressBar = new StatusProgress(progressWidth);
		}
		return jProgressBar;
	}

	private GridBagConstraints getStatusLabelConstraints() {
		if (jStatusLabelConstraints == null) {
			jStatusLabelConstraints = new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
					new Insets(0, 5, 1, 1), 0, 0);
		}
		return jStatusLabelConstraints;
	}

	private StatusLabel getStatusLabel() {
		if (jStatusLabel == null) {
			jStatusLabel = new StatusLabel();
		}
		return jStatusLabel;
	}

	void setIndeterminateProgress(boolean indeterminate) {
		getProgressBar().setIndeterminate(indeterminate);
	}

	boolean hasIndeterminateProgress() {
		try {
			getProgressBar().setIndeterminate(getProgressBar().isIndeterminate());
		} catch (Throwable t) {
			return false;
		}
		return true;
	}

	void resetUIState(StatusLevelData dto) {
		getProgressBar().setMinimum(dto.getMinValue());
		getProgressBar().setValue(dto.getValue());
		getProgressBar().setMaximum(dto.getMaxValue());
		getStatusLabel().setText(dto.getMessage());
		if (hasIndeterminateProgress()) {
			getProgressBar().setIndeterminate(dto.isUndetermined());
		}
	}

}
