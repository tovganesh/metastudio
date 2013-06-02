/*
 * NotificationWindow.java
 *
 * Created on April 11, 2004, 12:28 PM
 */

package org.meta.shell.idebeans;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import org.meta.common.resource.ImageResource;

/**
 * Part of notification UI used in MeTA Studio v2.0 <br>
 * Displays a popup window displaying the notification and the probable
 * action to be taken, Usage: <br>
 * <code>
 * NotificationWindow nw = new NotificationWindow("Molecule loaded"); <br>
 * nw.setClickClosable([true | false]);  // default is true: click outside,
 *                                       // closes the window
 * nw.showIt(null, x, y); <br>
 * ... <br>
 * nw.hideIt();
 * </code>
 * <br>
 * If additional components are to be added use: <br>
 * <code>
 * getCotentPane().setLayout([LayoutManager]); <br>
 * getCotentPane().add(]JComponent]); 
 * </code>
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class NotificationWindow extends JComponent implements AWTEventListener {
    
    private JPanel notificationPanel, titleBar, mainPanel;
    
    private JLabel titleLabel;
    private JButton closeButton;
    
    /** Holds value of property orientation. */
    private int orientation;
    
    /** Holds value of property title. */
    private String title;
    
    private PopupFactory factory;
    private Popup popup;
    
    /** used internally to enable/ disable call to addImpl() */
    private boolean checkAddImpl;
    
    /** Holds value of property type. */
    private NotificationWindowType type;
    
    /** Holds value of property autoCloseTime. */
    private int autoCloseTime;
    
    /** the auto closer thread */
    private Thread closerThread;
    
    /** Creates a new instance of NotificationWindow 
     * @param title - the title of this window
     */
    public NotificationWindow(String title) {
        this(title, SwingConstants.NORTH);
    }
    
    /** Creates a new instance of NotificationWindow 
     * @param title - the title of this window
     * @param orientation - the orientation of the title bar <br>
     * Valid values for this parameter are: <br>
     * <code>
     * SwingConstants.NORTH   (default) <br>
     * SwingConstants.SOUTH <br>
     * SwingConstants.EAST <br>
     * SwingConstants.WEST <br>
     * </code>
     * If any other value is provided, it is defaulted to SwingConstants.NORTH
     */
    public NotificationWindow(String title, int orientation) {        
        this(title, orientation, NotificationWindowType.NORMAL);
    }
    
    /** Creates a new instance of NotificationWindow 
     * @param title - the title of this window
     * @param orientation - the orientation of the title bar <br>
     * Valid values for this parameter are: <br>
     * <code>
     * SwingConstants.NORTH   (default) <br>
     * SwingConstants.SOUTH <br>
     * SwingConstants.EAST <br>
     * SwingConstants.WEST <br>
     * </code>
     * If any other value is provided, it is defaulted to SwingConstants.NORTH
     * @param type - the type of notification window
     */
    public NotificationWindow(String title, int orientation, 
                              NotificationWindowType type) {
        this.type          = type;
        this.title         = title;
        this.orientation   = orientation;      
        this.autoCloseTime = 1000; // milliseconds
        this.factory       = PopupFactory.getSharedInstance();
        
        this.checkAddImpl = false; // do not perform add() check during init
        initComponents();
        this.checkAddImpl = true; // and then enable the check
    }
    
    /** This method is called from within the constructor to
     * initialize the UI. 
     */
    private void initComponents() {
        mainPanel = new JPanel(new BorderLayout());
        
        // make the title 
        titleBar = new JPanel(new BorderLayout());
        // the title
        titleLabel = new JLabel(title);        
        titleBar.add(titleLabel, BorderLayout.CENTER);
        // and the close button
        ImageIcon cbImage = ImageResource.getInstance().getCloseWindow();
        closeButton = new JButton(cbImage);
        closeButton.setPreferredSize(
             new Dimension(cbImage.getIconWidth(), cbImage.getIconHeight()));
        closeButton.setBorder(BorderFactory.createEmptyBorder());
        closeButton.setToolTipText("Close");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                hideIt();
            }
        });
        titleBar.add(closeButton, BorderLayout.EAST);
        titleBar.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));        
                
        addTitleBar();
        
        // add the notification panel
        notificationPanel = new JPanel();
        notificationPanel.setBorder(BorderFactory.createLineBorder(Color.gray));
        mainPanel.add(notificationPanel, BorderLayout.CENTER);
        
        // add the main panel to this window
        super.setLayout(new BorderLayout());
        super.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        super.add(mainPanel, BorderLayout.CENTER);
    }
    
    /**
     * addTitleBar() - private method to appropriately add a title bar
     */
    private void addTitleBar() {
        switch(orientation) {
            case SwingConstants.NORTH:                
                mainPanel.add(titleBar, BorderLayout.NORTH);
                break;
            case SwingConstants.EAST:                
                mainPanel.add(titleBar, BorderLayout.EAST);
                break;
            case SwingConstants.WEST:
                mainPanel.add(titleBar, BorderLayout.WEST);
                break;
            case SwingConstants.SOUTH:
                mainPanel.add(titleBar, BorderLayout.SOUTH);
                break;
            default:
                orientation = SwingConstants.NORTH;
                mainPanel.add(titleBar, BorderLayout.NORTH);
                break;
        } // end switch case
    }        
    
    /**
     * show the notification window
     *
     * @param owner - Component mouse coordinates are relative to, may be null
     * @param x - Initial x screen coordinate
     * @param y - Initial y screen coordinate 
     */
    public synchronized void showIt(Component owner, int x, int y) {
        // make sure the popup is not already active
        if (popup != null) { 
            hideIt();
        } // end if
        
        // try and see if x, y are out of range ?
        // a very simplified adjustment
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        
        // adjust 'x' if needed
        if (x + getPreferredSize().width > screen.width) {
            x -= getPreferredSize().width;
        } // end if
        
        // adjust 'y' if needed
        if (y + getPreferredSize().height > screen.height) {
            y -= getPreferredSize().height;
        } // end if
        
        // show the stuff
        popup = factory.getPopup(owner, this, x, y);
        popup.show();
        
        // add a closer thread if needed
        if (type.equals(NotificationWindowType.AUTO_CLOSE)) {
            closerThread = new Thread() {
                @Override
                public void run() {
                    try {
                        sleep(autoCloseTime);
                    } catch (InterruptedException ignored) { }
                    
                    hideIt(); // hide this window
                }
            }; // closerThread
            closerThread.setPriority(Thread.MIN_PRIORITY);
            closerThread.setName("Notification window closer thread");
            closerThread.start();
        } // end if
    }
    
    /**
     * hide the notification window
     */
    public synchronized void hideIt() { 
        if (popup != null) {
            popup.hide();
            popup = null;
            
            if (closerThread != null) {
                if (closerThread.isAlive()) {
                    // make sure to interrupt the closerThread
                    // and allow a grace full exit
                    closerThread.interrupt();
                } // end if
            } // end if
        } // end if
    }
    
    /** Getter for property orientation.
     * @return Value of property orientation.
     *
     */
    public int getOrientation() {
        return this.orientation;
    }
    
    /** Setter for property orientation. <br>
     * <strong> Warning : </strong> Not thread safe! 
     *
     * @param orientation New value of property orientation.
     *
     */
    public void setOrientation(int orientation) {
        this.orientation = orientation;
        
        // reorient
        this.checkAddImpl = false; // do not perform add() check during init                
        mainPanel.remove(titleBar);
        addTitleBar();
        this.checkAddImpl = true; // and then enable the check
    }
    
    /** Getter for property title.
     * @return Value of property title.
     *
     */
    public String getTitle() {
        return this.title;
    }
    
    /** Setter for property title.
     * @param title New value of property title.
     *
     */
    public void setTitle(String title) {
        this.title = title;
        titleLabel.setText(title);
    }
    
    /**
     * overriden method to trap ESC key and any mouse clicks outside the
     * notification window
     */
    @Override
    public void eventDispatched(AWTEvent event) {
        KeyEvent ke;
        MouseEvent me;
        
        switch(event.getID()) {
            case KeyEvent.KEY_PRESSED:
                ke = (KeyEvent) event;
                
                // close if ESC is pressed
                if (ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    hideIt();
                } // end if
                
                break;
            case MouseEvent.MOUSE_PRESSED:
            case MouseEvent.MOUSE_CLICKED:
            case MouseEvent.MOUSE_RELEASED:
                if (type.equals(NotificationWindowType.AUTO_CLOSE)) {
                    break;
                } // end if

                if (!clickClosable) break;
                
                me = (MouseEvent) event;
                
                if (!this.contains(me.getPoint())) { 
                    hideIt();
                } // end if
                
                break;
        } // end switch .. case
    }
    
    /**
     * Return the content pane...
     * The proper way of adding external componants is to use the ContentPane
     *
     * @return JComponent - the content pane to add additional componants
     */
    public JComponent getContentPane() {
        return notificationPanel;
    }
    
    /**
     * overridden methods for the notification process to be enabled.
     */
    @Override
    public void addNotify() {
        super.addNotify();
        
        Toolkit.getDefaultToolkit().addAWTEventListener(this, 
                AWTEvent.FOCUS_EVENT_MASK | AWTEvent.KEY_EVENT_MASK
                | AWTEvent.MOUSE_EVENT_MASK);
    }
    
    @Override
    public void removeNotify() {
        super.removeNotify();
        
        Toolkit.getDefaultToolkit().removeAWTEventListener(this);                
    }    
    
    /**
     * Throws a runtime exception .. specifying use of 
     * <code> NotificationWindow.getContentPane().setLayout() </code> instead.
     */
    @Override
    public void setLayout(LayoutManager mgr) {
        String typ = getClass().getName();

        getContentPane().setLayout(mgr);
    }
    
    /**
     * Throws a runtime exception .. specifying use of 
     * <code> NotificationWindow.getContentPane().getLayout() </code> instead.
     */
    @Override
    public LayoutManager getLayout() {
        String typ = getClass().getName();

        return getContentPane().getLayout();
    }
    
    /**
     * Throws a runtime exception .. specifying use of 
     * <code> NotificationWindow.getContentPane().add() </code> instead.
     */
    @Override
    protected void addImpl(Component comp, Object constraints, int index) {
        if (!checkAddImpl) { 
            super.addImpl(comp, constraints, index);
            return;
        } // end if
        
        getContentPane().add(comp, constraints, index);
    }
    
    /** Getter for property type.
     * @return Value of property type.
     *
     */
    public NotificationWindowType getType() {
        return this.type;
    }
    
    /** Setter for property type.
     * @param type New value of property type.
     *
     */
    public void setType(NotificationWindowType type) {
        this.type = type;
    }
    
    /** Getter for property autoCloseTime.
     * @return Value of property autoCloseTime (milliseconds).
     *
     */
    public int getAutoCloseTime() {
        return this.autoCloseTime;
    }
    
    /** Setter for property autoCloseTime.
     * @param autoCloseTime New value of property autoCloseTime (milliseconds).
     *
     */
    public void setAutoCloseTime(int autoCloseTime) {
        this.autoCloseTime = autoCloseTime;
    }
    
    /** 
     * Does clicking any where other than this panel window close this 
     * window? Default operation is to close it.
     */
    private boolean clickClosable = true;

    /**
     * Get the value of clickClosable
     *
     * @return the value of clickClosable
     */
    public boolean isClickClosable() {
        return clickClosable;
    }

    /**
     * Set the value of clickClosable
     *
     * @param clickClosable new value of clickClosable
     */
    public void setClickClosable(boolean clickClosable) {
        this.clickClosable = clickClosable;
    }

} // end of class NotificationWindow
