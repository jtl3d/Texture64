import java.awt.image.*;
import javax.imageio.*;
import java.io.*;
import java.util.Arrays;
import java.nio.file.*;

public class Texture64 {

    public BufferedImage bi = null;

    public static void main(String[] args) {
        Texture64 t64 = new Texture64();
        if (args.length != 1) {
            System.out.println("usage: java -jar Texture64.jar <file_path>");
            System.exit(0);
        }
        t64.run(args[0]);
    }

    public void run(String filename) {

        try {
            // init BufferedImage
            this.bi = ImageIO.read(new File(filename));
        
        } catch (IOException e) {
            // close when file is not present
            System.out.println("File not found!");
            System.exit(0);
       
        } catch (Exception e) {
            // generic error catch
            System.out.println("Unknown error occured!");
            System.exit(0);
        }

        // create generic data array (ARGB_8888, java default)
        int startX = 0;
        int startY = 0;
        int w = this.bi.getWidth();
        int h = this.bi.getHeight();
        int rgbArray[] = new int[w * h];
        int offset = 0;
        int scansize = w;
        bi.getRGB(startX, startY, w, h, rgbArray, offset, scansize);

        // test rgba5551
        rgba5551(rgbArray);
        rgba8888(rgbArray);
    }

    public void rgba5551(int[] rgbArray) {
        // declare variable for each channel
        int red;
        int green;
        int blue;
        int alpha;
        int argb8888;

        // create RGBA_5551 (16 bits, 2 bytes per pixel)
        byte outArray[] = new byte[rgbArray.length * 2];

        // holds rrrrrggg bits
        byte colorHigh;

        // holds ggbbbbba bits
        byte colorLow;

        // index for outArray
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

        // interleave the array (O(n))
        this.interleave(outArray);

        // output the file
        try (FileOutputStream fos = new FileOutputStream("out.rgba5551")) {
               fos.write(outArray);
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

        // create RGBA_5551 (32 bits, 4 bytes per pixel)
        byte outArray[] = new byte[rgbArray.length * 4];

        // index for outArray
        int j = 0;

        for (int i = 0; i < rgbArray.length; i++) {
            // get color
            argb8888 = rgbArray[i];

            // get channels
            red = getRed(argb8888);
            green = getGreen(argb8888);
            blue = getBlue(argb8888);
            alpha = getAlpha(argb8888);

            // update array
            outArray[j + 0] = (byte) red;
            outArray[j + 1] = (byte) green;
            outArray[j + 2] = (byte) blue;
            outArray[j + 3] = (byte) alpha;
            j += 4;
        }

        // interleave the array (O(n))
        this.interleave(outArray);

        // output the file
        try (FileOutputStream fos = new FileOutputStream("out.rgba8888")) {
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
        return (argb8888 >> 0) & 0xFF;
    }

    public void interleave(byte[] array) {
        // every other line needs to be interleaved
    
        // if standard bmp/image data looks like 
        // (line 0) AABBCCDD EEFFGGHH
        // (line 1) IIJJKKLL MMNNOOPP
        // (line 2) QQRRSSTT UUVVWWXX
        // (line 3) YYZZ0011 22334455

        // then interleaved data looks like
        // (line 0) AABBCCDD EEFFGGHH
        // (line 1) MMNNOOPP IIJJKKLL
        // (line 2) QQRRSSTT UUVVWWXX
        // (line 3) 22334455 YYZZ0011

        // temp variables to hold swap data
        byte a, b, c, d, e, f, g, h;

        // quick calculation to get number of bytes (image formats have different bytes per pixel)
        int bytesPerLine = array.length / this.bi.getHeight();

        // advance to second line immediately
        int i = bytesPerLine;

        // holds bytes left in a row of image data
        int bytesLeft = bytesPerLine;

        // while i < file length
        while (i < array.length - 7) {

            // if we're at (or near) the end of the line
            if (bytesLeft < 8) {
                // advance to end of the line
                i += bytesLeft;

                // advance to the next line
                i += bytesPerLine;

                // reset bytes left
                bytesLeft = bytesPerLine;
            }

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

            // inc/dec
            i += 8;
            bytesLeft -= 8;
        }
    }
}
