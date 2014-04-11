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
 * ------------------
 * RowAttributes.java
 * ------------------
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
 * 13-Feb-2002 : Added new accessor methods (DG);
 * 07-Jul-2003 : Changed GPL --> LGPL, changed package name, updated company name (DG);
 *
 */

package org.jfree.workbook;

/**
 * Stores the attributes for a range of rows (or sometimes just one row).
 */
public class RowAttributes implements Comparable<RowAttributes> {

    /** The starting row number (0 to Worksheet.MAX_ROWS-1). */
    protected int startRow;

    /** The ending row number (startRow to Worksheet.MAX_ROWS-1). */
    protected int endRow;

    /** The row height. */
    protected double height;

    /** The left margin. */
    protected int marginA;

    /** The right margin. */
    protected int marginB;

    /** Not sure what this is (Gnumeric uses it for something). */
    protected boolean hardSize;

    /** Flag that indicates whether this row is hidden. */
    protected boolean hidden;

    /**
     * Standard constructor: uses default values.
     * 
     * @param start  the start row.
     * @param end  the end row.
     */
    public RowAttributes(int start, int end) {

        this(start, end, Worksheet.DEFAULT_ROW_HEIGHT);

    }

    /**
     * Standard constructor.
     * 
     * @param start  the start row.
     * @param end  the end row.
     * @param height  the row height.
     */
    public RowAttributes(int start, int end, double height) {

        this.startRow = start;
        this.endRow = end;
        this.height = height;

    }

    /**
     * Returns the start row.
     * 
     * @return The start row.
     */
    public int getStartRow() {
        return this.startRow;
    }

    /**
     * Returns the end row.
     * 
     * @return The end row.
     */
    public int getEndRow() {
        return this.endRow;
    }

    /**
     * Returns the row height.
     * 
     * @return The row height.
     */
    public double getHeight() {
        return this.height;
    }

    /**
     * Sets the row height.
     * 
     * @param height  the new row height.
     */
    public void setHeight(double height) {
        this.height = height;
    }

    /**
     * Returns margin A.
     * 
     * @return Margin A.
     */
    public int getMarginA() {
        return this.marginA;
    }

    /**
     * Returns margin B.
     * 
     * @return Margin B.
     */
    public int getMarginB() {
        return this.marginB;
    }

    /**
     * Returns a flag indicating ???.
     * 
     * @return A boolean.
     */
    public boolean isHardSize() {
        return this.hardSize;
    }

    /**
     * Returns a flag indicating whether or not the row is hidden.
     * 
     * @return A boolean.
     */
    public boolean isHidden() {
        return this.hidden;
    }

    /**
     * Returns true if this attributes record applies to the specified row.
     * 
     * @param row  the row.
     * 
     * @return A boolean.
     */
    public boolean includesRow(int row) {

        return ((row >= startRow) && (row <= endRow));

    }

    /**
     * Returns a new RowAttributes object, based on this one but only up to the specified row.
     * 
     * @param split  the row.
     * 
     * @return The row attributes.
     */
    public RowAttributes getSplitToRow(int split) {
        return new RowAttributes(this.startRow, split, this.height);
    }

    /**
     * Returns a new RowAttributes object, based on this one but only from the specified row.
     * 
     * @param split  the row.
     * 
     * @return The row attributes.
     */
    public RowAttributes getSplitFromRow(int split) {
        return new RowAttributes(split, this.endRow, this.height);
    }

    /**
     * Returns a new RowAttributes object, based on this one but only for the specified range of
     * rows.
     * 
     * @param r1  the start row.
     * @param r2  the end row.
     * 
     * @return The row attributes.
     */
    public RowAttributes getSubset(int r1, int r2) {
        return new RowAttributes(r1, r2, this.height);
    }

    /**
     * Implements the Comparable interface.
     * 
     * @param other  the object to compare against.
     * 
     * @return An integer that indicates the relative order of the objects.
     */
    public int compareTo(RowAttributes ra) {       
        return this.startRow - ra.startRow;        
    }

}
