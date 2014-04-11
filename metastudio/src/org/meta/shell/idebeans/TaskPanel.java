/*
 * TaskPanel.java
 *
 * Created on March 25, 2004, 11:04 PM
 */

package org.meta.shell.idebeans;

import java.awt.*;
import java.util.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import org.meta.common.resource.FontResource;
import org.meta.common.resource.ImageResource;
import org.meta.shell.idebeans.chemnotes.IDEChemNote;

/**
 * A generic Task panel, with specialization for MeTA Studio.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class TaskPanel extends JPanel {
    
    /** Holds value of property taskList. The list of TaskGroup s. */
    private Vector<TaskGroup> taskList;
    
    /** the task tree ... */
    private JTree tasks;
    private DefaultMutableTreeNode taskTree;
    
    /** and chem note */
    private IDEChemNote chemNote;
    
    /** Creates a new instance of TaskPanel */
    public TaskPanel() {
        taskList = new Vector<TaskGroup>(5);                
    }
    
    /**
     * method to refresh the UI once all the tasks has been added,
     * or are deleted. This method is not automatically called, it 
     * needs to be called explicitely to display the correct UI.
     */
    public void refreshUI() {
        removeAll();
        
        setLayout(new BorderLayout());
        
        // make up the tasks tree
        tasks = new JTree();           
        
        Enumeration tList = taskList.elements();
        Object theTask;
        
        taskTree = new DefaultMutableTreeNode("MeTA Studio Tasks");
        
        while(tList.hasMoreElements()) {
            theTask = tList.nextElement();
            
            if (theTask instanceof TaskGroup) {
                if (((TaskGroup) theTask).isVisible()) {
                    DefaultMutableTreeNode theTaskGroup = 
                               new DefaultMutableTreeNode(theTask);
                
                    createNodes(theTaskGroup, (TaskGroup) theTask);
                    taskTree.add(theTaskGroup);
                } // end if
            } // end if ... else just igonore .. not worried abt. any thing else
        } // end while
                
        // finally add the tree
        tasks.setModel(new DefaultTreeModel(taskTree)); 
        tasks.setRootVisible(false);
        tasks.setShowsRootHandles(false);        
        tasks.putClientProperty("JTree.lineStyle", "None");        
        tasks.setCellRenderer(new TreeCellRenderer());
        tasks.setToggleClickCount(1);
        tasks.getSelectionModel()
                 .setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        for(int row=0; row<=tasks.getRowCount(); row++) {
            tasks.expandRow(row);
        } // end for
        tasks.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = 
                  (DefaultMutableTreeNode) tasks.getLastSelectedPathComponent();
                
                if (node.getUserObject() instanceof Task) {
                    if (!((Task) node.getUserObject()).isEnabled()) {
                        tasks.setSelectionRow(0);
                        return;
                    } // end if
                    
                    ActionEvent ae = new ActionEvent(node.getUserObject(), 1, 
                                                     node.toString());
                    
                    ((Task) node.getUserObject())
                                    .fireActionListenerActionPerformed(ae);
                } // end if
                
                tasks.setSelectionRow(0);
            }
        });        
        tasks.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        ToolTipManager.sharedInstance().registerComponent(tasks);
        add(new JScrollPane(tasks), BorderLayout.NORTH); 
        
        // TODO : this need to be refined and moved out from here
        if (chemNote == null)
            chemNote = new IDEChemNote();
        add(new JScrollPane(chemNote), BorderLayout.CENTER); 
        
        updateUI();
    }
    
    /**
     * Create the task tree.
     */
    private void createNodes(DefaultMutableTreeNode top, TaskGroup taskGroup) {
        DefaultMutableTreeNode category = null;
        DefaultMutableTreeNode theTask  = null;                
        
        // add the tasks
        Iterator theTasks = taskGroup.getTaskList().iterator();
        Object tsk;
        
        while(theTasks.hasNext()) {
          tsk = theTasks.next();
          
          if (tsk instanceof Task) {
            if (((Task) tsk).isVisible()) {
                theTask = new DefaultMutableTreeNode(tsk);
                top.add(theTask);
            } // end if
          } else if (tsk instanceof TaskGroup) {
            // we need to traverse further
            if (((TaskGroup) tsk).isVisible()) {
                category = new DefaultMutableTreeNode(tsk);
                createNodes(category, (TaskGroup) tsk);
                top.add(category);
            } // end if
          } // end if .. else ignore
        } // end while                
    }
    
    /**
     * Add a sub task group to this TaskGroup
     *
     * @param taskGroup - the task group to be added
     */
    public void add(TaskGroup taskGroup) {
        taskList.add(taskGroup);
    }        
    
    /** Getter for property taskList.
     * @return Value of property taskList.
     *
     */
    public Vector<TaskGroup> getTaskList() {
        return this.taskList;
    }
    
    /** Setter for property taskList.
     * @param taskList New value of property taskList.
     *
     */
    public void setTaskList(Vector<TaskGroup> taskList) {
        this.taskList = taskList;
    }
        
    /**
     * activate the first occurrence of the taskgroup described by
     * taskGroupDescription
     *
     * @param taskGroupDescription - the description of the task group
     */
    public void activate(String taskGroupDescription) {
        Iterator taskGroups = taskList.iterator();
        TaskGroup tg;
        
        while(taskGroups.hasNext()) {
            tg = (TaskGroup) taskGroups.next();
            
            if (tg.getDescription().equals(taskGroupDescription)) {
                tg.setVisible(true);
                refreshUI();
                break;
            } // end if
        } // end while
    }
    
    /**
     * deactivate the first occurrence of the taskgroup described by
     * taskGroupDescription
     *
     * @param taskGroupDescription - the description of the task group
     */
    public void deactivate(String taskGroupDescription) {
        Iterator taskGroups = taskList.iterator();
        TaskGroup tg;
        
        while(taskGroups.hasNext()) {
            tg = (TaskGroup) taskGroups.next();
            
            if (tg.getDescription().equals(taskGroupDescription)) {
                tg.setVisible(false);
                refreshUI();
                break;
            } // end if
        } // end while
    }
    
    /**
     * Inner class to represent cell rendering.
     */
    public class TreeCellRenderer extends DefaultTreeCellRenderer {
        public TreeCellRenderer() {
            ImageResource images = ImageResource.getInstance();
        
            setLeafIcon(null);
            setOpenIcon(images.getShrink());
            setClosedIcon(images.getExpand());            
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
            
            if (value instanceof TaskGroup) {
                setFont(FontResource.getInstance().getTaskGroupFont());
                setToolTipText(((TaskGroup) value).getToolTipText());
                setEnabled(((TaskGroup) value).isEnabled());
            } else if (value instanceof Task) {
                setFont(FontResource.getInstance().getTaskFont());
                setToolTipText(((Task) value).getToolTipText());
                setEnabled(((Task) value).isEnabled());
                setIcon(((Task) value).getIcon());
            } // end if
                        
            return this;
        }
    } // end of inner class TreeCellRenderer
} // end of class TaskPanel
