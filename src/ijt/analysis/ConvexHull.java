package ijt.analysis;

import ij.gui.PolygonRoi;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Utility class to compute Convex hull of a set of points.
 */
public class ConvexHull
{
	/**
	 * Uses the gift wrap algorithm with floating point values to find the
	 * convex hull and returns it as a list of points.
	 * 
	 * Code from ij.gui.PolygonRoi.getConvexHull(), adapted to return a polygon
	 * oriented counter-clockwise.
	 */
	public static final ArrayList<Point2D> convexHull_jarvis(
			ArrayList<? extends Point2D> vertices)
	{
		// Get polygon info
		int n = vertices.size();
		double[] xCoordinates = new double[n];
		double[] yCoordinates = new double[n];
		
		for (int i = 0; i < n; i++)
		{
			Point2D vertex = vertices.get(i);
			xCoordinates[i] = vertex.getX();
			yCoordinates[i] = vertex.getY();
		}
		
		// convex hull coordinates
		ArrayList<Point2D> hull = new ArrayList<>();
		
		// find bound in vertical direction
		double smallestY = java.lang.Double.MAX_VALUE;
		double x, y;
		for (int i = 0; i < n; i++)
		{
			y = yCoordinates[i];
			if (y < smallestY)
				smallestY = y;
		}
	
		// find left-most vertex of horizontal line with smallest y
		double smallestX = java.lang.Double.MAX_VALUE;
		int p1 = 0;
		for (int i = 0; i < n; i++)
		{
			x = xCoordinates[i];
			y = yCoordinates[i];
			if (y == smallestY && x < smallestX)
			{
				smallestX = x;
				p1 = i;
			}
		}
		
		// p1: index of current hull vertex
		// p2: index of current candidate for next hull vertex
		// p3: index of iterator on point set
		
		int pstart = p1;
		do
		{
			// coordinates of current convex hull vertex
			double x1 = xCoordinates[p1];
			double y1 = yCoordinates[p1];
			
			// coordinates of next vertex candidate
			int p2 = (p1 + 1) % n;
			double x2 = xCoordinates[p2];
			double y2 = yCoordinates[p2];
	
			// find the next "wrapping" vertex by computing oriented angle
			int p3 = (p2 + 1) % n;
			do
			{
				double x3 = xCoordinates[p3];
				double y3 = yCoordinates[p3];
				
				// if V1-V2-V3 is oriented CW, use V3 as next wrapping candidate
				double determinate = x1 * (y2 - y3) - y1 * (x2 - x3) + (y3 * x2 - y2 * x3);
//				if (determinate < 0)
				if (determinate < 1e-12)
				{
					x2 = x3;
					y2 = y3;
					p2 = p3;
				}
				p3 = (p3 + 1) % n;
			} while (p3 != p1);
			
			hull.add(new Point2D.Double(x1, y1));
			p1 = p2;
		} while (p1 != pstart);
	
		return hull;
	}

