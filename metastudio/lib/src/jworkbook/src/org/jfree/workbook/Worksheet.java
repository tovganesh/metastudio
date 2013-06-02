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
 * Worksheet.java
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
 * 11-Feb-2002 : Implemented GnumericWriter.java and XLWriter.java (DG);
 * 07-Jul-2003 : Changed GPL --> LGPL, changed package name, updated company name (DG);
 *
 */

package org.jfree.workbook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jfree.workbook.date.SerialDate;

/**
 * Represents one worksheet within a workbook.
 */
public class Worksheet {

    /** The maximum number of columns in a worksheet. */
    public static final int MAX_COLUMNS = 256;

    /** The maximum number of rows in a worksheet. */
    public static final int MAX_ROWS = 65536;

    /** The default width for columns. */
    public static final double DEFAULT_COLUMN_WIDTH = 48;

    /** The default height for rows. */
    public static final double DEFAULT_ROW_HEIGHT = 12.8;

    /** The name of the worksheet. */
    protected String name;

    /** Display formulae rather than calculated values (true/false). */
    protected boolean displayFormulae;

    /** Hide zeros (true/false). */
    protected boolean hideZeros;

    /** A flag indicating whether or not the grid is visible. */
    protected boolean gridVisible;

    /** A flag indicating whether or not the column header is visible. */
    protected boolean columnHeaderVisible;

    /** A flag indicating whether or not the row header is visible. */
    protected boolean rowHeaderVisible;

    /** The index of the right-most column used in the worksheet. */
    protected int maxColumn;

    /** The index of the bottom-most row used in the worksheet. */
    protected int maxRow;

    /** The current zoom factor (in the range 0.05 to 5.00, that is 5% to 500%). */
    protected double zoom;

    /** Manager for names defined in the worksheet. */
    protected NamesManager namesManager;

    /** Print setup. */
    protected PrintInformation printInfo;

    /** Styles that apply to ranges within the worksheet. */
    protected Styles styles;

    /** Column attributes (width, margins, hidden etc). */
    protected ColumnAttributesManager columnAttributesManager;

    /** Row attributes (height, margins, hidden etc). */
    protected RowAttributesManager rowAttributesManager;

    /** Ranges that are selected. */
    protected Selections selections;

    /** The cells in the worksheet. */
    protected Cells cells;

    /** Comments for cells in the worksheet. */
    protected List<Comment> comments;

    /** Settings for the solver utility. */
    protected Solver solver;

    /**
     * Standard constructor: creates a named empty worksheet.
     * 
     * @param name  the name of the worksheet.
     */
    public Worksheet(String name) {

        this.name = name;
        this.maxColumn = -1;
        this.maxRow = -1;
        this.zoom = 1.0;
        this.namesManager = new NamesManager();
        this.printInfo = new PrintInformation();
        this.styles = new Styles();
        this.columnAttributesManager = new ColumnAttributesManager();
        this.rowAttributesManager = new RowAttributesManager();
        this.selections = new Selections();
        this.cells = new Cells();
        this.comments = new ArrayList<Comment>();
        this.solver = new Solver();

    }

    /**
     * Returns the name of the worksheet.
     * 
     * @return The name of the worksheet.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name of the worksheet.
     * 
     * @param name  the name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the index of the rightmost column in the sheet.
     * 
     * @return The maximum column index.
     */
    public int getMaxColumn() {
        return this.maxColumn;
    }

    /**
     * Returns the index of the bottommost row in the sheet.
     * 
     * @return The maximum row index.
     */
    public int getMaxRow() {
        return this.maxRow;
    }

    /**
     * Returns the zoom factor.
     * 
     * @return The zoom factor.
     */
    public double getZoom() {
        return this.zoom;
    }

    /**
     * Sets the zoom factor.
     * 
     * @param percent  the new zoom factor (should be in the range 0.05 to 5.00).
     */
    public void setZoom(double percent) {

        if ((percent < 0.05) || (percent > 5.00)) {
            throw new IllegalArgumentException("Worksheet.setZoom(): value outside valid range.");
        }

        this.zoom = percent;

    }

    /**
     * Returns true if formulae rather than values are being displayed in the worksheet.
     * 
     * @return True if formulae rather than values are being displayed in the worksheet.
     */
    public boolean getDisplayFormulae() {
        return this.displayFormulae;
    }

