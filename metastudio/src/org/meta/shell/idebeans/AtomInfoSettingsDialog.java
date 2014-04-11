/*
 * AtomInfoSettingsDialog.java
 *
 * Created on April 23, 2005, 8:15 AM
 */

package org.meta.shell.idebeans;

import java.awt.*;
import java.util.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
        
import java.lang.ref.WeakReference;
import org.meta.common.resource.MiscResource;
import org.meta.common.resource.StringResource;
import org.meta.shell.ide.MeTA;
import org.meta.config.impl.AtomInfo;
        
/**
 * A dialog that shows up a periodic table style interface and allows you to 
 * change various parameters related to representation of an atom with in 
 * the MeTA Studio.
 * Follows a singleton pattern.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class AtomInfoSettingsDialog extends JDialog {
    
    private static WeakReference<AtomInfoSettingsDialog> _aisDialog = null;
    
    private JPanel elementsPanel, elementPropertiesPanel;
    private ButtonGroup elementButtonGroup;
    
    private HashMap<String, Integer> positionMap;
          
    private JLabel symbolLabel, nameLabel, atomicNumberLabel,
                   atomicWeightLabel, covalentRadiusLabel, vdwRadiusLabel,
                   defaultValencyLabel, weakBondAngleLabel, 
                   doubleBondOverlapLabel;
    private JButton color, basisSets, restoreDefaults;
    private JTextField symbol, name, atomicNumber;
    private JSpinner atomicWeight, covalentRadius, vdwRadius, defaultValency, 
            weakBondAngle, doubleBondOverlap;
    
    /** Creates a new instance of AtomInfoSettingsDialog */
    private AtomInfoSettingsDialog(MeTA ideInstance) {
        super(ideInstance);
        
        setTitle("AtomInfo Settings : " 
                  + StringResource.getInstance().getVersion());
        
        // set up position map of the elements
        positionMap = new HashMap<String, Integer>(100);
        positionMap.put("H" , 0);
        positionMap.put("He", 18);
        positionMap.put("Li", 19);
        positionMap.put("Be", 20);
        positionMap.put("B" , 32);
        positionMap.put("C" , 33);
        positionMap.put("N" , 34);
        positionMap.put("O" , 35);
        positionMap.put("F" , 36);
        positionMap.put("Ne", 37);
        positionMap.put("Na", 38);
        positionMap.put("Mg", 39);
        positionMap.put("Al", 51);
        positionMap.put("Si", 52);
        positionMap.put("P" , 53);
        positionMap.put("S" , 54);
        positionMap.put("Cl", 55);
        positionMap.put("Ar", 56);
        positionMap.put("K" , 57);
        positionMap.put("Ca", 58);
        positionMap.put("Sc", 60);
        positionMap.put("Ti", 61);
        positionMap.put("V" , 62);
        positionMap.put("Cr", 63);
        positionMap.put("Mn", 64);
        positionMap.put("Fe", 65);
        positionMap.put("Co", 66);
        positionMap.put("Ni", 67);
        positionMap.put("Cu", 68);
        positionMap.put("Zn", 69);
        positionMap.put("Ga", 70);
        positionMap.put("Ge", 71);
        positionMap.put("As", 72);
        positionMap.put("Se", 73);
        positionMap.put("Br", 74);
        positionMap.put("Kr", 75);
        positionMap.put("Rb", 76);
        positionMap.put("Sr", 77);
        positionMap.put("Y" , 79);
        positionMap.put("Zr", 80);
        positionMap.put("Nb", 81);
        positionMap.put("Mo", 82);
        positionMap.put("Tc", 83);
        positionMap.put("Ru", 84);
        positionMap.put("Rh", 85);
        positionMap.put("Pd", 86);
        positionMap.put("Ag", 87);
        positionMap.put("Cd", 88);
        positionMap.put("In", 89);
        positionMap.put("Sn", 90);
        positionMap.put("Sb", 91);
        positionMap.put("Te", 92);
        positionMap.put("I" , 93);
        positionMap.put("Xe", 94);
        positionMap.put("Cs", 95);
        positionMap.put("Ba", 96);
        positionMap.put("La", 97);
        positionMap.put("Lu", 98);
        positionMap.put("Hf", 99);
        positionMap.put("Ta", 100);
        positionMap.put("W" , 101);
        positionMap.put("Re", 102);
        positionMap.put("Os", 103);
        positionMap.put("Ir", 104);
        positionMap.put("Pt", 105);
        positionMap.put("Au", 106);
        positionMap.put("Hg", 107);
        positionMap.put("Tl", 108);
        positionMap.put("Pb", 109);
        positionMap.put("Bi", 110);
        
        positionMap.put("Vec", 131);
        positionMap.put("X"  , 132);
        
        // then make the components
        initComponents();
        setSize(MiscResource.getInstance().getAtomInfoDialogDimension());        
    }
    
    /** This method is called to initialize the UI. 
     */
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // set up the elements panel .. 7 rows and 19 columns lay out
        elementsPanel = new JPanel(new GridLayout(7, 19, 0, 0));
        setUpElementsPanel();
        add(elementsPanel, BorderLayout.CENTER);
        
        elementPropertiesPanel = new JPanel();        
        setUpElementPropertiesPanel();
        add(elementPropertiesPanel, BorderLayout.SOUTH);
        
        // and add the accelerator .. ESC key for canceling the dialog
        ActionListener actionCancel = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                setVisible(false);
                dispose();
            }
        };
                
        KeyStroke keyStrokeCancel = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 
                                                           0);
        elementsPanel.registerKeyboardAction(actionCancel, "cancel", 
                                    keyStrokeCancel, 
                                    JComponent.WHEN_IN_FOCUSED_WINDOW);
    }
    
    /**
     * construct the elements panel
     */
    private void setUpElementsPanel() {
        AtomInfo atomInfo = AtomInfo.getInstance();
        elementButtonGroup = new ButtonGroup();                
        
        Enumeration<String> atomSymbols = atomInfo.getNameTable().keys();
        Border lineBorder  = new LineBorder(Color.black, 1, true);
        Border emptyBorder = new EmptyBorder(0, 0, 0, 0);
        int i = 0;
        
        // first setup dummy buttons
        for(i=0; i<7; i++) {
            for(int j=0; j<19; j++) {
                JToggleButton elementButton = new JToggleButton(" ");
                elementButton.setBorder(emptyBorder);
                elementButton.setEnabled(false);
                elementsPanel.add(elementButton);
            } // end for
        } // end for
                
        // make the action listener
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {  
                AtomInfo atomInfo = AtomInfo.getInstance();
                String theSymbol = ae.getActionCommand();
                
                symbol.setText(theSymbol);
                name.setText(atomInfo.getName(theSymbol));
                atomicNumber.setText(atomInfo.getAtomicNumber(theSymbol) + "");
                atomicWeight.setValue(
                      new Double(atomInfo.getAtomicWeight(theSymbol)));
                color.setBackground(atomInfo.getColor(theSymbol));
                covalentRadius.setValue(
                      new Double(atomInfo.getCovalentRadius(theSymbol)));
                vdwRadius.setValue(
                      new Double(atomInfo.getVdwRadius(theSymbol)));
                defaultValency.setValue(
                      new Integer(atomInfo.getDefaultValency(theSymbol)));
                weakBondAngle.setValue(
                      new Double(atomInfo.getWeakBondAngle(theSymbol)));
                doubleBondOverlap.setValue(
                      new Double(atomInfo.getDoubleBondOverlap(theSymbol)));
            }
        };
        
        // and then fill in the actual ones        
        while(atomSymbols.hasMoreElements()) { 
            String symbol = atomSymbols.nextElement();            
            JToggleButton elementButton = (JToggleButton)
                 elementsPanel.getComponent(positionMap.get(symbol).intValue());
            
            elementButton.setText(symbol);
            elementButton.setBackground(atomInfo.getColor(symbol));
            elementButton.setToolTipText(atomInfo.getName(symbol));
            elementButton.setBorder(lineBorder);
            elementButton.setEnabled(true);
            elementButton.addActionListener(actionListener);
            elementButtonGroup.add(elementButton);            
        } // end while
        
        elementsPanel.setBorder(BorderFactory.createTitledBorder(
                "Select an atom to change its properties: "));                
    }
    
    /**
     * construct the element properties panel
     */
    private void setUpElementPropertiesPanel() {
        elementPropertiesPanel.setBorder(BorderFactory.createTitledBorder(
                         "Properties of currently selected atom: "));
        
        elementPropertiesPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(2, 5, 2, 1);
        
        // symbol
        symbolLabel = new JLabel("Symbol: ", JLabel.RIGHT);        
        gbc.gridx = 0;
        gbc.gridy = 0;
        elementPropertiesPanel.add(symbolLabel, gbc);
        
        symbol = new JTextField(2);
        symbol.setEditable(false);
        symbol.setToolTipText("IUPAC symbol of atom");
        symbolLabel.setDisplayedMnemonic('S');
        symbolLabel.setLabelFor(symbol);
        gbc.gridx = 1;
        gbc.gridy = 0;        
        elementPropertiesPanel.add(symbol, gbc);
        
        // name
        nameLabel = new JLabel("Name: ", JLabel.RIGHT);             
        gbc.gridx = 2;
        gbc.gridy = 0;
        elementPropertiesPanel.add(nameLabel, gbc);
        
        name = new JTextField(15);
        name.setEditable(false);
        name.setToolTipText("Name of atom");
        nameLabel.setDisplayedMnemonic('a');
        nameLabel.setLabelFor(name);
        gbc.gridx = 3;
        gbc.gridy = 0;
        elementPropertiesPanel.add(name, gbc);
        
        // Atomic Number
        atomicNumberLabel = new JLabel("Atomic Number: ", JLabel.RIGHT);        
        gbc.gridx = 0;
        gbc.gridy = 1;
        elementPropertiesPanel.add(atomicNumberLabel, gbc);
        
        atomicNumber = new JTextField(5);
        atomicNumber.setToolTipText("Atomic Number of atom");
        atomicNumber.setEditable(false);
        atomicNumberLabel.setDisplayedMnemonic('N');
        atomicNumberLabel.setLabelFor(atomicNumber);
        gbc.gridx = 1;
        gbc.gridy = 1;
        elementPropertiesPanel.add(atomicNumber, gbc);
       
        // Atomic Weight
        atomicWeightLabel = new JLabel("Atomic Weight: ", JLabel.RIGHT);        
        gbc.gridx = 2;
        gbc.gridy = 1;
        elementPropertiesPanel.add(atomicWeightLabel, gbc);
        
        atomicWeight = new JSpinner(
                            new SpinnerNumberModel(0.0, 0.0, 500.0, 0.01));
        atomicWeight.setToolTipText("Atomic weight of atom in a.u.");
        atomicWeight.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                Double value = (Double) atomicWeight.getModel().getValue();
                AtomInfo ai = AtomInfo.getInstance();
                
                ai.setAtomicWeight(symbol.getText(), value.doubleValue());
                saveUserConfigFile();
            }
        });
        atomicWeightLabel.setDisplayedMnemonic('g');
        atomicWeightLabel.setLabelFor(atomicWeight);
        gbc.gridx = 3;
        gbc.gridy = 1;
        elementPropertiesPanel.add(atomicWeight, gbc);
        
        // color
        color = new JButton("Color");
        color.setMnemonic('o');
        color.setToolTipText("Color of atom when displayed in MeTA Studio");
        color.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (symbol.getText() == null || symbol.getText().equals(" ")) {
                    return;
                } // end if
                
                JColorChooser cc = new JColorChooser();
                
                Color newColor = cc.showDialog(AtomInfoSettingsDialog.this,
                        "Choose a color for : " + symbol.getText()
                        + "(" + name.getText() + ")",
                        color.getBackground());
                if (newColor != null) {
                    AtomInfo ai = AtomInfo.getInstance();
                    
                    ai.setColor(symbol.getText(), newColor);
                    saveUserConfigFile();
                    color.setBackground(newColor);
                    
                    JToggleButton elementButton = (JToggleButton)
                         elementsPanel.getComponent(
                                positionMap.get(symbol.getText()).intValue());
                
                    elementButton.setBackground(newColor);
                } // end if
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        elementPropertiesPanel.add(color, gbc);
        
        gbc.gridwidth = 1;
        
        // Covalent Radius
        covalentRadiusLabel = new JLabel("Covalent Radius: ", JLabel.RIGHT);        
        gbc.gridx = 0;
        gbc.gridy = 3;
        elementPropertiesPanel.add(covalentRadiusLabel, gbc);
        
        covalentRadius = new JSpinner(
                            new SpinnerNumberModel(0.0, 0.0, 20.0, 0.01));
        covalentRadius.setToolTipText("Covalent radius in angstroms");
        covalentRadius.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                Double value = (Double) covalentRadius.getModel().getValue();
                AtomInfo ai = AtomInfo.getInstance();
                
                ai.setCovalentRadius(symbol.getText(), value.doubleValue());
                saveUserConfigFile();                                
            }
        });
        covalentRadiusLabel.setDisplayedMnemonic('C');
        covalentRadiusLabel.setLabelFor(covalentRadius);
        gbc.gridx = 1;
        gbc.gridy = 3;
        elementPropertiesPanel.add(covalentRadius, gbc);
        
        // vdW Radius
        vdwRadiusLabel = new JLabel("vdW Radius: ", JLabel.RIGHT);        
        gbc.gridx = 2;
        gbc.gridy = 3;
        elementPropertiesPanel.add(vdwRadiusLabel, gbc);
        
        vdwRadius = new JSpinner(
                            new SpinnerNumberModel(0.0, 0.0, 40.0, 0.01));
        vdwRadius.setToolTipText("van der Waals radius in angstroms");
        vdwRadius.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
                Double value = (Double) vdwRadius.getModel().getValue();
                AtomInfo ai = AtomInfo.getInstance();
                
                ai.setVdwRadius(symbol.getText(), value.doubleValue());
                saveUserConfigFile();
            }
        });
        vdwRadiusLabel.setDisplayedMnemonic('v');
        vdwRadiusLabel.setLabelFor(vdwRadius);
        gbc.gridx = 3;
        gbc.gridy = 3;
        elementPropertiesPanel.add(vdwRadius, gbc);
        
        // default valency
        defaultValencyLabel = new JLabel("Default Valency: ", JLabel.RIGHT);        
        gbc.gridx = 0;
        gbc.gridy = 4;
        elementPropertiesPanel.add(defaultValencyLabel, gbc);
        
        defaultValency = new JSpinner(
                            new SpinnerNumberModel(0, 0, 8, 1));
        defaultValency.setToolTipText("Default valency of this atom");
        defaultValency.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                Integer value = (Integer) defaultValency.getModel().getValue();
                AtomInfo ai = AtomInfo.getInstance();
                
                ai.setDefaultValency(symbol.getText(), value.intValue());
                saveUserConfigFile();
            }
        });
        defaultValencyLabel.setDisplayedMnemonic('e');
        defaultValencyLabel.setLabelFor(defaultValency);
        gbc.gridx = 1;
        gbc.gridy = 4;        
        elementPropertiesPanel.add(defaultValency, gbc);
        
        // weak bond angle
        weakBondAngleLabel = new JLabel("Weak Bond Angle: ", JLabel.RIGHT);        
        gbc.gridx = 2;
        gbc.gridy = 4;
        elementPropertiesPanel.add(weakBondAngleLabel, gbc);
        
        weakBondAngle = new JSpinner(
                            new SpinnerNumberModel(0.0, 0.0, 3.15, 0.01));
        weakBondAngle.setToolTipText("Angle in radians used to determine " +
                                     "presence of a weak interaction");
        weakBondAngle.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                Double value = (Double) weakBondAngle.getModel().getValue();
                AtomInfo ai = AtomInfo.getInstance();
                
                ai.setWeakBondAngle(symbol.getText(), value.doubleValue());
                saveUserConfigFile();
            }
        });
        weakBondAngleLabel.setDisplayedMnemonic('W');
        weakBondAngleLabel.setLabelFor(weakBondAngle);
        gbc.gridx = 3;
        gbc.gridy = 4;
        elementPropertiesPanel.add(weakBondAngle, gbc);
        
        // double bond overlap
        doubleBondOverlapLabel = new JLabel("Double Bond Overlap: ", 
                                            JLabel.RIGHT);        
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        elementPropertiesPanel.add(doubleBondOverlapLabel, gbc);
        
        doubleBondOverlap = new JSpinner(
                            new SpinnerNumberModel(0.0, 0.0, 100.0, 1.0));
        doubleBondOverlap.setToolTipText("Percentage overlap to determine " +
                                         "a double bond between two atoms");
        doubleBondOverlap.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                Double value = (Double) doubleBondOverlap.getModel().getValue();
                AtomInfo ai = AtomInfo.getInstance();
                
                ai.setDoubleBondOverlap(symbol.getText(), value.doubleValue());
                saveUserConfigFile();
            }
        });
        doubleBondOverlapLabel.setDisplayedMnemonic('u');
        doubleBondOverlapLabel.setLabelFor(doubleBondOverlap);
        gbc.gridx = 2;
        gbc.gridy = 5;        
        elementPropertiesPanel.add(doubleBondOverlap, gbc);
        
        // basis sets
        gbc.insets = new Insets(5, 5, 5, 1);
        basisSets = new JButton("Basis Sets...");
        basisSets.setMnemonic('B');
        basisSets.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                
            }
        });
        basisSets.setToolTipText("View/Modify basis sets related to this atom");
        gbc.gridx = 0;
        gbc.gridy = 6;
        elementPropertiesPanel.add(basisSets, gbc);
        
        // roll back evey thing        
        restoreDefaults = new JButton("Restore Defaults");
        restoreDefaults.setMnemonic('f');
        restoreDefaults.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (symbol.getText() == null || symbol.getText().equals(" ")) {
                    return;
                } // end if
                
                AtomInfo ai = AtomInfo.getInstance();
                String theSymbol = symbol.getText();
                
                ai.resetValues(theSymbol);
                saveUserConfigFile();
                
                JToggleButton elementButton = (JToggleButton)
                    elementsPanel.getComponent(
                            positionMap.get(theSymbol).intValue());
                
                elementButton.setBackground(ai.getColor(theSymbol));
                atomicWeight.setValue(
                      new Double(ai.getAtomicWeight(theSymbol)));
                color.setBackground(ai.getColor(theSymbol));
                covalentRadius.setValue(
                      new Double(ai.getCovalentRadius(theSymbol)));
                vdwRadius.setValue(
                      new Double(ai.getVdwRadius(theSymbol)));
                defaultValency.setValue(
                      new Integer(ai.getDefaultValency(theSymbol)));
                weakBondAngle.setValue(
                      new Double(ai.getWeakBondAngle(theSymbol)));
                doubleBondOverlap.setValue(
                      new Double(ai.getDoubleBondOverlap(theSymbol)));
            }
        });
        restoreDefaults.setToolTipText("Restore the default properties " +
                                       "(not basis sets) related to this atom");
        gbc.gridx = 2;
        gbc.gridy = 6;
        elementPropertiesPanel.add(restoreDefaults, gbc);
    }
    
    /**
     * save the use config file 
     */
    private void saveUserConfigFile() {
        try { 
            AtomInfo.getInstance().saveUserConfigFile(); 
        } catch(Exception e) { 
        } // end of try .. catch block
    }
    
    /**
     * Get an instance of AtomInfoSettingsDialog
     *
     * @param ideInstance the parent MeTA Studio frame, i.e.
     * @return instance of this dialog 
     */
    public static AtomInfoSettingsDialog getInstance(MeTA ideInstance) {
        if (_aisDialog == null) {
            _aisDialog = 
                 new WeakReference<AtomInfoSettingsDialog>(
                         new AtomInfoSettingsDialog(ideInstance));
        } // end if   
        
        AtomInfoSettingsDialog aisDialog = _aisDialog.get();
        
        if (aisDialog == null) {
            aisDialog  = new AtomInfoSettingsDialog(ideInstance);
            _aisDialog = new WeakReference<AtomInfoSettingsDialog>(aisDialog);
        } // end if
        
        return aisDialog;
    }
} // end of class AtomInfoSettingsDialog
