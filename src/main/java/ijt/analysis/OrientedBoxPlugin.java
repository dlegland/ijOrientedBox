/*-
 * #%L
 * Mathematical morphology library and plugins for ImageJ/Fiji.
 * %%
 * Copyright (C) 2014 - 2017 INRA.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package ijt.analysis;

import java.util.Map;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.measure.ResultsTable;
import ij.plugin.filter.PlugInFilter;
import ij.plugin.frame.RoiManager;
import ij.process.ImageProcessor;
import inra.ijpb.label.LabelImages;

public class OrientedBoxPlugin implements PlugInFilter
{
	// ====================================================
	// Global Constants

	// ====================================================
	// Class variables

	/**
	 * When this options is set to true, information messages are displayed on
	 * the console.
	 */
	public boolean debug = true;

	ImagePlus imagePlus;

	// ====================================================
	// Calling functions

	/*
	 * (non-Javadoc)
	 * 
	 * @see ij.plugin.filter.PlugInFilter#setup(java.lang.String, ij.ImagePlus)
	 */
	@Override
	public int setup(String arg, ImagePlus imp)
	{
		if (imp == null)
		{
			IJ.noImage();
			return DONE;
		}

		this.imagePlus = imp;
		return DOES_ALL | NO_CHANGES;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ij.plugin.PlugIn#run(java.lang.String)
	 */
	public void run(ImageProcessor ip)
	{
		// check if image is a label image
		if (!LabelImages.isLabelImageType(imagePlus))
		{
			IJ.showMessage("Input image should be a label image");
			return;
		}

		// create the list of image names
		int[] indices = WindowManager.getIDList();
		String[] imageNames = new String[indices.length];
		for (int i=0; i<indices.length; i++)
		{
			imageNames[i] = WindowManager.getImage(indices[i]).getTitle();
		}
		
		// name of selected image
		String selectedImageName = IJ.getImage().getTitle();
		
		// create the dialog
		GenericDialog gd = new GenericDialog("Oriented Box");
//		gd.addChoice("Label Image:", imageNames, selectedImageName);
		gd.addCheckbox("Show Overlay Result", true);
		gd.addChoice("Image to overlay:", imageNames, selectedImageName);
		gd.addCheckbox("Export to ROI Manager", true);
		gd.showDialog();
		
		if (gd.wasCanceled())
			return;
		
		// parse current parameters
		boolean showOverlay = gd.getNextBoolean();
		int overlayImageIndex = gd.getNextChoiceIndex();
		boolean exportToRoiManager = gd.getNextBoolean();

		// Execute the plugin
		IJ.showStatus("Compute Oriented Boxes");
		Map<Integer, OrientedBox2D> labelBoxMap = OrientedBox2D.orientedBox(imagePlus.getProcessor());
		int nBoxes = labelBoxMap.size(); 

		// Show results table
		IJ.showStatus("Convert To Table");
		String tableName = imagePlus.getShortTitle() + "-OBoxes";
		ResultsTable table = OrientedBox2D.asTable(labelBoxMap);
		table.show(tableName);

		// Optionally overlay on an image
		ImagePlus overlayImage = WindowManager.getImage(indices[overlayImageIndex]);
		if (showOverlay)
		{
			IJ.showStatus("Compute box overlay");
			Overlay overlay = new Overlay();
			int count = 0;
			for (OrientedBox2D box : labelBoxMap.values())
			{
				IJ.showProgress(count++, nBoxes);
				overlay.add(box.getRoi());
			}
			overlayImage.setOverlay(overlay);
			
			IJ.showProgress(1);
		}
		
		// Export Oriented Boxed to ROI Manager
		if (exportToRoiManager)
		{
			IJ.showStatus("Compute box ROI");
			// get instance of ROI Manager
			RoiManager manager = RoiManager.getRoiManager();
			int index = 0;
			for (Map.Entry<Integer, OrientedBox2D> entry : labelBoxMap.entrySet())
			{
				IJ.showProgress(index, nBoxes);
				int label = entry.getKey();
				Roi roi = entry.getValue().getRoi();
				int nDigits = ((int) Math.log10(nBoxes)) + 1;
				roi.setName(String.format("label-%0" + nDigits +"d", label));
//				IJ.log(roi.getName());
				manager.add(overlayImage, roi, 0);
				// enforce the name of the ROI
				manager.rename(index, roi.getName());
				index++;
			}
			IJ.showProgress(1);
		}
		
		IJ.showStatus("");
	}

//	/**
//	 * Main body of the plugin.
//	 * 
//	 * @param imagePlus
//	 *            the image to analyze
//	 * @return the ResutlsTable describing each label
//	 */
//	public ResultsTable process(ImagePlus imagePlus)
//	{
//		// Check validity of parameters
//		if (imagePlus == null)
//			return null;
//
//		if (debug)
//		{
//			System.out.println("Compute Oriented Boxes on Label image '"
//					+ imagePlus.getTitle());
//		}
//
//		ImageProcessor image = imagePlus.getProcessor();
//
//		// Extract spatial calibration
//		Calibration cal = imagePlus.getCalibration();
//		double[] resol = new double[] { 1, 1 };
//		if (cal.scaled())
//		{
//			resol[0] = cal.pixelWidth;
//			resol[1] = cal.pixelHeight;
//		}
//
//		ResultsTable results = OrientedBox2D.asTable(OrientedBox2D.orientedBox(image));
//
//		// return the created array
//		return results;
//	}

}
