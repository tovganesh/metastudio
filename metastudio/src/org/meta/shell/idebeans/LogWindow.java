/*
 * LogWindow.java
 *
 * Created on June 22, 2003, 8:39 PM
 */

package org.meta.shell.idebeans;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.*;


import javax.swing.*;
import org.meta.common.resource.FontResource;
import org.meta.common.resource.ImageResource;
import org.meta.common.resource.StringResource;

/**
 * Define the log window for the IDE. 
 * This class heavily relies on the services of java.util.logging package.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class LogWindow extends JInternalFrame {
    
    /**
     * the UI elements
     */
    private JTextArea workSpaceLogArea;
    private JTextArea runtimeLogArea;
    private JScrollPane workSpaceScrollPane, runtimeScrollPane;
    private JTabbedPane logTabs;
    private JPopupMenu logUIMenu;
    private JMenuItem copy, clearAll, selectAll;    
    
    /** Holds value of property logger. */
    private Logger logger;
    
    /** Holds value of property updateInterval. */
    private int updateInterval;
    
    /** Creates a new instance of LogWindow */
    public LogWindow() {          
        super("Log Window");
        updateInterval = 100;
        
        setFrameIcon(ImageResource.getInstance().getLogWindow());
        
        // initilize the UI
        initComponents();  
        
        // and then init the logger 
        logger = Logger.getLogger(StringResource.getInstance().
                                                 getIdeLoggerName());                
        logger.addHandler(new TextAreaLogHandler(workSpaceLogArea));  
        
        try { captureStdStreams(); } catch (Exception e) { 
            runtimeLogArea.append("\nError in redirection thread.");
            runtimeLogArea.append("\nException is : " + e.toString());
        } // end try .. catch     
    }
    
    /** This method is called from within the constructor to
     * initialize the UI. 
     */
    private void initComponents() {
        getContentPane().setFont(FontResource.getInstance().getFrameFont());       
        
        getContentPane().setLayout(new BorderLayout());
        
        logTabs = new JTabbedPane(JTabbedPane.BOTTOM);
        
        workSpaceLogArea    = new JTextArea();
        workSpaceLogArea.setEditable(false);
        workSpaceScrollPane = new JScrollPane(workSpaceLogArea, 
                                   JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                                   JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);        
        
        runtimeLogArea    = new JTextArea();
        runtimeLogArea.setEditable(false);
        runtimeScrollPane = new JScrollPane(runtimeLogArea, 
                                   JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                                   JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        logTabs.setFont(FontResource.getInstance().getFrameFont());
        logTabs.addTab("Workspace Log", workSpaceScrollPane);
        logTabs.addTab("Runtime Log", runtimeScrollPane);
        
        getContentPane().add(logTabs);
        
        // then construct the popup menu ...
        ImageResource images = ImageResource.getInstance();
        
        logUIMenu = new JPopupMenu("LogWindowPopup");    
        copy = new JMenuItem("Copy", images.getCopy());
        copy.setMnemonic('C');
        copy.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK));
        copy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int selectedTab = logTabs.getSelectedIndex();
                
                if (logTabs.getTitleAt(selectedTab).equals("Workspace Log")) {
                    workSpaceLogArea.copy();                    
                } else {  // "Runtime Log"
                    runtimeLogArea.copy();
                } // end if
            }
        });
        logUIMenu.add(copy);
        
        selectAll = new JMenuItem("Select All");
        selectAll.setMnemonic('A');
        selectAll.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_MASK));
        selectAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int selectedTab = logTabs.getSelectedIndex();
                
                if (logTabs.getTitleAt(selectedTab).equals("Workspace Log")) {
                    workSpaceLogArea.setCaretPosition(0);
                    workSpaceLogArea.moveCaretPosition(
                                         workSpaceLogArea.getText().length());
                } else {  // "Runtime Log"
                    runtimeLogArea.setCaretPosition(0);
                    runtimeLogArea.moveCaretPosition(
                                         runtimeLogArea.getText().length());               
                } // end if
            }
        });
        logUIMenu.add(selectAll);
        
        logUIMenu.addSeparator();
        
        clearAll = new JMenuItem("Clear All");
        clearAll.setMnemonic('l');
        clearAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                clearLog();
            }
        });
        logUIMenu.add(clearAll);
        
        // add the event listeners for the pop up  
        MouseAdapter eventHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopUp(e);                    
                } // end if 
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopUp(e);
                } // end if 
            }
            
            private void showPopUp(MouseEvent e) {
                // check enabling / disabling any menues
                int selectedTab = logTabs.getSelectedIndex();
                
                if (selectedTab == -1) return;
                
                if (logTabs.getTitleAt(selectedTab).equals("Workspace Log")) {
                    copy.setEnabled(workSpaceLogArea.getSelectedText() != null);
                } else {  // "Runtime Log"
                    copy.setEnabled(runtimeLogArea.getSelectedText() != null);
                } // end if                                 
                
                logUIMenu.show((Component) e.getSource(), e.getX(), e.getY());
            }
        }; // end MouseAdapter                
        
        workSpaceLogArea.addMouseListener(eventHandler);
        runtimeLogArea.addMouseListener(eventHandler);        
    }        
    
    /**
     * method to capture System.out and System.err streams into the text area
     * of runtimeLogArea.
     */
    private void captureStdStreams() throws IOException {
        System.setOut(new PrintStream(new RuntimeLogOutputStream()));
        System.setErr(new PrintStream(new RuntimeLogOutputStream()));
    }
    
    /** 
     * Getter for property logger.
     * @return Value of property logger.     
     */
    public Logger getLogger() {
        return this.logger;
    }
    
    /**
     * method to append an information log.
     *
     * @param logEntry the log string
     */
    public void appendInfo(String logEntry) {
        LogRecord logRecord = new LogRecord(Level.INFO, logEntry);
        
        tryToSetLogCaller(logRecord);
        
        logger.log(logRecord);

        workSpaceLogArea.setCaretPosition(workSpaceLogArea.getText().length());
    }
    
    /**
     * method to append an warning log.
     *
     * @param logEntry the log string
     */
    public void appendWarning(String logEntry) {
        LogRecord logRecord = new LogRecord(Level.WARNING, logEntry);
        
        tryToSetLogCaller(logRecord);
        
        logger.log(logRecord);

        workSpaceLogArea.setCaretPosition(workSpaceLogArea.getText().length());
    }
    
    /**
     * method to append an error log.
     *
     * @param logEntry the log string
     */
    public void appendError(String logEntry) {
        LogRecord logRecord = new LogRecord(Level.SEVERE, logEntry);
        
        tryToSetLogCaller(logRecord);
        
        logger.log(logRecord);

        workSpaceLogArea.setCaretPosition(workSpaceLogArea.getText().length());
    }
    
    /**
     * method to clear all the logs
     */
    public void clearLog() {
        workSpaceLogArea.setText("");
        runtimeLogArea.setText("");       
    }
    
    /**
     * private method to set the LogRecord object with the best
     * known caller!
     * the success of this method is not guaranteed! if not successful
     * most of the times nothing special happens in extreme cases an
     * relevant runtime exception will be thrown.
     *
     * @param logRecord object of LogRecord that needs to be modified
     */
    private synchronized void tryToSetLogCaller(LogRecord logRecord) {	
	// first get the stack trace.
	StackTraceElement stack[] = (new Throwable()).getStackTrace();
        String loggerClassName = StringResource.getInstance().getLoggerClass();
       
	// then start searching back to a method 
        // that refers to the Logger class
	int i = 0;
	while (i < stack.length) {
	    StackTraceElement theFrame = stack[i];
	    String className = theFrame.getClassName();
	    if (className.equals(loggerClassName)) {
		break;
	    } // end if
	    i++;
	} // end while
                
	// then try to search for the first frame before 
        // the Logger class, which is *most probably* the caller
        // i don't know if that is always true
	while (i < stack.length) {
	    StackTraceElement theFrame = stack[i];
	    String className = theFrame.getClassName();
	    if (!className.equals(loggerClassName)) {
		// it seems we are bit lucky, but not sure 
                // it that is correct!                
	        logRecord.setSourceClassName(className);
	        logRecord.setSourceMethodName(theFrame.getMethodName());
		return;
	    }
	    i++;
	}	
    }
    
    /**
     * finalizer!
     */
    @Override
    public void finalize() throws Throwable {
        super.finalize();        
    }
    
    /** Getter for property updateInterval.
     * @return Value of property updateInterval.
     *
     */
    public int getUpdateInterval() {
        return this.updateInterval;
    }
    
    /** Setter for property updateInterval.
     * @param updateInterval New value of property updateInterval.
     *
     */
    public void setUpdateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
    }

    /** inner class for output redirection */
    public class RuntimeLogOutputStream extends OutputStream {

        public RuntimeLogOutputStream() {
            super();            
        }

        @Override
        public void write(int i) {
            runtimeLogArea.append(Character.toString((char) i));
            runtimeLogArea.setCaretPosition(runtimeLogArea.getText().length());
        }

        @Override
        public void write(byte b[]) throws IOException {
            write(b, 0, b.length);
        }
        
        @Override
        public void write(byte[] buf, int off, int len) {                        
            String s = new String(buf, off, len);
            runtimeLogArea.append(s);
            runtimeLogArea.setCaretPosition(runtimeLogArea.getText().length());
        }

        @Override
        public void flush() throws IOException {
            runtimeLogArea.setCaretPosition(runtimeLogArea.getText().length());
        }
    }

} // end of clas LogWindow
