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
 * -----------------------
 * ColorStyleModifier.java
 * -----------------------
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
 * 13-Feb-2002 : Renamed StyleColorModifier.java --> ColorStyleModifier.java (DG);
 * 07-Jul-2003 : Changed GPL --> LGPL, changed package name, updated company name (DG);
 *
 */

package org.jfree.workbook;

/**
 * A style modifier that changes the foreground, background or pattern color for a style, but leaves
 * all other settings unchanged.
 */
public class ColorStyleModifier implements StyleModifier {

    /** Foreground, background or pattern color. */
    protected int which;

    /** The new color. */
    protected Color color;

    /**
     * Standard constructor.
     * 
     * @param which  indicates which color to change (use constants BACKGROUND_COLOR,
     *               FOREGROUND_COLOR and PATTERN_COLOR in the StyleColor class).
     * @param color  the new color.
     */
    public ColorStyleModifier(int which, Color color) {
        this.which = which;
        this.color = color;
    }

    /**
     * Returns a new style with the same settings as the style passed in, except with a different
     * foreground, background or pattern color.
     * 
     * @param style  the style to be modified.
     * 
     * @return A new style with the same settings as the style passed in, except with a different
     *         foreground, background or pattern color.
     */
    public Style getModifiedStyle(Style style) {

        switch(which) {

            case Color.BACKGROUND_COLOR: 
                return Style.applyBackgroundColor(style, color);

            case Color.FOREGROUND_COLOR: 
                return Style.applyForegroundColor(style, color);

            case Color.PATTERN_COLOR: 
                return Style.applyPatternColor(style, color);

            default: return null;

        }

    }

}