    /**
     * Returns a flag indicating whether or not zero values are displayed.
     * 
     * @return A boolean.
     */
    public boolean getHideZeros() {
        return this.hideZeros;
    }

    /**
     * Returns a flag indicating whether or not the grid is visible.
     * 
     * @return A boolean.
     */
    public boolean isGridVisible() {
        return this.gridVisible;
    }

    /**
     * Returns a flag indicating whether or not the column header is visible.
     * 
     * @return A boolean.
     */
    public boolean isColumnHeaderVisible() {
        return this.columnHeaderVisible;
    }

    /**
     * Returns a flag indicating whether or not the row header is visible.
     * 
     * @return A boolean.
     */
    public boolean isRowHeaderVisible() {
        return this.rowHeaderVisible;
    }

    /**
     * Returns a reference to the names manager.
     * 
     * @return A reference to the names manager.
     */
    public NamesManager getNamesManager() {
        return this.namesManager;
    }

    /**
     * Returns the print information for the sheet.
     * 
     * @return Print information.
     */
    public PrintInformation getPrintInformation() {
        return this.printInfo;
    }

    /**
     * Returns the styles information for the sheet.
     * 
     * @return Styles information.
     */
    public Styles getStyles() {
        return this.styles;
    }

    /**
     * Returns the selections for the sheet.
     * 
     * @return The selections.
     */
    public Selections getSelections() {
        return this.selections;
    }

    /**
     * Returns the cells for the worksheet.
     * 
     * @return The cells.
     */
    public Cells getCells() {
        return this.cells;
    }

    /**
     * Returns the solver for the worksheet.
     * 
     * @return The solver.
     */
    public Solver getSolver() {
        return this.solver;
    }

    /**
     * Returns the comments for the worksheet.
     * 
     * @return The comments.
     */
    public List getComments() {
        return this.comments;
    }

    /**
     * Returns a reference to the object managing the column attributes.
     * 
     * @return The column attributes manager.
     */
    public ColumnAttributesManager getColumnAttributesManager() {
        return this.columnAttributesManager;
    }

    /**
     * Returns a reference to the object managing the row attributes.
     * 
     * @return The row attributes manager.
     */
    public RowAttributesManager getRowAttributesManager() {
        return this.rowAttributesManager;
    }

    /**
     * Sets the height of one row.
     * 
     * @param row  the row (0 <= startRow < Worksheets.MAX_ROWS).
     * @param height  the new height.
     */
    public void setRowHeight(int row, double height) {

        // pass to more general method...
        this.setRowHeight(row, row, height);

    }

    /**
     * Sets the height of a range of rows.
     * 
     * @param startRow  the row (0 <= startRow < Worksheets.MAX_ROWS).
     * @param endRow  the row (startRow <= endRow < Worksheets.MAX_ROWS).
     * @param height  the new height (height >= 0.0).
     */
    public void setRowHeight(int startRow, int endRow, double height) {

        // pass to the row attributes object...
        this.rowAttributesManager.setRowHeight(startRow, endRow, height);

    }

    /**
     * Sets the width of one column.
     * 
     * @param column  the column (0 to Worksheets.MAX_COLUMNS-1).
     * @param width  the new width.
     */
    public void setColumnWidth(int column, double width) {

        // pass to more general method...
        this.setColumnWidth(column, column, width);

    }

    /**
     * Sets the width of a range of columns.
     * 
     * @param startColumn  the start column (0 <= startColumn < Worksheets.MAX_COLUMNS).
     * @param endColumn  the end column (startColumn <= endColumn < Worksheets.MAX_COLUMNS).
     * @param width  the new width.
     */
    public void setColumnWidth(int startColumn, int endColumn, double width) {

        // pass to the column attributes object...
        this.columnAttributesManager.setColumnWidth(startColumn, endColumn, width);

    }

    /**
     * Sets the value at the specified cell.
     * 
     * @param cell  the new cell.
     */
    public void addCell(Cell cell) {

        // update the max cell...
        this.maxRow = Math.max(cell.row, this.maxRow);
        this.maxColumn = Math.max(cell.column, this.maxColumn);

        // then add the cell to the cell manager...
        cells.add(cell);

    }

