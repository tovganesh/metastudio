/*
  Declaration.java
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

package org.jrman.parameters;

import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.jrman.parser.Tokenizer;

public class Declaration {

    private String name;

    private StorageClass storageClass;

    private Type type;

    private int count;

    public static class StorageClass {

        public static final StorageClass CONSTANT =
            new StorageClass("constant");

        public static final StorageClass UNIFORM = new StorageClass("uniform");

        public static final StorageClass VARYING = new StorageClass("varying");

        public static final StorageClass VERTEX = new StorageClass("vertex");

        private final static Map<String, StorageClass> map
                                    = new HashMap<String, StorageClass>();

        static {
            map.put("constant", CONSTANT);
            map.put("uniform", UNIFORM);
            map.put("varying", VARYING);
            map.put("vertex", VERTEX);
        }

        private String name;

        private StorageClass(String name) {
            this.name = name;
        }

        public static StorageClass getNamed(String name) {
            StorageClass result = (StorageClass) map.get(name);
            if (result == null)
                throw new IllegalArgumentException("No such storage class: " +
                                                   name);
            return result;
        }

        public static boolean isStorageClassName(String name) {
            return map.containsKey(name);
        }

        public String toString() {
            return name;
        }

    }

    public static class Type {

        public static final Type FLOAT = new Type("float");

        public static final Type INTEGER = new Type("integer");

        public static final Type STRING = new Type("string");

        public static final Type COLOR = new Type("color");

        public static final Type POINT = new Type("point");

        public static final Type VECTOR = new Type("vector");

        public static final Type NORMAL = new Type("normal");

        public static final Type MATRIX = new Type("matrix");

        public static final Type HPOINT = new Type("hpoint");

        private final static Map<String, Type> map = new HashMap<String, Type>();

        static {
            map.put("float", FLOAT);
            map.put("integer", INTEGER);
            map.put("string", STRING);
            map.put("color", COLOR);
            map.put("point", POINT);
            map.put("vector", VECTOR);
            map.put("normal", NORMAL);
            map.put("matrix", MATRIX);
            map.put("hpoint", HPOINT);
        }

        private String name;

        private Type(String name) {
            this.name = name;
        }

        public static Type getNamed(String name) {
            Type result = (Type) map.get(name);
            if (result == null)
                throw new IllegalArgumentException("No such data type: " +
                                                   name);
            return result;
        }

        public String toString() {
            return name;
        }

    }

    public Declaration(String name, StorageClass storageClass, Type type,
                       int count) {
        this.name = name.intern();
        this.storageClass = storageClass;
        this.type = type;
        this.count = count;
    }

    public Declaration(String name, String decl) {
        this.name = name.intern();
        count = 1;
        try {
            Tokenizer st = new Tokenizer(new StringReader(decl));
            if (st.nextToken() != StreamTokenizer.TT_WORD)
                throw new Exception("expected storage class");
            if (StorageClass.isStorageClassName(st.sval))
                storageClass = StorageClass.getNamed(st.sval);
            else {
                st.pushBack();
                storageClass = StorageClass.UNIFORM;
            }
            if (st.nextToken() != StreamTokenizer.TT_WORD)
                throw new Exception("Expected type");
            type = Type.getNamed(st.sval);
            int token = st.nextToken();
            if (token == '[') {
                if (st.nextToken() != StreamTokenizer.TT_NUMBER)
                    throw new Exception("Expected array size");
                count = (int) st.nval;
                token = st.nextToken();
                if (token != ']')
                    throw new Exception("expected ]");
                token = st.nextToken();
            }
            if (token != StreamTokenizer.TT_EOF)
                throw new Exception("Extra data at end of declaration");
        } catch (Exception e) {
            throw new IllegalArgumentException(e.toString());
        }
    }

    public String getName() {
        return name;
    }

    public StorageClass getStorageClass() {
        return storageClass;
    }

    public Type getType() {
        return type;
    }

    public int getCount() {
        return count;
    }

    public boolean equals(Object other) {
        if (other == null)
            return false;
        if (!(other instanceof Declaration))
            return false;
        Declaration d = (Declaration) other;
        return (
            d.name == name
                && d.storageClass == storageClass
                && d.type == type
                && d.count == count);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(name).append(" ");
        sb.append(storageClass).append(" ");
        sb.append(type);
        if (count > 1)
            sb.append(" [").append(count).append("]");
        return sb.toString();
    }

    private float[] getFloats(float[] numbers, int arraySize) {
        float[] result = new float[arraySize];
        System.arraycopy(numbers, 0, result, 0, arraySize);
        return result;
    }

    private int[] getInts(float[] numbers, int arraySize) {
        int[] result = new int[arraySize];
        for (int i = 0; i < arraySize; i++)
            result[i] = (int) numbers[i];
        return result;
    }

    private String[] getStrings(String[] strings, int arraySize) {
        String[] result = new String[arraySize];
        System.arraycopy(strings, 0, result, 0, arraySize);
        return result;
    }

    public Parameter buildParameter(float[] numbers, String[] strings,
                                    int arraySize) {
        if (type == Type.FLOAT) {
            if (storageClass == StorageClass.CONSTANT) {
                if (count == 1)
                    return new UniformScalarFloat(this, numbers[0]);
                else
                    return new UniformArrayFloat(this,
                                                 getFloats(numbers,
                                                           arraySize));
            }
            if (storageClass == StorageClass.UNIFORM) {
                if (count == 1) {
                    if (arraySize == 1)
                        return new UniformScalarFloat(this, numbers[0]);
                    else
                        return new VaryingScalarFloat(this,
                                                      getFloats(numbers,
                                                                arraySize));
                } else {
                    if (arraySize == count)
                        return new UniformArrayFloat(this,
                                                     getFloats(numbers,
                                                               arraySize));
                    else
                        return new VaryingArrayFloat(this,
                                                     getFloats(numbers,
                                                               arraySize));
                }
            } else {
                if (count == 1)
                    return new VaryingScalarFloat(this,
                                                  getFloats(numbers,
                                                            arraySize));
                else
                    return new VaryingArrayFloat(this,
                                                 getFloats(numbers, arraySize));
            }
        } else if (type == Type.INTEGER) {
            if (storageClass == StorageClass.CONSTANT) {
                if (count == 1)
                    return new UniformScalarInteger(this, (int) numbers[0]);
                else
                    return new UniformArrayInteger(this,
                                                   getInts(numbers, arraySize));
            } else if (storageClass == StorageClass.UNIFORM) {
                if (arraySize == 1)
                    return new UniformScalarInteger(this, (int) numbers[0]);
                else
                    return new UniformArrayInteger(this,
                                                   getInts(numbers, arraySize));
            } else
                throw new IllegalArgumentException(
                                       "Can't handle varying integer: " + this);
        } else if (type == Type.STRING) {
            if (storageClass == StorageClass.CONSTANT) {
                if (count == 1)
                    return new UniformScalarString(this, strings[0]);
                else
                    return new UniformArrayString(this,
                                                  getStrings(strings,
                                                             arraySize));
            } else if (storageClass == StorageClass.UNIFORM) {
                if (arraySize == 1)
                    return new UniformScalarString(this, strings[0]);
                else
                    return new UniformArrayString(this, getStrings(strings,
                                                                   arraySize));
            } else
                throw new IllegalArgumentException(
                                        "Can't handle varying string: " + this);
        } else if (
            type == Type.COLOR
                || type == Type.POINT
                || type == Type.VECTOR
                || type == Type.NORMAL) {
            if (storageClass == StorageClass.CONSTANT) {
                if (count == 1)
                    return new UniformScalarTuple3f(this, numbers[0],
                                                    numbers[1], numbers[2]);
                else
                    return new UniformArrayTuple3f(this,
                                                   getFloats(numbers,
                                                             arraySize));
            } else if (storageClass == StorageClass.UNIFORM) {
                if (count == 1) {
                    if (arraySize == 3)
                        return new UniformScalarTuple3f(
                            this,
                            numbers[0],
                            numbers[1],
                            numbers[2]);
                    else
                        return new VaryingScalarTuple3f(this,
                                                        getFloats(numbers,
                                                                  arraySize));
                } else {
                    if (arraySize == count * 3)
                        return new UniformArrayTuple3f(this,
                                                       getFloats(numbers,
                                                                 arraySize));
                    else
                        return new VaryingArrayTuple3f(this,
                                                       getFloats(numbers,
                                                                 arraySize));
                }
            } else {
                if (count == 1)
                    return new VaryingScalarTuple3f(this,
                                                    getFloats(numbers,
                                                              arraySize));
                else
                    return new VaryingArrayTuple3f(this,
                                                   getFloats(numbers,
                                                             arraySize));
            }
        } else if (type == Type.HPOINT) {
            if (storageClass == StorageClass.CONSTANT) {
                if (count == 1)
                    return new UniformScalarHPoint(
                        this,
                        numbers[0],
                        numbers[1],
                        numbers[2],
                        numbers[3]);
                else
                    return new UniformArrayHPoint(this,
                                                  getFloats(numbers,
                                                            arraySize));
            } else if (storageClass == StorageClass.UNIFORM) {
                if (count == 1) {
                    if (arraySize == 4)
                        return new UniformScalarHPoint(
                            this,
                            numbers[0],
                            numbers[1],
                            numbers[2],
                            numbers[3]);
                    else
                        return new VaryingScalarHPoint(this,
                                                       getFloats(numbers,
                                                                 arraySize));
                } else {
                    if (arraySize == count * 4)
                        return new UniformArrayHPoint(this,
                                                      getFloats(numbers,
                                                                arraySize));
                    else
                        return new VaryingArrayHPoint(this,
                                                      getFloats(numbers,
                                                                arraySize));
                }
            } else {
                if (count == 1)
                    return new VaryingScalarHPoint(this,
                                                   getFloats(numbers,
                                                             arraySize));
                else
                    return new VaryingArrayHPoint(this,
                                                  getFloats(numbers,
                                                            arraySize));
            }
        }
        throw new IllegalArgumentException("Unknown type");
    }

}
