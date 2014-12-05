package org.aliece.mine.MineClient;

import java.io.IOException;
import java.nio.channels.SelectionKey;

public interface NioHandler {
	void execute(SelectionKey key) throws IOException;
}
