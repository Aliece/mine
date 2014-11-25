package org.aliece.mine.net;

import java.nio.channels.CompletionHandler;

import org.aliece.mine.buffer.BufferQueue;
import org.apache.log4j.Logger;

public class NIOConnector implements CompletionHandler<Void, AbstractConnection> {
	private static final Logger logger = Logger.getLogger(NIOConnector.class);
	private static final ConnectIdGenerator ID_GENERATOR = new ConnectIdGenerator();
	protected int socketRecvBuffer = 16 * 1024;
	protected int socketSendBuffer = 8 * 1024;
	protected int packetHeaderSize = 4;
	protected int maxPacketSize = 16 * 1024 * 1024;
	protected int writeQueueCapcity = 8;
	protected long idleTimeout = 8 * 3600 * 1000L;
	private NIOProcessor[] processors;
	private int nextProcessor;
	private long connectCount;

	@Override
	public void completed(Void result, AbstractConnection attachment) {
		finishConnect(attachment);
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

	@Override
	public void failed(Throwable exc, AbstractConnection conn) {
		conn.onConnectFailed(exc);
	}

	private void postConnect(AbstractConnection c) {
		c.setPacketHeaderSize(packetHeaderSize);
		c.setMaxPacketSize(maxPacketSize);
		c.setWriteQueue(new BufferQueue(writeQueueCapcity));
		c.setIdleTimeout(idleTimeout);
	}

	public long getConnectCount() {
		return connectCount;
	}

	public void setProcessors(NIOProcessor[] processors) {
		this.processors = processors;
	}

	private void finishConnect(AbstractConnection c) {
		System.out.println(c);
		
		postConnect(c);
		try {
			if (((Connection) c).finishConnect()) {
				c.setId(ID_GENERATOR.getId());
				NIOProcessor processor = nextProcessor();
				c.setProcessor(processor);
				c.register();
			}
		} catch (Throwable e) {
			logger.info("connect err " + e);
		}
	}

	private NIOProcessor nextProcessor() {
		int inx = ++nextProcessor;
		if (inx >= processors.length) {
			nextProcessor = 0;
			inx = 0;
		}
		return processors[inx];
	}

	private static class ConnectIdGenerator {

		private static final long MAX_VALUE = Long.MAX_VALUE;

		private long connectId = 0L;
		private final Object lock = new Object();

		private long getId() {
			synchronized (lock) {
				if (connectId >= MAX_VALUE) {
					connectId = 0L;
				}
				return ++connectId;
			}
		}
	}

}
