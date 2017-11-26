package ijt.analysis;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.Overlay;
import ij.gui.PolygonRoi;
import ij.process.ImageProcessor;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;

/**
 * Computes oriented bounding box of circles.tif image, and displays result
 * using ImageJ.
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

		ImageProcessor image = imagePlus.getProcessor();
		ArrayList<Point2D> points = FeretDiameters.binaryParticleCorners(image);
		
		ArrayList<Point2D> convexHull = Polygons2D.convexHull_jarvis(points);
		PolygonRoi hullRoi = Polygons2D.createPolygonRoi(convexHull);
		imagePlus.setRoi(hullRoi, true);
		
		System.out.println("number of points: " + points.size());
		OrientedBox2D obox = OrientedBox2D.computeBox(points);

		System.out.println("oriented box: ");
		System.out.println("  xc: " + obox.x0);
		System.out.println("  yc: " + obox.y0);
		System.out.println("  l:  " + obox.length);
		System.out.println("  w:  " + obox.width);
		System.out.println("  Th:  " + obox.theta);

//		// use pre-computed oriented box
//		OrientedBox obox = new OrientedBox(108.2667, 130.0151, 268.9114, 109.2686, 61);
		
		PolygonRoi roi = obox.getRoi();
		roi.setStrokeColor(Color.GREEN);
		roi.setStrokeWidth(.5);
		
		Overlay ovr = new Overlay();
		ovr.add(roi);
		
		imagePlus.setOverlay(ovr);
	}	
}
