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
 * ---------------------
 * PrintInformation.java
 * ---------------------
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
 * 12-Feb-2002 : Added accessor methods (DG);
 * 07-Jul-2003 : Changed GPL --> LGPL, changed package name, updated company name (DG);
 *
 */

package org.jfree.workbook;

/**
 * Records print information for a workbook.
 */
public class PrintInformation {

    /** Margins. */
    protected Margins margins;

    /** Flag indicating whether the worksheet is vertically centered on the page. */
    protected boolean verticallyCentered;

    /** Flag indicating whether the worksheet is horizontally centered on the page. */
    protected boolean horizontallyCentered;

    /** Flag indicating whether the grid is visible. */
    protected boolean gridVisible;

    /** Flag indicating whether the printing is monochrome. */
    protected boolean monochrome;

    /** Flag indicating whether the printing is draft quality. */
    protected boolean draft;

    /** Titles? */
    protected boolean titles;

    /** ?? */
    protected String repeatTop;

    /** ?? */
    protected String repeatLeft;

    /** ?? */
    protected String order;

    /** The paper orientation. */
    protected int orientation;  // 0 = LANDSCAPE; 1 = PORTRAIT

    /** The left header. */
    protected String headerLeft;

    /** The middle header. */
    protected String headerMiddle;

    /** The right header. */
    protected String headerRight;

    /** The left footer. */
    protected String footerLeft;

    /** The middle footer. */
    protected String footerMiddle;

    /** The right footer. */
    protected String footerRight;

    /** The paper size. */
    protected String paper;

    /**
     * Default constructor.
     */
    public PrintInformation() {

        margins = new Margins();
        verticallyCentered = false;
        horizontallyCentered = false;
        gridVisible = true;
        monochrome = false;
        draft = false;
        titles = false;
        repeatTop = "";
        repeatLeft = "";
        order = "r_then_d";
        orientation = 0;  // landscape
        headerLeft = "";
        headerMiddle = "&amp;[TAB]";
        headerRight = "";
        footerLeft = "";
        footerMiddle = "Page &amp;[PAGE]";
        footerRight = "";
        paper = "A4";

    }

    /**
     * Returns the margins.
     * 
     * @return The margins.
     */
    public Margins getMargins() {
        return this.margins;
    }

    /**
     * Returns a flag indicating whether or not the content is vertically centered on the page.
     * 
     * @return A boolean.
     */
    public boolean isVerticallyCentered() {
        return this.verticallyCentered;
    }

    /**
     * Returns a flag indicating whether or not the content is horizontally centered on the page.
     * 
     * @return A boolean.
     */
    public boolean isHorizontallyCentered() {
        return this.horizontallyCentered;
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
     * Returns a flag indicating whether or not the output is monochrome.
     * 
     * @return A boolean.
     */
    public boolean isMonochrome() {
        return this.monochrome;
    }

    /**
     * Returns a flag indicating the print quality.
     * 
     * @return A boolean.
     */
    public boolean isDraft() {
        return this.draft;
    }

    /**
     * Returns a flag indicating whether or not the titles are visible.
     * 
     * @return A boolean.
     */
    public boolean isTitlesVisible() {
        return this.titles;
    }

    /**
     * Returns ???.
     * 
     * @return ???.
     */
    public String getRepeatTop() {
        return this.repeatTop;
    }

    /**
     * Returns ???
     * 
     * @return ???.
     */
    public String getRepeatLeft() {
        return this.repeatLeft;
    }

    /**
     * Returns the order of page printing.
     * 
     * @return The order of page printing.
     */
    public String getOrder() {
        return this.order;
    }

    /**
     * Returns the page orientation.
     * 
     * @return The page orientation.
     */
    public int getOrientation() {
        return this.orientation;
    }

    /**
     * Returns the left header string.
     * 
     * @return The left header string.
     */
    public String getHeaderLeft() {
        return this.headerLeft;
    }

    /**
     * Returns the middle header string.
     * 
     * @return The middle header string.
     */
    public String getHeaderMiddle() {
        return this.headerMiddle;
    }

    /**
     * Returns the right header string.
     * 
     * @return The right header string.
     */
    public String getHeaderRight() {
        return this.headerRight;
    }

    /**
     * Returns the left footer string.
     * 
     * @return The left footer string.
     */
    public String getFooterLeft() {
        return this.footerLeft;
    }

    /**
     * Returns the middle footer string.
     * 
     * @return The middle footer string.
     */
    public String getFooterMiddle() {
        return this.footerMiddle;
    }

    /**
     * Returns the right footer string.
     * 
     * @return The right footer string.
     */
    public String getFooterRight() {
        return this.footerRight;
    }

    /**
     * Returns the paper size.
     * 
     * @return The paper size.
     */
    public String getPaperSize() {
        return this.paper;
    }

}
