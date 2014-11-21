package org.aliece.mine.server;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

public class NonBlockingSession implements Session {

	private static final Logger logger = Logger
			.getLogger(NonBlockingSession.class);

	private long id;
	private Object receivedMessage;
	private Date createDate;
	private Map<Object, Object> attributeMap = new ConcurrentHashMap<Object, Object>();
	private String localIp;
	private int localPort;
	private int remotePort;
	private String remoteIp;
	private final ServerConnection source;

	public NonBlockingSession(ServerConnection source) {
		createDate = new Date();
		this.source = source;
		id = UUID.randomUUID().getLeastSignificantBits();
	}

	public Object getAttribute(Object key) {
		return attributeMap.get(key);
	}

	public Date getCreateDate() {
		return createDate;
	}

	public long getId() {
		return id;
	}

	public String getLocalIp() {
		return localIp;
	}

	public int getLocalPort() {
		return localPort;
	}

	public Object getReceiveMessage() {
		return receivedMessage;
	}

	public String getRemoteIp() {
		return remoteIp;
	}

	public int getRemotePort() {
		return remotePort;
	}

	public void setAttribute(Object key, Object value) {
		attributeMap.put(key, value);
	}

	public void wirte(Object buffer) {

	}

	public void setReceiveMessage(Object msg) {
		receivedMessage = msg;
	}

	@Override
	public boolean close() {
		return source.isClosed();
	}

}
