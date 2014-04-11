/*
  ParameterList.java
  Copyright (C) 2003, 2006 Gerardo Horvilleur Martinez
  
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


public class ParameterList {

    private Parameter[] list;
    
    int count = 0;

    public ParameterList() {
        list = new Parameter[8];
    }
    
    public ParameterList(int size) {
        list = new Parameter[size];
    }

    public ParameterList(ParameterList pl) {
        list = new Parameter[pl.getParameterCount()];
        for (int i = 0; i < pl.getParameterCount(); i++)
            addParameter(pl.getParameter(i));
    }

    public ParameterList linearInterpolate(float min, float max) {
        ParameterList pl = new ParameterList(count);
        for (int i = 0; i < getParameterCount(); i++)
            pl.addParameter(getParameter(i).linearInterpolate(min, max));
        return pl;
    }

    public ParameterList bilinearInterpolate(float uMin, float uMax,
                                             float vMin, float vMax) {
        ParameterList pl = new ParameterList(count);
        for (int i = 0; i < getParameterCount(); i++)
            pl.addParameter(getParameter(i).bilinearInterpolate(uMin, uMax,
                                                                vMin, vMax));
        return pl;
    }

    public ParameterList selectValues(
        int[] uniformIndexes,
        int[] varyingIndexes,
        int[] vertexIndexes) {
        ParameterList pl = new ParameterList(count);
        for (int i = 0; i < getParameterCount(); i++) {
            Parameter p = getParameter(i);
            Declaration.StorageClass sclass =
                p.getDeclaration().getStorageClass();
            if (sclass.equals(Declaration.StorageClass.CONSTANT))
                pl.addParameter(p);
            else if (sclass.equals(Declaration.StorageClass.UNIFORM))
                pl.addParameter(p.selectValues(uniformIndexes));
            else if (sclass.equals(Declaration.StorageClass.VARYING))
                pl.addParameter(p.selectValues(varyingIndexes));
            else if (sclass.equals(Declaration.StorageClass.VERTEX))
                pl.addParameter(p.selectValues(vertexIndexes));
        }
        return pl;
    }

    public void addParameter(Parameter parameter) {
        if (count == list.length) {
            Parameter[] tmp = new Parameter[count * 2];
            System.arraycopy(list, 0, tmp, 0, count);
            list = tmp;
        }
        list[count++] = parameter;
    }

    public Parameter getParameter(String name) {
        Parameter result = null;
        for (int i = count - 1; i >= 0; i--) {
            Parameter parameter = list[i];
            if (parameter.getDeclaration().getName() == name) {
                result = parameter;
                break;
            }
        }
        return result;
    }

    public int getParameterCount() {
        return count;
    }

    public Parameter getParameter(int index) {
        return list[index];
    }

    public void removeParameter(String name) {
        for (int i = 0; i < count; i++) {
            Parameter parameter = list[i];
            if (parameter.getDeclaration().getName() == name) {
                System.arraycopy(list, i + 1, list, i, count - i - 1);
                count--;
                break;
            }
        }
    }
    
    public String toString() {
        return list.toString();
    	}

}
