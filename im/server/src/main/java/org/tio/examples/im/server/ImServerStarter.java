package org.tio.examples.im.server;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.examples.im.common.Const;
import org.tio.examples.im.common.ImTioUuid;
import org.tio.examples.im.server.utils.Threads;
import org.tio.examples.im.service.BadWordService;
import org.tio.examples.im.service.ImgFjService;
import org.tio.examples.im.service.ImgMnService;
import org.tio.examples.im.service.ImgTxService;
import org.tio.server.ServerGroupContext;
import org.tio.server.TioServer;
import org.tio.server.intf.ServerAioHandler;
import org.tio.server.intf.ServerAioListener;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 *
 * @author tanyaowu
 *
 */
public class ImServerStarter {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(ImServerStarter.class);

	public static Config conf = ConfigFactory.load("tio-im-server.conf");

	static ServerAioHandler aioHandler = new ImServerAioHandler();

	static ServerAioListener aioListener = new ImServerAioListener();
	static ImGroupListener imGroupListener = new ImGroupListener();
	static ServerGroupContext serverGroupContext = new ServerGroupContext(aioHandler, aioListener, Threads.tioExecutor, Threads.groupExecutor);
	static TioServer tioServer = new TioServer(serverGroupContext);
	static String bindIp = null;//"127.0.0.1";

	/**
	 * @param args
	 *
	 * @author tanyaowu
	 * @throws IOException
	 * 2016年11月17日 下午5:59:24
	 *
	 */
	public static void main(String[] args) throws Exception {
		if (conf.getBoolean("start.img.capture")) {
			ImgFjService.start();
			ImgMnService.start();
			ImgTxService.start();
		}

		//		if (conf.getBoolean("start.userAgent")) {
		//			//这货初始化比较慢，所以启动前就调用一下
		//			UserAgentAnalyzerFactory.getUserAgentAnalyzer();
		//		} else {
		//			new Thread(new Runnable() {
		//				@Override
		//				public void run() {
		//					UserAgentAnalyzerFactory.getUserAgentAnalyzer();
		//				}
		//			}).start();
		//		}

		BadWordService.init();

		long workerId = conf.getLong("uuid.workerid");
		long datacenterId = conf.getLong("uuid.datacenter");
		ImTioUuid imTioUuid = new ImTioUuid(workerId, datacenterId);
		serverGroupContext.setTioUuid(imTioUuid);

		serverGroupContext.setGroupListener(imGroupListener);
		serverGroupContext.setReadBufferSize(1024 * 50);
//		serverGroupContext.setPacketHandlerMode(PacketHandlerMode.QUEUE);
		
//		String keyStoreFile = "classpath:config/ssl/keystore.jks";
//		String trustStoreFile = "classpath:config/ssl/keystore.jks";
//		String keyStorePwd = "214323428310224";
//		serverGroupContext.useSsl(keyStoreFile, trustStoreFile, keyStorePwd);
		
		tioServer.start(bindIp, Const.SERVER_PORT);
	}

	/**
	 *
	 *
	 * @author tanyaowu
	 * 2016年11月17日 下午5:59:24
	 *
	 */
	public ImServerStarter() {

	}

}
