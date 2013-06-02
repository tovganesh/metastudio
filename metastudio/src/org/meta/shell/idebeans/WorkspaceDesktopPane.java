/*
 * WorkspaceDesktopPane.java
 *
 * Created on September 29, 2003, 6:31 AM
 */

package org.meta.shell.idebeans;

import java.awt.*;
import java.util.*;
import java.beans.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

/**
 * A specialized desktop pane with a task bar and a simple task list manager.
 * The current task list manager can be invoked using <code> Crtl + ` </code>
 * key combination.
 * In short a very simple window manager. <br>
 *
 * Note: When adding customized event handlers for JInternalFrame,  the
 * event handlers should be added only after a call to addInternalFrame(),
 * for smooth functioning.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class WorkspaceDesktopPane extends JPanel 
                                  implements AWTEventListener {
    
    private JDesktopPane theDesktop;
    private JPanel theTaskBar;
    
    private Hashtable<JComponent, JComponent> taskBarMap;
    private Hashtable<JInternalFrame, Boolean> frameMaximizedMap;
    private Stack<JInternalFrame> workspaceFrameMap;
    private ButtonGroup taskBarGroup;    
    
    private JPopupMenu frameMenu;
    private JMenuItem minimize, restore, close, 
                      tileVertically, tileHorizontally, undoTile;
    
    private JToggleButton targetButton;
    private JToggleButton theInvisible;
    
    private int frameCount, maximizedFrameCount;
    
    private boolean taskListShown;
    
    /** Creates a new instance of WorkspaceDesktopPane */
    public WorkspaceDesktopPane() {
        initComponents();
        
        taskBarMap        = new Hashtable<JComponent, JComponent>(10);
        frameMaximizedMap = new Hashtable<JInternalFrame, Boolean>(10);
        workspaceFrameMap = new Stack<JInternalFrame>();
        taskBarGroup      = new ButtonGroup();
        taskBarGroup.add(theInvisible); // the invisible fellow
        
        frameCount = maximizedFrameCount = 0;
        
        taskListShown = false;
    }        
    
    /** This method is called from within the constructor to
     * initialize the UI. 
     */
    private void initComponents() {
        theDesktop = new JDesktopPane();
        theDesktop.setLayout(new BorderLayout());
        theDesktop.setDesktopManager(null);
        
        theTaskBar = new JPanel(new GridLayout());
        
        theTaskBar.setBorder(new LineBorder(Color.lightGray, 1, true));        
        theInvisible = new JToggleButton("");        
        theInvisible.setVisible(false);
        
        super.setLayout(new BorderLayout());
        
        super.add(theDesktop, BorderLayout.CENTER);
        super.add(theTaskBar, BorderLayout.SOUTH);                
        
        // make the frame menu
        frameMenu = new JPopupMenu();
        
        minimize = new JMenuItem("Minimize");
        minimize.setMnemonic('n');
        frameMenu.add(minimize);
        
        restore = new JMenuItem("Restore");
        restore.setMnemonic('R');
        frameMenu.add(restore);
        
        frameMenu.addSeparator();
        close = new JMenuItem("Close");
        close.setMnemonic('l');
        close.setAccelerator(KeyStroke.getKeyStroke(
                                          KeyEvent.VK_F4, KeyEvent.CTRL_MASK));
        frameMenu.add(close);
        
        frameMenu.addSeparator();
        tileVertically = new JMenuItem("Tile Windows Vertically");
        tileVertically.setMnemonic('V');
        frameMenu.add(tileVertically);
                
        tileHorizontally = new JMenuItem("Tile Windows Horizontally");
        tileHorizontally.setMnemonic('H');
        frameMenu.add(tileHorizontally);
        
        undoTile = new JMenuItem("Undo Tile");
        undoTile.setMnemonic('U');        
        
        // add listener for popup menu
        ActionListener frameMenuListemer = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                String command = ae.getActionCommand();
                JInternalFrame frame = (JInternalFrame) 
                                                 taskBarMap.get(targetButton);
                
                try {
                    if (command.equals("Minimize")) {
                        frame.setIcon(true);
                    } else if (command.equals("Restore")) {
                        // update counter and UI                
                        if (!((Boolean) frameMaximizedMap.get(frame))
                                                         .booleanValue()) 
                           maximizedFrameCount++;
                
                        // select the button
                        targetButton.setSelected(true);
                        
                        // restore the frame
                        frame.setVisible(true);
                        frame.toFront();
                        frame.setMaximum(true);
                        frame.grabFocus();
                        frameMaximizedMap.put(frame, new Boolean(true));
                    } else if (command.equals("Close")) {
                        frame.doDefaultCloseAction();
                    } else if (command.equals("Tile Windows Vertically")) {
                        theDesktop.setLayout(
                           new GridLayout(1, maximizedFrameCount));
                        theDesktop.updateUI();
                        frameMenu.remove(undoTile);
                        frameMenu.add(undoTile);
                    } else if (command.equals("Tile Windows Horizontally")) {
                        theDesktop.setLayout(
                           new GridLayout(maximizedFrameCount, 1));
                        theDesktop.updateUI();
                        frameMenu.remove(undoTile);
                        frameMenu.add(undoTile);
                    } else if (command.equals("Undo Tile")) {
                        theDesktop.setLayout(new BorderLayout());
                        theDesktop.updateUI();
                        frameMenu.remove(undoTile);
                    } // end if
                } catch (PropertyVetoException ignored) {}
            }
        };
        
        minimize.addActionListener(frameMenuListemer);
        restore.addActionListener(frameMenuListemer);
        close.addActionListener(frameMenuListemer);
        tileVertically.addActionListener(frameMenuListemer);
        tileHorizontally.addActionListener(frameMenuListemer);
        undoTile.addActionListener(frameMenuListemer);
    }
    
    /**
     * Adds a new JInternalFrame to the desktop and maps it to a 
     * taskbar button.
     *
     * @param frame - the frame which is to be part of this desktop.
     */
    public void addInternalFrame(JInternalFrame frame) {
        addInternalFrame(frame, false);
    }
    
    /**
     * Adds a new JInternalFrame to the desktop and maps it to a 
     * taskbar button. <br> 
     * isWorkspaceFrm flag specifies whether this window is to be closed
     * when the workspace is closed.
     *
     * @param frame - the frame which is to be part of this 
     *        desktop.
     * @param isWorkspaceFrm - is it a workspace frame?
     */
    public void addInternalFrame(JInternalFrame frame, boolean isWorkspaceFrm) {
        addInternalFrame(frame, isWorkspaceFrm, true);
    }
    
    /**
     * Adds a new JInternalFrame to the desktop and maps it to a 
     * taskbar button. <br> 
     * isWorkspaceFrm flag specifies whether this window is to be closed
     * when the workspace is closed.
     *
     * @param frame - the frame which is to be part of this 
     *        desktop.
     * @param isWorkspaceFrm - is it a workspace frame?
     * @param handleClosingOperation - do we need to internally handle closing
     *                                 operation?
     */
    public void addInternalFrame(JInternalFrame frame, boolean isWorkspaceFrm, 
                                 boolean handleClosingOperation) {
        // make the taskbar button first
        JToggleButton taskButton = new JToggleButton(frame.getTitle(),
                                              frame.getFrameIcon(), false);
        
        taskButton.setHorizontalAlignment(SwingConstants.LEFT);
        taskButton.setToolTipText(frame.getTitle());
        taskButton.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));
        
        // force default close operation to be null
        frame.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
        
        // add event handlers
        // action listener for the taskButton handles showing and highlighting
        // the frames accordingly
        taskButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {                          
                JInternalFrame theFrame = (JInternalFrame) 
                                            taskBarMap.get(ae.getSource());
                theFrame.setVisible(true);                
                
                // update counter and UI                 
                if (!((Boolean) frameMaximizedMap.get(theFrame)).booleanValue()) 
                    maximizedFrameCount++;                
                
                try {
                    theFrame.toFront();
                    theFrame.setMaximum(true);
                    frameMaximizedMap.put(theFrame, new Boolean(true));
                    theFrame.setSelected(true);
                } catch (PropertyVetoException ignored) {}
            }
        }); // addActionListener
        
        // mouse listener fot taskButton for showing appropriate 
        // frame menu
        taskButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                if (SwingUtilities.isRightMouseButton(me)) {
                    JToggleButton taskButton = (JToggleButton) me.getSource();
                    JInternalFrame frame = (JInternalFrame) 
                                                taskBarMap.get(me.getSource());
                    
                    if (taskButton.isSelected()) {
                        restore.setEnabled(false);
                        minimize.setEnabled(true);
                    } else if (frame.isIcon()) {
                        restore.setEnabled(true);
                        minimize.setEnabled(false);                        
                    } else { 
                        restore.setEnabled(true);
                        minimize.setEnabled(true);
                    } // end if 
                    
                    // set the target button
                    targetButton = taskButton;
                    
                    // and show the properly constructed frame menu                    
                    frameMenu.show(taskButton, me.getX(), me.getY());
                } // end if
            }
        }); // addMouseListener
        
        // and the generic internal frame listener
        if (handleClosingOperation) {
          frame.addInternalFrameListener(new InternalFrameAdapter() {
              
            @Override
            public void internalFrameIconified(InternalFrameEvent e) {
                JInternalFrame theFrame = e.getInternalFrame();                           
                theFrame.setVisible(false);
                
                // update counter
                maximizedFrameCount--;                
                frameMaximizedMap.put(theFrame, new Boolean(false));
                
                if (maximizedFrameCount == 0) {
                    // do the invisible trick
                    theInvisible.setSelected(true);
                } // end if                                
            }
            
            @Override
            public void internalFrameActivated(InternalFrameEvent e) {
                JToggleButton theTaskButton = (JToggleButton)
                                                taskBarMap.get(e.getSource());
                theTaskButton.setSelected(true);
            }
            
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {                
                e.getInternalFrame().dispose();                
            }
            
            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                JInternalFrame theFrame = (JInternalFrame) e.getSource();                
                JToggleButton theTaskButton = (JToggleButton)
                                                taskBarMap.get(e.getSource());
                
                if ((theTaskButton == null) || (theFrame == null)) return;
                
                // adjust the counters and clean up                
                frameCount--;
                if (frameMaximizedMap.get(theFrame) != null) {
                 if (((Boolean) frameMaximizedMap.get(theFrame)).booleanValue()) 
                    maximizedFrameCount--;
                } // end if
                
                taskBarMap.remove(taskBarMap.get(theTaskButton)); // frame map
                taskBarMap.remove(theTaskButton); // button map
                taskBarGroup.remove(theTaskButton);
                theTaskBar.remove(theTaskButton);
                frameMaximizedMap.remove(theFrame);
                workspaceFrameMap.remove(theFrame);
                                
                // and update the UI
                theTaskBar.updateUI(); 
                
                // and remove from the desktop
                theDesktop.remove(theFrame);
                theFrame = null;
            }
          }); // addInternalFrameListener
        } else {
          frame.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameIconified(InternalFrameEvent e) {
                JInternalFrame theFrame = e.getInternalFrame();                
                theFrame.setVisible(false);
                
                // update counter
                maximizedFrameCount--;                
                frameMaximizedMap.put(theFrame, new Boolean(false));
                
                if (maximizedFrameCount == 0) {
                    // do the invisible trick
                    theInvisible.setSelected(true);
                } // end if                                
            }
            
            @Override
            public void internalFrameActivated(InternalFrameEvent e) {
                JToggleButton theTaskButton = (JToggleButton)
                                                taskBarMap.get(e.getSource());
                theTaskButton.setSelected(true);
            }                        
            
            private boolean secondCall = false;
            
            @Override
            public void internalFrameClosed(InternalFrameEvent e) {                
                if (secondCall) return;
                
                JInternalFrame theFrame = (JInternalFrame) e.getSource();
                
                JToggleButton theTaskButton = (JToggleButton)
                                                taskBarMap.get(e.getSource());
                
                // adjust the counters and clean up                
                frameCount--;
                if (frameMaximizedMap.get(theFrame) != null) {
                 if (((Boolean) frameMaximizedMap.get(theFrame)).booleanValue()) 
                    maximizedFrameCount--;
                } // end if
                
                // frame map                
                taskBarMap.remove(taskBarMap.get(theTaskButton)); 
                taskBarMap.remove(theTaskButton); // button map
                taskBarGroup.remove(theTaskButton);
                theTaskBar.remove(theTaskButton);
                frameMaximizedMap.remove(theFrame);
                workspaceFrameMap.remove(theFrame);
                                
                // and update the UI
                theTaskBar.updateUI(); 
                
                // and remove from the desktop
                theDesktop.remove(theFrame);
                theFrame = null;
                
                // make this flag true, so that we are never called again!
                secondCall = true;
            }
          }); // addInternalFrameListener
        } // end if
        
        // title change listener, to update the task button text
        frame.addPropertyChangeListener("title", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent pce) {
                JToggleButton theTaskButton = (JToggleButton)
                                                taskBarMap.get(pce.getSource());

                String title = ((JInternalFrame) pce.getSource()).getTitle();
                theTaskButton.setText(title);
                theTaskButton.setToolTipText(title);
            }
        });
        
        // add the mapping and the group
        taskBarGroup.add(taskButton);
        taskButton.setSelected(true);
        taskBarMap.put(taskButton, frame); // two way mapping ...
        taskBarMap.put(frame, taskButton);
        frameMaximizedMap.put(frame, new Boolean(true));
        if (isWorkspaceFrm) {
            workspaceFrameMap.push(frame);
        } // end if
        
        // reset UI .. layout
        theDesktop.setLayout(new BorderLayout());
        
        // add them to UI
        theTaskBar.add(taskButton);
        theDesktop.add(frame);
        try {
            frame.toFront();
            frame.setMaximum(true);            
            frame.setSelected(true);
        } catch (PropertyVetoException ignored) {}
        
        // adjust the counters
        frameCount++;
        maximizedFrameCount++;        
    }
   
    /**
     * overriden method to trap Ctrl + ` and related key events to handle the
     * task list interface.
     */
    @Override
    public void eventDispatched(AWTEvent event) {
        KeyEvent ke;
        
        switch(event.getID()) {
            case KeyEvent.KEY_PRESSED:
                ke = (KeyEvent) event;                              
                
                if (((ke.getKeyCode() == KeyEvent.VK_BACK_QUOTE)
                     || (ke.getKeyCode() == KeyEvent.VK_TAB))
                       && ke.isControlDown() && (!taskListShown)
                       && (taskBarMap.size() > 2)) {
                    if (!IDETaskListDialog.isItDisplayed()) {
                       IDETaskListDialog.invokeIt(this, taskBarMap.keys());
                       taskListShown = true;
                    } // end if
                } // end if
                
                break;
            case KeyEvent.KEY_RELEASED:
                ke = (KeyEvent) event;
                
                if ((!ke.isControlDown()) 
                    && (ke.getKeyCode() == KeyEvent.VK_CONTROL)
                    && taskListShown) {
                    // first get the selected frame
                    JInternalFrame frame = IDETaskListDialog.getSelectedFrame();
                    // update counter and UI                
                    if (!((Boolean) frameMaximizedMap.get(frame))
                                                         .booleanValue()) 
                        maximizedFrameCount++;                                                
                
                    // select the button
                    ((JToggleButton) taskBarMap.get(frame)).setSelected(true);
                    
                    // then dispose the popup
                    IDETaskListDialog.hideIt();
                    taskListShown = false;
                    
                    // restore the frame                    
                    try {
                        frame.setVisible(true);
                        frame.toFront();
                        frame.grabFocus();
                        frame.setMaximum(true);
                        frame.setSelected(true);
                    } catch (PropertyVetoException ignored) {}
                    
                    frameMaximizedMap.put(frame, new Boolean(true));
                } // end if
                break;
        } // end switch .. case block
    }
    
    /**
     * return a frame list of this desktop
     *
     * @return Enumeration of the frame list
     */
    public Enumeration<JComponent> getFrameList() {
        return taskBarMap.keys();
    }
    
    /**
     * return a frame list of this desktop
     *
     * @return Enumeration of the frame list
     */
    public Enumeration<JComponent> getDockableFrameList() {
        Vector<JComponent> dockableFrames = new Vector<JComponent>();
        
        for(JComponent comp : taskBarMap.keySet()) {
            if (comp instanceof DockableFrame) {
                dockableFrames.add(comp);
            } // end if
        } // end for
        
        return dockableFrames.elements();
    }
    
    /**
     * Method to get a reference to currently active frame. <br>
     *
     * @return JInternalFrame the currently active frame of 
     * <code>null</code> is none.
     */
    public JInternalFrame getActiveFrame() {
        Enumeration<JComponent> frames = getFrameList();
        JComponent frame;
        
        while(frames.hasMoreElements()) {
            frame = frames.nextElement();
            
            if (!(frame instanceof JInternalFrame)) continue;
                        
            // close the currently selected frame
            if (((JInternalFrame) frame).isSelected()) {
                return ((JInternalFrame) frame);
            } // end if
            
        } // end while
        
        return null;
    }
    
    /**
     * Method to initiate closing of all workspace frames
     */
    public void closeAllWorkspaceFrames() {
        while (!workspaceFrameMap.empty()) {
            try {                
                ((JInternalFrame) workspaceFrameMap.pop()).setClosed(true);                
            } catch (Exception e) {
                System.err.println("Warning: Unable to close internal frame.");
                e.printStackTrace();
            } // end try .. catch block
        } // end for
        
        workspaceFrameMap.removeAllElements();
    }
    
    /**
     * Method to check whether the given frame instance is a part of workspace
     * frame ?
     *
     * @param frame the instance of JInternalFrame to be tested
     * @return true if it is the part of a workspace false otherwise
     */
    public boolean isWorkspaceFrame(JInternalFrame frame) {
        return workspaceFrameMap.contains(frame);
    }
    
    /**
     * Method to initiate closing of currently active frame
     */
    public void closeActiveFrame() {
        JInternalFrame frame = getActiveFrame();
        
        if (frame != null) {
            try {
                frame.setClosed(true);
            } catch (Exception e) {                
                System.err.println("Warning: Unable to get currently "
                                   + "active internal frame.");
                e.printStackTrace();           
            } // end try .. catch block
        } // end if
    }
    
    /**
     * overridden methods for the notification process to be enabled.
     */
    @Override
    public void addNotify() {
        super.addNotify();
        
        Toolkit.getDefaultToolkit().addAWTEventListener(this, 
                AWTEvent.FOCUS_EVENT_MASK | AWTEvent.KEY_EVENT_MASK);
    }
    
    @Override
    public void removeNotify() {
        super.removeNotify();
        
        Toolkit.getDefaultToolkit().removeAWTEventListener(this);                
    }
    
} // end of class WorkspaceDesktopPane
