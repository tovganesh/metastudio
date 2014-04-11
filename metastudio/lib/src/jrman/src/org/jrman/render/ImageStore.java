/*
 ImageStore.java
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

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import org.jrman.options.Display;

public class ImageStore {

    private int hSize;

    private int vSize;

    private BufferedImage image;

    private WritableRaster raster;

    private int[] pixel = new int[1];

    public ImageStore(int hSize, int vSize, Display display) {
        this.hSize = hSize;
        this.vSize = vSize;
        if (display.getMode() == Display.Mode.RGB)
            image = new BufferedImage(hSize, vSize, BufferedImage.TYPE_INT_RGB);
        else
            image = new BufferedImage(hSize, vSize, BufferedImage.TYPE_INT_ARGB);
        raster = image.getRaster();
    }

    public void setPixels(int[] pixels, int x, int y, int w, int h, int rowLength) {
        for (int row = 0; row < h; row++) {
            if (y + row < 0 || y + row >= vSize)
                continue;
            for (int col = 0; col < w; col++) {
                if (x + col < 0 || x + col >= hSize)
                    continue;
                pixel[0] = pixels[row * rowLength + col];
                /// image.setRGB(x + col, y + row, pixel);
                raster.setDataElements(x + col, y + row, pixel);
            }
        }
    }

    public BufferedImage getImage() {
        return image;
    }

    public int getHSize() {
        return hSize;
    }

    public int getVSize() {
        return vSize;
    }

}
