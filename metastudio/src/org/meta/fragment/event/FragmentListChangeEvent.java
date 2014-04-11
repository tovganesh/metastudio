/*
 * FragmentListChangeEvent.java
 *
 * Created on April 10, 2005, 7:30 AM
 */

package org.meta.fragment.event;

import java.util.*;
import org.meta.fragment.Fragment;
import org.meta.fragmentor.FragmentationScheme;

/**
 * The event object fired when a fragment list changes.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FragmentListChangeEvent extends EventObject {

    /**
     * Holds value of property type.
     */
    private FragmentListChangeEventType type;

    /**
     * Holds value of property affectedFragment.
     */
    private Fragment affectedFragment;
    
    /** Creates a new instance of FragmentListChangeEvent */
    public FragmentListChangeEvent(Object source) {
        super(source);
    }   

    /**
     * Getter for property type.
     * @return Value of property type.
     */
    public FragmentListChangeEventType getType() {
        return this.type;
    }

    /**
     * Setter for property type.
     * @param type New value of property type.
     */
    public void setType(FragmentListChangeEventType type) {
        this.type = type;
    }

    /**
     * Getter for property effectedFragment.
     * @return Value of property effectedFragment.
     */
    public Fragment getAffectedFragment() {
        return this.affectedFragment;
    }

    /**
     * Setter for property effectedFragment.
     * @param affectedFragment New value of property effectedFragment.
     */
    public void setAffectedFragment(Fragment affectedFragment) {
        this.affectedFragment = affectedFragment;
    }

    /**
     * overridden toString()
     */
    public String toString() {
        return this.type.toString();
    }
    
    /** enumerator of type of events */
    public enum FragmentListChangeEventType {
        FRAGMENT_ADDED,
        FRAGMENT_DELETED,
        FRAGMENT_MODIFIED
    }

    /**
     * Holds value of property affectedFragmentationScheme.
     */
    private FragmentationScheme affectedFragmentationScheme;

    /**
     * Getter for property affectedFragmentationScheme.
     * @return Value of property affectedFragmentationScheme.
     */
    public FragmentationScheme getAffectedFragmentationScheme() {
        return this.affectedFragmentationScheme;
    }

    /**
     * Setter for property affectedFragmentationScheme.
     * @param affectedFragmentationScheme New value of property 
     *        affectedFragmentationScheme.
     */
    public void setAffectedFragmentationScheme(FragmentationScheme 
                                                 affectedFragmentationScheme) {
        this.affectedFragmentationScheme = affectedFragmentationScheme;
    }
} // end of class FragmentListChangeEvent
