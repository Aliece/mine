package org.aliece.mine.server.model;

import java.nio.ByteBuffer;

import org.aliece.mine.config.ServerCapabilities;

public class AuthPacket extends DataPacket {

	private static final byte[] FILLER = new byte[23];

	public long clientCapabilities;
	public int extClientCapabilities;
	public long maxPacket;
	public byte charset;
	public byte[] extra;
	public String username;
	public byte[] password;
	public String db;
	public String rwsplit;

	@Override
	public void write(ByteBuffer bb) {

	}

	@Override
	public int getLength() {
		return 0;
	}

	@Override
	public void read(byte[] data) {
		DataMessage message = new DataMessage(data);

		packetLen = message.readUB3();
		packetNum = message.read();
		clientCapabilities = message.readUB4();
		maxPacket = message.readUB4();
		charset = message.read();
		// read extra
		int current = message.getPosition();
		int len = (int) message.readLength();
		if (len > 0 && len < FILLER.length) {
			byte[] ab = new byte[len];
			System.arraycopy(message.bytes(), message.getPosition(), ab, 0, len);
			this.extra = ab;
		}
		message.setPosition(current + FILLER.length);
		username = message.readStringWithNull();
		password = message.readBytesWithLength();
		if (((clientCapabilities & ServerCapabilities.CLIENT_CONNECT_WITH_DB) != 0)
				&& message.hasRemaining()) {
			db = message.readStringWithNull();
		}
		rwsplit = message.readStringWithNull();
	}
}
