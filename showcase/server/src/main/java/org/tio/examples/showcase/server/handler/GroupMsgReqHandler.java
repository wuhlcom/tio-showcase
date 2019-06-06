package org.tio.examples.showcase.server.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Tio;
import org.tio.core.ChannelContext;
import org.tio.examples.showcase.common.ShowcasePacket;
import org.tio.examples.showcase.common.Type;
import org.tio.examples.showcase.common.intf.AbsShowcaseBsHandler;
import org.tio.examples.showcase.common.packets.GroupMsgReqBody;
import org.tio.examples.showcase.common.packets.GroupMsgRespBody;
import org.tio.utils.json.Json;

/**
 * @author tanyaowu
 * 2017年3月27日 下午9:51:28
 */
public class GroupMsgReqHandler extends AbsShowcaseBsHandler<GroupMsgReqBody> {
	private static Logger log = LoggerFactory.getLogger(GroupMsgReqHandler.class);

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
	public GroupMsgReqHandler() {
	}

	/**
	 * @return
	 * @author tanyaowu
	 */
	@Override
	public Class<GroupMsgReqBody> bodyClass() {
		return GroupMsgReqBody.class;
	}

	/**
	 * @param packet
	 * @param bsBody
	 * @param channelContext
	 * @return
	 * @throws Exception
	 * @author tanyaowu
	 */
	@Override
	public Object handler(ShowcasePacket packet, GroupMsgReqBody bsBody, ChannelContext channelContext) throws Exception {
		log.info("收到群聊请求消息:{}", Json.toJson(bsBody));
		GroupMsgRespBody groupMsgRespBody = new GroupMsgRespBody();
		groupMsgRespBody.setText(bsBody.getText());
		groupMsgRespBody.setToGroup(bsBody.getToGroup());

		ShowcasePacket respPacket = new ShowcasePacket();
		respPacket.setType(Type.GROUP_MSG_RESP);
		respPacket.setBody(Json.toJson(groupMsgRespBody).getBytes(ShowcasePacket.CHARSET));
		Tio.sendToGroup(channelContext.groupContext, bsBody.getToGroup(), respPacket);

		return null;
	}
}
