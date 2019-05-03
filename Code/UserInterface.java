package plugins.ashten2.userinterface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import icy.sequence.Sequence;
import jxl.read.biff.BiffException;
import plugins.adufour.ezplug.EzButton;
import plugins.adufour.ezplug.EzGroup;
import plugins.adufour.ezplug.EzLabel;
import plugins.adufour.ezplug.EzPlug;
import plugins.adufour.ezplug.EzStoppable;
import plugins.adufour.ezplug.EzVarBoolean;
import plugins.adufour.ezplug.EzVarDouble;
import plugins.adufour.ezplug.EzVarInteger;
import plugins.adufour.ezplug.EzVarText;
import plugins.alfredangeline.activecelldetector.CellDetector;
import plugins.alfredangeline.activecelldetector.ImageAcquisition;
import plugins.tkkoba1997.getallzstacks.GetAllZStacks;
import plugins.tprovoost.Microscopy.MicroManager.tools.StageMover;


public class Microscopy2 extends EzPlug implements EzStoppable {
	
	boolean stopFlag = false;

	double x_location, y_location;
	
	double captured_left_x, captured_right_x, captured_top_y, captured_bottom_y, captured_z_focus, z_depth;
	
	// Initializes variable for the default channel focus offset and exposure times
	final double CH1_OFFSET = -1, CH2_OFFSET = 0, CH3_OFFSET = 0, CH4_OFFSET = -0.5, CH5_OFFSET = -1, CH6_OFFSET = -1.5, NUMBER_OF_SLICES  = 10, SLICE_STEP_SIZE = 1;
	final int CH1_EXP = 500, CH2_EXP = 500, CH3_EXP = 500, CH4_EXP = 500, CH5_EXP = 500, CH6_EXP = 500; 
	
	EzVarDouble signal_noise_ratio, objective_field, ch1_offset, ch2_offset, ch3_offset, ch4_offset, ch5_offset, ch6_offset, number_of_slices, slice_step_size;

	EzVarInteger ch1_exp, ch2_exp, ch3_exp, ch4_exp, ch5_exp, ch6_exp;
	
	EzVarText ch1_name, ch2_name, ch3_name, ch4_name, ch5_name, ch6_name;
	
	EzVarBoolean advanced_focus, advanced_exposure, advanced_stack, advanced_filter;
	
	EzVarInteger Cancer, DIC, Nucleus;
	
	File save_location;
	JFileChooser fc;
	/**
	 * Following code performs an actions based on button presses from the user
	 * Each Button corresponds to a specific location/stage position
	 * When the button is pressed by the user the corresponding stage location is saved in variable
	 * for use in determining the starting and ending position of the sample
	 */
	
