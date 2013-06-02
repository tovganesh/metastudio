/**
 * SimpleAsyncTaskQueue.java
 *
 * Created on 22/09/2009
 */

package org.meta.parallel;

import java.lang.reflect.Method;
import java.util.ArrayDeque;
import org.meta.parallel.event.AsyncTaskCompletionListener;

/**
 * A simple async task queue that allows for submitting a large number of 
 * batch tasks for async processing. This class follows a singleton pattern.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class SimpleAsyncTaskQueue extends Thread {

    private ArrayDeque<SimpleAsyncTask> asyncTaskQueue;
    
    /** Private constructor */
    private SimpleAsyncTaskQueue() {
        setName("SimpleAsyncTaskQueue excuter thread");
        setPriority(MIN_PRIORITY);
        start();
    }

    private static SimpleAsyncTaskQueue _theInstance;

    /**
     * Obtain an instance of this task queue
     *
     * @return the instance of task queue
     */
    public static SimpleAsyncTaskQueue getInstance() {
        if (_theInstance == null) _theInstance = new SimpleAsyncTaskQueue();

        return _theInstance;
    }

    /** Initialize the task queue */
    private synchronized void initTaskQueue() {
        if (asyncTaskQueue == null)
            asyncTaskQueue = new ArrayDeque<SimpleAsyncTask>();
    }

    /**
     * Add a async task to this queue
     * 
     * @param theObject the target object
     * @param theMethod the method that is to be invoked asynchronously
     * @param args arguments to this method
     */
    public synchronized void addAsyncTask(Object theObject, Method theMethod,
                             Object... args) {
        initTaskQueue();
        asyncTaskQueue.add(SimpleAsyncTask.init(theObject, theMethod, args));
    }

    /**
     * Add a async task to this queue
     *
     * @param theObject the target object
     * @param theMethod the method that is to be invoked asynchronously
     * @param al a listener to be notified when this task completes
     * @param args arguments to this method
     */
    public synchronized void addAsyncTask(Object theObject, Method theMethod,
                             AsyncTaskCompletionListener al, Object... args) {
        initTaskQueue();

        SimpleAsyncTask aTask = SimpleAsyncTask.init(theObject, theMethod, args);
        aTask.addAsyncTaskCompletionListener(al);
        asyncTaskQueue.add(aTask);
    }
    
    /**
     * Add a async task to this queue
     *
     * @param theObject the target object
     * @param methodName the name of method that is to be invoked asynchronously
     * @param args arguments to this method
     * @throws NoSuchMethodException if a method of this name could not be found
     */
    public synchronized void addAsyncTask(Object theObject, String methodName,
                             Object... args) throws NoSuchMethodException {
        initTaskQueue();
        asyncTaskQueue.add(SimpleAsyncTask.init(theObject, methodName, args));
    }

    /**
     * Add a async task to this queue
     *
     * @param theObject the target object
     * @param methodName the name of method that is to be invoked asynchronously
     * @param args arguments to this method
     * @throws NoSuchMethodException if a method of this name could not be found
     */
    public synchronized void addAsyncTask(Object theObject, String methodName,
                             AsyncTaskCompletionListener al,
                             Object... args) throws NoSuchMethodException {
        initTaskQueue();
        
        SimpleAsyncTask aTask = SimpleAsyncTask.init(theObject, methodName, args);
        aTask.addAsyncTaskCompletionListener(al);
        asyncTaskQueue.add(aTask);
    }

    /** Run the async task queue executer thread */
    @Override
    public void run() {
        // end-less while loop
        while(true) {
            try {
                if (asyncTaskQueue == null || asyncTaskQueue.isEmpty()) {
                    sleep(1000);
                    continue;
                } // end if

                // get a SimpleAsyncTask
                SimpleAsyncTask asyncTask = null;
                synchronized(asyncTaskQueue) {
                    asyncTask = asyncTaskQueue.removeFirst();
                } // synchronized block

                // start the task
                asyncTask.start();

                // and wait for it to complete
                asyncTask.join();
            } catch(Exception ignored) {
                System.err.println("Exception in SimpleAsyncTaskQueue: "
                                    + ignored);
                ignored.printStackTrace();
                continue;
            } // end of try .. catch block
        } // end while
    }
}
