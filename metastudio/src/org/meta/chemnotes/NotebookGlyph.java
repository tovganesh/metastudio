/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.meta.chemnotes;

import java.util.Date;
import java.util.HashMap;
import org.meta.chemnotes.event.NotebookGlyphModifiedEvent;
import org.meta.chemnotes.event.NotebookGlyphModifiedListener;
import org.meta.common.EventListenerList;

/**
 * A single item in ChemNotebook
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0) 
 */
public class NotebookGlyph {
    
    /**
     * How was the source associated with this glyph created?
     */
    public enum GlyphCreationType {

        /**
         * manually created by user
         */
        MANUAL,
        
        /**
         * is an output from a process
         */
        PROCESS,
        
        /**
         * system created glyph
         */
        SYSTEM,
        
        /**
         * is external (like read from a file, over the Internet stream) etc. 
         */
        EXTERNAL
    };
    
    /**
     * what type of object does this glyph encompass
     */
    public enum GlyphType {

        /**
         * a text object
         */
        cnb_text,
        
        /**
         * an audio object 
         */
        cnb_audio,
        
        /**
         * a molecule object
         */
        cnb_molecule,
        
        /**
         * a process object
         */
        cnb_process,
        
        /**
         * a process connection object
         */
        cnb_processconnection
    };
    
    /**
     * creation date stamp of this glyph
     */
    protected Date creationTimeStamp;
    
    /**
     * last modified date stamp of this glyph
     */
    protected Date modificationTimeStamp;
    
    /**
     * The relative x position in ChemBook
     */
    protected int x;
    
    /**
     * The relative y position in ChemBook
     */
    protected int y;
    
    /**
     * The current height of this glyph
     */
    protected int height;
    
    /**
     * The current width of this glyph
     */
    protected int width;
    
    /**
     * An identifier for the glyph
     */
    protected String id;
    
    /**
     * The UI (fully qualified) class name implementing this glyph
     */
    protected String uiClassName;
    
    /**
     * How was this glyph created (manual, process, etc. )
     */
    protected GlyphCreationType cretionType;    
    
    /**
     * The type of the glyph
     */
    protected GlyphType type;
    
    /**
     * The content of this glyph, if any
     */
    protected NotebookGlyphContent content;
    
    /**
     * Listener list
     */
    protected EventListenerList<NotebookGlyphModifiedListener> listeners;
    
    /**
     * Owner ChemNotebook
     */
    protected ChemNotebook ownerBook;

    /**
     * Is this glyph a process?
     */
    protected boolean executable;
    
    /**
     * If glyph is a process it can accept a set of inputs
     * of the form <"input name", glyph>
     */
    protected HashMap<String, NotebookGlyph> inputs;
    
    /**
     * the supported input types
     */
    protected HashMap<String, GlyphType> inputTypes;
            
    /**
     * If glyph is a process it can produce a set of outputs
     * of the form <"input name", glyph>
     */
    protected HashMap<String, NotebookGlyph> outputs;
    
    /**
     * the supported output types
     */
    protected HashMap<String, GlyphType> outputTypes;

    /**
     * Creates a new instance of NotebookGlyph
     */
    public NotebookGlyph() {        
    }
    
    /**
     * Create a new instance of glyph
     * 
     * @param id and identifier for the glyph
     * @param uiClassName the fully qualified class handling UI
     * @param type type of this glyph
     * @param ownerBook the owner ChemBook
     */
    public NotebookGlyph(String id, String uiClassName, GlyphType type, ChemNotebook ownerBook) {
        this.id = id;
        this.uiClassName = uiClassName;
        this.type = type;
        this.ownerBook = ownerBook;
    }        
    
    /**
     * Get creation time stamp 
     * @return date when created
     */
    public Date getCreationTimeStamp() {
        return creationTimeStamp;
    }

    /**
     * Set the creation time stamp
     * @param creationTimeStamp the new creation time stamp
     */
    public void setCreationTimeStamp(Date creationTimeStamp) {
        this.creationTimeStamp = creationTimeStamp;
    }

    /**
     * Get the modification time stamp
     * 
     * @return date when modified
     */
    public Date getModificationTimeStamp() {
        return modificationTimeStamp;
    }

    /**
     * Set the modification time stamp
     * 
     * @param modificationTimeStamp 
     */
    public void setModificationTimeStamp(Date modificationTimeStamp) {
        this.modificationTimeStamp = modificationTimeStamp;
    }

    /**
     * Get value of creationType
     * 
     * @return the creation type
     */
    public GlyphCreationType getCretionType() {
        return cretionType;
    }

    /**
     * Set the creationType
     * 
     * @param cretionType set the current creationType
     */
    public void setCretionType(GlyphCreationType cretionType) {
        this.cretionType = cretionType;
    }

    /**
     * Get the height
     * 
     * @return return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Set the height
     * 
     * @param height the height 
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Get the Id, identifier for this item in the ChemNotebook
     * 
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Set the id
     * 
     * @param id the new identifier
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get the glyph type
     * 
     * @return glyph type
     */
    public GlyphType getType() {
        return type;
    }

