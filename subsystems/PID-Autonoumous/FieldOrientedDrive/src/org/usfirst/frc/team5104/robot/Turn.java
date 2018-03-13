package org.usfirst.frc.team5104.robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class Turn extends Command {
	Robot robot;
	double target;

    public Turn(Robot robot, double degrees) {
        this.robot = robot;
        target = degrees;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	robot.turnController.reset();
    	
    	robot.ahrs.reset();
    	robot.turnController.setSetpoint( (/*robot.ahrs.getAngle()*/ + target) );

    	robot.turnController.enable();
    	
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	robot.drive.arcadeDrive(0, robot.rotateToAngleRate);
    	System.out.println("Turn Error: " + robot.turnController.getError());
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return robot.turnController.onTarget();
    }

    // Called once after isFinished returns true
    protected void end() {
    	
    	robot.drive.arcadeDrive(0, 0);
    	
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	
    }
}
