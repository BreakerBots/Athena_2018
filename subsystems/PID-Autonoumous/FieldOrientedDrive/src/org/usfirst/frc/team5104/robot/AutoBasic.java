package org.usfirst.frc.team5104.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
public class AutoBasic extends CommandGroup {
	String gameData; 
	char position;
	Robot robot;
	
    public AutoBasic(Robot robot) {
    	this.robot = robot;
//	    move(209.235);
//	    turn(-90);
//	    move(230);
//	    turn(-90);
//	    move(21.735);
//	    turn(-90);
//	    move(23.5);
    	move(120);
    	delay(150);
    	turn(-90);
    	delay(150);
    	move(240);
    	delay(150);
    	turn(-90);
    	delay(150);
    	move(120);
    	delay(150);
    	turn(-90);
    	delay(150);
    	move(240);
    	delay(150);
    	turn(-90);
    }
    
    public void move(double inches) {
    	addSequential(new Move(robot, inches));
    }
    
    public void turn(double degrees) {
    	addSequential(new Turn(robot, degrees));
    }
    
    public void delay(int milliseconds) {
	    addSequential(new Delay(milliseconds));
    }
}
