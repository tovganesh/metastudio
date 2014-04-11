/*
 * MovieUpdateEvent.java
 *
 * Created on March 12, 2005, 8:36 PM
 */

package org.meta.movie.event;

/**
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MovieUpdateEvent extends java.util.EventObject {

    /**
     * Holds value of property currentMovieScene.
     */
    private int currentMovieScene;

    /**
     * Holds value of property movieStatus.
     */
    private MovieStatus movieStatus;
    
    /** Creates a new instance of MovieUpdateEvent */
    public MovieUpdateEvent(Object source) {
        super(source);
    }

    /**
     * Getter for property currentMovieScene.
     * @return Value of property currentMovieScene.
     */
    public int getCurrentMovieScene() {
        return this.currentMovieScene;
    }

    /**
     * Setter for property currentMovieScene.
     * @param currentMovieScene New value of property currentMovieScene.
     */
    public void setCurrentMovieScene(int currentMovieScene) {
        this.currentMovieScene = currentMovieScene;
    }   

    /**
     * Getter for property movieStatus.
     * @return Value of property movieStatus.
     */
    public MovieStatus getMovieStatus() {
        return this.movieStatus;
    }

    /**
     * Setter for property movieStatus.
     * @param movieStatus New value of property movieStatus.
     */
    public void setMovieStatus(MovieStatus movieStatus) {
        this.movieStatus = movieStatus;
    }
    
    /**
     * The movie status
     */
    public enum MovieStatus {
        PAUSED, PLAYING, STOPED, RESUMED
    }
} // end of class MovieUpdateEvent
