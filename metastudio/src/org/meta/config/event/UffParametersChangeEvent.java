package org.meta.config.event;

import java.util.EventObject;

/**
 * The EventObject indicating the UffParameters change event to all the "listeners".
 *
 * @author  J. Milthorpe
 * @version 2.0 (Part of MeTA v2.0)
 */
public class UffParametersChangeEvent extends EventObject {
    private ChangeType changeType;
    private Object newValue;
    private Object oldValue;

    /** the change types */
    public enum ChangeType {
        COLLISION_DISTANCE, WELL_DEPTH, EFFECTIVE_CHARGE, ALL
    };
    /** Holds value of property atomSymbol. */
    private String atomSymbol;

    /** Creates a new instance of UffParametersChangeEvent */
    public UffParametersChangeEvent(Object source) {
        super(source);
    }

    public ChangeType getChangeType() {
        return this.changeType;
    }

    public void setChangeType(ChangeType changeType) {
        this.changeType = changeType;
    }

    public Object getNewValue() {
        return this.newValue;
    }

    public void setNewValue(Object newValue) {
        this.newValue = newValue;
    }

    public Object getOldValue() {
        return this.oldValue;
    }

    public void setOldValue(Object oldValue) {
        this.oldValue = oldValue;
    }

    public String getAtomSymbol() {
        return this.atomSymbol;
    }

    public void setAtomSymbol(String atomSymbol) {
        this.atomSymbol = atomSymbol;
    }
}
