package org.aliece.mine.server.model;

import java.nio.ByteBuffer;
import java.util.List;

import org.aliece.mine.utils.ByteBufferUtil;

public class RowDataPacket extends DataPacket {
	private static final byte NULL_MARK = (byte) 251;
	public int fieldCount;
	public List<byte[]> fieldValues;

	@Override
	public void write(ByteBuffer bb) {
		ByteBufferUtil.writeUB3(bb, getLength());
		bb.put(packetNum);
		for (int i = 0; i < fieldCount; i++) {
			byte[] fv = fieldValues.get(i);
			if (fv == null || fv.length == 0) {
				bb.put(RowDataPacket.NULL_MARK);
			} else {
				ByteBufferUtil.writeLength(bb, fv.length);
				bb.put(fv);
			}
		}
	}

	@Override
	public int getLength() {
		int size = 0;
		for (int i = 0; i < fieldCount; i++) {
			byte[] v = fieldValues.get(i);
			size += (v == null || v.length == 0) ? 1 : ByteBufferUtil
					.getLength(v);
		}
		return size;
	}

	@Override
	public void read(byte[] data) {

	}
}
