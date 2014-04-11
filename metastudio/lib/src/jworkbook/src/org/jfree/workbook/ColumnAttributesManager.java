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
 * ----------------------------
 * ColumnAttributesManager.java
 * ----------------------------
 * (C) Copyright 2001, 2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id$
 *
 * Changes:
 * ---------------
 * 05-Nov-2001 : Version 1 (DG);
 * 12-Feb-2002 : Implemented GnumericWriter and XLWriter (DG);
 * 07-Jul-2003 : Changed GPL --> LGPL, changed package name, updated company name (DG);
 *
 */

package org.jfree.workbook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Records information about the columns in a worksheet.
 */
public class ColumnAttributesManager {

    /** The default column width. */
    protected double defaultColumnWidth;

    /** List of ColumnAttributes objects. */
    protected List<ColumnAttributes> attributes;

    /**
     * Constructs a new ColumnAttributesManager.
     */
    public ColumnAttributesManager() {

        this.defaultColumnWidth = Worksheet.DEFAULT_COLUMN_WIDTH;
        this.attributes = new ArrayList<ColumnAttributes>();

    }

    /**
     * Returns the default column width.
     * 
     * @return The default column width.
     */
    public double getDefaultColumnWidth() {
        return this.defaultColumnWidth;
    }

    /**
     * Sets the default column width.
     * 
     * @param width  the new default column width.
     */
    public void setDefaultColumnWidth(double width) {
        this.defaultColumnWidth = width;
    }

    /**
     * Sets the width of a range of columns.
     * 
     * @param startColumn  the start column (0 <= startColumn < Worksheets.MAX_COLUMNS).
     * @param endColumn  the end column (startColumn <= endColumn < Worksheets.MAX_COLUMNS).
     * @param width  the new width.
     */
    public void setColumnWidth(int startColumn, int endColumn, double width) {

        // check parameters...columns get checked later...
        if (width < 0) {
            throw new IllegalArgumentException("setColumnWidth(): width must be at least 0.");
        }

        ColumnAttributesModifier modifier = new ColumnWidthModifier(width);
        modifyColumnAttributes(modifier, startColumn, endColumn);

    }

    /**
     * General method for modifying column attributes.
     * 
     * @param modifier  the modifier.
     * @param c1  the start column.
     * @param c2  the end column.
     */
    public void modifyColumnAttributes(ColumnAttributesModifier modifier, int c1, int c2) {

        // check the columns are in valid range...
        if ((c1 < 0) || (c1 >= Worksheet.MAX_COLUMNS)) {
            throw new IllegalArgumentException(
                                    "modifyColumnAttributes(): c1 outside valid range.");
        }

        if ((c2 < 0) || (c2 >= Worksheet.MAX_COLUMNS)) {
            throw new IllegalArgumentException(
                                      "modifyColumnAttributes(): c2 outside valid range.");
        }

        if (c1 > c2) {
            throw new IllegalArgumentException("modifyColumnAttributes(): c1 > c2.");
        }

        // now iterate over existing records to restructure as required...
        List<ColumnAttributes> addList = new ArrayList<ColumnAttributes>();
        List<ColumnAttributes> removeList = new ArrayList<ColumnAttributes>();

        ColumnAttributes split1 = null;
        ColumnAttributes split2 = null;

        Iterator<ColumnAttributes> iterator = attributes.iterator();
        while (iterator.hasNext()) {
            ColumnAttributes existing = (ColumnAttributes) iterator.next();

            if (c1 < existing.startColumn) { // 1,2,3,4
                if (c2 < existing.startColumn) {
                    // CASE 1 : do nothing
                }
                else { // 2,3,4
                    if (c2 < existing.endColumn) {
                        // CASE 2
                        removeList.add(existing);
                        split1 = existing.getSplitToColumn(c2);
                        modifier.modifyColumnAttributes(split1);
                        split2 = existing.getSplitFromColumn(c2 + 1);
                        addList.add(split1);
                        addList.add(split2);
                    }
                    else if (c2 == existing.endColumn) {
                        // CASES 3
                        modifier.modifyColumnAttributes(existing);
                        c2 = existing.startColumn - 1;
                    }
                    else if (c2 > existing.endColumn) {
                        modifier.modifyColumnAttributes(existing);
                        c1 = existing.endColumn + 1;  // we are discarding the earlier part
                    }
                }
            }
            else {  // 5,6,7,8,9,10,11
                if (c1 == existing.startColumn) { // 5,6,7
                    if (c2 < existing.endColumn) {
                        // CASE 5
                        removeList.add(existing);
                        split1 = existing.getSplitToColumn(c2);
                        modifier.modifyColumnAttributes(split1);
                        split2 = existing.getSplitFromColumn(c2 + 1);
                        addList.add(split1);
                        addList.add(split2);
                        c1 = -1;
                        c2 = -1;
                    }
                    else {
                        if (c2 == existing.endColumn) {
                            // CASE 6
                            modifier.modifyColumnAttributes(existing);
                            c1 = -1;
                            c2 = -1;
                        }
                        else {
                            // CASE 7
                            modifier.modifyColumnAttributes(existing);
                            c1 = existing.endColumn + 1;
                        }
                    }
                }
                else { // 8,9,10,11

                    if (c2 < existing.endColumn) {
                        // CASE 8
                        removeList.add(existing);
                        split1 = existing.getSplitToColumn(c1 - 1);
                        split2 = existing.getSplitFromColumn(c2 + 1);
                        ColumnAttributes subset = existing.getSubset(c1, c2);
                        modifier.modifyColumnAttributes(subset);
                        addList.add(split1);
                        addList.add(split2);
                        addList.add(subset);
                        c1 = -1;
                        c2 = -1;
                    }
                    else if (c2 == existing.endColumn) { //9
                        removeList.add(existing);
                        split1 = existing.getSplitToColumn(c1 - 1);
                        split2 = existing.getSplitFromColumn(c1);
                        modifier.modifyColumnAttributes(split2);
                        addList.add(split1);
                        addList.add(split2);
                        c1 = -1;
                        c2 = -1;
                    }
                    else { //10,11
                        if (c1 <= existing.endColumn) {
                            removeList.add(existing);
                            split1 = existing.getSplitToColumn(c1 - 1);
                            split2 = existing.getSplitFromColumn(c1);
                            modifier.modifyColumnAttributes(split2);
                            addList.add(split1);
                            addList.add(split2);
                            c1 = existing.endColumn + 1;
                        }
                        else {
                            // CASE 11 : do nothing
                        }
                    }

                }
            }
        }

        if ((c1 >= 0) && (c2 >= 0)) {
            ColumnAttributes newAttributes = new ColumnAttributes(c1, c2);
            modifier.modifyColumnAttributes(newAttributes);
            addList.add(newAttributes);
        }

        attributes.removeAll(removeList);
        attributes.addAll(addList);
        Collections.sort(attributes);

    }

    /**
     * Returns an iterator that provides access to the attribute records.
     * 
     * @return The iterator.
     */
    public Iterator getAttributesIterator() {

        return attributes.iterator();

    }

}
