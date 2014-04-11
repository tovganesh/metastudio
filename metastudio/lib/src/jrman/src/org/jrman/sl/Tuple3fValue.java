/*
 Tuple3fValue.java
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

import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import org.jrman.grid.BooleanGrid;

public class Tuple3fValue extends Value {
    
    private Tuple3f value;
    
    private Tuple3f getCompatibleTuple() {
        if (value instanceof Point3f)
            return new Point3f();
        if (value instanceof Vector3f)
            return new Vector3f();
        return new Color3f();
    }
    
    public Tuple3fValue(Tuple3f value) {
        this.value = value;
    }
    
    public Tuple3f getValue() {
        return value;
    }
    
    public Value add(Value other, BooleanGrid cond) {
        if (other instanceof Tuple3fValue) {
            Tuple3f t3f = getCompatibleTuple();
            t3f.add(value, ((Tuple3fValue) other).value);
            return new Tuple3fValue(t3f);
        }
        if (other instanceof FloatValue) {
            float v = ((FloatValue) other).getValue();
            Tuple3f t3f = getCompatibleTuple();
            t3f.x = value.x + v;
            t3f.y = value.y + v;
            t3f.z = value.z + v;
            return new Tuple3fValue(t3f);
        }
        if (other instanceof FloatGridValue) {
            Tuple3fGridValue t3fgv = Tuple3fGridValue.cast((FloatGridValue) other);
            return t3fgv.reverseAdd(this, cond);
        }
        return other.reverseAdd(this, cond);
    }
    
    public Value reverseAdd(Value other, BooleanGrid cond) {
        if (other instanceof Tuple3fValue) {
            Tuple3f t3f = getCompatibleTuple();
            t3f.add(((Tuple3fValue) other).value, value);
            return new Tuple3fValue(t3f);
        }
        if (other instanceof FloatValue) {
            float v = ((FloatValue) other).getValue();
            Tuple3f t3f = getCompatibleTuple();
            t3f.x = v + value.x;
            t3f.y = v + value.y;
            t3f.z = v + value.z;
            return new Tuple3fValue(t3f);
        }
        if (other instanceof FloatGridValue) {
            Tuple3fGridValue t3fgv = Tuple3fGridValue.cast((FloatGridValue) other);
            return t3fgv.reverseAdd(this, cond);
        }
        throw new UnsupportedOperationException("reverseAdd: " + other.getClass());
    }

    public Value sub(Value other, BooleanGrid cond) {
        if (other instanceof Tuple3fValue) {
            Tuple3f t3f = getCompatibleTuple();
            t3f.sub(value, ((Tuple3fValue) other).value);
            return new Tuple3fValue(t3f);
        }
        if (other instanceof FloatValue) {
            float v = ((FloatValue) other).getValue();
            Tuple3f t3f = getCompatibleTuple();
            t3f.x = value.x - v;
            t3f.y = value.y - v;
            t3f.z = value.z - v;
            return new Tuple3fValue(t3f);
        }
        if (other instanceof FloatGridValue) {
            Tuple3fGridValue t3fgv = Tuple3fGridValue.cast((FloatGridValue) other);
            return t3fgv.reverseSub(this, cond);
        }
        return other.reverseSub(this, cond);
    }
    
    public Value reverseSub(Value other, BooleanGrid cond) {
        if (other instanceof Tuple3fValue) {
            Tuple3f t3f = getCompatibleTuple();
            t3f.sub(((Tuple3fValue) other).value, value);
            return new Tuple3fValue(t3f);
        }
        if (other instanceof FloatValue) {
            float v = ((FloatValue) other).getValue();
            Tuple3f t3f = getCompatibleTuple();
            t3f.x = v - value.x;
            t3f.y = v - value.y;
            t3f.z = v - value.z;
            return new Tuple3fValue(t3f);
        }
        if (other instanceof FloatGridValue) {
            Tuple3fGridValue t3fgv = Tuple3fGridValue.cast((FloatGridValue) other);
            return t3fgv.sub(this, cond);
        }
        throw new UnsupportedOperationException("reverseSub: " + other.getClass());
    }

    public Value mul(Value other, BooleanGrid cond) {
        if (other instanceof Tuple3fValue) {
            Tuple3f t3f = getCompatibleTuple();
            Tuple3f otherValue = ((Tuple3fValue) other).getValue();
            t3f.x = value.x * otherValue.x;
            t3f.y = value.y * otherValue.y;
            t3f.z = value.z * otherValue.z;
            return new Tuple3fValue(t3f);
        }
        if (other instanceof FloatValue) {
            float v = ((FloatValue) other).getValue();
            Tuple3f t3f = getCompatibleTuple();
            t3f.x = value.x * v;
            t3f.y = value.y * v;
            t3f.z = value.z * v;
            return new Tuple3fValue(t3f);
        }
        if (other instanceof FloatGridValue) {
            Tuple3fGridValue t3fgv = Tuple3fGridValue.cast((FloatGridValue) other);
            return t3fgv.reverseMul(this, cond);
        }
        return other.reverseMul(this, cond);
    }
    
    public Value reverseMul(Value other, BooleanGrid cond) {
        if (other instanceof Tuple3fValue) {
            Tuple3f t3f = getCompatibleTuple();
            Tuple3f otherValue = ((Tuple3fValue) other).getValue();
            t3f.x = otherValue.x * value.x;
            t3f.y = otherValue.y * value.y;
            t3f.z = otherValue.z  * value.z;
            return new Tuple3fValue(t3f);
        }
        if (other instanceof FloatValue) {
            float v = ((FloatValue) other).getValue();
            Tuple3f t3f = getCompatibleTuple();
            t3f.x = v * value.x;
            t3f.y = v * value.y;
            t3f.z = v * value.z;
            return new Tuple3fValue(t3f);
        }
        if (other instanceof FloatGridValue) {
            Tuple3fGridValue t3fgv = Tuple3fGridValue.cast((FloatGridValue) other);
            return t3fgv.reverseMul(this, cond);
        }
        throw new UnsupportedOperationException("reverseMul: " + other.getClass());
    }

    public Value div(Value other, BooleanGrid cond) {
        if (other instanceof Tuple3fValue) {
            Tuple3f t3f = getCompatibleTuple();
            Tuple3f otherValue = ((Tuple3fValue) other).getValue();
            t3f.x = value.x / otherValue.x;
            t3f.y = value.y / otherValue.y;
            t3f.z = value.z / otherValue.z;
            return new Tuple3fValue(t3f);
        }
        if (other instanceof FloatValue) {
            float v = ((FloatValue) other).getValue();
            Tuple3f t3f = getCompatibleTuple();
            t3f.x = value.x / v;
            t3f.y = value.y / v;
            t3f.z = value.z / v;
            return new Tuple3fValue(t3f);
        }
        if (other instanceof FloatGridValue) {
            Tuple3fGridValue t3fgv = Tuple3fGridValue.cast((FloatGridValue) other);
            return t3fgv.reverseDiv(this, cond);
        }
        return other.reverseDiv(this, cond);
    }
    
    public Value reverseDiv(Value other, BooleanGrid cond) {
        if (other instanceof Tuple3fValue) {
            Tuple3f t3f = getCompatibleTuple();
            Tuple3f otherValue = ((Tuple3fValue) other).getValue();
            t3f.x = otherValue.x / value.x;
            t3f.y = otherValue.y / value.y;
            t3f.z = otherValue.z  / value.z;
            return new Tuple3fValue(t3f);
        }
        if (other instanceof FloatValue) {
            float v = ((FloatValue) other).getValue();
            Tuple3f t3f = getCompatibleTuple();
            t3f.x = v / value.x;
            t3f.y = v / value.y;
            t3f.z = v / value.z;
            return new Tuple3fValue(t3f);
        }
        if (other instanceof FloatGridValue) {
            Tuple3fGridValue t3fgv = Tuple3fGridValue.cast((FloatGridValue) other);
            return t3fgv.div(this, cond);
        }
        throw new UnsupportedOperationException("reverseDiv: " + other.getClass());
    }
    
    public Value negate(BooleanGrid cond) {
        Tuple3f t3f = getCompatibleTuple();
        t3f.negate(value);
        return new Tuple3fValue(t3f);
    }

}
