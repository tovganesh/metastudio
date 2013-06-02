/*
 * StopWatch.java
 *
 * Created on Feb 1, 2010, 4:37 PM
 */

package org.meta.common;

/**
 * Monitor time conveniently, based on Timer class as implemented in X10 HPC benckmark examples.
 * The time is measured using <code>System.nanoTime() </code>
 *
 * <b>Note:</b> It is up to the programmer to take care that <code>.start()</code> 
 * and <code>.stop()</code> for appropriate IDs are called for the values 
 * of <code>.total()</code> to be sensible!
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */

public final class StopWatch {
    private long [] total;
    private long [] count;

    /** Creates a new instance of StopWatch.
     *  
     * @param n - number of unique timers to create
     */  
    public StopWatch(int n) {
        total = new long[n];
        count = new long[n];
    }

    /**
     * Start a timer for a particular ID (0 to n-1)
     *
     * @param id the ID of the timer
     */
    public void start(int id) { 
       total[id] -= System.nanoTime(); 
    }

    /**
     * Clear a timer for a particular ID (0 to n-1)
     *
     * @param id the ID of the timer
     */
    public void clear(int id) { 
       total[id] = 0; 
    }

    /**
     * Stop a timer for a particular ID (0 to n-1)
     *
     * @param id the ID of the timer
     */
    public void stop(int id) { 
       total[id] += System.nanoTime(); 
       count[id]++; 
    }   

    /**
     * Total time for a particular ID
     *
     * @param id the ID of the timer
     */  
    public long total(int id) {
       return total[id];
    }

    /**
     * Total number of times the timer was stopped
     *
     * @param id the ID of the timer
     */  
    public long count(int id) {
       return count[id];
    }
}

