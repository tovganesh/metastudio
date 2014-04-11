/*
 * IDETalkFrame.java
 *
 * Created on July 23, 2006, 11:03 AM
 */

package org.meta.shell.idebeans.talk;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Date;

import java.util.Enumeration;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import org.meta.molecule.Molecule;
import org.meta.common.resource.ImageResource;
import org.meta.net.FederationRequest;
import org.meta.net.impl.consumer.talk.FederationServiceVoiceTalkConsumer;
import org.meta.net.impl.service.talk.ImageTalkObject;
import org.meta.net.impl.service.talk.MoleculeTalkObject;
import org.meta.net.impl.service.talk.TalkBackend;
import org.meta.net.impl.service.talk.TalkCommand;
import org.meta.net.impl.service.talk.TalkObject;
import org.meta.net.impl.service.talk.TalkUISupport;

import org.meta.shell.ide.MeTA;
import org.meta.shell.idebeans.DockableFrame;
import org.meta.shell.idebeans.DockingPanel;
import org.meta.shell.idebeans.IDEFileChooser;
import org.meta.shell.idebeans.IDEFileFilter;
import org.meta.shell.idebeans.IDEFileView;
import org.meta.shell.idebeans.MoleculeListDialog;
import org.meta.shell.idebeans.workbook.IDEWorkbook;
import org.meta.shell.idebeans.workbook.WorkbookItem;

