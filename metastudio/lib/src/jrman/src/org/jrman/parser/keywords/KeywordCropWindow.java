/*
 KeywordCropWindow.java
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

public class KeywordCropWindow extends FrameKeywordParser {

    public void parse(Tokenizer st) throws Exception {
        boolean array = false;
        int token = st.nextToken();
        // Check por array
        if (token == TK_LBRACE)
            array = true;
        else
            st.pushBack();

        // Expect xmin
        match(st, TK_NUMBER);
        float xmin = (float) st.nval;
        // Expect xmax
        match(st, TK_NUMBER);
        float xmax = (float) st.nval;
        // Expect ymin
        match(st, TK_NUMBER);
        float ymin = (float) st.nval;
        // Expect ymax
        match(st, TK_NUMBER);
        float ymax = (float) st.nval;
        if (array)
            match(st, TK_RBRACE);
        parser.setCropWindow(xmin, xmax, ymin, ymax);
    }

}
