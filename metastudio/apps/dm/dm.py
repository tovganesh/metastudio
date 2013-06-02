import sys
import string

import metastudio

from metastudio import *
from org.meta.math.qm import Fock, Overlap, MolecularOrbitals, Density

fn = showFileDialog() # "carotene-mo-lowtr.fck"
sn = showFileDialog() # "carotene-mo-smat"

def readMat(fil):
  f = open(fil)
  fl = f.readlines()
  f.close()

  mat = []
  n = string.atoi(fl[0])

  for i in range(n):
      mat.append([])
      for j in range(n):
          mat[i].append(0.0)

  ii = 1
  for i in range(n):
      for j in range(i+1):
          mat[i][j] = mat[j][i] = string.atof(string.strip(fl[ii]))
          ii += 1

  return mat

f = readMat(fn)
s = readMat(sn)

n = len(f)

F = Fock(n)
S = Overlap(n)

F.setMatrix(f)
S.setMatrix(s)

# print F.getMatrix()

print n

MO = MolecularOrbitals(n)
MO.compute(F, S)
print MO.getOrbitalEnergies()

D = Density(n)
D.compute(None, False, None, 1, MO)

PS = D.mul(S)

print PS
print PS.trace()

