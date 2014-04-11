#
# metastudio.py
#
# Wrappers for accessing MeTA Studio APIs. Where ever possible these
# wrappers are consistent with the ones provided in BeanShell (the default
# scripting interface for MeTA Studio). 
# However, due to some restriction in Python, like no support for function 
# overloading, certain functions may be missing all together!
#
# @author V. Ganesh
# 25 March, 2009
#
# updated: 27 May, 2009 ; updated to reflect few additions to BeanShell
# updated: 20 March, 2011 ; updated to reflect few additions to BeanShell
#

import string

from java.io import File
from java.util import ArrayList
from java.io import BufferedReader,InputStreamReader,FileInputStream,\
                    BufferedWriter,OutputStreamWriter,FileOutputStream

from javax.swing import JFileChooser

from org.meta.molecule import Molecule, MoleculeBuilder, Atom
from org.meta.moleculereader import MoleculeFileReaderFactory
from org.meta.common import Utility
from org.meta.common.resource import StringResource
from org.meta.math.geom import Point3D, BoundingBox
from org.meta.math import Vector3D, Matrix3D
from org.meta.net import FederationServiceDiscovery
from org.meta.shell.idebeans import MoleculeViewerFrame, IDEFileChooser, MoleculeListDialog, IDEWidget
from org.meta.shell.idebeans.eventhandlers import MainMenuEventHandlers
from org.meta.shell.idebeans.viewers.impl.moleculeviewer import MoleculeScene
from org.meta.config.impl import AtomInfo
from org.meta.math.qm.property import ElectronDensity, MODensity
from org.meta.molecule.property.electronic import GridProperty

def getIDEInstance():
    return MainMenuEventHandlers.getInstance(None).getIdeInstance()
    
def molecule(title):
    mol = Utility.getDefaultImplFor(Molecule).newInstance() 
    mol.setTitle(title)
   
    return mol

def atom(sym, charge, center):
    return Atom(sym, charge, center)

def point(x, y, z):
    return Point3D(x, y, z)

def boundingbox(upperLeft, bottomRight):
    return BoundingBox(upperLeft, bottomRight)

def vector3d(*args):
    if (len(args) == 3):
       return Vector3D(args[0], args[1], args[2])
    else:
       return Vector3D(args[0])

def matrix3d():
    return Matrix3D()
 
def atomInfo():
    return AtomInfo.getInstance();

def buildConnectivity(mol, makeZMatrix="true", copyConnectivity="false"):
    mb = Utility.getDefaultImplFor(MoleculeBuilder).newInstance() 

    if (copyConnectivity == "false"):
      try: 
        if (makeZMatrix == "false"):
          for i in range(0, len(mol)):
             mb.makeConnectivity(mol[i])
        else:
          for i in range(0, len(mol)):
             mb.makeZMatrix(mol[i])
      except:
        if (makeZMatrix == "false"):
          mb.makeConnectivity(mol)
        else:
          mb.makeZMatrix(mol)
    else:
      try:
        if (makeZMatrix == "false"):
          mb.makeConnectivity(mol[0])
        else:
          mb.makeZMatrix(mol[0])
        
        for i in range(1, len(mol)):
           atms  = mol[0].getAtoms();
           iatms = mol[i].getAtoms();  

           while (atms.hasNext() and iatms.hasNext()):
              iatms.next().setConnectedList(atms.next().getConnectedList())
      except:
        if (makeZMatrix == "false"):
          mb.makeConnectivity(mol)
        else:
          mb.makeZMatrix(mol)

def buildSimpleConnectivity(mol):
    mb = Utility.getDefaultImplFor(MoleculeBuilder).newInstance() 

    try:
      for i in range(0, len(mol)):
         mb.makeSimpleConnectivity(mol)
    except:
      mb.makeSimpleConnectivity(mol)
      
def showMolecule(mol):
    ideInstance = getIDEInstance()

    mvf = MoleculeViewerFrame(ideInstance)
    mvf.getMoleculeViewer().disableUndo()

    try: 
      for i in range(0, len(mol)):
         mvf.addScene(MoleculeScene(mol[i]))
    except:
      mvf.addScene(MoleculeScene(mol))
      
    mvf.getMoleculeViewer().enableUndo() 

    ideInstance.getWorkspaceDesktop().addInternalFrame(mvf)

