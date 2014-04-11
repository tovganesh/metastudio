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
 * FontStyle.java
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
 * 13-Feb-2002 : Renamed StyleFont.java --> FontStyle.java (DG);
 * 07-Jul-2003 : Changed GPL --> LGPL, changed package name, updated company name (DG);
 *
 */

package org.jfree.workbook;

/**
 * Represents a font within a style.
 */
public class FontStyle {

    /** The font name. */
    protected String name;

    /** The size. */
    protected int size;

    /** Bold. */
    protected boolean bold;

    /** Italic. */
    protected boolean italic;

    /** Underline. */
    protected boolean underline;

    /** Strikethrough. */
    protected boolean strikethrough;

    /**
     * Constructs a font (12pt) with the given name.
     * 
     * @param name  the font name (not validated).
     */
    public FontStyle(String name) {

        this(name, 12);

    }

    /**
     * Constructs a font with the given name and point size.
     * 
     * @param name  the font name (not validated).
     * @param size  the point size.
     *
     */
    public FontStyle(String name, int size) {

        this(name, size, false, false, false, false);

    }

    /**
     * Constructs a font with all attributes as specified.
     * 
     * @param name  the font name (not validated).
     * @param size  the point size.
     * @param bold  flag for bold attribute.
     * @param italic  flag for italic attribute.
     * @param underline  flag for underline attribute.
     * @param strikethrough  flag for strikethrough attribute.
     *
     */
    public FontStyle(String name, int size, boolean bold, boolean italic, boolean underline,
                     boolean strikethrough) {

        this.name = name;
        this.size = size;
        this.bold = bold;
        this.italic = italic;
        this.underline = underline;
        this.strikethrough = strikethrough;

    }

    /**
     * Returns the font name.
     * 
     * @return The font name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the font size.
     * 
     * @return The font size.
     */
    public int getSize() {
        return this.size;
    }

    /**
     * Returns a flag indicating whether or not the bold attribute is set.
     * 
     * @return The bold attribute.
     */
    public boolean isBold() {
        return this.bold;
    }

    /**
     * Returns a flag indicating whether or not the italic attribute is set.
     * 
     * @return The italic attribute.
     */
    public boolean isItalic() {
        return this.italic;
    }

    /**
     * Returns a flag indicating whether or not the underline attribute is set.
     * 
     * @return The underline attribute.
     */
    public boolean isUnderline() {
        return this.underline;
    }

    /**
     * Returns a flag indicating whether or not the strikethrough attribute is set.
     * 
     * @return The strikethrough attribute.
     */
    public boolean isStrikethrough() {
        return this.strikethrough;
    }

}
