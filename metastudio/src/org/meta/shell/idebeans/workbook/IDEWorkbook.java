/*
 * IDEWorkbook.java
 *
 * Created on March 6, 2006, 9:48 PM
 *
 */

package org.meta.shell.idebeans.workbook;

import java.awt.*;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;
import org.meta.molecule.Molecule;
import org.meta.shell.idebeans.IDEVerticalFlowLayout;
import org.meta.symbolic.SymbolicInterpreter;

/**
 * A new way of programming in MeTA Studio. 
 * This interface is meant to a solid framework for easier 
 * Mathematica <sup>TM</sup> style programming with in MeTA Studio. However,
 * it must be pointed out here that BeanShell is still the primary and most
 * powerful way of programming in MeTA Studio. Also, the workbook interface 
 * in-turn uses the BeanShell framework for actual execution in the JVM.
 * The IDEWorkbook can also be used in a passive mode, where in you can 
 * programmatically add WorkbookItem s but no "workbook" interpreter is used.
 * (TODO: interpreter is not yet implemented)
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class IDEWorkbook extends JPanel {
    
    private JScrollPane workbookScrollPane;
    
    /** Creates a new instance of IDEWorkbook in passive mode, no interpreter
     *  used for the IDEWorkbook
     */
    public IDEWorkbook() {                        
        this(true); // default: we do not use the workbook imterpreter, that is
                    // we are passive by default
    }

    /** Creates a new instance of IDEWorkbook
     *
     * @param passiveMode if true, no interpreter is used, else a symbolic
     *        interpreter is used
     */
    public IDEWorkbook(boolean passiveMode) {
        workbookScrollPane = new JScrollPane(this);
        setAutoscrolls(true);
        setLayout(new IDEVerticalFlowLayout());
        setBackground(Color.white);

        this.passiveMode = passiveMode;

        if (!passiveMode) initSymbolicInterpreter();
    }

    /**
     * Holds value of property passiveMode.
     */
    private boolean passiveMode;

    /**
     * Getter for property passiveMode.
     * @return Value of property passiveMode.
     */
    public boolean isPassiveMode() {
        return this.passiveMode;
    }

    /**
     * Setter for property passiveMode.
     * @param passiveMode New value of property passiveMode.
     */
    public void setPassiveMode(boolean passiveMode) {
        this.passiveMode = passiveMode;
    }

    /**
     * Holds value of property title.
     */
    private String title;

    /**
     * Getter for property title.
     * @return Value of property title.
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Setter for property title.
     * @param title New value of property title.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    private SymbolicInterpreter symInterpreter;
    
    /** Initialize the symbolic interpreter mode */
    protected void initSymbolicInterpreter() {
        symInterpreter = new SymbolicInterpreter();
        
        // TODO: primitive, needs redesign
        KeyAdapter ka = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    JTextField jf = (JTextField) e.getSource();

                    jf.setEditable(false);
                    jf.removeKeyListener(this);

                    String text = jf.getText();

                    Object result = symInterpreter.eval(text);
                    
                    // TODO: handle other objects
                    if (result instanceof Molecule) {
                        addWorkbookItem(new WorkbookItem((Molecule) result));
                    } // end if

                    addWorkbookItem(
                            WorkbookItem.createTextBoxWithLabel("In: ", this));
                } // end if
            }
        };
        
        addWorkbookItem(WorkbookItem.createTextBoxWithLabel("In: ", ka));
    }

    /**
     * Add a workbook item
     *
     * @param wi an instance of WorkbookItem
     */
    public void addWorkbookItem(WorkbookItem wi) {
        add(Box.createVerticalStrut(2));
        add(wi);                                        
                             
        scrollRectToVisible(new Rectangle(0, getSize().height, 
                                             getSize().width, 
                                             getSize().height));
        updateUI();
    }
} // end of class IDEWorkbook
