/*
  PointsPolygon.java
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

import javax.vecmath.Point3f;

import org.jrman.attributes.Attributes;
import org.jrman.geom.BoundingVolume;
import org.jrman.geom.ConvexHull3f;
import org.jrman.geom.MutableBounds3f;
import org.jrman.parameters.ParameterList;
import org.jrman.parameters.VaryingScalarTuple3f;

public class PointsPolygons extends Primitive {
    
    static Point3f tmpPoint = new Point3f();

    int[] nVertices;

    int[] vertices;

    VaryingScalarTuple3f points;

    public PointsPolygons(
        int[] nVertices,
        int[] vertices,
        ParameterList parameters,
        Attributes attributes) {
        this.parameters = parameters;
        this.attributes = attributes;
        this.nVertices = nVertices;
        this.vertices = vertices;
        points = (VaryingScalarTuple3f) parameters.getParameter("P");
        parameters.removeParameter("P");
    }

    public BoundingVolume getBoundingVolume() {
        MutableBounds3f mb = new MutableBounds3f();
        for (int i = 0, n = points.getCount(); i < n; i++) {
            points.getValue(i, tmpPoint);
            mb.addPoint(tmpPoint);
        }
        return mb;
    }

    public Primitive[] split() {
        Primitive[] result = new Primitive[nVertices.length];
        int offset = 0;
        for (int i = 0; i < nVertices.length; i++) {
            result[i] = new Polygon(i, nVertices[i], offset, parameters, attributes);
            offset += nVertices[i];
        }
        return result;
    }

    public boolean isReadyToBeDiced(int gridsize) {
        return false;
    }

    private class Polygon extends Primitive {
        
        private int pos;

        private int n;

        private int offset;

        Polygon(int pos, int n, int offset, ParameterList parameters, Attributes attributes) {
            this.pos = pos;
            this.n = n;
            this.offset = offset;
            this.parameters = parameters;
            this.attributes = attributes;
        }

        public BoundingVolume getBoundingVolume() {
            ConvexHull3f ch = new ConvexHull3f();
            for (int i = 0; i < n; i++) {
                points.getValue(vertices[offset + i], tmpPoint);
                ch.addPoint(tmpPoint);
            }
            return ch;
        }

        public Primitive[] split() {
            Primitive[] result = new Primitive[n - 3 + 1];
            int[] posIndex = new int[1];
            posIndex[0] = pos;
            int[] indexes = new int[] { vertices[offset], 0, vertices[offset], 0 };
            for (int i = 1; i < n - 1; i++) {
                indexes[1] = vertices[offset + i];
                indexes[3] = vertices[offset + i + 1];
                ParameterList pl = parameters.selectValues(posIndex, indexes, indexes);
                pl.addParameter(points.selectValues(indexes));
                result[i - 1] = new BilinearPatch(pl, attributes);
            }
            return result;
        }

        public boolean isReadyToBeDiced(int gridsize) {
            return false;
        }

        public boolean shouldSortBucket() {
            return false;
        }

    }

}
