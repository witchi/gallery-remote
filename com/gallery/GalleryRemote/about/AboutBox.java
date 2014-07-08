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
package com.gallery.GalleryRemote.about;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.MouseEvent;

import javax.swing.JDialog;

import com.gallery.GalleryRemote.util.DialogUtil;
import com.gallery.GalleryRemote.util.GRI18n;

/**
 * Gallery Remote About Box
 * 
 * @author paour
 * @author arothe
 */
public class AboutBox extends JDialog {

	private static final long serialVersionUID = 117092856826918304L;
	private static final String MODULE = "About";
	private static final int TOP = 5;
	private static final int BOTTOM = 105;
	private final AnimationThread thread;
	private AboutPanel panel;

	public AboutBox() {
		super();
		this.thread = new AnimationThread(getAboutPanel());
		initUI();
	}

	public AboutBox(Frame owner) {
		super(owner);
		this.thread = new AnimationThread(getAboutPanel());
		initUI();
	}

	private AboutPanel getAboutPanel() {
		if (panel == null) {
			panel = new AboutPanel(TOP, BOTTOM);
		}
		return panel;
	}

	private void initUI() {
		setModal(true);
		setUndecorated(true);

		getContentPane().add(getAboutPanel(), BorderLayout.CENTER);
		setTitle(GRI18n.getString(MODULE, "title"));

		pack();

		DialogUtil.center(this);

		addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				setVisible(false);
				dispose();
			}
		});
	}

	/**
	 * Adds a feature to the Notify attribute
	 */
	@Override
	public void addNotify() {
		super.addNotify();
		thread.start();
	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		thread.kill();
	}
}
