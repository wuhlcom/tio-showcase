/**
 *
 */
package org.tio.examples.im.client.handler;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Tio;
import org.tio.core.ChannelContext;
import org.tio.examples.im.common.ImPacket;
import org.tio.examples.im.common.packets.Command;
import org.tio.examples.im.common.packets.LoginReqBody;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 *
 * @author tanyaowu
 * 2017年5月9日 上午11:46:31
 */
public class AuthRespHandler implements ImAioHandlerIntf {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(AuthRespHandler.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Config conf = ConfigFactory.load("app.conf");
		int bar1 = conf.getInt("client.count");
		//		Config foo = conf.getConfig("foo");
		//		int bar2 = foo.getInt("bar");

		System.out.println(bar1);
	}

	/**
	 *
	 */
	public AuthRespHandler() {

	}

	/**
	 *
	 * @param packet
	 * @param channelContext
	 * @return
	 * @throws Exception
	 * @author tanyaowu
	 */
	@Override
	public Object handler(ImPacket packet, ChannelContext channelContext) throws Exception {
		String loginname = RandomStringUtils.randomAlphabetic(5, 10);
		String password = "123456";
		LoginReqBody reqBody = LoginReqBody.newBuilder().setLoginname(loginname).setPassword(password).build();
		byte[] body = reqBody.toByteArray();

		ImPacket respPacket = new ImPacket(Command.COMMAND_LOGIN_REQ, body);
		Tio.send(channelContext, respPacket);
		return null;
	}
}
