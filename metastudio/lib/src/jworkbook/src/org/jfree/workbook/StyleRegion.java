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
 * ----------------
 * StyleRegion.java
 * ----------------
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
 * 13-Feb-2002 : Reversed order of row and column parameters (DG);
 * 07-Jul-2003 : Changed GPL --> LGPL, changed package name, updated company name (DG);
 *
 */

package org.jfree.workbook;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a region and its associated style.
 * <P>
 * Note that the style can be changed, but the region is immutable.  This is done because the
 * regions form part of a non-overlapping collection that completely cover a worksheet.  If you
 * could change the region, that would potentially break the collection.
 */
public class StyleRegion {

    /** The style. */
    protected Style style;

    /** The start row. */
    protected int startRow;

    /** The start column. */
    protected int startColumn;

    /** The end row. */
    protected int endRow;

    /** The end column. */
    protected int endColumn;

    /**
     * Constructs a style region.
     * 
     * @param style  the style.
     * @param startRow  the start row for the region.
     * @param startColumn  the start column for the region.
     * @param endRow  the end row for the region.
     * @param endColumn  the end column for the region.
     */
    public StyleRegion(Style style, int startRow, int startColumn, int endRow, int endColumn) {

        // check arguments...
        if ((startRow < 0) || (startRow >= Worksheet.MAX_ROWS)) {
            throw new IllegalArgumentException("StyleRegion constructor: "
                                               + "startRow outside valid range.");
        }

        if ((endRow < startRow) || (endRow >= Worksheet.MAX_ROWS)) {
            throw new IllegalArgumentException("StyleRegion constructor: "
                                               + "endRow outside valid range.");
        }

        if ((startColumn < 0) || (startColumn >= Worksheet.MAX_COLUMNS)) {
            throw new IllegalArgumentException("StyleRegion constructor: "
                                               + "startColumn outside valid range.");
        }

        if ((endColumn < startColumn) || (endColumn >= Worksheet.MAX_COLUMNS)) {
            throw new IllegalArgumentException("StyleRegion constructor: "
                                               + "endColumn outside valid range.");
        }

        // initialise...
        this.style = style;
        this.startRow = startRow;
        this.startColumn = startColumn;
        this.endRow = endRow;
        this.endColumn = endColumn;

    }

    /**
     * Returns the style for this region.
     * 
     * @return The style for this region.
     */
    public Style getStyle() {
        return this.style;
    }

    /**
     * Sets the style for this region.
     * 
     * @param style  the new style.
     */
    public void setStyle(Style style) {
        this.style = style;
    }

    /**
     * Returns the start row of the region.
     * 
     * @return The start row of the region.
     */
    public int getStartRow() {
        return this.startRow;
    }

    /**
     * Returns the start column of the region.
     * 
     * @return The start column of the region.
     */
    public int getStartColumn() {
        return this.startColumn;
    }

    /**
     * Returns the end row of the region.
     * 
     * @return The end row of the region.
     */
    public int getEndRow() {
        return this.endRow;
    }

    /**
     * Returns the end column of the region.
     * 
     * @return The end column of the region.
     */
    public int getEndColumn() {
        return this.endColumn;
    }

    /**
     * Returns true if the region include the cell at the specified row and column.
     * 
     * @param row  the row.
     * @param column  the column.
     * 
     * @return A boolean.
     */
    public boolean contains(int row, int column) {

        if (row < this.startRow) {
            return false;
        } 
        if (row > this.endRow) {
            return false;
        }
        if (column < this.startColumn) {
            return false;
        } 
        if (column > this.endColumn) {
            return false;
        }
        return true;

    }

    /**
     * Returns true if the specified range intersects with this region, and false otherwise.
     * 
     * @param row1  the start row of the range.
     * @param col1  the start column of the range.
     * @param row2  the end row of the range.
     * @param col2  the end column of the range.
     * 
     * @return A boolean.
     */
    public boolean intersects(int row1, int col1, int row2, int col2) {

        if (row1 > this.endRow) {
            return false;
        } 
        else if (row2 < this.startRow) {
            return false;
        } 
        else if (col1 > this.endColumn) {
            return false;
        } 
        else if (col2 < this.startColumn) {
            return false;
        } 
        else {
            return true;
        } 

    }

    /**
     * Returns true if the specified region intersects with this region, and false otherwise.
     * 
     * @param region  the region being tested.
     * 
     * @return A boolean.
     */
    public boolean intersects(StyleRegion region) {

        // pass over to more general method...
        return intersects(region.startRow, region.startColumn, region.endRow, region.endColumn);

    }

