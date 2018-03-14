package com.frc5104.autonomous;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.frc5104.main.subsystems.Drive;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.SPI.Port;

public class PIDauto {
	
	static PIDauto m_instance = null;
	public static PIDauto getInstance() {
		if (m_instance == null) {
			m_instance = new PIDauto();
			m_instance.init();
		}
		return m_instance;
	} private PIDauto() {  }
	
	//Start
	AHRS ahrs = new AHRS(Port.kMXP);
	
	//Drive
//	TalonSRX talonL = new TalonSRX(11);
//    TalonSRX talonR = new TalonSRX(13);
	
    //PID Controllers
	PIDController turnController;
	PIDController moveController; PIDController movesController;
	
	//Archer
//	double TPI = 231;
	//Ares
	double TPI = /*- 3-13-18 went backward*/273.357142857;
	
	double rotateToAngleRate;
	double moveToDistance; double movesDistance;
	
	//Turning PID Values
	static double tP = 0.05;
	static double tI = 0.0003;
	static double tD = 0.000002;
	static double tF = 0.00;
	static double tToleranceDegrees = 2;
	
	//Moving Forward PID Values
	static double mP = 4E-5;
	static double mI = 1E-6;
//	static double mI = 0;
	static double mD = 0.0001;
	static double mF = 0.00;
	static double mToleranceTicks = 80.0;
	
	//Move Straight PID Values
	static double msP = 0.0003;
	static double msI = 0.0;
	static double msD = 0.0;
	static double msF = 0.00;
	static double msToleranceDegrees = 2;
	
	public void init() {
		//Turning
		ahrs.reset();
		turnController = new PIDController(tP, tI, tD, tF, new PIDSource() {
			public void setPIDSourceType(PIDSourceType pidSource) {
			}
			public PIDSourceType getPIDSourceType() {
				return PIDSourceType.kDisplacement;
			}
			public double pidGet() {
				return ahrs.getYaw();
			}}, new PIDOutput() {
			public void pidWrite(double output) {
				rotateToAngleRate = -output;
			}
		});
//	    turnController.setInputRange(-180.0f,  180.0f);
	    turnController.setOutputRange(-0.5, 0.5);
	    turnController.setAbsoluteTolerance(tToleranceDegrees);
//	    turnController.setContinuous(true);
	    
	    //Moving
	    moveController = new PIDController(mP, mI, mD, mF, new PIDSource() {
			public void setPIDSourceType(PIDSourceType pidSource) {
			}
			public PIDSourceType getPIDSourceType() {
				return PIDSourceType.kDisplacement;
			}
			public double pidGet() {
//				return -/*3-13-18 went backward*/talonL.getSelectedSensorPosition(0);
				return -Drive.getInstance().getEncoderLeft();
			}}, new PIDOutput() {
				@Override
				public void pidWrite(double output) {
					moveToDistance = output;
				}
			});
	    moveController.setOutputRange(-1.0, 1.0);
	    moveController.setAbsoluteTolerance(mToleranceTicks);
	    
//	    talonL.setSelectedSensorPosition(0, 0, 10);
//		talonR.setSelectedSensorPosition(0, 0, 10);
		Drive.getInstance().resetEncoders();
	    
		//Move Straight
		movesController = new PIDController(msP, msI, msD, msF, new PIDSource() {
			public void setPIDSourceType(PIDSourceType pidSource) {
			}
			public PIDSourceType getPIDSourceType() {
				return PIDSourceType.kDisplacement;
			}
			public double pidGet() {
				return ahrs.getAngle();
			}}, new PIDOutput() {
			public void pidWrite(double output) {
				movesDistance = output;
			}
		});
//	    movesController.setInputRange(-180.0f,  180.0f);
//	    movesController.setContinuous(true);
	    movesController.setOutputRange(-1.0, 1.0);
	    movesController.setAbsoluteTolerance(msToleranceDegrees);
	}
}
