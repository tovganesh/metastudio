/*
 * ColorToFunctionRangeMap.java
 *
 * Created on July 19, 2005, 7:34 PM
 *
 */

package org.meta.common;
        
import java.awt.Color;
import java.util.ArrayList;

/**
 * Class to hold a color range to function range map. 
 * Also provides functionality for interpolating color values based on function 
 * values using the following formula:
 * <pre>
 *  Ci - Cmin        fi - fmin
 * -----------   =  -----------
 * Cmax - Cmin      fmax - fmin
 * </pre>
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class ColorToFunctionRangeMap {   
    
    // some pre-computed values
    private double minMaxFValDifference;
    private int minMaxRDifference, minMaxGDifference, minMaxBDifference,
                minR, minG, minB;
    private double splitRatio;
    
    private ArrayList<Color> colorRange;
    private boolean useColorRange;
    
    /**
     * Creates a new instance of ColorToFunctionRangeMap 
     */
    public ColorToFunctionRangeMap() {
        this(Color.white, Color.black, 0.0, 0.0);
    }

    /**
     * Creates a new instance of ColorToFunctionRangeMap 
     *
     * @param minColor the minimum color value in the range
     * @param maxColor the maximum color value in the range
     * @param minFVal the minimum function value in the range
     * @param maxFVal the maximum function value in the range
     */
    public ColorToFunctionRangeMap(Color minColor, Color maxColor,
                                   double minFVal, double maxFVal) {
        this.minColor = minColor;
        this.maxColor = maxColor;
        this.minFunctionValue = minFVal;
        this.maxFunctionValue = maxFVal;                
        this.useColorRange = false;
        
        minMaxFValDifference = maxFunctionValue - minFunctionValue;
        computeColorDifference();
    }
    
    /**
     * Creates a new instance of ColorToFunctionRangeMap 
     *
     * @param colorRange use a color range for the functions values
     * @param minFVal the minimum function value in the range
     * @param maxFVal the maximum function value in the range
     */
    public ColorToFunctionRangeMap(ArrayList<Color> colorRange,
                                   double minFVal, double maxFVal) {
        this.minColor = colorRange.get(0);
        this.maxColor = colorRange.get(colorRange.size()-1);
        this.minFunctionValue = minFVal;
        this.maxFunctionValue = maxFVal;                
        this.colorRange = colorRange;
        this.useColorRange = true;
        
        minMaxFValDifference = maxFunctionValue - minFunctionValue;
        computeColorDifference();
    }
    
    /**
     * Compute the color differences.
     */
    private void computeColorDifference() {
        minR = minColor.getRed();
        minG = minColor.getGreen();
        minB = minColor.getBlue();
        minMaxRDifference = maxColor.getRed()   - minR;
        minMaxGDifference = maxColor.getGreen() - minG;
        minMaxBDifference = maxColor.getBlue()  - minB;
        
        if (useColorRange) {
            splitRatio = minMaxFValDifference/colorRange.size();            
        } // end if
    }
    
    /**
     * Holds value of property minColor.
     */
    private Color minColor;

    /**
     * Getter for property minColor.
     * @return Value of property minColor.
     */
    public Color getMinColor() {
        return this.minColor;
    }

    /**
     * Setter for property minColor.
     * @param minColor New value of property minColor.
     */
    public void setMinColor(Color minColor) {
        this.minColor = minColor;
        
        computeColorDifference();
    }

    /**
     * Holds value of property maxColor.
     */
    private Color maxColor;

    /**
     * Getter for property maxColor.
     * @return Value of property maxColor.
     */
    public Color getMaxColor() {
        return this.maxColor;
    }

    /**
     * Setter for property maxColor.
     * @param maxColor New value of property maxColor.
     */
    public void setMaxColor(Color maxColor) {
        this.maxColor = maxColor;
        
        computeColorDifference();
    }

    /**
     * Holds value of property minFunctionValue.
     */
    private double minFunctionValue;

    /**
     * Getter for property minFunctionValue.
     * @return Value of property minFunctionValue.
     */
    public double getMinFunctionValue() {
        return this.minFunctionValue;
    }

    /**
     * Setter for property minFunctionValue.
     * @param minFunctionValue New value of property minFunctionValue.
     */
    public void setMinFunctionValue(double minFunctionValue) {
        this.minFunctionValue = minFunctionValue;
        
        minMaxFValDifference = maxFunctionValue - minFunctionValue;
        
        if (useColorRange) {
            splitRatio = minMaxFValDifference/colorRange.size();            
        } // end if
    }

    /**
     * Holds value of property maxFunctionValue.
     */
    private double maxFunctionValue;

    /**
     * Getter for property maxFunctionValue.
     * @return Value of property maxFunctionValue.
     */
    public double getMaxFunctionValue() {
        return this.maxFunctionValue;
    }

    /**
     * Setter for property maxFunctionValue.
     * @param maxFunctionValue New value of property maxFunctionValue.
     */
    public void setMaxFunctionValue(double maxFunctionValue) {
        this.maxFunctionValue = maxFunctionValue;
        
        minMaxFValDifference = maxFunctionValue - minFunctionValue;
        
        if (useColorRange) {
            splitRatio = minMaxFValDifference/colorRange.size();            
        } // end if
    }
    
    /**
     * Obtain the interpolated color value for this function value in the 
     * color range. This is under the assumption that the provided function
     * value is between <code>minFunctionValue</code> and 
     * <code>maxFunctionValue</code>.
     * The color value is interpolated using the following formula:
     * <pre>
     *  Ci - Cmin        fi - fmin
     * -----------   =  -----------
     * Cmax - Cmin      fmax - fmin
     * </pre>
     *
     * @param functionValue the function value for which color value is
     *        expected.
     * @return Color object corresponding to the function value
     */
    public Color getInterpolatedColor(double functionValue) {        
        
        if (useColorRange) return getRangeInterpolatedColor(functionValue);               
        
        int R = (int) ((((functionValue - minFunctionValue) 
                         * minMaxRDifference) / minMaxFValDifference
                       ) + minR
                      );        
        int G = (int) ((((functionValue - minFunctionValue) 
                         * minMaxGDifference) / minMaxFValDifference
                       ) + minG
                      ); 
        int B = (int) ((((functionValue - minFunctionValue) 
                         * minMaxBDifference) / minMaxFValDifference
                       ) + minB
                      );                         
        
        if (R > 255)    R = 255;
        else if (R < 0) R = 0;
        if (G > 255)    G = 255;
        else if (G < 0) G = 0;
        if (B > 255)    B = 255;
        else if (B < 0) B = 0;        
        
        return new Color(R, G, B);
    }
    
    /**
     * Obtain the interpolated color value for this function value in the 
     * color range. This is under the assumption that the provided function
     * value is between <code>minFunctionValue</code> and 
     * <code>maxFunctionValue</code>.
     * The color value is interpolated using the following formula:
     * <pre>
     *  Ci - Cmin        fi - fmin
     * -----------   =  -----------
     * Cmax - Cmin      fmax - fmin
     * </pre>
     *
     * @param functionValue the function value for which color value is
     *        expected.
     * @return Color object corresponding to the function value
     */
    private Color getRangeInterpolatedColor(double functionValue) {   
        int colorRangeIndex = 0;
        double mnF = 0.0, mnmxF = 0.0;
        int mnR, mnG, mnB, mnmxR, mnmxG, mnmxB;
        
        mnR = mnG = mnB = mnmxR = mnmxG = mnmxB = 0;
        
        for(colorRangeIndex=0; 
            colorRangeIndex<colorRange.size()-1; 
            colorRangeIndex++) {
            if ((functionValue>=minFunctionValue+(colorRangeIndex*splitRatio)
            && (functionValue<minFunctionValue+((colorRangeIndex+1)*splitRatio))
            )) {                
                mnF   = minFunctionValue+(colorRangeIndex*splitRatio);
                mnmxF = splitRatio;
                
                mnR   = colorRange.get(colorRangeIndex).getRed();
                mnG   = colorRange.get(colorRangeIndex).getGreen();
                mnB   = colorRange.get(colorRangeIndex).getBlue();
        
                mnmxR = colorRange.get(colorRangeIndex+1).getRed()   - mnR;
                mnmxG = colorRange.get(colorRangeIndex+1).getGreen() - mnG;
                mnmxB = colorRange.get(colorRangeIndex+1).getBlue()  - mnB;
                
                break;
            }
        } // end for
        
        if (mnmxF != splitRatio) {
            mnF   = minFunctionValue;
            mnmxF = minMaxFValDifference;
            
            mnR = minR; mnG = minG; mnB = minB;
            mnmxR = minMaxRDifference; 
            mnmxG = minMaxGDifference; 
            mnmxB = minMaxBDifference;
        } // end id
        
        int R = (int) ((((functionValue - mnF) * mnmxR) / mnmxF) + mnR);
        int G = (int) ((((functionValue - mnF) * mnmxG) / mnmxF) + mnG);
        int B = (int) ((((functionValue - mnF) * mnmxB) / mnmxF) + mnB);        
        
        if (R > 255)    R = 255;
        else if (R < 0) R = 0;
        if (G > 255)    G = 255;
        else if (G < 0) G = 0;
        if (B > 255)    B = 255;
        else if (B < 0) B = 0;        
        
        return new Color(R, G, B);
    }
    
    /**
     * Obtain the interpolated color value for this function value in the 
     * color range with an alpha blend. This is under the assumption that the 
     * provided function value is between <code>minFunctionValue</code> and 
     * <code>maxFunctionValue</code>.
     * The color value is interpolated using the following formula:
     * <pre>
     *  Ci - Cmin        fi - fmin
     * -----------   =  -----------
     * Cmax - Cmin      fmax - fmin
     * </pre>
     *
     * Note: This function is purposely re-implemented for performance issues.
     *
     * @param functionValue the function value for which color value is
     *        expected.
     * @param alpha the alpha blend to this color
     * @return Color object corresponding to the function value
     */
    public Color getInterpolatedColor(double functionValue, int alpha) {        
        
        if (useColorRange) 
            return getRangeInterpolatedColor(functionValue, alpha); 
        
        int R = (int) ((((functionValue - minFunctionValue) 
                         * minMaxRDifference) / minMaxFValDifference
                       ) + minR
                      );        
        int G = (int) ((((functionValue - minFunctionValue) 
                         * minMaxGDifference) / minMaxFValDifference
                       ) + minG
                      ); 
        int B = (int) ((((functionValue - minFunctionValue) 
                         * minMaxBDifference) / minMaxFValDifference
                       ) + minB
                      ); 
        
        if (R > 255)    R = 255;
        else if (R < 0) R = 0;
        if (G > 255)    G = 255;
        else if (G < 0) G = 0;
        if (B > 255)    B = 255;
        else if (B < 0) B = 0;        
        if (alpha > 255)    alpha = 255;
        else if (alpha < 0) alpha = 0;        
        
        return new Color(R, G, B, alpha);
    }
    
    /**
     * Obtain the interpolated color value for this function value in the 
     * color range. This is under the assumption that the provided function
     * value is between <code>minFunctionValue</code> and 
     * <code>maxFunctionValue</code>.
     * The color value is interpolated using the following formula:
     * <pre>
     *  Ci - Cmin        fi - fmin
     * -----------   =  -----------
     * Cmax - Cmin      fmax - fmin
     * </pre>
     *
     * @param functionValue the function value for which color value is
     *        expected.
     * @return Color object corresponding to the function value
     */
    private Color getRangeInterpolatedColor(double functionValue, int alpha) {   
        int colorRangeIndex = 0;
        double mnF = 0.0, mnmxF = 0.0;
        int mnR, mnG, mnB, mnmxR, mnmxG, mnmxB;
        
        mnR = mnG = mnB = mnmxR = mnmxG = mnmxB = 0;
        
        for(colorRangeIndex=0; 
            colorRangeIndex<colorRange.size()-1; 
            colorRangeIndex++) {
            if ((functionValue>=minFunctionValue+(colorRangeIndex*splitRatio)
            && (functionValue<minFunctionValue+((colorRangeIndex+1)*splitRatio))
            )) {                
                mnF   = minFunctionValue+(colorRangeIndex*splitRatio);
                mnmxF = splitRatio;
                
                mnR   = colorRange.get(colorRangeIndex).getRed();
                mnG   = colorRange.get(colorRangeIndex).getGreen();
                mnB   = colorRange.get(colorRangeIndex).getBlue();
        
                mnmxR = colorRange.get(colorRangeIndex+1).getRed()   - mnR;
                mnmxG = colorRange.get(colorRangeIndex+1).getGreen() - mnG;
                mnmxB = colorRange.get(colorRangeIndex+1).getBlue()  - mnB;
                
                break;
            }
        } // end for
        
        int R = (int) ((((functionValue - mnF) * mnmxR) / mnmxF) + mnR);
        int G = (int) ((((functionValue - mnF) * mnmxG) / mnmxF) + mnG);
        int B = (int) ((((functionValue - mnF) * mnmxB) / mnmxF) + mnB);        
        
        if (R > 255)    R = 255;
        else if (R < 0) R = 0;
        if (G > 255)    G = 255;
        else if (G < 0) G = 0;
        if (B > 255)    B = 255;
        else if (B < 0) B = 0;        
        if (alpha > 255)    alpha = 255;
        else if (alpha < 0) alpha = 0;        
        
        return new Color(R, G, B, alpha);
    }    

    /**
     * Getter for property colorRange.
     * @return Value of property colorRange.
     */
    public ArrayList<Color> getColorRange() {
        if (this.useColorRange) {
            return this.colorRange;
        } else {
            return null;
        } // end if
    }

    /**
     * Setter for property colorRange.
     * @param colorRange New value of property colorRange.
     */
    public void setColorRange(ArrayList<Color> colorRange) {
        this.colorRange = colorRange;
        
        this.minColor = colorRange.get(0);
        this.maxColor = colorRange.get(colorRange.size()-1);
        this.useColorRange = true;
        
        minMaxFValDifference = maxFunctionValue - minFunctionValue;
        computeColorDifference();
    }

    /**
     * Getter for property splitRatio.
     * @return Value of property splitRatio.
     */
    public double getSplitRatio() {
        return this.splitRatio;
    }
} // end of class ColorToFunctionRangeMap
