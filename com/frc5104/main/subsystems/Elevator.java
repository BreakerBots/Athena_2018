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
	
	public static final int TALON_ID = 31;
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
	private TalonSRX talon = new TalonSRX(TALON_ID);
	private NetworkTable table = null;
	
	private double effort = kHoldEffort;
	
	private Elevator () {
	}//Elevator
	
	public void update() {
		if (talon.getSensorCollection().isRevLimitSwitchClosed())
			talon.setSelectedSensorPosition(0, 0, 10);

		double elevatorEffort = talon.getMotorOutputPercent();
		if (getBoolean("closed_loop_control", false)) {
			elevatorEffort = joy.getRawAxis(AXIS_ID);
			elevatorEffort = Deadband.getDefault().get(elevatorEffort);
			talon.set(ControlMode.PercentOutput, elevatorEffort);
		} else {
			talon.set(ControlMode.Position, getDouble("setpoint", 
					talon.getSelectedSensorPosition(0)));
		}
	}//update
	
	//----- Elevator Actions ------//
	public void raise () {
		effort = kRaiseEffort;
	}//raise
	
	public void lower() {
		effort = kLowerEffort;
	}//lower
	
	public void hold() {
		effort = kHoldEffort;
	}//hold
	
	//----- Elevator Sensors ------//
	public boolean getLowerLimit() {
		return talon.getSensorCollection().isRevLimitSwitchClosed();
	}//getLowerLimit
	
	public boolean getUpperLimit() {
		return talon.getSensorCollection().isFwdLimitSwitchClosed();
	}//getUpperLimit
	
	public boolean isLowEnoughToDrop() {
		return talon.getSelectedSensorPosition(0) > SOFT_STOP_BOTTOM - 2000;
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
		boolean lower = talon.getSensorCollection().isFwdLimitSwitchClosed();
		boolean upper = talon.getSensorCollection().isRevLimitSwitchClosed();
		if (lower) {
			talon.setSelectedSensorPosition(0, 0, 10);
		}

		setBoolean("limits/lower-fwd", lower);
		setBoolean("limits/upper-rev", upper);
		
		setDouble("motor/effort", talon.getMotorOutputPercent());
		setDouble("motor/voltage", talon.getMotorOutputVoltage());
		setDouble("motor/current", talon.getOutputCurrent());
		
		setDouble("pid/position", talon.getSelectedSensorPosition(0));
		setDouble("pid/velocity", talon.getSelectedSensorVelocity(0));
		
		setDouble("pid/i_accum", talon.getIntegralAccumulator(0));
		if (getBoolean("pid/clear_i_accum", false)) {
			setBoolean("pid/clear_i_accum", false);
			talon.setIntegralAccumulator(0, 0, 10);
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
