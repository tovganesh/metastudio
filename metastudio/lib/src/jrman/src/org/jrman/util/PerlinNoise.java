/*
 PerlinNoise.java Copyright (C) 2003 Alessandro Falappa

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
package org.jrman.util;


/**
 * A multidimensional Perlin noise and cell noise generator class.
 * <p>
 * The generator produces float numbers in the range [-1;1] given one, two or
 * three arguments. The noise function is C1 and C2 continuous everywhere and
 * assumes a zero value at integer arguments. 
 * <p>
 * This class can also provide a constant noise over integer cells. Generation
 * of values is as that of Perlin noise without final smooth interpolation
 * between values at the grid corners.  
 * <p>
 * The algorithm is outlined in the Siggraph 2002 paper by <i>Ken Perlin
 * &quot; Improving Noise&quot;</i>. The code in this class is based on the
 * reference implementation found at http://mrl.nyu.edu/~perlin/noise/.
 * 
 * @author Alessandro Falappa
 */
public final class PerlinNoise {
	static public float noise1d(float x) {
		int X= Calc.floor(x) & 255;
		x -= Calc.floor(x);
		float u= fade(x);
		return Calc.interpolate(
			grad(p[p[p[X]]], x, 0, 0),
			grad(p[p[p[X + 1]]], x - 1, 0, 0),
			u);
	}

	static public float noise2d(float x, float y) {
		int X= Calc.floor(x) & 255;
		int Y= Calc.floor(y) & 255;
		x -= Calc.floor(x);
		y -= Calc.floor(y);
		float u= fade(x);
		float v= fade(y);
		int A= p[X] + Y;
		int AA= p[A];
		int AB= p[A + 1];
		A= p[X + 1] + Y;
		int BA= p[A];
		int BB= p[A + 1];
		float tmp1=
			Calc.interpolate(grad(p[AA], x, y, 0), grad(p[BA], x - 1, y, 0), u);
		float tmp2=
			Calc.interpolate(
				grad(p[AB], x, y - 1, 0),
				grad(p[BB], x - 1, y - 1, 0),
				u);
		return Calc.interpolate(tmp1, tmp2, v);
	}

	static public float noise3d(float x, float y, float z) {
		int X= Calc.floor(x) & 255;
		int Y= Calc.floor(y) & 255;
		int Z= Calc.floor(z) & 255;
		x -= Calc.floor(x);
		y -= Calc.floor(y);
		z -= Calc.floor(z);
		float u= fade(x);
		float v= fade(y);
		float w= fade(z);
		int A= p[X] + Y;
		int AA= p[A] + Z;
		int AB= p[A + 1] + Z;
		A= p[X + 1] + Y;
		int BA= p[A] + Z;
		int BB= p[A + 1] + Z;
		float tmp1=
			Calc.interpolate(grad(p[AA], x, y, z), grad(p[BA], x - 1, y, z), u);
		float tmp2=
			Calc.interpolate(
				grad(p[AB], x, y - 1, z),
				grad(p[BB], x - 1, y - 1, z),
				u);
		float tmp3=
			Calc.interpolate(
				grad(p[AA + 1], x, y, z - 1),
				grad(p[BA + 1], x - 1, y, z - 1),
				u);
		float tmp4=
			Calc.interpolate(
				grad(p[AB + 1], x, y - 1, z - 1),
				grad(p[BB + 1], x - 1, y - 1, z - 1),
				u);
		return Calc.interpolate(
			Calc.interpolate(tmp1, tmp2, v),
			Calc.interpolate(tmp3, tmp4, v),
			w);
	}

