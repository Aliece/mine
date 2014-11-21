package org.aliece.mine;

import org.aliece.mine.config.loader.xml.XMLConfigLoader;
import org.aliece.mine.config.model.SystemConfig;

public class ConfigInitializer {
	private volatile SystemConfig system;
	
	public ConfigInitializer() {
		XMLConfigLoader configLoader = new XMLConfigLoader();
		this.system = configLoader.getSystemConfig();
	}
	
	public SystemConfig getSystem() {
		return system;
	}
}
