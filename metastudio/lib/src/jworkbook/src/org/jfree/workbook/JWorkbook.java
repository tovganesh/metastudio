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
 * JWorkbook.java
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
 * 11-Feb-2002 : Implemented GnumericWriter and XLWriter as separate classes (DG);
 * 07-Jul-2003 : Changed GPL --> LGPL, changed package name, updated company name (DG);
 *
 */

package org.jfree.workbook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jfree.workbook.date.SerialDate;
import org.jfree.workbook.io.GnumericWriter;
import org.jfree.workbook.io.XLWriter;

/**
 * Represents a workbook so that it can be exported to (for now) the Gnumeric XML file format.  This
 * class can be used to export data to spreadsheets from Java.  The file format can, of course, be
 * read directly into Gnumeric.  But there is also a plan to write an importer for Excel (unless
 * there already is one).
 * <P>
 * Note that this workbook does not have a calculation engine, and I have no plans to add one at
 * this point (too much work!).  Maybe one day it will happen, it would be nice...
 */
public class JWorkbook {

    /** Version identification. */
    public static final String VERSION = "0.3.0";

    /** The workbook name. */
    protected String name;

    /** Visibility of the horizontal scroll bar. */
    protected boolean showHorizontalScrollbar = true;

    /** Visibility of the vertical scroll bar. */
    protected boolean showVerticalScrollbar = true;

    /** Visibility of the notebook tabs. */
    protected boolean showNotebookTabs = true;

    /** A summary of the workbook. */
    protected Summary summary;

    /** Names defined in the workbook - not currently supported. */
    protected List names;

    /** The width of the workbook when displayed in Gnumeric. */
    protected int geometryWidth = 552;

    /** The height of the workbook when displayed in Gnumeric. */
    protected int geometryHeight = 402;

    /** The worksheets contained in this workbook. */
    protected List<Worksheet> worksheets;

    /**
     * Default constructor: creates a new empty workbook.
     */
    public JWorkbook() {
        this("Untitled", 0);
    }

    /**
     * Standard constructor.
     * @param name The workbook name.
     * @param sheets The number of worksheets in the workbook (initially).
     */
    public JWorkbook(String name, int sheets) {

        this.name = name;
        this.summary = new Summary();
        this.summary.setApplication("JWorkbook " + JWorkbook.VERSION);
        this.worksheets = new ArrayList<Worksheet>();
        for (int i = 0; i < sheets; i++) {
            Worksheet sheet = new Worksheet("Sheet " + i);
            worksheets.add(sheet);
        }

    }

    /**
     * Returns the name of the workbook.
     * 
     * @return The name of the workbook.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name of the workbook.
     * 
     * @param name  the name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the summary (author, company etc.) for the workbook.
     * 
     * @return The summary for the workbook.
     */
    public Summary getSummary() {
        return this.summary;
    }

    /**
     * Returns the visibility of the horizontal scroll bar.
     * 
     * @return The visibility of the horizontal scroll bar.
     */
    public boolean isHorizontalScrollBarVisible() {
        return this.showHorizontalScrollbar;
    }

    /**
     * Returns the visibility of the vertical scroll bar.
     * 
     * @return The visibility of the vertical scroll bar.
     */
    public boolean isVerticalScrollBarVisible() {
        return this.showVerticalScrollbar;
    }

    /**
     * Returns the visibility of the notebook tabs.
     * 
     * @return The visibility of the vertical scroll bar.
     */
    public boolean isNotebookTabsVisible() {
        return this.showNotebookTabs;
    }

    /**
     * Returns the display width for the workbook.
     * 
     * @return The width.
     */
    public int getGeometryWidth() {
        return this.geometryWidth;
    }

    /**
     * Returns the display width for the workbook.
     * 
     * @return The height.
     */
    public int getGeometryHeight() {
        return this.geometryHeight;
    }


    /**
     * Adds a worksheet with the specified name.  The new worksheet will appear last in the
     * list of worksheets.
     * 
     * @param name The worksheet name.
     * 
     * @return The new worksheet.
     */
    public Worksheet add(String name) {

        Worksheet sheet = new Worksheet(name);
        worksheets.add(sheet);
        return sheet;

    }

