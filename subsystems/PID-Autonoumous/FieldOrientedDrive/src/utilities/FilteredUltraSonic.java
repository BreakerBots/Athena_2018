package utilities;

import edu.wpi.first.wpilibj.Ultrasonic;

public class FilteredUltraSonic {
	Ultrasonic ultra; 
	
	private double avgDistance = 0;
	private double instantDistance = 0;
	private double distances[];
	private int index = 0;
	
//	public FilteredUltraSonic(int ping, int echo, int filterLength, int maxDistance) {
//		ultra = new Ultrasonic(ping, echo);
//		distances = new double[filterLength];
//		
//		maxDistance = maxDistance;
//	}
	
	public FilteredUltraSonic(int ping, int echo, int filterLength) {
		ultra = new Ultrasonic(ping, echo);
		distances = new double[filterLength];
	}//FilteredUltraSonic
	
	public FilteredUltraSonic(int ping, int echo) {
		//this(ping, echo, 20);
		ultra = new Ultrasonic(ping, echo);
		distances = new double[20];
	}//FilteredUltraSonic
	
	public void init() {
		ultra.setAutomaticMode(true);
		ultra.setEnabled(true);
		
		instantDistance = ultra.getRangeInches();
		avgDistance = instantDistance;
	}//init
	
	public void update() {
		instantDistance = ultra.getRangeInches();
		
		if (index > distances.length-1) {
			index = 0;
		}
		distances[index] = instantDistance;
		index++;
				
		avgDistance = avg(distances);
	}//update
	
	public double getDistance() {
		return avgDistance;
	}//getDistance
	
	private double avg(double[] nums) {
		double total = 0;
		for (int i = 0; i < nums.length; i++) {
			total += nums[i];
		}
		return total/nums.length;
	}//avg
}//FilteredUltraSonic
