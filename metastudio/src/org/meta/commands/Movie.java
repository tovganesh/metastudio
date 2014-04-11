/*
 * Movie.java
 *
 * Created on October 10, 2004, 11:44 AM
 */

package org.meta.commands;

import org.meta.movie.MovieMode;

/**
 * The Movie class to be only used with <code> movie() </code> script.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public abstract class Movie implements Runnable, MovieMode {
    
    protected int delay;
    
    protected boolean stop, started;

    /**
     * Holds value of property repeat.
     */
    protected boolean repeat;

    protected boolean paused;
    
    /** Creates a new instance of Movie */
    public Movie() { this(10); }
    
    /** Creates a new instance of Movie */
    public Movie(int delay) {
        this.delay = delay;

        stop = started = paused = false;
    }
    
    /**
     * start the execution
     */
    @Override
    public abstract void beginMovie();
    
    /**
     * start the movie
     */
    @Override
    public abstract void endMovie();
    
    /**
     * pause the movie
     */
    @Override
    public abstract void pauseMovie();

    /**
     * resume the movie
     */
    @Override
    public void resumeMovie() {
        // TODO: not implimented
    }
    
    /**
     * Setter for property repeat.
     * @param repeat New value of property repeat.
     */
    @Override
    public void setRepeatMovie(boolean repeat) {
        this.repeat = repeat;
    }
    
    /**
     * Getter for property repeat.
     * @return Value of property repeat.
     */
    @Override
    public boolean isRepeatMovie() {
        return this.repeat;
    }
    
    /**
     * setter method for delay
     */
    @Override
    public void setMovieDelay(int delay) {
        this.delay = delay;
    }
    
    /**
     * Getter for property delay.
     * @return Value of property delay.
     */
    @Override
    public int getMovieDelay() {
        return this.delay;
    }
    
    /**
     * Getter for property currentMovieScene.
     * @return Value of property currentMovieScene.
     */
    @Override
    public int getCurrentMovieScene() {
        // TODO: NOT implimented!
        return 0;
    }

    /**
     * Setter for property currentMovieScene.
     * @param currentMovieScene New value of property currentMovieScene.
     */
    @Override
    public void setCurrentMovieScene(int currentMovieScene) {
        // TODO: NOT implimented!
    }
} // end of class Movie