	static public float noise4d(float x, float y, float z, float w) {
		int X= Calc.floor(x) & 255;
		int Y= Calc.floor(y) & 255;
		int Z= Calc.floor(z) & 255;
		int W= Calc.floor(w) & 255;
		x -= Calc.floor(x);
		y -= Calc.floor(y);
		z -= Calc.floor(z);
		w -= Calc.floor(w);
		float a= fade(x);
		float b= fade(y);
		float c= fade(z);
		float d= fade(w);
		int A= p[X] + Y,
			AA= p[A] + Z,
			AB= p[A + 1] + Z,
			B= p[X + 1] + Y,
			BA= p[B] + Z,
			BB= p[B + 1] + Z,
			AAA= p[AA] + W,
			AAB= p[AA + 1] + W,
			ABA= p[AB] + W,
			ABB= p[AB + 1] + W,
			BAA= p[BA] + W,
			BAB= p[BA + 1] + W,
			BBA= p[BB] + W,
			BBB= p[BB + 1] + W;
		return Calc.interpolate(
			Calc.interpolate(
				Calc.interpolate(
					Calc.interpolate(
						grad4d(p[AAA], x, y, z, w),
						grad4d(p[BAA], x - 1, y, z, w),
						a),
					Calc.interpolate(
						grad4d(p[ABA], x, y - 1, z, w),
						grad4d(p[BBA], x - 1, y - 1, z, w),
						a),
					b),
				Calc.interpolate(
					Calc.interpolate(
						grad4d(p[AAB], x, y, z - 1, w),
						grad4d(p[BAB], x - 1, y, z - 1, w),
						a),
					Calc.interpolate(
						grad4d(p[ABB], x, y - 1, z - 1, w),
						grad4d(p[BBB], x - 1, y - 1, z - 1, w),
						a),
					b),
				c),
			Calc.interpolate(
				Calc.interpolate(
					Calc.interpolate(
						grad4d(p[AAA + 1], x, y, z, w - 1),
						grad4d(p[BAA + 1], x - 1, y, z, w - 1),
						a),
					Calc.interpolate(
						grad4d(p[ABA + 1], x, y - 1, z, w - 1),
						grad4d(p[BBA + 1], x - 1, y - 1, z, w - 1),
						a),
					b),
				Calc.interpolate(
					Calc.interpolate(
						grad4d(p[AAB + 1], x, y, z - 1, w - 1),
						grad4d(p[BAB + 1], x - 1, y, z - 1, w - 1),
						a),
					Calc.interpolate(
						grad4d(p[ABB + 1], x, y - 1, z - 1, w - 1),
						grad4d(p[BBB + 1], x - 1, y - 1, z - 1, w - 1),
						a),
					b),
				c),
			d);
	}
	
	public static float cellnoise1d(float x) {
		int X= Calc.floor(x) & 255;
		return p[X]/255f;
	}
	
	public static float cellnoise2d(float x, float y) {
		int X= Calc.floor(x) & 255;
		int Y= Calc.floor(y) & 255;
		return p[p[X]+Y]/255f;
	}
	
	public static float cellnoise3d(float x, float y, float z) {
		int X= Calc.floor(x) & 255;
		int Y= Calc.floor(y) & 255;
		int Z= Calc.floor(z) & 255;
		return p[p[p[X]+Y]+Z]/255f;
	}

	public static float cellnoise4d(float x, float y, float z, float w) {
		int X= Calc.floor(x) & 255;
		int Y= Calc.floor(y) & 255;
		int Z= Calc.floor(z) & 255;
		int W= Calc.floor(w) & 255;
		return p[p[p[p[X]+Y]+Z]+W]/255f;
	}
	
	private static float fade(float t) {
		return t * t * t * (t * (t * 6 - 15) + 10);
	}

	private static float grad(int hash, float x, float y, float z) {
		int h= hash & 15;
		float u= h < 8 ? x : y;
		float v= h < 4 ? y : (h == 12 || h == 14 ? x : z);
		return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
	}

	private static float grad4d(int hash, float x, float y, float z, float w) {
		int h= hash & 31;
		float a= y, b= z, c= w;
		switch (h >> 3) {
			case 1 :
				a= w;
				b= x;
				c= y;
				break;
			case 2 :
				a= z;
				b= w;
				c= x;
				break;
			case 3 :
				a= y;
				b= z;
				c= w;
				break;
		}
		return ((h & 4) == 0 ? -a : a)
			+ ((h & 2) == 0 ? -b : b)
			+ ((h & 1) == 0 ? -c : c);
	}

