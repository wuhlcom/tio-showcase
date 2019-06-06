package org.tio.examples.im.server.handler;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Tio;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.examples.im.common.ImPacket;
import org.tio.examples.im.common.ImSessionContext;
import org.tio.examples.im.common.http.websocket.WebsocketPacket;
import org.tio.examples.im.common.packets.ChatRespBody;
import org.tio.examples.im.common.packets.ChatType;
import org.tio.examples.im.common.packets.Client;
import org.tio.examples.im.common.packets.Command;
import org.tio.examples.im.common.packets.LoginReqBody;
import org.tio.examples.im.common.packets.LoginRespBody;
import org.tio.examples.im.common.packets.User;
import org.tio.examples.im.service.UserService;
import org.tio.utils.SystemTimer;
import org.tio.utils.lock.SetWithLock;

public class LoginReqHandler implements ImBsHandlerIntf {
	private static Logger log = LoggerFactory.getLogger(LoginReqHandler.class);

	@Override
	public Object handler(ImPacket packet, ChannelContext channelContext) throws Exception {
		if (packet.getBody() == null) {
			Tio.remove(channelContext, "body is null");
			return null;
		}

		ImSessionContext imSessionContext = (ImSessionContext) channelContext.getAttribute();
		String handshakeToken = imSessionContext.getToken();

		LoginReqBody loginReqBody = LoginReqBody.parseFrom(packet.getBody());
		String token = loginReqBody.getToken();
		String loginname = loginReqBody.getLoginname();
		String password = loginReqBody.getPassword();

		User user = null;
		if (!StringUtils.isBlank(handshakeToken)) {
			user = UserService.getUser(handshakeToken);
		}
		if (user == null) {
			if (!StringUtils.isBlank(loginname)) {
				user = UserService.getUser(loginname, password);
			} else if (!StringUtils.isBlank(token)) {
				user = UserService.getUser(token);
			}
		}

		if (user == null) {
			log.info("登录失败, loginname:{}, password:{}", loginname, password);
			Tio.remove(channelContext, "loginname and token is null");
			return null;
		}
		long userid = user.getId();
		GroupContext groupContext = channelContext.groupContext;
		SetWithLock<ChannelContext> bindedChannelContexts = groupContext.users.find(groupContext, userid + "");
		if (bindedChannelContexts != null) {
			ChatRespBody.Builder builder = ChatRespBody.newBuilder();
			builder.setType(ChatType.CHAT_TYPE_PUBLIC);
			builder.setText("<div style='color:#ee3344'>系统检测到你已经开了多个窗口，请友好浏览^_^</div>");
			builder.setFromClient(org.tio.examples.im.service.UserService.sysClient);
			//			builder.setGroup();
			builder.setTime(SystemTimer.currTime);
			ChatRespBody chatRespBody = builder.build();
			WebsocketPacket respPacket1 = new WebsocketPacket(Command.COMMAND_CHAT_RESP, chatRespBody.toByteArray());
			Tio.send(channelContext, respPacket1);
		}

		LoginRespBody.Builder loginRespBodyBuilder = LoginRespBody.newBuilder();

		Tio.bindUser(channelContext, user.getId() + "");

		if (StringUtils.isBlank(token)) {
			token = UserService.newToken();
		}
		imSessionContext.setToken(token);

		Client client = imSessionContext.getClient().toBuilder().setUser(user).build();
		imSessionContext.setClient(client);

		loginRespBodyBuilder.setUser(user);
		loginRespBodyBuilder.setToken(token);

		LoginRespBody loginRespBody = loginRespBodyBuilder.build();
		byte[] bodyByte = loginRespBody.toByteArray();

		WebsocketPacket respPacket = new WebsocketPacket(Command.COMMAND_LOGIN_RESP, bodyByte);
		Tio.send(channelContext, respPacket);
		return null;
	}

}
