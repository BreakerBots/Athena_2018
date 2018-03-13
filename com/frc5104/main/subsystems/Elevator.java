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
	
	public static final double kRaiseEffort = 0.8;
	public static final double kLowerEffort = 0.5;
	public static final double kHoldEffort = 0;
		// Constant effort to hold up elevator
		// Might change w/ Power Cube
	
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
	public double position = 0;
	
	private Elevator () {
		if (TWO_TALONS) {
			talon2 = new TalonSRX(TALON2_ID);
			talon2.set(ControlMode.Follower, TALON1_ID);
		}
	}//Elevator
	
	public void setEffort(double effort) {
		controlMode = Control.kEffort;
		this.effort = effort;
		
		update();
	}//setEffort
	
	public void setPosition(double position) {
		controlMode = Control.kPosition;
		this.position = position;
		
		update();
	}//setPosition
	
	public void userControl() {
		if (!getBoolean("closed_loop_control", false)) {
			effort = -joy.getRawAxis(AXIS_ID);
			effort = Deadband.getDefault().get(effort);
			talon1.set(ControlMode.PercentOutput, effort);
		} else {
			talon1.set(ControlMode.Position, getDouble("setpoint", 
					talon1.getSelectedSensorPosition(0)));
		}
		update();
		updateTables();
	}//userControl

	private void update() {
		if (controlMode == Control.kEffort) {
			talon1.set(ControlMode.PercentOutput, effort);
		} else if (controlMode == Control.kPosition) {
			talon1.set(ControlMode.Position, position);
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
