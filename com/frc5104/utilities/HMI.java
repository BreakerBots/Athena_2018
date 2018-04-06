package com.frc5104.utilities;

import com.frc5104.utilities.ControllerHandler.Control;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

public class HMI {

	//Recording
	public static final Control kStartRecording = Control.MENU;
	public static final Control kStopRecording = Control.MENU;
	public static final Control kPlayback = Control.LIST;
	
	//Drive
	public static final Control kDriveX = Control.LX;
	public static final Control kDriveY = Control.LY;
	public static final Control kDriveShift = Control.RT;
	
	//Elevator
	public static final Control kPtoHoldAndHookPressButton = Control.Y; //hold for 0.4 sec
	public static final Control kElevatorUpDown = Control.RY;
	public static final Control kOpenHookHolder = /*Control.A -- Changed ToY*/ Control.Y;
	
	//Squeezy
	public static final Control kSqueezyUp = Control.N;
	public static final Control kSqueezyDown = Control.S;
	
	public static final Control kSqueezyOpen = Control.W;
	public static final Control kSqueezyClose = Control.E;
	
	public static final Control kSqueezyEject = Control.LB;
	public static final Control kSqueezyNeutral = Control.B;
	public static final Control kSqueezyIntake = Control.X;
	
	public static final Control kElevatorToggleBottomSwitch = Control.A;

	private static NetworkTable table = null;
	public static void PutOnDashboard() {
		if (table == null) { table = NetworkTableInstance.getDefault().getTable("HMI"); }
		
		try {
			for (int i = 0; i < HMI.class.getFields().length; i++) {
				putControl(HMI.class.getFields()[i].getName(), HMI.class.getFields()[i].get(new HMI()));
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	private static void putControl(String name, Object object) {
//		System.out.println(name + " = " + object);
		table.getEntry(name).setString(object.toString());
	}
}
