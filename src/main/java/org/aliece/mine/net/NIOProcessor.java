package org.aliece.mine.net;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

import org.aliece.mine.buffer.BufferPool;

public class NIOProcessor {

	private final String name;
	private final BufferPool bufferPool;
	private final ExecutorService executor;
	private final ConcurrentMap<Long, AbstractConnection> connections;
	private long netInBytes;
	private long netOutBytes;

	public NIOProcessor(String name, int bufferPoolSize, int bufferchunk,
			ExecutorService executor) throws IOException {
		this.name = name;
		this.bufferPool = new BufferPool(bufferPoolSize, bufferchunk);
		this.executor = executor;
		this.connections = new ConcurrentHashMap<Long, AbstractConnection>();
	}

	public String getName() {
		return name;
	}

	public BufferPool getBufferPool() {
		return bufferPool;
	}

	public int getWriteQueueSize() {
		int total = 0;

		for (AbstractConnection con : connections.values()) {
			total += con.getWriteQueue().snapshotSize();
		}

		return total;
	}

	public ExecutorService getExecutor() {
		return executor;
	}

	public long getNetInBytes() {
		return netInBytes;
	}

	public void addNetInBytes(long bytes) {
		netInBytes += bytes;
	}

	public long getNetOutBytes() {
		return netOutBytes;
	}

	public void addNetOutBytes(long bytes) {
		netOutBytes += bytes;
	}

	public void addConnection(AbstractConnection c) {
		connections.put(c.getId(), c);
	}

	public ConcurrentMap<Long, AbstractConnection> getConnection() {
		return connections;
	}

	public void checkConnection() {
		connectionCheck();
	}

	/*
	 * check connection
	 */
	private void connectionCheck() {
		Iterator<Entry<Long, AbstractConnection>> it = connections.entrySet()
				.iterator();

		while (it.hasNext()) {
			AbstractConnection c = it.next().getValue();
			if (c == null) {
				it.remove();
				continue;
			}
			if (c.isClosed()) {
				c.cleanup();
				it.remove();
			} else {
				c.idleCheck();
			}
		}
	}
}
