package org.aliece.mine.config.loader.xml;

import org.aliece.mine.config.model.SystemConfig;

public class XMLServerLoader {

	private final SystemConfig system;

	public XMLServerLoader() {
		this.system = new SystemConfig();
	}

	public SystemConfig getSystem() {
		return system;
	}
}
