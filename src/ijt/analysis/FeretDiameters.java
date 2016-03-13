/**
 * 
 */
package ijt.analysis;

import ij.process.ImageProcessor;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * @author dlegland
 *
 */
public class FeretDiameters
{
	public final static double TWO_PI = Math.PI * 2;
	
	/**
	 * Computes Maximum Feret diameter from a single particle in a binary image.
	 *  
	 * @param image a binary image representing the particle.
	 * @return the maximum Feret diameter of the particle
	 */
	public final static AngleDiameterPair maxFeretDiameterSingle(ImageProcessor image)
	{
//		ArrayList<Point> points = boundaryPoints(image);
//		ArrayList<Point> convHull = ConvexHull.convexHull_jarvis_int(points);
		ArrayList<Point2D> points = binaryParticleCorners(image);
		ArrayList<Point2D> convHull = ConvexHull.convexHull_jarvis(points);

		return maxFeretDiameter(convHull);
	}
	
	/**
	 * Computes Maximum Feret diameter of a set of points.
	 * 
	 * Note: it is often a good idea to compute convex hull before computing Feret diameter.
	 *  
	 * @param points a collection of planar points
	 * @return the maximum Feret diameter of the point set
	 */
	public final static AngleDiameterPair maxFeretDiameter(ArrayList<? extends Point2D> points)
	{
		double distMax = Double.MIN_VALUE;
		double angleMax = 0;
		for (Point2D p1 : points)
		{
			for (Point2D p2 : points)
			{
				double dist = p1.distance(p2);
				distMax = Math.max(distMax, dist);
				angleMax = Math.atan2(p2.getY() - p1.getY(), p2.getX() - p1.getX());
			}
		}
	
		return new AngleDiameterPair(angleMax, distMax);
	}
	
	/**
	 * Computes Minimum Feret diameter from a single particle in a binary image.
	 *  
	 * @param image a binary image representing the particle.
	 * @return the minimum Feret diameter of the particle
	 */
	public final static AngleDiameterPair minFeretDiameterSingle(ImageProcessor image)
	{
		ArrayList<Point> points = boundaryPoints(image);
		ArrayList<Point> convHull = ConvexHull.convexHull_jarvis_int(points);
	
		return minFeretDiameter(convHull);
	}


	/**
	 * Computes Minimum Feret diameter of a set of points, using the rotating
	 * caliper algorithm.
	 * 
	 * @param points
	 *            a collection of planar points
	 * @return the minimum Feret diameter of the point set
	 */
	public final static AngleDiameterPair minFeretDiameter(ArrayList<? extends Point2D> points)
	{
		// first compute convex hull to simplify
		ArrayList<Point2D> convHull = ConvexHull.convexHull_jarvis(points);
		int n = convHull.size();
		
		// find index of extreme vertices in vertical direction
		int indA = 0;
		int indB = 0;
		double yMin = Double.MAX_VALUE;
		double yMax = Double.MIN_VALUE;
		for (int i = 0; i < n; i++)
		{
			Point2D p = convHull.get(i);
			double y = p.getY();
			if (y < yMin)
			{
				yMin = y;
				indA = i;
			}
			if (y > yMax)
			{
				yMax = y;
				indB = i;
			}
		}
		
		// Caliper A points along the positive x-axis
		Vector2D caliperA = new Vector2D(1, 0);
		// Caliper B points along the negative x-axis
		Vector2D caliperB = new Vector2D(-1, 0);
		
		// initialize result
		double width;
		double widthMin = Double.POSITIVE_INFINITY;
		double angleMin = 0;
		StraightLine2D line;
		
		// Find the direction with minimum width (rotating caliper algorithm)
		double rotatedAngle = 0;
		while (rotatedAngle < Math.PI)
		{
		    // compute the direction vector corresponding to first edge
		    int indA2 = (indA + 1) % n;
		    Point2D pA1 = convHull.get(indA);
		    Point2D pA2 = convHull.get(indA2);
		    Vector2D vectorA = new Vector2D(pA2.getX() - pA1.getX(), pA2.getY() - pA1.getY());
			
		    // compute the direction vector corresponding to second edge
		    int indB2 = (indB + 1) % n;
		    Point2D pB1 = convHull.get(indB);
		    Point2D pB2 = convHull.get(indB2);
		    Vector2D vectorB = new Vector2D(pB2.getX() - pB1.getX(), pB2.getY() - pB1.getY());
		    
		    // Determine the angle between each caliper and the next adjacent edge
		    // in the polygon 
		    double angleA = (Vector2D.angle(caliperA, vectorA) + TWO_PI) % TWO_PI;
		    double angleB = (Vector2D.angle(caliperB, vectorB) + TWO_PI) % TWO_PI;
		    
		    // increment rotatedAngle by the smallest of these angles
		    double angleIncrement = Math.min(angleA, angleB);
		    rotatedAngle += angleIncrement;
		    
		    // compute current width, and update opposite vertex
		    if (angleA < angleB)
		    {
		        line = new StraightLine2D(pA1, pA2);
		        width = line.distance(pB1);
		        indA = indA2;
		    }
		    else
		    {
		        line = new StraightLine2D(pB1, pB2);
		        width = line.distance(pA1);
		        indB = indB2;
		    }

		    // update minimum width and corresponding angle if needed
		    if (width < widthMin)
		    {
		        widthMin = width;
		        angleMin = rotatedAngle;
		    }
		}

		return new AngleDiameterPair(angleMin - Math.PI/2, widthMin);				
	}
	
