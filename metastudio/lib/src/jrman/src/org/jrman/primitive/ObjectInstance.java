/*
  ObjectInstance.java
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

package org.jrman.primitive;

import org.jrman.attributes.Attributes;
import org.jrman.geom.BoundingVolume;
import org.jrman.geom.Transform;
import org.jrman.parser.ObjectInstanceList;
import org.jrman.render.ShaderVariables;

public class ObjectInstance extends Primitive {
    
    private ObjectInstanceList objectInstanceList;

    public ObjectInstance(ObjectInstanceList oil, Attributes attributes) {
        super(null, attributes);
        objectInstanceList = oil;
    }

    public BoundingVolume getBoundingVolume() {
        return objectInstanceList.getBoundingVolume();
    }

    public Primitive[] split() {
        return objectInstanceList.createPrimitives(attributes);
    }

    public void dice(ShaderVariables shaderVariables, Transform worldToCamera) {
        throw new RuntimeException("Tried to dice an object instance!");
    }

    public boolean isReadyToBeDiced(int gridSize) {
        return false;
    }

}
