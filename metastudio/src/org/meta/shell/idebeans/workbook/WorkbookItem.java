/*
 * WorkbookItem.java
 *
 * Created on September 3, 2006, 8:57 AM
 */

package org.meta.shell.idebeans.workbook;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;
import javax.swing.*;
import org.meta.common.resource.StringResource;
import org.meta.molecule.Molecule;
import org.meta.shell.ide.MeTA;
import org.meta.shell.idebeans.IDEFileChooser;
import org.meta.shell.idebeans.IDEFileFilter;
import org.meta.shell.idebeans.IDEFileView;
import org.meta.shell.idebeans.MoleculeEditorFrame;
import org.meta.shell.idebeans.MoleculeViewerFrame;
import org.meta.shell.idebeans.eventhandlers.MainMenuEventHandlers;
import org.meta.shell.idebeans.viewers.impl.ImageQuickViewer;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.MoleculeScene;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.MoleculeViewer;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.SelectionState;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.TransformState;
import org.syntax.jedit.JEditTextArea;
import org.syntax.jedit.tokenmarker.BeanShellTokenMarker;

/**
 * An item in a IDEWorkbook instance.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class WorkbookItem extends JPanel {
    
    /** Creates a new instance of WorkbookItem */
    public WorkbookItem() {
        expanded = true;
        
        setLayout(new BorderLayout());
        setBackground(Color.white);
    }

    /** Creates a new instance of WorkbookItem as a text message
      */
    public WorkbookItem(String txtMessage) {        
        this();
        
        textArea = new JTextArea(txtMessage);
        textArea.setEditable(false);
        textArea.setBorder(BorderFactory.createEmptyBorder());
        JScrollPane sp = new JScrollPane(textArea,
                                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sp.setBackground(Color.white);
        sp.setForeground(Color.white);
        sp.setBorder(BorderFactory.createEmptyBorder());
        add(sp);
    }
    
    /** Creates a new instance of WorkbookItem as a text message (which
     * may be a BeanShell script.
      */
    public WorkbookItem(String txtMessage, boolean isBshScript) {        
        this();
        
        if (!isBshScript) {
            textArea = new JTextArea(txtMessage);
            textArea.setEditable(false);
            textArea.setBorder(BorderFactory.createEmptyBorder());
            JScrollPane js = new JScrollPane(textArea,
                                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            add(js);
        } else {
            makeBshPanel(txtMessage);
        } // end if
    }
    
    /** Creates a new instance of WorkbookItem as a embedded Molecule object
      */
    public WorkbookItem(Molecule molecule) {        
        this();
        
        makeMoleculePanel(molecule);
    }

    /** Creates a new instance of WorkbookItem as a embedded Molecule object
      */
    public WorkbookItem(Image image) {        
        this();
        
        makeImagePanel(image);
    }

    /** Factory method for creating specialized WorkbookItem instances.
     *  This one creates a simple text box with a label
     *
     * @param label the label of this text box item
     * @param kl the KeyListener for text box
     * @return instance of WorkbookItem
     */
    public static WorkbookItem createTextBoxWithLabel(String label,
                                                      KeyListener kl) {
        WorkbookItem wbi = new WorkbookItem();

        wbi.setLayout(new BorderLayout());

        JLabel txtLabel = new JLabel(label, JLabel.RIGHT);
        txtLabel.setBorder(BorderFactory.createEmptyBorder());
        txtLabel.setBackground(Color.white);
        wbi.add(txtLabel, BorderLayout.WEST);

        JTextField tf = new JTextField();
        tf.addKeyListener(kl);
        tf.grabFocus();
        tf.setBorder(BorderFactory.createEmptyBorder());
        tf.setBackground(Color.white);
        wbi.add(tf, BorderLayout.CENTER);
        
        return wbi;
    }

    /**
     * extract the widget ID
     */
    private String extractWidgetID(String bshScript) {
        int idx = bshScript.indexOf("setWidgetID(");
        
        if (idx >= 0) {
            int idx1 = bshScript.substring(idx).indexOf("\"");
            int idx2 = bshScript.substring(idx).indexOf("\")");
            
            if ((idx1 >=0 ) && (idx2 >= 0)) {
                return bshScript.substring(idx+idx1+1, idx+idx2);
            } // end if
        } // end if
        
        return null;
    }
    
    /**
     * Saves the currently loaded beanshell script
     */
    private void save(String fileName) {
        try {
            FileOutputStream fos = new FileOutputStream(fileName);

            fos.write(editorPane.getText().getBytes());

            fos.close();                                 
        } catch(Exception e) {
            JOptionPane.showMessageDialog(this, 
                        "Error saving file : " + e.toString(),
                        "Error!", JOptionPane.ERROR_MESSAGE);
        } // end try .. catch block
    }
    
    /**
     * make the beanshell script panel
     */
    private void makeBshPanel(String bshScript) {
        setLayout(new BorderLayout());
        
        editorPane = new JEditTextArea();
        editorPane.setEditable(false);
        editorPane.setBackground(Color.white);
        editorPane.setTokenMarker(new BeanShellTokenMarker());             
        editorPane.setText(bshScript);
        add(editorPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.white);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder());
        JButton saveScript, makePlugin, makeWidget;
        
        saveScript = new JButton("Save Script");
        saveScript.setBackground(Color.white);
        saveScript.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {                                
                IDEFileChooser fileChooser = new IDEFileChooser();
        
                fileChooser.setDialogTitle("Save the BeanShell script file...");

                IDEFileFilter fileFilter = new IDEFileFilter("bsh", 
                                                      "BeanShell script files");
               
                // and add the file filter
                fileChooser.addChoosableFileFilter(fileFilter);
                // and add the iconic stuff
                fileChooser.setFileView(new IDEFileView());

                if (fileChooser.showSaveDialog(WorkbookItem.this)
                     == IDEFileChooser.APPROVE_OPTION) {
                  String theFileName = fileChooser.getSelectedFile()
                                                  .getAbsolutePath();

                  if (!theFileName.endsWith(".bsh")) theFileName += ".bsh";

                  save(theFileName);
                } // end if
            }
        });
        buttonPanel.add(saveScript);
        
        makePlugin = new JButton("Make Plugin");
        makePlugin.setBackground(Color.white);
        makePlugin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String widgetID = extractWidgetID(editorPane.getText());
                
                if (widgetID == null) {
                    // generate a widgetID
                    widgetID = System.currentTimeMillis() + "";
                } // end if
                
                String pluginDir = StringResource.getInstance().getPluginDir();
                
                save(pluginDir + File.separatorChar + widgetID + ".bsh");
            }
        });
        buttonPanel.add(makePlugin);
        
        makeWidget = new JButton("Make Widget");
        makeWidget.setBackground(Color.white);
        makeWidget.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String widgetID = extractWidgetID(editorPane.getText());
                
                if (widgetID == null) {
                    // generate a widgetID
                    widgetID = System.currentTimeMillis() + "";
                } // end if
                
                String widgetDir = StringResource.getInstance().getWidgetsDir();
                
                save(widgetDir + File.separatorChar + widgetID + ".bsh");
            }
        });
        buttonPanel.add(makeWidget);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private Image image;
    
    /**
     * Image panel     
     */
    private void makeImagePanel(Image img) {
        this.image = img;
        
        setLayout(new BorderLayout());
        
        JLabel imageLabel = new JLabel(new ImageIcon(
         WorkbookItem.this.image.getScaledInstance(200, 200, Image.SCALE_FAST)));
        imageLabel.setHorizontalTextPosition(SwingConstants.LEADING);
        imageLabel.setBackground(Color.white);
        imageLabel.setForeground(Color.white);
        imageLabel.setBorder(BorderFactory.createEmptyBorder());
        add(imageLabel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.white);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder());
        
        JButton viewImage = new JButton("View the full image");
        viewImage.setToolTipText("View the image in a viewer");
        viewImage.setBackground(Color.white);
        viewImage.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent ae)  {
             try {  
                ImageQuickViewer qv = new ImageQuickViewer();
                qv.showImage(image);                
             } catch (Exception e) {
                System.err.println("Unable to create quick preview: " 
                                   + e.toString()); 
                e.printStackTrace();
             } // end of try .. catch block
           }
        });
        buttonPanel.add(viewImage);
        
        JButton saveImage = new JButton("Save image to disk");
        saveImage.setToolTipText("Save the current image to disk");
        saveImage.setBackground(Color.white);
        saveImage.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent ae)  {
                // first grab the file name .. and the format
                IDEFileChooser fileChooser = new IDEFileChooser();
                    
                fileChooser.setDialogTitle("Specify name of the image file...");        
                // add the filters
                fileChooser.removeChoosableFileFilter(
                                          fileChooser.getAcceptAllFileFilter());
    
                String [] writerFormats = ImageIO.getWriterFormatNames();
    
                for(String wForamt : writerFormats) {
                  fileChooser.addChoosableFileFilter(
                    new IDEFileFilter(wForamt, wForamt + " image file format"));
                } // end for
                
                if (fileChooser.showSaveDialog(null)
                    == IDEFileChooser.APPROVE_OPTION) {        
                    String fileName = fileChooser.getSelectedFile()
                                                 .getAbsolutePath();
                    String format   = fileChooser.getFileFilter()
                                                 .getDescription();

                    // a BIG HACK.. be careful!
                    format = format.substring(0,
                                    format.indexOf(" image file format"));

                    // make of for the file extension..
                    if (fileName.indexOf(format) < 0) {
                        fileName += "." + format;
                    } // end if
                    
                    try {
                        ImageIO.write((BufferedImage) image, 
                                      format, new File(fileName));
                    } catch (Exception e) {
                        
                    } // end of try .. catch block
                }
           }
        });
        buttonPanel.add(saveImage);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * The molecule panel
     */
    private void makeMoleculePanel(final Molecule molecule) {
        moleculeViewer = new MoleculeViewer(null, null);
        moleculeViewer.setSelectionState(SelectionState.NO_STATE);
        moleculeViewer.setTransformState(TransformState.ROTATE_STATE);
        
        JPanel moleculePanel, detailPanel;
        
        setLayout(new BorderLayout());
        moleculePanel = new JPanel(new BorderLayout());
        moleculePanel.add(moleculeViewer, BorderLayout.CENTER);
           JPanel molButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
           molButtonPanel.setBackground(Color.white);
           JButton openMolInWindow = new JButton("Open molecule "
                   + molecule.getTitle() + " in a new window");
           openMolInWindow.setToolTipText("Click to open molecule object " +
                                          "in a separate window");
           openMolInWindow.setBackground(Color.white);
           openMolInWindow.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent ae)  {
                   MeTA ideInstance = MainMenuEventHandlers
                                          .getInstance(null).getIdeInstance();
                   MoleculeViewerFrame mvf
                                    = new MoleculeViewerFrame(ideInstance);

                   mvf.getMoleculeViewer().disableUndo();
                   mvf.addScene(new MoleculeScene(molecule));
                   mvf.getMoleculeViewer().enableUndo();

                   ideInstance.getWorkspaceDesktop().addInternalFrame(mvf);
               }
           });
           molButtonPanel.add(openMolInWindow);

           JButton editMolInWindow = new JButton("Edit molecule "
                   + molecule.getTitle() + " in a new window");
           editMolInWindow.setToolTipText("Click to open molecule object " +
                                          "in a separate window for editing");
           editMolInWindow.setBackground(Color.white);
           editMolInWindow.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent ae)  {
                   MeTA ideInstance = MainMenuEventHandlers
                                          .getInstance(null).getIdeInstance();
                   MoleculeEditorFrame mef
                                    = new MoleculeEditorFrame(ideInstance);

                   mef.getMoleculeViewer().disableUndo();
                   mef.addScene(new MoleculeScene(molecule));
                   mef.getMoleculeViewer().enableUndo();

                   ideInstance.getWorkspaceDesktop().addInternalFrame(mef);
               }
           });
           molButtonPanel.add(editMolInWindow);

           JCheckBox syncMoleculeObject = new JCheckBox("Sync with original");
           syncMoleculeObject.setBackground(Color.white);
           syncMoleculeObject.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent ae)  {
                    // TODO:
               }
           });
           molButtonPanel.add(syncMoleculeObject);
           
           moleculePanel.add(molButtonPanel, BorderLayout.SOUTH);
        
        detailPanel = new JPanel(new BorderLayout());
        detailPanel.setBackground(Color.white);
           JLabel details = new JLabel("", JLabel.LEFT);
           details.setBackground(Color.white);
           StringBuffer sb = new StringBuffer();
           sb.append("<html><head></head><body>");
           sb.append("<u>General information:</u>");
           sb.append("<table>");
           sb.append("<tr>");
           sb.append("<td>Title: </td>");
           sb.append("<td>" + molecule.getTitle() + "</td>");
           sb.append("</tr>");
           sb.append("<tr>");
           sb.append("<td>Number of Atoms: </td>");
           sb.append("<td>" + molecule.getNumberOfAtoms() + "</td>");
           sb.append("</tr>");
           sb.append("<tr>");
           sb.append("<td>Molecular Formula: </td>");
           sb.append("<td>" + molecule.getFormula().getHTMLFormula() + "</td>");
           sb.append("</tr>");
           sb.append("<tr>");
           sb.append("<td>Number of Electrons: </td>");
           sb.append("<td>" + molecule.getNumberOfElectrons() + "</td>");
           sb.append("</tr>");
           sb.append("<tr>");
           sb.append("<td>Molecular Mass: </td>");
           sb.append("<td>" 
            + (new DecimalFormat("#.#####").format(molecule.getMolecularMass()))
            + "</td>");
           sb.append("</tr>");
           sb.append("</table>");
           sb.append("<br><u>Bond statistics:</u>");
           sb.append("<table>");
           sb.append("<tr>");
           sb.append("<td>Number of Single Bonds: </td>");
           sb.append("<td>" + molecule.getNumberOfSingleBonds() + "</td>");
           sb.append("</tr>");
           sb.append("<tr>");
           sb.append("<td>Number of Multiple Bonds: </td>");
           sb.append("<td>" + molecule.getNumberOfMultipleBonds() + "</td>");
           sb.append("</tr>");
           sb.append("<tr>");
           sb.append("<td>Number of Strong Bonds: </td>");
           sb.append("<td>" + molecule.getNumberOfStrongBonds() + "</td>");
           sb.append("</tr>");
           sb.append("<tr>");
           sb.append("<td>Number of Weak Bonds: </td>");
           sb.append("<td>" + molecule.getNumberOfWeakBonds() + "</td>");
           sb.append("</tr>");
           sb.append("</table>");
           sb.append("</html></body>");
           details.setText(sb.toString());
           detailPanel.add(details, BorderLayout.CENTER);
        add(detailPanel, BorderLayout.WEST);
        
        moleculeViewer.addScene(new MoleculeScene(molecule));
        add(moleculePanel, BorderLayout.CENTER);
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
        
        setBorder(BorderFactory.createTitledBorder(title));
    }

    /**
     * Holds value of property expanded.
     */
    private boolean expanded;

    /**
     * Getter for property expanded.
     * @return Value of property expanded.
     */
    public boolean isExpanded() {
        return this.expanded;
    }

    /**
     * Setter for property expanded.
     * @param expanded New value of property expanded.
     */
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    /**
     * Holds value of property textArea.
     */
    private JTextArea textArea;

    /**
     * Getter for property textArea.
     * @return Value of property textArea.
     */
    public JTextArea getTextArea() {
        return this.textArea;
    }

    /**
     * Holds value of property moleculeViewer.
     */
    private MoleculeViewer moleculeViewer;

    /**
     * Getter for property moleculeViewer.
     * @return Value of property moleculeViewer.
     */
    public MoleculeViewer getMoleculeViewer() {
        return this.moleculeViewer;
    }

    /**
     * Holds value of property editorPane.
     */
    private JEditTextArea editorPane;

    /**
     * Getter for property editorPane.
     * @return Value of property editorPane.
     */
    public JEditTextArea getEditorPane() {
        return this.editorPane;
    }
    
} // end of class WorkbookItem