    /**
     * Adds a name to the worksheet.  For now, there is no validation performed here, so be
     * careful what you add.
     * 
     * @param name  the new name.
     * @param value  the value that the name resolves to.
     */
    public void addName(String name, String value) {

        this.getNamesManager().addName(name, value);

    }

    /**
     * Adds the specified style region.
     * <P>
     * Note: this method will not adjust row sizes for the particular style that is being applied.
     * Not sure what is the best way to achieve this...
     * 
     * @param styleRegion  the style region being added.
     */
    public void addStyleRegion(StyleRegion styleRegion) {
        styles.addStyleRegion(styleRegion);
    }

    /**
     * Set the style for the specified range of the worksheet.
     * <P>
     * Note: this method will not adjust row sizes for the particular style that is being applied.
     * Not sure what is the best way to achieve this...
     * 
     * @param style  the style to be applied.
     * @param startRow  the start row for the style region.
     * @param startColumn  the start column for the style region.
     * @param endRow  the end row for the style region.
     * @param endColumn  the end column for the style region.
     */
    public void setStyle(Style style, int startRow, int startColumn, int endRow, int endColumn) {

        // check parameters...handled by StyleRegion constructor.
        StyleRegion styleRegion = new StyleRegion(style, startRow, startColumn, endRow, endColumn);
        this.addStyleRegion(styleRegion);

    }

    /**
     * Puts a date in a cell, overwriting any existing cell contents.
     * 
     * @param row  the row (0 <= row < Worksheet.MAX_ROWS).
     * @param column  the column (0 <= column < Worksheet.MAX_COLUMNS).
     * @param date  the date.
     */
    public void putDate(int row, int column, SerialDate date) {

        DateCell cell = new DateCell(date, row, column);
        this.addCell(cell);

    }

    /**
     * Puts a formula in a cell, overwriting any existing cell contents.
     * 
     * @param formula  the formula (not validated).
     * @param column  the column (0 <= column < Worksheet.MAX_COLUMNS).
     * @param row  the row (0 <= row < Worksheet.MAX_ROWS).
     */
    public void putFormula(int row, int column, String formula) {

        FormulaCell formulaCell = new FormulaCell(formula, row, column);
        this.addCell(formulaCell);

    }

    /**
     * Puts a label in a cell, overwriting any existing cell contents.
     * 
     * @param label  the label.
     * @param column  the column (0 <= column < Worksheet.MAX_COLUMNS).
     * @param row  the row (0 <= row < Worksheet.MAX_ROWS).
     */
    public void putLabel(int row, int column, String label) {

        LabelCell labelCell = new LabelCell(label, row, column);
        this.addCell(labelCell);

    }

    /**
     * Puts a value in a cell, overwriting any existing cell contents.
     * 
     * @param value  the value.
     * @param column  the column (0 <= column < Worksheet.MAX_COLUMNS).
     * @param row  the row (0 <= row < Worksheet.MAX_ROWS).
     */
    public void putValue(int row, int column, double value) {

        ValueCell valueCell = new ValueCell(value, row, column);
        this.addCell(valueCell);

    }

    /**
     * Puts a comment in a cell, overwriting any existing cell comments.
     * 
     * @param row  the row (0 <= row < Worksheet.MAX_ROWS).
     * @param column  the column (0 <= column < Worksheet.MAX_COLUMNS).
     * @param comment  the comment.
     */
    public void putComment(int row, int column, String comment) {

        Comment c = new Comment(row, column, comment);
        int commentIndex = Collections.binarySearch(comments, c);
        if (commentIndex > 0) {
            comments.remove(commentIndex);
            comments.add(commentIndex, c);
        }
        else {
            comments.add(-commentIndex - 1, c);
            this.maxRow = Math.max(row, this.maxRow);
            this.maxColumn = Math.max(column, this.maxColumn);
        }

    }

    /**
     * Applies the font to the specified cell.
     * <P>
     * This may involve restructuring the style regions.
     * 
     * @param font  the font.
     * @param row  the row (0 <= row < Worksheet.MAX_ROWS).
     * @param column  the column (0 <= column < Worksheet.MAX_COLUMNS).
     */
    public void applyFont(FontStyle font, int row, int column) {

        // delegate to more general method...
        this.applyFont(font, row, column, row, column);

    }

