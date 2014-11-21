package org.aliece.mine.net.factory;

import java.io.IOException;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousSocketChannel;

import org.aliece.mine.buffer.BufferQueue;
import org.aliece.mine.net.Connection;

public abstract class ConnectionFactory {

	protected int socketRecvBuffer = 8 * 1024;
	protected int socketSendBuffer = 16 * 1024;
	protected int packetHeaderSize = 4;
	protected int maxPacketSize = 16 * 1024 * 1024;
	protected int writeQueueCapcity = 16;
	protected long idleTimeout = 8 * 3600 * 1000L;
	protected String charset = "utf8";

	protected abstract Connection getConnection(
			AsynchronousSocketChannel channel) throws IOException;

	public Connection make(AsynchronousSocketChannel channel)
			throws IOException {
		channel.setOption(StandardSocketOptions.SO_RCVBUF, socketRecvBuffer);
		channel.setOption(StandardSocketOptions.SO_SNDBUF, socketSendBuffer);
		channel.setOption(StandardSocketOptions.TCP_NODELAY, true);
		channel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

		Connection c = getConnection(channel);
		c.setPacketHeaderSize(packetHeaderSize);
		c.setMaxPacketSize(maxPacketSize);
		c.setWriteQueue(new BufferQueue(writeQueueCapcity));
		c.setIdleTimeout(idleTimeout);
		c.setCharset(charset);
		return c;
	}

	public int getSocketRecvBuffer() {
		return socketRecvBuffer;
	}

	public void setSocketRecvBuffer(int socketRecvBuffer) {
		this.socketRecvBuffer = socketRecvBuffer;
	}

	public int getSocketSendBuffer() {
		return socketSendBuffer;
	}

	public void setSocketSendBuffer(int socketSendBuffer) {
		this.socketSendBuffer = socketSendBuffer;
	}

	public int getPacketHeaderSize() {
		return packetHeaderSize;
	}

	public void setPacketHeaderSize(int packetHeaderSize) {
		this.packetHeaderSize = packetHeaderSize;
	}

	public int getMaxPacketSize() {
		return maxPacketSize;
	}

	public void setMaxPacketSize(int maxPacketSize) {
		this.maxPacketSize = maxPacketSize;
	}

	public int getWriteQueueCapcity() {
		return writeQueueCapcity;
	}

	public void setWriteQueueCapcity(int writeQueueCapcity) {
		this.writeQueueCapcity = writeQueueCapcity;
	}

	public long getIdleTimeout() {
		return idleTimeout;
	}

	public void setIdleTimeout(long idleTimeout) {
		this.idleTimeout = idleTimeout;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}
}
