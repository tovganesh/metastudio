/* =============================================================
 * JWorkbook : a free Java library for writing spreadsheet files
 * =============================================================
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
 * -------------------
 * GnumericWriter.java
 * -------------------
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

package org.jfree.workbook.io;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;

import org.jfree.workbook.Border;
import org.jfree.workbook.Cell;
import org.jfree.workbook.Cells;
import org.jfree.workbook.ColumnAttributes;
import org.jfree.workbook.ColumnAttributesManager;
import org.jfree.workbook.Comment;
import org.jfree.workbook.FontStyle;
import org.jfree.workbook.JWorkbook;
import org.jfree.workbook.Margins;
import org.jfree.workbook.Name;
import org.jfree.workbook.NamesManager;
import org.jfree.workbook.PrintInformation;
import org.jfree.workbook.Row;
import org.jfree.workbook.RowAttributes;
import org.jfree.workbook.RowAttributesManager;
import org.jfree.workbook.Selection;
import org.jfree.workbook.Selections;
import org.jfree.workbook.Solver;
import org.jfree.workbook.Style;
import org.jfree.workbook.StyleRegion;
import org.jfree.workbook.Styles;
import org.jfree.workbook.Summary;
import org.jfree.workbook.Worksheet;

/**
 * A class that knows how to write a JWorkbook object to the Gnumeric XML file format.
 */
public class GnumericWriter {

    /** Two spaces for indenting. */
    private static final String INDENT = "  ";

    /** The workbook. */
    protected JWorkbook workbook;

    /**
     * Creates a new writer for the specified workbook.
     * 
     * @param workbook  the workbook.
     */
    public GnumericWriter(JWorkbook workbook) {
        this.workbook = workbook;
    }

    /**
     * Saves the workbook in Gnumeric XML format.  We don't gzip the output yet, but that will
     * be simple to add.
     * 
     * @param filename  the name of the file.
     * 
     * @throws IOException if there is an I/O problem.
     */
    public void saveWorkbook(String filename) throws IOException {

        PrintStream out = new PrintStream(
                              new BufferedOutputStream(
                                  new FileOutputStream(filename)
                              )
                          );
        out.println("<?xml version=\"1.0\"?>");
        writeWorkbook(out);
        out.close();

    }

    /**
     * Writes the workbook element to the specified stream.
     * 
     * @param out  the output stream.
     */
    private void writeWorkbook(PrintStream out) {

        out.println("<gmr:Workbook xmlns:gmr=\"http://www.gnome.org/gnumeric/v7\">");

        writeAttributes(out, INDENT);
        writeSummary(out, INDENT);
        writeWorkbookNames(out, INDENT);
        writeGeometry(out, INDENT);

        out.println(INDENT + "<gmr:Sheets>");

        Iterator iterator = this.workbook.getWorksheetsIterator();
        while (iterator.hasNext()) {

            Worksheet sheet = (Worksheet) iterator.next();
            writeWorksheet(sheet, out, INDENT + INDENT);

        }
        out.println(INDENT + "</gmr:Sheets>");

        writeUIData(out, INDENT);

        out.println("</gmr:Workbook>");

    }

    /**
     * Writes the attributes element to the specified stream.
     * 
     * @param out  the output stream.
     * @param indent  the indentation.
     */
    private void writeAttributes(PrintStream out, String indent) {

        String nextIndent = indent + INDENT;

        out.println(indent + "<gmr:Attributes>");

        String horizontal = (this.workbook.isHorizontalScrollBarVisible() ? "TRUE" : "FALSE");
        writeAttribute(out, "WorkbookView::show_horizontal_scrollbar", 4, horizontal, nextIndent);

        String vertical = (this.workbook.isVerticalScrollBarVisible() ? "TRUE" : "FALSE");
        writeAttribute(out, "WorkbookView::show_vertical_scrollbar", 4, vertical, nextIndent);

        String tabs = (this.workbook.isNotebookTabsVisible() ? "TRUE" : "FALSE");
        writeAttribute(out, "WorkbookView::show_notebook_tabs", 4, tabs, nextIndent);

        out.println(indent + "</gmr:Attributes>");

    }

