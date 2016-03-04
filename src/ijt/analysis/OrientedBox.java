/**
 * 
 */
package ijt.analysis;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;

import ij.ImagePlus;
import ij.gui.Overlay;
import ij.gui.PolygonRoi;
import ij.gui.Roi;

/**
 * Representation of object-oriented boxes, with static methods for computing
 * oriented boxes from point sets or directly from images.
 * 
 * @author dlegland
 *
 */
public class OrientedBox
{
	public static final OrientedBox computeBox(ArrayList<? extends Point2D> points)
	{
		ArrayList<Point2D> convexHull = ConvexHull.convexHull_jarvis(points);
		
		// compute convex hull centroid
		
		return null;
		
		
	}
	
	public static final Point2D polygonCentroid(ArrayList<? extends Point2D> vertices)
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
		
		sumC *= 6;
		return new Point2D.Double(sumX / sumC, sumY / sumC);
	}
	
	
	/** X-coordinate of oriented box center */
	double x0;

	/** Y-coordinate of oriented box center */
	double y0;
	
	/** The larger dimension of the box */
	double length;
	
	/** The smaller dimension of the box */
	double width;
	
	/**
	 * The orientation of the box, in degrees, counted counter-clockwise from
	 * the horizontal.
	 */
	double theta;
		
	/**
	 * Creates a new oriented box instance.
	 * 
	 * @param x0
	 *            X-coordinate of oriented box center
	 * @param y0
	 *            Y-coordinate of oriented box center
	 * @param length
	 *            The larger dimension of the box
	 * @param width
	 *            The smaller dimension of the box
	 * @param theta
	 *            The orientation of the box, in degrees, counted
	 *            counter-clockwise from the horizontal.
	 */
	public OrientedBox(double x0, double y0, double length, double width, double theta)
	{
		this.x0 = x0;
		this.y0 = y0;
		this.length = length;
		this.width = width;
		this.theta = theta;
	}
	
	/**
	 * Draws this instance of oriented box on the specified image. 
	 * @param imp
	 */
	public void draw(ImagePlus imp)
	{
		Roi roi = this.getRoi();
		
		Overlay overlay = new Overlay();
		overlay.add(roi);
		
		imp.setOverlay(overlay);
	}
	
	/**
	 * Converts this oriented box into a PolygonRoi instance that can be
	 * displayed on images.
	 * 
	 * @return an instance of PolygonRoi
	 */
	public PolygonRoi getRoi()
	{
		// pre-compute angle functions
		double thetaRadians = Math.toRadians(this.theta);
		double cot = Math.cos(thetaRadians);
		double sit = Math.sin(thetaRadians);

		// use half-size to simplify computations
		double l2 = this.length / 2;
		double w2 = this.width / 2;
		
		float[] px = new float[4]; 
		float[] py = new float[4]; 
		px[0] = (float) ( l2 * cot - w2 * sit + this.x0);
		py[0] = (float) ( l2 * sit + w2 * cot + this.y0);
		px[1] = (float) ( l2 * cot + w2 * sit + this.x0);
		py[1] = (float) ( l2 * sit - w2 * cot + this.y0);
		px[2] = (float) (-l2 * cot + w2 * sit + this.x0);
		py[2] = (float) (-l2 * sit - w2 * cot + this.y0);
		px[3] = (float) (-l2 * cot - w2 * sit + this.x0);
		py[3] = (float) (-l2 * sit + w2 * cot + this.y0);
		
		return new PolygonRoi(px, py, 4, Roi.POLYGON);
	}
}
