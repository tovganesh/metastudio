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
 * Comment.java
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
 * 13-Feb-2002 : Added accessor methods and moved Gnumeric specific method to
 *               GnumericWriter.java (DG);
 * 07-Jul-2003 : Changed GPL --> LGPL, changed package name, updated company name (DG);
 *
 */

package org.jfree.workbook;

/**
 * Represents a comment that can be attached to a cell.
 */
public class Comment implements Comparable<Comment> {

    /** The cell row. */
    protected int row;

    /** The cell column. */
    protected int column;

    /** The comment. */
    protected String text;

    /** The author. */
    protected String author;

    /**
     * Standard constructor.
     * 
     * @param column  the column.
     * @param row  the row.
     * @param text  the comment.
     */
    public Comment(int row, int column, String text) {

        this.row = row;
        this.column = column;
        this.text = text;
        this.author = "";

    }

    /**
     * Returns the row of the cell that the comment applies to.
     * 
     * @return The row.
     */
    public int getRow() {
        return this.row;
    }

    /**
     * Returns the column of the cell that the comment applies to.
     * 
     * @return The column.
     */
    public int getColumn() {
        return this.column;
    }

    /**
     * Returns the comment text.
     * 
     * @return The text.
     */
    public String getText() {
        return this.text;
    }

    /**
     * Returns the name of the comment author.
     * 
     * @return The author.
     */
    public String getAuthor() {
        return this.author;
    }

    /**
     * Implements the Comparable interface.
     * 
     * @param other  the object to compare against.
     * 
     * @return An integer that indicates the relative order of the objects.
     */
    public int compareTo(Comment otherComment) {       
        return (this.row * 256 + this.column) - (otherComment.row * 256 + otherComment.column);
    }

}
