//This requires PixyEception, PixyPacket, and PixyI2C to run properly.

package org.usfirst.frc.team9104.robot;


import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class Vision extends Subsystem {

	public PixyI2C visionPixy;
	Port port = Port.kMXP;
	String print;
	public PixyPacket[] packet1 = new PixyPacket[7];

	public Vision() {
		visionPixy = new PixyI2C("", new I2C(port, 0x54), packet1, new PixyException(print), new PixyPacket());

	}

	public void initDefaultCommand() {

	}

	public PixyPacket usePixy() {
		for (int i = 0; i < packet1.length; i++)
			packet1[i] = null;
		SmartDashboard.putString("visionPixy hello", "working");
		for (int i = 1; i < 8; i++) {
			try {
				packet1[i - 1] = visionPixy.readPacket(i);
			} catch (PixyException e) {
				SmartDashboard.putString("visionPixy Error: " + i, "exception");
			}
			if (packet1[i - 1] == null) {
				SmartDashboard.putString("visionPixy Error: " + i, "True");
				continue;
			}
			SmartDashboard.putNumber("visionPixy X Value: " + i, packet1[i - 1].X);
			SmartDashboard.putNumber("visionPixy Y Value: " + i, packet1[i - 1].Y);
			SmartDashboard.putNumber("visionPixy Width Value: " + i, packet1[i - 1].Width);
			SmartDashboard.putNumber("visionPixy Height Value: " + i, packet1[i - 1].Height);
			SmartDashboard.putString("visionPixy Error: " + i, "False");
			
			return packet1[i-1];
		}
		
		return null;
	}




	public PixyPacket[] getPegPosition() {
		PixyPacket[] blocks = visionPixy.readBlocks();
		SmartDashboard.putBoolean("Peg Blocks Array is null", blocks == null);
		if (blocks == null)
			return null;
		SmartDashboard.putString("Peg Block 0", (blocks[0] == null) ? "null" : blocks[0].toString());
		SmartDashboard.putString("Peg Block 1", (blocks[1] == null) ? "null" : blocks[1].toString());
		return blocks;
	}


}