package org.aliece.mine.server.model;

import java.nio.ByteBuffer;

public class ResultsetPacket extends DataPacket {
	public final ResultSetHeaderPacket headerPacket;
	public final FieldPacket fieldPacket;
	public final EOFPacket midEofPacket;
	public final RowDataPacket rowDataPacket;
	public final EOFPacket endEofPacket;

	public ResultsetPacket(ResultSetHeaderPacket headerPacket,
			FieldPacket fieldPacket, EOFPacket midEofPacket,
			RowDataPacket rowDataPacket, EOFPacket endEofPacket) {
		this.headerPacket = headerPacket;
		this.fieldPacket = fieldPacket;
		this.midEofPacket = midEofPacket;
		this.rowDataPacket = rowDataPacket;
		this.endEofPacket = endEofPacket;
	}

	@Override
	public void write(ByteBuffer bb) {
		headerPacket.write(bb);
		fieldPacket.write(bb);
		midEofPacket.write(bb);
		rowDataPacket.write(bb);
		endEofPacket.write(bb);
	}

	@Override
	public int getLength() {
		return 0;
	}

	@Override
	public void read(byte[] data) {

	}
}
