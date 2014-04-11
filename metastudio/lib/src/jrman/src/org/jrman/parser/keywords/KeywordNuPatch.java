/*
 KeywordNuPatch.java
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
import org.jrman.parameters.ParameterList;

public class KeywordNuPatch extends MoveableObjectKeywordParser {

    public void parse(Tokenizer st) throws Exception {
        // Expect nu
        match(st, TK_NUMBER);
        int nu = (int) st.nval;

        // Expect u order
        match(st, TK_NUMBER);
        int uorder = (int) st.nval;

        // Expect u knot
        parseArray(st);
        float[] uknot = new float[arraySize];
        System.arraycopy(numbers, 0, uknot, 0, arraySize);

        // Expect u min
        match(st, TK_NUMBER);
        float umin = (float) st.nval;

        // Expect u max
        match(st, TK_NUMBER);
        float umax = (float) st.nval;

        // Expect nv
        match(st, TK_NUMBER);
        int nv = (int) st.nval;

        // Expect v order
        match(st, TK_NUMBER);
        int vorder = (int) st.nval;
        
        // Expect v knot
        parseArray(st);
        float[] vknot = new float[arraySize];
        System.arraycopy(numbers, 0, vknot, 0, arraySize);

        // Expect v min
        match(st, TK_NUMBER);
        float vmin = (float) st.nval;

        // Expect v max
        match(st, TK_NUMBER);
        float vmax = (float) st.nval;

        // Expect parameter list
        ParameterList parameterList = parseParameterList(st);
        parser.addNuPatch(nu, uorder, uknot, umin, umax, 
                          nv, vorder, vknot, vmin, vmax, 
                          parameterList);
    }

}
