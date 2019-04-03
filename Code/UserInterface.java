package plugins.ashten2.userinterface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import icy.file.Loader;
import icy.file.Saver;
import icy.gui.dialog.MessageDialog;
import icy.image.IcyBufferedImage;
import icy.image.IcyBufferedImageUtil;
import icy.sequence.Sequence;
import icy.type.collection.array.Array1DUtil;
import plugins.adufour.ezplug.EzButton;
import plugins.adufour.ezplug.EzGroup;
import plugins.adufour.ezplug.EzLabel;
import plugins.adufour.ezplug.EzPlug;
import plugins.adufour.ezplug.EzStoppable;
import plugins.adufour.ezplug.EzVarBoolean;
import plugins.adufour.ezplug.EzVarDouble;
import plugins.adufour.ezplug.EzVarInteger;
import plugins.adufour.ezplug.EzVarText;
import plugins.alfredangeline.ActiveCellDetector.CellDetector;
//import plugins.tkkoba1997.acquirezstack.AcquireZStack;
import plugins.tprovoost.Microscopy.MicroManager.MicroManager;
import plugins.tprovoost.Microscopy.MicroManager.tools.StageMover;
import plugins.tprovoost.scripteditor.uitools.filedialogs.FileDialog;

public class Microscopy2 extends EzPlug implements EzStoppable {
	
	boolean stopFlag;

	double x_location, y_location;
	
	double captured_left_x, captured_right_x, captured_top_y, captured_bottom_y, captured_z_focus, z_depth;
	
	// Initializes variable for the default channel focus offset and exposure times
	final double CH1_OFFSET = -1, CH2_OFFSET = 0, CH3_OFFSET = 0, CH4_OFFSET = -0.5, CH5_OFFSET = -1, CH6_OFFSET = -1.5, NUMBER_OF_SLICES  = 10, SLICE_STEP_SIZE = 1;
	final int CH1_EXP = 500, CH2_EXP = 500, CH3_EXP = 500, CH4_EXP = 500, CH5_EXP = 500, CH6_EXP = 500; 
	
	EzVarDouble signal_noise_ratio, ch1_offset, ch2_offset, ch3_offset, ch4_offset, ch5_offset, ch6_offset, number_of_slices, slice_step_size;

	EzVarInteger ch1_exp, ch2_exp, ch3_exp, ch4_exp, ch5_exp, ch6_exp;
	
	EzVarText ch1_name, ch2_name, ch3_name, ch4_name, ch5_name, ch6_name;
	
	EzVarBoolean advanced_options;
	
	File save_location;
	JFileChooser fc;
	/**
	 * Following code performs an actions based on button presses from the user
	 * Each Button corresponds to a specific location/stage position
	 * When the button is pressed by the user the corresponding stage location is saved in variable
	 * for use in determining the starting and ending position of the sample
	 */
	