    /**
     * Returns a StyleRegion representing the portion of this region that intersects with the
     * specified region.  If there is no intersection, returns null.
     * 
     * @param r1  the start row of the region.
     * @param c1  the start column of the region.
     * @param r2  the end row of the region.
     * @param c2  the end column of the region.
     * @param modifier  an optional style modifier that will be applied to the style before
     *                  returning the intersecting region.
     * 
     * @return The style region.
     */
    public StyleRegion getIntersectionStyleRegion(int r1, int c1, int r2, int c2,
                                                  StyleModifier modifier) {

        StyleRegion result = null;

        int intersectCase = getIntersectCase(r1, c1, r2, c2);

        switch (intersectCase) {

            case 0 :  break;  // no intersection

            case 1 :  result = new StyleRegion(this.style,
                                               this.startRow, this.startColumn, r2, c2);
                      break; 


            case 2 :  result = new StyleRegion(this.style, this.startRow, this.startColumn,
                                               r2, this.endColumn);
                      break; 

            case 3 :  result = new StyleRegion(this.style, this.startRow, c1, r2, this.endColumn);
                      break; 

            case 4 :  result = new StyleRegion(this.style, this.startRow, this.startColumn,
                                               this.endRow, c2);
                      break; 

            case 5 :  result = this;
                      break; 

            case 6 :  result = new StyleRegion(this.style, this.startRow, c1,
                                               this.endRow, this.endColumn);
                      break; 

            case 7 :  result = new StyleRegion(this.style, r1, this.startColumn, this.endRow, c2);
                      break; 

            case 8 :  result = new StyleRegion(this.style, r1, this.startColumn,
                                               this.endRow, this.endColumn);
                      break; 

            case 9 :  result = new StyleRegion(this.style, r1, c1, this.endRow, this.endColumn);
                      break; 

            case 10 : result = new StyleRegion(this.style, this.startRow, c1, r2, c2);
                      break;

            case 11 : result = new StyleRegion(this.style, r1, c1, r2, this.endColumn);
                      break; 
                      
            case 12 : result = new StyleRegion(this.style, r1, c1, this.endRow, c2);
                      break; 

            case 13 : result = new StyleRegion(this.style, r1, this.startColumn, r2, c2);
                      break;

            case 14 : result = new StyleRegion(this.style, r1, c1, r2, c2);
                      break; 
                      
            case 15 : result = new StyleRegion(this.style, this.startRow, c1, this.endRow, c2);
                      break;

            case 16 : result = new StyleRegion(this.style, r1, this.startColumn,
                                               r2, this.endColumn);
                      break; 

            default : // do nothing

        }

        if (modifier != null) {
            if (result != null) {
                result.setStyle(modifier.getModifiedStyle(result.getStyle()));
            }
        }

        return result;

    }

    /**
     * Returns a list of style regions that cover the non-intersecting part of this region.
     * 
     * @param region  the region to calculate the intersection against.
     * 
     * @return A list of style regions that cover the non-intersecting part of this region.
     */
    public List<StyleRegion> getNonIntersectionStyleRegionList(StyleRegion region) {

        return getNonIntersectionStyleRegionList(region.startRow,
                                                 region.startColumn,
                                                 region.endRow,
                                                 region.endColumn);

    }

