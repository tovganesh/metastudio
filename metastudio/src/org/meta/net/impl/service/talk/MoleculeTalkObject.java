/*
 * MoleculeTalkObject.java
 *
 * Created on August 4, 2006, 10:25 PM
 */

package org.meta.net.impl.service.talk;

import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;
import org.meta.math.geom.Point3D;
import org.meta.common.Utility;
import org.meta.molecule.Atom;
import org.meta.molecule.BondType;
import org.meta.molecule.Molecule;
import org.meta.molecule.ZMatrixItem;

/**
 * Encapsulate molecule object as a talk object that can be transported over
 * network i/o stream.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MoleculeTalkObject extends TalkObject 
                                implements Externalizable {
    
    /** Creates a new instance of MoleculeTalkObject */
    public MoleculeTalkObject() {        
        setType(TalkObject.TalkObjectType.MOLECULE);
    }

    /**
     * Write the molecule object as XML file.
     *
     * @throws IOException if error occured
     */
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {      
        try {
            Molecule molecule = (Molecule) talkObjectContent;
            
            // pass basic information about molecule
            byte [] stringBytes = molecule.getTitle().getBytes();
            out.writeInt(stringBytes.length);
            out.write(stringBytes);
            out.writeInt(molecule.getNumberOfAtoms());            
            out.writeInt(molecule.getNumberOfSingleBonds());
            out.writeInt(molecule.getNumberOfMultipleBonds());            
            out.writeInt(molecule.getNumberOfWeakBonds());
            out.writeBoolean(molecule.isZMatrixComputed());
            out.writeBoolean(molecule.isAdditionalInformationAvailable());
            
            // then pass atoms and its connectivity
            for(int i=0; i<molecule.getNumberOfAtoms(); i++) {
                Atom atom = molecule.getAtom(i);
                
                out.writeInt(atom.getIndex());
                stringBytes = atom.getSymbol().getBytes();
                out.writeInt(stringBytes.length);
                out.write(stringBytes);
                out.writeDouble(atom.getX());
                out.writeDouble(atom.getY());
                out.writeDouble(atom.getZ());
                out.writeDouble(atom.getCharge());
                
                // connectivity
                Hashtable<Integer, BondType> conn = atom.getConnectedList();
                
                out.writeInt(conn.size());
                Enumeration<Integer> bonds = conn.keys();
                for(int j=0; j<conn.size(); j++) {
                    Integer bond = bonds.nextElement();
                    
                    out.writeInt(bond);
                    out.writeObject(conn.get(bond));
                } // end for                                
            } // end for
            
            // write out Z-matrix, if available             
            if (molecule.isZMatrixComputed()) {
               for(int i=0; i<molecule.getNumberOfAtoms(); i++) {
                    Atom atom = molecule.getAtom(i);
                    
                    // write out Z-matrix elements                    
                    // length:         
                    if (i > 0) {
                      out.writeInt(atom.getLengthReference().getReferenceAtom()
                                                            .getIndex());
                      out.writeDouble(atom.getLengthReference().getValue());
                    } // end if
                    
                    // angle:   
                    if (i > 1) {
                      out.writeInt(atom.getAngleReference().getReferenceAtom()
                                                           .getIndex());
                      out.writeDouble(atom.getAngleReference().getValue());
                    } // end if
                    
                    // dihedral:
                    if (i > 2) {
                      out.writeInt(atom.getDihedralReference().getReferenceAtom()
                                                              .getIndex());
                      out.writeDouble(atom.getDihedralReference().getValue());
                    } // end if
               } // end for
            } // end if
                
            // TODO:
            // pass in any special structures identified
            
            // and pass on any additional information attached to this
            // molecule object            
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Unexpected exception! " + e);
        } // end of try .. catch block
    }

    /**
     * Read the molecule object as from XML file.
     *
     * @throws IOException if error occured
     * @throws ClassNotFoundException missing Molecule class?
     */
    @Override
    public void readExternal(ObjectInput in) throws IOException, 
                                                    ClassNotFoundException {
        try {
            Molecule molecule = 
             (Molecule) Utility.getDefaultImplFor(Molecule.class).newInstance();
            
            // pass basic information about molecule
            int len = in.readInt();
            byte [] stringBytes = new byte[len];
            
            in.read(stringBytes);
            molecule.setTitle(new String(stringBytes));                        
            
            int noOfAtoms = in.readInt();
            
            molecule.disableListeners();
            molecule.setNumberOfSingleBonds(in.readInt());
            molecule.setNumberOfMultipleBonds(in.readInt());
            molecule.setNumberOfWeakBonds(in.readInt());
            molecule.setZMatrixComputed(in.readBoolean());
            molecule.setAdditionalInformationAvailable(in.readBoolean());                        
            
            // then pass atoms and its connectivity
            for(int i=0; i<noOfAtoms; i++) {                
                
                int index = in.readInt();
                
                len = in.readInt();
                stringBytes = new byte[len];
                in.read(stringBytes);                
                String symbol = new String(stringBytes);                                
                
                Point3D center = new Point3D(in.readDouble(), in.readDouble(),
                                             in.readDouble());
                
                double charge = in.readDouble();
                
                Atom atom = new Atom(symbol, charge, center, index);
                
                // connectivity                                
                int connSize = in.readInt();                                
                for(int j=0; j<connSize; j++) {
                    atom.addConnection(in.readInt(), 
                                       (BondType) in.readObject());
                } // end for
                
                molecule.addAtom(atom);
            } // end for
            
            // write out Z-matrix, if available
            if (molecule.isZMatrixComputed()) {
               for(int i=0; i<molecule.getNumberOfAtoms(); i++) {
                    Atom atom = molecule.getAtom(i);
                    
                    // write out Z-matrix elements                    
                    // length:  
                    if (i > 0) {
                      atom.setLengthReference(new ZMatrixItem(
                            molecule.getAtom(in.readInt()), in.readDouble()));
                    } // end if
                    
                    // angle:                    
                    if (i > 1) {
                      atom.setAngleReference(new ZMatrixItem(
                            molecule.getAtom(in.readInt()), in.readDouble()));
                    } // end if
                    
                    // dihedral:
                    if (i > 2) {
                      atom.setDihedralReference(new ZMatrixItem(
                            molecule.getAtom(in.readInt()), in.readDouble()));
                    } // end if
               } // end for
            } // end if
                
            // TODO:
            // read in any special structures identified
            
            // and read in any additional information attached to this
            // molecule object
            
            molecule.enableListeners();
            talkObjectContent = molecule;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Unexpected exception! " + e);
        } // end of try .. catch block
    }
    
} // end of class MoleculeTalkObject
