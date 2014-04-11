package org.meta.config.impl;

import java.util.*;

import java.beans.PropertyVetoException;
import org.meta.common.EventListenerList;
import org.meta.common.Utility;
import org.meta.common.resource.StringResource;
import org.meta.config.Configuration;
import org.meta.config.Parameter;
import org.meta.config.event.UffParametersChangeEvent;
import org.meta.config.event.UffParametersChangeListener;
import org.meta.math.mm.LennardJonesParameters;
import org.w3c.dom.*;

/**
 * The default UffParameters configuration.
 *
 * .. follows a singleton pattern.
 * .. and an observer pattern for notifying the registered classes of the
 *    changes at runtime.
 *
 * @author  Josh Milthorpe
 * @version 2.0 (Part of MeTA v2.0)
 */
public class UffParametersConfiguration implements Configuration {
    
    private static final String DEFAULT_KEY = "X";
    private static UffParametersConfiguration _uffParametersConfiguration;
    private static final int DEFAULT_TABLE_SIZE = 90;
    
    /** Holds value of property vdwBondRadiussTable. */
    private Hashtable<String, UffParameters> uffParametersTable,  originalUffParametersTable;
    
    /** Utility field used by event firing mechanism. */
    private EventListenerList<UffParametersChangeListener> listenerList = null;

    /** Creates a new instance of UffParametersConfiguration */
    public UffParametersConfiguration() {
        uffParametersTable = new Hashtable<String, UffParameters>(
                DEFAULT_TABLE_SIZE);
        originalUffParametersTable = new Hashtable<String, UffParameters>(
                DEFAULT_TABLE_SIZE);

        // the initial parameters
        try {
            setDefaultParams();
        } catch (PropertyVetoException ignored) {
            // because it never should happen in this context
            System.err.println(ignored.toString());
        }
    }

    /**
     * Obtain an instance of this ...
     */
    public static UffParametersConfiguration getInstance() {
        if (_uffParametersConfiguration == null) {
            _uffParametersConfiguration = new UffParametersConfiguration();
        } // end if

        return _uffParametersConfiguration;
    }

    /**
     * method to reset the instance of UffParametersConfiguration
     * .. so that default values are loaded.
     * Note that the the values are not saved to a persistent store.
     */
    public static void reset() {
        if (_uffParametersConfiguration == null) {
            return;
        }

        // re-initilize the parameters
        try {
            _uffParametersConfiguration.setDefaultParams();

            UffParametersChangeEvent changeEvent = new UffParametersChangeEvent(
                    _uffParametersConfiguration);

            changeEvent.setChangeType(UffParametersChangeEvent.ChangeType.ALL);
            // fire the event!
            _uffParametersConfiguration.fireUffParametersChangeEvent(changeEvent);
        } catch (PropertyVetoException ignored) {
            // because it never should happen in this context
            System.err.println(ignored.toString());
        } // end of try catch
    }

    /**
     * method to reset the instance of UffParameters .. so that default values
     * are loaded for a particular species.
     * Note that the values are not saved to a persistent store.
     */
    public void resetValues(String symbol) {
        uffParametersTable.put(symbol, originalUffParametersTable.get(symbol));

        UffParametersChangeEvent changeEvent =
                new UffParametersChangeEvent(this);

        changeEvent.setChangeType(UffParametersChangeEvent.ChangeType.ALL);

        fireUffParametersChangeEvent(changeEvent);
    }

    /**
     * private method to set the default parameters
     */
    private void setDefaultParams() throws PropertyVetoException {
        StringResource strings = StringResource.getInstance();
        try {
            // read the internal XML config file
            Document configDoc = Utility.parseXML(
                    getClass().getResourceAsStream(getConfigFile()));

            // and save the info. properly
            saveOriginal(configDoc);

            copyOriginals();

        } catch (Exception e) {
            e.printStackTrace();
            throw new PropertyVetoException("Exception in " +
                    "UffParametersConfiguration.setParameter()" + e.toString(),
                    null);
        } // end of try .. catch block
    }

    private void saveOriginal(Node n) {
        saveOriginal(n, null);
    }

