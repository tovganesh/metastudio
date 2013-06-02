/*
 * IDEFederationRuleEditor.java 
 *
 * Created on 14 Sep, 2008 
 */

package org.meta.shell.idebeans;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.meta.shell.ide.MeTA;
import org.meta.common.resource.MiscResource;
import org.meta.common.resource.StringResource;
import org.meta.net.FederationRequestType;
import org.meta.net.security.FederationSecurityAction;
import org.meta.net.security.FederationSecurityRule;
import org.meta.net.security.FederationSecurityShield;

/**
 * UI for editing federation rules.
 * 
 * @author V. Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class IDEFederationRuleEditor extends JDialog {

    private JTable ruleTable;    
    private MeTA ideInstance;
    private FederationSecurityShield fsheild;
    private JButton appendRule, deleteRule, defaultRules;
    private JButton moveUp, moveDown;
    
    private Object [][] rulesData;
    
    /**
     * Creates a new instance of IDEFederationRuleEditor
     * 
     * @param ideInstance the IDE instance
     */
    public IDEFederationRuleEditor(MeTA ideInstance) {            
        super(ideInstance);
        setTitle("Federation rule editor for: " 
                 + StringResource.getInstance().getVersion());
        
        this.ideInstance = ideInstance;                                
        
        initComponents();
        setSize(MiscResource.getInstance().getFederationRuleEditorDimension());
        setLocationRelativeTo(ideInstance);
        setVisible(true);
    }
    
    /**
     * initlize UI
     */
    private void initComponents() {
        setLayout(new BorderLayout());
        
        JPanel commandsPanel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        appendRule = new JButton("Append Rule");
        appendRule.setMnemonic('A');
        appendRule.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                DefaultTableModel dtm = 
                                (DefaultTableModel) ruleTable.getModel();
                    
                dtm.addRow(new Object[] {
                            dtm.getRowCount(),
                            "127.0.0.1",
                            FederationRequestType._ANY,
                            FederationSecurityAction.PROMPT
                          });              
                
                saveRules();
            }
        });
        commandsPanel1.add(appendRule);
        
        deleteRule = new JButton("Delete Rule");
        deleteRule.setMnemonic('l');
        deleteRule.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                DefaultTableModel tm = 
                                (DefaultTableModel) ruleTable.getModel();
                    
                if (ruleTable.getSelectedRow() == -1) return;
                
                tm.removeRow(ruleTable.getSelectedRow());     
                
                saveRules();
            }
        });
        commandsPanel1.add(deleteRule);
        
        defaultRules = new JButton("Set default rules");
        defaultRules.setMnemonic('S');
        defaultRules.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                DefaultTableModel dtm = 
                                (DefaultTableModel) ruleTable.getModel();                    
                int noOfRows = dtm.getRowCount();
                
                // remove all data!
                for(int i=0; i<noOfRows; i++) dtm.removeRow(0);    
                
                // and add a default rule
                dtm.addRow(new Object[] {
                            dtm.getRowCount(),
                            "*",
                            FederationRequestType._ANY,
                            FederationSecurityAction.PROMPT
                          });              
                
                saveRules();
            }
        });
        commandsPanel1.add(defaultRules);
        
        add(commandsPanel1, BorderLayout.NORTH);
        
        JPanel commandsPanel2 = new JPanel(new IDEVerticalFlowLayout());        
        
        moveUp = new JButton("Move up");
        moveUp.setMnemonic('u');
        moveUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                DefaultTableModel dtm = 
                                (DefaultTableModel) ruleTable.getModel(); 
                int selectedRow = ruleTable.getSelectedRow();
                
                if (selectedRow == -1 || selectedRow == 0) return;
                
                dtm.moveRow(selectedRow, selectedRow, selectedRow-1);
                saveRules();
            }
        });
        commandsPanel2.add(moveUp);
        
        moveDown = new JButton("Move down");
        moveDown.setMnemonic('d');
        moveDown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                DefaultTableModel dtm = 
                                (DefaultTableModel) ruleTable.getModel(); 
                int selectedRow = ruleTable.getSelectedRow();
                
                if (selectedRow == -1 || selectedRow == dtm.getRowCount()-1) 
                    return;
                
                dtm.moveRow(selectedRow, selectedRow, selectedRow+1);
                saveRules();
            }
        });
        commandsPanel2.add(moveDown);
        
        add(commandsPanel2, BorderLayout.EAST);
        
        fsheild = FederationSecurityShield.getInstance();        
        
        ArrayList<FederationSecurityRule> rulesList = fsheild.getRuleList();
        
        rulesData = new Object[rulesList.size()][4];
        for(int i=0; i<rulesList.size(); i++) {
            rulesData[i][0] = i;
            rulesData[i][1] = fsheild.getRuleList().get(i).getHost();
            rulesData[i][2] = fsheild.getRuleList().get(i).getType();
            rulesData[i][3] = fsheild.getRuleList().get(i).getAction();
        } // end for
        
        ruleTable = new JTable(new DefaultTableModel(
                rulesData,
                new String [] {
                    "@", "Host", "Service Type", "Action"
                }
            ) {
            Class[] types = new Class [] {
                Integer.class,
                String.class, 
                FederationRequestType.class,
                FederationSecurityAction.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        ruleTable.setShowGrid(true);
        ruleTable.getColumn("@").setMaxWidth(20);
        ruleTable.sizeColumnsToFit(0);
        
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() { 
            @Override
            public void setValue(Object value) { 
                super.setValue(value); 
                
                saveRules();
 	    } 
        }; 
        
        ruleTable.getColumn("Host").setCellRenderer(cellRenderer);
        
        TableColumn serviceTypeColumn = ruleTable.getColumn("Service Type");
        JComboBox serviceType = new JComboBox(FederationRequestType.values());
        serviceTypeColumn.setCellEditor(new DefaultCellEditor(serviceType));
        serviceTypeColumn.setCellRenderer(cellRenderer);
                
        TableColumn actionTypeColumn = ruleTable.getColumn("Action");
        JComboBox actionType = new JComboBox(FederationSecurityAction.values());
        actionTypeColumn.setCellEditor(new DefaultCellEditor(actionType));        
        actionTypeColumn.setCellRenderer(cellRenderer);
        
        add(new JScrollPane(ruleTable), BorderLayout.CENTER);
    }
    
    /**
     * save the rules to rules storage
     */
    private void saveRules() {
        DefaultTableModel dtm = (DefaultTableModel) ruleTable.getModel();
        Vector data = dtm.getDataVector();
        ArrayList<FederationSecurityRule> ruleList 
                = new ArrayList<FederationSecurityRule>(data.size());
        
        for(int i=0; i<data.size(); i++) {
            Vector rule = (Vector) data.elementAt(i);
            
            ruleList.add(new FederationSecurityRule(
                                (String) rule.elementAt(1),
                                (FederationRequestType) rule.elementAt(2),
                                (FederationSecurityAction) rule.elementAt(3)));            
        } // end for
        
        fsheild.setRuleList(ruleList);
    }
}
