package org.aliece.mine.server.model;

import java.nio.ByteBuffer;

import org.aliece.mine.utils.ByteBufferUtil;

public class EOFPacket extends DataPacket {
	public static final byte FIELD_COUNT = (byte) 0xfe;

	public byte fieldCount = FIELD_COUNT;
	public int warningCount;
	public int status = 2;

	public void read(byte[] data) {
		DataMessage mm = new DataMessage(data);
		packetLen = mm.readUB3();
		packetNum = mm.read();
		fieldCount = mm.read();
		warningCount = mm.readUB2();
		status = mm.readUB2();
	}

	@Override
	public void write(ByteBuffer buffer) {
		ByteBufferUtil.writeUB3(buffer, getLength());
		buffer.put(packetNum);
		buffer.put(fieldCount);
		ByteBufferUtil.writeUB2(buffer, warningCount);
		ByteBufferUtil.writeUB2(buffer, status);
	}

	@Override
	public int getLength() {
		return 0;
	}
}
