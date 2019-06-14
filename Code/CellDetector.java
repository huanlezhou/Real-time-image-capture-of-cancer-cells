package plugins.alfredangeline.celldetector;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import icy.file.Loader;
import icy.imagej.ImageJUtil;
import icy.sequence.Sequence;
import icy.sequence.SequenceUtil;
import icy.util.XLSUtil;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.io.FileSaver;
import ij.measure.ResultsTable;
import ij.plugin.Concatenator;
import ij.plugin.filter.EDM;
import ij.plugin.filter.ParticleAnalyzer;
import ij.plugin.frame.RoiManager;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import jxl.read.biff.BiffException;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import plugins.tprovoost.Microscopy.MicroManager.tools.StageMover;

public class CellDetector{

	public double activeThreshold;
	public int activeChannel;
	public int nucleusChannel;
	public int cellCounter;
	public int activeCellCounter;
	
	public Sequence images;
	public ImagePlus nucleus, active, outputImage;
	
	public WritableWorkbook workbook;
	public File destinationFile;
	public WritableSheet page;
	public int sheetRow, sheetColumn;
	
	public CellDetector(double threshold, int nucleus, int active, File destination)
	{
		nucleusChannel = nucleus;
		activeChannel = active;
		activeThreshold = threshold;
		destinationFile = destination;
		CreateXLSfile(destination);
	}
	

	
	public void DetectCell(Sequence data)
	{
		JFileChooser fc = new JFileChooser();
	    fc.setCurrentDirectory(new java.io.File("."));
	    fc.setDialogTitle("Please choose a sequence to open ");
	    fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
	    fc.setAcceptAllFileFilterUsed(false);

	    if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) 
	    {
	    	if(fc.getSelectedFile() == null)
	 	    {
	 	    	IJ.showMessage("User Cancel !");
	 	    	return;
	 	    }
	    	else
	    	{
	    		File file = fc.getSelectedFile();
	    		String filePath = file.getAbsolutePath();
				images = Loader.loadSequence(filePath, 0, false);
				
				cell_counter(images);
				Concatenator im = new Concatenator();
				outputImage = im.concatenate(nucleus.flatten(), active.flatten(), true);
				
				FileSaver savefile = new FileSaver(outputImage);
				
				File fs = new File(destinationFile.getParent() + "/ProcessedImages");
				fs.mkdir();
				savefile.saveAsTiff( fs +"/"+ images.getName());
	    	}
	      
	    } else {
	      System.out.println("No Selection ");
	    }
	    
	    
			
	}
	
	public void cell_counter(Sequence sequence)
	{
		activeCellCounter = 0;
		cellCounter = 0;
		Sequence nucleus1 = SequenceUtil.extractChannel(sequence, nucleusChannel);
		
		nucleus = ImageJUtil.convertToImageJImage(nucleus1, null);
		ImagePlus image = nucleus.duplicate();
		RoiManager manager = new RoiManager(true);
		Roi[] nucleusResult = detectCells(image);
		cellCounter = nucleusResult.length;
		//manager.moveRoisToOverlay(nucleus);
		for(int i = 0; i < nucleusResult.length; i++)
		{
			manager.addRoi(nucleusResult[i]);
		}
		manager.moveRoisToOverlay(nucleus);
		
		Sequence active1 = SequenceUtil.extractChannel(sequence, activeChannel);
		active = ImageJUtil.convertToImageJImage(active1, null);
		
		RoiManager manager1 = new RoiManager(true);
		
		double bg = active.getProcessor().getStats().min;
		double threshold = bg * activeThreshold;
		
		for(int i = 0; i < nucleusResult.length; i++)
		{
				active.setRoi(nucleusResult[i]);
				
				if(active.getStatistics().mean > threshold)
				{
					manager1.addRoi(nucleusResult[i]);
					activeCellCounter++;
				}
		}
		manager1.moveRoisToOverlay(active);
		UpdateXLSfile(sequence);
	}
	
	public Roi[] detectCells(ImagePlus image)
	{
		ImageProcessor iProcessor;
		iProcessor = image.getProcessor();
		image.setProcessor(null, iProcessor.convertToByte(true));
		iProcessor = image.getProcessor();
		iProcessor.smooth();
		iProcessor.autoThreshold();
		iProcessor.invertLut();
		EDM edm = new EDM();
		edm.toWatershed(iProcessor);
		
		RoiManager manager = new RoiManager(true);
		ParticleAnalyzer.setRoiManager(manager);
		ResultsTable resultTable = new ResultsTable();
		int options = ParticleAnalyzer.ADD_TO_MANAGER | ParticleAnalyzer.SHOW_OUTLINES;

		
		ParticleAnalyzer particleAnalyzer = new ParticleAnalyzer(options, 0, resultTable, 20, Double.MAX_VALUE, 0.3, 1);
		particleAnalyzer.setHideOutputImage(true);
		
		ParticleAnalyzer.setLineWidth(12);
		particleAnalyzer.analyze(image);
		//outputImage = particleAnalyzer.getOutputImage();
		
		return manager.getRoisAsArray();
	}
	
	
	public void CreateXLSfile(File destination)
	{
		sheetRow = 0;
		sheetColumn = 0;
		
		if(destination.isFile())
		{
			String filePath = destination.getParent();
			destinationFile = new File(filePath + "/result.xls");
		}
		else
		{
			destinationFile = new File(destination + "/result.xls");
		}
		
		try {
			
			workbook = XLSUtil.createWorkbook(destinationFile);
			page = XLSUtil.createNewPage(workbook, "result");
			
			XLSUtil.setCellString(page, sheetColumn++, sheetRow, "Image");
			XLSUtil.setCellString(page, sheetColumn++, sheetRow, "Stage: X");
			XLSUtil.setCellString(page, sheetColumn++, sheetRow, "Stage: Y");
			XLSUtil.setCellString(page, sheetColumn++, sheetRow, "Stage: Z");
			XLSUtil.setCellString(page, sheetColumn++, sheetRow, "cell nucleus");
			XLSUtil.setCellString(page, sheetColumn++, sheetRow, "active cell");
			XLSUtil.saveAndClose(workbook);
			
		} catch (IOException | WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
	}
	
	
	public void UpdateXLSfile(Sequence data)
	{
		sheetColumn = 0;
		sheetRow++;
		try {
					//icy.type.point.Point3D.Double stage = StageMover.getXYZ();
			
					workbook = XLSUtil.loadWorkbookForWrite(destinationFile);
					page = XLSUtil.getPage(workbook, "result");
					
					XLSUtil.setCellString(page, sheetColumn++, sheetRow, data.getName());
					XLSUtil.setCellNumber(page, sheetColumn++, sheetRow, 0);  //stage.x
					XLSUtil.setCellNumber(page, sheetColumn++, sheetRow, 0); //stage.y
					XLSUtil.setCellNumber(page, sheetColumn++, sheetRow, 0); //stage.z
					XLSUtil.setCellNumber(page, sheetColumn++, sheetRow, cellCounter);
					XLSUtil.setCellNumber(page, sheetColumn++, sheetRow, activeCellCounter);
					XLSUtil.saveAndClose(workbook);
					
				} catch (IOException | WriteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (BiffException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}

	
}
