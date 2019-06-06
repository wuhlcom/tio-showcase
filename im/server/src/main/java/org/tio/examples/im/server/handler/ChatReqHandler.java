package org.tio.examples.im.server.handler;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.Tio;
import org.tio.core.stat.ChannelStat;
import org.tio.examples.im.common.ImPacket;
import org.tio.examples.im.common.ImSessionContext;
import org.tio.examples.im.common.http.websocket.WebsocketPacket;
import org.tio.examples.im.common.packets.ChatReqBody;
import org.tio.examples.im.common.packets.ChatRespBody;
import org.tio.examples.im.common.packets.ChatType;
import org.tio.examples.im.common.packets.Client;
import org.tio.examples.im.common.packets.Command;
import org.tio.examples.im.common.utils.ImUtils;
import org.tio.examples.im.server.ImServerStarter;
import org.tio.examples.im.service.BadWordService;
import org.tio.examples.im.service.IdiomService;
import org.tio.examples.im.service.ImgFjService;
import org.tio.examples.im.service.ImgMnService;
import org.tio.examples.im.service.UserService;
import org.tio.examples.im.vo.ChatCommandVo;
import org.tio.examples.im.vo.IdiomVo;
import org.tio.server.ServerChannelContext;
import org.tio.server.ServerGroupContext;
import org.tio.server.ServerGroupStat;
import org.tio.utils.SystemTimer;
import org.tio.utils.json.Json;

import com.typesafe.config.ConfigFactory;

import cn.hutool.core.io.FileUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 *
 *
 * @author tanyaowu
 *
 */
public class ChatReqHandler implements ImBsHandlerIntf {
	public static class ChatCommand {
		/**
		 * 显示一张图片的命令
		 */
		public static final String SHOW_MM_IMG = CHAT_COMMAND_PREFIX + "mm";

		/**
		 * 批量查看图片的命令
		 */
		public static final String SHOW_BATCH_MM_IMG = CHAT_COMMAND_PREFIX + "mm-10";
		/**
		 * 显示一张风景图片的命令
		 */
		public static final String SHOW_FJ_IMG = CHAT_COMMAND_PREFIX + "fj";

		/**
		 * 显示tio码云地址的命令
		 */
		public static final String SHOW_TIO_IN_MAYUN_IMG = CHAT_COMMAND_PREFIX + "tio";

		/**
		 * 获取源代码
		 */
		public static final String GET_IM_CODE = CHAT_COMMAND_PREFIX + "get-im-code";

		/**
		 * 获取统计信息
		 */
		public static final String STAT = CHAT_COMMAND_PREFIX + "stat";

		/**
		 * 刷新页面
		 */
		public static final String REFRESH = CHAT_COMMAND_PREFIX + "refresh";

		/**
		 * 五方会议的命令
		 */
		public static final String SHOW_WFHT_IMG = "五方会谈";

		/**
		 * 来电话了的命令
		 */
		public static final String SHOW_LDHL_IMG = "来电话了";
	}

	private static Logger log = LoggerFactory.getLogger(ChatReqHandler.class);

	private static Logger chatlog = LoggerFactory.getLogger("tio-chatxxxx-trace-log");

	/**
	 * key : groupid
	 * value : 这个组的聊天消息
	 *
	 */
	//	private static Cache<String, Cache<String, List<ImPacket>>> groupChatCache = CacheUtil.newFIFOCache(500, 1000 * 60 * 60 * 24);
	//
	//	/**
	//	 * key : userid
	//	 * value : 发给该用户的消息
	//	 */
	//	private static Cache<String, Cache<String, List<ImPacket>>> userChatCache = CacheUtil.newFIFOCache(500, 1000 * 60 * 60 * 24);

	private static final String CHAT_COMMAND_PREFIX = "c:"; //聊天命令前缀

	/*
	 * 敏感词的替换词
	 */
	public static final String replaceText = "<span style='color:#ee3344;padding:4px;border:1px solid #ee3344;border-radius:5px;margin:4px 4px;'><a href='http://www.gov.cn' target='_blank'>此处为敏感词</a></span>";

