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
 * -----------------
 * NamesManager.java
 * -----------------
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
 * 12-Feb-2002 : Added an iterator that provides access to the names (DG);
 * 07-Jul-2003 : Changed GPL --> LGPL, changed package name, updated company name (DG);
 *
 */

package org.jfree.workbook;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Maintains a list of names.
 */
public class NamesManager {

    /** Storage for the names. */
    protected List<Name> names;

    /**
     * Default constructor.
     */
    public NamesManager() {
        this.names = new ArrayList<Name>();
    }

    /**
     * Adds a name to the list.  Need to add code to make sure the name is not a duplicate and
     * some checks on the validity of the name.
     * 
     * @param name  the name.
     * @param value  the value.
     */
    public void addName(String name, String value) {
        names.add(new Name(name, value));
    }

    /**
     * Returns the number of names in the list.
     * 
     * @return The number of names in the list.
     */
    public int getNameCount() {
        return names.size();
    }

    /**
     * Returns an iterator that provides access to the names.
     * 
     * @return The iterator.
     */
    public Iterator<Name> getNamesIterator() {
        return names.iterator();
    }

}
