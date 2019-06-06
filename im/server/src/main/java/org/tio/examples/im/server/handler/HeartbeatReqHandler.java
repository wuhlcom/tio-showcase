package org.tio.examples.im.server.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Tio;
import org.tio.core.ChannelContext;
import org.tio.examples.im.common.ImPacket;
import org.tio.examples.im.common.ImSessionContext;
import org.tio.examples.im.common.http.websocket.WebsocketPacket;
import org.tio.examples.im.common.http.websocket.WebsocketPacket.Opcode;
import org.tio.examples.im.common.packets.Command;

public class HeartbeatReqHandler implements ImBsHandlerIntf {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(HeartbeatReqHandler.class);

	@Override
	public Object handler(ImPacket packet, ChannelContext channelContext) throws Exception {

		ImSessionContext imSessionContext = (ImSessionContext) channelContext.getAttribute();
		if (imSessionContext.isWebsocket()) {
			WebsocketPacket respPacket = new WebsocketPacket(Command.COMMAND_HEARTBEAT_RESP);
			respPacket.setWsOpcode(Opcode.PONG);
			Tio.send(channelContext, respPacket);
		}

		return null;
	}
}
