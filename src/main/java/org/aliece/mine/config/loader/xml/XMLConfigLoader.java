package org.aliece.mine.config.loader.xml;

import org.aliece.mine.config.loader.ConfigLoader;
import org.aliece.mine.config.model.SystemConfig;

public class XMLConfigLoader implements ConfigLoader {

	private final SystemConfig system;

	public XMLConfigLoader() {
		XMLServerLoader serverLoader = new XMLServerLoader();
		this.system = serverLoader.getSystem();
	}
	
	@Override
    public SystemConfig getSystemConfig() {
        return system;
    }

}