    /**
     * Writes one attribute element to the stream.
     * 
     * @param out  the output stream.
     * @param name  the attribute name.
     * @param type  the attribute type.
     * @param value  the attribute value.
     * @param indent  the indentation.
     */
    private void writeAttribute(PrintStream out,
                                String name, int type, String value, String indent) {

        out.println(indent + "<gmr:Attribute>");
        out.println(indent + INDENT + "<gmr:name>" + name + "</gmr:name>");
        out.println(indent + INDENT + "<gmr:type>" + type + "</gmr:type>");
        out.println(indent + INDENT + "<gmr:value>" + value + "</gmr:value>");
        out.println(indent + "</gmr:Attribute>");

    }

    /**
     * Writes the workbook summary element.
     * 
     * @param out  the output stream.
     * @param indent  the indentation.
     */
    private void writeSummary(PrintStream out, String indent) {

        Summary summary = this.workbook.getSummary();

        out.println(indent + "<gmr:Summary>");

        String nextIndent = indent + INDENT;

        if (summary.getTitle() != null) {
            writeSummaryItem(out, "title", summary.getTitle(), nextIndent);
        }

        if (summary.getKeywords() != null) {
            writeSummaryItem(out, "keywords", summary.getKeywords(), nextIndent);
        }

        if (summary.getComments() != null) {
            writeSummaryItem(out, "comments", summary.getComments(), nextIndent);
        }

        if (summary.getCategory() != null) {
            writeSummaryItem(out, "category", summary.getCategory(), nextIndent);
        }

        if (summary.getManager() != null) {
            writeSummaryItem(out, "manager", summary.getManager(), nextIndent);
        }

        if (summary.getApplication() != null) {
            writeSummaryItem(out, "application", summary.getApplication(), nextIndent);
        }

        if (summary.getAuthor() != null) {
            writeSummaryItem(out, "author", summary.getAuthor(), nextIndent);
        }

        if (summary.getCompany() != null) {
            writeSummaryItem(out, "company", summary.getCompany(), nextIndent);
        }

        out.println(indent + "</gmr:Summary>");

    }

    /**
     * Writes one summary item element to the stream.
     * 
     * @param out  The output stream.
     * @param name  The item name.
     * @param value  The item value.
     * @param indent  The indentation.
     */
    private void writeSummaryItem(PrintStream out,
                                  String name, String value, String indent) {

        out.println(indent + "<gmr:Item>");
        out.println(indent + INDENT + "<gmr:name>" + name + "</gmr:name>");
        out.println(indent + INDENT + "<gmr:val-string>" + value + "</gmr:val-string>");
        out.println(indent + "</gmr:Item>");

    }

    /**
     * Write the names for the workbook.  Currently not supported, so we write out an empty names
     * element.
     * 
     * @param out  the output stream.
     * @param indent  the indentation.
     */
    private void writeWorkbookNames(PrintStream out, String indent) {

        out.println(indent + "<gmr:Names/>");

    }

    /**
     * Write the "geometry" for the workbook.
     * 
     * @param out  the output stream.
     * @param indent  the indentation.
     */
    private void writeGeometry(PrintStream out, String indent) {

        out.println(indent + "<gmr:Geometry Width=\"" + this.workbook.getGeometryWidth()
                           + "\" Height=\"" + this.workbook.getGeometryHeight() + "\"/>");

    }

    /**
     * Write the "UIData" for the workbook.
     * 
     * @param out  the output stream.
     * @param indent  the indentation.
     */
    private void writeUIData(PrintStream out, String indent) {
        out.println(indent + "<gmr:UIData SelectedTab=\"0\"/>");
    }


