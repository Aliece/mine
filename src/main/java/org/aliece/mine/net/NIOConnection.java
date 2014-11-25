package org.aliece.mine.net;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface NIOConnection extends ClosableConnection {
	
	void register() throws IOException;

	void handle(byte[] data);

	void write(ByteBuffer buffer);

	void error(int errCode, Throwable t);
}
