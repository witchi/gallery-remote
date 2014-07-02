package com.gallery.GalleryRemote;

import com.gallery.GalleryRemote.model.Picture;
import com.gallery.GalleryRemote.model.Album;

import javax.swing.*;
import java.util.Arrays;
import java.awt.*;
import java.io.File;

/**
 * @author paour
 * @version Jan 15, 2004
 */
public class CoreUtils {
	public static final String MODULE = "CoreUtils";

	static GalleryRemoteCore core = null;

	public static void initCore() {
		core = GalleryRemote.instance().getCore();
	}

	public static void deleteSelectedPictures() {
		JList<Picture> jPicturesList = core.getPicturesList();
		int[] indices = jPicturesList.getSelectedIndices();
		int selected = jPicturesList.getSelectedIndex();
		Picture reselect = null;

		// find non-selected item after selected index
		Arrays.sort(indices);
		boolean found = false;

		int i = selected + 1;
		while (i < jPicturesList.getModel().getSize()) {
			if (Arrays.binarySearch(indices, i) < 0) {
				found = true;
				break;
			}

			i++;
		}

		if (!found) {
			i = selected - 1;
			while (i >= 0) {
				if (Arrays.binarySearch(indices, i) < 0) {
					found = true;
					break;
				}

				i--;
			}
		}

		if (found) {
			reselect = jPicturesList.getModel().getElementAt(i);
		}

		core.getCurrentAlbum().removePictures(indices);

		if (reselect != null) {
			jPicturesList.setSelectedValue(reselect, true);
		}
	}

	/**
	 * Move selected Pictures up
	 */
	public static void movePicturesUp() {
		JList<Picture> jPicturesList = core.getPicturesList();
		int[] indices = jPicturesList.getSelectedIndices();
		int[] reselect = new int[indices.length];

		Arrays.sort(indices);

		for (int i = 0; i < indices.length; i++) {
			if (indices[i] > 0) {
				Album currentAlbum = core.getCurrentAlbum();

				Picture buf = currentAlbum.getPicture(indices[i]);
				currentAlbum.setPicture(indices[i], currentAlbum.getPicture(indices[i] - 1));
				currentAlbum.setPicture(indices[i] - 1, buf);
				reselect[i] = indices[i] - 1;
			} else {
				reselect[i] = indices[i];
			}
		}

		jPicturesList.setSelectedIndices(reselect);
		jPicturesList.ensureIndexIsVisible(jPicturesList.getSelectedIndex());
	}

	/**
	 * Move selected Pictures down
	 */
	public static void movePicturesDown() {
		JList<Picture> jPicturesList = core.getPicturesList();
		int[] indices = jPicturesList.getSelectedIndices();
		int[] reselect = new int[indices.length];

		Arrays.sort(indices);

		for (int i = indices.length - 1; i >= 0; i--) {
			Album currentAlbum = core.getCurrentAlbum();

			if (indices[i] < currentAlbum.sizePictures() - 1) {
				Picture buf = currentAlbum.getPicture(indices[i]);
				currentAlbum.setPicture(indices[i], currentAlbum.getPicture(indices[i] + 1));
				currentAlbum.setPicture(indices[i] + 1, buf);
				reselect[i] = indices[i] + 1;
			} else {
				reselect[i] = indices[i];
			}
		}

		jPicturesList.setSelectedIndices(reselect);
		jPicturesList.ensureIndexIsVisible(jPicturesList.getSelectedIndex());
	}

	public static void selectNextPicture() {
		JList<Picture> jPicturesList = core.getPicturesList();
		int i = jPicturesList.getSelectedIndex();

		if (i < jPicturesList.getModel().getSize() - 1) {
			jPicturesList.setSelectedIndex(i + 1);
			jPicturesList.ensureIndexIsVisible(i + 1);
		}
	}

	public static void selectPrevPicture() {
		JList<Picture> jPicturesList = core.getPicturesList();
		int i = jPicturesList.getSelectedIndex();

		if (i > 0) {
			jPicturesList.setSelectedIndex(i - 1);
			jPicturesList.ensureIndexIsVisible(i - 1);
		}
	}

	static class FileCellRenderer extends DefaultListCellRenderer {

		private static final long serialVersionUID = -7506167308544454638L;

		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean hasFocus) {
			super.getListCellRendererComponent(list, value, index, selected, hasFocus);

			Album currentAlbum = core.getCurrentAlbum();
			if (null != currentAlbum && null != value && -1 != index) {

				try {
					Picture p = currentAlbum.getPicture(index);

					if (p.isOnline()) {
						if (p.getParentAlbum() != p.getAlbumOnServer() || p.getIndex() != p.getIndexOnServer()) {
							setForeground(Color.red);
						} else {
							setForeground(Color.green);
						}
					}

					if (GalleryRemote.instance().properties.getShowThumbnails()) {
						Image icon = core.getThumbnail(p);
						if (icon != null) {
							Icon iicon = getIcon();
							if (iicon == null || !(iicon instanceof ImageIcon)) {
								setIcon((iicon = new ImageIcon()));
							}
							((ImageIcon) iicon).setImage(icon);

							setIconTextGap(4 + GalleryRemote.instance().properties.getThumbnailSize().width - icon.getWidth(this));
						}
					}

					StringBuffer text = new StringBuffer();
					text.append("<html><p>");

					if (p.isOnline()) {
						text.append(p.getName());
					} else {
						File f = p.getSource();
						text.append(f.getName());
						if (GalleryRemote.instance().properties.getShowPath()) {
							text.append(" [").append(f.getParent()).append("]</p>");
						}
					}

					if (p.getCaption() != null && getIcon() != null) {
						text.append("<p><font color=\"gray\">").append(p.getEscapedCaption()).append("</font></p>");
					}

					text.append("</html>");
					// Log.log(Log.TRACE, MODULE, text.toString());
					setText(text.toString());
				} catch (Exception e) {
					setText("Problem...");
					Log.logException(Log.LEVEL_ERROR, MODULE, e);
				}
			} else {
				setText("dummy");
			}

			return this;
		}
	}
}
