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
 * ------------
 * Margins.java
 * ------------
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
 * Printing margins for a worksheet.
 */
public class Margins {

    /** Top margin. */
    protected double topPts;

    /** The top units. */
    protected int topUnits = 0;  // cm

    /** Bottom margin. */
    protected double bottomPts;

    /** The bottom units. */
    protected int bottomUnits = 0;

    /** Left margin. */
    protected double leftPts;

    /** The left units. */
    protected int leftUnits = 0;

    /** Right margin. */
    protected double rightPts;

    /** The right units. */
    protected int rightUnits = 0;

    /** Header margin. */
    protected double headerPts;

    /** The header units. */
    protected int headerUnits = 0;

    /** Footer margin. */
    protected double footerPts;

    /** The footer units. */
    protected int footerUnits = 0;

    /**
     * Default constructor.
     */
    public Margins() {

        this.topPts = 28.3;
        this.bottomPts = 28.3;
        this.leftPts = 28.3;
        this.rightPts = 28.3;
        this.headerPts = 14.2;
        this.footerPts = 14.2;

    }

    /**
     * Returns the top margin.
     * 
     * @return The margin.
     */
    public double getTopPts() {
        return this.topPts;
    }

    /**
     * Returns the bottom margin.
     * 
     * @return The margin.
     */
    public double getBottomPts() {
        return this.bottomPts;
    }

    /**
     * Returns the left margin.
     * 
     * @return The margin.
     */
    public double getLeftPts() {
        return this.leftPts;
    }

    /**
     * Returns the right margin.
     * 
     * @return The margin.
     */
    public double getRightPts() {
        return this.rightPts;
    }

    /**
     * Returns the header margin.
     * 
     * @return The margin.
     */
    public double getHeaderPts() {
        return this.headerPts;
    }

    /**
     * Returns the footer margin.
     * 
     * @return The margin.
     */
    public double getFooterPts() {
        return this.footerPts;
    }

}
