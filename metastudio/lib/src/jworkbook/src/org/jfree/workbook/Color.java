/* =================================================
 * JWorkbook : data export from Java to spreadsheets
 * =================================================
 *
 * Project Info:  http://www.jfree.org/jworkbook/index.html;
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2001-2003, by Object Refinery Limited.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * ----------
 * Color.java
 * ----------
 * (C) Copyright 2001, 2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id$
 *
 * Changes:
 * --------
 * 05-Nov-2001 : Version 1 (DG);
 * 13-Feb-2002 : Renamed StyleColor.java --> Color.java, and created ColorConstants interface (DG);
 * 07-Jul-2003 : Changed GPL --> LGPL, changed package name, updated company name (DG);
 *
 */

package org.jfree.workbook;

/**
 * Represents a color within a style.
 */
public class Color implements ColorConstants {

    /** Red component. */
    protected int red;

    /** Green component. */
    protected int green;

    /** Blue component. */
    protected int blue;

    /**
     * Constructs a Color with the specified red, green and blue components.
     * 
     * @param red  the red component.
     * @param green  the green component.
     * @param blue  the blue component.
     */
    public Color(int red, int green, int blue) {

        this.red = red;
        this.green = green;
        this.blue = blue;

    }

    /**
     * Returns the red component of the color.
     * 
     * @return The red component of the color.
     */
    public int getRed() {
        return this.red;
    }

    /**
     * Returns the green component of the color.
     * 
     * @return The green component of the color.
     */
    public int getGreen() {
        return this.green;
    }

    /**
     * Returns the blue component of the color.
     * 
     * @return The blue component of the color.
     */
    public int getBlue() {
        return this.blue;
    }

    /**
     * Returns the color as a string in the format required by Gnumeric.
     * 
     * @return The color as a string in the format required by Gnumeric.
     */
    public String toString() {

        String redHex = Integer.toHexString(red);
        String greenHex = Integer.toHexString(green);
        String blueHex = Integer.toHexString(blue);
        return redHex + ":" + greenHex + ":" + blueHex;

    }

