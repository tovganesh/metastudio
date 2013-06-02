; Windows (XP, Vista, 7) installer for MeTA Studio
; Should work on other version of Windows, but not tested
; author: V.Ganesh
; 12th May 2007

;--------------------------------
;Include Modern UI

 !include "MUI.nsh"

;--------------------------------
; Use only user level privilages
 RequestExecutionLevel user


;--------------------------------
;General

 ;Name and file
 Name "MeTA Studio"
 Caption "MeTA Studio: IDE for computational chemist"
 OutFile "MeTAStudioInstaller.exe"
 DirText "Please choose a directory to which you would like to install MeTA Studio"

 ;Default installation folder
 InstallDir "$PROFILE\meta"

 ;Get installation folder from registry if available
 ; InstallDirRegKey HKCU "Software\MeTa Studio" ""

;--------------------------------
;Interface Settings

 !define MUI_ABORTWARNING

;--------------------------------
;Pages

 ; LGPL licence text to be included
 !insertmacro MUI_PAGE_LICENSE "C:\Users\Ganesh\meta\LICENSE.txt"
 !insertmacro MUI_PAGE_COMPONENTS
 !insertmacro MUI_PAGE_DIRECTORY
 !insertmacro MUI_PAGE_INSTFILES
  
 !insertmacro MUI_UNPAGE_CONFIRM
 !insertmacro MUI_UNPAGE_INSTFILES
  
;--------------------------------
;Languages

 !insertmacro MUI_LANGUAGE "English"

;--------------------------------
;Installer Sections

Section "MeTA Studio" SecMeTA

 ; check for installed JRE
 readRegStr $0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" CurrentVersion
 StrCmp $0 "" NoFound

 ; add files
 SetOutPath $INSTDIR\bin 
 File C:\Users\Ganesh\meta\bin\MeTA.jar

 SetOutPath $INSTDIR\help
 File C:\Users\Ganesh\meta\help\MeTAHelpAPI.jar 

 SetOutPath $INSTDIR\lib
 File C:\Users\Ganesh\meta\lib\*.jar
 File C:\Users\Ganesh\meta\lib\apache-LICENSE.txt

 SetOutPath $INSTDIR\lib\ext
 File /r C:\Users\Ganesh\meta\lib\ext\*.jar

 SetOutPath $INSTDIR\lib\ext\meta-jython
 File /r C:\Users\Ganesh\meta\lib\ext\meta-jython\*.bsh
 File /r C:\Users\Ganesh\meta\lib\ext\meta-jython\*.py

 SetOutPath $INSTDIR\lib\ext\meta-jython\Lib
 File /r C:\Users\Ganesh\meta\lib\ext\meta-jython\Lib\*

 SetOutPath $INSTDIR\lib\windows\x86
 File /r C:\Users\Ganesh\meta\lib\windows\x86\*.jar
 File /r C:\Users\Ganesh\meta\lib\windows\x86\*.dll

 SetOutPath $INSTDIR\scripts
 File /r C:\Users\Ganesh\meta\scripts\*.bsh

 SetOutPath $INSTDIR\widgets
 File /r C:\Users\Ganesh\meta\widgets\*.bsh

 SetOutPath $INSTDIR\apps
 File /r C:\Users\Ganesh\meta\apps\*

 ; create uninstaller
 WriteUninstaller $INSTDIR\Uninstall.exe

 ; create directories and shortcut
 SetOutPath $INSTDIR\bin 
 File C:\Users\Ganesh\meta\installer\win32\icon.ico

 CreateDirectory "$SMPROGRAMS\MeTA Studio"
 CreateShortcut "$SMPROGRAMS\MeTA Studio\MeTA Studio.lnk" "$SYSDIR\javaw.exe" "-jar MeTA.jar" "$INSTDIR\bin\icon.ico"
 CreateShortcut "$SMPROGRAMS\MeTA Studio\Uninstall.lnk" "$INSTDIR\Uninstall.exe" 
 CreateShortcut "$DESKTOP\MeTA Studio.lnk" "$SYSDIR\javaw.exe" "-jar MeTA.jar" "$INSTDIR\bin\icon.ico"

 ;Store installation folder
 ;WriteRegStr HKCU "Software\Modern UI Test" "" $INSTDIR
 ;WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\MeTA Studio" "DisplayName" "MeTA Studio (remove only)"

 goto InstallComplete

 ; if JRE is not found
 NoFound:
   MessageBox MB_OK "JRE not found. Installation will quit now. Install JRE to run the Java Program. Get JRE from http://www.java.com"

 ; every thing ok
 InstallComplete:

SectionEnd

;--------------------------------
;Descriptions

  ;Language strings
  LangString DESC_SecMeTA ${LANG_ENGLISH} "MeTA Studio"

  ;Assign language strings to sections
  !insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
    !insertmacro MUI_DESCRIPTION_TEXT ${SecMeTA} $(DESC_SecMeTA)
  !insertmacro MUI_FUNCTION_DESCRIPTION_END

;--------------------------------
;Uninstaller Section
Section "Uninstall"

 Delete "$SMPROGRAMS\MeTA Studio\MeTA Studio.lnk"
 Delete "$SMPROGRAMS\MeTA Studio\Uninstall.lnk"

 RMDir /r "$SMPROGRAMS\MeTA Studio"
 RMDir /r "$INSTDIR"

 Delete "$DESKTOP\MeTA Studio.lnk"
 
 ; DeleteRegKey /ifempty HKCU "Software\MeTa Studio"
SectionEnd