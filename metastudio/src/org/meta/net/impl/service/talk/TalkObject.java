/*
 * TalkObject.java
 *
 * Created on July 21, 2006, 6:37 AM
 */

package org.meta.net.impl.service.talk;

/**
 * Represents the objects transferred between Talk sessions.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class TalkObject implements java.io.Serializable {
    
    /** Creates a new instance of TalkObject */
    public TalkObject() {
    }

    /** enumerates the type of this object */
    public enum TalkObjectType implements java.io.Serializable {
        STRING, MOLECULE, BEANSHELL_SCRIPT, IMAGE, TALK_COMMAND
    }

    /**
     * Holds value of property type.
     */
    protected TalkObjectType type;

    /**
     * Getter for property type.
     * @return Value of property type.
     */
    public TalkObjectType getType() {
        return this.type;
    }

    /**
     * Setter for property type.
     * @param type New value of property type.
     */
    public void setType(TalkObjectType type) {
        this.type = type;
    }

    /**
     * Holds value of property talkObjectContent.
     */
    protected Object talkObjectContent;

    /**
     * Getter for property talkObjectContent.
     * @return Value of property talkObjectContent.
     */
    public Object getTalkObjectContent() {
        return this.talkObjectContent;
    }

    /**
     * Setter for property talkObjectContent.
     * @param talkObjectContent New value of property talkObjectContent.
     */
    public void setTalkObjectContent(Object talkObjectContent) {
        this.talkObjectContent = talkObjectContent;
    }
    
} // end of class TalkObject
