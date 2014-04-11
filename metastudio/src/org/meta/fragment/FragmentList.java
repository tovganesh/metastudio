/*
 * FragmentList.java
 *
 * Created on August 20, 2004, 6:59 AM
 */

package org.meta.fragment;

import java.util.Iterator;
import org.meta.common.EventListenerList;
import org.meta.fragment.event.FragmentListChangeEvent;
import org.meta.fragment.event.FragmentListChangeListener;
import org.meta.fragmentor.FragmentationScheme;

/**
 * The fragment list for a particular molecule.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public abstract class FragmentList {

    /**
     * Utility field used by event firing mechanism.
     */
    private EventListenerList<FragmentListChangeListener> listenerList =  null;
    
    protected FragmentListChangeEvent fragmentListChangeEvent;
    
    /** Creates a new instance of FragmentList */
    public FragmentList() {
        fragmentListChangeEvent = new FragmentListChangeEvent(this);
    }
    
    /**
     * Add a fragment to this list, No duplicate checks are made.
     *
     * @param fragment the new fragment object in this list
     */
    public abstract void addFragment(Fragment fragment);
    
    /**
     * Remove a fragment from this list.
     *
     * @param fragment the fragment object to be removed from this list
     */
    public abstract void removeFragment(Fragment fragment);
    
    /**
     * Removes all fragment from this list.    
     */
    public abstract void removeAllFragments();
    
    /**
     * Removes all overlap fragment from this list.    
     */
    public abstract void removeOverlapFragments();
    
    /**
     * Purn the overlap fragments that have 'zero' cardinality sign
     */
    public abstract void purnZeroFragments();
    
    /**
     * Append this fragment list with another.
     *
     * @param fragmentList the fragment list to be appended to this list
     */
    public abstract void append(FragmentList fragmentList);
    
    /**
     * Return the list of fragments as an iterator object.
     *
     * @return an iterator of Fragment objects
     */
    public abstract Iterator<Fragment> getFragments();
    
    /**
     * Return the fragments with the provided index.
     *
     * @return an instance of Fragment objects
     */
    public abstract Fragment getFragment(int index);
    
    /**
     * Return the size of this list
     *
     * @return an integer representing the size of this list
     */
    public abstract int size();

    /**
     * To indiacted if listeners will be notified
     */
    private boolean listernersEnabled = true;
    
    /**
     * Enable the listeners
     */
    public void enableListiners() {
        listernersEnabled = true;
    }
    
    /**
     * Disable the listeners
     */
    public void disableListiners() {
        listernersEnabled = false;
    }
    
    /**
     * Registers FragmentListChangeListener to receive events.
     * @param listener The listener to register.
     */
    public synchronized void addFragmentListChangeListener(
                                        FragmentListChangeListener listener) {
        if (listenerList == null ) {
            listenerList = new EventListenerList<FragmentListChangeListener>();
        }
        listenerList.add(FragmentListChangeListener.class, listener);
    }

    /**
     * Removes FragmentListChangeListener from the list of listeners.
     * @param listener The listener to remove.
     */
    public synchronized void removeFragmentListChangeListener(
                                          FragmentListChangeListener listener) {
        listenerList.remove(FragmentListChangeListener.class, listener);
    }

    /**
     * Trigger listeners, the default event type sent is FRAGMENT_MODIFIED
     */
    public void triggerListeners() {
     if (fragmentListChangeEvent == null)
        fragmentListChangeEvent = new FragmentListChangeEvent(this);
     
     // no triggering is parent scheme is not set
     if (parentFragmentationScheme == null) return;
     
     fragmentListChangeEvent.setAffectedFragmentationScheme(
                                parentFragmentationScheme);
     fragmentListChangeEvent.setType(
             FragmentListChangeEvent.FragmentListChangeEventType
              .FRAGMENT_MODIFIED);
     
     fireFragmentListChangeListenerFragmentListChanged(fragmentListChangeEvent);
    }
    
    /**
     * Trigger listeners, the default event type sent is FRAGMENT_MODIFIED
     *
     * @param fragment the affected fragment
     */
    public void triggerListeners(Fragment fragment) {
     if (fragmentListChangeEvent == null)
        fragmentListChangeEvent = new FragmentListChangeEvent(this);
     
     // no triggering is parent scheme is not set
     if (parentFragmentationScheme == null) return;
     
     fragmentListChangeEvent.setAffectedFragmentationScheme(
                                parentFragmentationScheme);
     fragmentListChangeEvent.setAffectedFragment(fragment);
     fragmentListChangeEvent.setType(
             FragmentListChangeEvent.FragmentListChangeEventType
              .FRAGMENT_MODIFIED);
     
     fireFragmentListChangeListenerFragmentListChanged(fragmentListChangeEvent);
    }
    
    /**
     * Notifies all registered listeners about the event.
     * 
     * @param event The event to be fired
     */
    protected void fireFragmentListChangeListenerFragmentListChanged(
                                                FragmentListChangeEvent event) {
        if (!listernersEnabled) return;
        
        if (listenerList == null) return;
        
        for(Object listener : listenerList.getListenerList()) {
            ((FragmentListChangeListener) listener).fragmentListChanged(event);
        } // end for        
    }

    /**
     * Holds value of property parentFragmentationScheme.
     */
    protected FragmentationScheme parentFragmentationScheme;

    /**
     * Getter for property parentFragmentationScheme.
     * @return Value of property parentFragmentationScheme.
     */
    public FragmentationScheme getParentFragmentationScheme() {
        return this.parentFragmentationScheme;
    }

    /**
     * Setter for property parentFragmentationScheme.
     * @param parentFragmentationScheme New value of property 
     *        parentFragmentationScheme.
     */
    public void setParentFragmentationScheme(
                                FragmentationScheme parentFragmentationScheme) {
        this.parentFragmentationScheme = parentFragmentationScheme;
        
        fragmentListChangeEvent.setAffectedFragmentationScheme(
                                                     parentFragmentationScheme);
    }
    
} // end of class FragmentList
