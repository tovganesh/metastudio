/*
 * FragmentListImpl.java
 *
 * Created on January 16, 2005, 6:09 PM
 */

package org.meta.fragment.impl;

import java.util.*;
import org.meta.fragment.Fragment;
import org.meta.fragment.FragmentList;
import org.meta.fragment.event.FragmentListChangeEvent;

/**
 * Default implementation of FragmentList.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FragmentListImpl extends FragmentList {
    
    private ArrayList<Fragment> fragmentList;
        
    /** Creates a new instance of FragmentListImpl */
    public FragmentListImpl() {
        super();
        
        fragmentList = new ArrayList<Fragment>(10);
    }

    /**
     * Add a fragment to this list, No duplicate checks are made.
     *
     * @param fragment the new fragment object in this list
     */
    @Override
    public void addFragment(Fragment fragment) {
        fragmentList.add(fragment);
        
        fragmentListChangeEvent.setType(
            FragmentListChangeEvent.FragmentListChangeEventType.FRAGMENT_ADDED);
        fragmentListChangeEvent.setAffectedFragment(fragment);
        fireFragmentListChangeListenerFragmentListChanged(
                                                  fragmentListChangeEvent);
    }

    /**
     * Append this fragment list with another.
     *
     * @param theList the fragment list to be appended to this list
     */
    @Override
    public void append(FragmentList theList) {
        Iterator<Fragment> fList = theList.getFragments();
        
        while(fList.hasNext()) {
            this.fragmentList.add(fList.next());
        } // end while
        
        fragmentListChangeEvent.setType(
            FragmentListChangeEvent.FragmentListChangeEventType.FRAGMENT_ADDED);
        fragmentListChangeEvent.setAffectedFragment(null);
        fireFragmentListChangeListenerFragmentListChanged(
                                                  fragmentListChangeEvent);
    }

    /**
     * Remove a fragment from this list.
     *
     * @param fragment the fragment object to be removed from this list
     */
    @Override
    public void removeFragment(Fragment fragment) {
        fragmentList.remove(fragment);
        
        fragmentListChangeEvent.setType(
          FragmentListChangeEvent.FragmentListChangeEventType.FRAGMENT_DELETED);
        fragmentListChangeEvent.setAffectedFragment(fragment);
        fireFragmentListChangeListenerFragmentListChanged(
                                                  fragmentListChangeEvent);
    }

    /**
     * Removes all fragment from this list.    
     */
    @Override
    public void removeAllFragments() {
        fragmentList.clear();
        
        fragmentListChangeEvent.setType(
          FragmentListChangeEvent.FragmentListChangeEventType.FRAGMENT_DELETED);        
        fireFragmentListChangeListenerFragmentListChanged(
                                                  fragmentListChangeEvent);        
    }
    
    /**
     * Removes all overlap fragment from this list.    
     */
    @Override
    public void removeOverlapFragments() {
        ArrayList<Fragment> remvList = new ArrayList<Fragment>();
                
        for(Fragment fragment : fragmentList) {
            if (fragment.isOverlapFragment()) {
                remvList.add(fragment);
            } // end if
        } // end for
        
        fragmentList.removeAll(remvList);
        
        fragmentListChangeEvent.setType(
          FragmentListChangeEvent.FragmentListChangeEventType.FRAGMENT_DELETED);        
        fireFragmentListChangeListenerFragmentListChanged(
                                                  fragmentListChangeEvent);        
    }
    
    /**
     * Purn the overlap fragments that have 'zero' cardinality sign
     */
    @Override
    public void purnZeroFragments() {
        ArrayList<Fragment> remvList = new ArrayList<Fragment>();
                
        for(Fragment fragment : fragmentList) {
            if (fragment.isOverlapFragment()) {
                if (fragment.getCardinalitySign() == 0) {
                    remvList.add(fragment);
                } // end if
            } // end if
        } // end for
        
        fragmentList.removeAll(remvList);
        
        fragmentListChangeEvent.setType(
          FragmentListChangeEvent.FragmentListChangeEventType.FRAGMENT_DELETED);        
        fireFragmentListChangeListenerFragmentListChanged(
                                                  fragmentListChangeEvent);        
    }
    
    /**
     * Return the list of fragments as an iterator object.
     *
     * @return an iterator of Fragment objects
     */
    @Override
    public Iterator<Fragment> getFragments() {
        return fragmentList.iterator();
    }
    
    /**
     * Return the fragments with the provided index.
     *
     * @return an instance of Fragment objects
     */
    @Override
    public Fragment getFragment(int index) {
        return fragmentList.get(index);
    }
    
    /**
     * Return the size of this list
     *
     * @return an integer representing the size of this list
     */
    @Override
    public int size() {
        return fragmentList.size();
    }
    
} // end of class FragmentListImpl
