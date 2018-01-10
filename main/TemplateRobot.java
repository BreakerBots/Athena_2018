//Imports the other files needed by the program
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

//As CANTalons are 3rd-party components, the libraries are provided separate from the WPILib library
	//and must be installed on their own from CrossTheRoadElectronics
import com.ctre.CANTalon;

public class TemplateRobot extends IterativeRobot {

//Define Robot Constant Values
	static final int LEFT_DRIVE_INDEX = 1;
	static final int RIGHT_DRIVE_INDEX = 2;


//Defines the variables as members of our Robot class
	Joystick controller;

	CANTalon leftTalon, rightTalon;
	RobotDrive driveTrain;

	Timer timer;

//Initializes the variables in the robotInit method, this method is called when the robot is initializing
	public void robotInit() {
		controller = new Joystick(1);

		leftTalon = new CANTalon(LEFT_DRIVE_INDEX);
		rightTalon = new CANTalon(RIGHT_DRIVE_INDEX);
		driveTrain = new RobotDrive(leftTalon,rightTalon);

		timer = new Timer();
	}

//Called once at the start of the autonomous period
	public void autonomousInit() {
		timer.reset();	//Reset timer to 0
		timer.start();	//Start timer
	}//autonomousInit
//Called repeatedly during the autonomous period
	public void autonomousPeriodic() {
		if (timer.get() < 2){	//For the first two seconds of autonomous...
			driveTrain.arcadeDrive(0.5,0);	//drive forward at half-speed
		} else {
			driveTrain.arcadeDrive(0,0);	//stop
		}
	}//autonomousPeriodic

//Called once at the start of the teleoperated period
	public void teleopInit() {
		
	}//teleopInit
//Called repeatedly during the teleoperated period
	public void teleopPeriodic() {
		double x = controller.getX();
		double y = controller.getY();
		driveTrain.arcadeDrive(y,x);
	}//teleopPeriodic

//Called once each time the robot is disabled
	//Both at the end of autonomous and at the end of teleop
	public void disabledInit() {
		
	}//disabledInit

}//TemplateRobot
