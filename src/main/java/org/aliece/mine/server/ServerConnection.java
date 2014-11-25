package org.aliece.mine.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;

import org.aliece.mine.config.ServerCapabilities;
import org.aliece.mine.net.Connection;
import org.aliece.mine.server.model.HandshakePacket;
import org.aliece.mine.utils.CharsetUtil;
import org.aliece.mine.utils.TimeUtil;

public class ServerConnection extends Connection {
	private static final long AUTH_TIMEOUT = 15 * 1000L;

	private volatile int txIsolation;
	private volatile boolean autocommit;
	private long lastInsertId;
	private NonBlockingSession session;

	private AtomicBoolean hasOkRsp = new AtomicBoolean(false);

	public ServerConnection(AsynchronousSocketChannel channel)
			throws IOException {
		super(channel);
		this.autocommit = true;
	}

	@Override
	public boolean isIdleTimeout() {
		return TimeUtil.currentTimeMillis() > Math.max(lastWriteTime,
				lastReadTime) + AUTH_TIMEOUT;
	}

	public int getTxIsolation() {
		return txIsolation;
	}

	public void setTxIsolation(int txIsolation) {
		this.txIsolation = txIsolation;
	}

	public boolean isAutocommit() {
		return autocommit;
	}

	public void setAutocommit(boolean autocommit) {
		this.autocommit = autocommit;
	}

	public long getLastInsertId() {
		return lastInsertId;
	}

	public void setLastInsertId(long lastInsertId) {
		this.lastInsertId = lastInsertId;
	}

	public NonBlockingSession getSession2() {
		return session;
	}

	public void setSession2(NonBlockingSession session2) {
		this.session = session2;
	}

	@Override
	public void close(String reason) {
		super.close(reason);
	}

	public AtomicBoolean isHasOkRsp() {
		return hasOkRsp;
	}

	public void register() {
		this.asynRead();

		HandshakePacket handshakePacket = new HandshakePacket();
		handshakePacket.packetNum = 0;
		handshakePacket.protocal = 10;
		handshakePacket.version = "mine-server".getBytes();
		handshakePacket.threadId = id;
		handshakePacket.salt = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8 };
		handshakePacket.serverCapabilities = getServerCapabilites();
		handshakePacket.serverLanguage = (byte) (CharsetUtil.getIndex(charset));
		handshakePacket.serverStatus = 0x0002;
		handshakePacket.restOfSalt = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
				10, 11 };

		seed = new byte[handshakePacket.salt.length
				+ handshakePacket.restOfSalt.length];
		System.arraycopy(handshakePacket.salt, 0, seed, 0,
				handshakePacket.salt.length);
		System.arraycopy(handshakePacket.restOfSalt, 0, seed,
				handshakePacket.salt.length, handshakePacket.restOfSalt.length);

		ByteBuffer bb = this.allocate();
		handshakePacket.write(bb);

		this.write(bb);
	}

	private int getServerCapabilites() {
		int flag = 0;
		flag |= ServerCapabilities.CLIENT_LONG_PASSWORD;
		flag |= ServerCapabilities.CLIENT_FOUND_ROWS;
		flag |= ServerCapabilities.CLIENT_LONG_FLAG;
		flag |= ServerCapabilities.CLIENT_CONNECT_WITH_DB;
		// flag |= Capabilities.CLIENT_NO_SCHEMA;
		// flag |= Capabilities.CLIENT_COMPRESS;
		flag |= ServerCapabilities.CLIENT_ODBC;
		// flag |= Capabilities.CLIENT_LOCAL_FILES;
		flag |= ServerCapabilities.CLIENT_IGNORE_SPACE;
		flag |= ServerCapabilities.CLIENT_PROTOCOL_41;
		flag |= ServerCapabilities.CLIENT_INTERACTIVE;
		// flag |= Capabilities.CLIENT_SSL;
		flag |= ServerCapabilities.CLIENT_IGNORE_SIGPIPE;
		flag |= ServerCapabilities.CLIENT_TRANSACTIONS;
		// flag |= ServerDefs.CLIENT_RESERVED;
		flag |= ServerCapabilities.CLIENT_SECURE_CONNECTION;
		return flag;
	}

}
