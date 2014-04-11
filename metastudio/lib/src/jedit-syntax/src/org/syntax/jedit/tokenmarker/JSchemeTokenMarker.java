/*
 * JSchemeTokenMarker.java
 *
 * Created on June 27, 2004, 2:44 PM
 */

package org.syntax.jedit.tokenmarker;

import org.syntax.jedit.*;
import javax.swing.text.Segment;

/**
 * JScheme token marker
 * @author  V.Ganesh
 * @version 1.0
 */
public class JSchemeTokenMarker extends CTokenMarker {
    
    /** Creates a new instance of JSchemeTokenMarker */
    public JSchemeTokenMarker() {
        super(false, getKeywords());
    }
    
    public byte markTokensImpl(byte token, Segment line, int lineIndex) {
        char[] array = line.array;
        int offset = line.offset;
        lastOffset = offset;
        lastKeyword = offset;
        int length = line.count + offset;
        boolean backslash = false;
        
        loop: for(int i = offset; i < length; i++) {
            int i1 = (i+1);
            
            char c = array[i];
            if(c == '\\') {
                backslash = !backslash;
                continue;
            }
            
            switch(token) {
                case Token.NULL:
                    switch(c) {
                        case '#':
                            if(backslash)
                                backslash = false;                            
                            break;
                        case '"':
                            doKeyword(line,i,c);
                            if(backslash)
                                backslash = false;
                            else {
                                addToken(i - lastOffset,token);
                                token = Token.LITERAL1;
                                lastOffset = lastKeyword = i;
                            }
                            break;
                        case '\'':
                            doKeyword(line,i,c);
                            if(backslash)
                                backslash = false;
                            else {
                                addToken(i - lastOffset,token);
                                token = Token.LITERAL2;
                                lastOffset = lastKeyword = i;
                            }
                            break;
                        case ':':
                            if(lastKeyword == offset) {
                                if(doKeyword(line,i,c))
                                    break;
                                backslash = false;
                                addToken(i1 - lastOffset,Token.LABEL);
                                lastOffset = lastKeyword = i1;
                            }
                            else if(doKeyword(line,i,c))
                                break;
                            break;
                        case ';':
                            backslash = false;
                            doKeyword(line,i,c);
                            if(length - i > 1) {                                
                                addToken(i - lastOffset,token);
                                addToken(length - i,Token.COMMENT1);
                                lastOffset = lastKeyword = length;
                                break loop;
                            }
                            break;
                        default:
                            backslash = false;
                            if(!Character.isLetterOrDigit(c)
                            && c != '_')
                                doKeyword(line,i,c);
                            break;
                    }
                    break;
                case Token.COMMENT1:
                case Token.COMMENT2:
                    backslash = false;                    
                    i++;
                    addToken((i+1) - lastOffset,token);
                    token = Token.NULL;
                    lastOffset = lastKeyword = i+1;                        
                    break;
                case Token.LITERAL1:
                    if(backslash)
                        backslash = false;
                    else if(c == '"') {
                        addToken(i1 - lastOffset,token);
                        token = Token.NULL;
                        lastOffset = lastKeyword = i1;
                    }
                    break;
                case Token.LITERAL2:
                    if(backslash)
                        backslash = false;
                    else if(c == '\'') {
                        addToken(i1 - lastOffset,Token.LITERAL1);
                        token = Token.NULL;
                        lastOffset = lastKeyword = i1;
                    }
                    break;
                default:
                    throw new InternalError("Invalid state: "
                    + token);
            }
        }
        
        if(token == Token.NULL)
            doKeyword(line,length,'\0');
        
        switch(token) {
            case Token.LITERAL1:
            case Token.LITERAL2:
                addToken(length - lastOffset,Token.INVALID);
                token = Token.NULL;
                break;
            case Token.KEYWORD2:
                addToken(length - lastOffset,token);
                if(!backslash)
                    token = Token.NULL;
            default:
                addToken(length - lastOffset,token);
                break;
        }
        
        return token;
    }
    
    public static KeywordMap getKeywords() {
        if(jschemeKeywords == null) {
            jschemeKeywords = new KeywordMap(false);
            
            jschemeKeywords.add("class", Token.KEYWORD3);
            jschemeKeywords.add("method", Token.KEYWORD3);
            jschemeKeywords.add("new", Token.KEYWORD1);
            jschemeKeywords.add("define", Token.KEYWORD1);
            jschemeKeywords.add("set", Token.KEYWORD1);
            jschemeKeywords.add("cond", Token.KEYWORD1);
            jschemeKeywords.add("if", Token.KEYWORD1);
            jschemeKeywords.add("else", Token.KEYWORD1);
            jschemeKeywords.add("lambda", Token.KEYWORD3);
            jschemeKeywords.add("macro", Token.KEYWORD3);
            jschemeKeywords.add("write", Token.KEYWORD3);
        } // end if 
        
        return jschemeKeywords;
    }
    
    // private members
    private static KeywordMap jschemeKeywords;
} // end of class JSchemeTokenMarker
