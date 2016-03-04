package ijt.analysis;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.Overlay;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.process.ImageProcessor;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;

/**
 * Run convex hull algorithm on circles.tif image, and display result using ImageJ.
 * 
 * @author dlegland
 *
 */
public class OrientedBox_Circles
{
	public static final void main(String[] args)
	{
		@SuppressWarnings("unused")
		ImageJ ij = new ImageJ();
		
		String fileName = new File("files/circles.tif").getPath();
		ImagePlus imagePlus = IJ.openImage(fileName);
		imagePlus.show();

		// use pre-computed oriented box
		OrientedBox obox = new OrientedBox(108.2667, 130.0151, 268.9114, 109.2686, 61);
		
		PolygonRoi roi = obox.getRoi();
		roi.setStrokeColor(Color.GREEN);
		roi.setStrokeWidth(2);
		
		Overlay ovr = new Overlay();
		ovr.add(roi);
		
		imagePlus.setOverlay(ovr);
	}
	
	
	
	public static PolygonRoi createPolygonRoi(ArrayList<Point2D> coords)
	{
		int n = coords.size();
		float[] px = new float[n];
		float[] py = new float[n];
		
		for (int i = 0; i < n; i++)
		{
			Point2D p = coords.get(i);
			px[i] = (float) p.getX();
			py[i] = (float) p.getY();
		}

		return new PolygonRoi(px, py, n, Roi.POLYGON);
	}

}
