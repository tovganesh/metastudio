/*
 Tuple3fGridValue.java
 Copyright (C) 2003 Gerardo Horvilleur Martinez

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

package org.jrman.sl;

import org.jrman.grid.BooleanGrid;
import org.jrman.grid.Color3fGrid;
import org.jrman.grid.Point3fGrid;
import org.jrman.grid.Tuple3fGrid;
import org.jrman.grid.Vector3fGrid;

public class Tuple3fGridValue extends Value {
    
    private Tuple3fGrid value;
    
    public static Tuple3fGridValue cast(FloatGridValue fgv) {
        Point3fGrid p3fg = Point3fGrid.getInstance();
        p3fg.set(fgv.getValue());
        return new Tuple3fGridValue(p3fg);
    }
    
    public Tuple3fGrid getCompatibleTupleGrid() {
        if (value instanceof Point3fGrid)
            return Point3fGrid.getInstance();
        if (value instanceof Vector3fGrid)
            return Vector3fGrid.getInstance();
        return Color3fGrid.getInstance();                    
    }
    
    public Tuple3fGridValue(Tuple3fGrid value) {
        this.value = value; 
    }
    
    public Tuple3fGrid getValue() {
        return value;
    }
    
    public Value add(Value other, BooleanGrid cond) {
        if (other instanceof Tuple3fGridValue) {
            Tuple3fGrid t3fg = getCompatibleTupleGrid();
            t3fg.add(value, ((Tuple3fGridValue) other).value, cond);
            return new Tuple3fGridValue(t3fg);
        }
        if (other instanceof Tuple3fValue) {
        }
        if (other instanceof FloatValue) {
        }
        if (other instanceof FloatGridValue) {
        }
        return other.reverseAdd(this, cond);
    }

}