    /**
     * Applies the font to the specified region.
     * <P>
     * This may involve restructuring the style regions.
     * 
     * @param font  the font.
     * @param startRow  the start row (0 <= startRow < Worksheet.MAX_ROWS).
     * @param startColumn  the start column (0 <= startColumn < Worksheet.MAX_COLUMNS).
     * @param endRow  the end row (0 <= endRow < Worksheet.MAX_ROWS).
     * @param endColumn  the end column (0 <= endColumn < Worksheet.MAX_COLUMNS).
     */
    public void applyFont(FontStyle font,
                          int startRow, int startColumn,
                          int endRow, int endColumn) {

        // create a style modifier that changes the font and pass it on...
        StyleModifier fontChanger = new FontStyleModifier(font);
        styles.modifyStyle(fontChanger, startRow, startColumn, endRow, endColumn);

    }

    /**
     * Applies a horizontal alignment setting to a cell.
     * <P>
     * This may involve restructuring the style regions.
     * 
     * @param align  the new alignment.
     * @param row  the row (0 <= row < Worksheet.MAX_ROWS).
     * @param column  the column (0 <= column < Worksheet.MAX_COLUMNS).
     */
    public void applyHorizontalAlignment(int align, int row, int column) {

        // pass on to the more general method...
        this.applyHorizontalAlignment(align, row, column, row, column);

    }

    /**
     * Applies a horizontal alignment setting to a range of cells.
     * <P>
     * This may involve restructuring the style regions.
     * 
     * @param align  the new alignment.
     * @param startRow  the start row (0 <= startRow < Worksheet.MAX_ROWS).
     * @param startColumn  the start column (0 <= startColumn < Worksheet.MAX_COLUMNS).
     * @param endRow  the end row (0 <= endRow < Worksheet.MAX_ROWS).
     * @param endColumn  the end column (0 <= endColumn < Worksheet.MAX_COLUMNS).
     */
    public void applyHorizontalAlignment(int align,
                                         int startRow, int startColumn, int endRow, int endColumn) {

        StyleModifier modifier = new GeneralStyleModifier(true, align, false, 0);
        styles.modifyStyle(modifier, startRow, startColumn, endRow, endColumn);

    }

    /**
     * Applies a vertical alignment setting to a cell.
     * <P>
     * This may involve restructuring the style regions.
     * 
     * @param align  the new alignment.
     * @param row  the row (0 <= row < Worksheet.MAX_ROWS).
     * @param column  the column (0 <= column < Worksheet.MAX_COLUMNS).
     */
    public void applyVerticalAlignment(int align, int row, int column) {

        // pass on to the more general method...
        this.applyVerticalAlignment(align, row, column, row, column);

    }

    /**
     * Applies a vertical alignment setting to a range of cells.
     * <P>
     * This may involve restructuring the style regions.
     * 
     * @param align  the new alignment.
     * @param startColumn  the start column (0 <= startColumn < Worksheet.MAX_COLUMNS).
     * @param startRow  the start row (0 <= startRow < Worksheet.MAX_ROWS).
     * @param endColumn  the end column (0 <= endColumn < Worksheet.MAX_COLUMNS).
     * @param endRow  the end row (0 <= endRow < Worksheet.MAX_ROWS).
     */
    public void applyVerticalAlignment(int align,
                                       int startRow, int startColumn, int endRow, int endColumn) {

        StyleModifier modifier = new GeneralStyleModifier(false, 0, true, align);
        styles.modifyStyle(modifier, startRow, startColumn, endRow, endColumn);

    }

    /**
     * Applies the "wrap-text" setting to the specified cell.
     * <P>
     * This may involve restructuring the style regions.
     * 
     * @param wrapText  the new value of the wrap-text flag.
     * @param row  the row (0 <= row < Worksheet.MAX_ROWS).
     * @param column  the column (0 <= column < Worksheet.MAX_COLUMNS).
     */
    public void applyWrapText(boolean wrapText, int row, int column) {

        // pass on to the more general method...
        this.applyWrapText(wrapText, row, column, row, column);

    }

    /**
     * Applies the "wrap-text" setting to the specified region.
     * <P>
     * This may involve restructuring the style regions.
     * 
     * @param wrapText  the new value of the wrap-text flag.
     * @param startColumn  the start column (0 <= startColumn < Worksheet.MAX_COLUMNS).
     * @param startRow  the start row (0 <= startRow < Worksheet.MAX_ROWS).
     * @param endColumn  the end column (0 <= endColumn < Worksheet.MAX_COLUMNS).
     * @param endRow  the end row (0 <= endRow < Worksheet.MAX_ROWS).
     */
    public void applyWrapText(boolean wrapText,
                              int startRow, int startColumn, int endRow, int endColumn) {

        StyleModifier modifier = new GeneralStyleModifier(true, wrapText);
        styles.modifyStyle(modifier, startRow, startColumn, endRow, endColumn);

    }

