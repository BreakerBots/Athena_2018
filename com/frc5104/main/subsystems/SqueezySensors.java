package com.frc5104.main.subsystems;

public class SqueezySensors {

	static SqueezySensors instance = null;
	
	public static SqueezySensors getInstance() {
		if (instance == null) {
			instance = new SqueezySensors();
		}
		return instance;
	}//getInstance
	
	FilteredUltraSonic centerUltra = new FilteredUltraSonic(0, 1, 50);
	FilteredUltraSonic leftUltra = new FilteredUltraSonic(2, 3);
	FilteredUltraSonic rightUltra = new FilteredUltraSonic(4, 5);
	
	private SqueezySensors() {
		centerUltra.init();
		leftUltra.init();
		rightUltra.init();
	}//SqueezySensors
	
	public void updateSensors() {
		centerUltra.update();
		leftUltra.update();
		rightUltra.update();
		
	}//updateSensors

	public boolean detectBox() {
		/*
		 * Squeezy's maximum no-block separation is 19in.
		 * Squeezy's minimum no-block separation is 8in
		 * 
		 * So, if the sum of the distances from the ultrasonics falls under 15in,
		 * it must be that there is a block between the two ultrasonics.
		 * 
		 * At the shortest distance, Left+Right will still be above 15in,
		 * at the largest distance, any significant block-sized object, (11-13in)
		 * will bring the Left+Right distance down to (19-11)+(0) == 8in.
		 */
		if (leftUltra.getDistance() + rightUltra.getDistance() < 14)
			return true;
		else
			return false;
	}//detectBox
	
	public boolean detectBoxGone() {
		if (centerUltra.getDistance() > 11.5)
			return true;
		else
			return false;
//		if (leftUltra.getDistance() > 4 && rightUltra.getDistance() > 4)
//			return true;
//		else
//			return false;
	}//detectBoxGone
	
	public boolean detectBoxHeld() {
		if (centerUltra.getDistance() < 6)
//			leftUltra.getDistance() < 2 &&
//			rightUltra.getDistance() < 2)
			return true;
		else
			return false;
	}//detectBoxHeld
	
	public double[] getDistances() {
		double[] distances = new double[3];
		distances[0] = centerUltra.getDistance();
		distances[1] = leftUltra.getDistance();
		distances[2] = rightUltra.getDistance();
		
		return distances;
	}//getInstantDistances
	
}//SqueezySensors
