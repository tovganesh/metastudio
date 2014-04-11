/*
 * RDFJobNotificationPanel.java
 *
 * Created on November 28, 2004, 11:11 AM
 */

package org.meta.shell.idebeans;

import java.io.*;
import java.awt.*;
import java.net.*;
import java.util.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import javax.swing.border.*;
import org.meta.common.Utility;
import org.meta.common.resource.FontResource;
import org.meta.common.resource.ImageResource;
import org.meta.common.resource.MiscResource;
import org.meta.config.impl.RDFSubscriptionConfiguration;
import org.meta.config.impl.SearchURLsConfiguration;
import org.meta.config.impl.URLImplParameter;
import org.meta.parallel.SimpleAsyncTaskQueue;
import org.w3c.dom.*;

import org.meta.shell.ide.MeTA;

/**
 * Use RSS (Really Simple Syndication) subscription to get notification of 
 * Jobs running on remote grids or supercomputers which support 
 * such notification. <br>
 * Reference implementation of such a service is available at: <br>
 * <code> http://chem.unipune.ernet.in/py-cgi/search.py?q=listjobs </code>
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class RDFJobNotificationPanel extends JPanel {
    
    private JButton options, go, searchList;
    private JComboBox searchBox;
    private JLabel searchInfo;
    
    private JPanel searchPanel, notificationPanel; 
    private RDFPanel rdfNotificationPanel;
    private SearchResultPanel searchResultPanel;
    
    private JPanel rdfPanel;
    private JTabbedPane metaSchedulerPanel;
    private JTabbedPane tabbedPane;
    
    private LocalThreadInfoPanel localThreadInfoPanel;
    private FederationActivityInfoPanel federationScriptInfoPanel;
    
    private JPopupMenu urlSelectorPopup;
    private ButtonGroup urlButtonGroup;
    
    private String selectedSearchEngine;        
    
    private MeTA ideInstance;
    
    /** Creates a new instance of RDFJobNotificationPanel */
    public RDFJobNotificationPanel(MeTA ideInstance) {        
        initComponents();     
        
        this.ideInstance = ideInstance;
    }
    
    /**
     * initilise the components
     */
    private void initComponents() {   
        setLayout(new BorderLayout());
        
        tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
        
        rdfPanel = new JPanel();
        rdfPanel.setLayout(new BorderLayout());
        tabbedPane.addTab("RDF Notifications", rdfPanel);
        
        metaSchedulerPanel = new JTabbedPane();
        
            localThreadInfoPanel = new LocalThreadInfoPanel();
            metaSchedulerPanel.addTab("Local Threads", localThreadInfoPanel);
            
            federationScriptInfoPanel = new FederationActivityInfoPanel();
            metaSchedulerPanel.addTab("Federation Activity",
                                      federationScriptInfoPanel);
        
        tabbedPane.addTab("MeTA Studio Scheduler", metaSchedulerPanel);
        
        ImageResource images = ImageResource.getInstance();
        
        options = new JButton("RDF Notification Options", images.getOptions());
        options.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent ae)  {
               (new RDFJobNotificationOptions()).setVisible(true);
           }
        });
        rdfPanel.add(options, BorderLayout.NORTH);
        
        notificationPanel = new JPanel(new GridLayout(2, 1));
        
        rdfNotificationPanel = new RDFPanel();
        notificationPanel.add(rdfNotificationPanel);
        
        searchResultPanel = new SearchResultPanel();
        notificationPanel.add(searchResultPanel);
        searchResultPanel.setVisible(false); 
        
        rdfPanel.add(notificationPanel, BorderLayout.CENTER);
        
        searchPanel = new JPanel(new BorderLayout());
        
        JPanel searchBoxPanel = new JPanel(new BorderLayout());
        
        searchBox = new JComboBox();
        searchBox.setEditable(true);
        searchBox.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent ae)  {               
               if (ae.getActionCommand().equals("comboBoxEdited")) {
                  String newSearchString = (String) searchBox.getSelectedItem();
                  
                  if (((DefaultComboBoxModel) searchBox.getModel())
                                   .getIndexOf(newSearchString) < 0) {
                    searchBox.addItem(newSearchString);
                  } // end if
               } else if (ae.getActionCommand().equals("comboBoxChanged")) {
                  if ((searchBox.getEditor().getItem() == null)
                      || (searchBox.getEditor().getItem().equals(""))) {
                      return;
                  } // end if
                  
                  go.doClick();
               } // end if
           }
        });
        searchBoxPanel.add(searchBox, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new BorderLayout());        
        go = new JButton(images.getGo());
        go.setPreferredSize(new Dimension(images.getGo().getIconWidth(), 
                                          images.getGo().getIconHeight()));
        go.setBorder(BorderFactory.createEmptyBorder());
        go.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent ae)  {
               searchResultPanel.setVisible(true);
               
               String searchURL = "";
               
               try {
                searchURL = (String) SearchURLsConfiguration.getInstance()
                                       .getUrlList().get(selectedSearchEngine);
                
                searchResultPanel.setPageURL(searchURL
                                  + searchBox.getEditor().getItem().toString());
               } catch (Exception e) {
                   System.err.println("Error accessing search site : " 
                                      + e.toString());
                   e.printStackTrace();
                   internetError(searchURL);
               } // end of try .. catch block
           }
        });
        buttonPanel.add(go, BorderLayout.CENTER);
        searchList = new JButton("" + Utility.DOWN_ARROW_SYMBOL);
        searchList.setBorder(BorderFactory.createEmptyBorder());
        searchList.setToolTipText("Click to select from a list of search " +
                                  "compute servers");
        searchList.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent ae)  {
               urlSelectorPopup.show(searchList, 0, 0);
           }
        });
        buttonPanel.add(searchList, BorderLayout.EAST);
        
        searchBoxPanel.add(buttonPanel, BorderLayout.EAST);   
        searchPanel.add(searchBoxPanel, BorderLayout.NORTH);
        
        searchInfo = new JLabel("<html><head></head><body>Use search box " +
                          "above to find a list of feeds" +
                          " available from your compute server.</body></head>");
        searchPanel.add(searchInfo, BorderLayout.CENTER);
        
        rdfPanel.add(searchPanel, BorderLayout.SOUTH);
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // then make the url selector popup
        makeSearchURLSelectorPopup();
    }
    
    /**
     * Make a popup for selecting between search engines
     */
    private void makeSearchURLSelectorPopup() {
        urlButtonGroup = new ButtonGroup();
        urlSelectorPopup = new JPopupMenu("Search URL selector");
        
        HashMap urlList = SearchURLsConfiguration.getInstance().getUrlList();
        
        Iterator keys = urlList.keySet().iterator();
        String key;
        boolean first = false;
        
        while(keys.hasNext()) {
            key = (String) keys.next();
            
            JRadioButtonMenuItem searchURL = new JRadioButtonMenuItem(key);            
            searchURL.setToolTipText(key + " : " + urlList.get(key));
            if (!first) {
                searchURL.setSelected(true);
                selectedSearchEngine = key;
                first = true;
            } // end if
            searchURL.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent ae) {
                   selectedSearchEngine = ae.getActionCommand();
               }
            });
            urlButtonGroup.add(searchURL);
            
            urlSelectorPopup.add(searchURL);
        } // end while
    }
    
    /**
     * show a standard internet error message
     */
    private void internetError(String url) {
        JOptionPane.showMessageDialog(ideInstance, 
                    "<html><head></head><body>" +
                    "<strong>Unable to access : " + url + "</strong><br>" +
                    "Possible reasons could be:" +
                    "<ul><li>You are not connected to the internet.</li>" +
                    "<li>The site you are trying to access is unavailable" +
                    " at this moment.</li>" +
                    "<li>This client is behind a firewall or proxy.</li>" +
                    "</ul><br><i>To resolve one or more of these issues " +
                    "you may have to contact your site system administrator." +
                    "</i><br><b>Please see Runtime Log for further details." +
                    "</b></body></html>",
                    "Unable to access internet!",
                    JOptionPane.ERROR_MESSAGE);            
    }
    
    /**
     * The RDF notification panel
     */
    private class RDFPanel extends JPanel {
        
        /** the rdf tree */
        private JTree rdfTree;
        
        private ArrayList<String> rdfURLList;
        
        private Thread updateThread;
        
        private boolean stopUpdate;                
    
        private JPopupMenu refreshPopup;
        private JMenuItem refreshRDF;
        
        /** Create new instance of RDFPanel */
        public RDFPanel() { 
            rdfURLList = new ArrayList<String>(RDFSubscriptionConfiguration
                                       .getInstance().getUrlList().values());
            initComponents();
            
            stopUpdate = false;
            
            updateThread = new Thread() {
                @Override
                public void run() {
                    while(!stopUpdate) {
                        try {
                            // sleep till next update
                            sleep(RDFSubscriptionConfiguration
                                  .getInstance().getRefreshRate() * 60 * 1000);
                        } catch (InterruptedException ignored) { }
                    
                        if (stopUpdate) return;
                        
                        // TODO: better way of update?
                        initComponents();
                    } // end while
                }
            };
            
            updateThread.setPriority(Thread.MIN_PRIORITY);
            updateThread.setName("RDF Update Thread");
            updateThread.start();
        }
        
        private boolean doRemoveElements = false;
        
        /**
         * initilise the components
         */
        private synchronized void initComponents() {            
            setLayout(new BorderLayout());
            
            doRemoveElements = (rdfTree != null);
            
            Thread treeMaker = new Thread() {
                @Override
                public synchronized void run() {
                    DefaultMutableTreeNode rdfs = new DefaultMutableTreeNode(
                                                      "RDF Job Notifications");
                    // reconstruct local list
                    rdfURLList = 
                          new ArrayList<String>(RDFSubscriptionConfiguration
                                        .getInstance().getUrlList().values());
                    
                    Iterator rdfURL = rdfURLList.iterator();
                    
                    // add the RDF feeds
                    while(rdfURL.hasNext()) {
                        try {
                            createNodes(rdfs, new URL((String) rdfURL.next()));
                        } catch (Exception e) {
                            System.err.println(e.toString());
                            e.printStackTrace();
                        } // end of try .. catch block
                    } // end while
                    
                    rdfTree = new JTree();
                    rdfTree.setCellRenderer(new TreeCellRenderer());
                    rdfTree.setModel(new DefaultTreeModel(rdfs));
                    // enable tool tips
                    ToolTipManager.sharedInstance().registerComponent(rdfTree);
                    
                    rdfTree.addTreeSelectionListener(
                      new TreeSelectionListener() {
                        @Override
                        public void valueChanged(TreeSelectionEvent e) {
                            final DefaultMutableTreeNode node =
                                     (DefaultMutableTreeNode) rdfTree
                                                .getLastSelectedPathComponent();
                            
                            if (node.getUserObject() instanceof String) {
                                if (node.getUserObject()
                                        .equals("View latest geometry")) {

                                    Thread asyncObject = new Thread() {
                                      @Override
                                      public void run() {
                                        try {
                                            Utility.executeBeanShellScript(
                                              "viewMoleculeFromGAMXML(\"" +
                                                 ((RDFJobNodeModal)
                                                   ((DefaultMutableTreeNode) 
                                                     node.getParent()).getUserObject())
                                                   .getXmlURL()
                                                  + "\")"
                                            );
                                        } catch (Exception ex) {
                                         System.err.println("Error executing " +
                                                    "script : " + ex.toString());
                                         ex.printStackTrace();
                                         JOptionPane.showMessageDialog(
                                                  ideInstance, 
                                                  "Error viewing molecule.\n" +
                                                  "See Runtime log for details",
                                                  "Error",
                                                  JOptionPane.ERROR_MESSAGE);
                                        } // end of try .. catch block
                                      }
                                    };

                                    asyncObject.setPriority(Thread.MIN_PRIORITY);
                                    asyncObject.start();
                                } else if (node.getUserObject()
                                        .equals("View all geometries")) {
                                    Thread asyncObject = new Thread() {
                                      @Override
                                      public void run() {
                                       try {
                                        Utility.executeBeanShellScript(
                                         "viewMoleculeFromGAMXML(\"" +
                                         ((RDFJobNodeModal)
                                           ((DefaultMutableTreeNode) 
                                             node.getParent()).getUserObject())
                                          .getXmlURL()
                                         + "\",\"true\")"
                                        );
                                       } catch (Exception ex) {
                                        System.err.println("Error executing " +
                                                   "script : " + ex.toString());
                                        ex.printStackTrace();
                                        JOptionPane.showMessageDialog(
                                                  ideInstance, 
                                                  "Error viewing molecule.\n" +
                                                  "See Runtime log for details",
                                                  "Error",
                                                  JOptionPane.ERROR_MESSAGE);
                                       } // end of try .. catch block
                                      }
                                    };
                                    
                                    asyncObject.setPriority(Thread.MIN_PRIORITY);
                                    asyncObject.start();
                                } else if (node.getUserObject()
                                        .equals("Save latest geometry")) {
                                    // The following is purposely not handled
                                    // in a background thread.
                                    // TODO: this might need to change
                                    try {
                                        Utility.executeBeanShellScript(
                                         "saveMoleculeFromGAMXML(\"" +
                                         ((RDFJobNodeModal)
                                           ((DefaultMutableTreeNode) 
                                             node.getParent()).getUserObject())
                                          .getXmlURL()
                                         + "\")"
                                        );
                                    } catch (Exception ex) {
                                        System.err.println("Error executing " +
                                                   "script : " + ex.toString());
                                        ex.printStackTrace();
                                        JOptionPane.showMessageDialog(
                                                  ideInstance,
                                                  "Error save molecule.\n" +
                                                  "See Runtime log for details",
                                                  "Error",
                                                  JOptionPane.ERROR_MESSAGE);
                                    } // end of try .. catch block
                                } // end if
                            } // end if
                            
                            rdfTree.setSelectionRow(0);
                        }
                    });
                    
                    // add popup support
                    rdfTree.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mousePressed(MouseEvent me) {
                            if (me.isPopupTrigger()) {
                                showPopup(me);
                                me.consume();
                            } // end if
                        }
                        
                        @Override
                        public void mouseReleased(MouseEvent me) {
                            if (me.isPopupTrigger()) {
                                showPopup(me);
                                me.consume();
                            } // end if
                        }
                        
                        /**
                         * method to show the popup
                         */
                        public void showPopup(MouseEvent me) {
                            refreshPopup.show(rdfTree, me.getX(), me.getY());
                        }
                    }); // addMouseListener
                
                    if (doRemoveElements) removeAll();

                    add(new JScrollPane(rdfTree), BorderLayout.CENTER);
                    
                    updateUI();
                }
            }; // end of thread treeMaker
            
            treeMaker.start();
            treeMaker.setName("RDF Tree maker Thread");
            makePopups();
        }
        
        /**
         * make the popups
         */
        private void makePopups() {
            refreshPopup = new JPopupMenu("Refresh RDFs");
            
            refreshRDF = new JMenuItem("Refresh Subscriptions");
            refreshRDF.setMnemonic('R');
            refreshRDF.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent ae) {
                   // TODO: better way of update?
                   initComponents();
               }
            });
            
            refreshPopup.add(refreshRDF);
        }
        
        /**
         * Create rdf feeds
         */
        private void createNodes(DefaultMutableTreeNode top, URL rdfURL) {
            DefaultMutableTreeNode rdfCategory = null;             
            
            // add the notifications 
            try {                
                Document rdfDoc = Utility.parseXML(rdfURL.openStream());
                
                NodeList rdfChannel = rdfDoc.getElementsByTagName("channel")
                                            .item(0).getChildNodes();
                int noOfItems = rdfDoc.getElementsByTagName("item").getLength();
                
                RDFJobNodeModal jobNode = new RDFJobNodeModal();
                jobNode.setNoOfItems(noOfItems);
                jobNode.setRdfURL(rdfURL.toExternalForm());
                
                for(int rdfIdx=0; rdfIdx<rdfChannel.getLength(); rdfIdx++) {
                    Node rdfNode = rdfChannel.item(rdfIdx);
                    
                    if (rdfNode.getNodeName().equals("title")) {
                        jobNode.setTitle(rdfNode.getChildNodes()
                                         .item(0).getNodeValue());
                    } else if (rdfNode.getNodeName().equals("description")) {
                        jobNode.setDescription(rdfNode.getChildNodes()
                                               .item(0).getNodeValue());
                    } else if (rdfNode.getNodeName().equals("pubDate")) {
                        jobNode.setPublicationDate(rdfNode.getChildNodes()
                                                   .item(0).getNodeValue());
                    } // end if
                } // end for
                
                rdfCategory = new DefaultMutableTreeNode(jobNode);
                
                // first add two special items
                // view last geometry and save last geometry
                rdfCategory.add(new DefaultMutableTreeNode(
                                                  "View latest geometry"));
                rdfCategory.add(new DefaultMutableTreeNode(
                                                  "View all geometries"));
                rdfCategory.add(new DefaultMutableTreeNode(
                                                  "Save latest geometry"));
                
                // then update other items
                NodeList rdfItems = rdfDoc.getElementsByTagName("item");
                
                for(int rdfIdx=0; rdfIdx<rdfItems.getLength(); rdfIdx++) {
                    NodeList rdfItem = rdfItems.item(rdfIdx).getChildNodes();
                    
                    RDFItemNodeModal theRDFItem = new RDFItemNodeModal();
                    
                    for(int itmLen=0; itmLen<rdfItem.getLength(); itmLen++) {
                       Node rdfProp = rdfItem.item(itmLen); 
                       
                       if (rdfProp.getNodeName().equals("title")) {
                           theRDFItem.setTitle(rdfProp.getChildNodes()
                                                .item(0).getNodeValue());
                       } else if (rdfProp.getNodeName().equals("description")) {
                           theRDFItem.setDescription(rdfProp.getChildNodes()
                                                      .item(0).getNodeValue());
                       } else if (rdfProp.getNodeName().equals("link")) {
                           theRDFItem.setHtmlURL(rdfProp.getChildNodes()
                                                  .item(0).getNodeValue());
                       } else if (rdfProp.getNodeName().equals("pubDate")) {
                           theRDFItem.setPublicationDate(
                                rdfProp.getChildNodes().item(0).getNodeValue());
                       } // end if
                    } // end for
                    
                    // add to tree
                    rdfCategory.add(new DefaultMutableTreeNode(theRDFItem));
                } // end for
                
                top.add(rdfCategory);
            } catch (Exception e) {
                System.err.println("Unable to update RDF : " + e.toString());
                e.printStackTrace();
            } // end of try .. catch block                        
        }                
        
        /**
         * Add a RDF URL
         *
         * @param rdfURL - the URL object
         */
        public void addRDF(URL rdfURL) {            
            RDFSubscriptionConfiguration.getInstance().setParameter(
                                rdfURL.getFile(), new URLImplParameter(rdfURL));
            
            rdfURLList = new ArrayList<String>(RDFSubscriptionConfiguration
                                       .getInstance().getUrlList().values());
            
            // TODO: change this to a better way of updating
            initComponents();
            updateUI();
        }
        
        /**
         * Add a RDF URL
         *
         * @param rdfURL - a string representing RDF URL
         */
        public void addRDF(String rdfURL) throws MalformedURLException {
            addRDF(new URL(rdfURL));
        }
        
        /**
         * Remove a named RDF entry
         */
        public void removeNamedRDF(String rdfName) {
            RDFSubscriptionConfiguration.getInstance()
                                        .setParameter(rdfName, null);
            
            // TODO: change this to a better way of updating
            initComponents();
            updateUI();
        }
        
        /**
         * overridden finalize() 
         */
        @Override
        protected void finalize() {
            stopUpdate = true;
            updateThread.interrupt();            
        }
        
        /**
         * inner class to represent the RDF item node modal
         */
         private class RDFItemNodeModal {
             
             /**
              * Holds value of property title.
              */
             private String title;
             
             /**
              * Holds value of property htmlURL.
              */
             private String htmlURL;
             
             /**
              * Holds value of property xmlURL.
              */
             private String xmlURL;
             
             /**
              * Holds value of property publicationDate.
              */
             private String publicationDate;
             
             /**
              * Holds value of property description.
              */
             private String description;
             
             /** Creates a new instance of RDFItemNodeModal */
             public RDFItemNodeModal() { }
             
             /**
              * Getter for property title.
              * @return Value of property title.
              */
             public String getTitle() {
                 return this.title;
             }
             
             /**
              * Setter for property title.
              * @param title New value of property title.
              */
             public void setTitle(String title) {
                 this.title = title;
             }
             
             /**
              * Getter for property htmlURL.
              * @return Value of property htmlURL.
              */
             public String getHtmlURL() {
                 return this.htmlURL;
             }
             
             /**
              * Setter for property htmlURL.
              * @param htmlURL New value of property htmlURL.
              */
             public void setHtmlURL(String htmlURL) {
                 this.htmlURL = htmlURL;
                 this.xmlURL = htmlURL.substring(0, htmlURL.lastIndexOf('.'))
                               + ".xml";
             }
             
             /**
              * Getter for property xmlURL.
              * @return Value of property xmlURL.
              */
             public String getXmlURL() {
                 return this.xmlURL;
             }
             
             /**
              * Getter for property publicationDate.
              * @return Value of property publicationDate.
              */
             public String getPublicationDate() {
                 return this.publicationDate;
             }
             
             /**
              * Setter for property publicationDate.
              * @param publicationDate New value of property publicationDate.
              */
             public void setPublicationDate(String publicationDate) {
                 this.publicationDate = publicationDate;
             }
             
             /**
              * overriddern toString() method
              */
             @Override
             public String toString() {
                 return getTitle();
             }
             
             /**
              * overriddern toExtendedString() method
              */
             public String toExtendedString() {
                 StringBuffer sb = new StringBuffer();
                 
                 sb.append("<html><head></head><body>");
                 sb.append("<strong>");
                 sb.append(getTitle());
                 sb.append("</strong> <i>");
                 sb.append(getPublicationDate());
                 sb.append("</i>");
                 sb.append("<hr><h4>");
                 sb.append(getDescription());
                 sb.append("</h4></body></html>");
                 
                 return sb.toString();
             }             
             
             /**
              * Getter for property description.
              * @return Value of property description.
              */
             public String getDescription() {
                 return this.description;
             }
             
             /**
              * Setter for property description.
              * @param description New value of property description.
              */
             public void setDescription(String description) {
                 this.description = description;
             }
             
         } // end of inner class RDFItemNodeModal
         
        /**
         * inner class to represent the RDF Job node modal
         */
         private class RDFJobNodeModal {
             
             /**
              * Holds value of property title.
              */
             private String title;
             
             /**
              * Holds value of property description.
              */
             private String description;
             
             /**
              * Holds value of property publicationDate.
              */
             private String publicationDate;
             
             /**
              * Holds value of property noOfItems.
              */
             private int noOfItems;
             
             /**
              * Holds value of property rdfURL.
              */
             private String rdfURL;
             
             /**
              * Holds value of property xmlURL.
              */
             private String xmlURL;
             
             /**
              * Holds value of property htmlURL.
              */
             private String htmlURL;
             
             /** Creates a new instance of RDFJobNodeModal */
             public RDFJobNodeModal() { }
             
             /**
              * Getter for property title.
              * @return Value of property title.
              */
             public String getTitle() {
                 return this.title;
             }
             
             /**
              * Setter for property title.
              * @param title New value of property title.
              */
             public void setTitle(String title) {
                 this.title = title;
             }
             
             /**
              * Getter for property description.
              * @return Value of property description.
              */
             public String getDescription() {
                 return this.description;
             }
             
             /**
              * Setter for property description.
              * @param description New value of property description.
              */
             public void setDescription(String description) {
                 this.description = description;
             }
             
             /**
              * Getter for property publicationDate.
              * @return Value of property publicationDate.
              */
             public String getPublicationDate() {
                 return this.publicationDate;
             }
             
             /**
              * Setter for property publicationDate.
              * @param publicationDate New value of property publicationDate.
              */
             public void setPublicationDate(String publicationDate) {
                 this.publicationDate = publicationDate;
             }
             
             /**
              * overriddern toString() method
              */
             @Override
             public String toString() {
                 StringBuffer sb = new StringBuffer();
                 
                 sb.append("<html><head></head><body>");
                 sb.append("<strong>");
                 sb.append(getTitle());
                 sb.append("</strong> <i>");
                 sb.append(getPublicationDate());
                 sb.append("</i> <b>");
                 sb.append(" (" + noOfItems + " items) </b>");
                 sb.append("</body></html>");
                 
                 return sb.toString();
             }             
             
             /**
              * overriddern toExtendedString() method
              */
             public String toExtendedString() {
                 StringBuffer sb = new StringBuffer();
                 
                 sb.append("<html><head></head><body>");
                 sb.append("<strong>");
                 sb.append(getTitle());
                 sb.append("</strong> <i>");
                 sb.append(getPublicationDate());
                 sb.append("</i> <b>");
                 sb.append(" (" + noOfItems + " items) </b><hr><h4>");
                 sb.append(getDescription());
                 sb.append("</h4></body></html>");
                 
                 return sb.toString();
             }             
             
             /**
              * Getter for property noOfItems.
              * @return Value of property noOfItems.
              */
             public int getNoOfItems() {
                 return this.noOfItems;
             }
             
             /**
              * Setter for property noOfItems.
              * @param noOfItems New value of property noOfItems.
              */
             public void setNoOfItems(int noOfItems) {
                 this.noOfItems = noOfItems;
             }             
             
             /**
              * Getter for property rdfURL.
              * @return Value of property rdfURL.
              */
             public String getRdfURL() {
                 return this.rdfURL;
             }
             
             /**
              * Setter for property rdfURL.
              * @param rdfURL New value of property rdfURL.
              */
             public void setRdfURL(String rdfURL) {
                 this.rdfURL  = rdfURL;
                 this.xmlURL  = rdfURL.substring(0, rdfURL.lastIndexOf('.'))
                                + ".xml";
                 this.htmlURL = rdfURL.substring(0, rdfURL.lastIndexOf('.'))
                                + ".html";
             }
             
             /**
              * Getter for property xmlURL.
              * @return Value of property xmlURL.
              */
             public String getXmlURL() {
                 return this.xmlURL;
             }
             
             /**
              * Getter for property htmlURL.
              * @return Value of property htmlURL.
              */
             public String getHtmlURL() {
                 return this.htmlURL;
             }
             
         } // end of inner class RDFJobNodeModal
         
         /**
          * Inner class to represent cell rendering.
          */
         private class TreeCellRenderer extends DefaultTreeCellRenderer {
             private ImageIcon nodeIcon;
             
             public TreeCellRenderer() {
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
                 
                 ImageResource images = ImageResource.getInstance();
                 
                 if (value instanceof String) {
                     if (value.equals("RDF Job Notifications")) {
                         setIcon(images.getRss());
                         setToolTipText("RDF Job Notifications");
                     } else if (value.equals("View latest geometry")) {
                         setIcon(images.getMoleculeViewer());
                         setToolTipText("View latest geometry from the XML " +
                                        "file if available");
                     } else if (value.equals("View all geometries")) {
                         setIcon(images.getMoleculeViewer());
                         setToolTipText("View all geometry from the XML " +
                                        "file if available");
                     } else if (value.equals("Save latest geometry")) {
                         setIcon(images.getSaveWorkspace());
                         setToolTipText("Save latest geometry from the XML " +
                                        "file if available");
                     } // end if 
                 } else if (value instanceof RDFJobNodeModal) {
                     setToolTipText(
                               ((RDFJobNodeModal) value).toExtendedString());
                     setIcon(images.getJobUpdate());
                 } else if (value instanceof RDFItemNodeModal) {
                     setToolTipText(
                               ((RDFItemNodeModal) value).toExtendedString());
                     setIcon(images.getJobItem());
                 } // end if
                 
                 return this;
             }
         } // end of inner class TreeCellRenderer
    } // end of class RDFPanel
    
    /**
     * The search result viewing panel
     */
    private class SearchResultPanel extends JPanel {
        
        private JLabel searchResultInfo;
        private JButton hide;
        
        private JEditorPane searchResults;
        
        public static final String HTML_TYPE = "html";        
        public static final String RDF_TYPE  = "rdf";
        public static final String XML_TYPE  = "xml";
        
        /** Create new instance for SearchResultPanel */
        public SearchResultPanel() {
            initComponents();
        }
        
        /**
         * initilise the components
         */
        private void initComponents() {
            setLayout(new BorderLayout());
            
            ImageResource images = ImageResource.getInstance();
            
            JPanel title = new JPanel(new BorderLayout());
            
            searchResultInfo = new JLabel("Serch Results:", JLabel.LEFT);
            title.add(searchResultInfo, BorderLayout.CENTER);
            title.setBorder(
                     BorderFactory.createBevelBorder(BevelBorder.RAISED));
            
            hide = new JButton(images.getCloseWindow());            
            hide.setPreferredSize(new Dimension(
                                  images.getCloseWindow().getIconWidth(), 
                                  images.getCloseWindow().getIconHeight()));
            hide.setBorder(BorderFactory.createEmptyBorder());
            hide.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae)  {
                    setVisible(false);
                }
            });
            title.add(hide, BorderLayout.EAST);
            
            add(title, BorderLayout.NORTH);
            
            searchResults = new JEditorPane();            
            searchResults.setEditable(false);
            searchResults.setContentType("text/html");            
            searchResults.addHyperlinkListener(new HyperlinkListener() {
                @Override
                public void hyperlinkUpdate(HyperlinkEvent hle) {
                    try {
                        if (hle.getEventType() 
                            == HyperlinkEvent.EventType.ACTIVATED) {
                            handleURL(hle.getURL());
                        } // end if
                    } catch (Exception e) {
                        System.err.println("Unable to handle URL event : "
                                           + e.toString());
                        internetError(hle.getURL().toExternalForm());
                    } // end of try .. catch block
                }
            });
            
            add(new JScrollPane(searchResults), BorderLayout.CENTER);
            
            // then make the "find tool"
            add(new FindTextPanel(searchResults, true, false, ">", "<"),
                BorderLayout.NORTH);
        }
        
        /**
         * handle the URL parsing
         *
         * @param pageURL URL object
         */
        public void handleURL(URL pageURL) throws IOException {
            if (pageURL.toExternalForm().endsWith(HTML_TYPE)) {
                setPageURL(pageURL);
            } else if (pageURL.toExternalForm().endsWith(RDF_TYPE)) {
                int option = JOptionPane.showConfirmDialog(null,
                 "<html><head></head><body>"
                 + "Do you want to subscribe to the following RDF URL?:<br>"
                 + "<strong>" + pageURL.toString() + "</strong>",
                 "Add a new RDF notification?",
                 JOptionPane.YES_NO_OPTION);
                
                if (option == JOptionPane.YES_OPTION) {
                    rdfNotificationPanel.addRDF(pageURL);
                } // end if
            } else {
                JOptionPane.showMessageDialog(null, 
                                       "No viewer available for this type!",
                                       "Unable to handle content!", 
                                       JOptionPane.WARNING_MESSAGE);
            } // end if
        }
        
        /**
         * set the current search page URL
         *
         * @param pageURL URL as a string
         */
        public void setPageURL(String pageURL) throws IOException {
            searchResults.setPage(pageURL);            
        }
        
        /**
         * set the current search page URL
         *
         * @param pageURL URL object
         */
        public void setPageURL(URL pageURL) throws IOException {
            searchResults.setPage(pageURL);                     
        }
    } // end of class SearchResultPanel
    
    /**
     * Popup menu for handling RDFs
     */
    public class RDFJobNotificationPopup extends JPopupMenu {
        
        /**
         * Holds value of property sourceRDFItem.
         */
        private Object sourceRDFItem;
        
        /** creates a new instance of RDFJobNotificationPopup */
        public RDFJobNotificationPopup() {
            super();
        }
        
        /** Creates new instance of RDFJobNotificationPopup
         *
         * @param label - the lable for this popup menu
         */
        public RDFJobNotificationPopup(String label) {
            super(label);
        }     
        
        /**
         * Getter for property sourceRDFItem.
         * @return Value of property sourceRDFItem.
         */
        public Object getSourceRDFItem() {
            return this.sourceRDFItem;
        }        
        
        /**
         * Setter for property sourceRDFItem.
         * @param sourceRDFItem New value of property sourceRDFItem.
         */
        public void setSourceRDFItem(Object sourceRDFItem) {
            this.sourceRDFItem = sourceRDFItem;
        }
        
    } // end of inner class RDFJobNotificationPopup
    
    /**
     * Class to provide an interface to change RDF notification parameters
     */
    public class RDFJobNotificationOptions extends JDialog {
        
        private JTabbedPane tabbedPane;
        
        private JPanel searchURLPanel, rdfURLPanel;
        
        private JList searchURLList, rdfURLList;
        
        private JButton addSearchURL, removeSearchURL, editSearchURL;
        private JButton addRdfURL, removeRdfURL, editRdfURL;
        
        private JSpinner refreshRate;
        
        /** Creates a new instance of RDFJobNotificationOptions */
        public RDFJobNotificationOptions() { 
            super(ideInstance);
                        
            setTitle("RDF Job Notification Options");
            setModal(true);
            
            initComponents();
            setSize(MiscResource.getInstance()
                                .getRdfOptionsDialogSize());
            setLocationRelativeTo(ideInstance);                        
        }
        
        /**
         * init the dialog components
         */
        private void initComponents() {
            getContentPane().setLayout(new BorderLayout());
            
            tabbedPane = new JTabbedPane(JTabbedPane.TOP);
            
            makeSearchURLPanel();
            makeRdfURLPanel();
            
            tabbedPane.addTab("Customize search URLs", searchURLPanel);
            tabbedPane.addTab("Customize RDF subscriptions", rdfURLPanel);
            
            getContentPane().add(tabbedPane, BorderLayout.CENTER);
            
            // add a ESC button listener
            // and add the accelerator
            ActionListener actionCancel = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    setVisible(false);
                    dispose();
                }
            };
            
            KeyStroke keyStrokeCancel = KeyStroke.getKeyStroke(
                                                  KeyEvent.VK_ESCAPE, 0);
            tabbedPane.registerKeyboardAction(actionCancel, "cancel",
                                             keyStrokeCancel,
                                             JComponent.WHEN_IN_FOCUSED_WINDOW);
        }
        
        /**
         * search URL panel
         */
        private void makeSearchURLPanel() {                        
            searchURLPanel = new JPanel(new BorderLayout());
                      
            JLabel infoLabel = new JLabel("Add, remove, edit search URLs " +
                                      "from the following list: ", JLabel.LEFT);
            
            infoLabel.setFont(FontResource.getInstance().getDescriptionFont());
            searchURLPanel.add(infoLabel, BorderLayout.NORTH);                 
            
            searchURLList = new JList(SearchURLsConfiguration.getInstance()
                                      .getUrlList().keySet().toArray());
            searchURLList.setBorder(BorderFactory.createEtchedBorder());
            searchURLPanel.add(searchURLList, BorderLayout.CENTER);
            
            JPanel spacerPanel = new JPanel(new BorderLayout());
            JPanel buttonPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill   = GridBagConstraints.BOTH;
            gbc.anchor = GridBagConstraints.NORTH;           
            
            addSearchURL = new JButton("Add URL...");
            addSearchURL.setMnemonic('A');
            addSearchURL.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae)  {
                    SearchURLsUI searchURLUI = new SearchURLsUI();
                    int option = searchURLUI.showTheDialog();
                    
                    if (option == JOptionPane.OK_OPTION) {
                        try {
                            SearchURLsConfiguration.getInstance()
                                  .setParameter(searchURLUI.getUrlName(), 
                                                new URLImplParameter(
                                                new URL(searchURLUI.getUrl())));
                            updateSearchURLList();
                        } catch (Exception e) {
                            System.out.println("Unable to add search URL : "
                                                + e.toString());
                            e.printStackTrace();
                        } // end of try .. catch block
                    } // end if
                }
            });    
            gbc.gridx = 0;
            gbc.gridy = 0;
            buttonPanel.add(addSearchURL, gbc);
                                           
            removeSearchURL = new JButton("Remove URL");
            removeSearchURL.setMnemonic('R');
            removeSearchURL.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae)  {
                    if (searchURLList.getSelectedIndex() < 0) return;
                    
                    String srchName = (String) searchURLList.getSelectedValue();
                    String srchURL  = (String) SearchURLsConfiguration
                                         .getInstance().getUrlList()
                                         .get(searchURLList.getSelectedValue());
                    
                    int option = JOptionPane.showConfirmDialog(ideInstance,
                    "<html><head></head><body>"
                    + "Do you want remove the following search URL?:"
                    + "<br><strong>" + srchURL + "(" + srchName + ")</strong>",
                    "Remove a search URL?",
                    JOptionPane.YES_NO_OPTION);
                    
                    if (option == JOptionPane.YES_OPTION) {                        
                        SearchURLsConfiguration.getInstance()
                                               .setParameter(srchName, null);
                        updateSearchURLList();                        
                    } // end if
                }
            });     
            gbc.gridx = 0;
            gbc.gridy = 1;
            buttonPanel.add(removeSearchURL, gbc);
            
            editSearchURL = new JButton("Edit URL...");
            editSearchURL.setMnemonic('E');
            editSearchURL.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae)  {
                    if (searchURLList.getSelectedIndex() < 0) return;
                    
                    SearchURLsUI searchURLUI = new SearchURLsUI();
                    
                    searchURLUI.setUrlName(
                                   searchURLList.getSelectedValue().toString());
                    searchURLUI.setUrl((String) SearchURLsConfiguration
                                        .getInstance().getUrlList()
                                        .get(searchURLList.getSelectedValue()));
                    
                    int option = searchURLUI.showTheDialog();
                    
                    if (option == JOptionPane.OK_OPTION) {
                        try {
                            SearchURLsConfiguration.getInstance()
                                  .setParameter(searchURLUI.getUrlName(), 
                                                new URLImplParameter(
                                                new URL(searchURLUI.getUrl())));
                            updateSearchURLList();
                        } catch (Exception e) {
                            System.out.println("Unable to add search URL : "
                                                + e.toString());
                            e.printStackTrace();
                        } // end of try .. catch block
                    } // end if
                }
            });
            gbc.gridx = 0;
            gbc.gridy = 2;
            buttonPanel.add(editSearchURL, gbc);                        
            
            spacerPanel.add(buttonPanel, BorderLayout.NORTH);
            searchURLPanel.add(spacerPanel, BorderLayout.EAST);
        }
        
        /**
         * RDF URL panel
         */
        private void makeRdfURLPanel() {
            rdfURLPanel = new JPanel(new BorderLayout());
            
            JLabel infoLabel = new JLabel("Add, remove, edit RDF " +
                    "subscription URLs from the following list: ", JLabel.LEFT);
            
            infoLabel.setFont(FontResource.getInstance().getDescriptionFont());
            rdfURLPanel.add(infoLabel, BorderLayout.NORTH);                 
            
            rdfURLList = new JList(RDFSubscriptionConfiguration.getInstance()
                                      .getUrlList().keySet().toArray());
            rdfURLList.setBorder(BorderFactory.createEtchedBorder());
            rdfURLPanel.add(rdfURLList, BorderLayout.CENTER);
            
            JPanel spacerPanel = new JPanel(new BorderLayout());
            JPanel buttonPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill   = GridBagConstraints.BOTH;
            gbc.anchor = GridBagConstraints.NORTH;
            
            addRdfURL = new JButton("Add URL...");
            addRdfURL.setMnemonic('A');
            addRdfURL.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae)  {
                    String rdfURL = JOptionPane.showInputDialog(ideInstance, 
                           "Enter a RDF URL to which you want to subscribe: ");
                    
                    try {
                        rdfNotificationPanel.addRDF(rdfURL);
                        updateRdfList();
                    } catch (Exception e) {
                        System.out.println("Unable to add RDF URL : " 
                                           + e.toString());
                        e.printStackTrace();
                    } // end of try .. catch block
                }
            });
            gbc.gridx = 0;
            gbc.gridy = 0;
            buttonPanel.add(addRdfURL, gbc);
            
            removeRdfURL = new JButton("Remove URL");
            removeRdfURL.setMnemonic('R');
            removeRdfURL.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae)  {
                    if (rdfURLList.getSelectedIndex() < 0) return;
                    
                    String rdfName = (String) rdfURLList.getSelectedValue();
                    String rdfURL  = (String) RDFSubscriptionConfiguration
                                            .getInstance().getUrlList()
                                            .get(rdfURLList.getSelectedValue());
                    
                    int option = JOptionPane.showConfirmDialog(ideInstance,
                    "<html><head></head><body>"
                    + "Do you want remove subscribtion to the following RDF "
                    + "URL?: <br><strong>" + rdfURL + "(" + rdfName 
                    + ")</strong>",
                    "Remove a RDF notification?",
                    JOptionPane.YES_NO_OPTION);
                    
                    if (option == JOptionPane.YES_OPTION) {
                        rdfNotificationPanel.removeNamedRDF(rdfName);
                        updateRdfList();
                    } // end if
                }
            });
            gbc.gridx = 0;
            gbc.gridy = 1;
            buttonPanel.add(removeRdfURL, gbc);
            
            editRdfURL = new JButton("Edit URL...");
            editRdfURL.setMnemonic('E');
            editRdfURL.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae)  {
                    if (rdfURLList.getSelectedIndex() < 0) return;
                    
                    String rdfURL = JOptionPane.showInputDialog(ideInstance, 
                           "Edit the RDF URL to which you want to subscribe: ", 
                           RDFSubscriptionConfiguration
                                           .getInstance().getUrlList()
                                           .get(rdfURLList.getSelectedValue()));
                    
                    try {
                        rdfNotificationPanel.addRDF(rdfURL);
                        updateRdfList();
                    } catch (Exception e) {
                        System.out.println("Unable to edit RDF URL : " 
                                           + e.toString());
                        e.printStackTrace();
                    } // end of try .. catch block
                }
            });
            gbc.gridx = 0;
            gbc.gridy = 2;
            buttonPanel.add(editRdfURL, gbc);
            
            spacerPanel.add(buttonPanel, BorderLayout.NORTH);
            rdfURLPanel.add(spacerPanel, BorderLayout.EAST);
            
            JPanel refreshPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            
            JLabel refreshInfoLabel = new JLabel("Refresh the subscription" +
                                             " RDF URLs every : ", JLabel.LEFT);
            refreshPanel.add(refreshInfoLabel);

            refreshRate = new JSpinner(new SpinnerNumberModel(
                   RDFSubscriptionConfiguration.getInstance().getRefreshRate(), 
                   1, 9999, 1));
            refreshRate.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent ce) {
                   RDFSubscriptionConfiguration.getInstance().setRefreshRate(
                      ((Integer) refreshRate.getValue()).intValue()
                   );
                }
            });
            refreshPanel.add(refreshRate);
            
            JLabel refreshUnits = new JLabel(" minutes.", JLabel.LEFT);
            refreshPanel.add(refreshUnits);
            
            rdfURLPanel.add(refreshPanel, BorderLayout.SOUTH);
        }
        
        /**
         * update RDF list
         */
        private void updateRdfList() {
            rdfURLList.setListData(RDFSubscriptionConfiguration.getInstance()
                                      .getUrlList().keySet().toArray());
        }
        
        /**
         * update search URL list
         */
        private void updateSearchURLList() {
            searchURLList.setListData(SearchURLsConfiguration.getInstance()
                                      .getUrlList().keySet().toArray());
            
            // remake the popup menu
            makeSearchURLSelectorPopup();
        }
        
        /**
         * Inner class to represent UI for search URLs
         */
        private class SearchURLsUI extends JDialog {
            
            JLabel infoURLName, infoURL;
            JTextField urlName, url;
            
            private JButton ok, cancel;
            
            private int returnValue;
            
            /** Creats a new instance of SearchURLsUI */
            public SearchURLsUI() { 
                super(ideInstance);
                
                initComponents();
                                
                pack();
                setModal(true);
                setLocationRelativeTo(ideInstance);
                
                setTitle("Add / Modify search URL");
            }
            
            /**
             * init the UI components
             */
            private void initComponents() {
                Container contentPane = getContentPane();
                
                contentPane.setLayout(new GridBagLayout());
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.fill = GridBagConstraints.BOTH;
                gbc.anchor = GridBagConstraints.NORTH;
                gbc.insets = new Insets(5, 2, 5, 2);
                
                infoURLName = new JLabel("Specify a friendly URL name like" +
                                         " \"chem grid\" etc.", JLabel.LEFT);
                gbc.gridx = 0;
                gbc.gridy = 0;
                contentPane.add(infoURLName, gbc);
                
                gbc.gridx = 0;
                gbc.gridy = 1;
                urlName = new JTextField();
                contentPane.add(urlName, gbc);
                
                infoURL = new JLabel("Specify a search URL like" +
                    " http://chem.unipune.ernet.in/py-cgi/search.py?q= etc.", 
                    JLabel.LEFT);                
                gbc.gridx = 0;
                gbc.gridy = 2;
                contentPane.add(infoURL, gbc);
                
                gbc.gridx = 0;
                gbc.gridy = 3;
                url = new JTextField();
                contentPane.add(url, gbc);
                
                JPanel buttonPane = new JPanel(new GridBagLayout());
                
                ok = new JButton("Ok");
                ok.setMnemonic('O');
                ok.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ae)  {
                        returnValue = JOptionPane.OK_OPTION;
                        setVisible(false);
                        dispose();
                    }
                });                
                gbc.gridx = 0;
                gbc.gridy = 0;
                buttonPane.add(ok, gbc);
                
                ActionListener actionCancel = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        returnValue = JOptionPane.CANCEL_OPTION;
                        setVisible(false);
                        dispose();
                    }
                };
                
                cancel = new JButton("Cancel");
                cancel.setMnemonic('l');
                cancel.addActionListener(actionCancel);                
                gbc.gridx = 1;
                gbc.gridy = 0;
                buttonPane.add(cancel, gbc);
                
                KeyStroke keyStrokeCancel = KeyStroke.getKeyStroke(
                                                         KeyEvent.VK_ESCAPE, 0);
                buttonPane.registerKeyboardAction(actionCancel, "cancel",
                                             keyStrokeCancel,
                                             JComponent.WHEN_IN_FOCUSED_WINDOW);
                
                gbc.gridx = 0;
                gbc.gridy = 4;
                contentPane.add(buttonPane, gbc);
            }
            
            /**
             * Show this dialog
             *
             * @return JOptionPane.OK_OPTION or JOptionPane.CANCEL_OPTION
             */ 
            public int showTheDialog() {
                setModal(true);
                setVisible(true);
                
                return returnValue;
            }
            
            /**
             * Getter for property urlName.
             * @return Value of property urlName.
             */
            public String getUrlName() {
                return urlName.getText();
            }
            
            /**
             * Setter for property urlName.
             * @param urlName New value of property urlName.
             */
            public void setUrlName(String urlName) {
                this.urlName.setText(urlName);
            }
            
            /**
             * Getter for property url.
             * @return Value of property url.
             */
            public String getUrl() {
                return url.getText();
            }
            
            /**
             * Setter for property url.
             * @param url New value of property url.
             */
            public void setUrl(String url) {
                this.url.setText(url);
            }
            
        } // end of class SearchURLsUI
    } // end of inner class RDFJobNotificationOptions

} // end of class RDFJobNotificationPanel