    /**
     * Writes a worksheet element in the Gnumeric format to a stream.
     * 
     * @param worksheet  the worksheet.
     * @param out  the output stream.
     * @param indent  the indentation.
     */
    public void writeWorksheet(Worksheet worksheet, PrintStream out, String indent) {

        boolean displayFormulae = worksheet.getDisplayFormulae();
        boolean hideZeros = worksheet.getHideZeros();
        boolean isGridVisible = worksheet.isGridVisible();
        boolean isColumnHeaderVisible = worksheet.isColumnHeaderVisible();
        boolean isRowHeaderVisible = worksheet.isRowHeaderVisible();

        out.println(indent + "<gmr:Sheet DisplayFormulas=\"" + (displayFormulae ? "true" : "false")
                           + "\" HideZero=\"" + (hideZeros ? "true" : "false")
                           + "\" HideGrid=\"" + (isGridVisible ? "false" : "true")
                           + "\" HideColHeader=\"" + (isColumnHeaderVisible ? "false" : "true")
                           + "\" HideRowHeader=\"" + (isRowHeaderVisible ? "false" : "true")
                           + "\">");

        String nextIndent = indent + INDENT;

        writeName(worksheet, out, nextIndent);
        writeMaxCol(worksheet, out, nextIndent);
        writeMaxRow(worksheet, out, nextIndent);
        writeZoom(worksheet, out, nextIndent);

        out.println(nextIndent + "<gmr:Names>");
        writeWorksheetNames(worksheet.getNamesManager(), out, nextIndent + INDENT);
        out.println(nextIndent + "</gmr:Names>");

        PrintInformation printInfo = worksheet.getPrintInformation();
        writePrintInformation(printInfo, out, nextIndent);

        Styles styles = worksheet.getStyles();
        writeStyles(styles, out, nextIndent);

        ColumnAttributesManager columnAttributes = worksheet.getColumnAttributesManager();
        writeColumns(columnAttributes, out, nextIndent);

        RowAttributesManager rowAttributes = worksheet.getRowAttributesManager();
        writeRows(rowAttributes, out, nextIndent);

        Selections selections = worksheet.getSelections();
        writeSelections(selections, out, nextIndent);

        writeObjects(worksheet, out, nextIndent);

        Cells cells = worksheet.getCells();
        writeCells(cells, out, nextIndent);

        Solver solver = worksheet.getSolver();
        writeSolver(solver, out, nextIndent);

        out.println(indent + "</gmr:Sheet>");

    }

    /**
     * Writes the name element.
     * 
     * @param worksheet  the worksheet.
     * @param out  the output stream.
     * @param indent  the indentation.
     */
    private void writeName(Worksheet worksheet, PrintStream out, String indent) {

        out.println(indent + "<gmr:Name>" + worksheet.getName() + "</gmr:Name>");

    }

    /**
     * Writes the maximum column element.
     * 
     * @param worksheet  the worksheet.
     * @param out  the output stream.
     * @param indent  the indentation.
     */
    private void writeMaxCol(Worksheet worksheet, PrintStream out, String indent) {

        out.println(indent + "<gmr:MaxCol>" + worksheet.getMaxColumn() + "</gmr:MaxCol>");

    }

    /**
     * Writes the maximum row element.
     * 
     * @param worksheet  the worksheet.
     * @param out  the output stream.
     * @param indent  the indentation.
     */
    private void writeMaxRow(Worksheet worksheet, PrintStream out, String indent) {

        out.println(indent + "<gmr:MaxRow>" + worksheet.getMaxRow() + "</gmr:MaxRow>");

    }

    /**
     * Writes the zoom element.
     * 
     * @param worksheet  the worksheet.
     * @param out  the output stream.
     * @param indent  the indentation.
     */
    private void writeZoom(Worksheet worksheet, PrintStream out, String indent) {

        out.println(indent + "<gmr:Zoom>" + worksheet.getZoom() + "</gmr:Zoom>");

    }

    /**
     * Writes out an objects element.  Only comment objects are supported by JWorkbook for now.
     * 
     * @param worksheet  the worksheet.
     * @param out  the output stream.
     * @param indent  the indentation.
     */
    private void writeObjects(Worksheet worksheet, PrintStream out, String indent) {

        out.println(indent + "<gmr:Objects>");
        Iterator iterator = worksheet.getComments().iterator();
        while (iterator.hasNext()) {
            Comment comment = (Comment) iterator.next();
            writeComment(comment, out, indent + INDENT);
        }
        out.println(indent + "</gmr:Objects>");

    }

    /**
     * Writes a print information element to the stream.
     * 
     * @param info  print information.
     * @param out  the output stream.
     * @param indent  the indentation.
     */
    public void writePrintInformation(PrintInformation info, PrintStream out, String indent) {

        out.println(indent + "<gmr:PrintInformation>");

        String nextIndent = indent + INDENT;

        writeMargins(info.getMargins(), out, nextIndent);
        writeVerticallyCentered(info, out, nextIndent);
        writeHorizontallyCentered(info, out, nextIndent);
        writeGridVisible(info, out, nextIndent);
        writeMonochrome(info, out, nextIndent);
        writeDraft(info, out, nextIndent);
        writeTitles(info, out, nextIndent);
        writeRepeatTop(info, out, nextIndent);
        writeRepeatLeft(info, out, nextIndent);
        writeOrder(info, out, nextIndent);
        writeOrientation(info, out, nextIndent);
        writeHeader(info, out, nextIndent);
        writeFooter(info, out, nextIndent);
        writePaper(info, out, nextIndent);

        out.println(indent + "</gmr:PrintInformation>");

    }

