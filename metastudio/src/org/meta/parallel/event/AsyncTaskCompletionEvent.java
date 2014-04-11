/**
 * AsyncTaskCompletionEvent.java
 *
 * Created on 22/09/2009
 */

package org.meta.parallel.event;

/**
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class AsyncTaskCompletionEvent extends java.util.EventObject {

    /** Creates a new instance of AsyncTaskCompletionEvent */
    public AsyncTaskCompletionEvent(Object source) {
        super(source);
    }

    protected Object returnValue;

    /**
     * Get the value of returnValue
     *
     * @return the value of returnValue
     */
    public Object getReturnValue() {
        return returnValue;
    }

    /**
     * Set the value of returnValue
     *
     * @param returnValue new value of returnValue
     */
    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }
}
