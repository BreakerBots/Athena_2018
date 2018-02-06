package com.frc5104.main.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class Drive {

	static Drive m_instance = new Drive();
	
	WPI_TalonSRX talonLeftMain = new WPI_TalonSRX(11),
			 talonRightMain = new WPI_TalonSRX(13),
			 talonLeftFollower = new WPI_TalonSRX(12),
			 talonRightFollower = new WPI_TalonSRX(14);
	
	SpeedControllerGroup leftDrive = new SpeedControllerGroup(talonLeftMain, talonLeftFollower),
						rightDrive = new SpeedControllerGroup(talonRightMain, talonRightFollower);
	
	DifferentialDrive drive = new DifferentialDrive(leftDrive, rightDrive);	

	private Drive() {}
	
	public void arcadeDrive(double moveVal, double rotateVal) {
		drive.arcadeDrive(moveVal, rotateVal);
	}//arcadeDrive
	
	public static Drive getInstance() {
		return m_instance;
	}//getInstance
	
	public double getEncoderLeft() {
		return talonLeftMain.getSelectedSensorPosition(0);
	}//getEncoderLeft
	
	public double getEncoderRight() {
		return talonRightMain.getSelectedSensorPosition(0);
	}//getEncoderRight
	
	public void postValuesToNetworkTable() {

	}//postValuesToNetworkTable
	
}//Drive
