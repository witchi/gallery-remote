package com.gallery.GalleryRemote.util;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.JLabel;

import com.gallery.GalleryRemote.Log;

import edu.stanford.ejalbert.BrowserLauncher;

/**
 * Created by IntelliJ IDEA. User: paour Date: Dec 16, 2003
 */
public class BrowserLink extends JLabel implements MouseListener {

	private static final long serialVersionUID = 6359091486724234508L;
	public static final String MODULE = "BrowserLink";
	String url = null;

	public BrowserLink() {
		super();

		setForeground(Color.blue);

		addMouseListener(this);
	}

	public BrowserLink(String url) {
		super(url);

		setForeground(Color.blue);

		addMouseListener(this);
	}

	@Override
	public void setText(String text) {
		url = text;

		super.setText("<html><u>" + text + "</u></html>");
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	/* MouseListener Interface */
	@Override
	public void mouseClicked(MouseEvent e) {
		try {
			BrowserLauncher.openURL(url);
		} catch (IOException e1) {
			Log.logException(Log.LEVEL_ERROR, MODULE, e1);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}
}
