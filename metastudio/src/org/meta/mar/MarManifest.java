/**
 * MarManifest.java
 *
 * Created on 17/04/2010
 */

package org.meta.mar;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.meta.common.Utility;
import org.meta.scripting.ScriptLanguage;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * The manifest file of MeTA Studio Archive format.
 * The following is the format of the manifest.xml file:
 * <code>
 *      <mar>
 *         <mainscript name="main.bsh" language="BEANSHELL" />
 *      </mar>
 * </code>
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MarManifest {
    /** Creates a new instance of MarManifest */
    public MarManifest() {
    }

    /** Creates a new instance of MarManifest */
    public MarManifest(ZipInputStream zip, ZipEntry entry) throws IOException {
        // read the zip entry and create this object
        byte data[] = new byte[(int) entry.getSize()];
        zip.read(data);

        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        try {
            Document manifestFile = Utility.parseXML(bis);

            saveManifest(manifestFile);
        } catch(Exception e) {
            bis.close();
            throw new IOException("Error parsing manifest file: " + e);
        } // end try .. catch

        bis.close();
    }

    /** Creates a new instance of MarManifest */
    public MarManifest(String mainScript, ScriptLanguage scriptLang) {
        this.mainScript = mainScript;
        this.scriptLanguage = scriptLang;
    }

    private String element = "element";

    private void saveManifest(Node n) {
        int type = n.getNodeType();   // get node type

        switch (type) {
        case Node.ELEMENT_NODE:
            element = n.getNodeName();

            NamedNodeMap atts = n.getAttributes();
            if (element.equals("mainscript")) {
                mainScript = atts.getNamedItem("name").getNodeValue();
                scriptLanguage = ScriptLanguage.valueOf(atts.getNamedItem("language").getNodeValue());

                // save the others
                for (int i = 0; i < atts.getLength(); i++) {
                    Node att = atts.item(i);
                    saveManifest(att);
                } // end for
            } else {
                if (atts == null) return;

                for (int i = 0; i < atts.getLength(); i++) {
                    Node att = atts.item(i);
                    saveManifest(att);
                } // end for
            } // end if
            break;
        default:
            break;
        } // end switch..case

        // save children if any
        for (Node child = n.getFirstChild(); child != null;
             child = child.getNextSibling()) {
             saveManifest(child);
        } // end for
    }

    protected String mainScript;

    /**
     * Get the value of mainScript
     *
     * @return the value of mainScript
     */
    public String getMainScript() {
        return mainScript;
    }

    /**
     * Set the value of mainScript
     *
     * @param mainScript new value of mainScript
     */
    public void setMainScript(String mainScript) {
        this.mainScript = mainScript;
    }

    protected ScriptLanguage scriptLanguage;

    /**
     * Get the value of scriptLanguage
     *
     * @return the value of scriptLanguage
     */
    public ScriptLanguage getScriptLanguage() {
        return scriptLanguage;
    }

    /**
     * Set the value of scriptLanguage
     *
     * @param scriptLanguage new value of scriptLanguage
     */
    public void setScriptLanguage(ScriptLanguage scriptLanguage) {
        this.scriptLanguage = scriptLanguage;
    }
}
