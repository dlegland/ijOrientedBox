package ijt.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Map;

import org.junit.Test;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.PolygonRoi;
import ij.measure.ResultsTable;
import ij.process.ImageProcessor;


public class OrientedBox2DTest
{
	@Test
	public void testOrientedBox_circle_stepByStep()
	{
		@SuppressWarnings("unused")
		ImageJ ij = new ImageJ();
		
		String fileName = getClass().getResource("/files/circles.tif").getFile();
		ImagePlus imagePlus = IJ.openImage(fileName);
		assertNotNull(imagePlus);
		
		ImageProcessor image = imagePlus.getProcessor();
		ResultsTable table = OrientedBox2D.asTable(OrientedBox2D.orientedBox(image));
	
		imagePlus.show();
		Map<Integer, ArrayList<Point2D>> labelCorners = OrientedBox2D.computeLabelsCorners(image, new int[]{255});
		ArrayList<Point2D> corners = labelCorners.get(255);

		ArrayList<Point2D> convexHull = Polygons2D.convexHull_jarvis(corners);
		PolygonRoi hullRoi = Polygons2D.createPolygonRoi(convexHull);
		imagePlus.setRoi(hullRoi, true);

		// Length of oriented box
		assertEquals(272.23, table.getValueAsDouble(2, 0), .05);
		// width of oriented box
		assertEquals(108.86, table.getValueAsDouble(3, 0), .05);
	}

	@Test
	public void testOrientedBox_circle()
	{
		@SuppressWarnings("unused")
		ImageJ ij = new ImageJ();
		
		String fileName = getClass().getResource("/files/circles.tif").getFile();
		ImagePlus imagePlus = IJ.openImage(fileName);
		assertNotNull(imagePlus);
		
		ImageProcessor image = imagePlus.getProcessor();
		OrientedBox2D box = OrientedBox2D.orientedBox(image).get(255);

		// Length of oriented box
		assertEquals(272.23, box.length, .05);
		// width of oriented box
		assertEquals(108.86, box.width, .05);
	}
}
