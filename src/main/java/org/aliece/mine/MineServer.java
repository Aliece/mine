package org.aliece.mine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.AsynchronousChannelGroup;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import org.aliece.mine.config.MineConfig;
import org.aliece.mine.config.model.SystemConfig;
import org.aliece.mine.net.NIOAcceptor;
import org.aliece.mine.net.NIOConnector;
import org.aliece.mine.net.NIOProcessor;
import org.aliece.mine.server.ServerConnectionFactory;
import org.aliece.mine.utils.ExecutorUtil;
import org.aliece.mine.utils.NameableExecutor;
import org.aliece.mine.utils.TimeUtil;
import org.apache.log4j.Logger;

public class MineServer {

	public static final String NAME = "Mine";
	private static final long LOG_WATCH_DELAY = 60000L;
	private static final long TIME_UPDATE_PERIOD = 20L;
	private static final MineServer INSTANCE = new MineServer();
	private static final Logger LOGGER = Logger.getLogger("Mine");
	private Properties dnIndexProperties;
	private final AsynchronousChannelGroup[] asyncChannelGroups;
	private int channelIndex = 0;

	public static final MineServer getInstance() {
		return INSTANCE;
	}

	private final MineConfig config;
	private final Timer timer;
	private final NameableExecutor aioExecutor;
	private final NameableExecutor timerExecutor;
	private final AtomicBoolean isOnline;
	private final long startupTime;
	private NIOProcessor[] processors;
	private NIOConnector connector;
	private NIOAcceptor server;

