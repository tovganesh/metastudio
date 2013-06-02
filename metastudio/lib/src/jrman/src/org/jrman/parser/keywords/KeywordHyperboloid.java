/*
 KeywordHyperboloid.java
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

public class KeywordHyperboloid extends MoveableObjectKeywordParser {

	public void parse(Tokenizer st) throws Exception {
		boolean array = false;
		int token = st.nextToken();
		// Check por array
		if (token == TK_LBRACE)
			array = true;
		else
			st.pushBack();

		// Expect x1
		match(st, TK_NUMBER);
		float x1 = (float) st.nval;
		// Expect y1
		match(st, TK_NUMBER);
		float y1 = (float) st.nval;
		// Expect z1
		match(st, TK_NUMBER);
		float z1 = (float) st.nval;
		// Expect x2
		match(st, TK_NUMBER);
		float x2 = (float) st.nval;
		// Expect y2
		match(st, TK_NUMBER);
		float y2 = (float) st.nval;
		// Expect z2
		match(st, TK_NUMBER);
		float z2 = (float) st.nval;
		// Expect theta max
		match(st, TK_NUMBER);
		float thetaMax = (float) st.nval;
		if (array)
			match(st, TK_RBRACE);
		// Expect parameter list
		ParameterList parameters = parseParameterList(st);
		parser.addHyperboloid(x1, y1, z1, x2, y2, z2, thetaMax, parameters);
	}

}
