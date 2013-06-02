/*
 KeywordPointsPolygons.java
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

import org.jrman.parameters.ParameterList;
import org.jrman.parser.Tokenizer;

public class KeywordPointsPolygons extends MotionKeywordParser {

    public void parse(Tokenizer st) throws Exception {
        
        // Expect array nVertices
        parseIntegerArray(st);
        int[] nVertices = new int[arraySize];
        System.arraycopy(integers, 0, nVertices, 0, arraySize);

        // Expect array vertices
        parseIntegerArray(st);
        int[] vertices = new int[arraySize];
        System.arraycopy(integers, 0, vertices, 0, arraySize);

        // Expect parameter list
		ParameterList parameterList = parseParameterList(st);
        parser.addPointsPolygons(nVertices, vertices, parameterList);
    }

}
