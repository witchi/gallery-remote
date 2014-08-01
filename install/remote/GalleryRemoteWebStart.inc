<?php
/*
 * Gallery - a web based photo album viewer and editor
 * Copyright (C) 2000-2008 Bharat Mediratta
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
 * Foundation, Inc., 51 Franklin Street - Fifth Floor, Boston, MA  02110-1301, USA.
 */

/**
 * Generate a WebStart jnpl file to kick off Gallery Remote
 * @package Remote
 * @subpackage UserInterface
 * @author Pierre-Luc Paour <paour@users.sourceforge.net>
 * @author Andre Rothe <andre.rothe@phosco.info>
 * @version $Revision: 17580 $
 */
class GalleryRemoteWebStartView extends GalleryView {

    /**
     * @see GalleryView::isImmediate
     */
    function isImmediate() {
        return true;
    }

    /**
     * @see GalleryView::isAllowedInEmbedOnly
     */
    function isAllowedInEmbedOnly() {
        return true;
    }

    /**
     * @see GalleryView::shouldSaveSession
     */
    function shouldSaveSession() {
        return false;
    }

    /**
     * @see GalleryView::loadTemplate
     */
    function renderImmediate($status, $error) {
        global $gallery;

        if (!headers_sent()) {
            header("Content-type: application/x-java-jnlp-file");
        }

        $urlGenerator =& $gallery->getUrlGenerator();
        $codebase = $urlGenerator->getCurrentUrlDir(true);
        /*
         * Removed the href attribute in the jnlp element, because it caused WebStart to fail
         * caching the application.
         */
        /*$jnlpFile = $urlGenerator->generateUrl(
                array('view' => 'remote.GalleryRemoteWebStart'),
                array('forceDirect' => true, 'forceFullUrl' => true, 'forceSessionId' => false));*/

        $user = $gallery->getActiveUser();
        if (isset($user)) {
            $username = $user->getUserName();
        }

        echo '<?xml version="1.0" encoding="utf-8"?>' . "\n";
?>
<jnlp spec="7.0" codebase="<?php echo $codebase . 'modules/remote'; ?>">
  <information>
    <title>Gallery Remote</title>
    <vendor>Gallery</vendor>
    <homepage href="http://gallery.menalto.com"/>
    <description>A Java application you can use to upload pictures to your Gallery</description>
    <icon href="images/rar_icon_32.gif"/>
    <icon kind="splash" href="images/rar_about_gr1.png"/>
  </information>
  
  <security>
      <all-permissions />
  </security>
  
  <update check="always" policy="always" />
  
  <resources>
    <java version="1.7+" href="http://java.sun.com/products/autodl/j2se" java-vm-args="-ea" initial-heap-size="128m" max-heap-size="512m" />
    <jar href="applets/GalleryRemote.jar" main="true"/>
    <jar href="applets/img.jar"/>
    <jar href="applets/metadata-extractor-2.1.jar"/>
  </resources>
  
  <application-desc main-class="com.gallery.galleryremote.GalleryRemote">
    <argument>-url</argument>
    <argument><?php echo $codebase; ?></argument>
    <?php if (isset($username)) { ?>
    <argument>-username</argument>
    <argument><?php echo $username; ?></argument>
    <?php } ?>
  </application-desc>
</jnlp>
<?php
        return null;
    }
}
?>
 