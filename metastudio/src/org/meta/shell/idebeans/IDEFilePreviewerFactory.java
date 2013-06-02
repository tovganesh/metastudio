/*
 * IDEFilePreviewerFactory.java
 *
 * Created on August 15, 2005, 10:36 AM
 *
 */

package org.meta.shell.idebeans;
  
import java.io.*;
import java.util.*;

import org.meta.common.Utility;
import org.meta.moleculereader.MoleculeFileReaderFactory;

/**
 * A simple file previewing factory in MeTA Studio. 
 * (an independence day gift! :) )
 *
 * Follows a singleton pattern.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class IDEFilePreviewerFactory {
    
    private static IDEFilePreviewerFactory _filePreviewerFactory;
            
    private HashMap<String, IDEFilePreviewer> previewers;
    
    /**
     * Creates a new instance of IDEFilePreviewerFactory 
     */
    private IDEFilePreviewerFactory() {
        // init previewer list
        previewers = new HashMap<String, IDEFilePreviewer>();
          
        previewers.put("default", new DefaultIDEFilePreviewer());
        
        IDEFilePreviewer pr = new MoleculeIDEFilePreviewer();
        
        try {
            MoleculeFileReaderFactory mfrm =
                        (MoleculeFileReaderFactory) Utility.getDefaultImplFor(
                                MoleculeFileReaderFactory.class).newInstance();
            
            Iterator<String> supportedFileFormats 
                                      = mfrm.getAllSupportedTypes();
            
            while(supportedFileFormats.hasNext()) {
                previewers.put(supportedFileFormats.next(), pr);
            } // end while
        } catch (Exception e) {
            // ignored
            e.printStackTrace();
        } // end of try .. catch block
    }
    
    /**
     * Method to obtain a instance of this class.
     *
     * @return instance of IDEFilePreviewerFactory
     */
    public static IDEFilePreviewerFactory getInstance() {
        if (_filePreviewerFactory == null) {
            _filePreviewerFactory = new IDEFilePreviewerFactory();
        } // end if 
        
        return _filePreviewerFactory;
    }
    
    /**
     * get an appropriate "preview" JComponent for this file object.
     *
     * @param file the File object whose preview is needed
     * @return an instance of IDEFilePreviewer
     */
    public IDEFilePreviewer getAccessoryFor(File file) {                
        IDEFilePreviewer previewer = null;
        
        String fileName = file.getAbsolutePath();                        
        
        String type = fileName.substring(fileName.lastIndexOf('.')+1,
                                         fileName.length());                
        
        if (type != null) {
            type = type.toLowerCase();
           
            previewer = previewers.get(type);
        } // end if
        
        if (previewer == null) {
            previewer = previewers.get("default");
        } // end if
        
        previewer.setFileToPreview(file);
        
        return previewer;        
    }
} // end of class IDEFilePreviewerFactory
