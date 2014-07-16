package com.gallery.galleryremote.prefs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeModelListener;

import com.gallery.galleryremote.main.MainFrame;
import com.gallery.galleryremote.model.Gallery;
import com.gallery.galleryremote.util.GRI18n;
import com.gallery.galleryremote.GalleryRemote;
import com.gallery.galleryremote.Log;

/**
 * @author paour
 * @version May 8, 2003
 */
public class URLPanel extends PreferencePanel implements ListSelectionListener, ActionListener {
	private static final long serialVersionUID = -4546598278693306150L;
	public static final String MODULE = "URLPa";

	JLabel icon = new JLabel(GRI18n.getString(MODULE, "icon"));

	GridBagLayout gridBagLayout1 = new GridBagLayout();
	JScrollPane jScrollPane1 = new JScrollPane();
	JList<Gallery> jGalleries = new JList<Gallery>();
	JButton jModify = new JButton();
	JButton jNew = new JButton();
	JButton jDelete = new JButton();
	JPanel jPanel1 = new JPanel();
	JLabel jDetails = new JLabel();
	GridLayout gridLayout1 = new GridLayout();

	@Override
	public JLabel getIcon() {
		return icon;
	}

	@Override
	public boolean isReversible() {
		return false;
	}

	@Override
	public void readProperties(PropertiesFile props) {
	}

	@Override
	public void writeProperties(PropertiesFile props) {
	}

	@Override
	public void buildUI() {
		jbInit();
	}

