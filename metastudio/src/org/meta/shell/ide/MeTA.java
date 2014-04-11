/*
 * MeTA.java
 *
 * Created on June 15, 2003, 11:31 AM
 */

package org.meta.shell.ide;

import bsh.Interpreter;
import java.io.*;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.awt.datatransfer.*;

import javax.swing.*;
import javax.net.ssl.SSLSocket;
import org.meta.common.Utility;
import org.meta.common.resource.FontResource;
import org.meta.common.resource.ImageResource;
import org.meta.common.resource.MiscResource;
import org.meta.common.resource.StringResource;
import org.meta.molecule.Molecule;
import org.meta.net.FederationRequestHandler;
import org.meta.net.FederationRequestHandlerUINotifier;
import org.meta.net.FederationRequestType;
import org.meta.net.FederationService;
import org.meta.net.FederationServiceDiscovery;
import org.meta.net.event.FederationDiscoveryEvent;
import org.meta.net.event.FederationDiscoveryListener;
import org.meta.net.impl.service.FederationRequestBroadcastHandler;
import org.meta.net.impl.service.FederationRequestObjectPushHandler;
import org.meta.net.impl.service.talk.MoleculeTalkObject;
import org.meta.net.security.FederationSecurityShield;
import org.meta.net.security.FederationSecurityShieldPromptHandler;
import org.meta.shell.idebeans.BeanShellScriptEngineGUI;
import org.meta.shell.idebeans.Explorer;
import org.meta.shell.idebeans.IDEWorkflowBar;
import org.meta.shell.idebeans.LogWindow;
import org.meta.shell.idebeans.NotificationTray;
import org.meta.shell.idebeans.SplashScreen;
import org.meta.shell.idebeans.StatusBar;
import org.meta.shell.idebeans.WorkspaceDesktopPane;
import org.meta.shell.idebeans.eventhandlers.MainMenuEventHandlers;
import org.meta.workspace.Workspace;
import org.meta.workspace.event.WorkspaceChangeEvent;
import org.meta.workspace.event.WorkspaceChangeListener;

