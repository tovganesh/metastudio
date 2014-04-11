//Chicchi
//Copyright (C) 2003, Alessandro Falappa
//
//Contact: alessandro@falappa.net
//
//This library is free software; you can redistribute it and/or
//modify it under the terms of the GNU General Public
//License as published by the Free Software Foundation; either
//version 2 of the License, or (at your option) any later version.
//
//This library is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//General Public License for more details.
//
//You should have received a copy of the GNU General Public
//License along with this library; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

package net.falappa.imageio;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.ResourceBundle;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import net.falappa.swing.widgets.JImageViewerPanel;

/**
 * A user <code>Action</code> to add to a <code>JimageViewerPanel</code> to save
 * the currently displayed image trough the Java ImageIO API.
 * Vanilla Java2 SDK 1.4.1 support writing JPEG and PNG file formats. By installing
 * the Java Advanced Imaging ImageIO codecs more formats become available.
 * @see JImageViewerPanel
 *  
 * @author Alessandro Falappa
 */
public class ImageViewerPanelSaveAction extends AbstractAction {
    private JImageViewerPanel viewerPanel;
    private int imageType;
    private static final ResourceBundle messagesBundle=
    ResourceBundle.getBundle(ImageViewerPanelSaveAction.class.getPackage().getName()
                             + ".res.ImageViewerPanelActions");
    private JFileChooser fc;

    /**
     * Constructs and initializes this object
     * @param viewerPanel the <code>JImageViewerPanel</code> this action is linked to
     * @param imageType
     */
    public ImageViewerPanelSaveAction(JImageViewerPanel viewerPanel,int imageType){
        super(messagesBundle.getString("ImageViewerPanelSaveAction.Save_1")); //$NON-NLS-1$
        assert viewerPanel!=null;
        this.imageType = imageType;
        this.viewerPanel= viewerPanel;
        putValue(SHORT_DESCRIPTION, 
                 messagesBundle.getString("ImageViewerPanelSaveAction.Save_image_to_file_2"));
        //$NON-NLS-1$
        putValue(SMALL_ICON, UIManager.getIcon("FileView.floppyDriveIcon")); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if (fc == null) {
            fc= new JFileChooser();
            fc.setAcceptAllFileFilterUsed(false);
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fc.setMultiSelectionEnabled(false);
            fc.setDialogTitle(messagesBundle.getString
                              ("ImageViewerPanelSaveAction.Choose_filename_to_save_4")); 
            //$NON-NLS-1$
            
            // prepare file filters
            IIORegistry theRegistry= IIORegistry.getDefaultInstance();
            Iterator it=
                theRegistry.getServiceProviders(ImageWriterSpi.class, false);
            while (it.hasNext()) {
                ImageWriterSpi writer= (ImageWriterSpi)it.next();
                if((imageType == BufferedImage.TYPE_INT_ARGB
                    || imageType == BufferedImage.TYPE_INT_ARGB_PRE) &&
                   "JPEG".equals(writer.getFormatNames()[0].toUpperCase()))
                    continue;
                ImageWriterSpiFileFilter ff= new ImageWriterSpiFileFilter(writer);
                fc.addChoosableFileFilter(ff);
            }
        }
        if (fc.showSaveDialog(viewerPanel) == JFileChooser.APPROVE_OPTION) {
            File selectedFile= fc.getSelectedFile();
            if (selectedFile != null) {
                String fileName= selectedFile.getAbsolutePath();
                ImageWriterSpiFileFilter ff=
                    (ImageWriterSpiFileFilter)fc.getFileFilter();
                if (!ff.hasCorrectSuffix(fileName))
                    fileName= ff.addSuffix(fileName);
                selectedFile= new File(fileName);
                if (selectedFile.exists()) {
                    String message = MessageFormat.format
                        (messagesBundle
                         .getString("ImageViewerPanelSaveAction.Overwrite_question_5"), 
                         //$NON-NLS-1$
                         fileName);
                    if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog
                        (viewerPanel, message, 
                         messagesBundle.getString("ImageViewerPanelSaveAction.Warning_6"), 
                         //$NON-NLS-1$
                         JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE))
                        return;
                }
                writeToFile(selectedFile, ff);
            }
        }
    }

    private void writeToFile(File selectedFile, ImageWriterSpiFileFilter ff) {
        try {
            ImageOutputStream ios=
                ImageIO.createImageOutputStream(selectedFile);
            ImageWriter iw= ff.getImageWriterSpi().createWriterInstance();
            iw.setOutput(ios);
            ImageWriteParam iwp= iw.getDefaultWriteParam();
            if (iwp.canWriteCompressed()) {
                iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                // set maximum image quality
                iwp.setCompressionQuality(1.f);
            }
            Image image= viewerPanel.getImage();
            BufferedImage bufferedImage;
            if (viewerPanel.getImage() instanceof BufferedImage)
                bufferedImage= (BufferedImage)viewerPanel.getImage();
            else {
                bufferedImage=
                    new BufferedImage(
                                      image.getWidth(null),
                                      image.getHeight(null),
                                      BufferedImage.TYPE_INT_RGB);
                bufferedImage.createGraphics().drawImage(image, 0, 0, null);
            }
            iw.write(null, new IIOImage(bufferedImage, null, null), iwp);
            iw.dispose();
            ios.close();
        }
        catch (IOException ioe) {
            JOptionPane.showMessageDialog
                (viewerPanel, messagesBundle.getString
                 ("ImageViewerPanelSaveAction.Error_during_image_saving_message_7"), 
                 //$NON-NLS-1$
                 messagesBundle.getString("ImageViewerPanelSaveAction.Error_dialog_title_8"), 
                 //$NON-NLS-1$
                 JOptionPane.ERROR_MESSAGE);
            ioe.printStackTrace();
        }
    }
}
