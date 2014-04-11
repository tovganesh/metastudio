/*
Tokenizer.java
Copyright (C) 2005 Gerardo Horvilleur Martinez

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

package org.jrman.compiler.preprocessor;

import java.io.IOException;
import java.io.Reader;

public class Tokenizer {
    
    private final static int STATE_DEFAULT = 1;
    
    private final static int STATE_AFTER_SHARP = 2;
    
    private final static int STATE_AFTER_INCLUDE = 3;
    
    private Reader reader;
    
    private int state;
    
    private int ch;
    
    private int[] pushback = new int[16];
    
    private int pushbackLast = 0;
    
    private boolean insideStringLiteral = false;
    
    Tokenizer(Reader reader) throws IOException {
        this.reader = reader;
        state = STATE_DEFAULT;
        ch = getNextChar();
    }
    
    TokenList getTokens() throws IOException {
        TokenList tl = new TokenList();
        Token token;
        while ((token = getNextToken()) != null)
            tl.addToken(token);
        if (token != Token.NEW_LINE)
            tl.addToken(Token.NEW_LINE);
        return tl;
    }
    
    private int readChar() throws IOException {
        if (pushbackLast > 0)
            return pushback[pushbackLast--];
        return reader.read();
    }
    
    private void pushbackChar(int ch) {
        if (pushbackLast == pushback.length - 1) {
            int[] tmp = new int[pushback.length * 2];
            System.arraycopy(pushback, 0, tmp, 0, pushback.length);
            pushback = tmp;
        }
        pushback[++pushbackLast] = ch;
    }
     private int getNextCharNewLines() throws IOException {
         int ch = readChar();
         if (ch == '\r') {
             ch = readChar();
             if (ch != '\n')
                 pushbackChar(ch);
             return '\n';
         }
         return ch;
     }
     
     private int getNextChar() throws IOException {
         int ch = getNextCharNewLines();
         if (insideStringLiteral || ch != '\\')
             return ch;
         ch = getNextCharNewLines();
         if (ch == '\n')
             return getNextChar();
         if (!isSpace(ch)) {
             pushbackChar(ch);
             return '\\';
         }
         while (isSpace(ch))
             ch = getNextCharNewLines();
         if (ch == '\n')
             return getNextChar();
         pushbackChar(ch);
         pushbackChar(' ');
         return '\\';
     }
    
    private Token getNextToken() throws IOException {
        if (ch == -1)
            return null;
        if (ch == '\n')
            return Token.NEW_LINE;
        if (isSpace(ch)) {
            while (isSpace(ch))
                ch = getNextChar();
            return Token.SPACE;
        }
        if (ch == '/') {
            ch = getNextChar();
            if (ch != '*' && ch != '/')
                return new Token(Token.Type.OPERATOR, "/");
            if (ch == '*') {
                while (true) {
                    while ((ch = getNextChar()) != '*')
                        if (ch == -1)
                            throw new UnexpectedEndOfFileException("inside /* comment");
                    ch = getNextChar();
                    if (ch == '/') {
                        ch = getNextChar();
                        return Token.SPACE;
                    }
                }
            } else {
                while ((ch = getNextChar()) != '\n')
                    if (ch == -1)
                        return null;
                ch = getNextChar();
                return Token.SPACE;
            }
        }
        return null;
    }
    
    private boolean isSpace(int ch) {
        return ch == ' ' || ch == '\t';
    }

}
