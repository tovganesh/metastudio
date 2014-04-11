/*
  DelayedReadArchive.java
  Copyright (C) 2004 Gerardo Horvilleur Martinez
  
  This program is free software; you can redistribute it and/or
  modify it under the terms of the GNU General Public License
  as published by the Free Software Foundation; either version 2
  of the License, or (at your option) any later version.
  
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/

package org.jrman.primitive;

import org.jrman.attributes.Attributes;
import org.jrman.geom.BoundingVolume;
import org.jrman.geom.Bounds3f;
import org.jrman.parser.Parser;

public class DelayedReadArchive extends Primitive {
    
    private final static Primitive[] emptyPrimitivesArray = new Primitive[0];
    
    private Parser parser;
    
    private String filename;
    
    private Bounds3f boundingBox;
    
    public DelayedReadArchive(Parser parser, String filename, float xMin, float xMax,
            float yMin, float yMax, float zMin, float zMax, Attributes attributes) {
        super(null, attributes);
        this.parser = parser;
        this.filename = filename;
        boundingBox = new Bounds3f(xMin, xMax, yMin, yMax, zMin, zMax);
    }

    public BoundingVolume getBoundingVolume() {
        return boundingBox;
    }

    public Primitive[] split() {
        parser.pushAttributes(attributes);
        try {
            parser.parse(filename);
        } catch (Exception e) {
            System.out.println("Couldn't parse " + filename);
            e.printStackTrace();
        }
        parser.popAttributes();
        return emptyPrimitivesArray;
    }
    
    public boolean isReadyToBeDiced(int gridSize) {
        return false;
    }

}
