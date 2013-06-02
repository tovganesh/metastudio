/*
 ShadowMap.java
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

package org.jrman.maps;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Point3f;

import org.jrman.geom.AffineTransform;
import org.jrman.geom.PerspectiveTransform;
import org.jrman.geom.Transform;
import org.jrman.util.Rand;

public class ShadowMap {

    private static Map<String, ShadowMap> map = new HashMap<String, ShadowMap>();
    
    private static String currentDirectory;

    private FloatBuffer data;

    private int width;

    private int height;

    private Transform worldToRaster;

    public static class Projection {

        public final static Projection PERSPECTIVE = new Projection(1, "perspective");

        public final static Projection ORTHOGRAPHIC = new Projection(2, "orthographic");

        private int code;

        private String name;

        private static Map<String, Projection> map = new HashMap<String, Projection>();

        static {
            map.put("perspective", PERSPECTIVE);
            map.put("orthographic", ORTHOGRAPHIC);
        }

        public static Projection getNamed(String name) {
            Projection result = (Projection) map.get(name);
            if (result == null)
                throw new IllegalArgumentException("No such ShadowMap projection: " + name);
            return result;
        }

        public static Projection getProjection(int code) {
            if (code == 1)
                return PERSPECTIVE;
            if (code == 2)
                return ORTHOGRAPHIC;
            throw new IllegalArgumentException("No such ShadowMap projection code: " + code);
        }

        public Projection(int code, String name) {
            this.code = code;
            this.name = name;
        }

        public int getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        public String toString() {
            return name;
        }

    }
    
    public static void setCurrentDirectory(String currentDir) {
        currentDirectory = currentDir;
    }

    public static void writeShadowMap(
        String filename,
        Transform worldToCamera,
        Transform cameraToRaster,
        int width,
        int height,
        float[] depths)
        throws IOException {
        Transform worldToRaster = cameraToRaster.concat(worldToCamera);
        if (currentDirectory != null)
            filename = currentDirectory + File.separator + filename;
        FileOutputStream fos = new FileOutputStream(filename);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        DataOutputStream dos = new DataOutputStream(bos);
        if (worldToRaster.isPerspective())
            dos.writeByte(Projection.getNamed("perspective").getCode());
        else
            dos.writeByte(Projection.getNamed("orthographic").getCode());
        worldToRaster.writeToFile(dos);
        dos.writeInt(width);
        dos.writeInt(height);
        for (int i = 0; i < depths.length; i++)
            dos.writeFloat(depths[i]);
        dos.close();
    }

    public static ShadowMap getShadowMap(String filename) {
        ShadowMap result = (ShadowMap) map.get(filename);
        if (result == null)
            try {
                result = new ShadowMap(filename);
                map.put(filename, result);
            } catch (Exception e) {
                System.err.println("Can't load shadow map: " + filename);
            }
        return result;
    }

    public static void flushShadowMap(String filename) {
        map.remove(filename);
        System.gc(); // Hope the mapped file is released....
    }

    private ShadowMap(String filename) throws IOException {
        try {
            if (currentDirectory != null)
                filename = currentDirectory + File.separator + filename;
            FileInputStream fis = new FileInputStream(filename);
            DataInputStream dis = new DataInputStream(fis);
            Projection projection = Projection.getProjection(dis.readByte());
            if (projection == Projection.PERSPECTIVE)
                worldToRaster = PerspectiveTransform.readFromFile(dis);
            else
                worldToRaster = AffineTransform.readFromFile(dis);
            width = dis.readInt();
            height = dis.readInt();
            int offset = 1 + 16 * 4 + 4 + 4;
            if (projection == Projection.PERSPECTIVE)
                offset += 4 + 4;
            int size = width * height * 4;
            FileChannel fc = fis.getChannel();
            ByteBuffer bf = fc.map(FileChannel.MapMode.READ_ONLY, offset, size);
            data = bf.asFloatBuffer();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private boolean get(int x, int y, float dist) {
        float z = data.get(y * width + x);
        return (z < dist);
    }

    public Transform getWorldToRaster() {
        return worldToRaster;
    }

    public float get(
        Point3f p1,
        Point3f p2,
        float bias,
        int samples,
        float oneOverSamples,
        float blur,
        float halfBlur) {
        float total = 0f;
        float dx = p2.x - p1.x + blur;
        float sx = p1.x - halfBlur;
        float dy = p2.y - p1.y + blur;
        float sy = p1.y - halfBlur;
        if (sx + dx < 0f || sx >= width || sy + dy < 0f || sy >= width)
            return 0f;
        float p1zBias = p1.z - bias;
        for (int i = 0; i < samples; i++) {
            int x = (int) (dx * Rand.uniform() + sx);
            if (x < 0 || x >= width)
                continue;
            int y = (int) (dy * Rand.uniform() + sy);
            if (y < 0 || y >= height)
                continue;
            if (get(x, y, p1zBias))
                total += oneOverSamples;
        }
        return total;
    }

}