    /**
     * Writes a vcenter element to the stream.
     * 
     * @param info  print information.
     * @param out  the output stream.
     * @param indent  the indentation.
     */
    private void writeVerticallyCentered(PrintInformation info, PrintStream out, String indent) {

        String center = (info.isVerticallyCentered() ? "1" : "0");
        out.println(indent + "<gmr:vcenter value=\"" + center + "\"/>");

    }

    /**
     * Writes a hcenter element to the stream.
     * 
     * @param info  print information.
     * @param out  the output stream.
     * @param indent  the indentation.
     */
    private void writeHorizontallyCentered(PrintInformation info, PrintStream out, String indent) {

        String center = (info.isHorizontallyCentered() ? "1" : "0");
        out.println(indent + "<gmr:hcenter value=\"" + center + "\"/>");

    }

    /**
     * Writes a grid visible element to the stream.
     * 
     * @param info  print information.
     * @param out  the output stream.
     * @param indent  the indentation.
     */
    private void writeGridVisible(PrintInformation info, PrintStream out, String indent) {

        String grid = (info.isGridVisible() ? "1" : "0");
        out.println(indent + "<gmr:grid value=\"" + grid + "\"/>");

    }

    /**
     * Writes a monochrome element to the stream.
     * 
     * @param info  print information.
     * @param out  the output stream.
     * @param indent  the indentation.
     */
    private void writeMonochrome(PrintInformation info, PrintStream out, String indent) {

        String flag = (info.isMonochrome() ? "1" : "0");
        out.println(indent + "<gmr:monochrome value=\"" + flag + "\"/>");

    }

    /**
     * Writes a draft element to the stream.
     * 
     * @param info  print information.
     * @param out  the output stream.
     * @param indent  the indentation.
     */
    private void writeDraft(PrintInformation info, PrintStream out, String indent) {

        String flag = (info.isDraft() ? "1" : "0");
        out.println(indent + "<gmr:draft value=\"" + flag + "\"/>");

    }

    /**
     * Writes a titles element to the stream.
     * 
     * @param info  print information.
     * @param out  the output stream.
     * @param indent  the indentation.
     */
    private void writeTitles(PrintInformation info, PrintStream out, String indent) {

        String flag = (info.isTitlesVisible() ? "1" : "0");
        out.println(indent + "<gmr:titles value=\"" + flag + "\"/>");

    }

    /**
     * Writes a repeat_top element to the stream.
     * 
     * @param info  print information.
     * @param out  the output stream.
     * @param indent  the indentation.
     */
    private void writeRepeatTop(PrintInformation info, PrintStream out, String indent) {

        out.println(indent + "<gmr:repeat_top value=\"" + info.getRepeatTop() + "\"/>");

    }

    /**
     * Writes a repeat_left element to the stream.
     * 
     * @param info  print information.
     * @param out  the output stream.
     * @param indent  the indentation.
     */
    private void writeRepeatLeft(PrintInformation info, PrintStream out, String indent) {

        out.println(indent + "<gmr:repeat_left value=\"" + info.getRepeatLeft() + "\"/>");

    }

    /**
     * Writes an order element to the stream.
     * 
     * @param info  print information.
     * @param out  the output stream.
     * @param indent  the indentation.
     */
    private void writeOrder(PrintInformation info, PrintStream out, String indent) {

        out.println(indent + "<gmr:order>" + info.getOrder() + "</gmr:order>");

    }

    /**
     * Writes an orientation element to the stream.
     * 
     * @param info  print information.
     * @param out  the output stream.
     * @param indent  the indentation.
     */
    private void writeOrientation(PrintInformation info, PrintStream out, String indent) {

        if (info.getOrientation() == 0) {
            out.println(indent + "<gmr:orientation>landscape</gmr:orientation>");
        }
        else {
            out.println(indent + "<gmr:orientation>portrait</gmr:orientation>");
        }

    }

    /**
     * Writes a header element to the stream.
     * 
     * @param info  print information.
     * @param out  the output stream.
     * @param indent  the indentation.
     */
    private void writeHeader(PrintInformation info, PrintStream out, String indent) {

        out.println(indent + "<gmr:Header Left=\"" + info.getHeaderLeft()
                           + "\" Middle=\"" + info.getHeaderMiddle()
                           + "\" Right=\"" + info.getHeaderRight() + "\"/>");

    }

