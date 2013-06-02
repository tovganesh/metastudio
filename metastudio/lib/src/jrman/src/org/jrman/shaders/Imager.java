/*
 Imager.java
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

package org.jrman.shaders;

import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import org.jrman.parameters.ParameterList;
import org.jrman.parameters.UniformScalarTuple3f;

public class Imager {
    
    protected String name;
    
    protected ParameterList parameters;

    protected Color3f bgColor;
    
    public static Imager createImager(String name, ParameterList parameters) {
        return new Imager(name, parameters); // Just for now...
    }
    
    protected Imager(String name, ParameterList parameters) {
        this.name = name;
        this.parameters = parameters;

        bgColor = new Color3f();

        ((UniformScalarTuple3f) parameters.getParameter("bgcolor"))
                 .getValue(bgColor);
    }

    public void getBgColor(Color4f oc) {
        oc.set(bgColor.x, bgColor.y, bgColor.z, 1.0f);
    }
}
