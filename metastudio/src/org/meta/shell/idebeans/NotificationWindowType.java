/*
 * NotificationWindowType.java
 *
 * Created on April 11, 2004, 10:44 PM
 */

package org.meta.shell.idebeans;

/**
 * Enumeration defining the type of NotificationWindow.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class NotificationWindowType {
    
    /** default interactive, nonintervening notification window type */
    public final static NotificationWindowType NORMAL = 
                                    new NotificationWindowType(1);
    
    /** for just displaying notification messages, and which automatiaclly
     * closes after a specified number of milliseconds.
     */
    public final static NotificationWindowType AUTO_CLOSE = 
                                    new NotificationWindowType(2);
    
    private int type;
    
    /** Creates a new instance of NotificationWindowType */
    private NotificationWindowType(int type) {
        this.type = type;
    }
    
    /**
     * overriden toString()
     */
    public String toString() {
        if (this.equals(NotificationWindowType.NORMAL)) {
            return 
             "default interactive, nonintervening notification window type";
        } else if (this.equals(NotificationWindowType.AUTO_CLOSE)) {
            return "or just displaying notification messages, "
             + "and which automatiaclly closes after a specified "
             + "number of milliseconds.";
        } else {
            return "Seems to be invalid type?";
        } // end if
    }
    
    /**
     * overriden equals()
     */
    public boolean equals(Object obj) {
        if ((obj == null) || (!(obj instanceof NotificationWindowType))) {
            return false;
        } // end if
        
        if (((NotificationWindowType) obj).type == this.type) {
            return true;
        } else {
            return false;
        }// end if
    }
    
} // end of class NotificationWindowType
