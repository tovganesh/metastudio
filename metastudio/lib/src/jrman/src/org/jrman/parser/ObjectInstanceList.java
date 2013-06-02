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

package org.jrman.parser;

import java.util.ArrayList;
import java.util.List;

import org.jrman.attributes.Attributes;
import org.jrman.attributes.MutableAttributes;
import org.jrman.geom.BoundingVolume;
import org.jrman.geom.MutableBounds3f;
import org.jrman.geom.Transform;
import org.jrman.primitive.Primitive;

public class ObjectInstanceList {
    
    private List<PrimitiveCreator> primitiveCreators
                       = new ArrayList<PrimitiveCreator>();
    
    private BoundingVolume boundingVolume;
    
    public static abstract class PrimitiveCreator {
        
        public abstract Primitive create(Attributes attributes);
        
        protected Attributes createAttributes(Transform transform, Attributes attributes) {
            MutableAttributes ma = new MutableAttributes(attributes);
            ma.setTransform(ma.getTransform().concat(transform));
            return ma;
        }
        
    }
    
    public void addPrimitiveCreator(PrimitiveCreator pc) {
        primitiveCreators.add(pc);
    }
    
    public Primitive[] createPrimitives(Attributes attributes) {
        Primitive[] result = new Primitive[primitiveCreators.size()];
        for (int i = 0; i < primitiveCreators.size(); i++)
            result[i] = ((PrimitiveCreator) primitiveCreators.get(i)).create(attributes);
       return result; 
    }
    
    public BoundingVolume getBoundingVolume() {
        if (boundingVolume == null) {
            MutableBounds3f mb = new MutableBounds3f();
            Primitive[] primitives = createPrimitives(new MutableAttributes());
            for (int i = 0; i < primitives.length; i++) {
                BoundingVolume bv = primitives[i].getBoundingVolume();
                bv = bv.transform(primitives[i].getAttributes().getTransform());
                mb.addBoundingVolume(bv);
            }
            boundingVolume = mb;
        }
        return boundingVolume;
    }
    
}
