package com.gallery.GalleryRemote;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import com.gallery.GalleryRemote.model.Album;
import com.gallery.GalleryRemote.model.Gallery;
import com.gallery.GalleryRemote.model.Picture;
import com.gallery.GalleryRemote.prefs.PreferenceNames;
import com.gallery.GalleryRemote.prefs.SlideshowPanel;
import com.gallery.GalleryRemote.statusbar.StatusBar;
import com.gallery.GalleryRemote.util.GRI18n;
import com.gallery.GalleryRemote.util.ImageUtils;

/**
 * @author paour
 * @version Oct 30, 2003
 */
public class GRAppletSlideshow extends GRAppletMini implements GalleryRemoteCore, ActionListener, ListDataListener, PreferenceNames {

	private static final long serialVersionUID = -5416086781671901467L;
	public static final String MODULE = "AppletSlideshow";
	JButton jStart;
	SlideshowPanel jSlidePanel;
	SlideshowFrame slideshowFrame = null;
	AppletInfo info;

	public GRAppletSlideshow() {
		coreClass = "com.gallery.GalleryRemote.GalleryRemoteMini";
	}

	@Override
	public void startup() {
		galleries = new DefaultComboBoxModel<Gallery>();
		info = getGRAppletInfo();

		gallery = info.gallery;

		galleries.addElement(gallery);
		ImageUtils.deferredTasks();

		album = new Album(gallery);
		album.setName(info.albumName);
		album.addListDataListener(this);

		album.fetchAlbumImages(jStatusBar, GalleryRemote.instance().properties.getBooleanProperty(SLIDESHOW_RECURSIVE, true),
				GalleryRemote.instance().properties.getIntProperty(SLIDESHOW_MAX_PICTURES, 0));
	}

	@Override
	protected void jbInit() {
		getContentPane().setLayout(new GridBagLayout());

		jStart = new JButton(GRI18n.getString(MODULE, "Start"));

		jStatusBar = new StatusBar(75);

		jSlidePanel = new SlideshowPanel();

		JPanel filler1 = new JPanel();
		filler1.setMinimumSize(new Dimension(0, 0));
		JPanel filler2 = new JPanel();
		filler2.setMinimumSize(new Dimension(0, 0));

		getContentPane().add(jSlidePanel,
				new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		getContentPane().add(
				new JLabel(GRI18n.getString(MODULE, "Disabled")),
				new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0,
						0));
		getContentPane().add(
				filler2,
				new GridBagConstraints(0, 2, 1, 1, 0.1, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 0,
						0));
		getContentPane().add(
				jStart,
				new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10,
						10), 0, 0));
		getContentPane().add(
				filler1,
				new GridBagConstraints(0, 4, 1, 1, 0.1, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 0,
						0));
		getContentPane().add(
				jStatusBar,
				new GridBagConstraints(0, 5, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0),
						0, 0));

		jSlidePanel.buildUI();
		jSlidePanel.remove(jSlidePanel.spacerPanel);
		jSlidePanel.readProperties(GalleryRemote.instance().properties);
		jStart.addActionListener(this);
		jStart.setEnabled(false);

		jPicturesList = new DroppableList();
	}

	@Override
	public void setInProgress(boolean inProgress) {
		jStart.setEnabled(!inProgress && album.getSize() > 0);

		this.inProgress = inProgress;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		jSlidePanel.writeProperties(GalleryRemote.instance().properties);

		if (slideshowFrame == null) {
			slideshowFrame = new SlideshowFrame();
		}

		slideshowFrame.showSlideshow();
		slideshowFrame.start(album.getPicturesList());

		// null slideshowFrame so that next time the user clicks the button
		// they get a blank one, in case they changed positioning
		slideshowFrame = null;
	}

	@Override
	public void shutdown() {
		if (hasStarted && GalleryRemote.instance() != null) {
			jSlidePanel.writeProperties(GalleryRemote.instance().properties);
			// GalleryRemote._().properties.write();
		}

		super.shutdown();
	}

	@Override
	public void contentsChanged(ListDataEvent e) {
		if (album.isHasFetchedImages()) {
			Log.log(Log.LEVEL_TRACE, MODULE, "Pictures were just added to the album. Preload the first one.");

			new Thread() {
				@Override
				public void run() {
					slideshowFrame = new SlideshowFrame();
					int index = 0;
					ArrayList<Picture> picturesList = album.getPicturesList();

					if (info.slideshowFrom != null) {
						for (int i = 0; i < picturesList.size(); i++) {
							if (info.slideshowFrom.equals(picturesList.get(i).getUniqueId())) {
								Log.log(Log.LEVEL_TRACE, MODULE, "Starting slideshow from index " + i);
								index = i;
								slideshowFrame.wantIndex = i - 1;
								break;
							}
						}
					}

					if (album.getSize() > index) {
						ImageUtils.download(picturesList.get(index), getGraphicsConfiguration().getBounds().getSize(), GalleryRemote
								.instance().getCore().getMainStatusUpdate(), null);
					} else {
						JOptionPane.showMessageDialog(GRAppletSlideshow.this, GRI18n.getString(MODULE, "emptyAlbum"));
					}
				}
			}.start();
		}
	}

	@Override
	public void intervalAdded(ListDataEvent e) {
	}

	@Override
	public void intervalRemoved(ListDataEvent e) {
	}
}
