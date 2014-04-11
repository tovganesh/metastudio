/**
 * FloatingList.java
 *
 * Created on Jul 7, 2009
 */
package org.meta.shell.idebeans;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;

/**
 * A floating container that incorporates a JList along with a JTextBox
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FloatingList extends JPanel implements AWTEventListener {

    private JList theList;
    private JScrollPane theListScrollPane;
    private JTextField theTextField;

    private PopupFactory factory;
    private Popup popup;

    /** Creates a new instance of FloatingList */
    public FloatingList() {
        this.factory = PopupFactory.getSharedInstance();

        initComponents();
    }

    /** Initialize the components */
    private void initComponents() {
        setLayout(new GridLayout(1,1));

        theTextField = new JTextField(20);
        theTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode()==KeyEvent.VK_ENTER ||
                    e.getKeyCode()==KeyEvent.VK_ESCAPE) {
                    hideIt();
                } else {
                    showIt(0, theTextField.getHeight());
                } // end if
            }
        });
        theTextField.addMouseListener(new MouseAdapter() {            
            @Override
            public void mouseClicked(MouseEvent e) {
                showIt(0, theTextField.getHeight());
            }                        
        });
        add(theTextField);

        theList = new JList();
        theList.setModel(new FloatingListModel());
        theListScrollPane = new JScrollPane(theList,
                                 JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                 JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        theTextField.getDocument().addDocumentListener(
                        (FloatingListModel) theList.getModel());
    }

    /**
     * Set the tool tip text
     * 
     * @param text the tool tip text
     */
    @Override
    public void setToolTipText(String text) {
        theTextField.setToolTipText(text);
    }

    /**
     * Add a mouse listener to this floating list
     *
     * @param ml
     */
    @Override
    public void addMouseListener(MouseListener ml) {
        theList.addMouseListener(ml);
    }

    /**
     * Remove the mouse listener attached to this floating list
     * 
     * @param ml
     */
    @Override
    public void removeMouseListener(MouseListener ml) {
        theList.removeMouseListener(ml);
    }

    /**
     * hide the JList window
     */
    private synchronized void hideIt() {
        if (popup != null) {
            popup.hide();
            popup = null;
        } // end if
    }

    /**
     * show the JList window
     *
     * @param owner - Component mouse coordinates are relative to, may be null
     * @param x - Initial x screen coordinate
     * @param y - Initial y screen coordinate
     */
    private synchronized void showIt(int x, int y) {
        // make sure the popup is not already active
        if (popup != null) {
            hideIt();
        } // end if

        Point p = new Point(x, y);
        SwingUtilities.convertPointToScreen(p, theTextField);

        x = p.x;
        y = p.y;
        
        // try and see if x, y are out of range ?
        // a very simplified adjustment
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

        // adjust 'x' if needed
        if (x + theListScrollPane.getPreferredSize().width > screen.width) {
            x -= theListScrollPane.getPreferredSize().width;
        } // end if

        // adjust 'y' if needed
        if (y + theListScrollPane.getPreferredSize().height > screen.height) {
            y -= theListScrollPane.getPreferredSize().height;
        } // end if

        // show the stuff
        popup = factory.getPopup(theTextField, theListScrollPane, x, y);
        popup.show();
    }

    /**
     * overridden method to trap ESC key and any mouse clicks outside the
     * floating list window
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
                me = (MouseEvent) event;

                // if clicked out side our area, hide it
                if (!this.contains(me.getPoint())
                      && !theListScrollPane.contains(me.getPoint())) {
                    hideIt();
                } // end if
                
                break;
        } // end switch .. case
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
     * Adds an action listener to the floating list
     * 
     * @param al the listener object
     */
    public void addActionListener(ActionListener al) {
        theTextField.addActionListener(al);
    }
    
    /**
     * Removes an action listener from this floating list
     * 
     * @param al the listener object
     */
    public void removeActionListener(ActionListener al) {
        theTextField.removeActionListener(al);
    }

    /**
     * Dynamically add entries if FloatingListModel interface is implemented
     * by the model for this floating list.
     * NOTE: This operation can be expensive!
     *
     * @param item the item to be added
     * @throws UnsupportedOperationException if this operation is not possible
     */
    public void addItem(Object item) {
        try {
            ((FloatingListModel) theList.getModel()).addElement(item);
            theList.setSelectedValue(item, true);
        } catch(Exception e) {
            throw new UnsupportedOperationException("addItem() is not supported!");
        }
    }

    /**
     * Dynamically remove entries if FloatingListModel interface is implemented
     * by the model for this floating list
     * NOTE: This operation can be expensive!
     * 
     * @param item the item to be removed
     * @throws UnsupportedOperationException if this operation is not possible
     */
    public void removeItem(Object item) {
        try {
            ((FloatingListModel) theList.getModel()).removeElement(item);
        } catch(Exception e) {
            throw new UnsupportedOperationException("removeItem() is not supported!");
        }
    }

    /**
     * Set the model for this floating list
     *
     * @param lm the new instance of ListModel
     * @throws IllegalArgumentException if lm is not of type FloatingListModel
     */
    public void setModel(ListModel lm) {
        if (!(lm instanceof FloatingListModel)) {
            throw new IllegalArgumentException("ListModel should be of type" +
                    " FloatingListModel");
        } else {
            theList.setModel(lm);
        } // end if
    }

    /**
     * Return the model associated with this floating list
     *
     * @return the instance of associated ListModel
     */
    public ListModel getModel() {
        return theList.getModel();
    }

    /**
     * Sets a new cell renderer for the floating list
     *
     * @param lcr instance of the new cell renderer
     */
    public void setCellRenderer(ListCellRenderer lcr) {
        theList.setCellRenderer(lcr);
    }

    /**
     * Return the current cell renderer of this floating list
     *
     * @return the current cell renderer
     */
    public ListCellRenderer getCellRenderer() {
        return theList.getCellRenderer();
    }

    /**
     * Get the selected item from this floating list
     *
     * @return the selected item
     */
    public Object getSelectedItem() {
        if (theList.getSelectedValue() == null)
            return theTextField.getText();
        else
            return theList.getSelectedValue();
    }

    /**
     * Get the index of an object in this list
     *
     * @param obj the object to search for
     * @return 0 or higher if the object is found -1 is none is found,
     *         or if the associated model is not FloatingListModel
     */
    public int getIndexOf(Object obj) {
        try {
            return ((FloatingListModel) theList.getModel()).getIndexOf(obj);
        } catch(Exception ignored) {
            return -1;
        } // try .. catch block
    }
}
