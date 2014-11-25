package org.aliece.mine.net;

import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;

import org.aliece.mine.utils.CharsetUtil;
import org.apache.log4j.Logger;

public abstract class Connection extends AbstractConnection {
	private static final Logger logger = Logger.getLogger(Connection.class);

	protected long id;
	protected String host;
	protected int port;
	protected int localPort;
	protected long idleTimeout;
	protected String charset;
	protected int charsetIndex;
	protected boolean isFinishConnect;
	protected byte[] seed;//密码加密种子

	protected boolean isAccepted;

	public Connection(AsynchronousSocketChannel channel) throws IOException {
		super(channel);
		InetSocketAddress localAddr = (InetSocketAddress) channel
				.getLocalAddress();
		InetSocketAddress remoteAddr = (InetSocketAddress) channel
				.getRemoteAddress();
		this.host = remoteAddr.getHostString();
		this.port = localAddr.getPort();
		this.localPort = remoteAddr.getPort();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getLocalPort() {
		return localPort;
	}

	public void setLocalPort(int localPort) {
		this.localPort = localPort;
	}

	public void setAccepted(boolean isAccepted) {
		this.isAccepted = isAccepted;
	}

	public void setProcessor(NIOProcessor processor) {
		super.setProcessor(processor);
	}

	public byte[] getSeed() {
		return seed;
	}

	public void setSeed(byte[] seed) {
		this.seed = seed;
	}

	public long getIdleTimeout() {
		return idleTimeout;
	}

	public void setIdleTimeout(long idleTimeout) {
		this.idleTimeout = idleTimeout;
	}

	public int getCharsetIndex() {
		return charsetIndex;
	}

	public void setCharsetIndex(int charsetIndex) {
		this.charsetIndex = charsetIndex;
	}

	public boolean isFinishConnect() {
		return isFinishConnect;
	}

	public void setFinishConnect(boolean isFinishConnect) {
		this.isFinishConnect = isFinishConnect;
	}

	public String getCharset() {
		return charset;
	}

	public boolean isAccepted() {
		return isAccepted;
	}

	public boolean setCharset(String charset) {
		int ci = CharsetUtil.getIndex(charset);
		if (ci > 0) {
			this.charset = charset;
			this.charsetIndex = ci;
			return true;
		} else {
			return false;
		}
	}

	protected boolean isConnectionReset(Throwable t) {
		if (t instanceof IOException) {
			String msg = t.getMessage();
			return (msg != null && msg.contains("Connection reset by peer"));
		}
		return false;
	}

	@Override
	public void error(int errCode, Throwable t) {
		if (isClosed()) {
			return;
		}
		// 根据异常类型和信息，选择日志输出级别。
		if (t instanceof EOFException) {
			if (logger.isDebugEnabled()) {
				logger.debug(toString(), t);
			}
		} else if (isConnectionReset(t)) {
			if (logger.isInfoEnabled()) {
				logger.info(toString(), t);
			}
		} else {
			logger.warn(toString(), t);
		}
		String msg = t.getMessage();
		logger.debug(msg);
	}

	@Override
	public void onConnectFailed(Throwable e) {

	}

	public boolean finishConnect() throws IOException {
		localPort = ((InetSocketAddress) channel.getLocalAddress()).getPort();
		isFinishConnect = true;
		return true;
	}

}
