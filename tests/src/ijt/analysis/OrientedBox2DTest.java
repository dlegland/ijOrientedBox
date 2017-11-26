package ijt.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.PolygonRoi;
import ij.measure.ResultsTable;
import ij.process.ImageProcessor;

import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;


public class OrientedBox2DTest
{
	@Test
	public void testOrientedBox_circle()
	{
		@SuppressWarnings("unused")
		ImageJ ij = new ImageJ();
		
		String fileName = new File("files/circles.tif").getPath();
		ImagePlus imagePlus = IJ.openImage(fileName);
		assertNotNull(imagePlus);
		
		ImageProcessor image = imagePlus.getProcessor();
		ResultsTable table = OrientedBox2D.orientedBox(image);
	
		imagePlus.show();
		HashMap<Integer, ArrayList<Point2D>> labelCorners = OrientedBox2D.computeLabelsCorners(image, new int[]{255});
		ArrayList<Point2D> corners = labelCorners.get(255);

		ArrayList<Point2D> convexHull = Polygons2D.convexHull_jarvis(corners);
		PolygonRoi hullRoi = Polygons2D.createPolygonRoi(convexHull);
		imagePlus.setRoi(hullRoi, true);

		assertEquals(272.23, table.getValueAsDouble(2, 0), .05);
		assertEquals(108.86, table.getValueAsDouble(3, 0), .05);
	}

}
