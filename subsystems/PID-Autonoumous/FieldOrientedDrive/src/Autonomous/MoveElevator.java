package Autonomous;

import com.ctre.phoenix.motorcontrol.ControlMode;

import Subsystems.Elevator;
import edu.wpi.first.wpilibj.command.Command;

public class MoveElevator extends Command {
	double target;

    public MoveElevator(double ticks) {
        target = ticks;
    }

    protected void initialize() {
    	Elevator.getInstance().setPosition(target);
    }

    protected void execute() {
    	System.out.println("Elevator Error: " + Elevator.getInstance().elController.getError());
    }

    protected boolean isFinished() {
        return Elevator.getInstance().elController.onTarget();
    }

    protected void end() {

    }

    protected void interrupted() {
    	
    }
}
