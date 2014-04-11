/*
 * MoleculeItem.java
 *
 * Created on November 2, 2003, 1:11 PM
 */

package org.meta.workspace.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import org.meta.common.Utility;
import org.meta.common.resource.StringResource;
import org.meta.fragment.Fragment;
import org.meta.fragment.FragmentAtom;
import org.meta.fragmentor.FragmentGoodnessProbeFactory;
import org.meta.fragmentor.FragmentationScheme;
import org.meta.fragmentor.FragmentationSchemeFactory;
import org.meta.math.geom.Point3D;
import org.meta.molecule.*;
import org.meta.molecule.event.MoleculeStateChangeEvent;
import org.meta.molecule.event.MoleculeStateChangeListener;
import org.meta.molecule.impl.DefaultAtomGroup;
import org.meta.molecule.impl.Ring;
import org.meta.molecule.impl.RingRecognizer;
import org.meta.workspace.ItemData;
import org.meta.workspace.WorkspaceIOException;
import org.meta.workspace.WorkspaceItem;
import org.meta.workspace.event.WorkspaceItemChangeEvent;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * A implementation of WorkspaceItem representing a Molecule object.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MoleculeItem extends WorkspaceItem 
                          implements MoleculeStateChangeListener {
    
    // for parsing help
    private String element = "element";
    private Atom currentAtom;
    private Ring currentRing;
    private DefaultAtomGroup currentAtomGroup;
    private FragmentationScheme currentFragmentationScheme;
    private Fragment currentFragment;
    private FragmentationSchemeFactory fragmentationSchemeFactory;
    
    private WorkspaceItemChangeEvent wice;
    
    /** Creates a new instance of MoleculeItem */
    public MoleculeItem(String moleculeItemFile) {
        super(moleculeItemFile);
        
        this.implClass   = "org.meta.workspace.impl.MoleculeItem";
        this.type        = "workspace/molecule";
        
        this.wice        = new WorkspaceItemChangeEvent(this);
    }
    
    /**
     * method's implementation should close the relevent workspace item with all 
     * the preferences saved on to the nonvolatile store for further retrival.
     *      
     * @throws WorkspaceIOException in case an error occurs.
     */
    @Override
    public void close() throws WorkspaceIOException {
        // just pass on ... we are not required to worry too much
        // at this moment at least .. but have to worry about this 
        // as soon as we add mutable properties related to this object!
    }
    
    /**
     * method's implementation should open the relevant workspace item with all 
     * the saved preferences.
     *
     * @throws WorkspaceIOException in case an error occurs.
     */
    @Override
    public void open() throws WorkspaceIOException {
        try {
            // read the configuration file
            Document wif = Utility.parseXML(baseDirectory + File.separatorChar 
                    + (new File(workspaceItemFile)).getName());            
        
            Molecule mol = (Molecule) Utility.getDefaultImplFor(Molecule.class)
                                             .newInstance();
            mol.addSpecialStructureRecognizer(new RingRecognizer());            
            
            fragmentationSchemeFactory = (FragmentationSchemeFactory)
                Utility.getDefaultImplFor(FragmentationSchemeFactory.class)
                       .newInstance();
            
            // update the object based on the configuration file
            updateIt(wif, mol);
            
            setItemData(new ItemData(mol));
            
            currentRing = null;
            currentAtom = null;
            
            currentFragment = null;
            currentFragmentationScheme = null;
            
            wif = null;
            
            // just make sure we have atleast an instance of default
            // fragmentation schme
            mol.getDefaultFragmentationScheme();
        } catch (Exception e) {
           System.err.println("Error reading molecule item " +
                              "configuration file: " + e.toString());
            e.printStackTrace();
            
            throw new WorkspaceIOException(
                              "Error reading molecule item configuration file: "
                               + e.toString());
        } // end of try .. catch block
    }
    
    /**
     * method's implementation should save the relevant workspace item with all 
     * its preferences in its current state to a nonvolatile store for further 
     * retrieval. 
     *      
     * @throws WorkspaceIOException in case an error occurs.
     */
    @Override
    public void save() throws WorkspaceIOException {        
        try {
            // first step in save process is to get the Molecule object ..
            // .. which needs to be converted into the XML form
            Molecule molecule = (Molecule) itemData.getData();
            
            // then open up the file for writing
            FileOutputStream fos = new FileOutputStream(baseDirectory + File.separatorChar + workspaceItemFile);
            
            fos.write(StringResource.getInstance().getXmlHeader().getBytes());
            
            fos.write(("\n<molecule title=\"" + molecule.getTitle() + "\""
                            + " numberOfSingleBonds=\"" 
                                    + molecule.getNumberOfSingleBonds() + "\""
                            + " numberOfMultipleBonds=\"" 
                                    + molecule.getNumberOfMultipleBonds() + "\""
                            + " numberOfWeakBonds=\"" 
                                    + molecule.getNumberOfWeakBonds() + "\""
                            + " molecularMass=\"" 
                                    + molecule.getMolecularMass() + "\""
                            + " numberOfElectrons=\"" 
                                    + molecule.getNumberOfElectrons() + "\""
                            + "> \n").getBytes());
            
            // now dump the atoms and connectivity
            Iterator atoms = molecule.getAtoms();
            int atomIndex = 0;
            
            while(atoms.hasNext()) {
                Atom atom = (Atom) atoms.next();
                    
                fos.write(("\t<atom atomIndex=\"" + atomIndex + "\""
                                + " symbol=\"" + atom.getSymbol() + "\""
                                + " x=\"" + atom.getX() + "\""
                                + " y=\"" + atom.getY() + "\""
                                + " z=\"" + atom.getZ() + "\""
                                + " charge=\"" + atom.getCharge() 
                                + "\"> \n").getBytes());
                
                // now write the connectivity
                Hashtable connectedList = atom.getConnectedList();
                Enumeration keys = connectedList.keys();
                
                fos.write("\t\t<connectedList>\n".getBytes());
                while(keys.hasMoreElements()) {
                    Integer ci = (Integer) keys.nextElement();
                    
                    fos.write(("\t\t\t<ca index=\"" + ci + "\""
                              + " bondType=\"" + connectedList.get(ci) 
                              + "\" /> \n").getBytes());
                } // end while
                fos.write("\t\t</connectedList>\n".getBytes());
                
                if (molecule.isZMatrixComputed()) {
                    // and the ZMatrix information
                    fos.write("\t\t<zmatrix>".getBytes());
                    if (atom.getIndex() == 0) {
                        // do nothing!
                    } else if (atom.getIndex() == 1) {
                        fos.write(("\n\t\t\t<length index=\"" 
                                   + atom.getLengthReference()
                                        .getReferenceAtom().getIndex() 
                                   + "\" value=\"" 
                                   + atom.getLengthReference().getValue()
                                   + "\" />"
                                  ).getBytes());
                    } else if (atom.getIndex() == 2) {
                        fos.write(("\n\t\t\t<length index=\"" 
                                   + atom.getLengthReference()
                                        .getReferenceAtom().getIndex() 
                                   + "\" value=\"" 
                                   + atom.getLengthReference().getValue() 
                                   + "\" />"
                                  ).getBytes());
                        fos.write(("\n\t\t\t<angle index=\"" 
                                   + atom.getAngleReference()
                                        .getReferenceAtom().getIndex() 
                                   + "\" value=\"" 
                                   + atom.getAngleReference().getValue() 
                                   + "\" />"
                                  ).getBytes());
                    } else {
                        fos.write(("\n\t\t\t<length index=\"" 
                                   + atom.getLengthReference()
                                        .getReferenceAtom().getIndex() 
                                   + "\" value=\"" 
                                   + atom.getLengthReference().getValue() 
                                   + "\" />"
                                  ).getBytes());
                        fos.write(("\n\t\t\t<angle index=\"" 
                                   + atom.getAngleReference()
                                        .getReferenceAtom().getIndex() 
                                   + "\" value=\"" 
                                   + atom.getAngleReference().getValue() 
                                   + "\" />"
                                  ).getBytes());
                        fos.write(("\n\t\t\t<dihedral index=\"" 
                                  + atom.getDihedralReference()
                                        .getReferenceAtom().getIndex() 
                                  + "\" value=\"" 
                                  + atom.getDihedralReference().getValue() 
                                  + "\" />"
                                  ).getBytes());
                    } // end if
                    fos.write("\n\t\t</zmatrix>\n".getBytes());
                } // end z-matrix
                
                fos.write("\t</atom> \n".getBytes());
                
                atomIndex++;
            } // end while
            
            // and then the rings
            AtomGroupList rings = molecule.getRingRecognizer().getGroupList();
            Iterator ringList = rings.getGroups();
            Ring ring;
            
            fos.write("\t<rings> \n".getBytes());
            
            int i = 0;
            while(ringList.hasNext()) {
                ring = (Ring) ringList.next();
                
                fos.write(("\t\t<ring ringIndex=\"" + i + "\""
                  + " ringType=\"" + ring.getRingType() + "\" > \n").getBytes());
                
                // the atom indices that constitute this ring
                Iterator indices = ring.getAtomIndices();
                while(indices.hasNext()) {
                    fos.write(("\t\t\t<ra index=\"" + ((Integer) indices.next()) 
                               + "\" /> \n").getBytes());
                } // end while
                fos.write("\t\t</ring> \n".getBytes());
                
                i++;
            } // end while
                
            fos.write("\t</rings> \n".getBytes());
            
            // and the other atom groups, monomer lists
            Iterator<SpecialStructureRecognizer> ssrlist = 
                                     molecule.getSpecialStructureRecognizers();

            i = 0;
            fos.write("\t<atomGroups> \n".getBytes());
            while(ssrlist.hasNext()) {
              SpecialStructureRecognizer ssr = ssrlist.next();
        
              // rings have already been addressed
              if (ssr instanceof RingRecognizer) continue;
              
              AtomGroupList atomGroups = ssr.getGroupList();
              Iterator atomGroupList = atomGroups.getGroups();
              DefaultAtomGroup atomGroup;
                          
              while(atomGroupList.hasNext()) {
                atomGroup = (DefaultAtomGroup) atomGroupList.next();
                
                fos.write(("\t\t<atomGroup atomGroupIndex=\"" + i 
                           + "\" >\n").getBytes());
                
                // the atom indices that constitute this ring
                Iterator indices = atomGroup.getAtomIndices();
                while(indices.hasNext()) {
                    fos.write(("\t\t\t<ga index=\"" + ((Integer) indices.next()) 
                               + "\" /> \n").getBytes());
                } // end while
                fos.write("\t\t</atomGroup> \n".getBytes());
                
                i++;
              } // end while
            } // end while                        
            fos.write("\t</atomGroups> \n".getBytes());
            
            // write fragment information
            writeFragmentInfo(fos, molecule);
            
            fos.write("</molecule>\n".getBytes());
            
            // close the stuff
            fos.close();
        } catch(Exception e) {
            System.err.println("Unable to do save operation : " + e.toString());
            e.printStackTrace();
            
            throw new WorkspaceIOException("Unable to do save operation : " 
                                           + e.toString());
        } // end of try .. catch block
    }        
    
    /**
     * Write the fragments information
     */
    private void writeFragmentInfo(FileOutputStream fos, Molecule molecule) 
                                   throws IOException {
        fos.write("\t<fragments> \n".getBytes());
        
        Iterator<FragmentationScheme> schemes = 
                                      molecule.getAllFragmentationSchemes();
        FragmentationScheme scheme;
        
        while(schemes.hasNext()) {
            scheme = schemes.next();
            
            fos.write(("\t\t<fragmentationScheme" +
                    " name=\"" + scheme.getName() + 
                    "\" implCalss=\"" + scheme.getClass().toString() +
                    "\" > \n").getBytes());
            
            // now write in the fragments
            Iterator<Fragment> fragments = 
                               scheme.getFragmentList().getFragments();
            Fragment fragment;
            int i = 0;
            
            while(fragments.hasNext()) {
                fragment = fragments.next();
                
                fos.write(("\t\t\t<fragment index=\"" + i 
                       + "\" overlap=\"" + fragment.isOverlapFragment()
                       + "\" cardinalitySign=\"" + fragment.getCardinalitySign() 
                       + "\" totalCharge=\"" + fragment.getTotalCharge()
                       + "\" > \n").getBytes());
                
                Iterator<FragmentAtom> fAtoms = fragment.getFragmentAtoms();
                FragmentAtom fAtom;
                
                // fragment atoms
                fos.write(("\t\t\t\t<fragmentAtoms> \n").getBytes());
                while(fAtoms.hasNext()) {
                    fAtom = fAtoms.next();
                    
                    if (!fAtom.isDummy()) {
                        fos.write(("\t\t\t\t\t<fragmentAtom index=\"" +
                                fAtom.getIndex() + "\" /> \n").getBytes());
                    } // end if
                } // end while
                fos.write(("\t\t\t\t</fragmentAtoms> \n").getBytes());
                
                fAtoms = fragment.getFragmentAtoms();
                
                // boundary atoms
                fos.write(("\t\t\t\t<boundaryAtoms> \n").getBytes());
                int index = 0;
                while(fAtoms.hasNext()) {
                    fAtom = fAtoms.next();
                    
                    // TODO: is this index correct?
                    if (fAtom.isBoundaryAtom() && !fAtom.isDummy()) {
                        fos.write(("\t\t\t\t\t<boundaryAtom index=\"" +
                                   index + "\" /> \n").getBytes());
                    } // end if
                    
                    index++;
                } // end while
                fos.write(("\t\t\t\t</boundaryAtoms> \n").getBytes());
                
                fAtoms = fragment.getFragmentAtoms();
                
                // dummy atoms
                fos.write(("\t\t\t\t<dummyAtoms> \n").getBytes());
                while(fAtoms.hasNext()) {
                    fAtom = fAtoms.next();
                    
                    if (fAtom.isDummy()) {
                        fos.write(("\t\t\t\t\t<dummyAtom index=\"" +
                                fAtom.getIndex() + "\" symbol=\"" + 
                                fAtom.getSymbol() + "\" charge=\"" +
                                fAtom.getCharge() + "\" x=\"" + 
                                fAtom.getX() + "\" y=\"" + 
                                fAtom.getY() + "\" z=\"" + 
                                fAtom.getZ() + "\" /> \n").getBytes());
                    } // end if
                } // end while
                fos.write(("\t\t\t\t</dummyAtoms> \n").getBytes());
                
                fos.write("\t\t\t</fragment> \n".getBytes());
                i++;
            } // end while
            
            // TODO: then write info. on goodness probes
            
            // TODO: the corrector constraints need to be added
            
            fos.write("\t\t</fragmentationScheme> \n".getBytes());            
        } // end while
        
        fos.write("\t</fragments> \n".getBytes());
    }
    
    /**
     * method to update this object based on the XML configuration data.     
     */
    private void updateIt(Node n, Molecule mol) throws WorkspaceIOException {
        int type = n.getNodeType();   // get node type
        
        // TODO: other groups reading
        
        switch (type) {
            case Node.ATTRIBUTE_NODE:
                String nodeName = n.getNodeName();
                
                if (nodeName.equals("title")) {
                    mol.setTitle(n.getNodeValue());
                } else if (nodeName.equals("numberOfSingleBonds")) {
                    mol.setNumberOfSingleBonds(
                                   Integer.parseInt(n.getNodeValue()));
                } else if (nodeName.equals("numberOfMultipleBonds")) {
                    mol.setNumberOfMultipleBonds(
                                   Integer.parseInt(n.getNodeValue()));
                } else if (nodeName.equals("numberOfWeakBonds")) {
                    mol.setNumberOfWeakBonds(
                                   Integer.parseInt(n.getNodeValue()));
                } // end if
                
                break;
            case Node.ELEMENT_NODE:
                element = n.getNodeName();
            
                NamedNodeMap atts = n.getAttributes();
                
                if (element.equals("atom")) { // atom node
                    currentAtom = 
                    new Atom(
                     atts.getNamedItem("symbol").getNodeValue(),
                     Double.parseDouble(atts.getNamedItem("charge")
                                            .getNodeValue()), 
                     new Point3D(Double.parseDouble(atts.getNamedItem("x")
                                                        .getNodeValue()),
                                 Double.parseDouble(atts.getNamedItem("y")
                                                        .getNodeValue()),
                                 Double.parseDouble(atts.getNamedItem("z")
                                                        .getNodeValue())),
                     Integer.parseInt(atts.getNamedItem("atomIndex")
                                                 .getNodeValue())
                    );
                    
                    mol.addAtom(currentAtom);
                } else if (element.equals("ca")) { // connected atom node
                    currentAtom.addConnection(
                     Integer.parseInt(atts.getNamedItem("index")
                                          .getNodeValue()),
                     BondType.getBondTypeFor(atts.getNamedItem("bondType")
                                          .getNodeValue())
                    );
                } else if (element.equals("length")) { // length: ZMatrix
                    currentAtom.setLengthReference(new ZMatrixItem(
                      mol.getAtom(Integer.parseInt(atts.getNamedItem("index")
                                          .getNodeValue())),
                      Double.parseDouble(atts.getNamedItem("value")
                                          .getNodeValue())
                     )
                    );
                } else if (element.equals("angle")) { // angle: ZMatrix
                    currentAtom.setAngleReference(new ZMatrixItem(
                      mol.getAtom(Integer.parseInt(atts.getNamedItem("index")
                                          .getNodeValue())),
                      Double.parseDouble(atts.getNamedItem("value")
                                          .getNodeValue())
                     )
                    );
                } else if (element.equals("dihedral")) { // dihedral: ZMatrix
                    currentAtom.setDihedralReference(new ZMatrixItem(
                      mol.getAtom(Integer.parseInt(atts.getNamedItem("index")
                                          .getNodeValue())),
                      Double.parseDouble(atts.getNamedItem("value")
                                          .getNodeValue())
                     )
                    );
                } else if (element.equals("ring")) { // ring node
                    currentRing = new Ring(atts.getNamedItem("ringType")
                                           .getNodeValue());
                    mol.getRingRecognizer().getGroupList()
                                           .addGroup(currentRing);                    
                } else if (element.equals("ra")) { // ring atom node
                    currentRing.addAtomIndex(
                                   Integer.parseInt(atts.getNamedItem("index")
                                          .getNodeValue()));
                } else if (element.equals("atomGroup")) { // atom group node
                    currentAtomGroup = new DefaultAtomGroup();
                    mol.getDefaultSpecialStructureRecognizer().getGroupList()
                                                    .addGroup(currentAtomGroup);
                } else if (element.equals("ga")) { // group atom node
                    currentAtomGroup.addAtomIndex(
                                   Integer.parseInt(atts.getNamedItem("index")
                                          .getNodeValue()));
                } else if (element.equals("fragmentationScheme")) { 
                    // fragment scheme node
                    currentFragmentationScheme = 
                            fragmentationSchemeFactory.getScheme(
                                    atts.getNamedItem("name").getNodeValue());
                    
                    // add a default goodness probe 
                    try {
                     FragmentGoodnessProbeFactory gProbes = 
                      (FragmentGoodnessProbeFactory)
                       Utility.getDefaultImplFor(
                                  FragmentGoodnessProbeFactory.class)
                              .newInstance();
               
                     currentFragmentationScheme.addFragmentGoodnessProbe(
                      gProbes.getDefaultFragmentGoodnessProbe(
                                 currentFragmentationScheme, mol));
                    } catch (Exception e) {
                     System.err.println("Warning! Unable to propery initilize" +
                             "goodness probes!! :" + e.toString());   
                    } // end of try .. catch block
                    
                    mol.addFragmentationScheme(currentFragmentationScheme);
                } else if (element.equals("fragment")) { 
                    // fragment node
                    try {
                        currentFragment = (Fragment) 
                               Utility.getDefaultImplFor(Fragment.class)
                                       .newInstance();
                        currentFragment.setParentMolecule(mol);
                        
                        try {
                         currentFragment.setCardinalitySign(Integer.parseInt(
                          atts.getNamedItem("cardinalitySign").getNodeValue()));
                         currentFragment.setOverlapFragment(
                                Boolean.parseBoolean(
                          atts.getNamedItem("overlap").getNodeValue()));
                         currentFragment.setTotalCharge(Integer.parseInt(
                          atts.getNamedItem("totalCharge").getNodeValue()));
                        } catch (Exception e) {
                         System.out.println(
                                 "Can't read complete fragment info!"); 
                        } // end of try .. catch
                        
                        currentFragmentationScheme.getFragmentList()
                                            .addFragment(currentFragment);
                    } catch (Exception e) {
                        throw new WorkspaceIOException(e.toString());
                    } // end try .. catch
                } else if (element.equals("fragmentAtom")) { 
                    // fragment atom node
                    // TODO: Warning this is under the assumption that 
                    // the molecule object is already formed
                    currentFragment.addFragmentAtom(new FragmentAtom(
                            mol.getAtom(Integer.parseInt(
                                  atts.getNamedItem("index").getNodeValue()))));
                } else if (element.equals("boundaryAtom")) { 
                    // boundry atom node
                    // TODO: Warning this is under the assumption that 
                    // the molecule object and the relevent
                    // fragment atom is already formed                    
                    FragmentAtom fa = currentFragment.getFragmentAtom(
                        Integer.parseInt(
                           atts.getNamedItem("index").getNodeValue()));
                    
                    fa.setBoundaryAtom(true);
                    currentFragment.addFragmentAtom(fa);
                } else if (element.equals("dummyAtom")) { 
                    // dummy atom node
                    currentFragment.addFragmentAtom(new FragmentAtom(
                       atts.getNamedItem("symbol").getNodeValue(),
                       Double.parseDouble(
                            atts.getNamedItem("charge").getNodeValue()),
                       new Point3D(
                            Double.parseDouble(
                                atts.getNamedItem("x").getNodeValue()),
                            Double.parseDouble(
                                atts.getNamedItem("y").getNodeValue()),
                            Double.parseDouble(
                                atts.getNamedItem("z").getNodeValue())
                       ),
                       Integer.parseInt(
                            atts.getNamedItem("index").getNodeValue())
                    ));
                } else {
                    for (int i = 0; i < atts.getLength(); i++) {
                        Node att = atts.item(i);
                        updateIt(att, mol);
                    } // end for
                } // end if
                
                break;
            default:
                break;
        } // end of switch .. case block
        
        // save children if any
        for (Node child = n.getFirstChild(); child != null;
             child = child.getNextSibling()) {
             updateIt(child, mol);
        } // end for
    }
    
    /**
     * Overridden setItemData()
     */
    @Override
    public void setItemData(ItemData itemData) {
        if (!(itemData.getData() instanceof Molecule)) {
            throw new UnsupportedOperationException("Only instances of " +
                       "Molecule class can be passed to this method.");            
        } // end if
        
        if (getItemData() != null) {
            try {
                finalize();
            } catch (Throwable ignored) { }
        } // end if
        
        super.setItemData(itemData);
        ((Molecule) getItemData().getData())
                                     .addMoleculeStateChangeListener(this);
    }
    
    /**
     * listen to changes in the contained Molecule object.
     */
    @Override
    public void moleculeChanged(MoleculeStateChangeEvent event) {
        wice.setMessage(event.toString());
        fireWorkspaceItemChangeListenerWorkspaceItemChanged(wice);
    }
        
    /**
     * clean up!!
     *
     * @throws Throwable the <code>Exception</code> raised by this method
     */
    @Override
    public void finalize() throws Throwable {
        ((Molecule) getItemData().getData())
                                     .removeMoleculeStateChangeListener(this);
    }
    
} // end of class MoleculeItem