def showFileDialog(title="Open", type="open"):
    type = type.lower()

    if type == "open":
       fileChooser = IDEFileChooser()
       fileChooser.setDialogTitle(title)    

       if (fileChooser.showOpenDialog(getIDEInstance()) == JFileChooser.APPROVE_OPTION):
           return fileChooser.getSelectedFile().getAbsolutePath()
       else: 
           return None
    elif type == "save":   
       fileChooser = IDEFileChooser()
       fileChooser.setDialogTitle(title)    

       if (fileChooser.showSaveDialog(getIDEInstance()) == JFileChooser.APPROVE_OPTION):
           return fileChooser.getSelectedFile().getAbsolutePath()
       else:
           return None
    else:   
       fileChooser = IDEFileChooser()
       fileChooser.setDialogTitle(title)    

       if (fileChooser.showOpenDialog(getIDEInstance()) == JFileChooser.APPROVE_OPTION):
           return fileChooser.getSelectedFile().getAbsolutePath()
       else:
           return None

def arraylistToPylist(arlist):
    list = []
    for i in range(0, arlist.size()): list.append(arlist.get(i))
    return list

def arraylist():
    return ArrayList()

def discover_meta(ipExpression=None, timeout=100):
    if ipExpression == None: 
       nl = FederationServiceDiscovery.getInstance().discoverMeTA()
    else:
       inst = FederationServiceDiscovery.getInstance()
       inst.setTimeout(timeout)
       nl = inst.discoverMeTA(ipExpression)

    return arraylistToPylist(nl)

def list_meta(timeout=100):
    inst = FederationServiceDiscovery.getInstance()
    inst.setTimeout(timeout)
    return arraylistToPylist(inst.listMeTA())

def readMoleculeFile(file):
    mfr = Utility.getDefaultImplFor(MoleculeFileReaderFactory).newInstance()    
    typ = file[file.index(".")+1:len(file)]
    rdr = mfr.getReader(typ)
    
    return rdr.readMoleculeFile(file)

def openFile(fileName, mode):
    if mode=="r":
       return BufferedReader(InputStreamReader(FileInputStream(fileName)))
    elif mode=="w":
       return BufferedWriter(OutputStreamWriter(FileOutputStream(fileName)))
       
def readMultiMoleculeFile(file, makeZMatrix="true", copyConnectivity="false"):
    mfr = Utility.getDefaultImplFor(MoleculeFileReaderFactory).newInstance()    
    typ = file[file.index(".")+1:len(file)]
    rdr = mfr.getReader(typ)
    br = openFile(file, "r")

    molList = [] 
    try:
       i = 0

       while 1:
          mol = rdr.readMolecule(br)

          if mol.getNumberOfAtoms() == 0: break

          if ((mol.getTitle()==None) or (mol.getTitle()=="") \
               or (mol.getTitle()=="Untitled") \
               or (mol.getTitle().index("Molecule")==0)): 
            mol.setTitle(Utility.getFileNamesSansExtension(File(file)) + "-" + repr(i))

          molList.append(mol)
          i += 1
    except:
       print "Warning: Could not read the complete file " + file

    br.close()

    buildConnectivity(molList, makeZMatrix, copyConnectivity)

    return molList

def showMoleculeListDialog():
    ml = MoleculeListDialog(getIDEInstance())

    return ml.showListDialog()

def play(fileName):
    from javazoom.jl.player import Player
 
    Player(FileInputStream(fileName)).play()

def getDefaultClass(theClass):
    return Utility.getDefaultImplFor(theClass)

def getDesktopInstance():
    ideInstance = getIDEInstance()

    if (ideInstance == None): 
       return None
    else: 
       return ideInstance.getWorkspaceDesktop() 

def getActiveFrame():
    wd = getDesktopInstance()
   
    if (wd == None): return None
    else: wd.getActiveFrame()

def getLoadedMoleculeScenes():
    ideInstance = getIDEInstance()
    if (ideInstance == None): return None
   
    wd = getDesktopInstance()
    if (wd == None): return None
    
    frameList = wd.getFrameList()
    sceneList = []
    for frame in frameList:
       try:
         scenes = frame.getSceneList()

         for scene in scenes: sceneList.append(scene)
       except:
         pass 

    return sceneList

def getLoadedMolecules():
    ideInstance = getIDEInstance()
    if (ideInstance == None): return None
 
    currentWorkspace = ideInstance.getCurrentWorkspace()
    if (currentWorkspace == None): return None

    workspaceItems = currentWorkspace.getWorkspaceItems()

    moleculeList = []
    for item in workspaceItems:
        try:
          item.getItemData().getData().getNumberOfAtoms() # just a way to see that this molecule object
          moleculeList.append(item.getItemData().getData())
        except:
          pass

    return moleculeList

def getMolecule(index):
    return getLoadedMoleculeScenes()[index].getMolecule()

