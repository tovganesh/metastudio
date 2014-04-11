/**
 * SimpleAsyncTask.java
 *
 * Created on May 12, 2009
 */
package org.meta.parallel;

import java.lang.reflect.Method;
import org.meta.common.EventListenerList;
import org.meta.parallel.event.AsyncTaskCompletionEvent;
import org.meta.parallel.event.AsyncTaskCompletionListener;

/**
 * Simple interface for running asynchronous activities. Functionality provided
 * by this interface is quite similar to one provided by
 * <code>java.util.concurrent.Future</code>, but does not have a
 * mechanism to return a value from this task.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class SimpleAsyncTask extends Thread {
    private Object theObject;
    private Method theMethod;
    private Object[] args;

    private SimpleAsyncTask(Object theObject, Method theMethod, Object... args) {
        this.theObject = theObject;
        this.theMethod = theMethod;
        this.args      = args;
    }

    /**
     * Generate an async activity based on the object and the method which is to
     * be called asynchronously.
     * 
     * @param theObject the target object
     * @param theMethod the method that is to be invoked asynchronously
     * @param args arguments to this method
     * @return instance of SimpleAsyncTask
     */
    public static SimpleAsyncTask init(Object theObject, Method theMethod,
                                       Object... args) {
        return new SimpleAsyncTask(theObject, theMethod, args);
    }

    /**
     * Generate an async activity based on the object and the method which is to
     * be called asynchronously.
     *
     * @param theObject the target object
     * @param methodName the name of method that is to be invoked asynchronously
     * @param args arguments to this method
     * @return instance of SimpleAsyncTask
     * @throws NoSuchMethodException if a method of this name could not be found
     */
    public static SimpleAsyncTask init(Object theObject, String methodName,
                                  Object... args) throws NoSuchMethodException {

        Class [] paramTypes = new Class[args.length];

        if (paramTypes.length > 0) {
            int i = 0;
            for(Object arg : args) {
                paramTypes[i++] = arg.getClass();
            } // end for
        } // end if
        
        return new SimpleAsyncTask(theObject, theObject.getClass()
                                    .getMethod(methodName, paramTypes), args);
    }
    
    @Override
    public void run() {
        try {
            setName("Thread SimpleAsyncTask on " + theObject.getClass().getName()
                 + "." + theMethod.getName() + "()");
            Object returnValue = theMethod.invoke(theObject, args);

            AsyncTaskCompletionEvent atce = new AsyncTaskCompletionEvent(this);
            atce.setReturnValue(returnValue);

            fireAsyncTaskCompletionListenerAsyncTaskCompleted(atce);
        } catch(Exception e) {
            System.err.println("Exception in thread : " + e.toString());
            e.printStackTrace();
        } // end of try ... catch block
    }

    /** Utility field used by event firing mechanism. */
    protected transient EventListenerList<AsyncTaskCompletionListener> listenerList =  null;
    
    /** Registers AsyncTaskCompletionListener to receive events.
     * @param listener The listener to register.
     *
     */
    public synchronized void addAsyncTaskCompletionListener(
                                AsyncTaskCompletionListener listener) {
        if (listenerList == null ) {
            listenerList = new EventListenerList<AsyncTaskCompletionListener>();
        }
        listenerList.add(AsyncTaskCompletionListener.class, listener);
    }

    /** Removes AsyncTaskCompletionListener from the list of listeners.
     * @param listener The listener to remove.
     *
     */
    public synchronized void removeAsyncTaskCompletionListener(
                                    AsyncTaskCompletionListener listener) {
        listenerList.remove(AsyncTaskCompletionListener.class, listener);
    }

    /** Notifies all registered listeners about the event.
     *
     * @param event The event to be fired
     *
     */
    protected void fireAsyncTaskCompletionListenerAsyncTaskCompleted(
                                    AsyncTaskCompletionEvent event) {
        if (listenerList == null) return;
        
        for(Object listener : listenerList.getListenerList()) {
            ((AsyncTaskCompletionListener)listener).asyncTaskCompleted(event);
        } // end for        
    }
}