/**
 * The main class of the new MeTA Studio v2.0, enjoy the flexibility!
 * All GUI code is purely hand written, I do not use any form builder tool.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class MeTA extends JFrame implements WorkspaceChangeListener {
    
    /** Creates new form MeTA */
    public MeTA() { 
        this(false); 
    }
    
    /** Creates new form MeTA
     * @param isDaemon true if this MeTA instance needs to run as daemon */
    public MeTA(boolean isDaemon) {                
        this.daemon = isDaemon;  
        initUI();                
    }    
    
    /**
     * initialize the UI
     */
    public void initUI() {
        // in deamon mode? then do not start the GUI IDE
        if (this.daemon) {
            // init the serivices
            if (initServices()) {
                System.out.println(StringResource.getInstance().getVersion()
                                   + " running in daemon mode...");
                System.out.print("Loading external libs...");
                loadExternalLibs();
                System.out.println(" Done.");
            } else {
                System.out.println(StringResource.getInstance().getVersion()
                        + " could not start in daemon mode... exiting!");
            } // end if
            
            return;
        } // end if
         
        setTitle(StringResource.getInstance().getVersion());
        
        // set the UI for the IDE
        try {
           UIManager.setLookAndFeel(StringResource.getInstance().getUiClass());
        } catch (Exception ignored) {} // don't worry much about UI
        
        // instantiate FontResource so that some default settings are
        // done ... the fonts matter a lot
        FontResource.getInstance();        
        
        // show the splash screen
        splashScreen = new SplashScreen();
        splashScreen.setVisible(true);        
        
        // initilise the UI elements
        initComponents();
        
        splashScreen.setProgressBarString("Opening " 
                                   + StringResource.getInstance().getVersion());
        
        // set the frame icon
        setIconImage(ImageResource.getInstance().getIcon().getImage());
        
        // and then dispose of the splash screen
        splashScreen.dispose();
                
        // show the main IDE
        setVisible(true);
        
        // set current state of frame to be maximized
        setSize(MiscResource.getInstance().getMainWindowDimension());        
    
        // enable drop even in the IDE
        enableDropInIDE();
        
        // init the serivices
        workspaceLog.appendInfo("Starting federation discovery and providers...");
        initServices();
            
        // make log entries
        workspaceLog.appendInfo("Done");        
        
        // try to maximize the window
        setExtendedState(JFrame.MAXIMIZED_BOTH);         
        
        // and then create the system tray
        initSystemTray();
        
        // and load the external libraries
        System.out.print("Loading external libs...");
        loadExternalLibs();
        System.out.println(" Done.");

        // and initiate the BeanShell so that startup widgets can be loaded
        System.out.print("Loading widgets and plugins...");
        startupScriptEngine = new BeanShellScriptEngineGUI();
        startupScriptEngine.getShellUI();
        System.out.println(" Done.");

        // set devider sizes
        mainSplitPane.setDividerLocation(0.0);
        workspaceSplitPane.setDividerLocation(1.0);

        // force a repaint!
        getContentPane().invalidate();
        repaint();

        // set devider sizes  .. again
        mainSplitPane.setResizeWeight(1.0);
        workspaceSplitPane.setResizeWeight(1.0);
        mainSplitPane.setDividerLocation(0.0);
        workspaceSplitPane.setDividerLocation(1.0);
    }
    
    /**
     * init federation discovery / providers
     * 
     * @return true if success, else false
     */
    private boolean initServices() {
        // start federation discovery service
        FederationServiceDiscovery d = FederationServiceDiscovery.getInstance();
        
        // add listeners to discovery service
        d.addFederationDiscoveryListener(new FederationDiscoveryListener() {
            @Override
            public void federated(FederationDiscoveryEvent fde) {
                if (!FederationServiceDiscovery.getInstance().isShowLogMessages())
                    return;
                
                String msg = fde.getDiscoveryType() + ": " + fde.getMessage();
                
                System.out.println(msg);
                
                if (isDaemon()) return;
                
                if (fde.getDiscoveryType() 
                    == FederationDiscoveryEvent.DiscoveryType.SUCCESS) {
                    getNotificationTray().notify("Discovery Event", msg, true);
                } // end if
            }
        });
        
        // start local federation service provider
        try {
            FederationService fs = FederationService.getInstance();

            fs.addUINotifier(new FederationRequestHandlerUINotifier() {
                @Override
                public void requestReceived(FederationRequestHandler sourceHandler) {
                   // DO Nothing
                }

                @Override
                public void requestProcessed(FederationRequestHandler sourceHandler) {
                    // See if we have an object push
                    if (sourceHandler instanceof FederationRequestObjectPushHandler) {
                        // then get the object
                        Serializable obj = ((FederationRequestObjectPushHandler)
                                               sourceHandler).getPushedObject();
                        
                        // then see if the object is of type MoleculeTalkObject
                        if (obj instanceof MoleculeTalkObject) {
                            Molecule mol = (Molecule) ((MoleculeTalkObject) obj)
                                                       .getTalkObjectContent();

                            try {
                                Interpreter intpr
                                      = Utility.createNewBeanShellInterpreter();
                                intpr.set("__mol__", mol);
                                intpr.eval("showMolecule(__mol__);");
                            } catch(Exception e) {
                                System.err.println("Unable to show molecule object!");
                                e.printStackTrace();
                            } // end of try .. catch blocl
                        } // end if
                    } else if (sourceHandler instanceof FederationRequestBroadcastHandler) { 
                        String msgReceived = ((FederationRequestBroadcastHandler) 
                                               sourceHandler).getMsgReceived();
                        System.out.println("Broadcast message: " + msgReceived); 
                        getNotificationTray().notify("Broadcast message",
                                                     msgReceived, true);
                    } // end if
                }
            });
        } catch (Exception e) {
            System.err.println("Federation service couldn't be started: " + e);
            e.printStackTrace();
            
            return false;
        } // end of try .. catch block                
        
        // Next start up the federation sercurity shield, and define the prompt
        // handler. If in deamon mode, the prompt handler always returns false.
        FederationSecurityShield fss = FederationSecurityShield.getInstance();
        fss.setPromptHandler(new FederationSecurityShieldPromptHandler() {
           @Override
           public boolean promptRequest(FederationRequestType requestType, 
                                         SSLSocket federatingClient) {
               
               if (isDaemon()) return false;
               
               String msg = ("Do you want " 
                         + federatingClient.getInetAddress().getHostAddress()
                         + " to access " + requestType.toString() + " service?");
                    
               showSystemTrayMessage("Federation Security",
                         federatingClient.getInetAddress().getHostAddress()
                         + " needs your permission to access " 
                         + requestType.toString() + " service.",
                         TrayIcon.MessageType.INFO);
               
               int opt = JOptionPane.showConfirmDialog(MeTA.this, msg,
                         "Federation security permission required", 
                         JOptionPane.YES_NO_OPTION);               
               
               return (JOptionPane.YES_OPTION == opt);
           }
        });
                
        // if control reaches here, probably we have done initing the services
        return true;
    }
    
    /**
     * sets the visibility of explorer pane
     *
     * @param visible - the visibility flag
     */
    public void setExplorerPaneVisibility(boolean visible) {
        if (visible) {
            mainSplitPane.setDividerLocation(0.26);
        } else {
            mainSplitPane.setDividerLocation(0.0);
        } // end if
    }
    
    /**
     * sets the visibility of log pane
     *
     * @param visible - the visibility flag
     */
    public void setLogPaneVisibility(boolean visible) {
        if (visible) {
            workspaceSplitPane.setDividerLocation(0.80);
        } else {
            workspaceSplitPane.setDividerLocation(1.0);
        } // end if
    }
    
    /** This method is called from within the constructor to
     * initialize the UI. 
     */
    private void initComponents() {
        getContentPane().setFont(FontResource.getInstance().getFrameFont());
        
        initDesktop(); // initilize the desktop pane

        // additional properties and events
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                exitForm(evt);
            }
        });       
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        // and the tool bar
        splashScreen.setProgressBarString("Initilizing workflow bar...");
        workflowBar = new IDEWorkflowBar(this);
        getContentPane().add(workflowBar, BorderLayout.NORTH);
        
        // and then init the status bar
        splashScreen.setProgressBarString("Initilizing status bar...");
        statusBar = new StatusBar();
        notificationTray = new NotificationTray(this);
        statusBar.addToNotificationPanel(notificationTray);
        getContentPane().add(statusBar, BorderLayout.SOUTH);
        
        // ensure a proper instance of MainMenuEventHandlers is created
        splashScreen.setProgressBarString("Initilizing event handlers...");
        MainMenuEventHandlers.getInstance(this);
    }
    
    /**
     * initialize the desktop UI.
     */
    private void initDesktop() { 
        // construct worspace pane
        splashScreen.setProgressBarString("Initilizing workspace...");
        workspacePanel   = new JPanel(new BorderLayout());
        workspaceDesktop = new WorkspaceDesktopPane();     
        workspacePanel.add(workspaceDesktop, BorderLayout.CENTER);
        
        workspaceLogPanel = new JPanel(new BorderLayout());
        
        // add the log window
        splashScreen.setProgressBarString("Initilizing workspace log...");
        workspaceLog = new LogWindow();
        workspaceLogPanel.add(workspaceLog, BorderLayout.CENTER);     
        workspaceLog.setVisible(true);
        
        workspaceSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                                            workspacePanel, workspaceLogPanel); 
        workspaceSplitPane.setOneTouchExpandable(true);
        
        // and then the main pane
        workspaceExplorerPanel = new JPanel(new BorderLayout());
        
        // add the workspace explorer 
        splashScreen.setProgressBarString("Initilizing workspace explorer...");
        workspaceExplorer = new Explorer(this);      
        workspaceExplorerPanel.add(workspaceExplorer, BorderLayout.CENTER);    
        workspaceExplorer.setVisible(true);
        
        splashScreen.setProgressBarString("Arranging split pane...");
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                       workspaceExplorerPanel, 
                                       workspaceSplitPane);       
        mainSplitPane.setOneTouchExpandable(true);
        
        // the desktop pane
        splashScreen.setProgressBarString("Initilizing desktop...");
        desktopPane = new JDesktopPane();
        
        desktopPane.setLayout(new BorderLayout());
        desktopPane.add(mainSplitPane, BorderLayout.CENTER);
        
        getContentPane().add(desktopPane, BorderLayout.CENTER);
    }        
        
    /**
     * initialize the system tray
     */
    private void initSystemTray() {
        if (SystemTray.isSupported()) {
            // get the SystemTray instance
            SystemTray tray = SystemTray.getSystemTray();
            
            // load an image
            ImageResource images = ImageResource.getInstance();
            
            // construct a TrayIcon
            trayIcon = new TrayIcon(images.getIcon().getImage(), 
                               StringResource.getInstance().getVersion());
            trayIcon.setImageAutoSize(true);
            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    MeTA.this.setVisible(!MeTA.this.isVisible());
                }                
            });
            
            // add the tray image
            try {
                tray.add(trayIcon);                
            } catch (AWTException e) {
                System.err.println(e);
            } // end of try .. catch block
        } // end if
    }
    
    /**
     * Show a message in tray icon.
     * 
     * @param caption the caption to be shown
     * @param text the message to be displayed
     * @param messageType the type of this message
     */
    public void showSystemTrayMessage(String caption, String text,
                                      TrayIcon.MessageType messageType) {
        if (trayIcon != null) {
            trayIcon.displayMessage(caption, text, messageType);
        }
    }
    
    /**
     * save the current workspace if required, close it, update UI.
     *
     * @return int - JOptionPane.CANCEL_OPTION if user canceled the operation
     *         else  JOptionPane.NO_OPTION
     */
    private int saveAndCloseCurrentWorkspace() {
        if (this.currentWorkspace != null) {
            try {
                if (this.currentWorkspace.isDirty()) {
                    int option = JOptionPane.showConfirmDialog(this,
                    "Do you want to save changes in the workspace ["
                    + this.currentWorkspace.getInternalName() + "] ?",
                    "Save changes?", JOptionPane.YES_NO_CANCEL_OPTION);
                    
                    switch(option) {
                        case JOptionPane.CANCEL_OPTION:
                            return JOptionPane.CANCEL_OPTION;
                        case JOptionPane.YES_OPTION:
                            this.currentWorkspace.save();
                            break;
                        default:
                            break;
                    }
                } // end if
                this.currentWorkspace.close();
                this.currentWorkspace.removeWorkspaceChangeListener(this);
                // and update the UI .. to be on safer side
                workspaceDesktop.closeAllWorkspaceFrames();

                return JOptionPane.NO_OPTION;
            } catch (Exception e) { // if not successful bail out!!!
                // should never happen
                System.err.println("ERROR: Workspace save operation failed!");
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                            "ERROR: Workspace save operation failed! \n"
                            + StringResource.getInstance().getStdErrorMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                return JOptionPane.NO_OPTION;
            } // end try .. catch ... finally block
        } else {
            return JOptionPane.NO_OPTION;
        } // end if    
    }
    
    /** shut down the IDE */    
    public void exitIDE(int code) { 
        if (saveAndCloseCurrentWorkspace() == JOptionPane.CANCEL_OPTION) return;

        // bye .. bye
        System.exit(code);
    }
    
    /** Exit the Application */
    private void exitForm(WindowEvent evt) {
        exitIDE(0);
    }
    
    /** enable drag 'n' drop in MeTA Studio */
    public void enableDropInIDE() {
        new DropTarget(MeTA.this, 
            new DropTargetAdapter() { 
                @Override
                public void drop(DropTargetDropEvent d) {
                    try {
                        // first check if, what we are dropping is of file type
                        int i = 0;
                                                
                        if (!d.getCurrentDataFlavorsAsList().get(i)
                                .isFlavorJavaFileListType()) {
                          // we have a difficult task here, need to fish
                          // out the correct stuff
                          d.acceptDrop(DnDConstants.ACTION_COPY);  
                          
                          for(DataFlavor df : 
                                d.getCurrentDataFlavorsAsList()) {
                              i++;                                                            
                            if (df.isMimeTypeEqual("text/plain")
                                && df.isFlavorTextType()
                                && df.getRepresentationClass ()
                                    .isAssignableFrom(java.lang.String.class)
                               ) break;
                          } // end for
                          
                          Object dropData = (d.getTransferable().getTransferData(
                             d.getCurrentDataFlavorsAsList().get(i)));
                          
                          String filNm = "";
                          
                          if (dropData instanceof StringReader) {
                              StringReader sr = (StringReader) dropData;
                              
                              char [] str = new char[1024];
                              
                              while(true) {
                                  int retVal = sr.read(str);
                                  
                                  if (retVal == -1) break;
                                  
                                  filNm += new String(str);
                              }
                              
                              sr.close();
                          } else {                          
                            filNm = (d.getTransferable().getTransferData(
                              d.getCurrentDataFlavorsAsList().get(i))).toString();
                          
                            filNm = filNm.substring(filNm.indexOf(':')+1, 
                                                    filNm.length()); 
                          }                         
                          
                          String [] fileNames = filNm.split("\n");
                          
                          for(String fileName : fileNames) {
                            shellOpenFile(fileName.trim());
                          }
                        } else {
                          // else then prepare to accept the drop
                          d.acceptDrop(DnDConstants.ACTION_COPY);
                          java.util.List files = (java.util.List)
                                    (d.getTransferable().getTransferData(
                                       d.getCurrentDataFlavorsAsList().get(0)));
                        
                          String fileName;
                          for(Object file : files.toArray()) {
                            // we only support molecule file opening
                            fileName = ((File) file).getAbsolutePath();
                            shellOpenFile(fileName);
                          } // end for
                        } // end for
                    } catch (Exception e) {
                        System.err.println("Drop operation failed! Reason: "
                                           + e.toString());
                        e.printStackTrace();
                    }
                }
            }
        );
    }
    
    /** 
     * Open the file thrown from the underlying OS UI.
     *
     * @param fileName the file name to be opened
     * @throws Exception
     */
    private void shellOpenFile(String fileName) throws Exception {
        // unix style separators
        fileName = fileName.replace('\\', '/');
                        
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        if (Utility.isSupportedType(fileName)) { 
            // use beanshell script to show molecule
            Utility.executeBeanShellScript(
                    "showMolecule(\"" + fileName + "\")");
        } else if (fileName.endsWith(StringResource.getInstance()
                                             .getBeanShellScriptExtension())) {
            // use beanshell script to show bean shell file
            Utility.executeBeanShellScript(
                    "edit(\"" + fileName + "\")");
        } // end if
        
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
    
    /** Getter for property workspaceDesktop.
     * @return Value of property workspaceDesktop.
     *
     */
    public WorkspaceDesktopPane getWorkspaceDesktop() {
        return this.workspaceDesktop;
    }
    
    /** Getter for property workspaceLog.
     * @return Value of property workspaceLog.
     *
     */
    public LogWindow getWorkspaceLog() {
        return this.workspaceLog;
    }
    
    /** Getter for property workspaceExplorer.
     * @return Value of property workspaceExplorer.
     *
     */
    public Explorer getWorkspaceExplorer() {
        return this.workspaceExplorer;
    }
    
    /** Getter for property statusBar.
     * @return Value of property statusBar.
     *
     */
    public StatusBar getStatusBar() {
        return this.statusBar;
    }
    
    /** Getter for property currentWorkspace.
     * @return Value of property currentWorkspace.
     *
     */
    public Workspace getCurrentWorkspace() {
        return this.currentWorkspace;
    }
    
    /** Setter for property currentWorkspace.
     * @param currentWorkspace New value of property currentWorkspace.
     *
     */
    public void setCurrentWorkspace(Workspace currentWorkspace) {
        // first initiate a close action on any existing workspace
        if (saveAndCloseCurrentWorkspace() == JOptionPane.CANCEL_OPTION) return;
        
        // and then welcome the new workspace!
        this.currentWorkspace = currentWorkspace;
        
        // update other UI componants .. TODO: has to change!
        workspaceExplorer.update(this.currentWorkspace);        
        
        // and set the title and change listener to reflect 
        // the current workspace
        if (this.currentWorkspace != null) {
            this.currentWorkspace.addWorkspaceChangeListener(this);
            setTitle(StringResource.getInstance().getVersion() + " - ["
                     + this.currentWorkspace.getInternalName() + "]");
        } else {
            setTitle(StringResource.getInstance().getVersion());
        } // end if
                
        // update the UI        
        repaint();
    }                
    
    /** Getter for property notificationTray.
     * @return Value of property notificationTray.
     *
     */
    public NotificationTray getNotificationTray() {
        return this.notificationTray;
    }
    
    /**
     * Getter for property startupScriptEngine
     * 
     * @return Value of property startupScriptEngine
     */
    public BeanShellScriptEngineGUI getStartupScriptEngine() {
        return this.startupScriptEngine;
    }
    
    /**
     * A notification received when there are some changes in the current
     * workspace.
     */
    @Override
    public void workspaceChanged(WorkspaceChangeEvent wce) {
        setTitle(StringResource.getInstance().getVersion() + " - ["
                     + currentWorkspace.getInternalName() + " * ]");
        workflowBar.getSaveWorkspaceButton().setEnabled(true);
    }
    
    // Variables declaration     
    private JDesktopPane desktopPane;
    private WorkspaceDesktopPane workspaceDesktop;   
    private Explorer workspaceExplorer;
    private LogWindow workspaceLog;    
    private StatusBar statusBar;
    private NotificationTray notificationTray;
    private JSplitPane mainSplitPane, workspaceSplitPane;
    private JPanel workspaceExplorerPanel, workspacePanel, workspaceLogPanel;
    private SplashScreen splashScreen;    
    private TrayIcon trayIcon = null;
    private BeanShellScriptEngineGUI startupScriptEngine;
            
    /** Holds value of property currentWorkspace. */
    private Workspace currentWorkspace;
    
    // End of variables declaration

    /**
     * Holds value of property workflowBar.
     */
    private IDEWorkflowBar workflowBar;

    /**
     * Getter for property workflowBar.
     * @return Value of property workflowBar.
     */
    public IDEWorkflowBar getWorkflowBar() {
        return this.workflowBar;
    }  

    /**
     * Holds value of property daemon.
     */
    private boolean daemon;

    /**
     * Getter for property daemon.
     * @return Value of property daemon.
     */
    public boolean isDaemon() {
        return this.daemon;
    }

    /**
     * try loading external libraries
     */
    private void loadExternalLibs() {        
        try {
            Utility.appendExtLibClassPaths();
        } catch (Exception ex) {
            System.out.println("Unable to load external libraries: " 
                               + ex.toString());
            ex.printStackTrace();
        } // end try .. catch block
    }
} // end of class MeTA
