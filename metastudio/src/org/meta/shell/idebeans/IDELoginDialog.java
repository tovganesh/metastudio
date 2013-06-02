/**
 * IDELoginDialog.java
 *
 * Created on Jun 30, 2009
 */
package org.meta.shell.idebeans;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * A generic login dialog, allowing a user to enter username and password.
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class IDELoginDialog extends IDEOkCancelDialog {

    private JTextField userName;
    private JPasswordField password;

    private JButton ok, cancel;
    
    public IDELoginDialog(Frame owner, String title) {
        super(owner, title);
    }

    /**
     * Initilize the components
     */
    @Override
    protected void initComponents() {
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;

        JLabel userNameLabel, passowordLabel;

        userNameLabel = new JLabel("User Name: ");
        userNameLabel.setDisplayedMnemonic('U');
        gbc.gridx = gbc.gridy = 0;
        add(userNameLabel, gbc);

        userName = new JTextField(20);
        userNameLabel.setLabelFor(userName);
        gbc.gridx = 1;
        add(userName, gbc);

        passowordLabel = new JLabel("Password: ");
        passowordLabel.setDisplayedMnemonic('P');
        gbc.gridx = 0;
        gbc.gridy = 1;
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
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);
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
     * Clear all fields
     */
    public void clearFields() {
        userName.setText("");
        password.setText("");
    }
}
