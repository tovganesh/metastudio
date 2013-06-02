/*
 * ViewerPopupMenu.java
 *
 * Created on February 11, 2004, 6:53 AM
 */
package org.meta.shell.idebeans.viewers.impl.moleculeviewer;

import java.awt.Container;
import java.util.*;
import java.awt.event.*;

import javax.swing.*;

import org.meta.math.MathUtil;
import org.meta.common.Utility;
import org.meta.math.mm.UniversalForceFieldMethod;
import org.meta.math.optimizer.impl.ConjugateGradientOptimizer;
import org.meta.molecule.Atom;
import org.meta.molecule.AtomGroup;
import org.meta.molecule.AtomGroupList;
import org.meta.molecule.BondType;
import org.meta.molecule.Molecule;
import org.meta.molecule.MoleculeBuilder;
import org.meta.shell.idebeans.LogWindow;
import org.meta.molecule.impl.DefaultAtomGroup;
import org.meta.moleculereader.MoleculeFileReader;
import org.meta.moleculereader.MoleculeFileReaderFactory;
import org.meta.parallel.SimpleAsyncTask;
import org.meta.shell.ide.MeTA;
import org.meta.shell.idebeans.IDEFileChooser;
import org.meta.shell.idebeans.IDEFileFilter;
import org.meta.shell.idebeans.IDEFileView;
import org.meta.shell.idebeans.MoleculeEditorFrame;
import org.meta.shell.idebeans.MoleculeViewerFrame;
import org.meta.shell.idebeans.propertysheet.IDEPropertySheetUI;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.event.FragmentAtomEditEvent;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.event.FragmentAtomEditListener;
import org.meta.shell.idebeans.viewers.impl.moleculeviewer.undo.*;

