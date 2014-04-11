/*
 * MemoryMonitor.java
 *
 * Created on June 22, 2003, 9:42 PM
 */

package org.meta.shell.idebeans;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

import org.meta.shell.ide.MeTALauncher;
import org.meta.common.resource.FontResource;
import org.meta.common.resource.StringResource;
import org.meta.shell.idebeans.eventhandlers.MainMenuEventHandlers;

/**
 * A small plugin to be put in the status bar for monitoring memory usage of 
 * JVM.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MemoryMonitor extends JPanel {
    
    public boolean stopUpdate;
    
    /** Holds value of property updateInterval. */
    private int updateInterval;
    
    private long totalMemory, freeMemory, usedMemory;
    
    /** Creates a new instance of MemoryMonitor */
    public MemoryMonitor() {
        stopUpdate     = false;
        updateInterval = 10000;
        
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the UI. 
     */
    private void initComponents() {
        setBackground(Color.black);
        setBorder(new LineBorder(Color.blue, 1, true));
        
        // on click .. request garbage collection, 
        // on right-click show memory configuration
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                if (SwingUtilities.isRightMouseButton(me)) {
                    final JFrame frame = MainMenuEventHandlers.getInstance(null)
                                                           .getIdeInstance();
                    try {
                       final MeTALauncher launcher 
                                             = MeTALauncher.getConfigInstance();
                       
                       JDialog dialog = new JDialog(frame);
                       
                       dialog.setTitle("Change memory settings...");
                       
                       // make the dialog
                       dialog.setLayout(new BorderLayout());                       
                       JLabel mainLanel = new JLabel("Changes made here will " +
                              "take effect only when you restart MeTA Studio.");
                       mainLanel.setFont(FontResource.getInstance()
                                                         .getDescriptionFont());
                       dialog.add(mainLanel, BorderLayout.NORTH);
                       
                       JPanel maxMemoryPanel = new JPanel(new FlowLayout());
                       maxMemoryPanel.add(new JLabel("Max memory available to" +
                               " MeTA Studio (in MB): ", JLabel.RIGHT));
                       JSpinner maxMemory = new JSpinner(
                          new SpinnerNumberModel(launcher.getMaxJVMMemory(), 
                                             launcher.getMinAllowedMemoryInMB(), 
                                             Long.MAX_VALUE, 1));
                       maxMemory.setToolTipText("Memory units in MB");
                       maxMemory.addChangeListener(new ChangeListener() {
                            @Override
                            public void stateChanged(ChangeEvent ce) {
                                long maxMemory = (long)
                                   ((Double) ((JSpinner) ce.getSource())
                                      .getValue()).doubleValue();
                                
                                try {
                                    launcher.setMaxJVMMemory(maxMemory);
                                    launcher.saveLauncherConfig();
                                } catch (Exception e) {
                                    JOptionPane.showMessageDialog(frame,
                                       "ERROR: Unable to save config! \n"
                                          + StringResource.getInstance()
                                                          .getStdErrorMessage(),
                                       "Error",
                                       JOptionPane.ERROR_MESSAGE);
                                } // end of try catch block
                            }
                       });                        
                       maxMemoryPanel.add(maxMemory);
                       
                       dialog.add(maxMemoryPanel, BorderLayout.CENTER);
                       
                       // show the dialog
                       dialog.setSize(new Dimension(550, 100));
                       dialog.setLocationRelativeTo(frame);                       
                       dialog.setModal(true);
                       dialog.setVisible(true);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(frame,
                            "ERROR: Unable to start config! \n"
                            + StringResource.getInstance().getStdErrorMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                        
                        System.out.println("Unable to start config!");
                        e.printStackTrace();
                    } // end of try .. catch block
                } else {
                    gc();
                } // end if
            }
        });
        
        startMemoryMonitorThread();
    }
    
    /**
     * This method updates the UI showing the memory usage.
     */
    private void startMemoryMonitorThread() {
        Thread memoryMonitorThread = new Thread() {
            @Override
            public void run() {                
                Runtime jvmRuntime = Runtime.getRuntime();
                
                while(!stopUpdate) {   // run it till needed
                    totalMemory = jvmRuntime.totalMemory() / 1024;
                    freeMemory  = jvmRuntime.freeMemory()  / 1024;
                    usedMemory  = totalMemory - freeMemory;
                    
                    // set some candy tool tip
                    setToolTipText("<html> <body><b><u>Memory (in KB):</u></b>" 
                       + "<table border=0> <tr>"
                       + "<td>Total:  </td><td>" + totalMemory + "</td> </tr>"
                       + "<tr><td> Available:  </td><td>" + freeMemory
                       + "</td> </tr>"
                       + "<tr><td> Used: </td><td>" + usedMemory
                       + "</td> </tr>"
                       + "</table>"
                       + "</body> </html>");
                    
                    // repaint the UI
                    repaint();
                    
                    try {
                        sleep(updateInterval);
                    } catch (InterruptedException ignored) {}
                } // end while                                                
            } // end of run method
        }; // end of thread definition memoryMonitorThread
        
        // start the thread with low priority
        memoryMonitorThread.setName("Memory Monitor Thread");
        memoryMonitorThread.setPriority(Thread.MIN_PRIORITY);
        memoryMonitorThread.start();
    }
    
    /**
     * try to run the garbage collector .. really paranoid way of doing things!
     */
    private static void gc() {
      try {
         System.gc();
         Thread.sleep(100);
         System.runFinalization();
         Thread.sleep(100);
         System.gc();
         Thread.sleep(100);
         System.runFinalization();
         Thread.sleep(100);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

    /**
     * overridden finalize
     */
    @Override
    protected void finalize() throws java.lang.Throwable {
        stopUpdate = true;
        
        super.finalize();                
    }
    
    /** Getter for property stopUpdate.
     * @return Value of property stopUpdate.
     */
    public boolean isStopUpdate() {
        return this.stopUpdate;
    }
    
    /** Setter for property stopUpdate.
     * @param stopUpdate New value of property stopUpdate.
     */
    public void setStopUpdate(boolean stopUpdate) {
        this.stopUpdate = stopUpdate;
    }
    
    /** Getter for property updateInterval.
     * @return Value of property updateInterval.
     */
    public int getUpdateInterval() {
        return this.updateInterval;
    }
    
    /** Setter for property updateInterval.
     * @param updateInterval New value of property updateInterval.
     */
    public void setUpdateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
    }
    
    /**
     * overridden paint method
     */
    @Override
    public void paint(Graphics graphics) {
        super.paint(graphics);
        
        int width  = getWidth();
        int height = getHeight();                         
        
        double graphHeightPercentage = (usedMemory * 100.0) / totalMemory;
        double graphHeight = (height * graphHeightPercentage) / 100.0;
                       
        ((Graphics2D) graphics).setPaint(new GradientPaint(
                                                    0, 0, Color.red,
                                                    0, height, Color.green));
        graphics.fillRect(0, (int) (height-graphHeight), width, height);
    }
    
} // end of class MemoryMonitor
