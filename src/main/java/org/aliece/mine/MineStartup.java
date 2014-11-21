package org.aliece.mine;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.helpers.LogLog;

public class MineStartup {
	private static final String dateFormat = "yyyy-MM-dd HH:mm:ss";

	public static void main(String[] args) {
		try {
			// init
			MineServer server = MineServer.getInstance();
			server.beforeStart();

			// startup
			server.startup();
			System.out
					.println("Mine Server startup successfully. see logs in logs/mine.log");
			while (true) {
				Thread.sleep(300 * 1000);
			}
		} catch (Throwable e) {
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
			LogLog.error(sdf.format(new Date()) + " startup error", e);
			System.exit(-1);
		}
	}

}