    /**
     * Writes a footer element to the stream.
     * 
     * @param info  print information.
     * @param out  the output stream.
     * @param indent  the indentation.
     */
    private void writeFooter(PrintInformation info, PrintStream out, String indent) {

        out.println(indent + "<gmr:Footer Left=\"" + info.getFooterLeft()
                           + "\" Middle=\"" + info.getFooterMiddle()
                           + "\" Right=\"" + info.getFooterRight() + "\"/>");

    }

    /**
     * Writes a paper element to the stream.
     * 
     * @param info  print information.
     * @param out  the output stream.
     * @param indent  the indentation.
     */
    private void writePaper(PrintInformation info, PrintStream out, String indent) {

        out.println(indent + "<gmr:paper>" + info.getPaperSize() + "</gmr:paper>");

    }

    /**
     * Writes a styles element to the stream.
     * 
     * @param styles  the styles.
     * @param out  the output stream.
     * @param indent  the indentation.
     */
    public void writeStyles(Styles styles, PrintStream out, String indent) {

        out.println(indent + "<gmr:Styles>");

        Iterator iterator = styles.getStylesIterator();
        while (iterator.hasNext()) {
            StyleRegion region = (StyleRegion) iterator.next();
            writeStyleRegion(region, out, indent + INDENT);
        }

        out.println(indent + "</gmr:Styles>");

    }

    /**
     * Writes the columns element to the specified stream.
     * 
     * @param columnAttributes  the column attributes.
     * @param out  the output stream.
     * @param indent  the indentation.
     */
    public void writeColumns(ColumnAttributesManager columnAttributes,
                             PrintStream out, String indent) {

        out.println(indent + "<gmr:Cols DefaultSizePts=\""
                           + columnAttributes.getDefaultColumnWidth() + "\">");

        Iterator iterator = columnAttributes.getAttributesIterator();
        while (iterator.hasNext()) {
            ColumnAttributes attributes = (ColumnAttributes) iterator.next();
            writeColInfo(attributes, out, indent + INDENT);
        }

        out.println(indent + "</gmr:Cols>");

    }

    /**
     * Writes the rows element to the specified stream.
     * 
     * @param rowAttributes  the row attributes.
     * @param out  the output stream.
     * @param indent  the indentation.
     */
    public void writeRows(RowAttributesManager rowAttributes, PrintStream out, String indent) {

        out.println(indent + "<gmr:Rows DefaultSizePts=\""
                           + rowAttributes.getDefaultRowHeight()
                           + "\">");

        Iterator iterator = rowAttributes.getAttributesIterator();
        while (iterator.hasNext()) {
            RowAttributes attributes = (RowAttributes) iterator.next();
            writeRowInfo(attributes, out, indent + INDENT);
        }

        out.println(indent + "</gmr:Rows>");

    }

    /**
     * Writes a selections element to the stream.
     * 
     * @param selections  the selections.
     * @param out  the output stream.
     * @param indent  the indentation.
     */
    public void writeSelections(Selections selections, PrintStream out, String indent) {

        out.println(indent + "<gmr:Selections "
                           + "CursorCol=\"" + selections.getCursorColumn() + "\" "
                           + "CursorRow=\"" + selections.getCursorRow()
                           + "\">");

        Iterator iterator = selections.getIterator();
        while (iterator.hasNext()) {
            Selection selection = (Selection) iterator.next();
            writeSelection(selection, out, indent + INDENT);
        }

        out.println(indent + "</gmr:Selections>");

    }

    /**
     * Writes the cells to the specified stream in the format required by the Gnumeric XML file.
     * 
     * @param cells  the cells.
     * @param out  the output stream.
     * @param indent  the indentation.
     */
    public void writeCells(Cells cells, PrintStream out, String indent) {

        out.println(indent + "<gmr:Cells>");
        Iterator iterator = cells.getRowsIterator();
        while (iterator.hasNext()) {
            Row row = (Row) iterator.next();
            writeRow(row, out, indent + INDENT);
        }
        out.println(indent + "</gmr:Cells>");

    }

