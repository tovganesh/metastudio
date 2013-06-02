package com.incors.plaf.kunststoff;

/*
 * This code was developed by INCORS GmbH (www.incors.com)
 * based on a contribution by Timo Haberkern.
 * It is published under the terms of the Lesser GNU Public License.
 */

import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

public class KunststoffTreeUI extends javax.swing.plaf.metal.MetalTreeUI { 
  protected static ImageIcon m_iconExpanded;
  protected static ImageIcon m_iconCollapsed;


  public KunststoffTreeUI(JComponent c) {
    try {
      m_iconExpanded = new ImageIcon(getClass().getResource("icons/treeex.gif"));
      m_iconCollapsed = new ImageIcon(getClass().getResource("icons/treecol.gif"));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static ComponentUI createUI(JComponent tree) {
    return new KunststoffTreeUI(tree);
  }

  // This method replaces the metal expand-/collaps-icons with some nicer ones.
  protected void paintExpandControl(Graphics g, Rectangle clipBounds,
                                Insets insets, Rectangle bounds,
                                TreePath path, int row, boolean isExpanded,
                                boolean hasBeenExpanded, boolean isLeaf) {
    if (isExpanded == true) {
      if (null != m_iconExpanded) {
        g.drawImage(m_iconExpanded.getImage(), bounds.x-17, bounds.y+4, null);
      }
    } else {
      if (null != m_iconCollapsed) {
        g.drawImage(m_iconCollapsed.getImage(), bounds.x-17, bounds.y+4, null);
      }
    }
  }


}