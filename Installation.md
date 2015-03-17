# Installation Guide #

Pre-requisites:

**Note:** Starting from v 2.0.14012013 the build system has moved to JDK 1.7, as a result you will need to update your JRE to 1.7 after you apply the update. If you do not wish to update the JRE, either stick to the previous binary, or compile the project form source.

Software:
A system that supports JRE 1.6 or higher with Sun Microsystems compliant J2SE platform libraries.

Hardware:
Reasonable processor (on which JRE 1.6 is supported) with atleast 256MB of RAM.
A supported graphics card for on-line 3D rendering (any of the standard Nvidia or ATI cards with proper driver support will do).

1) Generic Installation Guide (All OSes supporting JRE 1.6 or higher)
> - Make sure that you have the latest JRE installed on your system. This on most of the systems can easily be done by visiting http://www.java.com from your browser. Follow the instructions on the site to ensure that you have a compatible JRE.

> - Download the generic MeTA Studio package from:
> > http://metastudio.googlecode.com/files/meta-bin.tar.bz


> - Untar and unzip using command prompt:
> > $ tar jxvf meta-bin.tar.bz


> OR

> Use an utility like 7-Zip (visit http://www.7-zip.org for details on how to use this utility)

> - Then from command prompt start MeTA Studio using:

> $ cd meta/bin

> $ java -jar MeTA.jar

> - Thats it .. enjoy!

2) Windows Installation Guide
> If you use Windows (XP or Vista)<sup>++</sup> then your job is far easier.

> - As above, make sure that you have the latest JRE installed on your system. This on most of the systems can easily be done by visiting http://www.java.com from your browser. Follow the instructions on the site to ensure that you have a compatible JRE.

> - Download MeTA Studio windows installer from:
> > http://metastudio.googlecode.com/files/MeTAStudioInstaller.exe


> - Double click this file from with in Windows Explorer and follow the on screen instructions to install MeTA Studio.

> - Once the installation is done the MeTA Studio icon appears on the desktop. Double click it .. and you are through!

3) Linux Installation
> A x86 Linux installer is also available (which is smaller in size than the generic installer).

> - Make sure you have the latest JRE installed on your machine. Also make sure that 'java' is accessible from default location, preferably '/usr/bin', is of appropriate version.

> - Download MeTA Studio installer for Linux from:
> > http://metastudio.googlecode.com/files/MeTAStudioLinuxInstaller_x86


> - Double click the installer (you may have to chmod +x) on the downloaded file, and proceed with online instructions.

> - The installer adds desktop shortcuts which should be usable to launch MeTA Studio from the shortcuts.

4) MeTA Studio on MAC OS X 10.5.6 Update 1 (Intel Macs), for 10.6.x no extra updates are required

> - Follow Generic Installation instructions as above.

> - Find the Java Version from command prompt using Terminal (spotlight: terminal).

> - Update Java [here](http://support.apple.com/downloads/Java_for_Mac_OS_X_10_5_Update_2).
> > Install Java by unpacking and running the disk image file (dmg).


> Set the updated Java as default by going to

> Applications(shortkey: apple+shift+a) > Utils > Java > Java Preferences.app

> Pops up a window. DRAG the 1.6 Version at the top in the list, this sets the 1.6 version as default.

> - Execute Meta Studio from terminal

> $ cd meta/bin

> $ java -jar MeTA.jar

> Note : Java3D and mp3player widget do not work as of yet.


<sup>++</sup> MeTA Studio should work on earlier version of Windows OS, but it has never been tested on them as the author doesn't have access to them. If any one tries it kindly let me know by posting a issue at http://code.google.com/p/metastudio/issues/list