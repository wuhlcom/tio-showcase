package org.tio.examples.im.server.handler;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Tio;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.examples.im.common.ImPacket;
import org.tio.examples.im.common.ImSessionContext;
import org.tio.examples.im.common.http.HttpRequestPacket;
import org.tio.examples.im.common.http.websocket.WebsocketPacket;
import org.tio.examples.im.common.packets.ChatRespBody;
import org.tio.examples.im.common.packets.ChatType;
import org.tio.examples.im.common.packets.Client;
import org.tio.examples.im.common.packets.Command;
import org.tio.examples.im.common.packets.JoinGroupNotifyRespBody;
import org.tio.examples.im.common.packets.JoinGroupReqBody;
import org.tio.examples.im.common.packets.JoinGroupRespBody;
import org.tio.examples.im.common.packets.JoinGroupResult;
import org.tio.examples.im.common.utils.ImUtils;
import org.tio.utils.SystemTimer;

/**
 *
 *
 * @author tanyaowu
 *
 */
public class JoinReqHandler implements ImBsHandlerIntf {
	private static Logger log = LoggerFactory.getLogger(JoinReqHandler.class);

	//	public static String[] imgs = new String[] {
	//			//						"http://p2.so.qhmsg.com/t016a799588a1a805b7.jpg"
	//			//						,"http://p1.so.qhimgs1.com/t014a25fa412c88726d.jpg"
	//			//						//章天泽
	//			//						,"http://p0.so.qhmsg.com/bdr/_240_/t015283ba5bcf7de205.jpg"
	//			//						,"http://p2.so.qhimgs1.com/bdr/_240_/t01705d6eaa265f7852.jpg"
	//			//						,"http://p0.so.qhmsg.com/bdr/_240_/t01ac699b2cc8653ee5.jpg"
	//			//						,"http://p2.so.qhmsg.com/bdr/_240_/t010c6e25cc7ba89cb7.jpg"
	//			//						,"http://p0.so.qhmsg.com/bdr/_240_/t01dede1c8875fdd895.jpg"
	//			//						,"http://p0.so.qhmsg.com/bdr/_240_/t014531e2ff627d7f45.jpg"
	//			//						,"http://p2.so.qhimgs1.com/bdr/_240_/t017e0fe253e725752e.jpg"
	//			//						,"http://p1.so.qhimgs1.com/bdr/_240_/t01ef258e81cfdef111.jpg"
	//			//						,"http://p2.so.qhimgs1.com/bdr/_240_/t0101fec1fbe7be779e.png"
	//			//						,"http://p0.so.qhmsg.com/bdr/_240_/t01bdfef08b250e6f9f.jpg"
	//			//
	//			//
	//			//
	//			//						//林依晨
	//			//						,"http://p3.so.qhmsg.com/bdr/_240_/t011cf9fa4cee87e408.jpg"
	//			//						,"http://p3.so.qhimgs1.com/bdr/_240_/t0155271eb256935093.jpg"
	//			//						,"http://p0.so.qhimgs1.com/bdr/_240_/t01d2c66efe4c2ec0b0.jpg"
	//			//						//景甜
	//			//						,"http://p4.so.qhimgs1.com/bdr/_240_/t011ae520c7e55923ea.jpg"
	//			//						,"http://p2.so.qhmsg.com/bdr/_240_/t01a4cd21a3a5badca7.jpg"
	//			//						,"http://p0.so.qhmsg.com/bdr/_240_/t01db5bfeeca64ae04f.jpg"
	//			//						,"http://p4.so.qhmsg.com/bdr/_240_/t0119179904fd21d499.jpg"
	//
	//			//唐艺昕
	//			"http://p4.so.qhimgs1.com/bdr/_240_/t01c883fa022ed309a4.jpg", "http://p4.so.qhimgs1.com/bdr/_240_/t01962fefe662849b5a.jpg",
	//			"http://p4.so.qhimgs1.com/bdr/_240_/t01ee3d8ed4ee60d73a.jpg", "http://p0.so.qhimgs1.com/bdr/_240_/t0114e9bd549f35cc5c.jpg",
	//			"http://p2.so.qhmsg.com/bdr/_240_/t0109da4f85f0e12c97.jpg", "http://p4.so.qhmsg.com/bdr/_240_/t010a0536093d42b969.jpg",
	//			"http://p4.so.qhimgs1.com/bdr/_240_/t01fdb65bb57cb994fa.jpg", "http://p5.so.qhimgs1.com/bdr/_240_/t0179d8ea81a089419b.jpg",
	//			"http://p1.so.qhimgs1.com/bdr/_240_/t0160a1d98168b1c991.jpg", "http://p4.so.qhimgs1.com/bdr/_240_/t01b46d864dff7df59a.jpg",
	//			"http://p0.so.qhimgs1.com/bdr/_240_/t0128c84c151ed704cc.jpg", "http://p1.so.qhmsg.com/bdr/_240_/t0149015239458be2d6.jpg",
	//			"http://p2.so.qhimgs1.com/bdr/_240_/t01f40b589b70444f82.jpg", "http://p0.so.qhmsg.com/bdr/_240_/t012241532f024cb585.jpg",
	//			"http://p4.so.qhimgs1.com/bdr/_240_/t01859834679a1f5fda.jpg", "http://p3.so.qhmsg.com/bdr/_240_/t01c4b9db98c6f91d18.jpg",
	//			"http://p2.so.qhimgs1.com/bdr/_240_/t01106cd4592d9e7a3e.jpg", "http://p1.so.qhmsg.com/bdr/_240_/t01b4c1fd7832d49876.jpg",
	//			"http://p5.so.qhimgs1.com/bdr/_240_/t01da6afaa651507a4b.jpg", "http://p3.so.qhmsg.com/bdr/_240_/t011ff4f173d1f08298.jpg",
	//			"http://p4.so.qhmsg.com/bdr/_240_/t016df7ce4c996c0359.jpg", "http://p5.so.qhimgs1.com/bdr/_240_/t013601d1899e9684cb.jpg",
	//			"http://p4.so.qhmsg.com/bdr/_240_/t0100974e075fe63de9.jpg", "http://p2.so.qhmsg.com/bdr/_240_/t01d879b6f4ee1a5fa7.jpg",
	//			"http://p3.so.qhimgs1.com/bdr/_240_/t01f39f3db828dcf6d3.jpg", "http://p4.so.qhmsg.com/bdr/_240_/t01e74c4c63f4b51959.jpg",
	//			"http://p1.so.qhimgs1.com/bdr/_240_/t01fdeed0b93fec0c9d.png", "http://p0.so.qhimgs1.com/bdr/_240_/t018b04bd0470ff6aa0.jpg",
	//			"http://p2.so.qhimgs1.com/bdr/_240_/t01bd239a83ddd23c7e.jpg", "http://p1.so.qhimgs1.com/bdr/_240_/t01ad50753a372de29d.jpg",
	//			"http://p1.so.qhimgs1.com/bdr/_240_/t0189b7462256934fad.jpg", "http://p0.so.qhmsg.com/bdr/_240_/t01da640ad404f26acf.png"
	//
	//	};

