package plugins.alfredangeline.celldetector;

import java.io.File;

import icy.image.IcyBufferedImage;
import icy.imagej.ImageJUtil;
import icy.sequence.Sequence;
import icy.sequence.SequenceUtil;
import ij.ImagePlus;
import plugins.tprovoost.Microscopy.MicroManager.MicroManager;
import plugins.tprovoost.Microscopy.MicroManager.tools.StageMover;

public class ImageCapture {
	
	public int[] exposure;
	public String[] Channels = {"1-LP409 ", "2-DAPI ", "3-QD565 ", "4-QD585 ", "5-QD605 ", "6-QD655 "};
	double[]  offset;
	File Destination;
	
	public ImageCapture(int[] exposure, double[] offset, File Destination)
	{
		this.exposure = exposure.clone();
		this.offset = offset.clone();
		this.Destination = Destination;
	}
	
	public ImagePlus getImage(int[] channels, double z_min, double z_max, int step) throws Exception
	{
		
		Sequence[] sequence = new Sequence[channels.length];
		Sequence seq;
		
		StageMover.moveZAbsolute(z_min, true);
		for(int i = 0; i < channels.length; i++)
		{
			seq = new Sequence();
			while(StageMover.getZ() < z_max)
			{	
				
				IcyBufferedImage im = MicroManager.snapImage();
				seq.addImage(im);
				StageMover.moveZRelative(step, true);
			}
			sequence[i] = seq;
		}
		
		seq = SequenceUtil.concatC(sequence);
		
		ImagePlus image = ImageJUtil.convertToImageJImage(seq, null);
		
		return image;
	}
	
	public ImagePlus getImage(int channel) throws Exception
	{
		
		Sequence sequence = new Sequence();
			
		IcyBufferedImage im = MicroManager.snapImage();
		sequence.addImage(im);
			
		
		ImagePlus image = ImageJUtil.convertToImageJImage(sequence, null);
		
		return image;
	}
	
	public ImagePlus getImage(int[] channels) throws Exception
	{
		
		Sequence[] sequence = new Sequence[channels.length];
		Sequence seq = new Sequence();
		
		for(int i = 0; i < channels.length; i++)
		{
			MicroManager.setConfigForGroup("Turret Refector", Channels[i], true);
			seq = new Sequence();
			IcyBufferedImage im = MicroManager.snapImage();
			seq.addImage(im);
			sequence[i] = seq;
		}
		
		ImagePlus image = ImageJUtil.convertToImageJImage(seq, null);
		
		return image;
	}

}