    /**
     * Set the glyph type
     * 
     * @param type the new glyph type
     */
    public void setType(GlyphType type) {
        this.type = type;
    }

    /**
     * Get width of glyph
     * 
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Set new width of glyph
     * 
     * @param width the new width
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Get X position 
     * 
     * @return the x position 
     */
    public int getX() {
        return x;
    }

    /**
     * Set new X position 
     * 
     * @param x the new x position
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Get new Y position 
     * 
     * @return the new y position
     */
    public int getY() {
        return y;
    }

    /**
     * Set new Y position 
     * 
     * @param y the new y position 
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Get content
     * 
     * @return the content 
     */
    public NotebookGlyphContent getContent() {
        return content;
    }

    /**
     * Set content 
     * 
     * @param content the new content
     */
    public void setNotebookGlyphContent(NotebookGlyphContent content) {
        this.content = content;
    }

    /**
     * Get UI class name 
     * 
     * @return the UI class name
     */
    public String getUiClassName() {
        return uiClassName;
    }

    /**
     * Set UI class name 
     * 
     * @param uiClassName the new UI class name
     */
    public void setUiClassName(String uiClassName) {
        this.uiClassName = uiClassName;
    }

    /**
     * Get the owner ChemNotebook
     * 
     * @return the instance of ChemNotebook
     */
    public ChemNotebook getOwnerBook() {
        return ownerBook;
    }

    /**
     * Set the owner ChemNotebook
     * @param ownerBook the instance of ChemNotebook 
     */
    public void setOwnerBook(ChemNotebook ownerBook) {
        this.ownerBook = ownerBook;
    }

    /**
     * If this glyph is of type process, it is executable
     * 
     * @return
     */
    public boolean isExecutable() {
        return executable;
    }

    /**
     * Set the executable type of this glyph
     * 
     * @param executable
     */
    public void setExecutable(boolean executable) {
        this.executable = executable;
    }

    /**
     * Get the list of inputs, in case this is an executable process
     * 
     * @return a hash map of the form <"input name", glyph>
     */
    public HashMap<String, NotebookGlyph> getInputs() {
        return inputs;
    }

    /**
     * Set the list of inputs, in case this is an executable process
     * 
     * @param inputs a hash map of the form <"input name", glyph>
     */
    public void setInputs(HashMap<String, NotebookGlyph> inputs) {
        this.inputs = inputs;
    }

    /**
     * Get the list of outputs (after execution), in case this is an executable process 
     * @return a hash map of the form <"output name", glyph>
     */
    public HashMap<String, NotebookGlyph> getOutputs() {
        return outputs;
    }

    /**
     * Set the list of outputs, in case this is an executable process
     * @param outputs a hash map of the form <"output name", glyph>
     */
    public void setOutputs(HashMap<String, NotebookGlyph> outputs) {
        this.outputs = outputs;
    }

    /**
     * Get a list of input types
     * 
     * @return a map of the form <"input name", glyphtype>
     */
    public HashMap<String, GlyphType> getInputTypes() {
        return inputTypes;
    }

    /**
     * Set the list of input types
     * 
     * @param inputTypes a map of the form <"input name", glyphtype>
     */
    public void setInputTypes(HashMap<String, GlyphType> inputTypes) {
        this.inputTypes = inputTypes;
    }

    /**
     * Get the list of output types
     * 
     * @return a map of the form <"output name", glyphtype>
     */
    public HashMap<String, GlyphType> getOutputTypes() {
        return outputTypes;
    }

    /**
     * Set the list of output types
     * 
     * @param outputTypes a map of the form <"output name", glyphtype>
     */
    public void setOutputTypes(HashMap<String, GlyphType> outputTypes) {
        this.outputTypes = outputTypes;
    }        
        
    /**
     * Execute this glyph in case this is a of process type
     */
    public void execute() {
        // default implimentation does nothing!
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Add a glyph modification listener 
     * 
     * @param nml the glyph modification listener
     */
    public void addNotebookModificationListener(NotebookGlyphModifiedListener nml) {
        if (listeners == null)
            listeners = new EventListenerList();
        
        listeners.add(NotebookGlyphModifiedListener.class, nml);
    }
    
    /**
     * Remove a glyph modification listener
     * 
     * @param nml the glyph modification listener
     */
    public void removeNotebookModificationListener(NotebookGlyphModifiedListener nml) {
        if (listeners == null) return;
        
        listeners.remove(NotebookGlyphModifiedListener.class, nml);
    }
    
    /**
     * Fire the modification event to the registered listeners
     * 
     * @param nme the event to be fired
     */
    protected void fireNotebookModificationEvent(NotebookGlyphModifiedEvent nme) {
        if (listeners == null) return;
        
        for(Object ngml : listeners.getListenerList()) {
            ((NotebookGlyphModifiedListener) ngml).notebookGlyphModified(nme);
        }
    }
}
