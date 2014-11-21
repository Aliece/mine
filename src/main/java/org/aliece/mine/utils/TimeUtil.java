package org.aliece.mine.utils;

public class TimeUtil {
	private static long CURRENT_TIME = System.currentTimeMillis();

	public static final long currentTimeMillis() {
		return CURRENT_TIME;
	}

	public static final void update() {
		CURRENT_TIME = System.currentTimeMillis();
	}

}
