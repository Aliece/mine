package org.aliece.mine.config;

import java.util.concurrent.locks.ReentrantLock;

import org.aliece.mine.ConfigInitializer;
import org.aliece.mine.config.model.SystemConfig;
import org.aliece.mine.utils.TimeUtil;

public class MineConfig {

	private static final int RELOAD = 1;
	private static final int ROLLBACK = 2;
	public static final String USERNAME = "test";
	public static final String PASSWORD = "test";
	public static final String DBNAME = "dbtest";

	private volatile SystemConfig system;
	private long reloadTime;
	private long rollbackTime;
	private int status;
	private final ReentrantLock lock;

	public MineConfig() {
		ConfigInitializer confInit = new ConfigInitializer();
		this.system = confInit.getSystem();
		this.reloadTime = TimeUtil.currentTimeMillis();
		this.rollbackTime = -1L;
		this.status = RELOAD;
		this.lock = new ReentrantLock();
	}

	public SystemConfig getSystem() {
		return system;
	}

	public ReentrantLock getLock() {
		return lock;
	}

	public long getReloadTime() {
		return reloadTime;
	}

	public long getRollbackTime() {
		return rollbackTime;
	}

}
