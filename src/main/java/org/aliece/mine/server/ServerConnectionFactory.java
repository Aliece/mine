package org.aliece.mine.server;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;

import org.aliece.mine.MineServer;
import org.aliece.mine.config.model.SystemConfig;
import org.aliece.mine.net.factory.ConnectionFactory;
import org.aliece.mine.net.handler.AuthServerHandler;

public class ServerConnectionFactory extends ConnectionFactory {

	@Override
	protected ServerConnection getConnection(AsynchronousSocketChannel channel)
			throws IOException {
		SystemConfig sys = MineServer.getInstance().getConfig().getSystem();
		ServerConnection c = new ServerConnection(channel);
		c.setHandler(new AuthServerHandler(c));
		c.setTxIsolation(sys.getTxIsolation());
		c.setSession2(new NonBlockingSession(c));
		return c;
	}

}