	// Action to capture the left sample position
	ActionListener get_x_left_y_top = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				captured_left_x = StageMover.getX();
				captured_top_y = StageMover.getY();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				JOptionPane.showMessageDialog(null, "Error occured when top left button pressed line 66");
			}
		}
	};
	EzButton Left_X_Top_Y = new EzButton("Left X and Top Y", get_x_left_y_top);
	
	// Action to capture the right sample position
	ActionListener get_x_right_y_bottom = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				captured_right_x = StageMover.getX();
				captured_bottom_y = StageMover.getY();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				JOptionPane.showMessageDialog(null, "Error occured when bottom right buttom pressed line 81");
			}
		}
	};
	EzButton Right_X_Bottom_Y = new EzButton("Right X and Btm Y", get_x_right_y_bottom);
	
	// Action to capture the stage height, when the sample is in focus
	ActionListener get_z_focus = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				captured_z_focus = StageMover.getZ();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				JOptionPane.showMessageDialog(null, "Error occured when focus button pressed line 95");
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
				JOptionPane.showMessageDialog(null, "Error occured when directory button was pressed line 112");
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
		
		objective_field = new EzVarDouble("Objective Field Number", 18, -20, 20, 0.25);
		
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
		advanced_focus = new EzVarBoolean("Focus Options", false);
		advanced_exposure = new EzVarBoolean("Exposure Time Options", false);
		advanced_stack = new EzVarBoolean("Z-Stack Options", false);
		advanced_filter = new EzVarBoolean("Filter Wheel Options", false);
		
		// Allow the user to modify the number and step size of the slices in the Z-Stack
		number_of_slices = new EzVarDouble("Desired Number of Slices in Z-Stack", NUMBER_OF_SLICES, -20,20,1);
		slice_step_size = new EzVarDouble("Desired Step Size in Z-Stack", SLICE_STEP_SIZE, -5, 5, 0.25); 
		
		// allow the user to change the location, on the filter wheel, of the DIC, cancer(active cells), and nucleus detection filters.
		DIC = new EzVarInteger("Location of DIC Filter", 1, 1, 6, 1);
		Cancer = new EzVarInteger("Location of Cancer(Tag of Interest) Filter", 2, 1, 6, 1);
		Nucleus = new EzVarInteger("Location of Nucleus Filter", 3, 1, 6, 1);
		
		// Creates and adds the S/N field to the GUI as it's own group
		EzLabel signal_noise_ins = new EzLabel("Please enter a value between 0 and 1, for the intensity thrshold, in the box below.");
		EzGroup SNR = new EzGroup("Threshold Intensity", signal_noise_ins, signal_noise_ratio);
		addEzComponent(SNR);
		
		/*
		EzLabel objective_field_ins = new EzLabel("Please enter the Field Number, located on the object.");
		EzGroup FV = new EzGroup("Field View Number", objective_field_ins, objective_field);
		addEzComponent(FV);
		*/
		
		// Creates and adds the offsets and stack options as their own group in the GUI
		EzGroup additional_focus = new EzGroup("Focus Offset", ch1_offset, ch2_offset, ch3_offset, ch4_offset, ch5_offset, ch6_offset);
		
		// Creates and adds the offsets and stack options as their own group in the GUI
		EzGroup additional_exposure = new EzGroup("Exposure time", ch1_exp, ch2_exp, ch3_exp, ch4_exp, ch5_exp, ch6_exp);
		
		// Creates and adds the offsets and stack options as their own group in the GUI
		EzGroup additional_stack = new EzGroup("Stack Size", number_of_slices, slice_step_size);
		EzGroup additional_filters  = new EzGroup("Filter Locations in Filter Wheel", DIC, Cancer, Nucleus);
		
		// Adds the button to capture the stage height to the focus
		EzGroup Focus = new EzGroup("Z Focus", new EzLabel("Adjust the focus until the sample is in focus, \nthen click the focus button "), Z_focus);
		super.addEzComponent(Focus);
		
		// Creates and groups the buttons for adding the x locations in the GUI
		EzGroup X_Y_Initial = new EzGroup("Top Left X and Y Location", new EzLabel("Move the sample to the top left location \nand click the coresponding button"), Left_X_Top_Y);
		super.addEzComponent(X_Y_Initial);
		
		// Creates and groups the buttons for adding the y locations in the GUI
		EzGroup X_Y_End = new EzGroup("Bottom Right X and Y Location", new EzLabel("Move the sample to the bottom right location \nand click the coresponding button"), Right_X_Bottom_Y);
		super.addEzComponent(X_Y_End);
		
		// Creates the button for selecting the save directory in the GUI
		EzGroup user_directory = new EzGroup("Directory Selection", new EzLabel("Click the Directory button, \nto select where you would like to save your images."), directory);
		super.addEzComponent(user_directory);
		
		EzGroup Advanced_options = new EzGroup("Advanced Options", new EzLabel("Please have only one box checked at a time."),
				advanced_focus, advanced_exposure, advanced_stack, advanced_filter, additional_filters, additional_focus, additional_exposure, additional_stack);
		super.addEzComponent(Advanced_options);
		
		// Hide slice and offset options under a box for advanced options
		advanced_focus.addVisibilityTriggerTo(additional_focus, true);
		advanced_exposure.addVisibilityTriggerTo(additional_exposure, true);
		advanced_stack.addVisibilityTriggerTo(additional_stack, true);
		advanced_filter.addVisibilityTriggerTo(additional_filters, true);
		
		// Adds a label asking the user to press play after the initial setup, button is automatically included in GUI
		super.addEzComponent(new EzLabel("Please press play, after entering information above."));
	}
	
	/**
	 * Contains the code for selective image acquisition
	 */
	
	@Override
	protected void execute() {
		stopFlag = false;
		
		double true_ch1_offset, true_ch2_offset, true_ch3_offset, true_ch4_offset, true_ch5_offset, true_ch6_offset, 
		true_signal_noise_ratio, true_number_of_slices, true_slice_step_size, stack_depth; //true_objective_field;
		double temp_captured_right_x, temp_captured_left_x, true_captured_right_x, true_captured_left_x;
		int true_dic, true_cancer, true_nucleus;
		int true_ch1_exposure, true_ch2_exposure, true_ch3_exposure, true_ch4_exposure, true_ch5_exposure, true_ch6_exposure;
		
		while(!stopFlag) {	
		// Following block of code retrieves the values entered into the GUI, for use by the rest of the system
		true_ch1_offset = ch1_offset.getValue();
		true_ch2_offset = ch2_offset.getValue();
		true_ch3_offset = ch3_offset.getValue();
		true_ch4_offset = ch4_offset.getValue();
		true_ch5_offset = ch5_offset.getValue();
		true_ch6_offset = ch6_offset.getValue();
		true_ch1_exposure = ch1_exp.getValue();
		true_ch2_exposure = ch2_exp.getValue();
		true_ch3_exposure = ch3_exp.getValue();
		true_ch4_exposure = ch4_exp.getValue();
		true_ch5_exposure = ch5_exp.getValue();
		true_ch6_exposure = ch6_exp.getValue();
		true_signal_noise_ratio = signal_noise_ratio.getValue();
		//true_objective_field = objective_field.getValue();
		true_number_of_slices = number_of_slices.getValue();
		true_slice_step_size = slice_step_size.getValue();
		stack_depth = true_number_of_slices * true_slice_step_size;
		true_dic = DIC.getValue() - 1;
		true_cancer = Cancer.getValue() - 1;
		true_nucleus = Nucleus.getValue() - 1;
		
		int filter[] = {true_dic, true_cancer, true_nucleus};
		double offset[] = {true_ch1_offset, true_ch2_offset, true_ch3_offset, true_ch4_offset, true_ch5_offset, true_ch6_offset};
		int exposure[] = {true_ch1_exposure, true_ch2_exposure, true_ch3_exposure, true_ch4_exposure, true_ch5_exposure, true_ch6_exposure};
		double exposure2[] = {true_ch1_exposure, true_ch2_exposure, true_ch3_exposure, true_ch4_exposure, true_ch5_exposure, true_ch6_exposure};
		/*
		System.out.println("The left most x position is " + captured_left_x);
		System.out.println("The right most x position is " + captured_right_x);
		System.out.println("The top most y position is " + captured_top_y);
		System.out.println("The bottom most y position is " + captured_bottom_y);
		System.out.println("The focus level is " + captured_z_focus);
		System.out.println("Your save directory is " + save_location);
		System.out.println(signal_noise_ratio);
		System.out.println(filter[0]);
		System.out.println(filter[1]);
		System.out.println(filter[2]);
		*/
		String z_directory = save_location.getAbsolutePath().concat("Tested Positive");
		File temp_z_d = new File(z_directory);
		temp_z_d.mkdir();
		File stack_directory = new File(z_directory);
		String test_directory = save_location.getAbsolutePath().concat("Single Test Image");
		File temp_test_d = new File(test_directory);
		temp_test_d.mkdir();
		File test_image_directory = new File(test_directory);

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
		
		try {
			StageMover.moveXYAbsolute(true_captured_left_x, captured_top_y);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, "Error occured when trying to move the stage to the initial sample position line 285");
		}
		
		Sequence data = new Sequence();
		ImageAcquisition imageAcquisition = new ImageAcquisition(exposure2, offset);
		CellDetector detector = new CellDetector(true_signal_noise_ratio, test_image_directory);
			
			do {
				
				do {
					try {
						data = imageAcquisition.acquireSequence(filter, captured_z_focus, test_image_directory);
					} catch(Exception e) {
						JOptionPane.showMessageDialog(null, "Error when trying to acquire test images line 298");
					}
						try {
							detector.DetectCell(data);
						} catch (BiffException e1) {
							// TODO Auto-generated catch block
							JOptionPane.showMessageDialog(null, "Error occcured when trying to check for active cells line 304");
						}
					
					try {
					if(detector.getDetectedActiveCells() != 0)
					{
						GetAllZStacks All_Stacks = new GetAllZStacks();
						All_Stacks.getZStacks(stack_directory, stack_depth, true_slice_step_size, offset, exposure);
					}
					} catch(Exception e) {
						JOptionPane.showMessageDialog(null, "Error occured when trying to acquire the z-stack line 314");
					}
					try {
						x_location = StageMover.getX();
						StageMover.moveXYRelative(-1000, 0);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						JOptionPane.showMessageDialog(null, "Error occured when trying to reposition the stage x-location line 321");
					}
				} while (x_location >= true_captured_right_x);
				
				try {
					y_location = StageMover.getY();
					StageMover.moveXYRelative(0, 1000);
					StageMover.moveXYAbsolute(true_captured_left_x, StageMover.getY());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null, "Error occured when trying to reposition the stage after the farthest x location has been reached line 331");
				}
				
			} while (y_location <= (captured_bottom_y - 1000));
			
			JOptionPane.showMessageDialog(null, "The end of the slide has been reached.");
			stopFlag = true;
		}
		 	
	}
	
	@Override
	public void clean() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopExecution()
	{
		stopFlag = true;
	}

}
