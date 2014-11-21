package client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class AioTcpClient {
	public static JTextField jt = new JTextField();
	public static ConcurrentHashMap<String, AsynchronousSocketChannel> sockets = new ConcurrentHashMap<>();
	static AioTcpClient me;

	private AsynchronousChannelGroup asyncChannelGroup;

	public AioTcpClient() throws Exception {
		// �����̳߳�
		ExecutorService executor = Executors.newFixedThreadPool(20);
		// ��������ͨ��������
		asyncChannelGroup = AsynchronousChannelGroup.withThreadPool(executor);
	}

	private final CharsetDecoder decoder = Charset.forName("GBK").newDecoder();

	public void start(final String ip, final int port) throws Exception {
		// ����20000���������ӣ�ʹ��20���̵߳ĳ���
		for (int i = 0; i < 2323; i++) {
			try {
				// �ͻ���socket.��Ȼ�����첽��ʽ�ġ�
				AsynchronousSocketChannel connector = null;
				if (connector == null || !connector.isOpen()) {
					// ���첽ͨ�����������õ��ͻ���socket
					connector = AsynchronousSocketChannel
							.open(asyncChannelGroup);
					sockets.putIfAbsent(String.valueOf(i), connector);

					connector
							.setOption(StandardSocketOptions.TCP_NODELAY, true);
					connector.setOption(StandardSocketOptions.SO_REUSEADDR,
							true);
					connector.setOption(StandardSocketOptions.SO_KEEPALIVE,
							true);
					// ��ʼ���ӷ�����������ĵ�connectԭ����
					// connect(SocketAddress remote, A attachment,
					// CompletionHandler<Void,? super A> handler)
					// Ҳ��������CompletionHandler ��A�Ͳ�����������ĵ��÷���
					// �ĵڶ�����������������connector���ͻ�����������
					// V��Ϊnull
					connector.connect(new InetSocketAddress(ip, port),
							connector, new AioConnectHandler(i));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void work() throws Exception {
		AioTcpClient client = new AioTcpClient();
		client.start("localhost", 8066);
	}

	public void send() throws UnsupportedEncodingException {
		AsynchronousSocketChannel socket = sockets.get("0");
		String sendString = jt.getText();
		ByteBuffer clientBuffer = ByteBuffer.wrap(sendString.getBytes("UTF-8"));
		socket.write(clientBuffer, clientBuffer, new AioSendHandler(socket));
	}

	public void createPanel() {
		me = this;
		JFrame f = new JFrame("Wallpaper");
		f.getContentPane().setLayout(new BorderLayout());

		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton bt = new JButton("����");
		p.add(bt);
		me = this;
		bt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					me.send();

				} catch (Exception ex) {
					Logger.getLogger(AioTcpClient.class.getName()).log(
							Level.SEVERE, null, ex);
				}
			}

		});

		bt = new JButton("����");
		p.add(bt);
		me = this;
		bt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			}

		});

		f.getContentPane().add(jt, BorderLayout.CENTER);
		f.getContentPane().add(p, BorderLayout.EAST);

		f.setSize(450, 300);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setLocationRelativeTo(null);
		f.setVisible(true);
	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				AioTcpClient d = null;
				try {
					d = new AioTcpClient();
				} catch (Exception ex) {
					Logger.getLogger(AioTcpClient.class.getName()).log(
							Level.SEVERE, null, ex);
				}

				d.createPanel();
				try {
					d.work();
				} catch (Exception ex) {
					Logger.getLogger(AioTcpClient.class.getName()).log(
							Level.SEVERE, null, ex);
				}

			}
		});
	}
}