    /**
     * Recursive routine save DOM tree nodes
     */
    private void saveOriginal(Node n, UffParameters currentSpecies) {
        int type = n.getNodeType();   // get node type

        switch (type) {
            case Node.ELEMENT_NODE:
                NamedNodeMap atts = n.getAttributes();
                if (n.getNodeName().equals("species")) {
                    String symbol = atts.getNamedItem("symbol").getNodeValue();
                    double vdwBondRadius = Double.parseDouble(atts.getNamedItem(
                            "vdwBondRadius").getNodeValue());
                    double vdwWellDepth = Double.parseDouble(atts.getNamedItem(
                            "vdwWellDepth").getNodeValue());
                    double effectiveCharge = Double.parseDouble(atts.
                            getNamedItem("effectiveCharge").getNodeValue());
                    double electronegativity = Double.parseDouble(atts.
                            getNamedItem("electronegativity").getNodeValue());
                    currentSpecies = new UffParameters(symbol,
                            new LennardJonesParameters(vdwBondRadius,
                            vdwWellDepth),
                            effectiveCharge, electronegativity);
                    currentSpecies.setCoordinationParams(
                            new ArrayList<UffParameters.UffCoordinationParameters>());
                    originalUffParametersTable.put(symbol, currentSpecies);

                } else if (n.getNodeName().equals("coordination")) {
                    String id = atts.getNamedItem("id").getNodeValue();
                    double bondRadius = Double.parseDouble(atts.getNamedItem(
                            "bondRadius").getNodeValue());
                    double bondAngle = Double.parseDouble(atts.getNamedItem(
                            "bondAngle").getNodeValue());
                    double sp2TorsionalBarrier = Double.parseDouble(atts.
                            getNamedItem("sp2TorsionalBarrier").getNodeValue());
                    double sp3TorsionalBarrier = Double.parseDouble(atts.
                            getNamedItem("sp3TorsionalBarrier").getNodeValue());

                    currentSpecies.getCoordinationParams().add(currentSpecies.new UffCoordinationParameters(
                            id, bondRadius, bondAngle, sp2TorsionalBarrier,
                            sp3TorsionalBarrier));
                }

                break;
            default:
                break;
        }

        // save children if any
        for (Node child = n.getFirstChild(); child != null;
                child = child.getNextSibling()) {
            saveOriginal(child, currentSpecies);
        }
    } // end of method saveOriginal()

    /** make a copy of the original */
    private void copyOriginals() {
        // just make a copy of original
        uffParametersTable =
                (Hashtable<String, UffParameters>) (originalUffParametersTable.
                clone());
    }

    public UffParameters getUffParameters(String symbol) {
        return (UffParameters) getParameter(symbol);
    }

    /**
     * Method returns the value of parameter pertaining to the key.
     *
     * @return Parameter - the parameter value.
     * @throws NullPointerException if the key is not found
     */
    @Override
    public Parameter getParameter(String key) {
        UffParameters parameters = null;
        parameters = uffParametersTable.get(key);
        if (parameters == null) {
            System.err.println("Couldn't find parameters for " + key);
            parameters = uffParametersTable.get(DEFAULT_KEY);
        }
        return parameters;
    }

    /**
     * method to set the new parameter value.
     *
     * @param key - the key whose value needs to be changed or added
     * @param parameter - the new parameter value
     * @throws PropertyVetoException - incase changing property value is
     *         not supported
     */
    @Override
    public void setParameter(String key, Parameter parameter)
            throws PropertyVetoException {
        UffParameters uffParam = (UffParameters) parameter;

        UffParametersChangeEvent changeEvent =
                new UffParametersChangeEvent(this);
        changeEvent.setChangeType(UffParametersChangeEvent.ChangeType.ALL);

        changeEvent.setOldValue(uffParametersTable.get(key));
        changeEvent.setNewValue(uffParam);
        changeEvent.setAtomSymbol(key);

        uffParametersTable.put(key, uffParam);
        fireUffParametersChangeEvent(changeEvent);
    }

    /**
     * is the configuration stored as file?
     *
     * @return boolean - true / false
     */
    @Override
    public boolean isStoredAsFile() {
        return true;
    }

    /**
     * Method returns the absolute path of the configuration file if
     * isStoredAsFile() call results in a true value, else a null is returned.
     *
     * @return String - the absolute path of the configuration file
     */
    @Override
    public String getConfigFile() {
        return "/org/meta/config/impl/UffParameters.xml";
    }

    /**
     * changes current value of file storage
     *
     * @param storedAsFile new value of this storage
     */
    @Override
    public void setStoredAsFile(boolean storedAsFile) {
        // ignored! ... we don't want to listen here
    }

    /**
     * sets the value of a parameter
     *
     * @param configFile - the new name of the config file
     * @throws PropertyVetoException some problem can't change the stuff!
     */
    @Override
    public void setConfigFile(String configFile) throws PropertyVetoException {
        throw new PropertyVetoException("Cannot change this property!", null);
    }

    public Hashtable<String, UffParameters> getUffParametersTable() {
        return uffParametersTable;
    }

    /** Registers UffParametersChangeListener to receive events.
     * @param listener The listener to register.
     *
     */
    public synchronized void addUffParametersChangeListener(
            UffParametersChangeListener listener) {
        if (listenerList == null) {
            listenerList = new EventListenerList<UffParametersChangeListener>();
        }
        listenerList.add(UffParametersChangeListener.class, listener);
    }

    /** Removes UffParametersChangeListener from the list of listeners.
     * @param listener The listener to remove.
     *
     */
    public synchronized void removeUffParametersChangeListener(
            UffParametersChangeListener listener) {
        listenerList.remove(UffParametersChangeListener.class, listener);
    }

    /** Notifies all registered listeners about the event.
     *
     * @param event The event to be fired
     */
    private void fireUffParametersChangeEvent(UffParametersChangeEvent event) {
        if (listenerList == null) {
            return;
        }

        // the great hack ... bypass notification if there was no real
        // change!
        if (event.getOldValue() != null) {
            if (event.getOldValue().equals(event.getNewValue())) {
                return;
            }
        } // end if

        // eventually let every one know
        for(Object listener : listenerList.getListenerList()) {
            ((UffParametersChangeListener) listener).uffParametersChanged(event);
        } // end if
    }
} 

