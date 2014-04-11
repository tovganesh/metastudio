/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.meta.chemnotes;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import org.meta.chemnotes.event.NotebookGlyphModifiedEvent;
import org.meta.chemnotes.event.NotebookGlyphModifiedListener;
import org.meta.chemnotes.event.NotebookModificationEvent;
import org.meta.chemnotes.event.NotebookModificationListener;
import org.meta.common.EventListenerList;

/**
 * The placeholder of a ChemNotebook
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class ChemNotebook implements NotebookGlyphModifiedListener {
   
    private String name;
    private Date creationTimeStamp;
    private Date modificationTimeStamp;
    
    private ArrayList<NotebookGlyph> noteBookItems;
    
    private EventListenerList<NotebookModificationListener> listeners;
    
    /** Creates a new instance of ChemNotebook
     * @param name 
     */
    public ChemNotebook(String name) {
        this(name, new Date());
    }
    
    /** Creates a new instance of ChemNotebook
     * @param name
     * @param creationTimeStamp  
     */
    public ChemNotebook(String name, Date creationTimeStamp) {
        this(name, creationTimeStamp, creationTimeStamp);
    }

    /** Creates a new instance of ChemNotebook
     * @param name 
     * @param modificationTimeStamp
     * @param creationTimeStamp  
     */
    public ChemNotebook(String name, Date creationTimeStamp,
                        Date modificationTimeStamp) {
        this.name = name;
        this.creationTimeStamp = creationTimeStamp;
        this.modificationTimeStamp = modificationTimeStamp;
    }
    
    /**
     * Get the name property 
     * @return return name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name property
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
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
     * Add a notebook glyph
     * 
     * @param ng the NotebookGlyph to add
     */
    public void addNotebookGlyph(NotebookGlyph ng) {
        if (noteBookItems == null) 
            noteBookItems = new ArrayList<NotebookGlyph>();
        
        noteBookItems.add(ng);
        
        NotebookModificationEvent nme = new NotebookModificationEvent(this);        
        nme.setGlyphModifedEvent(true);
        
        NotebookGlyphModifiedEvent ngme = new NotebookGlyphModifiedEvent(ng);
        ngme.setModificationType(NotebookGlyphModifiedEvent.ModificationType.ADDED);
        nme.setGlyphModificationEvent(ngme);
        
        fireNotebookModificationEvent(nme);
    }
    
    /**
     * Remove a notebook glyph
     * 
     * @param ng the NotebookGlyph to remove
     */
    public void removeNotebookGlyph(NotebookGlyph ng) {
        if (noteBookItems == null) return;
        
        noteBookItems.remove(ng);
    }
    
    /**
     * Get all list of all notebook glyph
     * @return the iterator of glyph
     */
    public Iterator<NotebookGlyph> getNotebookGlyphs() {
        return noteBookItems.iterator();
    }
    
    /**
     * Add a notebook modification listener 
     * 
     * @param nml the notebook modification listener
     */
    public void addNotebookModificationListener(NotebookModificationListener nml) {
        if (listeners == null)
            listeners = new EventListenerList();
        
        listeners.add(NotebookModificationListener.class, nml);
    }
    
    /**
     * Remove a notebook modification listener
     * 
     * @param nml the notebook modification listener
     */
    public void removeNotebookModificationListener(NotebookModificationListener nml) {
        if (listeners == null) return;
        
        listeners.remove(NotebookModificationListener.class, nml);
    }
    
    /**
     * Fire the modification event to the registered listeners
     * 
     * @param nme the event to be fired
     */
    protected void fireNotebookModificationEvent(NotebookModificationEvent nme) {
        if (listeners == null) return;
        
        for(Object ngml : listeners.getListenerList()) {
            ((NotebookModificationListener) ngml).notebookModified(nme);
        }
    }

    /**
     * Callback on a notebook glyph being added
     * 
     * @param ngme the associated event object
     */
    @Override
    public void notebookGlyphModified(NotebookGlyphModifiedEvent ngme) {
        NotebookModificationEvent nme = new NotebookModificationEvent(this);
        
        nme.setGlyphModifedEvent(true);
        nme.setGlyphModificationEvent(ngme);
        fireNotebookModificationEvent(nme);
    }
}
