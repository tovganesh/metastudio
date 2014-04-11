/*
 KeywordSkew.java
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

public class KeywordSkew extends MotionKeywordParser {
    
    public KeywordSkew() {
        validStates.add(Parser.State.OBJECT);
    }

    public void parse(Tokenizer st) throws Exception {
        boolean array = false;
        int token = st.nextToken();
        // Check por array
        if (token == TK_LBRACE)
            array = true;
        else
            st.pushBack();

        // Expect angle
        match(st, TK_NUMBER);

        // Expect dx1
        match(st, TK_NUMBER);

        // Expect dy1
        match(st, TK_NUMBER);

        // Expect dz1
        match(st, TK_NUMBER);

        // Expect dx2
        match(st, TK_NUMBER);

        // Expect dy2
        match(st, TK_NUMBER);

        // Expect dz2
        match(st, TK_NUMBER);

        if (array)
            match(st, TK_RBRACE);
    }

}
