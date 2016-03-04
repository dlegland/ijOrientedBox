package ijt.analysis;

import static org.junit.Assert.*;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.junit.Test;

public class OrientedBoxTest
{

	@Test
	public void polygonCentroidTest_Rect1()
	{
		ArrayList<Point2D> vertices = new ArrayList<>(4);
		vertices.add(new Point2D.Double(10, 20));
		vertices.add(new Point2D.Double(10+30, 20));
		vertices.add(new Point2D.Double(10+30, 20+40));
		vertices.add(new Point2D.Double(10, 20+40));
		
		Point2D.Double exp = new Point2D.Double(25, 40);
		Point2D centroid = OrientedBox.polygonCentroid(vertices);
		
		assertEquals(exp.getX(), centroid.getX(), .01);
		assertEquals(exp.getY(), centroid.getY(), .01);
	}

	/**
	 * The same polygon, with identical first and last verrtex
	 */
	@Test
	public void polygonCentroidTest_Rect2()
	{
		ArrayList<Point2D> vertices = new ArrayList<>(5);
		vertices.add(new Point2D.Double(10, 20));
		vertices.add(new Point2D.Double(10+30, 20));
		vertices.add(new Point2D.Double(10+30, 20+40));
		vertices.add(new Point2D.Double(10, 20+40));
		vertices.add(new Point2D.Double(10, 20));
		
		Point2D.Double exp = new Point2D.Double(25, 40);
		Point2D centroid = OrientedBox.polygonCentroid(vertices);
		
		assertEquals(exp.getX(), centroid.getX(), .01);
		assertEquals(exp.getY(), centroid.getY(), .01);
	}

}
