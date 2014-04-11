/*
 * WorkspaceItemFactoryImpl.java
 *
 * Created on November 9, 2003, 6:09 PM
 */

package org.meta.workspace.impl;

import java.util.*;
import java.beans.PropertyVetoException;
import org.meta.common.resource.StringResource;
import org.meta.workspace.WorkspaceItem;
import org.meta.workspace.WorkspaceItemFactory;

/**
 * A simple factory for generating WorkspaceItem objects.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class WorkspaceItemFactoryImpl implements WorkspaceItemFactory {
    
    private Hashtable<String, String> theKeys;
    
    /** Creates a new instance of WorkspaceItemFactoryImpl */
    public WorkspaceItemFactoryImpl() {
        // the initial parameters
        try {
            theKeys = new Hashtable<String, String>(10);
            
            setDefaultParams();
        } catch (PropertyVetoException ignored) {
            // because it never should happen in this context
            System.err.println(ignored.toString());
        } 
    }
    
    /**
     * private method to set the default parameters
     */
    private void setDefaultParams() throws PropertyVetoException {
        StringResource strings   = StringResource.getInstance();
        ResourceBundle resources = ResourceBundle.getBundle(
                                      strings.getWorkspaceItemImplResource());
        
        // first, we map the important interface implementation classes
        // that define the concrete MeTA Studio implementation!        
        Enumeration<String> implKeys = resources.getKeys();
        String theKey;
        
        while(implKeys.hasMoreElements()) {
            theKey = implKeys.nextElement();
            this.theKeys.put(theKey, resources.getString(theKey));
        } // end while                      
    }
    
    /**
     * method to return all available workspace items in the form similar to:
     * <pre>
     *   workspace/[item-type]
     * </pre>
     * the <code>item-type</code> may be a string e.g. <code>molecule</code>
     * or <code>dm</code> etc.
     *
     * @return Iterator list of all the supported types
     */
    @Override
    public Iterator getAllItemTypes() {        
        return theKeys.keySet().iterator();  
    }
    
    /**
     * method to get the current class handling a particular item type.
     * for e.g. "workspace/molecule"
     *
     * @return The class of type WorkspaceItem handling the specified item
     */
    @Override
    public Class<WorkspaceItem> getItemClass(String itemType) {        
        try {
            return ((Class<WorkspaceItem>) 
                     Class.forName((String) theKeys.get(itemType)));
        } catch (Exception e) {
            throw new UnsupportedOperationException("Unable to find a proper " 
                      + "class for handling type : " + itemType + "."
                      + " Exception is : " + e.toString());
        } // end try .. catch block
    }
    
} // end of class WorkspaceItemFactoryImpl
