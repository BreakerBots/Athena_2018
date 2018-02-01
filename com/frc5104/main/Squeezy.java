package com.frc5104.main;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.frc5104.main.subsystems.ButtonS;

import edu.wpi.first.wpilibj.DoubleSolenoid;

public class Squeezy {

	static final double kCloseEffort = 0.7;
	static final double kOpenEffort= -0.7;
	
	static final double kIntakeEffort = 0.5;
	static final double kEjectEffort = 0.5;
	
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
	
	SqueezyState state = SqueezyState.HOLDING;
	
	ButtonS buttonIntake = new ButtonS(3),
			buttonEject = new ButtonS(1),
			buttonCancel = new ButtonS(2);
	
	//Talon IDs start with 2_
	TalonSRX leftSpin,
			rightSpin,
			squeezer;
	
	DoubleSolenoid lifter;
	
	private Squeezy () {
		leftSpin  = new TalonSRX(21);
		rightSpin = new TalonSRX(22);
		squeezer  = new TalonSRX(23);
		
		lifter = new DoubleSolenoid(0,1);
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
			if (buttonIntake.Pressed)
				state = SqueezyState.CLOSING;
			if (buttonCancel.Pressed)
				state = SqueezyState.EMPTY;
			break;
		case CLOSING: //Should not exist with Ultrasonic
			if (detectClosedOnBox())
				state = SqueezyState.HOLDING;
			if (buttonCancel.Pressed)
				state = SqueezyState.INTAKE;
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
		
	}//poll
	
	public void updateState() {
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
	}//spinIn
	
	private void spinOut() {
		leftSpin.set(ControlMode.PercentOutput, kEjectEffort);
		rightSpin.set(ControlMode.PercentOutput, kEjectEffort);
	}//spinOut
	
	private void spinStop() {
		leftSpin.set(ControlMode.PercentOutput, 0);
		rightSpin.set(ControlMode.PercentOutput, 0);
	}//setSpinnerState
	
	private void open() {
		squeezer.set(ControlMode.PercentOutput, kOpenEffort);
	}//open
	
	private void close() {
		squeezer.set(ControlMode.PercentOutput, kCloseEffort);
	}//close
	
	private void raise() {
		lifter.set(DoubleSolenoid.Value.kForward);
	}//raise
	
	private void lower() {
		lifter.set(DoubleSolenoid.Value.kReverse);
	}//lower
	
}//Squeezy
