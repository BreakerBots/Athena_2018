package com.frc5104.utilities;

import com.frc5104.utilities.ControllerHandler.Control;

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
	public static final Control kPtoButton = Control.X;
	public static final Control kElevator = Control.RY;
	
	//Squeezy
	public static final Control kSqueezyUp = Control.N;
	public static final Control kSqueezyDown = Control.S;
	public static final Control kOpenButton = Control.E;
	public static final Control kCloseButton = Control.W;
	public static final Control kEjectButton = Control.LB;
	public static final Control kNeutralButton = Control.B;

}
