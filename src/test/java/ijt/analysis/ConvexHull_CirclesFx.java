package ijt.analysis;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

/**
 * Run convex hull algorithm on circles.tif image, and display result using JavaFX.
 * 
 * @author dlegland
 *
 */
public class ConvexHull_CirclesFx extends Application
{
	ImageProcessor image = null;
	
//	double scale = 2;
	
	@Override
	public void start(Stage stage) 
	{
		String fileName = new File("files/circles.tif").getPath();
		ImagePlus imagePlus = IJ.openImage(fileName);
		ImageProcessor image = imagePlus.getProcessor();

		ArrayList<Point> points = FeretDiameters.boundaryPoints(image);
		System.out.println("number of points: " + points.size());

		ArrayList<Point> convHull = Polygons2D.convexHull_jarvis_int(points);
		
		ImageView imv = new ImageView();
		imv.setImage(convertImage(image));
		imv.setSmooth(false);
		
		Group root = new Group();

		root.getChildren().add(imv);
        
		Polygon poly = createPolygon(convHull);
        poly.setFill(null);
        poly.setStroke(Color.GREEN);
        poly.setStrokeWidth(1);
        root.getChildren().add(poly);
                
        Group ptsView = new Group();
        for (Point p : points)
        {
        	Circle circle = new Circle(p.x, p.y, 1);
        	circle.setStroke(Color.RED);
        	circle.setFill(Color.RED);
        	ptsView.getChildren().add(circle);
        }

        root.getTransforms().addAll(new Scale(2, 2));
        
		Scene scene = new Scene(root, 600, 650);

        stage.setTitle("My JavaFX Application");
        stage.setScene(scene);
        stage.show();
    }
	
	public Image convertImage(ImageProcessor image)
	{
		int width = image.getWidth();
		int height = image.getHeight();
		WritableImage imageFx = new WritableImage(width, height);
		
		// Initialize image values
		PixelWriter pixelWriter = imageFx.getPixelWriter();
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				double value = image.getf(x, y);
				value = Math.max(Math.min(value, 255), 0);
				int g8 = ((int) value)  & 0x00FF;
				pixelWriter.setColor(x, y, Color.rgb(g8, g8, g8));
			}
		}

		return imageFx;
	}
	
	public Polygon createPolygon(ArrayList<Point> coords)
	{
		Polygon poly = new Polygon();
		for (Point p : coords)
		{
			poly.getPoints().add(((double) p.x) + .5);
			poly.getPoints().add(((double) p.y) + .5);
		}
		return poly;
	}
	

	public static final void main(String[] args)
	{
		 launch(args);
	}
}
