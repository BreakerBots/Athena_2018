package com.frc5104.vision;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class VisionThread extends Thread {

	int brightness = 100;
	int exposure = 0;
	UsbCamera usb;
//	GPipeline pipeline;
	
	@Override
	public void run() {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		usb = CameraServer.getInstance().startAutomaticCapture("lifecam", 0);
		usb.setBrightness(brightness);
		usb.setExposureManual(exposure);
		usb.setResolution(320, 240);
		usb.setFPS(15);

		CvSink sink = CameraServer.getInstance().getVideo();
		
		CvSource output = CameraServer.getInstance().putVideo("cooked", 320, 240);
		
		Mat src = new Mat(240,320,CvType.CV_8UC3);
		while (!Thread.interrupted()) {
			adjustCameraSettings();
			
			sink.grabFrame(src);
			
			
			
		}
		
	}//run
	private void adjustCameraSettings() {
		int newBri = (int)SmartDashboard.getNumber("brightness", brightness);
		int newExp = (int)SmartDashboard.getNumber("exposure", exposure);
		
		if (newBri != brightness) {
			brightness = newBri;
			usb.setBrightness(brightness);
		}
		if (newExp != exposure) {
			exposure = newExp;
			usb.setExposureManual(exposure);
			
		}
	}
}//VisionThread
