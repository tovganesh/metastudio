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

import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import net.falappa.swing.widgets.JImageViewerPanel;

/**
 * A user <code>Action</code> to add to a <code>JimageViewerPanel</code> to load
 * an image trough the Java ImageIO API.
 * Vanilla Java2 SDK 1.4.1 support reading GIF, JPEG and PNG file formats.
 * By installing the Java Advanced Imaging ImageIO codecs more formats become 
 * available.
 * @see JImageViewerPanel
 *  
 * @author Alessandro Falappa
 */
public class ImageViewerPanelLoadAction extends AbstractAction {
	private JImageViewerPanel viewerPanel;
	private static final ResourceBundle messagesBundle=
		ResourceBundle.getBundle(
			ImageViewerPanelSaveAction.class.getPackage().getName()
				+ ".res.ImageViewerPanelActions");
	private JFileChooser fc;

	/**
	 * Constructs and initializes this object
	 * @param viewerPanel the <code>JImageViewerPanel</code> this action is linked to
	 */
	public ImageViewerPanelLoadAction(JImageViewerPanel viewerPanel) {
		super(messagesBundle.getString("ImageViewerPanelLoadAction.Load_2")); //$NON-NLS-1$
		this.viewerPanel= viewerPanel;
		putValue(SHORT_DESCRIPTION, messagesBundle.getString("ImageViewerPanelLoadAction.Load_image_from_file_3")); //$NON-NLS-1$
		putValue(SMALL_ICON, UIManager.getIcon("Tree.openIcon")); //$NON-NLS-1$
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
			fc.setDialogTitle(messagesBundle.getString("ImageViewerPanelLoadAction.Choose_filename_to_load_5")); //$NON-NLS-1$
			// prepare file filters
			IIORegistry theRegistry= IIORegistry.getDefaultInstance();
			Iterator it=
				theRegistry.getServiceProviders(ImageReaderSpi.class, false);
			while (it.hasNext()) {
				ImageReaderSpi reader= (ImageReaderSpi)it.next();
				ImageReaderSpiFileFilter ff=
					new ImageReaderSpiFileFilter(reader);
				fc.addChoosableFileFilter(ff);
			}
		}
		if (fc.showOpenDialog(viewerPanel) == JFileChooser.APPROVE_OPTION) {
			File selectedFile= fc.getSelectedFile();
			if (selectedFile != null && selectedFile.exists()) {
				//String fileName= selectedFile.getAbsolutePath();
				ImageReaderSpiFileFilter ff=
					(ImageReaderSpiFileFilter)fc.getFileFilter();
				readFromFile(selectedFile, ff);
			}
		}
	}

	/**
	 * @param selectedFile
	 * @param ff
	 */
	private void readFromFile(File selectedFile, ImageReaderSpiFileFilter ff) {
		try {
			ImageInputStream iis= ImageIO.createImageInputStream(selectedFile);
			ImageReader ir= ff.getImageReaderSpi().createReaderInstance();
			ir.setInput(iis);
			// preparing a destination bufferedimage of type INT_RGB will make
			// scrolling and zooming faster (at least on Win2k)
			int idx= ir.getMinIndex();
			BufferedImage bufferedImage=
				new BufferedImage(
					ir.getWidth(idx),
					ir.getHeight(idx),
					BufferedImage.TYPE_INT_RGB);
			ImageReadParam irp= ir.getDefaultReadParam();
			irp.setDestination(bufferedImage);
			bufferedImage= ir.read(idx,irp);
			viewerPanel.setImage(bufferedImage);
			ir.dispose();
			iis.close();
		}
		catch (IOException ioe) {
			JOptionPane.showMessageDialog(viewerPanel, messagesBundle.getString("ImageViewerPanelLoadAction.Error_during_image_loading_7"), //$NON-NLS-1$
			messagesBundle.getString("ImageViewerPanelLoadAction.Error_8"), //$NON-NLS-1$
			JOptionPane.ERROR_MESSAGE);
			ioe.printStackTrace();
		}
	}

}
