/*
 * FindTextPanel.java 
 *
 * Created on 9 Oct, 2008 
 */

package org.meta.shell.idebeans;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.syntax.jedit.JEditTextArea;

/**
 * Provides a "find-as-you-type" customizable panel that can be used
 * with text editing components.
 * 
 * @author V. Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FindTextPanel extends JPanel {

    protected JEditorPane editorPane;
    protected JEditTextArea editorPaneTA;

    protected JTextField findString;
    protected JButton findNext, findPrev;
    
    protected boolean showNext, showPrevious;
    protected String nextButtonText, prevButtonText;
            
    /** Creates an instance of FindTextPanel */
    public FindTextPanel(JComponent editorPane) {        
        this(editorPane, true, true);
    }
    
    /** Creates an instance of FindTextPanel */
    public FindTextPanel(JComponent editorPane, 
                         boolean showNext, boolean showPrevious) {        
        this(editorPane, showNext, showPrevious,
             "Next", "Previous");
    }
    
    /** Creates an instance of FindTextPanel */
    public FindTextPanel(JComponent editorPane, 
                         boolean showNext, boolean showPrevious,
                         String nextButtonText, String prevButtonText) {  
        if (editorPane instanceof JEditorPane) {
            this.editorPane   = (JEditorPane) editorPane;
            this.editorPaneTA = null;
        } else if (editorPane instanceof JEditTextArea) {
            this.editorPaneTA = (JEditTextArea) editorPane;
            this.editorPane   = null;
        } else {
            throw new UnsupportedOperationException("FindTextPanel can only" +
                    " be used with JEditTextArea or JEditorPane insatances.");
        } // end if
        
        this.showNext     = showNext;
        this.showPrevious = showPrevious;
        
        this.nextButtonText = nextButtonText;
        this.prevButtonText = prevButtonText;
        
        initComponents();
    }        
    
    /** initilize the components */
    private void initComponents() {
        removeAll();
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        
        JLabel findLabel = new JLabel("Find: ", JLabel.RIGHT);
        findLabel.setDisplayedMnemonic('F');
        add(findLabel);                    
        setBorder(BorderFactory.createLineBorder(Color.black, 1));

        findString = new JTextField(20);            
        findLabel.setLabelFor(findString);
        if (editorPane != null) {
            findString.addKeyListener(new KeyAdapter() {           
                @Override
                public void keyReleased(KeyEvent e) {
                    try {                    
                        int found = editorPane.getDocument().getText(0, 
                                      editorPane.getDocument().getLength())
                                           .indexOf(findString.getText(), 0);
                        if (found >= 0) {                        
                            editorPane.setSelectionEnd(found);
                            editorPane.setSelectionStart(found);
                            editorPane.setSelectionEnd(
                                       found + findString.getText().length());                            
                            findString.setBackground(Color.white);
                        } else {
                            findString.setBackground(Color.red);
                        } // end if                                                  
                    } catch(Exception ignored) {
                        System.err.println("Exception in find: " + ignored);
                        ignored.printStackTrace();
                    } // end of try .. catch block
                }
            });
        } else if (editorPaneTA != null) {
            findString.addKeyListener(new KeyAdapter() {           
                @Override
                public void keyReleased(KeyEvent e) {
                    try {       
                        int start = editorPaneTA.getMagicCaretPosition();
                        int found = editorPaneTA.getDocument().getText(0, 
                                      editorPaneTA.getText().length()).indexOf(
                                           findString.getText(), start);
                        if (found >= 0) {                        
                            editorPaneTA.setMagicCaretPosition(found);
                            editorPaneTA.setSelectionEnd(found);
                            editorPaneTA.setSelectionStart(found);
                            editorPaneTA.setSelectionEnd(
                                       found + findString.getText().length());                            
                            findString.setBackground(Color.white);
                        } else {
                            findString.setBackground(Color.red);
                        } // end if                                                  
                    } catch(Exception ignored) {
                        System.err.println("Exception in find: " + ignored);
                        ignored.printStackTrace();
                    } // end of try .. catch block
                }
            });
        } // end if
        add(findString);                                            

        if (showNext) {
            findNext = new JButton(nextButtonText);
            findNext.setIconTextGap(0);
            findNext.setBorderPainted(false);  
            findNext.setMargin(new Insets(1, 1, 1, 1));
            findNext.setToolTipText("Find next occurance");
            if (editorPane != null) {
                findNext.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        try {
                            int start = editorPane.getCaretPosition();
                            int found = editorPane.getDocument().getText(0, 
                                         editorPane.getDocument().getLength())
                                          .indexOf(findString.getText(), start);
                            if (found >= 0) {                        
                                editorPane.setSelectionEnd(found);
                                editorPane.setSelectionStart(found);
                                editorPane.setSelectionEnd(
                                         found + findString.getText().length());                            
                            } // end if                                                
                        } catch(Exception ignored) {
                            System.err.println("Exception in find: " + ignored);
                            ignored.printStackTrace();
                        } // end of try .. catch block
                    }
                });
            } else if (editorPaneTA != null) {
                findNext.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        try {
                            int start = editorPaneTA.getCaretPosition();
                            int found = editorPaneTA.getDocument().getText(0, 
                                       editorPaneTA.getText().length()).indexOf(
                                               findString.getText(), start);
                            if (found >= 0) {                        
                                editorPaneTA.setSelectionEnd(found);
                                editorPaneTA.setSelectionStart(found);
                                editorPaneTA.setSelectionEnd(
                                         found + findString.getText().length());                            
                            } // end if                                                
                        } catch(Exception ignored) {
                            System.err.println("Exception in find: " + ignored);
                            ignored.printStackTrace();
                        } // end of try .. catch block
                    }
                });
            } // end if            
            add(findNext);
        } // end if (showNext);

        if (showPrevious) {
            findPrev = new JButton(prevButtonText);
            findPrev.setIconTextGap(0);
            findPrev.setBorderPainted(false);   
            findPrev.setMargin(new Insets(1, 1, 1, 1));
            findPrev.setToolTipText("Find previous occurance");
            if (editorPane != null) {
                findPrev.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        try {                        
                          int start = editorPane.getCaretPosition();
                          int found = editorPane.getDocument().getText(0, start)
                                             .lastIndexOf(findString.getText());                        
                          if (found >= 0) {                            
                              editorPane.setSelectionEnd(found);
                              editorPane.setSelectionStart(found 
                                               + findString.getText().length());
                              editorPane.setSelectionEnd(found);
                          } // end if
                        } catch(Exception ignored) {
                          System.err.println("Exception in find: " + ignored);
                          ignored.printStackTrace();
                        } // end of try .. catch block
                    }
                });
            } else if (editorPaneTA != null) {
                findPrev.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                       try {                        
                        int start = editorPaneTA.getCaretPosition();
                        int found = editorPaneTA.getDocument().getText(0, start)
                                             .lastIndexOf(findString.getText());                        
                        if (found >= 0) {                            
                              editorPaneTA.setSelectionEnd(found);
                              editorPaneTA.setSelectionStart(found 
                                               + findString.getText().length());
                              editorPaneTA.setSelectionEnd(found);
                        } // end if
                       } catch(Exception ignored) {
                        System.err.println("Exception in find: " + ignored);
                        ignored.printStackTrace();
                       } // end of try .. catch block
                    }
                });
            } // end if
            add(findPrev);
        } // end if (showPrevious)
    }
}
