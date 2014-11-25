package org.aliece.mine.utils;

import java.nio.ByteBuffer;

import org.aliece.mine.server.model.DataPacket;

public class ByteBufferUtil {
	public static void writeUB2(ByteBuffer bb, int val) {
		bb.put((byte) val);
		bb.put((byte) (val >>> 8));
	}

	public static void writeUB3(ByteBuffer bb, int val) {
		bb.put((byte) val);
		bb.put((byte) (val >>> 8));
		bb.put((byte) (val >>> 16));
	}

	public static void writeUB4(ByteBuffer bb, long val) {
		bb.put((byte) val);
		bb.put((byte) (val >>> 8));
		bb.put((byte) (val >>> 16));
		bb.put((byte) (val >>> 24));
	}

	public static void writeLength(ByteBuffer bb, long val) {
		if (val < 251) {
			bb.put((byte) val);
		} else if (val < 0x10000L) {
			bb.put((byte) 252);
			writeUB2(bb, (int) val);
		} else if (val < 0x1000000L) {
			bb.put((byte) 253);
			writeUB3(bb, (int) val);
		} else {
			bb.put((byte) 254);
			writeLong(bb, val);
		}
	}

	public static void writeLong(ByteBuffer bb, long val) {
		bb.put((byte) (val & 0xff));
		bb.put((byte) (val >>> 8));
		bb.put((byte) (val >>> 16));
		bb.put((byte) (val >>> 24));
		bb.put((byte) (val >>> 32));
		bb.put((byte) (val >>> 40));
		bb.put((byte) (val >>> 48));
		bb.put((byte) (val >>> 56));
	}

	public static final void writeWithLength(ByteBuffer buffer, byte[] val) {
		if (val == null) {
			buffer.put(DataPacket.NULL_BYTE);
		} else {
			int length = val.length;
			if (length < 251) {
				buffer.put((byte) length);
			} else if (length < 0x10000L) {
				buffer.put((byte) 252);
				writeUB2(buffer, length);
			} else if (length < 0x1000000L) {
				buffer.put((byte) 253);
				writeUB3(buffer, length);
			} else {
				buffer.put((byte) 254);
				writeLong(buffer, length);
			}
			buffer.put(val);
		}
	}

	public static int getLength(long val) {
		if (val < 251) {
			return 1;
		} else if (val < 0x10000L) {
			return 3;
		} else if (val < 0x1000000L) {
			return 4;
		} else {
			return 9;
		}
	}

	public static final int getLength(byte[] val) {
		int length = val.length;
		if (length < 251) {
			return 1 + val.length;
		} else if (length < 0x10000L) {
			return 3 + val.length;
		} else if (length < 0x1000000L) {
			return 4 + val.length;
		} else {
			return 9 + val.length;
		}
	}
}
