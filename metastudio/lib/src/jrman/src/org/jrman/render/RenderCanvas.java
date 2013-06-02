/*
 RenderCanvas.java
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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JComponent;

public class RenderCanvas extends JComponent {
    
    private int hSize;
    
    private int vSize;
    
    private Image image;
    
    public RenderCanvas(ImageStore is) {
        this.hSize = is.getHSize();
        this.vSize = is.getVSize();
        setPreferredSize(new Dimension(hSize,vSize));
        image = is.getImage();
    }
    
    public void update(Graphics g) {
        paint(g);
    }
    
    public void paint(Graphics g) {
        g.drawImage(image, 0, 0, null);
    }
    
}