	@Override
	public Object handler(ImPacket packet, ChannelContext channelContext) throws Exception {
		if (packet.getBody() == null) {
			throw new Exception("body is null");
		}

		JoinGroupReqBody reqBody = JoinGroupReqBody.parseFrom(packet.getBody());

		String group = reqBody.getGroup();
		if (StringUtils.isBlank(group)) {
			log.error("group is null,{}", channelContext);
			Tio.close(channelContext, "group is null when join group");
			return null;
		}
		//		GroupContext groupContext = channelContext.groupContext;

		ImSessionContext imSessionContext = (ImSessionContext) channelContext.getAttribute();
		HttpRequestPacket httpHandshakePacket = imSessionContext.getHttpHandshakePacket();
		Tio.bindGroup(channelContext, group);

		//回一条消息，告诉对方进群结果
		JoinGroupResult joinGroupResult = JoinGroupResult.JOIN_GROUP_RESULT_OK;
		JoinGroupRespBody joinRespBody = JoinGroupRespBody.newBuilder().setResult(joinGroupResult).setGroup(group).build();
		WebsocketPacket respPacket = new WebsocketPacket(Command.COMMAND_JOIN_GROUP_RESP, joinRespBody.toByteArray());
		Tio.send(channelContext, respPacket);

		//发进房间通知  COMMAND_JOIN_GROUP_NOTIFY_RESP
		JoinGroupNotifyRespBody joinGroupNotifyRespBody = JoinGroupNotifyRespBody.newBuilder().setGroup(group).setClient(imSessionContext.getClient()).build();
		WebsocketPacket respPacket2 = new WebsocketPacket(Command.COMMAND_JOIN_GROUP_NOTIFY_RESP, joinGroupNotifyRespBody.toByteArray());
		Tio.sendToGroup(channelContext.groupContext, group, respPacket2);
		//		respPacket2.setBody(body);

		//额外再群发一条聊天消息，增加一些人气
		//		String imgsrc = ImgMnService.nextImg();
		//		String text = "<a alt='点击查看大图' href='"+imgsrc+"' target='_blank'>点击查看大图<br><img src='" + imgsrc + "'><br>点击查看大图</a>";

		Client currClient = imSessionContext.getClient();
		String nick = currClient.getUser().getNick();
		String region = imSessionContext.getDataBlock().getRegion();

		String formatedUserAgent = ImUtils.formatUserAgent(channelContext);

		//		String imgsrc = "http://images.rednet.cn/articleimage/2013/01/23/1403536948.jpg";
		//		String href = "http://mp.weixin.qq.com/s/RSi8Au0n7UrlebVVYLGFGw";
		//		String title = "";
		String content = "";
		//		content += "<div>";

		//		content += "<div style='color:#1496ff;padding:4px;border:1px solid #1496ff;border-radius:5px;margin:4px 0px;'>欢迎来自" + region + "的朋友<span style='border-radius: 5px;padding:0px 4px;margin:0px 4px; border:solid 1px #989898'>" + nick
		//				+ "</span>乘坐<span style='border-radius: 5px;padding:0px 4px;margin:0px 4px; border:solid 1px #989898'>" + formatedUserAgent
		//				+ "</span>进入群组" + group + "</div>";

		//		content += "<div style='color:#ee3344;padding:4px;border:1px solid #ee3344;border-radius:5px;margin:4px 0px;'>由于t-io作者最近正面回击了某些谣言，晒了t-io官网被DDos攻击的证据，损害了某些人的利益，导致OSC上一些用户对t-io抹黑谩骂的行为变本加厉！请大家不要回复他们！让他们自娱自乐^_^" + "</div>";
		//		content += "<div style='color:#ee3344;padding:4px;border:1px solid #ee3344;border-radius:5px;margin:4px 0px;'>大部分时候，他们DDos攻击的其实是nginx，目前t-io只提供了websocket服务，2M小网站防不了DDos攻击是业界共识，所以请大家把心态放好^_^" + "</div>";
		//		content += "<div style='color:#ee3344;padding:4px;border:1px solid #ee3344;border-radius:5px;margin:4px 0px;'>请文明聊天,不要输入政治、色情、犯罪等敏感词----<a href='https://gitee.com/tywo45/t-io' target='_blank'>t-io平台</a>在过滤掉这些词汇的同时，会收集这些词汇的来源。" + "</div>";
		//		content += "<div style='color:#ee3344;padding:4px;border:1px solid #ee3344;border-radius:5px;margin:4px 0px;'>武汉地区的ip暂时被列入了黑名单，所以不能聊天，t-io这2M小站防不了DDos攻击，只能先这样处理了" + "</div>";

		//		content += "<div style='color:#1496ff;padding:4px;border:1px solid #1496ff;border-radius:5px;margin:4px 0px;'>查看大图片可能会有点慢，是因为图片太大了，作者与DDos攻击者线下似乎达成默契，这几天没有检查到DDos攻击，也许就此泯恩仇了！</div>";

		//		content += "<div><a href='http://mp.weixin.qq.com/s/RSi8Au0n7UrlebVVYLGFGw' target='_blank'><span style='width:150px;'>来电话了</span></a>（t-io作者开发的可以省电话费的公众号）" + "</div>";
		//		content += "<div><a href='http://www.dtcaas.com' target='_blank'><span style='width:150px;'>五方会谈</span></a>（t-io作者开发的高清视频会议平台）" + "</div>";
		//		content += "</div>";

		//		String text = "<a alt='" + title + "' title='" + title + "' href='" + href + "' target='_blank'>" + "<img style='width:200px;height:100px;' src='" + imgsrc + "'>" + "</a>"
		//				+ content;

		//		content += "<div>";
		//		content += "<div style='color:#1496ff;padding:4px;border:1px solid #1496ff;border-radius:5px;margin:4px 0px;'>刷新页面，聊天数据不丢失！要不要试一下？</div>";
		//		content += "</div>";

		//		content += "<div>";
		//		content += "<div style='color:#1496ff;padding:4px;border:1px solid #1496ff;border-radius:5px;margin:4px 0px;'>输入四字成语试试，会有意想不到的效果，尝试输入'靡靡之声','梦中说梦'</div>";
		//		content += "</div>";

		String text = content;

		ChatRespBody.Builder builder = ChatRespBody.newBuilder();
		builder.setType(ChatType.CHAT_TYPE_PUBLIC);
		builder.setText(text);
		builder.setFromClient(org.tio.examples.im.service.UserService.sysClient);
		//		builder.setGroup(group);
		builder.setTime(SystemTimer.currTime);
		GroupContext groupContext = channelContext.groupContext;
		builder.setId(groupContext.getTioUuid().uuid());
		ChatRespBody chatRespBody = builder.build();
		WebsocketPacket respPacket1 = new WebsocketPacket(Command.COMMAND_CHAT_RESP, chatRespBody.toByteArray());
		//		Tio.send(channelContext, respPacket1);

		return null;
	}
}
