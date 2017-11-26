
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
		
		// compare with the result of the "imOrientedBox" function, from the
		// MatImage libray for Matlab
		// (intermediate result obtained before adding the spacing of one pixel in each direction).
		assertEquals(107.46, diameter, .02);
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

}
