package org.tio.examples.im.common.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.examples.im.common.ImSessionContext;
import org.tio.examples.im.common.http.HttpRequestPacket;
import org.tio.examples.im.common.packets.Client;

/**
 * @author tanyaowu
 * 2017年5月5日 下午5:35:02
 */
public class ImUtils {
	private static Logger log = LoggerFactory.getLogger(ImUtils.class);

	public static String formatRegion(String region) {
		if (StringUtils.isBlank(region)) {
			return "";
		}

		String[] arr = StringUtils.split(region, "|");//.split("|");
		List<String> validArr = new ArrayList<>();
		for (String element : arr) {
			String e = element;
			if (StringUtils.isNotBlank(e) && !"0".equals(e)) {
				validArr.add(e);
			}
		}
		if (validArr.size() == 0) {
			return "";
		} else if (validArr.size() == 1) {
			return validArr.get(0);
		} else {
			return validArr.get(validArr.size() - 2) + validArr.get(validArr.size() - 1);
		}
	}

	public static String formatUserAgent(ChannelContext channelContext) {
		ImSessionContext imSessionContext = (ImSessionContext) channelContext.getAttribute();
		HttpRequestPacket httpHandshakePacket = imSessionContext.getHttpHandshakePacket();

		if (httpHandshakePacket != null) {
			//			UserAgent userAgent = httpHandshakePacket.getUserAgent();

			//			String DeviceName = userAgent.getValue(UserAgent.DEVICE_NAME);//StringUtils.leftPad(userAgent.getValue(UserAgent.DEVICE_NAME), 1);
			//			String DeviceCpu = userAgent.getValue("DeviceCpu"); //StringUtils.leftPad(userAgent.getValue("DeviceCpu"), 1);
			//			String OperatingSystemNameVersion = userAgent.getValue("OperatingSystemNameVersion"); //StringUtils.leftPad(userAgent.getValue("OperatingSystemNameVersion"), 1);
			//			String AgentNameVersion = userAgent.getValue("AgentNameVersion");//StringUtils.leftPad(userAgent.getValue("AgentNameVersion"), 1);
			//			String useragentStr = AgentNameVersion + " " + OperatingSystemNameVersion + " " + DeviceName + " " + DeviceCpu;

			return "";
		} else {
			return "";
		}

	}

	/**
	 * @param args
	 * @author tanyaowu
	 */
	public static void main(String[] args) {
	}

	//	formatRegion(region) {
	//	      var arr = region.split("|");
	//	      var validArr = [];
	//	      for (var i = 0; i < arr.length; i++) {
	//	        var e = arr[i];
	//	        if (e && e != '0') {
	//	          validArr.push(e);
	//	        }
	//	      }
	//	      if (validArr.length == 0) {
	//	        return "";
	//	      } else if (validArr.length == 1) {
	//	        return validArr[0];
	//	      } else {
	//	        return validArr[validArr.length - 2] + validArr[validArr.length - 1];
	//	      }
	//	    }

	/**
	 * 设置Client对象到ImSessionContext中
	 * @param channelContext
	 * @return
	 * @author tanyaowu
	 */
	public static Client setClient(ChannelContext channelContext) {
		ImSessionContext imSessionContext = (ImSessionContext) channelContext.getAttribute();
		Client.Builder clientBuilder = null;
		Client client = imSessionContext.getClient();
		if (client == null) {
			clientBuilder = Client.newBuilder();

			//			clientBuilder.setUser(imSessionContext.getUser());
			clientBuilder.setId(channelContext.getId());
			clientBuilder.setIp(channelContext.getClientNode().getIp());
			clientBuilder.setPort(channelContext.getClientNode().getPort());

			if (imSessionContext.getDataBlock() != null) {
				clientBuilder.setRegion(imSessionContext.getDataBlock().getRegion());
			}

			client = clientBuilder.build();
			imSessionContext.setClient(client);
		}

		return client;
	}
}
