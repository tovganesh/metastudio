/*
 * GenericIDEWizard.java
 *
 * Created on July 20, 2003, 5:54 PM
 */

package org.meta.shell.idebeans;

import java.awt.*;
import java.awt.event.*;

import java.net.URL;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.html.*;
import org.meta.common.resource.FontResource;
import org.meta.common.resource.ImageResource;
import org.meta.common.resource.MiscResource;

/**
 * Defines a generic wizard framework to be used by all the IDE wizards.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class GenericIDEWizard extends JDialog {
    
    /** Holds value of property wizardName. */
    protected String wizardName;
    
    /** Holds value of property helpFile. */
    protected URL helpFile;
    
    /** Holds value of property noOfWizardSteps. */
    protected int noOfWizardSteps;
    
    /** Holds value of property wizardSteps. */
    protected String[] wizardSteps;
    
    /** Holds value of property stepDescriptions. */
    protected String[] stepDescriptions;
    
    /** Holds value of property wizardPages. */
    protected JPanel[] wizardPages;
    
    protected JEditorPane guidePanel;
    
    protected JPanel wizardPanel, controlPanel;
    
    protected JButton back, next, finish, cancel, help;
    
    /** Holds value of property currentStep. */
    protected int currentStep;
    
    /** Creates a new instance of GenericIDEWizard */
    public GenericIDEWizard(JFrame parent, String title, int noOfWizardSteps, 
                            String[] wizardSteps, String[] stepDescriptions) {                
        super(parent);
                            
        this.wizardName       = title;
        this.noOfWizardSteps  = noOfWizardSteps;
        this.wizardSteps      = wizardSteps;
        this.stepDescriptions = stepDescriptions;
        this.currentStep      = 0; // yes the steps start from zero        
        
        // update the title 
        setTitle(wizardName + " - " + wizardSteps[currentStep] 
               + " [Step " + (currentStep+1) + " of " + noOfWizardSteps + "]");
        
        initComponents();                
    }
    
    /** This method is called from within the constructor to
     * initialize the UI. 
     */
    protected void initComponents() {
        FontResource fonts = FontResource.getInstance();
        MiscResource misc  = MiscResource.getInstance();
        
        // set up the layout
        getContentPane().setLayout(new BorderLayout());
        
        // set up the general components
        
        // the guide panel
        guidePanel = new JEditorPane();        
        guidePanel.setEditorKit(new HTMLEditorKit());
        guidePanel.setEditable(false);
        guidePanel.setFont(fonts.getFrameFont());
        guidePanel.setText(getCurrentGuideText());
        guidePanel.setPreferredSize(misc.getGuidePanelDimension());
        getContentPane().add(guidePanel, BorderLayout.WEST);
        
        // the control panel
        controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.setFont(fonts.getFrameFont());
        back = new JButton("< Back");
        back.setMnemonic('B');
        back.setFont(fonts.getFrameFont());
        back.setEnabled(false); // in the beginning 'back' is disabled button
        back.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                backActionPerformed(event);
            }
        });
        controlPanel.add(back);
        
        next = new JButton("Next >");
        next.setMnemonic('N');
        next.setFont(fonts.getFrameFont());
        getRootPane().setDefaultButton(next);
        next.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                nextActionPerformed(event);
            }
        });
        controlPanel.add(next);
        
        cancel = new JButton("Cancel");
        cancel.setMnemonic('C');
        cancel.setFont(fonts.getFrameFont());
        ActionListener actionCancel = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                cancelActionPerformed(event);
            }
        };
        cancel.addActionListener(actionCancel);
        // and add the accelerator
        KeyStroke keyStrokeCancel = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 
                                                           0);
        cancel.registerKeyboardAction(actionCancel, "cancel", keyStrokeCancel, 
                                    JComponent.WHEN_IN_FOCUSED_WINDOW);
        controlPanel.add(cancel);
        
        finish = new JButton("Finish");
        finish.setMnemonic('F');
        finish.setFont(fonts.getFrameFont());
        finish.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                finishActionPerformed(event);
            }
        });
        controlPanel.add(finish);
        
        help = new JButton("Help");
        help.setMnemonic('H');
        help.setFont(fonts.getFrameFont());
        help.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                helpActionPerformed(event);
            }
        });
        controlPanel.add(help);
        controlPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        
        getContentPane().add(controlPanel, BorderLayout.SOUTH);
        
        // finally init the wizard pages
        wizardPanel = new JPanel(new CardLayout());        
        initWizardPages();
        
        // now add the wizard pages to the panel
        for(int i=0; i<noOfWizardSteps; i++) {
            wizardPanel.add(wizardPages[i], "" + i);
        } // end for
        
        getContentPane().add(wizardPanel, BorderLayout.CENTER);
        
        // additional properties and events 
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                cancelActionPerformed(null);
            }
        });      
    }
    
    /**
     * method that returns the current guid info as HTML.
     */
    protected String getCurrentGuideText() {
        StringBuffer guideString = new StringBuffer();
        
        guideString.append("<html> <head> </head>");        
        guideString.append("<body background=\"" 
                          + ImageResource.getInstance().getWaterMarkURL()
                          + "\" >");        
        guideString.append("<ol>");
        
        // make the current step bold and display the rest
        for(int i=0; i<wizardSteps.length; i++) {
            if (currentStep != i) {
                guideString.append("<li>" + wizardSteps[i]);
            } else {
                guideString.append("<li><b>" + wizardSteps[i] + "</b>");
            } // end if
        } // end for
        
        guideString.append("</ol>");        
        guideString.append("</html>");
        
        return guideString.toString();
    }
    
    /**
     * Methods implimentation should initilize the wizard pages.
     */
    protected void initWizardPages() {
        // do very little initilizations...
        wizardPages = new JPanel[noOfWizardSteps];
        
        for(int i=0; i<noOfWizardSteps; i++) {
            wizardPages[i] = new JPanel();
        } // end for
    }
    
    /** Getter for property wizardName.
     * @return Value of property wizardName.
     *
     */
    public String getWizardName() {
        return this.wizardName;
    }
    
    /** Setter for property wizardName.
     * @param wizardName New value of property wizardName.
     *
     */
    public void setWizardName(String wizardName) {
        this.wizardName = wizardName;
    }
    
    /** Getter for property helpFile.
     * @return Value of property helpFile.
     *
     */
    public URL getHelpFile() {
        return this.helpFile;
    }
    
    /** Setter for property helpFile.
     * @param helpFile New value of property helpFile.
     *
     */
    public void setHelpFile(URL helpFile) {
        this.helpFile = helpFile;
    }
    
    /** Getter for property noOfWizardSteps.
     * @return Value of property noOfWizardSteps.
     *
     */
    public int getNoOfWizardSteps() {
        return this.noOfWizardSteps;
    }
    
    /** Setter for property noOfWizardSteps.
     * @param noOfWizardSteps New value of property noOfWizardSteps.
     *
     */
    public void setNoOfWizardSteps(int noOfWizardSteps) {
        this.noOfWizardSteps = noOfWizardSteps;
    }
    
    /** Indexed getter for property wizardSteps.
     * @param index Index of the property.
     * @return Value of the property at <CODE>index</CODE>.
     *
     */
    public String getWizardSteps(int index) {
        return this.wizardSteps[index];
    }
    
    /** Getter for property wizardSteps.
     * @return Value of property wizardSteps.
     *
     */
    public String[] getWizardSteps() {
        return this.wizardSteps;
    }
    
    /** Indexed setter for property wizardSteps.
     * @param index Index of the property.
     * @param wizardSteps New value of the property at <CODE>index</CODE>.
     *
     */
    public void setWizardSteps(int index, String wizardSteps) {
        this.wizardSteps[index] = wizardSteps;
    }
    
    /** Setter for property wizardSteps.
     * @param wizardSteps New value of property wizardSteps.
     *
     */
    public void setWizardSteps(String[] wizardSteps) {
        this.wizardSteps = wizardSteps;
    }
    
    /** Indexed getter for property stepDescriptions.
     * @param index Index of the property.
     * @return Value of the property at <CODE>index</CODE>.
     *
     */
    public String getStepDescriptions(int index) {
        return this.stepDescriptions[index];
    }
    
    /** Getter for property stepDescriptions.
     * @return Value of property stepDescriptions.
     *
     */
    public String[] getStepDescriptions() {
        return this.stepDescriptions;
    }
    
    /** Indexed setter for property stepDescriptions.
     * @param index Index of the property.
     * @param stepDescriptions New value of the property at <CODE>index</CODE>.
     *
     */
    public void setStepDescriptions(int index, String stepDescriptions) {
        this.stepDescriptions[index] = stepDescriptions;
    }
    
    /** Setter for property stepDescriptions.
     * @param stepDescriptions New value of property stepDescriptions.
     *
     */
    public void setStepDescriptions(String[] stepDescriptions) {
        this.stepDescriptions = stepDescriptions;
    }
    
    /** Indexed getter for property wizardPages.
     * @param index Index of the property.
     * @return Value of the property at <CODE>index</CODE>.
     *
     */
    public JPanel getWizardPages(int index) {
        return this.wizardPages[index];
    }
    
    /** Getter for property wizardPages.
     * @return Value of property wizardPages.
     *
     */
    public JPanel[] getWizardPages() {
        return this.wizardPages;
    }
    
    /** Indexed setter for property wizardPages.
     * @param index Index of the property.
     * @param wizardPages New value of the property at <CODE>index</CODE>.
     *
     */
    public void setWizardPages(int index, JPanel wizardPages) {
        this.wizardPages[index] = wizardPages;
    }
    
    /** Setter for property wizardPages.
     * @param wizardPages New value of property wizardPages.
     *
     */
    public void setWizardPages(JPanel[] wizardPages) {
        this.wizardPages = wizardPages;
    }
    
    /**
     * Method called when <I>back</I> button of this wizard is 
     * clicked. Implimentors should override this method is case additional
     * functionality is needed.
     */
    protected void backActionPerformed(ActionEvent event) {
        currentStep--;
        
        // update UI
        if (currentStep == 0) back.setEnabled(false);
        
        next.setEnabled(true);
        
        ((CardLayout) wizardPanel.getLayout()).previous(wizardPanel);
        
        // and update guide panel accordingly
        guidePanel.setText(getCurrentGuideText());
        
        // update the title 
        setTitle(wizardName + " - " + wizardSteps[currentStep] 
               + " [Step " + (currentStep+1) + " of " + noOfWizardSteps + "]"); 
    }
    
    /**
     * Method called when <I>next</I> button of this wizard is 
     * clicked. Implimentors should override this method is case additional
     * functionality is needed.
     */
    protected void nextActionPerformed(ActionEvent event) {
        currentStep++;
        
        // update UI
        if (currentStep == noOfWizardSteps-1) next.setEnabled(false);
        
        back.setEnabled(true);
        
        ((CardLayout) wizardPanel.getLayout()).next(wizardPanel);
        
        // and update guide panel accordingly
        guidePanel.setText(getCurrentGuideText());
        
        // update the title 
        setTitle(wizardName + " - " + wizardSteps[currentStep] 
               + " [Step " + (currentStep+1) + " of " + noOfWizardSteps + "]"); 
    }
    
    /**
     * Method called when <I>cancel</I> button of this wizard is 
     * clicked. Implimentors should override this method is case additional
     * functionality is needed.
     */
    protected void cancelActionPerformed(ActionEvent event) {
        if (JOptionPane.showConfirmDialog(this, 
                "Do you want to exit this wizard?", "Quit the wizard?", 
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        } else {            
            dispose();
        }
    }
    
    /**
     * Method called when <I>finish</I> button of this wizard is 
     * clicked. Implimentors should override this method is case additional
     * functionality is needed.
     */
    protected void finishActionPerformed(ActionEvent event) {
        this.setVisible(false); // hide the wizard
    }
    
    /**
     * Method called when <I>help</I> button of this wizard is 
     * clicked. Implimentors should override this method is case additional
     * functionality is needed.
     */
    protected void helpActionPerformed(ActionEvent event) {
    }
    
    /** Getter for property currentStep.
     * @return Value of property currentStep.
     *
     */
    public int getCurrentStep() {
        return this.currentStep;
    }
    
    /** Setter for property currentStep.
     * @param currentStep New value of property currentStep.
     *
     */
    public void setCurrentStep(int currentStep) {
        this.currentStep = currentStep;
    }
    
} // end of class GenericIDEWizard
