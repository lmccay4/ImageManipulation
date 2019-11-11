package Manipulator;

import java.awt.image.BufferedImage;
import java.io.IOException;

import Manipulator.Manipulate.SizeException;

public class ImageManipulator {
	
	private static void sort(String path, String newFileName) throws IOException {
		BufferedImage img = Manipulate.loadImage(path);
	    int[][] arr = Manipulate.getPixels(img);
	    int[][] result = Manipulate.sortPixels(arr);
	    BufferedImage buffImg = Manipulate.createImage(result);
	    BufferedImage rotImg = Manipulate.rotateAndMirrorImage(buffImg);
	    Manipulate.fixBadJPEG(rotImg, newFileName);
	}
	
	private static void abstractSort(String path, String newFileName) throws IOException {
		BufferedImage img = Manipulate.loadImage(path);
	    int[][] arr = Manipulate.getPixels(img);
	    int[][] result = Manipulate.abstractSortPixels(arr);
	    BufferedImage buffImg = Manipulate.createImage(result);
	    BufferedImage rotImg = Manipulate.rotateAndMirrorImage(buffImg);
	    Manipulate.fixBadJPEG(rotImg, newFileName);
	}
	
	private static void gradSort(String path, int numberOfPics, String newFileName) throws IOException {
		BufferedImage img1 = Manipulate.loadImage(path);
	    int[][] arr1 = Manipulate.getPixels(img1);
	    Manipulate.gradualSort(arr1, numberOfPics, newFileName);
	}
	
	private static void merge(String path1, String path2, String newFileName) throws IOException, SizeException {
		BufferedImage img1 = Manipulate.loadImage(path1);
		BufferedImage img2 = Manipulate.loadImage(path2);
		int[][] arr1 = Manipulate.getPixels(img1);
	    int[][] arr2 = Manipulate.getPixels(img2);
	    int[][] result = Manipulate.mergePixels(arr1, arr2);
	    BufferedImage img = Manipulate.createImage(result);
	    BufferedImage rotImg = Manipulate.rotateAndMirrorImage(img);
	    Manipulate.fixBadJPEG(rotImg, newFileName);
	}
	
	private static void mergeAndSort(String path1, String path2, String newFileName) throws IOException, SizeException {
		BufferedImage img1 = Manipulate.loadImage(path1);
		BufferedImage img2 = Manipulate.loadImage(path2);
		int[][] arr1 = Manipulate.getPixels(img1);
	    int[][] arr2 = Manipulate.getPixels(img2);
	    int[][] result = Manipulate.mergePixels(arr1, arr2);
	    int[][] sorted = Manipulate.sortPixels(result);
	    BufferedImage img = Manipulate.createImage(sorted);
	    BufferedImage rotImg = Manipulate.rotateAndMirrorImage(img);
	    Manipulate.fixBadJPEG(rotImg, newFileName);
	}
	
	private static void mergeAndAbstractSort(String path1, String path2, String newFileName) throws IOException, SizeException {
		BufferedImage img1 = Manipulate.loadImage(path1);
		BufferedImage img2 = Manipulate.loadImage(path2);
		int[][] arr1 = Manipulate.getPixels(img1);
	    int[][] arr2 = Manipulate.getPixels(img2);
	    int[][] result = Manipulate.mergePixels(arr1, arr2);
	    int[][] sorted = Manipulate.abstractSortPixels(result);
	    BufferedImage buffImg = Manipulate.createImage(sorted);
	    BufferedImage rotImg = Manipulate.rotateAndMirrorImage(buffImg);
	    Manipulate.fixBadJPEG(rotImg, newFileName);
	}
	
	private static void mergeAndGradSort(String path1, String path2, int numberOfPics, String newFileName) throws IOException, SizeException {
		BufferedImage img1 = Manipulate.loadImage(path1);
		BufferedImage img2 = Manipulate.loadImage(path2);
	    int[][] arr1 = Manipulate.getPixels(img1);
		int[][] arr2 = Manipulate.getPixels(img2);
	    int[][] result = Manipulate.mergePixels(arr1, arr2);
	    Manipulate.gradualSort(result, numberOfPics, newFileName);
	}

	public static void main(String [] args) throws Exception {
		sort("/Users/larm2112/Projects/Misc Java Projects/images/larryCrop.jpg", "newLarrySortClass");
	}
	
	
}
