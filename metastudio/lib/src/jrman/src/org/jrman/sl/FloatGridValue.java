/*
 FloatGridValue.java
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
import org.jrman.grid.FloatGrid;

public class FloatGridValue extends Value {

    private FloatGrid value;

    public FloatGridValue(FloatGrid value) {
        this.value = value;
    }

    public FloatGrid getValue() {
        return value;
    }

    public Value add(Value other, BooleanGrid cond) {
        if (other instanceof FloatValue) {
            FloatGrid fg = FloatGrid.getInstance();
            if (cond != null)
                fg.add(value, ((FloatValue) other).getValue(), cond);
            else
                fg.add(value, ((FloatValue) other).getValue());
            return new FloatGridValue(fg);
        }
        if (other instanceof FloatGridValue) {
            FloatGrid fg = FloatGrid.getInstance();
            if (cond != null)
                fg.add(value, ((FloatGridValue) other).getValue(), cond);
            else
                fg.add(value, ((FloatGridValue) other).getValue());
            return new FloatGridValue(fg);
        }
        return other.reverseAdd(this, cond);
    }

    public Value reverseAdd(Value other, BooleanGrid cond) {
        if (other instanceof FloatValue) {
            FloatGrid fg = FloatGrid.getInstance();
            if (cond != null)
                fg.add(value, ((FloatValue) other).getValue(), cond);
            else
                fg.add(value, ((FloatValue) other).getValue());
            return new FloatGridValue(fg);
        }
        throw new UnsupportedOperationException("reverseAdd: " + other.getClass());
    }

    public Value sub(Value other, BooleanGrid cond) {
        if (other instanceof FloatValue) {
            FloatGrid fg = FloatGrid.getInstance();
            if (cond != null)
                fg.sub(value, ((FloatValue) other).getValue(), cond);
            else
                fg.sub(value, ((FloatValue) other).getValue());
            return new FloatGridValue(fg);
        }
        if (other instanceof FloatGridValue) {
            FloatGrid fg = FloatGrid.getInstance();
            if (cond != null)
                fg.sub(value, ((FloatGridValue) other).getValue(), cond);
            else
                fg.sub(value, ((FloatGridValue) other).getValue());
            return new FloatGridValue(fg);
        }
        return other.reverseSub(this, cond);
    }

    public Value reverseSub(Value other, BooleanGrid cond) {
        if (other instanceof FloatValue) {
            FloatGrid fg = FloatGrid.getInstance();
            if (cond != null)
                fg.sub(((FloatValue) other).getValue(), value, cond);
            else
                fg.sub(((FloatValue) other).getValue(), value);
            return new FloatGridValue(fg);
        }
        throw new UnsupportedOperationException("reverseSub: " + other.getClass());
    }

    public Value mul(Value other, BooleanGrid cond) {
        if (other instanceof FloatValue) {
            FloatGrid fg = FloatGrid.getInstance();
            if (cond != null)
                fg.mul(value, ((FloatValue) other).getValue(), cond);
            else
                fg.mul(value, ((FloatValue) other).getValue());
            return new FloatGridValue(fg);
        }
        if (other instanceof FloatGridValue) {
            FloatGrid fg = FloatGrid.getInstance();
            if (cond != null)
                fg.mul(value, ((FloatGridValue) other).getValue(), cond);
            else
                fg.mul(value, ((FloatGridValue) other).getValue());
            return new FloatGridValue(fg);
        }
        return other.reverseMul(this, cond);
    }

    public Value reverseMul(Value other, BooleanGrid cond) {
        if (other instanceof FloatValue) {
            FloatGrid fg = FloatGrid.getInstance();
            if (cond != null)
                fg.mul(value, ((FloatValue) other).getValue(), cond);
            else
                fg.mul(value, ((FloatValue) other).getValue());
            return new FloatGridValue(fg);
        }
        throw new UnsupportedOperationException("reverseMul: " + other.getClass());
    }

    public Value div(Value other, BooleanGrid cond) {
        if (other instanceof FloatValue) {
            FloatGrid fg = FloatGrid.getInstance();
            if (cond != null)
                fg.div(value, ((FloatValue) other).getValue(), cond);
            else
                fg.div(value, ((FloatValue) other).getValue());
            return new FloatGridValue(fg);
        }
        if (other instanceof FloatGridValue) {
            FloatGrid fg = FloatGrid.getInstance();
            if (cond != null)
                fg.div(value, ((FloatGridValue) other).getValue(), cond);
            else
                fg.div(value, ((FloatGridValue) other).getValue());
            return new FloatGridValue(fg);
        }
        return other.reverseDiv(this, cond);
    }

    public Value reverseDiv(Value other, BooleanGrid cond) {
        if (other instanceof FloatValue) {
            FloatGrid fg = FloatGrid.getInstance();
            if (cond != null)
                fg.div(((FloatValue) other).getValue(), value, cond);
            else
                fg.div(((FloatValue) other).getValue(), value);
            return new FloatGridValue(fg);
        }
        throw new UnsupportedOperationException("reverseDiv: " + other.getClass());
    }

    public Value negate(BooleanGrid cond) {
        FloatGrid fg = FloatGrid.getInstance();
        if (cond != null)
            fg.negate(value, cond);
        else
            fg.negate(value);
        return new FloatGridValue(fg);
    }

}
