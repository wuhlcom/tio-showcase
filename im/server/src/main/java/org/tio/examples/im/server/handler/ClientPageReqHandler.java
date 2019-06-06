package org.tio.examples.im.server.handler;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.Tio;
import org.tio.examples.im.common.ImPacket;
import org.tio.examples.im.common.ImSessionContext;
import org.tio.examples.im.common.http.websocket.WebsocketPacket;
import org.tio.examples.im.common.packets.ClientPageReqBody;
import org.tio.examples.im.common.packets.ClientPageRespBody;
import org.tio.examples.im.common.packets.Command;
import org.tio.utils.page.Page;

public class ClientPageReqHandler implements ImBsHandlerIntf {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(ClientPageReqHandler.class);

	@Override
	public Object handler(ImPacket packet, ChannelContext channelContext) throws Exception {
		if (packet.getBody() == null) {
			Tio.remove(channelContext, "body is null");
			return null;
		}

		GroupContext groupContext = channelContext.groupContext;
		//		ImSessionContext imSessionContext = (ImSessionContext)channelContext.getAttribute();

		ClientPageReqBody clientPageReqBody = ClientPageReqBody.parseFrom(packet.getBody());
		int pageIndex = clientPageReqBody.getPageIndex();
		int pageSize = clientPageReqBody.getPageSize();
		String group = clientPageReqBody.getGroup();

		Page<ChannelContext> page = null;

		if (StringUtils.isNotBlank(group)) {
			page = Tio.getPageOfGroup(groupContext, group, pageIndex, pageSize);
		} else {
			page = Tio.getPageOfAll(groupContext, pageIndex, pageSize);
		}

		ClientPageRespBody.Builder clientPageRespBodyBuilder = ClientPageRespBody.newBuilder();
		clientPageRespBodyBuilder.setPageIndex(page.getPageNumber()).setPageSize(page.getPageSize()).setRecordCount(page.getTotalRow());

		List<ChannelContext> pageData = page.getList();
		if (pageData != null) {
			for (ChannelContext ele : pageData) {
				clientPageRespBodyBuilder.addClients(((ImSessionContext) ele.getAttribute()).getClient());
			}
		}

		ClientPageRespBody clientPageRespBody = clientPageRespBodyBuilder.build();
		ImPacket respPacket = new WebsocketPacket(Command.COMMAND_CLIENT_PAGE_RESP, clientPageRespBody.toByteArray());
		Tio.send(channelContext, respPacket);
		Tio.send(channelContext, respPacket);
		return null;
	}

}
