package com.frc5104.main.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.frc5104.utilities.ControllerHandler;
import com.frc5104.utilities.HMI;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Squeezy {
	
	private class PriorityCounter {
		private int counter = 0;
		
		public boolean getLow() {
			return counter % 75 == 0;
		}
		public boolean getMedium() {
			return counter % 50 == 0;
		}
		public boolean getHigh() {
			return counter % 20 == 0;
		}
		public void count() {
			counter++;
		}
	}//PriorityCounter
	
	public static final int MAIN_ID = 21;
	public static final int LEFT_ID = 22;
	public static final int RIGHT_ID = 23;
	
	static final int kHasCubePosition = -68000;
	
	//For Opening/Closing Arms
	static final double kHoldEffort = -0.25;
	static final double kShootSqueezeEffort = -0.05;
	static final double kCloseEffort = -0.30;
	static final double kOpenEffort  = 0.25;
	
	//For Spinning Wheels
	static final double kRightSpinMultiplier = 1.1;
	static final double kIntakeEffort = -/*0.4*//*3-12-18 0.2*/0.2;
	static final double kPinchEffort = -0.2;
	public static double kEjectEffort = 0.6;
	
	public enum SqueezyState {
		EMPTY, EJECT,
		//Auto State Chart
		INTAKE, CLOSING, HOLDING, TILT_UNJAM, UNJAM,
		//Manual State Chart
		MANUAL_OPEN, MANUAL_CLOSE
		
	}
	boolean manualStateDiagram = false;
	
	static Squeezy m_instance;
	
	public static Squeezy getInstance() {
		if (m_instance == null) {
			m_instance = new Squeezy();
		}
		return m_instance;
	}//getInstance
	
	NetworkTable table = null;
	
	//An unreasonable starting value
	private boolean useManualControls = false;/* (!)Add Mixin For Auto and Manual Controls */
	private boolean calibrated = false;
	private SqueezyState prevState = SqueezyState.EJECT;
	SqueezyState state = SqueezyState.HOLDING;
	ControllerHandler controller = ControllerHandler.getInstance();
	
	//Talon IDs fall are contained in [20,30)
	TalonSRX squeezer  = new TalonSRX(MAIN_ID);
	TalonSRX leftSpin  = new TalonSRX(LEFT_ID);
	TalonSRX rightSpin = new TalonSRX(RIGHT_ID);
	
	//Eject Timing
	long ejectTime = System.currentTimeMillis();
	
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
	
	TimedButton grabbedSensor = new TimedButton();
	boolean leftUnjam = true;
	public void updateState() {
		if (squeezer.getSensorCollection().isFwdLimitSwitchClosed()) {
//			if (!calibrated)
			squeezer.setSelectedSensorPosition(0, 0, 10);
			calibrated = true;
		}
		
		prevState = state;
		
		switch (state) {
		case EMPTY:
			if (controller.getPressed(HMI.kSqueezyIntake)) {
				state = SqueezyState.INTAKE;
				manualStateDiagram = false;
			}
			break;
		case EJECT:
			if ((System.currentTimeMillis() - ejectTime) > 1000)
				if (manualStateDiagram)
					state = SqueezyState.MANUAL_OPEN;
				else
					state = SqueezyState.UNJAM;
			break;
		//--------------------------Auto State Chart--------------------------//
		case INTAKE:
			//UltraSonic: Move directly to holding when box is detected by motor stalls
			if (sensors.detectBox()) {
				state = SqueezyState.CLOSING;
				grabbedSensor.reset();
			}
			break;
		case CLOSING:
//			if (controller.getPressed(HMI.kSqueezyCancel))
//				state = SqueezyState.UNJAM;
//			if (!(sensors.detectBox() && squeezer.getSelectedSensorPosition(0) > -90000))
//				state = SqueezyState.INTAKE;
//			if (sensors.detectBoxHeld()) {
//				state = SqueezyState.HOLDING;
//				ControllerHandler.getInstance().rumbleHardFor(0.5, 0.5);
			int vel = getEncoderVelocity();
			int pos = getEncoderPosition();
			boolean bool = vel < 10 && vel > -500;
//				bool = bool && pos < -60000;
			grabbedSensor.update(bool);
				
			if (grabbedSensor.get(20)) {
				state = SqueezyState.HOLDING;
				controller.rumbleHardFor(0.5, 0.5);
			}
			if (squeezer.getSensorCollection().isRevLimitSwitchClosed())
				state = SqueezyState.INTAKE;
			break;
		case HOLDING:
			if (squeezer.getSensorCollection().isRevLimitSwitchClosed())
				state = SqueezyState.INTAKE;
//			if (getEncoderPosition() > kHasCubePosition) {
			if (controller.getPressed(HMI.kSqueezyKnock)) {
				leftUnjam = sensors.getDistances()[1] > sensors.getDistances()[2];
				ejectTime = System.currentTimeMillis();
				state = SqueezyState.TILT_UNJAM;
			}
			break;
		case TILT_UNJAM:
			if (squeezer.getSensorCollection().isRevLimitSwitchClosed())
				state = SqueezyState.EMPTY;
			if (System.currentTimeMillis() - ejectTime > 500) {
				state = SqueezyState.HOLDING;
			}
			break;
		case UNJAM:
			if (squeezer.getSensorCollection().isFwdLimitSwitchClosed())
				state = SqueezyState.INTAKE;
			if (controller.getPressed(HMI.kSqueezyIntake))
				state = SqueezyState.INTAKE;
			break;
			
		//---------------------Manual State Chart------------------------//
		case MANUAL_OPEN:
		case MANUAL_CLOSE:
			//Manual Controls are available at any time,
				// thus they do not fall in here.
			//However, we do want the ability to go back into auto/intake mode.
			if (controller.getPressed(HMI.kSqueezyIntake)) {
				state = SqueezyState.INTAKE;
				manualStateDiagram = false;
			}
			break;
		}//switch
		
		if (controller.getPressed(HMI.kSqueezyOpen)) {
			state = SqueezyState.MANUAL_OPEN;
			manualStateDiagram = true;
		}
		if (controller.getPressed(HMI.kSqueezyClose)) {
			state = SqueezyState.MANUAL_CLOSE;
			manualStateDiagram = true;
		}
		
		if (controller.getPressed(HMI.kSqueezyEject)) {
			System.out.println("EJECTING!!!");
			ejectTime = System.currentTimeMillis();
			state = SqueezyState.EJECT;
		}
		if (controller.getPressed(HMI.kSqueezyNeutral))
			state = SqueezyState.EMPTY;
		
	}//poll
	
	public void update() {
		update(false);
	}//update no squeezy up
	public void update(boolean squeezyIsUp) {
		switch (state) {
		case EMPTY:
			raise();
			spinStop();
			if (squeezyIsUp)
				close();
			else
				leave();
			break;
		case EJECT:
			lower();
			spinOut();
			shootSqueeze();
			break;

		//-------------Auto State Chart--------------//
		case INTAKE:
			lower();
			spinIn();
			if (squeezyIsUp)
				close();
			else
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
		case TILT_UNJAM:
			raise();
			spinUnjam();
			hold();
			break;
		case UNJAM:
			lower();
			spinStop();
			if (squeezyIsUp)
				close();
			else
				open();
			break;
		//-------------Manual State Chart--------------//
		case MANUAL_OPEN:
			lower();
			spinIn();
			if (!squeezyIsUp)
				open();
			else
				close();
			break;
		case MANUAL_CLOSE:
			lower();
			spinIn();
			close();
			break;
		}//switch
		
		postData();
		
	}//updateState
	
	//--------- Squeezy States ----------//
	public double getRelativeEncoderPosition() {
		double raw_pos = getEncoderPosition();
		double rel_pos = raw_pos / -120000;
		
		return rel_pos;
	}//getRelativeEncoderPosition
	public double getRelativeEncoderVelocity() {
		int raw_vel = getEncoderVelocity();
		
		//if raw_vel = (raw_pos - raw_prev_pos) / time
		//and pos = raw_pos / -120000.0
		
		double rel_vel = raw_vel / -120000;
				
		return rel_vel;
	}//getRelativeEncoderVelocity
	public int getEncoderPosition() {
		return squeezer.getSelectedSensorPosition(0);
	}//getEncoderPosition
	public int getEncoderVelocity() {
		return squeezer.getSelectedSensorVelocity(0);
	}//getEncoderVelocity
	
	public boolean getOpenLimitSwitch() {
		return squeezer.getSensorCollection().isFwdLimitSwitchClosed();
	}//getOpenLimitSwitch

	public boolean getClosedLimitSwitch() {
		return squeezer.getSensorCollection().isRevLimitSwitchClosed();
	}//getOpenLimitSwitch

	public void forceState(SqueezyState newState) {
		state = newState;
		if (state == SqueezyState.EJECT)
			ejectTime = System.currentTimeMillis();
	}//forceState
	
	public boolean isInState (SqueezyState checkState) {
		return state == checkState;
	}//isInState
	
	public boolean hasCube() {
		return state == SqueezyState.HOLDING;
	}//hasCube
	
	//--------- Squeezy Actions ---------//
	private void setSpinners(double effort) {
		setSpinners(effort, 0);
	}//setSpinners	
	private void setSpinners(double effort, int invert) {
		//invert
		//0 invert none
		//-1 invert left
		//1 invert right
		switch (invert) {
		case -1:
			leftSpin.set(ControlMode.PercentOutput, -effort);
			rightSpin.set(ControlMode.PercentOutput, -kRightSpinMultiplier*effort);
			break;
		case 0:
			leftSpin.set(ControlMode.PercentOutput, effort);
			rightSpin.set(ControlMode.PercentOutput, -kRightSpinMultiplier*effort);
			break;
		case 1:
			leftSpin.set(ControlMode.PercentOutput, effort);
			rightSpin.set(ControlMode.PercentOutput, kRightSpinMultiplier*effort);
			break;
		}
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
	private void spinUnjam() {
		setSpinners(kIntakeEffort, leftUnjam?-1:1);
	}//spinUnjam
	
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
	private void leave() {
		squeezer.set(ControlMode.PercentOutput, 0);
	}//leave
	
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
	
	PriorityCounter networkTablePriorityCounter = new PriorityCounter();
	public void postData() {
		if (table != null) {
			
			if (networkTablePriorityCounter.getLow()) {
				setBoolean("sensors/detect_box", sensors.detectBox());
				setBoolean("sensors/detect_box_gone", sensors.detectBoxGone());
				setBoolean("sensors/detect_box_held", sensors.detectBoxHeld());
				
				double[] dists = sensors.getDistances();
				
				setDouble("ultrasonic/center", dists[0]);
				setDouble("ultrasonic/left", dists[1]);
				setDouble("ultrasonic/right", dists[2]);
	
				setString("prevState", prevState.toString());
				setString("state", state.toString());
			}

			if (networkTablePriorityCounter.getLow()) {
				setDouble("debug/voltage_squeezer", squeezer.getMotorOutputVoltage());
				setDouble("debug/voltage_leftspin", leftSpin.getMotorOutputVoltage());
				setDouble("debug/voltage_rightspin", rightSpin.getMotorOutputVoltage());
				
				setDouble("debug/current_squeezer", squeezer.getOutputCurrent());
				setDouble("debug/current_leftspin", leftSpin.getOutputCurrent());
				setDouble("debug/current_rightspin", rightSpin.getOutputCurrent());
			}
			
			if (networkTablePriorityCounter.getMedium()) {
				//Adding 1 to the hopefully non-negative encoder position should eliminate 1/0
				setDouble("pos_rel", getRelativeEncoderPosition());
				setDouble("vel_rel", getRelativeEncoderVelocity());
				setDouble("pos", getEncoderPosition());
				setDouble("vel", getEncoderVelocity());
				
				setDouble("in-out", getEncoderPosition()/1000);
				setBoolean("Up", false);
				
				setBoolean("debug/state_Eject", state == SqueezyState.EJECT);
				setBoolean("debug/state_Intake", state == SqueezyState.INTAKE);
				
				setBoolean("DetectedBox", sensors.detectBox());
				setBoolean("BoxHeld", sensors.detectBoxHeld());
	
				setBoolean("debug/limit-fwd", squeezer.getSensorCollection().isFwdLimitSwitchClosed());
				setBoolean("debug/limit-rev", squeezer.getSensorCollection().isRevLimitSwitchClosed());
			}
			
			networkTablePriorityCounter.count();
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