	// Action to capture the left sample position
	ActionListener get_x_left = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				captured_left_x = StageMover.getX();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	};
	EzButton Left_X = new EzButton("Left X", get_x_left);
	
	// Action to capture the right sample position
	ActionListener get_x_right = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				captured_right_x = StageMover.getX();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	};
	EzButton Right_X = new EzButton("Right X", get_x_right);
	
	// Action to capture the top sample position
	ActionListener get_y_top = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				captured_top_y = StageMover.getY();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	};
	EzButton Top_Y = new EzButton("Top Y", get_y_top);
	
	// Action to capture the bottom sample position
	ActionListener get_y_bottom = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				captured_bottom_y = StageMover.getY();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	};
	EzButton Bottom_Y = new EzButton("Btm Y", get_y_bottom);
	
	// Action to capture the stage height, when the sample is in focus
	ActionListener get_z_focus = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				captured_z_focus = StageMover.getZ();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	};
	EzButton Z_focus = new EzButton("Focus", get_z_focus);
	
	// Action to choose the save location for the images taken
	ActionListener save_directory = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				fc.showSaveDialog(null);
				save_location = fc.getSelectedFile();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	};
	EzButton directory = new EzButton("Directory", save_directory);
	
	/**
	 * Initializes the variables for creating the GUI, and enumerating default values
	 */
	@Override
	protected void initialize() {
		// TODO Auto-generated method stub
		
		// Allows the user to set the threshold value for determining the presence of active cells
		signal_noise_ratio = new EzVarDouble("Threshold Intensity");
		
		// Allows the user to enter the focus offsets for different filters, initializes with default values
		ch1_offset = new EzVarDouble("Ch1. Offset", CH1_OFFSET, -5, 5, 0.25);
		ch2_offset = new EzVarDouble("Ch2. Offset", CH2_OFFSET, -5, 5, 0.25);
		ch3_offset = new EzVarDouble("Ch3. Offset", CH3_OFFSET, -5, 5, 0.25);
		ch4_offset = new EzVarDouble("Ch4. Offset", CH4_OFFSET, -5, 5, 0.25);
		ch5_offset = new EzVarDouble("Ch5. Offset", CH5_OFFSET, -5, 5, 0.25);
		ch6_offset = new EzVarDouble("Ch6. Offset", CH6_OFFSET, -5, 5, 0.25);
		
		// Allows the user to enter modify the exposure time for different filters
		ch1_exp = new EzVarInteger("Ch1. exposure time", CH1_EXP, 300, 500, 1);
		ch2_exp = new EzVarInteger("Ch2. exposure time", CH2_EXP, 300, 500, 1);
		ch3_exp = new EzVarInteger("Ch3. exposure time", CH3_EXP, 300, 500, 1);
		ch4_exp = new EzVarInteger("Ch4. exposure time", CH4_EXP, 300, 500, 1);
		ch5_exp = new EzVarInteger("Ch5. exposure time", CH5_EXP, 300, 500, 1);
		ch6_exp = new EzVarInteger("Ch6. exposure time", CH6_EXP, 300, 500, 1);
		
				
		// Creates a boolean expression for showing and hiding additional configuration options
		advanced_options = new EzVarBoolean("Advanced Options", false);
		
		// Allow the user to modify the number and step size of the slices in the Z-Stack
		number_of_slices = new EzVarDouble("Desired Number of Slices in Z-Stack", NUMBER_OF_SLICES, -20,20,1);
		slice_step_size = new EzVarDouble("Desired Step Size in Z-Stack", SLICE_STEP_SIZE, -5, 5, 0.25); 
		
		// Creates and adds the S/N field to the GUI as it's own group
		EzLabel signal_noise_ins = new EzLabel("Please enter a value between 0 and 1, for the intensity thrshold, in the box below.");
		EzGroup SNR = new EzGroup("Threshold Intensity", signal_noise_ins, signal_noise_ratio);
		addEzComponent(SNR);
		
		// Creates and adds the offsets and stack options as their own group in the GUI
		EzGroup additional_options = new EzGroup("Focus Offset, exposure time, and Stack Size", ch1_offset, ch2_offset, ch3_offset, ch4_offset, ch5_offset, ch6_offset, 
				number_of_slices, slice_step_size, ch1_exp, ch2_exp, ch3_exp, ch4_exp, ch5_exp, ch6_exp);
		
		
		// Adds the button to capture the stage height to the focus
		EzGroup Focus = new EzGroup("Z Focus", new EzLabel("Adjust the focus until the sample is in focus, \nthen click the focus button "), Z_focus);
		super.addEzComponent(Focus);
		
		// Creates and groups the buttons for adding the x locations in the GUI
		EzGroup X_Location = new EzGroup("X Location", new EzLabel("Move the sample to each, X, location \nand click the coresponding button"), Left_X, Right_X);
		super.addEzComponent(X_Location);
		
		// Creates and groups the buttons for adding the y locations in the GUI
		EzGroup Y_Location = new EzGroup("Y Location", new EzLabel("Move the sample to each, Y, location \nand click the coresponding button"), Top_Y, Bottom_Y);
		super.addEzComponent(Y_Location);
		
		// Creates the button for selecting the save directory in the GUI
		EzGroup user_directory = new EzGroup("Directory Selection", new EzLabel("Click the Directory button, \nto select where you would like to save your images."), directory);
		super.addEzComponent(user_directory);

		super.addEzComponent(additional_options);
		super.addEzComponent(advanced_options);
		
		// Adds a label asking the user to press play after the initial setup, button is automatically included in GUI
		super.addEzComponent(new EzLabel("Please press play, after entering setup information above."));
		
		// Hide slice and offset options under a box for advanced options
		advanced_options.addVisibilityTriggerTo(additional_options , true);
	}
	
	/**
	 * Contains the code for selective image acquisition
	 */
	
	@Override
	protected void execute() {
		
		double true_ch1_offset, true_ch2_offset, true_ch3_offset, true_ch4_offset, true_ch5_offset, true_ch6_offset, 
		true_signal_noise_ratio, true_number_of_slices, true_slice_step_size, stack_depth;
		double temp_captured_right_x, temp_captured_left_x, true_captured_right_x, true_captured_left_x;
		
		// Following block of code retrieves the values entered into the GUI, for use by the rest of the system
		true_ch1_offset = ch1_offset.getValue();
		true_ch2_offset = ch2_offset.getValue();
		true_ch3_offset = ch3_offset.getValue();
		true_ch4_offset = ch4_offset.getValue();
		true_ch5_offset = ch5_offset.getValue();
		true_ch6_offset = ch6_offset.getValue();
		true_signal_noise_ratio = signal_noise_ratio.getValue();
		true_number_of_slices = number_of_slices.getValue();
		true_slice_step_size = slice_step_size.getValue();
		stack_depth = true_number_of_slices * true_slice_step_size;
		/*
		System.out.println("The left most x position is " + captured_left_x);
		System.out.println("The right most x position is " + captured_right_x);
		System.out.println("The top most y position is " + captured_top_y);
		System.out.println("The bottom most y position is " + captured_bottom_y);
		System.out.println("The focus level is " + captured_z_focus);
		System.out.println("Your save directory is " + save_location);
		System.out.println(signal_noise_ratio);
		*/
		
		// Confirms that the sample locations are assigned to the correct min and max locations
		temp_captured_right_x = captured_right_x;
		temp_captured_left_x = captured_left_x;
		if (captured_right_x > captured_left_x)
		{
			true_captured_left_x = temp_captured_right_x;
			true_captured_right_x = temp_captured_left_x;
		}
		else
		{
			true_captured_left_x = temp_captured_left_x;
			true_captured_right_x = temp_captured_right_x;
		}
		
		// Determines if the currently displayed sample contains cancerous cells.
		int[] cellSize = {10, 1000};
		int Cancer_channel = 2;
		int Nucleus_channel = 3;
		
		Sequence data = new Sequence();
		
		do {
			
			data = getActiveSequence();
			
			if (data == null)
			{
				MessageDialog.showDialog("There are no active sequence. \nPlease choose a sequence to open first");	
				File file = FileDialog.open();
				if(file == null)
				{
					MessageDialog.showDialog("User cancelled!");
					return;
					
				}
				
				String path = file.getAbsolutePath();
				data = Loader.loadSequence(path, 0, false);
			}
			
			CellDetector detector = new CellDetector(true_signal_noise_ratio, cellSize);
			
			detector.DetectCell(data, Cancer_channel, Nucleus_channel);
			addSequence(detector.cancer);
			addSequence(detector.nucleus);
			
			if(detector.getDetectedActiveCells() != 0)
			{
				//getZStack(save_location, stack_depth, true_slice_step_size, true_ch1_offset, true_ch2_offset, true_ch3_offset, true_ch4_offset, true_ch5_offset, true_ch6_offset);
			}
	
			// determines if the end of the sample has been reached
			try {
				x_location = StageMover.getX();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				y_location = StageMover.getY();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			next_location(x_limit(x_location, true_captured_right_x), y_limit(y_location, captured_bottom_y), x_location, y_location, true_captured_left_x, captured_bottom_y);
			
			//AcquireZStack zstack_object = new AcquireZStack();
			System.out.println(signal_noise_ratio);
		} while (x_location >= captured_right_x && y_location <= captured_bottom_y);
		
		
	}
	
	@Override
	public void clean() {
		// TODO Auto-generated method stub
		
	}

/*
	@Override
	public void run() {
		// TODO Auto-generated by Icy4Eclipse
		MessageDialog.showDialog("User Interface is working fine !");
	}
*/
	public boolean x_limit(double x_location, double true_captured_right_x) {
		if (x_location >= true_captured_right_x)
			return false;
		else 
			return true;
	}
	
	private boolean y_limit(double y_location, double captured_bottom_y) {
		if (y_location <= captured_bottom_y)
			return false;
		else
			return true;
	}
	
	private void next_location(boolean x_limit, boolean y_limit, double x_location, double y_locatoin, double true_captured_left_x, double captured_bottom_y) {
		if (x_limit == true)
			if (y_limit == true)
				JOptionPane.showMessageDialog(null, "The end of the slide has been reached.");
			else
				try {
					StageMover.moveXYRelative(-(true_captured_left_x - x_location), 5000);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		else
			try {
				StageMover.moveXYRelative(5000, 0);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return;
	}
	private void getZStack(File filePath, double zoom_range, double z_increment, 
			double zcorrect0, double zcorrect1, double zcorrect2, double zcorrect3,
			double zcorrect4, double zcorrect5) { 

		//importClass(Packages.icy.type.collection.array.Array1DUtil)
		//importClass(Packages.icy.image.IcyBufferedImageUtil)
		//importClass(Packages.icy.sequence.Sequence)
		//importClass(Packages.plugins.tprovoost.Microscopy.MicroManager.MicroManager)
		//importClass(Packages.plugins.tprovoost.Microscopy.MicroManager.tools.StageMover)
		//importClass(Packages.icy.file.Saver)
		//importClass(Packages.java.io.File)
		//importClass(Packages.java.util.concurrent.TimeUnit)


		double initial_z_position = 0.0;
		try {
			initial_z_position = StageMover.getZ();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		int delay_interval = 100;	//Makes certain hardware (HW) keeps up with software (SW) which runs must faster. By default SW does not wait for HW changes. 
		int zoom_degree = -1;	// An integer loop variable which needs be set to -1 before entering the z-stack acquisition loops.
								// It must be an integer because it is used as an index.
		Sequence sequence0 =  new Sequence();

		// Get the current reflector turret (channel) state. 
		String channel_state_str = "0";
		try {
			channel_state_str = MicroManager.getCore().getProperty("ZeissReflectorTurret", "State");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		char[] channel_state_array = channel_state_str.toCharArray();
		char channel_state_char = channel_state_array[0];
		int channel_state = 0;
		
		if ((channel_state_char=='0')&&(channel_state_array.length < 2)) {
			channel_state = 0;}
		else if ((channel_state_char=='1')||((channel_state_char=='0')&&(channel_state_array.length==2))) {
			channel_state = 1;}
		else if (channel_state_char=='2') {
			channel_state = 2;}
		else if (channel_state_char=='3') {
			channel_state = 3;}
		else if (channel_state_char=='4') {
			channel_state = 4;}
		else if (channel_state_char=='5') {
			channel_state = 5;}
			
		try {
			TimeUnit.MILLISECONDS.sleep(delay_interval);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// If the channel state is not in the cancer detecting channel position, change it to it.
		if (channel_state != 0){
			try {
				MicroManager.getCore().setProperty("ZeissReflectorTurret", "State", 0);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			channel_state = 0; }
		try {
			TimeUnit.MILLISECONDS.sleep(delay_interval);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			StageMover.moveZAbsolute(initial_z_position-zoom_range);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		IcyBufferedImage image = null;
		try {
			image = MicroManager.snapImage();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IcyBufferedImage image0 = image;
		IcyBufferedImage image1 = image;
		IcyBufferedImage image2 = image;
		IcyBufferedImage image3 = image;
		IcyBufferedImage image4 = image;
		IcyBufferedImage image5 = image;
		Object dataArray = image.getDataCopyXY(0);
		double[] doubleDataArray = Array1DUtil.arrayToDoubleArray(dataArray, image.isSignedDataType());
		double z_correction = 0.0;


		/////////////////////////////Z-stack image acquisition loops////////////////////////////////////////
		// The for-loop cycles through all the z-positions for the z-stack.
			for (double current_zoom = 0.0; current_zoom <= (zoom_range * 2 + 0.001); current_zoom += z_increment){
			zoom_degree += 1;
			
			// The do-while loop (you see the "do" below) cycles through all the channels.
			// Thus channels are cycles through or each z-slice, and then the stage is moved to the next z-position. 
			do {
								
				// Need to correct z_positions to reflect different focuses of different channels.
				if (channel_state==0){
					z_correction = zcorrect0;} //Should be variable input to the function however. CHANGE LATER!
				else if (channel_state==1){
					z_correction = zcorrect1;}
				else if (channel_state==2){
					z_correction = zcorrect2;}
				else if (channel_state==3){
					z_correction = zcorrect3;}
				else if (channel_state==4){
					z_correction = zcorrect4;}
				else if (channel_state==5){
					z_correction = zcorrect5;}
				
				double current_z = 0.0;
				try {
					current_z = StageMover.getZ();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					StageMover.moveZAbsolute(current_z+z_correction);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//println("Channel State: "+channel_state+"    Zoom = "+current_zoom+"    Zoom degree:"+zoom_degree+"    Corrected Z-position:   "+(current_z+z_correction)+"\n");
				
				// Snap the image and wait for the HW to get the job done.
				try {
					image = MicroManager.snapImage();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					TimeUnit.MILLISECONDS.sleep(delay_interval);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				// Code for saving/manipulating images begins here
				if ((zoom_degree==0)&&(channel_state==0)){
				// Create a single image the first time a z-slice is taken under channel 0
				//IcyBufferedImage image0 = image;
				image0 = image;}
				else if ((zoom_degree==0)&&(channel_state==1)){
				// Create a single image the first time a z-slice is taken under channel 1
				//IcyBufferedImage image1 = image;
				image1 = image;}
				else if ((zoom_degree==0)&&(channel_state==2)){
				// Create a single image the first time a z-slice is taken under channel 2
				//IcyBufferedImage image2 = image;
				image2 = image;}
				else if ((zoom_degree==0)&&(channel_state==3)){
				//IcyBufferedImage image3 = image;
				image3 = image;}
				else if ((zoom_degree==0)&&(channel_state==4)){
				//IcyBufferedImage image4 = image;
				image4 = image;}
				else if ((zoom_degree==0)&&(channel_state==5)){
				//IcyBufferedImage image5 = image;
				image5 = image;}
				
				// If the zoom_degree doesn't equal 0, that is, the first z-slice has already been acquired, then these else-if branches
				// are taken.
				else if ((!(zoom_degree==0))&&(channel_state==0)){
					// Add a blank channel image to image0 and return the result named as image0 
					image0 = IcyBufferedImageUtil.addChannel(image0);
					// Begin an update on image0, is used to avoid image refresh & recalculations which slow down program execution
					image0.beginUpdate();
					// Create an array (matrix) with a copy of the pixel data of the image snapped on line 49
					dataArray = image.getDataCopyXY(0);
					// Convert that data to type double (floating point) values.
					doubleDataArray = Array1DUtil.arrayToDoubleArray(dataArray, image.isSignedDataType());
					// Convert the doubleDataArray to the data type format of the original image and store the result in the corresponding channel
					// frame of image0.
					Array1DUtil.doubleArrayToSafeArray(doubleDataArray, image0.getDataXY(zoom_degree), image0.isSignedDataType());
					// End the update on image0. Causes recalculations and refresh events a single time.
					image0.endUpdate();
					}
				else if ((!(zoom_degree==0))&&(channel_state==1)){
					image1 = IcyBufferedImageUtil.addChannel(image1);
					image1.beginUpdate();
					dataArray = image.getDataCopyXY(0);
					doubleDataArray = Array1DUtil.arrayToDoubleArray(dataArray, image.isSignedDataType());
					Array1DUtil.doubleArrayToSafeArray(doubleDataArray, image1.getDataXY(zoom_degree), image1.isSignedDataType());
					image1.endUpdate();
					}
				else if ((!(zoom_degree==0))&&(channel_state==2)){
					image2 = IcyBufferedImageUtil.addChannel(image2);
					image2.beginUpdate();
					dataArray = image.getDataCopyXY(0);
					doubleDataArray = Array1DUtil.arrayToDoubleArray(dataArray, image.isSignedDataType());
					Array1DUtil.doubleArrayToSafeArray(doubleDataArray, image2.getDataXY(zoom_degree), image2.isSignedDataType());
					image2.endUpdate();
					}
				else if ((!(zoom_degree==0))&&(channel_state==3)){
					image3 = IcyBufferedImageUtil.addChannel(image3);
					image3.beginUpdate();
					dataArray = image.getDataCopyXY(0);
					doubleDataArray = Array1DUtil.arrayToDoubleArray(dataArray, image.isSignedDataType());
					Array1DUtil.doubleArrayToSafeArray(doubleDataArray, image3.getDataXY(zoom_degree), image3.isSignedDataType());
					image3.endUpdate();
					}
				else if ((!(zoom_degree==0))&&(channel_state==4)){
					image4 = IcyBufferedImageUtil.addChannel(image4);
					image4.beginUpdate();
					dataArray = image.getDataCopyXY(0);
					doubleDataArray = Array1DUtil.arrayToDoubleArray(dataArray, image.isSignedDataType());
					Array1DUtil.doubleArrayToSafeArray(doubleDataArray, image4.getDataXY(zoom_degree), image4.isSignedDataType());
					image4.endUpdate();
					}
				else if ((!(zoom_degree==0))&&(channel_state==5)){
					image5 = IcyBufferedImageUtil.addChannel(image5);
					image5.beginUpdate();
					dataArray = image.getDataCopyXY(0);
					doubleDataArray = Array1DUtil.arrayToDoubleArray(dataArray, image.isSignedDataType());
					Array1DUtil.doubleArrayToSafeArray(doubleDataArray, image5.getDataXY(zoom_degree), image5.isSignedDataType());
					image5.endUpdate();
					}
			
			// If the channel_state is not in the greatest valued position (5), increment it.
			if (channel_state < 5) {
				//This if branch alleviates the issue of the channel_state becoming a variable of type string and returning "01" instead of integer 1.
				//This was an issue in the past. Possibly because line 30 set the channel state with "0" instead of 0. A string versus an integer.
				if ((channel_state < 0)||(channel_state > 5)){
					channel_state = 1;}
				channel_state = channel_state + 1; // Do NOT replace with "channel_state += 1". Causes errors, likely due to variable type casting
				try {
					MicroManager.getCore().setProperty("ZeissReflectorTurret", "State", channel_state);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					TimeUnit.MILLISECONDS.sleep(delay_interval);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
			// If the channel_state is NOT less than 5, e.g. it is 5, set it to 0.
			else {
				channel_state = 0;
				try {
					MicroManager.getCore().setProperty("ZeissReflectorTurret", "State", channel_state);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					TimeUnit.MILLISECONDS.sleep(delay_interval);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
			try {
				StageMover.moveZAbsolute(current_z);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			} while (channel_state != 0);
				double current_z = 0.0;
				try {
					current_z = StageMover.getZ();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			try {
				StageMover.moveZAbsolute(current_z + z_increment);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		////////////////////End Z-stack Acquisition Loops/////////////////////////////////////////////

		// Store the image# files (multiple channels used to hold z-slices) in the sequence. 
		sequence0.setImage(0, 0, image0);
		sequence0.setImage(0, 1, image1);
		sequence0.setImage(0, 2, image2);
		sequence0.setImage(0, 3, image3);
		sequence0.setImage(0, 4, image4);
		sequence0.setImage(0, 5, image5);

		double x_position = 0.0;
		try {
			x_position = StageMover.getX();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		double y_position = 0.0;
		try {
			y_position = StageMover.getY();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Write out the sequence containing the z-stacks in the user specified directory.

		//Check the correction of adding "File " before "file = new..."
		File file = new File(filePath + "x"+ x_position + "y" + y_position+".tiff");
		Saver.save(sequence0, file);
		}
	
	
}
