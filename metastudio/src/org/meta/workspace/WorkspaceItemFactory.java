/*
 * WorkspaceItemFactory.java
 *
 * Created on November 9, 2003, 7:08 AM
 */

package org.meta.workspace;

import java.util.Iterator;

/**
 * The preferred way of creating WorkspaceItem is via WorkspaceItemFactory, 
 * but this is not an enforcement as with other subsustem's factory interface.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface WorkspaceItemFactory {
   
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
    public Iterator getAllItemTypes();
    
    /**
     * method to get the current class handling a particular item type.
     *
     * @return The class of type WorkspaceItem handling the specified item
     */
    public Class<WorkspaceItem> getItemClass(String itemType);
    
} // end of interface WorkspaceItemFactory
