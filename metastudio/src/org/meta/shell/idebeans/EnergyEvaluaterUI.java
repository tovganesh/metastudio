/*
 * EnergyEvaluaterUI.java
 *
 * Created on January 24, 2007, 10:45 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.meta.shell.idebeans;

import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import org.meta.common.resource.MiscResource;
import org.meta.fragment.FragmentList;
import org.meta.fragmentor.EnergyEvaluater;
import org.meta.fragmentor.FragmentationScheme;
import org.meta.shell.ide.MeTA;

/**
 * Energy evaluater UI for evaluating energy from cardinality expression.
 * Ref:  V.Ganesh <i>et al. </i> J. Chem. Phys., <b>125</b>, 104109 (2006).
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class EnergyEvaluaterUI extends GenericIDEWizard {
    
    private MeTA ideInstance;
    private FragmentationScheme fragmentScheme;
    
    /* panel 1 UI */
    private JRadioButton manually, throughFile;
    private JLabel energyFileLabel;
    private JButton browse;
    private JTextField energyFile;
    
    /* panel 2 UI */
    private JTextField expression, energy;
    private JButton recalculate;
    private JTable energiesTable;
    
    /** Creates a new instance of EnergyEvaluaterUI */
    public EnergyEvaluaterUI(MeTA ideInstance, FragmentationScheme fs) {
        super(ideInstance, 
                "Energy evaluater: energy using cardinality expression", 2, 
             (new String[] {"Choose options", "Check and compute energy"}),
             (new String[] {"Either enter energies manually or read from file", 
                            "Check and compute energy from the fragment " +
                            "cardinality expression."}));
        
        this.ideInstance = ideInstance;
        this.fragmentScheme = fs;                           
        
        setSize(MiscResource.getInstance().getEnergyEvaulaterUIDimension());
        setLocationRelativeTo(ideInstance);
        setVisible(true);
    }
    
    /**
     * set up all the wizard pages one by one
     */
    @Override
    protected void initWizardPages() {
        super.initWizardPages();
                
        makePageOne();
        makePageTwo();     
    }
    
    /**
     * setup page1 of the wizard
     */
    protected void makePageOne() {
        // grid bag constraints
        GridBagConstraints gbc = new GridBagConstraints();        
        gbc.insets = new Insets(2, 2, 2, 0);
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.fill   = GridBagConstraints.BOTH;
        
        // >>>>> Page 1
        wizardPages[0].setLayout(new GridBagLayout());                
        wizardPages[0].setAutoscrolls(true);
        // the following needed for the autoscrolls features
        wizardPages[0].addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
                ((JPanel)e.getSource()).scrollRectToVisible(r);
            }
        });
        
        // the following two buttons are disabled in this page at the 
        // beginning:        
        finish.setEnabled(false);
        
        gbc.gridx = gbc.gridy = 0;
        wizardPages[0].add(new JLabel("Energy evaluater helps you to " +
                "estimate energy from fragment cardinality expression."), gbc);
        
        
        JPanel chooseOptPanel = new JPanel(new GridBagLayout());
        chooseOptPanel.setBorder(
                BorderFactory.createTitledBorder("Choose an option:"));
        
            gbc.gridx = gbc.gridy = 0;
            gbc.gridwidth = 3;
            ButtonGroup bg = new ButtonGroup();            
            
            manually = new JRadioButton("Enter fragment energies manually");
            manually.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (manually.isSelected()) {
                        energyFile.setEnabled(false);
                        energyFileLabel.setEnabled(false);
                        browse.setEnabled(false);
                    } // end if
                }
            });
            bg.add(manually);
            chooseOptPanel.add(manually, gbc);            
            
            gbc.gridy = 1;
            throughFile = new JRadioButton("Read energies from a file");
            throughFile.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (throughFile.isSelected()) {
                        energyFile.setEnabled(true);
                        energyFileLabel.setEnabled(true);
                        browse.setEnabled(true);
                    } // end if
                }
            });
            bg.add(throughFile);
            chooseOptPanel.add(throughFile, gbc);
            throughFile.setSelected(true);
            
            gbc.gridwidth = 1;
            gbc.gridy = 2;
            
            gbc.gridx = 0;
            energyFileLabel = new JLabel("Energy file: ", JLabel.RIGHT);
            chooseOptPanel.add(energyFileLabel, gbc);
            
            gbc.gridx = 1;
            energyFile = new JTextField(10);
            energyFileLabel.setLabelFor(energyFile);
            energyFileLabel.setDisplayedMnemonic('f');
            chooseOptPanel.add(energyFile, gbc);
            
            gbc.gridx = 2;
            browse = new JButton("Browse...");
            browse.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    IDEFileChooser locationChooser = new IDEFileChooser();                    
                    locationChooser.setDialogTitle("Choose a location...");
                    
                    if (locationChooser.showOpenDialog(EnergyEvaluaterUI.this)
                         == IDEFileChooser.APPROVE_OPTION) {
                        energyFile.setText(
                           locationChooser.getSelectedFile().getAbsolutePath());
                    } // end if
                }
            });
            chooseOptPanel.add(browse, gbc);
            
        gbc.gridx = 0;
        gbc.gridy = 1;
        wizardPages[0].add(chooseOptPanel, gbc);
    }
     
    /**
     * overridden nextActionPerformed()
     */
    @Override
    protected void nextActionPerformed(ActionEvent event) {
        if (currentStep == 0) {
           setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
           wizardPages[1].removeAll();
           makePageTwo();  
           setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        } // end if 
        
        super.nextActionPerformed(event);
    }
    
    /**
     * setup page1 of the wizard
     */
    protected void makePageTwo() {
        // grid bag constraints
        GridBagConstraints gbc = new GridBagConstraints();        
        gbc.insets = new Insets(2, 2, 2, 0);
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.fill   = GridBagConstraints.BOTH;
        
        // >>>>> Page 2
        wizardPages[1].setLayout(new GridBagLayout());                
        wizardPages[1].setAutoscrolls(true);
        // the following needed for the autoscrolls features
        wizardPages[1].addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
                ((JPanel)e.getSource()).scrollRectToVisible(r);
            }
        });
        
        gbc.gridx = gbc.gridy = 0;
        gbc.gridwidth = 3;
        wizardPages[1].add(new JLabel("Fragment Energies:", JLabel.LEFT), gbc);
        
        // the the table
        gbc.gridheight = 4;
        energiesTable = new JTable(new FragmentEnergyModel());        
        gbc.gridx = 0; gbc.gridy = 1;
        energiesTable.setBorder(BorderFactory.createEtchedBorder());
        JScrollPane sp = new JScrollPane(energiesTable, 
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        wizardPages[1].add(sp, gbc);        
        
        // rest
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.gridx = 0; gbc.gridy = 5;
        wizardPages[1].add(new JLabel("Expression:", JLabel.RIGHT), gbc);
        
        gbc.gridx = 1; gbc.gridy = 5;
        gbc.gridwidth = 2;
        expression = new JTextField(20);
        expression.setEditable(false);
        wizardPages[1].add(expression, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 1;
        wizardPages[1].add(new JLabel("Estimated energy:", JLabel.RIGHT), gbc);
        
        gbc.gridx = 1; gbc.gridy = 6;
        gbc.gridwidth = 1;
        energy = new JTextField(20);
        energy.setEditable(false);
        wizardPages[1].add(energy, gbc);
        
        gbc.gridx = 2; gbc.gridy = 6;
        gbc.gridwidth = 1;
        recalculate = new JButton("Recalculate");
        recalculate.setMnemonic('R');
        recalculate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Hashtable<Integer, Double> fragmentEnergies
                        = new Hashtable<Integer, Double>();
                
                TableModel tm = energiesTable.getModel();
                FragmentList fl = fragmentScheme.getFragmentList();
                
                for(int i=0; i<fl.size(); i++) {
                    fragmentEnergies.put(i, 
                            Double.parseDouble(tm.getValueAt(i, 2).toString()));
                } // end for
                
                EnergyEvaluater ee = new EnergyEvaluater(fragmentScheme,
                                                         fragmentEnergies);
                energy.setText(ee.getEnergy()+"");
                expression.setText(ee.getEnergyExpression());
            }
        });
        wizardPages[1].add(recalculate, gbc);
    }
    
    /**
     * prepare energy data and return it as 2D array
     */
    private Object [][] getEnergyData() {
        if (fragmentScheme == null) {
            return new Object[1][3];
        } // end if
        
        FragmentList fl = fragmentScheme.getFragmentList();
        
        Object [][] rowData = new Object[fl.size()][3];
                
        for(int i=0; i<fl.size(); i++) {
            rowData[i][0] = i + "";
            rowData[i][1] = fl.getFragment(i).getCardinalitySign() + "";
            rowData[i][2] = "0.0";
        } // end for
        
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                                    new FileInputStream(energyFile.getText())));
            boolean isZeroIndexed = false;
            
            while(true) {
                String line = br.readLine();
                
                if ((line == null) || (line.equals(""))) break;
                    
                String [] words = line.split("\\s+");
                                
                if (Integer.parseInt(words[0].trim()) == 0) {
                    isZeroIndexed = true;
                    break;
                } // end if
            } // end while
            
            br.close();
            
            br = new BufferedReader(new InputStreamReader(
                                    new FileInputStream(energyFile.getText())));
            
            while(true) {
                String line = br.readLine();
                
                if ((line == null) || (line.equals(""))) break;
                
                String [] words = line.split("\\s+");
                
                if (isZeroIndexed) {
                    rowData[Integer.parseInt(words[0].trim())][2]
                            = Double.parseDouble(words[1].trim());
                } else {
                    rowData[Integer.parseInt(words[0].trim())-1][2]
                            = Double.parseDouble(words[1].trim());
                } // end if
            } // end while
            
            br.close();
        } catch (Exception ignored) {
            System.err.println("Exception reading energy file: " + ignored);
            ideInstance.getStatusBar().setStatusText(
                    "Error reading energy file: " + ignored);
        } // end of try .. catch block
        
        return rowData;
    }
    
    /**
     * Fragment energy data model
     */
    public class FragmentEnergyModel extends AbstractTableModel {
        
        private Object [][] rowData;
        
        public FragmentEnergyModel() {
            rowData = getEnergyData();
        }

        @Override
        public int getColumnCount() {             
            return 3; 
        }

        @Override
        public int getRowCount() {             
            return rowData.length;
        }

        @Override
        public Object getValueAt(int row, int col) {
            try {
                return rowData[row][col];
            } catch (Exception e) {                
                return new String();
            }
        }

        @Override
        public String getColumnName(int column) {
            if (column == 0) return "Fragment #";
            if (column == 1) return "Sign";
            if (column == 2) return "Energy";
            
            return "";
        }

        @Override
        public Class getColumnClass(int c) {
            return String.class;
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            if (col < 2) return false;
            return true;
        }

        @Override
        public void setValueAt(Object aValue, int row, int column) {
            try {
                rowData[row][column] = aValue;                
            } catch (Exception ignored) { }
        }
    } // end of FragmentEnergyModel
    
} // end of class EnergyEvaluaterUI
