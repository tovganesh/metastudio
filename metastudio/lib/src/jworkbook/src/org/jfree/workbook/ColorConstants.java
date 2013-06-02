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
 * ColorConstants.java
 * -------------------
 * (C) Copyright 2002, 2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id$
 *
 * Changes:
 * --------
 * 13-Feb-2002 : Version 1 (DG);
 * 07-Jul-2003 : Changed GPL --> LGPL, changed package name, updated company name (DG);
 *
 */

package org.jfree.workbook;

/**
 * Useful constants for defining colors.
 */
public interface ColorConstants {

    /** Constant representing the foreground color. */
    public static final int FOREGROUND_COLOR = 0;

    /** Constant representing the background color. */
    public static final int BACKGROUND_COLOR = 1;

    /** Constant representing the pattern color. */
    public static final int PATTERN_COLOR = 2;

    /** Useful constant representing the standard color BLACK. */
    public static final int BLACK = 0;

    /** Useful constant representing the standard Gnumeric color BLACK. */
    public static final int GNUMERIC_BLACK = BLACK;

    /** Useful constant representing the standard Excel color BLACK. */
    public static final int EXCEL_BLACK = BLACK;

    /** Useful constant representing the standard color BLACK. */
    public static final int WHITE = BLACK + 1;

    /** Useful constant representing the standard Gnumeric color WHITE. */
    public static final int GNUMERIC_WHITE = WHITE;

    /** Useful constant representing the standard Excel color WHITE. */
    public static final int EXCEL_WHITE = WHITE;

    /** Useful constant representing the standard Gnumeric color LIGHT_BROWN. */
    public static final int GNUMERIC_LIGHT_BROWN = WHITE + 1;

    /** Useful constant representing the standard Gnumeric color BROWN_GOLD. */
    public static final int GNUMERIC_BROWN_GOLD = GNUMERIC_LIGHT_BROWN + 1;

    /** Useful constant representing the standard Gnumeric color DARK_GREEN_2. */
    public static final int GNUMERIC_DARK_GREEN_2 = GNUMERIC_BROWN_GOLD + 1;

    /** Useful constant representing the standard Gnumeric color NAVY. */
    public static final int GNUMERIC_NAVY = GNUMERIC_DARK_GREEN_2 + 1;

    /** Useful constant representing the standard Gnumeric color DARK_BLUE. */
    public static final int GNUMERIC_DARK_BLUE = GNUMERIC_NAVY + 1;

    /** Useful constant representing the standard Gnumeric color PURPLE_2. */
    public static final int GNUMERIC_PURPLE_2 = GNUMERIC_DARK_BLUE + 1;

    /** Useful constant representing the standard Gnumeric color VERY_DARK_GRAY. */
    public static final int GNUMERIC_VERY_DARK_GRAY = GNUMERIC_PURPLE_2 + 1;

    /** Useful constant representing the standard Gnumeric color DARK_RED. */
    public static final int GNUMERIC_DARK_RED = GNUMERIC_VERY_DARK_GRAY + 1;

    /** Useful constant representing the standard Gnumeric color RED_ORANGE. */
    public static final int GNUMERIC_RED_ORANGE = GNUMERIC_DARK_RED + 1;

    /** Useful constant representing the standard Gnumeric color GOLD. */
    public static final int GNUMERIC_GOLD = GNUMERIC_RED_ORANGE + 1;

    /** Useful constant representing the standard Gnumeric color DARK_GREEN. */
    public static final int GNUMERIC_DARK_GREEN = GNUMERIC_GOLD + 1;

    /** Useful constant representing the standard Gnumeric color DULL_BLUE. */
    public static final int GNUMERIC_DULL_BLUE = GNUMERIC_DARK_GREEN + 1;

    /** Useful constant representing the standard Gnumeric color BLUE. */
    public static final int GNUMERIC_BLUE = GNUMERIC_DULL_BLUE + 1;

    /** Useful constant representing the standard Gnumeric color DULL_PURPLE. */
    public static final int GNUMERIC_DULL_PURPLE = GNUMERIC_BLUE + 1;

    /** Useful constant representing the standard Gnumeric color DARK_GRAY. */
    public static final int GNUMERIC_DARK_GRAY = GNUMERIC_DULL_PURPLE + 1;

    /** Useful constant representing the standard Gnumeric color RED. */
    public static final int GNUMERIC_RED = GNUMERIC_DARK_GRAY + 1;

    /** Useful constant representing the standard Gnumeric color ORANGE. */
    public static final int GNUMERIC_ORANGE = GNUMERIC_RED + 1;

    /** Useful constant representing the standard Gnumeric color LIME. */
    public static final int GNUMERIC_LIME = GNUMERIC_ORANGE + 1;

    /** Useful constant representing the standard Gnumeric color DULL_GREEN. */
    public static final int GNUMERIC_DULL_GREEN = GNUMERIC_LIME + 1;