    /**
     * Writes a solver element to the stream.
     * 
     * @param solver  the solver.
     * @param out  the output stream.
     * @param indent  the indentation.
     */
    public void writeSolver(Solver solver, PrintStream out, String indent) {

        out.println(indent + "<gmr:Solver TargetCol=\"" + solver.getTargetColumn()
                           + "\" TargetRow=\"" + solver.getTargetRow()
                           + "\" ProblemType=\"" + solver.getProblemType()
                           + "\" Inputs=\"" + solver.getInputs() + "\"/>");
    }

    /**
     * Writes a names element in the Gnumeric format.
     * 
     * @param names  the names manager.
     * @param out  the output stream.
     * @param indent  the indentation.
     */
    public void writeWorksheetNames(NamesManager names, PrintStream out, String indent) {

        Iterator iterator = names.getNamesIterator();
        while (iterator.hasNext()) {
            Name current = (Name) iterator.next();
            out.println(indent + "<gmr:Name>");
            out.println(indent + INDENT + "<gmr:name>" + current.getName() + "</gmr:name>");
            out.println(indent + INDENT + "<gmr:value>" + current.getValue() + "</gmr:value>");
            out.println(indent + "</gmr:Name>");
        }

    }

    /**
     * Writes the margins element to the stream.
     * 
     * @param margins  the margins.
     * @param out  the output stream.
     * @param indent  the indentation.
     */
    public void writeMargins(Margins margins, PrintStream out, String indent) {

        out.println(indent + "<gmr:Margins>");

        String nextIndent = indent + INDENT;
        writeMargin(out, "top", margins.getTopPts(), nextIndent);
        writeMargin(out, "bottom", margins.getBottomPts(), nextIndent);
        writeMargin(out, "left", margins.getLeftPts(), nextIndent);
        writeMargin(out, "right", margins.getRightPts(), nextIndent);
        writeMargin(out, "header", margins.getHeaderPts(), nextIndent);
        writeMargin(out, "footer", margins.getFooterPts(), nextIndent);

        out.println(indent + "</gmr:Margins>");

    }

    /**
     * Writes a single margin element to the stream.
     * 
     * @param out  the output stream.
     * @param tag  the tag name.
     * @param pts  the points.
     * @param indent  the indentation.
     */
    private void writeMargin(PrintStream out, String tag, double pts, String indent) {
        out.println(indent + "<gmr:" + tag + " Points=\"" + pts + "\" PrefUnit=\"cm\"/>");
    }

    /**
     * Writes a style border element to the stream.
     * 
     * @param style  the style.
     * @param out  the output stream.
     * @param indent  the indentation.
     */
    public void writeBorderStyle(Border style, PrintStream out, String indent) {

        String nextIndent = indent + INDENT;

        out.println(indent + "<gmr:StyleBorder>");

        out.println(nextIndent + "<gmr:Top Style=\"" + style.getTop() + "\" Color=\"0:0:0\"/>");
        out.println(nextIndent + "<gmr:Bottom Style=\"" + style.getBottom() 
                               + "\" Color=\"0:0:0\"/>");
        out.println(nextIndent + "<gmr:Left Style=\"" + style.getLeft() + "\" Color=\"0:0:0\"/>");
        out.println(nextIndent + "<gmr:Right Style=\"" + style.getRight() + "\" Color=\"0:0:0\"/>");
        out.println(nextIndent + "<gmr:Diagonal Style=\"" + style.getDiagonal() 
                               + "\" Color=\"0:0:0\"/>");
        out.println(nextIndent + "<gmr:Rev-Diagonal Style=\"" + style.getReverseDiagonal()
                                                              + "\" Color=\"0:0:0\"/>");

        out.println(indent + "</gmr:StyleBorder>");

    }

    /**
     * Writes a date cell.
     * 
     * @param cell  the cell.
     * @param out  the output stream.
     * @param indent  the indentation.
     */
    public void writeDateCell(Cell cell, PrintStream out, String indent) {

        out.println(indent + "<gmr:Cell Col=\""
                           + cell.getColumn() + "\" Row=\""
                           + cell.getRow() + "\" ValueType=\"40\" ValueFormat=\"d-mmm-yyyy\">");
        writeCellContent(cell, out, indent + INDENT);
        out.println(indent + "</gmr:Cell>");

    }

    /**
     * Write the cell element.  So far, it will work for labels, numbers and expressions.  Of
     * course, the intention is to ensure that it works for all types.
     * 
     * @param cell  the cell.
     * @param out  the output stream.
     * @param indent  the indentation.
     */
    public void writeCell2(Cell cell, PrintStream out, String indent) {

        out.println(indent + "<gmr:Cell Col=\"" + cell.getColumn()
                           + "\" Row=\"" + cell.getRow() + "\">");
        writeCellContent(cell, out, indent + INDENT);
        out.println(indent + "</gmr:Cell>");

    }

