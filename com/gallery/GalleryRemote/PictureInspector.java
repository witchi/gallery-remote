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
package com.gallery.GalleryRemote;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.gallery.GalleryRemote.model.Album;
import com.gallery.GalleryRemote.model.Picture;
import com.gallery.GalleryRemote.prictureinspect.CaptionPanel;
import com.gallery.GalleryRemote.prictureinspect.IconAreaPanel;
import com.gallery.GalleryRemote.prictureinspect.PathPanel;
import com.gallery.GalleryRemote.util.GRI18n;
import com.gallery.GalleryRemote.util.ImageUtils;

/**
 * Bean inspector for Pictures (the right column in the main frame)
 * 
 * @author paour
 * @author arothe
 */
public class PictureInspector extends JPanel implements ActionListener, DocumentListener {

	private static final long serialVersionUID = -5594312109149362431L;
	private static final String MODULE = "PictInspec";
	private static final int FIRST_ROW_EXTRA = 8;

	HashMap<String, JLabel> extraLabels = new HashMap<String, JLabel>();
	HashMap<String, JTextArea> extraTextAreas = new HashMap<String, JTextArea>();
	ArrayList<String> currentExtraFields = null;
	List<Picture> pictures = null;
	MainFrame mf = null;


	JLabel jLabel5;
	GridBagConstraints jLabel5Constraints;

	JLabel jLabel6;
	GridBagConstraints jLabel6Constraints;

	JLabel jLabel4;
	GridBagConstraints jLabel4Constraints;

	JLabel jLabel8;
	GridBagConstraints jLabel8Constraints;

	JLabel jLabel1;
	GridBagConstraints jLabel1Constraints;

	JLabel jLabel2;
	GridBagConstraints jLabel2Constraints;

	JPanel jSpacer;
	GridBagConstraints jSpacerConstraints;

	JButton jDeleteButton;
	GridBagConstraints jDeleteButtonConstraints;

	JButton jUpButton;
	GridBagConstraints jUpButtonConstraints;

	JButton jDownButton;
	GridBagConstraints jDownButtonConstraints;

	IconAreaPanel jIconAreaPanel;
	GridBagConstraints jIconAreaPanelConstraints;

	JTextArea jAlbum;
	GridBagConstraints jAlbumConstraints;

	JTextArea jSize;
	GridBagConstraints jSizeConstraints;

	PathPanel jPathPanel;
	GridBagConstraints jPathPanelConstraints;

	CaptionPanel jCaptionPanel;
	GridBagConstraints jCaptionPanelConstraints;

	/**
	 * Constructor for the PictureInspector object
	 */
	public PictureInspector() {
		initUI();
		initEvents();
		Log.log(Log.LEVEL_TRACE, MODULE, "emptyIconHeight: " + getIconAreaPanel().getEmptyIconHeight());
	}

	private void initUI() {
		setLayout(new GridBagLayout());

		add(getPathLabel(), getPathLabelConstraints());
		add(getAlbumLabel(), getAlbumLabelConstraints());
		add(getCaptionLabel(), getCaptionLabelConstraints());
		add(getMoveLabel(), getMoveLabelConstraints());
		add(getSizeLabel(), getSizeLabelConstraints());
		add(getDeleteLabel(), getDeleteLabelConstraints());
		add(getSpacer(), getSpacerConstraints());
		add(getIconAreaPanel(), getIconAreaPanelConstraints());
		add(getAlbumTextArea(), getAlbumTextAreaConstraints());
		add(getSizeTextArea(), getSizeTextAreaConstraints());
		add(getUpButton(), getUpButtonConstraints());
		add(getDownButton(), getDownButtonConstraints());
		add(getDeleteButton(), getDeleteButtonConstraints());
		add(getPathPanel(), getPathPanelConstraints());
		add(getCaptionPanel(), getCaptionPanelConstraints());

		setupKeyboardHandling(getCaption());
		this.setMinimumSize(new Dimension(150, 0));
	}