	/**
	 * Computes Minimum Feret diameter of a set of points, using the rotating
	 * caliper algorithm.
	 * 
	 * @param points
	 *            a collection of planar points
	 * @return the minimum Feret diameter of the point set
	 */
	public final static AngleDiameterPair minFeretDiameterNaive(ArrayList<? extends Point2D> points)
	{
		// first compute convex hull to simplify
		ArrayList<Point2D> convHull = ConvexHull.convexHull_jarvis(points);
		int n = convHull.size();

		// initialize result
		double width;
		double widthMin = Double.POSITIVE_INFINITY;
		double angleMin = 0;
		StraightLine2D line;

		for (int i = 0; i < n; i++)
		{
			Point2D p1 = convHull.get(i);
			Point2D p2 = convHull.get((i + 1) % n);
			
			// avoid degenerated lines
			if (p1.distance(p2) < 1e-12)
			{
				continue;
			}

			// Compute the width for this polygon edge
			line = new StraightLine2D(p1, p2);
			width = 0;
			for (Point2D p : convHull)
			{
				double dist = line.distance(p);
				width = Math.max(width, dist);
			}
			
			// check if smallest width
			if (width < widthMin)
			{
				widthMin = width;
				double dx = p2.getX() - p1.getX();
				double dy = p2.getY() - p1.getY();
				angleMin = Math.atan2(dy, dx);
			}
		}
				
		return new AngleDiameterPair(angleMin - Math.PI/2, widthMin);				
	}
	
	/**
	 * Returns a set of points located at the corners of a binary particle.
	 * Point coordinates are integer (ImageJ locates pixels in a [0 1]^d area.
	 * 
	 * @param image
	 *            a binary image representing the particle
	 * @return a list of points that can be used for convex hull computation
	 */
	public final static ArrayList<Point2D> binaryParticleCorners(ImageProcessor image)
	{
		int width = image.getWidth();
		int height = image.getHeight();
		
		ArrayList<Point2D> points = new ArrayList<>();
		
		// try to fin a pair of points for each row
		for (int y = 0; y < height; y++)
		{
			// Identify transition inside and outside the particle 
			boolean inside = false;
			for (int x = 0; x < width; x++)
			{
				if (image.get(x, y) > 0 && !inside)
				{
					// transition from background to foreground
					Point2D p = new Point2D.Double(x, y);
					if (!points.contains(p))
					{
						points.add(p);
					}
					points.add(new Point2D.Double(x, y+1));
					inside = true;
				} 
				else if (image.get(x, y) == 0 && inside)
				{
					// transition from foreground to background 
					Point2D p = new Point2D.Double(x, y);
					if (!points.contains(p))
					{
						points.add(p);
					}
					points.add(new Point2D.Double(x, y+1));
					inside = false;
				}
			}
			
			// if particle touches right border, add another point
			if (inside)
			{
				Point2D p = new Point2D.Double(width, y);
				if (!points.contains(p))
				{
					points.add(p);
				}
				points.add(new Point2D.Double(width, y+1));
			}
		}
		
		return points;
	}

	/**
	 * Returns a set of boundary points from a binary image.
	 * 
	 * @param image
	 *            a binary image representing the particle
	 * @return a list of points that can be used for convex hull computation
	 */
	public final static ArrayList<Point> boundaryPoints(ImageProcessor image)
	{
		int width = image.getWidth();
		int height = image.getHeight();
		
		ArrayList<Point> points = new ArrayList<>();
		
		// try to fin a pair of points for each row
		for (int y = 0; y < height; y++)
		{
			// Identify transition inside and outside the particle 
			boolean inside = false;
			for (int x = 0; x < width; x++)
			{
				if (image.get(x, y) > 0 && !inside)
				{
					// transition from background to foreground
					points.add(new Point(x, y));
					inside = true;
				} 
				else if (image.get(x, y) == 0 && inside)
				{
					// transition from foreground to background 
					points.add(new Point(x-1, y));
					inside = false;
				}
			}
			
			// if particle touches right border, add another point
			if (inside)
			{
				points.add(new Point(width-1, y));
			}
		}
		
		return points;
	}

	/**
	 * Data structure used to return result of feret diameter computation.
	 * 
	 * @author dlegland
	 *
	 */
	public static class AngleDiameterPair
	{
		/** Angle in radians */
		public double angle;

		/** Diameter computed in the direction of the angle */
		public double diameter;
		
		public AngleDiameterPair(double angle, double diameter)
		{
			this.angle = angle;
			this.diameter = diameter;
		}
	}
}
