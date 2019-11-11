package Manipulator;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class Manipulate {
	
	public static BufferedImage loadImage(String path) throws IOException {  
        BufferedImage img = null;  
        
        try {  
            img = ImageIO.read(new File(path)); 
        } catch (Exception e) {  
            e.printStackTrace();  
        } 
        return img;  
    }  
	
	public static BufferedImage rotateAndMirrorImage(BufferedImage oldImg) throws IOException {
		//rotates image 90 degrees clockwise
		double rads = Math.toRadians(90);
		double sin = Math.abs(Math.sin(rads));
		double cos = Math.abs(Math.cos(rads));
		int w = (int) Math.floor(oldImg.getWidth() * cos + oldImg.getHeight() * sin);
		int h = (int) Math.floor(oldImg.getHeight() * cos + oldImg.getWidth() * sin);
		BufferedImage img = new BufferedImage(w, h, oldImg.getType());
		AffineTransform at = new AffineTransform();
		at.translate(w / 2, h / 2);
		at.rotate(rads,0, 0);
		at.translate(-oldImg.getWidth() / 2, -oldImg.getHeight() / 2);
		AffineTransformOp rotateOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		img = rotateOp.filter(oldImg, null);
		
		//mirrors image (horizontal flip)
		AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
	    tx.translate(-img.getWidth(null), 0);
	    AffineTransformOp mirrorOp = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
	    img = mirrorOp.filter(img, null);
		    
		return img;
	}
	
	public static int[][] getPixels(BufferedImage img) {
		final byte[] pixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
	      final int width = img.getWidth();
	      final int height = img.getHeight();
	      final boolean hasAlphaChannel = img.getAlphaRaster() != null;

	      int[][] result = new int[height][width];
	      if (hasAlphaChannel) {
	         final int pixelLength = 4;
	         for (int pixel = 0, row = 0, col = 0; pixel + 3 < pixels.length; pixel += pixelLength) {
	            int argb = 0;
	            argb += (((int) pixels[pixel] & 0xff) << 24); // alpha
	            argb += ((int) pixels[pixel + 1] & 0xff); // blue
	            argb += (((int) pixels[pixel + 2] & 0xff) << 8); // green
	            argb += (((int) pixels[pixel + 3] & 0xff) << 16); // red
	            result[row][col] = argb;
	            col++;
	            if (col == width) {
	               col = 0;
	               row++;
	            }
	         }
	      } else {
	         final int pixelLength = 3;
	         for (int pixel = 0, row = 0, col = 0; pixel + 2 < pixels.length; pixel += pixelLength) {
	            int argb = 0;
	            argb += -16777216; // 255 alpha
	            argb += ((int) pixels[pixel] & 0xff); // blue
	            argb += (((int) pixels[pixel + 1] & 0xff) << 8); // green
	            argb += (((int) pixels[pixel + 2] & 0xff) << 16); // red
	            result[row][col] = argb;
	            col++;
	            if (col == width) {
	               col = 0;
	               row++;
	            }
	         }
	      }
	      return result;
	}
	
	public static int[][] mergePixels(int[][] img1, int[][] img2) throws SizeException {
		int rowLength = 0;
		int colLength = 0;
		
		if (img1.length != img2.length || img1[0].length != img2[0].length) {
			throw new Manipulate.SizeException("Images must be the same size");
		}
		
		if (img1.length > img2.length) {
			rowLength = img1.length;
		}
		else {
			rowLength = img2.length;
		}
		
		if (img1[0].length > img2[0].length) {
			colLength = img1[0].length;
		}
		else {
			colLength = img2[0].length;
		}
		
		int[][] result = new int[rowLength][colLength];
		
		int row = 0;
		int col = 0;
		
		for (row = 0; row < img1.length && row < img2.length; row++) {
			for (col = 0; col < img1[0].length && col < img2[0].length && col < img1[1].length && col < img2[1].length; col++) {
				result[row][col] = img1[row][col];
				result[row][++col] = img2[row][col];
			}
		}

		for (; row < img1.length; row++) {
			for (; col < colLength; col++) {
				result[row][col] = img1[row][col];
			}
			col = 0;
		}
		
		for (; row < img2.length; row++) {
			for (; col < colLength; col++) {
				result[row][col] = img2[row][col];
			}
			col = 0;
		}
		
		return result;
	}	
	
	public static int[][] sortPixels(int[][] result) {
		for(int[] arr : result) {
			Arrays.sort(arr);
		}
	    return result;
	}
	
	public static int[][] abstractSortPixels(int[][] result) {
		Arrays.sort(result, (a, b) -> Integer.compare(a[50], b[50]));
		return result;
	}
	
	
	public static int[][] scatterPixels(int[][] result) {
		Random random = new Random();

	    for (int i = result.length - 1; i > 0; i--) {
	        for (int j = result[i].length - 1; j > 0; j--) {
	            int m = random.nextInt(i + 1);
	            int n = random.nextInt(j + 1);

	            int temp = result[i][j];
	            result[i][j] = result[m][n];
	            result[m][n] = temp;
	        }
	    }
	    return result;
	}
	
	public static void gradualSort(int[][] result, int pics, String newFileName) throws IOException {
		int pixelCounter = 0;
		int totalPixels = getNumberOfPixels(result);
		int limit = totalPixels/pics;
		int fileNum = 1;
		for(int[] arr : result) {
			Arrays.sort(arr);
			for (int element : arr) {
				pixelCounter++;
			}
			if ( pixelCounter == limit ) {
				Arrays.sort(arr);
				BufferedImage img = createImage(result);
				BufferedImage rotImg = rotateAndMirrorImage(img);
				fixBadJPEG(rotImg, newFileName + fileNum);
				fileNum++;
				pixelCounter = 0;
			}
		}
	}
	
	private static int getNumberOfPixels(int[][] result) {
		int pixelCount = 0;
		
		for(int[] arr : result) {
			for (int element : arr) {
				pixelCount++;
			}
		}
		return pixelCount;
	}
	
	public static BufferedImage createImage(int[][] result) throws IOException {
		int xLength = result.length;
		int yLength = result[0].length;
		BufferedImage img = new BufferedImage(xLength, yLength, 3);
		
		for(int x = 0; x < xLength; x++) {
		    for(int y = 0; y < yLength; y++) {
		        int rgb = (int)result[x][y]<<16 | (int)result[x][y] << 8 | (int)result[x][y];
		        img.setRGB(x, y, rgb);
		    }
		}
		return img;
	}
	
	public static BufferedImage convertARGBToRGB(BufferedImage img) {
		int[] pixels = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
		BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
		int[] pixelsOut = ((DataBufferInt) result.getRaster().getDataBuffer()).getData();
		System.arraycopy(pixels, 0, pixelsOut, 0, pixels.length);
		return result;
		}
	
	public static void fixBadJPEG(BufferedImage img, String fileName) throws IOException {
		
		// IF IMAGETYPE = 3 do the fix, else just print 
		// (probably have to do .getImagetype or whatever it is
		// really just need to add metadata dependency 
		
        int[] arr = new int[img.getWidth() * img.getHeight()];
        img.getRGB(0, 0, img.getWidth(), img.getHeight(), arr, 0, img.getWidth());
        for (int i = arr.length - 1; i >= 0; i--)
        {
            int y = arr[i] >> 16 & 0xFF; // Y
            int b = (arr[i] >> 8 & 0xFF) - 128; // Pb
            int r = (arr[i] & 0xFF) - 128; // Pr

            int g = (y << 8) + -88 * b + -183 * r >> 8; 
            b = (y << 8) + 454 * b >> 8;
            r = (y << 8) + 359 * r >> 8;

            if (r > 255)
                r = 255;
            else if (r < 0) r = 0;
            if (g > 255)
                g = 255;
            else if (g < 0) g = 0;
            if (b > 255)
                b = 255;
            else if (b < 0) b = 0;

            arr[i] = 0xFF000000 | (r << 8 | g) << 8 | b;
        }
        img.setRGB(0, 0, img.getWidth(), img.getHeight(), arr, 0, img.getWidth());
			
        //ImageIO.write(img, "jpg", new File(fileName + ".jpg")); 
        
        ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();
        ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();
        jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        jpgWriteParam.setCompressionQuality(1.0f);

        ImageOutputStream outputStream = ImageIO.createImageOutputStream(new File("/Users/larm2112/Projects/Misc Java Projects/images/" + fileName + ".jpg"));
        jpgWriter.setOutput(outputStream);
        IIOImage outputImage = new IIOImage(img, null, null);
        jpgWriter.write(null, outputImage, jpgWriteParam);
        jpgWriter.dispose();        
        
		System.out.println("image created");
    }
	
	static class SizeException extends Exception { 
	    public SizeException(String s) { 
	        super(s); 
	    } 
	} 
	    
}




