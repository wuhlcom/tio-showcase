package org.tio.examples.im.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Tio;
import org.tio.core.ChannelContext;
import org.tio.core.intf.GroupListener;
import org.tio.examples.im.common.ImSessionContext;
import org.tio.examples.im.common.http.websocket.WebsocketPacket;
import org.tio.examples.im.common.packets.Command;
import org.tio.examples.im.common.packets.ExitGroupNotifyRespBody;

/**
 * @author tanyaowu
 * 2017年5月13日 下午10:38:36
 */
public class ImGroupListener implements GroupListener {
	private static Logger log = LoggerFactory.getLogger(ImGroupListener.class);

	/**
	 * @param args
	 * @author tanyaowu
	 */
	public static void main(String[] args) {

	}

	/**
	 *
	 * @author tanyaowu
	 */
	public ImGroupListener() {
	}

	/**
	 * @param channelContext
	 * @param group
	 * @throws Exception
	 * @author tanyaowu
	 */
	@Override
	public void onAfterBind(ChannelContext channelContext, String group) throws Exception {
	}

	/**
	 * @param channelContext
	 * @param group
	 * @throws Exception
	 * @author tanyaowu
	 */
	@Override
	public void onAfterUnbind(ChannelContext channelContext, String group) throws Exception {
		ImSessionContext imSessionContext = (ImSessionContext) channelContext.getAttribute();
		ExitGroupNotifyRespBody exitGroupNotifyRespBody = ExitGroupNotifyRespBody.newBuilder().setGroup(group).setClient(imSessionContext.getClient()).build();
		WebsocketPacket respPacket2 = new WebsocketPacket(Command.COMMAND_EXIT_GROUP_NOTIFY_RESP, exitGroupNotifyRespBody.toByteArray());
		Tio.sendToGroup(channelContext.groupContext, group, respPacket2);

	}
}
