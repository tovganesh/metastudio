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
 * ---------------
 * Selections.java
 * ---------------
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Stores information about all the selection ranges in a worksheet.
 */
public class Selections {

    /** The column in which the cursor is located. */
    protected int cursorColumn;

    /** The row in which the cursor is located. */
    protected int cursorRow;

    /** A list of all selections. */
    protected List<Selection> selections;

    /**
     * Default constructor.
     */
    public Selections() {

        this.cursorColumn = 0;
        this.cursorRow = 0;
        this.selections = new ArrayList<Selection>();
        this.selections.add(new Selection(0, 0, 0, 0));

    }

    /**
     * Returns the cursor column.
     * 
     * @return The column.
     */
    public int getCursorColumn() {
        return this.cursorColumn;
    }

    /**
     * Returns the cursor row.
     * 
     * @return The row.
     */
    public int getCursorRow() {
        return this.cursorRow;
    }

    /**
     * Returns an iterator that provides access to the selections.
     * 
     * @return The iterator.
     */
    public Iterator getIterator() {
        return selections.iterator();
    }

}