    /**
     * Applies the foreground color to the specified cell.
     * <P>
     * This may involve restructuring the style regions.
     * 
     * @param border  the border.
     * @param row  the row (0 <= row < Worksheet.MAX_ROWS).
     * @param column  the column (0 <= column < Worksheet.MAX_COLUMNS).
     */
    public void applyBorder(Border border, int row, int column) {

        // delegate to more general method...
        this.applyBorder(border, row, column, row, column);

    }

    /**
     * Applies the border to the specified region.
     * <P>
     * This may involve restructuring the style regions.
     * 
     * @param border  the border.
     * @param startRow  the start row (0 <= startRow < Worksheet.MAX_ROWS).
     * @param startColumn  the start column (0 <= startColumn < Worksheet.MAX_COLUMNS).
     * @param endRow  the end row (0 <= endRow < Worksheet.MAX_ROWS).
     * @param endColumn  the end column (0 <= endColumn < Worksheet.MAX_COLUMNS).
     */
    public void applyBorder(Border border,
                            int startRow, int startColumn,
                            int endRow, int endColumn) {

        // create a style modifier that changes the font and pass it on...
        StyleModifier borderChanger = new BorderStyleModifier(border);
        styles.modifyStyle(borderChanger, startRow, startColumn, endRow, endColumn);

    }

    /**
     * Applies a thin outline border to a cell.
     * 
     * @param row  the row (0 <= row < Worksheet.MAX_ROWS).
     * @param column  the column (0 <= column < Worksheet.MAX_COLUMNS).
     */
    public void applyOutline(int row, int column) {

        this.applyOutline(Border.LINE, row, column);

    }

    /**
     * Applies a thin outline border to a range of cells.
     * 
     * @param startRow  the start row (0 <= startRow < Worksheet.MAX_ROWS).
     * @param startColumn  the start column (0 <= startColumn < Worksheet.MAX_COLUMNS).
     * @param endRow  the end row (0 <= endRow < Worksheet.MAX_ROWS).
     * @param endColumn  the end column (0 <= endColumn < Worksheet.MAX_COLUMNS).
     */
    public void applyOutline(int startRow, int startColumn, int endRow, int endColumn) {
        this.applyOutline(Border.LINE, startRow, startColumn, endRow, endColumn);
    }

    /**
     * Applies a border to a cell.
     * 
     * @param borderStyle  the border style.
     * @param row  the row.
     * @param column  the column.
     */
    public void applyOutline(int borderStyle, int row, int column) {

        this.applyOutline(borderStyle, row, column, row, column);

    }