/**
 * The popup menu for the molecule viewer.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class ViewerPopupMenu extends JPopupMenu
                             implements FragmentAtomEditListener {
    private MoleculeViewer moleculeViewer;
    private JMenu monitor,  selection,  captions,  fragment,  bonds,  addBond,
                  tools,  multiView;
    private JMenuItem properties,  distance,  angle,  dihedral,  removeTrackers;
    private JMenuItem openInViewer,  openInJmol;
    private JCheckBoxMenuItem atomSymbol,  atomIndex,  atomCenter;
    private JMenuItem removeCaptions;
    private JCheckBoxMenuItem fragmentationMode,  showGoodnessMeter;
    private JMenuItem addAtomToFragment,  removeAtomFrmFragment,  addAsFragment;
    private JMenuItem exportSelection,  exportSelection_copy,
                      invertSelection;
    private JMenuItem undo,  redo;
    private JMenuItem addAtomGroup;
    private JMenuItem removeBond,  bondClosest,  singleBond,  doubleBond,
                      tripleBond,  weakBond;
    private JCheckBoxMenuItem gridGeneratorMode,  showAdditionalInfo;
    private JMenuItem addMolecule,  editMolecule,  minimize;
    private JCheckBoxMenuItem enableMultiView;
    private JMenuItem addNewView,  showAllViews;
    private LabelUndoableEdit labelUndo;

    /** Creates a new instance of ViewerPopupMenu */
    public ViewerPopupMenu(MoleculeViewer moleculeViewer) {
        super();

        this.moleculeViewer = moleculeViewer;

        initComponents();
    }

    /**
     * return the parent frame
     *
     * @returns MoleculeViewerFrame instance which is the parnet 
     *          encompasing this popup
     */
    private MoleculeViewerFrame getViewerParent() {
        Container parent = moleculeViewer.getParent();

        while (true) {
            if (parent instanceof MoleculeViewerFrame) {
                break;
            }
            if (parent == null) {
                break;
            }

            parent = parent.getParent();
        } // end if

        if (parent == null) {
            return null;
        }

        return (MoleculeViewerFrame) parent;
    }

    /** This method is called from within the constructor to
     * initialize the UI.
     */
    private void initComponents() {
        // monitor menu
        monitor = new JMenu("Monitor");
        monitor.setMnemonic('M');
        distance = new JMenuItem("Distance");
        distance.setMnemonic('D');
        distance.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                addTrackers();
            }
        }); // actionPerformed
        monitor.add(distance);

        angle = new JMenuItem("Angle");
        angle.setMnemonic('A');
        angle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                addTrackers();
            }
        }); // actionPerformed
        monitor.add(angle);

        dihedral = new JMenuItem("Dihedral");
        dihedral.setMnemonic('h');
        dihedral.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                addTrackers();
            }
        }); // actionPerformed
        monitor.add(dihedral);

        monitor.addSeparator();

        removeTrackers = new JMenuItem("Remove All");
        removeTrackers.setMnemonic('R');
        removeTrackers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                removeTrackers();
            }
        }); // actionPerformed
        monitor.add(removeTrackers);

        add(monitor);

        // captions menu
        captions = new JMenu("Captions");
        captions.setMnemonic('C');

        atomSymbol = new JCheckBoxMenuItem("Atom Symbol");
        atomSymbol.setMnemonic('S');
        atomSymbol.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                addAtomSymbolCaptions(atomSymbol.isSelected());
            }
        }); // actionPerformed
        captions.add(atomSymbol);

        atomIndex = new JCheckBoxMenuItem("Atom Index");
        atomIndex.setMnemonic('I');
        atomIndex.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                addAtomIndexCaptions(atomIndex.isSelected());
            }
        }); // actionPerformed
        captions.add(atomIndex);

        atomCenter = new JCheckBoxMenuItem("Atom Center");
        atomCenter.setMnemonic('C');
        atomCenter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                addAtomCenterCaptions(atomCenter.isSelected());
            }
        }); // actionPerformed
        captions.add(atomCenter);

        captions.addSeparator();

        removeCaptions = new JMenuItem("Remove All Captions");
        removeCaptions.setMnemonic('R');
        removeCaptions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                removeAtomCaptions();
            }
        }); // actionPerformed
        captions.add(removeCaptions);

        add(captions);

        // selection menu
        selection = new JMenu("Selection");
        selection.setMnemonic('S');

        openInViewer = new JMenuItem("Open in new viewer");
        openInViewer.setMnemonic('v');
        openInViewer.setToolTipText("Opens the current selection as a " +
                "molecule in a new viewer");
        openInViewer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                // we use the bean shell command here!
                // for a great simplicity
                try {
                    Utility.executeBeanShellScript(
                            "openSelectedMoleculePart(\"false\")");
                } catch (Exception ignored) {
                    System.err.println("Warning! Unable to import " +
                            "commands : " + ignored);

                    ignored.printStackTrace();

                    JOptionPane.showMessageDialog(moleculeViewer,
                            "Error while saving file. " +
                            "\nPlease look into Runtime log for more " +
                            "information.",
                            "Error while saving content!",
                            JOptionPane.ERROR_MESSAGE);
                } // end try .. catch block
            }
        });
        selection.add(openInViewer);

        openInJmol = new JMenuItem("Open in Jmol");
        openInJmol.setMnemonic('J');
        openInJmol.setToolTipText("Opens the current selection as a " +
                "molecule in Jmol viewer");
        openInJmol.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                // we use the bean shell command here!
                // for a great simplicity
                try {
                    Utility.executeBeanShellScript(
                            "openSelectedMoleculePart(\"true\")");
                } catch (Exception ignored) {
                    System.err.println("Warning! Unable to import " +
                            "commands : " + ignored);

                    ignored.printStackTrace();

                    JOptionPane.showMessageDialog(moleculeViewer,
                            "Error while saving file. " +
                            "\nPlease look into Runtime log for more " +
                            "information.",
                            "Error while saving content!",
                            JOptionPane.ERROR_MESSAGE);
                } // end try .. catch block
            }
        });
        selection.add(openInJmol);

        exportSelection_copy = new JMenuItem("Export selection...");
        exportSelection_copy.setMnemonic('x');
        exportSelection_copy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                exportSelectedAtoms();
            }
        });
        selection.add(exportSelection_copy);

        selection.addSeparator();

        invertSelection = new JMenuItem("Invert selection");
        invertSelection.setMnemonic('I');
        invertSelection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                invertSelectedAtoms();
            }
        });
        selection.add(invertSelection);
        
        add(selection);

        addSeparator();

        bonds = new JMenu("Bonds");
        bonds.setMnemonic('B');

        addBond = new JMenu("Add bond");
        addBond.setMnemonic('A');

        singleBond = new JMenuItem("Single");
        singleBond.setMnemonic('S');
        singleBond.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                addBond(BondType.SINGLE_BOND);
            }
        });
        addBond.add(singleBond);

        doubleBond = new JMenuItem("Double");
        doubleBond.setMnemonic('D');
        doubleBond.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                addBond(BondType.DOUBLE_BOND);
            }
        });
        addBond.add(doubleBond);

        tripleBond = new JMenuItem("Triple");
        tripleBond.setMnemonic('T');
        tripleBond.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                addBond(BondType.TRIPLE_BOND);
            }
        });
        addBond.add(tripleBond);

        tripleBond = new JMenuItem("Aromatic");
        tripleBond.setMnemonic('A');
        tripleBond.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                addBond(BondType.AROMATIC_BOND);
            }
        });
        addBond.add(tripleBond);

        weakBond = new JMenuItem("Weak");
        weakBond.setMnemonic('W');
        weakBond.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                addBond(BondType.WEAK_BOND);
            }
        });
        addBond.add(weakBond);

        bonds.add(addBond);

        removeBond = new JMenuItem("Remove bond");
        removeBond.setMnemonic('R');
        removeBond.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                addBond(BondType.NO_BOND);
            }
        });
        bonds.add(removeBond);

        bonds.addSeparator();

        bondClosest = new JMenuItem("Add bonds to closest atoms");
        bondClosest.setMnemonic('c');
        bondClosest.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                addBond(BondType.SINGLE_BOND);
            }
        });
        bonds.add(bondClosest);

        add(bonds);

        addSeparator();

        // add atom groups menu
        addAtomGroup = new JMenuItem("Add atom group...");
        addAtomGroup.setMnemonic('g');
        addAtomGroup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                // get the selection as a molecule object
                try {
                    // obtain the selection as new molecule object, and
                    // build connectivity information
                    MoleculeScene selectedScene =
                            moleculeViewer.getFirstSelectedScene();
                    Molecule selection =
                            selectedScene.getSelectionAsNewMolecule();
                    Molecule molecule = moleculeViewer.getFirstSelectedScene().
                            getMolecule();
                    MoleculeBuilder mb =
                            (MoleculeBuilder) Utility.getDefaultImplFor(
                            MoleculeBuilder.class).newInstance();
                    mb.makeConnectivity(selection);
                    mb = null;

                    // check if we have connected atoms?
                    Iterator atoms = selection.traversePath(0);
                    int atomCount = 0;

                    while (atoms.hasNext()) {
                        atoms.next();
                        atomCount++;
                    }

                    // a valid group
                    if (atomCount == selection.getNumberOfAtoms()) {
                        selection = selectedScene.getSelectionAsMolecule();

                        AtomGroupList agl =
                                molecule.getDefaultSpecialStructureRecognizer().
                                getGroupList();
                        AtomGroup ag = new DefaultAtomGroup();

                        atoms = selection.getAtoms();

                        while (atoms.hasNext()) {
                            ag.addAtomIndex(((Atom) atoms.next()).getIndex());
                        } // end while

                        // add this group to the list
                        agl.addGroup(ag);

                        // TODO: need improvement!
                        moleculeViewer.getIDEInstance().getWorkspaceExplorer().
                                getWorkspacePanel().updatePanel();
                        moleculeViewer.getIDEInstance().getCurrentWorkspace().
                                setDirty(true);
                    } // end if
                } catch (Exception ignored) {
                    System.err.println("Warning! Unable to add the atom group " +
                            ignored);

                    ignored.printStackTrace();

                    JOptionPane.showMessageDialog(moleculeViewer,
                            "Error adding atom group. " +
                            "\n Please look into Runtime log for more " +
                            "information.",
                            "Error adding atom group!",
                            JOptionPane.ERROR_MESSAGE);
                } // end of try .. catch block
            }
        });
        addAtomGroup.setEnabled(false);
        add(addAtomGroup);

        addSeparator();

        // fragment menu
        fragment = new JMenu("Fragmentation");
        fragment.setMnemonic('F');

        fragmentationMode = new JCheckBoxMenuItem("Fragmentation mode");
        fragmentationMode.setMnemonic('F');
        fragment.add(fragmentationMode);

        showGoodnessMeter = new JCheckBoxMenuItem("Show goodness meter");
        showGoodnessMeter.setMnemonic('g');
        showGoodnessMeter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                showGoodnessMeter.setEnabled(
                        moleculeViewer.isPartOfWorkspace());

                if (!showGoodnessMeter.isEnabled()) {
                    return;
                }

                MoleculeViewerFrame parent = getViewerParent();

                if (parent == null) {
                    return;
                }

                if (showGoodnessMeter.isSelected()) {
                    parent.showGoodnessSidePanel();
                } else {
                    parent.hideSidePanel();
                } // end if
            }
        });
        fragment.add(showGoodnessMeter);

        fragment.addSeparator();

        addAtomToFragment = new JMenuItem("Add Atom(s)");
        addAtomToFragment.setMnemonic('d');
        addAtomToFragment.setToolTipText("Add selected atom(s) to " +
                "the visible fragment(s)");
        addAtomToFragment.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                addAtomToFragment();
            }
        });
        fragment.add(addAtomToFragment);

        addAsFragment = new JMenuItem("Add Atom(s) as fragment");
        addAsFragment.setMnemonic('d');
        addAsFragment.setToolTipText("Add selected atom(s) as " +
                "a new fragment");
        addAsFragment.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                addAtomAsFragment();
            }
        });
        fragment.add(addAsFragment);

        removeAtomFrmFragment = new JMenuItem("Remove Atom(s)");
        removeAtomFrmFragment.setMnemonic('d');
        removeAtomFrmFragment.setToolTipText("Remove selected atom(s) " +
                "from the visible fragment(s)");
        removeAtomFrmFragment.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                removeAtomFromFragment();
            }
        });
        fragment.add(removeAtomFrmFragment);

        fragment.addSeparator();

        exportSelection = new JMenuItem("Export selection...");
        exportSelection.setMnemonic('x');
        exportSelection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                exportSelectedAtoms();
            }
        });
        fragment.add(exportSelection);

        add(fragment);

        addSeparator();

        // undo .. redo
        undo = new JMenuItem("Undo");
        undo.setMnemonic('U');
        undo.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_MASK));
        undo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (moleculeViewer.canUndo()) {
                    moleculeViewer.undo();
                } // end if
                undo.setEnabled(moleculeViewer.canUndo());
                redo.setEnabled(moleculeViewer.canRedo());
            }
        });
        add(undo);

        redo = new JMenuItem("Redo");
        redo.setMnemonic('R');
        redo.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_MASK));
        redo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (moleculeViewer.canRedo()) {
                    moleculeViewer.redo();
                } // end if
                undo.setEnabled(moleculeViewer.canUndo());
                redo.setEnabled(moleculeViewer.canRedo());
            }
        });
        add(redo);

        addSeparator();

        // tools menu
        tools = new JMenu("Tools");
        tools.setMnemonic('T');

        gridGeneratorMode = new JCheckBoxMenuItem("Grid Generator");
        gridGeneratorMode.setMnemonic('G');
        gridGeneratorMode.setToolTipText("Tool to help generate a grid " +
                "around the molecule to calculate properties.");
        gridGeneratorMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                MoleculeViewerFrame parent = getViewerParent();

                if (parent == null) {
                    return;
                }

                moleculeViewer.setGridDrawn(gridGeneratorMode.isSelected());

                if (gridGeneratorMode.isSelected()) {
                    parent.showGridGeneratorSidePanel();
                } else {
                    parent.hideSidePanel();
                } // end if
            }
        });
        tools.add(gridGeneratorMode);
        tools.addSeparator();

        minimize = new JMenuItem("Minimize");
        minimize.setMnemonic('M');
        minimize.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_M, KeyEvent.CTRL_MASK));
        minimize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                Iterator<MoleculeScene> scenes = moleculeViewer.getSceneList().
                        iterator();

                while (scenes.hasNext()) {
                    Molecule mol = scenes.next().getMolecule();
                    UniversalForceFieldMethod uffMethod =
                            new UniversalForceFieldMethod(mol, true);
                    ConjugateGradientOptimizer optimizer =
                            new ConjugateGradientOptimizer(uffMethod, 3.0e-8);
                    // SteepestDescentOptimizer optimizer =
                    //    new SteepestDescentOptimizer(uffMethod, 0.25, 0.4);
                    optimizer.setMaxIterations(10000);
                    optimizer.getConvergenceCriteria().setTolerance(1.0e-8);

                    optimizer.setVariables(uffMethod.getAtomCoords());
                    try {
                        SimpleAsyncTask.init(optimizer, optimizer.getClass()
                                             .getMethod("minimize")).start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } // end of try .. catch block
                }
            }
        });
        tools.add(minimize);
        tools.addSeparator();

        showAdditionalInfo = new JCheckBoxMenuItem("Show Additional Info");
        showAdditionalInfo.setMnemonic('I');
        showAdditionalInfo.setToolTipText("Show additional information " +
                "related to this molecule, such as energy, if available.");
        showAdditionalInfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                MoleculeViewerFrame parent = getViewerParent();

                if (parent == null) {
                    return;
                }

                parent.getMoleculeViewer().showAdditionalInfoPanel(
                        showAdditionalInfo.isSelected());
            }
        });
        tools.add(showAdditionalInfo);
        tools.addSeparator();

        addMolecule = new JMenuItem("Add molecule...");
        addMolecule.setMnemonic('A');
        addMolecule.setToolTipText("Add a molecule object to this frame.");
        addMolecule.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                addMolecule();
            }
        });
        tools.add(addMolecule);

        editMolecule = new JMenuItem("Edit molecule...");
        editMolecule.setMnemonic('E');
        editMolecule.setToolTipText("Open the current molecule object" +
                " in a internal molecule editor.");
        editMolecule.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                editMolecule();
            }
        });
        tools.add(editMolecule);

        add(tools);

        addSeparator();

        multiView = new JMenu("Multi View");
        multiView.setMnemonic('V');
        multiView.setToolTipText(
                "Control multiple views of the molecule scene");

        enableMultiView = new JCheckBoxMenuItem("Enable multi view");
        enableMultiView.setMnemonic('E');
        enableMultiView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                MoleculeViewerFrame parent = getViewerParent();

                if (parent == null) {
                    return;
                }

                parent.setMultiViewEnabled(enableMultiView.isSelected());
                addNewView.setEnabled(enableMultiView.isSelected());
                showAllViews.setEnabled(enableMultiView.isSelected());
            }
        });
        multiView.add(enableMultiView);
        multiView.addSeparator();

        addNewView = new JMenuItem("Add new view");
        addNewView.setMnemonic('A');
        addNewView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                MoleculeViewerFrame parent = getViewerParent();

                if (parent == null) {
                    return;
                }

                parent.addNewMoleculeView();
            }
        });
        multiView.add(addNewView);

        showAllViews = new JMenuItem("Show all views");
        showAllViews.setMnemonic('S');
        showAllViews.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                MoleculeViewerFrame parent = getViewerParent();

                if (parent == null) {
                    return;
                }

            // TODO:
            }
        });
        multiView.add(showAllViews);

        add(multiView);

        addSeparator();

        // properties menu
        properties = new JMenuItem("Properties");
        properties.setMnemonic('P');
        properties.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                IDEPropertySheetUI ui = new IDEPropertySheetUI(
                        MoleculeViewerPropertyWrapper.class,
                        new MoleculeViewerPropertyWrapper(moleculeViewer),
                        moleculeViewer.getIDEInstance());

                // show the UI
                ui.show(null);
            }
        });
        add(properties);

        setInvoker(moleculeViewer);
    }

    /**
     * edit a molecule
     */
    public void editMolecule() {
        MoleculeViewerFrame parent = getViewerParent();

        if (parent == null) {
            return;
        }

        MeTA ideInstance = moleculeViewer.getIDEInstance();
        MoleculeEditorFrame mef = new MoleculeEditorFrame(ideInstance);

        // only copy molecule scene ... we can only edit them!
        mef.getMoleculeEditor().disableUndo();
        for (MoleculeScene scene : moleculeViewer.getSceneList()) {
            mef.addScene(new MoleculeScene(scene.getMolecule()));
        } // end for
        mef.getMoleculeEditor().enableUndo();

        // add the frame to studio desktop
        ideInstance.getWorkspaceDesktop().addInternalFrame(mef);
    }

    /**
     * add a molecule to the current viewer
     */
    public void addMolecule() {
        MoleculeViewerFrame parent = getViewerParent();

        if (parent == null) {
            return;
        }

        IDEFileChooser fileChooser = new IDEFileChooser();

        fileChooser.setShowPreview(true);

        // construct the file filter
        try {
            MoleculeFileReaderFactory mfrm =
                    (MoleculeFileReaderFactory) Utility.getDefaultImplFor(
                    MoleculeFileReaderFactory.class).newInstance();

            Iterator<String> supportedFileFormats = mfrm.getAllSupportedTypes();
            Vector<String> fileFormatList = new Vector<String>();

            while (supportedFileFormats.hasNext()) {
                fileFormatList.add(supportedFileFormats.next());
            } // end while

            IDEFileFilter fileFilter =
                    new IDEFileFilter(fileFormatList.toArray(),
                    "All supported formats");

            // and add the file filter
            fileChooser.addChoosableFileFilter(fileFilter);
            // and add the iconic stuff
            fileChooser.setFileView(new IDEFileView());
        } catch (Exception e) {
            System.err.println("Exception : " + e.toString());
            System.err.println("Could not make proper file filters.");
            e.printStackTrace();
        } // end of try .. catch block

        if (fileChooser.showOpenDialog(
                moleculeViewer.getIDEInstance()) ==
                IDEFileChooser.APPROVE_OPTION) {
            try {
                MoleculeFileReaderFactory mfr =
                        (MoleculeFileReaderFactory) Utility.getDefaultImplFor(
                        MoleculeFileReaderFactory.class).newInstance();

                String fileName =
                        fileChooser.getSelectedFile().getAbsolutePath();
                String typ = fileName.substring(fileName.lastIndexOf(".") + 1,
                        fileName.length());

                MoleculeFileReader rdr = mfr.getReader(typ);

                Molecule mol = rdr.readMoleculeFile(fileName);

                MoleculeBuilder mb =
                        (MoleculeBuilder) Utility.getDefaultImplFor(
                        MoleculeBuilder.class).newInstance();

                mb.makeConnectivity(mol);

                moleculeViewer.addScene(new MoleculeScene(mol));
            } catch (Exception e) {
                System.err.println(e.toString());
                e.printStackTrace();

                moleculeViewer.getIDEInstance().getStatusBar().setStatusText(
                        "ERROR: Unable to add molecule. " +
                        "See runtime log for details.");
            } // end for try .. catch block
        } // end if
    }

    /**
     * invert selected atoms
     */
    private void invertSelectedAtoms() {
        for(MoleculeScene scene : moleculeViewer.getSceneList()) {
            scene.invertSelection();
        } // end for
    }

    /**
     * export selected atoms
     */
    private void exportSelectedAtoms() {
        // we use the bean shell command here!
        // for a great simplicity
        try {
            Utility.executeBeanShellScript(
                    "saveSelectedMoleculePart()");
        } catch (Exception ignored) {
            System.err.println("Warning! Unable to import " +
                    "commands : " + ignored);

            ignored.printStackTrace();

            JOptionPane.showMessageDialog(moleculeViewer,
                    "Error while saving file. " +
                    "\nPlease look into Runtime log for more " + "information.",
                    "Error while saving content!",
                    JOptionPane.ERROR_MESSAGE);
        } // end try .. catch block
    }

    /**
     * Adds relevent symbol captions
     */
    public void addAtomSymbolCaptions(boolean onOrOff) {
        Iterator scenes = moleculeViewer.getSceneList().iterator();
        MoleculeScene scene;

        while (scenes.hasNext()) {
            scene = (MoleculeScene) scenes.next();
            if (scene.getSelectionStack().size() > 0) {
                // fire undo event
                labelUndo = new LabelUndoableEdit(moleculeViewer);
                labelUndo.setMoleculeScene(scene);
                labelUndo.setSelectionStack(scene.getSelectionStack());
                labelUndo.setShowingSymbol(true);
                labelUndo.setCurrentSymbolState(onOrOff);
                moleculeViewer.addEdit(labelUndo);

                // show the stuff
                scene.showSymbolLables(onOrOff);

                // clear the selection stack
                scene.getSelectionStack().clear();
            } // end if
        } // end while
    }

    /**
     * Adds relevent index captions
     */
    public void addAtomIndexCaptions(boolean onOrOff) {
        Iterator scenes = moleculeViewer.getSceneList().iterator();
        MoleculeScene scene;

        while (scenes.hasNext()) {
            scene = (MoleculeScene) scenes.next();
            if (scene.getSelectionStack().size() > 0) {
                // fire undo event
                labelUndo = new LabelUndoableEdit(moleculeViewer);
                labelUndo.setMoleculeScene(scene);
                labelUndo.setSelectionStack(scene.getSelectionStack());
                labelUndo.setShowingID(true);
                labelUndo.setCurrentIDState(onOrOff);
                moleculeViewer.addEdit(labelUndo);

                // show the stuff
                scene.showIDLables(onOrOff);

                // clear the selection stack
                scene.getSelectionStack().clear();
            } // end if
        } // end while
    }

    /**
     * remove all atom captions
     */
    public void removeAtomCaptions() {
        Iterator scenes = moleculeViewer.getSceneList().iterator();
        MoleculeScene scene;

        while (scenes.hasNext()) {
            scene = (MoleculeScene) scenes.next();
            scene.removeAllCaptions();
            scene.getSelectionStack().clear();
        } // end while         
    }

    /**
     * Adds relevent center captions
     */
    public void addAtomCenterCaptions(boolean onOrOff) {
        Iterator scenes = moleculeViewer.getSceneList().iterator();
        MoleculeScene scene;

        while (scenes.hasNext()) {
            scene = (MoleculeScene) scenes.next();
            if (scene.getSelectionStack().size() > 0) {
                // fire undo event
                labelUndo = new LabelUndoableEdit(moleculeViewer);
                labelUndo.setMoleculeScene(scene);
                labelUndo.setSelectionStack(scene.getSelectionStack());
                labelUndo.setShowingCenter(true);
                labelUndo.setCurrentCenterState(onOrOff);
                moleculeViewer.addEdit(labelUndo);

                // show the stuff
                scene.showCenterLables(onOrOff);

                // clear the selection stack
                scene.getSelectionStack().clear();
            } // end if
        } // end while     
    }

    /**
     * Add bond to the selection
     */
    private void addBond(BondType type) {
        Iterator scenes = moleculeViewer.getSceneList().iterator();
        MoleculeScene scene;

        while (scenes.hasNext()) {
            scene = (MoleculeScene) scenes.next();

            if (scene.getSelectionStack() != null) {
                Stack<ScreenAtom> selectionStack = scene.getSelectionStack();

                if (selectionStack.size() == 2) {
                    Atom atom1, atom2;

                    atom1 = selectionStack.get(0).getAtom();
                    atom2 = selectionStack.get(1).getAtom();

                    Molecule molecule = scene.getMolecule();
                    BondType oldBond = molecule.getBondType(atom1.getIndex(),
                            atom2.getIndex());

                    molecule.setBondType(atom1.getIndex(),
                            atom2.getIndex(), type);

                    // undo 
                    moleculeViewer.addEdit(
                            new BondUndoableEdit(molecule, oldBond, type,
                            atom1.getIndex(), atom2.getIndex()));

                    selectionStack.clear();
                } else if (selectionStack.size() > 2) {
                    // in this case, we add bonds (single) to
                    // the closest atoms
                    Molecule molecule = scene.getMolecule();

                    for (int i = 0; i < selectionStack.size(); i++) {
                        ScreenAtom sa = selectionStack.get(i);
                        ScreenAtom sb = scene.getClosestSelectedAtom(sa);

                        if (sb == null) {
                            continue;
                        }

                        Atom atom1 = sa.getAtom(), atom2 = sb.getAtom();

                        BondType oldBond = molecule.getBondType(
                                atom1.getIndex(), atom2.getIndex());

                        molecule.setBondType(atom1.getIndex(),
                                atom2.getIndex(), type);

                        // undo
                        moleculeViewer.addEdit(
                                new BondUndoableEdit(molecule, oldBond, type,
                                atom1.getIndex(), atom2.getIndex()));
                    } // end for

                    selectionStack.clear();
                } // end if
            } // end if                
        } // end while
    }

    /**
     * add atom(s) to fragment
     */
    public void addAtomToFragment() {
        for (MoleculeScene scene : moleculeViewer.getSceneList()) {
            scene.addSelectionToVisibleFragments(this);
        } // end for
    }

    /**
     * add atom(s) as new fragment
     */
    public void addAtomAsFragment() {
        for (MoleculeScene scene : moleculeViewer.getSceneList()) {
            scene.addSelectionAsFragments(this);
        } // end for
    }

    /**
     * remove atom(s) to fragment
     */
    public void removeAtomFromFragment() {
        for (MoleculeScene scene : moleculeViewer.getSceneList()) {
            scene.removeSelectionFromVisibleFragments(this);
        } // end for
    }

    /**
     * Add the relevent trackers
     */
    public void addTrackers() {
        Iterator scenes = moleculeViewer.getSceneList().iterator();
        MoleculeScene scene;

        while (scenes.hasNext()) {
            scene = (MoleculeScene) scenes.next();

            if (scene.getSelectionStack() != null) {
                addTracker(scene, scene.getSelectionStack());
            }
        } // end while
    }

    /**
     * Remove the relevent trackers
     */
    public void removeTrackers() {
        Iterator scenes = moleculeViewer.getSceneList().iterator();

        while (scenes.hasNext()) {
            ((MoleculeScene) scenes.next()).removeAllTrackers();
        } // end while
    }

    /**
     * add the appropriate tracker
     */
    private void addTracker(MoleculeScene scene,
            Stack<ScreenAtom> selectionStack) {
        ScreenAtom atom1, atom2, atom3, atom4;

        LogWindow log = moleculeViewer.getIDEInstance().getWorkspaceLog();
        StringBuffer logString = new StringBuffer();

        switch (selectionStack.size()) {
            case 2: // distance tracker
                atom1 = selectionStack.get(0);
                atom2 = selectionStack.get(1);

                scene.addDistanceTracker(atom1.getAtom().getIndex(),
                        atom2.getAtom().getIndex());
                // undo 
                moleculeViewer.addEdit(
                        new TrackerUndoableEdit(scene,
                        scene.getLastAddedTracker()));

                // log
                logString.append("Distance between : ");
                logString.append(atom1.getAtom().getSymbol() + " (" + atom1.
                        getAtom().getIndex() + ") -- ");
                logString.append(atom2.getAtom().getSymbol() + " (" + atom2.
                        getAtom().getIndex() + ") is : ");
                logString.append(atom1.getAtom().distanceFrom(atom2.getAtom()) +
                        " " + Utility.ANGSTROM_SYMBOL);

                // remove the selection now its not needed
                atom1.setSelected(false);
                atom2.setSelected(false);
                selectionStack.clear();
                break;
            case 3: // angle tracker
                atom1 = selectionStack.get(0);
                atom2 = selectionStack.get(1);
                atom3 = selectionStack.get(2);

                scene.addAngleTracker(atom1.getAtom().getIndex(),
                        atom2.getAtom().getIndex(),
                        atom3.getAtom().getIndex());
                // undo 
                moleculeViewer.addEdit(
                        new TrackerUndoableEdit(scene,
                        scene.getLastAddedTracker()));

                // log
                logString.append("Angle : ");
                logString.append(atom1.getAtom().getSymbol() + " (" + atom1.
                        getAtom().getIndex() + ") -- ");
                logString.append(atom2.getAtom().getSymbol() + " (" + atom2.
                        getAtom().getIndex() + ") -- ");
                logString.append(atom3.getAtom().getSymbol() + " (" + atom3.
                        getAtom().getIndex() + ") is : ");
                logString.append(MathUtil.toDegrees(MathUtil.findAngle(
                        atom1.getAtom().getAtomCenter(),
                        atom2.getAtom().getAtomCenter(),
                        atom3.getAtom().getAtomCenter())) + " " +
                        Utility.DEGREE_SYMBOL);

                // remove the selection now its not needed
                atom1.setSelected(false);
                atom2.setSelected(false);
                atom3.setSelected(false);
                selectionStack.clear();
                break;
            case 4: // dihedral tracker
                atom1 = selectionStack.get(0);
                atom2 = selectionStack.get(1);
                atom3 = selectionStack.get(2);
                atom4 = selectionStack.get(3);

                scene.addDihedralTracker(atom1.getAtom().getIndex(),
                        atom2.getAtom().getIndex(),
                        atom3.getAtom().getIndex(),
                        atom4.getAtom().getIndex());
                // undo 
                moleculeViewer.addEdit(
                        new TrackerUndoableEdit(scene,
                        scene.getLastAddedTracker()));

                // log
                logString.append("Dihedral : ");
                logString.append(atom1.getAtom().getSymbol() + " (" + atom1.
                        getAtom().getIndex() + ") -- ");
                logString.append(atom2.getAtom().getSymbol() + " (" + atom2.
                        getAtom().getIndex() + ") -- ");
                logString.append(atom3.getAtom().getSymbol() + " (" + atom3.
                        getAtom().getIndex() + ") -- ");
                logString.append(atom4.getAtom().getSymbol() + " (" + atom4.
                        getAtom().getIndex() + ") is : ");
                logString.append(MathUtil.toDegrees(MathUtil.findDihedral(
                        atom1.getAtom().getAtomCenter(),
                        atom2.getAtom().getAtomCenter(),
                        atom3.getAtom().getAtomCenter(),
                        atom4.getAtom().getAtomCenter())) + " " +
                        Utility.DEGREE_SYMBOL);
                // remove the selection now its not needed
                atom1.setSelected(false);
                atom2.setSelected(false);
                atom3.setSelected(false);
                atom4.setSelected(false);
                selectionStack.clear();
                break;
            default:
                return;
        } // end if

        log.appendInfo(logString.toString());
        System.out.println(logString);
    }

    /**
     * method to update the menus, enable / disable them
     */
    public void updateMenus() {
        Iterator scenes = moleculeViewer.getSceneList().iterator();
        MoleculeScene scene;

        undo.setEnabled(moleculeViewer.canUndo());
        redo.setEnabled(moleculeViewer.canRedo());

        distance.setEnabled(false);
        angle.setEnabled(false);
        dihedral.setEnabled(false);

        atomSymbol.setEnabled(false);
        atomSymbol.setSelected(false);
        atomIndex.setEnabled(false);
        atomIndex.setSelected(false);
        atomCenter.setEnabled(false);
        atomCenter.setSelected(false);

        bondClosest.setEnabled(false);
        addBond.setEnabled(false);
        removeBond.setEnabled(false);

        addAtomToFragment.setEnabled(false);
        addAsFragment.setEnabled(false);
        removeAtomFrmFragment.setEnabled(false);

        // the fragmentation submenu is only enabled if the number of 
        // scenes is one...
        if (moleculeViewer.getSceneList().size() == 1) {
            fragment.setEnabled(true);
            fragment.setToolTipText("Fragmentation mode");

            addAtomGroup.setEnabled(true && moleculeViewer.isPartOfWorkspace());

            multiView.setEnabled(true);
            multiView.setToolTipText(
                    "Control multiple views of the molecule scene");

            enableMultiView.setSelected(moleculeViewer.isMultiViewEnabled());
        } else {
            fragment.setEnabled(false);
            fragment.setToolTipText("Fragmentation mode only enabled when " +
                    "exactly one molecule is present in the viewer.");

            addAtomGroup.setEnabled(false);

            multiView.setEnabled(false);
            multiView.setToolTipText("Multi view mode only enabled when " +
                    "exactly one molecule is present in the viewer.");
        } // end if

        addNewView.setEnabled(
                multiView.isEnabled() && enableMultiView.isSelected());
        showAllViews.setEnabled(
                multiView.isEnabled() && enableMultiView.isSelected());

        if (addAtomGroup.isEnabled()) {
            addAtomGroup.setToolTipText("Adds the selection as atom group");
        } else {
            addAtomGroup.setToolTipText("Enabled only when " +
                    "exactly one molecule is present in the viewer " +
                    "connected to a open workspace.");
        } // end if

        // there we should enable only relevent menues
        while (scenes.hasNext()) {
            scene = (MoleculeScene) scenes.next();

            if (scene.getSelectionStack().size() >= 1) {
                boolean atomSymbolState, atomIndexState, atomCenterState;
                Iterator sa = scene.getSelectionStack().iterator();
                ScreenAtom atom;

                atom = (ScreenAtom) sa.next();
                atomSymbolState = atom.isShowingSymbolLabel();
                atomIndexState = atom.isShowingIDLabel();
                atomCenterState = atom.isShowingCenterLabel();

                while (sa.hasNext()) {
                    atom = (ScreenAtom) sa.next();
                    atomSymbolState = atomSymbolState && atom.
                            isShowingSymbolLabel();
                    atomIndexState = atomIndexState && atom.isShowingIDLabel();
                    atomCenterState = atomCenterState && atom.
                            isShowingCenterLabel();
                } // end while

                atomSymbol.setEnabled(true);
                atomSymbol.setSelected(atomSymbolState);
                atomIndex.setEnabled(true);
                atomIndex.setSelected(atomIndexState);
                atomCenter.setEnabled(true);
                atomCenter.setSelected(atomCenterState);

                addAtomToFragment.setEnabled(true);
                addAsFragment.setEnabled(true);
                removeAtomFrmFragment.setEnabled(true);
            } // end if

            if (scene.getSelectionStack().size() > 2) {
                bondClosest.setEnabled(true);
            }

            switch (scene.getSelectionStack().size()) {
                case 2: // distance tracker, add/remove bond
                    distance.setEnabled(true);
                    addBond.setEnabled(true);
                    removeBond.setEnabled(true);
                    return;
                case 3: // angle tracker
                    angle.setEnabled(true);
                    return;
                case 4: // dihedral tracker
                    dihedral.setEnabled(true);
                    return;
            } // end switch .. case 

            // single atom selection can't form an atom group
            addAtomGroup.setEnabled(addAtomGroup.isEnabled() && (scene.
                    getSelectionStack().size() >= 2));
        } // end while
    }

    /**
     * Returns true/ false if fragmentationMode is selected
     *
     * @return the state of fragmentationMode checkbox menu item
     */
    public boolean isFragmentationMode() {
        return fragmentationMode.isSelected();
    }

    /**
     * Enambles or disables fragmentation mode, externally
     * 
     * @param newState the new state 
     */
    public void setFragmentationMode(boolean newState) {
        fragmentationMode.setSelected(newState);
    }

    /** 
     * callback on fragment atom edit event
     *
     * @param faee is the object describing the event
     */
    @Override
    public void fragmentAtomEdited(FragmentAtomEditEvent faee) {
        // undo 
        moleculeViewer.addEdit(new FragmentAtomUndoableEdit(faee));
    }
} // end of class ViewerPopupMenu
