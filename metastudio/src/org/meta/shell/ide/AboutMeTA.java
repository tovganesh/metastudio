/*
 * AboutMeTA.java
 *
 * Created on June 29, 2003, 8:26 PM
 */

package org.meta.shell.ide;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.html.*;
import org.meta.common.resource.FontResource;
import org.meta.common.resource.ImageResource;
import org.meta.common.resource.MiscResource;
import org.meta.common.resource.StringResource;

/**
 * Simple class for telling info. about the IDE. 
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class AboutMeTA extends JDialog {
        
    private JPanel aboutPanel, logoPanel, detailPanel, creditsPanel;
    private JEditorPane details, credits;
    private JTabbedPane tabbedPane;    
 
    /** Creates a new instance of AboutMeTA */
    public AboutMeTA(JFrame parent) {
        super(parent, 
             "About " + StringResource.getInstance().getVersion() + " ...", 
              true);
        
        initComponents();
        
        pack();
        setResizable(false);       
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);                
        
        setVisible(true);
    }
    
    /**
     * method to initialize the components of this about box.
     */
    private void initComponents() {
        getContentPane().setLayout(new BorderLayout());
        
        logoPanel = new JPanel() {
            @Override
             public void paint(Graphics g) {
                 super.paint(g);        
                 
                 // draw the IDE splash image
                 g.drawImage(ImageResource.getInstance().getIdeSplash(), 
                             0, 0, getWidth(), getHeight(), this); 
             }
        };
        Dimension splashSize = MiscResource.getInstance()
                                               .getSplashImageDimension();
        logoPanel.setMaximumSize(splashSize);
        logoPanel.setPreferredSize(splashSize);

        aboutPanel = new JPanel(new BorderLayout());
        aboutPanel.add(logoPanel, BorderLayout.CENTER);
        aboutPanel.add(new JLabel("<html><head></head><body><b>Web:</b> " +
            "<a href=\"" + StringResource.getInstance().getMeTAStudioWebSite() +
            "\">" + StringResource.getInstance().getMeTAStudioWebSite() +
            "</a><br><b>Blog:</b> <a href=\""
              + StringResource.getInstance().getMeTAStudioBlog() +
              "\">" + StringResource.getInstance().getMeTAStudioBlog() +
            "</a></body></html>"), BorderLayout.SOUTH);
        
        // and get the details
        detailPanel = new JPanel(new BorderLayout());
        details = new JEditorPane();
        details.setEditable(false);
        details.setEditorKit(new HTMLEditorKit());
        details.setText(getDetailsString());
        details.setCaretPosition(0);
        detailPanel.add(new JScrollPane(details, 
                                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
                                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
        detailPanel.setMaximumSize(splashSize);
        detailPanel.setPreferredSize(splashSize);
        
        // and get the credits
        creditsPanel = new JPanel(new BorderLayout());
        credits = new JEditorPane();
        credits.setEditable(false);
        credits.setEditorKit(new HTMLEditorKit());
        credits.setText(getCreditsString());
        credits.setCaretPosition(0);
        creditsPanel.add(new JScrollPane(credits, 
                                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
                                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
        creditsPanel.setMaximumSize(splashSize);
        creditsPanel.setPreferredSize(splashSize);
        
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(FontResource.getInstance().getFrameFont());
        tabbedPane.addTab("About", aboutPanel);
        tabbedPane.addTab("Details", detailPanel);
        tabbedPane.addTab("Credits", creditsPanel);
        
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
        
        // and add the accelerator
        ActionListener actionCancel = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                setVisible(false);
                dispose();
            }
        };
                
        KeyStroke keyStrokeCancel = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 
                                                           0);
        tabbedPane.registerKeyboardAction(actionCancel, "cancel", 
                                    keyStrokeCancel, 
                                    JComponent.WHEN_IN_FOCUSED_WINDOW);
    }        
    
    /**
     * method to generate string of details of the IDE
     */
    private String getDetailsString() {
        StringBuffer sb = new StringBuffer();
        
        sb.append("<html><body>");
        sb.append("<center><h3><u>MeTA Studio v2.0</u></h3></center>");
        sb.append("<table border=0>");
        
        sb.append("<tr><td><b>Product Version:</b></td>");
        sb.append("<td>" + StringResource.getInstance().getVersion());
        sb.append("</td></tr>");
        
        sb.append("<tr><td><b>Operating System:</b></td>");
        sb.append("<td>" + System.getProperty("os.name") + " " 
                         + System.getProperty("os.version"));
        sb.append("</td></tr>");
        
        sb.append("<tr><td><b>Architecture:</b></td>");
        sb.append("<td>" + System.getProperty("os.arch"));
        sb.append("</td></tr>");
        
        sb.append("<tr><td><b>Java:</b></td>");
        sb.append("<td>" + System.getProperty("java.version"));
        sb.append("</td></tr>");
        
        sb.append("</table>");
        sb.append("</body></html>");
        
        return sb.toString();
    }
    
    /**
     * method to generate string of details of the IDE
     */
    private String getCreditsString() {
        StringBuffer sb = new StringBuffer();
        
        sb.append("<html><body>");
        sb.append("MeTA Studio uses the following LGPL libraries and codes:");
        sb.append("<ol>");
        sb.append("<li>BeanShell</li>");
        sb.append("<li>Jython</li>");
        sb.append("<li>JRMan</li>");
        sb.append("<li>Kunststoff UI library</li>");
        sb.append("<li>JEditSyntax</li>");
        sb.append("<li>Jmol</li>");
        sb.append("<li>Jogg and Jlayer</li>");
        sb.append("<li>JScheme</li>");
        sb.append("<li>JFreeChart</li>");
        sb.append("<li>Smack</li>");
        sb.append("<li>Apache commons</li>");
        sb.append("</ol>");
        sb.append("And the developers at: http://code.google.com/p/metastudio/people/list");
        sb.append("</body></html>");
        
        return sb.toString();
    }
        
} // end of class AboutMeTA
