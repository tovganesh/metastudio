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
 * -------------
 * XLWriter.java
 * -------------
 * (C) Copyright 2002, 2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id$
 *
 * Changes:
 * --------
 * 12-Feb-2002 : Version 1 (DG);
 * 07-Jul-2003 : Changed GPL --> LGPL, changed package name, updated company name (DG);
 *
 */

package org.jfree.workbook.io;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jfree.workbook.Border;
import org.jfree.workbook.Cell;
import org.jfree.workbook.ColumnAttributes;
import org.jfree.workbook.ColumnAttributesManager;
import org.jfree.workbook.JWorkbook;
import org.jfree.workbook.Row;
import org.jfree.workbook.RowAttributesManager;
import org.jfree.workbook.Style;
import org.jfree.workbook.Worksheet;

/**
 * This class takes a JWorkbook object and saves it to an XL compatible file format.  The POI/HSSF
 * library (http://sourceforge.net/projects/poi) is being used to achieve this.  POI/HSSF doesn't
 * have all the functions we require yet (e.g. formula support) so the .xls file will be missing
 * some information.  However, as new versions of POI/HSSF are released, this will improve.
 */
public class XLWriter {

    /**
     * Constructs an XLWriter.
     */
    public XLWriter() {

    }

    /**
     * Saves the workbook in an XL compatible file format.  This is achieved by converting the
     * JWorkbook object to an HSSFWorkbook so that the POI/HSSF project can be used as the basis
     * for saving to XL.  For more information about POI, see http://sourceforge.net/projects/poi.
     * 
     * @param workbook  the workbook.
     * @param filename  the name of the file.
     * 
     * @throws IOException if there is an I/O problem.
     */
    public void saveWorkbook(JWorkbook workbook, String filename) throws IOException {

        try {
            OutputStream out = new BufferedOutputStream(new FileOutputStream(filename));
            HSSFWorkbook hssfWorkbook = createHSSFWorkbook(workbook);
            hssfWorkbook.write(out);
            out.close();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Converts the JWorkbook to an HSSFWorkbook.
     * 
     * @param workbook  the workbook.
     * 
     * @return  The HSSF workbook.
     */
    private HSSFWorkbook createHSSFWorkbook(JWorkbook workbook) {

        HSSFWorkbook result = new HSSFWorkbook();

        Iterator iterator = workbook.getWorksheetsIterator();
        while (iterator.hasNext()) {
            Worksheet worksheet = (Worksheet) iterator.next();
            createHSSFWorksheet(result, worksheet);
        }

        return result;

    }

    /**
     * Creates a new sheet in the HSSFWorkbook, based on the supplied worksheet.
     * 
     * @param hssfWorkbook  the workbook.
     * @param worksheet  the worksheet.
     */
    private void createHSSFWorksheet(HSSFWorkbook hssfWorkbook, Worksheet worksheet) {

        HSSFSheet hssfSheet = hssfWorkbook.createSheet(worksheet.getName());

        Iterator iterator = worksheet.getCells().getRowsIterator();
        while (iterator.hasNext()) {

            Row row = (Row) iterator.next();
            createHSSFRow(hssfWorkbook, hssfSheet, worksheet, row);

        }

        ColumnAttributesManager attributes = worksheet.getColumnAttributesManager();

        // default column width (pts as double) : convert it to chars as short...
        short w = (short) (attributes.getDefaultColumnWidth() / 4.0);
        hssfSheet.setDefaultColumnWidth(w);

        RowAttributesManager rowAttrs = worksheet.getRowAttributesManager();

        // default row height ...
        hssfSheet.setDefaultRowHeightInPoints((float) rowAttrs.getDefaultRowHeight());

        // now define the individual column widths...
        iterator = attributes.getAttributesIterator();
        while (iterator.hasNext()) {
            ColumnAttributes ca = (ColumnAttributes) iterator.next();
            for (int c = ca.getStartColumn(); c <= ca.getEndColumn(); c++) {

                hssfSheet.setColumnWidth((short) c, (short) (ca.getWidth() * 64));

            }
        }

    }

    /**
     * Creates a new row in the HSSFSheet, based on the cells in the supplied row.
     * 
     * @param hssfWorkbook  the workbook.
     * @param hssfSheet  the HSSF worksheet.
     * @param worksheet  the worksheet.
     * @param row  the row.
     * 
     */
    private void createHSSFRow(HSSFWorkbook hssfWorkbook, HSSFSheet hssfSheet,
                               Worksheet worksheet, Row row) {

        HSSFRow hssfRow = hssfSheet.createRow((short) row.getIndex());

        // work out the height of the row...
        float rowHeight = (float) worksheet.getRowAttributesManager().getHeight(row.getIndex());
        hssfRow.setHeightInPoints(rowHeight);

        Iterator iterator = row.getCellsIterator();
        while (iterator.hasNext()) {

            Cell cell = (Cell) iterator.next();
            createHSSFCell(hssfWorkbook, hssfRow, worksheet, cell);

        }

    }

    /**
     * Creates a new cell in the HSSFRow, based on the contents of the supplied cell.
     * 
     * @param hssfWorkbook  the workbook.
     * @param hssfRow  the row.
     * @param worksheet  the worksheet.
     * @param cell  the cell.
     */
    private void createHSSFCell(HSSFWorkbook hssfWorkbook, HSSFRow hssfRow,
                                Worksheet worksheet, Cell cell) {

        HSSFCell hssfCell = hssfRow.createCell((short) cell.getColumn());
        if (cell.getType() == Cell.LABEL_TYPE) {
            hssfCell.setCellType(HSSFCell.CELL_TYPE_STRING);
            hssfCell.setCellValue(cell.getContent());
        }
        else if (cell.getType() == Cell.VALUE_TYPE) {
            hssfCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
            hssfCell.setCellValue(Double.valueOf(cell.getContent()).doubleValue());
        }
        else if (cell.getType() == Cell.DATE_TYPE) {
            hssfCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
            hssfCell.setCellValue(Double.valueOf(cell.getContent()).doubleValue());
        }

        HSSFCellStyle hssfStyle = hssfWorkbook.createCellStyle();

        Style style = worksheet.getStyles().getStyle(cell.getRow(), cell.getColumn());

        hssfStyle.setAlignment(getXLHorizontalAlignment(style));
        hssfStyle.setVerticalAlignment(getXLVerticalAlignment(style));
        hssfStyle.setWrapText(style.isWrapText());

        //hssfStyle.setFillBackgroundColor(style.getBackgroundColor());
        hssfStyle.setBorderTop(getXLBorder(style.getBorder().getTop()));
        hssfStyle.setBorderBottom(getXLBorder(style.getBorder().getBottom()));
        hssfStyle.setBorderLeft(getXLBorder(style.getBorder().getLeft()));
        hssfStyle.setBorderRight(getXLBorder(style.getBorder().getRight()));

        hssfCell.setCellStyle(hssfStyle);

    }

    /**
     * A utility method that returns the HSSF horizontal alignment constant that corresponds to
     * the horizontal alignment for the specified style.
     * 
     * @param style  the style.
     * 
     * @return The HSSF alignment.
     */
    private short getXLHorizontalAlignment(Style style) {

        short result = HSSFCellStyle.ALIGN_GENERAL;

        int align = style.getHorizontalAlignment();
        if (align == Style.ALIGN_GENERAL) {
            result = HSSFCellStyle.ALIGN_GENERAL;
        } 
        else if (align == Style.ALIGN_LEFT) {
            result = HSSFCellStyle.ALIGN_LEFT;
        } 
        else if (align == Style.ALIGN_RIGHT) {
            result = HSSFCellStyle.ALIGN_RIGHT;
        } 
        else if (align == Style.ALIGN_CENTER) {
            result = HSSFCellStyle.ALIGN_CENTER;
        } 
        else if (align == Style.ALIGN_CENTER_ACROSS_SELECTION) {
            result = HSSFCellStyle.ALIGN_CENTER_SELECTION;
        } 
        else if (align == Style.ALIGN_JUSTIFY_HORIZONTAL) {
            result = HSSFCellStyle.ALIGN_JUSTIFY;
        } 
        else if (align == Style.ALIGN_FILL) {
            result = HSSFCellStyle.ALIGN_FILL;
        } 

        return result;

    }

    /**
     * A utility method that returns the HSSF vertical alignment constant that corresponds to
     * the vertical alignment for the specified style.
     * 
     * @param style  the style.
     * 
     * @return The alignment.
     */
    private short getXLVerticalAlignment(Style style) {

        short result = HSSFCellStyle.VERTICAL_TOP;

        int align = style.getVerticalAlignment();
        if (align == Style.ALIGN_TOP) {
            result = HSSFCellStyle.VERTICAL_TOP;
        } 
        else if (align == Style.ALIGN_MIDDLE) {
            result = HSSFCellStyle.VERTICAL_CENTER;
        } 
        else if (align == Style.ALIGN_BOTTOM) {
            result = HSSFCellStyle.VERTICAL_BOTTOM;
        } 
        else if (align == Style.ALIGN_JUSTIFY_VERTICAL) {
            result = HSSFCellStyle.VERTICAL_JUSTIFY;
        } 

        return result;

    }

    /**
     * A utility method that returns the HSSF border constant that corresponds to
     * the border for the specified style.
     * 
     * @param style  the style.
     * 
     * @return The border constant.
     */
    private short getXLBorder(int style) {

        short result = HSSFCellStyle.BORDER_NONE;

        if (style == Border.NONE) {
            result = HSSFCellStyle.BORDER_NONE;
        } 
        else if (style == Border.DOTTED) {
            result = HSSFCellStyle.BORDER_DOTTED;
        } 
        else if (style == Border.DASHED1) {
            result = HSSFCellStyle.BORDER_DOTTED;
        } 
        else if (style == Border.DASHED2) {
            result = HSSFCellStyle.BORDER_DASHED;
        } 
        else if (style == Border.DASHED3) {
            result = HSSFCellStyle.BORDER_DASH_DOT;
        } 
        else if (style == Border.DASHED4) {
            result = HSSFCellStyle.BORDER_DASH_DOT_DOT;
        } 
        else if (style == Border.LINE) {
            result = HSSFCellStyle.BORDER_THIN;
        } 
        else if (style == Border.THICK_DASHED1) {
            result = HSSFCellStyle.BORDER_MEDIUM;
        } 
        else if (style == Border.THICK_DASHED2) {
            result = HSSFCellStyle.BORDER_MEDIUM_DASHED;
        } 
        else if (style == Border.THICK_DASHED3) {
            result = HSSFCellStyle.BORDER_MEDIUM_DASH_DOT;
        } 
        else if (style == Border.THICK_DASHED4) {
            result = HSSFCellStyle.BORDER_MEDIUM_DASH_DOT_DOT;
        } 
        else if (style == Border.THICK_LINE) {
            result = HSSFCellStyle.BORDER_THICK;
        } 
        else if (style == Border.EXTRA_THICK_LINE) {
            result = HSSFCellStyle.BORDER_THICK;
        } 
        else if (style == Border.DOUBLE) {
            result = HSSFCellStyle.BORDER_DOUBLE;
        } 

        return result;

    }

}