    /**
     * Returns a list of StyleRegions equivalent to the non-intersection with the specified
     * range.
     * <P>
     * There are 17 cases to deal with, one of which entirely obliterates
     * the existing StyleRegion.
     * 
     * @param row1  the start row.
     * @param col1  the start column.
     * @param row2  the end row.
     * @param col2  the end column.
     * 
     * @return A list of style regions equivalent to the non-intersection with the specified
     *         range.
     */
    public List<StyleRegion> getNonIntersectionStyleRegionList(int row1, int col1, int row2, int col2) {

        List<StyleRegion> result = new ArrayList<StyleRegion>();

        int intersectCase = getIntersectCase(row1, col1, row2, col2);

        switch (intersectCase) {
            case 0 : break; // no intersection
            case 1 : result = getNonIntersect1RegionList(row1, col1, row2, col2); 
                     break; 
            case 2 : result = getNonIntersect2RegionList(row1, col1, row2, col2); 
                     break; 
            case 3 : result = getNonIntersect3RegionList(row1, col1, row2, col2); 
                     break; 
            case 4 : result = getNonIntersect4RegionList(row1, col1, row2, col2);
                     break; 
            case 5 : result = getNonIntersect5RegionList(row1, col1, row2, col2); 
                     break; 
            case 6 : result = getNonIntersect6RegionList(row1, col1, row2, col2); 
                     break; 
            case 7 : result = getNonIntersect7RegionList(row1, col1, row2, col2); 
                     break; 
            case 8 : result = getNonIntersect8RegionList(row1, col1, row2, col2); 
                     break; 
            case 9 : result = getNonIntersect9RegionList(row1, col1, row2, col2); 
                     break; 
            case 10 : result = getNonIntersect10RegionList(row1, col1, row2, col2); 
                      break;
            case 11 : result = getNonIntersect11RegionList(row1, col1, row2, col2); 
                      break;
            case 12 : result = getNonIntersect12RegionList(row1, col1, row2, col2); 
                      break;
            case 13 : result = getNonIntersect13RegionList(row1, col1, row2, col2); 
                      break;
            case 14 : result = getNonIntersect14RegionList(row1, col1, row2, col2); 
                      break;
            case 15 : result = getNonIntersect15RegionList(row1, col1, row2, col2); 
                      break;
            case 16 : result = getNonIntersect16RegionList(row1, col1, row2, col2); 
                      break;
            default : // do nothing
        }

        return result;

    }

    /**
     * Returns an integer denoting one of the 17 cases of intersection.  Each case represents a
     * general type of intersection from which it is straightforward to calculate the subregions
     * that make up either the intersection or the non-intersection regions.
     * <P>
     * The 17 cases to deal with are:
     * 0: no intersection;
     * 1: top left corner;
     * 2: top section;
     * 3: top right corner;
     * 4: left section;
     * 5: all;
     * 6: right section;
     * 7: bottom left corner;
     * 8: bottom section;
     * 9: bottom right corner;
     * 10: top "bite";
     * 11: right "bite";
     * 12: bottom "bite";
     * 13: left "bite";
     * 14: central "bite";
     * 15: vertical section;
     * 16: horizontal section;
     * 
     * @param c1  the start column.
     * @param r1  the start row.
     * @param c2  the end column.
     * @param r2  the end row.
     * 
     * @return The case number.
     */
    public int getIntersectCase(int r1, int c1, int r2, int c2) {

        int result = 0;

        // first check for no intersection at all...
        if ((r1 > this.endRow) || (r2 < this.startRow)
                               || (c1 > this.endColumn)
                               || (c2 < this.startColumn)) {
            result = 0;
        }
        else {  // one of remaining 16 cases of intersection...
            if (r1 <= this.startRow) {  // 1, 2, 3, 4, 5, 6, 10, 15 ***
                if (r2 < this.endRow) {  // 1, 2, 3, 10 ***
                    if (c1 <= this.startColumn) {  // 1, 2 ***
                        if (c2 < this.endColumn) {
                            // CASE 1 ***
                            result = 1;
                        }
                        else {
                            // CASE 2 ***
                            result = 2;
                        }
                    }
                    else { // 3, 10 ***
                        if (c2 >= this.endColumn) {
                            // CASE 3 ***
                            result = 3;
                        }
                        else {
                            // CASE 10 ***
                            result = 10;
                        }
                    }
                }
                else { // 4, 5, 6, 15 ***
                    if (c1 <= this.startColumn) { // 4, 5***
                        if (c2 < this.endColumn) {
                            // CASE 4 ***
                            result = 4;
                        }
                        else {
                            // CASE 5 ***
                            result = 5;
                        }
                    }
                    else { // 6, 15 ***
                        if (c2 <= this.endColumn) {
                            // CASE 6 ***
                            result = 6;
                        }
                        else {
                            // CASE 15 ***
                            result = 15;
                        }
                    }

                }

            }
            else {  // 7, 8, 9, 11, 12, 13, 14, 16 ***
                if (c1 <= this.startColumn) {  // 7, 8, 13, 16 ***

                    if (c2 < this.endColumn) { // 7, 13 ***
                        if (r2 >= this.endRow) {
                            // CASE 7 ***
                            result = 7;
                        }
                        else {
                            // CASE 13 ***
                            result = 13;
                        }
                    }
                    else { // 8, 16 ***
                        if (r2 >= this.endRow) {
                            // CASE 8 ***
                            result = 8;
                        }
                        else {
                            // CASE 16 ***
                            result = 16;
                        }
                    }

                }
                else {  // 9, 11, 12, 14 ***
                    if (r2 >= this.endRow) { // 9, 12 ***
                        if (c2 >= this.endColumn) {
                            // CASE 9 ***
                            result = 9;
                        }
                        else {
                            // CASE 12 ***
                            result = 12;
                        }
                    }
                    else {  // 11, 14 **
                        if (c2 >= this.endColumn) {
                            // CASE 11 ***
                            result = 11;
                        }
                        else {
                            // CASE 14 ***
                            result = 14;
                        }
                    }
                }
            }
        }

        return result;

    }

