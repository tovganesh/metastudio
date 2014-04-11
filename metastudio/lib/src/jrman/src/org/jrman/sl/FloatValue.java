/*
 FloatValue.java
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

public class FloatValue extends Value {

    private float value;

    public FloatValue(float value) {
        this.value = value;
    }

    public float getValue() {
        return value;
    }

    public Value add(Value other, BooleanGrid cond) {
        if (other instanceof FloatValue)
            return new FloatValue(value + ((FloatValue) other).value);
        else
            return other.reverseAdd(this, cond);
    }

    public Value sub(Value other, BooleanGrid cond) {
        if (other instanceof FloatValue)
            return new FloatValue(value - ((FloatValue) other).value);
        return other.reverseSub(this, cond);
    }

    public Value mul(Value other, BooleanGrid cond) {
        if (other instanceof FloatValue)
            return new FloatValue(value * ((FloatValue) other).value);
        return other.reverseMul(this, cond);
    }

    public Value div(Value other, BooleanGrid cond) {
        if (other instanceof FloatValue)
            return new FloatValue(value / ((FloatValue) other).value);
        return other.reverseDiv(this, cond);
    }

    public Value negate(BooleanGrid cond) {
        return new FloatValue(-value);
    }

}