    /**
     * Returns one of the standard Excel or Gnumeric colors.
     * 
     * @param colorCode Code for one of the standard colors.  If an unrecognised code is used,
     *                  the method returns the color EXCEL_WHITE.
     * 
     * @return One of the standard Excel or Gnumeric colors.
     */
    public static Color createInstance(int colorCode) {

        switch(colorCode) {

            case GNUMERIC_BLACK:           return new Color(0x0000, 0x0000, 0x0000);

            case GNUMERIC_LIGHT_BROWN:     return new Color(0x9800, 0x3000, 0x0000);

            case GNUMERIC_BROWN_GOLD:      return new Color(0x3000, 0x3000, 0x0000);

            case GNUMERIC_DARK_GREEN_2:    return new Color(0x0000, 0x3000, 0x0000);

            case GNUMERIC_NAVY:            return new Color(0x0000, 0x3000, 0x6000);

            case GNUMERIC_DARK_BLUE:       return new Color(0x0000, 0x0000, 0x8000);

            case GNUMERIC_PURPLE_2:        return new Color(0x3000, 0x3000, 0x9800);

            case GNUMERIC_VERY_DARK_GRAY:  return new Color(0x3000, 0x3000, 0x3000);

            case GNUMERIC_DARK_RED:        return new Color(0x8000, 0x0000, 0x0000);

            case GNUMERIC_RED_ORANGE:      return new Color(0xF800, 0x6400, 0x0000);

            case GNUMERIC_GOLD:            return new Color(0x8000, 0x8000, 0x0000);

            case GNUMERIC_DARK_GREEN:      return new Color(0x0000, 0x8000, 0x0000);

            case GNUMERIC_DULL_BLUE:       return new Color(0x0000, 0x8000, 0x8000);

            case GNUMERIC_BLUE:            return new Color(0x0000, 0x0000, 0xF800);

            case GNUMERIC_DULL_PURPLE:     return new Color(0x6000, 0x6400, 0x9800);

            case GNUMERIC_DARK_GRAY:       return new Color(0x8000, 0x8000, 0x8000);

            case GNUMERIC_RED:             return new Color(0xF800, 0x0000, 0x0000);

            case GNUMERIC_ORANGE:          return new Color(0xF800, 0x9800, 0x0000);

            case GNUMERIC_LIME:            return new Color(0x9800, 0xCC00, 0x0000);

            case GNUMERIC_DULL_GREEN:      return new Color(0x3000, 0x9800, 0x6000);

            case GNUMERIC_DULL_BLUE_2:     return new Color(0x3000, 0xCC00, 0xC800);

            case GNUMERIC_SKY_BLUE_2:      return new Color(0x3000, 0x6400, 0xF800);

            case GNUMERIC_PURPLE:          return new Color(0x8000, 0x0000, 0x8000);

            case GNUMERIC_GRAY:            return new Color(0x9000, 0x9400, 0x9000);

            case GNUMERIC_MAGENTA:         return new Color(0xF800, 0x0000, 0xF800);

            case GNUMERIC_BRIGHT_ORANGE:   return new Color(0xF800, 0xCC00, 0x0000);

            case GNUMERIC_YELLOW:          return new Color(0xF800, 0xFC00, 0x0000);

            case GNUMERIC_GREEN:           return new Color(0x0000, 0xFC00, 0x0000);

            case GNUMERIC_CYAN:            return new Color(0x0000, 0xFC00, 0xF800);

            case GNUMERIC_BRIGHT_BLUE:     return new Color(0x0000, 0xCC00, 0xF800);

            case GNUMERIC_RED_PURPLE:      return new Color(0x9800, 0x3000, 0x6000);

            case GNUMERIC_LIGHT_GRAY:      return new Color(0xC000, 0xC000, 0xC000);

            case GNUMERIC_PINK:            return new Color(0xF800, 0x9800, 0xC800);

            case GNUMERIC_LIGHT_ORANGE:    return new Color(0xF800, 0xCC00, 0x9800);

            case GNUMERIC_LIGHT_YELLOW:    return new Color(0xF800, 0xFC00, 0x9800);

            case GNUMERIC_LIGHT_GREEN:     return new Color(0xC800, 0xFC00, 0xC800);

            case GNUMERIC_LIGHT_CYAN:      return new Color(0xC800, 0xFC00, 0xF800);

            case GNUMERIC_LIGHT_BLUE:      return new Color(0x9800, 0xCC00, 0xF800);

            case GNUMERIC_LIGHT_PURPLE:    return new Color(0xC800, 0x9800, 0xF800);

            case GNUMERIC_WHITE:           return new Color(0xFFFF, 0xFFFF, 0xFFFF);

            case EXCEL_BROWN:              return new Color(0x9999, 0x3333, 0x0000);

            case EXCEL_OLIVE_GREEN:        return new Color(0x3333, 0x3333, 0x0000);

            case EXCEL_DARK_GREEN:         return new Color(0x0000, 0x3333, 0x0000);

            case EXCEL_DARK_TEAL:          return new Color(0x0000, 0x3333, 0x6666);

            case EXCEL_DARK_BLUE:          return new Color(0x0000, 0x0000, 0x8080);

            case EXCEL_INDIGO:             return new Color(0x3333, 0x3333, 0x9999);

            case EXCEL_GRAY_80PERCENT:     return new Color(0x3333, 0x3333, 0x3333);

            case EXCEL_DARK_RED:           return new Color(0x8080, 0x0000, 0x0000);

            case EXCEL_ORANGE:             return new Color(0xFFFF, 0x6666, 0x0000);

            case EXCEL_DARK_YELLOW:        return new Color(0x8080, 0x8080, 0x0000);

            case EXCEL_GREEN:              return new Color(0x0000, 0x8080, 0x0000);

            case EXCEL_TEAL:               return new Color(0x0000, 0x8080, 0x8080);

            case EXCEL_BLUE:               return new Color(0x0000, 0x0000, 0xFFFF);

            case EXCEL_BLUE_GRAY:          return new Color(0x6666, 0x6666, 0x9999);

            case EXCEL_GRAY_50PERCENT:     return new Color(0x8080, 0x8080, 0x8080);

            case EXCEL_RED:                return new Color(0xFFFF, 0x0000, 0x0000);

            case EXCEL_LIGHT_ORANGE:       return new Color(0xFFFF, 0x9999, 0x0000);

            case EXCEL_LIME:               return new Color(0x9999, 0xCCCC, 0x0000);

            case EXCEL_SEA_GREEN:          return new Color(0x3333, 0x9999, 0x6666);

            case EXCEL_AQUA:               return new Color(0x3333, 0xCCCC, 0xCCCC);

            case EXCEL_LIGHT_BLUE:         return new Color(0x3333, 0x6666, 0xFFFF);

            case EXCEL_VIOLET:             return new Color(0x8080, 0x0000, 0x8080);

            case EXCEL_GRAY_40PERCENT:     return new Color(0x9696, 0x9696, 0x9696);

            case EXCEL_PINK:               return new Color(0xFFFF, 0x0000, 0xFFFF);

            case EXCEL_GOLD:               return new Color(0xFFFF, 0xCCCC, 0x0000);

            case EXCEL_YELLOW:             return new Color(0xFFFF, 0xFFFF, 0x0000);

            case EXCEL_BRIGHT_GREEN:       return new Color(0x0000, 0xFFFF, 0x0000);

            case EXCEL_TURQUOISE:          return new Color(0x0000, 0xFFFF, 0xFFFF);

            case EXCEL_SKY_BLUE:           return new Color(0x0000, 0xCCCC, 0xFFFF);

            case EXCEL_PLUM:               return new Color(0x9999, 0x3333, 0x6666);

            case EXCEL_GRAY_25PERCENT:     return new Color(0xC0C0, 0xC0C0, 0xC0C0);

            case EXCEL_ROSE:               return new Color(0xFFFF, 0x9999, 0xCCCC);

            case EXCEL_TAN:                return new Color(0xFFFF, 0xCCCC, 0x9999);

            case EXCEL_LIGHT_YELLOW:       return new Color(0xFFFF, 0xFFFF, 0x9999);

            case EXCEL_LIGHT_GREEN:        return new Color(0xCCCC, 0xFFFF, 0xCCCC);

            case EXCEL_LIGHT_TURQUOISE:    return new Color(0xCCCC, 0xFFFF, 0xFFFF);

            case EXCEL_PALE_BLUE:          return new Color(0x9999, 0xCCCC, 0xFFFF);

            case EXCEL_LAVENDER:           return new Color(0xCCCC, 0x9999, 0xFFFF);

            default:                       return new Color(0xFFFF, 0xFFFF, 0xFFFF);

        }

    }

}
