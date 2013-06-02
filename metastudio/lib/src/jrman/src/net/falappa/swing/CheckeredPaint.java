package net.falappa.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class CheckeredPaint extends TexturePaint {
	private static final int TONE_BITMASK= 0x0f;
	private static final int SIZE_BITMASK= 0xf0;
	public static final int PRESET_LIGHT_TONE_SMALL= 0x00;
	public static final int PRESET_MID_TONE_SMALL= 0x01;
	public static final int PRESET_DARK_TONE_SMALL= 0x02;
	public static final int PRESET_LIGHT_TONE_MEDIUM= 0x10;
	public static final int PRESET_MID_TONE_MEDIUM= 0x11;
	public static final int PRESET_DARK_TONE_MEDIUM= 0x12;
	public static final int PRESET_LIGHT_TONE_LARGE= 0x20;
	public static final int PRESET_MID_TONE_LARGE= 0x21;
	public static final int PRESET_DARK_TONE_LARGE= 0x22;

	private void init(int squareSize, Color color1, Color color2) {
		Graphics g= getImage().getGraphics();
		g.setColor(color1);
		g.fillRect(0, 0, squareSize, squareSize);
		g.fillRect(squareSize, squareSize, squareSize, squareSize);
		g.setColor(color2);
		g.fillRect(squareSize, 0, squareSize, squareSize);
		g.fillRect(0, squareSize, squareSize, squareSize);
	}

	public CheckeredPaint(int squareSize, Color color1, Color color2) {
		super(
			new BufferedImage(
				2 * squareSize,
				2 * squareSize,
				BufferedImage.TYPE_INT_RGB),
			new Rectangle2D.Float(0.f, 0.f, 2 * squareSize, 2 * squareSize));
		init(squareSize, color1, color2);
	}

	public CheckeredPaint() {
		this(8, Color.white, Color.lightGray);
	}

	public static CheckeredPaint createPreset(int preset) {
		int checkSize;
		Color c1, c2;
		int i= (preset & SIZE_BITMASK) >> 4;
		if (i == 2)
			checkSize= 32;
		else if (i == 1)
			checkSize= 16;
		else
			checkSize= 8;
		i= (preset & TONE_BITMASK);
		if (i == 2) {
			c1= Color.lightGray;
			c2= Color.darkGray;
		}
		else if (i == 1) {
			c1= Color.white;
			c2= Color.gray;
		}
		else {
			c1= Color.white;
			c2= Color.lightGray;
		}
		return new CheckeredPaint(checkSize, c1, c2);
	}

}
