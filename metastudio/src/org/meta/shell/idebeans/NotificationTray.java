/*
 * NotificationTray.java
 *
 * Created on April 12, 2004, 6:48 AM
 */

package org.meta.shell.idebeans;

import java.awt.*;
import java.util.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.*;
import org.meta.common.resource.ImageResource;
import org.meta.common.resource.MiscResource;

/**
 * A notification tray to be used with the status bar.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class NotificationTray extends JPanel {
    
    private JButton notificationButton;
    
    private NotificationWindow notificationWindow, notificationQueueWindow;
    private JOptionPane notification;
    
    private Component owner;
    
    private Hashtable<String, NotificationType> notificationQueue;
    private JTable notifyList;
    private JButton clearAll, close;
    
    /** Creates a new instance of NotificationTray */
    public NotificationTray(Component owner) {
       this.owner = owner;
       
       ImageIcon image = ImageResource.getInstance().getAlert();
       notificationButton = new JButton(image);
       notificationButton.setPreferredSize(
           new Dimension(image.getIconHeight(), image.getIconWidth()));
       notificationButton.setBorder(BorderFactory.createEmptyBorder());
       notificationButton.setBorderPainted(false);
       notificationButton.setRolloverEnabled(true);
       notificationButton.setToolTipText("Notification Tray");
       notificationButton.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent ae) {               
               if (notificationQueueWindow == null) {
                   // make the UI for displaying all the notifiations
                   // and optionally clearing them
                   notificationQueueWindow = new NotificationWindow(
                                                "Notification Message History");
                   
                   JComponent contentPane =
                                     notificationQueueWindow.getContentPane();
                   contentPane.setLayout(new BorderLayout());
                   notifyList = new JTable(new DefaultTableModel(
                       new Object [][] { },
                       new String [] {
                           "@", "Notifications"
                       }
                   ) {
                       Class[] types = new Class [] {
                           ImageIcon.class, String.class
                       };
                       boolean[] canEdit = new boolean [] {
                           false, false
                       };
                       
                       @Override
                       public Class getColumnClass(int columnIndex) {
                           return types[columnIndex];
                       }

                       @Override
                       public boolean isCellEditable(int rowIndex, 
                                                     int columnIndex) {
                           return canEdit[columnIndex];
                       }
                   }); // notifyList
                   notifyList.getColumn("@").setMaxWidth(20);
                   notifyList.sizeColumnsToFit(0);                     
                   notifyList.setShowGrid(false);  // do not show the grid                                
                   // mouse listener
                   notifyList.addMouseListener(new MouseAdapter() {
                       @Override
                       public void mouseClicked(MouseEvent me) {
                           if ((notifyList.getSelectedRow() >= 0)
                               && (me.getClickCount() == 2)) {
                               DefaultTableModel dtm = (DefaultTableModel)
                                                          notifyList.getModel(); 
                               JOptionPane.showMessageDialog(notifyList, 
                                dtm.getValueAt(notifyList.getSelectedRow(), 1),
                                "Notification", JOptionPane.DEFAULT_OPTION,
                                (ImageIcon) dtm.getValueAt(
                                               notifyList.getSelectedRow(), 0));
                           } // end if
                       }
                   });
                   contentPane.add(new JScrollPane(notifyList),
                                       BorderLayout.CENTER);
                   
                   JPanel buttonPanel = new JPanel(
                                            new FlowLayout(FlowLayout.CENTER));
                   clearAll   = new JButton("Clear All");
                   clearAll.setMnemonic('l');
                   clearAll.setToolTipText("Clear all notifications");
                   clearAll.addActionListener(new ActionListener() {
                       @Override
                       public void actionPerformed(ActionEvent ae) {
                           notificationQueue.clear();
                           DefaultTableModel dtm = (DefaultTableModel)
                                               notifyList.getModel();                           
                           dtm.setDataVector(new Object [][] { },
                                             new String [] {
                                               "@", "Notifications"
                                             });
                           notifyList.getColumn("@").setMaxWidth(20);
                           notifyList.sizeColumnsToFit(0);  
                       }
                   });
                   buttonPanel.add(clearAll);
                   
                   close = new JButton("Close");
                   close.setMnemonic('C');
                   close.setToolTipText("Close window");
                   close.addActionListener(new ActionListener() {
                       @Override
                       public void actionPerformed(ActionEvent ae) {
                           notificationQueueWindow.hideIt();
                       }
                   });
                   buttonPanel.add(close);
                   
                   contentPane.add(buttonPanel, BorderLayout.SOUTH);
               } // end if  
               
               // update the stuff              
               Object [][] messages = new Object[notificationQueue.size()][2];
                 
               Object [] values = notificationQueue.values().toArray();
               Object [] keys   = notificationQueue.keySet().toArray();
               
               for(int i=0; i<values.length; i++) {
                   messages[i][0] = getIconFor((NotificationType) values[i]);
                   messages[i][1] = keys[i];
               } // end for
               
               DefaultTableModel dtm = (DefaultTableModel)
                                               notifyList.getModel();                
               dtm.setDataVector(messages,
                                 new String [] {
                                   "@", "Notifications"
                                 });
               notifyList.getColumn("@").setMaxWidth(20);
               notifyList.sizeColumnsToFit(0);  
                           
               // show the message history
               notificationQueueWindow.setPreferredSize(
                         MiscResource.getInstance().getSplashScreenDimension());
               NotificationTray.this.notify(notificationQueueWindow);
           }
       });
       
       setLayout(new BorderLayout(0, 0));
       add(notificationButton, BorderLayout.CENTER);
       
       notificationQueue = new Hashtable<String, NotificationType>(10);
    }
        
    /**
     * show a auto closable notification window, with the specified
     * text and title.
     *
     * @param title - the title of the notification window
     * @param message - the message to be displayed in the notification window
     */
    public void notify(String title, String message) {        
        // show the stuff
        notify(title, message, false);
    }
    
    /**
     * show a auto closable notification window, with the specified
     * text and title.
     *
     * @param title - the title of the notification window
     * @param message - the message to be displayed in the notification window
     * @param type the NotificationType (info, warn, error...)
     */
    public void notify(String title, String message, NotificationType type) {
        // show the stuff
        notify(title, message, false, 1000, type);                  
    }
    
    /**
     * show a auto closable notification window, with the specified
     * text and title.
     *
     * @param title - the title of the notification window
     * @param message - the message to be displayed in the notification window
     * @param queueIt should we queue the message     
     */
    public void notify(String title, String message, boolean queueIt) {
        // show the stuff
        notify(title, message, queueIt, 1000);
    }
    
    /**
     * show a auto closable notification window, with the specified
     * text and title.
     *
     * @param title - the title of the notification window
     * @param message - the message to be displayed in the notification window
     * @param queueIt should we queue the message
     * @param autoCloseTime the autoclose time, if the window is auto-closable     
     */
    public void notify(String title, String message, boolean queueIt, 
                       int autoCloseTime) {
        // show the stuff
        notify(title, message, queueIt, autoCloseTime, NotificationType.INFO);                 
    }
    
    /**
     * show a auto closable notification window, with the specified
     * text and title.
     *
     * @param title - the title of the notification window
     * @param message - the message to be displayed in the notification window
     * @param queueIt should we queue the message     
     * @param type the NotificationType (info, warn, error...)
     */
    public void notify(String title, String message, boolean queueIt, 
                       NotificationType type) {
        // show the stuff
        notify(title, message, queueIt, 1000, type);
    }
    
    /**
     * show a auto closable notification window, with the specified
     * text and title.
     *
     * @param title - the title of the notification window
     * @param message - the message to be displayed in the notification window
     * @param queueIt should we queue the message
     * @param autoCloseTime the auto close time, if the window is auto-closable
     * @param type the NotificationType (info, warn, error...)
     */
    public void notify(String title, String message, boolean queueIt, 
                       int autoCloseTime, NotificationType type) {
        if (notificationWindow == null || notification == null) {
            // make the notificationWindow for the first time
            notificationWindow = new NotificationWindow(title);
            notificationWindow.setType(NotificationWindowType.AUTO_CLOSE);            
            
            JComponent contentPane = notificationWindow.getContentPane();
            contentPane.setLayout(new BorderLayout());
            notification = new JOptionPane(message);
            JButton okButton = new JButton("Ok");
            okButton.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent ae) {
                   notificationWindow.hideIt();
               }
            });
            notification.setOptions(new Object[] {okButton});
            contentPane.add(notification, BorderLayout.CENTER);            
        } // end if
        
        // set the title, auto-close time and message
        notificationWindow.setTitle(title);
        notificationWindow.setAutoCloseTime(autoCloseTime);
        notification.setMessage(message);
        notification.setIcon(getIconFor(type));
        
        if (queueIt) {
            notificationQueue.put((new Date()).toString() + " :: \n" + message,
                                  type);
        } // end if
        
        // show the stuff
        notify(notificationWindow);
    }
    
    /**
     * show the specified notification window at an appropriate place.
     *
     * @param nw - the notification window to be displayed
     */
    public void notify(NotificationWindow nw) {
        Point p = new Point(getX(), getY());
        
        SwingUtilities.convertPointToScreen(p, this);
        p.x -= ((p.x-nw.getPreferredSize().width) >= 0 
                 ? nw.getPreferredSize().width
                 : 0);
        p.y -= ((p.y-nw.getPreferredSize().height) >= 0 
                 ? nw.getPreferredSize().height
                 : 0);
        
        Point rp = new Point(owner.getX()+owner.getWidth(),
                             owner.getY()+owner.getHeight());
        SwingUtilities.convertPointToScreen(rp, owner);

        if (p.x+nw.getPreferredSize().width > rp.x)
            p.x = (rp.x - nw.getPreferredSize().width);
        if (p.y+nw.getPreferredSize().height > rp.y)
            p.y = (rp.y - nw.getPreferredSize().height);

        nw.showIt(owner, p.x, p.y);
    }
    
    /**
     * get icon for a particular NotificationType
     */
    public ImageIcon getIconFor(NotificationType type) {
        ImageResource images = ImageResource.getInstance();
        
        if (type.equals(NotificationType.INFO)) {
            return images.getInform();
        } else if (type.equals(NotificationType.WARN)) {
            return images.getWarn();
        } else if (type.equals(NotificationType.ERROR)) {
            return images.getError();
        } else {
            // default
            return images.getQuestion();
        } // end if
    }
    
    /**
     * Getter for property trayIcon.
     * @return Value of property trayIcon.
     */
    public ImageIcon getTrayIcon() {
        return (ImageIcon) notificationButton.getIcon();
    }
    
    /**
     * Setter for property trayIcon.
     * @param trayIcon New value of property trayIcon.
     */
    public void setTrayIcon(ImageIcon trayIcon) {
        notificationButton.setPreferredSize(
           new Dimension(trayIcon.getIconHeight(), trayIcon.getIconWidth()));
        notificationButton.setIcon(trayIcon);
    }
    
} // end of class NotificationTray
