package org.usfirst.frc.team5104.robot;

import java.util.ArrayList;

public class SubsystemHandler {
	static SubsystemHandler m_instance;
	public static SubsystemHandler getInstance() {
		if (m_instance == null) {
			m_instance = new SubsystemHandler();
		}
		return m_instance;
	} private SubsystemHandler() {  }
	
	
	private ArrayList<Subsystem> systems = new ArrayList<Subsystem>();
	
	public void addSystem(Subsystem system) {
		systems.add(system);
	}
	
	public void robotInit() {
		for (Subsystem i:systems) {
			i.robotInit();
		}
	}
	
	public void init() {
		for (Subsystem i:systems) {
			i.init();
		}
	}
	
	public void update() {
		for (Subsystem i:systems) {
			i.update();
		}
	}
}