def getSelectionStack(vframe):
    sal = []

    for scene in vframe.getSelectionList():
       for sa in scene.getSelectionStack():
          sal.append(sa)

    return sa

def messageBox(msg, title="", type="normal"):
    from javax.swing import JOptionPane

    if type == "normal": 
       JOptionPane.showMessageDialog(getIDEInstance(), msg, title, JOptionPane.INFORMATION_MESSAGE)
    elif type == "warning":
       JOptionPane.showMessageDialog(getIDEInstance(), msg, title, JOptionPane.WARNING_MESSAGE)
    elif type == "warn":
       JOptionPane.showMessageDialog(getIDEInstance(), msg, title, JOptionPane.WARNING_MESSAGE)
    elif type == "error":
       JOptionPane.showMessageDialog(getIDEInstance(), msg, title, JOptionPane.ERROR_MESSAGE)
    else:
       JOptionPane.showMessageDialog(getIDEInstance(), msg, title, JOptionPane.INFORMATION_MESSAGE)

def color(*args):
    from java.awt import Color
    if (len(args) == 1): return Color.decode(args[0])
    else:
       c = args
       return Color(c[0], c[1], c[2])

def button(text, isToggle, bg):
    from javax.swing import JButton, JToggleButton
         
    if isToggle:
       tb = JToggleButton(text)
       bg.add(tb)
       
       return tb
    else: return JButton(text)

def buttongroup():
    from javax.swing import ButtonGroup

    return ButtonGroup()

def checkbox(text):
    from javax.swing import JCheckBox

    return javax.swing.JCheckBox(text)

def combobox(values):
    from javax.swing import JComboBox

    return JComboBox(values)

def frame(txtTitle=""):
    from javax.swing import JFrame

    return JFrame(txtTitle)

def label(txt):
    from javax.swing import JLabel

    return JLabel(txt)

def listbox(values):
    from javax.swing import JList

    return JList(values)

def panel():
    from javax.swing import JPanel

    return JPanel()

def radiobutton(text, bg):
    from javax.swing import JRadioButton
    rb = JRadioButton(text)
    bg.add(rb)

    return rb

def scratchpad():
    from org.meta.shell.idebeans import ScratchPad

    ideInstance = getIDEInstance()
    sp = ScratchPad(ideInstance)
    ideInstance.getWorkspaceDesktop().addInternalFrame(sp)

    return sp

def degrees(radian):
    from org.meta.math import MathUtil

    return MathUtil.toDegrees(radian)

def radians(degree):
    from org.meta.math import MathUtil

    return MathUtil.toRadians(degree)

def diagonalize(mat):
    from org.meta.math.la import DiagonalizerFactory

    d =  DiagonalizerFactory.getInstance().getDefaultDiagonalizer()
    d.diagonalize(mat)

    return d

def getRemoteAppDir():
    return StringResource.getInstance().getRemoteAppDir()

def getWidgetsPanel():
    return getIDEInstance().getWorkspaceExplorer().getWidgetsPanel()

def widget(id):
    return IDEWidget(id)

def moleculeScene(mol):
    return MoleculeScene(mol)

def hfscf(molecule, basisSet):
    from java.text import DecimalFormat
    from java.lang import System
    from org.meta.math.qm import *

    print("Starting computation for " + repr(molecule) + " at " + repr(basisSet) + " basis")

    t1  = System.currentTimeMillis()
    bfs = BasisFunctions(molecule, basisSet)
    t2  = System.currentTimeMillis()
    print("Number of basis functions : " + repr(bfs.getBasisFunctions().size()))
    print("Time till setting up basis : " + repr(t2-t1) + " ms")
  
    oneEI = OneElectronIntegrals(bfs, molecule)
    t2  = System.currentTimeMillis()
    print("Time till 1EI evaluation : " + repr(t2-t1) + " ms")

    twoEI = TwoElectronIntegrals(bfs)
    t2  = System.currentTimeMillis()
    print("Time till 2EI evaluation : " + repr(t2-t1) + " ms")
    scfm  = SCFMethodFactory.getInstance().getSCFMethod(molecule, oneEI, twoEI, SCFType.HARTREE_FOCK)    
    scfm.scf()
    
    print("Final Energy : " + repr(scfm.getEnergy()))

    return scfm.getEnergy()

