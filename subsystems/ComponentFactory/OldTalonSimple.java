package org.usfirst.frc.team5104.robot;

import org.usfirst.frc.team5104.robot.ControllerHandler.Control;
import edu.wpi.first.wpilibj.Talon;

public class OldTalonSimple implements Component {
	Talon talon;
	Control axis;
	
	public OldTalonSimple(int port, Control axis) {
		talon = new Talon(port);
		this.axis = axis;
	}
	
	public void init() {
		
	}

	public void update() {
		talon.set(ControllerHandler.getInstance().getAxis(axis));
	}
}
