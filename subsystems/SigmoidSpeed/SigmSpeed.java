//Returns speed with a sigmoid function
package org.usfirst.frc.team5104.robot;

public class SigmSpeed {
	private double aDist;
	private double dDist;
	private double target;
	private double current;
	
	public SigmSpeed(double aDist, double dDist, double target) {
		this.aDist = aDist;
		this.dDist = dDist;
		this.target = target;
	}
	
	public void setAccDist(int x) {
		this.aDist = x;
	}
	
	public void setDecDist(int x) {
		this.dDist = x;
	}
	
	public void setTarget(int x) {
		this.target = x;
	}
	
	public double getSpeed(double current) {
		this.current = Math.abs(current);
		if(this.current < this.aDist) {
			return sigm(current/aDist) + .1;
		} else if(this.current > (this.target - this.dDist)) {
			return sigm((target - current)/dDist);
		} else
			return 1;
	}
	
	public double sigm(double x) {
		double y, z;
		y = Math.pow(81, (0.5 - x));
		z = 1/(1 + y);
		return z;
	}
}
