/*
 * QuickViewerFactory.java
 *
 * Created on October 24, 2003, 10:01 PM
 */

package org.meta.shell.idebeans.viewers;

import java.util.Iterator;

/**
 * Defines how a factory should make objects of QuickViewer ;)
 * Well that is what it does, dynamically! as usual.
 * Follows the factory design pattern.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface QuickViewerFactory {
    
    /**
     * Return an instance of QuickViewer for the "mime" string supplied 
     * or else throw an UnsupportedOperationException.
     *
     * @return appropriate instance of QuickViewer for "mime"
     */
    public QuickViewer getViewer(String mime);

    /**
     * Returns a list of all available viewers as a list of "mime" strings
     *
     * @return Iterator : list of "mime" strings
     */
    public Iterator getAllSupportedViewers();
} // end of interface QuickViewerFactory
