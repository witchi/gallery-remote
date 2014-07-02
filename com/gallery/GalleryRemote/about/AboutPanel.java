package com.gallery.GalleryRemote.about;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;

import com.gallery.GalleryRemote.GalleryRemote;
import com.gallery.GalleryRemote.Log;

/**
 * AboutPanel: scrolling panel of credits for About boxes
 * 
 * @author paour
 */
public class AboutPanel extends JComponent {
	private static final long serialVersionUID = 539994781856850049L;
	private static final String MODULE = "About";

	private final int top;
	private final int bottom;

	ImageIcon image;
	ArrayList<String> text;
	int scrollPosition;
	int maxWidth;
	FontMetrics fm;
	int initialPosition;

	/**
	 * Constructor for the AboutPanel object
	 */
	public AboutPanel(int top, int bottom) {
		this.top = top;
		this.bottom = bottom;

		setFont(UIManager.getFont("Label.font"));
		fm = getFontMetrics(getFont());

		URL imu = getClass().getResource("/rar_about_gr1.png");
		Log.log(Log.LEVEL_TRACE, MODULE,
				"Looking for splash screen in " + imu.toString());
		image = new ImageIcon(imu);

		setBorder(new MatteBorder(1, 1, 1, 1, Color.gray));

		text = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(
				GalleryRemote.instance().properties.getProperty("aboutText"),
				"\n");
		while (st.hasMoreTokens()) {
			String line = st.nextToken();
			text.add(line);
			maxWidth = Math.max(maxWidth, fm.stringWidth(line) + 10);
		}
		initialPosition = getHeight() - this.bottom - (2 * this.top);
		scrollPosition = initialPosition;
	}

	/**
	 * Gets the preferredSize attribute of the AboutPanel object
	 * 
	 * @return The preferredSize value
	 */
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(1 + image.getIconWidth(),
				1 + image.getIconHeight());
	}

	/**
	 * Description of the Method
	 * 
	 * @param g
	 *            Description of Parameter
	 */
	@Override
	public void paintComponent(Graphics g) {

		image.paintIcon(this, g, 1, 1);

		FontMetrics fm = g.getFontMetrics();

		String version = GalleryRemote.instance().properties
				.getProperty("version");
		g.drawString(version, (getWidth() - fm.stringWidth(version)) / 2,
				getHeight() - 5);

		g = g.create((getWidth() - maxWidth) / 2, this.top, maxWidth,
				getHeight() - this.top - this.bottom);

		int height = fm.getHeight();
		int firstLine = scrollPosition / height;

		int firstLineOffset = height - scrollPosition % height;
		int lines = (getHeight() - this.top - this.bottom) / height;

		int y = firstLineOffset;
		g.setColor(new Color(255, 255, 255));
		for (int i = 0; i <= lines; i++) {
			if (i + firstLine >= 0 && i + firstLine < text.size()) {
				String line = (String) text.get(i + firstLine);
				g.drawString(line, (maxWidth - fm.stringWidth(line)) / 2, y);
			}
			y += fm.getHeight();
		}
	}
}