    /**
     * Returns non-intersecting regions for case 1.
     * 
     * @param r1  the start row for the intersecting region.
     * @param c1  the start column for the intersecting region.
     * @param r2  the end row for the intersecting region.
     * @param c2  the end column for the intersecting region.
     * 
     * @return A list of non-intersecting regions.
     */
    private List<StyleRegion> getNonIntersect1RegionList(int r1, int c1, int r2, int c2) {

        StyleRegion a = new StyleRegion(style, r2 + 1, this.startColumn, this.endRow, c2);
        StyleRegion b = new StyleRegion(style, this.startRow, c2 + 1, this.endRow, this.endColumn);
        ArrayList<StyleRegion> result = new ArrayList<StyleRegion>();
        result.add(a);
        result.add(b);
        return result;

    }

    /**
     * Returns non-intersecting regions for case 2.
     * 
     * @param r1  the start row for the intersecting region.
     * @param c1  the start column for the intersecting region.
     * @param r2  the end row for the intersecting region.
     * @param c2  the end column for the intersecting region.
     * 
     * @return A list of non-intersecting regions.
     */
    private List<StyleRegion> getNonIntersect2RegionList(int r1, int c1, int r2, int c2) {

        StyleRegion a = new StyleRegion(
            style, r2 + 1, this.startColumn, this.endRow, this.endColumn
        );
        ArrayList<StyleRegion> result = new ArrayList<StyleRegion>();
        result.add(a);
        return result;

    }

    /**
     * Returns non-intersecting regions for case 3.
     * 
     * @param r1  the start row for the intersecting region.
     * @param c1  the start column for the intersecting region.
     * @param r2  the end row for the intersecting region.
     * @param c2  the end column for the intersecting region.
     * 
     * @return A list of non-intersecting regions.
     */
    private List<StyleRegion> getNonIntersect3RegionList(int r1, int c1, int r2, int c2) {

        StyleRegion a = new StyleRegion(
            style, this.startRow, this.startColumn, this.endRow, c1 - 1
        );
        StyleRegion b = new StyleRegion(style, r2 + 1, c1, this.endRow, this.endColumn);
        ArrayList<StyleRegion> result = new ArrayList<StyleRegion>();
        result.add(a);
        result.add(b);
        return result;

    }

    /**
     * Returns non-intersecting regions for case 4.
     * 
     * @param r1  the start row for the intersecting region.
     * @param c1  the start column for the intersecting region.
     * @param r2  the end row for the intersecting region.
     * @param c2  the end column for the intersecting region.
     * 
     * @return A list of non-intersecting regions.
     */
    private List<StyleRegion> getNonIntersect4RegionList(int r1, int c1, int r2, int c2) {

        StyleRegion a = new StyleRegion(style, this.startRow, c2 + 1, this.endRow, this.endColumn);
        ArrayList<StyleRegion> result = new ArrayList<StyleRegion>();
        result.add(a);
        return result;

    }

    /**
     * Returns non-intersecting regions for case 5.
     * 
     * @param r1  the start row for the intersecting region.
     * @param c1  the start column for the intersecting region.
     * @param r2  the end row for the intersecting region.
     * @param c2  the end column for the intersecting region.
     * 
     * @return A list of non-intersecting regions.
     */
    private List<StyleRegion> getNonIntersect5RegionList(int r1, int c1, int r2, int c2) {
        return new ArrayList<StyleRegion>();
    }

    /**
     * Returns non-intersecting regions for case 6.
     * 
     * @param r1  the start row for the intersecting region.
     * @param c1  the start column for the intersecting region.
     * @param r2  the end row for the intersecting region.
     * @param c2  the end column for the intersecting region.
     * 
     * @return A list of non-intersecting regions.
     */
    private List<StyleRegion> getNonIntersect6RegionList(int r1, int c1, int r2, int c2) {

        StyleRegion a = new StyleRegion(
            style, this.startRow, this.startColumn, this.endRow, c1 - 1
        );
        ArrayList<StyleRegion> result = new ArrayList<StyleRegion>();
        result.add(a);
        return result;

    }

