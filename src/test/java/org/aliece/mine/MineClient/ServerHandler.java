package org.aliece.mine.MineClient;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class ServerHandler implements NioHandler {

	private Selector selector;
	private ServerSocketChannel server;

	public ServerHandler(ServerSocketChannel server, Selector selector) {
		this.selector = selector;
		this.server = server;
	}

	@Override
	public void execute(SelectionKey key) throws IOException {
		handleKey(key);
	}

	protected void handleKey(SelectionKey key) throws IOException {
		SocketChannel sc = server.accept();
		ByteBuffer dst = ByteBuffer.allocate(1024);
		int ret = sc.read(dst);  
        if (ret > 0) {
            String msg = NIOServer.ByteBufferToString(dst);
            sc.write(ByteBuffer.wrap(msg.getBytes()));
        } else {  
            System.out.println("client no send!!!");  
            sc.close();  
            return;  
        }  
		sc.configureBlocking(false);
		ClientHandler h = new ClientHandler(sc, selector);
		h.InitClientHandler();
	}
}
