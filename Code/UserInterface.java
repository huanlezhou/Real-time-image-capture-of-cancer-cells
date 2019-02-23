package plugins.ashten2.userinterface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import icy.gui.dialog.MessageDialog;
import icy.plugin.abstract_.PluginActionable;
import plugins.adufour.ezplug.EzButton;
import plugins.adufour.ezplug.EzGroup;
import plugins.adufour.ezplug.EzLabel;
import plugins.adufour.ezplug.EzPlug;
import plugins.adufour.ezplug.EzStoppable;
import plugins.adufour.ezplug.EzVar;
import plugins.adufour.ezplug.EzVarDouble;
import plugins.adufour.ezplug.EzVarFolder;
import plugins.tprovoost.Microscopy.MicroManager.tools.StageMover;

public class UserInterface extends EzPlug implements EzStoppable {
	
	boolean stopFlag;
	
	ActionListener get_x_left = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				double captured_left_x = StageMover.getX();
				System.out.println("The left most x position is " + captured_left_x);
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
				double captured_right_x = StageMover.getX();
				System.out.println("The right most x position is " + captured_right_x);
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
				double captured_top_y = StageMover.getY();
				System.out.println("The top most y position is " + captured_top_y);
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
				double captured_bottom_y = StageMover.getY();
				System.out.println("The bottom most y position is " + captured_bottom_y);
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
				double captured_z_focus = StageMover.getZ();
				System.out.println("The focus level is " + captured_z_focus);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	};
	EzButton Z_focus = new EzButton("Z Focus", get_z_focus);
	
	@Override
	protected void initialize() {
		// TODO Auto-generated method stub
		EzVarDouble signal_noise_ratio = new EzVarDouble("S/N");
		//EzVarDouble top_y_position = new EzVarDouble("Top Edge of Sample");
		//EzVarDouble bottom_y_position = new EzVarDouble("Bottom Edge of Sample");
		//EzVarDouble left_x_position = new EzVarDouble("Left Edge of Sample");
		//EzVarDouble right_x_position = new EzVarDouble("Right Edge of Sample");
		//EzVarDouble z_focus = new EzVarDouble("Sample in Focus");
		EzVarFolder save_location = new EzVarFolder("Desired File Directory", null);
		
		EzLabel signal_noise_ins = new EzLabel("Please enter the S/R in the box above.");
		EzGroup SNR = new EzGroup("Signal to Noise Ratio", signal_noise_ratio, signal_noise_ins);
		super.addEzComponent(SNR);
		/*
		EzGroup y_location = new EzGroup("Y Location", top_y_position, bottom_y_position);
		super.addEzComponent(y_location);
		
		EzGroup x_location = new EzGroup("X Location", left_x_position, right_x_position);
		super.addEzComponent(x_location);
		
		super.addEzComponent(z_focus);
		*/
		
		addEzComponent(new EzLabel("Press Left X button after putting the left most sample in view"));
		addEzComponent(Left_X);
		
		addEzComponent(new EzLabel("Press Right X button after putting the right most sample in view"));
		addEzComponent(Right_X);
		
		addEzComponent(new EzLabel("Press Top Y button after putting the top most sample in view"));
		addEzComponent(Top_Y);
		
		addEzComponent(new EzLabel("Press Btm Y button after putting the bottom most sample in view"));
		addEzComponent(Bottom_Y);
		
		addEzComponent(new EzLabel("Press Z Focuse button after bringing the cells into focus"));
		addEzComponent(Z_focus);
		
		super.addEzComponent(save_location);
	}
	
	@Override
	protected void execute() {
		// TODO Auto-generated method stub
	//	System.out.println("The bottom most y position is " +  top_y_position);
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