/*
  VaryingArrayTuple3f.java
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

import javax.vecmath.Tuple3f;

import org.jrman.grid.Grid;
import org.jrman.util.Calc;

public class VaryingArrayTuple3f extends Parameter {

    final private float[] values;

    public VaryingArrayTuple3f(Declaration declaration, float[] values) {
        super(declaration);
        this.values = values;
    }

    public void getValue(int index, int arrayIndex, Tuple3f out) {
        int offset = (index * declaration.getCount() + arrayIndex) * 3;
        out.x = values[offset++];
        out.y = values[offset++];
        out.z = values[offset];
    }

    public Parameter linearInterpolate(float min, float max) {
        int n = declaration.getCount();
        int off = n * 3;
        float[] newValues = new float[n * 6];
        for (int i = 0; i < off; i++) {
            newValues[i] = Calc.interpolate(values[i], values[off + i], min);
            newValues[off + i] =
                Calc.interpolate(values[i], values[off + i], max);
        }
        return new VaryingArrayTuple3f(declaration, newValues);
    }

    public Parameter bilinearInterpolate(float uMin, float uMax,
                                         float vMin, float vMax) {
        int n = declaration.getCount();
        int off1 = n * 3;
        int off2 = off1 * 2;
        int off3 = off1 + off2;
        float[] newValues = new float[n * 12];
        for (int i = 0; i < off1; i++) {
            newValues[i] =
                Calc.interpolate(
                    values[i],
                    values[off1 + i],
                    values[off2 + i],
                    values[off3 + i],
                    uMin,
                    vMin);
            newValues[off1 + i] =
                Calc.interpolate(
                    values[i],
                    values[off1 + i],
                    values[off2 + i],
                    values[off3 + i],
                    uMax,
                    vMin);
            newValues[off1 + i] =
                Calc.interpolate(
                    values[i],
                    values[off1 + i],
                    values[off2 + i],
                    values[off3 + i],
                    uMin,
                    vMax);
            newValues[off1 + i] =
                Calc.interpolate(
                    values[i],
                    values[off1 + i],
                    values[off2 + i],
                    values[off3 + i],
                    uMax,
                    vMax);
        }
        return new VaryingArrayTuple3f(declaration, newValues);
    }

    public void linearDice(Grid grid) {
        throw new UnsupportedOperationException("Dice array not implemented: " +
                                                declaration);
    }

    public void bilinearDice(Grid grid) {
        throw new UnsupportedOperationException("Dice array not implemented: " +
                                                declaration);
    }

    public Parameter selectValues(int[] indexes) {
        int n = declaration.getCount() * 3;
        if (declaration.getStorageClass() == Declaration.StorageClass.UNIFORM
            && indexes.length == 1) {
                float[] newValues = new float[n];
                int offset = indexes[0] * n;
                for (int i = 0; i < n; i++)
                    newValues[i] = values[offset + i];
                return new UniformArrayTuple3f(declaration, newValues);
        } else {
            float[] newValues = new float[indexes.length * n];
            for (int i = 0; i < indexes.length; i++) {
                int srcOffset = indexes[i] * n;
                int dstOffset = i * n;
                for (int e = 0; e < n; e++)
                    newValues[dstOffset + e] = values[srcOffset + e];
            }
            return new VaryingArrayTuple3f(declaration, newValues);
        }
    }

}
