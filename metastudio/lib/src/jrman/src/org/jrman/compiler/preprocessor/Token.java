/*
Token.java
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

class Token {
    
    final static Token SPACE = new Token(Type.SPACE, " ");
    
    final static Token NEW_LINE = new Token(Type.NEW_LINE, "\n");
    
    final private Type type;
    
    final private String image;
    
    Token(Type type, String image) {
        this.type = type;
        this.image = image;
    }
    
    Token(Type type, char image) {
        this(type, "" + image);
    }
    
    Type getType() {
        return type;
    }
    
    String getImage() {
        return image;
    }
    
    public String toString() {
        return type.toString() + ": " + image;
    }
    
    static class Type {
        
        final static Type IDENTIFIER = new Type("IDENTIFIER");
        
        final static Type OPERATOR = new Type("OPERATOR");
        
        final static Type NUMBER_LITERAL = new Type("NUMBER");
        
        final static Type STRING_LITERAL = new Type("STRING_LITERAL");
        
        final static Type ANGLES_STRING_LITERAL =
            new Type("ANGLES_STRING_LITERAL");
        
        final static Type SPACE = new Type("SPACE");
        
        final static Type NEW_LINE = new Type("NEW_LINE");
        
        final private String name;
        
        private Type(String name) {
            this.name = name;
        }
        
        public String toString() {
            return name;
        }
    }

}
