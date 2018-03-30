package org.usfirst.frc.team5104.robot;

import org.usfirst.frc.team5104.robot.ControllerHandler.Control;

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
	public static final Control kShift = Control.RT;
	
	//Elevator
	public static final Control kPtoButton = Control.A;
	public static final Control kElevator = Control.RY;
	public static final Control kOpenHookHolder = Control.A;
	
	//Squeezy
	public static final Control kSqueezyUp = Control.N;
	public static final Control kSqueezyDown = Control.S;
	
	public static final Control kOpenButton = Control.E;
	public static final Control kCloseButton = Control.W;
	
	public static final Control kEjectButton = Control.LB;
	public static final Control kNeutralButton = Control.B;
	public static final Control kSqueezyIntake = Control.X;

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
		System.out.println(name + " = " + object);
		table.getEntry(name).setString(object.toString());
	}
}