    /** Useful constant representing the standard Gnumeric color DULL_BLUE_2. */
    public static final int GNUMERIC_DULL_BLUE_2 = GNUMERIC_DULL_GREEN + 1;

    /** Useful constant representing the standard Gnumeric color SKY_BLUE_2. */
    public static final int GNUMERIC_SKY_BLUE_2 = GNUMERIC_DULL_BLUE_2 + 1;

    /** Useful constant representing the standard Gnumeric color PURPLE. */
    public static final int GNUMERIC_PURPLE = GNUMERIC_SKY_BLUE_2 + 1;

    /** Useful constant representing the standard Gnumeric color GRAY. */
    public static final int GNUMERIC_GRAY = GNUMERIC_PURPLE + 1;

    /** Useful constant representing the standard Gnumeric color MAGENTA. */
    public static final int GNUMERIC_MAGENTA = GNUMERIC_GRAY + 1;

    /** Useful constant representing the standard Gnumeric color BRIGHT_ORANGE. */
    public static final int GNUMERIC_BRIGHT_ORANGE = GNUMERIC_MAGENTA + 1;

    /** Useful constant representing the standard Gnumeric color YELLOW. */
    public static final int GNUMERIC_YELLOW = GNUMERIC_BRIGHT_ORANGE + 1;

    /** Useful constant representing the standard Gnumeric color GREEN. */
    public static final int GNUMERIC_GREEN = GNUMERIC_YELLOW + 1;

    /** Useful constant representing the standard Gnumeric color CYAN. */
    public static final int GNUMERIC_CYAN = GNUMERIC_GREEN + 1;

    /** Useful constant representing the standard Gnumeric color BRIGHT_BLUE. */
    public static final int GNUMERIC_BRIGHT_BLUE = GNUMERIC_CYAN + 1;

    /** Useful constant representing the standard Gnumeric color RED_PURPLE. */
    public static final int GNUMERIC_RED_PURPLE = GNUMERIC_BRIGHT_BLUE + 1;

    /** Useful constant representing the standard Gnumeric color LIGHT_GRAY. */
    public static final int GNUMERIC_LIGHT_GRAY = GNUMERIC_RED_PURPLE + 1;

    /** Useful constant representing the standard Gnumeric color PINK. */
    public static final int GNUMERIC_PINK = GNUMERIC_LIGHT_GRAY + 1;

    /** Useful constant representing the standard Gnumeric color LIGHT_ORANGE. */
    public static final int GNUMERIC_LIGHT_ORANGE = GNUMERIC_PINK + 1;

    /** Useful constant representing the standard Gnumeric color LIGHT_YELLOW. */
    public static final int GNUMERIC_LIGHT_YELLOW = GNUMERIC_LIGHT_ORANGE + 1;

    /** Useful constant representing the standard Gnumeric color LIGHT_GREEN. */
    public static final int GNUMERIC_LIGHT_GREEN = GNUMERIC_LIGHT_YELLOW + 1;

    /** Useful constant representing the standard Gnumeric color LIGHT_CYAN. */
    public static final int GNUMERIC_LIGHT_CYAN = GNUMERIC_LIGHT_GREEN + 1;

    /** Useful constant representing the standard Gnumeric color LIGHT_BLUE. */
    public static final int GNUMERIC_LIGHT_BLUE = GNUMERIC_LIGHT_CYAN + 1;

    /** Useful constant representing the standard Gnumeric color LIGHT_PURPLE. */
    public static final int GNUMERIC_LIGHT_PURPLE = GNUMERIC_LIGHT_BLUE + 1;

    /** Useful constant representing the standard Excel color BROWN. */
    public static final int EXCEL_BROWN = GNUMERIC_LIGHT_PURPLE + 1;

    /** Useful constant representing the standard Excel color OLIVE_GREEN. */
    public static final int EXCEL_OLIVE_GREEN = EXCEL_BROWN + 1;

    /** Useful constant representing the standard Excel color DARK_GREEN. */
    public static final int EXCEL_DARK_GREEN = EXCEL_OLIVE_GREEN + 1;

    /** Useful constant representing the standard Excel color DARK_TEAL. */
    public static final int EXCEL_DARK_TEAL = EXCEL_DARK_GREEN + 1;

    /** Useful constant representing the standard Excel color DARK_BLUE. */
    public static final int EXCEL_DARK_BLUE = EXCEL_DARK_TEAL + 1;

    /** Useful constant representing the standard Excel color INDIGO. */
    public static final int EXCEL_INDIGO = EXCEL_DARK_BLUE + 1;

    /** Useful constant representing the standard Excel color GRAY_80PERCENT. */
    public static final int EXCEL_GRAY_80PERCENT = EXCEL_INDIGO + 1;

    /** Useful constant representing the standard Excel color DARK_RED. */
    public static final int EXCEL_DARK_RED = EXCEL_GRAY_80PERCENT + 1;