	/**
	 * 处理聊天中的指令
	 * @param initText
	 * @return
	 * @author tanyaowu
	 * @param channelContext
	 */
	public static ChatCommandVo handlerChatCommand(String initText, ChannelContext channelContext) {

		final ServerGroupContext groupContext = (ServerGroupContext) channelContext.groupContext;

		ChatCommandVo chatCommandVo = new ChatCommandVo();
		chatCommandVo.setInitText(initText);
		chatCommandVo.setCommand(true);
		chatCommandVo.setPrivateChat(true);

		if (StringUtils.startsWith(initText, CHAT_COMMAND_PREFIX) || ChatCommand.SHOW_WFHT_IMG.equals(initText) || ChatCommand.SHOW_LDHL_IMG.equals(initText)) {
			initText = StringUtils.trim(initText);
			initText = StringUtils.replaceAll(initText, "\r", "");
			initText = StringUtils.replaceAll(initText, "\n", "");

			if (StringUtils.equals(ChatCommand.SHOW_MM_IMG, initText)) {
				String imgsrc = ImgMnService.nextImg();
				String href = imgsrc;
				String title = "点击查看大图";
				initText = "<a alt='" + title + "' title='" + title + "' href='" + href + "' target='_blank'>" + title + "<br><img style='width:200px;height:100px;' src='" + imgsrc
						+ "'><br>" + title + "</a>";

				chatCommandVo.setAfterText(initText);
				return chatCommandVo;
			} else if (StringUtils.equals(ChatCommand.SHOW_BATCH_MM_IMG, initText)) {
				initText = "";
				for (int i = 0; i < 3; i++) {
					initText += "<div style='padding:4px;border:1px solid #5FB878;border-radius:5px;margin:4px 0px;'>";
					for (int j = 0; j < 3; j++) {
						String imgsrc = ImgMnService.nextImg();
						String href = imgsrc;
						String title = "点击查看大图";
						initText += "<span style='padding:4px;border:1px solid #5FB878;border-radius:5px;margin:4px 0px;'>";
						initText += "<a style='border-radius: 9px; -webkit-border-radius: 9px; -moz-border-radius: 9px;' alt='" + title + "' title='" + title + "' href='" + href
								+ "' target='_blank'><img style='width:200px;height:100px;' src='" + imgsrc + "'></a>";
						initText += "</span>";
					}
					initText += "</div>";
				}
				chatCommandVo.setAfterText(initText);
				return chatCommandVo;
			} else if (StringUtils.equals(ChatCommand.SHOW_FJ_IMG, initText)) {
				String imgsrc = ImgFjService.nextImg();
				String href = imgsrc;
				String title = "点击查看大图";
				initText = "<a alt='" + title + "' title='" + title + "' href='" + href + "' target='_blank'>" + title + "<br><img style='width:200px;height:100px;' src='" + imgsrc
						+ "'><br>" + title + "</a>";
				chatCommandVo.setAfterText(initText);
				return chatCommandVo;
			} else if (StringUtils.equals(ChatCommand.SHOW_TIO_IN_MAYUN_IMG, initText)) {
				String imgsrc = "https://gitee.com/tywo45/t-io/raw/master/docs/api/t-io-api.png";
				String href = "https://gitee.com/tywo45/t-io";
				String title = "点击带你去tio在码云的老巢";
				initText = "<a alt='" + title + "' title='" + title + "' href='" + href + "' target='_blank'>" + title + "<br><img style='width:200px;height:100px;' src='" + imgsrc
						+ "'><br>" + title + "</a>";
				chatCommandVo.setAfterText(initText);
				return chatCommandVo;
			} else if (StringUtils.equals(ChatCommand.SHOW_WFHT_IMG, initText)) {
				String imgsrc = "https://gitee.com/tywo45/t-io/raw/master/docs/dl/1.png";
				String href = "http://www.dtcaas.com";
				String title = "点击带你去看高清无码的五方会谈";
				initText = "<a alt='" + title + "' title='" + title + "' href='" + href + "' target='_blank'>" + title + "<br><img style='width:200px;height:100px;' src='" + imgsrc
						+ "'><br>" + title + "</a>";
				chatCommandVo.setAfterText(initText);
				return chatCommandVo;
			} else if (StringUtils.equals(ChatCommand.SHOW_LDHL_IMG, initText)) {
				String imgsrc = ImgMnService.nextImg();//"http://images.rednet.cn/articleimage/2013/01/23/1403536948.jpg";
				String href = "http://mp.weixin.qq.com/s/RSi8Au0n7UrlebVVYLGFGw";
				String title = "点击带你了解'来电话了'公众号";
				initText = "<a alt='" + title + "' title='" + title + "' href='" + href + "' target='_blank'>" + title + "<br><img style='width:200px;height:100px;' src='" + imgsrc
						+ "'><br>" + title + "</a>";
				chatCommandVo.setAfterText(initText);
				return chatCommandVo;
			} else if (StringUtils.equals(ChatCommand.GET_IM_CODE, initText)) {
				String href = "https://gitee.com/tywo45/t-io";
				initText = "<div style='padding:4px;border:1px solid #077d11;border-radius:5px;margin:4px 0px;'>本站服务器端源代码已经开源，源代码位于'src/example/im'目录，  不过出于保护作者自己，防攻击和百度两个功能点的代码被阉割</div>";
				initText += "<div style='padding:4px;border:1px solid #077d11;border-radius:5px;margin:4px 0px;'><a href='" + href + "' target='_blank'>点击获取</a></div>";
				chatCommandVo.setAfterText(initText);
				return chatCommandVo;
			} else if (StringUtils.equals("c:idiom", initText)) {
				IdiomVo idiomVo = IdiomService.random();
				if (idiomVo != null) {
					initText = idiomVo.getIdiom();
				}
				chatCommandVo.setAfterText(initText);
				return chatCommandVo;
			} else if (StringUtils.equals(ChatCommand.STAT, initText)) {
				ServerChannelContext serverChannelContext = (ServerChannelContext) channelContext;

				ServerGroupStat groupStat = (ServerGroupStat)groupContext.groupStat;
				initText = "";

				//				text += "<div style='padding:4px;border:1px solid #077d11;border-radius:5px;margin:4px 0px;'>";
				//				text += "<div>所有链路汇总（含鉴权、登录、入群、获取用户列表等消息）</div>";
				initText += "<table class='im_table'>";
				initText += "	<caption>";
				initText += "	所有链路汇总（含鉴权、登录、入群、获取用户列表等消息）";
				initText += "	</caption>";

				initText += "	<tr>";
				initText += "		<th>已接收</th>";
				initText += "		<th>已处理</th>";
				initText += "		<th>已发送</th>";
				initText += "		<th>接收TCP请求数</th>";
				initText += "		<th>关闭TCP连接数</th>";
				//				text += "		<th>当前连接成功的TCP连接数</th>";
				initText += "		<th>当前TCP连接数</th>";
				initText += "		<th>被拉黑的IP数</th>";
				initText += "	</tr>";
				initText += "	<tr>";
				initText += "		<td>" + groupStat.receivedPackets + "条<br>" + groupStat.receivedBytes + "字节" + "</td>";
				initText += "		<td>" + groupStat.handledPackets + "条<br>" + groupStat.handledBytes + "字节" + "</td>";
				initText += "		<td>" + groupStat.sentPackets + "条<br>" + groupStat.sentBytes + "字节" + "</td>";
				initText += "		<td>" + groupStat.accepted + "</td>";
				initText += "		<td>" + groupStat.closed + "</td>";
				//				text += "		<td>" + groupContext.connecteds.size() + "</td>";
				initText += "		<td>" + groupContext.connections.size() + "</td>";
				initText += "		<td title='" + Json.toJson(Tio.IpBlacklist.getAll(groupContext)) + "'>" + Tio.IpBlacklist.getAll(groupContext).size() + "</td>";
				initText += "	</tr>";
				initText += "</table>";
				//				text += "</div>";

				ChannelStat channelStat = serverChannelContext.stat;
				initText += "<table class='im_table' style='margin-top:30px;'>";
				initText += "	<caption>";
				initText += "	我的汇总（含鉴权、登录、入群、获取用户列表等消息，另外接收和发送请倒过来理解）";
				initText += "	</caption>";
				initText += "	<tr>";
				initText += "		<th>已接收</th>";
				initText += "		<th>已处理</th>";
				initText += "		<th>已发送</th>";
				initText += "	</tr>";
				initText += "	<tr>";
				initText += "		<td>" + channelStat.receivedPackets + "条<br>" + channelStat.receivedBytes + "字节" + "</td>";
				initText += "		<td>" + channelStat.handledPackets + "条<br>" + channelStat.handledBytes + "字节" + "</td>";
				initText += "		<td>" + channelStat.sentPackets + "条<br>" + channelStat.sentBytes + "字节" + "</td>";
				initText += "	</tr>";
				initText += "</table>";
				chatCommandVo.setAfterText(initText);
				return chatCommandVo;
			} else if (StringUtils.equals(ChatCommand.REFRESH, initText)) {
				initText = "<refresh>";

				chatCommandVo.setCommand(true);
				chatCommandVo.setPrivateChat(false);
				chatCommandVo.setAfterText(initText);
				return chatCommandVo;
			} else if (StringUtils.startsWith(initText, "c:goto:")) {
				String url = StringUtils.substring(initText, "c:goto:".length());
				if ("1".equals(url)) {
					url = "https://gitee.com/tywo45/t-io";
				} else if ("2".equals(url)) {
					url = "https://www.oschina.net/p/t-io";
				}
				initText = "<goto>" + url;

				chatCommandVo.setCommand(true);
				chatCommandVo.setPrivateChat(false);
				chatCommandVo.setAfterText(initText);
				return chatCommandVo;
			} else if (StringUtils.equals(initText, "c:updateconf")) {
				String confpath = FileUtil.getAbsolutePath("classpath:tio-im-server.conf");
				File confFile = new File(confpath);

				ImServerStarter.conf = ConfigFactory.parseFile(confFile);//.load("tio-im-server.conf");
				initText = "配置文件更新成功";

				String fileContent = FileUtil.readUtf8String(confFile);
				fileContent = StringUtils.replaceAll(fileContent, "\r\n", "<br>");
				initText += fileContent;

				chatCommandVo.setCommand(true);
				chatCommandVo.setPrivateChat(true);
				chatCommandVo.setAfterText(initText);
				return chatCommandVo;
			} else if (StringUtils.equals(initText, "c:clearblack")) {
				Tio.IpBlacklist.clear(groupContext);
				initText = "管理员刚刚清空了ip黑名单，被拉黑的朋友可以访问了";

				chatCommandVo.setFromClient(UserService.sysClient);
				chatCommandVo.setCommand(true);
				chatCommandVo.setPrivateChat(false);
				chatCommandVo.setAfterText(initText);
				return chatCommandVo;
			} else if (StringUtils.startsWith(initText, "c:addblack:")) {
				final String ip = StringUtils.substring(initText, "c:addblack:".length());
				final long sleeptime = 2000L;

				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(sleeptime);
						} catch (InterruptedException e) {
							log.error(e.toString(), e);
						}
						Tio.IpBlacklist.add(groupContext, ip);
					}
				}).start();

				initText = "<a target='_blank' href='https://www.baidu.com/s?word=" + ip + "'>" + ip + "</a> 将在" + sleeptime + "毫秒后被拉入了黑名单，请大家文明聊天";

				chatCommandVo.setFromClient(UserService.sysClient);
				chatCommandVo.setCommand(true);
				chatCommandVo.setPrivateChat(false);
				chatCommandVo.setAfterText(initText);
				return chatCommandVo;
			}
		}

		ImSessionContext imSessionContext = (ImSessionContext) channelContext.getAttribute();
		Client currClient = imSessionContext.getClient();

		initText = StringEscapeUtils.escapeHtml4(initText);
		//https://www.baidu.com/s?word=%E5%88%98%E5%BE%B7%E5%8D%8E
		String text1 = BadWordService.replaceBadWord(initText, BadWordService.BaiduBadWordHandler.instance,
				channelContext + " 【" + currClient.getRegion() + "】 【" + currClient.getUser().getNick() + "】");
		if (text1 != null) { //有敏感词
			initText = text1;
		} else {
			//			if (StringUtils.startsWith(initText, "s:")) {
			//				chatCommandVo.setCommand(true);
			//				chatCommandVo.setPrivateChat(false);
			//				String text2 = BaiduService.replaceToSearchHtml(StringUtils.substring(initText, 2));//去百度一把
			//				if (text2 != null) {
			//					initText = text2;
			//					chatCommandVo.setAfterText(initText);
			//					chatCommandVo.setCommand(true);
			//					chatCommandVo.setPrivateChat(false);
			//					return chatCommandVo;
			//				}
			//			}
		}

		chatCommandVo.setAfterText(initText);
		chatCommandVo.setCommand(false);
		chatCommandVo.setPrivateChat(false);
		return chatCommandVo;
	}

	public static void main(String[] args) throws Exception {

		OkHttpClient client = new OkHttpClient();
		for (int j = 0; j < 100; j++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					for (int i = 0; i < 1000000; i++) {

						String url = "http://t-io.org/static/js/main.b391329a.js";
						//		String url = "http://127.0.0.1/static/js/vendor.ee7536976248d8788643.js";
						Request request = new Request.Builder().url(url).build();
						try {
							Response response = client.newCall(request).execute();
							String xxdd = response.body().string();
						} catch (IOException e) {
							log.error(e.toString(), e);
						}
					}
				}
			}).start();
		}

		String url = "http://t-io.org/#/donate";
		//		String url = "http://127.0.0.1/static/js/vendor.ee7536976248d8788643.js";
		Request request = new Request.Builder().url(url).build();

		Response response = client.newCall(request).execute();
		String xxdd = response.body().string();

		//		String xxdd = HttpUtil.get("http://127.0.0.1/static/js/vendor.ee7536976248d8788643.js", "utf-8");
		System.out.println(xxdd);

		//		xxdd = HttpUtil.get("http://112.74.183.177/static/js/vendor.ee7536976248d8788643.js", "utf-8");
		//		System.out.println(xxdd);

		//		String xx;
		//		try {
		//			xx = URLEncoder.encode("<**004*18626888880#", "utf-8");
		//			System.out.println(xx);
		//		} catch (UnsupportedEncodingException e) {
		//			log.error(e.toString(), e);
		//		}
		//
		//		String text = "张内呆地无可奈何地在地枯无可奈何地枯无可奈何地顶替枯";
		//		ChatType chatType = ChatType.CHAT_TYPE_PRIVATE;
		//		String toGroup = "g";
		//
		//		ChatRespBody.Builder builder = ChatRespBody.newBuilder();
		//		builder.setType(chatType);
		//		builder.setText(text);
		//		builder.setGroup(toGroup);
		//		builder.setTime(SystemTimer.currTime);
		//		ChatRespBody chatRespBody = builder.build();
		//		byte[] bodyByte = chatRespBody.toByteArray();

		//
		//		RuntimeSchema<ChatRespBody.Builder> schema = RuntimeSchema.createFrom(ChatRespBody.Builder.class);
		//		ChatRespBody.Builder chatRespBody1 = schema.newMessage();
		//		ProtobufIOUtil.mergeFrom(bodyByte, chatRespBody1, schema);
		//		System.out.println(chatRespBody1);

		//		ProtobufIOUtil.

		//		ProtobufIOUtil
	}

	private static void processIdiom(String text, String group, ChannelContext channelContext, String fromNick) {
		if (text.length() == 4) {
			IdiomVo idiomVo1 = IdiomService.get(text);
			if (idiomVo1 != null) {
				IdiomVo idiomVo = IdiomService.next(text);
				String respText = "";

				respText += "<div style='color:#1496ff;border:1px solid; border-radius:10px;padding:4px;margin:4px 0px;'>";
				respText += "<div>成语解释：<span style='border: solid 1px; padding: 4px; border-radius:10px;' class='animated bounceInLeft'>" + text + "</span></div>";
				respText += "<div>拼音:" + idiomVo1.getPinyin() + "</div>";
				respText += "<div>解释:" + idiomVo1.getParaphrase() + "</div>";
				respText += "</div>";

				respText += "<span style='background-color:#d3eebc;color:#00356a;border:1px solid; border-radius:10px;padding:2px 4px;display:inline-block'>@" + fromNick
						+ "</span>";

				respText += "<div style='color:#1496ff;border:1px solid; border-radius:10px;padding:4px;margin:4px 0px;'>";
				if (idiomVo == null) {
					String end = text.substring(text.length() - 1);
					respText += "<div>成语接龙：<span style='border: solid 1px; padding: 4px; border-radius:10px;' class='animated bounceInLeft'>" + text.substring(0, text.length() - 1)
							+ "<span style='font-size:20px;font-weight:bold'>" + end
							+ "</span></span> --> <span style='color:#ff8000; border: solid 1px; padding: 4px; border-radius:10px;' class='animated bounceInRight'><span style='font-size:20px;font-weight:bold'>"
							+ end + "</span>没找到</span></div>";
				} else {
					respText += "<div>成语接龙：<span style='border: solid 1px; padding: 4px; border-radius:10px;' class='animated bounceInLeft'>" + text.substring(0, text.length() - 1)
							+ "<span style='font-size:20px;font-weight:bold'>" + text.substring(text.length() - 1)
							+ "</span></span> --> <span style='border: solid 1px; padding: 4px; border-radius:10px;' class='animated bounceInRight'><span style='font-size:20px;font-weight:bold'>"
							+ idiomVo.getFirst() + "</span>" + idiomVo.getIdiom().substring(1) + "</span></div>";
					respText += "<div>拼音:" + idiomVo.getPinyin() + "</div>";
					respText += "<div>解释:" + idiomVo.getParaphrase() + "</div>";
				}
				respText += "</div>";

				ChatRespBody.Builder builder = ChatRespBody.newBuilder();
				builder.setType(ChatType.CHAT_TYPE_PUBLIC);
				builder.setText(respText);
				builder.setFromClient(org.tio.examples.im.service.UserService.sysClient);
				builder.setGroup(group);
				builder.setTime(SystemTimer.currTime);
				GroupContext groupContext = channelContext.groupContext;
				builder.setId(groupContext.getTioUuid().uuid());
				ChatRespBody chatRespBody = builder.build();
				WebsocketPacket respPacket1 = new WebsocketPacket(Command.COMMAND_CHAT_RESP, chatRespBody.toByteArray());
				//				Tio.sendToGroup(channelContext.groupContext, group, respPacket1);
				Tio.send(channelContext, respPacket1);

			}

		}

	}

	//	public static String convertCommand(String text) {
	//
	//		return null;
	//	}

	public static void saveChat(ImPacket respPacket) {
		//dd
	}

	@Override
	public Object handler(ImPacket packet, ChannelContext channelContext) throws Exception {

		if (packet.getBody() == null) {
			throw new Exception("body is null");
		}

		ImSessionContext imSessionContext = (ImSessionContext) channelContext.getAttribute();
		Client currClient = imSessionContext.getClient();
		String formatedUserAgent = ImUtils.formatUserAgent(channelContext);

		ChatReqBody chatReqBody = ChatReqBody.parseFrom(packet.getBody());

		if (chatReqBody != null) {
			String toId = chatReqBody.getToId();
			String text = StringUtils.trim(chatReqBody.getText());
			if (StringUtils.isBlank(text)) {
				return null;
			}

			String region = StringUtils.rightPad(imSessionContext.getDataBlock().getRegion(), 10);
			String clientNodeStr = StringUtils.rightPad(channelContext.getClientNode().toString(), 21);
			String userId = StringUtils.rightPad(currClient.getUser().getId() + "", 11);
			String userNick = StringUtils.rightPad(currClient.getUser().getNick(), 8);
			String formattedaddress = currClient.getAddress().getFormattedaddress();
			chatlog.info("{} {} {} {} {} {} \r\n【{}】\r\n", region, formattedaddress, clientNodeStr, userId, userNick, formatedUserAgent, text);

			//指令的逻辑需要整理一下，一个一个加上来的，还没整理
			ChatCommandVo chatCommandVo = handlerChatCommand(text, channelContext);

			String toGroup = chatReqBody.getGroup();

			Client toClient = null;
			ChannelContext toChannelContext = Tio.getChannelContextById(channelContext.groupContext, toId);
			if (toChannelContext != null) {
				toClient = ((ImSessionContext) toChannelContext.getAttribute()).getClient();
			}

			ChatRespBody.Builder builder = ChatRespBody.newBuilder();
			builder.setType(chatReqBody.getType());
			builder.setText(chatCommandVo.getAfterText());

			if (chatCommandVo.getFromClient() != null) {
				builder.setFromClient(chatCommandVo.getFromClient());
			} else {
				builder.setFromClient(currClient);
			}

			if (toClient != null) {
				builder.setToClient(toClient);
			}

			builder.setGroup(toGroup);
			builder.setTime(SystemTimer.currTime);
			GroupContext groupContext = channelContext.groupContext;
			builder.setId(groupContext.getTioUuid().uuid());
			ChatRespBody chatRespBody = builder.build();
			byte[] bodyByte = chatRespBody.toByteArray();

			WebsocketPacket respPacket = new WebsocketPacket();
			respPacket.setCommand(Command.COMMAND_CHAT_RESP);

			respPacket.setBody(bodyByte);

			//公聊则发往群里
			if (Objects.equals(ChatType.CHAT_TYPE_PUBLIC, chatReqBody.getType())) {
				if (chatCommandVo.isPrivateChat()) {
					Tio.send(channelContext, respPacket);
				} else {
					Tio.sendToGroup(channelContext.groupContext, toGroup, respPacket);
				}

				//发送成语
				processIdiom(chatCommandVo.getAfterText(), toGroup, channelContext, currClient.getUser().getNick());
				//				}

			} else if (Objects.equals(ChatType.CHAT_TYPE_PRIVATE, chatReqBody.getType())) {
				if (toClient != null) {
					Tio.sendToId(channelContext.groupContext, toId + "", respPacket);
				} else {
					log.info("用户不在线,channel id:{}", toId);
				}
			}
		}
		return null;
	}
}
