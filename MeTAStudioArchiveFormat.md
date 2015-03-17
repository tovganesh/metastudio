# Introduction #

MeTA Studio archive format (.mar) is a way to package application written for MeTA Studio platfrom.

# Details #

> The following are format restrictions for .mar file:
  * They are ZIP archive with .mar extension
  * They must have exactly one manifest.xml file
  * There is not directory structure supported (read not package base)
  * All flies, including resource files should be in the current directory
  * If directory entries are found, the implementation reader might    choose to ignore it, but no error should be flagged.

> The following is the format of the manifest.xml file:
```
   <code>   
     <mar>   
       <mainscript name="main.bsh" language="BEANSHELL" />   
     </mar>   
   </code>  
```

> Note: ` language ` parameter may take ` BEANSHELL, JYTHON or JSCHEME `

# Examples #
  1. [hello.mar](http://cid-76d41f4618b0b6af.office.live.com/self.aspx/metastudio/hello.mar)
> > Is a simple 'hello world' application written using Beanshell.
  1. [dm.mar](http://cid-76d41f4618b0b6af.office.live.com/self.aspx/metastudio/dm.mar)
> > Is an application that reads Fock and Overlap matrix and form a Density matrix (P). Also prints out Tr(PS) for verification. This application in written using Jython.