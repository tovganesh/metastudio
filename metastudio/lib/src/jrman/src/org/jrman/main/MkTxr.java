/*
 MkTxr.java
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

package org.jrman.main;

import org.jrman.maps.MipMap;

public class MkTxr {

    public static void main(String[] args) {
        if (args.length != 7) {
            System.err.println("usage: mktxr imageFile textureFile sWrap tWrap filter sWidth tWidth");
            System.exit(1);
        }
        try {
            MipMap.makeMipMap(
                args[0],
                args[1],
                MipMap.Mode.getNamed(args[2]),
                MipMap.Mode.getNamed(args[3]),
                args[4],
                Float.parseFloat(args[5]),
                Float.parseFloat(args[6]));
        } catch (Exception e) {
            System.err.println("Cannot create texture file");
        }
    }

}
