package org.aliece.mine.buffer;

import java.nio.ByteBuffer;
import java.util.LinkedList;

public final class BufferQueue {
	// private static final Logger LOGGER = Logger.getLogger(BufferQueue.class);
	private ByteBuffer attachment;
	private final int total;
	private final LinkedList<ByteBuffer> items = new LinkedList<ByteBuffer>();

	public BufferQueue(int capacity) {
		this.total = capacity;
	}

	public ByteBuffer attachment() {
		return attachment;
	}

	public void attach(ByteBuffer buffer) {
		this.attachment = buffer;
	}

	/**
	 * used for statics
	 * 
	 * @return
	 */
	public int snapshotSize() {
		return this.items.size();
	}

	public boolean isEmpty() {
		return items.isEmpty();
	}

	/**
	 * 
	 * @param buffer
	 * @throws InterruptedException
	 */
	public void put(ByteBuffer buffer) throws InterruptedException {
		this.items.offer(buffer);

		if (items.size() > total) {
			throw new java.lang.RuntimeException(
					"bufferQueue size exceeded ,maybe sql returned too many records ,cursize:"
							+ items.size());

		}
	}

	public ByteBuffer poll() {
		ByteBuffer buf = items.poll();
		return buf;
	}

}