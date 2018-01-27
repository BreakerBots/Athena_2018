package org.usfirst.frc.team5104.robot.java;

public class Test {

	public static void main(String[] args) {
		Logger logger = new Logger("C:\\Users\\BreakerBots\\Desktop\\testing");
		System.out.println("Created logger");
		
		logger.logLong("test", new LogValue() {
			public Object get() {
				return System.currentTimeMillis();
			}
			
			
		});
		System.out.println("Added log object");
		
		for (int i=0; i<100; i++){
			logger.collect();
			System.out.println("Collected: "+(i+1));
		}
		
		logger.log();
		System.out.println("Logged Data");
	}//main
	
}//Test
