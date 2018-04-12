package com.frc5104.main.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Drive {

	static Drive m_instance = null;
	
	public static Drive getInstance() {
		if (m_instance == null) {
			m_instance = new Drive();
		}
		return m_instance;
	}//getInstance

	WPI_TalonSRX talonLeftMain = new WPI_TalonSRX(11),
			 talonRightMain = new WPI_TalonSRX(13),
			 talonLeftFollower = new WPI_TalonSRX(12),
			 talonRightFollower = new WPI_TalonSRX(14);
	
//	SpeedControllerGroup leftDrive = new SpeedControllerGroup(talonLeftMain, talonLeftFollower),
//						rightDrive = new SpeedControllerGroup(talonRightMain, talonRightFollower);
	
	DifferentialDrive drive = new DifferentialDrive(talonLeftMain, talonRightMain);	
	
	double kP = 0.1, kI = 0.0001, kD = 2;
	AHRS gyro = new AHRS(Port.kMXP);
	double omega = 0;
	double delta = 0;
	PIDController turnController = new PIDController(kP, kI, kD, new PIDSource() {
		double lastAngle = 0;
		@Override
		public void setPIDSourceType(PIDSourceType pidSource) {
		}
		@Override
		public PIDSourceType getPIDSourceType() {
			return PIDSourceType.kRate;
		}
		@Override
		public double pidGet() {
			double angle = gyro.getAngle();
			delta = angle - lastAngle;
			lastAngle = angle;
			return delta;
		}
		
	}, null);

	private Drive() {
		drive.setDeadband(0);
		
		talonLeftFollower.set(ControlMode.Follower, 11);
		talonRightFollower.set(ControlMode.Follower, 13);
		
		SmartDashboard.putNumber("Drive Angle", gyro.getAngle());
		SmartDashboard.putNumberArray("PID", new double[] {kP, kI, kD});
	}
	
	public void arcadeDrive(double moveVal, double rotateVal) {
		drive.arcadeDrive(moveVal, rotateVal);
	}//arcadeDrive
	
	public void pidDrive(double moveVal, double rotateVal) {
		SmartDashboard.putNumber("Drive Angle", gyro.getAngle());

		omega = rotateVal;
		turnController.setSetpoint(omega);
		
		double effort = turnController.get();
		
		SmartDashboard.putNumber("pid.input", delta);
		SmartDashboard.putNumber("pid.setpoint", omega);
		SmartDashboard.putNumber("pid.output", effort);
		
		drive.arcadeDrive(moveVal, rotateVal);
	}//pidDrive
	
	public void postValuesToNetworkTable() {

	}//postValuesToNetworkTable
	
}//Drive
