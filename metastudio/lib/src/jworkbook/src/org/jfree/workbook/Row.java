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
 * --------
 * Row.java
 * --------
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
 * 12-Feb-2002 : Added iterator access to the cells in the row (DG);
 * 07-Jul-2003 : Changed GPL --> LGPL, changed package name, updated company name (DG);
 *
 */

package org.jfree.workbook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a single row of cells in a worksheet.
 */
public class Row implements Comparable<Row> {

    /** The row index (0 to Worksheet.MAX_ROWS). */
    protected int index;

    /** The cells in the row. */
    protected List<Cell> cells;

    /**
     * Constructs an empty row with the specified index.
     * 
     * @param index  the row index.
     */
    public Row(int index) {

        this.cells = new ArrayList<Cell>();
        this.index = index;

    }

    /**
     * Returns the index of the row.
     * 
     * @return The index of the row.
     */
    public int getIndex() {
        return this.index;
    }

    /**
     * Returns an iterator that provides access to the cells in the row.
     * 
     * @return The iterator.
     */
    public Iterator getCellsIterator() {
        return cells.iterator();
    }

    /**
     * Adds a cell to the row, replacing the existing cell if any.  Cells are be maintained in
     * column order.
     * 
     * @param cell  the cell to add.
     */
    public void add(Cell cell) {

        int position = Collections.binarySearch(cells, cell);
        if (position > 0) {
            cells.remove(position);
            cells.add(position, cell);
        }
        else {
            cells.add(-position - 1, cell);
        }

    }

    /**
     * Returns the cell at the specified column.
     * 
     * @param column  the column.
     * 
     * @return The cell at the specified column.
     */
    public Cell get(int column) {
        Cell key = new Cell(CellConstants.LABEL_TYPE, "KEY", this.index, column);
        int arrayIndex = Collections.binarySearch(cells, key);
        return (Cell) cells.get(arrayIndex);

    }

    /**
     * Implements the Comparator interface so that the rows can easily be sorted into order.
     * 
     * @param other  the object to compare against.
     * 
     * @return An int that indicates the relative order of the objects.
     */
    public int compareTo(Row otherRow) {        
        return index - otherRow.getIndex();        
    }

}
