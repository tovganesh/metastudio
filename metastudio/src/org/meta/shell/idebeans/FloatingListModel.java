/**
 * FloatingListModel.java
 *
 * Created on 26/07/2009
 */

package org.meta.shell.idebeans;

import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 * Create a specialized model for FloatingList. Influenced by:
 * <a href="http://java.sun.com/developer/JDCTechTips/2005/tt1214.html">
 * http://java.sun.com/developer/JDCTechTips/2005/tt1214.html</a>. 
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FloatingListModel extends DefaultComboBoxModel
                               implements DocumentListener {
    private Vector<Object> filteredList;
    private String lastFilter = "";

    /** Creates a new instance of FloatingListModel */
    public FloatingListModel() {
        super();
        filteredList = new Vector<Object>();
    }

    /**
     * Add an element to the model, and also filter it
     *
     * @param element the element to be added
     */
    @Override
    public void addElement(Object element) {
        super.addElement(element);
        filter(lastFilter);
    }

    /**
     * Remove an element to the model, and also filter it
     *
     * @param element the element to be added
     */
    @Override
    public void removeElement(Object element) {
        super.removeElement(element);
        filter(lastFilter);
    }

    /**
     * Get the current size of this model
     *
     * @return the size of the model
     */
    @Override
    public int getSize() {
        return filteredList.size();
    }

    /**
     * Get element at the specified index
     *
     * @param index the index
     * @return the value at the above specified index
     */
    @Override
    public Object getElementAt(int index) {
        Object returnValue;
        if (index < filteredList.size()) {
            returnValue = filteredList.get(index);
        } else {
            returnValue = null;
        }
        return returnValue;
    }

    /**
     * Do the actual filtering into the locally maintained list
     * 
     * @param search the search string
     */
    protected void filter(String search) {
        filteredList.clear();
        for (int i=0; i<super.getSize(); i++) {
            Object element = super.getElementAt(i);
            
            if (search.equals("")) {
                filteredList.add(element);
            } else if (element.toString().toLowerCase().indexOf(search, 0) != -1) {
                filteredList.add(element);
            } // end of
        } // end for

        fireContentsChanged(this, 0, getSize());
    }

    /**
     * Document handling method, for filtering
     *
     * @param event the document event
     */
    @Override
    public void insertUpdate(DocumentEvent event) {
        Document doc = event.getDocument();
        try {
            lastFilter = doc.getText(0, doc.getLength());
            filter(lastFilter);
        } catch (BadLocationException ble) {
            System.err.println("Bad location: " + ble);
        }
    }

    /**
     * Document handling event, for filtering
     *
     * @param event the document event
     */
    @Override
    public void removeUpdate(DocumentEvent event) {
        Document doc = event.getDocument();
        try {
            lastFilter = doc.getText(0, doc.getLength());
            filter(lastFilter);
        } catch (BadLocationException ble) {
            System.err.println("Bad location: " + ble);
        }
    }

    /**
     * Document handling event, for filtering
     *
     * @param event the document event
     */
    @Override
    public void changedUpdate(DocumentEvent event) {
        // EMPTY!
    }
}