    /**
     * Applies a border around a range of cells.
     * 
     * @param lineStyle  the style of line for the border.
     * @param startRow  the start row (0 <= startRow < Worksheet.MAX_ROWS).
     * @param startColumn  the start column (0 <= startColumn < Worksheet.MAX_COLUMNS).
     * @param endRow  the end row (0 <= endRow < Worksheet.MAX_ROWS).
     * @param endColumn  the end column (0 <= endColumn < Worksheet.MAX_COLUMNS).
     */
    public void applyOutline(int lineStyle,
                             int startRow, int startColumn, int endRow, int endColumn) {

        if (startColumn == endColumn) { // must be either single cell or single column...

            if (startRow == endRow) {
                // single cell
                Border border = new Border(lineStyle, lineStyle, lineStyle, lineStyle,
                                           Border.NONE, Border.NONE);
                applyBorder(border, startRow, startColumn, endRow, endColumn);
            }
            else {  // single column

                // top cell
                Border top = new Border(lineStyle, Border.NONE, lineStyle, lineStyle,
                                        Border.NONE, Border.NONE);
                applyBorder(top, startRow, startColumn, startRow, endColumn);

                // middle cells...
                if ((endRow - startRow) > 1) {
                    Border middle = new Border(Border.NONE, Border.NONE, lineStyle, lineStyle,
                                               Border.NONE, Border.NONE);
                    this.applyBorder(middle, startRow + 1, startColumn, endRow - 1, endColumn);
                }

                // bottom cell
                Border bottom = new Border(Border.NONE, lineStyle, lineStyle, lineStyle,
                                           Border.NONE, Border.NONE);
                applyBorder(bottom, endRow, startColumn, endRow, endColumn);
            }

        }
        else {
            if (startRow == endRow) {  // single row

                // left cell...
                Border left = new Border(lineStyle, lineStyle, lineStyle, Border.NONE,
                                         Border.NONE, Border.NONE);
                applyBorder(left, startRow, startColumn, endRow, startColumn);

                // middle cells...
                if ((endColumn - startColumn) > 1) {
                    Border middle = new Border(lineStyle, lineStyle, Border.NONE, Border.NONE,
                                               Border.NONE, Border.NONE);
                    applyBorder(middle, startRow, startColumn + 1, endRow, endColumn - 1);
                }

                // right cell...
                Border right = new Border(lineStyle, lineStyle, Border.NONE, lineStyle,
                                          Border.NONE, Border.NONE);
                applyBorder(right, endRow, endColumn, endRow, endColumn);

            }
            else {

                // top left cell...
                Border topleft = new Border(lineStyle, Border.NONE, lineStyle, Border.NONE,
                                            Border.NONE, Border.NONE);
                this.applyBorder(topleft, startRow, startColumn, startRow, startColumn);

                // top right cell...
                Border topright = new Border(lineStyle, Border.NONE, Border.NONE, lineStyle,
                                             Border.NONE, Border.NONE);
                applyBorder(topright, startRow, endColumn, startRow, endColumn);

                // bottom right cell...
                Border bottomright = new Border(Border.NONE, lineStyle, Border.NONE, lineStyle,
                                                Border.NONE, Border.NONE);
                applyBorder(bottomright, endRow, endColumn, endRow, endColumn);

                // bottom left cell...
                Border bottomleft = new Border(Border.NONE, lineStyle, lineStyle, Border.NONE,
                                               Border.NONE, Border.NONE);
                this.applyBorder(bottomleft, endRow, startColumn, endRow, startColumn);

                if ((endColumn - startColumn) > 1) {

                    // top...
                    Border top = new Border(lineStyle, Border.NONE, Border.NONE, Border.NONE,
                                            Border.NONE, Border.NONE);
                    applyBorder(top, startRow, startColumn + 1, startRow, endColumn - 1);

                    // bottom...
                    Border bottom = new Border(Border.NONE, lineStyle, Border.NONE, Border.NONE,
                                               Border.NONE, Border.NONE);
                    applyBorder(bottom, endRow, startColumn + 1, endRow, endColumn - 1);
                }

                if ((endRow - startRow) > 1) {

                    // left...
                    Border left = new Border(Border.NONE, Border.NONE, lineStyle, Border.NONE,
                                             Border.NONE, Border.NONE);
                    applyBorder(left, startRow + 1, startColumn,  endRow - 1, startColumn);

                    // right...
                    Border right = new Border(Border.NONE, Border.NONE, Border.NONE, lineStyle,
                                              Border.NONE, Border.NONE);

                    applyBorder(right, startRow + 1, endColumn, endRow - 1, endColumn);
                }
            }
        }

    }

    /**
     * Applies the foreground color to the specified cell.
     * <P>
     * This may involve restructuring the style regions.
     * 
     * @param color  the color.
     * @param row  the row (0 <= row < Worksheet.MAX_ROWS).
     * @param column  the column (0 <= column < Worksheet.MAX_COLUMNS).
     */
    public void applyForegroundColor(Color color, int row, int column) {

        // delegate to more general method...
        this.applyForegroundColor(color, row, column, row, column);

    }

    /**
     * Applies the foreground color to the specified region.
     * <P>
     * This may involve restructuring the style regions.
     * 
     * @param color  the color.
     * @param startRow  the start row (0 <= startRow < Worksheet.MAX_ROWS).
     * @param startColumn  the start column (0 <= startColumn < Worksheet.MAX_COLUMNS).
     * @param endRow  the end row (0 <= endRow < Worksheet.MAX_ROWS).
     * @param endColumn  the end column (0 <= endColumn < Worksheet.MAX_COLUMNS).
     */
    public void applyForegroundColor(Color color,
                                     int startRow, int startColumn,
                                     int endRow, int endColumn) {

        // create a style modifier that changes the foreground color and pass it on...
        StyleModifier foreground = new ColorStyleModifier(Color.FOREGROUND_COLOR, color);
        styles.modifyStyle(foreground, startRow, startColumn, endRow, endColumn);

    }

