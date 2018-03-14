package com.frc5104.autonomous;

import com.frc5104.main.subsystems.Drive;

import edu.wpi.first.wpilibj.command.Command;

public class PIDTurn extends Command {
	double target;
		
    public PIDTurn(double degrees) {
        target = degrees;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	PIDauto.getInstance().turnController.reset();
    	
    	PIDauto.getInstance().ahrs.reset();
    	double angle = 0;
    	for (int i=0; i<5; i++)
    		angle = PIDauto.getInstance().ahrs.getAngle();
    	PIDauto.getInstance().turnController.setSetpoint( (angle + target) );

    	PIDauto.getInstance().turnController.enable();
    	
    }//initialize

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Drive.getInstance().arcadeDrive(0, PIDauto.getInstance().rotateToAngleRate);
    	System.out.println("Turn Error: " + PIDauto.getInstance().turnController.getError()
    					+ "Turn Output: " + PIDauto.getInstance().turnController.get());
    	
//    	if (PIDauto.getInstance().turnController.getError() > 20) {
//    		PIDauto.getInstance().turnController.reset();
//    		PIDauto.getInstance().turnController.enable();
//    	}
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return PIDauto.getInstance().turnController.onTarget();
    }

    // Called once after isFinished returns true
    protected void end() {
    	Drive.getInstance().arcadeDrive(0, 0);
    }//end

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	
    }
}