    /**
     * Writes a style element to the stream.
     * 
     * @param style  the style.
     * @param out  the stream.
     * @param indent  the indentation.
     */
    public void writeStyle(Style style, PrintStream out, String indent) {

        out.println(indent + "<gmr:Style "
                           + "HAlign=\"" + style.getHorizontalAlignment()
                           + "\" VAlign=\"" + style.getVerticalAlignment()
                           + "\" WrapText=\"" + (style.isWrapText() ? "1" : "0")
                           + "\" Orient=\"" + style.getOrientation()
                           + "\" Shade=\"" + style.getShade()
                           + "\" Indent=\"" + style.getCellIndent()
                           + "\" Fore=\"" + style.getForegroundColor().toString()
                           + "\" Back=\"" + style.getBackgroundColor().toString()
                           + "\" PatternColor=\"" + style.getPatternColor().toString()
                           + "\" Format=\"" + style.getFormat()
                           + "\">");

        String nextIndent = indent + INDENT;
        writeFontStyle(style.getFont(), out, nextIndent);
        writeBorderStyle(style.getBorder(), out, nextIndent);

        out.println(indent + "</gmr:Style>");

    }

    /**
     * Write the cell element.  So far, it will work for labels, numbers and expressions.  Of
     * course, the intention is to ensure that it works for all types.
     * 
     * @param cell  the cell.
     * @param out  the output stream.
     * @param indent  the indentation.
     */
    public void writeCell(Cell cell, PrintStream out, String indent) {

        int type = cell.getType();

        if (type == 0) {
            out.println(indent + "<gmr:Cell Col=\"" + cell.getColumn() + "\" Row=\""
                                                    + cell.getRow() + "\" ExprID=\"1\">");
            writeCellContent(cell, out, indent + INDENT);
            out.println(indent + "</gmr:Cell>");
        }
        else if (type == 30) {
            out.println(indent + "<gmr:Cell Col=\"" + cell.getColumn() + "\" Row=\""
                                                    + cell.getRow() + "\" ValueType=\"30\">");
            writeCellContent(cell, out, indent + INDENT);
            out.println(indent + "</gmr:Cell>");
        }
        else if (type == 40) {
            out.println(indent + "<gmr:Cell Col=\"" + cell.getColumn() + "\" Row=\""
                                                    + cell.getRow() + "\" ValueType=\"40\">");
            writeCellContent(cell, out, indent + INDENT);
            out.println(indent + "</gmr:Cell>");
        }
        else if (type == 60) {
            out.println(indent + "<gmr:Cell Col=\"" + cell.getColumn() + "\" Row=\""
                                                    + cell.getRow() + "\" ValueType=\"60\">");
            writeCellContent(cell, out, indent + INDENT);
            out.println(indent + "</gmr:Cell>");
        }

    }

    /**
     * Write the cell contents.
     * 
     * @param cell  the cell.
     * @param out  the output stream.
     * @param indent  the indentation.
     */
    protected void writeCellContent(Cell cell, PrintStream out, String indent) {

        out.println(indent + "<gmr:Content>" + cell.getContent() + "</gmr:Content>");

    }

    /**
     * Writes a style region element to the stream.
     * 
     * @param region  the style region.
     * @param out  the output stream.
     * @param indent  the indentation.
     */
    public void writeStyleRegion(StyleRegion region, PrintStream out, String indent) {

        out.println(indent + "<gmr:StyleRegion "
                           + "startCol=\"" + region.getStartColumn()
                           + "\" startRow=\"" + region.getStartRow()
                           + "\" endCol=\"" + region.getEndColumn()
                           + "\" endRow=\"" + region.getEndRow()
                           + "\">");

        writeStyle(region.getStyle(), out, indent + INDENT);

        out.println(indent + "</gmr:StyleRegion>");

    }
    /**
     * Writes a row element to the stream.
     * 
     * @param row  the row.
     * @param out  the output stream.
     * @param indent  the indentation.
     */
    public void writeRow(Row row, PrintStream out, String indent) {

        Iterator iterator = row.getCellsIterator();
        while (iterator.hasNext()) {
            Cell cell = (Cell) iterator.next();
            int type = cell.getType();
            if (type == Cell.EXPRESSION_TYPE) {
                writeCell(cell, out, indent);
            }
            else if (type == Cell.DATE_TYPE) {
                writeDateCell(cell, out, indent);
            }
            else if (type == Cell.VALUE_TYPE) {
                writeCell(cell, out, indent);
            }
            else if (type == Cell.LABEL_TYPE) {
                writeCell(cell, out, indent);
            }
        }

    }

