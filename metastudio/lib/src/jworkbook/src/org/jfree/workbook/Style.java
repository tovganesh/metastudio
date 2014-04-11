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
 * ----------
 * Style.java
 * ----------
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
 * Represents a style that (when incorporated within a StyleRegion object) applies to a range of
 * cells in a worksheet.
 * <P>
 * This class should be immutable so we can use it in multiple places without risking it being
 * changed.
 */
public class Style implements StyleConstants {

    /** The font details. */
    protected FontStyle font;

    /** The border details. */
    protected Border border;

    /** The horizontal alignment. */
    protected int horizontalAlignment;

    /** The vertical alignment. */
    protected int verticalAlignment;

    /** Wrap text (true/false). */
    protected boolean wrapText;

    /** The orientation. */
    protected int orientation;

    /** The shade.  Not all that sure about this yet.  It might in fact be a boolean in the
     *  Gnumeric file format.  It needs to be "1" if the cell has a background color. */
    protected int shade;

    /** The indentation for the cells. */
    protected int cellIndent;

    /** The foreground color. */
    protected Color foregroundColor;

    /** The background color. */
    protected Color backgroundColor;

    /** The pattern color. */
    protected Color patternColor;

    /** The format. */
    protected String format;

    /**
     * Constructs a new Style object with default values.
     */
    public Style() {

        this(new FontStyle("Helvetica", 9, false, false, false, false), 1, 2);

    }

    /**
     * Constructs a style with a specific font and alignment.
     * 
     * @param font  the font.
     * @param horizontalAlignment  the horizontal alignment.
     * @param verticalAlignment  the vertical alignment.
     */
    public Style(FontStyle font, int horizontalAlignment, int verticalAlignment) {

        this.font = font;
        this.border = new Border(0, 0, 0, 0, 0, 0);
        this.horizontalAlignment = horizontalAlignment;
        this.verticalAlignment = verticalAlignment;
        this.wrapText = false;
        this.orientation = 1;
        this.shade = 0;
        this.cellIndent = 0;
        this.foregroundColor = Color.createInstance(Color.BLACK);
        this.backgroundColor = Color.createInstance(Color.WHITE);
        this.patternColor = Color.createInstance(Color.BLACK);
        this.format = "General";

    }

    /**
     * Full constructor.
     * 
     * @param font  the font.
     * @param border  the border.
     * @param horizontalAlignment  the horizontal alignment.
     * @param verticalAlignment  the vertical alignment.
     * @param wrapText  wrap text (true/false).
     * @param orientation  the text orientation.
     * @param shade  ??
     * @param cellIndent  cell indentation.
     * @param foregroundColor  the foreground color.
     * @param backgroundColor  the bacground color.
     * @param patternColor  the pattern color.
     * @param format  the format string for the cell.
     */
    public Style(FontStyle font, Border border,
                 int horizontalAlignment, int verticalAlignment,
                 boolean wrapText, int orientation, int shade, int cellIndent,
                 Color foregroundColor,  Color backgroundColor, Color patternColor,
                 String format) {

        this.font = font;
        this.border = border;
        this.horizontalAlignment = horizontalAlignment;
        this.verticalAlignment = verticalAlignment;
        this.wrapText = wrapText;
        this.orientation = orientation;
        this.shade = shade;
        this.cellIndent = cellIndent;
        this.foregroundColor = foregroundColor;
        this.backgroundColor = backgroundColor;
        this.patternColor = patternColor;
        this.format = format;

    }

    /**
     * Returns the font for this style.
     * 
     * @return The font for this style.
     */
    public FontStyle getFont() {
        return this.font;
    }

    /**
     * Returns the border for this style.
     * 
     * @return The border for this style.
     */
    public Border getBorder() {
        return this.border;
    }

    /**
     * Returns the horizontal alignment for this style.
     * 
     * @return The horizontal alignment for this style.
     */
    public int getHorizontalAlignment() {
        return this.horizontalAlignment;
    }

    /**
     * Returns the vertical alignment for this style.
     * 
     * @return The vertical alignment for this style.
     */
    public int getVerticalAlignment() {
        return this.verticalAlignment;
    }