	public MineServer() {
		this.config = new MineConfig();
		SystemConfig system = config.getSystem();

		int processorCount = system.getProcessors();
		asyncChannelGroups = new AsynchronousChannelGroup[processorCount];

		// startup processors
		int threadpool = system.getProcessorExecutor();

		try {

			aioExecutor = ExecutorUtil.create("AIOExecutor", threadpool);

			processors = new NIOProcessor[processorCount];
			int processBuferPool = system.getProcessorBufferPool();
			int processBufferChunk = system.getProcessorBufferChunk();

			for (int i = 0; i < processors.length; i++) {
				asyncChannelGroups[i] = AsynchronousChannelGroup
						.withThreadPool(aioExecutor);
				processors[i] = new NIOProcessor("Processor" + i,
						processBuferPool, processBufferChunk, aioExecutor);
			}

			// startup connector
			connector = new NIOConnector();
			connector.setWriteQueueCapcity(system.getFrontWriteQueueSize());
			connector.setProcessors(processors);

			this.timer = new Timer(NAME + "Timer", true);
			this.timerExecutor = ExecutorUtil.create("TimerExecutor",
					system.getTimerExecutor());
			this.isOnline = new AtomicBoolean(true);
			dnIndexProperties = loadDnIndexProps();
			try {
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			this.startupTime = TimeUtil.currentTimeMillis();

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * get next AsynchronousChannel ,first is exclude if multi
	 * AsynchronousChannelGroups
	 * 
	 * @return
	 */
	public AsynchronousChannelGroup getNextAsyncChannelGroup() {
		if (asyncChannelGroups.length == 1) {
			return asyncChannelGroups[0];
		} else {
			int index = (++channelIndex) % asyncChannelGroups.length;
			if (index == 0) {
				++channelIndex;
				return asyncChannelGroups[1];
			} else {
				return asyncChannelGroups[index];
			}

		}
	}

	public MineConfig getConfig() {
		return config;
	}

	public void beforeStart() {
		// String home = SystemConfig.getHomePath();
		// Log4jInitializer.configureAndWatch(home + "/conf/log4j.xml",
		// LOG_WATCH_DELAY);
		Log4jInitializer.configureAndWatch(MineServer.class.getClassLoader().getResource("").getPath() + "/conf/log4j.xml", LOG_WATCH_DELAY);
	}

	public void startup() throws IOException {
		// server startup
		LOGGER.info("===============================================");
		LOGGER.info(NAME + " is ready to startup ...");
		SystemConfig system = config.getSystem();
		String inf = "Startup processors ...,total processors:"
				+ system.getProcessors() + ",aio thread pool size:"
				+ system.getProcessorExecutor()
				+ "    \r\n each process allocated socket buffer pool "
				+ " bytes ,buffer chunk size:"
				+ system.getProcessorBufferChunk()
				+ "  buffer pool's capacity(buferPool/bufferChunk) is:"
				+ system.getProcessorBufferPool()
				/ system.getProcessorBufferChunk();
		LOGGER.info(inf);
		LOGGER.info("sysconfig params:" + system.toString());
		timer.schedule(updateTime(), 0L, TIME_UPDATE_PERIOD);

		timer.schedule(processorCheck(), 0L, system.getProcessorCheckPeriod());

		// startup server
		ServerConnectionFactory sf = new ServerConnectionFactory();
		sf.setWriteQueueCapcity(system.getFrontWriteQueueSize());
		sf.setCharset(system.getCharset());
		sf.setIdleTimeout(system.getIdleTimeout());
		server = new NIOAcceptor(NAME + "Server", system.getBindIp(),
				system.getServerPort(), sf, this.asyncChannelGroups[0]);
		server.setProcessors(processors);
		server.start();
		// server started
		LOGGER.info(server.getName() + " is started and listening on "
				+ server.getPort());
		LOGGER.info("===============================================");
	}

	private Properties loadDnIndexProps() {
		Properties prop = new Properties();
		File file = new File(SystemConfig.getHomePath(), "conf"
				+ File.separator + "dnindex.properties");
		if (!file.exists()) {
			return prop;
		}
		FileInputStream filein = null;
		try {
			filein = new FileInputStream(file);
			prop.load(filein);
		} catch (Exception e) {
			LOGGER.warn("load DataNodeIndex err:" + e);
		} finally {
			if (filein != null) {
				try {
					filein.close();
				} catch (IOException e) {
				}
			}
		}
		return prop;
	}

	/**
	 * save cur datanode index to properties file
	 * 
	 * @param dataNode
	 * @param curIndex
	 */
	public synchronized void saveDataHostIndex(String dataHost, int curIndex) {

		File file = new File(SystemConfig.getHomePath(), "conf"
				+ File.separator + "dnindex.properties");
		FileOutputStream fileOut = null;
		try {
			String oldIndex = dnIndexProperties.getProperty(dataHost);
			String newIndex = String.valueOf(curIndex);
			if (newIndex.equals(oldIndex)) {
				return;
			}
			dnIndexProperties.setProperty(dataHost, newIndex);
			LOGGER.info("save DataHost index  " + dataHost + " cur index "
					+ curIndex);

			File parent = file.getParentFile();
			if (parent != null && !parent.exists()) {
				parent.mkdirs();
			}

			fileOut = new FileOutputStream(file);
			dnIndexProperties.store(fileOut, "update");
		} catch (Exception e) {
			LOGGER.warn("saveDataNodeIndex err:", e);
		} finally {
			if (fileOut != null) {
				try {
					fileOut.close();
				} catch (IOException e) {
				}
			}
		}

	}

	public NIOProcessor[] getProcessors() {
		return processors;
	}

	public NIOConnector getConnector() {
		return connector;
	}

	public NameableExecutor geAIOExecutor() {
		return aioExecutor;
	}

	public NameableExecutor getTimerExecutor() {
		return timerExecutor;
	}

	public long getStartupTime() {
		return startupTime;
	}

	public boolean isOnline() {
		return isOnline.get();
	}

	public void offline() {
		isOnline.set(false);
	}

	public void online() {
		isOnline.set(true);
	}

	// 系统时间定时更新任务
	private TimerTask updateTime() {
		return new TimerTask() {
			@Override
			public void run() {
				TimeUtil.update();
			}
		};
	}

	// 处理器定时检查任务
	private TimerTask processorCheck() {
		return new TimerTask() {
			@Override
			public void run() {
				timerExecutor.execute(new Runnable() {
					@Override
					public void run() {
						for (NIOProcessor p : processors) {
							p.checkConnection();
						}

					}
				});
			}
		};
	}

}
