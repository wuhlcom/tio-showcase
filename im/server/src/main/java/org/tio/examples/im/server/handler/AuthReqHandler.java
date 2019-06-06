package org.tio.examples.im.server.handler;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Tio;
import org.tio.core.ChannelContext;
import org.tio.examples.im.common.ImPacket;
import org.tio.examples.im.common.ImSessionContext;
import org.tio.examples.im.common.http.websocket.WebsocketPacket;
import org.tio.examples.im.common.packets.Address;
import org.tio.examples.im.common.packets.AuthReqBody;
import org.tio.examples.im.common.packets.AuthRespBody;
import org.tio.examples.im.common.packets.Client;
import org.tio.examples.im.common.packets.Command;
import org.tio.examples.im.common.packets.DeviceType;
import org.tio.examples.im.common.packets.Geolocation;

/**
 *
 *
 * @author tanyaowu
 *
 */
public class AuthReqHandler implements ImBsHandlerIntf {
	private static Logger log = LoggerFactory.getLogger(AuthReqHandler.class);

	private static Logger addresslog = LoggerFactory.getLogger("tio-addressxxxx-trace-log");

	//	private static final byte tokenIndex = 0;

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

	}

	/**
	 *
	 */
	public AuthReqHandler() {

	}

	@Override
	public Object handler(ImPacket packet, ChannelContext channelContext) throws Exception {
		if (packet.getBody() == null) {
			throw new Exception("body is null");
		}

		AuthReqBody authReqBody = AuthReqBody.parseFrom(packet.getBody());
		String token = authReqBody.getToken();
		String deviceId = authReqBody.getDeviceId();
		String deviceInfo = authReqBody.getDeviceInfo();
		Long seq = authReqBody.getSeq();
		DeviceType deviceType = authReqBody.getDeviceType();
		String sign = authReqBody.getSign();

		Geolocation geolocation = authReqBody.getGeolocation();
		Address address = authReqBody.getAddress();

		ImSessionContext imSessionContext = (ImSessionContext) channelContext.getAttribute();
		Client client = imSessionContext.getClient();

		if (geolocation != null) {
			client = client.toBuilder().setGeolocation(geolocation).build();
		}

		if (address != null) {
			client = client.toBuilder().setAddress(address).build();
			addresslog.info(address.getFormattedaddress() + " | " + client.getIp() + " | " + client.getRegion() + " | [" + client.getGeolocation().getLng() + ","
					+ client.getGeolocation().getLat() + "]");
		}

		imSessionContext.setClient(client);

		if (StringUtils.isBlank(deviceId)) {
			Tio.close(channelContext, "did is null");
			return null;
		}

		if (seq == null || seq <= 0) {
			Tio.close(channelContext, "seq is null");
			return null;
		}

		token = token == null ? "" : token;
		deviceInfo = deviceInfo == null ? "" : deviceInfo;

		String data = token + deviceId + deviceInfo + seq + org.tio.examples.im.common.Const.authkey;

		//		try
		//		{
		//			String _sign = Md5.getMD5(data);
		//			if (!_sign.equals(sign))
		//			{
		//				log.error("sign is invalid, {}, actual sign:{},expect sign:{}", channelContext.toString(), sign, _sign);
		//				Tio.close(channelContext, "sign is invalid");
		//				return null;
		//			}
		//		} catch (Exception e)
		//		{
		//			log.error(e.toString(), e);
		//			Tio.close(channelContext, e.getMessage());
		//			return null;
		//		}

		WebsocketPacket imRespPacket = new WebsocketPacket();
		AuthRespBody authRespBody = AuthRespBody.newBuilder().build();
		imRespPacket.setCommand(Command.COMMAND_AUTH_RESP);
		imRespPacket.setBody(authRespBody.toByteArray());
		Tio.send(channelContext, imRespPacket);
		return null;
	}
}
