### Pre-requisites ###

Make sure you have latest version of MeTA Studio installed. For installation instructions see http://code.google.com/p/metastudio/wiki/Installation. After you install MeTA Studio, make sure you update it using the update tool in _Help->Check_ for updates option.


### Details ###

Currently MeTA Studio does not support direct computing of one electron properties like electron density or electrostatic potential, but don't be surprised if it does in near future!

In the meantime, you will need a third party tool that can do these calculations for you and generate a three dimensional grid. Two of the well known of these packages are [Gaussian](http://www.gaussian.com/) and [GAMESS](http://www.msg.ameslab.gov/GAMESS/). Output files generated from these programs (generally known as cube files) can be directly opened using _Molecule->Open Molecule_ option. You can then click on the property button in the MolecueViewer window which shows up interface where you can adjust various parameters for viewing the surface plot, the most important being the function value for which you would like to see the plot.

If you have never used GAMESS or Gaussian before, or just do not want to learn the keywords required to get the calculation done, there is a simpler option in form of [WebProp](http://chem.unipune.ernet.in/~tcg/webprop/).

There are a number of ways you can use WebProp:
  1. Visit its website http://chem.unipune.ernet.in/~tcg/webprop/ and see instructions on using it.
  1. If you are a fan of Google toolbar, get a WebProp button here: http://www.google.com/gadgets/directory?synd=toolbar&q=webprop
  1. And the best option is to use MeTA Studio widget available from here http://tovganesh.googlepages.com/webpropWidget.bsh

To use the above widget (which is also available in `meta/widgets` directory):
  1. Download the above file and save it to some location on your machine.
  1. Click and drag the divider on the left panel of MeTA Studio IDE to show the Widgets panel.
  1. Click _Add / Remove Widgets_, and then click _Add Widget..._
  1. Select the file `webpropWidget.bsh` that you have saved before.
  1. Webprop widget will appear on the widgets panel ( note that this installation step is to be done only once, the next time you start the IDE, the widget will be automatically loaded).
  1. Use _Molecule->Open Molecule_ to open the molecule file for which you want to do a property calculation.
  1. Now use the webpropWidget from the Widgets panel to submit a WebProp job. First choose the molecule for the molecule list dialog. Then choose what you want to compute with what basis set etc. Then click on submit. At this point the widget will contact the WebProp server and give you a ticket and will ask you for an email address to which you would like to send the output to. Note that you will need to ensure yourself that the email you provide is valid and accessible to you. Also note that the output sent by WebProp server is in few MBs, so ensure that your mail server can accept attachments up to 20MB.
  1. Once you submit your job the widget will indicate how you can monitor your job. Please note that if you have any problem with running your job you must be contacting the current maintainers of Webprop at : webprop -at- chem.unipune.ernet.in
  1. If your job is completed you will get the output in your specified email as a ZIP attachment. The ZIP attachment contains a `.vis` file (with contains molecular coordinates), a `.prop` file (contains the functions values of the 3D property you have evaluated, provided you have chosen this) and a `.raw` file (contains volumetric data which can be opened in [Drishti](http://anusf.anu.edu.au/Vizlab/drishti/)).
  1. To visualize, first open the `.vis` file and then attach the property file using _Molecule->Attach Property_ option. To adjust the viewing properties you the property button in the MoleculeViewer window which shows appropriate UI for exploring, creating and rendering 3D plots such as these http://tovganesh.blogspot.com/2007/06/playing-with-meta-studio-renderer-and.html.