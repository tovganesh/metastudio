/**
 * MarObject.java
 *
 * Created on 18/04/2010
 */

package org.meta.mar;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A runtime equivalent of MeTA Studio Archive format object.
 * 
 * Contains all the mar items including the manifest file.
 * 
 * The following are format restrictions for .mar file:
 *  - They are ZIP archive with .mar extension
 *  - They must have exactly one manifest.xml file
 *  - There is not directory structure supported (read not package base)
 *  - All flies, including resource files should be in the current directory
 *  - If directory entries are found, the implementation reader might
 *    choose to ignore it, but no error should be flagged. 
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MarObject {

    private ArrayList<MarItem> marItems;
    
    /** Creates a new instance of MarObject */
    public MarObject() {
        marItems = new ArrayList<MarItem>();
    }

    /**
     * Add an archive object
     *
     * @param item the archive object to be added
     */
    public void addMarItem(MarItem item) {
        marItems.add(item);
    }

    /**
     * Remove an archive object
     *
     * @param index the index of the archive object to be removed
     */
    public void removeMarItem(int index) {
        marItems.remove(index);
    }

    /**
     * Remove an archive object
     *
     * @param item the archive object ot be removed
     */
    public void removeMarItem(MarItem item) {
        marItems.remove(item);
    }

    /**
     * Get the instance of archive object
     *
     * @param index the index of archive object to be retrived
     * @return the instance of the archive object requested
     * @throws ArrayIndexOutOfBoundsException if no element is found at the
     *         requested index
     */
    public MarItem getMarItem(int index) {
        return marItems.get(index);
    }

    /**
     * Get an iterator of the the archive objects
     *
     * @return the iterator instance
     */
    public Iterator<MarItem> getMarItems() {
        return marItems.iterator();
    }

    protected MarManifest marManifest;

    /**
     * Get the value of marManifest
     *
     * @return the value of marManifest
     */
    public MarManifest getMarManifest() {
        return marManifest;
    }

    /**
     * Set the value of marManifest
     *
     * @param marManifest new value of marManifest
     */
    public void setMarManifest(MarManifest marManifest) {
        this.marManifest = marManifest;
    }

    /**
     * Returns a MarItem with the specified name
     *
     * @param marItemName the name of the MarItem requested
     * @return the MarItem with the specified name if found, null other wise
     */
    public MarItem getMarItem(String marItemName) {
        if (marItems.isEmpty()) return null;

        for(MarItem mi : marItems) {
            if (mi.getName().equals(marItemName)) return mi;
        } // end for

        return null;
    }
}