	private void initEvents() {
		jDeleteButton.addActionListener(this);
		jUpButton.addActionListener(this);
		jDownButton.addActionListener(this);
		getRotateLeftButton().addActionListener(this);
		getRotateRightButton().addActionListener(this);
		getFlipButton().addActionListener(this);
		getCaption().getDocument().addDocumentListener(this);
	}

	private void setupKeyboardHandling(JComponent c) {
		c.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), nextFocusAction.getValue(Action.NAME));
		c.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_MASK), prevFocusAction.getValue(Action.NAME));
		c.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), nextPictureAction.getValue(Action.NAME));
		c.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), prevPictureAction.getValue(Action.NAME));
		c.getActionMap().put(nextFocusAction.getValue(Action.NAME), nextFocusAction);
		c.getActionMap().put(prevFocusAction.getValue(Action.NAME), prevFocusAction);
		c.getActionMap().put(nextPictureAction.getValue(Action.NAME), nextPictureAction);
		c.getActionMap().put(prevPictureAction.getValue(Action.NAME), prevPictureAction);
	}

	// Event handling
	/**
	 * Menu and button handling
	 * 
	 * @param e
	 *           Action event
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		Log.log(Log.LEVEL_INFO, MODULE, "Command selected " + command);

		if (command.equals("Delete")) {
			// We must call the MainFrame to delete pictures
			// so that it can know that the document is now dirty.
			mf.deleteSelectedPictures();
		} else if (command.equals("Up")) {
			// We must call the MainFrame to move pictures
			// so that it can know that the document is now dirty.
			mf.movePicturesUp();
		} else if (command.equals("Down")) {
			// We must call the MainFrame to move pictures
			// so that it can know that the document is now dirty.
			mf.movePicturesDown();
		} else if (command.equals("Left")) {
			for (Picture p : pictures) {
				p.rotateLeft();
			}
			setPictures(pictures);
			mf.repaint();
			mf.previewFrame.repaint();
		} else if (command.equals("Right")) {
			for (Picture p : pictures) {
				p.rotateRight();
			}
			setPictures(pictures);
			mf.repaint();
			mf.previewFrame.repaint();
		} else if (command.equals("Flip")) {
			for (Picture p : pictures) {
				p.flip();
			}
			setPictures(pictures);
			mf.repaint();
			mf.previewFrame.repaint();
		}
	}

	/**
	 * Caption JTextArea events.
	 */
	@Override
	public void insertUpdate(DocumentEvent e) {
		textUpdate(e);
	}

	/**
	 * Caption JTextArea events.
	 */
	@Override
	public void removeUpdate(DocumentEvent e) {
		textUpdate(e);
	}

	/**
	 * Caption JTextArea events.
	 */
	@Override
	public void changedUpdate(DocumentEvent e) {
		textUpdate(e);
	}

	public void textUpdate(DocumentEvent e) {
		if (pictures != null && pictures.size() == 1) {
			Picture p = pictures.get(0);

			if (e.getDocument() == getCaption().getDocument()) {
				p.setCaption(getCaption().getText());
			}

			Iterator<String> it = extraTextAreas.keySet().iterator();
			while (it.hasNext()) {
				String name = it.next();
				JTextArea field = extraTextAreas.get(name);

				if (e.getDocument() == field.getDocument()) {
					String text = field.getText();
					if (text.length() == 0) {
						p.removeExtraField(name);
					} else {
						p.setExtraField(name, text);
					}

					break;
				}
			}
		}
	}

	/**
	 * Sets the mainFrame attribute of the PictureInspector object
	 * 
	 * @param mf
	 *           The new mainFrame value
	 */
	public void setMainFrame(MainFrame mf) {
		this.mf = mf;
		replaceIcon(getIcon(), ImageUtils.defaultThumbnail);
	}

	/**
	 * Sets the picture attribute of the PictureInspector object
	 * 
	 * @param pictures
	 *           The new picture value
	 */
	public void setPictures(java.util.List<Picture> pictures) {
		// Log.log(Log.TRACE, MODULE, "setPictures " + pictures);
		// Log.logStack(Log.TRACE, MODULE);
		this.pictures = pictures;

		getIcon().setPreferredSize(
				new Dimension(0, GalleryRemote.instance().properties.getThumbnailSize().height + getIconAreaPanel().getEmptyIconHeight()
						+ getIcon().getIconTextGap()));

		if (pictures == null || pictures.isEmpty()) {
			getIcon().setText(GRI18n.getString(MODULE, "noPicSel"));
			replaceIcon(getIcon(), ImageUtils.defaultThumbnail);
			getPath().setText("");
			jAlbum.setText("");

			getCaption().setText("");
			getCaption().setEditable(false);
			getCaption().setBackground(UIManager.getColor("TextField.inactiveBackground"));

			jSize.setText("");

			jUpButton.setEnabled(false);
			jDownButton.setEnabled(false);
			jDeleteButton.setEnabled(false);
			getRotateLeftButton().setEnabled(false);
			getRotateRightButton().setEnabled(false);
			getFlipButton().setEnabled(false);

			removeExtraFields();
		} else if (pictures.size() == 1) {
			Picture p = pictures.get(0);

			replaceIcon(getIcon(), mf.getThumbnail(p));
			if (p.isOnline()) {
				getPath().setText(GRI18n.getString(MODULE, "onServer"));
				getIcon().setText(p.getName());
			} else {
				getIcon().setText(p.getSource().getName());
				getPath().setText(p.getSource().getParent());
			}
			jAlbum.setText(p.getParentAlbum().getTitle());
			if (p.getParentAlbum().getGallery().getComm(mf.jStatusBar)
					.hasCapability(mf.jStatusBar, GalleryCommCapabilities.CAPA_UPLOAD_CAPTION)) {
				getCaption().setText(p.getCaption());
				getCaption().setEditable(true);
				getCaption().setBackground(UIManager.getColor("TextField.background"));
			}
			jSize.setText(NumberFormat.getInstance().format((int) p.getFileSize()) + " bytes");

			jUpButton.setEnabled(isEnabled());
			jDownButton.setEnabled(isEnabled());
			jDeleteButton.setEnabled(isEnabled());
			getRotateLeftButton().setEnabled(isEnabled());
			getRotateRightButton().setEnabled(isEnabled());
			getFlipButton().setEnabled(isEnabled());

			addExtraFields(p);
		} else {
			Picture p = pictures.get(0);

			Object[] params = { new Integer(pictures.size()) };
			getIcon().setText(GRI18n.getString(MODULE, "countElemSel", params));
			replaceIcon(getIcon(), ImageUtils.defaultThumbnail);
			getPath().setText("");
			jAlbum.setText(p.getParentAlbum().getTitle());
			getCaption().setText("");
			getCaption().setEditable(false);
			getCaption().setBackground(UIManager.getColor("TextField.inactiveBackground"));
			jSize.setText(NumberFormat.getInstance().format(Album.getObjectFileSize(pictures)) + " bytes");

			jUpButton.setEnabled(isEnabled());
			jDownButton.setEnabled(isEnabled());
			jDeleteButton.setEnabled(isEnabled());
			getRotateLeftButton().setEnabled(isEnabled());
			getRotateRightButton().setEnabled(isEnabled());
			getFlipButton().setEnabled(isEnabled());

			removeExtraFields();
		}
	}

	void addExtraFields(Picture p) {
		ArrayList<String> newExtraFields = p.getParentAlbum().getExtraFields();

		if (newExtraFields == null) {
			removeExtraFields();
		} else {
			if (!newExtraFields.equals(currentExtraFields)) {
				removeExtraFields();

				int i = 0;
				Iterator<String> it = newExtraFields.iterator();
				while (it.hasNext()) {
					String name = it.next();
					// String value = p.getExtraField(name);

					JLabel label = new JLabel(name);
					extraLabels.put(name, label);
					add(label, new GridBagConstraints(0, FIRST_ROW_EXTRA + i, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST,
							GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 2, 0));

					JTextArea field = new JTextArea();
					extraTextAreas.put(name, field);
					field.setFont(UIManager.getFont("Label.font"));
					field.setLineWrap(true);
					field.setWrapStyleWord(true);
					add(field, new GridBagConstraints(1, FIRST_ROW_EXTRA + i, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
							GridBagConstraints.BOTH, new Insets(5, 5, 0, 5), 0, 0));
					field.getDocument().addDocumentListener(this);
					setupKeyboardHandling(field);

					i++;
				}

				currentExtraFields = newExtraFields;
			}

			Iterator<String> it = newExtraFields.iterator();
			while (it.hasNext()) {
				String name = it.next();
				String value = p.getExtraField(name);
				JTextArea field = extraTextAreas.get(name);
				if (value == null) {
					field.setText("");
				} else {
					field.setText(value);
				}
			}
		}
	}

	void removeExtraFields() {
		Iterator<JLabel> it = extraLabels.values().iterator();
		while (it.hasNext()) {
			JLabel label = it.next();
			remove(label);
		}

		Iterator<JTextArea> i = extraTextAreas.values().iterator();
		while (i.hasNext()) {
			JTextArea textArea = i.next();
			remove(textArea);
		}

		extraLabels.clear();
		extraTextAreas.clear();

		currentExtraFields = null;
	}

	@Override
	public void setEnabled(boolean enabled) {
		// Log.log(Log.TRACE, MODULE, "setEnabled " + enabled);
		getIcon().setEnabled(enabled);
		jUpButton.setEnabled(enabled);
		jDownButton.setEnabled(enabled);
		jDeleteButton.setEnabled(enabled);
		getRotateLeftButton().setEnabled(enabled);
		getRotateRightButton().setEnabled(enabled);
		getFlipButton().setEnabled(enabled);
		getCaption().setEnabled(enabled);

		super.setEnabled(enabled);
	}

	// Focus traversal actions
	public Action nextFocusAction = new AbstractAction("Move Focus Forwards") {
		private static final long serialVersionUID = -7481079099741609449L;

		@Override
		public void actionPerformed(ActionEvent evt) {
			((Component) evt.getSource()).transferFocus();
		}
	};

	public Action prevFocusAction = new AbstractAction("Move Focus Backwards") {
		private static final long serialVersionUID = -4207478878462672331L;

		@Override
		public void actionPerformed(ActionEvent evt) {
			((Component) evt.getSource()).transferFocusBackward();
		}
	};

	public Action nextPictureAction = new AbstractAction("Select Next Picture") {
		private static final long serialVersionUID = -3058236737204149574L;

		@Override
		public void actionPerformed(ActionEvent evt) {
			CoreUtils.selectNextPicture();
		}
	};

	public Action prevPictureAction = new AbstractAction("Select Prev Picture") {
		private static final long serialVersionUID = 5771498665345070313L;

		@Override
		public void actionPerformed(ActionEvent evt) {
			CoreUtils.selectPrevPicture();
		}
	};

	public void replaceIcon(JLabel label, Image icon) {
		Icon i = label.getIcon();

		if (i == null || !(i instanceof ImageIcon)) {
			i = new ImageIcon();
			label.setIcon(i);
		}

		((ImageIcon) i).setImage(icon);
	}

	private JLabel getPathLabel() {
		if (jLabel5 == null) {
			jLabel5 = new JLabel();
			jLabel5.setText(GRI18n.getString(MODULE, "Path"));
		}
		return jLabel5;
	}

	private GridBagConstraints getPathLabelConstraints() {
		if (jLabel5Constraints == null) {
			jLabel5Constraints = new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
					new Insets(0, 0, 0, 0), 2, 0);
		}
		return jLabel5Constraints;
	}

	private JLabel getAlbumLabel() {
		if (jLabel6 == null) {
			jLabel6 = new JLabel();
			jLabel6.setText(GRI18n.getString(MODULE, "Album"));
		}
		return jLabel6;
	}

	private GridBagConstraints getAlbumLabelConstraints() {
		if (jLabel6Constraints == null) {
			jLabel6Constraints = new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,
					0, 0, 0), 2, 0);
		}
		return jLabel6Constraints;
	}

	private JLabel getCaptionLabel() {
		if (jLabel4 == null) {
			jLabel4 = new JLabel();
			jLabel4.setText(GRI18n.getString(MODULE, "Caption"));
		}
		return jLabel4;
	}

	private GridBagConstraints getCaptionLabelConstraints() {
		if (jLabel4Constraints == null) {
			jLabel4Constraints = new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
					new Insets(5, 0, 0, 0), 2, 0);
		}
		return jLabel4Constraints;
	}

	private JLabel getMoveLabel() {
		if (jLabel8 == null) {
			jLabel8 = new JLabel();
			jLabel8.setText(GRI18n.getString(MODULE, "Move"));
		}
		return jLabel8;
	}

	private GridBagConstraints getMoveLabelConstraints() {
		if (jLabel8Constraints == null) {
			jLabel8Constraints = new GridBagConstraints(0, 4, 1, 2, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,
					0, 0, 0), 2, 0);
		}
		return jLabel8Constraints;
	}

	private JLabel getSizeLabel() {
		if (jLabel1 == null) {
			jLabel1 = new JLabel();
			jLabel1.setText(GRI18n.getString(MODULE, "Size"));
		}
		return jLabel1;
	}

	private GridBagConstraints getSizeLabelConstraints() {
		if (jLabel1Constraints == null) {
			jLabel1Constraints = new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,
					0, 0, 0), 2, 0);
		}
		return jLabel1Constraints;
	}

	private JLabel getDeleteLabel() {
		if (jLabel2 == null) {
			jLabel2 = new JLabel();
			jLabel2.setText(GRI18n.getString(MODULE, "Delete"));
		}
		return jLabel2;
	}

	private GridBagConstraints getDeleteLabelConstraints() {
		if (jLabel2Constraints == null) {
			jLabel2Constraints = new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,
					0, 0, 0), 2, 0);
		}
		return jLabel2Constraints;
	}

	private JPanel getSpacer() {
		if (jSpacer == null) {
			jSpacer = new JPanel();
		}
		return jSpacer;
	}

	private GridBagConstraints getSpacerConstraints() {
		if (jSpacerConstraints == null) {
			jSpacerConstraints = new GridBagConstraints(0, 99, 2, 1, 1.0, 0.1, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
					new Insets(0, 0, 0, 0), 0, 0);
		}
		return jSpacerConstraints;
	}

	private IconAreaPanel getIconAreaPanel() {
		if (jIconAreaPanel == null) {
			jIconAreaPanel = new IconAreaPanel();
		}
		return jIconAreaPanel;
	}

	private GridBagConstraints getIconAreaPanelConstraints() {
		if (jIconAreaPanelConstraints == null) {
			jIconAreaPanelConstraints = new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
					new Insets(5, 5, 5, 5), 0, 0);
		}
		return jIconAreaPanelConstraints;
	}

	private JTextArea getAlbumTextArea() {
		if (jAlbum == null) {
			jAlbum = new JTextArea();
			jAlbum.setRows(0);
			jAlbum.setEditable(false);
			jAlbum.setFont(UIManager.getFont("Label.font"));
			jAlbum.setBackground(UIManager.getColor("TextField.inactiveBackground"));
		}
		return jAlbum;
	}

	private GridBagConstraints getAlbumTextAreaConstraints() {
		if (jAlbumConstraints == null) {
			jAlbumConstraints = new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
					new Insets(0, 5, 0, 5), 0, 0);
		}
		return jAlbumConstraints;
	}

	private JTextArea getSizeTextArea() {
		if (jSize == null) {
			jSize = new JTextArea();
			jSize.setRows(0);
			jSize.setEditable(false);
			jSize.setFont(UIManager.getFont("Label.font"));
			jSize.setBackground(UIManager.getColor("TextField.inactiveBackground"));
		}
		return jSize;
	}

	private GridBagConstraints getSizeTextAreaConstraints() {
		if (jSizeConstraints == null) {
			jSizeConstraints = new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
					new Insets(0, 5, 0, 5), 0, 0);
		}
		return jSizeConstraints;
	}

	private JButton getUpButton() {
		if (jUpButton == null) {
			jUpButton = new JButton();
			jUpButton.setMaximumSize(new Dimension(120, 23));
			jUpButton.setMinimumSize(new Dimension(120, 23));
			jUpButton.setPreferredSize(new Dimension(120, 23));
			jUpButton.setToolTipText(GRI18n.getString(MODULE, "upBtnTip"));
			jUpButton.setText(GRI18n.getString(MODULE, "upBtn"));
			jUpButton.setActionCommand("Up");
			jUpButton.setHorizontalAlignment(SwingConstants.LEFT);
			jUpButton.setIcon(GalleryRemote.iUp);
		}
		return jUpButton;
	}

	private GridBagConstraints getUpButtonConstraints() {
		if (jUpButtonConstraints == null) {
			jUpButtonConstraints = new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
					new Insets(2, 0, 0, 0), 0, 0);
		}
		return jUpButtonConstraints;
	}

	private JButton getDownButton() {
		if (jDownButton == null) {
			jDownButton = new JButton();
			jDownButton.setMaximumSize(new Dimension(120, 23));
			jDownButton.setMinimumSize(new Dimension(120, 23));
			jDownButton.setPreferredSize(new Dimension(120, 23));
			jDownButton.setToolTipText(GRI18n.getString(MODULE, "dnBtnTip"));
			jDownButton.setText(GRI18n.getString(MODULE, "dnBtn"));
			jDownButton.setActionCommand("Down");
			jDownButton.setHorizontalAlignment(SwingConstants.LEFT);
			jDownButton.setIcon(GalleryRemote.iDown);
		}
		return jDownButton;
	}

	private GridBagConstraints getDownButtonConstraints() {
		if (jDownButtonConstraints == null) {
			jDownButtonConstraints = new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
					new Insets(0, 0, 0, 0), 0, 0);
		}
		return jDownButtonConstraints;
	}

	private JButton getDeleteButton() {
		if (jDeleteButton == null) {
			jDeleteButton = new JButton();
			jDeleteButton.setMaximumSize(new Dimension(120, 23));
			jDeleteButton.setMinimumSize(new Dimension(120, 23));
			jDeleteButton.setPreferredSize(new Dimension(120, 23));
			jDeleteButton.setToolTipText(GRI18n.getString(MODULE, "delBtnTip"));
			jDeleteButton.setActionCommand("Delete");
			jDeleteButton.setHorizontalAlignment(SwingConstants.LEFT);
			jDeleteButton.setText(GRI18n.getString(MODULE, "Delete"));
			jDeleteButton.setIcon(GalleryRemote.iDelete);
		}
		return jDeleteButton;
	}

	private GridBagConstraints getDeleteButtonConstraints() {
		if (jDeleteButtonConstraints == null) {
			jDeleteButtonConstraints = new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
					new Insets(2, 0, 0, 0), 0, 0);
		}
		return jDeleteButtonConstraints;
	}

	private JButton getRotateLeftButton() {
		return getIconAreaPanel().getRotateLeftButton();
	}

	private JButton getRotateRightButton() {
		return getIconAreaPanel().getRotateRightButton();
	}

	private JButton getFlipButton() {
		return getIconAreaPanel().getFlipButton();
	}

	private JLabel getIcon() {
		return getIconAreaPanel().getIcon();
	}

	private PathPanel getPathPanel() {
		if (jPathPanel == null) {
			jPathPanel = new PathPanel();
		}
		return jPathPanel;
	}

	private GridBagConstraints getPathPanelConstraints() {
		if (jPathPanelConstraints == null) {
			jPathPanelConstraints = new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 5, 0, 5), 0, 0);
		}
		return jPathPanelConstraints;
	}

	private CaptionPanel getCaptionPanel() {
		if (jCaptionPanel == null) {
			jCaptionPanel = new CaptionPanel();
		}
		return jCaptionPanel;
	}

	private GridBagConstraints getCaptionPanelConstraints() {
		if (jCaptionPanelConstraints == null) {
			jCaptionPanelConstraints = new GridBagConstraints(1, 7, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH,
					new Insets(5, 5, 0, 5), 0, 0);
		}
		return jCaptionPanelConstraints;
	}

	private JTextArea getCaption() {
		return getCaptionPanel().getCaption();
	}

	private JTextArea getPath() {
		return getPathPanel().getPath();
	}
}
