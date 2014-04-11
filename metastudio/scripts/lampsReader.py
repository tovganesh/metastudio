import metastudio
from metastudio import *

atomType = {"1" : "O", "2" : "H" }
atomType = {"1":"Si", "2":"C", "3":"O", "4":"C", "5":"O", "6":"C", "7":"H","8":"F", "9":"OW", "10":"HW" }

def readLampsFile(lfil):
    lf = open(lfil)
    mols = []

    while 1:
      line = lf.readline()
      if (line == None or line == ""): break

      if (line.find("ITEM: TIMESTEP")):
         timstep = lf.readline()
         
         nats = int(lf.readline().strip())

         lf.readline(); lf.readline()
         lf.readline(); lf.readline()
         lf.readline()

         mol = molecule(timstep.strip())
         for i in range(nats):
             line = lf.readline()
             words = line.split()
                
             atm = atom(atomType[words[1].strip()], 0.0, point(float(words[2].strip()), float(words[3].strip()), float(words[4].strip())))   
             atm.setIndex(int(words[0])-1)
             mol.addAtom(atm)
         
         mols.append(mol)

    # buildConnectivity(mols, "false", "true")

    return mols
      
showMolecule(readLampsFile(showFileDialog()))
