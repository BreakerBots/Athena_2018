package org.usfirst.frc.team5104.robot;

import java.util.ArrayList;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.vision.VisionThread;

public class Robot extends IterativeRobot {
	GripPipeline grip = new GripPipeline();
	
	CustomDrive drive = new CustomDrive(11, 12, 13, 14);
	
	int avgX = 0, avgY = 0;
	
	static double lengthBetweenContours;
	static double distanceFromTarget;
	static double lengthError;
	static double[] centerX;
	static double HEIGHT_CLOSENESS = .15;

    public void robotInit() {    
    	    	
    	UsbCamera camera = CameraServer.getInstance().startAutomaticCapture("lifecam", 0);
    	camera.setResolution(320, 240);
    	camera.setWhiteBalanceAuto();
    	camera.setFPS(30);
    	camera.setExposureAuto();
    	camera.setBrightness(50);

    	new Thread(() -> {
            CvSink cvSink = CameraServer.getInstance().getVideo();
//            CvSource outputStream = CameraServer.getInstance().putVideo("cam1", 640, 480);
//            outputStream.setResolution(640, 480);
//        	outputStream.setFPS(30);
            
            CvSource output = CameraServer.getInstance().putVideo("target", 320, 240);
            
            Mat source = new Mat(240,320,CvType.CV_8UC3);
                        
            while(!Thread.interrupted()) {
            	cvSink.grabFrame(source);
            	
            	grip.process(source);
            	
            	ArrayList<MatOfPoint> contours = grip.filterContoursOutput();

            	int sumX = 0, sumY = 0, count = 0;
            	
            	Point[] points;
            	for (int i = 0; i < contours.size(); i++) {
            		points = contours.get(i).toArray();

            		for (Point p: points) {
            			count++;
            			sumX += p.x;
            			sumY += p.y;
            		}
            	}
            	
            	avgX = 20; avgY = 20;
            	if (count != 0) {
	            	avgX = sumX/count;
	            	avgY = sumY/count;
            	}
            	
            	Imgproc.rectangle(source, new Point(avgX-50, avgY-50), new Point(avgX+50,avgY+50), new Scalar(0,255,255), /*thickness*/2);
            	
            	output.putFrame(source);
            }
        }).start();
    }
    
    public void autonomousPeriodic() {
    	drive.update();
        double turn = avgX - (320 / 2);
        if (avgX == 20) { 
        	drive.arcadeDrive(0, 0.5);
        }
        else {
        	drive.arcadeDrive(0.0, turn * 0.004);
        }
    	System.out.println(turn);
    }
}