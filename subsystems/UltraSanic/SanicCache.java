package org.usfirst.frc.team5104.robot;

import edu.wpi.first.wpilibj.Ultrasonic;

public class SanicCache {
	private Ultrasonic sanic;
	private double[] cache = new double[100];
	private double maxi = 100;
	private double mini = 0;
	private int index = 0;
	private int loops = 0;
	
	public SanicCache(int input, int output) {
		sanic = new Ultrasonic(input, output);
	}
	
	public SanicCache(int input, int output, double minReading, double maxReading) {
		sanic = new Ultrasonic(input, output);
		maxi = maxReading; mini = minReading;
	}
	
	public void collect() {
		cache[index] = (clamp(sanic.getRangeInches(), mini, maxi));
		index++; if (index >= 100) { index = 0; } loops++;
	}
	
	public double getAvg(int cacheSize) {
		double avg = 0.0;
		for (int i = loops < 100 ? loops - 1 : cache.length; i > cacheSize; i++) {
			avg += cache[i];
		}
		return avg / cacheSize;
	}
	
	public double getLast() {
		return (clamp(sanic.getRangeInches(), mini, maxi));
	}
	
	public double getRaw() {
		return sanic.getRangeInches();
	}
	
	private double clamp(double val, double min, double max) {
		if (val > max) { return max; }
		if (val < min) { return min; }
		return val;
	}
}
