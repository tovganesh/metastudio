#
# dmview.py - DM visualization, ported over from previous version of MeTA Studio,
#             metav1, which was never publicly released.
#
#  This visualization was first used in the following publication:
#    DOI: 10.1007/s00214-003-0531-6
#
#  If you use this script for a research publication please cite MeTA Studio as 
#  indicated at the URL: http://code.google.com/p/metastudio 
#
# (c) V. Ganesh
# 
# @author V. Ganesh
# Date: 6th March 2011
#

from java.awt import *
from java.awt.event import *

from javax.swing import *
from javax.swing.event import *

from metastudio import *

from java.lang import Math

import math
import string

class DMVisualization(JDialog):
    
    # few constance for look and feel of the map    
    POINT_SIZE    = 5    
    POINT_SPACING = 2
    NO_OF_GRADATIONS = 180
    DM_VISUAL_THREASHOLD = 10e-6
    PANEL_PADDING = 100

    def __init__(self, moleculeFileName, matrixFileName):
        JDialog.__init__(self, JFrame(), "The Matrix map", True)

        self.minR = 1.0
        self.minG = 0.0
        self.maxR = 0.0
        self.maxG = 1.0
        self.minB = 0.0
        self.maxB = 0.0
  
        self.largestValue  = 0.0
        self.smallestValue = 0.0

        self.dmVisualThreashold = DMVisualization.DM_VISUAL_THREASHOLD

        self.caption = "Based on Bond Distance"

        # read molecule, reorder according to connectivity
        # Note: this is done when Z-Matrix is constructed
        molecule = readMoleculeFile(moleculeFileName)
        buildSimpleConnectivity(molecule)
        atomMap  = []

        # """
        # switch this on in case reordering with atom connectivity is required
        traversedPath = molecule.traversePath(0)
        while traversedPath.hasNext():
            atomMap.append(traversedPath.next())
        
        """
        for i in range(molecule.getNumberOfAtoms()):
            atomMap.append(i)
        """

        # read the DM or other matrix
        self.noOfContractions, self.dm = self.readDM(atomMap, matrixFileName)

        self.visualizationPanelBD = DMVisualization.PanelBD(self)        
        self.visualizationPanelBD.setPreferredSize(Dimension(self.noOfContractions+DMVisualization.PANEL_PADDING, \
                                                             self.noOfContractions+DMVisualization.PANEL_PADDING)) 
        
        scrollBD = JScrollPane(self.visualizationPanelBD,  \
                               JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, \
                               JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS)
        
        self.add(scrollBD)

        self.setResizable(True)
        self.setSize(500, 400)

    def readDM(self, atomMap, matrixFileName):
        fil   = open(matrixFileName)
        lines = fil.readlines()
        fil.close()

        noOfContractions = string.atoi(string.strip(lines[0]))

        # read in (a, b, i, j, val) format
        # reducre the indices by 1, for Py format indices
        indxdm = {}
        ii = 0
        for i in range(1, len(lines)):
            words = string.split(lines[i])
            a = string.atoi(string.strip(words[0]))-1
            b = string.atoi(string.strip(words[1]))-1
            i = string.atoi(string.strip(words[2]))-1
            j = string.atoi(string.strip(words[3]))-1
            dmval = string.atof(string.strip(words[4]))

            if (a==b and i==j): ii += 1

            if (indxdm.has_key(a)):
               if (indxdm[a].has_key(b)):
                  if (indxdm[a][b].has_key(i)):
                     if (indxdm[a][b][i].has_key(j)): print "duplicate key!"
                     else: indxdm[a][b][i][j] = dmval
                  else:
                     indxdm[a][b][i]    = {}
                     indxdm[a][b][i][j] = dmval 
               else:     
                  indxdm[a][b]       = {}
                  indxdm[a][b][i]    = {}
                  indxdm[a][b][i][j] = dmval
            else:
               indxdm[a]          = {}
               indxdm[a][b]       = {}
               indxdm[a][b][i]    = {}
               indxdm[a][b][i][j] = dmval

        # print len(indxdm), ii

        # for max contraction list
        mxcnt = [0]*len(atomMap)
        for a in range(len(indxdm)):
            for b in range(a+1):
                bkl = None
                try:
                    blk = indxdm[atomMap[a]][atomMap[b]]
                except:
                    blk = indxdm[atomMap[b]][atomMap[a]]
                
                if (blk == None or blk == {}): continue

                for i in blk:
                    for j in blk[i]:
                        if (i==j): 
                            mxcnt[atomMap[a]] = max(mxcnt[atomMap[a]], i+1)
   
        # print mxcnt, sum(mxcnt)
                        
        # next linearlize
        dm = []
        for i in range(noOfContractions):
            dmrow = []
            for j in range(noOfContractions):
                dmrow.append(0.0)
            dm.append(dmrow)

        # print "<<", len(dm), len(dm[0])

        imap = 0; jmap  = 0
        ii = 0
        iContractions = 0; jContractions = 0
        self.largestValue  = dm[0][0]
        self.smallestValue = dm[0][0]
        for a in range(len(indxdm)):
            for b in range(a+1):
                bkl = None
                try:
                    blk = indxdm[atomMap[a]][atomMap[b]]
                except:        
                    blk = indxdm[atomMap[b]][atomMap[a]]

                if (blk == None or blk == {}):
                    jContractions += mxcnt[atomMap[b]]
                    continue

                for i in blk:
                    imap = iContractions + i
                    for j in blk[i]:                       
                       jmap = jContractions + j

                       dmval = blk[i][j]
                       dm[imap][jmap] = dm[jmap][imap] = dmval

                       if (dmval != 0):
                          if (self.largestValue  < dmval): self.largestValue  = dmval
                          if (self.smallestValue > dmval): self.smallestValue = dmval

                       if (a==b and i==j): 
                           ii += 1
                           # print a, b, i, j, " :: ", imap, jmap

                jContractions += mxcnt[atomMap[b]]
            iContractions += mxcnt[atomMap[a]]
            jContractions = 0

        # print imap, jmap, noOfContractions, ii

        return noOfContractions, dm

    # for generating a simple color map image for showing the variation...
    def getColorMap(self, fmax, fmin):
        # we always draw O(100) gradations...as we don't expect a very large variation
        theMap = self.createImage(DMVisualization.NO_OF_GRADATIONS+30, 40)
        stepValue = 0.0;                            
        
        imageGraphics = theMap.getGraphics()
        
        j = 0
        i = 0.0
        
        imageGraphics.setColor(self.visualizationPanelBD.getBackground())
        imageGraphics.fillRect(0, 0, DMVisualization.NO_OF_GRADATIONS+30, 40)
        
        imageGraphics.setFont(Font("Sans Serif", Font.ITALIC, 10))
        
        # first shade:        
        stepValue = (10e-5 - self.dmVisualThreashold) / (DMVisualization.NO_OF_GRADATIONS / 6)
        imageGraphics.setColor(Color(~self.visualizationPanelBD.getBackground().getRGB()))
        imageGraphics.drawString("10e-6", j, 30)
        i=self.dmVisualThreashold; j=0 
        while i<=10e-5:            
            imageGraphics.setColor(self.findInterpolatedColor(fmax, fmin, i))
            imageGraphics.drawLine(j, 0, j, 20)            
            i+=stepValue; j+=1
        imageGraphics.setColor(Color(~self.visualizationPanelBD.getBackground().getRGB()))         
        
        # second shade:        
        stepValue = (10e-4 - 10e-5) / (DMVisualization.NO_OF_GRADATIONS / 6)
        imageGraphics.setColor(Color(~self.visualizationPanelBD.getBackground().getRGB()))
        imageGraphics.drawString("10e-5", j, 30)
        i=10e-5
        while i<=10e-4:            
            imageGraphics.setColor(self.findInterpolatedColor(fmax, fmin, i))
            imageGraphics.drawLine(j, 0, j, 20)         
            i+=stepValue; j+=1
        imageGraphics.setColor(Color(~self.visualizationPanelBD.getBackground().getRGB()))
        imageGraphics.drawString("10e-4", j, 30)
        
        # third shade:
        stepValue = (10e-3 - 10e-4) / (DMVisualization.NO_OF_GRADATIONS / 6)
        i=10e-4
        while i<=10e-3:            
            imageGraphics.setColor(self.findInterpolatedColor(fmax, fmin, i))
            imageGraphics.drawLine(j, 0, j, 20)
            i+=stepValue; j+=1
        imageGraphics.setColor(Color(~self.visualizationPanelBD.getBackground().getRGB()))
        imageGraphics.drawString("10e-3", j, 30)
        
        # forth shade:
        stepValue = (10e-2 - 10e-3) / (DMVisualization.NO_OF_GRADATIONS / 6)
        i=10e-3
        while i<=10e-2:            
            imageGraphics.setColor(self.findInterpolatedColor(fmax, fmin, i))
            imageGraphics.drawLine(j, 0, j, 20)
            i+=stepValue; j+=1
        imageGraphics.setColor(Color(~self.visualizationPanelBD.getBackground().getRGB()))
        imageGraphics.drawString("10e-2", j, 30)
        
        # fifth shade:
        stepValue = (10e-1 - 10e-2) / (DMVisualization.NO_OF_GRADATIONS / 6)
        i=10e-2
        while i<=10e-1:            
            imageGraphics.setColor(self.findInterpolatedColor(fmax, fmin, i))
            imageGraphics.drawLine(j, 0, j, 20)
            i+=stepValue; j+=1
        imageGraphics.setColor(Color(~self.visualizationPanelBD.getBackground().getRGB()))
        imageGraphics.drawString("10e-1", j, 30)
        
        # sixth shade:
        stepValue = (fmax - 10e-1) / (DMVisualization.NO_OF_GRADATIONS / 6)
        i=10e-1
        while i<=fmax:            
            imageGraphics.setColor(self.findInterpolatedColor(fmax, fmin, i))
            imageGraphics.drawLine(j, 0, j, 20)
            i+=stepValue; j+=1
        imageGraphics.setColor(Color(~self.visualizationPanelBD.getBackground().getRGB()))
        imageGraphics.drawString("" + repr(Math.rint(fmax)), j, 30)
        
        return theMap;

    def findInterpolatedColor(self, fmax, fmin, goodnessValue):                
        if ((goodnessValue >= self.dmVisualThreashold) and (goodnessValue < 10e-5)):
            self.minR = 0.0
            self.maxR = 0.5
            self.minG = 1.0
            self.maxG = 0.0 
            self.minB = 0.0
            self.maxB = 0.5
        elif ((goodnessValue >= 10e-5) and (goodnessValue < 10e-4)):
            self.minR = 0.5
            self.maxR = 1.0
            self.minG = 0.0
            self.maxG = 1.0
            self.minB = 0.5
            self.maxB = 0.5
        elif ((goodnessValue >= 10e-4) and (goodnessValue < 10e-3)):
            self.minR = 1.0
            self.maxR = 1.0
            self.minG = 1.0
            self.maxG = 0.0
            self.minB = 0.5
            self.maxB = 1.0
        elif ((goodnessValue >= 10e-3) and (goodnessValue < 10e-2)):
            self.minR = 1.0
            self.maxR = 0.0
            self.minG = 0.0
            self.maxG = 0.0
            self.minB = 1.0
            self.maxB = 1.0
        elif ((goodnessValue >= 10e-2) and (goodnessValue < 10e-1)):
            self.minR = 0.0
            self.maxR = 0.5
            self.minG = 0.0
            self.maxG = 0.0
            self.minB = 1.0
            self.maxB = 0.0
        elif (goodnessValue >= 10e-1):
            self.minR = 0.5
            self.maxR = 1.0
            self.minG = 0.0
            self.maxG = 0.0
            self.minB = 0.0
            self.maxB = 0.0
         
        r = ((((goodnessValue - fmin) / (fmax - fmin)) * (self.maxR - self.minR)) + self.minR)
        g = ((((goodnessValue - fmin) / (fmax - fmin)) * (self.maxG - self.minG)) + self.minG)
        b = ((((goodnessValue - fmin) / (fmax - fmin)) * (self.maxB - self.minB)) + self.minB)
        
        # TODO : this should not be req
        if (r > 1.0): r = 1.0
        if (g > 1.0): g = 1.0
        if (b > 1.0): b = 1.0

        return Color(r, g, b)
   
    def getSizeScaling(self, goodnessValue):
        if ((goodnessValue >= self.dmVisualThreashold) and (goodnessValue < 10e-5)):
            return 1.0
        elif ((goodnessValue >= 10e-5) and (goodnessValue < 10e-4)):
            return 1.5
        elif ((goodnessValue >= 10e-4) and (goodnessValue < 10e-3)):
            return 2.0
        elif ((goodnessValue >= 10e-3) and (goodnessValue < 10e-2)):
            return 3.0
        elif ((goodnessValue >= 10e-2) and (goodnessValue < 10e-1)):
            return 4.5
        elif (goodnessValue >= 10e-1):
            return 6.0
        else: 
            return 0.0 # no need to draw

    # the panel to show Matrix
    class PanelBD(JPanel):
        def __init__(self, dmViewObject):
            JPanel.__init__(self)

            self.setBackground(Color.black)
            self.setForeground(Color.white)            
            self.setToolTipText("Click to change the background color")            

            self.theImage = None

            self.dmViewObject = dmViewObject
            self.imageChange  = True

        def paint(self, graphics):
            if ((self.theImage == None) or (self.getSize().width  != self.theImage.getWidth(self)) \
                                   or (self.getSize().height != self.theImage.getHeight(self))): 
                self.theImage = self.createImage(self.getSize().width, self.getSize().height)           
                self.imageChange  = True
            else:
                self.imageChange  = False

            if (not self.imageChange):
                graphics.drawImage(self.theImage, 0, 0, self)
                return

            # caclulate how a point would be represented
            pointSpacing = DMVisualization.POINT_SPACING
            pointSize    = int(math.ceil(self.theImage.getWidth(self)*1.0 / (self.dmViewObject.noOfContractions * pointSpacing)))
            pointSpacing = pointSize

            # print pointSpacing, pointSize, self.theImage.getWidth(self)

            centerX = centerY = 40  # we can't rt now handle really big matrics
            
            JPanel.paint(self, graphics)
        
            imageGraphics = self.theImage.getGraphics()
          
            # first paint the background...
            imageGraphics.setColor(self.getBackground())
            imageGraphics.fillRect(0, 0, self.theImage.getWidth(self), self.theImage.getHeight(self))
            
            imageGraphics.setColor(Color.cyan)            
            imageGraphics.setFont(Font("Sans Serif", Font.PLAIN|Font.ITALIC|Font.BOLD, 12))
            imageGraphics.drawString(self.dmViewObject.caption, 10, 10)                 
            imageGraphics.drawImage(self.dmViewObject.getColorMap(self.dmViewObject.largestValue, self.dmViewObject.smallestValue), \
                                    175, 0, self)
            imageGraphics.setColor(Color.green)

            value = 0.0
            squareSize = 0
                
            for i in range(self.dmViewObject.noOfContractions):
                for j in range(i):
                    value = abs(self.dmViewObject.dm[i][j]);
                    if (value > self.dmViewObject.dmVisualThreashold):
                        squareSize = int(pointSize * self.dmViewObject.getSizeScaling(value))
                        imageGraphics.setColor(self.dmViewObject.findInterpolatedColor(self.dmViewObject.largestValue, self.dmViewObject.smallestValue, value))
                        imageGraphics.fillRect((j*pointSpacing) + centerX, (i*pointSpacing) + centerY, squareSize, squareSize)

            print "rendering complete"

            graphics.drawImage(self.theImage, 0, 0, self)
         
def dmView():
    # dmv = DMVisualization("/Users/Ganesh/Documents/metapps/dmview/file_2.xyz", "/Users/Ganesh/Documents/metapps/dmview/file_2_hf_act.meta2")
    # Note: contraction indexed matrix file is of the form: 
    #     a b i j value 
    # a and b are atom indices and i and j are contraction indices, value is the real quantity representing the DM element
    dmv = DMVisualization(showFileDialog(title="Molecule File"), showFileDialog(title="Contraction indexed Matrix file"))
    dmv.show()

dmView()