    /** Useful constant representing the standard Excel color ORANGE. */
    public static final int EXCEL_ORANGE = EXCEL_DARK_RED + 1;

    /** Useful constant representing the standard Excel color DARK_YELLOW. */
    public static final int EXCEL_DARK_YELLOW = EXCEL_ORANGE + 1;

    /** Useful constant representing the standard Excel color GREEN. */
    public static final int EXCEL_GREEN = EXCEL_DARK_YELLOW + 1;

    /** Useful constant representing the standard Excel color TEAL. */
    public static final int EXCEL_TEAL = EXCEL_GREEN + 1;

    /** Useful constant representing the standard Excel color BLUE. */
    public static final int EXCEL_BLUE = EXCEL_TEAL + 1;

    /** Useful constant representing the standard Excel color BLUE_GRAY. */
    public static final int EXCEL_BLUE_GRAY = EXCEL_BLUE + 1;

    /** Useful constant representing the standard Excel color GRAY_50PERCENT. */
    public static final int EXCEL_GRAY_50PERCENT = EXCEL_BLUE_GRAY + 1;

    /** Useful constant representing the standard Excel color RED. */
    public static final int EXCEL_RED = EXCEL_GRAY_50PERCENT + 1;

    /** Useful constant representing the standard Excel color LIGHT_ORANGE. */
    public static final int EXCEL_LIGHT_ORANGE = EXCEL_RED + 1;

    /** Useful constant representing the standard Excel color LIME. */
    public static final int EXCEL_LIME = EXCEL_LIGHT_ORANGE + 1;

    /** Useful constant representing the standard Excel color SEA_GREEN. */
    public static final int EXCEL_SEA_GREEN = EXCEL_LIME + 1;

    /** Useful constant representing the standard Excel color AQUA. */
    public static final int EXCEL_AQUA = EXCEL_SEA_GREEN + 1;

    /** Useful constant representing the standard Excel color LIGHT_BLUE. */
    public static final int EXCEL_LIGHT_BLUE = EXCEL_AQUA + 1;

    /** Useful constant representing the standard Excel color VIOLET. */
    public static final int EXCEL_VIOLET = EXCEL_LIGHT_BLUE + 1;

    /** Useful constant representing the standard Excel color GRAY_40PERCENT. */
    public static final int EXCEL_GRAY_40PERCENT = EXCEL_VIOLET + 1;

    /** Useful constant representing the standard Excel color PINK. */
    public static final int EXCEL_PINK = EXCEL_GRAY_40PERCENT + 1;

    /** Useful constant representing the standard Excel color GOLD. */
    public static final int EXCEL_GOLD = EXCEL_PINK + 1;

    /** Useful constant representing the standard Excel color YELLOW. */
    public static final int EXCEL_YELLOW = EXCEL_GOLD + 1;

    /** Useful constant representing the standard Excel color BRIGHT_GREEN. */
    public static final int EXCEL_BRIGHT_GREEN = EXCEL_YELLOW + 1;

    /** Useful constant representing the standard Excel color TURQUOISE. */
    public static final int EXCEL_TURQUOISE = EXCEL_BRIGHT_GREEN + 1;

    /** Useful constant representing the standard Excel color SKY_BLUE. */
    public static final int EXCEL_SKY_BLUE = EXCEL_TURQUOISE + 1;

    /** Useful constant representing the standard Excel color PLUM. */
    public static final int EXCEL_PLUM = EXCEL_SKY_BLUE + 1;

    /** Useful constant representing the standard Excel color GRAY_25PERCENT. */
    public static final int EXCEL_GRAY_25PERCENT = EXCEL_PLUM + 1;

    /** Useful constant representing the standard Excel color ROSE. */
    public static final int EXCEL_ROSE = EXCEL_GRAY_25PERCENT + 1;

    /** Useful constant representing the standard Excel color TAN. */
    public static final int EXCEL_TAN = EXCEL_ROSE + 1;

    /** Useful constant representing the standard Excel color LIGHT_YELLOW. */
    public static final int EXCEL_LIGHT_YELLOW = EXCEL_TAN + 1;

    /** Useful constant representing the standard Excel color LIGHT_GREEN. */
    public static final int EXCEL_LIGHT_GREEN = EXCEL_LIGHT_YELLOW + 1;

    /** Useful constant representing the standard Excel color LIGHT_TURQUOISE. */
    public static final int EXCEL_LIGHT_TURQUOISE = EXCEL_LIGHT_GREEN + 1;

    /** Useful constant representing the standard Excel color PALE_BLUE. */
    public static final int EXCEL_PALE_BLUE = EXCEL_LIGHT_TURQUOISE + 1;

    /** Useful constant representing the standard Excel color LAVENDER. */
    public static final int EXCEL_LAVENDER = EXCEL_PALE_BLUE + 1;

}