	/**
	 * Uses the gift wrap algorithm with integer to find the convex hull and
	 * returns it as a list of points.
	 * 
	 * Code from ij.gui.PolygonRoi.getConvexHull(), adapted to return a polygon
	 * oriented counter-clockwise.
	 */
	public static final ArrayList<Point> convexHull_jarvis_int(ArrayList<Point> vertices)
	{
		// Get polygon info
		int n = vertices.size();
		int[] xCoordinates = new int[n];
		int[] yCoordinates = new int[n];
		
		for (int i = 0; i < n; i++)
		{
			Point vertex = vertices.get(i);
			xCoordinates[i] = vertex.x;
			yCoordinates[i] = vertex.y;
		}
		
		// convex hull coordinates
		ArrayList<Point> hull = new ArrayList<>();
		
		// find bound in vertical direction
		int smallestY = Integer.MAX_VALUE;
		int x, y;
		for (int i = 0; i < n; i++)
		{
			y = yCoordinates[i];
			if (y < smallestY)
				smallestY = y;
		}
	
		// find left-most vertex of horizontal line with smallest y
		int smallestX = Integer.MAX_VALUE;
		int p1 = 0;
		for (int i = 0; i < n; i++)
		{
			x = xCoordinates[i];
			y = yCoordinates[i];
			if (y == smallestY && x < smallestX)
			{
				smallestX = x;
				p1 = i;
			}
		}
		
		// p1: index of current hull vertex
		// p2: index of current candidate for next hull vertex
		// p3: index of iterator on point set
		
		int pstart = p1;
		do
		{
			// coordinates of current convex hull vertex
			int x1 = xCoordinates[p1];
			int y1 = yCoordinates[p1];
			
			// coordinates of next vertex candidate
			int p2 = (p1 + 1) % n;
			int x2 = xCoordinates[p2];
			int y2 = yCoordinates[p2];
	
			// find the next "wrapping" vertex by computing oriented angle
			int p3 = (p2 + 1) % n;
			do
			{
				int x3 = xCoordinates[p3];
				int y3 = yCoordinates[p3];
				
				// if V1-V2-V3 is oriented CW, use V3 as next wrapping candidate
				int determinate = x1 * (y2 - y3) - y1 * (x2 - x3) + (y3 * x2 - y2 * x3);
				if (determinate < 0)
				{
					x2 = x3;
					y2 = y3;
					p2 = p3;
				}
				p3 = (p3 + 1) % n;
			} while (p3 != p1);
			
			hull.add(new Point(x1, y1));
			p1 = p2;
		} while (p1 != pstart);
	
		return hull;
	}

	/**
	 * Uses the gift wrap algorithm to find the convex hull and returns it as a
	 * Polygon.
	 * 
	 * Code from ij.gui.PolygonRoi.getConvexHull().
	 */
	public static final Polygon getConvexHull(PolygonRoi polygonRoi)
	{
		// Get polygon info
		int n = polygonRoi.getNCoordinates();
		int[] xCoordinates = polygonRoi.getXCoordinates();
		int[] yCoordinates = polygonRoi.getYCoordinates();
		Rectangle r = polygonRoi.getBounds();
	
		// lower bounds
		int xbase = r.x;
		int ybase = r.y;
		
		// convex hull coordinates
		int[] xx = new int[n];
		int[] yy = new int[n];
		
		// number of vertices in hull
		int n2 = 0;
		
		// find bound in vertical direction
		int smallestY = Integer.MAX_VALUE;
		int x, y;
		for (int i = 0; i < n; i++)
		{
			y = yCoordinates[i];
			if (y < smallestY)
				smallestY = y;
		}
	
		// find left-most vertex of horizontal line with smallest y
		int smallestX = Integer.MAX_VALUE;
		int p1 = 0;
		for (int i = 0; i < n; i++)
		{
			x = xCoordinates[i];
			y = yCoordinates[i];
			if (y == smallestY && x < smallestX)
			{
				smallestX = x;
				p1 = i;
			}
		}
		
		
		int pstart = p1;
		int x1, y1, x2, y2, x3, y3, p2, p3;
		int determinate;
		int count = 0;
		do
		{
			x1 = xCoordinates[p1];
			y1 = yCoordinates[p1];
			p2 = p1 + 1;
			if (p2 == n)
				p2 = 0;
			x2 = xCoordinates[p2];
			y2 = yCoordinates[p2];
			
			p3 = p2 + 1;
			if (p3 == n)
				p3 = 0;
			
			do
			{
				x3 = xCoordinates[p3];
				y3 = yCoordinates[p3];
				determinate = x1 * (y2 - y3) - y1 * (x2 - x3) + (y3 * x2 - y2 * x3);
				if (determinate > 0)
				{
					x2 = x3;
					y2 = y3;
					p2 = p3;
				}
				p3 += 1;
				if (p3 == n)
					p3 = 0;
			} while (p3 != p1);
			
			if (n2 < n)
			{
				xx[n2] = xbase + x1;
				yy[n2] = ybase + y1;
				n2++;
			} 
			else
			{
				count++;
				if (count > 10)
					return null;
			}
			p1 = p2;
		} while (p1 != pstart);
		
		return new Polygon(xx, yy, n2);
	}

}
