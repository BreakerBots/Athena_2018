package org.usfirst.frc.team5104.robot;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.Ultrasonic;

public class SanicCache {
	private Ultrasonic sanic;
	private ArrayList<Double> cache = new ArrayList<Double>();
	private double max = 100;
	private double min = 0;
	
	public SanicCache(int input, int output) {
		sanic = new Ultrasonic(input, output);
	}
	
	public SanicCache(int input, int output, double minReading, double maxReading) {
		sanic = new Ultrasonic(input, output);
		max = maxReading; min = minReading;
	}
	
	public void collect() {
		cache.add(uclamp(sanic.getRangeInches()));
	}
	
	public double getAvg(int cacheSize) {
		Double avg = 0.0;
		for (int i = cache.size(); i > cacheSize; i++) {
			avg += cache.get(i);
		}
		return avg / cacheSize;
	}
	
	public double getLast() {
		return cache.get(cache.size() - 1);
	}
	
	public double getRaw() {
		return sanic.getRangeInches();
	}
	
	private Double uclamp(Double val) {
		if (val > max) { return max; }
		if (val < min) { return min; }
		return val;
	}
}