	private void jbInit() {
		this.setLayout(gridBagLayout1);
		jModify.setActionCommand("Modify");
		jModify.setText(GRI18n.getString(MODULE, "modify"));
		jNew.setActionCommand("New");
		jNew.setText(GRI18n.getString(MODULE, "new"));
		jDelete.setActionCommand("Delete");
		jDelete.setText(GRI18n.getString(MODULE, "delete"));
		jPanel1.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(Color.white, new Color(148, 145, 140)), GRI18n.getString(MODULE,
				"details")));
		jPanel1.setLayout(gridLayout1);
		gridLayout1.setColumns(1);
		jDetails.setMinimumSize(new Dimension(0, 50));
		jDetails.setPreferredSize(new Dimension(0, 50));
		jDetails.setHorizontalAlignment(SwingConstants.LEFT);
		jDetails.setVerticalAlignment(SwingConstants.TOP);
		this.add(jScrollPane1, new GridBagConstraints(0, 0, 1, 3, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,
				0, 0, 0), 0, 0));
		this.add(jModify, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
				5, 5, 0, 5), 0, 0));
		this.add(jNew, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5,
				5, 0, 5), 0, 0));
		this.add(jDelete, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5,
				5, 0, 5), 0, 0));
		this.add(jPanel1, new GridBagConstraints(0, 3, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0,
				0, 0), 0, 0));
		jPanel1.add(jDetails, null);
		jScrollPane1.getViewport().add(jGalleries, null);

		jGalleries.setModel(GalleryRemote.instance().getCore().getGalleries());
		jGalleries.setCellRenderer(new GalleryCellRenderer());
		jGalleries.addListSelectionListener(this);

		jGalleries.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int index = jGalleries.locationToIndex(e.getPoint());
					modifyGallery(jGalleries.getModel().getElementAt(index));
				}
			}
		});

		if (GalleryRemote.instance().getCore().getGalleries().getSize() > 0) {
			jGalleries.setSelectedIndex(0);
		} else {
			resetUIState();
		}

		jModify.addActionListener(this);
		jNew.addActionListener(this);
		jDelete.addActionListener(this);
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		resetUIState();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		Gallery g = jGalleries.getSelectedValue();

		Log.log(Log.LEVEL_INFO, MODULE, "Command selected " + cmd + " Gallery: " + g);

		if (cmd.equals("Modify")) {
			modifyGallery(g);
		} else if (cmd.equals("New")) {
			Gallery newG = new Gallery(GalleryRemote.instance().getCore().getMainStatusUpdate());
			if (GalleryRemote.instance().getCore() instanceof TreeModelListener) {
				newG.addTreeModelListener((TreeModelListener) GalleryRemote.instance().getCore());
			}

			GalleryEditorDialog ged = new GalleryEditorDialog(dialog, newG);

			if (ged.isOK()) {
				GalleryRemote.instance().getCore().getGalleries().addElement(newG);
				jGalleries.setSelectedValue(newG, true);

				Gallery.uncacheAmbiguousUrl();

				resetUIState();
			}
		} else if (cmd.equals("Delete")) {
			Object[] params = { g.getGalleryUrl("") };
			int n = JOptionPane.showConfirmDialog(this, GRI18n.getString(MODULE, "delConfirm", params), GRI18n.getString(MODULE, "delete"),
					JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION);

			if (n == JOptionPane.YES_OPTION) {
				((MainFrame) GalleryRemote.instance().getCore()).removeGallery(g);

				Gallery.uncacheAmbiguousUrl();
			}
		} else if (cmd.equals("GalleryEditorDialog")) {
			Gallery newG = (Gallery) e.getSource();

			if (GalleryRemote.instance().getCore().getGalleries().getIndexOf(newG) == -1) {
				GalleryRemote.instance().getCore().getGalleries().addElement(newG);
				jGalleries.setSelectedValue(newG, true);
			}

			Gallery.uncacheAmbiguousUrl();
			resetUIState();
		} else {
			Log.log(Log.LEVEL_ERROR, MODULE, "Unknown command: " + cmd);
		}
	}

	private void modifyGallery(Gallery g) {
		GalleryEditorDialog ged = new GalleryEditorDialog(dialog, g);

		if (ged.isOK()) {
			// jGalleries.repaint();
			int i = GalleryRemote.instance().getCore().getGalleries().getIndexOf(g);
			GalleryRemote.instance().getCore().getGalleries().removeElementAt(i);
			GalleryRemote.instance().getCore().getGalleries().insertElementAt(g, i);

			Gallery.uncacheAmbiguousUrl();
		}
	}

	public void resetUIState() {
		Gallery selectedGallery = jGalleries.getSelectedValue();

		StringBuffer sb = new StringBuffer();

		if (selectedGallery != null) {
			sb.append("<HTML>");

			if (selectedGallery.getType() == Gallery.TYPE_STANDALONE) {
				sb.append(GRI18n.getString(MODULE, "gllryURL")).append(selectedGallery.getStUrlString()).append("<br>");
			} else if (selectedGallery.getType() == Gallery.TYPE_POSTNUKE) {
				sb.append(GRI18n.getString(MODULE, "pnLoginURL")).append(selectedGallery.getPnLoginUrlString()).append("<br>");
				sb.append(GRI18n.getString(MODULE, "pnGllryURL")).append(selectedGallery.getPnGalleryUrlString()).append("<br>");
			} else if (selectedGallery.getType() == Gallery.TYPE_PHPNUKE) {
				sb.append(GRI18n.getString(MODULE, "phpnLoginURL")).append(selectedGallery.getPhpnLoginUrlString()).append("<br>");
				sb.append(GRI18n.getString(MODULE, "phpnGllryURL")).append(selectedGallery.getPhpnGalleryUrlString()).append("<br>");
			} else if (selectedGallery.getType() == Gallery.TYPE_GEEKLOG) {
				sb.append(GRI18n.getString(MODULE, "glLoginURL")).append(selectedGallery.getGlLoginUrlString()).append("<br>");
				sb.append(GRI18n.getString(MODULE, "glGllryURL")).append(selectedGallery.getGlGalleryUrlString()).append("<br>");
			}

			String username = selectedGallery.getUsername();
			if (username == null || username.length() == 0) {
				username = "&lt;Not set&gt;";
			}
			sb.append(GRI18n.getString(MODULE, "username")).append(username).append("<br>");

			if (selectedGallery.isAutoLoadOnStartup()) {
				sb.append(GRI18n.getString(MODULE, "autoLogin")).append("<br>");
			}

			sb.append("</HTML>");

			jModify.setEnabled(true);
			jDelete.setEnabled(true);
		} else {
			jModify.setEnabled(false);
			jDelete.setEnabled(false);
		}

		jDetails.setText(sb.toString());
	}

	public class GalleryCellRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 7050351817484333593L;

		/**
		 * Gets the listCellRendererComponent attribute of the FileCellRenderer
		 * object
		 * 
		 * @param list
		 *           Description of Parameter
		 * @param value
		 *           Description of Parameter
		 * @param index
		 *           Description of Parameter
		 * @param selected
		 *           Description of Parameter
		 * @param hasFocus
		 *           Description of Parameter
		 * @return The listCellRendererComponent value
		 */
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean hasFocus) {
			super.getListCellRendererComponent(list, value, index, selected, hasFocus);

			Gallery g = (Gallery) value;

			setText(g.toString());

			if (g.isAutoLoadOnStartup()) {
				setFont(getFont().deriveFont(Font.BOLD));
			} else {
				setFont(getFont().deriveFont(Font.PLAIN));
			}

			return this;
		}
	}
}
