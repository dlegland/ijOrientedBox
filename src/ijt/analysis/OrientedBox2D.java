/**
 * 
 */
package ijt.analysis;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

import ij.ImagePlus;
import ij.gui.Overlay;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.measure.ResultsTable;
import ij.process.ImageProcessor;
import inra.ijpb.label.LabelImages;

/**
 * Representation of object-oriented boxes, with static methods for computing
 * oriented boxes from point sets or directly from images.
 * 
 * @author dlegland
 *
 */
public class OrientedBox2D
{
	/**
	 * Computes the object-oriented bounding box of a set of points.
	 * 
	 * @param points
	 *            a list of points (not necessarily ordered)
	 * @return the oriented box of this set of points.
	 */
	public static final OrientedBox2D computeBox(ArrayList<? extends Point2D> points)
	{
		ArrayList<Point2D> convexHull = Polygons2D.convexHull_jarvis(points);
		
		// compute convex hull centroid
		Point2D center = Polygons2D.centroid(convexHull);
		double cx = center.getX();
		double cy = center.getY();
		
		// recenter the convex hull
		ArrayList<Point2D> centeredHull = new ArrayList<Point2D>(convexHull.size());
		for (Point2D p : convexHull)
		{
			centeredHull.add(new Point2D.Double(p.getX() - cx, p.getY() - cy));
		}
		
		FeretDiameters.AngleDiameterPair minFeret = FeretDiameters.minFeretDiameter(centeredHull);
//		FeretDiameters.AngleDiameterPair minFeret = FeretDiameters.minFeretDiameter(convexHull);
		
		
		// orientation of the main axis
		// pre-compute trigonometric functions
		double cot = Math.cos(minFeret.angle);
		double sit = Math.sin(minFeret.angle);

		// compute elongation in direction of rectangle length and width
		double xmin = Double.MAX_VALUE;
		double ymin = Double.MAX_VALUE;
		double xmax = Double.MIN_VALUE;
		double ymax = Double.MIN_VALUE;
		for (Point2D p : centeredHull)
		{
			// coordinates of current point
			double x = p.getX(); 
			double y = p.getY();
			
			// compute rotated coordinates
			double x2 = x * cot + y * sit; 
			double y2 = - x * sit + y * cot;
			
			// update bounding box
			xmin = Math.min(xmin, x2);
			ymin = Math.min(ymin, y2);
			xmax = Math.max(xmax, x2);
			ymax = Math.max(ymax, y2);
		}
		
		// position of the center with respect to the centroid compute before
		double dl = (xmax + xmin) / 2;
		double dw = (ymax + ymin) / 2;

		// change coordinates from rectangle to user-space
		double dx  = dl * cot - dw * sit;
		double dy  = dl * sit + dw * cot;

		// coordinates of oriented box center
		cx += dx;
		cy += dy;

		// size of the rectangle
		double length = ymax - ymin;
		double width  = xmax - xmin;
		
		// store angle in degrees
		double angle = Math.toDegrees(minFeret.angle);

		return new OrientedBox2D(cx, cy, length, width, angle);
	}
	
	/**
	 * Computes parameters of oriented box for each label of the input label
	 * image.
	 * 
	 * @param image
	 *            a label image (8, 16 or 32 bits)
	 * @return a ResultsTable containing oriented box parameters
	 */
	public final static ResultsTable orientedBox(ImageProcessor image)
	{
		// Check validity of parameters
		if (image == null)
			return null;

		// extract particle labels
		int[] labels = LabelImages.findAllLabels(image);
		int nLabels = labels.length;

        // For each label, create a list of corner points
        HashMap<Integer, ArrayList<Point2D>> labelCornerPoints = computeLabelsCorners(image, labels);
        
        OrientedBox2D[] oboxes = new OrientedBox2D[nLabels];
        for (int i = 0; i < nLabels; i++)
        {
        	oboxes[i] = computeBox(labelCornerPoints.get(labels[i]));
        }
        
		// Create data table
		ResultsTable table = new ResultsTable();

		// compute ellipse parameters for each region
		for (int i = 0; i < nLabels; i++) 
		{

			table.incrementCounter();
			table.addLabel(Integer.toString(labels[i]));
			// add coordinates of origin pixel (IJ coordinate system)
			OrientedBox2D obox = oboxes[i];
			table.addValue("Box.Center.X", 	obox.x0);
			table.addValue("Box.Center.Y",	obox.y0);
			table.addValue("Box.Length", 	obox.length);
			table.addValue("Box.Width", 	obox.width);
			table.addValue("Box.Orientation", obox.theta);
		}

		return table;
	}

	/**
	 * Returns a set of points located at the corners of a binary particle.
	 * Point coordinates are integer (ImageJ locates pixels in a [0 1]^d area.
	 * 
	 * @param image
	 *            a binary image representing the particle
	 * @return a list of points that can be used for convex hull computation
	 */
	public final static HashMap<Integer, ArrayList<Point2D>> computeLabelsCorners(ImageProcessor image, int[] labels)
	{
		int width = image.getWidth();
		int height = image.getHeight();
		
        // For each label, create a list of corner points
        HashMap<Integer, ArrayList<Point2D>> labelCornerPoints = new HashMap<>();
        for (int label : labels)
        {
        	labelCornerPoints.put(label, new ArrayList<Point2D>());
        }
		
		// for each row, add corner point for first and last pixel of each run-length
		for (int y = 0; y < height; y++)
		{
			// start from background
			int currentLabel = 0;

			// Identify transition inside and outside the each label
			for (int x = 0; x < width; x++)
			{
				int pixel = (int) image.getf(x, y);
				
				if (pixel > 0 && pixel != currentLabel)
				{

					// transition into a new region
					
					// if leave a region, add a new corner points for the end of the region
					if (currentLabel > 0)
					{
						ArrayList<Point2D> corners = labelCornerPoints.get(currentLabel);
						Point2D p = new Point2D.Double(x, y);
						if (!corners.contains(p))
						{
							corners.add(p);
						}
						corners.add(new Point2D.Double(x, y+1));
					}
					
					// add a new corner points for the beginning of the new region
					ArrayList<Point2D> corners = labelCornerPoints.get(pixel);
					Point2D p = new Point2D.Double(x, y);
					if (!corners.contains(p))
					{
						corners.add(p);
					}
					corners.add(new Point2D.Double(x, y+1));
					
					// update current label
					currentLabel = pixel;
				} 
				else if (pixel == 0 && currentLabel > 0)
				{
					// transition from a label to the  background 
					ArrayList<Point2D> corners = labelCornerPoints.get(currentLabel);
					Point2D p = new Point2D.Double(x, y);
					if (!corners.contains(p))
					{
						corners.add(p);
					}
					corners.add(new Point2D.Double(x, y+1));
					currentLabel = 0;
				}
			}
			
			// if particle touches right border, add another point
			if (currentLabel > 0)
			{
				ArrayList<Point2D> corners = labelCornerPoints.get(currentLabel);
				Point2D p = new Point2D.Double(width, y);
				if (!corners.contains(p))
				{
					corners.add(p);
				}
				corners.add(new Point2D.Double(width, y+1));
			}
		}
		
		return labelCornerPoints;
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
	public OrientedBox2D(double x0, double y0, double length, double width, double theta)
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
		
		// convert to overlay
		Overlay overlay = new Overlay();
		overlay.add(roi);
		
		// add overlay on current image
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
		
		// compute box vertex coordinates
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
