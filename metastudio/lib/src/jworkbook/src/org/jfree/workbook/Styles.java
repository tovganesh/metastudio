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
 * -----------
 * Styles.java
 * -----------
 * (C) Copyright 2001, 2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id$
 *
 * Changes:
 * ---------------
 * 05-Nov-2001 : Version 1 (DG);
 * 12-Feb-2002 : Implemented XLWriter class (DG);
 * 07-Jul-2003 : Changed GPL --> LGPL, changed package name, updated company name (DG);
 *
 */

package org.jfree.workbook;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A collection of style regions.  This class performs some of the background work, restructuring
 * the regions as required for applying new styles.
 * <P>
 * At the moment we restructure so that the entire worksheet region is covered by non-overlapping
 * style regions.  Gnumeric appears to have a different strategy, still non-overlapping but it
 * chooses different ways to restructure.  We should try to match what Gnumeric does, although
 * it seems to handle any differences relatively smoothly in the limited testing done so far.
 *
 */
public class Styles {

    /** Storage for the style regions. */
    protected List<StyleRegion> regions;

    /**
     * Constructs a new default styles collection.
     */
    public Styles() {

        regions = new ArrayList<StyleRegion>();
        Style style = new Style();
        StyleRegion region = new StyleRegion(style, 0, 0, 65535, 255);
        regions.add(region);

    }

    /**
     * Returns the style that applies to the specified row and column.
     * 
     * @param row  the row.
     * @param column  the column.
     * 
     * @return The style.
     */
    public Style getStyle(int row, int column) {

        Style result = null;
        Iterator iterator = regions.iterator();
        while (iterator.hasNext()) {
            StyleRegion region = (StyleRegion) iterator.next();
            if (region.contains(row, column)) {
                result = region.getStyle();
                break;
            }
        }
        return result;

    }

    /**
     * Adds the specified style region, restructuring the existing style regions to avoid any
     * overlapping.
     * 
     * @param styleRegion  the new style region.
     */
    public void addStyleRegion(StyleRegion styleRegion) {

        List<StyleRegion> toRemove = new ArrayList<StyleRegion>();

        // get a list of all the existing style regions that intersect with this one;
        Iterator<StyleRegion> iterator = regions.iterator();
        while (iterator.hasNext()) {
            StyleRegion existing = (StyleRegion) iterator.next();
            if (existing.intersects(styleRegion)) {
                toRemove.add(existing);
            }
        }

        // do the non-intersection list thing
        List<StyleRegion> toAdd = new ArrayList<StyleRegion>();
        iterator = toRemove.iterator();
        while (iterator.hasNext()) {
            StyleRegion going = (StyleRegion) iterator.next();
            List<StyleRegion> coming = going.getNonIntersectionStyleRegionList(styleRegion);
            toAdd.addAll(coming);
        }

        regions.removeAll(toRemove);
        regions.addAll(toAdd);
        regions.add(styleRegion);

    }

    /**
     * Apply the style modifier to the styles that cover the specified region.  This will
     * usually involve some restructuring of the regions.
     * 
     * @param modifier  the modifier.
     * @param r1  the start row.
     * @param c1  the start column.
     * @param r2  the end row.
     * @param c2  the end column.
     */
    public void modifyStyle(StyleModifier modifier, int r1, int c1, int r2, int c2) {

        List<StyleRegion> modifiedRegions = new ArrayList<StyleRegion>();
        Iterator iterator = regions.iterator();
        while (iterator.hasNext()) {
            StyleRegion existing = (StyleRegion) iterator.next();
            StyleRegion intersection = existing.getIntersectionStyleRegion(r1, c1, r2, c2,
                                                                           modifier);
            if (intersection != null) {
                modifiedRegions.add(intersection);
            }
        }

        iterator = modifiedRegions.iterator();
        while (iterator.hasNext()) {
            StyleRegion modified = (StyleRegion) iterator.next();
            this.addStyleRegion(modified);
        }

    }

    /**
     * Returns an iterator that provides access to all the styles in the collection.
     * 
     * @return An iterator.
     */
    public Iterator getStylesIterator() {
        return regions.iterator();
    }


}
