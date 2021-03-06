/*
 KeywordRotate.java
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

package org.jrman.parser.keywords;

import org.jrman.parser.Parser;
import org.jrman.parser.Tokenizer;

public class KeywordRotate extends MotionKeywordParser {
    
    public KeywordRotate() {
        validStates.add(Parser.State.OBJECT);
    }

    public void parse(Tokenizer st) throws Exception {
        // Expect angle
        match(st, TK_NUMBER);
        float angle = (float) st.nval;
        // Expect dx
        match(st, TK_NUMBER);
        float dx = (float) st.nval;
        // Expect dy
        match(st, TK_NUMBER);
        float dy = (float) st.nval;
        // Expect dz
        match(st, TK_NUMBER);
        float dz = (float) st.nval;
        parser.rotate(angle, dx, dy, dz);
    }

}
