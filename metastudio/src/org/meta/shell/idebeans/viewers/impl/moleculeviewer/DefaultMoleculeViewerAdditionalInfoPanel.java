/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.meta.shell.idebeans.viewers.impl.moleculeviewer;

import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import org.meta.common.Utility;
import org.meta.molecule.Atom;
import org.meta.molecule.Molecule;
import org.meta.shell.idebeans.IDEVerticalFlowLayout;
import org.meta.shell.idebeans.NotificationWindow;
import org.meta.shell.idebeans.NotificationWindowType;

/**
 * Default implementation of MoleculeViewerAdditionalInfoPanel
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class DefaultMoleculeViewerAdditionalInfoPanel
             implements MoleculeViewerAdditionalInfoPanel {

    private NotificationWindow metaInfoWindow;

    private MoleculeViewer currentMoleculeViewer;

    /** Creates a new instance of DefaultMoleculeViewerAdditionalInfoPanel */
    public DefaultMoleculeViewerAdditionalInfoPanel() {
        metaInfoWindow = new NotificationWindow("Additional Information",
                                                 SwingConstants.NORTH,
                                                 NotificationWindowType.NORMAL);
    }

    /**
     * Show this panel attached to a MoleculeViewer object
     *
     * @param mv MoleculeViewer to which this viewer must be attached
     */
    @Override
    public void showIt(MoleculeViewer mv) {
        currentMoleculeViewer = mv;

        MoleculeScene ms = mv.getFirstVisibleScene();

        if (ms == null) return;

        Molecule mol = ms.getMolecule();

        JComponent cPane = metaInfoWindow.getContentPane();
        cPane.setLayout(new IDEVerticalFlowLayout());
        cPane.removeAll();
        
        if (mol.isAdditionalInformationAvailable()) {
            final Molecule.AdditionalInformation ai
                           = mol.getAdditionalInformation();

            if (ai.isEnergyAvailable()) {
                JPanel energyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

                energyPanel.add(new JLabel("Energy : " + ai.getEnergy()
                                     + " " + ai.getEnergyUnit()));

                cPane.add(energyPanel);
            } // end if

            if (ai.isGradientAvailable()) {
                JPanel gradientPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

                gradientPanel.add(new JLabel("Gradient: (Max="
                                     + ai.getMaxGradient()
                                     + ") (RMS=" + + ai.getRmsGradient()
                                     + ") " + ai.getGradientUnit()));
                cPane.add(gradientPanel);
            } // end if

            if (ai.isDipoleAvailable()) {
                JPanel dipolePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

                dipolePanel.add(new JLabel("Dipole : "
                                + ai.getDipole().magnitude()
                                + " " + ai.getDipoleUnit()));

                JButton showDipoleVector = new JButton("Show dipole vector");
                showDipoleVector.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            Molecule mol = (Molecule) Utility.getDefaultImplFor(
                                                  Molecule.class).newInstance();

                            mol.addAtom(new Atom("vec", 0.0,
                                         ai.getDipole().toPoint3D(), 0));

                            currentMoleculeViewer.addScene(
                                                     new MoleculeScene(mol));
                        } catch (Exception ignored) {
                            System.out.println("Exception in showDipoleVector: "
                                              + ignored);
                            ignored.printStackTrace();
                        } // end of try .. catch block
                    }
                });
                dipolePanel.add(showDipoleVector);

                cPane.add(dipolePanel);
            } // end if

            Point p = new Point(10, 10);
            SwingUtilities.convertPointToScreen(p, mv);
            metaInfoWindow.setClickClosable(false);
            metaInfoWindow.showIt(mv, p.x, p.y);
        } // end if
    }

    /**
     * Hide this panel
     */
    @Override
    public void hideIt() {
        if (metaInfoWindow != null) metaInfoWindow.hideIt();
    }
}
