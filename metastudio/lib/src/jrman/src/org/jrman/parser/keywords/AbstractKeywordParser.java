/*
 AbstractKeywordParser.java
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

import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.vecmath.Color3f;
import javax.vecmath.Matrix4f;

import org.jrman.geom.Bounds3f;
import org.jrman.parameters.Declaration;
import org.jrman.parameters.ParameterList;
import org.jrman.parser.Global;
import org.jrman.parser.Parser;
import org.jrman.parser.Parser.State;
import org.jrman.parser.Tokenizer;

public abstract class AbstractKeywordParser implements KeywordParser {

    final static int TK_KEYWORD = StreamTokenizer.TT_WORD;

    final static int TK_NUMBER = StreamTokenizer.TT_NUMBER;

    final static int TK_EOF = StreamTokenizer.TT_EOF;

    final static int TK_EOL = StreamTokenizer.TT_EOL;

    final static int TK_STRING = '"';

    final static int TK_LBRACE = '[';

    final static int TK_RBRACE = ']';

    protected static float[] numbers = new float[5000];

    protected static int[] integers = new int[5000];

    protected static String[] strings = new String[10];

    protected static int arraySize;

    protected Parser parser;

    protected Set<State> validStates = new HashSet<State>();
    
    private Map<String, Declaration> declarationsCache = new HashMap<String, Declaration>();

    public static void reset() {
        numbers = new float[5000];
        integers = new int[5000];
        strings = new String[10];
    }

    public void setParser(Parser parser) {
        this.parser = parser;
    }

    private String tokenToString(int token) {
        String result;
        switch (token) {
            case TK_KEYWORD :
                result = "keyword";
                break;
            case TK_NUMBER :
                result = "number";
                break;
            case TK_EOF :
                result = "EOF";
                break;
            case TK_EOL :
                result = "EOL";
                break;
            case TK_STRING :
                result = "string";
                break;
            default :
                result = "" + (char) token;
                break;
        }
        return result;
    }

    protected void match(Tokenizer st, int expected) throws Exception {
        int found = st.nextToken();
        if (found != expected)
            throw new Exception(
                "Parsing error.\n"
                    + "Expected: "
                    + tokenToString(expected)
                    + "\n"
                    + "Found: "
                    + tokenToString(found));
    }

    protected Bounds3f readBounds(Tokenizer st) throws IOException, Exception {
        boolean array = false;
        int token = st.nextToken();
        // Check por array
        if (token == TK_LBRACE)
            array = true;
        else
            st.pushBack();
        // Expect x min
        match(st, TK_NUMBER);
        float xMin = st.nval;
        // Expect x max
        match(st, TK_NUMBER);
        float xMax = st.nval;
        // Expect y min
        match(st, TK_NUMBER);
        float yMin = st.nval;
        // Expect y max
        match(st, TK_NUMBER);
        float yMax = st.nval;
        // Expect z min
        match(st, TK_NUMBER);
        float zMin = st.nval;
        // Expect z max
        match(st, TK_NUMBER);
        float zMax = st.nval;
        if (array)
            match(st, TK_RBRACE);
        Bounds3f bounds = new Bounds3f(xMin, xMax, yMin, yMax, zMin, zMax);
        return bounds;
    }

    protected Matrix4f readMatrix(Tokenizer st) throws Exception {
        // Expect array start
        match(st, TK_LBRACE);
        // Expect a
        match(st, TK_NUMBER);
        float a = st.nval;
        // Expect b
        match(st, TK_NUMBER);
        float b = st.nval;
        // Expect c
        match(st, TK_NUMBER);
        float c = st.nval;
        // Expect d
        match(st, TK_NUMBER);
        float d = st.nval;
        // Expect e
        match(st, TK_NUMBER);
        float e = st.nval;
        // Expect f
        match(st, TK_NUMBER);
        float f = st.nval;
        // Expect g
        match(st, TK_NUMBER);
        float g = st.nval;
        // Expect h
        match(st, TK_NUMBER);
        float h = st.nval;
        // Expect i
        match(st, TK_NUMBER);
        float i = st.nval;
        // Expect j
        match(st, TK_NUMBER);
        float j = st.nval;
        // Expect k
        match(st, TK_NUMBER);
        float k = st.nval;
        // Expect l
        match(st, TK_NUMBER);
        float l = st.nval;
        // Expect m
        match(st, TK_NUMBER);
        float m = st.nval;
        // Expect n
        match(st, TK_NUMBER);
        float n = st.nval;
        // Expect o
        match(st, TK_NUMBER);
        float o = st.nval;
        // Expect p
        match(st, TK_NUMBER);
        float p = st.nval;
        // Expect array end
        match(st, TK_RBRACE);
        // Renderman uses row vectors and Java3D uses column vectors....
        Matrix4f transform = new Matrix4f(a, e, i, m, b, f, j, n, c, g, k, o, d, h, l, p);
        return transform;
    }

    protected Color3f parseColor(Tokenizer st) throws IOException, Exception {
        boolean array = false;
        int token = st.nextToken();
        // Check por array
        if (token == TK_LBRACE)
            array = true;
        else
            st.pushBack();
        // Expect red
        match(st, TK_NUMBER);
        float red = st.nval;
        // Expect green
        match(st, TK_NUMBER);
        float green = st.nval;
        // Expect blue
        match(st, TK_NUMBER);
        float blue = st.nval;
        if (array)
            match(st, TK_RBRACE);
        Color3f color = new Color3f(red, green, blue);
        return color;
    }

    public Set getValidStates() {
        return validStates;
    }

    protected float[] readFloatArray(Tokenizer st) throws Exception, IOException {
        List<Float> floats = new ArrayList<Float>();
        // Expect array start
        match(st, TK_LBRACE);
        // Expect a variable length sequence of numbers
        while (st.nextToken() == TK_NUMBER)
            floats.add(new Float(st.nval));
        st.pushBack();
        float[] result = new float[floats.size()];
        for (int i = 0; i < floats.size(); i++)
            result[i] = ((Float) floats.get(i)).floatValue();
        // Expect array end
        match(st, TK_RBRACE);
        return result;
    }

    /*
    protected Map parseParameterList(Tokenizer st) throws Exception {
        List list = new ArrayList();
        // Expect list of key & value pairs
        while (st.nextToken() == TK_STRING) {
            list.add(st.sval);
            int token = st.nextToken();
            if (token == TK_LBRACE) {
                st.pushBack();
                list.add(parseArray(st));
            } else if (token == TK_NUMBER) {
                List array = new ArrayList();
                array.add(new Float((float) st.nval));
                list.add(array);
            } else if (token == TK_STRING) {
                List array = new ArrayList();
                array.add(st.sval);
                list.add(array);
            }
        }
        st.pushBack();
        return parser.translateParameterList(list);
    }
    */

    protected void parseArray(Tokenizer st) throws Exception {
        arraySize = 0;
        // Expect array start
        match(st, TK_LBRACE);
        // Expect a variable length sequence of numbers or strings
        int token;
        while ((token = st.nextToken()) == TK_NUMBER || token == TK_STRING)
            switch (token) {
                case TK_NUMBER :
                    if (arraySize == numbers.length) {
                        float[] tmp = new float[numbers.length * 2];
                        System.arraycopy(numbers, 0, tmp, 0, numbers.length);
                        numbers = tmp;
                    }
                    numbers[arraySize++] = st.nval;
                    break;
                case TK_STRING :
                    if (arraySize == strings.length) {
                        String[] tmp = new String[strings.length * 2];
                        System.arraycopy(strings, 0, tmp, 0, strings.length);
                        strings = tmp;
                    }
                    strings[arraySize++] = st.sval;
                    break;
            }
        st.pushBack();
        // Expect array end
        match(st, TK_RBRACE);
    }

    protected void parseIntegerArray(Tokenizer st) throws Exception {
        arraySize = 0;
        // Expect array start
        match(st, TK_LBRACE);
        // Expect a variable length sequence of integers
        while (st.nextToken() == TK_NUMBER) {
            if (arraySize == integers.length) {
                int[] tmp = new int[integers.length * 2];
                System.arraycopy(integers, 0, tmp, 0, integers.length);
                integers = tmp;
            }
            integers[arraySize++] = (int) st.nval;
        }
        st.pushBack();
        // Expect array end
        match(st, TK_RBRACE);
    }

    private Declaration getDeclaration(String name) {
        Declaration declaration = Global.getDeclaration(name);
        if (declaration != null)
            return declaration;
        // Check for inline declaration
        declaration = (Declaration) declarationsCache.get(name);
        if (declaration != null)
            return declaration;
        name = name.trim(); // Just being careful...
        int pos = name.lastIndexOf(' ');
        if (pos == -1)
            throw new IllegalArgumentException();
        String decl = name.substring(0, pos);
        String vname = name.substring(pos + 1);
        declaration = new Declaration(vname, decl);
        declarationsCache.put(name, declaration);
        return declaration;
    }

    protected ParameterList parseParameterList(Tokenizer st) throws Exception {
        ParameterList parameters = new ParameterList();
        // Expect list of key & value pairs
        while (st.nextToken() == TK_STRING) {
            Declaration declaration = null;
            try {
                declaration = getDeclaration(st.sval);
            } catch (IllegalArgumentException e) {
                System.err.println("Unknown type for parameter: " + st.sval);
            }
            int token = st.nextToken();
            if (token == TK_LBRACE) {
                st.pushBack();
                parseArray(st);
            } else if (token == TK_NUMBER) {
                numbers[0] = st.nval;
                arraySize = 1;
            } else if (token == TK_STRING) {
                strings[0] = st.sval;
                arraySize = 1;
            }
            parameters.addParameter(declaration.buildParameter(numbers, strings, arraySize));
        }
        st.pushBack();
        return parameters;
    }

}
