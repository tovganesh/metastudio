/*
 KeywordProcedural.java
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

public class KeywordProcedural extends ObjectKeywordParser {

    public void parse(Tokenizer st) throws Exception {
        // Expect procname
        match(st, TK_STRING);
        String s = st.sval;
        
        // Expect args
        parseArray(st);
        String[] args = new String[arraySize];
        System.arraycopy(strings, 0, args, 0, arraySize);
        
        // Expect bounds
        parseArray(st);
        if (arraySize != 6)
            throw new Exception("Invalid number of arguments for bounding box: "
                    + arraySize);
        float xmin = numbers[0];
        float xmax = numbers[1];
        float ymin = numbers[2];
        float ymax = numbers[3];
        float zmin = numbers[4];
        float zmax = numbers[5];
        if (s.equals("DelayedReadArchive")) {
            if (args.length != 1)
                throw new Exception("Invalid number of arguments for DelayedReadArchive: " +
                        args.length);
            parser.addDelayedReadArchive(args[0], xmin, xmax, ymin, ymax, zmin, zmax);
        } else
            throw new Exception("Unknown procedural: " + s);
    }

}
