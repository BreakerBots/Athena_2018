package Autonomous;

import Subsystems.CustomDrive;
import edu.wpi.first.wpilibj.command.Command;

public class Move extends Command {
	double target;

    public Move(double inches) {
    	target = inches * PIDauto.getInstance().TPI;
    }

    protected void initialize() {
    	PIDauto.getInstance().moveController.reset();
    	PIDauto.getInstance().movesController.reset();
    	
    	PIDauto.getInstance().moveController.setSetpoint(PIDauto.getInstance().talonL.getSelectedSensorPosition(0) + target);
    	PIDauto.getInstance().movesController.setSetpoint(PIDauto.getInstance().ahrs.getAngle());
    	
    	PIDauto.getInstance().moveController.enable();
    	PIDauto.getInstance().movesController.enable();
    	
    }

    protected void execute() {
    	CustomDrive.getInstance().arcadeDrive(PIDauto.getInstance().moveToDistance, PIDauto.getInstance().movesDistance);
    	
    	double mErr = PIDauto.getInstance().moveController.getError(); double mOut = PIDauto.getInstance().moveController.get(); double tErr = PIDauto.getInstance().movesController.getError(); double tOut = PIDauto.getInstance().movesController.get(); System.out.printf("mErr: %.2f\tmOut: %.2f\\ttErr: %.2f\\ttOut: %.2f\n", mErr, mOut, tErr, tOut);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return PIDauto.getInstance().moveController.onTarget();
    }

    // Called once after isFinished returns true
    protected void end() {
    	CustomDrive.getInstance().arcadeDrive(0, 0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	
    }
}
