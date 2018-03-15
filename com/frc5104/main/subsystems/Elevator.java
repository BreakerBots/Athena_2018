package com.frc5104.main.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.frc5104.utilities.Deadband;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Elevator {

	public static final int SOFT_STOP_BOTTOM = 0;
	public static final int SOFT_STOP_TOP = -16150;
	
	public static final boolean TWO_TALONS = true;
	public static final int TALON1_ID = 31;
	public static final int TALON2_ID = 32;
	public static final int AXIS_ID = 5;
	
	public enum Stage {
		kBottom(0),
		kPortal(-3000),
		kSwitch(-5000),
		kLowerScale(-13000),
		kTop(-16500);
		
		int position;
		Stage (int position){
			this.position = position;
		}
		public int getCounts() {
			return this.position;
		}
	}//Stage
	
	static Elevator m_instance = null;
	
	public static Elevator getInstance() {
		if (m_instance == null)
			m_instance = new Elevator();
		return m_instance;
	}//getInstance

	private Joystick joy = new Joystick(0);
	private TalonSRX talon1 = new TalonSRX(TALON1_ID),
					 talon2;
	private NetworkTable table = null;
	
	enum Control {
		kPosition, kEffort
	}
	Control controlMode = Control.kEffort;
	public double effort = 0;
	Stage currentStage;
	
	private Elevator () {
		if (TWO_TALONS) {
			talon2 = new TalonSRX(TALON2_ID);
			talon2.set(ControlMode.Follower, TALON1_ID);
		}
		
		talon1.configReverseSoftLimitEnable(true, 10);
		talon1.configReverseSoftLimitThreshold(SOFT_STOP_TOP, 10);

		talon1.configForwardSoftLimitEnable(true, 10);
		talon1.configForwardSoftLimitThreshold(SOFT_STOP_BOTTOM, 10);

		
		talon1.config_kP(0, 2, 10);
		talon1.config_IntegralZone(0, 2000, 10);
		
		currentStage = Stage.kBottom;
		setEffort(0);
//		setPosition(currentStage);
	}//Elevator
	
	public void setEffort(double effort) {
		controlMode = Control.kEffort;
		this.effort = effort;
		
		update();
	}//setEffort
	
	public void setPosition(Stage stage) {
		controlMode = Control.kPosition;
		this.currentStage = stage;
		
		update();
	}//setPosition
	
	public void moveUp() {
		int newStage = currentStage.ordinal() + 1;
		if (newStage >= Stage.values().length)
			newStage = Stage.values().length - 1;
		
		setPosition(Stage.values()[newStage]);
	}//moveUp
	
	public void moveDown() {
		int newStage = currentStage.ordinal() - 1;
		if (newStage < 0)
			newStage = 0;
		
		setPosition(Stage.values()[newStage]);
	}//moveDown
	
	public int getError() {
		return talon1.getClosedLoopError(0);
	}//getError
	
	public boolean onTarget() {
		return Math.abs(talon1.getSelectedSensorPosition(0) - currentStage.getCounts()) < 200;
	}//onTarget
	
	public void userControl() {
		if (!getBoolean("closed_loop_control", false)) {
			effort = joy.getRawAxis(AXIS_ID);
			effort = Deadband.getDefault().get(effort);
			setEffort(effort);
		} else {
			setPosition(Stage.valueOf(getString("setpoint", currentStage.toString())));
		}
		update();
		updateTables();
	}//userControl

	private void update() {
		if (controlMode == Control.kEffort) {
			talon1.set(ControlMode.PercentOutput, effort);
		} else if (controlMode == Control.kPosition) {
			talon1.set(ControlMode.Position, currentStage.getCounts());
			System.out.println("Elevator Effort: "+talon1.getMotorOutputPercent());
		}
	}//update
	
	//----- Elevator Sensors ------//
	public boolean getLowerLimit() {
		return talon1.getSensorCollection().isFwdLimitSwitchClosed();
	}//getLowerLimit
	
	public boolean getUpperLimit() {
		return talon1.getSensorCollection().isRevLimitSwitchClosed();
	}//getUpperLimit
	
	public int getEncoderPosition() {
		return talon1.getSelectedSensorPosition(0);
	}//getEncoderPosition
	
	public boolean isLowEnoughToDrop() {
		return talon1.getSelectedSensorPosition(0) > SOFT_STOP_BOTTOM - 2000;
	}//isLowEnoughToDrop
	
	public static boolean isRaised() {
		if (m_instance != null)
			return !m_instance.isLowEnoughToDrop();
		return false;
	}//isRaised
	
	//----- Network Tables ---------//
	public void initTable(NetworkTable inst) {
		if (inst == null) {
			inst = NetworkTableInstance.getDefault().getTable("elevator");
		}
		table = inst;
		
		if (!table.containsKey("closed_loop_control"))
			setBoolean("closed_loop_control",false);
		
		if (!table.containsKey("pid/clear_i_accum"))
			setBoolean("pid/clear_i_accum", false);
	}//initTable
	
	public void updateTables() {
		boolean lower = talon1.getSensorCollection().isFwdLimitSwitchClosed();
		boolean upper = talon1.getSensorCollection().isRevLimitSwitchClosed();
		if (lower) {
			talon1.setSelectedSensorPosition(0, 0, 10);
		}

		setBoolean("limits/lower-fwd", lower);
		setBoolean("limits/upper-rev", upper);
		
		setDouble("motor/effort", talon1.getMotorOutputPercent());
		setDouble("motor/voltage", talon1.getMotorOutputVoltage());
		setDouble("motor/current", talon1.getOutputCurrent());
		
		if (TWO_TALONS) {
			setDouble("motor2/effort", talon2.getMotorOutputPercent());
			setDouble("motor2/voltage", talon2.getMotorOutputVoltage());
			setDouble("motor2/current", talon2.getOutputCurrent());
		}
		
		setDouble("pid/position", talon1.getSelectedSensorPosition(0));
		setDouble("pid/velocity", talon1.getSelectedSensorVelocity(0));
		
		setDouble("pid/i_accum", talon1.getIntegralAccumulator(0));
		if (getBoolean("pid/clear_i_accum", false)) {
			setBoolean("pid/clear_i_accum", false);
			talon1.setIntegralAccumulator(0, 0, 10);
		}
	}//updateTables
	
	private void setString(String key, String value) {
		table.getEntry(key).setString(value);
	}//setString

	private String getString(String key, String defaultValue) {
		return table.getEntry(key).getString(defaultValue);
	}//getBoolean
	
	private void setDouble(String key, double value) {
		table.getEntry(key).setDouble(value);
	}//setDouble

	private double getDouble(String key, double defaultValue) {
		return table.getEntry(key).getDouble(defaultValue);
	}//getBoolean
	
	private void setBoolean(String key, boolean value) {
		table.getEntry(key).setBoolean(value);
	}//setBoolean
	
	private boolean getBoolean(String key, boolean defaultValue) {
		return table.getEntry(key).getBoolean(defaultValue);
	}//getBoolean
	
	
}//Elevator
