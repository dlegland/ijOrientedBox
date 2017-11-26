/**
 * 
 */
package ijt.analysis;

import ij.gui.PolygonRoi;
import ij.gui.Roi;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * A set of static methods operating on polygons.
 * 
 * @author dlegland
 *
 */
public class Polygons2D
{
	/**
	 * Private constructor to prevent instantiation.
	 */
	private Polygons2D()
	{
	}

	/**
	 * Computes the centroid of a polygon defined by an ordered list of
	 * vertices.
	 * 
	 * @param vertices
	 *            the ordered list of vertices
	 * @return the centroid of the polygon.
	 */
	public static final Point2D centroid(ArrayList<? extends Point2D> vertices)
	{
		// accumulators
		double sumC = 0;
		double sumX = 0;
		double sumY = 0;
		
		// iterate on vertex pairs
		int n = vertices.size();
		for (int i = 1; i <= n; i++)
		{
			Point2D p1 = vertices.get(i - 1);
			Point2D p2 = vertices.get(i % n);
			double x1 = p1.getX();
			double y1 = p1.getY();
			double x2 = p2.getX();
			double y2 = p2.getY();
			double common = x1 * y2 - x2 * y1;
			
			sumX += (x1 + x2) * common;
			sumY += (y1 + y2) * common;
			sumC += common;
		}
		
		// the area is the sum of the common factors divided by 2, 
		// but we need to divide by 6 for centroid computation, 
		// resulting in a factor 3.
		sumC *= 6 / 2;
		return new Point2D.Double(sumX / sumC, sumY / sumC);
	}
	
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
		// create array for storing polygon coordinates
		int n = vertices.size();
		int[] xCoords = new int[n];
		int[] yCoords = new int[n];
		
		// extract vertex coordinates
		for (int i = 0; i < n; i++)
		{
			Point vertex = vertices.get(i);
			xCoords[i] = vertex.x;
			yCoords[i] = vertex.y;
		}
		
		// create structure for storing convex hull coordinates
		ArrayList<Point> hull = new ArrayList<>();
		
		// find minimum bound in vertical direction
		int ymin = Integer.MAX_VALUE;
		for (int i = 0; i < n; i++)
		{
			ymin = Math.min(ymin, yCoords[i]);
		}
	
		// find left-most vertex of horizontal line with smallest y
		int p1 = 0;
		int xmin = Integer.MAX_VALUE;
		for (int i = 0; i < n; i++)
		{
			int x = xCoords[i];
			int y = yCoords[i];
			if (y == ymin && x < xmin)
			{
				xmin = x;
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
			int x1 = xCoords[p1];
			int y1 = yCoords[p1];
			
			// coordinates of next vertex candidate
			int p2 = (p1 + 1) % n;
			int x2 = xCoords[p2];
			int y2 = yCoords[p2];
	
			// find the next "wrapping" vertex by computing oriented angle
			int p3 = (p2 + 1) % n;
			do
			{
				int x3 = xCoords[p3];
				int y3 = yCoords[p3];
				
				// if V1-V2-V3 is oriented CW, use V3 as next wrapping candidate
				int det = x1 * (y2 - y3) - y1 * (x2 - x3) + (y3 * x2 - y2 * x3);
				if (det < 0)
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
	 * Uses the gift wrap algorithm to find the convex hull of a PolygonRoi, and
	 * returns it as a Polygon.
	 * 
	 * Code adapted from ij.gui.PolygonRoi.getConvexHull().
	 */
	public static final Polygon convexHull(PolygonRoi polygonRoi)
	{
		// get vertex coordinates
		int n = polygonRoi.getNCoordinates();
		int[] xCoordinates = polygonRoi.getXCoordinates();
		int[] yCoordinates = polygonRoi.getYCoordinates();
		
		// lower bounds of the polygon
		Rectangle r = polygonRoi.getBounds();
		int xbase = r.x;
		int ybase = r.y;
		
		// allocate array for convex hull coordinates
		int[] xx = new int[n];
		int[] yy = new int[n];
		
		// number of vertices in hull
		int n2 = 0;
		
		int x, y;

		// find bound in vertical direction
		int smallestY = Integer.MAX_VALUE;
		for (int i = 0; i < n; i++)
		{
			smallestY = Math.min(smallestY, yCoordinates[i]);
		}
	
		// find left-most vertex of horizontal line with smallest y
		int p1 = 0;
		int smallestX = Integer.MAX_VALUE;
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
		int count = 0;
		do
		{
			int x1 = xCoordinates[p1];
			int y1 = yCoordinates[p1];
			int p2 = (p1 + 1) % n; // modulo
			int x2 = xCoordinates[p2];
			int y2 = yCoordinates[p2];
			
			int p3 = (p2 + 1) % n; // modulo
			
			do
			{
				int x3 = xCoordinates[p3];
				int y3 = yCoordinates[p3];
				int det = x1 * (y2 - y3) - y1 * (x2 - x3) + (y3 * x2 - y2 * x3);
				if (det > 0)
				{
					x2 = x3;
					y2 = y3;
					p2 = p3;
				}
				p3 = (p3 + 1) % n; // modulo
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
