/*
 World.java
 Copyright (C) 2003 Gerardo Horvilleur
 
  This program is free software; you can redistribute it and/or
  modify it under the terms of the GNU General Public License
  as published by the Free Software Foundation; either version 2
  of the License, or (at your option) any latre version.
  
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILIY or FITNESS FOR A PARTICULAR PURPOSE. See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc. 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
*/

package org.jrman.parser;

import java.util.HashMap;
import java.util.Map;

import org.jrman.shaders.LightShader;

public class World {

    private Map<Integer, LightShader> lights;

    public World() {
        lights = new HashMap<Integer, LightShader>();
    }
    
    public World(World other) {
        lights = new HashMap<Integer, LightShader>(other.lights);
    }

    public void addLight(int sequenceNumber, LightShader light) {
        lights.put(new Integer(sequenceNumber), light);
    }

    public LightShader getLight(int sequenceNumber) {
        LightShader light = (LightShader) lights.get(new Integer(sequenceNumber));
        if (light == null)
            throw new IllegalArgumentException("no such light: " + sequenceNumber);
        return light;
    }

}