    /**
     * Writes a style font element to the stream.
     * 
     * @param font  the font style.
     * @param out  the stream for output.
     * @param indent  the indentation.
     */
    public void writeFontStyle(FontStyle font, PrintStream out, String indent) {

        String boldText = (font.isBold() ? "1" : "0");
        String italicText = (font.isItalic() ? "1" : "0");
        String underlineText = (font.isUnderline() ? "1" : "0");
        String strikethroughText = (font.isStrikethrough() ? "1" : "0");

        out.println(indent + "<gmr:Font Unit=\"" + font.getSize() + "\" Bold=\"" + boldText
                                                 + "\" Italic=\"" + italicText
                                                 + "\" Underline=\"" + underlineText
                                                 + "\" StrikeThrough=\"" + strikethroughText
                                                 + "\">" + font.getName() + "</gmr:Font>");

    }

    /**
     * Writes the columns element to the specified stream in the Gnumeric format.
     * 
     * @param attributes  the column attributes.
     * @param out  the output stream.
     * @param indent  the indentation.
     */
    public void writeColInfo(ColumnAttributes attributes, PrintStream out, String indent) {

        out.println(indent + "<gmr:ColInfo "
                           + "No=\"" + attributes.getStartColumn() + "\" "
                           + "Unit=\"" + attributes.getWidth() + "\" "
                           + "MarginA=\"" + attributes.getMarginA() + "\" "
                           + "MarginB=\"" + attributes.getMarginB() + "\" "
                           + "HardSize=\"" + (attributes.isHardSize() ? "1" : "0") + "\" "
                           + "Hidden=\"" + (attributes.isHidden() ? "1" : "0") + "\" "
                           + "Count=\"" 
                           + (attributes.getEndColumn() - attributes.getStartColumn() + 1)
                           + "\"/>");

    }

    /**
     * Writes the rows element to the specified stream in the Gnumeric format.
     * 
     * @param attributes  the row attributes.
     * @param out  the output stream.
     * @param indent  the indentation.
     */
    public void writeRowInfo(RowAttributes attributes, PrintStream out, String indent) {

        out.println(indent + "<gmr:RowInfo No=\"" + attributes.getStartRow() + "\" "
                           + "Unit=\"" + attributes.getHeight() + "\" "
                           + "MarginA=\"" + attributes.getMarginA() + "\" "
                           + "MarginB=\"" + attributes.getMarginB() + "\" "
                           + "HardSize=\"" + (attributes.isHardSize() ? "1" : "0") + "\" "
                           + "Hidden=\"" + (attributes.isHidden() ? "1" : "0") + "\" "
                           + "Count=\"" + (attributes.getEndRow() - attributes.getStartRow() + 1)
                           + "\"/>");

    }

    /**
     * Writes a comment element in the Gnumeric file format.
     * 
     * @param comment  the comment.
     * @param out  the output stream.
     * @param indent  the indentation.
     */
    public void writeComment(Comment comment, PrintStream out, String indent) {

        String cellReference = Worksheet.cellReference(comment.getRow(), comment.getColumn());
        out.println(indent + "<gmr:CellComment Author=\"" + comment.getAuthor()
                           + "\" Text=\"" + comment.getText()
                           + "\" ObjectBound=\"" + cellReference
                           + "\" ObjectOffset=\"" + "0 0 0 0"
                           + "\" ObjectAnchorType=\"" + "33 32 33 32"
                           + "\"/>");

    }

    /**
     * Writes a selection element to the stream.
     * 
     * @param selection  the selection.
     * @param out  the output stream.
     * @param indent  the indentation.
     */
    public void writeSelection(Selection selection, PrintStream out, String indent) {

        out.println(indent + "<gmr:Selection "
                           + "startCol=\"" + selection.getStartColumn() + "\" "
                           + "startRow=\"" + selection.getStartRow() + "\" "
                           + "endCol=\"" + selection.getEndColumn() + "\" "
                           + "endRow=\"" + selection.getEndRow()
                           + "\"/>");

    }

}
