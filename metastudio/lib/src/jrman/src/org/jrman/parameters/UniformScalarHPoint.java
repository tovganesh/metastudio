/*
  UniformScalarHPoint.java
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

public class UniformScalarHPoint extends Parameter {
    
    final private float x;
    
    final private float y;
    
    final private float z;
    
    final private float w;

    public UniformScalarHPoint(Declaration declaration,
                               float x, float y, float z, float w) {
        super(declaration);
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    
    public void getValue(Tuple4f out) {
        out.x = x;
        out.y = y;
        out.z = z;
        out.w = w;
    }

    public Parameter linearInterpolate(float min, float max) {
        return this;
    }

    public Parameter bilinearInterpolate(float uMin, float uMax,
                                         float vMin, float vMax) {
        return this;
    }

    public void linearDice(Grid grid) {
        throw new UnsupportedOperationException("Parameter not diceable: " +
                                                declaration);        
    }

    public void bilinearDice(Grid grid) {
        throw new UnsupportedOperationException("Parameter not diceable: " +
                                                declaration);        
    }

    public Parameter selectValues(int[] indexes) {
        return this;
    }

}
