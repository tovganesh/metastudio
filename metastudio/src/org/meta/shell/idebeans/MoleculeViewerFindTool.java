/*
 * MoleculeViewerFindTool.java
 *
 * Created on August 31, 2004, 7:57 PM
 */

package org.meta.shell.idebeans;

import java.awt.*;
import java.util.*;
import java.awt.event.*;

import javax.swing.*;
import org.meta.math.geom.BoundingBox;
import org.meta.math.geom.Point3D;
import org.meta.common.Utility;
import org.meta.common.resource.ImageResource;

import org.meta.config.impl.AtomInfo;
import org.meta.math.Matrix3D;
import org.meta.math.interpolater.InterpolaterFactory;
import org.meta.molecule.Atom;
import org.meta.molecule.BondType;
import org.meta.molecule.Molecule;
import org.meta.molecule.property.electronic.GridProperty;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.MoleculeScene;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.graphicsengine.surfaces.PropertyScene;

/**
 * A customised find tool for the molecule viewer for selecting, higlighting 
 * and adding trackers (like distance, angle etc.) using a simple comma 
 * separated language. A few examples are given below: <br>
 * <ul>
 * <li> <u>33</u>: Finds, heighlights and lables atom numbered 33
 * <li> <u>33,34</u>: Finds, heighlights and lables atom numbered 33 and 34
 * <li> <u>33,34,dist</u>: Finds, heighlights and lables atom numbered 33 and 34
 *                         and then adds a distance tracker between 33 and 34
 * <li> <u>33,34,35,ang</u>: Finds, heighlights and lables atom numbered 33 
 *                           34 and 35, and then adds an angle tracker for the 
 *                           three atoms.
 * <li> <u>33,34,35,36,dihed</u>: Finds, heighlights and lables atom numbered 33 
 *                           34, 35 and 36 and then adds a dihedral tracker for 
 *                           the four atoms.
 * </ul>
 * <br>
 * P.S.: Can only be used in conjugation with MoleculeViewerFrame class.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MoleculeViewerFindTool extends JPanel {
    
    private MoleculeViewerFrame moleculeViewerFrame;
    
    private JComboBox findInput, findTargetScene;
    private JCheckBox zoomToFindOption;
    private JLabel findInputLabel, findTargetSceneLabel;
    
    private NotificationTray findToolNotifiation;
    
    private FindLanguageInterpreter findInterpreter;

    private boolean zoomToFind;
    
    /** Creates a new instance of MoleculeViewerFindTool */
    public MoleculeViewerFindTool(MoleculeViewerFrame moleculeViewerFrame) {
        this.moleculeViewerFrame = moleculeViewerFrame;
        
        initComponents();
        
        findInterpreter = new FindLanguageInterpreter();
        zoomToFind = true;
    }
    
    /**
     * Initilize the components associated with this find tool
     */
    private void initComponents() {
        ImageResource images = ImageResource.getInstance();
                
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0.0;
        setBorder(BorderFactory.createLineBorder(Color.blue, 1));        
        
        findInputLabel = new JLabel("Find :");
        findInputLabel.setDisplayedMnemonic('F');
        gbc.gridx = gbc.gridy = 0;
        add(findInputLabel, gbc);
                
        findInput = new JComboBox();
        findInput.setEditable(true);
        findInput.setPrototypeDisplayValue("Molecule Molecule");
        findInput.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent ae) {
               if (ae.getActionCommand().equals("comboBoxEdited")
                   && !findInterpreter.isInErrorState()) {
                  String newFindString = (String) findInput.getSelectedItem();
                  
                  if (((DefaultComboBoxModel) findInput.getModel())
                                   .getIndexOf(newFindString) < 0) {
                    findInput.addItem(newFindString);
                  } // end if
               } else if (ae.getActionCommand().equals("comboBoxChanged")) {
                  findInterpreter.execute((String) findInput.getSelectedItem());
               } // end if
           }
        }); // addActionListener        
        findInputLabel.setLabelFor(findInput);
        findInput.setToolTipText("<html><head></head><body><pre>"
                        + "Enter your find string here. \n"
                        + "- Atom indices and keywords separated by ',' \n"
                        + "- Atom indices range from 0 to noOfAtoms-1 \n"
                        + "- dist (distace), ang (angle), dihed (dihedral) etc."
                        + "</pre></body></html>");
        gbc.gridx = 1;
        gbc.gridwidth = 4;
        gbc.weightx = 1.0;
        add(findInput, gbc);
        
        findTargetSceneLabel = new JLabel(" in :");
        findTargetSceneLabel.setDisplayedMnemonic('i');
        gbc.gridx = 5;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        add(findTargetSceneLabel, gbc);
                
        findTargetScene = new JComboBox();
        findTargetScene.setEditable(false);
        findTargetScene.setPrototypeDisplayValue("Molecule Molecule");
        findTargetSceneLabel.setLabelFor(findTargetScene); 
        findTargetScene.setToolTipText("Choose the target molecule scene here");
        gbc.gridx = 6;
        gbc.gridwidth = 2;
        add(findTargetScene, gbc);

        findToolNotifiation = new NotificationTray(moleculeViewerFrame);
        findToolNotifiation.setTrayIcon(images.getInform());
        gbc.gridx = 8;
        gbc.gridwidth = 1;
        add(findToolNotifiation, gbc);
        
        zoomToFindOption = new JCheckBox("Zoom to find!");
        zoomToFindOption.setToolTipText("Select this option to enable zooming" +
               " of the molecule to the place queried for in the find dialog.");
        zoomToFindOption.setSelected(true);
        zoomToFindOption.setMnemonic('Z');
        zoomToFindOption.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent ae) {
                zoomToFind = zoomToFindOption.isSelected();
           }
        });
        gbc.gridx = 9;
        gbc.gridwidth = 1;
        add(zoomToFindOption, gbc);
    }
    
    /**
     * Update the find target scene list
     */
    public void updateFindTragetSceneList() {
        Iterator<MoleculeScene> scenes = 
                moleculeViewerFrame.getSceneList().iterator();
        
        findTargetScene.removeAllItems();
        
        while(scenes.hasNext()) {
            findTargetScene.addItem(scenes.next());
        } // end while
        
        // add a special entry
        findTargetScene.addItem("all molecules");
    }
    
    /**
     * Inner class for handling the find language
     */
    protected class FindLanguageInterpreter {
        
        /** the line to be parsed */
        private String line;
        
        // the keywords...
        
        protected final static String DISTANCE_KEYWORD      = "dist";
        
        protected final static String ANGLE_KEYWORD         = "ang";
        
        protected final static String DIHEDRAL_KEYWORD      = "dihed";
        
        protected final static String BOND_KEYWORD          = "bond";
        
        protected final static String CONN_KEYWORD          = "conn";
        
        protected final static String NEAR_KEYWORD          = "near";
        
        protected final static String PROP_KEYWORD          = "prop";
        
        protected final static String SUB_VOL_KEYWORD       = "subvol";
        
        protected final static String INTERP_KEYWORD        = "interp";
        
        protected final static String SHOW_ALL_KEYWORD      = "showall";
        
        protected final static String SHOW_ONLY_KEYWORD     = "showonly";
        
        protected final static String HIDE_ALL_KEYWORD      = "hideall";
        
        protected final static String SHOW_KEYWORD          = "show";
        
        protected final static String HIDE_KEYWORD          = "hide";
        
        protected final static String FORMULA_KEYWORD       = "formula";
        
        protected final static String ATOM_COUNT_KEYWORD    = "atomcount";
        
        protected final static String ELEC_COUNT_KEYWORD    = "eleccount";
        
        protected final static String PICK_KEYWORD          = "pick";

        protected final static String CLEAR_PICK_KEYWORD    = "clearpick";

        protected final static String INVERT_PICK_KEYWORD   = "invertpick";

        protected final static String HIDE_UNPICKED_KEYWORD = "hideunpicked";
        
        protected final static String LIST_KEYWORD          = "list";
        
        /**
         * the keyword command mapping goes here
         */
        protected HashMap<String, FindKeywordCommand> keywordCommandMap; 
        
        /**
         * Holds value of property inErrorState.
         */
        private boolean inErrorState;
        
        /** Creates a new instance of FindLanguageInterpreter */
        public FindLanguageInterpreter() {
            line = "";
            inErrorState = false;
            
            // init the keywordCommandMap 
            keywordCommandMap = new HashMap<String, FindKeywordCommand>(10);
            
            keywordCommandMap.put(DISTANCE_KEYWORD, 
                                  new DistanceFindKeywordCommand());
            keywordCommandMap.put(ANGLE_KEYWORD, 
                                  new AngleFindKeywordCommand());
            keywordCommandMap.put(DIHEDRAL_KEYWORD,
                                  new DihedralFindKeywordCommand());
            keywordCommandMap.put(BOND_KEYWORD,
                                  new BondFindKeywordCommand());
            keywordCommandMap.put(CONN_KEYWORD,
                                  new ConnFindKeywordCommand());
            keywordCommandMap.put(NEAR_KEYWORD,
                                  new NearAdvancedFindKeywordCommand());
            keywordCommandMap.put(PROP_KEYWORD,
                                  new PropertyAdvancedFindKeywordCommand());
            keywordCommandMap.put(SUB_VOL_KEYWORD,
                                  new SubVolAdvancedFindKeywordCommand());
            keywordCommandMap.put(INTERP_KEYWORD,
                           new InterpolatePropertyAdvancedFindKeywordCommand());
            keywordCommandMap.put(SHOW_ALL_KEYWORD,
                           new ShowAllAdvancedFindKeywordCommand());
            keywordCommandMap.put(SHOW_ONLY_KEYWORD,
                           new ShowOnlyAdvancedFindKeywordCommand());
            keywordCommandMap.put(HIDE_ALL_KEYWORD,
                           new HideAllAdvancedFindKeywordCommand());
            keywordCommandMap.put(ATOM_COUNT_KEYWORD,
                           new AtomCountAdvancedFindKeywordCommand());
            keywordCommandMap.put(ELEC_COUNT_KEYWORD,
                           new ElectronCountAdvancedFindKeywordCommand());
            keywordCommandMap.put(FORMULA_KEYWORD,
                           new FormulaAdvancedFindKeywordCommand());
            keywordCommandMap.put(SHOW_KEYWORD,
                           new ShowFindKeywordCommand());
            keywordCommandMap.put(HIDE_KEYWORD,
                           new HideFindKeywordCommand());
            keywordCommandMap.put(PICK_KEYWORD,
                           new PickKeywordCommand());
            keywordCommandMap.put(CLEAR_PICK_KEYWORD,
                           new ClearPickKeywordCommand());
            keywordCommandMap.put(INVERT_PICK_KEYWORD,
                           new InvertPickKeywordCommand());
            keywordCommandMap.put(HIDE_UNPICKED_KEYWORD,
                           new HideUnPickedKeywordCommand());
        }
        
        /**
         * A quite executer, all information is automatically logged into the 
         * notification tray.
         */
        public void execute(String line) {
            inErrorState = true;
            
            if ((line == null) || line.equals("")) return;
            
            this.line = line;
            
            // split the line based on ',' the command separator
            String [] orgWords = line.split(",");
            
            if (orgWords.length == 0) return;                                                
                
            // pre process to check if we have "list" keyword             
            
            if (orgWords[orgWords.length-1].indexOf(LIST_KEYWORD) < 0) {              
                processFindStatement(orgWords);
            } else {
                // else we have a compount list keyword
                // that needs to be processed for generating the
                // actual Find statements
                ArrayList<String> words = new ArrayList<String>();
                for(int i=0; i<orgWords.length-1; i++) {
                    String theWord = orgWords[i];
                    if (keywordCommandMap.get(theWord) != null) {
                        words.add(theWord);

                        String [] w = new String[words.size()];
                        for(int j=0; j<w.length; j++)
                            w[j] = words.get(j);

                        words.clear();
                        processFindStatement(w);
                    } else {
                        words.add(theWord);
                    } // end if
                } // end for
                
                // process the remaining stuff
                if (words.size() > 0) {
                    String [] w = new String[words.size()];
                    for(int j=0; j<w.length; j++)
                        w[j] = words.get(j);

                    words.clear();
                    processFindStatement(w);
                } // end if
            } // end if
        }  
        
        /**
         * do actual processing of the Find statement
         * 
         * @param words the words on the stack
         */
        private void processFindStatement(String [] words) {
            inErrorState = false;
            
            Stack<Integer> indexStack = new Stack<Integer>();
            Stack<Double>  valueStack = new Stack<Double>();
            Stack<String> stringStack = new Stack<String>();
            String keyword   = "";
            FindKeywordCommand keywordCommand = null;
            Integer theIndex; Double theValue;
            boolean isKeyword = false;
            ArrayList<MoleculeScene> targetScenes 
                      = new ArrayList<MoleculeScene>();
            
            if (findTargetScene.getSelectedItem() instanceof MoleculeScene) {
                targetScenes.add((MoleculeScene) 
                                  findTargetScene.getSelectedItem());
            } else if (findTargetScene.getSelectedItem() instanceof String){
                // add all the scenes
                for(MoleculeScene scene : moleculeViewerFrame.getSceneList()) {
                    targetScenes.add(scene);
                } // end for
            } // end if
            
            // for all target scenes!!!
            // begin a compound edit for molecular viewer
            moleculeViewerFrame.getMoleculeViewer().beginCompoundEdit();
            
            // TODO: may be this could be more effitient
            for(MoleculeScene targetScene : targetScenes) {
                // clear the stack
                indexStack.clear();
                valueStack.clear();
                stringStack.clear();
                
                // and then scan through each word
                for(int i=0; i<words.length; i++) {
                    words[i] = words[i].trim();
                    
                    try {
                        theIndex = new Integer(words[i]);
                        
                        // check for correctness of index
                        if ((theIndex.intValue() < 0)
                        || (theIndex.intValue() > (targetScene.getMolecule()
                                                      .getNumberOfAtoms()-1))) {
                            logWarning("Atom having index " + theIndex
                                    + " not found in : " + targetScene);
                            return;
                        } // end if
                        
                        // check if index is duplicate?
                        if (indexStack.contains(theIndex)) {
                            logWarning("Duplicate index " + theIndex
                                    + " found in : " + line);
                            return;
                        } // end if
                        
                        // now its safe to add it in stack
                        indexStack.push(theIndex);
                    } catch (NumberFormatException nfe) {
                        // if the control reaches here, 
                        // there could be two reasons
                        // 1. this may be a value (real number)
                        // 2. there is an error
                        // 3. the word may be a key word
                        
                        // is it a value?
                        try {
                            theValue = new Double(words[i]); 
                            valueStack.push(theValue);
                        } catch (NumberFormatException ne) {
                            // .. then check if that is a key word 
                            keywordCommand = keywordCommandMap.get(words[i]);

                            if (keywordCommand == null) {
                                if (i == words.length-1) {
                                    // error in keyword definition
                                    logError("Error in find specification '" 
                                             + line + "' near : " + words[i]);
                                    return;
                                } else {
                                    stringStack.push(
                                                Utility.capitalise(words[i]));
                                } // end if
                            } else {
                                keyword   = words[i];
                                isKeyword = true;
                                break; // we have reached a keyword, process it!
                            } // end if
                        } // end try .. catch block
                    } // end of try catch
                } // end for                                
                
                // if we reached here, check if we are doing a keyword find?
                if (isKeyword) {
                    try {
                        // execute the keyword command
                        if (stringStack.size() != 0) {
                            ((AdvancedFindKeywordCommand) keywordCommand)
                                .execute(stringStack, valueStack,
                                         indexStack, targetScene);
                        } else if (valueStack.size() != 0) {
                            ((AdvancedFindKeywordCommand) keywordCommand)
                                .execute(valueStack, indexStack, targetScene);                          
                        } else if (indexStack.size() != 0) {
                            keywordCommand.execute(indexStack, targetScene);
                        } else {
                            ((AdvancedFindKeywordCommand) keywordCommand)
                                .execute(stringStack, valueStack,
                                         indexStack, targetScene);
                        } // end if

                        moleculeViewerFrame.repaint();
                    } catch (InvalidArgumentNumbersException iae) {
                        logError("Error in find specification '" + line
                                + "'.\n Keyword : " + keyword + " " + iae);
                        // end the compound edit for the molecular viewer
                        moleculeViewerFrame.getMoleculeViewer()
                                           .endCompoundEdit();
                        return;
                    } catch (Exception e) {
                        logError("Unexpected error occured : \n" 
                                  + e.toString());
                        // end the compound edit for the molecular viewer
                        moleculeViewerFrame.getMoleculeViewer()
                                           .endCompoundEdit();
                        e.printStackTrace();
                        
                        return;
                    } // end of try..catch block
                } // end if
                
                // no stack? return
                if ((indexStack.size() != 0) 
                    && !(keywordCommand instanceof ShowFindKeywordCommand)
                    && !(keywordCommand instanceof HideFindKeywordCommand)
                    && !(keywordCommand instanceof PickKeywordCommand)) {
                    // TODO: more efficient
                    
                    // first bring the atoms under consideration to 
                    // center stage!
                    bringToCenterStage(indexStack, targetScene);

                    // then select them .. symbols
                    moleculeViewerFrame.getMoleculeViewer()
                               .setIndexSelectionStack(indexStack, targetScene);
                    moleculeViewerFrame.getMoleculeViewer().getPopup()
                               .addAtomSymbolCaptions(true);

                    // then select them .. indices
                    moleculeViewerFrame.getMoleculeViewer()
                               .setIndexSelectionStack(indexStack, targetScene);
                    moleculeViewerFrame.getMoleculeViewer().getPopup()
                               .addAtomIndexCaptions(true);
                } // end if                                
            } // end for
            
            // end the compound edit for the molecular viewer
            moleculeViewerFrame.getMoleculeViewer().endCompoundEdit();
        }
        
        /**
         * bring the current atoms on the stack to viewable center stage.
         */
        private void bringToCenterStage(Stack<Integer> indexStack, 
                                        MoleculeScene scene) {
            if (!zoomToFind) return;  // do we need to zoom ?

            // build a bounding box of the atom indices on stack 
            Molecule molecule = scene.getMolecule();
            Point3D point = (molecule.getAtom(indexStack.peek()))
                                         .getAtomCenter();
            
            BoundingBox bb = new BoundingBox(point, point);
            
            Point3D newPoint;
            BoundingBox newBB;
            
            // stack is aranged reverse, so 0 to n-1 elements should be parsed
            for(int i=0; i<indexStack.size()-1; i++) {
                newPoint = (molecule.getAtom(indexStack.get(i)))
                                        .getAtomCenter();
                newBB = new BoundingBox(newPoint, newPoint);
                
                bb = bb.combine(newBB);
            } // end for                                    
                            
            Dimension d = moleculeViewerFrame.getMoleculeViewer().getSize();

            // scale the bounding box to 75% of the screen !?
            double toScale = 1.0;

            if (!bb.getBottomRight().equals(bb.getUpperLeft())) {
                // if more than one atoms are selected...
                BoundingBox bbBig = 
                  moleculeViewerFrame.getMoleculeViewer().getBoundingBox();

                if (bb.getXWidth() != 0) 
                    toScale = bbBig.getXWidth() / bb.getXWidth();

                if (bb.getYWidth() != 0) 
                    toScale = Math.min(toScale,
                                       bbBig.getYWidth() / bb.getYWidth());

                if (bb.getZWidth() != 0) 
                    toScale = Math.min(toScale,
                                       bbBig.getZWidth() / bb.getZWidth());
            } else { // if only one atom is selected!
                toScale = Math.abs(Math.min(
                  d.getWidth()  / bb.getBottomRight().getX(),
                  d.getHeight() / bb.getBottomRight().getY()
                ) * 0.25);
            } // end if                                

            moleculeViewerFrame.getMoleculeViewer()
                               .setUndoableScaleFactor(toScale);

            moleculeViewerFrame.getMoleculeViewer().updateTransforms();

            // transform the center, with the current transformation 
            // matrix of the molecule viewer
            point =  bb.center();                
            newPoint = moleculeViewerFrame.getMoleculeViewer()
                                          .getTransform().transform(point);

            // then translate to the center of bounding box
            Matrix3D toTranslate = (Matrix3D) 
                                    moleculeViewerFrame.getMoleculeViewer()
                                             .getTranslateMatrix().clone();
            toTranslate.translate((d.getWidth()/2-newPoint.getX()),
                                  (d.getHeight()/2-newPoint.getY()),
                                  0.0);
            moleculeViewerFrame.getMoleculeViewer()
                               .setUndoableTranslateMatrix(toTranslate);            
        }
        
        /**
         * log error into notification tray
         */
        private void logError(String message) {
            System.out.println("Error in find specification!");
            findToolNotifiation.notify("Error!", 
                                       message, true, NotificationType.ERROR); 
            inErrorState = true;
        }
        
        /**
         * log warning into notification tray
         */
        private void logWarning(String message) {
            System.out.println("Ambiguity in find specification!");
            findToolNotifiation.notify("Warning!", 
                                       message, true, NotificationType.WARN); 
            inErrorState = true;
        }
        
        /**
         * Getter for property errorState.
         * @return Value of property errorState.
         */
        public boolean isInErrorState() {
            return this.inErrorState;
        }
        
        /**
         * Setter for property errorState.
         * @param inErrorState New value of property inErrorState.
         */
        public void setInErrorState(boolean inErrorState) {
            this.inErrorState = inErrorState;
        }
        
    } // end of class FindLanguageParse
    
    /**
     * Inner interface for defining a command for the find language keyword
     */
    protected interface FindKeywordCommand {
        /**
         * ececute the action for the command using the index stack ... the
         * argument stack
         *
         * @param indexStack the argument stack
         * @param targetScene the target MoleculeScene
         * @throws InvalidArgumentNumbersException if the number of arguments
         *         do not match that of those required by the command
         */
        public void execute(Stack<Integer> indexStack, 
                            MoleculeScene targetScene) 
                            throws InvalidArgumentNumbersException;
    } // end of interface FindKeywordCommand
    
    /**
     * Inner interface for defining a some advanced (fuzzy) commands 
     * for the find language keyword
     */
    protected abstract class AdvancedFindKeywordCommand 
                                     implements FindKeywordCommand {        
        /**
         * ececute the action for the command using the index stack ... the
         * argument stack
         *
         * @param valueStack the argument stack
         * @param indexStack another set of argument stack
         * @param targetScene the target MoleculeScene
         * @throws InvalidArgumentNumbersException if the number of arguments
         *         do not match that of those required by the command
         */
        public abstract void execute(Stack<Double> valueStack, 
                            Stack<Integer> indexStack, 
                            MoleculeScene targetScene) 
                            throws InvalidArgumentNumbersException;
        
        /**
         * executes the action for a command using the strig stack ... as the 
         * argument stack
         * 
         * @param valueStack - the string value stack
         * @param dValueStack the double value stack
         * @param indexStack another set of argument stack
         * @param targetScene the target MoleculeScene
         * @throws InvalidArgumentNumbersException if the number of arguments
         *         do not match that of those required by the command
         */
        public abstract void execute(Stack<String> valueStack,
                            Stack<Double> dValueStack,
                            Stack<Integer> indexStack,
                            MoleculeScene targetScene) 
                            throws InvalidArgumentNumbersException;
    } // end of interface AdvancedFindKeywordCommand
    
    /**
     * The inner class defines how the keyword <code><b>dist</b></code> 
     * is processed.
     */
    protected class DistanceFindKeywordCommand implements FindKeywordCommand {
        
        /** Creates a new instance of DistanceFindKeywordCommand */
        public DistanceFindKeywordCommand() { }
        
        /**
         * Handling the <code><b>dist</b></code> keyword
         */
        @Override
        public void execute(Stack<Integer> indexStack, 
                            MoleculeScene targetScene) 
                            throws InvalidArgumentNumbersException {
            if (indexStack.size() == 2) {
                moleculeViewerFrame.getMoleculeViewer()
                              .setIndexSelectionStack(indexStack, targetScene);
                moleculeViewerFrame.getMoleculeViewer()
                                   .getPopup().addTrackers();
            } else {
                throw new InvalidArgumentNumbersException(
                                            "requires exactly two arguments.");
            } // end if
        }                
    } // end of class DistanceFindKeywordCommand
    
    /**
     * The inner class defines how keyword <code><b>ang</b></code> 
     * is processed.
     */
    protected class AngleFindKeywordCommand implements FindKeywordCommand {
        
        /** Creates a new instance of AngleFindKeywordCommand */
        public AngleFindKeywordCommand() { }
        
        /**
         * Handling the <code><b>ang</b></code> keyword
         */
        @Override
        public void execute(Stack<Integer> indexStack, 
                            MoleculeScene targetScene) 
                            throws InvalidArgumentNumbersException {
            if (indexStack.size() == 3) {                
                moleculeViewerFrame.getMoleculeViewer()
                              .setIndexSelectionStack(indexStack, targetScene);
                moleculeViewerFrame.getMoleculeViewer()
                                   .getPopup().addTrackers();
            } else {
                throw new InvalidArgumentNumbersException(
                                           "requires exactly three arguments.");
            } // end if
        }                
    } // end of class AngleFindKeywordCommand
    
    /**
     * The inner class defines how keyword <code><b>dihed</b></code> 
     * is processed.
     */
    protected class DihedralFindKeywordCommand implements FindKeywordCommand {
        
        /** Creates a new instance of DihedralFindKeywordCommand */
        public DihedralFindKeywordCommand() { }
        
        /**
         * Handling the <code><b>dihed</b></code> keyword
         */
        @Override
        public void execute(Stack<Integer> indexStack, 
                            MoleculeScene targetScene) 
                            throws InvalidArgumentNumbersException {
            if (indexStack.size() == 4) {
                moleculeViewerFrame.getMoleculeViewer()
                              .setIndexSelectionStack(indexStack, targetScene);
                moleculeViewerFrame.getMoleculeViewer()
                                   .getPopup().addTrackers();
            } else {
                throw new InvalidArgumentNumbersException(
                                           "requires exactly four arguments.");
            } // end if
        }                
    } // end of class DihedralFindKeywordCommand
    
    /**
     * The inner class defines how keyword <code><b>bond</b></code> 
     * is processed.
     */
    protected class BondFindKeywordCommand implements FindKeywordCommand {
                
        /** Creates a new instance of BondFindKeywordCommand */
        public BondFindKeywordCommand() { }
        
        /**
         * Handling the <code><b>bond</b></code> keyword
         */
        @Override
        public void execute(Stack<Integer> indexStack, 
                            MoleculeScene targetScene) 
                            throws InvalidArgumentNumbersException {
            if (indexStack.size() == 2) {
                Molecule molecule = targetScene.getMolecule();
                int atomIndex1 = ((Integer) indexStack.get(0)).intValue();
                int atomIndex2 = ((Integer) indexStack.get(1)).intValue();
                
                BondType bondType = molecule.getBondType(atomIndex1,
                                                         atomIndex2);
                String message = "";
                
                String atomString = molecule.getAtom(atomIndex2).getSymbol()
                                    + "(" + atomIndex2 + ") and "
                                    + molecule.getAtom(atomIndex1).getSymbol()
                                    + "(" + atomIndex1 + ") ";
                
                if (bondType.equals(BondType.NO_BOND)) {
                    message = "There is no direct bond detected between "
                              + atomString + " in " + targetScene;
                } else { 
                    message = "Atoms " + atomString
                              + " are bonded by a " + bondType
                              + " in " + targetScene;
                } // end if
                
                // show the message
                findToolNotifiation.notify("Bond Information", message, true,
                                           5000, NotificationType.INFO);
            } else {
                throw new InvalidArgumentNumbersException(
                                           "requires exactly two arguments.");
            } // end if
        }                
    } // end of class BondFindKeywordCommand
    
    /**
     * The inner class defines how keyword <code><b>conn</b></code> 
     * is processed.
     */
    protected class ConnFindKeywordCommand implements FindKeywordCommand {
                
        /** Creates a new instance of ConnFindKeywordCommand */
        public ConnFindKeywordCommand() { }
        
        /**
         * Handling the <code><b>conn</b></code> keyword
         */
        @Override
        public void execute(Stack<Integer> indexStack, 
                            MoleculeScene targetScene) 
                            throws InvalidArgumentNumbersException {
            if (indexStack.size() == 1) {
                Molecule molecule = targetScene.getMolecule();
                int atomIndex = ((Integer) indexStack.peek()).intValue();
                
                Hashtable<Integer, BondType> conns = molecule.getAtom(atomIndex)
                                                            .getConnectedList();
                
                // pump the conneceted atoms into the indexStack so that they
                // are also braught to the center stage
                Enumeration<Integer> connAtms = conns.keys();
                while(connAtms.hasMoreElements())
                    indexStack.push(connAtms.nextElement());
                
                // and the do the informing
                String message = "";
                
                String atomMessage = molecule.getAtom(atomIndex).getSymbol()
                                     + "(" + atomIndex + ") has ";
                
                if (conns.size() == 0) {
                    message = atomMessage + "no connected atom in " 
                              +  targetScene + ".";
                } else if (conns.size() == 1) {
                    message = atomMessage + "1 connected atom in " 
                              +  targetScene
                              + ". It is: \n" + conns.toString();
                } else { // the size is supposed to be +ve!
                    message = atomMessage + conns.size()
                              + " connected atoms in " +  targetScene
                              + ". They are: \n" + conns.toString();
                } // end if
                
                // show the message
                findToolNotifiation.notify("Connection Information", message, 
                                           true, 5000, NotificationType.INFO);
            } else {
                throw new InvalidArgumentNumbersException(
                                           "requires exactly one arguments.");
            } // end if
        }                
    } // end of class BondFindKeywordCommand
    
    /**
     * Thrown when there are invalid number of arguments for an find keyword
     * command.
     */
    protected class InvalidArgumentNumbersException extends Exception {
        
        private String msg;
        
        /** creates a new instance of InvalidArgumentNumbersException */
        public InvalidArgumentNumbersException(String msg) {
            super(msg);
            
            this.msg = msg;
        }
        
        /**
         * overridden toString() method
         */
        @Override
        public String toString() {
            return msg;
        }
    } // end of class InvalidArgumentNumbersException
    
    /**
     * The inner class defines how keyword <code><b>near</b></code> 
     * is processed.
     */
    protected class NearAdvancedFindKeywordCommand 
                             extends AdvancedFindKeywordCommand {
        /** Creates a new instance of NearAdvancedFindKeywordCommand */
        public NearAdvancedFindKeywordCommand() { }
        
        /**
         * ececute the action for the command using the index stack ... the
         * argument stack
         *
         * @param indexStack the argument stack
         * @param targetScene the target MoleculeScene
         * @throws InvalidArgumentNumbersException if the number of arguments
         *         do not match that of those required by the command
         */
        @Override
        public void execute(Stack<Integer> indexStack, 
                            MoleculeScene targetScene) 
                            throws InvalidArgumentNumbersException {
               throw new InvalidArgumentNumbersException("Method not " +
                                                         "implemented!");
        }
        
        /**
         * ececute the action for the command using the index stack ... the
         * argument stack
         *
         * @param valueStack the argument stack
         * @param indexStack another set of argument stack
         * @param targetScene the target MoleculeScene
         * @throws InvalidArgumentNumbersException if the number of arguments
         *         do not match that of those required by the command
         */
        @Override
        public void execute(Stack<Double> valueStack, 
                            Stack<Integer> indexStack, 
                            MoleculeScene targetScene) 
                            throws InvalidArgumentNumbersException {
            if (valueStack.size() == 3) {
                Molecule molecule = targetScene.getMolecule();
                double minDist =  Double.MAX_VALUE, dist=0.0; int minIndex = -1;
                Point3D point = new Point3D(valueStack.get(0), 
                                            valueStack.get(1), 
                                            valueStack.get(2));
                Iterator<Atom> atoms = molecule.getAtoms();
                
                for(Atom atom=null;atoms.hasNext();) {
                    atom = atoms.next();
                    dist = atom.distanceFrom(point);
                    
                    if (dist < minDist) {
                        minDist = dist;
                        minIndex = atom.getIndex();
                    } // end if
                } // end for
                
                String message = "";
                if (minIndex != -1) {
                    message = "Nearest atom found near (" + point + ") is: " 
                              + molecule.getAtom(minIndex) + " with index "
                              + minIndex;
                } else {
                    message = "No atom found in the proximity of the point: "
                              + point;
                } // end if
                
                // show the message
                findToolNotifiation.notify("Nearest Atom", message, 
                                           true, 5000, NotificationType.INFO);
            } else {
                throw new InvalidArgumentNumbersException(
                                           "requires exactly three arguments.");
            } // end if
        }

        /**
         * executes the action for a command using the strig stack ... as the 
         * argument stack
         * 
         * @param valueStack - the string value stack
         * @param dValueStack the double value stack
         * @param indexStack another set of argument stack
         * @param targetScene the target MoleculeScene
         * @throws InvalidArgumentNumbersException if the number of arguments
         *         do not match that of those required by the command
         */
        @Override
        public void execute(Stack<String> valueStack,
                            Stack<Double> dValueStack,
                            Stack<Integer> indexStack,
                            MoleculeScene targetScene) 
                throws MoleculeViewerFindTool.InvalidArgumentNumbersException {
            throw new InvalidArgumentNumbersException("Method not " +
                                                         "implemented!");
        }
    }

    /**
     * The inner class defines how keyword <code><b>prop</b></code> 
     * is processed.
     */
    protected class PropertyAdvancedFindKeywordCommand 
                                 extends AdvancedFindKeywordCommand {
        /** Creates a new instance of PropertyAdvancedFindKeywordCommand */
        public PropertyAdvancedFindKeywordCommand() { }
        
        /**
         * ececute the action for the command using the index stack ... the
         * argument stack
         *
         * @param indexStack the argument stack
         * @param targetScene the target MoleculeScene
         * @throws InvalidArgumentNumbersException if the number of arguments
         *         do not match that of those required by the command
         */
        @Override
        public void execute(Stack<Integer> indexStack, 
                            MoleculeScene targetScene) 
                            throws InvalidArgumentNumbersException {
               throw new InvalidArgumentNumbersException("Method not " +
                                                         "implemented!");
        }
        
        /**
         * ececute the action for the command using the index stack ... the
         * argument stack
         *
         * @param valueStack the argument stack
         * @param indexStack another set of argument stack
         * @param targetScene the target MoleculeScene
         * @throws InvalidArgumentNumbersException if the number of arguments
         *         do not match that of those required by the command
         */
        @Override
        public void execute(Stack<Double> valueStack, 
                            Stack<Integer> indexStack, 
                            MoleculeScene targetScene) 
                            throws InvalidArgumentNumbersException {            
            if ((indexStack.size() == 1) && (valueStack.size() == 3)) {
                Iterator<PropertyScene> ps = targetScene.getAllPropertyScene();
                PropertyScene scene = null;
                
                scene=ps.next();
                for(int i=1; i<indexStack.get(0);i++)
                    scene=ps.next();
                
                if (scene == null) {
                    throw new InvalidArgumentNumbersException(
                                           "invalid property scene number.");
                } // end if
                
                double value = scene.getGridProperty().getFunctionValueAt(
                                                           valueStack.get(0), 
                                                           valueStack.get(1), 
                                                           valueStack.get(2));
                String message = "Value near (" + valueStack.get(0)
                                              + ", " + valueStack.get(1)
                                              + ", " + valueStack.get(2)
                                              + ") is: " + value;
                
                // show the message
                findToolNotifiation.notify("Property Value Information", message, 
                                           true, 5000, NotificationType.INFO);
            } else {
                throw new InvalidArgumentNumbersException(
                                           "requires exactly four arguments.");
            } // end if
        }
        
        /**
         * executes the action for a command using the strig stack ... as the 
         * argument stack
         * 
         * @param valueStack - the string value stack
         * @param dValueStack the double value stack
         * @param indexStack another set of argument stack
         * @param targetScene the target MoleculeScene
         * @throws InvalidArgumentNumbersException if the number of arguments
         *         do not match that of those required by the command
         */
        @Override
        public void execute(Stack<String> valueStack,
                            Stack<Double> dValueStack,
                            Stack<Integer> indexStack,
                            MoleculeScene targetScene) 
                throws MoleculeViewerFindTool.InvalidArgumentNumbersException {
            throw new InvalidArgumentNumbersException("Method not " +
                                                         "implemented!");
        }
    }
    
    /**
     * The inner class defines how keyword <code><b>subvol</b></code> 
     * is processed.
     */
    protected class SubVolAdvancedFindKeywordCommand 
                                 extends AdvancedFindKeywordCommand {
        /** Creates a new instance of SubVolAdvancedFindKeywordCommand */
        public SubVolAdvancedFindKeywordCommand() { }
        
        /**
         * ececute the action for the command using the index stack ... the
         * argument stack
         *
         * @param indexStack the argument stack
         * @param targetScene the target MoleculeScene
         * @throws InvalidArgumentNumbersException if the number of arguments
         *         do not match that of those required by the command
         */
        @Override
        public void execute(Stack<Integer> indexStack, 
                            MoleculeScene targetScene) 
                            throws InvalidArgumentNumbersException {
               throw new InvalidArgumentNumbersException("Method not " +
                                                         "implemented!");
        }
        
        /**
         * ececute the action for the command using the index stack ... the
         * argument stack
         *
         * @param valueStack the argument stack
         * @param indexStack another set of argument stack
         * @param targetScene the target MoleculeScene
         * @throws InvalidArgumentNumbersException if the number of arguments
         *         do not match that of those required by the command
         */
        @Override
        public void execute(Stack<Double> valueStack, 
                            Stack<Integer> indexStack, 
                            MoleculeScene targetScene) 
                            throws InvalidArgumentNumbersException {            
            if ((indexStack.size() == 1) && (valueStack.size() == 6)) {
                Iterator<PropertyScene> ps = targetScene.getAllPropertyScene();
                PropertyScene scene = null;
                
                scene=ps.next();
                for(int i=1; i<indexStack.get(0);i++)
                    scene=ps.next();
                
                if (scene == null) {
                    throw new InvalidArgumentNumbersException(
                                           "invalid property scene number.");
                } // end if
                
                BoundingBox newBB = new BoundingBox(new Point3D(
                                                           valueStack.get(0), 
                                                           valueStack.get(1), 
                                                           valueStack.get(2)),
                                                    new Point3D(
                                                           valueStack.get(3), 
                                                           valueStack.get(4), 
                                                           valueStack.get(5)));
                
                GridProperty newGP = scene.getGridProperty().subProperty(newBB);
                
                targetScene.addPropertyScene(new PropertyScene(targetScene,
                                                               newGP));
                
                // show the message
                findToolNotifiation.notify("Sub Property Information", 
                                     "New Sub Property Added:\n" 
                                     + newGP.toString(), 
                                     true, 5000, NotificationType.INFO);
            } else {
                throw new InvalidArgumentNumbersException(
                                           "requires exactly seven arguments.");
            } // end if
        }
        
        /**
         * executes the action for a command using the strig stack ... as the 
         * argument stack
         * 
         * @param valueStack - the string value stack
         * @param dValueStack the double value stack
         * @param indexStack another set of argument stack
         * @param targetScene the target MoleculeScene
         * @throws InvalidArgumentNumbersException if the number of arguments
         *         do not match that of those required by the command
         */
        @Override
        public void execute(Stack<String> valueStack,
                            Stack<Double> dValueStack,
                            Stack<Integer> indexStack,
                            MoleculeScene targetScene) 
                throws MoleculeViewerFindTool.InvalidArgumentNumbersException {
            throw new InvalidArgumentNumbersException("Method not " +
                                                         "implemented!");
        }
    }        
    
    /**
     * The inner class defines how keyword <code><b>interp</b></code> 
     * is processed.
     */
    protected class InterpolatePropertyAdvancedFindKeywordCommand 
                                 extends AdvancedFindKeywordCommand {
        /** Creates a new instance of 
         *  InterpolatePropertyAdvancedFindKeywordCommand */
        public InterpolatePropertyAdvancedFindKeywordCommand() { }
        
        /**
         * ececute the action for the command using the index stack ... the
         * argument stack
         *
         * @param indexStack the argument stack
         * @param targetScene the target MoleculeScene
         * @throws InvalidArgumentNumbersException if the number of arguments
         *         do not match that of those required by the command
         */
        @Override
        public void execute(Stack<Integer> indexStack, 
                            MoleculeScene targetScene) 
                            throws InvalidArgumentNumbersException {
               throw new InvalidArgumentNumbersException("Method not " +
                                                         "implemented!");
        }
        
        /**
         * ececute the action for the command using the index stack ... the
         * argument stack
         *
         * @param valueStack the argument stack
         * @param indexStack another set of argument stack
         * @param targetScene the target MoleculeScene
         * @throws InvalidArgumentNumbersException if the number of arguments
         *         do not match that of those required by the command
         */
        @Override
        public void execute(Stack<Double> valueStack, 
                            Stack<Integer> indexStack, 
                            MoleculeScene targetScene) 
                            throws InvalidArgumentNumbersException {            
            if ((indexStack.size() == 1) && (valueStack.size() == 3)) {
                Iterator<PropertyScene> ps = targetScene.getAllPropertyScene();
                PropertyScene scene = null;
                
                scene=ps.next();
                for(int i=1; i<indexStack.get(0);i++)
                    scene=ps.next();
                
                if (scene == null) {
                    throw new InvalidArgumentNumbersException(
                                           "invalid property scene number.");
                } // end if
                
                GridProperty gp = scene.getGridProperty();                
                GridProperty newGP = gp.interpolate(valueStack.get(0),
                                                    valueStack.get(1),
                                                    valueStack.get(2),
                        InterpolaterFactory.getInstance().getInterpolater(
                           InterpolaterFactory.InterpolaterType.TRI_LINEAR));
                
                targetScene.addPropertyScene(new PropertyScene(targetScene,
                                                               newGP));
                
                // show the message
                findToolNotifiation.notify("Property Interpolation Information", 
                                           "Interpolated property added (" +
                                           valueStack.get(0) + ", " +
                                           valueStack.get(1) + ", " +
                                           valueStack.get(2) + ").", 
                                           true, 5000, NotificationType.INFO);
            } else {
                throw new InvalidArgumentNumbersException(
                                           "requires exactly four arguments.");
            } // end if
        }
        
        /**
         * executes the action for a command using the strig stack ... as the 
         * argument stack
         * 
         * @param valueStack - the string value stack
         * @param dValueStack the double value stack
         * @param indexStack another set of argument stack
         * @param targetScene the target MoleculeScene
         * @throws InvalidArgumentNumbersException if the number of arguments
         *         do not match that of those required by the command
         */
        @Override
        public void execute(Stack<String> valueStack,
                            Stack<Double> dValueStack,
                            Stack<Integer> indexStack,
                            MoleculeScene targetScene) 
                throws MoleculeViewerFindTool.InvalidArgumentNumbersException {
            throw new InvalidArgumentNumbersException("Method not " +
                                                         "implemented!");
        }
    }
    
    /**
     * The inner class defines how keyword <code><b>showall</b></code> 
     * is processed.
     */
    protected class ShowAllAdvancedFindKeywordCommand 
                             extends AdvancedFindKeywordCommand {
        /** Creates a new instance of ShowAllAdvancedFindKeywordCommand */
        public ShowAllAdvancedFindKeywordCommand() { }
        
        /**
         * ececute the action for the command using the index stack ... the
         * argument stack
         *
         * @param indexStack the argument stack
         * @param targetScene the target MoleculeScene
         * @throws InvalidArgumentNumbersException if the number of arguments
         *         do not match that of those required by the command
         */
        @Override
        public void execute(Stack<Integer> indexStack, 
                            MoleculeScene targetScene) 
                            throws InvalidArgumentNumbersException {
               throw new InvalidArgumentNumbersException("Method not " +
                                                         "implemented!");
        }
        
        /**
         * ececute the action for the command using the index stack ... the
         * argument stack
         *
         * @param valueStack the argument stack
         * @param indexStack another set of argument stack
         * @param targetScene the target MoleculeScene
         * @throws InvalidArgumentNumbersException if the number of arguments
         *         do not match that of those required by the command
         */
        @Override
        public void execute(Stack<Double> valueStack, 
                            Stack<Integer> indexStack, 
                            MoleculeScene targetScene) 
                            throws InvalidArgumentNumbersException {
            throw new InvalidArgumentNumbersException("Method not " +
                                                         "implemented!");
        }

        /**
         * executes the action for a command using the strig stack ... as the 
         * argument stack
         * 
         * @param valueStack - the string value stack
         * @param dValueStack the double value stack
         * @param indexStack another set of argument stack
         * @param targetScene the target MoleculeScene
         * @throws InvalidArgumentNumbersException if the number of arguments
         *         do not match that of those required by the command
         */
        @Override
        public void execute(Stack<String> valueStack,
                            Stack<Double> dValueStack,
                            Stack<Integer> indexStack,
                            MoleculeScene targetScene) 
                throws MoleculeViewerFindTool.InvalidArgumentNumbersException {            
            if (valueStack.size() == 0) {
                int i;
                
                for(i=0; i<targetScene.getMolecule().getNumberOfAtoms(); i++) {
                    targetScene.setScreenAtomVisibility(i, true);
                } // end for
            } else {                
                int i;
                
                for(i=0; i<targetScene.getMolecule().getNumberOfAtoms(); i++) {
                    if (valueStack.contains(
                            targetScene.getMolecule().getAtom(i).getSymbol())) {
                        targetScene.setScreenAtomVisibility(i, true);
                    } // end if
                } // end for
            } // end if
        }
    }

    /**
     * The inner class defines how keyword <code><b>showonly</b></code> 
     * is processed.
     */
    protected class ShowOnlyAdvancedFindKeywordCommand 
                             extends AdvancedFindKeywordCommand {
        /** Creates a new instance of ShowOnlyAdvancedFindKeywordCommand */
        public ShowOnlyAdvancedFindKeywordCommand() { }
        
        /**
         * ececute the action for the command using the index stack ... the
         * argument stack
         *
         * @param indexStack the argument stack
         * @param targetScene the target MoleculeScene
         * @throws InvalidArgumentNumbersException if the number of arguments
         *         do not match that of those required by the command
         */
        @Override
        public void execute(Stack<Integer> indexStack, 
                            MoleculeScene targetScene) 
                            throws InvalidArgumentNumbersException {
               throw new InvalidArgumentNumbersException("Method not " +
                                                         "implemented!");
        }
        
        /**
         * ececute the action for the command using the index stack ... the
         * argument stack
         *
         * @param valueStack the argument stack
         * @param indexStack another set of argument stack
         * @param targetScene the target MoleculeScene
         * @throws InvalidArgumentNumbersException if the number of arguments
         *         do not match that of those required by the command
         */
        @Override
        public void execute(Stack<Double> valueStack, 
                            Stack<Integer> indexStack, 
                            MoleculeScene targetScene) 
                            throws InvalidArgumentNumbersException {
            throw new InvalidArgumentNumbersException("Method not " +
                                                         "implemented!");
        }

        /**
         * executes the action for a command using the strig stack ... as the 
         * argument stack
         * 
         * @param valueStack - the string value stack
         * @param dValueStack the double value stack
         * @param indexStack another set of argument stack
         * @param targetScene the target MoleculeScene
         * @throws InvalidArgumentNumbersException if the number of arguments
         *         do not match that of those required by the command
         */
        @Override
        public void execute(Stack<String> valueStack,
                            Stack<Double> dValueStack,
                            Stack<Integer> indexStack,
                            MoleculeScene targetScene) 
                throws MoleculeViewerFindTool.InvalidArgumentNumbersException {            
            if (valueStack.size() == 0) {
                throw new InvalidArgumentNumbersException(
                                           "requires one or more arguments.");
            } else {                
                int i;
                
                for(i=0; i<targetScene.getMolecule().getNumberOfAtoms(); i++) {
                    if (valueStack.contains(
                            targetScene.getMolecule().getAtom(i).getSymbol())) {
                        targetScene.setScreenAtomVisibility(i, true);
                    } else {
                        targetScene.setScreenAtomVisibility(i, false);
                    } // end if
                } // end for
            } // end if
        }
    }
    
    /**
     * The inner class defines how keyword <code><b>hideall</b></code> 
     * is processed.
     */
    protected class HideAllAdvancedFindKeywordCommand 
                             extends AdvancedFindKeywordCommand {
        /** Creates a new instance of HideAllAdvancedFindKeywordCommand */
        public HideAllAdvancedFindKeywordCommand() { }
        
        /**
         * ececute the action for the command using the index stack ... the
         * argument stack
         *
         * @param indexStack the argument stack
         * @param targetScene the target MoleculeScene
         * @throws InvalidArgumentNumbersException if the number of arguments
         *         do not match that of those required by the command
         */
        @Override
        public void execute(Stack<Integer> indexStack, 
                            MoleculeScene targetScene) 
                            throws InvalidArgumentNumbersException {
               throw new InvalidArgumentNumbersException("Method not " +
                                                         "implemented!");
        }
        
        /**
         * ececute the action for the command using the index stack ... the
         * argument stack
         *
         * @param valueStack the argument stack
         * @param indexStack another set of argument stack
         * @param targetScene the target MoleculeScene
         * @throws InvalidArgumentNumbersException if the number of arguments
         *         do not match that of those required by the command
         */
        @Override
        public void execute(Stack<Double> valueStack, 
                            Stack<Integer> indexStack, 
                            MoleculeScene targetScene) 
                            throws InvalidArgumentNumbersException {
            throw new InvalidArgumentNumbersException("Method not " +
                                                         "implemented!");
        }

        /**
         * executes the action for a command using the strig stack ... as the 
         * argument stack
         * 
         * @param valueStack - the string value stack
         * @param dValueStack the double value stack
         * @param indexStack another set of argument stack
         * @param targetScene the target MoleculeScene
         * @throws InvalidArgumentNumbersException if the number of arguments
         *         do not match that of those required by the command
         */
        @Override
        public void execute(Stack<String> valueStack,
                            Stack<Double> dValueStack,
                            Stack<Integer> indexStack,
                            MoleculeScene targetScene) 
                throws MoleculeViewerFindTool.InvalidArgumentNumbersException {            
            if (valueStack.size() == 0) {
                int i;
                
                for(i=0; i<targetScene.getMolecule().getNumberOfAtoms(); i++) {
                    targetScene.setScreenAtomVisibility(i, false);
                } // end for
            } else {                
                int i;
                
                for(i=0; i<targetScene.getMolecule().getNumberOfAtoms(); i++) {
                    if (valueStack.contains(
                            targetScene.getMolecule().getAtom(i).getSymbol())) {
                        targetScene.setScreenAtomVisibility(i, false);
                    } // end if
                } // end for
            } // end if
        }
    }
    
    /**
     * The inner class defines how keyword <code><b>atomcount</b></code> 
     * is processed.
     */
    protected class AtomCountAdvancedFindKeywordCommand 
                             extends AdvancedFindKeywordCommand {
        /** Creates a new instance of AtomCountAdvancedFindKeywordCommand */
        public AtomCountAdvancedFindKeywordCommand() { }
        
        /**
         * ececute the action for the command using the index stack ... the
         * argument stack
         *
         * @param indexStack the argument stack
         * @param targetScene the target MoleculeScene
         * @throws InvalidArgumentNumbersException if the number of arguments
         *         do not match that of those required by the command
         */
        @Override
        public void execute(Stack<Integer> indexStack, 
                            MoleculeScene targetScene) 
                            throws InvalidArgumentNumbersException {
               throw new InvalidArgumentNumbersException("Method not " +
                                                         "implemented!");
        }
        
        /**
         * ececute the action for the command using the index stack ... the
         * argument stack
         *
         * @param valueStack the argument stack
         * @param indexStack another set of argument stack
         * @param targetScene the target MoleculeScene
         * @throws InvalidArgumentNumbersException if the number of arguments
         *         do not match that of those required by the command
         */
        @Override
        public void execute(Stack<Double> valueStack, 
                            Stack<Integer> indexStack, 
                            MoleculeScene targetScene) 
                            throws InvalidArgumentNumbersException {
            throw new InvalidArgumentNumbersException("Method not " +
                                                         "implemented!");
        }

        /**
         * executes the action for a command using the strig stack ... as the 
         * argument stack
         * 
         * @param valueStack - the string value stack
         * @param dValueStack the double value stack
         * @param indexStack another set of argument stack
         * @param targetScene the target MoleculeScene
         * @throws InvalidArgumentNumbersException if the number of arguments
         *         do not match that of those required by the command
         */
        @Override
        public void execute(Stack<String> valueStack,
                            Stack<Double> dValueStack,
                            Stack<Integer> indexStack,
                            MoleculeScene targetScene) 
                throws MoleculeViewerFindTool.InvalidArgumentNumbersException {            
            if (valueStack.size() == 0) {                
                findToolNotifiation.notify("Number of atoms",
                                          "Total number of atoms in molecule " 
                                          + targetScene.getMolecule().getTitle()
                                          + " is " + targetScene.getMolecule()
                                                           .getNumberOfAtoms(), 
                                           true, 5000, NotificationType.INFO);
            } else if (valueStack.size() == 1) {                
                int i, countedAtoms = 0;
                
                for(i=0; i<targetScene.getMolecule().getNumberOfAtoms(); i++) {
                    if (valueStack.contains(
                            targetScene.getMolecule().getAtom(i).getSymbol())) {
                        countedAtoms++;
                    } // end if
                } // end for
                
                findToolNotifiation.notify("Number of atoms",
                                          "Total number of atoms in molecule " 
                                          + targetScene.getMolecule().getTitle()
                                          + " of type '" + valueStack.get(0)
                                          + "' is " + countedAtoms, 
                                           true, 5000, NotificationType.INFO);
            } else { 
                throw new InvalidArgumentNumbersException(
                                           "requires atmost" +
                                           " one argument.");
            } // end if                     
        }
    }
    
    /**
     * The inner class defines how keyword <code><b>eleccount</b></code> 
     * is processed.
     */
    protected class ElectronCountAdvancedFindKeywordCommand 
                             extends AdvancedFindKeywordCommand {
        /** Creates a new instance of ElectronCountAdvancedFindKeywordCommand */
        public ElectronCountAdvancedFindKeywordCommand() { }
        
        /**
         * ececute the action for the command using the index stack ... the
         * argument stack
         *
         * @param indexStack the argument stack
         * @param targetScene the target MoleculeScene
         * @throws InvalidArgumentNumbersException if the number of arguments
         *         do not match that of those required by the command
         */
        @Override
        public void execute(Stack<Integer> indexStack, 
                            MoleculeScene targetScene) 
                            throws InvalidArgumentNumbersException {
               throw new InvalidArgumentNumbersException("Method not " +
                                                         "implemented!");
        }
        
        /**
         * ececute the action for the command using the index stack ... the
         * argument stack
         *
         * @param valueStack the argument stack
         * @param indexStack another set of argument stack
         * @param targetScene the target MoleculeScene
         * @throws InvalidArgumentNumbersException if the number of arguments
         *         do not match that of those required by the command
         */
        @Override
        public void execute(Stack<Double> valueStack, 
                            Stack<Integer> indexStack, 
                            MoleculeScene targetScene) 
                            throws InvalidArgumentNumbersException {
            throw new InvalidArgumentNumbersException("Method not " +
                                                         "implemented!");
        }

        /**
         * executes the action for a command using the strig stack ... as the 
         * argument stack
         * 
         * @param valueStack - the string value stack
         * @param dValueStack the double value stack
         * @param indexStack another set of argument stack
         * @param targetScene the target MoleculeScene
         * @throws InvalidArgumentNumbersException if the number of arguments
         *         do not match that of those required by the command
         */
        @Override
        public void execute(Stack<String> valueStack,
                            Stack<Double> dValueStack,
                            Stack<Integer> indexStack,
                            MoleculeScene targetScene) 
                throws MoleculeViewerFindTool.InvalidArgumentNumbersException {            
            if (valueStack.size() == 0) {                
                findToolNotifiation.notify("Number of electrons",
                                        "Total number of electrons in molecule " 
                                        + targetScene.getMolecule().getTitle()
                                        + " is " + targetScene.getMolecule()
                                                        .getNumberOfElectrons(), 
                                        true, 5000, NotificationType.INFO);
            } else if (valueStack.size() == 1) {                
                int i, countedElectrons = 0;
                AtomInfo ai = AtomInfo.getInstance();
                
                for(i=0; i<targetScene.getMolecule().getNumberOfAtoms(); i++) {
                    if (valueStack.contains(
                            targetScene.getMolecule().getAtom(i).getSymbol())) {
                        countedElectrons += ai.getAtomicNumber(
                              targetScene.getMolecule().getAtom(i).getSymbol());
                    } // end if
                } // end for
                
                findToolNotifiation.notify("Number of electrons",
                                        "Total number of electrons in molecule " 
                                        + targetScene.getMolecule().getTitle()
                                        + " of type '" + valueStack.get(0)
                                        + "' is " + countedElectrons, 
                                        true, 5000, NotificationType.INFO);
            } else { 
                throw new InvalidArgumentNumbersException(
                                           "requires atmost" +
                                           " one argument.");
            } // end if                     
        }
    }
    
    /**
     * The inner class defines how keyword <code><b>formula</b></code> 
     * is processed.
     */
    protected class FormulaAdvancedFindKeywordCommand 
                             extends AdvancedFindKeywordCommand {
        /** Creates a new instance of FormulaAdvancedFindKeywordCommand */
        public FormulaAdvancedFindKeywordCommand() { }
        
        /**
         * ececute the action for the command using the index stack ... the
         * argument stack
         *
         * @param indexStack the argument stack
         * @param targetScene the target MoleculeScene
         * @throws InvalidArgumentNumbersException if the number of arguments
         *         do not match that of those required by the command
         */
        @Override
        public void execute(Stack<Integer> indexStack, 
                            MoleculeScene targetScene) 
                            throws InvalidArgumentNumbersException {
               throw new InvalidArgumentNumbersException("Method not " +
                                                         "implemented!");
        }
        
        /**
         * ececute the action for the command using the index stack ... the
         * argument stack
         *
         * @param valueStack the argument stack
         * @param indexStack another set of argument stack
         * @param targetScene the target MoleculeScene
         * @throws InvalidArgumentNumbersException if the number of arguments
         *         do not match that of those required by the command
         */
        @Override
        public void execute(Stack<Double> valueStack, 
                            Stack<Integer> indexStack, 
                            MoleculeScene targetScene) 
                            throws InvalidArgumentNumbersException {
            throw new InvalidArgumentNumbersException("Method not " +
                                                         "implemented!");
        }

        /**
         * executes the action for a command using the strig stack ... as the 
         * argument stack
         * 
         * @param valueStack - the string value stack
         * @param dValueStack the double value stack
         * @param indexStack another set of argument stack
         * @param targetScene the target MoleculeScene
         * @throws InvalidArgumentNumbersException if the number of arguments
         *         do not match that of those required by the command
         */
        @Override
        public void execute(Stack<String> valueStack,
                            Stack<Double> dValueStack,
                            Stack<Integer> indexStack,
                            MoleculeScene targetScene) 
                throws MoleculeViewerFindTool.InvalidArgumentNumbersException {            
            if (valueStack.size() == 0) {                
                findToolNotifiation.notify("Molecular Formula",
                                        "<html><body>Molecular formula for " 
                                        + targetScene.getMolecule().getTitle()
                                        + " is " + targetScene.getMolecule()
                                                 .getFormula().getHTMLFormula()
                                        + "</body></html>", 
                                        true, 5000, NotificationType.INFO);
            } else { 
                throw new InvalidArgumentNumbersException(
                                           "requires no arguments.");
            } // end if                     
        }
    }
    
    /**
     * The inner class defines how keyword <code><b>show</b></code> 
     * is processed.
     */
    protected class ShowFindKeywordCommand 
                             implements FindKeywordCommand {
        /** Creates a new instance of ShowFindKeywordCommand */
        public ShowFindKeywordCommand() { }
        
        /**
         * ececute the action for the command using the index stack ... the
         * argument stack
         *
         * @param indexStack the argument stack
         * @param targetScene the target MoleculeScene
         * @throws InvalidArgumentNumbersException if the number of arguments
         *         do not match that of those required by the command
         */
        @Override
        public void execute(Stack<Integer> indexStack, 
                            MoleculeScene targetScene) 
                            throws InvalidArgumentNumbersException {
               if (indexStack.size() > 0) {
                   for(int i=0; 
                       i<targetScene.getMolecule().getNumberOfAtoms(); 
                       i++) {
                       if (indexStack.contains(i)) {
                          targetScene.setScreenAtomVisibility(i, true);
                       } // end if
                   } // end for
               } else {
                   throw new InvalidArgumentNumbersException(
                                           "requires one or more arguments.");
               } // end if
        }        
    }

    /**
     * The inner class defines how keyword <code><b>hide</b></code> 
     * is processed.
     */
    protected class HideFindKeywordCommand 
                             implements FindKeywordCommand {
        /** Creates a new instance of HideFindKeywordCommand */
        public HideFindKeywordCommand() { }
        
        /**
         * ececute the action for the command using the index stack ... the
         * argument stack
         *
         * @param indexStack the argument stack
         * @param targetScene the target MoleculeScene
         * @throws InvalidArgumentNumbersException if the number of arguments
         *         do not match that of those required by the command
         */
        @Override
        public void execute(Stack<Integer> indexStack, 
                            MoleculeScene targetScene) 
                            throws InvalidArgumentNumbersException {
               if (indexStack.size() > 0) {
                   for(int i=0; 
                       i<targetScene.getMolecule().getNumberOfAtoms(); 
                       i++) {
                       if (indexStack.contains(i)) {
                          targetScene.setScreenAtomVisibility(i, false);
                       } // end if
                   } // end for
               } else {
                   throw new InvalidArgumentNumbersException(
                                           "requires one or more arguments.");
               } // end if
        }        
    }
    
    /**
     * The inner class defines how keyword <code><b>pick</b></code> 
     * is processed.
     */
    protected class PickKeywordCommand 
                             extends AdvancedFindKeywordCommand {
        /** Creates a new instance of PickKeywordCommand */
        public PickKeywordCommand() { }
        
        /**
         * ececute the action for the command using the index stack ... the
         * argument stack
         *
         * @param indexStack the argument stack
         * @param targetScene the target MoleculeScene
         * @throws InvalidArgumentNumbersException if the number of arguments
         *         do not match that of those required by the command
         */
        @Override
        public void execute(Stack<Integer> indexStack, 
                            MoleculeScene targetScene) 
                            throws InvalidArgumentNumbersException {
               if (indexStack.size() > 0) {
                   for(Integer index : indexStack) {
                       targetScene.selectAtomIndex(index);
                   } // end for
               } else {
                   throw new InvalidArgumentNumbersException(
                                           "requires one or more arguments.");
               } // end if
        }

        /**
         * ececute the action for the command using the index stack ... the
         * argument stack
         *
         * @param valueStack the argument stack
         * @param indexStack another set of argument stack
         * @param targetScene the target MoleculeScene
         * @throws InvalidArgumentNumbersException if the number of arguments
         *         do not match that of those required by the command
         */
        @Override
        public void execute(Stack<Double> valueStack,
                            Stack<Integer> indexStack,
                            MoleculeScene targetScene)
                            throws InvalidArgumentNumbersException {
            throw new InvalidArgumentNumbersException("Method not " +
                                                         "implemented!");
        }

        /**
         * executes the action for a command using the strig stack ... as the
         * argument stack
         *
         * @param valueStack - the string value stack
         * @param dValueStack the double value stack
         * @param indexStack another set of argument stack
         * @param targetScene the target MoleculeScene
         * @throws InvalidArgumentNumbersException if the number of arguments
         *         do not match that of those required by the command
         */
        @Override
        public void execute(Stack<String> valueStack,
                            Stack<Double> dValueStack,
                            Stack<Integer> indexStack,
                            MoleculeScene targetScene)
                throws MoleculeViewerFindTool.InvalidArgumentNumbersException {
            if (valueStack.size() == 0) {
                throw new InvalidArgumentNumbersException(
                                           "requires one or more arguments.");
            } else {
                int i;

                for(i=0; i<targetScene.getMolecule().getNumberOfAtoms(); i++) {
                    if (valueStack.contains(
                            targetScene.getMolecule().getAtom(i).getSymbol())) {
                        targetScene.selectAtomIndex(i, true);
                    } else {
                        targetScene.selectAtomIndex(i, false);
                    } // end if
                } // end for
            } // end if

            if (indexStack.size() > 0) execute(indexStack, targetScene);
        }
    }

    /**
     * The inner class defines how keyword <code><b>invertpick</b></code>
     * is processed.
     */
    protected class InvertPickKeywordCommand
                             extends AdvancedFindKeywordCommand {
        /** Creates a new instance of InvertPickKeywordCommand */
        public InvertPickKeywordCommand() { }

        /**
         * ececute the action for the command using the index stack ... the
         * argument stack
         *
         * @param indexStack the argument stack
         * @param targetScene the target MoleculeScene
         * @throws InvalidArgumentNumbersException if the number of arguments
         *         do not match that of those required by the command
         */
        @Override
        public void execute(Stack<Integer> indexStack,
                            MoleculeScene targetScene)
                            throws InvalidArgumentNumbersException {
               throw new InvalidArgumentNumbersException("Method not " +
                                                         "implemented!");
        }

        /**
         * ececute the action for the command using the index stack ... the
         * argument stack
         *
         * @param valueStack the argument stack
         * @param indexStack another set of argument stack
         * @param targetScene the target MoleculeScene
         * @throws InvalidArgumentNumbersException if the number of arguments
         *         do not match that of those required by the command
         */
        @Override
        public void execute(Stack<Double> valueStack,
                            Stack<Integer> indexStack,
                            MoleculeScene targetScene)
                            throws InvalidArgumentNumbersException {
            throw new InvalidArgumentNumbersException("Method not " +
                                                         "implemented!");
        }

        /**
         * executes the action for a command using the strig stack ... as the
         * argument stack
         *
         * @param valueStack - the string value stack
         * @param dValueStack the double value stack
         * @param indexStack another set of argument stack
         * @param targetScene the target MoleculeScene
         * @throws InvalidArgumentNumbersException if the number of arguments
         *         do not match that of those required by the command
         */
        @Override
        public void execute(Stack<String> valueStack,
                            Stack<Double> dValueStack,
                            Stack<Integer> indexStack,
                            MoleculeScene targetScene)
                throws MoleculeViewerFindTool.InvalidArgumentNumbersException {

            if (valueStack.size() > 0) {
                throw new InvalidArgumentNumbersException(
                                           "requires no arguments.");
            } // end if
           
            for(int i=0; i<targetScene.getMolecule().getNumberOfAtoms(); i++) {
                targetScene.selectAtomIndex(i);
            } // end for
        }
    }

    /**
     * The inner class defines how keyword <code><b>clearpick</b></code>
     * is processed.
     */
    protected class ClearPickKeywordCommand
                             extends AdvancedFindKeywordCommand {
        /** Creates a new instance of ClearPickKeywordCommand */
        public ClearPickKeywordCommand() { }

        /**
         * ececute the action for the command using the index stack ... the
         * argument stack
         *
         * @param indexStack the argument stack
         * @param targetScene the target MoleculeScene
         * @throws InvalidArgumentNumbersException if the number of arguments
         *         do not match that of those required by the command
         */
        @Override
        public void execute(Stack<Integer> indexStack,
                            MoleculeScene targetScene)
                            throws InvalidArgumentNumbersException {
               throw new InvalidArgumentNumbersException("Method not " +
                                                         "implemented!");
        }

        /**
         * ececute the action for the command using the index stack ... the
         * argument stack
         *
         * @param valueStack the argument stack
         * @param indexStack another set of argument stack
         * @param targetScene the target MoleculeScene
         * @throws InvalidArgumentNumbersException if the number of arguments
         *         do not match that of those required by the command
         */
        @Override
        public void execute(Stack<Double> valueStack,
                            Stack<Integer> indexStack,
                            MoleculeScene targetScene)
                            throws InvalidArgumentNumbersException {
            throw new InvalidArgumentNumbersException("Method not " +
                                                         "implemented!");
        }

        /**
         * executes the action for a command using the strig stack ... as the
         * argument stack
         *
         * @param valueStack - the string value stack
         * @param dValueStack the double value stack
         * @param indexStack another set of argument stack
         * @param targetScene the target MoleculeScene
         * @throws InvalidArgumentNumbersException if the number of arguments
         *         do not match that of those required by the command
         */
        @Override
        public void execute(Stack<String> valueStack,
                            Stack<Double> dValueStack,
                            Stack<Integer> indexStack,
                            MoleculeScene targetScene)
                throws MoleculeViewerFindTool.InvalidArgumentNumbersException {

            if (valueStack.size() > 0) {
                throw new InvalidArgumentNumbersException(
                                           "requires no arguments.");
            } // end if

            for(int i=0; i<targetScene.getMolecule().getNumberOfAtoms(); i++) {
                targetScene.selectAtomIndex(i, false);
            } // end for
        }
    }

    /**
     * The inner class defines how keyword <code><b>hideunpicked</b></code>
     * is processed.
     */
    protected class HideUnPickedKeywordCommand
                             extends AdvancedFindKeywordCommand {
        /** Creates a new instance of HideUnPickedKeywordCommand */
        public HideUnPickedKeywordCommand() { }

        /**
         * ececute the action for the command using the index stack ... the
         * argument stack
         *
         * @param indexStack the argument stack
         * @param targetScene the target MoleculeScene
         * @throws InvalidArgumentNumbersException if the number of arguments
         *         do not match that of those required by the command
         */
        @Override
        public void execute(Stack<Integer> indexStack,
                            MoleculeScene targetScene)
                            throws InvalidArgumentNumbersException {
               throw new InvalidArgumentNumbersException("Method not " +
                                                         "implemented!");
        }

        /**
         * ececute the action for the command using the index stack ... the
         * argument stack
         *
         * @param valueStack the argument stack
         * @param indexStack another set of argument stack
         * @param targetScene the target MoleculeScene
         * @throws InvalidArgumentNumbersException if the number of arguments
         *         do not match that of those required by the command
         */
        @Override
        public void execute(Stack<Double> valueStack,
                            Stack<Integer> indexStack,
                            MoleculeScene targetScene)
                            throws InvalidArgumentNumbersException {
            throw new InvalidArgumentNumbersException("Method not " +
                                                         "implemented!");
        }

        /**
         * executes the action for a command using the strig stack ... as the
         * argument stack
         *
         * @param valueStack - the string value stack
         * @param dValueStack the double value stack
         * @param indexStack another set of argument stack
         * @param targetScene the target MoleculeScene
         * @throws InvalidArgumentNumbersException if the number of arguments
         *         do not match that of those required by the command
         */
        @Override
        public void execute(Stack<String> valueStack,
                            Stack<Double> dValueStack,
                            Stack<Integer> indexStack,
                            MoleculeScene targetScene)
                throws MoleculeViewerFindTool.InvalidArgumentNumbersException {

            if (valueStack.size() > 0) {
                throw new InvalidArgumentNumbersException(
                                           "requires no arguments.");
            } // end if

            for(int i=0; i<targetScene.getMolecule().getNumberOfAtoms(); i++) {
                if (!targetScene.isAtomIndexSelected(i)) {
                    targetScene.setScreenAtomVisibility(i, false);
                } // end if
            } // end for
        }
    }
} // end of class MoleculeViewerFindTool
