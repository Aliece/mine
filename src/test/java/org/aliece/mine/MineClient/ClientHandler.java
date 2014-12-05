package org.aliece.mine.MineClient;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class ClientHandler implements NioHandler {

	private Selector selector;
	private SocketChannel sc = null;
	protected FileChannel channel;
	protected ByteBuffer buffer;
	protected ThreadLocal<CharsetDecoder> decoders = new ThreadLocal<CharsetDecoder>();

	protected CharsetDecoder decoder;
	static int BLOCK = 4096;
	protected String filename = "F:\\Desert.jpg"; // a big file

	protected ByteBuffer clientBuffer = ByteBuffer.allocate(BLOCK);

	public ClientHandler(SocketChannel sc, Selector selector)
			throws FileNotFoundException {
		this.sc = sc;
		this.selector = selector;

		this.channel = new FileInputStream(filename).getChannel();

		this.buffer = ByteBuffer.allocate(BLOCK);

		Charset charset = Charset.forName("GB2312");
//		decoder = decoders.get();
//		if (decoder == null) {
			decoder = charset.newDecoder();
//			decoders.set(decoder);
//		}
		NIOServer.connum++;
		System.out.println(NIOServer.connum + " Client:"
				+ sc.socket().getRemoteSocketAddress().toString() + " open");
	}

	public int InitClientHandler() {
		try {
			SelectionKey key = null;
			key = this.sc.register(this.selector, SelectionKey.OP_READ);
			key.attach(this);
		} catch (Exception e) {
			e.printStackTrace();
			return 1;
		}
		return 0;
	}

	public ByteBuffer readBlock() {

		try {

			buffer.clear();

			int count = channel.read(buffer);

			buffer.flip();

			if (count <= 0)

				return null;

		} catch (IOException e) {

			e.printStackTrace();

		}

		return buffer;

	}

	public void close() {

		try {

			channel.close();
			NIOServer.connum--;

		} catch (IOException e) {

			e.printStackTrace();

		}

	}

	@Override
	public void execute(SelectionKey key) throws IOException {
		if (key.isAcceptable()) { // 接收请求

			ServerSocketChannel server = (ServerSocketChannel) key.channel();

			SocketChannel channel = server.accept();
			channel.configureBlocking(false);

			channel.register(selector, SelectionKey.OP_READ);

		} else if (key.isReadable()) { // 读信息

			SocketChannel channel = (SocketChannel) key.channel();

			int count = channel.read(clientBuffer);

			if (count > 0) {

				clientBuffer.flip();

				CharBuffer charBuffer = decoder.decode(clientBuffer);

				System.out.println("Client >>" + charBuffer.toString());

				SelectionKey wKey = channel.register(selector,
						SelectionKey.OP_WRITE);

				wKey.attach(this);

			} else {
				channel.close();
			}

			clientBuffer.clear();

		} else if (key.isWritable()) { // 写事件

			SocketChannel channel = (SocketChannel) key.channel();

			ClientHandler handler = (ClientHandler) key.attachment();

			ByteBuffer block = handler.readBlock();

			if (block != null)

				channel.write(block);
			else {

				handler.close();

				channel.close();

			}

		}
	}

}
