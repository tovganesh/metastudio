/*
 * IDEWidget.java
 *
 * Created on February 19, 2007, 10:29 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.meta.shell.idebeans;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.meta.common.resource.ImageResource;

/**
 * A simple IDEWidget holder, can be used to construct other widgets.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class IDEWidget extends JPanel {
    
    private JPanel widgetPlaceHolder;
    private JPanel widgetHeader;
            
    private JButton close;
    
    private JLabel widgetTitle;
    
    /** Creates a new instance of IDEWidget */
    public IDEWidget() {
        this("Undefined Widget");
    }

    /** Creates a new instance of IDEWidget */
    public IDEWidget(String widgetID) {
        this.widgetID = widgetID;
        
        widgetPlaceHolder = new JPanel();
        
        setBorder(BorderFactory.createLineBorder(Color.black));
        
        super.setLayout(new BorderLayout(0, 0));
        super.add(widgetPlaceHolder, BorderLayout.CENTER);      
        
        widgetHeader = new JPanel(new BorderLayout(5, 0)){ 
 	    Insets insets = new Insets(0, 0, 0, 0); 
            @Override
 	    public Insets getInsets() { 
 		return insets; 
 	    }            
 	};         
        widgetHeader.setBorder(BorderFactory.createLineBorder(Color.lightGray));
        
        JPanel widgetButton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        
            ImageResource images = ImageResource.getInstance();                        

            close = new JButton(images.getCloseWindow());
            close.setToolTipText("Close");
            close.setForeground(Color.red);
            close.setBorder(BorderFactory.createEmptyBorder());
            close.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    IDEWidget.this.setVisible(false);
                    
                    if (widgetsPanel != null) {
                        widgetsPanel.removeWidget(IDEWidget.this);
                    } // end if
                    
                    updateUI();
                }                
            });
            widgetButton.add(close);
        
        widgetHeader.add(widgetButton, BorderLayout.EAST);
        widgetTitle = new JLabel(widgetID, JLabel.LEFT);
        widgetHeader.add(widgetTitle, BorderLayout.CENTER);
            
        super.add(widgetHeader, BorderLayout.NORTH);
    }           
        
    /**
     * Holds value of property widgetID.
     */
    private String widgetID;

    /**
     * Getter for property widgetID.
     * @return Value of property widgetID.
     */
    public String getWidgetID() {
        return this.widgetID;
    }

    /**
     * Setter for property widgetID.
     * @param widgetID New value of property widgetID.
     */
    public void setWidgetID(String widgetID) {
        this.widgetID = widgetID;
        
        widgetTitle.setText(widgetID);
    }
    
    /**
     * Checking equality of two widget objects
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        
        if (!(obj instanceof IDEWidget)) return false;
        
        IDEWidget w = (IDEWidget) obj;
        
        return w.widgetID.equals(widgetID);
    }

    /**
     * 
     * Appends the specified component to the end of this container. 
     * This is a convenience method for {@link #addImpl}.
     * <p>
     * Note: If a component has been added to a container that
     * has been displayed, <code>validate</code> must be
     * called on that container to display the new component.
     * If multiple components are being added, you can improve
     * efficiency by calling <code>validate</code> only once,
     * after all the components have been added.
     * 
     * @param comp   the component to be added
     * @return the component argument
     * @exception NullPointerException if {@code comp} is {@code null}
     * @see #addImpl
     * @see #validate
     * @see javax.swing.JComponent#revalidate()
     */
    @Override
    public Component add(Component comp) {
        return widgetPlaceHolder.add(comp);
    }   

    /**
     * Adds the specified component to this container with the specified
     * constraints at the specified index.  Also notifies the layout 
     * manager to add the component to the this container's layout using 
     * the specified constraints object.
     * This is a convenience method for {@link #addImpl}.
     * <p>
     * Note: If a component has been added to a container that
     * has been displayed, <code>validate</code> must be
     * called on that container to display the new component.
     * If multiple components are being added, you can improve
     * efficiency by calling <code>validate</code> only once,
     * after all the components have been added.
     * 
     * @param comp the component to be added
     * @param constraints an object expressing layout contraints for this
     * @param index the position in the container's list at which to insert
     * the component; <code>-1</code> means insert at the end
     * component
     * @exception NullPointerException if {@code comp} is {@code null}
     * @exception IllegalArgumentException if {@code index} is invalid (see
     *            {@link #addImpl} for details)
     * @see #addImpl
     * @see #validate
     * @see javax.swing.JComponent#revalidate()
     * @see #remove
     * @see LayoutManager
     */
    @Override
    public void add(Component comp, Object constraints, int index) {
        widgetPlaceHolder.add(comp, constraints, index);
    }

    /**
     * 
     * Adds the specified component to this container at the given 
     * position. 
     * This is a convenience method for {@link #addImpl}.
     * <p>
     * Note: If a component has been added to a container that
     * has been displayed, <code>validate</code> must be
     * called on that container to display the new component.
     * If multiple components are being added, you can improve
     * efficiency by calling <code>validate</code> only once,
     * after all the components have been added.
     * 
     * @param comp   the component to be added
     * @param index    the position at which to insert the component, 
     *                   or <code>-1</code> to append the component to the end
     * @return the component <code>comp</code>
     * @exception NullPointerException if {@code comp} is {@code null}
     * @exception IllegalArgumentException if {@code index} is invalid (see
     *            {@link #addImpl} for details)
     * @see #addImpl
     * @see #remove
     * @see #validate
     * @see javax.swing.JComponent#revalidate()
     */
    @Override
    public Component add(Component comp, int index) {
        return widgetPlaceHolder.add(comp, index);
    }

    /**
     * 
     * Sets the layout manager for this container.
     * 
     * @param mgr the specified layout manager
     * @see #doLayout
     * @see #getLayout
     */
    @Override
    public void setLayout(LayoutManager mgr) {
        if (widgetPlaceHolder != null)
            widgetPlaceHolder.setLayout(mgr);
        else
            super.setLayout(mgr);
    } 

    /**
     * Adds the specified component to the end of this container.
     * Also notifies the layout manager to add the component to 
     * this container's layout using the specified constraints object.
     * This is a convenience method for {@link #addImpl}.
     * <p>
     * Note: If a component has been added to a container that
     * has been displayed, <code>validate</code> must be
     * called on that container to display the new component.
     * If multiple components are being added, you can improve
     * efficiency by calling <code>validate</code> only once,
     * after all the components have been added.
     * 
     * @param comp the component to be added
     * @param constraints an object expressing 
     *                  layout contraints for this component
     * @exception NullPointerException if {@code comp} is {@code null}
     * @see #addImpl
     * @see #validate
     * @see javax.swing.JComponent#revalidate()
     * @see LayoutManager
     * @since JDK1.1
     */
    @Override
    public void add(Component comp, Object constraints) {
        widgetPlaceHolder.add(comp, constraints);
    }

    /**
     * Adds the specified component to this container.
     * This is a convenience method for {@link #addImpl}.
     * <p>
     * This method is obsolete as of 1.1.  Please use the
     * method <code>add(Component, Object)</code> instead.
     * 
     * @exception NullPointerException if {@code comp} is {@code null}
     * @see #add(Component, Object)
     */
    @Override
    public Component add(String name, Component comp) {
        return widgetPlaceHolder.add(name, comp);
    }
   
    private IDEWidgetsPanel widgetsPanel;
    
    /**
     * Set the new value of widgetsPanel
     * 
     * @param widgetsPanel the panel on which this object is embedded
     */
    public void setWidgetsPanel(IDEWidgetsPanel widgetsPanel) {
        this.widgetsPanel = widgetsPanel;
    }
    
    /**
     * Get the value of widgetsPanel
     * 
     * @return the panel on which this object is embedded
     */
    public IDEWidgetsPanel getWidgetsPanel() {
        return this.widgetsPanel;
    }
    
    /**
     * overridden toString() method
     * 
     * @return a string!
     */
    @Override
    public String toString() {
        return widgetID;
    }
} // end of class IDEWidget
