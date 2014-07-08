package com.gallery.GalleryRemote;

import java.applet.Applet;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.gallery.GalleryRemote.model.Album;
import com.gallery.GalleryRemote.model.Gallery;
import com.gallery.GalleryRemote.model.Picture;
import com.gallery.GalleryRemote.prefs.PreferenceNames;
import com.gallery.GalleryRemote.statusbar.StatusBar;
import com.gallery.GalleryRemote.statusbar.StatusBarModel;
import com.gallery.GalleryRemote.statusbar.StatusBarPresenter;
import com.gallery.GalleryRemote.statusbar.StatusBarPresenterImpl;
import com.gallery.GalleryRemote.statusbar.StatusUpdate;
import com.gallery.GalleryRemote.util.DialogUtil;
import com.gallery.GalleryRemote.util.GRI18n;
import com.gallery.GalleryRemote.util.ImageUtils;

/**
 * @author paour
 * @version Oct 30, 2003
 */
public class GRAppletMini extends GRApplet implements GalleryRemoteCore, ActionListener, DocumentListener, ListSelectionListener,
		PreferenceNames {
	private static final long serialVersionUID = 3643620550047079843L;

	public static final String MODULE = "AppletMini";

	JButton jUpload;
	JButton jAdd;
	StatusBarPresenter jStatusBar;
	JScrollPane jScrollPane;
	DroppableList jPicturesList;
	// JPanel jContentPanel;
	JCheckBox jResize;
	JCheckBox jThumbnails;
	JPanel jInspector;
	JLabel captionLabel;
	JTextArea jCaption;
	JSplitPane jDivider;
	ArrayList<JTextArea> jExtrafields;

	DefaultComboBoxModel<Gallery> galleries = null;
	Album album = null;
	Gallery gallery = null;
	boolean inProgress = false;
	boolean hasHadPictures = false;
	Method call;
	Object window;
	ThumbnailCache thumbnailCache = null;

	public GRAppletMini() {
		coreClass = "com.gallery.GalleryRemote.GalleryRemoteMini";
	}

	@Override
	public void initUI() {
		// update the look and feel
		SwingUtilities.updateComponentTreeUI(this);
		int appletFontSize = GalleryRemote.instance().properties.getIntProperty(APPLET_FONTSIZE, 0);
		if (appletFontSize != 0) {
			// the default font for many components is too big on Mac and Linux
			Log.log(Log.LEVEL_TRACE, MODULE, "Overriding font size to " + appletFontSize);
			Log.log(Log.LEVEL_TRACE, MODULE, "Default font size " + UIManager.getFont("TitledBorder.font").getSize());
			Font f = UIManager.getFont("TitledBorder.font").deriveFont((float) appletFontSize);
			Log.log(Log.LEVEL_TRACE, MODULE, "Checking font size " + f.getSize());
			UIManager.put("Label.font", f);
			UIManager.put("TextField.font", f);
			UIManager.put("Button.font", f);
			UIManager.put("CheckBox.font", f);
			UIManager.put("ComboBox.font", f);
			UIManager.put("TitledBorder.font", f);
			UIManager.put("TabbedPane.font", f);
		}

		jbInit();
	}

	@Override
	public void startup() {
		galleries = new DefaultComboBoxModel<Gallery>();
		AppletInfo info = getGRAppletInfo();

		gallery = info.gallery;

		galleries.addElement(gallery);

		ImageUtils.deferredTasks();

		album = new Album(gallery);
		// album.setSuppressEvents(true);
		album.setName(info.albumName);
		gallery.createRootAlbum().add(album);

		album.fetchAlbumProperties(getMainStatusUpdate());
		jbUpdate();

		jPicturesList.setModel(album);
		jPicturesList.setInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, null);
		jPicturesList.setInputMap(JComponent.WHEN_FOCUSED, null);
		jPicturesList.setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, null);
		jPicturesList.setCellRenderer(new CoreUtils.FileCellRenderer());

		jResize.setSelected(GalleryRemote.instance().properties.getBooleanProperty(RESIZE_BEFORE_UPLOAD));
		jThumbnails.setSelected(GalleryRemote.instance().properties.getShowThumbnails());
		setShowThumbnails(GalleryRemote.instance().properties.getShowThumbnails());

		jStatusBar.setStatus(GRI18n.getString("MainFrame", "selPicToAdd"));
	}

	@Override
	public void shutdown() {
		if (hasStarted && GalleryRemote.instance() != null) {
			// this is also executed from GRAppletSlideshow
			if (jDivider != null) {
				GalleryRemote.instance().properties.setIntProperty(APPLET_DIVIDER_LOCATION, jDivider.getDividerLocation());
			}

			ImageUtils.purgeTemp();
			GalleryRemote.instance().properties.write();
			GalleryRemote.shutdownInstance();
		}
	}

	@Override
	public void shutdown(boolean shutdownOs) {
		shutdown();
	}

	@Override
	public void flushMemory() {
	}

	@Override
	public void preloadThumbnails(Iterator<Picture> pictures) {
		if (thumbnailCache != null) {
			thumbnailCache.preloadThumbnails(pictures);
		}
	}

	@Override
	public Image getThumbnail(Picture p) {
		if (p == null) {
			return null;
		}

		Image thumb = thumbnailCache.getThumbnail(p);

		if (thumb == null) {
			thumb = ImageUtils.defaultThumbnail;
		} else {
			thumb = ImageUtils.rotateImage(thumb, p.getAngle(), p.isFlipped(), getGlassPane());
		}

		return thumb;
	}

	@Override
	public StatusUpdate getMainStatusUpdate() {
		return jStatusBar;
	}

	@Override
	public DefaultComboBoxModel<Gallery> getGalleries() {
		return galleries;
	}

	@Override
	public void thumbnailLoadedNotify() {
		jPicturesList.repaint();
	}

	@Override
	public void setInProgress(boolean inProgress) {
		jUpload.setEnabled(!inProgress);
		jAdd.setEnabled(!inProgress);
		jPicturesList.setEnabled(!inProgress);
		jCaption.setEnabled(!inProgress);
		jResize.setEnabled(!inProgress);
		jThumbnails.setEnabled(!inProgress);

		this.inProgress = inProgress;

		if (!inProgress && hasHadPictures) {
			// probably finished uploading...
			try {
				// no update for G2 and for embedded applets (non-embedded
				// applets are TYPE_STANDALONE)
				if (!(gallery.getComm(null) instanceof GalleryComm2_5) && gallery.getType() != Gallery.TYPE_APPLET) {
					getAppletContext().showDocument(new URL(getCodeBase().toString() + "add_photos_refresh.php"), "hack");
				}

				// use Java to Javascript scripting
				g2Feedback("doneUploading", new Object[] {});
			} catch (MalformedURLException e) {
				Log.logException(Log.LEVEL_ERROR, MODULE, e);
			}

			hasHadPictures = false;
		}
	}

	@Override
	public void addPictures(File[] files, int index, boolean select) {
		ArrayList<Picture> newPictures = null;
		if (index == -1) {
			newPictures = album.addPictures(files);
		} else {
			newPictures = album.addPictures(files, index);
		}

		preloadThumbnails(newPictures.iterator());
	}

	@Override
	public void addPictures(Picture[] pictures, int index, boolean select) {
		album.addPictures(Arrays.asList(pictures), index);
	}

	@Override
	public Album getCurrentAlbum() {
		return album;
	}

	@Override
	public JList<Picture> getPicturesList() {
		return jPicturesList;
	}

	protected void jbInit() {
		jUpload = new JButton();
		jAdd = new JButton();
		jStatusBar = new StatusBarPresenterImpl(new StatusBarModel(), new StatusBar(75));
		jScrollPane = new JScrollPane();
		jPicturesList = new DroppableList();
		// jContentPanel = new JPanel(new GridBagLayout());
		jResize = new JCheckBox();
		jThumbnails = new JCheckBox();
		jInspector = new JPanel(new GridBagLayout());
		captionLabel = new JLabel();
		jCaption = new JTextArea();
		jDivider = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

		jScrollPane.setBorder(new TitledBorder(BorderFactory.createEmptyBorder(), GRI18n.getString(MODULE, "pictures")));

		jUpload.setText(GRI18n.getString(MODULE, "Upload"));
		jAdd.setText(GRI18n.getString(MODULE, "Add"));

		JPanel jButtonPanel = new JPanel();
		jButtonPanel.setLayout(new GridLayout(2, 2, 5, 0));
		jButtonPanel.add(jThumbnails);
		jButtonPanel.add(jResize);
		jButtonPanel.add(jAdd);
		jButtonPanel.add(jUpload);

		jResize.setToolTipText(GRI18n.getString(MODULE, "ResizeBeforeUploadHelp"));
		jResize.setText(GRI18n.getString(MODULE, "ResizeBeforeUpload"));
		jThumbnails.setToolTipText(GRI18n.getString(MODULE, "ThumbnailsHelp"));
		jThumbnails.setText(GRI18n.getString(MODULE, "Thumbnails"));
		captionLabel.setText(GRI18n.getString(MODULE, "Caption") + "            ");
		jScrollPane.getViewport().add(jPicturesList, null);

		if (!GalleryRemote.instance().properties.getBooleanProperty(APPLET_SHOW_RESIZE, true)) {
			jResize.setVisible(false);
		}

		jCaption.setLineWrap(true);
		jCaption.setEditable(false);
		jCaption.setFont(UIManager.getFont("Label.font"));
		jCaption.setBackground(UIManager.getColor("TextField.inactiveBackground"));

		jDivider.setBorder(null);
		jDivider.setOneTouchExpandable(true);
		// jDivider.setResizeWeight(.75);
		jDivider.setDividerLocation(GalleryRemote.instance().properties.getIntProperty(APPLET_DIVIDER_LOCATION));

		jScrollPane.setMinimumSize(new Dimension(100, 0));
		jInspector.setMinimumSize(new Dimension(0, 0));
		jDivider.setLeftComponent(jScrollPane);
		jDivider.setRightComponent(jInspector);

		jInspector.add(captionLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(0, 5, 0, 0), 0, 0));
		jInspector.add(jCaption, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
				0, 5, 5, 5), 0, 0));

		this.getContentPane().setLayout(new GridBagLayout());
		this.getContentPane().add(jDivider,
				new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		this.getContentPane().add(jButtonPanel,
				new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		this.getContentPane().add(
				jStatusBar.getView(),
				new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0),
						0, 0));

		jAdd.addActionListener(this);
		jUpload.addActionListener(this);
		jCaption.getDocument().addDocumentListener(this);
		jPicturesList.addListSelectionListener(this);
		jResize.addActionListener(this);
		jThumbnails.addActionListener(this);

		jPicturesList.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				jListKeyPressed(e);
			}
		});

		Class<?> jsObject;
		try {
			// this class is not signed by Gallery, so we can't use the secure
			// instantiation
			jsObject = Class.forName("netscape.javascript.JSObject");
			Method getWindow = jsObject.getMethod("getWindow", new Class<?>[] { Applet.class });
			call = jsObject.getMethod("call", new Class[] { String.class, Object[].class });
			window = getWindow.invoke(null, new Object[] { this });
		} catch (ClassNotFoundException e) {
			Log.logException(Log.LEVEL_ERROR, MODULE, e);
		} catch (IllegalAccessException e) {
			Log.logException(Log.LEVEL_ERROR, MODULE, e);
		} catch (NoSuchMethodException e) {
			Log.logException(Log.LEVEL_ERROR, MODULE, e);
		} catch (InvocationTargetException e) {
			Log.logException(Log.LEVEL_ERROR, MODULE, e);
		}

		thumbnailCache = new ThumbnailCache();
	}

	public void jbUpdate() {
		ArrayList<String> extrafields = album.getExtraFields();
		if (extrafields != null) {
			jExtrafields = new ArrayList<JTextArea>(extrafields.size());
			Iterator<String> i = extrafields.iterator();
			int j = 2;

			while (i.hasNext()) {
				String name = i.next();
				JTextArea jExtrafield = new JTextArea();
				jExtrafield.setName(name);
				jExtrafields.add(jExtrafield);

				jExtrafield.setLineWrap(true);
				jExtrafield.setEditable(false);
				jExtrafield.setFont(UIManager.getFont("Label.font"));
				jExtrafield.setBackground(UIManager.getColor("TextField.inactiveBackground"));

				jInspector.add(new JLabel(name + ":"), new GridBagConstraints(0, j++, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
				jInspector.add(jExtrafield, new GridBagConstraints(0, j++, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 5, 5, 5), 0, 0));

				jExtrafield.getDocument().addDocumentListener(this);
			}
		}
	}

	public void jListKeyPressed(KeyEvent e) {
		if (!inProgress) {
			int vKey = e.getKeyCode();
			Log.log(Log.LEVEL_TRACE, MODULE, "Key pressed: " + vKey);

			switch (vKey) {
			case KeyEvent.VK_DELETE:
			case KeyEvent.VK_BACK_SPACE:
				CoreUtils.deleteSelectedPictures();
				break;
			case KeyEvent.VK_LEFT:
				CoreUtils.movePicturesUp();
				break;
			case KeyEvent.VK_RIGHT:
				CoreUtils.movePicturesDown();
				break;
			case KeyEvent.VK_UP:
				CoreUtils.selectPrevPicture();
				break;
			case KeyEvent.VK_DOWN:
				CoreUtils.selectNextPicture();
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == jAdd) {
			jStatusBar.setStatus(GRI18n.getString("MainFrame", "selPicToAdd"));
			File[] files = AddFileDialog.addFiles(this);

			if (files != null) {
				addPictures(files, -1, false);
				hasHadPictures = true;
			}
		} else if (source == jUpload) {
			g2Feedback("startingUpload", new Object[] {});
			gallery.doUploadFiles(new UploadProgress(DialogUtil.findParentWindow(this)) {
				@Override
				public void doneUploading(String newItemName, Picture picture) {
					g2Feedback("uploadedOne", new Object[] { newItemName, picture.toString() });
				}
			});
		} else if (source == jResize) {
			GalleryRemote.instance().properties.setBooleanProperty(RESIZE_BEFORE_UPLOAD, jResize.isSelected());
		} else if (source == jThumbnails) {
			setShowThumbnails(jThumbnails.isSelected());
		}
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		textUpdate(e);
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		textUpdate(e);
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		textUpdate(e);
	}

	public void textUpdate(DocumentEvent e) {
		Picture p = jPicturesList.getSelectedValue();

		if (p != null) {
			if (e.getDocument() == jCaption.getDocument()) {
				p.setCaption(jCaption.getText());
			} else {
				Iterator<JTextArea> i = jExtrafields.iterator();
				while (i.hasNext()) {
					JTextArea jExtrafield = i.next();

					if (e.getDocument() == jExtrafield.getDocument()) {
						p.setExtraField(jExtrafield.getName(), jExtrafield.getText());
						// Log.log("textUpdate: " + jExtrafield.getName() +
						// " - " + jExtrafield.getText());
					}
				}
			}
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		Picture p = jPicturesList.getSelectedValue();

		if (p == null) {
			jCaption.setText("");
			jCaption.setEditable(false);
			jCaption.setBackground(UIManager.getColor("TextField.inactiveBackground"));

			Iterator<JTextArea> i = jExtrafields.iterator();
			while (i.hasNext()) {
				JTextArea jExtrafield = i.next();

				jExtrafield.setText("");
				jExtrafield.setEditable(false);
				jExtrafield.setBackground(UIManager.getColor("TextField.inactiveBackground"));
			}
		} else {
			jCaption.setText(p.getCaption());
			jCaption.setEditable(true);
			jCaption.setBackground(UIManager.getColor("TextField.background"));

			Iterator<JTextArea> i = jExtrafields.iterator();
			while (i.hasNext()) {
				JTextArea jExtrafield = i.next();

				// Log.log("valueChanged: " + jExtrafield.getName() + " - " +
				// jExtrafield.getText());
				String extrafield = p.getExtraField(jExtrafield.getName());
				jExtrafield.setText(extrafield == null ? "" : extrafield);
				jExtrafield.setEditable(true);
				jExtrafield.setBackground(UIManager.getColor("TextField.background"));
			}
		}
	}

	public void setShowThumbnails(boolean show) {
		if (show != GalleryRemote.instance().properties.getShowThumbnails()) {
			GalleryRemote.instance().properties.setShowThumbnails(show);
		}

		if (show) {
			preloadThumbnails(album.getPictures());

			jPicturesList.setFixedCellHeight(GalleryRemote.instance().properties.getThumbnailSize().height + 4);
		} else {
			if (thumbnailCache != null) {
				thumbnailCache.cancelLoad();
			}
			jPicturesList.setFixedCellHeight(-1);
		}

		jPicturesList.repaint();
	}

	public void g2Feedback(String method, Object[] params) {
		if (gallery.getGalleryVersion() == 2) {
			try {
				Log.log(Log.LEVEL_TRACE, MODULE, "Invoking Javascript method '" + method + "' with " + Arrays.asList(params) + " on " + window);
				call.invoke(window, new Object[] { method, params });
			} catch (Throwable e) {
				Log.logException(Log.LEVEL_ERROR, MODULE, e);
			}
		}
	}
}
