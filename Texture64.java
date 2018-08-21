import java.awt.image.*;
import javax.imageio.*;
import java.io.*;
import java.util.Arrays;
import java.nio.file.*;

public class Texture64 {

	public static void main(String[] args) {
		Texture64 t64 = new Texture64();
		if (args.length != 1) {
			System.out.println("usage: java -jar Texture64.jar");
			System.exit(0);
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

	public void rgba5551(int[] rgbArray) {
		// declare variable for each channel
		int red;
		int green;
		int blue;
		int alpha;
		int argb8888;

		// create RGBA_5551[]
		byte outArray[] = new byte[rgbArray.length * 2];
		byte colorLow;
		byte colorHigh;
		int j = 0;

		for (int i = 0; i < rgbArray.length; i++) {
			// get color
			argb8888 = rgbArray[i];

			// get channels
			red = (getRed(argb8888) & 0xF8) >> 3;
			green = (getGreen(argb8888) & 0xF8) >> 3;
			blue = (getBlue(argb8888) & 0xF8) >> 3;
			
			// check for transparency 
			alpha = getAlpha(argb8888);
			if (alpha > 0) {
				alpha = 1;
			} else {
				alpha = 0;
			}

			// bit manipulation
			// have 
			// 000rrrrr
			// 000ggggg
			// 000bbbbb
			// 0000000a
			// need 
			// rrrrrggg
			// ggbbbbba 
			colorHigh = 0;
			colorHigh |= red << 3;
			colorHigh |= (green & 0x1D) >> 2;
			colorLow = 0;
			colorLow |= (green & 0x3) << 6;
			colorLow |= blue << 1;
			colorLow |= alpha;

			// update array
			outArray[j + 0] = colorHigh;
			outArray[j + 1] = colorLow;
			j += 2;
		}

		// interleve the array (O(n))
		this.interleve(outArray);

		// output the file
		try (FileOutputStream fos = new FileOutputStream("out.rgba5551")) {
   			fos.write(outArray);
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

	public void interleve(byte[] array) {
		// AABBCCDD EEFFGGHH ... AABBCCDD EEFFGGHH
		// to
		// EEFFGGHH AABBCCDD ... EEFFGGHH AABBCCDD

		byte a, b, c, d, e, f, g, h;

		for (int i = 0; i < array.length; i += 8) {
			// save
			a = array[i + 0];
			b = array[i + 1];
			c = array[i + 2];
			d = array[i + 3];
			e = array[i + 4];
			f = array[i + 5];
			g = array[i + 6];
			h = array[i + 7];

			// update
			array[i + 0] = e;
			array[i + 1] = f;
			array[i + 2] = g;
			array[i + 3] = h;
			array[i + 4] = a;
			array[i + 5] = b;
			array[i + 6] = c;
			array[i + 7] = d;
		}
	}

}