    /**
     * Returns non-intersecting regions for case 7.
     * 
     * @param r1  the start row for the intersecting region.
     * @param c1  the start column for the intersecting region.
     * @param r2  the end row for the intersecting region.
     * @param c2  the end column for the intersecting region.
     * 
     * @return A list of non-intersecting regions.
     */
    private List<StyleRegion> getNonIntersect7RegionList(int r1, int c1, int r2, int c2) {

        StyleRegion a = new StyleRegion(style, this.startRow, this.startColumn, r1 - 1, c2);
        StyleRegion b = new StyleRegion(style, this.startRow, c2 + 1, this.endRow, this.endColumn);
        ArrayList<StyleRegion> result = new ArrayList<StyleRegion>();
        result.add(a);
        result.add(b);
        return result;

    }

    /**
     * Returns non-intersecting regions for case 8.
     * 
     * @param r1  the start row for the intersecting region.
     * @param c1  the start column for the intersecting region.
     * @param r2  the end row for the intersecting region.
     * @param c2  the end column for the intersecting region.
     * 
     * @return A list of non-intersecting regions.
     */
    private List<StyleRegion> getNonIntersect8RegionList(int r1, int c1, int r2, int c2) {

        StyleRegion a = new StyleRegion(
            style, this.startRow, this.startColumn, r1 - 1, this.endColumn
        );
        ArrayList<StyleRegion> result = new ArrayList<StyleRegion>();
        result.add(a);
        return result;

    }

    /**
     * Returns non-intersecting regions for case 9.
     * 
     * @param r1  the start row for the intersecting region.
     * @param c1  the start column for the intersecting region.
     * @param r2  the end row for the intersecting region.
     * @param c2  the end column for the intersecting region.
     * 
     * @return A list of non-intersecting regions.
     */
    private List<StyleRegion> getNonIntersect9RegionList(int r1, int c1, int r2, int c2) {

        StyleRegion a = new StyleRegion(
            style, this.startRow, this.startColumn, this.endRow, c1 - 1
        );
        StyleRegion b = new StyleRegion(style, this.startRow, c1, r1 - 1, this.endColumn);
        ArrayList<StyleRegion> result = new ArrayList<StyleRegion>();
        result.add(a);
        result.add(b);
        return result;

    }

    /**
     * Returns non-intersecting regions for case 10.
     * 
     * @param r1  the start row for the intersecting region.
     * @param c1  the start column for the intersecting region.
     * @param r2  the end row for the intersecting region.
     * @param c2  the end column for the intersecting region.
     * 
     * @return A list of non-intersecting regions.
     */
    private List<StyleRegion> getNonIntersect10RegionList(int r1, int c1, int r2, int c2) {

        StyleRegion a = new StyleRegion(
            style, this.startRow, this.startColumn, this.endRow, c1 - 1
        );
        StyleRegion b = new StyleRegion(style, r2 + 1, c1, this.endRow, c2);
        StyleRegion c = new StyleRegion(style, this.startRow, c2 + 1, this.endRow, this.endColumn);
        ArrayList<StyleRegion> result = new ArrayList<StyleRegion>();
        result.add(a);
        result.add(b);
        result.add(c);
        return result;

    }

    /**
     * Returns non-intersecting regions for case 11.
     * 
     * @param r1  the start row for the intersecting region.
     * @param c1  the start column for the intersecting region.
     * @param r2  the end row for the intersecting region.
     * @param c2  the end column for the intersecting region.
     * 
     * @return A list of non-intersecting regions.
     */
    private List<StyleRegion> getNonIntersect11RegionList(int r1, int c1, int r2, int c2) {

        StyleRegion a = new StyleRegion(
            style, this.startRow, this.startColumn, this.endRow, c1 - 1
        );
        StyleRegion b = new StyleRegion(style, this.startRow, c1, r1 - 1, this.endColumn);
        StyleRegion c = new StyleRegion(style, r2 + 1, c1, this.endRow, this.endColumn);
        ArrayList<StyleRegion> result = new ArrayList<StyleRegion>();
        result.add(a);
        result.add(b);
        result.add(c);
        return result;

    }

