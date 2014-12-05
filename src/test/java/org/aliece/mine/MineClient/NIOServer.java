package org.aliece.mine.MineClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;

public class NIOServer {

	public static int connum = 0;
	static int BLOCK = 4096;
	protected String filename = "F:\\Desert.jpg"; // a big file
//	protected String filename = "F:\\高性能MySQL_第3版（中文）.pdf";
	protected Selector selector;

	public NIOServer(int port) throws IOException {

		selector = this.getSelector(port);
	}

	// 获取Selector

	protected Selector getSelector(int port) throws IOException {

		ServerSocketChannel server = ServerSocketChannel.open();
		Selector sel = Selector.open();
		server.socket().bind(new InetSocketAddress(port));
		server.configureBlocking(false);
		SelectionKey skey = server.register(sel, SelectionKey.OP_ACCEPT);
		skey.attach(new ServerHandler(server, sel));
		System.out.println("等待客户端连接……");
		return sel;

	}

	// 监听端口

	public void listen() {

		try {
			while (!Thread.interrupted()) {
				int n = selector.select();
				if (n == 0) {
					continue;
				}
				Iterator<SelectionKey> it = selector.selectedKeys().iterator();
				while (it.hasNext()) {
					SelectionKey key = it.next();
					it.remove();

					NioHandler handler = (NioHandler) key.attachment();
					handler.execute(key);
				}
			}

		} catch (IOException e) {

			e.printStackTrace();

		}

	}

	public static String ByteBufferToString(ByteBuffer dst) {
		String ret = null;
		if (dst != null) {
			dst.flip();
			byte[] tempb = new byte[dst.limit()];
			dst.get(tempb);
			ret = new String(tempb);
		}
		return ret;
	}

	public static ByteBuffer StringToByteBuffer(String s) {
		ByteBuffer other = null;
		if (s != null) {
			other = ByteBuffer.wrap(s.getBytes());
		}
		return other;
	}

	// 处理事件

	public static void main(String[] args) {

		int port = 12345;

		try {

			NIOServer server = new NIOServer(port);

			System.out.println("Listernint on " + port);

			while (true) {

				server.listen();

			}

		} catch (IOException e) {

			e.printStackTrace();

		}

	}

}