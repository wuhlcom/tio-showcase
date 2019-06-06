package org.tio.examples.im.client.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Tio;
import org.tio.core.ChannelContext;
import org.tio.examples.im.common.Const;
import org.tio.examples.im.common.ImPacket;
import org.tio.examples.im.common.ImSessionContext;
import org.tio.examples.im.common.packets.AuthReqBody;
import org.tio.examples.im.common.packets.Command;
import org.tio.examples.im.common.packets.DeviceType;

/**
 *
 * @author tanyaowu
 * 2017年5月9日 上午11:46:24
 */
public class HandshakeRespHandler implements ImAioHandlerIntf {
	private static Logger log = LoggerFactory.getLogger(HandshakeRespHandler.class);

	/**
	 * 构建鉴权包
	 * @return
	 * @throws Exception
	 */
	public static ImPacket createAuthPacket(String did, String token, String info, Long seq) throws Exception {

		AuthReqBody.Builder authReqBodyBuilder = org.tio.examples.im.common.packets.AuthReqBody.newBuilder();
		authReqBodyBuilder.setDeviceId(did);
		authReqBodyBuilder.setSeq(seq);
		authReqBodyBuilder.setDeviceType(DeviceType.DEVICE_TYPE_PC);
		authReqBodyBuilder.setDeviceInfo(info);
		authReqBodyBuilder.setToken(token);

		did = did == null ? "" : did;
		token = token == null ? "" : token;
		info = info == null ? "" : info;
		seq = seq == null ? 0 : seq;

		@SuppressWarnings("unused")
		String data = token + did + info + seq + Const.authkey;
		//		String sign = null;
		//		try
		//		{
		//			sign = Md5.getMD5(data);
		//		} catch (Exception e)
		//		{
		//			log.error(e.getLocalizedMessage(), e);
		//			throw new RuntimeException(e);
		//		}
		//		authReqBodyBuilder.setSign(sign);

		AuthReqBody authReqBody = authReqBodyBuilder.build();

		ImPacket imReqPacket = new ImPacket(Command.COMMAND_AUTH_REQ, authReqBody.toByteArray());
		return imReqPacket;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

	/**
	 *
	 */
	public HandshakeRespHandler() {

	}

	@Override
	public Object handler(ImPacket packet, ChannelContext channelContext) throws Exception {
		ImSessionContext imSessionContext = (ImSessionContext) channelContext.getAttribute();
		imSessionContext.setHandshaked(true);

		String did = "did";
		String token = "token";
		String info = "info";
		Long seq = 1L;
		ImPacket respPacket;
		try {
			respPacket = createAuthPacket(did, token, info, seq);
			Tio.send(channelContext, respPacket);
		} catch (Exception e) {
			log.error(e.toString(), e);
		}

		return null;
	}
}
