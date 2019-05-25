package plugins.tkkoba1997.getallzstacks;

import plugins.tprovoost.Microscopy.MicroManager.MicroManager;
import plugins.tprovoost.Microscopy.MicroManager.tools.StageMover;
import java.util.concurrent.TimeUnit;
import java.io.File;
import icy.file.Saver;
import icy.image.IcyBufferedImageUtil;
import icy.sequence.Sequence;
import icy.type.collection.array.Array1DUtil;
import icy.image.IcyBufferedImage;

// This code is functionally equivalent to the GetZStacks code except:
// 1. The Z stacks are acquired for a single light filter THEN the turret changes filter
// 2. A single .tiff file is produced with all the zstacks in it as opposed to six separate image files. 
public class getStacks {
			@SuppressWarnings("deprecation")
			public void getZStacks(File filePath, double zoom_range, double z_increment, 
								  double[] zcorrections, int[] expsr_corrections) { 

			double current_z = 0.0;
			double initial_z_position = 0.0;
			try { initial_z_position = StageMover.getZ(); } catch (Exception e1) { e1.printStackTrace(); }
			
			int delay_interval = 100;	//Delay to make certain hardware (HW) keeps up with software (SW). 
			int zoom_degree = 0;		// An integer loop variable for the z-stack acquisition loops.
			int channel_state = 0;		// Turret's light reflector/filter position/state.
			
			Sequence sequence0 =  new Sequence();

			IcyBufferedImage image = null;
			try { image = MicroManager.snapImage(); } catch (Exception e) { e.printStackTrace(); }
			try {MicroManager.getCore().waitForDevice("CoolSNAP ES2");} catch (Exception e4) {e4.printStackTrace();}

			
			// Need an images to constantly append slices to. 
			// Initialize to a snapped image so XY dimensions are all equal.
			IcyBufferedImage image0 = image;
			
			// Create an array out of the image data.
			Object dataArray = image.getDataCopyXY(0);
			// Convert and store it to a double type array, making note if the original was signed data type.
			double[] doubleDataArray = Array1DUtil.arrayToDoubleArray(dataArray, image.isSignedDataType());
			
			// Some initialization/defaults
			int camera_exposure = 125;
			try { MicroManager.setExposure((double)camera_exposure); } catch (Exception e2) { e2.printStackTrace(); }
			
			/////////////////////////////Z-stack image acquisition loops begin ////////////////////////////////////////
			// The for-loop cycles through all the z-positions for the z-stack.
				for (channel_state=0; channel_state <= 5; channel_state++){
				zoom_degree = 0;
				
				
				try { MicroManager.getCore().setProperty("ZeissReflectorTurret", "State", channel_state); } catch (Exception e) { e.printStackTrace(); }
				try { MicroManager.getCore().waitForDevice("ZeissReflectorTurret"); } catch (Exception e3) { e3.printStackTrace(); }
				
				current_z = 0.0;	
				try { StageMover.moveZAbsolute(initial_z_position-zoom_range); } catch (Exception e) { e.printStackTrace(); }
				try { current_z = StageMover.getZ(); } catch (Exception e1) { e1.printStackTrace(); }
				try {MicroManager.getCore().waitForDevice("ZeissXYStage"); } catch (Exception e3) {e3.printStackTrace(); }
				try { StageMover.moveZAbsolute(current_z + zcorrections[channel_state]); } catch (Exception e) { e.printStackTrace(); }
				
				camera_exposure = expsr_corrections[channel_state];
				try { MicroManager.setExposure((double)camera_exposure); } catch (Exception e2) { e2.printStackTrace(); }
				
				
				// The do-while loop (you see the "do" below) cycles through all the z_positions.
				for (double current_zoom = 0.0; current_zoom <= (zoom_range * 2 + 0.001); current_zoom += z_increment) {			
					// Need to correct z_positions to reflect different focuses of different channels. Same with camera exposure times.
					
					System.out.println("Channel State: "+channel_state+"    Zoom = "+current_zoom+"    Zoom degree:"+zoom_degree+"    Corrected Z-position:   "+(current_z + zcorrections[channel_state])+"\n");
					
					// Snap the image and wait for the HW to get the job done.
					try { image = MicroManager.snapImage(); } catch (Exception e1) { e1.printStackTrace(); }
					try {MicroManager.getCore().waitForDevice("CoolSNAP ES2");} catch (Exception e2) {e2.printStackTrace();}

					// Code for saving/manipulating images begins here
					// Create a single frame image the first time a z-slice is taken under each channel.
					if (zoom_degree==0) { image0 = image; }
					// If first z-slice has already been acquired, then:
					// Add blank frame to image.
					// Begin an update on the image.
					// Create an array (matrix) with a copy of the pixel data of the new <image> snapped.
					// Convert that data to type double (floating point) values.
					// Convert the doubleDataArray to the original data type of <image> and store in corresponding image<particular_channel>
					// End update on image<particular_channel>. Recalculation & refresh performed once, as opposed to manual for loop modification of individual pixels. 
					else {
						image0 = IcyBufferedImageUtil.addChannel(image0);
						image0.beginUpdate();
						dataArray = image.getDataCopyXY(0);
						doubleDataArray = Array1DUtil.arrayToDoubleArray(dataArray, image.isSignedDataType());
						Array1DUtil.doubleArrayToSafeArray(doubleDataArray, image0.getDataXY(zoom_degree), image0.isSignedDataType());
						image0.endUpdate(); }
				
				zoom_degree+=1;
				try { current_z = StageMover.getZ(); } catch (Exception e1) { e1.printStackTrace(); }
				try { StageMover.moveZAbsolute(current_z + z_increment); } catch (Exception e) { e.printStackTrace(); }
				}
				
				// Store the image, holding multiple z-slices, in the sequence.
				sequence0.setImage(channel_state, 0, image0);
					
			}  ////////////////////End Z-stack Acquisition Loops/////////////////////////////////////////////
			
			// Restore stage to initial_z_position.
			try { StageMover.moveZAbsolute(initial_z_position); } catch (Exception e1) { e1.printStackTrace(); }
				
			double x_position = 0.0;
			double y_position = 0.0;
			try { x_position = StageMover.getX(); } catch (Exception e) { e.printStackTrace(); }
			try { y_position = StageMover.getY(); } catch (Exception e) { e.printStackTrace(); }
			
			// Write out the sequence containing the z-stacks in the user specified directory.
			//Check the correction of adding "File " before "file = new..."
			File file = new File(filePath +"/"+"x"+ x_position + "y" + y_position+".tiff");
			

			Saver.save(sequence0, file, false);
			}
}
