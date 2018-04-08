package org.usfirst.frc.team5104.robot;

import java.util.ArrayList;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.Joystick;

public class ControllerHandler {
	static ControllerHandler m_instance;
	public static ControllerHandler getInstance() {
		if (m_instance == null) {
			m_instance = new ControllerHandler();
		}
		return m_instance;
	} private ControllerHandler() {  }
	
	//Start
	
	private Joystick controller = new Joystick(0);

	//Normal Buttons
	public enum Control { 
		A, B, X, Y, LB, RB, MENU, LIST, LJ, RJ, 
		N, NE, E, SE, S, SW, W, NW,
		LX, LY, LT, RT, RX, RY }
	private static final int[] Slots = {
		1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 
		0, 45, 90, 135, 180, 225, 270, 315,
		0, 1, 2, 3, 4, 5 }; 
	private int[]     Type = {
		/*Button*/1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 
		/*Dpad*/3, 3, 3, 3, 3, 3, 3, 3,
		/*Axis*/2, 2, 2, 2, 2, 2
	};
	private double[]  Deadzones = { /*The Default Deadzones*/ 0.6,0.6,0.6,0.6,0.6,0.6,0.6,0.6,0.6,0.6,0.6,0.6,0.6,0.6,0.6,0.6,0.6,0.6,0.6,0.6,0.6,0.6,0.6,0.6 };
	private boolean[] Val = new boolean[Slots.length];
	private boolean[] LastVal = new boolean[Slots.length];
	private boolean[] Pressed = new boolean[Slots.length];
	private boolean[] Released = new boolean[Slots.length];
	private    long[] Time = new long[Slots.length];
		
	//Rumble
	private long hardTarget; private boolean hardTimer = false;
	private long softTarget; private boolean softTimer = false;
	
	public void update() {
		//Normal Buttons
		for (int i = 0; i < Slots.length; i++) {
			Pressed[i] = false;
			Released[i] = false;
			
			Val[i] = Type[i] == 1 ? controller.getRawButton(Slots[i]) : (Type[i] == 2 ? (controller.getRawAxis(Slots[i]) <= Deadzones[i] ? false : true) : (controller.getPOV() == Slots[i]));
			
			if (Val[i] != LastVal[i]) {
				LastVal[i] = Val[i];
				if (Val[i] == true) { Pressed[i] = true; Time[i] = System.currentTimeMillis(); }
				else { Released[i] = true; }
			}
		}
		
		//Rumble
		if (hardTimer) { if (hardTarget <= System.currentTimeMillis()) { controller.setRumble(RumbleType.kLeftRumble, 0); hardTimer = false; } }
		if (softTimer) { if (softTarget <= System.currentTimeMillis()) { controller.setRumble(RumbleType.kRightRumble, 0); softTimer = false; } }
	}
	
	//Control Functions
	/**Returns the percent of the axis, Just The Default Axis*/
	public double getAxis(Control control) { return controller.getRawAxis(Slots[control.ordinal()]); }
	/**Returns true if button is down, Just The Default Button State*/
	public boolean getHeld(Control control) { return Val[control.ordinal()]; }
	/**Returns how long the button has been held down for, if not held down returns 0*/
	public double getHeldTime(Control control) { return Val[control.ordinal()] ? ((double)(System.currentTimeMillis() - Time[control.ordinal()]))/1000 : 0; }
	/**Returns true for one tick if button goes from up to down*/
	public boolean getPressed(Control control) { return Pressed[control.ordinal()]; }
	/**Returns true for one tick if button goes from down to up*/
	public boolean getReleased(Control control) { return Released[control.ordinal()]; }
	/** Sets the deadzone[ the desired point in which the axis is considered pressed ] for the desired axis. */
	public void setDeadzone(Control control, double deadzonePercent) { Deadzones[control.ordinal()] = deadzonePercent; }
	/**Returns the time the click lasted for, for one tick when button goes from down to up*/
	public double getClickTime(Control control) { return Released[control.ordinal()] ? ((double)(System.currentTimeMillis() - Time[control.ordinal()]))/1000 : 0; }
	/**Returns true for one tick if the button has been held for the specified time*/
	public boolean getHeldEvent(Control control, double time) { return Math.abs(getHeldTime(control) - time) <= 0.01; }

	//Rumble
	/** Rumbles the controller hard[ hard rumble is a deep rumble and soft rumble is lighter rumble ] at (strength) until set again */
	public void rumbleHard(double strength) { controller.setRumble(RumbleType.kLeftRumble, strength); hardTimer = false; }
	/** Rumbles the controller soft[ hard rumble is a deep rumble and soft rumble is lighter rumble ] at (strength) until set again */
	public void rumbleSoft(double strength) { controller.setRumble(RumbleType.kRightRumble, strength); softTimer = false; }
	/** Rumbles the controller hard[ hard rumble is a deep rumble and soft rumble is lighter rumble ] at (strength) for (seconds) */
	public void rumbleHardFor(double strength, double seconds) { controller.setRumble(RumbleType.kLeftRumble, strength);  hardTarget = (System.currentTimeMillis() + ((long) (seconds*1000))); hardTimer = true; }
	/** Rumbles the controller soft[ hard rumble is a deep rumble and soft rumble is lighter rumble ] at (strength) for (seconds) */
	public void rumbleSoftFor(double strength, double seconds) { controller.setRumble(RumbleType.kRightRumble, strength); softTarget = (System.currentTimeMillis() + ((long) (seconds*1000))); softTimer = true; }
}
