/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team5104.robot;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.vision.VisionPipeline;
import edu.wpi.first.wpilibj.vision.VisionThread;

public class Robot extends IterativeRobot {
	private VisionThread visionThread;
	private double centerX = 0.0;
	
	private final Object imgLock = new Object();
	
	TalonSRX talon1 = new TalonSRX(11);
	TalonSRX talon2 = new TalonSRX(12);
	TalonSRX talon3 = new TalonSRX(13);
	TalonSRX talon4 = new TalonSRX(14);
	CustomDrive drive = new CustomDrive(talon1, talon2, talon3, talon4);
	
	Joystick controller = new Joystick(0);
	
//	public void robotInit() {
//	    UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
//	    camera.setResolution(640, 480);
//	    
//	    visionThread = new VisionThread(camera, new MVisionPipeline(), pipeline -> {
//	        if (!pipeline.filterContoursOutput().isEmpty()) {
//	            Rect r = Imgproc.boundingRect(pipeline.filterContoursOutput().get(0));
//	            synchronized (imgLock) {
//	                centerX = r.x + (r.width / 2);
//	            }
//	        }
//	    });
//	    visionThread.start();	        
//	}
	
	public void teleopPeriodic() {
		drive.ArcadeDrive(controller.getRawAxis(0), controller.getRawAxis(1));
	}
	
//	public void autonomousPeriodic() {
//	    double centerX;
//	    synchronized (imgLock) {
//	        centerX = this.centerX;
//	    }
//	    double turn = centerX - (640 / 2);
//	    drive.ArcadeDrive(-0.6, turn * 0.005);
//	}
}
