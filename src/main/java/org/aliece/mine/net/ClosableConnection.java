package org.aliece.mine.net;

public interface ClosableConnection {
	
	void close(String reason);
	
	boolean isClosed();
	
	public void idleCheck();
	
	long getStartupTime();

	String getHost();

	int getPort();

	int getLocalPort();

	long getNetInBytes();

	long getNetOutBytes();

}
