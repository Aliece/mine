package org.aliece.mine.server.model;

import java.nio.ByteBuffer;

import org.aliece.mine.utils.ByteBufferUtil;

/**
 * MySQL握手协议包
 */
public class HandshakePacket extends DataPacket {
	private static final byte[] FILLER_13 = new byte[] { 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0 };

	public byte protocal;// 1
	public byte[] version;// n+1
	public long threadId;// 4
	public byte[] salt;// 8+1
	public int serverCapabilities;// 2
	public byte serverLanguage;// 1
	public int serverStatus;// 2
	public byte[] restOfSalt;// 12+1

	@Override
	public void write(ByteBuffer bb) {
		ByteBufferUtil.writeUB3(bb, getLength());
		bb.put(packetNum);
		bb.put(protocal);
		bb.put(version);
		bb.put(NULL_BYTE);
		ByteBufferUtil.writeUB4(bb, threadId);
		bb.put(salt);
		bb.put(NULL_BYTE);
		ByteBufferUtil.writeUB2(bb, serverCapabilities);
		bb.put(serverLanguage);
		ByteBufferUtil.writeUB2(bb, serverStatus);
		bb.put(FILLER_13);
		bb.put(restOfSalt);
		bb.put(NULL_BYTE);
	}

	@Override
	public int getLength() {
		// int size = 1;
		// size += version.length+1;// n+1
		// size += 4;// 4
		// size += salt.length+1;// 8+1
		// size += 18;// 2+1+2+13
		// size += restOfSalt.length+1;// 12+1
		// return size;
		return 46 + version.length;
	}

	@Override
	public void read(byte[] data) {

	}
}
