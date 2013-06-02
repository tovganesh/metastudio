/*
 * IDEJRManFramebufferImpl.java
 *
 * Created on November 8, 2005, 7:50 PM
 *
 */

package org.meta.shell.idebeans;

import java.io.*;
import java.awt.*;
import java.text.*;
import java.util.*;
import java.awt.event.*;
import java.awt.image.*;

import javax.swing.*;
import javax.imageio.*;
import javax.imageio.spi.*;
import javax.imageio.stream.*;

import org.meta.shell.idebeans.eventhandlers.MainMenuEventHandlers;

import org.jrman.ui.Framebuffer;
import net.falappa.swing.widgets.JImageViewerPanel;
import net.falappa.imageio.ImageWriterSpiFileFilter;
import org.meta.common.resource.ImageResource;

/**
 * Implementaion of Framebuffer for MeTA Studio. Majorly a copy of
 * <code>org.jrman.ui.FramebufferImpl</code>.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class IDEJRManFramebufferImpl extends JInternalFrame
        implements Framebuffer {
    
    private JImageViewerPanel imagePanel = new JImageViewerPanel();
    private ImageViewerPanelSaveAction save =
            new ImageViewerPanelSaveAction(imagePanel,
                                BufferedImage.TYPE_INT_ARGB);
    private String name;
    
    /** Creates a new instance of IDEJRManFramebufferImpl */
    public IDEJRManFramebufferImpl(String name, BufferedImage image) {
        super("JRMan rendered: " + name, true, true, true, true);
        this.name = name;
        
        save.setEnabled(false);
        
        imagePanel.setImage(image);
        imagePanel.addToolbarAction(save);
        if (image.getType() == BufferedImage.TYPE_INT_ARGB
                || image.getType() == BufferedImage.TYPE_INT_ARGB_PRE) {
            imagePanel.setShowTransparencyPattern(true);
        }
        
        getRootPane().setDoubleBuffered(false);
        getContentPane().add(imagePanel);
        pack();
        
        ImageResource images = ImageResource.getInstance();
        
        // set the frame icon
        setFrameIcon(images.getJrMan());
        
        // add this to the IDE desktop
        MainMenuEventHandlers.getInstance(null).getIdeInstance()
            .getWorkspaceDesktop().addInternalFrame(this, true);
    }
    
    /**
     * Signal that a certain rectangular region has changed
     * @param x top-left x coordinate
     * @param y top-left y coordinate
     * @param w rectangle width
     * @param h rectangle height
     */
    @Override
    public void refresh(int x, int y, int w, int h) {
        imagePanel.repaintImage(x, y, w, h);
    }
    
    /**
     * Signal image is completed
     */
    @Override
    public void completed() {
        save.setEnabled(true);
    }
    
    // the resource bundels
    private static final ResourceBundle messagesBundle=
           ResourceBundle.getBundle(
              net.falappa.imageio.ImageViewerPanelSaveAction.class.getPackage()
                                   .getName() + ".res.ImageViewerPanelActions");
    
    /**
     * inner class to handle save action
     */
    public class ImageViewerPanelSaveAction extends AbstractAction {
        private JImageViewerPanel viewerPanel;
        private int imageType;        
        private IDEFileChooser fc;
        
        /**
         * Constructs and initializes this object
         * @param viewerPanel the <code>JImageViewerPanel</code> this action is 
         *        linked to
         * @param imageType
         */
        public ImageViewerPanelSaveAction(JImageViewerPanel viewerPanel,
                                          int imageType){
            super(messagesBundle.getString(
                            "ImageViewerPanelSaveAction.Save_1")); //$NON-NLS-1$
            assert viewerPanel!=null;
            this.imageType = imageType;
            this.viewerPanel= viewerPanel;
            putValue(SHORT_DESCRIPTION,
                    messagesBundle.getString(
                            "ImageViewerPanelSaveAction.Save_image_to_file_2"));
            //$NON-NLS-1$
            putValue(SMALL_ICON, UIManager.getIcon(
                                     "FileView.floppyDriveIcon")); //$NON-NLS-1$
        }
            
        @Override
        public void actionPerformed(ActionEvent e) {
            if (fc == null) {
                fc = new IDEFileChooser();
                fc.setFileView(new IDEFileView());
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
                    ImageWriterSpiFileFilter ff
                            = new ImageWriterSpiFileFilter(writer);
                    fc.addChoosableFileFilter(ff);
                }
            }
            
            if (fc.showSaveDialog(viewerPanel) == JFileChooser.APPROVE_OPTION) {
                File selectedFile= fc.getSelectedFile();
                
                if (selectedFile != null) {
                    String fileName = selectedFile.getAbsolutePath();
                    ImageWriterSpiFileFilter ff=
                            (ImageWriterSpiFileFilter)fc.getFileFilter();
                    if (!ff.hasCorrectSuffix(fileName))
                        fileName= ff.addSuffix(fileName);
                    selectedFile= new File(fileName);
                    if (selectedFile.exists()) {
                        String message = MessageFormat.format
                          (messagesBundle.getString(
                             "ImageViewerPanelSaveAction.Overwrite_question_5"),
                             //$NON-NLS-1$
                             fileName);
                        if (JOptionPane.NO_OPTION 
                              == JOptionPane.showConfirmDialog (viewerPanel, 
                                   message,
                                   messagesBundle.getString(
                                        "ImageViewerPanelSaveAction.Warning_6"),
                                //$NON-NLS-1$
                                JOptionPane.YES_NO_OPTION, 
                                JOptionPane.WARNING_MESSAGE))
                            return;
                    }
                    writeToFile(selectedFile, ff);
                }
            }
        }
        
        private void writeToFile(File selectedFile, 
                                 ImageWriterSpiFileFilter ff) {
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
            } catch (IOException ioe) {
                JOptionPane.showMessageDialog
                    (viewerPanel, messagesBundle.getString
                    ("ImageViewerPanelSaveAction." +
                        "Error_during_image_saving_message_7"),
                    //$NON-NLS-1$
                    messagesBundle.getString("ImageViewerPanelSaveAction." +
                                              "Error_dialog_title_8"),
                    //$NON-NLS-1$
                    JOptionPane.ERROR_MESSAGE);
                ioe.printStackTrace();
            }
        }
    }
} // end of class IDEJRManFramebufferImpl
