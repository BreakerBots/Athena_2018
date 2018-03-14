package com.frc5104.autonomous;

import com.frc5104.main.subsystems.Drive;

import edu.wpi.first.wpilibj.command.Command;

public class Move extends Command {
	double target;

    public Move(double inches) {
    	target = inches * PIDauto.getInstance().TPI;
    }

    protected void initialize() {
//    	PIDauto.getInstance().moveController.reset();
    	PIDauto.getInstance().movesController.reset();
//    	
//    	PIDauto.getInstance().moveController.setSetpoint(Drive.getInstance().getEncoderLeft() + target);
    	PIDauto.getInstance().movesController.setSetpoint(PIDauto.getInstance().ahrs.getAngle());
//    	
//    	if (target > -40*PIDauto.getInstance().TPI) {
//    		Drive.getInstance().resetEncoders();
//    		PIDauto.getInstance().moveController.setSetpoint(target);
//    	}
//
//    	PIDauto.getInstance().moveController.enable();
    	PIDauto.getInstance().movesController.enable();
    	
    	target = Drive.getInstance().getEncoderLeft() + target;
    	
    }

    protected void execute() {
//    	Drive.getInstance().arcadeDrive(PIDauto.getInstance().moveToDistance, PIDauto.getInstance().movesDistance);
    	int encoder = Drive.getInstance().getEncoderLeft();

    	//    	double mErr = PIDauto.getInstance().moveController.getError();
//    	double mOut = PIDauto.getInstance().moveController.get();
    	double mErr = target, mOut = encoder;
    	double tErr = PIDauto.getInstance().movesController.getError();
    	double tOut = PIDauto.getInstance().movesController.get();
    	System.out.printf("mErr: %.2f\tmOut: %.2f\\ttErr: %.2f\\ttOut: %.2f\n", mErr, mOut, tErr, tOut);
    	
    	int e = 100;
    	
    	if (encoder < target-e)
    		Drive.getInstance().arcadeDrive(-0.65, PIDauto.getInstance().movesDistance);
    	else if (encoder > target+e)
    		Drive.getInstance().arcadeDrive(0.65, PIDauto.getInstance().movesDistance);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
//        return PIDauto.getInstance().moveController.onTarget();
    	return Math.abs(Drive.getInstance().getEncoderLeft()-target) < 100;
    }

    // Called once after isFinished returns true
    protected void end() {
    	Drive.getInstance().arcadeDrive(0, 0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	
    }
}
