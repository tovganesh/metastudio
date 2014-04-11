/*
 * TalkCommand.java
 *
 * Created on July 23, 2006, 2:24 PM
 */

package org.meta.net.impl.service.talk;

/**
 * Talk commands passed between consumer and service provider.
 *
 * @author V.Ganesh
 */
public class TalkCommand implements java.io.Serializable {    
    
    private String description;

    /**
     * get value of description
     * 
     * @return description for command, if available
     */
    public String getDescription() {
        return description;
    }

    
    /**
     * set value of description
     * 
     * @param description for command, if available
     */
    public void setDescription(String description) {
        this.description = description;
    }   

    /** Create instance */
    public TalkCommand(String desc, CommandType typ) {
        description = desc;
        type = typ;
    }  

    public enum CommandType {
        CLOSE_SESSION, 
        DISPLAY_NAME_CHANGED;
    }

    /**
     * Holds value of property type.
     */
    private CommandType type;

    /**
     * Getter for property type.
     * @return Value of property type.
     */
    public CommandType getType() {
        return this.type;
    }

    /**
     * Setter for property type.
     * @param type New value of property type.
     */
    public void setType(CommandType type) {
        this.type = type;
    }   
} // end of enum TalkCommand
