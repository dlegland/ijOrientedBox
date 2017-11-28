
package ijt.analysis;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

public class ConvexHullJarvisDemo extends Application
{
	
	ArrayList<Point> polygon = new ArrayList<Point>(0);
	
	ArrayList<Point> convHull = null;
	
	@Override
	public void start(Stage stage) 
	{
		initializePolygon_random(500);
		convHull = Polygons2D.convexHull_jarvis_int(polygon);
		
        Group root = new Group();
        for (Point p : polygon)
        {
        	root.getChildren().add(new Circle(p.x, p.y, 2));
        }
        Polygon poly = createPolygon(convHull);
        poly.setFill(null);
        poly.setStroke(Color.BLUE);
        root.getChildren().add(poly);
        
        Scene scene = new Scene(root, 400, 350);

        stage.setTitle("My JavaFX Application");
        stage.setScene(scene);
        stage.show();
    }
	
	public void initializePolygon_sedgewick()
	{
		polygon = new ArrayList<Point>(16);
		int[] px = new int[]{3, 11, 6, 4, 5, 8, 1, 7, 9, 14, 10, 16, 15, 13, 2, 12};
		int[] py = new int[]{9, 1, 8, 3, 15, 11, 6, 4, 7, 5, 13, 14, 2, 16, 12, 10};
		for (int i = 0; i < px.length; i++)
		{
			polygon.add(new Point(px[i] * 20, py[i] * 20));	
		}
	}

	public void initializePolygon_random(int nPolygons)
	{
		polygon = new ArrayList<Point>(nPolygons);
		Random rand = new Random();
		for (int i = 0; i < nPolygons; i++)
		{
			polygon.add(new Point(rand.nextInt(300)+20, rand.nextInt(300)+20));	
		}
	}

	public Polygon createPolygon(ArrayList<Point> coords)
	{
		Polygon poly = new Polygon();
		for (Point p : coords)
		{
			poly.getPoints().add((double) p.x);
			poly.getPoints().add((double) p.y);
		}
		return poly;
	}
	
	public static final void main(String[] args)
	{
		 launch(args);
	}
}
