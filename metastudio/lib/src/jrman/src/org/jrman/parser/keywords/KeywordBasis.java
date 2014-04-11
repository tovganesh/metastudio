/*
 KeywordBasis.java
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

import javax.vecmath.Matrix4f;

import org.jrman.attributes.Basis;
import org.jrman.parser.Tokenizer;

public class KeywordBasis extends MotionKeywordParser {

    public void parse(Tokenizer st) throws Exception {
        // Expect u basis
        Basis uBasis = readBasis(st);

        // Expect u step
        match(st, TK_NUMBER);
        int uStep = (int) st.nval;
            
        // Expect v basis
        Basis vBasis = readBasis(st);
        
        // Expect u step
        match(st, TK_NUMBER);
        int vStep = (int) st.nval;
            
        parser.setBasis(uBasis, uStep, vBasis, vStep);

    }

    private Basis readBasis(Tokenizer st) throws Exception {
        Basis result;
        int token = st.nextToken();
        st.pushBack();
        // Is a basis name or an array?
        if (token == TK_LBRACE) {
            // Expect matrix
            Matrix4f matrix = readMatrix(st);
            result = new Basis(matrix);
        }
        else{
            // Expect basis name
            match(st, TK_STRING);
            String name = st.sval;
            
            result = Basis.getNamed(name);
        }
        return result;
    }

}
