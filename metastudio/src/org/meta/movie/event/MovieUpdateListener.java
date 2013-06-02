/*
 * MovieUpdateListener.java
 *
 * Created on March 12, 2005, 8:36 PM
 */

package org.meta.movie.event;

/**
 * The movie update event listener.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface MovieUpdateListener extends java.util.EventListener {
    /**
     * The movie update event
     * 
     * @param me the movie update event object
     */
    public void movieUpdate(MovieUpdateEvent me);
} // end of interface MovieUpdateListener