    /**
     * Returns a flag indicating whether or not the text is wrapped.
     * 
     * @return A flag indicating whether or not the text is wrapped.
     */
    public boolean isWrapText() {
        return this.wrapText;
    }

    /**
     * Returns the text orientation.
     * 
     * @return The text orientation.
     */
    public int getOrientation() {
        return this.orientation;
    }

    /**
     * Returns the shade.
     * 
     * @return The shade.
     */
    public int getShade() {
        return this.shade;
    }

    /**
     * Returns the indentation.
     * 
     * @return The indentation.
     */
    public int getCellIndent() {
        return this.cellIndent;
    }

    /**
     * Returns the foreground color.
     * 
     * @return The foreground color.
     */
    public Color getForegroundColor() {
        return this.foregroundColor;
    }

    /**
     * Returns the background color.
     * 
     * @return The background color.
     */
    public Color getBackgroundColor() {
        return this.backgroundColor;
    }

    /**
     * Returns the pattern color.
     * 
     * @return The pattern color.
     */
    public Color getPatternColor() {
        return this.patternColor;
    }

    /**
     * Returns the format string for the cell.
     * 
     * @return The format string for the cell.
     */
    public String getFormat() {
        return this.format;
    }

    /**
     * Creates a new style based on the provided style, but replacing the font as specified.
     * 
     * @param style  the base style.
     * @param font  the new font.
     * 
     * @return The style.
     */
    public static Style applyFont(Style style, FontStyle font) {

        return new Style(font, style.getBorder(),
                         style.getHorizontalAlignment(), style.getVerticalAlignment(),
                         style.isWrapText(), style.getOrientation(), style.getShade(),
                         style.getCellIndent(), style.getForegroundColor(),
                         style.getBackgroundColor(), style.getPatternColor(), style.getFormat());

    }

    /**
     * Creates a new style based on the provided style, but replacing the border as specified.
     * 
     * @param base  the base style.
     * @param border  the new border.
     * 
     * @return The style.
     */
    public static Style applyBorder(Style base, Border border) {

        return new Style(base.getFont(), border,
                         base.getHorizontalAlignment(), base.getVerticalAlignment(),
                         base.isWrapText(), base.getOrientation(), base.getShade(),
                         base.getCellIndent(), base.getForegroundColor(),
                         base.getBackgroundColor(), base.getPatternColor(), base.getFormat());

    }

    /**
     * Creates a new style based on the provided style, but replacing the foreground color as
     * specified.
     * 
     * @param style  the base style.
     * @param foregroundColor  the new foreground color.
     * 
     * @return The style.
     */
    public static Style applyForegroundColor(Style style, Color foregroundColor) {

        return new Style(style.getFont(), style.getBorder(),
                         style.getHorizontalAlignment(), style.getVerticalAlignment(),
                         style.isWrapText(), style.getOrientation(), style.getShade(),
                         style.getCellIndent(), foregroundColor,
                         style.getBackgroundColor(), style.getPatternColor(), style.getFormat());

    }

    /**
     * Creates a new style based on the provided style, but replacing the background color as
     * specified.
     * <P>
     * We set the shade value to 1, since it looks as though this is required by the Gnumeric file
     * format...will try to find out more about this.
     * 
     * @param style  the base style.
     * @param backgroundColor  the new background color.
     * 
     * @return The style.
     */
    public static Style applyBackgroundColor(Style style, Color backgroundColor) {

        return new Style(style.getFont(), style.getBorder(),
                         style.getHorizontalAlignment(), style.getVerticalAlignment(),
                         style.isWrapText(), style.getOrientation(), 1,
                         style.getCellIndent(), style.getForegroundColor(),
                         backgroundColor, style.getPatternColor(), style.getFormat());

    }

    /**
     * Creates a new style based on the provided style, but replacing the pattern color as
     * specified.
     * 
     * @param style  t base style.
     * @param patternColor  the pattern color.
     * 
     * @return The style.
     */
    public static Style applyPatternColor(Style style, Color patternColor) {

        return new Style(style.getFont(), style.getBorder(),
                         style.getHorizontalAlignment(), style.getVerticalAlignment(),
                         style.isWrapText(), style.getOrientation(), style.getShade(),
                         style.getCellIndent(), style.getForegroundColor(),
                         style.getBackgroundColor(), patternColor, style.getFormat());

    }

}
