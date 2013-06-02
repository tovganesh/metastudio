/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.meta.chemnotes.store;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.meta.chemnotes.ChemNotebook;
import org.meta.chemnotes.NotebookGlyph;
import org.meta.chemnotes.NotebookGlyphContent;
import org.meta.chemnotes.event.NotebookModificationEvent;
import org.meta.common.Utility;
import org.meta.common.resource.StringResource;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * ChemNotebook file storage implementation.
 * The file is stored in the form of compressed XML storage, with each 
 * notebook glyph stored in a separate XML file. Contents of compressed store:
 *  GlyphName_ID.xml  => ID is current_time_millies.
 *  (...)
 * 
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class ChemNotebookFileStore extends ChemNotebookStore {

    private File chemNotebookStore;

    /**
     * Create new instance of ChemNotebookFileStore
     * 
     * @param chemNotebookStore the notebook storage 
     * @param chemNotebook the chembook to be associated with file store
     * @throws IOException in case there is a problem with association 
     */
    public ChemNotebookFileStore(File chemNotebookStore,
                                 ChemNotebook chemNotebook) throws IOException {
        super(chemNotebook);
        
        this.chemNotebookStore = chemNotebookStore;
    }

    private void save() throws IOException {
        // TODO: 
    }
    
    @Override
    public void notebookModified(NotebookModificationEvent nme) {
        try {
            save();
        } catch (IOException ex) {
            Logger.getLogger(ChemNotebookFileStore.class.getName()).log(Level.SEVERE,  null, ex);
        } // end try .. catch 
    }

    @Override
    public void load() throws IOException {
        if (chemNotebookStore == null) return;
        
        if (!chemNotebookStore.exists()) return;
        
        // the file store is a ZIP archive, first open it
        ZipInputStream chemNoteStore = new ZipInputStream(
                new FileInputStream(chemNotebookStore));

        // the default manifest file
        String manifestName = StringResource.getInstance().getChemStoreManifestFileName();

        while (true) {
            // TODO :
            ZipEntry zi = chemNoteStore.getNextEntry();

            if (zi == null) {
                break;
            } // end if

            if (zi.getName().equals(manifestName)) {
                // TODO : handle manifest file
                continue;
            } // end if

            // TODO : handle other other objects (notebook glyph objects)
            NotebookGlyph ng = readGlyphEntry(chemNoteStore, zi);
            chemNotebook.addNotebookGlyph(ng);
        } // end while

        chemNoteStore.close();
    }

    /**
     * Read a glyph entry
     * 
     * @param zip the zip stream to read from
     * @param zi the zip entry to read
     * @return the stored instance of NotebookGlyph
     * @throws IOException in case an exception occurs
     */
    protected NotebookGlyph readGlyphEntry(ZipInputStream zip, ZipEntry zi) throws IOException {
        // TODO: byte by byte reading is not a good idea!
        byte data[] = new byte[(int) zi.getSize()];

        int readByte, i = 0;
        while (true) {
            readByte = zip.read();
            if (readByte == -1) {
                break;
            }
            data[i++] = (byte) readByte;
        } // end while

        // TODO: 
        String contentToParse = new String(data);

        NotebookGlyph ng = parseGlyph(contentToParse);

        return ng;
    }

    /**
     * Parse the XML data of glyph.
     * The following format is used:
     * <notebookglyph id="" 
     *                creationDate="in ms" 
     *                modificationDate="in ms"
     *                uiClass="fully qualified ui class name"
     *                creationType=""
     *                type="cnb_text .. "
     *                x="" y="" width="" hight=""
     *                executable="true/false" 
     *                >
     *    <content object="binary data in Java serializable form" originalObject="original object source" />
     * </notebookglyph>
     * 
     * @param contentToParse the XML data 
     * @return the stored instance of NotebookGlyph
     */
    protected NotebookGlyph parseGlyph(String contentToParse) throws IOException {
        try {
            // read the internal XML config file
            Document glyphDoc = Utility.parseXML(
                    new ByteArrayInputStream(contentToParse.getBytes()));

            NotebookGlyph ng = new NotebookGlyph();

            createGlyphObject(ng, glyphDoc);
            return ng;
        } catch (Exception e) {
            throw new IOException("Exception in ChemNotebookFileStore.parseGlyph()"
                    + e.toString(), null);
        } // end of try .. catch block       
    }

    /**
     * recursive routine to save dom tree nodes
     * 
     * @param ng the result of the dom tree is stored here
     * @param n the dom node to be parsed recursively 
     */
    private void createGlyphObject(NotebookGlyph ng, Node n) {
        // TODO : 
        int type = n.getNodeType();   // get node type

        switch (type) {
            case Node.ATTRIBUTE_NODE:
                String nodeName = n.getNodeName();

                if (nodeName.equals("id")) {
                    ng.setId(n.getNodeValue());
                } else if (nodeName.equals("creationDate")) {
                    ng.setCreationTimeStamp(new Date(Long.parseLong(n.getNodeValue())));
                } else if (nodeName.equals("modificationDate")) {
                    ng.setModificationTimeStamp(new Date(Long.parseLong(n.getNodeValue())));
                } else if (nodeName.equals("uiClass")) {
                    ng.setUiClassName(n.getNodeValue());
                } else if (nodeName.equals("type")) {
                    ng.setType(NotebookGlyph.GlyphType.valueOf(n.getNodeValue()));
                } else if (nodeName.equals("creationType")) {
                    ng.setCretionType(NotebookGlyph.GlyphCreationType.valueOf(n.getNodeValue()));
                } else if (nodeName.equals("x")) {
                    ng.setX(Integer.parseInt(n.getNodeValue()));
                } else if (nodeName.equals("y")) {
                    ng.setY(Integer.parseInt(n.getNodeValue()));
                } else if (nodeName.equals("width")) {
                    ng.setWidth(Integer.parseInt(n.getNodeValue()));
                } else if (nodeName.equals("height")) {
                    ng.setHeight(Integer.parseInt(n.getNodeValue()));
                } else if (nodeName.equals("executable")) {
                    ng.setExecutable(Boolean.parseBoolean(n.getNodeValue()));
                } // end if

                break;
            case Node.ELEMENT_NODE:
                String element = n.getNodeName();

                NamedNodeMap atts = n.getAttributes();

                if (element.equals("content")) {
                    String objectSource = atts.getNamedItem("object").getNodeValue();
                    String originalObjectSource = atts.getNamedItem("originalObject").getNodeValue();
                    
                    NotebookGlyphContent ngc = new NotebookGlyphContent();
                    
                    ngc.setContent(objectSource);
                    ngc.setOriginalSource(originalObjectSource);
                    ng.setNotebookGlyphContent(ngc);
                } else {
                    if (atts == null) {
                        return;
                    } // end if

                    for (int i = 0; i < atts.getLength(); i++) {
                        Node att = atts.item(i);

                        createGlyphObject(ng, att);
                    } // end for
                } // end if
                break;
            default:
                break;
        } // end switch .. case

        // save children if any
        for (Node child = n.getFirstChild(); 
                child != null;
                child = child.getNextSibling()) {
            createGlyphObject(ng, child);
        } // end for
    }

    /**
     * The associated File for this chem store
     * 
     * @return the associated file object with this store
     */
    public File getChemNotebookStore() {
        return chemNotebookStore;
    }
}
