package org.aliece.mine.server;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;

import org.aliece.mine.net.Connection;
import org.aliece.mine.utils.TimeUtil;

public class ServerConnection extends Connection {
	private static final long AUTH_TIMEOUT = 15 * 1000L;

	private volatile int txIsolation;
	private volatile boolean autocommit;
	private volatile boolean txInterrupted;
	private volatile String txInterrputMsg = "";
	private long lastInsertId;
	private NonBlockingSession session;

	private AtomicBoolean hasOkRsp = new AtomicBoolean(false); // 表示在Data返回后还有OK
																// Packet报文的语句

	public ServerConnection(AsynchronousSocketChannel channel)
			throws IOException {
		super(channel);
		this.txInterrupted = false;
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
}
