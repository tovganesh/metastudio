/*
 * FragmentationWizard.java
 *
 * Created on February 25, 2005, 7:24 AM
 */

package org.meta.shell.idebeans;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import org.meta.common.resource.FontResource;
import org.meta.common.resource.MiscResource;

import org.meta.shell.ide.MeTA;

/**
 * This great wizard guids you through creation as well as refinment of 
 * fragment(s) and or fragmentation scheme(s).
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FragmentationWizard extends GenericIDEWizard {
    
    /** the UI components for page 1 */
    private JLabel fragmentSchemeNameHelpLabel, orLabel;
    private JLabel descriptionLabel1;
    private JTextField fragmentSchemeName;
    private JComboBox fragmentSchemes;
    private JRadioButton newFragmentSchemeName, existingFragmentSchemeName;
    private ButtonGroup fragmentSchemeNameGroup;
    
    /** the UI components for page 2 */
    private JLabel descriptionLabel2;
    private JComboBox automatedFragmentation;
    private JLabel methodDescription;
    
    /** the IDE instance */
    private MeTA ideInstance;
    
    /** Creates a new instance of FragmentationWizard */
    public FragmentationWizard(JFrame parent) {
        super(parent, "Fragmentation wizard", 6, 
             (new String[] {"Specify Name", "Fragmentation Method", 
                            "Choose Constraints", "Evaluate Fragments", 
                            "Check Goodness", "Summary"}),
             (new String[] {"Specify a new scheme or choose an existing scheme", 
                            "Do you want to perform automatic fragmentation?", 
                            "Choose the constraints you want to apply.",
                            "Evaluate the new generated fragment(s).",
                            "Verify the goodness of the atoms in this fragment",
                            "A summary of the wizard operations."}));
                            
        // TODO: change this
        setSize(MiscResource.getInstance().getFragmentationWizardDimension());
        
        // the following line is very crutial .. if the following code fails
        // the other methods may not work correctly
        try {
            ideInstance = (MeTA) parent;
        } catch (Exception e) {
            System.err.println("Warning! : FragmentationWizard is not passed "
                               + "with a proper instance of MeTA : " + e);
            e.printStackTrace();
        } // end try .. catch block
    }
    
    /**
     * set up all the wizard pages one by one
     */
    @Override
    protected void initWizardPages() {
        super.initWizardPages();
                
        makePageOne();
        makePageTwo();
    }
    
    /**
     * setup page1 of the wizard
     */
    protected void makePageOne() {
        FontResource fonts = FontResource.getInstance();
        
        // grid bag constraints
        GridBagConstraints gbc = new GridBagConstraints();        
        gbc.insets = new Insets(2, 2, 2, 0);
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.fill   = GridBagConstraints.BOTH;
        
        // >>>>> Page 1
        wizardPages[0].setLayout(new GridBagLayout());        
        wizardPages[0].setAutoscrolls(true);
        // the following needed for the autoscrolls features
        wizardPages[0].addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
                ((JPanel)e.getSource()).scrollRectToVisible(r);
            }
        });                
        
        fragmentSchemeNameGroup = new ButtonGroup();
        
        // scheme name choosers... first a new name
        newFragmentSchemeName = new JRadioButton(
                "Specify name for Fragmentation Scheme: *");
        fragmentSchemeNameGroup.add(newFragmentSchemeName);
        newFragmentSchemeName.setSelected(true);
        newFragmentSchemeName.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                // enable the relevent components
                fragmentSchemeName.setEnabled(true);
                fragmentSchemeNameHelpLabel.setEnabled(true);
                
                // and disable the irrelevent ones
                fragmentSchemes.setEnabled(false);
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 0;        
        wizardPages[0].add(newFragmentSchemeName, gbc);
        
        fragmentSchemeName = new JTextField();
        gbc.gridx = 0;
        gbc.gridy = 1;        
        wizardPages[0].add(fragmentSchemeName, gbc);
        
        // instruction ...
        fragmentSchemeNameHelpLabel = new JLabel("* A name helps you identify" +
                " a scheme and compare with others.", JLabel.LEFT);
        fragmentSchemeNameHelpLabel.setFont(fonts.getSmallFont());
        gbc.insets = new Insets(1, 5, 2, 0);
        gbc.gridx = 0;
        gbc.gridy = 2;        
        wizardPages[0].add(fragmentSchemeNameHelpLabel, gbc);
        
        // OR...
        orLabel = new JLabel("Or", JLabel.LEFT);
        orLabel.setFont(fonts.getTaskGroupFont());
        gbc.insets = new Insets(2, 2, 2, 0);
        gbc.gridx = 0;
        gbc.gridy = 3;        
        wizardPages[0].add(orLabel, gbc);
        
        // scheme name choosers... from an existing name
        existingFragmentSchemeName = new JRadioButton(
           "Choose an existing Fragmentation Scheme to add the new fragment: ");
        fragmentSchemeNameGroup.add(existingFragmentSchemeName);
        existingFragmentSchemeName.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                // enable the relevent components
                fragmentSchemes.setEnabled(true);                
                
                // and disable the irrelevent ones
                fragmentSchemeName.setEnabled(false);
                fragmentSchemeNameHelpLabel.setEnabled(false);
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 4;        
        wizardPages[0].add(existingFragmentSchemeName, gbc);
        
        // and then show the fragment schemes
        // TODO ... fill up this combo
        fragmentSchemes = new JComboBox();
        fragmentSchemes.setEditable(false);
        fragmentSchemes.setEnabled(false);
        gbc.gridx = 0;
        gbc.gridy = 5;        
        wizardPages[0].add(fragmentSchemes, gbc);
        
        //      and finally add a label for description
        descriptionLabel1 = new JLabel(stepDescriptions[0], JLabel.LEFT);
        descriptionLabel1.setFont(fonts.getDescriptionFont());
        gbc.insets = new Insets(6, 2, 2, 0);
        gbc.gridx  = 0;
        gbc.gridy  = 6;        
        gbc.fill   = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.SOUTH;
        wizardPages[0].add(descriptionLabel1, gbc);
    }
    
    /**
     * setup page2 of the wizard
     */
    protected void makePageTwo() {
        FontResource fonts = FontResource.getInstance();
        
        // grid bag constraints
        GridBagConstraints gbc = new GridBagConstraints();        
        gbc.insets = new Insets(2, 2, 2, 0);
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.fill   = GridBagConstraints.BOTH;
        
        // >>>>> Page 2
        wizardPages[1].setLayout(new GridBagLayout());        
        wizardPages[1].setAutoscrolls(true);
        // the following needed for the autoscrolls features
        wizardPages[1].addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
                ((JPanel)e.getSource()).scrollRectToVisible(r);
            }
        });        
        
        // add a label for description
        descriptionLabel2 = new JLabel(stepDescriptions[1], JLabel.LEFT);
        descriptionLabel2.setFont(fonts.getDescriptionFont());
        gbc.insets = new Insets(2, 2, 6, 0);
        gbc.gridx  = 0;
        gbc.gridy  = 0;        
        gbc.fill   = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.SOUTH;
        wizardPages[1].add(descriptionLabel2, gbc);
        
        // and add the options
        automatedFragmentation = new JComboBox();
        automatedFragmentation.setEditable(false);
        automatedFragmentation.addItem("No, thanks; Show me other options.");
        automatedFragmentation.addItem("Yes, Please!"); 
        automatedFragmentation.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent ie) {
                if (ie.getItem().equals("Yes, Please!")) {
                    methodDescription.setText(
                            "Allows you to do automated fragmentation.");
                } else {
                    methodDescription.setText(
                            "Allows you to do manual selection of fragments.");
                } // end if
            }
        });
        gbc.gridx  = 0;
        gbc.gridy  = 1;                
        wizardPages[1].add(automatedFragmentation, gbc);
        
        // and add the description lable for the fragmentation method
        methodDescription = new JLabel(
                "Allows you to do manual selection of fragments.", JLabel.LEFT);
        methodDescription.setFont(fonts.getSmallFont());
        gbc.insets = new Insets(6, 2, 6, 0);
        gbc.gridx  = 0;
        gbc.gridy  = 2;        
        gbc.fill   = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.SOUTH;
        wizardPages[1].add(methodDescription, gbc);
    }
    
} // end of class FragmentationWizard
