/**
 * IDEOkCancelDialog.java
 *
 * Created on 31/07/2009
 */

package org.meta.shell.idebeans;

import java.awt.Frame;
import javax.swing.JDialog;
import org.meta.common.resource.ImageResource;

/**
 * Generic Ok / Cancel dialog in the IDE
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public abstract class IDEOkCancelDialog extends JDialog {

    protected IDEDialogState currentState;

    /** Creates a new instace of this dialog */
    public IDEOkCancelDialog(Frame owner, String title) {
        super(owner, title);

        // this is always a modal dialog
        setModal(true);

        // set icon for this dialog
        setIconImage(ImageResource.getInstance().getQuestion().getImage());

        // init the dialog
        initComponents();

        currentState = IDEDialogState.NONE;
    }

    /**
     * Actually show the login dialog
     *
     * @return the dialog status, OK / CANCEL
     */
    public IDEDialogState showLoginDialog() {
        pack();
        setLocationRelativeTo(getOwner());
        setVisible(true);
       
        return currentState;
    }
    
    /** method to be overidden by the subclass */
    protected abstract void initComponents();

    /** the dialog state for this dialog */
    public enum IDEDialogState { OK, CANCEL, NONE };
}
