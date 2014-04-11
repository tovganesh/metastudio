import metastudio
from metastudio import *

from org.meta.math.qm import BasisFunctions, MolecularOrbitals
from org.meta.common import Utility
from org.meta.shell.idebeans.viewers.impl.moleculeviewer.graphicsengine.surfaces import PropertyScene

def moDensity(mol, mos, bfs, gp, monumber):

  print "monumber: ", monumber

  fVals = [0]*gp.getNumberOfGridPoints()

  xmin = gp.getBoundingBox().getUpperLeft().getX()
  ymin = gp.getBoundingBox().getUpperLeft().getY()
  zmin = gp.getBoundingBox().getUpperLeft().getZ()

  xinc = gp.getXIncrement()
  yinc = gp.getYIncrement()
  zinc = gp.getZIncrement()

  print xinc, ",", yinc, ",", zinc

  nclosed = mol.getNumberOfElectrons() / 2

  lb = gp.getBoundingBox().getUpperLeft()

  mo = mos.getMatrix()

  bfs = bfs.getBasisFunctions()
  nbf = bfs.size()
  ii = 0
  npx = gp.getNoOfPointsAlongX()
  npy = gp.getNoOfPointsAlongX()
  npz = gp.getNoOfPointsAlongX()

  print npx, ", ", npy, ", ", npz

  amp = [0]*nbf;

  for i in range(npx):
    x = (xmin + (i*xinc)) / Utility.AU_TO_ANGSTROM_FACTOR
    for j in range(npy):
        y = (ymin + (j*yinc)) / Utility.AU_TO_ANGSTROM_FACTOR
        for k in range(npz):
            z = (zmin + (k*zinc)) / Utility.AU_TO_ANGSTROM_FACTOR

            p = point(x,y,z)
            den = 0.0
            for m in range(nbf):
               amp[m] = bfs.get(m).amplitude(p)

            for m in range(nbf):
              for l in range(nbf):
                den += mo[monumber][m]*mo[monumber][l]*amp[l]*amp[m]

            fVals[ii] = den
            ii += 1
        
  gp.setFunctionValues(fVals)

def readMOs(siz):
  fil = showFileDialog()
  f = open(fil)
  lines = f.readlines()
  f.close()

  mos = MolecularOrbitals(siz)

  m = n = 0
  for line in lines:
     dmval = string.atof(line.strip())
     mos.setMatrixAt(m, n, dmval)
     n += 1
     if (n == siz):
        n = 0
        m += 1

  return mos

def scfden(mol, gp):
    bfs = BasisFunctions(mol, "sto3g")
    nbf = bfs.getBasisFunctions().size()

    print nbf

    mos = readMOs(nbf)

    moDensity(mol, mos, bfs, gp, 5)

dt = getDesktopInstance()
fl = dt.getFrameList()

mvf = None
for f in fl:
  if isinstance(f, MoleculeViewerFrame):
     mvf = f
     break 

if (mvf != None):
  mv = mvf.getMoleculeViewer()
  mols = mv.getSceneList().get(0)
  mol = mols.getMolecule()
  gp = mv.getScreenGridProperty()

  print gp.toString()

  scfden(mol, gp)
  
  ps = PropertyScene(mols, gp)
  mols.addPropertyScene(ps)




