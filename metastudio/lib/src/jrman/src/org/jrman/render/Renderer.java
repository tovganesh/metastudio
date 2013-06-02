/*
 Renderer.java
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

package org.jrman.render;

import org.jrman.options.Hider;
import org.jrman.parser.Frame;
import org.jrman.parser.Parser;
import org.jrman.parser.World;
import org.jrman.primitive.Primitive;

public abstract class Renderer {
    
    protected Frame frame;
    
    protected World world;
    
    protected Parser parser;
    
    public static Renderer createRenderer(Frame frame, World world, Parser parser) {
        Renderer renderer = null;
        Hider hider = frame.getHider();
        if (hider == Hider.HIDDEN)
            renderer = new RendererHidden();
        else if (hider == Hider.NULL)
            renderer = new RendererNull();
        else if (hider == Hider.BOUNDING_BOX)
            renderer = new RendererBoundingBox();
        renderer.init(frame, world, parser);
        return renderer;
    }
    
    public abstract void addPrimitive(Primitive primitive);
    
    public abstract void render();

    protected void init(Frame frame, World world, Parser parser) {
        this.frame = frame;
        this.world = world;
        this.parser = parser;
    }

}
