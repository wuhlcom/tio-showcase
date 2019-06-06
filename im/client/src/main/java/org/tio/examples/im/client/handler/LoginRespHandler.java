package org.tio.examples.im.client.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Tio;
import org.tio.core.ChannelContext;
import org.tio.examples.im.client.ui.JFrameMain;
import org.tio.examples.im.common.ImPacket;
import org.tio.examples.im.common.ImSessionContext;
import org.tio.examples.im.common.packets.Client;
import org.tio.examples.im.common.packets.Command;
import org.tio.examples.im.common.packets.JoinGroupReqBody;
import org.tio.examples.im.common.packets.LoginRespBody;
import org.tio.examples.im.common.packets.User;

/**
 *
 * @author tanyaowu
 * 2017年5月9日 上午11:45:56
 */
public class LoginRespHandler implements ImAioHandlerIntf {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(LoginRespHandler.class);

	/**
	 *
	 */
	public LoginRespHandler() {

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
		if (packet.getBody() == null) {
			log.info("{} 登录失败了", channelContext);
			return null;
		}

		ImSessionContext imSessionContext = (ImSessionContext) channelContext.getAttribute();

		LoginRespBody loginRespBody = LoginRespBody.parseFrom(packet.getBody());
		User user = loginRespBody.getUser();
		String token = loginRespBody.getToken();

		if (user != null) {
			Tio.bindUser(channelContext, user.getId() + "");

			Client client = imSessionContext.getClient().toBuilder().setUser(user).build();
			imSessionContext.setClient(client);

			imSessionContext.setToken(token);

			log.info("{}登录了", user.getNick());
		} else {
			log.info("{} 登录失败了", channelContext);
		}

		//进入群组
		String group = JFrameMain.getInstance().getGroupField().getText();
		JoinGroupReqBody reqBody = JoinGroupReqBody.newBuilder().setGroup(group).build();
		ImPacket joinGroupPacket = new ImPacket(Command.COMMAND_JOIN_GROUP_REQ, reqBody.toByteArray());
		Tio.send(channelContext, joinGroupPacket);

		return null;
	}
}
