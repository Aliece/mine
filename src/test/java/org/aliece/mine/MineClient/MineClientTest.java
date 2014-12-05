package org.aliece.mine.MineClient;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Random;
import java.util.StringTokenizer;

import org.aliece.mine.MineServer;
import org.aliece.mine.buffer.BufferQueue;
import org.aliece.mine.net.NIOProcessor;
import org.aliece.mine.server.ServerConnection;

public class MineClientTest {

	static MineClientTest me;

	private AsynchronousChannelGroup asyncChannelGroup;
	private NIOProcessor[] processors;

	public MineClientTest() {
		asyncChannelGroup = MineServer.getInstance().getNextAsyncChannelGroup();
		processors = MineServer.getInstance().getProcessors();
	}

	public void connect() throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		String url = "jdbc:mysql://localhost:3306/dbtest";
		String user = "test";
		String password = "test";
		Connection conn = DriverManager.getConnection(url, user, password);
		if (!conn.isClosed()) {
			System.out.println("success");
		}
		conn.close();
	}

	public void start(final String ip, final int port) {
		try {
			AsynchronousSocketChannel channel = openSocketChannel();

			ServerConnection connection = new ServerConnection(channel);

			// AIOConnection connection = new AIOConnection(channel);
			connection.setWriteQueue(new BufferQueue(1024));
			connection.setProcessor(processors[0]);

			// channel.connect(new InetSocketAddress(ip, port), channel,
			// new AioConnectHandler(i));

			channel.connect(new InetSocketAddress(ip, port), connection,
					MineServer.getInstance().getConnector());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void work() throws Exception {
		MineClientTest client = new MineClientTest();
		client.start("localhost", 8066);
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		String s = "sda.sd.sd";
		System.out.println(s.indexOf("."));
		StringTokenizer st = new StringTokenizer(s, ".");
		System.out.println(st.nextToken());
		System.out.println(st.nextToken());

		// char x = '0';
		// int i = 0;
		// System.out.println(true ? x : 0);// X
		// System.out.println(false ? i : x);// 88

		char ch = '0';
		ch *= 1.1;
		System.out.println(ch); // prints '4'

		Class<?> clazz = Class.forName("java.lang.Integer$IntegerCache");
		Field field = clazz.getDeclaredField("cache");
		field.setAccessible(true);
		Integer[] cache = (Integer[]) field.get(clazz);

		// Rewrite the Integer cache
		for (int i = 0; i < cache.length; i++) {
			cache[i] = new Integer(new Random().nextInt(cache.length));
		}

		for (int i = 0; i < 10; i++) {
			System.out.println((Integer) i);
		}

		// for (int i = 0; i < 10; i++) {
		// new Thread(new Runnable() {
		// @Override
		// public void run() {
		// MineClientTest client = new MineClientTest();
		// try {
		// client.connect();
		// // client.work();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// }).start();
		// }
	}

	protected AsynchronousSocketChannel openSocketChannel() throws IOException {
		AsynchronousSocketChannel channel = AsynchronousSocketChannel
				.open(asyncChannelGroup);
		channel.setOption(StandardSocketOptions.TCP_NODELAY, true);
		channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
		channel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

		return channel;
	}

}
