package org.aliece.mine.net.handler;

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;

import org.aliece.mine.config.MineConfig;
import org.aliece.mine.server.ServerConnection;
import org.aliece.mine.server.model.AuthPacket;
import org.aliece.mine.utils.CharsetUtil;
import org.aliece.mine.utils.SecurityUtil;
import org.apache.log4j.Logger;

public class AuthServerHandler implements NIOHandler {

	private static final Logger logger = Logger
			.getLogger(AuthServerHandler.class);
	private static final byte[] AUTH_OK = new byte[] { 7, 0, 0, 2, 0, 0, 0, 2,
			0, 0, 0 };

	private ServerConnection source;

	public AuthServerHandler(ServerConnection source) {
		this.source = source;
	}

	@Override
	public void handle(byte[] data) {

		AuthPacket authPacket = new AuthPacket();
		authPacket.read(data);
		logger.info("======login=========" + authPacket.username + ";"
				+ authPacket.db + ";");
		if (!MineConfig.USERNAME.equals(authPacket.username)) {
			logger.error("=====用户名不正确====");
			return;
		}
		if (!MineConfig.DBNAME.equals(authPacket.db)) {
			logger.error("=====数据库名不正确====");
			return;
		}
		if (!checkPassword(authPacket.password)) {
			logger.error("=====密码不正确====");
			return;
		}

		success(authPacket);
	}

	private void success(AuthPacket authPacket) {
		logger.info("======认证通过，登录成功=========");
		// 切换前端连接的命令处理器。
		source.setHandler(new CommandServerHandler(this.source));

		this.source.setCharset(CharsetUtil.getCharset(authPacket.charset));
		ByteBuffer buffer = source.allocate();
		// 返回OKPacket
//		source.write(AUTH_OK);
		source.write(source.writeToBuffer(AUTH_OK, buffer));
	}

	private boolean checkPassword(byte[] password) {
		String pass = MineConfig.PASSWORD;

		// check null
		if (pass == null || pass.length() == 0) {
			if (password == null || password.length == 0) {
				return true;
			} else {
				return false;
			}
		}
		if (password == null || password.length == 0) {
			return false;
		}
		// encrypt
		byte[] encryptPass = null;
		try {
			encryptPass = SecurityUtil.scramble411(pass.getBytes(),
					source.getSeed());
		} catch (NoSuchAlgorithmException e) {
			logger.warn(source.toString(), e);
			return false;
		}
		if (encryptPass != null && (encryptPass.length == password.length)) {
			int i = encryptPass.length;
			while (i-- != 0) {
				if (encryptPass[i] != password[i]) {
					return false;
				}
			}
		} else {
			return false;
		}
		return true;
	}
}
