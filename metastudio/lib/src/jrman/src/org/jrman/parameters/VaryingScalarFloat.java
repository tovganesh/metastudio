/*
  VaryingScalarFloat.java
  Copyright (C) 2003, 2004, 2006 Gerardo Horvilleur Martinez
  
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

package org.jrman.parameters;

import org.jrman.grid.FloatGrid;
import org.jrman.grid.Grid;
import org.jrman.util.Calc;

public class VaryingScalarFloat extends Parameter {

    private float[] values;

    public VaryingScalarFloat(Declaration declaration, float[] values) {
        super(declaration);
        this.values = values;
    }

    public float getValue(int index) {
        return values[index];
    }

    public int getCount() {
        return values.length;
    }

    public Parameter linearInterpolate(float min, float max) {
        float[] newValues = new float[2];
        newValues[0] = Calc.interpolate(values[0], values[1], min);
        newValues[1] = Calc.interpolate(values[0], values[1], max);
        return new VaryingScalarFloat(declaration, newValues);
    }

    public Parameter bilinearInterpolate(float uMin, float uMax,
                                         float vMin, float vMax) {
        float[] newValues = new float[4];
        newValues[0] =
            Calc.interpolate(values[0], values[1], values[2], values[3],
                             uMin, vMin);
        newValues[1] =
            Calc.interpolate(values[0], values[1], values[2], values[3],
                             uMax, vMin);
        newValues[2] =
            Calc.interpolate(values[0], values[1], values[2], values[3],
                             uMin, vMax);
        newValues[3] =
            Calc.interpolate(values[0], values[1], values[2], values[3],
                             uMax, vMax);
        return new VaryingScalarFloat(declaration, newValues);
    }

    public void linearDice(Grid grid) {
        FloatGrid g = (FloatGrid) grid;
        int vSize = Grid.getVSize();
        float vStep = 1f / (vSize - 1);
        float v = 0f;
        for (int iv = 0; iv < vSize; iv++) {
            float val = Calc.interpolate(values[0], values[1], v);
            g.set(0, iv, val);
            g.set(1, iv, val);
            v += vStep;
        }
    }

    public void bilinearDice(Grid grid) {
        FloatGrid g = (FloatGrid) grid;
        int uSize = Grid.getUSize();
        int vSize = Grid.getVSize();
        float uStep = 1f / (uSize - 1);
        float vStep = 1f / (vSize - 1);
        float u = 0f;
        for (int iu = 0; iu < uSize; iu++) {
            float v = 0f;
            for (int iv = 0; iv < vSize; iv++) {
                g.set(
                    iu,
                    iv,
                    Calc.interpolate(values[0], values[1], values[2], values[3],
                                     u, v));
                v += vStep;
            }
            u += uStep;
        }
    }

    public Parameter selectValues(int[] indexes) {
        if (declaration.getStorageClass() == Declaration.StorageClass.UNIFORM
            && indexes.length == 1) {
            return new UniformScalarFloat(declaration, values[indexes[0]]);
        } else {
            float[] newValues = new float[indexes.length];
            for (int i = 0; i < indexes.length; i++)
                newValues[i] = values[indexes[i]];
            return new VaryingScalarFloat(declaration, newValues);
        }
    }

    public void expandTo(int nu, int nv) {
        if (values.length == 4) {
            float[] newValues = new float[nu * nv];
            float uStep = 1f / (nu - 1);
            float vStep = 1f / (nv - 1);
            float fv = 0f;
            for (int v = 0; v < nv; v++) {
                float fu = 0f;
                for (int u = 0; u < nu; u++) {
                    newValues[v * nu + u] =
                        Calc.interpolate(values[0], values[1],
                                         values[2], values[3], fu, fv);
                    fu += uStep;
                }
                fv += vStep;
            }
            values = newValues;
        }
    }

}
