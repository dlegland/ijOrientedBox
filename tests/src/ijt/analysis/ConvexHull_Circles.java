package ijt.analysis;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.process.ImageProcessor;

import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;

/**
 * Run convex hull algorithm on circles.tif image, and display result using ImageJ.
 * 
 * @author dlegland
 *
 */
public class ConvexHull_Circles
{
	public static final void main(String[] args)
	{
		@SuppressWarnings("unused")
		ImageJ ij = new ImageJ();
		
		String fileName = new File("files/circles.tif").getPath();
		ImagePlus imagePlus = IJ.openImage(fileName);
		imagePlus.show();
		
		ImageProcessor image = imagePlus.getProcessor();
		
//		ArrayList<Point> points = FeretDiameters.boundaryPoints(image);
		ArrayList<Point2D> points = FeretDiameters.binaryParticleCorners(image);
		System.out.println("number of points: " + points.size());

		ArrayList<Point2D> convexHull = Polygons2D.convexHull_jarvis(points);

		PolygonRoi roi = createPolygonRoi(convexHull);
		imagePlus.setRoi(roi, true);
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
