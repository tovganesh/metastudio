/*
 * FragmentListChangeListener.java
 *
 * Created on April 10, 2005, 7:36 AM
 */

package org.meta.fragment.event;

import java.util.*;

/**
 * The listeners of fragment list change event.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface FragmentListChangeListener extends EventListener {
    /**
     * called when a fragment list is changed
     *
     * @param flce the instance of FragmentListChangeEvent indicating the type
     *             of change.
     */
    public void fragmentListChanged(FragmentListChangeEvent flce);    
} // end of interface FragmentListChangeListener
