/*
 * FederationActivityInfoPanel.java 
 *
 * Created on 8 Oct, 2008 
 */

package org.meta.shell.idebeans;

import java.awt.BorderLayout;
import java.util.Date;
import java.util.HashMap;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.meta.net.FederationRequest;
import org.meta.net.FederationServiceStateRegistry;

/**
 * Info panel for remotely executing Federation scripts.
 * 
 * @author V. Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class FederationActivityInfoPanel extends JPanel {

    private JTable federationInfoTable;
    
    /** Creates a new instance of FederationActivityInfoPanel */
    public FederationActivityInfoPanel() {        
        initComponents();
    }

    /**
     * init the components of this panel
     */
    private void initComponents() {
        federationInfoTable = new JTable(new DefaultTableModel(
                new Object [][] { },
                new String [] {
                    "ID", "Service Type", "Remote IP", "Time"
                }
            ) {
            Class[] types = new Class [] {
                String.class, String.class, String.class, String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }

            @Override
            public Object getValueAt(int row, int column) {
                try {
                    return super.getValueAt(row, column);
                } catch(Exception e) {
                    return "";
                } // end of try .. catch block
            }                        
        });
        federationInfoTable.setShowGrid(false);  // do not show the grid
        
        // set column width
        federationInfoTable.getColumn("ID").setMaxWidth(20);
        federationInfoTable.sizeColumnsToFit(0);
        
        setLayout(new BorderLayout());
        add(new JScrollPane(federationInfoTable), BorderLayout.CENTER);
        
        startFederationInfoUpdateThread();
    }

    private boolean stopFederationInfoUpdateThread = false;
        
    private Thread federationInfoUpdateThread = null;  
    
    /** the update thread */
    private void startFederationInfoUpdateThread() {
        federationInfoUpdateThread = new Thread() {
            @Override
            public void run() {
                while(!stopFederationInfoUpdateThread) {          
                  synchronized(this) {
                    DefaultTableModel dtm = (DefaultTableModel)
                                                 federationInfoTable.getModel();
                    
                    try {
                      // clear the table
                      while(dtm.getRowCount() != 0) {
                        dtm.removeRow(0);
                      } // end while
                    
                      // and populate with new entries                         
                      FederationServiceStateRegistry reg 
                              = FederationServiceStateRegistry.getInstance();
                      HashMap<FederationRequest, Date> fedList 
                              = reg.getRegistryList();
                      
                      if (fedList == null || fedList.size() == 0) { 
                          try {
                            sleep(updateAfter*1000);
                          } catch (InterruptedException ignored) { }
                          
                          continue;
                      } // end if
                      
                      int i = 0;
                      for(FederationRequest fedReq : fedList.keySet()) {
                        try {
                          Object [] rowData = new Object[] {
                            (i++) + "",
                            fedReq.getType().toString(),
                            fedReq.getFederationConnection().getInetAddress()+"",
                            fedList.get(fedReq).toString()
                          };
                          
                          dtm.addRow(rowData);
                        } catch(Exception ignored) { continue; }
                      } // end for
                    } catch(Exception e) {
                      System.err.println("Error updating Federation info: " 
                                         + e.toString());  
                      e.printStackTrace();
                    } // end of try catch block
                    
                    try {
                      sleep(updateAfter*1000);
                    } catch (InterruptedException ignored) { }
                  } // end synchronized
                } // end while                
            }
         };
         
         federationInfoUpdateThread.setName("Federation info update Thread");
         federationInfoUpdateThread.setPriority(Thread.MIN_PRIORITY);
         federationInfoUpdateThread.start();
    }
    
    /** Overridden finalize method */
    @Override
    public void finalize() throws Throwable {
        super.finalize();
        
        stopFederationInfoUpdateThread = true;                
    }        
    
    /** Update after this many seconds */
    protected int updateAfter = 5; 

    /**
     * Get the value of updateAfter
     *
     * @return the value of updateAfter
     */
    public int getUpdateAfter() {
        return updateAfter;
    }

    /**
     * Set the value of updateAfter
     *
     * @param updateAfter new value of updateAfter
     */
    public void setUpdateAfter(int updateAfter) {
        this.updateAfter = updateAfter;
    }
}
