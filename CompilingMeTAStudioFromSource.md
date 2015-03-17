MeTA Studio code is entirely written in pure Java and a set of BeanShell scripts. The base API are written in Java while some of the UI part is written in BeanShell. The BeanShell scripts within MeTA Studio also give a glimpse of how to use the inbuilt APIs to write small "ChemLets".

### Building MeTA.jar ###
The easiest way to compile MeTA Studio is to use NetBeans IDE. Personally, I use the NetBeans IDE http://www.netbeans.org with the latest Sun JDK for building MeTA Studio. The included source files http://code.google.com/p/metastudio/downloads/list contain the NetBeans project files to make your task simpler. You can of course use any (or no tool) depending upon your taste. You may also use the daily source updates from http://code.google.com/p/metastudio/source/browse/. I must, however note that I do not use any UI editing tools like the one available in NetBeans and Eclipse to build any part of the UI for MeTA Studio. All the UI for MeTA Studio are hand coded or dynamically generated within. Consequently, any code contributions to MeTA Studio for UI enhancements must be hand coded.

The source code archive http://code.google.com/p/metastudio/downloads/list or the HG http://code.google.com/p/metastudio/source/browse/, however doesnot contain any of the library .jar files required to compile MeTA Studio. These must be extracted from the ` lib ` directory of the binary distribution of MeTA Studio obtained via http://metastudio.googlecode.com/files/meta-bin.tar.bz. The source of all the libraries used for compiling MeTA Studio are available at their respective project pages. Some of these libraries are however modified for use in MeTA Studio, in which case their source is provided in ` lib/src ` directory in the source archive.

### Building Help system ###
The MeTA Studio help system is build using the Java help system. The help files consists of two major componets: the API documentation and the general user help. The API documentation can be automatically generated using NetBeans IDE (or can be generated using javadoc command line tool). The general help is a set of HTML files that are manually edited before being packaged.

To compile the help system, you should have latest version of Java Help System installed from http://download.java.net/javadesktop/javahelp/javahelp2_0_05.zip. After extracting the binary at appropriate location, ensure that ` jhindexer ` is in the path. Now, change the directory to `docs/ ` and run the command :

` $ jhindexer apidocs studiodocs `


The above will generate the indexing information for the help set. After this is done, run the following command:

` $ jar cvf ../help/MeTAHelpAPI.jar . `

This will generate the required help set.

Note: Use ` / ` or ` \ ` as the file separator depending on your system.