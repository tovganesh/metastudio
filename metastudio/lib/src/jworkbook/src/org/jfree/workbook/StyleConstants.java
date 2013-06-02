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
 * -------------------
 * StyleConstants.java
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
 * 07-Jul-2003 : Changed GPL --> LGPL, changed package name, updated company name (DG);
 *
 */

package org.jfree.workbook;

/**
 * Useful style constants.
 */
public interface StyleConstants {

    /** Constant for "general" horizontal alignment. */
    public static final int ALIGN_GENERAL = 1;

    /** Constant for left horizontal alignment. */
    public static final int ALIGN_LEFT = 2;

    /** Constant for right horizontal alignment. */
    public static final int ALIGN_RIGHT = 4;

    /** Constant for centered horizontal alignment. */
    public static final int ALIGN_CENTER = 8;

    /** Constant for "fill" horizontal alignment. */
    public static final int ALIGN_FILL = 16;

    /** Constant for "justify" horizontal alignment. */
    public static final int ALIGN_JUSTIFY_HORIZONTAL = 32;

    /** Constant for centered-across-selection horizontal alignment. */
    public static final int ALIGN_CENTER_ACROSS_SELECTION = 64;

    /** Constant for top vertical alignment. */
    public static final int ALIGN_TOP = 1;

    /** Constant for bottom vertical alignment. */
    public static final int ALIGN_BOTTOM = 2;

    /** Constant for centered vertical alignment. */
    public static final int ALIGN_MIDDLE = 4;

    /** Constant for "justified" vertical alignment. */
    public static final int ALIGN_JUSTIFY_VERTICAL = 8;

}
