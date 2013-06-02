/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.meta.chemnotes;

/**
 * Context information attached to a NotebookGlyph
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class ContextTag {
    
    /**
     * How much confidence does the name and description for this context tag have?
     */
    public enum TagConfidence {

        /**
         * high confidence, mostly sure that this is correct
         */
        HIGH,
        
        /**
         * low confidence, partly sure, but can't be verified
         */
        LOW,
        
        /**
         * not sure at all!
         */
        NOT_SURE
    };
    
    private String tagName;
    private String description;
    private TagConfidence confidence;

    /**
     * 
     * @param tagName
     * @param description
     * @param confidence
     */
    public ContextTag(String tagName, String description, TagConfidence confidence) {
        this.tagName = tagName;
        this.description = description;
        this.confidence = confidence;
    }

    /**
     * 
     * @return
     */
    public TagConfidence getConfidence() {
        return confidence;
    }

    /**
     * 
     * @param confidence
     */
    public void setConfidence(TagConfidence confidence) {
        this.confidence = confidence;
    }

    /**
     * 
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * 
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 
     * @return
     */
    public String getTagName() {
        return tagName;
    }

    /**
     * 
     * @param tagName
     */
    public void setTagName(String tagName) {
        this.tagName = tagName;
    }    
}
