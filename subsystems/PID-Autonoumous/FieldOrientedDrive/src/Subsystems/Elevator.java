package Subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import utilities.Deadband;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import javafx.scene.input.TouchPoint.State;

public class Elevator {
	static Elevator m_instance = null;
	public static Elevator getInstance() {
		if (m_instance == null)
			m_instance = new Elevator();
		return m_instance;
	}
	

	
	//Values
	public static final int SOFT_STOP_BOTTOM = 0;
	public static final int SOFT_STOP_TOP = -16150;
	public static final double kRaiseEffort = 0.8;
	public static final double kLowerEffort = 0.5;
	public static final double kHoldEffort = 0;

	//Talons
	private TalonSRX talonE1 = new TalonSRX(31);
	private TalonSRX talonE2 = new TalonSRX(32);
	
	//Network Table
	private NetworkTable table = null;
	
	//Pid
	public PIDController elController;
	public double target = 0;
	private double TPI = 100 /* Need Val */;
	double elevatorDistance;
		//Elevator PID Values
		static double eP = 0.07;
		static double eI = 0.000002;
		static double eD = 0.00002;
		static double eF = 0.00;
		static double eToleranceTicks = 10.0;
	
	//Set Stage
	public void setPosition(double ticks) {
		target = ticks;
	}
	
	//Set Stage
	enum stage             { Bottom, Exchange, Portal, Switch, LowScale, Climb };
	double stagePoints[] = { 0,      -500,     -2500,  -3000,  -5000,    -6000 };
	
	public void setStage(stage Stage) {
		target = stagePoints[Stage.ordinal()];
	}//Set Specific Stage
	
	enum dir { up, down }; public void changeStage(dir Dir) {
		stage currentStage = ticksToStage(target);
		if (Dir == dir.up) {
			//Up
			if (currentStage.ordinal() < stagePoints.length - 1) {
				target = stagePoints[currentStage.ordinal() + 1];
			} 
			else {
				target = stagePoints[stagePoints.length - 1];
			}
		}
		else {
			//Down
			if (currentStage.ordinal() > 0) {
				target = stagePoints[currentStage.ordinal() - 1];
			} 
			else {
				target = stagePoints[0];
			}
		}
	} //Move Change Up and Down
	
	public stage getCurrentStage() {
		return ticksToStage(target);
	} //Get the current stage by guessing
	
	private stage ticksToStage(double ticks) {
		for (int i = 0; i < stagePoints.length; i++) {
			if (ticks >= stagePoints[i]) {
				if (i == 0) { return stage.values()[i]; }
				else {
					if ((stagePoints[i] - ticks) >= (ticks - stagePoints[i - 1])) {
						return stage.values()[i];
					}
					else { 
						return stage.values()[i - 1];
					}
				}
			}
		}
		return stage.values()[stagePoints.length - 1];
	}

	public void robotInit() {
		//Elevator
		elController = new PIDController(eP, eI, eD, eF, new PIDSource() {
			public void setPIDSourceType(PIDSourceType pidSource) {
			}
			public PIDSourceType getPIDSourceType() {
				return PIDSourceType.kDisplacement;
			}
			public double pidGet() {
				return talonE1.getSelectedSensorPosition(0);
			}}, new PIDOutput() {
				@Override
				public void pidWrite(double output) {
					elevatorDistance = output;
				}
			});
	    elController.setOutputRange(-1.0, 1.0);
	    elController.setAbsoluteTolerance(eToleranceTicks);
	    
	    talonE1.setSelectedSensorPosition(0, 0, 10);
	}
	
	public void start() {
		talonE2.set(ControlMode.Follower, talonE1.getDeviceID());
    	
    	elController.reset();
    	
    	elController.setSetpoint(target);
    	
    	elController.enable();   
	}
	
	public void update() {
		elController.setSetpoint(target);
		talonE1.set(ControlMode.PercentOutput, elevatorDistance);
	}
	
	
	
	
	//----- Elevator Sensors ------//
	public boolean getLowerLimit() {
		return talonE1.getSensorCollection().isFwdLimitSwitchClosed();
	}//getLowerLimit
	
	public boolean getUpperLimit() {
		return talonE1.getSensorCollection().isRevLimitSwitchClosed();
	}//getUpperLimit
	
	public int getEncoderPosition() {
		return talonE1.getSelectedSensorPosition(0);
	}//getEncoderPosition
	
	public boolean isLowEnoughToDrop() {
		return talonE1.getSelectedSensorPosition(0) > SOFT_STOP_BOTTOM - 2000;
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
		boolean lower = talonE1.getSensorCollection().isFwdLimitSwitchClosed();
		boolean upper = talonE1.getSensorCollection().isRevLimitSwitchClosed();
		if (lower) {
			talonE1.setSelectedSensorPosition(0, 0, 10);
		}

		setBoolean("limits/lower-fwd", lower);
		setBoolean("limits/upper-rev", upper);
		
		setDouble("motor/effort", talonE1.getMotorOutputPercent());
		setDouble("motor/voltage", talonE1.getMotorOutputVoltage());
		setDouble("motor/current", talonE1.getOutputCurrent());
		
		setDouble("motor2/effort", talonE2.getMotorOutputPercent());
		setDouble("motor2/voltage", talonE2.getMotorOutputVoltage());
		setDouble("motor2/current", talonE2.getOutputCurrent());
		
		setDouble("pid/position", talonE1.getSelectedSensorPosition(0));
		setDouble("pid/velocity", talonE1.getSelectedSensorVelocity(0));
		
		setDouble("pid/i_accum", talonE1.getIntegralAccumulator(0));
		if (getBoolean("pid/clear_i_accum", false)) {
			setBoolean("pid/clear_i_accum", false);
			talonE1.setIntegralAccumulator(0, 0, 10);
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
