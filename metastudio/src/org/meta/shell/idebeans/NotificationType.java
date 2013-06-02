/*
 * NotificationType.java
 *
 * Created on September 1, 2004, 8:21 PM
 */

package org.meta.shell.idebeans;

/**
 * The notification type: Info, warning or error.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class NotificationType {
    
    /** 
     * default informative notification 
     */
    public final static NotificationType INFO = new NotificationType(1);
    
    /** 
     * a warning notification
     */
    public final static NotificationType WARN = new NotificationType(2);
    
    /** 
     * an error notification
     */
    public final static NotificationType ERROR = new NotificationType(3);
    
    private int type;
    
    /** Creates a new instance of NotificationWindowType */
    private NotificationType(int type) {
        this.type = type;
    }
    
    /**
     * overriden toString()
     */
    public String toString() {
        if (this.equals(NotificationType.INFO)) {
            return "Informative notification";
        } else if (this.equals(NotificationType.WARN)) {
            return "Notification of a Warning";
        } else if (this.equals(NotificationType.ERROR)) {
            return "Notification of an ERROR";
        } else {
            return "Seems to be invalid type?";
        } // end if
    }
    
    /**
     * overriden equals()
     */
    public boolean equals(Object obj) {
        if ((obj == null) || (!(obj instanceof NotificationType))) {
            return false;
        } // end if
        
        if (((NotificationType) obj).type == this.type) {
            return true;
        } else {
            return false;
        }// end if
    }
    
} // end of class NotificationType
