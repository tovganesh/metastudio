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
 * ---------------------
 * ColumnAttributes.java
 * ---------------------
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
 * 12-Feb-2002 : Implemented GnumericWriter and XLWriter (DG);
 * 07-Jul-2003 : Changed GPL --> LGPL, changed package name, updated company name (DG);
 *
 */

package org.jfree.workbook;

/**
 * Stores the attributes for a range of columns (or sometimes just one column).
 */
public class ColumnAttributes implements Comparable<ColumnAttributes> {

    /** The starting column number (0 to Worksheet.MAX_COLUMNS-1). */
    protected int startColumn;

    /** The ending column number (startColumn to Worksheet.MAX_COLUMNS-1). */
    protected int endColumn;

    /** The column width in pts. */
    protected double width;

    /** The left margin. */
    protected int marginA;

    /** The right margin. */
    protected int marginB;

    /** Not sure what this is (Gnumeric uses it for something). */
    protected boolean hardSize;

    /** Flag that indicates whether this column is hidden. */
    protected boolean hidden;

    /**
     * Constructs a new ColumnAttributes object, using default values where necessary.
     * 
     * @param start  the start column.
     * @param end  the end column.
     */
    public ColumnAttributes(int start, int end) {

        this(start, end, Worksheet.DEFAULT_COLUMN_WIDTH);

    }

    /**
     * Constructs a new ColumnAttributes object.
     * 
     * @param start  the start column.
     * @param end  the end column.
     * @param width  the column width.
     */
    public ColumnAttributes(int start, int end, double width) {

        this.startColumn = start;
        this.endColumn = end;
        this.width = width;

    }

    /**
     * Returns the start column.
     * 
     * @return The start column.
     */
    public int getStartColumn() {
        return this.startColumn;
    }

    /**
     * Returns the end column.
     * 
     * @return The end column.
     */
    public int getEndColumn() {
        return this.endColumn;
    }

    /**
     * Returns the column width.
     * 
     * @return The column width.
     */
    public double getWidth() {
        return this.width;
    }

    /**
     * Sets the column width.
     * 
     * @param width  the new column width.
     */
    public void setWidth(double width) {
        this.width = width;
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
     * Returns a flag indicating whether or not the column is hidden.
     * 
     * @return A boolean.
     */
    public boolean isHidden() {
        return this.hidden;
    }

    /**
     * Returns a new ColumnAttributes object, based on this one but only up to the specified column.
     * 
     * @param split  the column.
     * 
     * @return The column attributes.
     */
    public ColumnAttributes getSplitToColumn(int split) {
        return new ColumnAttributes(this.startColumn, split, this.width);
    }

    /**
     * Returns a new ColumnAttributes object, based on this one but only from the specified column.
     * 
     * @param split  the column.
     * 
     * @return The column attributes.
     */
    public ColumnAttributes getSplitFromColumn(int split) {
        return new ColumnAttributes(split, this.endColumn, this.width);
    }

    /**
     * Returns a new ColumnAttributes object, based on this one but only for the specified range of
     * columns.
     * 
     * @param c1  the start column.
     * @param c2  the end column.
     * 
     * @return The column attributes.
     */
    public ColumnAttributes getSubset(int c1, int c2) {
        return new ColumnAttributes(c1, c2, this.width);
    }

    /**
     * Implements the Comparable interface.
     * 
     * @param other The object to compare against.
     * 
     * @return An integer indicating the relative order of the objects.
     */
    public int compareTo(ColumnAttributes ca) {       
        return this.startColumn - ca.startColumn;        
    }
}
