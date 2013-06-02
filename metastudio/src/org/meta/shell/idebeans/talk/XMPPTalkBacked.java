/**
 * XMPPTalkBacked.java
 *
 * Created on 28/07/2009
 */

package org.meta.shell.idebeans.talk;

import java.io.IOException;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.meta.common.resource.StringResource;
import org.meta.net.FederationRequest;
import org.meta.net.impl.service.talk.TalkBackend;
import org.meta.net.impl.service.talk.TalkObject;
import org.meta.net.impl.service.talk.TalkUISupport;

/**
 * A talk backend for XMPP client.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class XMPPTalkBacked implements TalkBackend, Runnable {

    private XMPPConnection xmppConnection;
    private IDETalkUser talkUser;

    private static final String MSG_TYPE    = "_messageType";
    private static final String MSG_CONTENT = "_messageContent";

    /** Creates a new instance of XMPPTalkBacked */
    public XMPPTalkBacked(XMPPConnection conn, IDETalkUser user) {
        xmppConnection = conn;
        talkUser       = user;

        Thread talkThread = new Thread(this);
        talkThread.setName("Talk Thread for : " + user.toString());
        talkThread.setPriority(Thread.MIN_PRIORITY);
        talkThread.start();
    }

    /**
     * Method called to send a message.
     *
     * @param talkObj the TalkObject to send
     * @throws IOException if send unsuccessful
     */
    @Override
    public void sendMessage(TalkObject talkObj) throws IOException {
        if (theChat != null) {
            try {
                Message msg = new Message();
                msg.setType(Message.Type.chat);
                msg.setProperty(MSG_TYPE, talkObj.getType());

                // prepare the body
                switch(talkObj.getType()) {
                    case STRING:
                    case BEANSHELL_SCRIPT:
                        msg.setBody(talkObj.getTalkObjectContent().toString());
                        break;
                    case MOLECULE:
                    case IMAGE:
                    case TALK_COMMAND:
                        msg.setProperty(MSG_CONTENT, talkObj);
                        msg.setBody(talkObj.getType()
                              + " type sent [Viewable in : " +
                              StringResource.getInstance().getVersion() + "]");
                        break;
                } // end switch ... case
                
                // send the message
                theChat.sendMessage(msg);
            } catch (XMPPException e) {
                throw new IOException("Error : " + e.toString());
            } // end try
        } // end if
    }

    /**
     * Holds value of property talkUIClient.
     */
    private TalkUISupport talkUIClient;

    /**
     * Regieter UI client
     *
     * @param ui the UI client for Talk
     */
    @Override
    public void setTalkUIClient(TalkUISupport ui) {
        this.talkUIClient = ui;
    }

    /**
     * Getter for property talkUIClient.
     * @return Value of property talkUIClient.
     */
    @Override
    public TalkUISupport getTalkUIClient() {
        return this.talkUIClient;
    }

    /**
     * Getter for property displayName.
     * @return Value of property displayName.
     */
    @Override
    public String getDisplayName() {
        // return talkUser.getUserName();
        return xmppConnection.getUser();
    }

    /**
     * Setter for property displayName.
     * @param displayName New value of property displayName.
     */
    @Override
    public void setDisplayName(String displayName) {
        talkUser.setUserName(displayName);
    }

    /**
     * Return the associated FederationRequest object.
     *
     * @return FederationRequest the associated FederationRequest object
     */
    @Override
    public FederationRequest getAssociatedFederationRequest() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private ChatManager chatManager;
    private Chat theChat;
    
    /**
     * The backed work
     */
    @Override
    public void run() {
        chatManager = xmppConnection.getChatManager();

        MessageListener ml = new MessageListener() {
            @Override
            public void processMessage(Chat chat, Message message) {
                if (message.getBody().trim().equals("")) return;

                try {
                    // first try to read in message type
                    Object mType = message.getProperty(MSG_TYPE);
                    TalkObject.TalkObjectType msgType
                               = TalkObject.TalkObjectType.STRING;

                    System.out.println("message type : " + mType);
                    
                    if (mType != null)
                        msgType = (TalkObject.TalkObjectType) mType;

                    TalkObject talkObj = new TalkObject();
                    talkObj.setType(msgType);

                    // prepare the talk object
                    switch(msgType) {
                        case STRING:
                        case BEANSHELL_SCRIPT:
                            talkObj.setTalkObjectContent(message.getBody());
                            break;
                        case MOLECULE:
                        case IMAGE:
                        case TALK_COMMAND:
                            talkObj.setTalkObjectContent(
                               ((TalkObject) message.getProperty(MSG_CONTENT))
                                  .getTalkObjectContent());
                            break;
                    } // end of switch .. case block

                    // and send notification to the UI
                    talkUIClient.messageReceived(talkObj);
                } catch(Exception e) {
                    // fall back ... make every thing as string
                    TalkObject talkObj = new TalkObject();
                    Message.Type xmppType = message.getType();

                    // only handle "chat" and "normal" types
                    if (xmppType.equals(Message.Type.chat)
                        || xmppType.equals(Message.Type.normal)) {
                        talkObj.setTalkObjectContent(message.getBody());
                        talkObj.setType(TalkObject.TalkObjectType.STRING);

                        talkUIClient.messageReceived(talkObj);
                    } // end if
                } // end of try catch block
            }
        };

        // the passed entry is either a RosterEntry or a new Chat entry
        try {
            RosterEntry re = (RosterEntry) talkUser.getAdditionalInfo();
            theChat = chatManager.createChat(re.getUser(), ml);
        } catch(Exception e) {
            theChat = (Chat) talkUser.getAdditionalInfo();
            theChat.addMessageListener(ml);
        } // end of try .. catch block
    }

    /**
     * Overridden finalize()
     * 
     * @throws Throwable in case of error
     */
    @Override
    protected void finalize() throws Throwable {
        MessageListener ml = theChat.getListeners().iterator().next();
        theChat.removeMessageListener(ml);
    }
}
