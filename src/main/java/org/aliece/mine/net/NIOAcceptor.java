package org.aliece.mine.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.atomic.AtomicLong;

import org.aliece.mine.net.factory.ConnectionFactory;
import org.apache.log4j.Logger;

public class NIOAcceptor implements
		CompletionHandler<AsynchronousSocketChannel, Long> {

	private static final Logger logger = Logger.getLogger(NIOAcceptor.class);

	private static final AcceptIdGenerator ID_GENERATOR = new AcceptIdGenerator();
	private final int port;
	private final AsynchronousServerSocketChannel serverChannel;
	private final ConnectionFactory factory;
	private NIOProcessor[] processors;
	private int nextProcessor;
	private long acceptCount;
	private final String name;

	public NIOAcceptor(String name, String ip, int port,
			ConnectionFactory factory, AsynchronousChannelGroup group)
			throws IOException {
		this.name = name;
		this.port = port;
		this.factory = factory;
		serverChannel = AsynchronousServerSocketChannel.open(group);
		/** 设置TCP属性 */
		serverChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
		serverChannel.setOption(StandardSocketOptions.SO_RCVBUF, 16 * 1024);
		// backlog=100
		serverChannel.bind(new InetSocketAddress(ip, port), 100);
	}

	public String getName() {
		return name;
	}

	public void start() {
		this.pendingAccept();
	}

	public int getPort() {
		return port;
	}

	public long getAcceptCount() {
		return acceptCount;
	}

	public void setProcessors(NIOProcessor[] processors) {
		this.processors = processors;
	}

	private void accept(AsynchronousSocketChannel channel, Long id) {
		try {
			System.out.println("有客户端连接:"
					+ channel.getRemoteAddress().toString());

			Connection c = factory.make(channel);
			c.setAccepted(true);
			c.setId(id);
			NIOProcessor processor = nextProcessor();
			c.setProcessor(processor);
			c.register();
		} catch (Throwable e) {
			closeChannel(channel);
		}
	}

	private void pendingAccept() {
		if (serverChannel.isOpen()) {
			serverChannel.accept(ID_GENERATOR.getId(), this);
		} else {
			throw new IllegalStateException(
					"MyCAT Server Channel has been closed");
		}

	}

	@Override
	public void completed(AsynchronousSocketChannel result, Long id) {
		accept(result, id);
		// next pending waiting
		pendingAccept();

	}

	@Override
	public void failed(Throwable exc, Long id) {
		logger.info("acception connect failed:" + exc);
		// next pending waiting
		pendingAccept();

	}

	private NIOProcessor nextProcessor() {
		int inx = ++nextProcessor;
		if (inx >= processors.length) {
			nextProcessor = 0;
			inx = 0;
		}
		return processors[inx];
	}

	private static void closeChannel(AsynchronousSocketChannel channel) {
		if (channel == null) {
			return;
		}
		try {
			channel.close();
		} catch (IOException e) {
		}
	}

	/**
	 * 前端连接ID生成器
	 */
	private static class AcceptIdGenerator {

		private static final long MAX_VALUE = 0xffffffffL;

		private AtomicLong acceptId = new AtomicLong();
		private final Object lock = new Object();

		private long getId() {
			long newValue = acceptId.getAndIncrement();
			if (newValue >= MAX_VALUE) {
				synchronized (lock) {
					newValue = acceptId.getAndIncrement();
					if (newValue >= MAX_VALUE) {
						acceptId.set(0);
					}
				}
				return acceptId.getAndDecrement();
			} else {
				return newValue;
			}
		}
	}
}
