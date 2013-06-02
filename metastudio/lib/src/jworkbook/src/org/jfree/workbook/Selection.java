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
 * --------------
 * Selection.java
 * --------------
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
 * 13-Feb-2002 : Added accessor methods and moved some code to GnumericWriter (DG);
 * 07-Jul-2003 : Changed GPL --> LGPL, changed package name, updated company name (DG);
 *
 */

package org.jfree.workbook;

/**
 * Stores information on a selection range in a worksheet.
 */
public class Selection {

    /** The start column for the selection. */
    protected int startColumn;

    /** The start row for the selection. */
    protected int startRow;

    /** The end column for the selection. */
    protected int endColumn;

    /** The end row for the selection. */
    protected int endRow;

    /**
     * Standard constructor.
     * 
     * @param startColumn  the start column.
     * @param startRow  the start row.
     * @param endColumn  the end column.
     * @param endRow  the end row.
     */
    public Selection(int startColumn, int startRow, int endColumn, int endRow) {
        this.startColumn = startColumn;
        this.startRow = startRow;
        this.endColumn = endColumn;
        this.endRow = endRow;
    }

    /**
     * Returns the selection's start column.
     * 
     * @return The column.
     */
    public int getStartColumn() {
        return this.startColumn;
    }

    /**
     * Returns the selection's start row.
     * 
     * @return The row.
     */
    public int getStartRow() {
        return this.startRow;
    }

    /**
     * Returns the selection's end column.
     * 
     * @return The column.
     */
    public int getEndColumn() {
        return this.endColumn;
    }

    /**
     * Returns the selection's end row.
     * 
     * @return The row.
     */
    public int getEndRow() {
        return this.endRow;
    }

}
