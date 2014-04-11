/*
  VaryingArrayFloat.java
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

import org.jrman.grid.Grid;
import org.jrman.util.Calc;

public class VaryingArrayFloat extends Parameter {

    final private float[] values;

    public VaryingArrayFloat(Declaration declaration, float[] values) {
        super(declaration);
        this.values = values;
    }

    public float getValue(int index, int arrayIndex) {
        return values[index * declaration.getCount() + arrayIndex];
    }

    public void setValue(int index, int arrayIndex, float value) {
        values[index * declaration.getCount() + arrayIndex] = value;
    }

    public Parameter linearInterpolate(float min, float max) {
        int n = declaration.getCount();
        float[] newValues = new float[2 * n];
        for (int i = 0; i < n; i++) {
            newValues[i] = Calc.interpolate(values[i], values[n + i], min);
            newValues[n + i] = Calc.interpolate(values[i], values[n + i], max);
        }
        return new VaryingArrayFloat(declaration, newValues);
    }

    public Parameter bilinearInterpolate(float uMin, float uMax,
                                         float vMin, float vMax) {
        int n = declaration.getCount();
        int n2 = n * 2;
        int n3 = n * 3;
        float[] newValues = new float[4 * n];
        for (int i = 0; i < n; i++) {
            newValues[i] =
                Calc.interpolate(
                    values[i],
                    values[n + i],
                    values[n2 + i],
                    values[n3 + i],
                    uMin,
                    vMin);
            newValues[n + i] =
                Calc.interpolate(
                    values[i],
                    values[n + i],
                    values[n2 + i],
                    values[n3 + i],
                    uMax,
                    vMin);
            newValues[n2 + i] =
                Calc.interpolate(
                    values[i],
                    values[n + i],
                    values[n2 + i],
                    values[n3 + i],
                    uMin,
                    vMax);
            newValues[n3 + i] =
                Calc.interpolate(
                    values[i],
                    values[n + i],
                    values[n2 + i],
                    values[n3 + i],
                    uMax,
                    vMax);
        }
        return new VaryingArrayFloat(declaration, newValues);
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
        int n = declaration.getCount();
        if (declaration.getStorageClass() == Declaration.StorageClass.UNIFORM
            && indexes.length == 1) {
            float[] newValues = new float[n];
            int offset = indexes[0] * n;
            for (int i = 0; i < n; i++)
                newValues[i] = values[offset + i];
            return new UniformArrayFloat(declaration, values);
        } else {
            float[] newValues = new float[indexes.length * n];
            for (int i = 0; i < indexes.length; i++) {
                int srcOffset = indexes[i] * n;
                int dstOffset = i * n;
                for (int e = 0; e < n; e++)
                    newValues[dstOffset + e] = values[srcOffset + e];
            }
            return new VaryingArrayFloat(declaration, newValues);
        }
    }
    
    public VaryingScalarFloat extract(Declaration decl, int arrayIndex) {
        int n = declaration.getCount();
        float[] newValues = new float[values.length / n];
        for (int i = 0; i < newValues.length; i++)
            newValues[i] = values[i * n + arrayIndex];
       return new VaryingScalarFloat(decl, newValues);
    }

}
