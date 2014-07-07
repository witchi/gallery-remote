package com.gallery.GalleryRemote.prictureinspect;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import com.gallery.GalleryRemote.CoreUtils;
import com.gallery.GalleryRemote.GalleryCommCapabilities;
import com.gallery.GalleryRemote.GalleryRemote;
import com.gallery.GalleryRemote.Log;
import com.gallery.GalleryRemote.model.Album;
import com.gallery.GalleryRemote.model.Picture;
import com.gallery.GalleryRemote.util.GRI18n;
import com.gallery.GalleryRemote.util.ImageUtils;

public class PictureInspectorPresenterImpl implements ActionListener, DocumentListener, PictureInspectorPresenter {

	private static final String MODULE = "PictInspec";

	private PictureInspectorModel model;
	private PictureInspector view;

	private Action nextFocusAction;
	private Action prevFocusAction;
	private Action nextPictureAction;
	private Action prevPictureAction;

	public PictureInspectorPresenterImpl(PictureInspectorModel model, PictureInspector view) {
		this.model = model;
		this.view = view;
		initDocuments();
		initEvents();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		Log.log(Log.LEVEL_INFO, MODULE, "Command selected " + command);

		if (command.equals(PictureInspectorActions.DELETE)) {
			model.deleteSelectedPictures();
		} else if (command.equals(PictureInspectorActions.UP.name())) {
			model.movePicturesUp();
		} else if (command.equals(PictureInspectorActions.DOWN.name())) {
			model.movePicturesDown();
		} else if (command.equals(PictureInspectorActions.ROTATE_LEFT.name())) {
			model.rotatePictureLeft();
		} else if (command.equals(PictureInspectorActions.ROTATE_RIGHT.name())) {
			model.rotatePictureRight();
		} else if (command.equals(PictureInspectorActions.FLIP.name())) {
			model.flipPicture();
		} else if (command.equals(PictureInspectorActions.REFRESH.name())) {
			refresh();
		} else if (command.equals(PictureInspectorActions.REMOVE_EXTRA_FIELDS.name())) {
			view.removeExtraFields();
		}
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		model.documentUpdate(e.getDocument());
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		model.documentUpdate(e.getDocument());
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		model.documentUpdate(e.getDocument());
	}

	@Override
	public Action getNextFocusAction() {
		if (nextFocusAction == null) {
			nextFocusAction = new AbstractAction("Move Focus Forwards") {
				private static final long serialVersionUID = -7481079099741609449L;

				@Override
				public void actionPerformed(ActionEvent evt) {
					((Component) evt.getSource()).transferFocus();
				}
			};
		}
		return nextFocusAction;
	}

	@Override
	public Action getPrevFocusAction() {
		if (prevFocusAction == null) {
			prevFocusAction = new AbstractAction("Move Focus Backwards") {
				private static final long serialVersionUID = -4207478878462672331L;

				@Override
				public void actionPerformed(ActionEvent evt) {
					((Component) evt.getSource()).transferFocusBackward();
				}
			};
		}
		return prevFocusAction;
	}

	@Override
	public Action getNextPictureAction() {
		if (nextPictureAction == null) {
			nextPictureAction = new AbstractAction("Select Next Picture") {
				private static final long serialVersionUID = -3058236737204149574L;

				@Override
				public void actionPerformed(ActionEvent evt) {
					CoreUtils.selectNextPicture();
				}
			};
		}
		return nextPictureAction;
	}

	@Override
	public Action getPrevPictureAction() {
		if (prevPictureAction == null) {
			prevPictureAction = new AbstractAction("Select Prev Picture") {
				private static final long serialVersionUID = 5771498665345070313L;

				@Override
				public void actionPerformed(ActionEvent evt) {
					CoreUtils.selectPrevPicture();
				}
			};
		}
		return prevPictureAction;
	}

	@Override
	public void setEnabled(boolean enabled) {
		view.setEnabled(enabled);
	}

	private void setExtraFields(Map<String, Document> docList) {
		view.setExtraFields(docList);
		Collection<PictureFieldTextArea> fieldSet = view.getExtraFields();
		for (PictureFieldTextArea field : fieldSet) {
			field.getDocument().addDocumentListener(this);
			field.addKeyboardListener(this);
		}
	}

	// called by an action of the model
	private void refresh() {
		Picture p;
		PictureInspectorDTO dto = new PictureInspectorDTO();

		dto.setIconSize(new Dimension(0, GalleryRemote.instance().properties.getThumbnailSize().height + view.getEmptyIconHeight()
				+ view.getIconTextGap()));

		switch (model.getPictureList().size()) {
		case 0:
			dto.setThumbnail(ImageUtils.defaultThumbnail);
			dto.setCapability(false);
			dto.setViewEnabled(false);

			model.setDocumentText("Icon", GRI18n.getString(MODULE, "noPicSel"));
			model.setDocumentText("Path", "");
			model.setDocumentText("Album", "");
			model.setDocumentText("Size", "");
			model.setDocumentText("Caption", "");

			view.refresh(dto);
			model.removeExtraFields();
			break;

		case 1:
			p = model.getPictureList().get(0);
			dto.setThumbnail(model.getThumbnail(p));
			dto.setCapability(model.hasCapability(p, GalleryCommCapabilities.CAPA_UPLOAD_CAPTION));
			dto.setViewEnabled(view.isEnabled());

			if (p.isOnline()) {
				model.setDocumentText("Path", GRI18n.getString(MODULE, "onServer"));
				model.setDocumentText("Icon", p.getName());
			} else {
				model.setDocumentText("Icon", p.getSource().getName());
				model.setDocumentText("Path", p.getSource().getParent());
			}
			model.setDocumentText("Album", p.getParentAlbum().getTitle());
			model.setDocumentText("Size", NumberFormat.getInstance().format(p.getFileSize()) + " bytes");
			model.setDocumentText("Caption", p.getCaption());

			view.refresh(dto);
			setExtraFields(model.getExtraFieldDocuments(p));
			break;

		default:
			p = model.getPictureList().get(0);
			dto.setThumbnail(ImageUtils.defaultThumbnail);
			dto.setCapability(false);
			dto.setViewEnabled(view.isEnabled());

			Object[] params = { new Integer(model.getPictureList().size()) };
			model.setDocumentText("Icon", GRI18n.getString(MODULE, "countElemSel", params));
			model.setDocumentText("Path", "");
			model.setDocumentText("Album", p.getParentAlbum().getTitle());
			model.setDocumentText("Size", NumberFormat.getInstance().format((int) Album.getObjectFileSize(model.getPictureList())) + " bytes");
			model.setDocumentText("Caption", "");

			view.refresh(dto);
			model.removeExtraFields();
			break;
		}
	}

	private void initEvents() {
		view.getDeleteButton().addActionListener(this);
		view.getUpButton().addActionListener(this);
		view.getDownButton().addActionListener(this);
		view.getRotateLeftButton().addActionListener(this);
		view.getRotateRightButton().addActionListener(this);
		view.getFlipButton().addActionListener(this);
		view.getCaption().getDocument().addDocumentListener(this);
		view.getCaption().addKeyboardListener(this);
		model.addActionListener(this);
	}

	private void initDocuments() {
		view.setFieldDocuments(model.getFieldDocuments());
	}
}
