package com.gallery.GalleryRemote.prefs;

import com.gallery.GalleryRemote.Log;
import com.gallery.GalleryRemote.util.GRI18n;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;

/**
 * Created by IntelliJ IDEA.
 * User: paour
 * Date: May 8, 2003
 */
public class GeneralPanel extends PreferencePanel implements PreferenceNames {
	public static final String MODULE = "GeneralPa";

    public static GRI18n grRes = GRI18n.getInstance();

	JLabel icon = new JLabel(grRes.getString(MODULE, "icon"));

	GridBagLayout gridBagLayout1 = new GridBagLayout();
	JPanel jPanel1 = new JPanel();
	JLabel jLabel1 = new JLabel();
	JTextField thumbnailWidth = new JTextField();
	JLabel jLabel2 = new JLabel();
	JTextField thumbnailHeight = new JTextField();
	JPanel jPanel2 = new JPanel();
	JCheckBox savePasswords = new JCheckBox();
	GridBagLayout gridBagLayout2 = new GridBagLayout();
	JLabel jLabel3 = new JLabel();
	JComboBox logLevel = new JComboBox(new String[] {
        grRes.getString(MODULE, "logLevel0"),
        grRes.getString(MODULE, "logLevel1"),
	    grRes.getString(MODULE, "logLevel2"),
        grRes.getString(MODULE, "logLevel3")});
	JPanel jPanel3 = new JPanel();
	JPanel jPanel4 = new JPanel();
	JCheckBox updateCheck = new JCheckBox();
	JCheckBox updateCheckBeta = new JCheckBox();
	GridBagLayout gridBagLayout3 = new GridBagLayout();
	JPanel jPanel5 = new JPanel();
	GridBagLayout gridBagLayout4 = new GridBagLayout();
	JPanel jPanel6 = new JPanel();
	JPanel jPanel7 = new JPanel();
	JCheckBox showThumbnails = new JCheckBox();

	public JLabel getIcon() {
		return icon;
	}

	public void readProperties(GalleryProperties props) {
		showThumbnails.setSelected(props.getBooleanProperty(SHOW_THUMBNAILS));
		thumbnailWidth.setText("" + (int) props.getDimensionProperty(THUMBNAIL_SIZE).getWidth());
		thumbnailHeight.setText("" + (int) props.getDimensionProperty(THUMBNAIL_SIZE).getHeight());

		savePasswords.setSelected(props.getBooleanProperty(SAVE_PASSWORDS));
		logLevel.setSelectedIndex(props.getIntProperty(LOG_LEVEL));

		updateCheck.setSelected(props.getBooleanProperty(UPDATE_CHECK));
		updateCheckBeta.setSelected(props.getBooleanProperty(UPDATE_CHECK_BETA));
	}

	public void writeProperties(GalleryProperties props) {
		props.setBooleanProperty(SHOW_THUMBNAILS, showThumbnails.isSelected());

		try {
			Dimension d = new Dimension(Integer.parseInt(thumbnailWidth.getText()), Integer.parseInt(thumbnailHeight.getText()));
			props.setDimensionProperty(THUMBNAIL_SIZE, d);
		} catch (Exception e) {
			Log.log(Log.ERROR, MODULE, "Thumbnail size should be integer numbers");
		}

		props.setBooleanProperty(SAVE_PASSWORDS, savePasswords.isSelected());
		props.setIntProperty(LOG_LEVEL, logLevel.getSelectedIndex());

		props.setBooleanProperty(UPDATE_CHECK, updateCheck.isSelected());
		props.setBooleanProperty(UPDATE_CHECK_BETA, updateCheckBeta.isSelected());
	}

	public void buildUI() {
		jbInit();
	}

	private void jbInit() {
		this.setLayout(gridBagLayout1);
		jLabel1.setText(grRes.getString(MODULE, "thumbS"));
		jPanel1.setLayout(gridBagLayout4);
		thumbnailWidth.setMinimumSize(new Dimension(25, 21));
		thumbnailWidth.setPreferredSize(new Dimension(25, 21));
		thumbnailWidth.setToolTipText(grRes.getString(MODULE, "thumbW"));
		jLabel2.setText(grRes.getString(MODULE, "label2"));
		thumbnailHeight.setMinimumSize(new Dimension(25, 21));
		thumbnailHeight.setPreferredSize(new Dimension(25, 21));
		thumbnailHeight.setToolTipText(grRes.getString(MODULE, "thumbH"));
		jPanel1.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(Color.white,new Color(148, 145, 140)),grRes.getString(MODULE, "thumb")));
		jPanel2.setLayout(gridBagLayout2);
		savePasswords.setToolTipText(grRes.getString(MODULE, "savePwdTip"));
		savePasswords.setText(grRes.getString(MODULE, "savePwd"));
		jPanel2.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(Color.white,new Color(148, 145, 140)),grRes.getString(MODULE, "log_priv")));
		jLabel3.setText(grRes.getString(MODULE, "logLevel"));
		logLevel.setToolTipText(grRes.getString(MODULE, "logLevelTip"));
		logLevel.setActionCommand("comboBoxChanged");
		logLevel.setSelectedIndex(-1);
		jPanel4.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(Color.white,new Color(148, 145, 140)),grRes.getString(MODULE, "versionCheck")));
		jPanel4.setMaximumSize(new Dimension(32767, 32767));
		jPanel4.setLayout(gridBagLayout3);
		updateCheck.setToolTipText(grRes.getString(MODULE, "updateCheckTip"));
		updateCheck.setText(grRes.getString(MODULE, "updateCheck"));
		updateCheckBeta.setToolTipText(grRes.getString(MODULE, "updateBetaCheckTip"));
		updateCheckBeta.setText(grRes.getString(MODULE, "updateBetaCheck"));
		showThumbnails.setToolTipText(grRes.getString(MODULE, "showThumbTip"));
		showThumbnails.setText(grRes.getString(MODULE, "showThumb"));
		this.add(jPanel1,     new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0
				,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
		jPanel1.add(jLabel1,       new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));
		jPanel1.add(thumbnailWidth,      new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		jPanel1.add(jLabel2,       new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
		jPanel1.add(thumbnailHeight,      new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		jPanel1.add(jPanel6,   new GridBagConstraints(4, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		jPanel1.add(showThumbnails,   new GridBagConstraints(0, 0, 4, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		this.add(jPanel2,   new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0
				,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
		jPanel2.add(savePasswords,      new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
				,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		jPanel2.add(jLabel3,     new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
				,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));
		jPanel2.add(logLevel,   new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
				,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		jPanel2.add(jPanel3,   new GridBagConstraints(3, 0, 1, 1, 1.0, 0.0
				,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		this.add(jPanel4,   new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0
				,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		jPanel4.add(updateCheck,    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
				,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		jPanel4.add(updateCheckBeta,    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
				,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		jPanel4.add(jPanel5,   new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
				,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		this.add(jPanel7,  new GridBagConstraints(0, 3, 1, 1, 1.0, 1.0
				,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
	}
}

