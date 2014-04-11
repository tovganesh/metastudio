/*
 * GoodnessIndicator.java
 *
 * Created on September 1, 2006, 10:39 PM
 */

package org.meta.shell.idebeans;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.meta.common.ColorToFunctionRangeMap;
import org.meta.fragment.Fragment;
import org.meta.fragment.FragmentList;
import org.meta.fragmentor.FragmentAtomGoodness;
import org.meta.fragmentor.FragmentGoodnessProbe;
import org.meta.molecule.Atom;
import org.meta.molecule.Molecule;

/**
 * A special UI to display color coded goodness of fragment atoms.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class GoodnessIndicator extends JPanel {
    
    private NotificationWindow nw;
    
    /** Creates a new instance of GoodnessIndicator */
    public GoodnessIndicator() {
        initUI();
    }
    
    /**
     * Initilise the UI
     */
    private void initUI() {                
        setPreferredSize(new Dimension(100, 20));                
                
        updateGoodnessIndicators(); 
        
        addMouseMotionListener(new MouseMotionAdapter() {            
            @Override
            public void mouseMoved(MouseEvent e) {
                for(int i=0, j=0; i<width; i+=widthOfEachAtom, j++) {                    
                    Rectangle r = new Rectangle(i, 0, widthOfEachAtom, height);
                    if (r.contains(e.getPoint())) {                        
                        if (referenceGoodnessProbe
                                 .getBestGoodnessIndex(j, j) != null) {
                          setToolTipText("Atom # " + j + " ("
                                + referenceMolecule.getAtom(j).getSymbol() 
                                + "), R-Goodness: "
                            + new DecimalFormat("#.###").format(
                               referenceGoodnessProbe.getBestGoodnessIndex(j, j)
                                                    .getValue()));
                        } else {
                             setToolTipText("Atom # " + j + " ("
                                + referenceMolecule.getAtom(j).getSymbol() 
                                + "), is not represented in any fragment!");
                        } // end if
                        
                        return;
                    } // end if
                } // end for
            }
        });                
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for(int i=0, j=0; i<width; i+=widthOfEachAtom, j++) {                    
                    Rectangle r = new Rectangle(i, 0, widthOfEachAtom, height);
                    if (r.contains(e.getPoint())) {     
                        Point p = new Point(e.getX(), e.getY());
        
                        SwingUtilities.convertPointToScreen(p, 
                                                        GoodnessIndicator.this);
        
                        FragmentList fl = referenceGoodnessProbe
                                  .getFragmentationScheme().getFragmentList();
                        Iterator<FragmentAtomGoodness> fag = 
                            referenceGoodnessProbe.getGoodnessIndices(j, j);
                        
                        if (fag == null) return;
                        
                        if (nw != null) {
                            nw.hideIt();
                            nw = null;
                        } // end if
                        
                        nw = new NotificationWindow(
                                "Atom R-Goodness (Atom # " + j + " ["
                                + referenceMolecule.getAtom(j).getSymbol() 
                                + "] )");
                        
                        FragmentAtomGoodness fragGoodness;
                        final Hashtable<Integer, Double> fragmnetIndices 
                                      = new Hashtable<Integer, Double>();
                        int bestFragIndex = -1;
                        double bestFragGoodness = -1.0;
                        
                        while(fag.hasNext()) {
                            fragGoodness = fag.next();
                            
                            Fragment frag = fragGoodness.getFragment();
                            double value = fragGoodness.getValue();
                            int idx = 0;
                            
                            for(idx=0; idx<fl.size(); idx++) {
                                if (fl.getFragment(idx).equals(frag)) break;
                            } // end for
                            
                            fragmnetIndices.put(idx, value);
                            
                            if (fragGoodness.isBest()) {
                                bestFragIndex = idx;
                                bestFragGoodness = value;
                            } // end if
                        } // end while                                              
                        
                        nw.getContentPane().setLayout(
                                               new IDEVerticalFlowLayout());
                        nw.getContentPane().add(
                         new JLabel("<html><body>Best <i>R-Goodness</i> of: <b>" 
                          + new DecimalFormat("#.###").format(bestFragGoodness) 
                          + "</b> is in Fragment # <b>" + bestFragIndex
                          + "</b></body></html>"));
                        
                        JPanel goodessPanel 
                                = new JPanel(new FlowLayout(FlowLayout.LEFT));
                        
                        goodessPanel.add(new JLabel("<html><body>" +
                                "<i>R-Goodness</i> in Fragment #"));
                        
                        final JList fragmnetGoodness = new JList(
                                            fragmnetIndices.keySet().toArray());
                        final JLabel currentRGoodness = new JLabel(" is : ");
                                                
                        fragmnetGoodness.addListSelectionListener(
                          new ListSelectionListener() {
                            @Override
                            public void valueChanged(ListSelectionEvent e) {
                                double goodness = fragmnetIndices.get(
                                        fragmnetGoodness.getSelectedValue());
                                
                                currentRGoodness.setText("<html><body> is : <b>"
                                  + new DecimalFormat("#.###").format(goodness) 
                                  + "</b></body></html>");
                            }
                        });
                                                  
                        fragmnetGoodness.setSelectedIndex(0);
                        goodessPanel.add(new JScrollPane(fragmnetGoodness));
                                                
                        goodessPanel.add(currentRGoodness);
                        
                        nw.getContentPane().add(goodessPanel);                        
                        
                        nw.showIt(GoodnessIndicator.this, p.x, p.y);
                        
                        return;
                    } // end if
                } // end for
            }                        
        });
    }
    
    private int widthOfEachAtom;
    private int width, height;
    
    /**
     * Update the goodness indicators
     */
    public void updateGoodnessIndicators() {
        if (referenceMolecule == null) return;                
        
        width = getWidth();
        height = getHeight();
        if ((width == 0) || (height == 0)) return;
        
        widthOfEachAtom = width / referenceMolecule.getNumberOfAtoms();
        
        if (width % referenceMolecule.getNumberOfAtoms() != 0) {
            widthOfEachAtom++;
        } // end if
        
        if (widthOfEachAtom < 2) widthOfEachAtom = 2;
        
        if (referenceGoodnessProbe == null) return;
        
        // TODO : this should be done in a separate thread 
        referenceGoodnessProbe.runGoodnessProbe();
        
        if (goodnessThreshold == 0) {
          goodnessThreshold 
           = referenceGoodnessProbe.getOverallMaximumGoodnessIndex().getValue();
        } // end if
    }
    
    /**
     * overidded paint
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        
        updateGoodnessIndicators();
        
        g.setColor(Color.black);
        g.drawRect(0, 0, width-1, height-1);                
        
        if (referenceGoodnessProbe == null) return;
        
        ColorToFunctionRangeMap map = new ColorToFunctionRangeMap(
           Color.red, Color.green, 
           0.0, goodnessThreshold);
                
        FragmentAtomGoodness fag;
        for(int i=0, j=0; i<width; i+=widthOfEachAtom, j++) {
            fag = referenceGoodnessProbe.getBestGoodnessIndex(j, j);
            
            if (fag != null) {
                g.setColor(map.getInterpolatedColor(fag.getValue()));
            } else {
                g.setColor(map.getInterpolatedColor(-1));
            } // end if
            
            g.fillRect(i, 0, widthOfEachAtom, height);
        } // end for                
    }
    
    /**
     * Holds value of property referenceMolecule.
     */
    private Molecule referenceMolecule;

    /**
     * Getter for property referenceMolecule.
     * @return Value of property referenceMolecule.
     */
    public Molecule getReferenceMolecule() {
        return this.referenceMolecule;
    }

    /**
     * Setter for property referenceMolecule.
     * @param referenceMolecule New value of property referenceMolecule.
     */
    public void setReferenceMolecule(Molecule referenceMolecule) {
        this.referenceMolecule = referenceMolecule;
        this.referenceGoodnessProbe 
                = referenceMolecule.getDefaultFragmentationScheme()
                                   .getFragmentGoodnessProbes().next();
        
        updateGoodnessIndicators();
        updateUI();
    }

    /**
     * get the list of offending atoms that have goodness values, less 
     * than the threadhold level specified.
     *
     * @param excludePendents exclude pendent atoms ?
     * @return an array of atoms that are offending the goodness threashold 
     *         criterion
     */
    public ArrayList<Atom> getOffendingAtoms(boolean excludePendents) {        
        ArrayList<Atom> offendingAtoms = new ArrayList<Atom>();
        
        if (!isVisible()) return offendingAtoms;
        
        for(int i=0; i<referenceMolecule.getNumberOfAtoms(); i++) {
            if (excludePendents) {
                if (referenceMolecule.getAtom(i).getConnectedList().size() == 1)
                    continue;
            } // end if
            
            if (referenceGoodnessProbe.getBestGoodnessIndex(i, i) == null) {
                continue;
            } // end if
            
            System.out.println(i + " : " 
                + referenceGoodnessProbe.getBestGoodnessIndex(i, i).getValue());
            
            if (referenceGoodnessProbe.getBestGoodnessIndex(i, i).getValue()
                 < goodnessThreshold) {
                offendingAtoms.add(referenceMolecule.getAtom(i));
            } // end if
        } // end for
        
        if (referenceGoodnessProbe.getOverallMaximumGoodnessIndex() != null
            && referenceGoodnessProbe.getOverallMaximumGoodnessAtom() != null) {
          System.out.println("Overall Maximum: " 
           + referenceGoodnessProbe.getOverallMaximumGoodnessIndex() + " at " 
           + referenceGoodnessProbe.getOverallMaximumGoodnessAtom().getIndex());
          System.out.println("Overall Minimum: " 
           + referenceGoodnessProbe.getOverallMinimumGoodnessIndex() + " at "
           + referenceGoodnessProbe.getOverallMinimumGoodnessAtom().getIndex());
        } // end if
        
        return offendingAtoms;
    }
    
    /**
     * Holds value of property referenceGoodnessProbe.
     */
    private FragmentGoodnessProbe referenceGoodnessProbe;

    /**
     * Getter for property referenceGoodnessProbe.
     * @return Value of property referenceGoodnessProbe.
     */
    public FragmentGoodnessProbe getReferenceGoodnessProbe() {
        return this.referenceGoodnessProbe;
    }

    /**
     * Setter for property referenceGoodnessProbe.
     * @param referenceGoodnessProbe New value of property 
     *                               referenceGoodnessProbe.
     */
    public void setReferenceGoodnessProbe(FragmentGoodnessProbe 
                                                 referenceGoodnessProbe) {
        this.referenceGoodnessProbe = referenceGoodnessProbe;
                
        updateGoodnessIndicators();
        updateUI();
    }

    /**
     * Holds value of property goodnessThreshold.
     */
    private double goodnessThreshold;

    /**
     * Getter for property goodnessThreshold.
     * @return Value of property goodnessThreshold.
     */
    public double getGoodnessThreshold() {
        return this.goodnessThreshold;
    }

    /**
     * Setter for property goodnessThreshold.
     * @param goodnessThreshold New value of property goodnessThreshold.
     */
    public void setGoodnessThreshold(double goodnessThreshold) {
        this.goodnessThreshold = goodnessThreshold;
        
        updateGoodnessIndicators();
        updateUI();
    }
    
} // end of class GoodnessIndicator
