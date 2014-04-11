/*
  VaryingScalarHPoint.java
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

import javax.vecmath.Tuple4f;

import org.jrman.grid.Grid;
import org.jrman.util.Calc;

public class VaryingScalarHPoint extends Parameter {

    final private float[] values;

    public VaryingScalarHPoint(Declaration declaration, float[] values) {
        super(declaration);
        this.values = values;
    }

    public VaryingScalarHPoint(Declaration declaration,
                               VaryingScalarTuple3f param) {
        super(declaration);
        values = new float[param.getCount() * 4];
        for (int i = 0; i < param.getCount(); i++) {
            values[i * 4] = param.getValue(i * 3);
            values[i * 4 + 1] = param.getValue(i * 3 + 1);
            values[i * 4 + 2] = param.getValue(i * 3 + 2);
            values[i * 4 + 3] = 1;
        }
    }

    public void getValue(int index, Tuple4f out) {
        out.x = values[index * 4];
        out.y = values[index * 4 + 1];
        out.z = values[index * 4 + 2];
        out.w = values[index * 4 + 3];
    }

    public void setValue(int index, Tuple4f in) {
        values[index * 4] = in.x;
        values[index * 4 + 1] = in.y;
        values[index * 4 + 2] = in.z;
        values[index * 4 + 3] = in.w;
    }

    public int getCount() {
        return values.length / 4;
    }

    public Parameter linearInterpolate(float min, float max) {
        float[] newValues = new float[8];
        newValues[0] = Calc.interpolate(values[0], values[4], min);
        newValues[1] = Calc.interpolate(values[1], values[5], min);
        newValues[2] = Calc.interpolate(values[2], values[6], min);
        newValues[3] = Calc.interpolate(values[3], values[7], min);
        newValues[4] = Calc.interpolate(values[0], values[4], max);
        newValues[5] = Calc.interpolate(values[1], values[5], max);
        newValues[6] = Calc.interpolate(values[2], values[6], max);
        newValues[7] = Calc.interpolate(values[3], values[7], max);
        return new VaryingScalarHPoint(declaration, newValues);
    }

    public Parameter bilinearInterpolate(float uMin, float uMax,
                                         float vMin, float vMax) {
        float[] newValues = new float[16];
        newValues[0] =
            Calc.interpolate(values[0], values[4], values[8], values[12],
                             uMin, vMin);
        newValues[1] =
            Calc.interpolate(values[1], values[5], values[9], values[13],
                             uMin, vMin);
        newValues[2] =
            Calc.interpolate(values[2], values[6], values[10], values[14],
                             uMin, vMin);
        newValues[3] =
            Calc.interpolate(values[3], values[7], values[11], values[15],
                             uMin, vMin);
        newValues[4] =
            Calc.interpolate(values[0], values[4], values[8], values[12],
                             uMax, vMin);
        newValues[5] =
            Calc.interpolate(values[1], values[5], values[9], values[13],
                             uMax, vMin);
        newValues[6] =
            Calc.interpolate(values[2], values[6], values[10], values[14],
                             uMax, vMin);
        newValues[7] =
            Calc.interpolate(values[3], values[7], values[11], values[15],
                             uMax, vMin);
        newValues[8] =
            Calc.interpolate(values[0], values[4], values[8], values[12],
                             uMin, vMax);
        newValues[9] =
            Calc.interpolate(values[1], values[5], values[9], values[13],
                             uMin, vMax);
        newValues[10] =
            Calc.interpolate(values[2], values[6], values[10], values[14],
                             uMin, vMax);
        newValues[11] =
            Calc.interpolate(values[3], values[7], values[11], values[15],
                             uMin, vMax);
        newValues[12] =
            Calc.interpolate(values[0], values[4], values[8], values[12],
                             uMax, vMax);
        newValues[13] =
            Calc.interpolate(values[1], values[5], values[9], values[13],
                             uMax, vMax);
        newValues[14] =
            Calc.interpolate(values[2], values[6], values[10], values[14],
                             uMax, vMax);
        newValues[15] =
            Calc.interpolate(values[3], values[7], values[11], values[15],
                             uMax, vMax);
        return new VaryingScalarHPoint(declaration, newValues);
    }

    public void linearDice(Grid grid) {
        throw new UnsupportedOperationException("Dice hpoint not implemented: "
                                                + declaration);
    }

    public void bilinearDice(Grid grid) {
        throw new UnsupportedOperationException("Dice hpoint not implemented: "
                                                + declaration);
    }

    public Parameter selectValues(int[] indexes) {
        if (declaration.getStorageClass() == Declaration.StorageClass.UNIFORM
            && indexes.length == 1) {
            int offset = indexes[0] * 4;
            return new UniformScalarHPoint(
                declaration,
                values[offset],
                values[offset + 1],
                values[offset + 2],
                values[offset + 3]);
        } else {
            float[] newValues = new float[indexes.length * 4];
            for (int i = 0; i < indexes.length; i++) {
                int srcOffset = indexes[i] * 4;
                int dstOffset = i * 4;
                newValues[dstOffset] = values[srcOffset];
                newValues[dstOffset + 1] = values[srcOffset + 1];
                newValues[dstOffset + 2] = values[srcOffset + 2];
                newValues[dstOffset + 3] = values[srcOffset + 3];
            }
            return new VaryingScalarHPoint(declaration, newValues);
        }
    }
}
