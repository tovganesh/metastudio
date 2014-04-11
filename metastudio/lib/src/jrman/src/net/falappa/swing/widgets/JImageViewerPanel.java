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

package net.falappa.swing.widgets;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.util.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import net.falappa.swing.CheckeredPaint;

/**
 * <CODE>JImageViewerPanel</CODE> is a swing visual component to view images which
 * supports scrolling and zooming.
 * <P>Zooming and scrolling are performed &quot;on the fly&quot; during panel
 * repainting trough a variant of the <code>java.awt.Graphics.drawImage()</code>
 * method and only the visible or to be updated portions of the image are drawn.
 * <BR>The zooming commands are accessed either trough a toolbar located on the top
 * border or a context menu brought up by right clicking with the mouse inside the
 * panel.
 * <BR>The classic four zooming commands are available: zoom in, zoom out, zoom
 * to fit, and original size. Through the toolbar you can also specify a custom
 * zoom level, integer percentage, or choose it from a set of predefined zoom levels.
 * <BR>Addition and removal of user supplied <CODE>Action</CODE> objects to the toolbar
 * and the context menu is supported.
 * <BR>The textual messages of the basic zoom commands are localized into english
 * and italian languages.
 * 
 * @author Alessandro Falappa
 */
public class JImageViewerPanel extends JPanel {
	private ZoomScrollImageView zoomView= new ZoomScrollImageView();
	private ZoomToolBar zoomToolbar= new ZoomToolBar();
	private boolean popupMenuEnabled= false;
	private transient JScrollPane scroller= new JScrollPane();
	private transient Point mouseDownPoint= null;
	private transient MouseAdapter mouseAdapter= new MouseAdapter() {
		public void mousePressed(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e))
				mouseDownPoint= e.getPoint();
		}

		public void mouseReleased(MouseEvent e) {
			if (popupMenuEnabled && e.isPopupTrigger())
				zoomToolbar.showContextMenu(zoomView, e.getX(), e.getY());
			if (SwingUtilities.isLeftMouseButton(e))
				mouseDownPoint= null;
		}
	};
	// code for a primitive mouse panning function
	//  private transient MouseMotionAdapter mouseMotionAdapter=new MouseMotionAdapter(){
	//      public void mouseDragged(MouseEvent e){
	//        int scrollX=e.getPoint().x-mouseDownPoint.x;
	//        int scrollY=e.getPoint().y-mouseDownPoint.y;
	//        Point corrected=SwingUtilities.convertPoint(zoomView,scrollX,scrollY,scroller);
	//        scroller.getViewport().setViewPosition(corrected);
	//      }
	//    };

	/** Creates an empty JImageViewerPanel */
	public JImageViewerPanel() {
		this(null);
	}

	/**
	 * Creates an initializes an JImageViewerPanel.
	 * @param im the image to display
	 */
	public JImageViewerPanel(Image im) {
		// code for a primitive mouse panning function
		//    zoomView.addMouseMotionListener(mouseMotionAdapter);
		//    scroller.addMouseMotionListener(mouseMotionAdapter);
		zoomView.addMouseListener(mouseAdapter);
		zoomView.setImage(im);
		zoomToolbar.setZoomScrollView(zoomView);
		scroller.setViewportView(zoomView);
		scroller.addMouseListener(mouseAdapter);
		this.setLayout(new BorderLayout());
		this.setBorder(new EmptyBorder(new Insets(2, 2, 2, 2)));
		this.add(zoomToolbar, BorderLayout.NORTH);
		this.add(scroller, BorderLayout.CENTER);
	}

	/**
	 * Tells wheter the toolbar is visible.
	 * @return true if the toolbar is visible false otherwise.
	 */
	public boolean isToolBarVisible() {
		return zoomToolbar.isVisible();
	}

	/**
	 * Sets the visibility of the toolbar.
	 * @param toolBarVisible true to show the toolbar false otherwise
	 */
	public void setToolBarVisible(boolean toolBarVisible) {
		zoomToolbar.setVisible(toolBarVisible);
	}

	/**
	 * Returns the image currently displayed in the panel.
	 * @return the current image or null if no image.
	 */
	public Image getImage() {
		return zoomView.getImage();
	}

	/**
	 * Specifies the image to display.
	 * @param image the new image to display or null for no image.
	 */
	public void setImage(Image image) {
		Image oldImage= zoomView.getImage();
		zoomView.setImage(image);
		firePropertyChange("image", oldImage, image);
	}

	/**
	 * Arbitrarily zooms by directly setting the zoom factor.
	 * @param zoomPercentage the new zoom factor, will be clamped to the range [6;1600]
	 */
	public void setZoomFactor(int zoomPercentage) {
		zoomToolbar.setZoomFactor(zoomPercentage);
	}

	/**
	 * Returns the current zoom factor.
	 * @return the zoom percentage.
	 */
	public int getZoomFactor() {
		return zoomView.getZoomFactor();
	}

	/**
	     * Sets the background color of this component (toolbar and little outer border).
	 * @param bg the desired background <code>Color</code>
	 * @see java.awt.Component#setBackground(java.awt.Color)
	 */
	public void setBackground(Color bg) {
		super.setBackground(bg);
		if (zoomToolbar != null)
			zoomToolbar.setBackground(bg);
	}

	/**
	 * Gets the background color of this component.
	 * @return this component's background color; if this component does
	 * 		not have a background color,
	 *		the background color of its parent is returned
	 * @see java.awt.Component#getBackground()
	 */
	public Color getBackground() {
		return super.getBackground();
	}

	/**
	 * Returns the current color of the image canvas.
	 * @return the color of the image canvas
	 */
	public Color getBackgroundImageArea() {
		return scroller.getViewport().getBackground();
	}

	/**
	 * Sets the color of the image canvas.
	 * @param imageAreaBackground the new color of the canvas
	 */
	public void setBackgroundImageArea(Color imageAreaBackground) {
		if (scroller != null)
			scroller.getViewport().setBackground(imageAreaBackground);
	}

	/**
	 * Enables or disables this component.
	 * @param enabled true to enable the component false to disable it.
	 */
	public void setEnabled(boolean enabled) {
		boolean oldEnabled= isEnabled();
		super.setEnabled(enabled);
		zoomToolbar.setEnabled(enabled);
		firePropertyChange("enabled", oldEnabled, enabled);
	}

	/**
	 * Tells wheter the right mouse click context menu is enabled.
	 * @return true if the context menu is enabled false otherwise
	 */
	public boolean isPopupMenuEnabled() {
		return popupMenuEnabled;
	}

	/**
	 * Enables or disables the the right mouse click context menu.
	     * @param enablePopup true to enable the the right mouse click context menu false
	 * to disable it
	 */
	public void setPopupMenuEnabled(boolean enablePopup) {
		boolean oldPopupMenuEnabled= popupMenuEnabled;
		popupMenuEnabled= enablePopup;
		firePropertyChange(
			"popupMenuEnabled",
			oldPopupMenuEnabled,
			popupMenuEnabled);
	}

	/**
	 * Adds a custom user action to the toolbar and context menu. The <CODE>Action</CODE>
	 * object should define at least the <CODE>NAME</CODE> property but the
	 * <CODE>SMALL_ICON</CODE> and the <CODE>SHORT_DESCRIPTION</CODE> are higly
	 * desirable as well.
	 * @param a the custom user action to add
	 */
	public void addToolbarAction(Action a) {
		if (a != null)
			zoomToolbar.add(a);
	}

	/**
	 * Removes all the custom user actions from both the toolbar and the context
	 * menu.
	 */
	public void removeToolbarActions() {
		zoomToolbar.removeActions();
	}

	/**
	     * Removes the specified custom user action from both the toolbar and the context
	 * menu.
	 * @param a the user action object to remove
	 */
	public void removeToolbarAction(Action a) {
		zoomToolbar.removeAction(a);
	}

	/**
	 * Repaints the whole image area.
	 */
	public void repaintImage() {
		zoomView.repaint();
	}

	/**
	 * Repaints the specified rectangular image area.
	 * @param xmin the rectangle upper left corner x coordinate
	 * @param ymin the rectangle upper left corner y coordinate
	 * @param w the rectangle width
	 * @param h the rectangle height
	 */
	public void repaintImage(int xmin, int ymin, int w, int h) {
		zoomView.repaint(xmin, ymin, w, h);
	}

	/**
	 * Tells whether the transparency pattern, a white and light gray checker, is
	 * drawn or not.
	 * @return true if transparency pattern is drawn
	 */
	public boolean isShowTransparencyPattern() {
		return zoomView.isShowTransparencyPattern();
	}

	/**
	 * Specify if a white and light gray checker pattern should be drawn to
	 * highlight transparent areas of the image.
	 * The pattern is not influenced by the current zoom level.
	 * @param flag if true the transparency pattern is drawn
	 */
	public void setShowTransparencyPattern(boolean flag) {
		zoomView.setShowTransparencyPattern(flag);
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#addPropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
		super.addPropertyChangeListener(listener);
		zoomView.addPropertyChangeListener(listener);
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#addPropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
	 */
	public synchronized void addPropertyChangeListener(
		String propertyName,
		PropertyChangeListener listener) {
		super.addPropertyChangeListener(propertyName, listener);
		zoomView.addPropertyChangeListener(propertyName, listener);
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#removePropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
		super.removePropertyChangeListener(listener);
		zoomView.removePropertyChangeListener(listener);
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#removePropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
	 */
	public synchronized void removePropertyChangeListener(
		String propertyName,
		PropertyChangeListener listener) {
		super.removePropertyChangeListener(propertyName, listener);
		zoomView.removePropertyChangeListener(propertyName, listener);
	}

}

/**
 * This class realizes the toolbar and the context menu functionalities setting
 * the appropriate zoom factor on the related <CODE>ZoomView</CODE> object.
 * <P>Furthermore it manages the set of custom user actions and their addition/
 * removal from the toolbar and the context menu.
 * @author Alessandro Falappa
 */
class ZoomToolBar extends JToolBar {
	private static ResourceBundle res=
		ResourceBundle.getBundle(
			ZoomToolBar.class.getPackage().getName()
				+ ".res.JImageViewerPanel");
	private JButton bNoZoom= new JButton();
	private JComboBox cbZoomValue= new JComboBox(zoomPercentages);
	private JButton bZoomOut= new JButton();
	private JButton bZoomIn= new JButton();
	private JButton bZoomToFit= new JButton();
	private static final Integer[] zoomPercentages=
		{
			new Integer(6),
			new Integer(12),
			new Integer(25),
			new Integer(33),
			new Integer(50),
			new Integer(66),
			new Integer(75),
			new Integer(100),
			new Integer(200),
			new Integer(300),
			new Integer(400),
			new Integer(600),
			new Integer(800),
			new Integer(1600)};
	private ZoomScrollImageView zoomScrollView;
	private transient JPopupMenu contextMenu= null;
	private Map<Action,JButton> userActions= new HashMap<Action, JButton>(5);
	private static final Insets buttonsInsets= new Insets(1, 1, 1, 1);
	private transient final ActionListener zoomInActionListener=
		new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			bZoomIn_actionPerformed(e);
		}
	};
	private transient final ActionListener zoomOutActionListener=
		new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			bZoomOut_actionPerformed(e);
		}
	};
	private transient final ActionListener noZoomActionListener=
		new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			bNoZoom_actionPerformed(e);
		}
	};
	private transient final ActionListener zoomToFitActionListener=
		new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			bZoomToFit_actionPerformed(e);
		}
	};
	private static final ImageIcon noZoomIcon=
		new ImageIcon(ZoomToolBar.class.getResource("res/NoZoom16.gif"));
	private static final ImageIcon zoomOutIcon=
		new ImageIcon(ZoomToolBar.class.getResource("res/ZoomOut16.gif"));
	private static final ImageIcon ZoomInIcon=
		new ImageIcon(ZoomToolBar.class.getResource("res/ZoomIn16.gif"));
	private static final ImageIcon zoomToFitIcon=
		new ImageIcon(ZoomToolBar.class.getResource("res/ZoomToFit16.gif"));

	ZoomToolBar() {
		cbZoomValue.setEnabled(false);
		cbZoomValue.setMaximumSize(cbZoomValue.getPreferredSize());
		//to make the combo box not get all the remaining space in the toolbar
		cbZoomValue.setToolTipText(res.getString("cbZoomValue_ToolTipText"));
		cbZoomValue.setEditable(true);
		cbZoomValue.setMaximumRowCount(14);
		cbZoomValue.setSelectedItem(new Integer(100));
		cbZoomValue.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				cbZoomValue_itemStateChanged(e);
			}
		});
		bNoZoom.setFocusPainted(false);
		bNoZoom.setEnabled(false);
		bNoZoom.setToolTipText(res.getString("bNoZoom_ToolTipText"));
		bNoZoom.setMargin(buttonsInsets);
		bNoZoom.setIcon(noZoomIcon);
		bNoZoom.addActionListener(noZoomActionListener);
		bZoomOut.setFocusPainted(false);
		bZoomOut.setEnabled(false);
		bZoomOut.setToolTipText(res.getString("bZoomOut_ToolTipText"));
		bZoomOut.setMargin(buttonsInsets);
		bZoomOut.setIcon(zoomOutIcon);
		bZoomOut.addActionListener(zoomOutActionListener);
		bZoomIn.setFocusPainted(false);
		bZoomIn.setEnabled(false);
		bZoomIn.setToolTipText(res.getString("bZoomIn_ToolTipText"));
		bZoomIn.setMargin(buttonsInsets);
		bZoomIn.setIcon(ZoomInIcon);
		bZoomIn.addActionListener(zoomInActionListener);
		bZoomToFit.setFocusPainted(false);
		bZoomToFit.setEnabled(false);
		bZoomToFit.setToolTipText(res.getString("bZoomToFit_ToolTipText"));
		bZoomToFit.setMargin(buttonsInsets);
		bZoomToFit.setIcon(zoomToFitIcon);
		bZoomToFit.addActionListener(zoomToFitActionListener);
		this.setFloatable(false);
		this.add(bZoomIn);
		this.add(bZoomOut);
		this.addSeparator();
		this.add(bNoZoom);
		this.add(bZoomToFit);
		this.addSeparator();
		this.add(cbZoomValue);
	}

	/**
	 * Programatically set the current zoom factor.
	 * @param zoomPercentage the new zoom percentage
	 */
	void setZoomFactor(int zoomPercentage) {
		cbZoomValue.setSelectedItem(new Integer(zoomPercentage));
	}

	/**
	 * Sets the <CODE>ZoomView</CODE> this toolbar is bound to.
	 * @param zoomView the controlled zoom view
	 */
	void setZoomScrollView(ZoomScrollImageView zoomView) {
		this.zoomScrollView= zoomView;
		bZoomIn.setEnabled(zoomView != null);
		bZoomOut.setEnabled(zoomView != null);
		bNoZoom.setEnabled(zoomView != null);
		bZoomToFit.setEnabled(zoomView != null);
		cbZoomValue.setEnabled(zoomView != null);
	}

	private void setContextMenuEnabled(boolean b) {
		if (contextMenu != null) {
			contextMenu.setEnabled(b);
			for (int i= 0; i < contextMenu.getComponentCount(); i++) {
				contextMenu.getComponent(i).setEnabled(b);
			}
		}
	}

	/**
	 * Shows the context menu.
	 * The menu is created or recreated if not already available.
	 * @param component the Component to show the menu into
	 * @param x the x coordinate of the topleft corner of the context menu
	 * @param y the y coordinate of the topleft corner of the context menu
	 */
	void showContextMenu(Component component, int x, int y) {
		if (contextMenu == null) {
			contextMenu= new JPopupMenu();
			JMenuItem menuItem=
				new JMenuItem(res.getString("bZoomIn_ToolTipText"), ZoomInIcon);
			menuItem.addActionListener(zoomInActionListener);
			contextMenu.add(menuItem);
			menuItem=
				new JMenuItem(
					res.getString("bZoomOut_ToolTipText"),
					zoomOutIcon);
			menuItem.addActionListener(zoomOutActionListener);
			contextMenu.add(menuItem);
			contextMenu.addSeparator();
			menuItem=
				new JMenuItem(res.getString("bNoZoom_ToolTipText"), noZoomIcon);
			menuItem.addActionListener(noZoomActionListener);
			contextMenu.add(menuItem);
			menuItem=
				new JMenuItem(
					res.getString("bZoomToFit_ToolTipText"),
					zoomToFitIcon);
			menuItem.addActionListener(zoomToFitActionListener);
			contextMenu.add(menuItem);
			if (!userActions.isEmpty()) {
				contextMenu.addSeparator();
				Iterator i= userActions.keySet().iterator();
				while (i.hasNext()) {
					JMenuItem mi= new JMenuItem((Action)i.next());
					contextMenu.add(mi);
				}
			}
			setContextMenuEnabled(isEnabled());
		}
		contextMenu.show(component, x, y);
	}

	/** Resets the UI property with a value from the current look and feel.
	 *
	 * @see JComponent#updateUI()
	 *
	 */
	public void updateUI() {
		contextMenu= null;
	}

	private int findNearestPercentage() {
		int nearest=
			Arrays.binarySearch(
				zoomPercentages,
				(Integer)cbZoomValue.getSelectedItem());
		return nearest < 0 ? (-nearest - 1) : nearest;
	}

	void bZoomIn_actionPerformed(ActionEvent e) {
		// if the selected item is not one of the predefined zoom percentages find
		// the nearest value to zoom in to
		if (cbZoomValue.getSelectedIndex() < 0) {
			int nearest= findNearestPercentage();
			cbZoomValue.setSelectedIndex(
				nearest >= zoomPercentages.length
					? (zoomPercentages.length - 1)
					: nearest);
		}
		else // select the next predefined zoom level
			if (cbZoomValue.getSelectedIndex()
				< (cbZoomValue.getItemCount() - 1))
				cbZoomValue.setSelectedIndex(
					cbZoomValue.getSelectedIndex() + 1);
	}

	void bZoomOut_actionPerformed(ActionEvent e) {
		// if the selected item is not one of the predefined zoom percentages find
		// the nearest value to zoom out to
		if (cbZoomValue.getSelectedIndex() < 0) {
			int nearest= findNearestPercentage();
			cbZoomValue.setSelectedIndex(nearest > 0 ? nearest - 1 : 0);
		}
		else // select the previous predefined zoom level
			if (cbZoomValue.getSelectedIndex() > 0)
				cbZoomValue.setSelectedIndex(
					cbZoomValue.getSelectedIndex() - 1);
	}

	void bNoZoom_actionPerformed(ActionEvent e) {
		cbZoomValue.setSelectedItem(new Integer(100));
	}

	void bZoomToFit_actionPerformed(ActionEvent e) {
		// retrieve the ancestor JScrollPane visible area size
		Container c=
			SwingUtilities.getAncestorOfClass(
				JScrollPane.class,
				zoomScrollView);
		Insets i= c.getInsets();
		Dimension zs= c.getSize();
		zs.height -= (i.top + i.bottom);
		zs.width -= (i.left + i.right);
		//calc the ratios along vertical and horizontal directions
		int xratio= 100 * zs.width / zoomScrollView.getImage().getWidth(null);
		int yratio= 100 * zs.height / zoomScrollView.getImage().getHeight(null);
		//set the smaller as new zoom factor
		cbZoomValue.setSelectedItem(
			new Integer(xratio < yratio ? xratio : yratio));
	}

	void cbZoomValue_itemStateChanged(ItemEvent e) {
		if (zoomScrollView != null)
			try {
				int zoomValue=
					((Integer)cbZoomValue.getSelectedItem()).intValue();
				//clamp the entered value
				if (zoomValue > zoomScrollView.getMaxZoomFactor())
					zoomValue= zoomScrollView.getMaxZoomFactor();
				if (zoomValue < zoomScrollView.getMinZoomFactor())
					zoomValue= zoomScrollView.getMinZoomFactor();
				zoomScrollView.setZoomFactor(zoomValue);
			}
			catch (Exception ex) {
				cbZoomValue.setSelectedItem(new Integer(100));
			}
	}

	/**
	 * Adds a custom user action to the toolbar and context menu. The <CODE>Action</CODE>
	 * object should define at least the <CODE>NAME</CODE> property but the
	 * <CODE>SMALL_ICON</CODE> and the <CODE>SHORT_DESCRIPTION</CODE> are higly
	 * desirable as well.
	 * @param a the custom user action to add
	 * @return a reference to the toolbar button bound to the custom user action
	 */
	public JButton add(Action a) {
		if (userActions.isEmpty())
			addSeparator();
		JButton newlyAdded= super.add(a);
		newlyAdded.setMargin(new Insets(1, 1, 1, 1));
		newlyAdded.setFocusPainted(false);
		userActions.put(a, newlyAdded);
		contextMenu= null;
		return newlyAdded;
	}

	/**
	 * Removes all the custom user actions from both the toolbar and the context
	 * menu.
	 */
	void removeActions() {
		for (int i= 7; i < getComponentCount(); i++)
			remove(i);
		userActions.clear();
		contextMenu= null;
	}

	/**
	     * Removes the specified custom user action from both the toolbar and the context
	 * menu.
	 * @param a the user action object to remove
	 */
	void removeAction(Action a) {
		JButton b= (JButton)userActions.get(a);
		if (b != null) {
			remove(b);
			userActions.remove(a);
			contextMenu= null;
		}
	}

	/**
	 * Enables or disables this component.
	 * @param enabled true to enable the component false to disable it.
	 */
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		for (int i= 0; i < getComponentCount(); i++)
			getComponent(i).setEnabled(enabled);
		setContextMenuEnabled(enabled);
	}

}

