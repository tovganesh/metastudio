/*
 * WorkspacePanel.java
 *
 * Created on December 14, 2003, 7:18 PM
 */

package org.meta.shell.idebeans;

import java.io.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.lang.reflect.*;
import java.text.DecimalFormat;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import org.meta.math.geom.Point3D;
import org.meta.common.Utility;
import org.meta.common.resource.ImageResource;
import org.meta.common.resource.StringResource;

import org.meta.shell.ide.MeTA;
import org.meta.config.impl.AtomInfo;
import org.meta.fragment.Fragment;
import org.meta.fragment.FragmentAtom;
import org.meta.fragment.event.FragmentListChangeEvent;
import org.meta.fragmentor.FragmentationScheme;
import org.meta.molecule.Atom;
import org.meta.molecule.AtomGroup;
import org.meta.molecule.Molecule;
import org.meta.molecule.MoleculeBuilder;
import org.meta.molecule.SpecialStructureRecognizer;
import org.meta.molecule.event.MoleculeStateChangeEvent;
import org.meta.molecule.event.MoleculeStateChangeListener;
import org.meta.molecule.impl.Ring;
import org.meta.molecule.impl.RingRecognizer;
import org.meta.shell.idebeans.eventhandlers.MainMenuEventHandlers;
import org.meta.shell.idebeans.propertysheet.IDEPropertySheetUI;
import org.meta.shell.idebeans.viewers.QuickViewer;
import org.meta.shell.idebeans.viewers.QuickViewerFactory;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.MoleculeScene;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.MoleculeViewer;
import org.meta.workspace.Workspace;
import org.meta.workspace.WorkspaceItem;
import org.meta.workspace.WorkspaceItemFactory;
import org.meta.workspace.impl.BeanShellScriptItem;
import org.meta.workspace.impl.MoleculeItem;