    /**
     * Applies the background color to the specified cell.
     * <P>
     * This may involve restructuring the style regions.
     * 
     * @param color  the color.
     * @param row  the row (0 <= row < Worksheet.MAX_ROWS).
     * @param column  the column (0 <= column < Worksheet.MAX_COLUMNS).
     */
    public void applyBackgroundColor(Color color, int row, int column) {

        // delegate to more general method...
        applyBackgroundColor(color, row, column, row, column);

    }

    /**
     * Applies the background color to the specified region.
     * <P>
     * This may involve restructuring the style regions.
     * 
     * @param color  the color.
     * @param startRow  the start row (0 <= startRow < Worksheet.MAX_ROWS).
     * @param startColumn  the start column (0 <= startColumn < Worksheet.MAX_COLUMNS).
     * @param endRow  the end row (0 <= endRow < Worksheet.MAX_ROWS).
     * @param endColumn  the end column (0 <= endColumn < Worksheet.MAX_COLUMNS).
     */
    public void applyBackgroundColor(Color color,
                                     int startRow, int startColumn,
                                     int endRow, int endColumn) {

        // create a style modifier that changes the background color and pass it on...
        StyleModifier background = new ColorStyleModifier(Color.BACKGROUND_COLOR, color);
        styles.modifyStyle(background, startRow, startColumn, endRow, endColumn);

    }

    /**
     * Applies the pattern color to the specified cell.
     * <P>
     * This may involve restructuring the style regions.
     * 
     * @param color  the color.
     * @param column  the column (0 <= column < Worksheet.MAX_COLUMNS).
     * @param row  the row (0 <= row < Worksheet.MAX_ROWS).
     */
    public void applyPatternColor(Color color, int row, int column) {

        // delegate to more general method...
        this.applyPatternColor(color, row, column, row, column);

    }

    /**
     * Applies the pattern color to the specified region.
     * <P>
     * This may involve restructuring the style regions.
     * 
     * @param color  the color.
     * @param startRow  the start row (0 <= startRow < Worksheet.MAX_ROWS).
     * @param startColumn  the start column (0 <= startColumn < Worksheet.MAX_COLUMNS).
     * @param endRow  the end row (0 <= endRow < Worksheet.MAX_ROWS).
     * @param endColumn  the end column (0 <= endColumn < Worksheet.MAX_COLUMNS).
     */
    public void applyPatternColor(Color color,
                                  int startRow, int startColumn, int endRow, int endColumn) {

        // create a style modifier that changes the pattern color and pass it on...
        StyleModifier pattern = new ColorStyleModifier(Color.PATTERN_COLOR, color);
        styles.modifyStyle(pattern, startRow, startColumn, endRow, endColumn);

    }

    //**********************************************************************************************

    /**
     * Returns a cell reference using letters for the column (e.g. D4) and the row number.
     * <P>
     * Note that internally we use zero based indices for the column and row numbers.  But
     * externally the user expects the first row to be number 1 (not 0).
     * 
     * @param row  the cell's row (0 <= row < Worksheet.MAX_ROWS).
     * @param column  the cell's column (0 <= column < Worksheet.MAX_COLUMNS).
     * 
     * @return A cell reference.
     */
    public static String cellReference(int row, int column) {

        char[] letters = {'A', 'B', 'C', 'D', 'E', 'F', 'G',
                          'H', 'I', 'J', 'K', 'L', 'M', 'N',
                          'O', 'P', 'Q', 'R', 'S', 'T', 'U',
                          'V', 'W', 'X', 'Y', 'Z'};

        String reference;

        int columnMost = column / 26;
        int columnLeast = column - (columnMost * 26);

        if (columnMost > 0) {
            char[] columnReference = new char[2];
            columnReference[0] = letters[columnMost - 1];
            columnReference[1] = letters[columnLeast];
            reference = new String(columnReference);
        }
        else {
            reference = new String(letters, columnLeast, 1);
        } 

        return reference + (row + 1);

    }

}
