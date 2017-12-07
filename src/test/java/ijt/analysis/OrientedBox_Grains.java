package ijt.analysis;

import java.awt.Color;
import java.util.Map;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.Overlay;
import ij.gui.PolygonRoi;
import ij.process.ImageProcessor;

/**
 * Computes oriented bounding box of circles.tif image, and displays result
 * using ImageJ.
 * 
 * @author dlegland
 *
 */
public class OrientedBox_Grains
{
	public static final void main(String[] args)
	{
		@SuppressWarnings("unused")
		ImageJ ij = new ImageJ();
		
		String fileName = OrientedBox_Grains.class.getResource("/files/grains-WTH-areaOpen-lbl2.tif").getFile();
		ImagePlus imagePlus = IJ.openImage(fileName);
		imagePlus.show();

		ImageProcessor image = imagePlus.getProcessor();

		Map<Integer, OrientedBox2D> labelBoxMap = OrientedBox2D.orientedBox(image);
		Overlay ovr = new Overlay();
		for (OrientedBox2D box : labelBoxMap.values())
		{
			PolygonRoi roi = box.getRoi();
			roi.setStrokeColor(Color.BLUE);
			roi.setStrokeWidth(.5);
			ovr.add(roi);
		}
		imagePlus.setOverlay(ovr);
	}	
}
