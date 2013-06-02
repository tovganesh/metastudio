/*
 MipMap.java
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

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.jrman.options.Filter;
import org.jrman.util.Calc;
import org.jrman.util.SamplesFilter;

public class MipMap {

    private static float[] d00 = new float[4];

    private static float[] d10 = new float[4];

    private static float[] d01 = new float[4];

    private static float[] d11 = new float[4];

    private static float[] ll = new float[4];

    private static float[] lh = new float[4];

    private static Map<String, MipMap> map = new HashMap<String, MipMap>();
    
    private static String currentDirectory;

    private ByteBuffer data;

    private int size;

    private int levels;

    private int[] levelOffsets;

    private int[] levelSizes;

    private Mode sMode;

    private Mode tMode;

    public static class Mode {

        public final static Mode BLACK = new Mode(1, "black");

        public final static Mode CLAMP = new Mode(2, "clamp");

        public final static Mode PERIODIC = new Mode(3, "periodic");

        private int code;

        private String name;

        private static Map<String, Mode> map = new HashMap<String, Mode>();

        static {
            map.put("black", BLACK);
            map.put("clamp", CLAMP);
            map.put("periodic", PERIODIC);
        }

        public static Mode getNamed(String name) {
            Mode result = (Mode) map.get(name);
            if (result == null)
                throw new IllegalArgumentException("No such MipMap mode: " + name);
            return result;
        }

        public static Mode getMode(int code) {
            if (code == 1)
                return BLACK;
            if (code == 2)
                return CLAMP;
            if (code == 3)
                return PERIODIC;
            throw new IllegalArgumentException("No such MipMap mode code: " + code);
        }

        public Mode(int code, String name) {
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

    private static byte[] scaleDown(
        byte[] data,
        int size,
        Mode sMode,
        Mode tMode,
        SamplesFilter samplesFilter) {
        byte[] result = new byte[data.length / 4];
        samplesFilter.doFilter(
            data,
            size,
            result,
            size / 2,
            size / 2,
            size / 2,
            2,
            2,
            sMode,
            tMode);
        return result;
    }

    public static void makeMipMap(
        BufferedImage image,
        String filename,
        Mode sMode,
        Mode tMode,
        Filter filter)
        throws IOException {
        int largerSide = Math.max(image.getWidth(), image.getHeight());
        int size = 1;
        while (size < largerSide)
            size *= 2;
        if (currentDirectory != null)
            filename = currentDirectory + File.separator + filename;
        FileOutputStream fos = new FileOutputStream(filename);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        DataOutputStream dos = new DataOutputStream(bos);
        dos.writeInt(size);
        dos.writeByte(sMode.getCode());
        dos.writeByte(tMode.getCode());
        AffineTransform af = new AffineTransform();
        af.scale((float) size / image.getWidth(), (float) size / image.getHeight());
        AffineTransformOp afop = new AffineTransformOp(af, AffineTransformOp.TYPE_BILINEAR);
        BufferedImage squareImage = afop.filter(image, null);
        image = null; // Release memory
        byte[] data = new byte[size * size * 4];
        for (int y = 0; y < size; y++)
            for (int x = 0; x < size; x++) {
                int offset = (y * size + x) * 4;
                int argb = squareImage.getRGB(x, y);
                data[offset] = (byte) (argb >> 16);
                data[offset + 1] = (byte) (argb >> 8);
                data[offset + 2] = (byte) argb;
                data[offset + 3] = (byte) (argb >> 24);
            }
        squareImage = null; // Release memory
        SamplesFilter samplesFilter = filter.getSamplesFilter();
        int width = Calc.ceil(filter.getHorizontalWidth() * 2);
        int height = Calc.ceil(filter.getVerticalWidth() * 2);
        samplesFilter.init(
            width,
            height,
            filter.getHorizontalWidth(),
            filter.getVerticalWidth());
        while (size >= 1) {
            dos.write(data);
            data = scaleDown(data, size, sMode, tMode, samplesFilter);
            size /= 2;
        }
        dos.close();
    }

    public static void makeMipMap(
        String imageFilename,
        String filename,
        Mode sMode,
        Mode tMode,
        String filterType,
        float sWidth,
        float tWidth)
        throws IOException {
        flushMipMap(filename);
        if (currentDirectory != null)
            imageFilename = currentDirectory + File.separator + imageFilename;
        File file = new File(imageFilename);
        BufferedImage image = ImageIO.read(file);
        makeMipMap(
            image,
            filename,
            sMode,
            tMode,
            new Filter(Filter.Type.getNamed(filterType), sWidth, tWidth));
    }

    public static MipMap getMipMap(String filename) {
        MipMap result = (MipMap) map.get(filename);
        if (result == null)
            try {
                result = new MipMap(filename);
                map.put(filename, result);
            } catch (Exception e) {
                System.err.println("Can't load mipmap: " + filename);
            }
        return result;
    }

    public static void flushMipMap(String filename) {
        map.remove(filename);
        System.gc(); // Hope the mapped file is released.....
    }

    private MipMap(String filename) throws IOException {
        if (currentDirectory != null)
            filename = currentDirectory + File.separator + filename;
        FileInputStream fis = new FileInputStream(filename);
        FileChannel fc = fis.getChannel();
        data = fc.map(FileChannel.MapMode.READ_ONLY, 0L, fc.size());
        size = data.getInt();
        sMode = Mode.getMode(data.get());
        tMode = Mode.getMode(data.get());
        levels = 1;
        int s = size;
        while (s > 1) {
            levels++;
            s /= 2;
        }
        levelOffsets = new int[levels];
        levelSizes = new int[levels];
        s = size;
        levelOffsets[0] = 6;
        levelSizes[0] = s;
        for (int i = 1; i < levels; i++) {
            levelOffsets[i] = levelOffsets[i - 1] + s * s * 4;
            s /= 2;
            levelSizes[i] = s;
        }
    }

    private float asFloat(int offset) {
        return (data.get(offset) & 0xff) / 255f;
    }

    private void getDataDirect(int s, int t, int level, int levelSize, float[] data) {
        int offset = levelOffsets[level];
        offset += (t * levelSize + s) * 4;
        data[0] = asFloat(offset++);
        data[1] = asFloat(offset++);
        data[2] = asFloat(offset++);
        data[3] = asFloat(offset);
    }

    private void getData(int s, int t, int level, int levelSize, float[] data) {
        if (s < 0 || s >= levelSize) {
            if (sMode == Mode.BLACK) {
                data[0] = data[1] = data[2] = data[3] = 0f;
                return;
            } else if (sMode == Mode.CLAMP)
                s = Calc.clamp(s, 0, levelSize - 1);
            else if (sMode == Mode.PERIODIC)
                s &= (levelSize - 1);
        }
        if (t < 0 || t >= levelSize) {
            if (tMode == Mode.BLACK) {
                data[0] = data[1] = data[2] = data[3] = 0f;
                return;
            } else if (tMode == Mode.CLAMP)
                t = Calc.clamp(t, 0, levelSize - 1);
            else if (tMode == Mode.PERIODIC)
                t &= (levelSize - 1);
        }
        getDataDirect(s, t, level, levelSize, data);
    }

    private void getData(float s, float t, int level, float[] data) {
        int levelSize = levelSizes[level];
        float rs = s * levelSize;
        int is = Calc.floor(rs);
        rs -= is;
        float rt = t * levelSize;
        int it = Calc.floor(rt);
        rt -= it;
        getData(is, it, level, levelSize, d00);
        getData(is + 1, it, level, levelSize, d10);
        getData(is, it + 1, level, levelSize, d01);
        getData(is + 1, it + 1, level, levelSize, d11);
        data[0] = Calc.interpolate(d00[0], d10[0], d01[0], d11[0], rs, rt);
        data[1] = Calc.interpolate(d00[1], d10[1], d01[1], d11[1], rs, rt);
        data[2] = Calc.interpolate(d00[2], d10[2], d01[2], d11[2], rs, rt);
        data[3] = Calc.interpolate(d00[3], d10[3], d01[3], d11[3], rs, rt);
    }

    private void getData(float s, float t, int level, float interp, float[] data) {
        getData(s, t, level, ll);
        getData(s, t, level + 1, lh);
        data[0] = Calc.interpolate(ll[0], lh[0], interp);
        data[1] = Calc.interpolate(ll[1], lh[1], interp);
        data[2] = Calc.interpolate(ll[2], lh[2], interp);
        data[3] = Calc.interpolate(ll[3], lh[3], interp);
    }

    public void getData(float s, float t, float area, float[] data) {
        float d = (float) Math.sqrt(area);
        d = Math.min(d, 1f);
        int id = Calc.floor(size * d);
        boolean singleLevel = (id == 0 || id == size);
        int level = 0;
        while (id > 1) {
            level++;
            id /= 2;
        }
        if (singleLevel)
            getData(s, t, level, data);
        else {
            float a = 1f / levelSizes[level];
            float b = 1f / levelSizes[level + 1];
            float interp = (d - a) / (b - a);
            getData(s, t, level, interp, data);
        }
    }

}
