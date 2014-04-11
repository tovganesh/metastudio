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
 * RowAttributesManager.java
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
 * 12-Feb-2002 : Moved some code to GnumericWriter.java (DG);
 * 07-Jul-2003 : Changed GPL --> LGPL, changed package name, updated company name (DG);
 *
 */

package org.jfree.workbook;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Records information about the rows in a worksheet, row height, visibility etc.
 */
public class RowAttributesManager {

    /** The default row height. */
    protected double defaultRowHeight;

    /** List of RowAttributes objects. */
    protected List<RowAttributes> attributes;

    /**
     * Constructs a RowAttributesManager.
     */
    public RowAttributesManager() {

        this.defaultRowHeight = Worksheet.DEFAULT_ROW_HEIGHT;
        this.attributes = new ArrayList<RowAttributes>();

    }

    /**
     * Returns the default row height.
     * 
     * @return The default row height.
     */
    public double getDefaultRowHeight() {
        return this.defaultRowHeight;
    }

    /**
     * Sets the default row height.
     * 
     * @param height  the height.
     */
    public void setDefaultRowHeight(double height) {
        this.defaultRowHeight = height;
    }

    /**
     * Returns the height of a particular row.
     * 
     * @param row  the row.
     * 
     * @return The row height.
     */
    public double getHeight(int row) {

        double result = this.defaultRowHeight;

        Iterator iterator = attributes.iterator();
        while (iterator.hasNext()) {
            RowAttributes ra = (RowAttributes) iterator.next();
            if (ra.includesRow(row)) {
                result = ra.getHeight();
            } 
        }

        return result;

    }

    /**
     * Sets the height of a range of rows.
     * 
     * @param startRow  the start row.
     * @param endRow  the end row.
     * @param height  the new height.
     */
    public void setRowHeight(int startRow, int endRow, double height) {

        RowAttributesModifier modifier = new RowHeightModifier(height);
        modifyRowAttributes(modifier, startRow, endRow);

    }

    /**
     * General method for modifying row attributes.
     * 
     * @param modifier  handles modification to individual row attribute records.
     * @param r1  the start row.
     * @param r2  the end row.
     */
    public void modifyRowAttributes(RowAttributesModifier modifier, int r1, int r2) {

        // check the rows are in valid range...
        if ((r1 < 0) || (r1 >= Worksheet.MAX_ROWS)) {
            throw new IllegalArgumentException("modifyRowAttributes(): r1 outside valid range.");
        }

        if ((r2 < 0) || (r2 >= Worksheet.MAX_ROWS)) {
            throw new IllegalArgumentException("modifyRowAttributes(): r2 outside valid range.");
        }

        if (r1 > r2) {
            throw new IllegalArgumentException("modifyRowAttributes(): r1 > r2.");
        }

        // now iterate over existing records to restructure as required...
        List<RowAttributes> addList = new ArrayList<RowAttributes>();
        List<RowAttributes> removeList = new ArrayList<RowAttributes>();

        RowAttributes split1 = null;
        RowAttributes split2 = null;

        Iterator iterator = attributes.iterator();
        while (iterator.hasNext()) {
            RowAttributes existing = (RowAttributes) iterator.next();

            if (r1 < existing.startRow) { // 1,2,3,4
                if (r2 < existing.startRow) {
                    // CASE 1 : do nothing
                }
                else { // 2,3,4
                    if (r2 < existing.endRow) {
                        // CASE 2
                        removeList.add(existing);
                        split1 = existing.getSplitToRow(r2);
                        modifier.modifyRowAttributes(split1);
                        split2 = existing.getSplitFromRow(r2 + 1);
                        addList.add(split1);
                        addList.add(split2);
                    }
                    else if (r2 == existing.endRow) {
                        // CASES 3
                        modifier.modifyRowAttributes(existing);
                        r2 = existing.startRow - 1;
                    }
                    else if (r2 > existing.endRow) {
                        modifier.modifyRowAttributes(existing);
                        r1 = existing.endRow + 1;  // we are discarding the earlier part
                    }
                }
            }
            else {  // 5,6,7,8,9,10,11
                if (r1 == existing.startRow) { // 5,6,7
                    if (r2 < existing.endRow) {
                        // CASE 5
                        removeList.add(existing);
                        split1 = existing.getSplitToRow(r2);
                        modifier.modifyRowAttributes(split1);
                        split2 = existing.getSplitFromRow(r2 + 1);
                        addList.add(split1);
                        addList.add(split2);
                        r1 = -1;
                        r2 = -1;
                    }
                    else {
                        if (r2 == existing.endRow) {
                            // CASE 6
                            modifier.modifyRowAttributes(existing);
                            r1 = -1;
                            r2 = -1;
                        }
                        else {
                            // CASE 7
                            modifier.modifyRowAttributes(existing);
                            r1 = existing.endRow + 1;
                        }
                    }
                }
                else { // 8,9,10,11

                    if (r2 < existing.endRow) {
                        // CASE 8
                        removeList.add(existing);
                        split1 = existing.getSplitToRow(r1 - 1);
                        split2 = existing.getSplitFromRow(r2 + 1);
                        RowAttributes subset = existing.getSubset(r1, r2);
                        modifier.modifyRowAttributes(subset);
                        addList.add(split1);
                        addList.add(split2);
                        addList.add(subset);
                        r1 = -1;
                        r2 = -1;
                    }
                    else if (r2 == existing.endRow) { //9
                        removeList.add(existing);
                        split1 = existing.getSplitToRow(r1 - 1);
                        split2 = existing.getSplitFromRow(r1);
                        modifier.modifyRowAttributes(split2);
                        addList.add(split1);
                        addList.add(split2);
                        r1 = -1;
                        r2 = -1;
                    }
                    else { //10,11
                        if (r1 <= existing.endRow) {
                            removeList.add(existing);
                            split1 = existing.getSplitToRow(r1 - 1);
                            split2 = existing.getSplitFromRow(r1);
                            modifier.modifyRowAttributes(split2);
                            addList.add(split1);
                            addList.add(split2);
                            r1 = existing.endRow + 1;
                        }
                        else {
                            // CASE 11 : do nothing
                        }
                    }

                }
            }
        }

        if ((r1 >= 0) && (r2 >= 0)) {
            RowAttributes newAttributes = new RowAttributes(r1, r2);
            modifier.modifyRowAttributes(newAttributes);
            addList.add(newAttributes);
        }

        attributes.removeAll(removeList);
        attributes.addAll(addList);
        Collections.sort(attributes);

    }

    /**
     * Returns an iterator that provides access to the row attributes.
     * 
     * @return An iterator.
     */
    public Iterator getAttributesIterator() {

        return attributes.iterator();

    }

}
