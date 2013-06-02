/*
 * LocalThreadInfoPanel.java 
 *
 * Created on 8 Oct, 2008 
 */

package org.meta.shell.idebeans;

import java.awt.BorderLayout;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.text.DecimalFormat;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Info panel for locally running threads connected with this instance of
 * MeTA Studio.
 * 
 * @author V. Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class LocalThreadInfoPanel extends JPanel {

    private static final double TIME_FACTOR = 10e-9;
    
    private JTable threadInfoTable;
    
    /** Creates instance of LocalThreadInfoPanel */
    public LocalThreadInfoPanel() {        
        initComponents();
    }

    /**
     * init the interface
     */
    private void initComponents() {
        threadInfoTable = new JTable(new DefaultTableModel(
                new Object [][] { },
                new String [] {
                    "ID", "Thread Name", "CPU Time (sec.)", "Suspended"
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
        threadInfoTable.setShowGrid(false);  // do not show the grid
        
        // set column width
        threadInfoTable.getColumn("ID").setMaxWidth(20);
        threadInfoTable.sizeColumnsToFit(0);
        
        setLayout(new BorderLayout());
        add(new JScrollPane(threadInfoTable), BorderLayout.CENTER);
        
        startThreadInfoUpdateThread();
    }

    private boolean stopThreadInfoUpdateThread = false;
        
    private Thread threadInfoUpdateThread = null;        
            
    /**
     * Update the thread info...
     */
    private void startThreadInfoUpdateThread() {
         threadInfoUpdateThread = new Thread() {
            @Override
            public void run() {
                while(!stopThreadInfoUpdateThread) {          
                  synchronized(this) {
                    DefaultTableModel dtm = (DefaultTableModel)
                                                    threadInfoTable.getModel();
                    
                    try {
                      // clear the table
                      while(dtm.getRowCount() != 0) {
                        dtm.removeRow(0);
                      } // end while
                    
                      // and populate with new entries                         
                      ThreadMXBean tBean = ManagementFactory.getThreadMXBean();                   
                      long [] threadIDs  = tBean.getAllThreadIds();    
                      DecimalFormat tmFrmt = new DecimalFormat("#.#");                    
                      for(long threadID : threadIDs) {
                        try {
                          Object [] rowData = new Object[] {
                            threadID + "",
                            tBean.getThreadInfo(threadID).getThreadName(),
                            tmFrmt.format(
                               tBean.getThreadCpuTime(threadID) * TIME_FACTOR),
                            tBean.getThreadInfo(threadID).isSuspended() + ""
                          };
                          
                          dtm.addRow(rowData);
                        } catch(Exception ignored) { continue; }
                      } // end for
                    } catch(Exception e) {
                      System.err.println("Error updating thread info: " 
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
         
         threadInfoUpdateThread.setName("Thread info update Thread!");
         threadInfoUpdateThread.setPriority(Thread.MIN_PRIORITY);
         threadInfoUpdateThread.start();
    }

    /** Overridden finalize method */
    @Override
    public void finalize() throws Throwable {
        super.finalize();
        
        stopThreadInfoUpdateThread = true;                
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
