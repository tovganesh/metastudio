/*
 * IDETaskListDialog.java
 *
 * Created on December 19, 2003, 9:54 PM
 */

package org.meta.shell.idebeans;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.util.Vector;
import java.util.Enumeration;
import java.lang.ref.WeakReference;

/**
 * The task list manager for this IDE.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class IDETaskListDialog extends JComponent 
                               implements AWTEventListener, LayoutManager {
    
    /** gap between two icons displayed in the task list dialog */
    private static int GAP       = 5;
    /** wrap the show if there are more icons! */
    private static int WRAP_AT   = 12;
    /** the size of the icon */
    private static int ICON_SIZE = 24;
        
    private static WeakReference<IDETaskListDialog> _taskList = null;
    
    private static Popup popup;
        
    private JLabel taskLabel;
    
    private IconPanel iconPanel;        
    
    private Vector<String> iconLables;
    
    private Vector<JInternalFrame> frameList;
    
    private JInternalFrame currentSelectedFrame;
    
    /** Creates a new instance of IDETaskListDialog */
    private IDETaskListDialog() {
        setBorder(BorderFactory.createRaisedBevelBorder());
        
        taskLabel = new JLabel();
        taskLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        taskLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        iconPanel = new IconPanel();
        
        add(taskLabel);
        add(iconPanel);                
        
        setLayout(this);
    }
    
    /**
     * create instance of this dialog!
     */    
    private static IDETaskListDialog getInstance() {                
        if (_taskList == null) {
            _taskList = 
                 new WeakReference<IDETaskListDialog>(new IDETaskListDialog());
        } // end if   
        
        IDETaskListDialog taskList = _taskList.get();
        
        if (taskList == null) {
            taskList  = new IDETaskListDialog();
            _taskList = new WeakReference<IDETaskListDialog>(taskList);
        } // end if
        
        return taskList;        
    }
    
    /** 
     * method to invoke this task list dialog
     *
     * @param target - the target component on the top of which this popup will
     *        be diaplayed
     * @param frameList - an inumeration of instance of JInternalFrame. Any 
     *        member instances are ignored.
     */
    public static void invokeIt(Component target, 
                                Enumeration<JComponent> frameList) {
        IDETaskListDialog taskList = IDETaskListDialog.getInstance();
        
        taskList.setIconsAndLables(frameList);
        
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension taskD = taskList.getPreferredSize();
        
        int x = (toolkit.getScreenSize().width  / 2) - (taskD.width  / 2);
        int y = (toolkit.getScreenSize().height / 2) - (taskD.height / 2);
        
        popup = PopupFactory.getSharedInstance().getPopup(target, 
                                       taskList, x, y);
        
        popup.show();                
    }
    
    /**
     * method to dispose off the popup
     */
    public static void hideIt() { 
        if (popup != null) {
            popup.hide();
            popup = null;
        } // end if
    }
    
    /**
     * method to check whether the popup is being displayed
     */
    public static boolean isItDisplayed() {
        return (popup != null);
    }
    
    /**
     * overriden method to trap Ctrl + ` and related key events to handle the
     * task list interface.
     */
    public void eventDispatched(AWTEvent event) {
        KeyEvent ke;
        
        switch(event.getID()) {
            case KeyEvent.KEY_PRESSED:
                ke = (KeyEvent) event;
                
                if (isItDisplayed() 
                    && ((ke.getKeyCode() == KeyEvent.VK_BACK_QUOTE)
                         || (ke.getKeyCode() == KeyEvent.VK_TAB))
                    && (ke.isControlDown()) && (!ke.isShiftDown())) {
                    iconPanel.incrementSelection();
                    taskLabel.setText(
                       (String) iconLables.get(iconPanel.getSelectionIndex()));
                } else if (isItDisplayed() 
                    && ((ke.getKeyCode() == KeyEvent.VK_BACK_QUOTE)
                         || (ke.getKeyCode() == KeyEvent.VK_TAB))
                    && (ke.isControlDown()) && (ke.isShiftDown())) {
                    iconPanel.decrementSelection();
                    taskLabel.setText(
                       (String) iconLables.get(iconPanel.getSelectionIndex()));
                } // end if
                
                break;                
        } // end switch .. case block
    }
    
    /**
     * return the currently selected frame
     *
     * @return JInternalFrame - instance of the currectly selected frame
     *          If none is selected or is popup is not showing, then null is
     *          returned
     */
    public static JInternalFrame getSelectedFrame() {
        if (isItDisplayed()) {
            IDETaskListDialog taskList = IDETaskListDialog.getInstance();
            
            return ((JInternalFrame) taskList.getFrameList().get(
                               taskList.getIconPanel().getSelectionIndex()));
        } else {
            return null;
        } // end if
    }
    
    /**
     * return the frame list associated with this popup
     */
    public Vector getFrameList() {
        return frameList;
    }
    
    /**
     * return the iconpanel associated with this popup
     */
    public IconPanel getIconPanel() {
        return iconPanel;
    }
    
    /**
     * overridden methods for the notification process to be enabled.
     */
    public void addNotify() {
        super.addNotify();
        
        Toolkit.getDefaultToolkit().addAWTEventListener(this, 
                AWTEvent.FOCUS_EVENT_MASK | AWTEvent.KEY_EVENT_MASK);
    }
    
    public void removeNotify() {
        super.removeNotify();
        
        Toolkit.getDefaultToolkit().removeAWTEventListener(this);                
    }    
    
    /**
     * method to setup the icons and lables 
     */
    private void setIconsAndLables(Enumeration<JComponent> frameList) {
        Vector<Icon> icons = new Vector<Icon>();
        iconLables     = new Vector<String>();
        this.frameList = new Vector<JInternalFrame>();
        
        JComponent theFrame;
        
        while(frameList.hasMoreElements()) {
            theFrame = frameList.nextElement();
             
            if (theFrame instanceof JInternalFrame) {
                icons.add(((JInternalFrame) theFrame).getFrameIcon());
                iconLables.add(((JInternalFrame) theFrame).getTitle());
                this.frameList.add((JInternalFrame) theFrame);            
            } // end if 
        } // end while
        
        // set up the icon panel
        Image [] theIcons = new Image[icons.size()];
        
        for(int i=0; i<icons.size(); i++) {
            if (icons.get(i) instanceof ImageIcon) {
                theIcons[i] = ((ImageIcon) icons.get(i)).getImage();
            } // end if
        } // end for
        
        iconPanel.setIcons(theIcons);
        
        // make the first selection
        taskLabel.setText((String) iconLables.get(
                                       iconPanel.getSelectionIndex()));
    }
    
    /**
     * layout manager methods...
     */
    
    public void addLayoutComponent(String name, Component comp) {
        // not needed now .. skip
    }
    
    public void layoutContainer(Container parent) {
        Dimension d = iconPanel.getPreferredSize();
        
        if (getGraphics() == null) return;
        
        int textHeight = getGraphics().getFontMetrics(
                                          taskLabel.getFont()).getHeight();
        
        iconPanel.setBounds(5, 5, d.width+1, d.height+1);
        taskLabel.setBounds(10, d.height+10, 
                            getWidth()-20, textHeight+4);
    }
    
    public Dimension minimumLayoutSize(Container parent) {
        return preferredLayoutSize(parent);
    }
    
    public Dimension preferredLayoutSize(Container parent) {
        Dimension reqD = iconPanel.getPreferredSize();
        Dimension labD = taskLabel.getPreferredSize();
        
        reqD.height += labD.height + 20;
        reqD.width  += 20;
        
        return reqD;
    }
    
    public void removeLayoutComponent(Component comp) {
        // not needed now .. skip
    }        
    
    /**
     * private inner class representing the list of icons.
     * This class is highly influenced by a similar class in the Netbeans IDE.
     */
    private class IconPanel extends JComponent {
        
        /** Holds value of property icons. */
        private Image[] icons;
        
        /** Holds value of property selectionIndex. */
        private int selectionIndex;
        
        private Icon unknownIcon = new Icon() {
            public int getIconHeight() {
                return ICON_SIZE;
            }
            
            public int getIconWidth() {
                return ICON_SIZE;
            }
            
            public void paintIcon(Component c, Graphics g, int x, int y) {
                g.setColor(Color.RED);
                FontMetrics fm = g.getFontMetrics();
                g.drawString("?", x+7,  y+4+fm.getAscent());
            }
        };
        
        /** Getter for property icons.
         * @return Value of property icons.
         *
         */
        public Image[] getIcons() {
            return this.icons;
        }
        
        /** Setter for property icons.
         * @param icons New value of property icons.
         *
         */
        public void setIcons(Image[] icons) {
            this.icons = icons;
        }
        
        /** Getter for property selectionIndex.
         * @return Value of property selectionIndex.
         *
         */
        public int getSelectionIndex() {
            return this.selectionIndex;
        }
        
        /** Setter for property selectionIndex.
         * @param selectionIndex New value of property selectionIndex.
         *
         */
        public void setSelectionIndex(int selectionIndex) {
            this.selectionIndex = selectionIndex;
            repaint();
        }
        
        /**
         * the overridden paint method
         */
        public void paint(Graphics g) {
            int x = GAP;
            int y = GAP;
            
            for(int i=0; i<icons.length; i++) {
                int w = ICON_SIZE;
                int h = ICON_SIZE;
                
                if (icons[i] != null) {
                    int xpos = x;
                    int ypos = y;
                    int iconWidth  = icons[i].getWidth(this);
                    int iconHeight = icons[i].getHeight(this);
                    
                    // adjust width and height
                    if (iconWidth < w) {
                        xpos += ((w - iconWidth) / 2) - (GAP / 2);
                    } // end if
                    
                    if (iconHeight < h) {
                        ypos += ((h - iconHeight) / 2) - (GAP / 2);
                    } // end if
                    
                    // paint the icon
                    new ImageIcon(icons[i]).paintIcon(this, g, xpos, ypos);
                } else {
                    unknownIcon.paintIcon(this, g, x, y);
                } // end if
                
                if (i == selectionIndex) {
                    g.setColor(UIManager.getColor("desktop"));
                    g.drawRect(x-GAP, y-GAP, w+GAP-1, h+GAP-1);
                } // end if
                
                x += w + GAP;
                if ((i+1) % WRAP_AT == 0) {
                    y += h + GAP;
                    x = GAP;
                } // end if
            } // end for
        }
        
        /**
         * increment / cycle the selection
         */
        public void incrementSelection() {
            if (selectionIndex == icons.length-1) {
                setSelectionIndex(0);
            } else {
                setSelectionIndex(selectionIndex + 1);
            } // end if
        }
        
        /**
         * decrement / cycle the selection
         */
        public void decrementSelection() {
            if (selectionIndex == 0) {
                setSelectionIndex(icons.length - 1);
            } else {
                setSelectionIndex(selectionIndex - 1);
            } // end if
        }
        
        /**
         * for layout stuff
         */
        public Dimension getPreferredSize() {
            int rowCont = ((icons.length / WRAP_AT) + 1);
            return new Dimension((24 * WRAP_AT) + (GAP * WRAP_AT),
                                 (rowCont * 24) + (GAP * rowCont));
        }
    }
} // end of class IDETaskListDialog
