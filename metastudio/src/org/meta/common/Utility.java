/*
 * Utility.java
 *
 * Created on June 1, 2003, 7:30 PM
 */

package org.meta.common;

import java.io.*;
import java.util.*;

import java.awt.Color;

import javax.xml.parsers.*;
import org.meta.moleculereader.MoleculeFileReader;
import org.meta.moleculereader.MoleculeFileReaderFactory;

import org.xml.sax.*;
import org.w3c.dom.*;

import org.meta.config.impl.DefaultConfiguration;

import bsh.Interpreter;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import javax.swing.tree.DefaultMutableTreeNode;
import org.meta.common.resource.StringResource;
import org.meta.config.GlobalConfiguration;
import org.meta.molecule.Molecule;
import org.meta.molecule.MoleculeBuilder;
import org.meta.scripting.ScriptEngine;
import org.meta.scripting.ScriptInterpreter;

/**
 * Utility class providing basic functionality such as ID generation etc. 
 * The class it self is declared as final, and all the methods 
 * exposed are static.
 *
 * @author  V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public final class Utility {
      
    /**
     * 1 a.u. = 0.52917724924 angstroms
     * When converting from a.u. to angstroms, multiply by this factor.
     * When converting from angstroms to a.u., divide by this factor.
     */
    public static final double AU_TO_ANGSTROM_FACTOR = 0.52917724924;

    /**
     * 1 kcal/mol = 0.00159362 AU.
     */
    public static final double KCAL_TO_AU_FACTOR = 0.00159362;
    
    /**
     * The angstrom symbol - unicode character
     */
    public static final char ANGSTROM_SYMBOL = '\u00c5';
    
    /**
     * The angstrom symbol - HTML code
     */
    public static final String ANGSTROM_HTML_CODE = "&#197;";
    
    /**
     * the degree symbol - unicode character
     */
    public static final char DEGREE_SYMBOL = '\u00b0';
    
    /**
     * the down arrow symbol - unicode character
     */
    public static final char DOWN_ARROW_SYMBOL = '\u25bc';    
    
    /**
     * private constructor ... avoid object creation
     */
    private Utility() { 
    }
    
    /**
     * Method to generate a workspace ID, no uniquness is guaranteed.
     * <pre>
     * Generated as:
     *    ID = "WS" + Date string + Current time in milli seconds
     * </pre>
     *
     * @return a string representing the ID    
     */
    public static synchronized String generateWorkspaceID() {          
        return ("WS" + (new Date()).toString() + System.currentTimeMillis());
    }
    
    /**
     * Method to generate a workspace item ID, no uniquness is guaranteed.
     * <pre>
     * Generated as:
     *    ID = "WI" + Date string + Current time in milli seconds
     * </pre>
     *
     * @return a string representing the ID    
     */
    public static synchronized String generateWorkspaceItemID() {
        return ("WI" + (new Date()).toString() + System.currentTimeMillis());
    }
    
    /**
     * Returns the default implementation of an MeTA API interface as
     * defined by <code> StringResource.getDefaultImplResource() </code> class.
     * 
     * @return an instance of class which impliments the given interface.
     * @throws may throw java.lang.ClassNotFoundException                   
     *                   java.lang.ClassCastException
     */
    public static synchronized Class getDefaultImplFor(Class theClass) 
                                                throws ClassNotFoundException {
        GlobalConfiguration glbCfg = DefaultConfiguration.getInstance();
        
        return Class.forName((String) glbCfg.getParameter(
                                         theClass.getName()).getValue());
    }

    /**
     * Get new instance of a molecule object using default implementation
     * 
     * @return the instance of Molecule object, a null if this method failed
     *         to do so
     */
    public static synchronized Molecule createMoleculeObject() {
        try {
            return (Molecule) getDefaultImplFor(Molecule.class).newInstance();
        } catch(Exception ignored) { return null; }
    }

    /**
     * Get new instance of a molecule builder object using default implementation
     *
     * @return the instance of Molecule object, a null if this method failed
     *         to do so
     */
    public static synchronized MoleculeBuilder createMoleculeBuilderObject() {
        try {
            return (MoleculeBuilder)
                      getDefaultImplFor(MoleculeBuilder.class).newInstance();
        } catch(Exception ignored) { return null; }
    }
    
    /**
     * Method to capitalise a string
     * 
     * @param theString the string to be capitalised
     * @return the string which is capitalised
     * @throws may throw NullPointerException, ArrayIndexOutOfBoundsException
     */
    public static String capitalise(String theString) {
        String firstChar = theString.substring(0, 1);
        String restString = theString.substring(1, theString.length());
        
        if (restString == null) return firstChar.toUpperCase();
        else return (firstChar.toUpperCase() + restString);
    }
    
    /**
     * A simple utility method to parse XML using a default DOM parser
     * and return the results as abstract instance of Document class.
     * An helper utility for <code>workspace</code> subsystem.
     *
     * @param xmlFile : fully qualified name of the XML file to be parsed.
     * @return Document : parsed XML document if no error occurs.
     *
     * @throws ParserConfigurationException / SAXException in case of error
     *         reading xml file or creating the parser.
     *         An IOException in case the XML file is not present!
     */
    public static Document parseXML(String xmlFile) 
               throws ParserConfigurationException, SAXException, IOException {
         return parseXML(new FileInputStream(xmlFile));
    } 
    
    /**
     * A simple utility method to parse XML using a default DOM parser
     * and return the results as abstract instance of Document class.
     * An helper utility for <code>workspace</code> subsystem.
     *
     * @param xmlStream : a stream connecting to XML data.-
     * @return Document : parsed XML document if no error occurs.
     *
     * @throws ParserConfigurationException / SAXException in case of error
     *         reading xml file or creating the parser.
     *         An IOException in case the XML file is not present!
     */
    public static Document parseXML(InputStream xmlStream) 
               throws ParserConfigurationException, SAXException, IOException {
        // we use the JAXP DOM parser for the job!
        // get an instance of the parser
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        
        // set the error handler .. to detect errors(?) in xml
        // in normal situations, error should never occur because
        // the xml files parsed here are automatically generated ones
        db.setErrorHandler(new ErrorHandler() {
            @Override
            public void warning(SAXParseException e) throws SAXException {
                System.err.println("Warning : " + e);
                e.printStackTrace();                
            }
            
            @Override
            public void error(SAXParseException e) throws SAXException {
                System.err.println("Error : " + e);
                e.printStackTrace();                
            }
            
            @Override
            public void fatalError(SAXParseException e) throws SAXException {
                System.err.println("Error : " + e);
                e.printStackTrace();               
            }
        }); // setErrorHandler
        
        // and now, parse the input file:
        return db.parse(xmlStream);
    }
    
    /**
     * method to get an integer out of Hashtable. If the key is not present,
     * then zero is returned
     *
     * @param table - the hashtable containing Integer objects
     * @param key - the key
     * @return int - the int value intended. if the key is not found in the 
     *  table a zero is returned instead.
     */
    public static int getInteger(Hashtable table, Object key) {
        int value = 0;
        
        if (table.containsKey(key)) {
            value = ((Integer) table.get(key)).intValue();
        } // end if
        
        return value;
    }
    
    /**
     * method to get an double out of Hashtable. If the key is not present,
     * then zero is returned
     *
     * @param table - the hashtable containing Double objects
     * @param key - the key
     * @return double - the double value intended. if the key is not found 
     * in the table a zero is returned instead.
     */
    public static double getDouble(Hashtable table, Object key) {
        double value = 0.0;
        
        if (table.containsKey(key)) {
            value = ((Double) table.get(key)).doubleValue();
        } // end if
        
        return value;
    }
    
    /**
     * method to get an String out of Hashtable. If the key is not present,
     * then a blank string is returned
     *
     * @param table - the hashtable containing Double objects
     * @param key - the key
     * @return String - the String value intended. if the key is not found 
     * in the table a blank String is returned instead.
     */
    public static String getString(Hashtable table, Object key) {
        String value = " ";
        
        if (table.containsKey(key)) {
            value = (String) table.get(key);
        } // end if
        
        return value;
    }
    
    /**
     * method to get an Color out of Hashtable. If the key is not present,
     * then a Color(1.0, 1.0, 1.0, 1.0) :: white is returned
     *
     * @param table - the hashtable containing Double objects
     * @param key - the key
     * @return Color - the Color value intended. if the key is not found 
     * in the table a Color(1.0, 1.0, 1.0, 1.0) :: white is returned instead.
     */
    public static Color getColor(Hashtable table, Object key) {
        Color value = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        
        if (table.containsKey(key)) {
            value = (Color) table.get(key);
        } // end if
        
        return value;
    }
    
    /**
     * method to check if the provided file type is supported by the 
     * MeTA Studio.
     *
     * @param fileName - the name of the file whos type needs to be tested
     * @return boolean true/ false indicating the support by the MeTA Studio
     */
    public static boolean isSupportedType(String fileName) {
        // right now we only check for the files of the type molecule
        try {
           MoleculeFileReaderFactory mfr = 
              (MoleculeFileReaderFactory) (Utility.getDefaultImplFor(
                               MoleculeFileReaderFactory.class).newInstance());
           
           String type = fileName.substring(fileName.lastIndexOf('.')+1,
                                            fileName.length());
           type = type.toLowerCase();
           
           Iterator itr = mfr.getAllSupportedTypes();
           
           while(itr.hasNext()) {
               if (itr.next().equals(type)) return true;
           } // end while
           
           return false;
        } catch (Exception e) {
            System.err.println("Error in Utility.isSupportedType() :" + e);
            e.printStackTrace();
            return false;
        } // end of try .. catch block
    }
    
    /**
     * method to get an appropriate reader for the provided file type.
     * ... only for moleule file types.
     *
     * @param fileName - the name of the file whos type needs to be tested
     * @return MoleculeFileReader - the reader
     * @throws UnsupportedOperationException if an error occurs
     */
    public static MoleculeFileReader getReaderFor(String fileName) 
                                     throws UnsupportedOperationException {
        // right now we only check for the files of the type molecule
        try {
           MoleculeFileReaderFactory mfr = 
              (MoleculeFileReaderFactory) (Utility.getDefaultImplFor(
                               MoleculeFileReaderFactory.class).newInstance());
           
           String type = fileName.substring(fileName.lastIndexOf('.')+1,
                                            fileName.length());                      
           
           return mfr.getReader(type.toLowerCase());
        } catch (Exception e) {
            System.err.println("Error in Utility.getReaderFor() :" + e);
            e.printStackTrace();
            throw new UnsupportedOperationException(
                                 "Error in Utility.getReaderFor() :" + e);
        } // end of try .. catch block
    }
    
    /**
     * Get the file name sans extension
     *
     * @param file the File object representing the file
     * @return the names of the file sans extension
     */
    public static String getFileNameSansExtension(File file) {
        String name = file.getName();
        
        name = name.substring(0, name.lastIndexOf('.'));
        
        return name;
    }
    
    /**
     * Get the file extension
     *
     * @param file the File object representing the file
     * @return the extension
     */
    public static String getFileExtension(File file) {
        String name = file.getName();
        
        name = name.substring(name.lastIndexOf('.')+1, name.length());
        
        return name;
    }
    
    /**
     * solely written for the scripts!
     *
     * @param fw - the filewriter object
     * @param lin - the line to be written to the file
     * @throws Exception - a general IOException, in case write fails
     */
    public static void dumpALine(FileWriter fw, String lin) throws IOException {
        fw.write(lin);
    } 
    
    /**
     * solely written for the scripts!
     *
     * @param bfw - the buffered filewriter object
     * @param lin - the line to be written to the file
     * @throws Exception - a general IOException, in case write fails
     */
    public static void dumpALine(BufferedWriter bfw, String lin)
                                                    throws IOException {
        bfw.write(lin);
    } 

    /**
     * Execute a script line using a new instance of appropriate interpreter
     * object
     *
     * @param scriptFileName the script file name
     * @throws Exception if the execution was unsuccessful
     */
    public static void executeScriptFile(String scriptFileName) throws Exception {
        File scriptFile = new File(scriptFileName);

        ScriptEngine scriptEngine = ScriptInterpreter.getInstance()
                                       .findProbableScriptEngineFor(scriptFile);

        scriptEngine.init();
        scriptEngine.loadBuiltInModules();
        scriptEngine.execute(scriptFile);
    }

    /** Interpreter object to be used through out the studio */
    private static Interpreter interpreter;
    
    /**
     * Execute a BeanShell script line using a new instance of the BeanShell
     * interpreter object.
     *
     * @param scriptLine the BeanShell script / command to be executed
     * @throws Exception if the execution was unsuccessful
     */
    public static void executeBeanShellScript(String scriptLine) 
                                              throws Exception {
        if (interpreter == null) {
            interpreter = new Interpreter();        
            interpreter.eval(
                          "importCommands(\"org.meta.commands\")");
        } // end if
        
        interpreter.eval(scriptLine);
    }
    
    /**
     * Execute a BeanShell script file using a new instance of the BeanShell
     * interpreter object.
     *
     * @param scriptFileName the BeanShell script file name
     * @throws Exception if the execution was unsuccessful
     */
    public static void executeBeanShellScriptFile(String scriptFileName) 
                                                      throws Exception {
        if (interpreter == null) {
            interpreter = new Interpreter();        
            interpreter.eval("importCommands(\"org.meta.commands\")");
        } // end if
        
        interpreter.eval("source(\"" + scriptFileName + "\");");
    }
    
    /**
     * Return a new instance of BeanShell interpreter as appropriate
     * 
     * @return the interpreter instance
     * @throws an exception if unable to create a new instance of interpreter
     *         and initialize it appropriately
     */
    public static Interpreter createNewBeanShellInterpreter() throws Exception {
        Interpreter intrp = new Interpreter();
        intrp.eval("importCommands(\"org.meta.commands\")");

        return intrp;
    }

    /**
     * Searches for immediate parent tree node for a node with a target
     * class instances and returns that node.
     *
     * @param node instance of DefaultMutableTreeNode to start with
     * @param toFind instance of class to be found in the parent nodes
     * @return the DefaultMutableTreeNode containing the class instance
     */
    public static DefaultMutableTreeNode findImmediateParentNode(
                                    DefaultMutableTreeNode node, Class toFind) {
        if (toFind.isInstance(node.getUserObject())) return node;
        else return findImmediateParentNode(
                        (DefaultMutableTreeNode) node.getParent(), toFind);
    }
    
    
    /**
     * Pass a sequence expression string like 1-10 to get a list of Strings
     * with the required number.
     *
     * @param seqExpression expression like 1-10 or 1,2,3-5.
     * @param prependZero prependZero infront of each number string
     * @param numPrependZero number of zeros to prepend
     * @return an array of number string according to the expression provided.
     */
    public static ArrayList<String> getFileSequenceSuffix(String seqExpression,
                                    boolean prependZero, int numPrependZero) {
        ArrayList<String> numStr = new ArrayList<String>();
        
        String [] commaSepStr = seqExpression.split(",");
        
        for(int i=0; i<commaSepStr.length; i++) {
            if (commaSepStr[i].indexOf("-") >= 0) {
                String [] hypSepStr = commaSepStr[i].split("-");
                
                try {
                    int low = Integer.parseInt(hypSepStr[0].trim());
                    int hig = Integer.parseInt(hypSepStr[1].trim());
                    
                    if (low > hig) {
                        int tmp = hig; hig = low;  low = hig;
                    } // end if
                    
                    for(int j=low; j<=hig; j++) {
                        if (prependZero)
                            numStr.add(zeroFill(j + "", numPrependZero));
                        else
                            numStr.add(j + "");
                    } // end for
                } catch (Exception e) {
                    throw new UnsupportedOperationException(
                            "Invalid sequence expression: " + seqExpression);
                } // end of try .. catch block
            } else {
                try {
                    if (prependZero)
                       numStr.add(zeroFill(
                                   (Integer.parseInt(commaSepStr[i].trim()))+"",
                                   numPrependZero));    
                    else
                       numStr.add((Integer.parseInt(commaSepStr[i].trim()))+"");    
                } catch (Exception e) {
                    throw new UnsupportedOperationException(
                            "Invalid sequence expression: " + seqExpression);
                } // end of try .. catch block
            } // end if
        } // end for
        
        return numStr;
    }
    
    /**
     * Fills zeros at the beginning of the string that is not numPrependZero 
     * wide.
     *
     * @param str the string under consideration
     * @param numPrependZero number of zeros to prepend
     * @return the prepended string
     */
    public static String zeroFill(String str, int numPrependZero) {
        if (str.length() >= numPrependZero) return str;
        
        String zeroStr = "";
        for(int i=0; i<(numPrependZero-str.length()); i++) zeroStr += "0";
        
        return zeroStr + str;
    }
    
    /**
     * Print an integer array to standard output
     * 
     * @param arr the array
     * @param len the lenght of array to print
     */
    public static void printArray(int [] arr, int len) {
        for(int i=0; i<=len; i++) System.out.print(arr[i] + ", ");
        System.out.println("");
    }        
    
    /**
     * save color map to file
     *
     * @param cm the color map instance to be written to a file
     * @param file the file to which the color map is written
     */
    public static void saveColorMapToFile(ColorToFunctionRangeMap cm,
                                          File file) throws IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                                                new FileOutputStream(file)));
        for(Color color : cm.getColorRange()) {
           bw.write(color.getRed() + " " + color.getGreen() 
                    + " " + color.getBlue() + "\n"); 
        } // end for
        
        bw.close();
    }
    
    /**
     * read and update a color map to file
     *
     * @param cm the color map instance updated
     * @param file the file from which the color map is read from
     */
    public static void updateColorMapFromFile(ColorToFunctionRangeMap cm,
                                              File file) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(
                                                new FileInputStream(file)));
        ArrayList<Color> colorList = new ArrayList<Color>();
        
        while(true) {
           String line = br.readLine();
           
           if (line == null || line.equals("")) break;
           
           String [] words = line.split("\\s+");
           
           Color color = new Color(Integer.parseInt(words[0].trim()),
                                   Integer.parseInt(words[1].trim()),
                                   Integer.parseInt(words[2].trim()));
           
           colorList.add(color);
        } // end while
        
        cm.setColorRange(colorList);
        br.close();
    }
    
    /**
     * method to read all the lines in the reader
     * 
     * @param reader the reader object
     * @return the lines in a ArrayList
     */
    public static ArrayList<String> readLines(BufferedReader reader) 
                                                  throws IOException {
        ArrayList<String> lines = new ArrayList<String>();
        String line;
                
        while(true) {
            line = reader.readLine();
            
            if (line == null) break;
            
            lines.add(line);
        } // end while
        
        return lines;
    }
    
    /**
     * method to read all the lines in the reader
     * 
     * @param url the URL of the file
     * @return the lines in a ArrayList
     */
    public static ArrayList<String> readLines(URL url) throws IOException {
        URLConnection conn = url.openConnection(); 
                
        return readLines(new BufferedReader(new InputStreamReader(
                                                     conn.getInputStream())));
    }
    
    /**
     * initiate a stream copy from input stream to the file output stream
     * 
     * @param istream the input stream
     * @param fos the file output stream
     * @throws java.lang.IOException thrown if we land up with some error
     */
    public static void streamCopy(InputStream istream, FileOutputStream fos) 
                                  throws IOException {
        byte [] readBytes = new byte[1024];
        int nBytes;

        while(true) {
            nBytes = istream.read(readBytes, 0, readBytes.length);                
            if (nBytes == -1 || nBytes == 0) break;
            fos.write(readBytes, 0, nBytes);                                
        } // end while
    }
    
    /**
     * nitiate a stream copy from file input stream to the output stream
     * 
     * @param fis the file input stream
     * @param ostream the output stream  
     * @throws java.io.IOException thrown if we land up with some error
     */
    public static void streamCopy(FileInputStream fis, OutputStream ostream) 
                                  throws IOException {
        byte [] readBytes = new byte[1024];
        int nBytes;

        while(true) {
            nBytes = fis.read(readBytes, 0, readBytes.length); 
            if (nBytes == -1 || nBytes == 0) break;
            ostream.write(readBytes, 0, nBytes);
            ostream.flush();                
        } // end while
    }        

    /**
     * This is intended to add external libraries to the class path of currently
     * running JVM. Its implementation as of now is a "hack" and is truely not
     * the best way to do things. Essentially this hack breaks the object
     * oriented principles by making a method accessible by using Java's
     * reflection APIs. 
     * 
     * TODO: This implementation should be appropriately changed, once a 
     * better working solution is found.
     *
     * @param more a list of more arguments to be added to the class path
     * @throws java.lang.Exception well if anything goes wrong!
     */
    public static void appendExtLibClassPaths(String... more) throws Exception {
         // init places...
        File extLibsFile 
                    = new File(StringResource.getInstance().getExtLibFile());
        if (!extLibsFile.exists()) return;
        
        ArrayList<String> extLibs 
                    = Utility.readLines(extLibsFile.toURI().toURL());
        String extLibsDir = StringResource.getInstance().getExtLibDir();
        
        // get current classloaded instance
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
                
        // the following condition is not entirely necessary but
        // a required guard
        if (systemClassLoader instanceof URLClassLoader) {
          // use reflection API to get hold of "addURL" method
          Method addURLMethod = URLClassLoader.class.getDeclaredMethod(
                                      "addURL", new Class[] {URL.class});
          
          // "addURL" is actually a protected method, 
          // according to URLClassLoader; make it publicly accessible for
          // the moment, so that we can call it!
          addURLMethod.setAccessible(true);
          
          for(String extLib : extLibs) {
            // check if the file exists, only then add it
            File extLibFile = new File(extLibsDir + File.separatorChar
                                       + extLib);
            
            if (extLibFile.exists()) {
                // invoke the method to add the external library
                addURLMethod.invoke(systemClassLoader, 
                                    new Object[] {extLibFile.toURI().toURL()});
            } // end if
          } // end for

          // and then add any additional libs
          for(String extLib : more) {
            // check if the file exists, only then add it
            File extLibFile = new File(extLib);

            if (extLibFile.exists()) {
                // invoke the method to add the external library
                addURLMethod.invoke(systemClassLoader,
                                    new Object[] {extLibFile.toURI().toURL()});
            } // end if
          } // end for
        } // end if
    }

    /**
     * Return the OS string as is used in directory structure of MeTA Studio
     *
     * @return a string indicating current OS
     */
    public static String getOsString() {
        String osString = "";
        String actualOStr = System.getProperty("os.name").toLowerCase();

        if (actualOStr.indexOf("solaris") >= 0) {
            osString = "solaris" + File.separatorChar;
        } else if (actualOStr.indexOf("linux") >= 0) {
            osString = "linux" + File.separatorChar;
        } else if (actualOStr.indexOf("windows") >= 0) {
            osString = "windows" + File.separatorChar;
        } else if (actualOStr.indexOf("mac") >= 0) {
            osString = "macosx" + File.separatorChar;
        } // end if

        return osString;
    }

    /**
     * Get the path string of platform libraries, including Java library paths
     *
     * @return the platform libraries path string
     */
    public static String getPlatformLibraryPaths() {
        try {
            String osString = Utility.getOsString();

            return (System.getProperty("java.library.path")
                    + File.pathSeparatorChar + ".."
                    + File.separatorChar + "lib" + File.separatorChar
                    + osString + System.getProperty("os.arch"));
        } catch (Exception ignored) { return null; }
    }

    /**
     * Same as getPlatformLibraryPaths(), excepet that it only return the
     * platform specific path for MeTA Studio, other standard Java library
     * paths are ignored.
     *
     * @return the platform library path string
     */
    public static String getPlatformSpecificLibraryPath() {
        try {
            String osString = Utility.getOsString();

            return (".." + File.separatorChar + "lib" + File.separatorChar
                    + osString + System.getProperty("os.arch"));
        } catch (Exception ignored) { return null; }
    }

    /**
     * Return the platform specific path of a library jar, as used in
     * MeTA Studio
     *
     * @param libJar the library jar file
     * @return the full platform specific class path of this library jar
     */
    public static String getPlatformClassPath(String libJar) {
        String osString = Utility.getOsString();

        String exClsPath = ".." + File.separatorChar + "lib" + File.separatorChar
                                + osString + System.getProperty("os.arch")
                                + File.separatorChar;

        return (exClsPath + libJar);
    }

    /**
     * Return an array of platform specific Jars that are available to
     * MeTA Studio runtime
     *
     * @return the list of platform specific jar files
     */
    public static String[] getPlatformSpecificJars() {
        return new String[]{"j3dcore.jar", "j3dutils.jar", "jogl.jar",
                            "gluegen-rt.jar"};
    }
    
    /**
     * Change the root, in case running MeTA as a new application. Useful to
     * preserve MeTA settings.
     * 
     * @param appName the new app name
     */
    public static void changeRoot(String appName) {
        StringResource strings = StringResource.getInstance();
        
        File appDir = new File(strings.getAppDir());
        if (!appDir.exists()) appDir.mkdir();
        
        appDir = new File(strings.getRemoteAppDir());
        if (!appDir.exists()) appDir.mkdir();
        
        strings.setAppDir(strings.getRemoteAppDir() + File.separatorChar + appName);
        strings.refreshAppSettingsPath();
    }
} // end of class Utility
