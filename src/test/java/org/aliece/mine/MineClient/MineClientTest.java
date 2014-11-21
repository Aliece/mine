package org.aliece.mine.MineClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;

import org.aliece.mine.MineServer;
import org.aliece.mine.client.AIOConnection;

public class MineClientTest {

	static MineClientTest me;

	private AsynchronousChannelGroup asyncChannelGroup;

	public MineClientTest() {
		asyncChannelGroup = MineServer.getInstance().getNextAsyncChannelGroup();
	}

	public void start(final String ip, final int port) {
		for (int i = 0; i < 2000; i++) {
			try {
				AsynchronousSocketChannel channel = openSocketChannel();

				AIOConnection connection = new AIOConnection(channel);

				// channel.connect(new InetSocketAddress(ip, port), channel,
				// new AioConnectHandler(i));

				channel.connect(new InetSocketAddress(ip, port), connection,
						MineServer.getInstance().getConnector());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void work() throws Exception {
		MineClientTest client = new MineClientTest();
		client.start("localhost", 8066);
	}

	public static void main(String[] args) throws IOException {

		new Thread(new Runnable() {
			@Override
			public void run() {
				MineClientTest client = new MineClientTest();
				try {
					client.work();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

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