/**
 * The workspace panel UI ... handling workspace in the MeTA Studio.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class WorkspacePanel extends JPanel {
    
    private JTree workspace;
    private DefaultMutableTreeNode workspaceTree, scriptTree;
    
    /** Class -> JPopupMenu association */
    private Hashtable<Class<?>, WorkspacePopup> popupAssociation;
    
    private AtomInfo atomInfo = AtomInfo.getInstance();
    
    /** Holds value of property currentWorkspace. */
    private Workspace currentWorkspace;    
    
    private WorkspacePopup workspacePopupMenu, moleculePopupMenu,
                           atomPopupMenu, scriptFolderMenu, scriptMenu,
                           fragmentSchemePopupMenu;  
    private FragmentWorkspacePopup fragmentPopupMenu;
    private JMenuItem addRemoveItem, workspaceProperties, saveWorkspace, 
                      closeWorkspace, changeMoleculeTitle, deleteMolecule,
                      moleculeProperties, atomProperties, exportGeometry,
                      fragmentProperties, deleteFragment,
                      viewFragmnetInActiveFrame, hideFragmnetInActiveFrame,
                      exportFragmentGeometry, exportFragmentList,
                      exportFragmentGeometries, importFragments,
                      generateCardinality, cleanOverlaps, cleanAllFragments,
                      addDummyAtoms, estimateCharges, energyEvaulater;
    private JMenu viewMolecule;
    private JMenuItem viewInActiveFrame, viewInNewFrame, viewCoordinates,
                      viewZMatrix;    
    private JMenuItem newScript, editScript, renameScript;
    
    // reference ro the Studio object
    private MeTA ideInstance;
    
    private int ringIndex; // private member to keep track of ring indices
    private int atomGroupIndex; // track atom group indices
    
    // molecule state change listener, one listener for all molecules
    private MoleculeStateChangeListener moleculeStateChangeListener;
    private ArrayList<Molecule> listenToList;
    
    /** Creates a new instance of WorkspacePanel */
    public WorkspacePanel(MeTA ideInstance) {        
        this.ideInstance = ideInstance;
        
        // set up the default popup associations
        popupAssociation = new Hashtable<Class<?>, WorkspacePopup>(10);
        makePopups();        
    }
    
    /**
     * method to make the popups
     */
    private void makePopups() {
        // the workspacePopupMenu
        workspacePopupMenu = new WorkspacePopup("Workspace Menu");
        addRemoveItem = new JMenuItem("Add / Remove ...");
        addRemoveItem.setMnemonic('A');
        addRemoveItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {  
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) 
                                       workspace.getLastSelectedPathComponent();
                    
                if (node == null) return;
                        
                Object workspace = node.getUserObject();
                       
                // show the add/ remove wizard, if valid object
                if (workspace instanceof Workspace) {
                   AddRemoveWizard addRemoveWizard = new AddRemoveWizard(
                                            ideInstance, (Workspace) workspace);
                   addRemoveWizard.setLocationRelativeTo(ideInstance);        
                   addRemoveWizard.setModal(true);
                   addRemoveWizard.setVisible(true);
                } // end if
            }
        });
        workspacePopupMenu.add(addRemoveItem);                
        
        workspacePopupMenu.addSeparator();
        
        workspaceProperties = new JMenuItem("Properties");
        workspaceProperties.setMnemonic('P');
        workspaceProperties.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {                
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) 
                                       workspace.getLastSelectedPathComponent();
                    
                if (node == null) return;
                        
                Object workspace = node.getUserObject();
                                
                if (workspace instanceof Workspace) {
                  IDEPropertySheetUI ui = new IDEPropertySheetUI(
                                       Workspace.class, workspace, ideInstance);
                  // show the UI
                  ui.show(null);
                } // end if                                
            }
        });
        workspacePopupMenu.add(workspaceProperties);        
        
        workspacePopupMenu.addSeparator();
        
        saveWorkspace = new JMenuItem("Save");
        saveWorkspace.setMnemonic('S');
        saveWorkspace.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainMenuEventHandlers.getInstance(ideInstance)
                                     .saveWorkspaceMenuItemActionPerformed(evt);
            }
        });
        workspacePopupMenu.add(saveWorkspace);
        
        workspacePopupMenu.addSeparator();
        
        closeWorkspace = new JMenuItem("Close");
        closeWorkspace.setMnemonic('C');
        closeWorkspace.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainMenuEventHandlers.getInstance(ideInstance)
                                    .closeWorkspaceMenuItemActionPerformed(evt);
            }
        });
        workspacePopupMenu.add(closeWorkspace);
        
        addPopupAssociation(Workspace.class, workspacePopupMenu);
        
        // the moleculePopupMenu
        moleculePopupMenu = new WorkspacePopup("Molecule Menu");
        
        changeMoleculeTitle = new JMenuItem("Change title...");
        changeMoleculeTitle.setMnemonic('t');
        changeMoleculeTitle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                                       workspace.getLastSelectedPathComponent();
                
                if (node == null) return;
                
                Object molecule = node.getUserObject();
                
                // varify that the correct stuff was clicked
                if (molecule instanceof Molecule) {
                    ((Molecule) molecule).setTitle(JOptionPane.showInputDialog(
                                    ideInstance,
                                    "Enter a new title for the molecule :", 
                                    ((Molecule) molecule).getTitle()));
                } // end if
            }
        });
        moleculePopupMenu.add(changeMoleculeTitle);
        
        moleculePopupMenu.addSeparator();                          
        
        // view sub menu
        viewMolecule = new JMenu("View");
        viewMolecule.setMnemonic('V');
        
            viewInActiveFrame = new JMenuItem("In active viewer");
            viewInActiveFrame.setMnemonic('a');
            viewInActiveFrame.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) 
                                       workspace.getLastSelectedPathComponent();
                    
                    if (node == null) return;
                        
                    Object molecule = node.getUserObject();
                    
                    // varify that the correct stuff was clicked
                    if (molecule instanceof Molecule) {
                        JInternalFrame activeFrame = 
                            ideInstance.getWorkspaceDesktop().getActiveFrame();
                        
                        // no active frame? .. return quitely
                        if (activeFrame == null) return;
                        
                        // else add the scene to the current active
                        // MoleculeViewerFrame
                        if (activeFrame instanceof MoleculeViewerFrame) {
                            ((MoleculeViewerFrame) activeFrame).addScene(
                                  new MoleculeScene((Molecule) molecule));
                        } // end if
                    } // end if
                }
            });
            viewMolecule.add(viewInActiveFrame);

            viewInNewFrame    = new JMenuItem("In new viewer");
            viewInNewFrame.setMnemonic('e');
            viewInNewFrame.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) 
                                       workspace.getLastSelectedPathComponent();
                    
                    if (node == null) return;
                        
                    Object molecule = node.getUserObject();
                    
                    // varify that the correct stuff was clicked
                    if (molecule instanceof Molecule) {
                      // open the MoleculeViewerFrame and related 
                      // molecules in it
                      MoleculeViewerFrame mvf = 
                                      new MoleculeViewerFrame(ideInstance);
                      ideInstance.getWorkspaceDesktop()
                                       .addInternalFrame(mvf, true);
                      ideInstance.getWorkspaceExplorer()
                           .getTaskPanel().activate("Molecule related Tasks"); 
                      mvf.addInternalFrameListener(new InternalFrameAdapter() {
                        @Override
                        public void internalFrameClosing(InternalFrameEvent e) {
                          // hide the task list
                          ideInstance.getWorkspaceExplorer()
                           .getTaskPanel().deactivate("Molecule related Tasks"); 
                        }
                      }); 
                      mvf.getMoleculeViewer().disableUndo();
                      mvf.addScene(new MoleculeScene((Molecule) molecule));
                      mvf.getMoleculeViewer().enableUndo();
                    } // end if
                }
            });
            viewMolecule.add(viewInNewFrame);
            
            viewMolecule.addSeparator();

            viewCoordinates   = new JMenuItem("Coordinates...");
            viewCoordinates.setMnemonic('C');
            viewCoordinates.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) 
                                       workspace.getLastSelectedPathComponent();
                    
                    if (node == null) return;
                        
                    Object molecule = node.getUserObject();
                    
                    // varify that the correct stuff was clicked
                    if (molecule instanceof Molecule) {
                      try {
                        QuickViewerFactory qvf = (QuickViewerFactory)
                          (Utility.getDefaultImplFor(QuickViewerFactory.class))
                                  .newInstance();
                        QuickViewer qv = qvf.getViewer("text/plain");
                        
                        StringBuffer sb = new StringBuffer();
                        
                        Iterator<Atom> atoms = ((Molecule) molecule).getAtoms();
                        Atom atom;
                        
                        while(atoms.hasNext()) {
                            atom = atoms.next();
                            
                            sb.append(atom.getSymbol() 
                                      + " " + atom.getAtomCenter() + '\n');
                        } // end while
                        
                        qv.showStringBuffer(sb);
                      } catch(Exception e) {
                        System.err.println("Exception : " + e.toString());
                        System.err.println("Could not initilize quick viewer.");
                        e.printStackTrace();
                      } // end of try .. catch block
                    } // end if
                }
            });
            viewMolecule.add(viewCoordinates);

            viewZMatrix      = new JMenuItem("Z Matrix...");
            viewZMatrix.setMnemonic('Z');
            viewZMatrix.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) 
                                       workspace.getLastSelectedPathComponent();
                    
                    if (node == null) return;
                        
                    Object molecule = node.getUserObject();
                    
                    // varify that the correct stuff was clicked
                    if (molecule instanceof Molecule) {
                      try {
                        QuickViewerFactory qvf = (QuickViewerFactory)
                          (Utility.getDefaultImplFor(QuickViewerFactory.class))
                                  .newInstance();
                        QuickViewer qv = qvf.getViewer("text/plain");
                        
                        StringBuffer sb = new StringBuffer();
                                   
                        // check is Z Matrix is computed ... if no then
                        // try to do so
                        if (!((Molecule) molecule).isZMatrixComputed()) {
                            try {
                              MoleculeBuilder mb = (MoleculeBuilder) 
                                    Utility.getDefaultImplFor(
                                          MoleculeBuilder.class).newInstance();
                              mb.makeZMatrix((Molecule) molecule);
                            } catch(Exception e) {
                              System.err.println("Exception : " + e.toString());
                              System.err.println("Z-Matrix display failed!");
                              e.printStackTrace();
                              return;
                            } // end of try .. catch block  
                        } // end if
                        
                        Iterator<Atom> atoms = ((Molecule) molecule).getAtoms();
                        Atom e, l, a, d;
                        int addIdx = 0;
                        
                        DecimalFormat f = new DecimalFormat("#.###");
                        
                        while(atoms.hasNext()) {
                            e = atoms.next();
                            
                            if (e.getIndex() == 0) {
                                sb.append(e.getSymbol() + '\n');
                            } else if (e.getIndex() == 1) {
                                l = e.getLengthReference().getReferenceAtom();
                                sb.append(e.getSymbol() + " " +
                                  (l.getIndex()+addIdx) + " "
                                 + f.format(e.getLengthReference().getValue())
                                 + '\n');
                            } else if (e.getIndex() == 2) {
                                l = e.getLengthReference().getReferenceAtom();
                                a = e.getAngleReference().getReferenceAtom();
                                sb.append(e.getSymbol() + " " +
                                    (l.getIndex()+addIdx) + " "
                                   + f.format(e.getLengthReference().getValue())
                                   + " " +
                                    (a.getIndex()+addIdx) + " "
                                   + f.format(e.getAngleReference().getValue())
                                   + '\n');
                            } else {
                                l = e.getLengthReference().getReferenceAtom();
                                a = e.getAngleReference().getReferenceAtom();
                                d = e.getDihedralReference().getReferenceAtom();
                                sb.append(e.getSymbol() + " " +
                                  (l.getIndex()+addIdx) + " "
                                 + f.format(e.getLengthReference().getValue())
                                 + " " +
                                  (a.getIndex()+addIdx) + " "
                                 + f.format(e.getAngleReference().getValue())
                                 + " " +
                                  (d.getIndex()+addIdx) + " "
                                 + f.format(e.getDihedralReference().getValue())
                                 + '\n');
                            } // end if
                        } // end while
                        
                        qv.showStringBuffer(sb);
                      } catch(Exception e) {
                        System.err.println("Exception : " + e.toString());
                        System.err.println("Could not initilize quick viewer.");
                        e.printStackTrace();
                      } // end of try .. catch block  
                    } // end if
                }
            });
            viewMolecule.add(viewZMatrix);
        
        moleculePopupMenu.add(viewMolecule);               
        
        deleteMolecule = new JMenuItem("Delete");
        deleteMolecule.setMnemonic('D');
        moleculePopupMenu.add(deleteMolecule);     
        
        moleculePopupMenu.addSeparator();
        
        exportGeometry = new JMenuItem("Export geometry...");
        exportGeometry.setMnemonic('x');
        exportGeometry.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) 
                                       workspace.getLastSelectedPathComponent();
                    
                if (node == null) return;
                        
                Object molecule = node.getUserObject();
                                
                if (molecule instanceof Molecule) {
                    // we use the bean shell command here!
                    // for a great simplicity                                        
                    try {                                                
                       Utility.executeBeanShellScript("saveSelectedMolecule()"); 
                       ideInstance.setCursor(Cursor.getDefaultCursor());
                    } catch (Exception ignored) {                        
                       ideInstance.setCursor(Cursor.getDefaultCursor());
                       System.err.println(
                            "Warning! Unable to import commands : " + ignored);
                       ignored.printStackTrace();
                        
                       JOptionPane.showMessageDialog(ideInstance,
                            "Error while saving file. "                           
                            + ". \n Please look into Runtime log for more "
                            + "information.",
                            "Error while saving content!",
                            JOptionPane.ERROR_MESSAGE);
                    } // end try .. catch block
                } // end if
            }
        });
        moleculePopupMenu.add(exportGeometry);
        
        moleculePopupMenu.addSeparator();
        
        moleculeProperties = new JMenuItem("Properties");
        moleculeProperties.setMnemonic('P');
        moleculeProperties.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {                
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) 
                                       workspace.getLastSelectedPathComponent();
                    
                if (node == null) return;
                        
                Object molecule = node.getUserObject();
                                
                if (molecule instanceof Molecule) {
                  IDEPropertySheetUI ui = new IDEPropertySheetUI(Molecule.class, 
                                                         molecule, ideInstance);
                  // show the UI
                  ui.show(null);
                } // end if                                
            }
        });
        moleculePopupMenu.add(moleculeProperties);
        
        addPopupAssociation(Molecule.class, moleculePopupMenu);
        
        // the atomPopupMenu
        atomPopupMenu = new WorkspacePopup("Atom Menu");
        
        atomProperties = new JMenuItem("Properties");
        atomProperties.setMnemonic('P');
        atomProperties.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {                
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) 
                                       workspace.getLastSelectedPathComponent();
                    
                if (node == null) return;
                        
                Object atom = node.getUserObject();
                                
                if (atom instanceof AtomNodeModal) {
                  IDEPropertySheetUI ui = new IDEPropertySheetUI(
                     Atom.class, ((AtomNodeModal) atom).getAtom(), ideInstance);
                  
                  // show the UI
                  ui.show(null);
                } // end if                                
            }
        });
        atomPopupMenu.add(atomProperties);
        
        addPopupAssociation(AtomNodeModal.class, atomPopupMenu);
        
        // scriptFolderMenu
        scriptFolderMenu = new WorkspacePopup("Scripts Menu");
        newScript = new JMenuItem("New Script...");
        newScript.setMnemonic('N');
        newScript.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                // we use the bean shell command here!
                // for a great simplicity
                try {
                    // Query for new name
                    String newName = JOptionPane.showInputDialog(ideInstance, 
                                            "Enter name of new script: ",
                                            "Untitled");
                
                    if (newName == null || newName.equals("")) return;
                    
                    ideInstance.setCursor(
                       Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    
                    // get the factory
                    WorkspaceItemFactory wif = (WorkspaceItemFactory)
                           Utility.getDefaultImplFor(
                                     WorkspaceItemFactory.class).newInstance();
                    Class<WorkspaceItem> implClz 
                                      = wif.getItemClass("workspace/beanshell");
                    
                    // and then do the classic stunt .. init the item
                    Constructor<WorkspaceItem> theConstructor = 
                          implClz.getDeclaredConstructor(
                                    new Class[]{String.class});        
                    WorkspaceItem wi = (WorkspaceItem) 
                          theConstructor.newInstance(
                              currentWorkspace.getWorkspaceDirectory()  
                              + File.separatorChar 
                              + newName + "." + StringResource.getInstance()
                                               .getBeanShellScriptExtension());
                    currentWorkspace.addWorkspaceItem(wi);
                    currentWorkspace.save();
                    
                    Utility.executeBeanShellScript("edit(\"" 
                        + wi.getWorkspaceItemFile().replace('\\', '/') 
                        + "\", \"true\")");
                    ideInstance.setCursor(Cursor.getDefaultCursor());
                    
                    // update the UI
                    update(currentWorkspace);
                } catch (Exception ignored) {
                    ideInstance.setCursor(Cursor.getDefaultCursor());
                    System.err.println(
                            "Warning! Unable to import commands : " + ignored);
                    ignored.printStackTrace();
                    
                    JOptionPane.showMessageDialog(ideInstance,
                          "Error while opening editor. "
                            + ". \n Please look into Runtime log for more "
                            + "information.",
                            "Error while opening editor!",
                          JOptionPane.ERROR_MESSAGE);
                } // end try .. catch block
            }
        });        
        scriptFolderMenu.add(newScript);
        
        addPopupAssociation(Scripts.class, scriptFolderMenu);
        
        // scriptMenu
        scriptMenu = new WorkspacePopup("Beanshell Script Menu");
        editScript = new JMenuItem("Edit Script...");
        editScript.setMnemonic('E');
        editScript.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                // we use the bean shell command here!
                // for a great simplicity
                try {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) 
                                       workspace.getLastSelectedPathComponent();
                    
                    if (node == null) return;
                    
                    BeanShellScriptItem bssi = 
                             (BeanShellScriptItem) node.getUserObject();
                    
                    ideInstance.setCursor(
                       Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    
                    Utility.executeBeanShellScript("edit(\""
                      + bssi.getWorkspaceItemFile().replace('\\', '/') 
                      + "\", \"true\")");
                    
                    ideInstance.setCursor(Cursor.getDefaultCursor());
                } catch (Exception ignored) {
                    ideInstance.setCursor(Cursor.getDefaultCursor());
                    System.err.println(
                            "Warning! Unable to import commands : " + ignored);
                    ignored.printStackTrace();
                    
                    JOptionPane.showMessageDialog(ideInstance,
                          "Error while opening file. "
                            + ". \n Please look into Runtime log for more "
                            + "information.",
                            "Error while opening file!",
                          JOptionPane.ERROR_MESSAGE);
                } // end try .. catch block
            }
        });        
        scriptMenu.add(editScript);
        
        scriptMenu.addSeparator();       
        
        renameScript = new JMenuItem("Rename...");
        renameScript.setMnemonic('R');
        renameScript.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) 
                                       workspace.getLastSelectedPathComponent();
                    
                if (node == null) return;
                
                try {
                    BeanShellScriptItem bssi = 
                             (BeanShellScriptItem) node.getUserObject();
                
                    // Query for new name
                    String newName = JOptionPane.showInputDialog(ideInstance, 
                                            "Enter a new name for the script: ",
                                            bssi.getName());
                
                    if (newName == null || newName.equals("")) return;
                
                    // and try to rename
                    File renameFile = new File(bssi.getWorkspaceItemFile());
                    renameFile.renameTo(
                          new File(currentWorkspace.getWorkspaceDirectory() 
                               + newName + StringResource.getInstance()
                                               .getBeanShellScriptExtension()));
                
                    // update the workspace .. and the UI
                    bssi.setWorkspaceItemFile(renameFile.getAbsolutePath());
                    currentWorkspace.save();
                    update(currentWorkspace);
                } catch (Exception ignored) {
                    ideInstance.setCursor(Cursor.getDefaultCursor());
                    System.err.println(
                            "Warning! Unable to rename : " + ignored);
                    ignored.printStackTrace();
                    
                    JOptionPane.showMessageDialog(ideInstance,
                          "Error while renaming file. "
                            + ". \n Please look into Runtime log for more "
                            + "information.",
                            "Error while renaming file!",
                          JOptionPane.ERROR_MESSAGE);
                } // end of try .. catch block
            }
        });
        scriptMenu.add(renameScript);
        
        addPopupAssociation(BeanShellScriptItem.class, scriptMenu);
        
        // fragment menu
        fragmentPopupMenu = new FragmentWorkspacePopup("Fragment Menu");
        viewFragmnetInActiveFrame = new JMenuItem(
                                         "View fragment in active frame");
        viewFragmnetInActiveFrame.setMnemonic('V');
        viewFragmnetInActiveFrame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                showFragment(true);
            }
        });
        fragmentPopupMenu.add(viewFragmnetInActiveFrame);
        
        hideFragmnetInActiveFrame = new JMenuItem(
                                         "Hide fragment in active frame");
        hideFragmnetInActiveFrame.setMnemonic('H');
        hideFragmnetInActiveFrame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                showFragment(false);
            }
        });
        fragmentPopupMenu.add(hideFragmnetInActiveFrame);
        
        deleteFragment = new JMenuItem("Delete");
        deleteFragment.setMnemonic('D');
        deleteFragment.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) 
                                       workspace.getLastSelectedPathComponent();
                    
                if (node == null) return;
                        
                Object fragmentNode = node.getUserObject();
                                
                if (fragmentNode instanceof FragmentNodeModal) {
                    // get the fragment instance
                    Fragment fragment =
                        ((FragmentNodeModal) fragmentNode).getFragment();
                    
                    // get the scheme associated with this fragment
                    FragmentationScheme fc = ((FragmentationSchemeNodeModal)
                            (Utility.findImmediateParentNode(node,
                                FragmentationSchemeNodeModal.class)
                            ).getUserObject()
                        ).getFragmentationScheme();
                   
                   // and remove the fragment from there!
                   fc.getFragmentList().removeFragment(fragment);
                }
            }
        });
        fragmentPopupMenu.add(deleteFragment);
        
        fragmentPopupMenu.addSeparator();
        
        exportFragmentGeometry = new JMenuItem("Export Geometry...");
        exportFragmentGeometry.setMnemonic('E');
        exportFragmentGeometry.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) 
                                       workspace.getLastSelectedPathComponent();
                    
                if (node == null) return;
                        
                Object fragment = node.getUserObject();
                                
                if (fragment instanceof FragmentNodeModal) {
                    // we use the bean shell command here!
                    // for a great simplicity                                        
                    try {                                                
                       Utility.executeBeanShellScript("saveSelectedFragment()"); 
                       ideInstance.setCursor(Cursor.getDefaultCursor());
                    } catch (Exception ignored) {                        
                       ideInstance.setCursor(Cursor.getDefaultCursor());
                       System.err.println(
                            "Warning! Unable to import commands : " + ignored);
                       ignored.printStackTrace();
                        
                       JOptionPane.showMessageDialog(ideInstance,
                            "Error while saving file. "                           
                            + ". \n Please look into Runtime log for more "
                            + "information.",
                            "Error while saving content!",
                            JOptionPane.ERROR_MESSAGE);
                    } // end try .. catch block
                } // end if
            }
        });
        fragmentPopupMenu.add(exportFragmentGeometry);
        
        fragmentPopupMenu.addSeparator();
        
        fragmentProperties = new JMenuItem("Properties");
        fragmentProperties.setMnemonic('P');
        fragmentProperties.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) 
                                       workspace.getLastSelectedPathComponent();
                    
                if (node == null) return;
                        
                Object fragment = node.getUserObject();
                                
                if (fragment instanceof FragmentNodeModal) {
                  IDEPropertySheetUI ui = new IDEPropertySheetUI(
                   Fragment.class, ((FragmentNodeModal) fragment).getFragment(), 
                   ideInstance);
                  
                  // show the UI
                  ui.show(null);
                } // end if   
            }
        });
        fragmentPopupMenu.add(fragmentProperties);
        
        addPopupAssociation(FragmentNodeModal.class, fragmentPopupMenu);
        
        // fragment scheme menu
        fragmentSchemePopupMenu = new WorkspacePopup("Fragment Scheme Menu");
        exportFragmentList = new JMenuItem("Export to MTA-GAMESS format...");
        exportFragmentList.setMnemonic('E');
        exportFragmentList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) 
                                       workspace.getLastSelectedPathComponent();
                    
                if (node == null) return;
                        
                Object fragmentScheme = node.getUserObject();
                                
                if (fragmentScheme instanceof FragmentationSchemeNodeModal) {
                    // we use the bean shell command here!
                    // for a great simplicity                                        
                    try {                                                
                       Utility.executeBeanShellScript("exportToMTAGAMESS()"); 
                       ideInstance.setCursor(Cursor.getDefaultCursor());
                    } catch (Exception ignored) {                        
                       ideInstance.setCursor(Cursor.getDefaultCursor());
                       System.err.println(
                            "Warning! Unable to import commands : " + ignored);
                       ignored.printStackTrace();
                        
                       JOptionPane.showMessageDialog(ideInstance,
                            "Error while saving file. "                           
                            + ". \n Please look into Runtime log for more "
                            + "information.",
                            "Error while saving content!",
                            JOptionPane.ERROR_MESSAGE);
                    } // end try .. catch block
                } // end if
            }
        });
        
        fragmentSchemePopupMenu.add(exportFragmentList);                
        
        exportFragmentGeometries = new JMenuItem(
                                        "Export fragment geometries...");
        exportFragmentGeometries.setMnemonic('f');
        exportFragmentGeometries.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) 
                                       workspace.getLastSelectedPathComponent();
                    
                if (node == null) return;
                        
                Object fragmentScheme = node.getUserObject();
                                
                if (fragmentScheme instanceof FragmentationSchemeNodeModal) {
                    // we use the bean shell command here!
                    // for a great simplicity                                        
                    try {                                                
                       Utility.executeBeanShellScript(
                                      "exportFragmentGeometries()"); 
                       ideInstance.setCursor(Cursor.getDefaultCursor());
                    } catch (Exception ignored) {                        
                       ideInstance.setCursor(Cursor.getDefaultCursor());
                       System.err.println(
                            "Warning! Unable to import commands : " + ignored);
                       ignored.printStackTrace();
                        
                       JOptionPane.showMessageDialog(ideInstance,
                            "Error while saving file. "                           
                            + ". \n Please look into Runtime log for more "
                            + "information.",
                            "Error while saving content!",
                            JOptionPane.ERROR_MESSAGE);
                    } // end try .. catch block
                } // end if
            }
        });
        
        fragmentSchemePopupMenu.add(exportFragmentGeometries);
        
        fragmentSchemePopupMenu.addSeparator();
        
        importFragments = new JMenuItem("Import fragment geometries...");
        importFragments.setMnemonic('I');
        importFragments.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) 
                                       workspace.getLastSelectedPathComponent();
                    
                if (node == null) return;
                        
                Object fragmentScheme = node.getUserObject();
                                
                if (fragmentScheme instanceof FragmentationSchemeNodeModal) {
                    // we use the bean shell command here!
                    // for a great simplicity                                        
                    try {                                                
                       Utility.executeBeanShellScript(
                                               "importFragmentGeometries()"); 
                       ideInstance.setCursor(Cursor.getDefaultCursor());
                    } catch (Exception ignored) {                        
                       ideInstance.setCursor(Cursor.getDefaultCursor());
                       System.err.println(
                            "Warning! Unable to import commands : " + ignored);
                       ignored.printStackTrace();
                        
                       JOptionPane.showMessageDialog(ideInstance,
                            "Error while saving file. "                           
                            + ". \n Please look into Runtime log for more "
                            + "information.",
                            "Error while saving content!",
                            JOptionPane.ERROR_MESSAGE);
                    } // end try .. catch block
                } // end if
            }
        });
        
        fragmentSchemePopupMenu.add(importFragments);
                
        fragmentSchemePopupMenu.addSeparator();
        
        generateCardinality = new JMenuItem("Generate cardinality fragment...");
        generateCardinality.setMnemonic('G');
        generateCardinality.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) 
                                       workspace.getLastSelectedPathComponent();
                    
                if (node == null) return;
                        
                Object fragmentScheme = node.getUserObject();
                                
                if (fragmentScheme instanceof FragmentationSchemeNodeModal) {
                    FragmentationScheme fs =
                            ((FragmentationSchemeNodeModal) fragmentScheme)
                                  .getFragmentationScheme();
                    
                    fs.generateCardinalityFragments();                    
                } // end if                
            }
        });
        
        fragmentSchemePopupMenu.add(generateCardinality);
        
        cleanOverlaps = new JMenuItem("Clean overlap (cardinality) fragments");
        cleanOverlaps.setMnemonic('o');
        cleanOverlaps.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) 
                                       workspace.getLastSelectedPathComponent();
                    
                if (node == null) return;
                        
                Object fragmentScheme = node.getUserObject();
                                
                if (fragmentScheme instanceof FragmentationSchemeNodeModal) {
                    FragmentationScheme fs =
                            ((FragmentationSchemeNodeModal) fragmentScheme)
                                  .getFragmentationScheme();
                    
                    fs.getFragmentList().removeOverlapFragments();
                    fs.getFragmentList().triggerListeners();
                } // end if
            }
        });
        
        fragmentSchemePopupMenu.add(cleanOverlaps);
        
        cleanAllFragments = new JMenuItem("Clean all fragments");
        cleanAllFragments.setMnemonic('a');
        cleanAllFragments.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) 
                                       workspace.getLastSelectedPathComponent();
                    
                if (node == null) return;
                        
                Object fragmentScheme = node.getUserObject();
                                
                if (fragmentScheme instanceof FragmentationSchemeNodeModal) {
                    FragmentationScheme fs =
                            ((FragmentationSchemeNodeModal) fragmentScheme)
                                  .getFragmentationScheme();
                    
                    fs.getFragmentList().removeAllFragments();
                    fs.getFragmentList().triggerListeners();
                } // end if
            }
        });
                
        fragmentSchemePopupMenu.add(cleanAllFragments);

        fragmentSchemePopupMenu.addSeparator();
        
        addDummyAtoms = new JMenuItem("Add dummy atoms...");
        addDummyAtoms.setMnemonic('u');
        addDummyAtoms.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) 
                                       workspace.getLastSelectedPathComponent();
                    
                if (node == null) return;
                        
                Object fragmentScheme = node.getUserObject();
                                
                if (fragmentScheme instanceof FragmentationSchemeNodeModal) {
                    FragmentationScheme fs =
                            ((FragmentationSchemeNodeModal) fragmentScheme)
                                  .getFragmentationScheme();
                    
                    // TODO: .. add UI
                    
                    fs.addDummyAtoms(new Atom("H", 1.0, 
                                              new Point3D(0.0, 0.0, 0.0)),
                       FragmentationScheme.DummyAtomPosition.STD_BOND_DISTANCE);
                    fs.getFragmentList().triggerListeners();
                } // end if                
            }
        });
                
        fragmentSchemePopupMenu.add(addDummyAtoms);
        
        estimateCharges = new JMenuItem("Estimate charges on fragment");
        estimateCharges.setMnemonic('s');
        estimateCharges.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) 
                                       workspace.getLastSelectedPathComponent();
                    
                if (node == null) return;
                        
                Object fragmentScheme = node.getUserObject();
                                
                if (fragmentScheme instanceof FragmentationSchemeNodeModal) {
                    FragmentationScheme fs =
                            ((FragmentationSchemeNodeModal) fragmentScheme)
                                  .getFragmentationScheme();
                    
                    fs.computeCharges();
                    fs.getFragmentList().triggerListeners();
                } // end if                
            }
        });
        
        fragmentSchemePopupMenu.add(estimateCharges);
        
        fragmentSchemePopupMenu.addSeparator();
        
        energyEvaulater = new JMenuItem("Energy evaulater");
        energyEvaulater.setMnemonic('v');
        energyEvaulater.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) 
                                       workspace.getLastSelectedPathComponent();
                    
                if (node == null) return;
                        
                Object fragmentScheme = node.getUserObject();
                                
                if (fragmentScheme instanceof FragmentationSchemeNodeModal) {
                    FragmentationScheme fs =
                            ((FragmentationSchemeNodeModal) fragmentScheme)
                                  .getFragmentationScheme();
                    
                    new EnergyEvaluaterUI(ideInstance, fs);
                } // end if                
            }
        });
        
        fragmentSchemePopupMenu.add(energyEvaulater);
        
        addPopupAssociation(FragmentationSchemeNodeModal.class, 
                            fragmentSchemePopupMenu);
    }
    
    /**
     * show/ hide a fragment
     */
    private void showFragment(boolean showIt) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                                       workspace.getLastSelectedPathComponent();
        
        if (node == null) return;
        
        Object fragmentNode = node.getUserObject();                
        
        // verify that the correct stuff was clicked
        if (fragmentNode instanceof FragmentNodeModal) {
            JInternalFrame activeFrame =
                    ideInstance.getWorkspaceDesktop().getActiveFrame();
            
            // no active frame? .. return quitely
            if (activeFrame == null) return;
            
            // else check to see if we have every thing right?
            // a) the frame is an instance of MoleculeViewerFrame
            // b) the frame contains exactly one MoleculeScene
            // c) the molecule scene contains the molecule of which
            //    the currently selected fragment is a part
            if (activeFrame instanceof MoleculeViewerFrame) {
                MoleculeViewer viewer =
                        ((MoleculeViewerFrame) activeFrame).getMoleculeViewer();
                
                // condition "b" not met? return quitely
                if (viewer.getSceneList().size() != 1) return;
                
                Molecule molecule =
                        viewer.getSceneList().get(0).getMolecule();
                Fragment fragment =
                        ((FragmentNodeModal) fragmentNode).getFragment();
                                
                // condition "c" not met? return quitely
                if (molecule != fragment.getParentMolecule()) return;
                
                // if every thing appears right, show the fragment
                // to do so first find out under which scheme this
                // fragment is present
                viewer.getSceneList().get(0).showFragment(
                    ((FragmentationSchemeNodeModal)
                        (Utility.findImmediateParentNode(node,
                            FragmentationSchemeNodeModal.class)
                        ).getUserObject()
                    ).getFragmentationScheme(),
                    fragment,
                    showIt
                );
                
                ((FragmentNodeModal) fragmentNode).setVisible(showIt);
            } // end if
        } // end if
    }
    
    /**
     * Returns the currectly selected node in workspace panel
     *
     * @return instance of DefaultMutableTreeNode, of the selected node or
     *          null if none is selected.
     */
    public DefaultMutableTreeNode getSelectedNode() {
        return (DefaultMutableTreeNode)workspace.getLastSelectedPathComponent();
    }
    
    /**
     * adds a popup association
     *
     * @param theInvoker - the invoker instance for this popup
     * @param popup - the popup to be displayed on the invoker
     */
    public void addPopupAssociation(Class<?> theInvoker, WorkspacePopup popup) {
        popupAssociation.put(theInvoker, popup);
    }
    
    /**
     * remove a popup association
     *
     * @param theInvoker - the invoker instance whose association is to 
     *                      be removed.
     */
    public void removePopupAssociation(Class<?> theInvoker) {
        popupAssociation.remove(theInvoker);
    }
    
    /** Getter for property currentWorkspace.
     * @return Value of property currentWorkspace.
     *
     */
    public Workspace getCurrentWorkspace() {
        return this.currentWorkspace;
    }
    
    /** The tree maker thread used in initComponents() */
    private Thread treeMaker = null;
    
    /** This method is called to initialize the UI. 
     */
    private void initComponents() {
        removeListeners();
        removeAll();
        
        ringIndex = 0; // init the ring index count
        atomGroupIndex = 0; // init the atom group index count
        
        if (currentWorkspace == null) { 
            workspaceTree = null;
            scriptTree    = null;
            return;
        } // end if
        
        setLayout(new BorderLayout());
        
        // make the initial tree ... as a separate thread
        workspaceTree = new DefaultMutableTreeNode(currentWorkspace);
        scriptTree    = new DefaultMutableTreeNode(new Scripts());
        
        // check if previous update thread is still running?
        if (treeMaker != null && treeMaker.isAlive()) {
            // if yes, wait till it complets
            try {
                treeMaker.join();
            } catch (InterruptedException ignored) {}
        } // end if
        
        treeMaker = new Thread() {
          @Override  
          public void run() {            
            DefaultMutableTreeNode loading = 
                                   new DefaultMutableTreeNode("Loading...");
            
            // indicate user that some thing is on ..
            workspaceTree.add(loading);
            workspace = new JTree(workspaceTree);
            
            workspace.setCellRenderer(new TreeCellRenderer());
            add(new JScrollPane(workspace), BorderLayout.CENTER);
                    
            // TODO: Support for properties
            Iterator wis = currentWorkspace.getWorkspaceItems().iterator();
            
            while (wis.hasNext()) {
                WorkspaceItem wi = (WorkspaceItem) wis.next();
                
                if (wi instanceof MoleculeItem) {
                    ringIndex = 0;
                    Molecule molecule = (Molecule) (((MoleculeItem) wi)
                                                     .getItemData().getData());
                    
                    // TODO: Caution: this is not thread safe
                    // ... may result in undesirable behaviour, but such
                    // situations can rarely occur.
                    listenForChanges(molecule);
                    
                    DefaultMutableTreeNode moleculeTree =
                                           new DefaultMutableTreeNode(molecule);
                    
                    // construct the molecule tree
                    createNodes(moleculeTree, molecule);  
                    workspaceTree.add(moleculeTree);      
                } else if (wi instanceof BeanShellScriptItem) {
                    scriptTree.add(new DefaultMutableTreeNode(wi));
                } else {                
                    // TODO : Support for other types of files
                }// end if                
            } // end while
            
            // finally add the script node 
            workspaceTree.add(scriptTree);  
            
            // over ! we can display the stuff
            workspaceTree.remove(loading);                       
            workspace.setModel(new DefaultTreeModel(workspaceTree));
            // enable tool tips
            ToolTipManager.sharedInstance().registerComponent(workspace);
            
            // add the listeners .. for handling dynamic popup
            workspace.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent me) {                    
                    if (me.isPopupTrigger()) {
                        showPopup(me);
                        me.consume();
                    } // end if
                }
                
                @Override
                public void mouseReleased(MouseEvent me) {
                    if (me.isPopupTrigger()) {
                        showPopup(me);
                        me.consume();
                    } // end if                    
                }
                
                @Override
                public void mouseClicked(MouseEvent me) {
                    if (me.isPopupTrigger()) {
                        showPopup(me);
                        me.consume();
                    } else {
                        selectObject(me);
                    } // end if 
                }
                
                /**
                 * select the object, in actual view if possible
                 */
                public void selectObject(MouseEvent me) {
                    int row = workspace.getRowForLocation(me.getX(), me.getY());
                    
                    if (row < 0) { 
                        workspace.setSelectionRow(-1);
                        return;  // no selection
                    } // end if
                    
                    // do the selection if required
                    workspace.setSelectionRow(row);
                    
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) 
                                       workspace.getLastSelectedPathComponent();
                    
                    if (node == null) return;
                    
                    Enumeration theInvokers = popupAssociation.keys();
                    Object userObject = node.getUserObject();
                                      
                    // get the currently active frame
                    if (ideInstance == null) return;
                    
                    JInternalFrame jf = 
                            ideInstance.getWorkspaceDesktop().getActiveFrame();
                    
                    // check if the viewer is of interest
                    if (!(jf instanceof MoleculeViewerFrame)) return;
                    
                    MoleculeViewerFrame mf = (MoleculeViewerFrame) jf;
                    
                    Vector<MoleculeScene> scenes = mf.getSceneList();
                    
                    // select objects if visible
                    if (userObject instanceof AtomNodeModal) {
                        AtomNodeModal atm = (AtomNodeModal) userObject;
                        
                        Molecule mol = atm.getMolecule();
                        
                        for(int i=0; i<scenes.size(); i++) {
                            if (scenes.get(i).getMolecule() == mol) {
                                scenes.get(i).selectIfContainedIn(
                                            atm.getAtom().getAtomCenter(), 0.1);
                                mf.repaint();
                                break;
                            } // end if
                        } // end for
                    } else if (userObject instanceof RingNodeModal) {
                        RingNodeModal ring = (RingNodeModal) userObject;
                        
                        Molecule mol = ring.getMolecule();
                        
                        for(int i=0; i<scenes.size(); i++) {
                            if (scenes.get(i).getMolecule() == mol) {
                                Iterator<Integer> ais 
                                        = ring.getRing().getAtomIndices();
                                Atom atm = mol.getAtom(ais.next());
                                
                                scenes.get(i).selectIfContainedIn(
                                               atm.getAtomCenter(), 0.1);
                                
                                while(ais.hasNext()) {
                                   atm = mol.getAtom(ais.next());
                                   
                                   scenes.get(i).selectIfContainedIn(
                                               atm.getAtomCenter(), 0.1, false);
                                } // end for
                                
                                mf.repaint();
                                break;
                            } // end if
                        } // end for
                    } else if (userObject instanceof AtomGroupNodeModal) {
                        AtomGroupNodeModal atmGroup = 
                                            (AtomGroupNodeModal) userObject;
                        
                        Molecule mol = atmGroup.getMolecule();
                        
                        for(int i=0; i<scenes.size(); i++) {
                            if (scenes.get(i).getMolecule() == mol) {
                                Iterator<Integer> ais 
                                     = atmGroup.getAtomGroup().getAtomIndices();
                                Atom atm = mol.getAtom(ais.next());
                                
                                scenes.get(i).selectIfContainedIn(
                                               atm.getAtomCenter(), 0.1);
                                
                                while(ais.hasNext()) {
                                   atm = mol.getAtom(ais.next());
                                   
                                   scenes.get(i).selectIfContainedIn(
                                               atm.getAtomCenter(), 0.1, false);
                                } // end for
                                
                                mf.repaint();
                                break;
                            } // end if
                        } // end for
                    } else if (userObject instanceof FragmentNodeModal) {
                        FragmentNodeModal fragment
                                = (FragmentNodeModal) userObject;
                        
                        showFragment(!fragment.isVisible());
                    } // end if
                    
                    workspace.updateUI();
                }
                
                /**
                 * method to show the popup
                 */
                public void showPopup(MouseEvent me) {       
                    int row = workspace.getRowForLocation(me.getX(), me.getY());
                    
                    if (row < 0) { 
                        workspace.setSelectionRow(-1);
                        return;  // no selection
                    } // end if
                    
                    // do the selection if required
                    workspace.setSelectionRow(row);
                    
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) 
                                       workspace.getLastSelectedPathComponent();
                    
                    if (node == null) return;
                    
                    Enumeration theInvokers = popupAssociation.keys();
                    Object userObject = node.getUserObject();
                    Class theInvoker;
                    
                    // for each invoker, check the type and its association
                    // and then invoke the appropriate menu
                    while(theInvokers.hasMoreElements()) {
                        theInvoker = (Class) theInvokers.nextElement();
                                                
                        if (theInvoker.isInstance(userObject)) {
                            // set up the invoker
                            ((WorkspacePopup) popupAssociation.get(theInvoker))
                                    .setSourceWorkspaceItem(userObject);
                            
                            // update the popups if required
                            ((WorkspacePopup) popupAssociation.get(theInvoker))
                                    .updatePopupMenues();
                            
                            // and then show the popup
                            ((WorkspacePopup) popupAssociation.get(theInvoker))
                                    .show(workspace, me.getX(), me.getY());
                            break;
                        } // end if
                    } // end while
                }
            });
            
            updateUI();
          }
        };
        
        treeMaker.setName("WorkspacePanel tree maker Thread");
        treeMaker.start(); // start making the tree, which may take long time!
    }
    
    /**
     * register listing ... for changes in the molecule object needs
     * to be reflected in the tree.
     */
    private synchronized void listenForChanges(Molecule molecule) {        
        moleculeStateChangeListener = new MoleculeStateChangeListener() {
             @Override
             public void moleculeChanged(MoleculeStateChangeEvent msce) {
                 System.out.println("Molecule Changed : " + msce);
                 
                 // TODO : more effitient
                 if (msce.getEventType() 
                        != MoleculeStateChangeEvent.FRAGMENTS_MODIFIED) {
                     initComponents();
                 } else {
                     // only update the fragment tree
                     FragmentListChangeEvent fe 
                             = msce.getFragmentListChangeEvent();
                     Enumeration treeNodes;
                     DefaultMutableTreeNode treeNode, 
                                            mainFrag = null, overFrag = null;
                     boolean foundTarget;
                     
                     switch(fe.getType()) {
                         case FRAGMENT_MODIFIED:
                             treeNodes = workspaceTree.depthFirstEnumeration();

                             mainFrag = null;
                             foundTarget = false;
                             
                             while(treeNodes.hasMoreElements()) {
                                treeNode = (DefaultMutableTreeNode) 
                                               treeNodes.nextElement();
                                if (treeNode.getUserObject() 
                                     instanceof FragmentationSchemeNodeModal) {
                                    if (((FragmentationSchemeNodeModal)
                                           treeNode.getUserObject())
                                        .getFragmentationScheme()
                                        == fe.getAffectedFragmentationScheme()){
                                        foundTarget = true;
                                    } // end if
                                } // end if 
                                
                                if (treeNode.getUserObject() 
                                    instanceof FragmentNodeModal) {
                                   if (((FragmentNodeModal) 
                                        treeNode.getUserObject())
                                       .getFragment()
                                       == fe.getAffectedFragment()) {
                                       mainFrag = 
                                        (DefaultMutableTreeNode)
                                           treeNode;
                                   } // end if                           
                                } // end if
                                
                                if ((mainFrag != null)
                                    && foundTarget) {
                                   break;
                                } // end if
                             } // end while
                                         
                             if (mainFrag != null) mainFrag.removeAllChildren();
                             makeFragmentTree((Molecule) msce.getSource(),
                                            fe.getAffectedFragment(),
                                            mainFrag);
                             
                             workspace.updateUI();
                             break;
                         case FRAGMENT_DELETED:
                             // TODO : more effitient
                             treeNodes = workspaceTree.depthFirstEnumeration();
                             
                             foundTarget = false;
                             treeNode = null;
                             
                             while(treeNodes.hasMoreElements()) {
                                treeNode = (DefaultMutableTreeNode) 
                                               treeNodes.nextElement();
                                if (treeNode.getUserObject() 
                                     instanceof FragmentationSchemeNodeModal) {
                                    if (((FragmentationSchemeNodeModal)
                                           treeNode.getUserObject())
                                        .getFragmentationScheme()
                                        == fe.getAffectedFragmentationScheme()){
                                        foundTarget = true;
                                        break;
                                    } // end if
                                } // end if
                             } // end while
                             
                             if (foundTarget) {
                                 if (treeNode != null) {
                                    treeNode.removeAllChildren();
                                 
                                    makeFragmentationSchemeTree(
                                         (Molecule) msce.getSource(), 
                                         fe.getAffectedFragmentationScheme(),
                                         treeNode);
                                 } // end if
                             } // end if
                             
                             workspace.updateUI();
                             break;
                         case FRAGMENT_ADDED:
                             treeNodes = workspaceTree.depthFirstEnumeration();

                             mainFrag = null;
                             overFrag = null; 
                             foundTarget = false;
                             
                             while(treeNodes.hasMoreElements()) {
                                treeNode = (DefaultMutableTreeNode) 
                                               treeNodes.nextElement();
                                if (treeNode.getUserObject() 
                                     instanceof FragmentationSchemeNodeModal) {
                                    if (((FragmentationSchemeNodeModal)
                                           treeNode.getUserObject())
                                        .getFragmentationScheme()
                                        == fe.getAffectedFragmentationScheme()){
                                        foundTarget = true;
                                    } // end if
                                } // end if 
                                
                                if (treeNode.getUserObject() instanceof String){
                                   if (((String) 
                                        treeNode.getUserObject())
                                       .equals("Main Fragments")) {
                                       mainFrag = 
                                        (DefaultMutableTreeNode)
                                           treeNode;
                                   } // end if

                                   if (((String) 
                                        treeNode.getUserObject())
                                       .equals("Overlap Fragments")) {
                                       overFrag = 
                                        (DefaultMutableTreeNode)
                                           treeNode;
                                   } // end if
                                } // end if
                                
                                if ((mainFrag != null)
                                    && (overFrag != null)
                                    && foundTarget) {
                                   break;
                                } // end if
                             } // end while
                             
                             addFragmentToTree((Molecule) msce.getSource(),
                                            fe.getAffectedFragment(),
                                            fe.getAffectedFragmentationScheme()
                                              .getFragmentList().size()-1,
                                            mainFrag, overFrag);
                             
                             workspace.updateUI();
                             break;
                         default:
                             break;
                     } // end of switch .. case
                 } // end if
             }
        };
        
        molecule.addMoleculeStateChangeListener(moleculeStateChangeListener);
        
        if (listenToList == null) listenToList = new ArrayList<Molecule>(10);
        listenToList.add(molecule);
    }
    
    /**
     * remove any registered listeners
     */
    private synchronized void removeListeners() {
        if (listenToList != null) {
            Iterator listeners = listenToList.iterator();
            
            while(listeners.hasNext()) {
              ((Molecule) listeners.next())
                .removeMoleculeStateChangeListener(moleculeStateChangeListener);
            } // end while
            
            listenToList = null;
        } // end if
    }
    
    /**
     * update the UI to reflect this new workspace.
     *
     * @param workspace - instance of the new Workspace
     */
    public void update(Workspace workspace) {
        this.currentWorkspace = workspace;        
        
        // update UI        
        initComponents();
    }
    
    /**
     * update the UI 
     */
    public void updatePanel() {
        // update UI        
        initComponents();
    }
    
    /**
     * Create the molecule tree.
     */
    private void createNodes(DefaultMutableTreeNode top, Molecule molecule) {
        DefaultMutableTreeNode atomCategory = null, ringCategory = null,
                               atomGroupCategory = null, 
                               fragmentationSchemesCategory = null;
        DefaultMutableTreeNode theAtom  = null, theRing  = null, 
                               theAtomGroup = null, theFragment = null;
        
        // the atoms only category
        atomCategory = new DefaultMutableTreeNode("Atoms");
        
        // add the atoms
        Iterator<Atom> atoms = molecule.getAtoms();
        
        while(atoms.hasNext()) {
          theAtom = new DefaultMutableTreeNode(
                               new AtomNodeModal(atoms.next(), molecule));
          atomCategory.add(theAtom);
        } // end while
        
        // finally add the atom category to the top level
        top.add(atomCategory);
        
        // the rings only category
        ringCategory = new DefaultMutableTreeNode("Rings");
        
        // add the rings
        Iterator<AtomGroup> rings = molecule.getRingRecognizer().getGroupList()
                                                                .getGroups();
        while(rings.hasNext()) {
            Ring ring = (Ring) rings.next();
            
            theRing = new DefaultMutableTreeNode(
                               new RingNodeModal(ring, molecule));
            // add the ring atoms to this node
            // TODO: is this memory expensive? i guess so
            for(int i=0; i<ring.getSize(); i++) {
                theRing.add(new DefaultMutableTreeNode(
                  new AtomNodeModal(molecule.getAtom(ring.getAtomIndexAt(i)),
                                    molecule)));
            } // end for
            
            ringCategory.add(theRing);
        }
        
        // add the ring category to the top level
        top.add(ringCategory);
        
        // atom groups
        atomGroupCategory = new DefaultMutableTreeNode("Atom Groups");
        
        // add the groups
        Iterator<SpecialStructureRecognizer> ssrlist = 
                                     molecule.getSpecialStructureRecognizers();
        
        while(ssrlist.hasNext()) {
          SpecialStructureRecognizer ssr = ssrlist.next();
        
          // rings have already been addressed
          if (ssr instanceof RingRecognizer) continue;
          
          Iterator<AtomGroup> groups = ssr.getGroupList().getGroups();
          
          while(groups.hasNext()) {
            AtomGroup atomGroup = groups.next();
            
            theAtomGroup = new DefaultMutableTreeNode(
                                   new AtomGroupNodeModal(atomGroup, molecule));
            // add the ring atoms to this node
            // TODO: is this memory expensive? i guess so
            for(int i=0; i<atomGroup.getSize(); i++) {
                theAtomGroup.add(new DefaultMutableTreeNode(
                  new AtomNodeModal(molecule.getAtom(
                                                atomGroup.getAtomIndexAt(i)),
                                    molecule)));
            } // end for
            
            atomGroupCategory.add(theAtomGroup);
          } // end while
        } // end while
        
        // add the atom group to the top level
        top.add(atomGroupCategory);
        
        // fragmentation schemes
        fragmentationSchemesCategory = new DefaultMutableTreeNode(
                                                  "Fragmentation Schemes");
        Iterator<FragmentationScheme> schemes = 
                                      molecule.getAllFragmentationSchemes();
        FragmentationScheme scheme;
        
        while(schemes.hasNext()) {
            scheme = schemes.next();
            DefaultMutableTreeNode schemeCategory = new DefaultMutableTreeNode(
                                      new FragmentationSchemeNodeModal(scheme));
            
            makeFragmentationSchemeTree(molecule, scheme, schemeCategory);
            
            // add this scheme
            fragmentationSchemesCategory.add(schemeCategory);
        } // end while
        
        // add the fragmentation schemes to the top level
        top.add(fragmentationSchemesCategory);
    }
    
    /**
     * make fragmetation scheme tree
     */
    private void makeFragmentationSchemeTree(Molecule molecule, 
                                  FragmentationScheme scheme,
                                  DefaultMutableTreeNode schemeCategory) {
        // add the fragments first
        Iterator<Fragment> fragments = scheme.getFragmentList()
                                             .getFragments();
        Fragment fragment;
        int index = 0;

        DefaultMutableTreeNode fragmentsCategory = 
                               new DefaultMutableTreeNode("Fragments");

        DefaultMutableTreeNode mainFragmentsCategory = 
                            new DefaultMutableTreeNode("Main Fragments");

        DefaultMutableTreeNode overlapFragmentsCategory = 
                            new DefaultMutableTreeNode("Overlap Fragments");

        while(fragments.hasNext()) {
            fragment = fragments.next();

            addFragmentToTree(molecule, fragment, index++,
                           mainFragmentsCategory, overlapFragmentsCategory);
        } // end while

        fragmentsCategory.add(mainFragmentsCategory);
        fragmentsCategory.add(overlapFragmentsCategory);

        // add this to scheme category
        schemeCategory.add(fragmentsCategory);

        // TODO: the goodness probes need to be added
        DefaultMutableTreeNode goodnessProbesCategory = 
                              new DefaultMutableTreeNode("Goodness Probes");

        schemeCategory.add(goodnessProbesCategory);

        // TODO: the corrector constraints need to be added
        DefaultMutableTreeNode constraintsCategory = 
                        new DefaultMutableTreeNode("Corrector Constraints");

        schemeCategory.add(constraintsCategory);
    }
    
    /**
     * add a fragment to the wrkspace tree
     */
    private void addFragmentToTree(Molecule molecule, 
                              Fragment fragment, int index,
                              DefaultMutableTreeNode mainFragmentsCategory,
                              DefaultMutableTreeNode overlapFragmentsCategory) {
        
        DefaultMutableTreeNode theFragment = new DefaultMutableTreeNode(
                                    new FragmentNodeModal(fragment, index));
                
        makeFragmentTree(molecule, fragment, theFragment);

        // add the fragment
        if (fragment.isOverlapFragment()) {
            overlapFragmentsCategory.add(theFragment);
        } else {
            mainFragmentsCategory.add(theFragment);
        } // end if
    }
    
    /**
     * make the fragment tree
     */
    private void makeFragmentTree(Molecule molecule, Fragment fragment,
                                  DefaultMutableTreeNode theFragment) {
        
        if (theFragment == null) return;
        
        Iterator<FragmentAtom> fAtoms = fragment.getFragmentAtoms();
        FragmentAtom fAtom;

        // create three entries for normal, boundary and dummy atoms
        DefaultMutableTreeNode fragmentAtoms = 
                new DefaultMutableTreeNode("Fragment Atoms");
        DefaultMutableTreeNode boundaryAtoms = 
                new DefaultMutableTreeNode("Boundary Atoms");
        DefaultMutableTreeNode dummyAtoms = 
                new DefaultMutableTreeNode("Dummy Atoms");

        while(fAtoms.hasNext()) {
            fAtom = fAtoms.next();

            if (fAtom.isDummy()) {
                dummyAtoms.add(new DefaultMutableTreeNode(
                                   new AtomNodeModal(fAtom, molecule)));
            } else if (fAtom.isBoundaryAtom()) {
                boundaryAtoms.add(new DefaultMutableTreeNode(
                                   new AtomNodeModal(fAtom, molecule)));
                fragmentAtoms.add(new DefaultMutableTreeNode(
                                   new AtomNodeModal(fAtom, molecule)));
            } else {
                fragmentAtoms.add(new DefaultMutableTreeNode(
                                   new AtomNodeModal(fAtom, molecule)));
            } // end if
        } // end while

        // add the fragment atoms
        theFragment.add(fragmentAtoms);
        theFragment.add(boundaryAtoms);
        theFragment.add(dummyAtoms);
    }
    
    /**
     * Inner class to represent cell rendering.
     */
    public class TreeCellRenderer extends DefaultTreeCellRenderer {
        private ImageIcon nodeIcon;
        
        public TreeCellRenderer() {
        }
        
        @Override
        public Component getTreeCellRendererComponent(JTree tree,
                Object value, boolean sel, boolean expanded, boolean leaf,
                int row, boolean hasFocus) {
                        
            super.getTreeCellRendererComponent(tree, value, sel,
                                     expanded, leaf, row, hasFocus);
            
            // set the icons accordingly!
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            value = node.getUserObject();
            
            ImageResource images = ImageResource.getInstance();
            
            if (value instanceof String) {
                if (((String) value).equals("Loading...")) {  
                    setIcon(null);
                    setToolTipText("Loading the workspace, please wait...");
                } else if (((String) value).equals("Atoms")) {  
                    setIcon(images.getAtoms());
                    setToolTipText("List of atoms in this molecule");
                } else if (((String) value).equals("Rings")) {  
                    setIcon(images.getRing());
                    setToolTipText("List of rings in this molecule");
                } else if (((String) value).equals("Atom Groups")) {  
                    setIcon(images.getAtomGroup());
                    setToolTipText(
                           "List of atom (functional) groups in this molecule");
                } else if (((String) value).equals("Fragmentation Schemes")) {  
                    setIcon(images.getFragmentationSchemes());
                    setToolTipText(
                           "Fragmentation schemes used for this molecule");
                } else if (((String) value).equals("Fragments")) {  
                    setIcon(images.getFragments());
                    setToolTipText("Fragments in this fragmentation scheme");
                } else if (((String) value).equals("Main Fragments")) {  
                    setIcon(images.getMainFragments());
                    setToolTipText("Main Fragments");
                } else if (((String) value).equals("Overlap Fragments")) {  
                    setIcon(images.getOverlapFragments());
                    setToolTipText("Overlap Fragments");
                } else if (((String) value).equals("Fragment Atoms")) {  
                    setIcon(images.getAtoms());
                    setToolTipText("List of fragment atoms in this fragment");
                } else if (((String) value).equals("Boundary Atoms")) {  
                    setIcon(images.getAtoms());
                    setToolTipText("List of boundary atoms in this fragment");
                } else if (((String) value).equals("Dummy Atoms")) {  
                    setIcon(images.getAtoms());
                    setToolTipText("List of dummy atoms in this fragment");
                } else if (((String) value).equals("Goodness Probes")) {  
                    setIcon(images.getGoodnessProbes());
                    setToolTipText("List of goodness probes that can be " +
                            "applied to this fragmentation scheme");
                } else if (((String) value).equals("Corrector Constraints")) {  
                    setIcon(images.getCorrectorConstraints());
                    setToolTipText("List of corrector constraints that " +
                            "can be applied to this fragmentation scheme");
                } else { 
                    setToolTipText(null);
                } // end if                
                
                return this;
            } else if (value instanceof Molecule) {
                setIcon(images.getMolecule());
                setToolTipText("<html><body><pre>"                    
                   + ((Molecule) value).toExtendedString()
                   + "</pre></body></html>");
            } else if (value instanceof Workspace) {
                setIcon(images.getWorkspace());
                setToolTipText("<html><body><pre>" +
                   ((Workspace) value).toExtendedString()
                   + "</pre></body></html>");
            } else if (value instanceof RingNodeModal) {
                RingNodeModal ringValue = (RingNodeModal) value;
                
                setIcon((ringValue.getRing().isPlanar() 
                          ? images.getPlanarRing() 
                          : images.getNonPlanarRing()));
                          
                setToolTipText(ringValue.toExtendedString());
            } else if (value instanceof AtomGroupNodeModal) {
                AtomGroupNodeModal atomGroup = (AtomGroupNodeModal) value;
                
                setIcon(images.getAtomGroup());
                          
                setToolTipText(atomGroup.toExtendedString());
            } else if (value instanceof Scripts) { 
                setIcon(images.getScriptFolder());
                setToolTipText("All the scripts in the workspace");
            } else if (value instanceof BeanShellScriptItem) { 
                setIcon(images.getIconFor(new File(
                        ((BeanShellScriptItem) value).getWorkspaceItemFile())));
                setToolTipText("<html><body><pre>" +
                                ((BeanShellScriptItem) value).toExtendedString()
                               + "</pre></body></html>");
            } else if (value instanceof FragmentationSchemeNodeModal) {
                FragmentationSchemeNodeModal scheme = 
                        (FragmentationSchemeNodeModal) value;
                
                setIcon(images.getFragmentationScheme());
                setToolTipText(scheme.toExtendedString());
            } else if (value instanceof FragmentNodeModal) {
                FragmentNodeModal fragment = (FragmentNodeModal) value;

                setIcon(images.getFragment());
                setToolTipText(fragment.toExtendedString());                
            } // end if
           
            if (! (value instanceof AtomNodeModal)) return this;
                
            nodeIcon = images.getAtomIconFor(((AtomNodeModal) value)
                                                              .getSymbol());
            
            setToolTipText(((AtomNodeModal) value).toExtendedString());
            
            if (leaf && (nodeIcon != null)) {
                setIcon(nodeIcon);                
            } // end if
            
            return this;
        }
    } // end of inner class TreeCellRendering    
    
    /**
     * Inner class to wrap the behavious of all scripts
     */
    private class Scripts {
        
        public Scripts() { }

        @Override
        public String toString() {
            return "Scripts";
        }
    } // end of class Scripts
    
    /**
     * Inner class to represent the tree node data for an atom.
     */
    private class AtomNodeModal {
        private Atom node;
        private Molecule molecule;
                
        public AtomNodeModal(Atom atom, Molecule molecule) {
            node = atom;     
            this.molecule = molecule;
        } // end constructor               
        
        public Molecule getMolecule() {
            return molecule;
        }
        
        public Atom getAtom() {
            return node;
        } // end of method getFile()
        
        @Override
        public String toString() {
            return node.getSymbol() + " ( " + node.getIndex() + " ) ";
        } // end of overridden method toString()
        
        public String getSymbol() {
            return node.getSymbol();
        } // end of overridden method toString()
        
        public String toExtendedString() {
            StringBuffer sb = new StringBuffer();
            
            sb.append("<html> <head> </head> <body>");
            sb.append("<center><u><b>" + node.getSymbol());
            sb.append(" (" + atomInfo.getName(node.getSymbol()) + ")");
            sb.append("</b></u></center>");
            sb.append("<table border=\"0\">");
            sb.append("<tr> <td> Atom Index : </td>");
            sb.append("<td>" + node.getIndex() + "</td> </tr>");
            sb.append("<tr> <td> Atom Position : </td>");
            sb.append("<td>(" + node.getX() + ", " + node.getY() + ", ");
            sb.append(node.getZ() + ")</td> </tr>");
            sb.append("<tr> <td> Bonding Info. : </td>");
            sb.append("<td>" + node.getConnectedList() + "</td> </tr>");
            sb.append("</table> </body> </html>");
            
            return sb.toString();
        } // end of overridden method toString()
    } // end of inner class AtomNodeModal
    
    /**
     * Inner class to represent the tree node data for a ring.
     */    
    private class RingNodeModal {
        private Ring node;                
        private Molecule molecule;
        
        private int index;
        
        public RingNodeModal(Ring ring, Molecule molecule) {
            node = ring;
            this.molecule = molecule;
            
            index = ringIndex++;
        } // end constructor               
        
        public Molecule getMolecule() {
            return molecule;
        }
        
        public Ring getRing() {
            return node;
        } // end of method getFile()

        @Override
        public String toString() {
            return "Ring # " + index;
        } // end of overridden method toString()
               
        public String toExtendedString() {
            StringBuffer sb = new StringBuffer();
            
            sb.append("<html> <head> </head> <body>");
            sb.append("<center><u><b>" + toString() + "</center>");
            sb.append("<table border=\"0\">");
            sb.append("<tr> <td> Ring type : </td>");
            sb.append("<td>" + node.getRingType() + "</td> </tr>");
            sb.append("<tr> <td> Number of ring atoms : </td>");
            sb.append("<td>" + node.getSize() + "</td> </tr>");
            sb.append("<tr> <td> Ring atoms : </td>");
            sb.append("<td>" + node.getAtomList() + "</td> </tr>");
            sb.append("</table> </body> </html>");
            
            return sb.toString();
        } // end of overridden method toString()
    } // end of inner class RingNodeModal
    
    
    /**
     * Inner class to represent the tree node data for an atom group.
     */    
    private class AtomGroupNodeModal {
        private AtomGroup node;                
        
        private int index;
        
        private Molecule molecule;       
        
        public AtomGroupNodeModal(AtomGroup ag, Molecule molecule) {
            node = ag;
            
            index = atomGroupIndex++;
            
            this.molecule = molecule;
        } // end constructor               
        
        public AtomGroup getAtomGroup() {
            return node;
        } // end of method getFile()

        @Override
        public String toString() {
            return "AtomGroup # " + index;
        } // end of overridden method toString()
               
        public String toExtendedString() {
            StringBuffer sb = new StringBuffer();
            
            sb.append("<html> <head> </head> <body>");
            sb.append("<center><u><b>" + toString() + "</center>");
            sb.append("<table border=\"0\">");            
            sb.append("<tr> <td> Number of group atoms : </td>");
            sb.append("<td>" + node.getSize() + "</td> </tr>");
            sb.append("<tr> <td> Group atoms : </td>");
            sb.append("<td>" + node.getAtomList() + "</td> </tr>");
            sb.append("</table> </body> </html>");
            
            return sb.toString();
        } // end of overridden method toString()
        
        public Molecule getMolecule() {
            return molecule;
        }
    } // end of inner class AtomGroupNodeModal
    /**
     * Inner class to represent the tree node data for a fragmentation scheme.
     */
    public class FragmentationSchemeNodeModal {
        private FragmentationScheme node;
                
        public FragmentationSchemeNodeModal(FragmentationScheme fScheme) {
            node = fScheme;
        } // end constructor  
        
        public FragmentationScheme getFragmentationScheme() {
            return node;
        } // end of method getFragmentationScheme()

        @Override
        public String toString() {
            return "Fragmentation Scheme: " + node.toString();
        } // end of overridden method toString()
               
        public String toExtendedString() {
            StringBuffer sb = new StringBuffer();
            
            sb.append("<html> <head> </head> <body>");
            sb.append("<center><u><b>" + toString() + "</center>");
            sb.append("<table border=\"0\">");            
            sb.append("<tr> <td> Number of fragments : </td>");
            sb.append("<td>" + node.getFragmentList().size() + "</td> </tr>");            
            sb.append("</table> </body> </html>");
            
            return sb.toString();
        } // end of overridden method toString()
    } // end of inner class FragmentationScheme
    
    /**
     * Inner class to represent the tree node data for a fragment scheme.
     */
    public class FragmentNodeModal {
        private Fragment node;
        private int index;
                
        public FragmentNodeModal(Fragment fragment, int index) {
            this.node  = fragment;
            this.index = index;
            this.visible = false;
        } // end constructor  
        
        public Fragment getFragment() {
            return node;
        } // end of method getFragment()

        @Override
        public String toString() {
            if (!visible) {
                return "Fragment # " + index;
            } else {
                return "<html><body><b>Fragment # " 
                        + index + "</b></body></html>";
            } // end if
        } // end of overridden method toString()
               
        public String toExtendedString() {
            StringBuffer sb = new StringBuffer();
            
            sb.append("<html> <head> </head> <body>");
            sb.append("<center><u><b>" + toString() + "</center>");
            sb.append("<table border=\"0\">");            
            sb.append("<tr> <td> Number of atoms : </td>");
            sb.append("<td>" + node.getTotalNumberOfAtoms() 
                      + "</td> </tr>");  
            sb.append("<tr> <td> Number of boundary atoms : </td>");
            sb.append("<td>" + node.getNumberOfBoundaryAtoms() 
                      + "</td> </tr>"); 
            sb.append("<tr> <td> Number of dummy atoms : </td>");
            sb.append("<td>" + node.getNumberOfDummyAtoms() 
                      + "</td> </tr>");
            sb.append("<tr> <td> Cardinality sign : </td>");
            sb.append("<td>" + node.getCardinalitySign()
                      + "</td> </tr>");
            sb.append("<tr> <td> Charge : </td>");
            sb.append("<td>" + node.getTotalCharge()
                      + "</td> </tr>");
            sb.append("</table> </body> </html>");
            
            return sb.toString();
        } // end of overridden method toString()

        /**
         * Holds value of property visible.
         */
        private boolean visible;

        /**
         * Getter for property visible.
         * @return Value of property visible.
         */
        public boolean isVisible() {
            return this.visible;
        }

        /**
         * Setter for property visible.
         * @param visible New value of property visible.
         */
        public void setVisible(boolean visible) {
            this.visible = visible;
        }
    } // end of inner class Fragment
    
    /**
     * A spelized popup menu for the WorkspacePanel
     */
    public class WorkspacePopup extends JPopupMenu {
        
        /** Holds value of property sourceWorkspaceItem. */
        private Object sourceWorkspaceItem;
        
        /** Creates new instance of WorkspacePopup */
        public WorkspacePopup() {
            super();
        }
        
        /** Creates new instance of WorkspacePopup 
         *
         * @param label - the lable for this popup menu
         */
        public WorkspacePopup(String label) {
            super(label);
        }        
        
        /** Getter for property sourceWorkspaceItem.
         * @return Value of property sourceWorkspaceItem.
         *
         */
        public Object getSourceWorkspaceItem() {
            return this.sourceWorkspaceItem;
        }
        
        /** Setter for property sourceWorkspaceItem.
         * @param sourceWorkspaceItem New value of property sourceWorkspaceItem.
         *
         */
        public void setSourceWorkspaceItem(Object sourceWorkspaceItem) {
            this.sourceWorkspaceItem = sourceWorkspaceItem;
        }
        
        /**
         * method to update the popups
         */
        public void updatePopupMenues() {            
        }
    } // end of inner class WorkspacePopup
    
    /**
     * A spelized popup menu for handling the fragments
     */
    public class FragmentWorkspacePopup extends WorkspacePopup {
        
        /** Creates new instance of FragmentWorkspacePopup */
        public FragmentWorkspacePopup() {
            super();
        }
        
        /** Creates new instance of WorkspacePopup 
         *
         * @param label - the lable for this popup menu
         */
        public FragmentWorkspacePopup(String label) {
            super(label);
        }   
        
        /**
         * method to update the popups
         */
        @Override
        public void updatePopupMenues() {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) 
                                       workspace.getLastSelectedPathComponent();
            Object source = getSourceWorkspaceItem();
            
            if (source instanceof FragmentNodeModal) {
                // get the fragment instance
                Fragment fragment =
                        ((FragmentNodeModal) source).getFragment();
                
                // get the scheme associated with this fragment
                FragmentationScheme fc = ((FragmentationSchemeNodeModal)
                    (Utility.findImmediateParentNode(node,
                            FragmentationSchemeNodeModal.class)
                    ).getUserObject()
                ).getFragmentationScheme();
                
                // TODO: the following is not very great coding style or design
                // but is just a workaround
                
                // check if we have the correct active frame available?
                JInternalFrame activeFrame =
                    ideInstance.getWorkspaceDesktop().getActiveFrame();
            
                // no active frame? .. return quitely
                if (activeFrame == null) {
                    viewFragmnetInActiveFrame.setEnabled(false);
                    hideFragmnetInActiveFrame.setEnabled(false);
                    return;
                } // end if                                
                
                // is it the correct active frame?
                if (activeFrame instanceof MoleculeViewerFrame) {
                    MoleculeViewer viewer =
                       ((MoleculeViewerFrame) activeFrame).getMoleculeViewer();

                    // more than one scenes, return quitely
                    if (viewer.getSceneList().size() != 1) {
                        viewFragmnetInActiveFrame.setEnabled(false);
                        hideFragmnetInActiveFrame.setEnabled(false);
                        return;
                    }

                    Molecule molecule =
                             viewer.getSceneList().get(0).getMolecule();

                    // condition "c" not met? return quitely
                    if (molecule != fragment.getParentMolecule()) return;

                    // if every thing appears right, show the fragment
                    // to do so first find out under which scheme this
                    // fragment is present
                    boolean isFragmentVisible = viewer.getSceneList().get(0)
                                               .isFragmentVisible(fc, fragment);
                    
                    viewFragmnetInActiveFrame.setEnabled(!isFragmentVisible);
                    hideFragmnetInActiveFrame.setEnabled(isFragmentVisible);
                } else {
                    viewFragmnetInActiveFrame.setEnabled(false);
                    hideFragmnetInActiveFrame.setEnabled(false);
                } // end if
            } else {
                viewFragmnetInActiveFrame.setEnabled(false);
                hideFragmnetInActiveFrame.setEnabled(false);
            } // end if
        }
    } // end of inner class FragmentWorkspacePopup
    
} // end of class WorkspacePanel
