package com.frc5104.main.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.frc5104.utilities.ControllerHandler;
import com.frc5104.utilities.HMI;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Squeezy {
	
	public static final int MAIN_ID = 21;
	public static final int LEFT_ID = 22;
	public static final int RIGHT_ID = 23;
	
	static final double kHoldEffort = -0.25;
	static final double kShootSqueezeEffort = -0.05;
	static final double kCloseEffort = -0.0;
	static final double kOpenEffort  = 0.25;
	
	static final double kRightSpinMultiplier = 1.1;
	static final double kIntakeEffort = -/*0.4*//*3-12-18 0.2*/0.2;
	static final double kPinchEffort = -0.2;
	public static double kEjectEffort = 0.6;
	
	public enum SqueezyState {
		EMPTY, INTAKE, CLOSING, HOLDING, LOADED, EJECT,
					UNJAM
	}
	
	static Squeezy m_instance;
	
	public static Squeezy getInstance() {
		if (m_instance == null) {
			m_instance = new Squeezy();
		}
		return m_instance;
	}//getInstance
	
	NetworkTable table = null;
	
	//An unreasonable starting value
	private boolean calibrated = false;
	private SqueezyState prevState = SqueezyState.EJECT;
	SqueezyState state = SqueezyState.HOLDING;
	
	//Talon IDs fall are contained in [20,30)
	TalonSRX squeezer  = new TalonSRX(MAIN_ID);
	TalonSRX leftSpin  = new TalonSRX(LEFT_ID);
	TalonSRX rightSpin = new TalonSRX(RIGHT_ID);
	
	//Eject Timing
	long lastTime;
	
	//DoubleSolenoid lifter = new DoubleSolenoid(0,1);
	
	SqueezySensors sensors = SqueezySensors.getInstance();

	private Squeezy () {
		
		
		//Make sure that the motor output and encoder counts are in sync
			//OTHERWISE, the finely tuned closed-loop control becomes
			//chaotic and accelerates away from the setpoint
		squeezer.setSensorPhase(true);
		state = SqueezyState.HOLDING;
		updateState();
		update();
	}//Squeezy
	
	long ejectTime = System.currentTimeMillis();
	
	public void updateState() {
		
		/*
		sensors.updateSensors();

		if (squeezer.getSensorCollection().isFwdLimitSwitchClosed()) {
			if (!calibrated)
				squeezer.setSelectedSensorPosition(0, 0, 10);
			calibrated = true;
		}
		
		switch (state) {
		case EMPTY:
			if (buttonIntake.Pressed)
				state = SqueezyState.INTAKE;
			break;
		case INTAKE:
			//UltraSonic: Move directly to holding when box is detected by motor stalls
			if (buttonCancel.Pressed)
				state = SqueezyState.EMPTY;
			if (sensors.detectBox() && squeezer.getSelectedSensorPosition(0) > -90000)
				state = SqueezyState.CLOSING;
			break;
		case CLOSING: //Should not exist with Ultrasonic
			if (buttonCancel.Pressed)
				state = SqueezyState.UNJAM;
			if (!(sensors.detectBox() && squeezer.getSelectedSensorPosition(0) > -90000))
				state = SqueezyState.INTAKE;
			if (sensors.detectBoxHeld()) {
				state = SqueezyState.HOLDING;
				ControllerHandler.getInstance().rumbleHardFor(0.5, 0.5);
			}
			break;
		case HOLDING:
			if (sensors.detectBoxGone())
				state = SqueezyState.EMPTY;
			break;
		case LOADED:
			if (buttonEject.Pressed)
				state = SqueezyState.EJECT;
			if(sensors.detectBoxGone())
				state = SqueezyState.EMPTY;
			break;
		case EJECT:
			if ((System.currentTimeMillis() - lastTime) > 1000)
				state = SqueezyState.UNJAM;
			if (buttonCancel.Pressed)
				state = SqueezyState.EMPTY;
			break;
		case UNJAM:
			if (squeezer.getSensorCollection().isFwdLimitSwitchClosed())
				state = SqueezyState.INTAKE;
			if (buttonIntake.Pressed)
				state = SqueezyState.INTAKE;
			break;
		}//switch
		
		if (buttonEject.Pressed) {
			state = SqueezyState.EJECT;
			lastTime = System.currentTimeMillis();
		}
		
		if (buttonUnjam.Pressed)
			state = SqueezyState.UNJAM;
		
		if (state != prevState) {
			postStateChange();
			prevState = state;
		}
		
		postUltrasonicData();
		postSqueezerData();
		
		*/
		
		ControllerHandler controller = ControllerHandler.getInstance();
		
		if (controller.getPressed(HMI.kNeutralButton))
			state = SqueezyState.EMPTY;
		if (controller.getPressed(HMI.kOpenButton))
			state = SqueezyState.INTAKE;
		if (controller.getPressed(HMI.kCloseButton))
			state = SqueezyState.HOLDING;
		if (controller.getPressed(HMI.kEjectButton)) {
			System.out.println("EJECTING!!!");
			ejectTime = System.currentTimeMillis();
			state = SqueezyState.EJECT;
		}
		
		if (state == SqueezyState.EJECT) {
			if (System.currentTimeMillis()-ejectTime > 1000)
				state = SqueezyState.HOLDING;
		}
		
	}//poll
	
	public void update() {
		switch (state) {
		case EMPTY:
			raise();
			spinStop();
			close();
			break;
		case INTAKE:
			lower();
			spinIn();
			open();
			break;
		case CLOSING:
			lower();
			spinIn();
			close();
			break;
		case HOLDING:
			raise();
			spinPinch();
			hold();
			break;
		case LOADED:
			lower();
			spinStop();
			hold();
			break;
		case EJECT:
			lower();
			spinOut();
			shootSqueeze();
			break;
		case UNJAM:
			lower();
			spinStop();
			open();
		}
		
	}//updateState
	
	//--------- Squeezy States ----------//
	
	public int getEncoderPosition() {
		return squeezer.getSelectedSensorPosition(0);
	}//getEncoderPosition

	public int getEncoderVelocity() {
		return squeezer.getSelectedSensorVelocity(0);
	}//getEncoderVelocity
	
	public void forceState(SqueezyState newState) {
		state = newState;
		if (state == SqueezyState.EJECT)
			ejectTime = System.currentTimeMillis();
	}//forceState
	
	public boolean isInState (SqueezyState checkState) {
		return state == checkState;
	}//isInState
	
	public boolean hasCube() {
		return state == SqueezyState.HOLDING || state == SqueezyState.LOADED;
	}//hasCube
	
	//--------- Squeezy Actions ---------//
	private void setSpinners(double effort) {
		leftSpin.set(ControlMode.PercentOutput, effort);
		rightSpin.set(ControlMode.PercentOutput, -kRightSpinMultiplier*effort);
	}//setSpinners
	
	private void spinIn() {
		setSpinners(kIntakeEffort);
//		System.out.printf("Spin Effort: %1.1f\t",kIntakeEffort);
	}//spinIn
	
	private void spinOut() {
		setSpinners(kEjectEffort);
//		System.out.printf("Spin Effort: %1.1f\t",kEjectEffort);
	}//spinOut
	
	private void spinStop() {
		setSpinners(0);
//		System.out.printf("Spin Effort: %1.1f\t", 0.0);
	}//setSpinnerState
	
	private void spinPinch() {
		setSpinners(kPinchEffort);
	}//spinPinch
	
	private void open() {
		squeezer.set(ControlMode.PercentOutput, kOpenEffort);
//		System.out.printf("Squeezer Effort: %1.1f\t",kOpenEffort);
	}//open
	
	private void close() {
		squeezer.set(ControlMode.PercentOutput, kCloseEffort);
//		System.out.printf("Squeezer Effort: %1.1f\t",kCloseEffort);
	}//close

	private void shootSqueeze() {
		squeezer.set(ControlMode.PercentOutput, kShootSqueezeEffort);
//		System.out.printf("Squeezer Effort: %1.1f\t",kCloseEffort);
	}//close
	
	private void hold() {
		squeezer.set(ControlMode.PercentOutput, kHoldEffort);
	}//hold
	
	private void raise() {
		//if (squeezer.getSelectedSensorPosition(0) < -10000)
			//lifter.set(DoubleSolenoid.Value.kReverse);
//		System.out.printf("Lifter Value: %s\t", DoubleSolenoid.Value.kForward.toString());
	}//raise
	
	private void lower() {
		//lifter.set(DoubleSolenoid.Value.kForward);
//		System.out.printf("Lifter Value: %s\t", DoubleSolenoid.Value.kReverse.toString());
	}//lower
	
	public void initTable(NetworkTable inst) {
		if (inst == null) {
			inst = NetworkTableInstance.getDefault().getTable("squeezy");
		}
		table = inst;
	}//initTable
	
	public void postUltrasonicData() {
		if (table != null) {
			setBoolean("sensors/detect_box", sensors.detectBox());
			setBoolean("sensors/detect_box_gone", sensors.detectBoxGone());
			setBoolean("sensors/detect_box_held", sensors.detectBoxHeld());
			
			double[] dists = sensors.getDistances();
			
			setDouble("ultrasonic/center", dists[0]);
			setDouble("ultrasonic/left", dists[1]);
			setDouble("ultrasonic/right", dists[2]);
		}
	}//postUltrasonicData
	
	public void postStateChange() {
		if (table != null) {
			setString("prevState", prevState.toString());
			setString("state", state.toString());
			
//			setString("lifter", lifter.get().toString());
		}
	}//updateTable

	public void postSqueezerData() {
		if (table != null) {
			setDouble("squeezer_voltage", squeezer.getMotorOutputVoltage());
//			setDouble("leftspin_voltage", leftSpin.getMotorOutputVoltage());
//			setDouble("rightspin_voltage", rightSpin.getMotorOutputVoltage());
			
			setDouble("squeezer_current", squeezer.getOutputCurrent());
//			setDouble("leftspin_current", leftSpin.getOutputCurrent());
//			setDouble("rightspin_current", rightSpin.getOutputCurrent());
			
			setDouble("squeezer_pos", squeezer.getSelectedSensorPosition(0));
			setDouble("squeezer_vel", squeezer.getSelectedSensorVelocity(0));
			
			setBoolean("Eject", state == SqueezyState.EJECT);
			setBoolean("Intake", state == SqueezyState.INTAKE);
			
			setBoolean("DetectedBox", sensors.detectBox());
			setBoolean("BoxHeld", sensors.detectBoxHeld());

			setBoolean("limits/fwd", squeezer.getSensorCollection().isFwdLimitSwitchClosed());
			setBoolean("limits/rev", squeezer.getSensorCollection().isRevLimitSwitchClosed());
			
//			setDouble("drive_left_pos", Drive.getInstance().getEncoderLeft());
//			setDouble("drive_right_pos", Drive.getInstance().getEncoderRight());
		}
	}//postSqueezerData
	
	private void setString(String key, String value) {
		table.getEntry(key).setString(value);
	}//setString

	private void setDouble(String key, double value) {
		table.getEntry(key).setDouble(value);
	}//setDouble

	private void setBoolean(String key, boolean value) {
		table.getEntry(key).setBoolean(value);
	}//setBoolean

}//Squeezy
