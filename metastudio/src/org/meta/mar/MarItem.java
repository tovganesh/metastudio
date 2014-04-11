/**
 * MarItem.java
 *
 * Created on 17/04/2010
 */

package org.meta.mar;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.meta.scripting.ScriptLanguage;

/**
 * Represents a MeTA Studio Archive format item.
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MarItem {

    private String content;
    
    /** Creates a new instance of MarItem */
    public MarItem() {
    }

    /** Creates a new instance of MarItem */
    public MarItem(ZipInputStream zip, ZipEntry entry) throws IOException {
        byte data[] = new byte[(int) entry.getSize()];
        
        int readByte, i = 0;
        while(true) {
           readByte = zip.read();           
           if (readByte == -1) break;                      
           data[i++] = (byte) readByte;
        }
        
        content = new String(data);
        
        name = entry.getName();
        type = (name.endsWith("bsh") ? ScriptLanguage.BEANSHELL.toString() :
                name.endsWith("py")  ? ScriptLanguage.JYTHON.toString() :
                name.endsWith("scheme") ? ScriptLanguage.JSCHEME.toString() :
                ScriptLanguage.BEANSHELL.toString());
    }

    protected String name;

    /**
     * Get the value of name
     *
     * @return the value of name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the value of name
     *
     * @param name new value of name
     */
    public void setName(String name) {
        this.name = name;
    }

    protected String type;

    /**
     * Get the value of type
     *
     * @return the value of type
     */
    public String getType() {
        return type;
    }

    /**
     * Set the value of type
     *
     * @param type new value of type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Return the content of this item from the actual archive
     * as a string
     *
     * @return the content in the archive as a string 
     */
    public String getContent() {
        return content;
    }
}
