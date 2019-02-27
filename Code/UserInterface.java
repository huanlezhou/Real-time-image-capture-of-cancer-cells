package plugins.ashten2.userinterface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;

import plugins.adufour.ezplug.EzButton;
import plugins.adufour.ezplug.EzGroup;
import plugins.adufour.ezplug.EzLabel;
import plugins.adufour.ezplug.EzPlug;
import plugins.adufour.ezplug.EzStoppable;
import plugins.adufour.ezplug.EzVarDouble;
import plugins.tprovoost.Microscopy.MicroManager.tools.StageMover;

public class Microscopy2 extends EzPlug implements EzStoppable {
	
	boolean stopFlag;
	
	double captured_left_x, captured_right_x, captured_top_y, captured_bottom_y, captured_z_focus;
	
	File save_location;
	JFileChooser fc;
	
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
	@Override
	protected void initialize() {
		// TODO Auto-generated method stub
		EzVarDouble signal_noise_ratio = new EzVarDouble("S/N");
		/*EzVarDouble top_y_position = new EzVarDouble("Top Edge of Sample");
		EzVarDouble bottom_y_position = new EzVarDouble("Bottom Edge of Sample");
		EzVarDouble left_x_position = new EzVarDouble("Left Edge of Sample");
		EzVarDouble right_x_position = new EzVarDouble("Right Edge of Sample");
		EzVarDouble z_focus = new EzVarDouble("Sample in Focus");
		*///EzVarFolder save_location = new EzVarFolder("Desired File Directory", null);
		
		EzLabel signal_noise_ins = new EzLabel("Please enter the S/R in the box below.");
		EzGroup SNR = new EzGroup("Signal to Noise Ratio", signal_noise_ins, signal_noise_ratio);
		super.addEzComponent(SNR);
		
		EzGroup Focus = new EzGroup("Z Focus", new EzLabel("Adjust the focus until the sample is in focus, \nthen click the focus button "), Z_focus);
		super.addEzComponent(Focus);
		
		EzGroup X_Location = new EzGroup("X Location", new EzLabel("Move the sample to each, X, location \nand click the coresponding button"), Left_X, Right_X);
		super.addEzComponent(X_Location);
		
		EzGroup Y_Location = new EzGroup("Y Location", new EzLabel("Move the sample to each, Y, location \nand click the coresponding button"), Top_Y, Bottom_Y);
		super.addEzComponent(Y_Location);
		
		EzGroup user_directory = new EzGroup("Directory Selection", new EzLabel("Click the Directory button, \nto select where you would like to save your images."), directory);
		super.addEzComponent(user_directory);
		
		addEzComponent(new EzLabel("Please press play, afetr entering setup information above."));
	}
	
	@Override
	protected void execute() {
		// TODO Auto-generated method stub
	//	System.out.println("The bottom most y position is " +  top_y_position);
		
		System.out.println("The left most x position is " + captured_left_x);
		System.out.println("The right most x position is " + captured_right_x);
		System.out.println("The top most y position is " + captured_top_y);
		System.out.println("The bottom most y position is " + captured_bottom_y);
		System.out.println("The focus level is " + captured_z_focus);
		System.out.println("Your save directory is " + save_location);
		
		
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
}