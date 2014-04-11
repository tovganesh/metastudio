/**
 * ProxyInfoDialog.java
 *
 * Created on 31/07/2009
 */

package org.meta.shell.idebeans;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Proxy;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

/**
 * Get information about network proxy from the user
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class ProxyInfoDialog extends IDEOkCancelDialog {

    private JComboBox proxyType;
    private JTextField userName, host;
    private JSpinner port;
    private JPasswordField password;

    private JButton ok, cancel;

    /** Creates a new instance of ProxyInfoDialog */
    public ProxyInfoDialog(Frame owner, String title) {
        super(owner, title);
    }

    /** inits the UI components for this dialog */
    @Override
    protected void initComponents() {
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;

        gbc.gridx = gbc.gridy = 0;
        add(new JLabel("Proxy type: "), gbc);
        proxyType = new JComboBox(Proxy.Type.values());
        gbc.gridx = 1;
        add(proxyType, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Host: "), gbc);
        host = new JTextField(20);
        gbc.gridx = 1;
        add(host, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Port: "), gbc);
        port = new JSpinner(new SpinnerNumberModel(80, 0, Short.MAX_VALUE, 1));
        gbc.gridx = 1;
        add(port, gbc);

        JLabel userNameLabel, passowordLabel;

        userNameLabel = new JLabel("User Name: ");
        userNameLabel.setDisplayedMnemonic('U');
        gbc.gridx = 0; gbc.gridy = 3;
        add(userNameLabel, gbc);

        userName = new JTextField(20);
        userNameLabel.setLabelFor(userName);
        gbc.gridx = 1;
        add(userName, gbc);

        passowordLabel = new JLabel("Password: ");
        passowordLabel.setDisplayedMnemonic('P');
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(passowordLabel, gbc);

        password = new JPasswordField(20);
        passowordLabel.setLabelFor(password);
        gbc.gridx = 1;
        add(password, gbc);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        ok = new JButton("Ok");
        ok.setMnemonic('O');
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentState = IDEDialogState.OK;
                setVisible(false);
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 0;
        buttonPanel.add(ok, gbc);

        cancel = new JButton("Cancel");
        cancel.setMnemonic('C');
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentState = IDEDialogState.CANCEL;
                setVisible(false);
            }
        });
        gbc.gridx = 1;
        buttonPanel.add(cancel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);
    }

    /**
     * Get the selected proxy type
     * 
     * @return the proxy type
     */
    public Proxy.Type getProxyType() {
        return (Proxy.Type) proxyType.getSelectedItem();
    }

    /**
     * Get the user named entered
     *
     * @return the user name
     */
    public String getUserName() {
        return userName.getText();
    }

    /**
     * Get the password text entered
     *
     * @return the password text
     */
    public String getPasswordText() {
        return new String(password.getPassword());
    }

    /**
     * Get the host name
     *
     * @return the host name
     */
    public String getHost() {
        return host.getText();
    }

    /**
     * Get the port
     *
     * @return the port number
     */
    public int getPort() {
        return Integer.parseInt(port.getValue().toString());
    }

    /**
     * Clear all fields
     */
    public void clearFields() {
        userName.setText("");
        password.setText("");
    }
}
