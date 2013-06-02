/*
  UniformScalarFloat.java
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

public class UniformScalarFloat extends Parameter {
    
    final private float value;

    public UniformScalarFloat(Declaration declaration, float value) {
        super(declaration);
        this.value = value;
    }
    
    public float getValue() {
        return value;
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
