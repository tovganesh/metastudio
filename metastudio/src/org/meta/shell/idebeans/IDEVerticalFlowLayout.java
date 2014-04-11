/*
 * IDEVirticalFlowLayout.java
 *
 * Created on April 21, 2007, 8:22 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.meta.shell.idebeans;

import java.awt.*;

/**
 * Vertical flow layout source obtained from:
 * <a href="http://www.risner.org/java/VerticalFlowLayout.java_102.html">
 *   http://www.risner.org/java/VerticalFlowLayout.java_102.html </a>
 *
 * and is appropriately modified for use in MeTA Studio.
 * 
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class IDEVerticalFlowLayout implements LayoutManager {
    
  private int vgap = 0;

  /**
   * IDEVerticalFlowLayout constructor.
   */
  public IDEVerticalFlowLayout() {
    this(0);
  }

  /**
   * IDEVerticalFlowLayout constructor.
   * 
   * @param vgap specifies the gap (vertical) between two layed out components
   */
  public IDEVerticalFlowLayout(int vgap) {
    if (vgap < 0) {
      this.vgap = 0;
    } else {
      this.vgap = vgap;
    } 
  }

  /**
   * addLayoutComponent .. again this is not implimeted
   */
  public void addLayoutComponent(String name, Component comp) {
      // NOT IMPLIMENTED
  } 

  /**
   * Called by layout manager to actually lay out the components
   *
   * @param parent the parent for which the lay out is to performed
   */
  public void layoutContainer(Container parent) {
    Insets insets = parent.getInsets();
    int w = parent.getSize().width - insets.left - insets.right;    
    int numComponents = parent.getComponentCount();
    
    if (numComponents == 0) {
      return;
    } 
    
    int y = insets.top;
    int x = insets.left;
    for (int i = 0; i < numComponents; ++i) {
      Component c = parent.getComponent(i);
      if (c.isVisible()) {
        Dimension d = c.getPreferredSize();
        c.setBounds(x, y, w, d.height);
        y += d.height + vgap;
      } 
    } 
  } 

  /**
   * Specify the minimum size for this container.
   *
   * @param parent the parent container
   * @return Dimension object specifying the minimum size
   */
  public Dimension minimumLayoutSize(Container parent) {
    Insets insets = parent.getInsets();
    int maxWidth = 0;
    int totalHeight = 0;
    int numComponents = parent.getComponentCount();
    
    for (int i = 0; i < numComponents; ++i) {
      Component c = parent.getComponent(i);
      if (c.isVisible()) {
        Dimension cd = c.getMinimumSize();
        maxWidth = Math.max(maxWidth, cd.width);
        totalHeight += cd.height;
      } 
    }
    
    Dimension td = new Dimension(maxWidth + insets.left + insets.right, 
                                 totalHeight + insets.top + insets.bottom 
                                 + vgap * numComponents);
    return td;
  } 

  /**
   * Specify the preferred size for this container.
   *
   * @param parent the parent container
   * @return Dimension object specifying the preferred size
   */
  public Dimension preferredLayoutSize(Container parent) {
    Insets insets = parent.getInsets();
    int maxWidth = 0;
    int totalHeight = 0;
    int numComponents = parent.getComponentCount();
    
    for (int i = 0; i < numComponents; ++i) {
      Component c = parent.getComponent(i);
      if (c.isVisible()) {
        Dimension cd = c.getPreferredSize();
        maxWidth = Math.max(maxWidth, cd.width);
        totalHeight += cd.height;
      } 
    }
    
    Dimension td = new Dimension(maxWidth + insets.left + insets.right, 
                                 totalHeight + insets.top + insets.bottom 
                                 + vgap * numComponents);
    return td;
  } 

  /**
   * removeLayoutComponent method ... is not implemented
   */
  public void removeLayoutComponent(Component comp) {
      // NOT IMPLIMENTED
  } 

} // end of class IDEVirticalFlowLayout
