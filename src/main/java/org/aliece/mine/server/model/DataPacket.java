package org.aliece.mine.server.model;

import java.nio.ByteBuffer;
import java.util.Date;

public abstract class DataPacket {
	public static byte NULL_BYTE = 0x00;

	public int packetLen;
	public byte packetNum;

	private long id;
	private String content;
	private Date sndTime;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getSndTime() {
		return sndTime;
	}

	public void setSndTime(Date sndTime) {
		this.sndTime = sndTime;
	}

	public abstract void write(ByteBuffer bb);

	public abstract int getLength();

	public abstract void read(byte[] data);
}
