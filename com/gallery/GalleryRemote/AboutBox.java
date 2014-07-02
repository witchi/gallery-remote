/*
 * Gallery Remote - a File Upload Utility for Gallery
 *
 * Gallery - a web based photo album viewer and editor
 * Copyright (C) 2000-2001 Bharat Mediratta
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.gallery.GalleryRemote;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;

import com.gallery.GalleryRemote.about.AboutPanel;
import com.gallery.GalleryRemote.util.DialogUtil;
import com.gallery.GalleryRemote.util.GRI18n;

/**
 * Gallery Remote About Box
 * 
 * @author paour
 */
public class AboutBox extends JDialog {
	
	private static final long serialVersionUID = 117092856826918304L;
	private static final String MODULE = "About";
	private static final int TOP = 5;
	private static final int BOTTOM = 105;
	private final AnimationThread thread;

	/**
	 * Constructor for the AboutBox object
	 */
	public AboutBox() {
		super();
		this.thread = new AnimationThread();
		initUI();
	}

	/**
	 * Constructor for the AboutBox object
	 * 
	 * @param owner
	 *            Description of Parameter
	 */
	public AboutBox(Frame owner) {
		super(owner);
		this.thread = new AnimationThread();
		initUI();
	}

	private void initUI() {
		setModal(true);
		getContentPane().add(new AboutPanel(TOP, BOTTOM), BorderLayout.CENTER);
		setTitle(GRI18n.getString(MODULE, "title"));

		pack();

		DialogUtil.center(this);

		addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				thisWindowClosing();
			}
		});
		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				thisWindowClosing();
			}
		});
	}

	// Close the window when the box is clicked
	private void thisWindowClosing() {
		setVisible(false);
		dispose();
	}
	
	/**
	 * Adds a feature to the Notify attribute of the AboutPanel object
	 */
	@Override
	public void addNotify() {
		super.addNotify();
		thread.start();
	}

	/**
	 * Description of the Method
	 */
	@Override
	public void removeNotify() {
		super.removeNotify();
		thread.kill();
	}
}
