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
 * -------------------------
 * GeneralStyleModifier.java
 * -------------------------
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
 * 07-Jul-2003 : Changed GPL --> LGPL, changed package name, updated company name (DG);
 *
 */

package org.jfree.workbook;

/**
 * A general style modifier that can change a variety of style settings.
 */
public class GeneralStyleModifier implements StyleModifier {

    /** Change the horizontal alignment (yes/no). */
    protected boolean modifyHorizontalAlignment;

    /** The new horizontal alignment, if required. */
    protected int horizontalAlignment;

    /** Change the vertical alignment (yes/no). */
    protected boolean modifyVerticalAlignment;

    /** The new vertical alignment, if required. */
    protected int verticalAlignment;

    /** Change the wrap text setting (yes/no). */
    protected boolean modifyWrapText;

    /** The new wrap text setting, if required. */
    protected boolean wrapText;

    /**
     * Standard constructor.
     * 
     * @param modifyHorizontalAlignment  change the horizontal alignment (yes/no).
     * @param horizontalAlignment  the new horizontal alignment.
     * @param modifyVerticalAlignment  change the vertical alignment (yes/no).
     * @param verticalAlignment  the new vertical alignment.
     */
    public GeneralStyleModifier(boolean modifyHorizontalAlignment, int horizontalAlignment,
                                boolean modifyVerticalAlignment, int verticalAlignment) {

        this.modifyHorizontalAlignment = modifyHorizontalAlignment;
        this.horizontalAlignment = horizontalAlignment;
        this.modifyVerticalAlignment = modifyVerticalAlignment;
        this.verticalAlignment = verticalAlignment;

        this.modifyWrapText = false;
        this.wrapText = false;

    }

    /**
     * Standard constructor.
     * 
     * @param modifyWrapText  change the wrap text setting (yes/no).
     * @param wrapText  the new wrap text setting.
     */
    public GeneralStyleModifier(boolean modifyWrapText, boolean wrapText) {
        this.modifyWrapText = modifyWrapText;
        this.wrapText = wrapText;
    }

    /**
     * Returns a new style based on the supplied style, but modified in some way.
     * 
     * @param style  the style to be changed.
     * 
     * @return The modified style.
     */
    public Style getModifiedStyle(Style style) {

        int newHorizontalAlignment;
        int newVerticalAlignment;
        boolean newWrapText;

        if (modifyHorizontalAlignment) {
            newHorizontalAlignment = horizontalAlignment;
        }
        else {
            newHorizontalAlignment = style.getHorizontalAlignment();
        }

        if (modifyVerticalAlignment) {
            newVerticalAlignment = verticalAlignment;
        }
        else {
            newVerticalAlignment = style.getVerticalAlignment();
        }

        if (modifyWrapText) {
            newWrapText = wrapText;
        }
        else {
            newWrapText = style.isWrapText();
        }

        return new Style(style.getFont(), style.getBorder(),
                         newHorizontalAlignment, newVerticalAlignment,
                         newWrapText, style.getOrientation(), style.getShade(), 
                         style.getCellIndent(),
                         style.getForegroundColor(), style.getBackgroundColor(),
                         style.getPatternColor(), style.getFormat());

    }

}
