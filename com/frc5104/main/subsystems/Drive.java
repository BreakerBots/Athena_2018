package com.frc5104.main.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
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

	private Drive() {
		drive.setDeadband(0);
		talonLeftFollower.set(ControlMode.Follower, 11);
		talonRightFollower.set(ControlMode.Follower, 13);
		
	}
	
	public void arcadeDrive(double moveVal, double rotateVal) {
		drive.arcadeDrive(moveVal, rotateVal);
		
		SmartDashboard.putNumberArray("Drive Encoders", new double[] {getEncoderLeft(), getEncoderRight()});
	}//arcadeDrive
	
	public int getEncoderLeft() {
		return talonLeftMain.getSelectedSensorPosition(0);
	}//getEncoderLeft
	
	public int getEncoderRight() {
		return talonRightMain.getSelectedSensorPosition(0);
	}//getEncoderRight
	
	public void resetEncoders() {
		talonLeftMain.setSelectedSensorPosition(0, 0, 20);
		talonRightMain.setSelectedSensorPosition(0, 0, 20);
	}//resetEncoders
	
	public void postValuesToNetworkTable() {

	}//postValuesToNetworkTable
	
}//Drive
