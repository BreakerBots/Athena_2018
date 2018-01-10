
public class TemplateSubsystem {
	//Class-specific configurations
	static final int MY_DEVICE_ID = 4;

	//Object members
	String name;
	int time;

	public TemplateSubsystem (String some, int args){
		//Initialize components of this subsystem
		name = some;
		time = args;
	}//TemplateSubsystem

	//Some abstracted functions for use from the main robot class
	public void push(){
		//Some code to complete a 'push' motion
	}

	public void open(){
		//Open system
	}//open
	public void close(){
		//Close system
	}//close

}//TemplateSubsystem