    /**
     * Returns non-intersecting regions for case 12.
     * 
     * @param r1  the start row for the intersecting region.
     * @param c1  the start column for the intersecting region.
     * @param r2  the end row for the intersecting region.
     * @param c2  the end column for the intersecting region.
     * 
     * @return A list of non-intersecting regions.
     */
    private List<StyleRegion> getNonIntersect12RegionList(int r1, int c1, int r2, int c2) {

        StyleRegion a = new StyleRegion(style, this.endRow, this.startColumn, this.endRow, c1 - 1);
        StyleRegion b = new StyleRegion(style, this.startRow, c1, r1 - 1, c2);
        StyleRegion c = new StyleRegion(style, this.startRow, c2 + 1, this.endRow, this.endColumn);
        ArrayList<StyleRegion> result = new ArrayList<StyleRegion>();
        result.add(a);
        result.add(b);
        result.add(c);
        return result;

    }

    /**
     * Returns non-intersecting regions for case 13.
     * 
     * @param r1  the start row for the intersecting region.
     * @param c1  the start column for the intersecting region.
     * @param r2  the end row for the intersecting region.
     * @param c2  the end column for the intersecting region.
     * 
     * @return A list of non-intersecting regions.
     */
    private List<StyleRegion> getNonIntersect13RegionList(int r1, int c1, int r2, int c2) {

        StyleRegion a = new StyleRegion(style, this.startRow, this.startColumn, r1 - 1, c2);
        StyleRegion b = new StyleRegion(style, this.startRow, c2 + 1, this.endRow, this.endColumn);
        StyleRegion c = new StyleRegion(style, r2 + 1, this.startColumn, this.endRow, c2);
        ArrayList<StyleRegion> result = new ArrayList<StyleRegion>();
        result.add(a);
        result.add(b);
        result.add(c);
        return result;

    }

    /**
     * Returns non-intersecting regions for case 14.
     * 
     * @param r1  the start row for the intersecting region.
     * @param c1  the start column for the intersecting region.
     * @param r2  the end row for the intersecting region.
     * @param c2  the end column for the intersecting region.
     * 
     * @return A list of non-intersecting regions.
     */
    private List<StyleRegion> getNonIntersect14RegionList(int r1, int c1, int r2, int c2) {

        StyleRegion a = new StyleRegion(
            style, this.startRow, this.startColumn, this.endRow, c1 - 1
        );
        StyleRegion b = new StyleRegion(style, this.startRow, c1, r1 - 1, c2);
        StyleRegion c = new StyleRegion(style, r2 + 1, c1, this.endRow, c2);
        StyleRegion d = new StyleRegion(style, this.startRow, c2 + 1, this.endRow, this.endColumn);
        ArrayList<StyleRegion> result = new ArrayList<StyleRegion>();
        result.add(a);
        result.add(b);
        result.add(c);
        result.add(d);
        return result;

    }

    /**
     * Returns non-intersecting regions for case 15.
     * 
     * @param r1  the start row for the intersecting region.
     * @param c1  the start column for the intersecting region.
     * @param r2  the end row for the intersecting region.
     * @param c2  the end column for the intersecting region.
     * 
     * @return A list of non-intersecting regions.
     */
    private List<StyleRegion> getNonIntersect15RegionList(int r1, int c1, int r2, int c2) {

        StyleRegion a = new StyleRegion(
            style, this.startRow, this.startColumn, this.endRow, c1 - 1
        );
        StyleRegion b = new StyleRegion(style, this.startRow, c2 + 1, this.endRow, this.endColumn);
        ArrayList<StyleRegion> result = new ArrayList<StyleRegion>();
        result.add(a);
        result.add(b);
        return result;

    }

    /**
     * Returns non-intersecting regions for case 16.
     * 
     * @param r1  the start row for the intersecting region.
     * @param c1  the start column for the intersecting region.
     * @param r2  the end row for the intersecting region.
     * @param c2  the end column for the intersecting region.
     * 
     * @return A list of non-intersecting regions.
     */
    private List<StyleRegion> getNonIntersect16RegionList(int r1, int c1, int r2, int c2) {

        StyleRegion a = new StyleRegion(
            style, this.startRow, this.startColumn, r1 - 1, this.endColumn
        );
        StyleRegion b = new StyleRegion(
            style, r2 + 1, this.startColumn, this.endRow, this.endColumn
        );
        ArrayList<StyleRegion> result = new ArrayList<StyleRegion>();
        result.add(a);
        result.add(b);
        return result;

    }

}
