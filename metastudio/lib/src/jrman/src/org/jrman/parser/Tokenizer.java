/*
 * Tokenizer.java Copyright (C) 2004 Gerardo Horvilleur Martinez
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package org.jrman.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.StreamTokenizer;
import java.util.Arrays;
import java.util.Comparator;

import org.jrman.util.Calc;

public class Tokenizer {

    private static String[] strings = new String[0];

    private static char[] buffer = new char[2000];

    public String sval;

    public float nval;

    private Reader reader;

    private char ch;

    private int pos;

    private int lineno = 1;

    private int token;

    private boolean pushedBack;

    public Tokenizer(Reader reader) throws IOException {
        this.reader = reader;
        nextChar();
    }

    private void nextChar() throws IOException {
        ch = (char) reader.read();
    }

    private void bufferReset() {
        pos = 0;
    }

    private void bufferAdd() throws IOException {
        if (pos == buffer.length) {
            char[] tmp = new char[buffer.length * 2];
            System.arraycopy(buffer, 0, tmp, 0, buffer.length);
            buffer = tmp;
        }
        buffer[pos++] = ch;
        nextChar();
    }

    /*
     * 
     * private static boolean matches(char[] buffer, int pos, String s) { if
     * (s.length() != pos) return false; for (int i = 0; i < pos; i++) if
     * (buffer[i] != s.charAt(i)) return false; return true; }
     * 
     * private String bufferToString() { for (int i = 0; i < strings.size();
     * i++) { String s = (String) strings.get(i); if (matches(buffer, pos, s))
     * return s; } String s = new String(buffer, 0, pos); strings.add(s); return
     * s; }
     *  
     */

    private Comparator<Serializable> comparator = new Comparator<Serializable>() {
        @Override
        public int compare(Serializable o1, Serializable o2) {
            String s = (String) o1;
            if (s.length() == pos) {
                for (int i = 0; i < pos; i++) {
                    char chs = s.charAt(i);
                    char chb = buffer[i];
                    if (chs < chb)
                        return -1;
                    if (chs > chb)
                        return 1;
                }
                return 0;
            } else {
                int l = Calc.min(s.length(), pos);
                for (int i = 0; i < l; i++) {
                    char chs = s.charAt(i);
                    char chb = buffer[i];
                    if (chs < chb)
                        return -1;
                    if (chs > chb)
                        return 1;
                }
                return s.length() < pos ? -1 : 1;
            }
        }
    };

    private String bufferToString() {
        int index = Arrays.binarySearch(strings, buffer, comparator);
        if (index >= 0)
            return strings[index];
        index = -(index + 1);
        String[] tmp = new String[strings.length + 1];
        System.arraycopy(strings, 0, tmp, 0, index);
        System.arraycopy(strings, index, tmp, index + 1, strings.length - index);
        strings = tmp;
        String s = new String(buffer, 0, pos);
        strings[index] = s;
        return s;
    }
    
    public int nextToken() throws IOException {
        if (pushedBack) {
            pushedBack = false;
            return token;
        }
        while (true) {
            while (Character.isWhitespace(ch)) {
                if (ch == '\n')
                    lineno++;
                nextChar();
            }
            if (ch == '#') {
                while (ch != '\n')
                    nextChar();
            } else
                break;
        }
        if (ch == 0xffff) {
            token = StreamTokenizer.TT_EOF;
            return token;
        }
        if (Character.isDigit(ch) || ch == '.' || ch == '-' || ch == '+') {
            float negative = 1f;
            float exp = 1f;
            boolean afterPoint = false;
            nval = 0f;
            if (ch == '.')
                afterPoint = true;
            else if (ch == '-')
                negative = -1f;
            else if (ch != '+')
                nval = ch - '0';
            nextChar();
            while (Character.isDigit(ch) || ch == '.') {
                if (afterPoint || ch == '.') {
                    if (!afterPoint)
                        nextChar();
                    while (Character.isDigit(ch)) {
                        nval = nval * 10f + (ch - '0');
                        exp *= 0.1f;
                        nextChar();
                    }
                } else {
                    nval = nval * 10f + (ch - '0');
                    nextChar();
                }
            }
            if (ch == 'e' || ch == 'E') {
                float negativeExp = 10f;
                int expVal = 0;
                nextChar();
                if (ch == '-') {
                    negativeExp = 0.1f;
                    nextChar();
                } else if (ch == '+')
                    nextChar();
                while (Character.isDigit(ch)) {
                    expVal = expVal * 10 + ch - '0';
                    nextChar();
                }
                for (int i = 0; i < expVal; i++)
                    exp *= negativeExp;
            }
            nval = negative * nval * exp;
            token = StreamTokenizer.TT_NUMBER;
            return token;
        }
        if (Character.isJavaIdentifierStart(ch)) {
            bufferReset();
            bufferAdd();
            while (Character.isJavaIdentifierPart(ch))
                bufferAdd();
            sval = bufferToString();
            token = StreamTokenizer.TT_WORD;
            return token;
        }
        if (ch == '"') {
            bufferReset();
            nextChar();
            while (ch != '"')
                bufferAdd();
            sval = bufferToString();
            nextChar();
            token = '"';
            return token;
        }
        token = ch;
        nextChar();
        return token;
    }

    public void pushBack() {
        pushedBack = true;
    }

    public int lineno() {
        return lineno;
    }

}