	private static final int p[]= new int[512];
	private static final int permutation[]=
		{
			151,
			160,
			137,
			91,
			90,
			15,
			131,
			13,
			201,
			95,
			96,
			53,
			194,
			233,
			7,
			225,
			140,
			36,
			103,
			30,
			69,
			142,
			8,
			99,
			37,
			240,
			21,
			10,
			23,
			190,
			6,
			148,
			247,
			120,
			234,
			75,
			0,
			26,
			197,
			62,
			94,
			252,
			219,
			203,
			117,
			35,
			11,
			32,
			57,
			177,
			33,
			88,
			237,
			149,
			56,
			87,
			174,
			20,
			125,
			136,
			171,
			168,
			68,
			175,
			74,
			165,
			71,
			134,
			139,
			48,
			27,
			166,
			77,
			146,
			158,
			231,
			83,
			111,
			229,
			122,
			60,
			211,
			133,
			230,
			220,
			105,
			92,
			41,
			55,
			46,
			245,
			40,
			244,
			102,
			143,
			54,
			65,
			25,
			63,
			161,
			1,
			216,
			80,
			73,
			209,
			76,
			132,
			187,
			208,
			89,
			18,
			169,
			200,
			196,
			135,
			130,
			116,
			188,
			159,
			86,
			164,
			100,
			109,
			198,
			173,
			186,
			3,
			64,
			52,
			217,
			226,
			250,
			124,
			123,
			5,
			202,
			38,
			147,
			118,
			126,
			255,
			82,
			85,
			212,
			207,
			206,
			59,
			227,
			47,
			16,
			58,
			17,
			182,
			189,
			28,
			42,
			223,
			183,
			170,
			213,
			119,
			248,
			152,
			2,
			44,
			154,
			163,
			70,
			221,
			153,
			101,
			155,
			167,
			43,
			172,
			9,
			129,
			22,
			39,
			253,
			19,
			98,
			108,
			110,
			79,
			113,
			224,
			232,
			178,
			185,
			112,
			104,
			218,
			246,
			97,
			228,
			251,
			34,
			242,
			193,
			238,
			210,
			144,
			12,
			191,
			179,
			162,
			241,
			81,
			51,
			145,
			235,
			249,
			14,
			239,
			107,
			49,
			192,
			214,
			31,
			181,
			199,
			106,
			157,
			184,
			84,
			204,
			176,
			115,
			121,
			50,
			45,
			127,
			4,
			150,
			254,
			138,
			236,
			205,
			93,
			222,
			114,
			67,
			29,
			24,
			72,
			243,
			141,
			128,
			195,
			78,
			66,
			215,
			61,
			156,
			180 };
	static {
		for (int i= 0; i < 256; i++)
			p[256 + i]= p[i]= permutation[i];
	}

	// prevent instantiation
	private PerlinNoise() {
	}

//	public static void main(String[] args) {
//		int samples= 400;
//		float dx= 40.f / samples;
//		float dy= 40.f / samples;
//		float min= 0f, max= 0f, x, y;
//		int i, j;
//		BufferedImage image=
//			new BufferedImage(samples, samples, BufferedImage.TYPE_INT_RGB);
//		long time= System.currentTimeMillis();
//		for (i= 0, x= 0f; i < samples; i++, x += dx)
//			for (j= 0, y= 0f; j < samples; j++, y += dy) {
//				float s= PerlinNoise.noise2d(x, y);
//				//s=PerlinNoise.noise2d(x+s,y+s);
//				//s=PerlinNoise.noise1d(s);
//				//s=PerlinNoise.noise1d(x+s);
//				if (s < min)
//					min= s;
//				else if (s > max)
//					max= s;
//				int col= Color.getHSBColor(0.f, 0.f, 0.5f*s+0.5f).getRGB();
//				image.setRGB(i, j, col);
//			}
//		System.out.println("millis " + (System.currentTimeMillis() - time));
//		System.out.println("min=" + min + " max=" + max);
//		JFrame frame= new JFrame("noise");
//		JLabel label= new JLabel(new ImageIcon(image));
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.getContentPane().add(label);
//		frame.pack();
//		frame.setVisible(true);
//	}
}
