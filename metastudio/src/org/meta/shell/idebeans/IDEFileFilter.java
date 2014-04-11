/*
 * IDEFileFilter.java
 *
 * Created on September 1, 2003, 6:42 AM
 */

package org.meta.shell.idebeans;

import java.util.*;
import java.io.File;

import javax.swing.filechooser.*;

/**
 * A generic filter class for providing file filters to JFileChooser dialogs.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class IDEFileFilter extends FileFilter {
    
    private Vector<String> filters;
    private String description;
    
    /** 
     * Creates a new instance of IDEFileFilter 
     *
     * @param filter eg "xyz", "vis" or "pdb"
     *        description the description of this file type
     */
    public IDEFileFilter(String filter, String description) {
        this(new String[] {filter}, description);
    }
    
    /** 
     * Creates a new instance of IDEFileFilter 
     *
     * @param filters eg new String[] { "xyz", "vis", "pdb"}
     *        description the description of these file types
     */
    public IDEFileFilter(String [] filters, String description) {
        this.filters = new Vector<String>();
        
        for(int i = 0; i<filters.length; i++) {
            this.filters.add(filters[i]);
        } // end for
        
        this.description = description;
    }
    
    /** 
     * Creates a new instance of IDEFileFilter 
     *
     * @param filters eg new Object[] with toString() 
     *        returning { "xyz", "vis", "pdb"}
     *        description the description of these file types
     */
    public IDEFileFilter(Object [] filters, String description) {
        this.filters = new Vector<String>();
        
        for(int i = 0; i<filters.length; i++) {
            this.filters.add(filters[i].toString());
        } // end for
        
        this.description = description;
    }
    
    /**
     * return true if this file should be shown else false.     
     */
    public boolean accept(File file) {
        if (file.isDirectory()) return true; // show all directories
        
        // and filter out the files that are required to be shown
        String extension = file.getName();
        
        if (extension.lastIndexOf('.') == -1) return false; // no extension
        
        extension = extension.substring(extension.lastIndexOf('.')+1, 
                                        extension.length()).toLowerCase();
        
        return filters.contains(extension);
    }
    
    /**
     * returns the human readable description of this filter. 
     * eg: "All molecules file formats (*.xyz, *.vis, *.pdb)"
     */
    public String getDescription() {
        String fileInfo = " (";
        
        for(int i=0; i<filters.size(); i++) {
            fileInfo += "*." + filters.get(i);
            
            if (i != filters.size()-1) fileInfo += ", ";
        } // end for
        
        fileInfo += ")";
        
        return description + fileInfo; 
    }
    
} // end of class IDEFileFilter
