/*
 KeywordDepthOfField.java
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
import org.jrman.util.Constants;

public class KeywordDepthOfField extends FrameKeywordParser {

    public void parse(Tokenizer st) throws Exception {
        // Might have zero arguments for a pin-hole camera
        int token = st.nextToken();
        st.pushBack();
        if (token != TK_KEYWORD) { // Is not a pin-hole camera
            // Expect fstop
            match(st, TK_NUMBER);
            float fstop = (float) st.nval;
            // Expect focal length
            match(st, TK_NUMBER);
            float focalLength = (float) st.nval;
            // Expect focal distance
            match(st, TK_NUMBER);
            float focalDistance = (float) st.nval;
            parser.setDepthOfField(fstop, focalLength, focalDistance);
        } else
            parser.setDepthOfField(Constants.INFINITY);
    }

}
