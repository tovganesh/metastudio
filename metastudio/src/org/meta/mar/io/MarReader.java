/**
 * MarReader.java
 *
 * Created on 17/04/2010
 */

package org.meta.mar.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.meta.common.resource.StringResource;
import org.meta.mar.MarItem;
import org.meta.mar.MarManifest;
import org.meta.mar.MarObject;
import org.meta.mar.exception.InvalidMarFileFormat;

/**
 * MeTA Studio Archive Reader
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MarReader {
    /** Creates a new instance of MarReader */
    public MarReader() {
    }

    /**
     * Read a MeTA Studio Archive file and convert it to its run time
     * representation MarObject
     *
     * @param file the file to read
     * @return MarObject of the
     * @throws IOException
     * @throws InvalidMarFileFormat
     */
    public MarObject read(String file) throws IOException,
                                              InvalidMarFileFormat {
        return read(new File(file));
    }

    /**
     * Read a MeTA Studio Archive file and convert it to its run time
     * representation MarObject
     *
     * @param file the file to read
     * @return MarObject of the
     * @throws IOException
     * @throws InvalidMarFileFormat
     */
    public MarObject read(File file) throws IOException, InvalidMarFileFormat {
        return read(new FileInputStream(file));
    }

    /**
     * Read a MeTA Studio Archive file and convert it to its run time
     * representation MarObject
     *
     * @param fis the file input stream
     * @return MarObject of the
     * @throws IOException
     * @throws InvalidMarFileFormat
     */
    public MarObject read(FileInputStream fis) throws IOException,
                                                      InvalidMarFileFormat {       
        MarObject marObject = new MarObject();
        ZipInputStream marFile = new ZipInputStream(fis);
        
        String marManifestName = StringResource.getInstance().getMarManifestFileName();
        
        while(true) {
            ZipEntry marItem = marFile.getNextEntry();

            if (marItem == null) break;

            if (marItem.getName().equals(marManifestName)) {
                // this is a manifest file name
                marObject.setMarManifest(new MarManifest(marFile, marItem));
                continue;
            } // end if
            
            marObject.addMarItem(new MarItem(marFile, marItem));
        } // end while

        marFile.close();
        
        return marObject;
    }
}
