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
 * -----------
 * Border.java
 * -----------
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
 * 12-Feb-2002 : Renamed StyleBorder.java --> Border.java (DG);
 * 07-Jul-2003 : Changed GPL --> LGPL, changed package name, updated company name (DG);
 *
 */

package org.jfree.workbook;

/**
 * Represents the border settings for a style range.
 * <P>
 * Doesn't support color borders yet.
 */
public class Border implements BorderConstants {

    /** The top border. */
    protected int top;

    /** The bottom border. */
    protected int bottom;

    /** The left border. */
    protected int left;

    /** The right border. */
    protected int right;

    /** The diagonal 'border'. */
    protected int diagonal;

    /** The reverse diagonal 'border'. */
    protected int reverseDiagonal;

    /**
     * Standard constructor.
     * 
     * @param top  the top border style.
     * @param bottom  the bottom border style.
     * @param left  the left border style.
     * @param right  the right border style.
     * @param diagonal  the diagonal border style.
     * @param reverseDiagonal The reverse diagonal border style.
     */
    public Border(int top, int bottom, int left, int right,
                  int diagonal, int reverseDiagonal) {

        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
        this.diagonal = diagonal;
        this.reverseDiagonal = reverseDiagonal;

    }

    /**
     * Returns the line type for the border at the top of a cell.
     * 
     * @return The line type.
     */
    public int getTop() {
        return this.top;
    }

    /**
     * Returns the line type for the border at the bottom of a cell.
     * 
     * @return The line type.
     */
    public int getBottom() {
        return this.bottom;
    }

    /**
     * Returns the line type for the border at the left of a cell.
     * 
     * @return The line type.
     */
    public int getLeft() {
        return this.left;
    }

    /**
     * Returns the line type for the border at the right of a cell.
     * 
     * @return The line type.
     */
    public int getRight() {
        return this.right;
    }

    /**
     * Returns the line type for the border diagonally crossing a cell.
     * 
     * @return The line type.
     */
    public int getDiagonal() {
        return this.diagonal;
    }

    /**
     * Returns the line type for the border reverse-diagonally crossing a cell.
     * 
     * @return The line type.
     */
    public int getReverseDiagonal() {
        return this.reverseDiagonal;
    }

}
