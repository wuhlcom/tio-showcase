package org.tio.examples.im.client.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.examples.im.common.ImPacket;

/**
 *
 * @author tanyaowu
 * 2017年5月14日 上午10:35:57
 */
public class JoinGroupNotifyRespHandler implements ImAioHandlerIntf {
	private static Logger log = LoggerFactory.getLogger(JoinGroupNotifyRespHandler.class);

	/**
	 *
	 */
	public JoinGroupNotifyRespHandler() {

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
		log.info("有人进群了");
		return null;
	}
}
