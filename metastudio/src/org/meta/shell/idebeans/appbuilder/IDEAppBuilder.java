/**
 * IDEAppBuilder.java
 *
 * Created on 01/09/2009
 */

package org.meta.shell.idebeans.appbuilder;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import org.meta.shell.idebeans.FindTextPanel;
import org.meta.shell.idebeans.IDEVerticalFlowLayout;
import org.syntax.jedit.JEditTextArea;
import org.syntax.jedit.tokenmarker.BeanShellTokenMarker;

/**
 * The App builder interface.
 * 
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class IDEAppBuilder extends JPanel {

    protected JPanel componentsPanel;
    protected JPanel propertiesPanel;
    protected JPanel designPanel, codePanel;
    protected JEditTextArea editorPane;

    /** Creates a new instance of IDEAppBuilder */
    public IDEAppBuilder() {
        initComponents();
    }

    /** init the UI */
    private void initComponents() {
        setLayout(new BorderLayout());

        componentsPanel = new JPanel(new IDEVerticalFlowLayout());
        componentsPanel.add(new JLabel("Components"));
        add(componentsPanel, BorderLayout.WEST);

        JTabbedPane appPanel = new JTabbedPane();

            designPanel = new JPanel();
            appPanel.addTab("Design", designPanel);

            codePanel = new JPanel(new BorderLayout());
                editorPane = new JEditTextArea();
                editorPane.setTokenMarker(new BeanShellTokenMarker());
                codePanel.add(editorPane, BorderLayout.CENTER);
                codePanel.add(new FindTextPanel(editorPane), BorderLayout.NORTH);
            appPanel.addTab("Code", codePanel);

        appPanel.setBorder(BorderFactory.createLineBorder(Color.blue));
        add(appPanel, BorderLayout.CENTER);

        propertiesPanel = new JPanel(new IDEVerticalFlowLayout());
        propertiesPanel.add(new JLabel("Properties"));
        add(propertiesPanel, BorderLayout.EAST);
    }
}
