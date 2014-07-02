package com.gallery.GalleryRemote.prefs;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.gallery.GalleryRemote.GalleryRemote;
import com.gallery.GalleryRemote.Log;
import com.gallery.GalleryRemote.model.Gallery;
import com.gallery.GalleryRemote.util.GRI18n;

/**
 * Created by IntelliJ IDEA. User: paour Date: May 8, 2003
 */
public class QuickConfigPanel extends PreferencePanel implements ActionListener {
	private static final long serialVersionUID = -336297219772040822L;
	public static final String MODULE = "QuickCPa";

	GalleryProperties newProps = null;

	JLabel icon = new JLabel(GRI18n.getString(MODULE, "icon"));
	GridBagLayout gridBagLayout1 = new GridBagLayout();
	JLabel jLabel1 = new JLabel();
	JTextField jURL = new JTextField();
	JButton jSetup = new JButton();
	JLabel jLabel2 = new JLabel();

	public JLabel getIcon() {
		return icon;
	}

	public boolean isReversible() {
		return false;
	}

	public void readProperties(PropertiesFile props) {
	}

	public void writeProperties(PropertiesFile props) {
	}

	public void buildUI() {
		jbInit();
	}

	private void jbInit() {
		jLabel1.setText(GRI18n.getString(MODULE, "URL"));
		this.setLayout(gridBagLayout1);

		jSetup.setText(GRI18n.getString(MODULE, "setup"));
		jLabel2.setText(GRI18n.getString(MODULE, "info"));
		jLabel2.setVerticalAlignment(SwingConstants.TOP);
		jLabel2.setPreferredSize(new Dimension(200, 100));
		this.add(jLabel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						0, 2, 0, 2), 0, 0));
		this.add(jURL, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 0, 0), 0, 0));
		this.add(jSetup, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,
						5, 0, 0), 0, 0));
		this.add(jLabel2, new GridBagConstraints(0, 1, 3, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						5, 0, 0, 0), 0, 0));

		jSetup.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		try {
			InputStream content = (InputStream) new URL(jURL.getText())
					.getContent();
			GalleryProperties newProps = new GalleryProperties();
			newProps.load(content);

			StringBuffer overridden = new StringBuffer("<ul>");
			GalleryProperties props = GalleryRemote.instance().properties;
			boolean newGallery = false;

			Iterator<Object> it = newProps.keySet().iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				String value = newProps.getProperty(key);

				if (key.endsWith(".99")) {
					if (!newGallery) {
						// add Gallery access settings

						Gallery g = Gallery.readFromProperties(newProps, 99,
								GalleryRemote.instance().getCore()
										.getMainStatusUpdate(), false);
						if (g != null) {
							g.setPrefsIndex(GalleryRemote.instance().getCore()
									.getGalleries().getSize());
							GalleryRemote.instance().getCore().getGalleries()
									.addElement(g);
							newGallery = true;

							overridden.append("<li>").append(g.toString())
									.append("</li>");
						}
					}
				} else {
					props.setProperty(key, value);

					overridden.append("<li>").append(key).append("</li>");
				}
			}

			overridden.append("</ul>");

			JOptionPane
					.showMessageDialog(
							this,
							GRI18n.getString(MODULE, "confLoaded")
									+ overridden.toString() + "</html>",
							GRI18n.getString(MODULE, "done"),
							JOptionPane.PLAIN_MESSAGE);

			dialog.setVisible(false);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this,
					"Could not find configuration file.",
					GRI18n.getString(MODULE, "error"),
					JOptionPane.ERROR_MESSAGE);
			Log.log(Log.LEVEL_ERROR, MODULE, "Fetching configuration failed");
			Log.logException(Log.LEVEL_ERROR, MODULE, ex);
		}
	}

	public void resetUIState() {
	}
}