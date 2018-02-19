package com.frc5104.utilities;

public class Deadband {

	private boolean inverted = false;
	
	private double radius;
	private double m, b;
	
	public Deadband (double radius) {
		this.radius = radius;
		
		//m = (y2-y1)/(x2-x1)
		//b = -m*radius
		m = (1-0) / (1-radius);
		b = 0 - m*radius;
		
	}//Deadband
	
	public double get(double x) {
		double output = 0;
		
		if (inverted) x *= -1;
		
		if (x > radius)
			output = m*x+b;
		else if (x < -radius)
			output = m*x-b;
			
		return output;
	}//get
	
	public void setInverted (boolean invert) {
		inverted = invert;
	}//setInverted
	
}//Deadband
