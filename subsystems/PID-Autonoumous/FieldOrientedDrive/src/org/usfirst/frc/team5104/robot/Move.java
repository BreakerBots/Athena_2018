package org.usfirst.frc.team5104.robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class Move extends Command {
	Robot robot;
	double target;

    public Move(Robot robot, double inches) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	this.robot = robot;
    	target = inches * robot.TPI;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	robot.moveController.reset();
    	robot.turnController.reset();
    	
    	robot.moveController.setSetpoint(robot.talonL.getSelectedSensorPosition(0) + target);
    	robot.turnController.setSetpoint(robot.ahrs.getAngle());
    	
    	robot.moveController.enable();
    	robot.turnController.enable();
    	
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	if (!robot.moveController.onTarget()) { 
    		robot.drive.arcadeDrive(robot.moveToDistance, robot.rotateToAngleRate);
    	}
    	else {
    		robot.drive.arcadeDrive(0, 0);
    	}
    	System.out.println("Move Error: " + robot.moveController.getError());
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return robot.moveController.onTarget();
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