def mp2scf(molecule, basisSet):
    from java.text import DecimalFormat
    from java.lang import System
    from org.meta.math.qm import *

    print("Starting computation for " + repr(molecule) + " at " + repr(basisSet) + " basis")

    t1  = System.currentTimeMillis()
    bfs = BasisFunctions(molecule, basisSet)
    t2  = System.currentTimeMillis()
    print("Number of basis functions : " + repr(bfs.getBasisFunctions().size()))
    print("Time till setting up basis : " + repr(t2-t1) + " ms")
  
    oneEI = OneElectronIntegrals(bfs, molecule)
    t2  = System.currentTimeMillis()
    print("Time till 1EI evaluation : " + repr(t2-t1) + " ms")

    twoEI = TwoElectronIntegrals(bfs)
    t2  = System.currentTimeMillis()
    print("Time till 2EI evaluation : " + repr(t2-t1) + " ms")
    scfm  = SCFMethodFactory.getInstance().getSCFMethod(molecule, oneEI, twoEI, SCFType.MOLLER_PLESSET)    
    scfm.scf()
    
    print("Final Energy : " + repr(scfm.getEnergy()))

    return scfm.getEnergy()

def mmEnergy(molecule):
    from org.meta.math.mm import *
    from java.lang import System

    print("Starting MM Energy for " + repr(molecule))
    mm  = MMEnergyMethodFactory.getInstance().getMMEnergyMethod(molecule)
    
    t1  = System.currentTimeMillis()
    energy = mm.getEnergy()
    t2  = System.currentTimeMillis()
   
    print("Energy = " + repr(energy) + " a.u.")
    print("Total Time : " + repr(t2-t1) + " ms")

    return energy

def federationode(nodeName, noOfProcessors=1):
    from java.net import InetAddress
    from org.meta.net import FederationNode

    fNode = FederationNode(InetAddress.getByName(nodeName))
    fNode.setNoOfProcessors(noOfProcessors)

    return fNode

def generateBshScript(pyScript, funName):
    bshScript = "import org.meta.common.resource.StringResource; import java.io.File; import org.python.util.PythonInterpreter;\n"
    bshScript = bshScript + funName + "() { \n"
    bshScript = bshScript + "File pluginDir = new File(StringResource.getInstance().getPluginDir());"
    bshScript = bshScript + "Properties props = new Properties();" 
    bshScript = bshScript + "props.setProperty(\"python.home\", \"../lib/ext/meta-jython\");"
    bshScript = bshScript + "props.setProperty(\"python.cachedir\", StringResource.getInstance().getRemoteAppDir());"
    bshScript = bshScript + "props.setProperty(\"python.path\", \"../lib/ext/meta-jython\" "
    bshScript = bshScript + " + File.pathSeparatorChar + pluginDir.getName());"         
    bshScript = bshScript + "PythonInterpreter.initialize(System.getProperties(), props, new String[] {\"\"});" 
    bshScript = bshScript + "PythonInterpreter pyInterpreter = new PythonInterpreter();"
    bshScript = bshScript + "pyInterpreter.setOut(System.out);"
    bshScript = bshScript + "pyInterpreter.setErr(System.err);"
    bshScript = bshScript + "data = getData(); pyInterpreter.set(\"__data\", data);"

    pyf = open(pyScript, "r")
    lines = pyf.readlines()
    pyf.close()
   
    pyl = "\\ndef getData():\\n\\t return __data\\n\\ndef setData(data):\\n\\tglobal __data\\n\\t__data=data\\n\\n"
    for line in lines: pyl += line.strip("\n").replace("\"", "\\\"") + "\\n"
 
    bshScript = bshScript + "pyInterpreter.exec(\"" + pyl + "\");\n"
    bshScript = bshScript + "setData(pyInterpreter.get(\"__data\", java.util.ArrayList.class));"

    bshScript = bshScript + " } \n " + funName + "();\n"   

    return bshScript

def mapreduce(mapScript, reduceScript, data):
    from org.meta.net.mapreduce.impl import MapFunctionScriptHelper, ReduceFunctionScriptHelper, MapReduceControllerFederationImpl
  
    # every thing is BeanShell, so we need to generate stub code here
    mf = MapFunctionScriptHelper(mapScript)
    mf.setBshScript(generateBshScript(mapScript, "pymap"))

    rf = ReduceFunctionScriptHelper(reduceScript)
    rf.setBshScript(generateBshScript(reduceScript, "pyreduce"))

    mapReduceController = MapReduceControllerFederationImpl()
    mapReduceController.executeTask(mf, rf, data)

    return mapReduceController.getTaskData()

def getDefaultMoleculeGrid(mol):
   bb = mol.getBoundingBox().expand(5)  # 5 angstroms in each direction

   return GridProperty(bb, 100)

def moDensity(scfm, gp, monumber=None):
    if (monumber == None): MODensity(scfm).compute(gp)
    else: MODensity(scfm, monumber).compute(gp)

def electronDensity(scfm, gp):
    ElectronDensity(scfm).compute(gp)

