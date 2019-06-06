/**
 *
 */
package org.tio.examples.im.client.handler;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.examples.im.client.ui.JFrameMain;
import org.tio.examples.im.common.ImPacket;
import org.tio.examples.im.common.packets.JoinGroupRespBody;
import org.tio.examples.im.common.packets.JoinGroupResult;

/**
 *
 * @author tanyaowu
 * 2017年5月9日 上午11:46:16
 */
public class JoinRespHandler implements ImAioHandlerIntf {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(JoinRespHandler.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

	/**
	 *
	 */
	public JoinRespHandler() {

	}

	//	@Override
	//	public Map<String, Object> onReceived(ImReqPacket packet, ChannelContext<ImClientChannelContextExt> channelContext) throws Exception
	//	{
	//
	//		ImClientChannelContextExt ext = channelContext.getAttribute();
	//		String bodyStr = null;
	//		if (packet.getBody() != null)
	//		{
	//			bodyStr = new String(packet.getBody(), Const.CHARSET_UTF8);
	//		}
	//		JoinGroupRespBody body = Json.toBean(bodyStr, JoinGroupRespBody.class);
	//		if (Objects.equals(JoinGroupResultVo.Code.OK, body.getResult().getCode()))
	//		{
	//			String group = body.getGroup();
	//			//			log.info("join group {}", group);
	//			String xx = channelContext.getId() + "(" + ext.getLoginname() + ")" + "进入组:" + group;
	//			JFrameMain.getInstance().getMsgTextArea().append(xx + System.lineSeparator());
	//			//顺利进入组
	//		} else
	//		{
	//			//被拒绝
	//			//			log.error("refused to join in group {}", body.getGroup());
	//			String xx = channelContext.getId() + "(" + ext.getLoginname() + ")" + "被拒绝进入组" + body.getGroup();
	//			JFrameMain.getInstance().getMsgTextArea().append(xx + System.lineSeparator());
	//		}
	//
	//		return null;
	//	}

	/**
	 * @see org.tio.examples.im.client.handler.ImAioHandlerIntf#handler(org.tio.examples.im.common.ImPacket, org.tio.core.ChannelContext)
	 *
	 * @param packet
	 * @param channelContext
	 * @return
	 * @throws Exception
	 * @author tanyaowu
	 * 2016年12月6日 下午2:25:44
	 *
	 */
	@Override
	public Object handler(ImPacket packet, ChannelContext channelContext) throws Exception {
		if (packet.getBody() == null) {
			throw new Exception("body is null");
		}

		JoinGroupRespBody respBody = JoinGroupRespBody.parseFrom(packet.getBody());

		if (Objects.equals(JoinGroupResult.JOIN_GROUP_RESULT_OK, respBody.getResult())) {

			String group = respBody.getGroup();
			//			channelContext.groupContext.getGroups().bind(group, channelContext);
			org.tio.core.Tio.bindGroup(channelContext, group);
			//			log.info("join group {}", group);
			//			String xx = ClientNodes.getKey(channelContext) + "进入组:" + group;
			//			JFrameMain.getInstance().getMsgTextArea().append(xx + System.lineSeparator());
			//顺利进入组
		} else {
			//被拒绝
			//			log.error("refused to join in group {}", body.getGroup());
			String xx = channelContext + "被拒绝进入组" + respBody.getGroup();
			JFrameMain.getInstance().getMsgTextArea().append(xx + System.lineSeparator());
		}

		return null;
	}
}
