package org.aliece.mine.server.model;

import java.nio.ByteBuffer;

import org.aliece.mine.utils.ByteBufferUtil;

public class FieldPacket extends DataPacket {
	private static final byte[] DEFAULT_CATALOG = "def".getBytes();
	private static final byte[] FILLER = new byte[2];

	public byte[] catalog = DEFAULT_CATALOG;
	public byte[] db;
	public byte[] table;
	public byte[] orgTable;
	public byte[] name;
	public byte[] orgName;
	public int charsetIndex;
	public long length;
	public int type;
	public int flags;
	public byte decimals;
	public byte[] definition;

	@Override
	public void write(ByteBuffer bb) {
		ByteBufferUtil.writeUB3(bb, getLength());
		bb.put(packetNum);
		ByteBufferUtil.writeWithLength(bb, catalog);
		ByteBufferUtil.writeWithLength(bb, db);
		ByteBufferUtil.writeWithLength(bb, table);
		ByteBufferUtil.writeWithLength(bb, orgTable);
		ByteBufferUtil.writeWithLength(bb, name);
		ByteBufferUtil.writeWithLength(bb, orgName);
		bb.put((byte) 0x0C);
		ByteBufferUtil.writeUB2(bb, charsetIndex);
		ByteBufferUtil.writeUB4(bb, length);
		bb.put((byte) (type & 0xff));
		ByteBufferUtil.writeUB2(bb, flags);
		bb.put(decimals);
		bb.position(bb.position() + FILLER.length);
		if (definition != null) {
			ByteBufferUtil.writeWithLength(bb, definition);
		}
	}

	@Override
	public int getLength() {
		int size = (catalog == null ? 1 : ByteBufferUtil.getLength(catalog));
		size += (db == null ? 1 : ByteBufferUtil.getLength(db));
		size += (table == null ? 1 : ByteBufferUtil.getLength(table));
		size += (orgTable == null ? 1 : ByteBufferUtil.getLength(orgTable));
		size += (name == null ? 1 : ByteBufferUtil.getLength(name));
		size += (orgName == null ? 1 : ByteBufferUtil.getLength(orgName));
		size += 13;// 1+2+4+1+2+1+2
		if (definition != null) {
			size += ByteBufferUtil.getLength(definition);
		}
		return size;
	}

	@Override
	public void read(byte[] data) {

	}
}
