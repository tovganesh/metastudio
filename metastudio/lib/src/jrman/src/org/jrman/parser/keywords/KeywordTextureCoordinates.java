/*
 KeywordTextureCoordinates.java
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

import org.jrman.parser.Tokenizer;

public class KeywordTextureCoordinates extends MotionKeywordParser {

    public void parse(Tokenizer st) throws Exception {
        boolean array = false;
        int token = st.nextToken();
        // Check por array
        if (token == TK_LBRACE)
            array = true;
        else
            st.pushBack();

        // Expect s1
        match(st, TK_NUMBER);
        float s1 = (float) st.nval;
        // Expect t1
        match(st, TK_NUMBER);
        float t1 = (float) st.nval;
        // Expect s2
        match(st, TK_NUMBER);
        float s2 = (float) st.nval;
        // Expect t2
        match(st, TK_NUMBER);
        float t2 = (float) st.nval;
        // Expect s3
        match(st, TK_NUMBER);
        float s3 = (float) st.nval;
        // Expect t3
        match(st, TK_NUMBER);
        float t3 = (float) st.nval;
        // Expect s4
        match(st, TK_NUMBER);
        float s4 = (float) st.nval;
        // Expect t4
        match(st, TK_NUMBER);
        float t4 = (float) st.nval;
        if (array)
            match(st, TK_RBRACE);
        parser.setTextureCoordinates(s1, t1, s2, t2, s3, t3, s4, t4);
    }

}
