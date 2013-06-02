/**
 * IntegratedIDETalkUI.java
 *
 * Created on Jun 19, 2009
 */
package org.meta.shell.idebeans.talk;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.InetAddress;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingConstants;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.proxy.ProxyInfo;
import org.meta.common.Utility;
import org.meta.common.resource.ImageResource;
import org.meta.net.FederationNode;
import org.meta.net.FederationRequest;
import org.meta.net.FederationServiceDiscovery;
import org.meta.net.impl.consumer.talk.FederationServiceTalkConsumer;
import org.meta.net.impl.service.talk.TalkUInitiator;
import org.meta.shell.ide.MeTA;
import org.meta.shell.idebeans.FloatingList;
import org.meta.shell.idebeans.FloatingListModel;
import org.meta.shell.idebeans.IDELoginDialog;
import org.meta.shell.idebeans.IDEOkCancelDialog.IDEDialogState;
import org.meta.shell.idebeans.ProxyInfoDialog;

/**
 * The integrated talk UI for the IDE. Includes support for talk on MeTA
 * Federation network as well as GTalk based servers.
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class IntegratedIDETalkUI extends JPanel {

    private JButton refresh, talk, moreOptions;
    private FloatingList talkUserList;

    private JPopupMenu moreOptionsPopup;
    private JMenu gtalk;
    private JMenuItem signInSignOut, proxySettings;
    private JRadioButtonMenuItem statusOnline, statusInvisible;
    private ButtonGroup statusButtonGroup;

    private XMPPConnection xmppConnection;

    private MeTA ideInstance;

    private ProxyInfo _proxyInfo;
    
    /** Creates a new instance of IntegratedIDETalkUI */
    public IntegratedIDETalkUI(MeTA ideInstance) {
        this(ideInstance, false);
    }

    /** Creates a new instance of IntegratedIDETalkUI */
    public IntegratedIDETalkUI(MeTA ideInstance, boolean displayedAsSelector) {
        this.ideInstance = ideInstance;
        this.displayedAsSelector = displayedAsSelector;

        _proxyInfo = ProxyInfo.forDefaultProxy();

        initComponents();
    }

    /** initialize the components */
    private void initComponents() {
        setBorder(BorderFactory.createLineBorder(Color.blue, 1));
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0.0;

        talkUserList = new FloatingList();
        talkUserList.setToolTipText("<html><body>" +
                "Enter the IP address of the MeTA Studio" +
                " user with whome you want to initiate a talk session. <br>" +
                "Or choose from a list of discovered services. <br>" +
                "Then press the Talk! button to initiate the talk session." +
                "</body></html>");
        talkUserList.setCellRenderer(new TalkListCellRenderer());
        talkUserList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newHostString = (String) talkUserList.getSelectedItem();

                if (((FloatingListModel) talkUserList.getModel())
                                            .getIndexOf(newHostString) < 0) {
                    talkUserList.addItem(new IDETalkUser("Anonymous",
                                   newHostString,
                                   "unidentified client",
                                   IDETalkUser.TalkUserDomain.MeTA));
                } // end if

                startTalk();
            }
        });

        MouseListener mouseListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    startTalk();
                } // end if
            }
        };
        talkUserList.addMouseListener(mouseListener);

        // start the discovey only when we are "visible"
        talkUserList.addHierarchyListener(new HierarchyListener() {
            @Override
            public void hierarchyChanged(HierarchyEvent e) {
                if (talkUserList.isShowing()) {
                    startDiscovery(true);
                } // end if
            }
        });
        
        gbc.gridx = gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        add(talkUserList, gbc);

        ImageResource images = ImageResource.getInstance();

        talk = new JButton(images.getTalk());
        talk.setIconTextGap(0);
        talk.setMargin(new Insets(0, 0, 0, 0));
        talk.setBorderPainted(false);
        talk.setRolloverEnabled(true);        
        talk.setToolTipText("Click to start a talk session");
        talk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startTalk();
            }
        });
        gbc.gridx = 2;
        gbc.weightx = 0.0;
        gbc.gridwidth = 1;
        add(talk, gbc);

        refresh = new JButton(images.getRefresh());      
        refresh.setIconTextGap(0);
        refresh.setMargin(new Insets(0, 0, 0, 0));
        refresh.setBorderPainted(false);
        refresh.setRolloverEnabled(true);
        refresh.setToolTipText("Click to refresh the list of available users " +
                "with whome you can start a conversation");
        refresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startDiscovery(false);
            }
        });
        gbc.gridx = 3;
        add(refresh, gbc);
        
        moreOptions = new JButton("GTalk login", images.getExpand());                
        moreOptions.setHorizontalTextPosition(SwingConstants.LEFT);
        moreOptions.setBorderPainted(false);
        moreOptions.setRolloverEnabled(true);        
        moreOptions.setToolTipText("Click for options to logging into GTalk");
        moreOptions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moreOptionsPopup.show(moreOptions, 0, moreOptions.getHeight());
            }
        });
        gbc.gridwidth = 2;
        gbc.gridx = 4;
        add(moreOptions, gbc);

        // make the moreOptionsPopup
        moreOptionsPopup = new JPopupMenu("More talk options");
            gtalk = new JMenu("Gtalk options");
                signInSignOut = new JMenuItem("Sign In ...");
                signInSignOut.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (signInSignOut.getText().equals("Sign In ...")) {
                            startGtalkSession();
                        } else {
                            if (xmppConnection != null) {
                                // first remove any listeners
                                ChatManagerListener cml =
                                        xmppConnection.getChatManager()
                                          .getChatListeners().iterator().next();
                                xmppConnection.getChatManager()
                                              .removeChatListener(cml);
                                // then disconnect
                                xmppConnection.disconnect();
                            } // end if

                            signInSignOut.setText("Sign In ...");
                            statusInvisible.setEnabled(false);
                            statusOnline.setEnabled(false);
                            statusInvisible.setSelected(true);
                        } // end if
                    }
                });
                gtalk.add(signInSignOut);

                gtalk.addSeparator();

                statusButtonGroup = new ButtonGroup();
                statusOnline = new JRadioButtonMenuItem("Online");
                statusOnline.setSelected(false);
                statusOnline.setEnabled(false);
                statusOnline.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (statusOnline.isSelected()) {
                            if (xmppConnection != null) {
                                Presence presence = new Presence(
                                                     Presence.Type.available);
                                xmppConnection.sendPacket(presence);
                            } // end if
                        } // end if
                    }
                });
                statusButtonGroup.add(statusOnline);
                gtalk.add(statusOnline);

                statusInvisible = new JRadioButtonMenuItem("Invisible");
                statusInvisible.setSelected(true);
                statusInvisible.setEnabled(false);
                statusInvisible.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (statusInvisible.isSelected()) {
                            if (xmppConnection != null) {
                                Presence presence = new Presence(
                                                     Presence.Type.unavailable);
                                xmppConnection.sendPacket(presence);
                            } // end if
                        } // end if
                    }
                });
                statusButtonGroup.add(statusInvisible);
                gtalk.add(statusInvisible);

                gtalk.addSeparator();

                proxySettings = new JMenuItem("Proxy settings ...");
                proxySettings.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ProxyInfoDialog pid = new ProxyInfoDialog(ideInstance,
                                "Specify proxy details for connection");

                        if (pid.showLoginDialog() == IDEDialogState.OK) {
                            switch(pid.getProxyType()) {
                                case DIRECT:
                                    _proxyInfo = ProxyInfo.forDefaultProxy();
                                    break;
                                case HTTP:
                                    _proxyInfo = ProxyInfo.forHttpProxy(
                                            pid.getHost(),
                                            pid.getPort(),
                                            pid.getUserName(),
                                            pid.getPasswordText());
                                    break;
                                case SOCKS:
                                    _proxyInfo = ProxyInfo.forSocks5Proxy(
                                            pid.getHost(),
                                            pid.getPort(),
                                            pid.getUserName(),
                                            pid.getPasswordText());
                                    break;
                            } // end switch .. case block
                        } // end if

                        pid.clearFields();
                    }
                });
                gtalk.add(proxySettings);
                
            moreOptionsPopup.add(gtalk);
    }

    private Thread discoveryThread;

    /** start discovery of peer MeTA Studio instances */
    private void startDiscovery(final boolean useCache) {
        if (discoveryThread == null || !discoveryThread.isAlive()) {
          discoveryThread = new Thread() {
            @Override
            public void run() {
                System.out.println("Discovery on, " +
                        "this may take few minutes...");
                FederationServiceDiscovery inst
                              = FederationServiceDiscovery.getInstance();
                inst.setTimeout(30); // time out
                inst.setShowLogMessages(false); // no log messages

                // first read from cache
                ArrayList<FederationNode> fNodes = inst.listMeTA();
                
                // if no cache is requested, or there are no entries
                // then force a discovery
                if (!useCache || fNodes.isEmpty())
                    fNodes = inst.discoverMeTA();
                
                for (FederationNode fNode : fNodes) {
                    IDETalkUser usr = new IDETalkUser(fNode.getUserName(),
                                   fNode.getIpAddress().getHostAddress(),
                                   fNode.getMetaVersion(),
                                   IDETalkUser.TalkUserDomain.MeTA);

                    // see if this entry is already there
                    if (talkUserList.getIndexOf(usr) < 0) {
                        talkUserList.addItem(usr);
                    } // end if
                } // end for

                System.out.println("Done");

                inst.setShowLogMessages(true); // turn it on again
            }
          };

          discoveryThread.setName("WorkflowBar discovery Thread");
          discoveryThread.setPriority(Thread.MIN_PRIORITY);
          discoveryThread.start();
        } // end if
    }

    /** actually start a talk session */
    private void startTalk() {
        if (displayedAsSelector) {
            selectedTalkUser = (IDETalkUser) talkUserList.getSelectedItem();
            return;
        } // end if

        // else we start a talk thread
        Thread talkThread = new Thread() {
            @Override
            public void run() {
                try {
                    if (talkUserList.getSelectedItem() == null) return;

                    FederationServiceTalkConsumer f =
                            new FederationServiceTalkConsumer();

                    IDETalkUser user = (IDETalkUser) talkUserList.getSelectedItem();

                    switch(user.getUserDomain()) {
                        case MeTA:
                            FederationRequest r = f.discover(
                                      InetAddress.getByName(user.getHostID()));
                            f.consume(r);
                            break;
                        case GTalk:
                            XMPPTalkBacked talkBackend =
                                    new XMPPTalkBacked(xmppConnection, user);

                            IDETalkFrame talkFrame =
                                    new IDETalkFrame(ideInstance, talkBackend);

                            talkBackend.setTalkUIClient(talkFrame);                            
                            break;
                    } // end of switch .. case block
                } catch (Exception ignored) {
                    System.err.println("Unhandeled exception while" +
                            " initiating a talk request: " + ignored.toString());
                    ignored.printStackTrace();

                    JOptionPane.showMessageDialog(IntegratedIDETalkUI.this,
                                    "Unable to start a talk request. "
                                    + "See runtime log for errors.",
                                    "Unable to start Talk",
                                    JOptionPane.ERROR_MESSAGE);
                } // end of try .. catch block
            }
        };

        talkThread.setName("WorkflowBar talk Thread");
        talkThread.start();
    }

    private Thread gtalkLoginThread;

    /** initiate a GTalk/Jabber session */
    private void startGtalkSession() {
        if (gtalkLoginThread == null || !gtalkLoginThread.isAlive()) {
            gtalkLoginThread = new Thread() {
                @Override
                public void run() {
                    IDELoginDialog ld = new IDELoginDialog(ideInstance,
                            "Enter your GTalk user name and password");

                    if (ld.showLoginDialog() 
                        == IDELoginDialog.IDEDialogState.OK) {

                        XMPPConfigReader xr = XMPPConfigReader.getInstance();
                        XMPPConfig xc = xr.getDefaultXMPPConfig();

                        String userName = ld.getUserName();
                        String domain = xc.getDomain();

                        if (userName.endsWith("@" + xc.getDomain())) {
                            userName = userName.split("@")[0];
                        } else {
                            if (userName.indexOf("@") > 0) {
                                domain = userName.split("@")[1];
                                userName = userName.split("@")[0];
                            } // end if
                        } // end if

                        ConnectionConfiguration connConfig = null;

                        System.out.println("Conneting to: " + domain);

                        if (domain.equals(xc.getDomain())) {
                            connConfig = new ConnectionConfiguration(
                                  xc.getServer(), xc.getPort(), xc.getDomain(),
                                  _proxyInfo);
                        } else {
                            connConfig = new ConnectionConfiguration(domain,
                                                   xc.getPort(), _proxyInfo);
                        } // end if

                        xmppConnection = new XMPPConnection(connConfig);

                        try {
                            System.out.println("Starting connection...");
                            xmppConnection.connect();
                            System.out.println("Connected.");

                            System.out.println("Logging in...");
                            xmppConnection.login(userName + "@" + domain,
                                                  ld.getPasswordText());
                            System.out.println("Logged in.");
                            Presence presence = new Presence(
                                                     Presence.Type.unavailable);
                            xmppConnection.sendPacket(presence);
                            System.out.println("Set the status");
                            
                            Roster roster = xmppConnection.getRoster();
                            FloatingListModel model =
                                    (FloatingListModel) talkUserList.getModel();

                            for (RosterEntry re : roster.getEntries()) {
                                IDETalkUser tUser = new IDETalkUser(re.getName(),
                                        xc.getDomain(), re.getType().name(),
                                        IDETalkUser.TalkUserDomain.GTalk);
                                if (re.getName() == null) {
                                    tUser.setUserName(re.getUser());
                                } // end if

                                tUser.setAdditionalInfo(re);
                                model.addElement(tUser);
                            } // end for

                            startRemoteChatListener();
                            
                            // if we are connected we enable some UI elements
                            statusInvisible.setEnabled(true);
                            statusOnline.setEnabled(true);
                            signInSignOut.setText("Sign Out");
                        } catch (Exception err) {
                            JOptionPane.showMessageDialog(ideInstance,
                                    "Unable to login: " + userName + "@"
                                    + domain + ". Check your user name and"
                                    + " password and try again.");
                            System.err.println("Error while logging in: " + err);
                            err.printStackTrace();
                        } // end of try .. catch exception block

                    } // end if

                    // paranoid about password text ligering around
                    ld.clearFields();
                    ld = null;
                }
            };

            gtalkLoginThread.setName("WorkflowBar gtalk login Thread");
            gtalkLoginThread.setPriority(Thread.MIN_PRIORITY);
            gtalkLoginThread.start();
        } // end if
    }

    /** start listening to remote chats */
    private void startRemoteChatListener() {
        if (xmppConnection == null) return;

        final ChatManager chatManager = xmppConnection.getChatManager();
        
        // listen for remote chats
        chatManager.addChatListener(new ChatManagerListener() {
            @Override
            public void chatCreated(Chat theChat, boolean createdLocally) {
                System.out.println("Chat created: " + createdLocally);
                
                if (!createdLocally) {
                    // then we need to invoke the UI
                    try {
                        TalkUInitiator uiInitiator =
                          (TalkUInitiator)
                            (Utility.getDefaultImplFor(TalkUInitiator.class)
                               .newInstance());

                        IDETalkUser selfUser =
                              new IDETalkUser(xmppConnection.getUser(),
                                              xmppConnection.getHost(),
                                              xmppConnection.getServiceName(),
                                              IDETalkUser.TalkUserDomain.GTalk);
                        selfUser.setAdditionalInfo(theChat);
                        
                        uiInitiator.initTalkUI(
                                new XMPPTalkBacked(xmppConnection, selfUser));
                    } catch (Exception e) {
                        System.out.println("Failed to initiate talk UI: " + e);
                        e.printStackTrace();
                    } // end of try .. catch block
                } // end if
            }
        });
    }

    private boolean displayedAsSelector;

    /**
     * Get the value of displayedAsSelector
     *
     * @return the value of displayedAsSelector
     */
    public boolean isDisplayedAsSelector() {
        return displayedAsSelector;
    }

    private IDETalkUser selectedTalkUser;

    /**
     * Get the value of selectedTalkUser. The value returned by this method
     * is only useful if <code>isDisplayedAsSelector()</code> returns true.
     *
     * @return the value of selectedTalkUser
     */
    public IDETalkUser getSelectedTalkUser() {
        return selectedTalkUser;
    }

    /**
     * Show this control in a dialog
     *
     * @return the selected IDETalkUser
     */
    public IDETalkUser showIt(String title) {
        final JDialog diag = new JDialog(ideInstance, title);
        diag.setLayout(new BorderLayout());
        diag.add(this, BorderLayout.CENTER);
        diag.setModal(true);

        // capture action performed event to close this dialog
        talkUserList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                diag.setVisible(false);
            }
        });

        diag.pack();
        diag.setLocationRelativeTo(ideInstance);
        diag.setVisible(true);

        return getSelectedTalkUser();
    }

    /** cell renderer for the list */
    private class TalkListCellRenderer extends DefaultListCellRenderer {
        private final ImageIcon talkImg = ImageResource.getInstance().getTalk();
        private final ImageIcon metaImg 
                = new ImageIcon(ImageResource.getInstance().getIcon().getImage()
                                  .getScaledInstance(talkImg.getIconWidth(),
                                                     talkImg.getIconHeight(),
                                                     Image.SCALE_SMOOTH));
        
        @Override
        public Component getListCellRendererComponent(JList list,
                Object value, int index, boolean isSelected, 
                boolean hasFocus)  {
            super.getListCellRendererComponent(list, value, index, 
                                               isSelected, hasFocus);

            IDETalkUser user = (IDETalkUser) value;

            setText(user.toHTMLString());
            
            switch(user.getUserDomain()) {
                case MeTA:
                    setIcon(metaImg);
                    break;
                case GTalk:
                    setIcon(talkImg);
                    break;
            } // end of switch ... case block

            return this;
        }
    }
}
