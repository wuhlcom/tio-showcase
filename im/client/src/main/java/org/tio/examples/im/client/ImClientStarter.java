package org.tio.examples.im.client;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.client.TioClient;
import org.tio.client.ClientGroupContext;
import org.tio.client.ReconnConf;
import org.tio.client.intf.ClientAioHandler;
import org.tio.client.intf.ClientAioListener;
import org.tio.examples.im.common.ImTioUuid;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 *
 * @author tanyaowu
 *
 */
public class ImClientStarter {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(ImClientStarter.class);

	private static ReconnConf reconnConf = new ReconnConf(5000L);

	/**
	 * @param args
	 *
	 * @author tanyaowu
	 * @throws IOException
	 * 2016年11月17日 下午5:59:24
	 *
	 */
	public static void main(String[] args) throws Exception {
		org.tio.examples.im.client.ui.JFrameMain.main(args);
	}

	private ClientAioHandler tioClientHandler = new ImClientAioHandler();
	private ClientAioListener aioListener = new ImClientAioListener();
	private ClientGroupContext clientGroupContext = new ClientGroupContext(tioClientHandler, aioListener, reconnConf);
	private TioClient tioClient = null;

	/**
	 *
	 *
	 * @author tanyaowu
	 * @throws IOException
	 * 2016年11月17日 下午5:59:24
	 *
	 */
	public ImClientStarter() throws IOException {

		//设置uuid
		Config conf = ConfigFactory.load("app.conf");
		long workerId = conf.getLong("uuid.workerid");
		long datacenterId = conf.getLong("uuid.datacenter");
		ImTioUuid imTioUuid = new ImTioUuid(workerId, datacenterId);
		clientGroupContext.setTioUuid(imTioUuid);
		clientGroupContext.setReadBufferSize(50 * 1024);

		tioClient = new TioClient(clientGroupContext);
	}

	/**
	 * @return the tioClient
	 */
	public TioClient getTioClient() {
		return tioClient;
	}

	/**
	 * @return the clientGroupContext
	 */
	public ClientGroupContext getClientGroupContext() {
		return clientGroupContext;
	}

}
