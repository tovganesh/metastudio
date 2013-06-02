/*
  BilinearPatch.java
  Copyright (C) 2003, 2006 Gerardo Horvilleur Martinez
  
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
import javax.vecmath.Vector3f;

import org.jrman.attributes.Attributes;
import org.jrman.geom.BoundingVolume;
import org.jrman.geom.ConvexHull3f;
import org.jrman.parameters.ParameterList;
import org.jrman.parameters.VaryingScalarTuple3f;
import org.jrman.render.ShaderVariables;

public class BilinearPatch extends Primitive {
    
    private static Point3f P00 = new Point3f();

    private static Point3f P10 = new Point3f();

    private static Point3f P01 = new Point3f();

    private static Point3f P11 = new Point3f();

    private static Vector3f vtmp = new Vector3f();

    public BilinearPatch(ParameterList parameters, Attributes attributes) {
        super(parameters, attributes);
    }

    private void extractPoints() {
        VaryingScalarTuple3f param =
            (VaryingScalarTuple3f) parameters.getParameter("P");
        param.getValue(0, P00);
        param.getValue(1, P10);
        param.getValue(2, P01);
        param.getValue(3, P11);
    }

    public BoundingVolume getBoundingVolume() {
        ConvexHull3f mb = new ConvexHull3f();
        extractPoints();
        mb.addPoint(P00);
        mb.addPoint(P10);
        mb.addPoint(P01);
        mb.addPoint(P11);
        return mb;
    }

    public Primitive[] split() {
        Primitive[] result = new Primitive[2];
        extractPoints();
        vtmp.sub(P10, P00);
        float l1 = vtmp.length();
        vtmp.sub(P11, P01);
        float l2 = vtmp.length();
        float ul = (l1 + l2) / 2f;
        vtmp.sub(P01, P00);
        l1 = vtmp.length();
        vtmp.sub(P11, P10);
        l2 = vtmp.length();
        float vl = (l1 + l2) / 2f;
        if (ul > vl) {
            result[0] =
                new BilinearPatch(bilinearInterpolateParameters(0f, .5f, 0f, 1f), attributes);
            result[1] =
                new BilinearPatch(bilinearInterpolateParameters(.5f, 1f, 0f, 1f), attributes);
        } else {
            result[0] =
                new BilinearPatch(bilinearInterpolateParameters(0f, 1f, 0f, .5f), attributes);
            result[1] =
                new BilinearPatch(bilinearInterpolateParameters(0f, 1f, .5f, 1f), attributes);
        }
        return result;
    }

    protected void dice_P(ShaderVariables sv) {
        VaryingScalarTuple3f param =
            (VaryingScalarTuple3f) parameters.getParameter("P");
        param.bilinearDice(sv.P);
    }

    protected void dice_Ng(ShaderVariables sv) {
        sv.Ng.cross(sv.dPdu, sv.dPdv);
    }

    public float getMinGridSize() {
        return 4f;
    }

}