/**
     * An image display area supporting scrolling and &quot;on the fly&quot; zooming.
 * Scrolling support is achieved by accounting for the clip region when repainting and
 * by implementing the Scrollable interface to interact with a JScrollPane container.
 * Zooming is obtained &quot;on the fly&quot; when repainting and is governed by an
 * integer zoom factor representing the percentage respect to the original (the factor is
 * mantained in the range 6-1600).
     * le dimensioni preferenziali riportate allo scrollpane contenitore sono pari a
 * un quarto dell'immagine da visualizzare oppure 200,200
 * @see Scrollable
 * @author Alessandro Falappa
 * @version 1.0
 */
class ZoomScrollImageView
	extends JComponent
	implements ScrollPaneConstants, Scrollable {
	private Image image;
	private static final Dimension prefScrollableDims= new Dimension(200, 200);
	private int zoomFactor= 100;
	private int zoomStep= 2;
	private int maxZoom= 1600;
	private int minZoom= 6;
	private CheckeredPaint cp=
		CheckeredPaint.createPreset(CheckeredPaint.PRESET_LIGHT_TONE_SMALL);
	private boolean showTransparencyPattern= false;

	private transient Rectangle destRect= new Rectangle();
	private transient Rectangle sourceRect= new Rectangle();
	/**
	 * Default constructor
	 */
	ZoomScrollImageView() {
	}

	/**
	 * Initializing constructor
	 * @param im an Image object to display
	 */
	ZoomScrollImageView(Image im) {
		setImage(im);
	}

	/**
	 * Called when view needs update
	 */
	private void updateView() {
		if (image != null) {
			setPreferredSize(
				new Dimension(
					(image.getWidth(null) * zoomFactor) / 100,
					(image.getHeight(null) * zoomFactor) / 100));
			revalidate();
		}
		repaint();
	}

	/**
	 * Setter method for the managed image.
	 * @param image an Image object
	 */
	void setImage(Image image) {
		if (this.image != image) {
			this.image= image;
			// notify swing architecture we are going to paint images in the view
			// this is needed for example to correctly display animated gifs
			if (this.image != null)
				prepareImage(this.image, this);
			updateView();
		}
	}

	/**
	 * Getter method for the managed image.
	 * @return a reference to an Image object or null if no image has been set.
	 */
	Image getImage() {
		return image;
	}

	/**
	 * Zooms in of a fixed step by increasing the zoom factor.
	 */
	void zoomIn() {
		if (zoomFactor < maxZoom)
			zoomFactor *= zoomStep;
		updateView();
	}

	/**
	 * Zooms out of a fixed step by decreasing the zoom factor.
	 */
	void zoomOut() {
		if (zoomFactor > minZoom)
			zoomFactor /= zoomStep;
		updateView();
	}

	/**
	 * Returns to no zoom state by setting the zoom factor to 100.
	 */
	void zoomActualPixels() {
		setZoomFactor(100);
	}

	/**
	 * Arbitrarily zooms by directly setting the zoom factor.
	 * @param zoomPercentage the new zoom factor, will be clamped to the range [6;1600]
	 */
	void setZoomFactor(int zoomPercentage) {
		int oldZoomFactor= zoomFactor;
		if (zoomPercentage > maxZoom)
			zoomFactor= maxZoom;
		else if (zoomPercentage < minZoom)
			zoomFactor= minZoom;
		else
			zoomFactor= zoomPercentage;
		updateView();
		firePropertyChange("zoomFactor", oldZoomFactor, zoomFactor);
	}

	/**
	 * Returns the current zoom factor.
	 * @return the zoom percentage.
	 */
	int getZoomFactor() {
		return zoomFactor;
	}

	/**
	 * Returns the minimum supported zoom factor.
	 * @return the minimum zoom factor
	 */
	int getMinZoomFactor() {
		return minZoom;
	}

	/**
	 * Returns the maximum supported zoom factor.
	 * @return the maximum zoom factor
	 */
	int getMaxZoomFactor() {
		return maxZoom;
	}

	//override of JComponent method
	protected void paintComponent(Graphics g) {
		Graphics2D g2= (Graphics2D)g;
		g2.getClipBounds(destRect);
		if (showTransparencyPattern) {
			g2.setPaint(cp);
			g2.fillRect(
				destRect.x,
				destRect.y,
				destRect.width,
				destRect.height);
		}
		if (image != null) {
			sourceRect.x= (destRect.x * 100) / zoomFactor;
			sourceRect.y= (destRect.y * 100) / zoomFactor;
			sourceRect.width= (destRect.width * 100) / zoomFactor;
			sourceRect.height= (destRect.height * 100) / zoomFactor;
			g2.drawImage(
				image,
				destRect.x,
				destRect.y,
				destRect.x + destRect.width,
				destRect.y + destRect.height,
				sourceRect.x,
				sourceRect.y,
				sourceRect.x + sourceRect.width,
				sourceRect.y + sourceRect.height,
				null);
		}
		else
			super.paintComponent(g);
	}

	// method of Scrollable interface
	public Dimension getPreferredScrollableViewportSize() {
		if (image != null) {
			int h= image.getHeight(null);
			int w= image.getWidth(null);
			//if the image is not 0 by 0...
			if (h > 0 && w > 0) {
				Dimension screen= Toolkit.getDefaultToolkit().getScreenSize();
				//...return image size...
				if (h < screen.height && w < screen.width)
					return new Dimension(w, h);
				else
					//...otherwise two thirds of the screen area
					return new Dimension(
						screen.width * 2 / 3,
						screen.height * 2 / 3);
			}
		}
		return prefScrollableDims;
	}

	// method of Scrollable interface
	public int getScrollableUnitIncrement(
		Rectangle visibleRect,
		int orientation,
		int direction) {
		if (orientation == SwingConstants.HORIZONTAL)
			return visibleRect.width / 4;
		else
			return visibleRect.height / 4;
	}

	// method of Scrollable interface
	public int getScrollableBlockIncrement(
		Rectangle visibleRect,
		int orientation,
		int direction) {
		if (orientation == SwingConstants.HORIZONTAL)
			return visibleRect.width;
		else
			return visibleRect.height;
	}

	// method of Scrollable interface
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	// method of Scrollable interface
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	/**
	 * @return
	 */
	boolean isShowTransparencyPattern() {
		return showTransparencyPattern;
	}

	/**
	 * @param b
	 */
	void setShowTransparencyPattern(boolean b) {
		showTransparencyPattern= b;
	}

}
