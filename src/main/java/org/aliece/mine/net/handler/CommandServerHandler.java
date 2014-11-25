package org.aliece.mine.net.handler;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.aliece.mine.server.ServerConnection;
import org.aliece.mine.server.model.DataMessage;
import org.aliece.mine.server.model.EOFPacket;
import org.aliece.mine.server.model.FieldPacket;
import org.aliece.mine.server.model.ResultSetHeaderPacket;
import org.aliece.mine.server.model.ResultsetPacket;
import org.aliece.mine.server.model.RowDataPacket;
import org.apache.log4j.Logger;

public class CommandServerHandler implements NIOHandler {
	private static final Logger logger = Logger
			.getLogger(CommandServerHandler.class);

	private final ServerConnection source;

	public CommandServerHandler(ServerConnection source) {
		this.source = source;
	}

	@Override
	public void handle(byte[] data) {
		DataMessage mm = new DataMessage(data);
		mm.setPosition(5);
		String sql = null;
		try {
			sql = mm.readString(source.getCharset());

			if (sql.equalsIgnoreCase("select @@version_comment limit 1")) {
				// ���ؽ����
				ResultSetHeaderPacket resultSetHeaderPacket = new ResultSetHeaderPacket();
				resultSetHeaderPacket.packetNum = 1;
				resultSetHeaderPacket.fieldCount = 1;

				FieldPacket fieldPacket = new FieldPacket();
				fieldPacket.packetNum = 2;
				fieldPacket.name = "@@version_comment".getBytes();

				EOFPacket midEofPacket = new EOFPacket();
				midEofPacket.packetNum = 3;

				RowDataPacket rowDataPacket = new RowDataPacket();
				rowDataPacket.packetNum = 4;
				rowDataPacket.fieldCount = 1;
				rowDataPacket.fieldValues = new ArrayList<byte[]>();
				rowDataPacket.fieldValues.add("test".getBytes());

				EOFPacket endEofPacket = new EOFPacket();
				endEofPacket.packetNum = 5;

				ResultsetPacket resultsetPacket = new ResultsetPacket(
						resultSetHeaderPacket, fieldPacket, midEofPacket,
						rowDataPacket, endEofPacket);

				ByteBuffer bb = source.allocate();
				resultsetPacket.write(bb);

				source.write(bb);
			} else {
				ResultSetHeaderPacket resultSetHeaderPacket = new ResultSetHeaderPacket();
				resultSetHeaderPacket.packetNum = 1;
				resultSetHeaderPacket.fieldCount = 1;

				FieldPacket fieldPacket = new FieldPacket();
				fieldPacket.packetNum = 2;
				fieldPacket.name = "Hello".getBytes();

				EOFPacket midEofPacket = new EOFPacket();
				midEofPacket.packetNum = 3;

				RowDataPacket rowDataPacket = new RowDataPacket();
				rowDataPacket.packetNum = 4;
				rowDataPacket.fieldCount = 1;
				rowDataPacket.fieldValues = new ArrayList<byte[]>();
				rowDataPacket.fieldValues.add(sql.getBytes());

				EOFPacket endEofPacket = new EOFPacket();
				endEofPacket.packetNum = 5;

				ResultsetPacket resultsetPacket = new ResultsetPacket(
						resultSetHeaderPacket, fieldPacket, midEofPacket,
						rowDataPacket, endEofPacket);

				ByteBuffer bb = source.allocate();
				resultsetPacket.write(bb);

				source.write(bb);
			}
		} catch (Throwable e) {
			logger.error("===error===", e);
		}
	}
}
