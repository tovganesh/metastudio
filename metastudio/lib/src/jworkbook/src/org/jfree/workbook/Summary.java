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
 * Summary.java
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
 * 5-Nov-2001 : Version 1 (DG);
 * 07-Jul-2003 : Changed GPL --> LGPL, changed package name, updated company name (DG);
 *
 */

package org.jfree.workbook;

/**
 * Records a summary of a workbook.
 */
public class Summary {

    /** The title. */
    protected String title;

    /** Related keywords. */
    protected String keywords;

    /** Comments. */
    protected String comments;

    /** Category. */
    protected String category;

    /** Manager. */
    protected String manager;

    /** The application that generated the workbook. */
    protected String application;

    /** The author or creator of the workbook. */
    protected String author;

    /** The company. */
    protected String company;

    /**
     * Constructs a new default summary.
     */
    public Summary() {
        this.application = "JWorkbook " + JWorkbook.VERSION;
    }

    /**
     * Returns the workbook title.
     * 
     * @return The workbook title.
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Sets the workbook title.
     * 
     * @param title  the new title.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the keywords for the workbook.
     * 
     * @return The keywords for the workbook.
     */
    public String getKeywords() {
        return this.keywords;
    }

    /**
     * Sets the keywords for the workbook.
     * 
     * @param keywords  the new keywords.
     */
    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    /**
     * Returns the comments for the workbook.
     * 
     * @return The comments for the workbook.
     */
    public String getComments() {
        return this.comments;
    }

    /**
     * Sets the comments for the workbook.
     * 
     * @param comments  the new comments.
     */
    public void setComments(String comments) {
        this.comments = comments;
    }

    /**
     * Returns the category for the workbook.
     * 
     * @return The category for the workbook.
     */
    public String getCategory() {
        return this.category;
    }

    /**
     * Sets the category for the workbook.
     * 
     * @param category  the new category.
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Returns the manager.
     * 
     * @return The manager.
     */
    public String getManager() {
        return this.manager;
    }

    /**
     * Sets the manager.
     * 
     * @param manager  the new manager.
     */
    public void setManager(String manager) {
        this.manager = manager;
    }

    /**
     * Returns the application.
     * 
     * @return The application.
     */
    public String getApplication() {
        return this.application;
    }

    /**
     * Sets the application.
     * 
     * @param application  the new application.
     */
    public void setApplication(String application) {
        this.application = application;
    }

    /**
     * Returns the author.
     * 
     * @return The author.
     */
    public String getAuthor() {
        return this.author;
    }

    /**
     * Sets the author.
     * 
     * @param author  the new author.
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Returns the company.
     * 
     * @return The company.
     */
    public String getCompany() {
        return this.company;
    }

    /**
     * Sets the company.
     * 
     * @param company  the company.
     */
    public void setCompany(String company) {
        this.company = company;
    }

}