    /**
     * Returns an iterator that gives access to all the worksheets in the workbook.
     * 
     * @return An iterator that gives access to all the worksheets in the workbook.
     */
    public Iterator getWorksheetsIterator() {

        return this.worksheets.iterator();

    }

    /**
     * Sets the author attribute.
     * 
     * @param name  the name of the author.
     */
    public void setAuthor(String name) {
        this.summary.setAuthor(name);
    }

    /**
     * Test program to demonstrate the JWorkbook.  Creates a workbook then saves it to Gnumeric
     * format and also XL format (partial support).
     * 
     * @param args  ignored.
     */
    public static void main(String[] args) {

        // create a new workbook...
        JWorkbook workbook = createSampleWorkbook();

        // write it out to a Gnumeric file...
        GnumericWriter writer1 = new GnumericWriter(workbook);
        try {
            writer1.saveWorkbook("sample.gnumeric");
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }

        // write it out to an XL file...
        XLWriter writer2 = new XLWriter();
        try {
            writer2.saveWorkbook(workbook, "sample.xls");
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    /**
     * Creates a sample workbook.
     * 
     * @return A workbook.
     */
    private static JWorkbook createSampleWorkbook() {

        JWorkbook result = new JWorkbook();

        result.setAuthor("David Gilbert");

        // add some data to the workbook...
        Worksheet worksheet1 = result.add("Sample");
        Worksheet worksheet2 = result.add("Another Worksheet");

        worksheet1.putLabel(1, 2, "Gnumeric Sample Spreadsheet");
        worksheet1.putLabel(3, 2, "Date:");
        worksheet1.putDate(3, 3, SerialDate.createInstance(12, 2, 2002));

        worksheet1.putLabel(5, 2, "Description:");
        worksheet1.putLabel(5, 3, "This spreadsheet has been created using JWorkbook 0.3.0, "
                                + "an open source Java class library.  "
                                + "Visit http://www.jfree.org/jworkbook for details.");

        worksheet1.applyVerticalAlignment(Style.ALIGN_TOP, 5, 2, 5, 3);
        worksheet1.applyWrapText(true, 5, 3);
        worksheet1.putComment(5, 3, "This is a comment!");

        worksheet1.putLabel(7, 2, "Calculation:");

        worksheet1.putValue(7, 3, 147.43);
        worksheet1.putValue(8, 3, 752.45);
        worksheet1.putValue(9, 3, 67.34);
        worksheet1.putFormula(10, 3, "=sum(D8:D10)");
        worksheet1.addName("Total", "Sample!$D$11");
        worksheet1.putComment(10, 3, "This cell is important, so it has a name defined for it.");

        Style style = new Style(new FontStyle("Courier", 12, false, true, false, false),
                                Style.ALIGN_LEFT, 2);
        StyleRegion styleRegion = new StyleRegion(style, 1, 2, 1, 2);
        worksheet1.addStyleRegion(styleRegion);
        worksheet1.setRowHeight(1, 17.5);
        worksheet1.setRowHeight(5, 60.0);

        worksheet1.applyBackgroundColor(Color.createInstance(Color.GNUMERIC_LIGHT_BLUE),
                                        1, 1, 1, 4);

        // set the column widths...
        worksheet1.setColumnWidth(0, 20);
        worksheet1.setColumnWidth(1, 10);
        worksheet1.setColumnWidth(2, 66);
        worksheet1.setColumnWidth(3, 180);
        worksheet1.setColumnWidth(4, 10);
        worksheet1.setColumnWidth(5, 20);

        FontStyle font1 = new FontStyle("Helvetica", 9, true, false, false, false);
        worksheet1.applyFont(font1, 3, 2, 7, 2);
        worksheet1.applyFont(font1, 10, 3);

        // Apply some borders...
        worksheet1.applyOutline(1, 1, 1, 4);
        worksheet1.applyOutline(2, 1, 11, 4);
        worksheet1.applyVerticalAlignment(Style.ALIGN_TOP, 5, 2, 5, 3);

        worksheet2.putLabel(1, 1, "This worksheet is here just so you know it can be done!");

        return result;

    }

}
