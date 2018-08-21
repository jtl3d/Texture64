import java.awt.image.*;
import javax.imageio.*;
import java.io.*;
import java.util.Arrays;

public class Texture64 {

	public static void main(String[] args) {
		Texture64 t64 = new Texture64();
		if (args.length != 1) {
			System.out.println("usage: java -jar Texture64.jar");
		}
		t64.run(args[0]);
	}


	public void run(String filename) {

		BufferedImage bi = null;

		try {
			bi = ImageIO.read(new File(filename));
		} catch (IOException e) {
			System.out.println("File not found!");
			System.exit(0);
		} catch (Exception e) {
			System.out.println("Unknown error occured!");
			System.exit(0);
		}

		// create generic data array (ARGB_8888, java default)
		int startX = 0;
		int startY = 0;
		int w = bi.getWidth();
		int h = bi.getHeight();
		int rgbArray[] = new int[w * h];
		int offset = 0;
		int scansize = w;
		bi.getRGB(startX, startY, w, h, rgbArray, offset, scansize);

		// test rgba5551
		rgba5551(rgbArray);
		// rgba8888(rgbArray);
		// ci4(rgbArray);
		// ci8(rgbArray);

	}

	public void ci4(int[] rgbArray) {
		// declare variable for each channel
		int red;
		int green;
		int blue;
		int alpha;
		int argb8888;

		// create RGBA_5551
		byte outArray[] = new byte[rgbArray.length / 2];
		
		// ?0, upper nibble
		int ci4High = 0;
		
		// 0?, lower nibble
		int ci4Low = 0;

		// palette of colors
		int[] palette = new int[16];

		// number of colors
		int colors = 0;
		int index = 0;

		// build palette
		for (int i = 0; i < rgbArray.length; i ++) {
			argb8888 = rgbArray[i];

			// if there are too mnay colors
			if (colors == 16) {
				// inform user and breka
				System.out.println("Too many colors for CI4!");
				return;
			}

			// if the color is not already in the palette
			// index = ArrayUtils.indexof(palette, argb8888);

			if (index == -1) {
				// add color to palette and increase colors
				palette[colors] = argb8888;
				colors++;

			// if the color is in the palette and even iteration
			} else if (i % 2 == 0) {
				ci4High = index << 4;

			}
		}


	}

	public void rgba5551(int[] rgbArray) {
		// declare variable for each channel
		int red;
		int green;
		int blue;
		int alpha;
		int argb8888;

		// create RGBA_5551
		short outArray[] = new short[rgbArray.length];
		short rgba5551;

		for (int i = 0; i < rgbArray.length; i++) {
			// get color
			argb8888 = rgbArray[i];

			// check for transparency
			alpha = getAlpha(argb8888);
			if (alpha > 0) {
				// get first 5 bits of color
				red = getRed(argb8888) & 0xF8;
				green = getGreen(argb8888) & 0xF8;
				blue = getBlue(argb8888) & 0xF8;
				alpha = 1;
			} else {
				red = 0;
				green = 0;
				blue = 0;
				alpha = 0;
			}

			// bit manipulation
			// have - 00000000?????000
			// need - rrrrrgggggbbbbba 
			rgba5551 = 0;
			rgba5551 |= red << 8;
			rgba5551 |= green << 3;
			rgba5551 |= blue >> 2;
			rgba5551 |= alpha;

			// update array
			outArray[i] = rgba5551;
		}

		// interleve the array (O(n))
		this.interleve(outArray);

		// output the file (O(n))
		try {
 			DataOutputStream dos = new DataOutputStream(new FileOutputStream("out.rgba5551"));
			for (short color : outArray) {
				dos.writeShort(color);
			}
			dos.close();
		} catch (Exception e) {
			System.out.println("Unknown error occured!");
			System.exit(0);
		}
	}

	public void rgba8888(int[] rgbArray) {
		// declare variable for each channel
		int red;
		int green;
		int blue;
		int alpha;
		int argb8888;

		// create RGBA_8888
		int outArray[] = new int[rgbArray.length];
		int rgba8888;

		for (int i = 0; i < rgbArray.length; i++) {
			// get color
			argb8888 = rgbArray[i];

			// check for transparency
			alpha = getAlpha(argb8888);
			
			if (alpha > 0) {
				// get colors
				red = getRed(argb8888);
				green = getGreen(argb8888);
				blue = getBlue(argb8888);
			} else {
				red = 0;
				green = 0;
				blue = 0;
				alpha = 0;
			}

			// bit manipulation
			// have - 00000000????????
			// need - rrrrrrrrggggggggbbbbbbbbaaaaaaaa 
			rgba8888 = 0;
			rgba8888 |= red << 24;
			rgba8888 |= green << 16;
			rgba8888 |= blue << 8;
			rgba8888 |= alpha;		

			// update array
			outArray[i] = rgba8888;
		}

		// interleve the array (O(n))
		this.interleve(outArray);

		// output the file (O(n))
		try {
 			DataOutputStream dos = new DataOutputStream(new FileOutputStream("out.rgba8888"));
			for (int color : outArray) {
				dos.writeInt(color);
			}
			dos.close();
		} catch (Exception e) {
			System.out.println("Unknown error occured!");
			System.exit(0);
		}
	}

	public int getAlpha(int argb8888) {
		return (argb8888 >> 24) & 0xFF;
	}

	public int getRed(int argb8888) {
		return (argb8888 >> 16) & 0xFF;
	}

	public int getGreen(int argb8888) {
		return (argb8888 >> 8) & 0xFF;
	}

	public int getBlue(int argb8888) {
		return argb8888 & 0xFF;
	}

	public void interleve(int[] array) {
		// AAAAAAAA BBBBBBBB CCCCCCCC DDDDDDDD
		// to
		// BBBBBBBB AAAAAAAA DDDDDDDD CCCCCCCC

		int a;
		int b;
		int c;
		int d;
		for (int i = 0; i < array.length; i += 4) {
			// save
			a = array[i + 0];
			b = array[i + 1];
			c = array[i + 2];
			d = array[i + 3];

			// update
			array[i + 0] = b;
			array[i + 1] = a;
			array[i + 2] = d;
			array[i + 3] = c;
		}
	}

	public void interleve(short[] array) {
		// AAAABBBB CCCCDDDD EEEEFFFF GGGGHHHH
		// to
		// CCCCDDDD AAAABBBB GGGGHHHH EEEEFFFF

		short a;
		short b;
		short c;
		short d;

		for (int i = 0; i < array.length; i += 4) {
			// save
			a = array[i + 0];
			b = array[i + 1];
			c = array[i + 2];
			d = array[i + 3];

			// update
			array[i + 0] = b;
			array[i + 1] = a;
			array[i + 2] = d;
			array[i + 3] = c;
		}
	}

}
