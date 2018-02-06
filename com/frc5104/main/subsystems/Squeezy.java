package com.frc5104.main.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DoubleSolenoid;

public class Squeezy {

	public static final int MAIN_ID = 21;
	public static final int LEFT_ID = 22;
	public static final int RIGHT_ID = 23;
	
	static final double kCloseEffort = 0.1;
	static final double kOpenEffort= -0.1;
	
	static final double kIntakeEffort = 0.1;
	static final double kEjectEffort = 0.1;
	
	enum SqueezyState {
		EMPTY, INTAKE, CLOSING, HOLDING, LOADED, EJECT
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
	private SqueezyState prevState = SqueezyState.EJECT;
	SqueezyState state = SqueezyState.EMPTY;
	
	ButtonS buttonIntake = new ButtonS(3),
			buttonEject = new ButtonS(1),
			buttonCancel = new ButtonS(2);
	
	//Talon IDs start with 2_
	TalonSRX squeezer  = new TalonSRX(MAIN_ID),
			leftSpin  = new TalonSRX(LEFT_ID),
			rightSpin = new TalonSRX(RIGHT_ID);
	
	DoubleSolenoid lifter = new DoubleSolenoid(2,3);
	
	SqueezySensors sensors = SqueezySensors.getInstance();

	private Squeezy () {
		lifter.set(DoubleSolenoid.Value.kForward);
	}//Squeezy
	
	public void poll() {
		buttonIntake.update();
		buttonEject.update();
		buttonCancel.update();
		
		switch (state) {
		case EMPTY:
			if (buttonIntake.Pressed)
				state = SqueezyState.INTAKE;
			break;
		case INTAKE:
			//UltraSonic: Move directly to holding when box is detected by motor stalls
			if (buttonCancel.Pressed)
				state = SqueezyState.EMPTY;
			if (sensors.detectBox())
				state = SqueezyState.CLOSING;
			if (buttonIntake.Pressed)
				state = SqueezyState.CLOSING;
			break;
		case CLOSING: //Should not exist with Ultrasonic
			if (buttonCancel.Pressed)
				state = SqueezyState.INTAKE;
			if (!sensors.detectBox())
				state = SqueezyState.INTAKE;
			if (detectClosedOnBox())
				state = SqueezyState.HOLDING;
			break;
		case HOLDING:
			if (buttonEject.Pressed)
				state = SqueezyState.LOADED;
			if (detectBoxGone())
				state = SqueezyState.EMPTY;
			break;
		case LOADED:
			if (buttonEject.Pressed)
				state = SqueezyState.EJECT;
			if(detectBoxGone())
				state = SqueezyState.EMPTY;
			break;
		case EJECT:
			if (detectBoxGone())
				state = SqueezyState.EMPTY;
			state = SqueezyState.EMPTY;
			break;
		}//switch
		
		if (state != prevState) {
			updateTable();
			prevState = state;
		}
		
	}//poll
	
	public void updateState() {
		System.out.printf("Squeezy State: %10s\t",state.toString());
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
			spinStop();
			close();
			break;
		case LOADED:
			lower();
			spinStop();
			close();
			break;
		case EJECT:
			lower();
			spinOut();
			close();
			break;
		}
	}//updateState
	
	//------------ Detections -----------//

	public boolean detectClosedOnBox() {
		return true;
	}//detectClosedOnBox
	
	public boolean detectBoxGone() {
		return false;
	}//detectBoxGone
	
	//--------- Squeezy Actions ---------//
	private void spinIn() {
		leftSpin.set(ControlMode.PercentOutput, kIntakeEffort);
		rightSpin.set(ControlMode.PercentOutput, kIntakeEffort);
		System.out.printf("Spin Effort: %1.1f\t",kIntakeEffort);
	}//spinIn
	
	private void spinOut() {
		leftSpin.set(ControlMode.PercentOutput, kEjectEffort);
		rightSpin.set(ControlMode.PercentOutput, kEjectEffort);
		System.out.printf("Spin Effort: %1.1f\t",kEjectEffort);
	}//spinOut
	
	private void spinStop() {
		leftSpin.set(ControlMode.PercentOutput, 0);
		rightSpin.set(ControlMode.PercentOutput, 0);
		System.out.printf("Spin Effort: %1.1f\t", 0.0);
	}//setSpinnerState
	
	private void open() {
		squeezer.set(ControlMode.PercentOutput, kOpenEffort);
		System.out.printf("Squeezer Effort: %1.1f\t",kOpenEffort);
	}//open
	
	private void close() {
		squeezer.set(ControlMode.PercentOutput, kCloseEffort);
		System.out.printf("Squeezer Effort: %1.1f\t",kCloseEffort);
	}//close
	
	private void raise() {
		lifter.set(DoubleSolenoid.Value.kForward);
		System.out.printf("Lifter Value: %s\t", DoubleSolenoid.Value.kForward.toString());
	}//raise
	
	private void lower() {
		lifter.set(DoubleSolenoid.Value.kReverse);
		System.out.printf("Lifter Value: %s\t", DoubleSolenoid.Value.kReverse.toString());
	}//lower
	
	public void initTable(NetworkTable inst) {
		if (inst == null) {
			inst = NetworkTableInstance.getDefault().getTable("squeezy");
		}
		table = inst;
	}
	
	public void updateTable() {
		if (table != null) {
			setString("prevState", prevState.toString());
			setString("state", state.toString());
			
			setDouble("squeezer_voltage", squeezer.getMotorOutputVoltage());
			setDouble("leftspin_voltage", leftSpin.getMotorOutputVoltage());
			setDouble("rightspin_voltage", rightSpin.getMotorOutputVoltage());
			
			setDouble("squeezer_current", squeezer.getOutputCurrent());
			setDouble("leftspin_current", leftSpin.getOutputCurrent());
			setDouble("rightspin_current", rightSpin.getOutputCurrent());
			
			setString("lifter", lifter.get().toString());
			
			setBoolean("box_detected", detectBoxGone());
		}
	}//updateTable
	
	private void setString(String key, String value) {
		table.getEntry(key).setString(value);
	}//setString

	private void setDouble(String key, double value) {
		table.getEntry(key).setDouble(value);
	}//setString

	private void setBoolean(String key, boolean value) {
		table.getEntry(key).setBoolean(value);
	}//setString

}//Squeezy
