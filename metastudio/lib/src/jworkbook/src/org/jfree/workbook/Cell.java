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
 * ---------
 * Cell.java
 * ---------
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
 * 13-Feb-2002 : Implemented CellConstants and new accessor methods (DG);
 * 07-Jul-2003 : Changed GPL --> LGPL, changed package name, updated company name (DG);
 *
 */

package org.jfree.workbook;

/**
 * Represents one cell in a worksheet.
 */
public class Cell implements CellConstants, Comparable<Cell> {

    /** The column number (0 to Worksheet.MAX_COLUMNS-1). */
    protected int column;

    /** The row number (0 to Worksheet.MAX_ROWS-1). */
    protected int row;

    /**
     * The type of data in the cell.
     * So far I know of: 30=date, 40=value, 60=label;
     * For expressions, no value type is shown, so assume 0 for now.
     */
    protected int type;

    /** The cell contents. */
    protected String content;

    /** The value format (if required). */
    protected String valueFormat;

    /**
     * Constructs a new cell.
     * 
     * @param type  the cell type.
     * @param content  the cell contents.
     * @param row  the row.
     * @param column  the column.
     */
    protected Cell(int type, String content, int row, int column) {

        this.type = type;
        this.content = content;
        this.row = row;
        this.column = column;

    }

    /**
     * Returns the cell's column.
     * 
     * @return The column.
     */
    public int getColumn() {
        return this.column;
    }

    /**
     * Returns the cell's row.
     * 
     * @return The row.
     */
    public int getRow() {
        return this.row;
    }

    /**
     * Returns the cell's type.
     * 
     * @return The type.
     */
    public int getType() {
        return this.type;
    }

    /**
     * Returns the cell's content.
     * 
     * @return The content.
     */
    public String getContent() {
        return this.content;
    }

    /**
     * Implements the Comparable interface so that cells can easily be sorted.
     * 
     * @param other The object being compared to.
     * 
     * @return An integer that indicates the relative order of the objects.
     */
    public int compareTo(Cell otherCell) {        
        return this.getSerialNumber() - otherCell.getSerialNumber();
    }

    /**
     * Returns an integer that increases across columns and down rows.
     * 
     * @return A serial number for the cell.
     */
    protected int getSerialNumber() {
        return (row * Worksheet.MAX_COLUMNS) + column;
    }

}
