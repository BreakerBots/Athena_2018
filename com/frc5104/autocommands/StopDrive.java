package com.frc5104.autocommands;

import com.frc5104.main.subsystems.Drive;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class StopDrive extends Command {

    public StopDrive() {
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	System.out.println("Stopping Drive");
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Drive.getInstance().arcadeDrive(0, 0);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
