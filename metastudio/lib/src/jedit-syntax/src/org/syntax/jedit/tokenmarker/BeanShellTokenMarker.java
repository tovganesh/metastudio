/*
 * BeanShellTokenMarker.java - Java token marker
 * Copyright (C) 2004 V.Ganesh
 *
 * You may use and modify this package for any purpose. Redistribution is
 * permitted, in both source and binary form, provided that this notice
 * remains intact in all source distributions of this package.
 */

package org.syntax.jedit.tokenmarker;

import org.syntax.jedit.*;
import javax.swing.text.Segment;

/**
 * Bean shell token marker.
 *
 * @author V.Ganesh
 * @version 1.0
 */
public class BeanShellTokenMarker extends CTokenMarker {
    
    public BeanShellTokenMarker() {
        super(false, getKeywords());
    }
    
    public static KeywordMap getKeywords() {
        if(beanShellKeywords == null) {
            beanShellKeywords = new KeywordMap(false);
            beanShellKeywords.add("package",Token.KEYWORD2);
            beanShellKeywords.add("import",Token.KEYWORD2);
            beanShellKeywords.add("byte",Token.KEYWORD3);
            beanShellKeywords.add("char",Token.KEYWORD3);
            beanShellKeywords.add("short",Token.KEYWORD3);
            beanShellKeywords.add("int",Token.KEYWORD3);
            beanShellKeywords.add("long",Token.KEYWORD3);
            beanShellKeywords.add("float",Token.KEYWORD3);
            beanShellKeywords.add("double",Token.KEYWORD3);
            beanShellKeywords.add("boolean",Token.KEYWORD3);
            beanShellKeywords.add("void",Token.KEYWORD3);
            beanShellKeywords.add("class",Token.KEYWORD3);
            beanShellKeywords.add("interface",Token.KEYWORD3);
            beanShellKeywords.add("abstract",Token.KEYWORD1);
            beanShellKeywords.add("final",Token.KEYWORD1);
            beanShellKeywords.add("private",Token.KEYWORD1);
            beanShellKeywords.add("protected",Token.KEYWORD1);
            beanShellKeywords.add("public",Token.KEYWORD1);
            beanShellKeywords.add("static",Token.KEYWORD1);
            beanShellKeywords.add("synchronized",Token.KEYWORD1);
            beanShellKeywords.add("native",Token.KEYWORD1);
            beanShellKeywords.add("volatile",Token.KEYWORD1);
            beanShellKeywords.add("transient",Token.KEYWORD1);
            beanShellKeywords.add("break",Token.KEYWORD1);
            beanShellKeywords.add("case",Token.KEYWORD1);
            beanShellKeywords.add("continue",Token.KEYWORD1);
            beanShellKeywords.add("default",Token.KEYWORD1);
            beanShellKeywords.add("do",Token.KEYWORD1);
            beanShellKeywords.add("else",Token.KEYWORD1);
            beanShellKeywords.add("for",Token.KEYWORD1);
            beanShellKeywords.add("if",Token.KEYWORD1);
            beanShellKeywords.add("instanceof",Token.KEYWORD1);
            beanShellKeywords.add("new",Token.KEYWORD1);
            beanShellKeywords.add("return",Token.KEYWORD1);
            beanShellKeywords.add("switch",Token.KEYWORD1);
            beanShellKeywords.add("while",Token.KEYWORD1);
            beanShellKeywords.add("throw",Token.KEYWORD1);
            beanShellKeywords.add("try",Token.KEYWORD1);
            beanShellKeywords.add("catch",Token.KEYWORD1);
            beanShellKeywords.add("extends",Token.KEYWORD1);
            beanShellKeywords.add("finally",Token.KEYWORD1);
            beanShellKeywords.add("implements",Token.KEYWORD1);
            beanShellKeywords.add("throws",Token.KEYWORD1);
            beanShellKeywords.add("this",Token.LITERAL2);
            beanShellKeywords.add("null",Token.LITERAL2);
            beanShellKeywords.add("super",Token.LITERAL2);
            beanShellKeywords.add("true",Token.LITERAL2);
            beanShellKeywords.add("false",Token.LITERAL2);
        }
        return beanShellKeywords;
    }
    
    // private members
    private static KeywordMap beanShellKeywords;
}
