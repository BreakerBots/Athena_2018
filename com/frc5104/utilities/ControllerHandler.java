package com.frc5104.utilities;

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
	public enum Button { A, B, X, Y, LB, RB, MENU, LIST, LJ, RJ }
	private static final int[] bSlots = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10}; 
	private boolean[] bVal = new boolean[bSlots.length];
	private boolean[] bLastVal = new boolean[bSlots.length];
	private boolean[] bPressed = new boolean[bSlots.length];
	private boolean[] bReleased = new boolean[bSlots.length];
	private    long[] bTime = new long[bSlots.length];
	
	//D-Pad
	public enum Dpad { N, NE, E, SE, S, SW, W, NW }
	private static final int[] dSlots = { 0, 45, 90, 135, 180, 225, 270, 315 }; 
	private boolean[] dVal = new boolean[dSlots.length];
	private boolean[] dLastVal = new boolean[dSlots.length];
	private boolean[] dPressed = new boolean[dSlots.length];
	private boolean[] dReleased = new boolean[dSlots.length];
	private    long[] dTime = new long[dSlots.length];
	
	//Axis Buttons
	public enum Axis { LX, LY, LT, RT, RX, RY }
	private static final int[] aSlots = { 0, 1, 2, 3, 4, 5 };
	private  double[] aZones = { 0.6, 0.6, 0.6, 0.6, 0.6, 0.6 };
	private boolean[] aVal = new boolean[aSlots.length];
	private boolean[] aLastVal = new boolean[aSlots.length];
	private boolean[] aPressed = new boolean[aSlots.length];
	private boolean[] aReleased = new boolean[aSlots.length];
	private    long[] aTime = new long[aSlots.length];
	
	//Rumble
	private long hardTarget; private boolean hardTimer = false;
	private long softTarget; private boolean softTimer = false;
	
	public void update() {
		//Normal Buttons
		for (int i = 0; i < bSlots.length; i++) {
			bPressed[i] = false;
			bReleased[i] = false;
			bVal[i] = controller.getRawButton(bSlots[i]);
			if (bVal[i] != bLastVal[i]) {
				bLastVal[i] = bVal[i];
				if (bVal[i] == true) { bPressed[i] = true; bTime[i] = System.currentTimeMillis(); }
				else { bReleased[i] = true; }
			}
		}
		
		//D-Pad
		for (int i = 0; i < dSlots.length; i++) {
			dPressed[i] = false;
			dReleased[i] = false;
			dVal[i] = controller.getPOV() == dSlots[i];
			if (dVal[i] != dLastVal[i]) {
				dLastVal[i] = dVal[i];
				if (dVal[i] == true) { dPressed[i] = true; dTime[i] = System.currentTimeMillis(); }
				else { dReleased[i] = true; }
			}
		}
		
		//Axis Buttons
		for (int i = 0; i < aSlots.length; i++) {
			aPressed[i] = false;
			aReleased[i] = false;
			aVal[i] = controller.getRawAxis(aSlots[i]) <= aZones[i] ? false : true;
			if (aVal[i] != aLastVal[i]) {
				aLastVal[i] = aVal[i];
				if (aVal[i] == true) { aPressed[i] = true; aTime[i] = System.currentTimeMillis(); }
				else { aReleased[i] = true; }
			}
		}
		
		//Rumble
		if (hardTimer) { if (hardTarget <= System.currentTimeMillis()) { controller.setRumble(RumbleType.kLeftRumble, 0); hardTimer = false; } }
		if (softTimer) { if (softTarget <= System.currentTimeMillis()) { controller.setRumble(RumbleType.kRightRumble, 0); softTimer = false; } }
	}
	
	//Normal Buttons
	/**Returns true if button is down, Just The Default Button State*/
	public boolean getHeld(Button button) { return bVal[button.ordinal()]; }
	/**Returns how long the button has been held down for, if not held down returns 0*/
	public double getHeldTime(Button button) { return bVal[button.ordinal()] ? ((double)(System.currentTimeMillis() - bTime[button.ordinal()]))/1000 : 0; }
	/**Returns true for one tick if button goes from up to down*/
	public boolean getPressed(Button button) { return bPressed[button.ordinal()]; }
	/**Returns true for one tick if button goes from down to up*/
	public boolean getReleased(Button button) { return bReleased[button.ordinal()]; }
	/**Returns the time the click lasted for, for one tick when button goes from down to up*/
	public double getClickTime(Button button) { return bReleased[button.ordinal()] ? ((double)(System.currentTimeMillis() - bTime[button.ordinal()]))/1000 : 0; }
	/**Returns true for one tick if the button has been held for the specified time*/
	public boolean getHeldEvent(Button button, double time) { return Math.abs(getHeldTime(button) - time) <= 0.01; }
	
	//Axes
	/**Returns the percent of the axis, Just The Default Axis*/
	public double getAxis(Axis axis) { return controller.getRawAxis(aSlots[axis.ordinal()]); }
	
	//Axis Buttons
	/**Returns true if button is down, Just The Default Button State*/
	public boolean getHeld(Axis button) { return aVal[button.ordinal()]; }
	/**Returns how long the button has been held down for, if not held down returns 0*/
	public double getHeldTime(Axis button) { return aVal[button.ordinal()] ? ((double)(System.currentTimeMillis() - aTime[button.ordinal()]))/1000 : 0; }
	/**Returns true for one tick if button goes from up to down*/
	public boolean getPressed(Axis button) { return aPressed[button.ordinal()]; }
	/**Returns true for one tick if button goes from down to up*/
	public boolean getReleased(Axis button) { return aReleased[button.ordinal()]; }
	/** Sets the deadzone[ the desired point in which the axis is considered pressed ] for the desired axis. */
	public void setDeadzone(Axis button, double deadzonePercent) { aZones[button.ordinal()] = deadzonePercent; }
	/**Returns the time the click lasted for, for one tick when button goes from down to up*/
	public double getClickTime(Axis button) { return aReleased[button.ordinal()] ? ((double)(System.currentTimeMillis() - aTime[button.ordinal()]))/1000 : 0; }
	/**Returns true for one tick if the button has been held for the specified time*/
	public boolean getHeldEvent(Axis button, double time) { return Math.abs(getHeldTime(button) - time) <= 0.01; }
	
	//D-Pad
	/**Returns true if button is down, Just The Default Button State*/
	public boolean getHeld(Dpad button) { return dVal[button.ordinal()]; }
	/**Returns how long the button has been held down for, if not held down returns 0*/
	public double getHeldTime(Dpad button) { return dVal[button.ordinal()] ? ((double)(System.currentTimeMillis() - dTime[button.ordinal()]))/1000 : 0; }
	/**Returns true for one tick if button goes from up to down*/
	public boolean getPressed(Dpad button) { return dPressed[button.ordinal()]; }
	/**Returns true for one tick if button goes from down to up*/
	public boolean getReleased(Dpad button) { return dReleased[button.ordinal()]; }
	/**Returns the time the click lasted for, for one tick when button goes from down to up*/
	public double getClickTime(Dpad button) { return dReleased[button.ordinal()] ? ((double)(System.currentTimeMillis() - dTime[button.ordinal()]))/1000 : 0; }
	/**Returns true for one tick if the button has been held for the specified time*/
	public boolean getHeldEvent(Dpad button, double time) { return Math.abs(getHeldTime(button) - time) <= 0.01; }
	
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
