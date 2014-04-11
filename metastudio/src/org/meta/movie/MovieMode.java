/*
 * MovieMode.java
 *
 * Created on March 12, 2005, 7:22 AM
 */

package org.meta.movie;

/**
 * A bare minimum interface for defining a movie based class.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public interface MovieMode {
    /**
     * Getter for property repeatMovie.
     * @return Value of property repeatMovie.
     */
    public boolean isRepeatMovie();

    /**
     * Setter for property repeatMovie.
     * @param repeat New value of property repeatMovie.
     */
    public void setRepeatMovie(boolean repeat);

    /**
     * Getter for property movieDelay.
     * @return Value of property movieDelay.
     */
    public int getMovieDelay();

    /**
     * Setter for property movieDelay.
     * @param delay New value of property movieDelay.
     */
    public void setMovieDelay(int delay);

    /**
     * start the movie
     */
    public void beginMovie();

    /**
     * stop the movie
     */
    public void endMovie();

    /**
     * pause the movie
     */
    public void pauseMovie();

    /**
     * resume the movie
     */
    public void resumeMovie();
    
    /**
     * Getter for property currentMovieScene.
     * @return Value of property currentMovieScene.
     */
    public int getCurrentMovieScene();

    /**
     * Setter for property currentMovieScene.
     * @param currentMovieScene New value of property currentMovieScene.
     */
    public void setCurrentMovieScene(int currentMovieScene);
        
} // end of interface MovieMode
