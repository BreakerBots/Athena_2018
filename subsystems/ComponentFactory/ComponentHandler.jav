package org.usfirst.frc.team5104.robot;

import java.util.ArrayList;
import java.util.List;

public class ComponentHandler {
	static ComponentHandler m_instance;
	public static ComponentHandler getInstance() {
		if (m_instance == null) {
			m_instance = new ComponentHandler();
		}
		return m_instance;
	} private ComponentHandler() {  }
	//
	
	private ArrayList<Component> components = new ArrayList<Component>(); 
	
	public void addSubsystem(Component component) { 
		components.add(component);
	}
	
	public void addSubsystems(Component components[]) { 
		for (int i = 0; i < components.length; i++) {
			this.components.add(components[i]);
		}
	}
	
	public void init() {
		for (int i = 0; i < components.size(); i++) {
			components.get(i).init();
		}
	}
	
	public void update() {
		for (int i = 0; i < components.size(); i++) {
			components.get(i).update();
		}
	}
}
