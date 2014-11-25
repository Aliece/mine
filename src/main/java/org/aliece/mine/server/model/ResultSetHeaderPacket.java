package org.aliece.mine.server.model;

import java.nio.ByteBuffer;

import org.aliece.mine.utils.ByteBufferUtil;

public class ResultSetHeaderPacket extends DataPacket {
	public int fieldCount;

	@Override
	public void write(ByteBuffer bb) {
		ByteBufferUtil.writeUB3(bb, getLength());
		bb.put(packetNum);
		ByteBufferUtil.writeLength(bb, fieldCount);
	}

	@Override
	public int getLength() {
		return ByteBufferUtil.getLength(fieldCount);
	}

	@Override
	public void read(byte[] data) {

	}
}
