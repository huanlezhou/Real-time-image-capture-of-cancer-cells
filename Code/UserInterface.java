package plugins.ashten2.userinterface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import plugins.adufour.ezplug.EzButton;
import plugins.adufour.ezplug.EzGroup;
import plugins.adufour.ezplug.EzLabel;
import plugins.adufour.ezplug.EzPlug;
import plugins.adufour.ezplug.EzStoppable;
import plugins.adufour.ezplug.EzVarBoolean;
import plugins.adufour.ezplug.EzVarDouble;
import plugins.adufour.ezplug.EzVarText;
import plugins.tprovoost.Microscopy.MicroManager.tools.StageMover;

public class Microscopy2 extends EzPlug implements EzStoppable {
	
	boolean stopFlag;

	double x_location, y_location;
	
	double captured_left_x, captured_right_x, captured_top_y, captured_bottom_y, captured_z_focus, z_depth;
	
	final double CH1_OFFSET = -1, CH2_OFFSET = 0, CH3_OFFSET = 0, CH4_OFFSET = -0.5, CH5_OFFSET = -1, CH6_OFFSET = -1.5, NUMBER_OF_SLICES  = 10, SLICE_STEP_SIZE = 1;
	
	EzVarDouble signal_noise_ratio, ch1_offset, ch2_offset, ch3_offset, ch4_offset, ch5_offset, ch6_offset, number_of_slices, slice_step_size;

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
		signal_noise_ratio = new EzVarDouble("S/N");
		
		// Allows the user to enter the focus offsets for different filters, initializes with default values
		ch1_offset = new EzVarDouble("Ch1. Offset", CH1_OFFSET, -5, 5, 0.25);
		ch2_offset = new EzVarDouble("Ch2. Offset", CH2_OFFSET, -5, 5, 0.25);
		ch3_offset = new EzVarDouble("Ch3. Offset", CH3_OFFSET, -5, 5, 0.25);
		ch4_offset = new EzVarDouble("Ch4. Offset", CH4_OFFSET, -5, 5, 0.25);
		ch5_offset = new EzVarDouble("Ch5. Offset", CH5_OFFSET, -5, 5, 0.25);
		ch6_offset = new EzVarDouble("Ch6. Offset", CH6_OFFSET, -5, 5, 0.25);
		
		// Creates a boolean expression for showing and hiding peripheral options
		advanced_options = new EzVarBoolean("Advanced Options", false);
		
		// Allow the user to modify the number and step size of the slices in the Z-Stack
		number_of_slices = new EzVarDouble("Desired Number of Slices in Z-Stack", NUMBER_OF_SLICES, -20,20,1);
		slice_step_size = new EzVarDouble("Desired Step Size in Z-Stack", SLICE_STEP_SIZE, -5, 5, 0.25); 
		
		// Creates and adds the S/N field to the GUI as it's own group
		EzLabel signal_noise_ins = new EzLabel("Please enter the S/R in the box below.");
		EzGroup SNR = new EzGroup("Signal to Noise Ratio", signal_noise_ins, signal_noise_ratio);
		addEzComponent(SNR);
		
		// Creates and adds the offsets and stack options as their own group in the GUI
		EzGroup additional_options = new EzGroup("Focus Offset and Stack Size", ch1_offset, ch2_offset, ch3_offset, ch4_offset, ch5_offset, ch6_offset, number_of_slices, slice_step_size);
		
		
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
		
		addEzComponent(additional_options);
		addEzComponent(advanced_options);
		
		// Adds a label asking the user to press play after the initial setup, button is automatically included in GUI
		addEzComponent(new EzLabel("Please press play, after entering setup information above."));
		
		// Hide slice and offset options under a box for advanced options
		advanced_options.addVisibilityTriggerTo(additional_options , true);
	}
	
	/**
	 * Contains the code for selective image acquisition
	 */
	
	@Override
	protected void execute() {
		// TODO Auto-generated method stub
		double temp_ch1_offset, temp_ch2_offset, temp_ch3_offset, temp_ch4_offset, temp_ch5_offset, temp_ch6_offset, temp_signal_noise_ratio;
		
		// Following block of code retrieves the values entered into the GUI, for use by the rest of the system
		temp_ch1_offset = ch1_offset.getValue();
		temp_ch2_offset = ch2_offset.getValue();
		temp_ch3_offset = ch3_offset.getValue();
		temp_ch4_offset = ch4_offset.getValue();
		temp_ch5_offset = ch5_offset.getValue();
		temp_ch6_offset = ch6_offset.getValue();
		temp_signal_noise_ratio = signal_noise_ratio.getValue();
		
		System.out.println("The left most x position is " + captured_left_x);
		System.out.println("The right most x position is " + captured_right_x);
		System.out.println("The top most y position is " + captured_top_y);
		System.out.println("The bottom most y position is " + captured_bottom_y);
		System.out.println("The focus level is " + captured_z_focus);
		System.out.println("Your save directory is " + save_location);
		

		
		System.out.println(signal_noise_ratio);
		
		//CellDetector;
		
		
		do {
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
			next_location(x_limit(x_location), y_limit(y_location), x_location, y_location);
		} while (x_location <= captured_right_x && y_location <= captured_bottom_y);
		
		
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
	public boolean x_limit(double x_location) {
		if (x_location <= captured_right_x)
			return false;
		else 
			return true;
	}
	
	public boolean y_limit(double y_location) {
		if (y_location <= captured_bottom_y)
			return false;
		else
			return true;
	}
	
	public void next_location(boolean x_limit, boolean y_limit, double x_location, double y_locatoin) {
		if (x_limit == true)
			if (y_limit == true)
				JOptionPane.showMessageDialog(null, "The end of the slide has been reached.");
			else
				try {
					StageMover.moveXYRelative((captured_left_x - x_location), 5000);
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
	
	
}