/*
 Value.java
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

public abstract class Value {
    
    public Value add(Value other, BooleanGrid cond) {
        throw new UnsupportedOperationException("add");
    }
    
    public Value reverseAdd(Value other, BooleanGrid cond) {
        throw new UnsupportedOperationException("reverseAdd");
    }
    
    public Value sub(Value other, BooleanGrid cond) {
        throw new UnsupportedOperationException("sub");
    }
    
    public Value reverseSub(Value other, BooleanGrid cond) {
        throw new UnsupportedOperationException("reverseSub");
    }
    
    public Value mul(Value other, BooleanGrid cond) {
        throw new UnsupportedOperationException("mul");
    }
    
    public Value reverseMul(Value other, BooleanGrid cond) {
        throw new UnsupportedOperationException("reverseMul");
    }
    
    public Value div(Value other, BooleanGrid cond) {
        throw new UnsupportedOperationException("div");
    }
    
    public Value reverseDiv(Value other, BooleanGrid cond) {
        throw new UnsupportedOperationException("reverseDiv");
    }
    
    public Value negate(BooleanGrid cond) {
        throw new UnsupportedOperationException("negate");
    }

}