/**
 * The main UI for the talk client.
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class IDETalkFrame extends JInternalFrame implements TalkUISupport {
    
    private JToolBar talkToolBar;
    private JButton sendMoleculeObject, sendBeanshellCode, sendImage, 
            changeDisaplayName, voiceCall, dockTo;
        
    private IDEWorkbook chatSession;    
            
    private JPanel typeAndSendPanel;
    private JTextArea typeArea;
    private JButton sendMessage;
    
    private static String frameTitle 
                               = "IDETalk - talk with peer MeTA Studio users";
    private MeTA ideInstance;
    
    private String remoteName;
    
    private JSplitPane splitPane;
    
    private DockingPanel talkDockingPanel;
    
    private FederationServiceVoiceTalkConsumer fedVoice;
    
    /** Creates a new instance of IDETalkFrame */
    public IDETalkFrame(MeTA ideInstance, TalkBackend tb) {
        super(frameTitle + " [" + tb.getDisplayName() + "]", 
               true, true, true, true);
        setFrameIcon(ImageResource.getInstance().getTalk());
        
        this.talkBackend = tb;
        this.talkBackend.setTalkUIClient(this);        
        
        // set default display for remote use
        remoteName = tb.getDisplayName();

        initUI();
        
        // add self to IDE workspace desktop pane
        this.ideInstance = ideInstance;
        ideInstance.getWorkspaceDesktop().addInternalFrame(this);
        
        setVisible(true);                   

        splitPane.setDividerLocation(0.90);
        
        addInternalFrameListener(new InternalFrameAdapter() {            
            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                if (!sendMessage.isEnabled()) return;
                
                TalkObject talkObj = new TalkObject();
                
                talkObj.setType(TalkObject.TalkObjectType.TALK_COMMAND);
                
                TalkCommand talkCommand = new TalkCommand("Close session",
                                         TalkCommand.CommandType.CLOSE_SESSION);
                
                talkObj.setTalkObjectContent(talkCommand);
                
                try {
                    talkBackend.sendMessage(talkObj);
                } catch (IOException ex) {
                    System.out.println(
                            "Exception while sending close session message: "
                            + ex.toString());
                    ex.printStackTrace();
                } // end of try .. catch block
            }            
        });
        
    }

    /**
     * choose and send Image object
     */
    private void chooseAndSendImage() {
        IDEFileChooser fileChooser = new IDEFileChooser();
        
        fileChooser.setDialogTitle("Open a image file...");
                
        String [] writerFormats = ImageIO.getReaderFormatNames();
    
        for(String wForamt : writerFormats) {
            fileChooser.addChoosableFileFilter(new IDEFileFilter(wForamt,
                                           wForamt + " image file format"));
        } // end for
        
        
        if (fileChooser.showOpenDialog(ideInstance)
                    == IDEFileChooser.APPROVE_OPTION) {   
            String fileName = fileChooser.getSelectedFile().getAbsolutePath();            
            
            try {
                BufferedImage image = ImageIO.read(new File(fileName));
                
                TalkObject talkObj = new ImageTalkObject();
                
                talkObj.setType(TalkObject.TalkObjectType.IMAGE);
                talkObj.setTalkObjectContent(image);
                
                talkBackend.sendMessage(talkObj);

                WorkbookItem itm = new WorkbookItem(
                             ((BufferedImage) talkObj.getTalkObjectContent()));
                itm.setTitle(talkBackend.getDisplayName()
                             + " (" + (new Date()).toString() + ") :");
                             chatSession.addWorkbookItem(itm);                
            } catch (IOException ex) {
                System.err.println("Unable to send message: " + ex.toString());
                ex.printStackTrace();                
            }                        
        } // end if        
    }
    
    /**
     * Allow to choose and send molecule object embedded in chat session.
     *
     */
    private void chooseAndSendMolecule() {        
        final MoleculeListDialog ml = new MoleculeListDialog(ideInstance);
        
        ml.setOkOptionText("Send selected molecule");
        ml.addOkOptionActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae)  {
                Molecule molecule = ml.getSelectedMolecule();
                
                if (molecule == null) return;
                
                TalkObject talkObj = new MoleculeTalkObject();
                
                talkObj.setType(TalkObject.TalkObjectType.MOLECULE);
                talkObj.setTalkObjectContent(molecule);
                
                try {
                    talkBackend.sendMessage(talkObj);
                    
                    WorkbookItem itm = new WorkbookItem(
                            ((Molecule) talkObj.getTalkObjectContent()));
                    itm.setTitle(talkBackend.getDisplayName()
                    + " (" + (new Date()).toString() + ") :");
                    chatSession.addWorkbookItem(itm);
                } catch (IOException ex) {
                    System.out.println(
                            "Exception while sending message: "
                            + ex.toString());
                    ex.printStackTrace();
                } // end of try .. catch block
            }
        });
        
        ml.showListDialog();
    }
    
    /**
     * Allow to choose and send BeanShell script embedded in chat session.
     *
     */
    private void chooseAndSendBsh() {
        IDEFileChooser fileChooser = new IDEFileChooser();
        
        fileChooser.setDialogTitle("Open a BeanShell script file...");
        
        IDEFileFilter fileFilter = new IDEFileFilter("bsh",
                                                     "BeanShell script files");
        
        // and add the file filter
        fileChooser.addChoosableFileFilter(fileFilter);
        // and add the iconic stuff
        fileChooser.setFileView(new IDEFileView());
        
        if (fileChooser.showOpenDialog(IDETalkFrame.this)
             == IDEFileChooser.APPROVE_OPTION) {
            if (!fileChooser.getSelectedFile().exists()) return;
            
            String fileName = fileChooser.getSelectedFile().getAbsolutePath();
            
            try {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(new FileInputStream(fileName)));
                String theLine;
                StringBuffer theLines = new StringBuffer();
                
                while(true) {
                    theLine = br.readLine();
                    
                    if (theLine == null) break;
                    
                    theLines.append(theLine + "\n");
                } // end while
                
                br.close();                                
                                
                TalkObject talkObj = new TalkObject();
                
                talkObj.setType(TalkObject.TalkObjectType.BEANSHELL_SCRIPT);
                talkObj.setTalkObjectContent(theLines.toString());
                
                try {
                    talkBackend.sendMessage(talkObj);
                    
                    WorkbookItem itm = new WorkbookItem(theLines.toString(), 
                                                        true);
                    
                    itm.setTitle(talkBackend.getDisplayName()
                                 + " (" + (new Date()).toString() + ") :");
                    chatSession.addWorkbookItem(itm);
                } catch (IOException ex) {
                    System.out.println(
                            "Exception while sending message: "
                            + ex.toString());
                    ex.printStackTrace();
                } // end of try .. catch block                
            } catch(Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error opening file : " + e.toString(),
                        "Error!", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } // end try .. catch block
            
        } // end if
    }        
    
    /**
     * initilize the talk UI
     */
    private void initUI() {
        talkDockingPanel = new DockingPanel();

        talkDockingPanel.setBackground(Color.white);
        talkDockingPanel.setLayout(new BorderLayout());
        
        final ImageResource images = ImageResource.getInstance();
                
        // the toolbar
        talkToolBar = new JToolBar();
        talkToolBar.setBackground(Color.white);
            sendMoleculeObject = new JButton("Send Molecule Object");
            sendMoleculeObject.setIcon(images.getMolecule());
            sendMoleculeObject.setBackground(Color.white);
            sendMoleculeObject.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent ae)  {
                   chooseAndSendMolecule();                                      
               }
            });
            talkToolBar.add(sendMoleculeObject);
            
            sendBeanshellCode = new JButton("Send Beanshell Code");
            sendBeanshellCode.setIcon(images.getBeany());
            sendBeanshellCode.setBackground(Color.white);
            sendBeanshellCode.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent ae)  {
                   chooseAndSendBsh();
               }
            });
            talkToolBar.add(sendBeanshellCode);
            
            sendImage = new JButton("Send Image");
            sendImage.setIcon(images.getMoleculeEditor());
            sendImage.setBackground(Color.white);
            sendImage.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent ae)  {
                   chooseAndSendImage();
               }            
            });
            talkToolBar.add(sendImage);
                        
            voiceCall = new JButton("Start a voice call");  
            voiceCall.setIcon(images.getVoice());
            voiceCall.setBackground(Color.white);
            voiceCall.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent ae)  {
                   if (voiceCall.getText().equals("Start a voice call")) {
                       try {                         
                         fedVoice = new FederationServiceVoiceTalkConsumer();
                         FederationRequest req = fedVoice.discover(talkBackend
                                .getAssociatedFederationRequest()
                                .getFederationConnection().getInetAddress());
                         fedVoice.consume(req);
                        
                         voiceCall.setText("Stop the voice call");
                         voiceCall.setIcon(images.getNovoice());
                       } catch (Exception e) {
                         System.err.println("Error starting voice call: " + 
                                            e.toString());
                         e.printStackTrace();
                       } // end of try .. catch block
                   } else {
                       fedVoice.stopVoiceChat();
                       voiceCall.setText("Start a voice call");
                       voiceCall.setIcon(images.getVoice());
                   } // end if
               }            
            });
            talkToolBar.add(voiceCall);
            
            changeDisaplayName = new JButton("Change Display Name [" 
                                             + talkBackend.getDisplayName() 
                                             + "]");
            changeDisaplayName.setBackground(Color.white);
            changeDisaplayName.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent ae)  {
                    String displayName = JOptionPane.showInputDialog(
                            ideInstance, 
                           "Enter a new display name", 
                            talkBackend.getDisplayName());
                    
                    if (displayName != null) {
                        talkBackend.setDisplayName(displayName);
                        changeDisaplayName.setText("Change Display Name [" 
                                          + talkBackend.getDisplayName() + "]");
                    } // end if
                    
                    changeDisaplayName.setToolTipText(
                            talkBackend.getDisplayName());
               }
            });
            changeDisaplayName.setToolTipText(talkBackend.getDisplayName());
            talkToolBar.add(changeDisaplayName);
            
            dockTo = new JButton("Dock To >>");
            dockTo.setBackground(Color.white);
            dockTo.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent ae)  {
                   if (dockTo.getText().equals("Un dock")) {
                      IDETalkFrame.this.talkDockingPanel.getDockedFrame().unDock();
                      IDETalkFrame.this.add(IDETalkFrame.this.talkDockingPanel);
                      dockTo.setText("Dock To >>");
                      IDETalkFrame.this.setVisible(true);
                      IDETalkFrame.this.toFront();                      
                      return;
                   } // end if
                   
                   
                   Enumeration<JComponent> dockableFrames 
                     = ideInstance.getWorkspaceDesktop().getDockableFrameList();
                   
                   JPopupMenu popup = new JPopupMenu();
                   
                   if (!dockableFrames.hasMoreElements()) return;
                   
                   while(dockableFrames.hasMoreElements()) {
                       JComponent dockableFrame = dockableFrames.nextElement();
                       
                       if (dockableFrame instanceof JInternalFrame) {
                          JInternalFrame frame = (JInternalFrame) dockableFrame;
                          
                          FrameJMenuItem menuItem = new FrameJMenuItem(
                                                          frame.getTitle(),
                                                          frame.getFrameIcon(),
                                                          frame);
                          menuItem.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent ae)  {
                                if (ae.getSource() instanceof FrameJMenuItem) {
                                  JInternalFrame frame = 
                                   ((FrameJMenuItem) ae.getSource()).getFrame();
                                                                    
                                  if (frame instanceof DockableFrame) {
                                      ((DockableFrame) frame).dockIt(
                                            IDETalkFrame.this.getTitle(), 
                                            IDETalkFrame.this.talkDockingPanel);
                                      IDETalkFrame.this.setVisible(false);
                                      frame.toFront();
                                      dockTo.setText("Un dock");
                                  } // end if
                                } // end if
                            }
                          });
                                  
                          popup.add(menuItem);
                       } // end for
                   } // end for                                      
                   popup.show(dockTo, dockTo.getWidth(), dockTo.getHeight());
               }
            });
            talkToolBar.add(dockTo);
            
        talkDockingPanel.add(talkToolBar, BorderLayout.NORTH);
        
        // the session        
        chatSession = new IDEWorkbook();        
        chatSession.setPassiveMode(true);        
        
        // type and send panel
        typeAndSendPanel = new JPanel(new BorderLayout());
        typeAndSendPanel.setBackground(Color.white);
            typeArea = new JTextArea();
            typeArea.setWrapStyleWord(true);
            typeArea.setLineWrap(true);
            typeArea.setBorder(BorderFactory.createEmptyBorder());
            typeArea.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        sendMessage.doClick();
                        
                        e.consume();
                        typeArea.setText("");
                    } // end if
                }                                
            });
            JScrollPane tpsp = new JScrollPane(typeArea,
                 JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                 JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            tpsp.setBackground(Color.white);
            tpsp.setBorder(BorderFactory.createEmptyBorder());
            typeAndSendPanel.add(tpsp, BorderLayout.CENTER);
            typeAndSendPanel.setBackground(Color.white);
            
            sendMessage = new JButton("Send");
            sendMessage.setMnemonic('S');
            sendMessage.setBackground(Color.white);
            sendMessage.setBorderPainted(false);
            sendMessage.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent ae)  {
                   TalkObject talkObj = new TalkObject();
                   
                   if ((typeArea.getText() == null)
                       || (typeArea.getText().equals(""))) {
                       typeArea.grabFocus();
                       return;
                   } 
                   
                   talkObj.setType(TalkObject.TalkObjectType.STRING);
                   talkObj.setTalkObjectContent(typeArea.getText());
                   typeArea.setText("");
                   
                   try {
                        talkBackend.sendMessage(talkObj);
                        WorkbookItem itm = new WorkbookItem(
                                   ((String) talkObj.getTalkObjectContent()));
                        itm.setTitle(talkBackend.getDisplayName() 
                                     + " (" + (new Date()).toString() + ") :");
                        chatSession.addWorkbookItem(itm);
                   } catch (IOException ex) {
                        System.out.println("Exception while sending message: " 
                                            + ex.toString());
                        ex.printStackTrace();
                   } // end of try .. catch block
                   
                   typeArea.grabFocus();
               }
            });
            typeAndSendPanel.setBorder(BorderFactory.createEmptyBorder());
            typeAndSendPanel.add(sendMessage, BorderLayout.EAST);
            
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
           new JScrollPane(chatSession, 
             JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
             JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), 
           typeAndSendPanel);        

        splitPane.setForeground(Color.white);
        splitPane.setBackground(Color.white);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        talkDockingPanel.add(splitPane, BorderLayout.CENTER);
        
        setLayout(new BorderLayout());
        add(talkDockingPanel, BorderLayout.CENTER);
    }
    
    /**
     * call back to UI when a message is received.
     *
     * @param talkObj reference to the message object
     */
    @Override
    public void messageReceived(TalkObject talkObj) {
        if (!isVisible()) setVisible(true);
        
        WorkbookItem itm;                

        // take appropriate action on the received message
        switch(talkObj.getType()) {
            case TALK_COMMAND:
                TalkCommand tc = (TalkCommand) talkObj.getTalkObjectContent();
                
                if (tc.getType() 
                         == TalkCommand.CommandType.DISPLAY_NAME_CHANGED) {
                    setTitle(frameTitle 
                             + " [" + (remoteName = tc.getDescription()) + "]");
                } else if (tc.getType()
                             == TalkCommand.CommandType.CLOSE_SESSION) {
                    typeArea.setText("This talk session is no longer valid!");
                    typeArea.setEnabled(false);
                    sendMessage.setEnabled(false);
                    typeAndSendPanel.setEnabled(false);
                    return;
                } // end if
                break;
            case STRING:
                itm = new WorkbookItem(
                                   ((String) talkObj.getTalkObjectContent()));
                itm.setTitle(remoteName
                              + " (" + (new Date()).toString() + ") :");
                chatSession.addWorkbookItem(itm);                
                break;
            case BEANSHELL_SCRIPT:
                itm = new WorkbookItem(
                                   ((String) talkObj.getTalkObjectContent()),
                                   true);
                itm.setTitle(remoteName
                              + " (" + (new Date()).toString() + ") :");
                chatSession.addWorkbookItem(itm);                
                break;
            case MOLECULE:
                WorkbookItem mitm = new WorkbookItem(
                                   ((Molecule) talkObj.getTalkObjectContent()));
                mitm.setTitle(remoteName
                              + " (" + (new Date()).toString() + ") :");
                chatSession.addWorkbookItem(mitm);
                break;
            case IMAGE:
                WorkbookItem iitm = new WorkbookItem(
                                     ((Image) talkObj.getTalkObjectContent()));
                iitm.setTitle(remoteName
                              + " (" + (new Date()).toString() + ") :");
                chatSession.addWorkbookItem(iitm);
                break;
            default:
                break;
        } // end of switch .. case        
        
        
        // TODO: more elegant ways of notification
        if ((!isSelected()) || (!isVisible())) {
            Toolkit.getDefaultToolkit().beep();
            
            ideInstance.showSystemTrayMessage("IDE Talk",
                                      "You have reveived a new message!",
                                      TrayIcon.MessageType.INFO);
        } // end if
    }

    /**
     * Holds value of property talkBackend.
     */
    private TalkBackend talkBackend;

    /**
     * Getter for property talkBackend.
     * @return Value of property talkBackend.
     */
    public TalkBackend getTalkBackend() {
        return this.talkBackend;
    }

    /**
     * Setter for property talkBackend.
     * @param talkBackend New value of property talkBackend.
     */
    public void setTalkBackend(TalkBackend talkBackend) {
        this.talkBackend = talkBackend;
    }
    
    /**
     * private class representing this menu item
     */
    private class FrameJMenuItem extends JMenuItem {                
        public FrameJMenuItem(String text, Icon icon, JInternalFrame frame) {
            super(text, icon);            
            this.frame = frame;
        }
        
        private JInternalFrame frame;

        /**
         * Get the value of frame
         *
         * @return the value of frame
         */
        public JInternalFrame getFrame() {
            return frame;
        }

        /**
         * Set the value of frame
         *
         * @param frame new value of frame
         */
        public void setFrame(JInternalFrame frame) {
            this.frame = frame;
        }
    }
} // end of class IDETalkFrame
