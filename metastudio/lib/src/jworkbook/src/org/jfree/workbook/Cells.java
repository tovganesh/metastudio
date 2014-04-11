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
 * Cells.java
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
 * 13-Feb-2002 : Added an iterator for access to rows (DG);
 * 07-Jul-2003 : Changed GPL --> LGPL, changed package name, updated company name (DG);
 *
 */

package org.jfree.workbook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Represents the cells in a worksheet.
 */
public class Cells {

    /** A list of the rows of cells. */
    protected ArrayList<Row> rows;

    /**
     * Default constructor.
     */
    public Cells() {
        rows = new ArrayList<Row>();
    }

    /**
     * Adds the specified cell to the collection of cells (if the cell already exists it is
     * replaced).
     * @param cell The cell to add.
     */
    public void add(Cell cell) {

        Row key = new Row(cell.getRow());
        int rowIndex = Collections.binarySearch(rows, key);
        if (rowIndex >= 0) {
            Row working = (Row) rows.get(rowIndex);
            working.add(cell);
        }
        else {
            rows.add(-rowIndex - 1, key);
            key.add(cell);
        }

    }

    /**
     * Returns an iterator that provides access to the rows.
     * 
     * @return An iterator.
     */
    public Iterator getRowsIterator() {
        return rows.iterator();
    }

}
