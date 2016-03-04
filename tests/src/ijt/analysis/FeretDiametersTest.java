
package ijt.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;

import org.junit.Test;

public class FeretDiametersTest
{

	@Test
	public void maxFeretDiameter_single()
	{
		String fileName = new File("files/circles.tif").getPath();
		ImagePlus imagePlus = IJ.openImage(fileName);
		assertNotNull(imagePlus);
		
		ImageProcessor image = imagePlus.getProcessor();
		double diameter = FeretDiameters.maxFeretDiameterSingle(image).diameter;
		
		assertEquals(272.71, diameter, .05);
	}

	@Test
	public void minFeretDiameter_single()
	{
		String fileName = new File("files/circles.tif").getPath();
		ImagePlus imagePlus = IJ.openImage(fileName);
		assertNotNull(imagePlus);
		
		ImageProcessor image = imagePlus.getProcessor();
		double diameter = FeretDiameters.minFeretDiameterSingle(image).diameter;
		
		assertEquals(108.8, diameter, .05);
	}


	@Test
	public void minFeretDiameter_Points_rect1()
	{
		ArrayList<Point> points = new ArrayList<Point>();
		points.add(new Point(20, 30));
		points.add(new Point(20, 80));
		points.add(new Point(40, 30));
		points.add(new Point(40, 80));
//		points.add(new Point(30, 20));
//		points.add(new Point(30, 90));
//		points.add(new Point(25, 60));
//		points.add(new Point(35, 60));
//		points.add(new Point(20, 40));
//		points.add(new Point(30, 70));
		
		FeretDiameters.AngleDiameterPair pair = FeretDiameters.minFeretDiameter(points);
		double minDiam = pair.diameter;
		assertEquals(20, minDiam, .01);
	}

	public void testConvexHull_jarvis_int()
	{
		ArrayList<Point> polygon = new ArrayList<Point>();
		polygon.add(new Point( 30,  90));
		polygon.add(new Point(110,  10));
		polygon.add(new Point( 60,  80));
		polygon.add(new Point( 40,  30));
		polygon.add(new Point( 50, 150));
		polygon.add(new Point( 80, 110));
		polygon.add(new Point( 10,  60));
		polygon.add(new Point( 70,  40));
		polygon.add(new Point( 90,  70));
		polygon.add(new Point(140,  50));
		polygon.add(new Point(100, 130));
		polygon.add(new Point(160, 140));
		polygon.add(new Point(150,  20));
		polygon.add(new Point(130, 160));
		polygon.add(new Point( 20, 120));
		polygon.add(new Point(120, 100));

		ArrayList<Point> convHull = ConvexHull.convexHull_jarvis_int(polygon);
		
		int i = 0;
		for (Point p : convHull)
		{
			System.out.println(String.format("Vertex %2d = (%3d, %3d)", i++, p.x, p.y));
		}
	}

}
