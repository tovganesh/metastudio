/*
  VaryingScalarTuple3f.java
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

import javax.vecmath.Point3f;
import javax.vecmath.Tuple3f;

import org.jrman.grid.Grid;
import org.jrman.grid.Tuple3fGrid;
import org.jrman.util.Calc;

public class VaryingScalarTuple3f extends Parameter {

    private static Point3f tmp = new Point3f();

    final private float[] values;

    public VaryingScalarTuple3f(Declaration declaration, float[] values) {
        super(declaration);
        this.values = values;
    }

    public void getValue(int index, Tuple3f out) {
        out.x = values[index * 3];
        out.y = values[index * 3 + 1];
        out.z = values[index * 3 + 2];
    }

    public void setValue(int index, Tuple3f in) {
        values[index * 3] = in.x;
        values[index * 3 + 1] = in.y;
        values[index * 3 + 2] = in.z;
    }

    public float getValue(int index) {
        return values[index];
    }

    public int getCount() {
        return values.length / 3;
    }

    public Parameter linearInterpolate(float min, float max) {
        float[] newValues = new float[6];
        newValues[0] = Calc.interpolate(values[0], values[3], min);
        newValues[1] = Calc.interpolate(values[1], values[4], min);
        newValues[2] = Calc.interpolate(values[2], values[5], min);
        newValues[3] = Calc.interpolate(values[0], values[3], max);
        newValues[4] = Calc.interpolate(values[1], values[4], max);
        newValues[5] = Calc.interpolate(values[2], values[5], max);
        return new VaryingScalarTuple3f(declaration, newValues);
    }

    public Parameter bilinearInterpolate(float uMin, float uMax,
                                       float vMin, float vMax) {
        float[] newValues = new float[12];
        newValues[0] =
            Calc.interpolate(values[0], values[3], values[6], values[9],
                             uMin, vMin);
        newValues[1] =
            Calc.interpolate(values[1], values[4], values[7], values[10],
                             uMin, vMin);
        newValues[2] =
            Calc.interpolate(values[2], values[5], values[8], values[11],
                             uMin, vMin);
        newValues[3] =
            Calc.interpolate(values[0], values[3], values[6], values[9],
                             uMax, vMin);
        newValues[4] =
            Calc.interpolate(values[1], values[4], values[7], values[10],
                             uMax, vMin);
        newValues[5] =
            Calc.interpolate(values[2], values[5], values[8], values[11],
                             uMax, vMin);
        newValues[6] =
            Calc.interpolate(values[0], values[3], values[6], values[9],
                             uMin, vMax);
        newValues[7] =
            Calc.interpolate(values[1], values[4], values[7], values[10],
                             uMin, vMax);
        newValues[8] =
            Calc.interpolate(values[2], values[5], values[8], values[11],
                             uMin, vMax);
        newValues[9] =
            Calc.interpolate(values[0], values[3], values[6], values[9],
                             uMax, vMax);
        newValues[10] =
            Calc.interpolate(values[1], values[4], values[7], values[10],
                             uMax, vMax);
        newValues[11] =
            Calc.interpolate(values[2], values[5], values[8], values[11],
                             uMax, vMax);
        return new VaryingScalarTuple3f(declaration, newValues);
    }

    public void linearDice(Grid grid) {
        Tuple3fGrid g = (Tuple3fGrid) grid;
        int vSize = Grid.getVSize();
        float vStep = 1f / (vSize - 1);
        float v = 0f;
        for (int iv = 0; iv < vSize; iv++) {
            tmp.x = Calc.interpolate(values[0], values[3], v);
            tmp.y = Calc.interpolate(values[1], values[4], v);
            tmp.z = Calc.interpolate(values[2], values[5], v);
            g.set(0, iv, tmp);
            g.set(1, iv, tmp);
            v += vStep;
        }
    }

    public void bilinearDice(Grid grid) {
        Tuple3fGrid g = (Tuple3fGrid) grid;
        int uSize = Grid.getUSize();
        int vSize = Grid.getVSize();
        float uStep = 1f / (uSize - 1);
        float vStep = 1f / (vSize - 1);
        float u = 0f;
        for (int iu = 0; iu < uSize; iu++) {
            float v = 0f;
            for (int iv = 0; iv < vSize; iv++) {
                tmp.x = Calc.interpolate(values[0], values[3],
                                         values[6], values[9], u, v);
                tmp.y = Calc.interpolate(values[1], values[4],
                                         values[7], values[10], u, v);
                tmp.z = Calc.interpolate(values[2], values[5],
                                         values[8], values[11], u, v);
                g.set(iu, iv, tmp);
                v += vStep;
            }
            u += uStep;
        }
    }

    public Parameter selectValues(int[] indexes) {
        if (declaration.getStorageClass() == Declaration.StorageClass.UNIFORM
            && indexes.length == 1) {
            int offset = indexes[0] * 3;
            return new UniformScalarTuple3f(
                declaration,
                values[offset],
                values[offset + 1],
                values[offset + 2]);
        } else {
            float[] newValues = new float[indexes.length * 3];
            for (int i = 0; i < indexes.length; i++) {
                int srcOffset = indexes[i] * 3;
                int dstOffset = i * 3;
                newValues[dstOffset] = values[srcOffset];
                newValues[dstOffset + 1] = values[srcOffset + 1];
                newValues[dstOffset + 2] = values[srcOffset + 2];
            }
            return new VaryingScalarTuple3f(declaration, newValues);
        }
    }

    public VaryingScalarFloat extract(Declaration decl, int index) {
        float[] newValues = new float[values.length / 3];
        for (int i = 0; i < newValues.length; i++)
            newValues[i] = values[i * 3 + index];
       return new VaryingScalarFloat(decl, newValues);
    }

}
