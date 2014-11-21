package org.aliece.mine.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;

import org.aliece.mine.net.AbstractConnection;
import org.aliece.mine.net.NIOProcessor;
import org.aliece.mine.utils.TimeUtil;

public class AIOConnection extends AbstractConnection {
	protected long id;
	protected String host;
	protected int port;
	protected int localPort;
	protected long idleTimeout;
	protected boolean isFinishConnect;

	public AIOConnection(AsynchronousSocketChannel channel) {
		super(channel);
	}

	public void register() {
		this.asynRead();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getLocalPort() {
		return localPort;
	}

	public void setLocalPort(int localPort) {
		this.localPort = localPort;
	}

	public long getIdleTimeout() {
		return idleTimeout;
	}

	public void setIdleTimeout(long idleTimeout) {
		this.idleTimeout = idleTimeout;
	}

	public boolean isIdleTimeout() {
		return TimeUtil.currentTimeMillis() > Math.max(lastWriteTime,
				lastReadTime) + idleTimeout;
	}


	public boolean finishConnect() throws IOException {
		localPort = ((InetSocketAddress) channel.getLocalAddress()).getPort();
		isFinishConnect = true;
		return true;
	}

	public void setProcessor(NIOProcessor processor) {
		super.setProcessor(processor);
		processor.addConnection(this);
	}

	@Override
	public String toString() {
		return "Connection [id=" + id + ", host=" + host + ", port="
				+ port + ", localPort=" + localPort + "]";
	}

	@Override
	public void error(int errCode, Throwable t) {
		
	}

	@Override
	public void onConnectFailed(Throwable e) {
		
	}

}
