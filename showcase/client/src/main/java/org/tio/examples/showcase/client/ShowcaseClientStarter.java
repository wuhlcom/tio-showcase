package org.tio.examples.showcase.client;

import org.apache.commons.lang3.StringUtils;
import org.tio.client.TioClient;
import org.tio.client.ClientChannelContext;
import org.tio.client.ClientGroupContext;
import org.tio.client.ReconnConf;
import org.tio.client.intf.ClientAioHandler;
import org.tio.client.intf.ClientAioListener;
import org.tio.core.Tio;
import org.tio.core.Node;
import org.tio.examples.showcase.common.Const;
import org.tio.examples.showcase.common.ShowcasePacket;
import org.tio.examples.showcase.common.Type;
import org.tio.examples.showcase.common.packets.GroupMsgReqBody;
import org.tio.examples.showcase.common.packets.JoinGroupReqBody;
import org.tio.examples.showcase.common.packets.LoginReqBody;
import org.tio.examples.showcase.common.packets.P2PReqBody;
import org.tio.utils.json.Json;

/**
 *
 * @author tanyaowu
 */
public class ShowcaseClientStarter {
	static String serverIp = "127.0.0.1";
	static int serverPort = Const.PORT;

	private static Node serverNode = new Node(serverIp, serverPort);

	//用来自动连接的，不想自动连接请设为null
	private static ReconnConf reconnConf = new ReconnConf(5000L);

	private static ClientAioHandler tioClientHandler = new ShowcaseClientAioHandler();
	private static ClientAioListener aioListener = new ShowcaseClientAioListener();
	private static ClientGroupContext clientGroupContext = new ClientGroupContext(tioClientHandler, aioListener, reconnConf);

	private static TioClient tioClient = null;

	static ClientChannelContext clientChannelContext;

	public static void command() throws Exception {
		@SuppressWarnings("resource")
		java.util.Scanner sc = new java.util.Scanner(System.in);
		int i = 1;
		StringBuilder sb = new StringBuilder();
		sb.append("使用指南:\r\n");
		sb.append(i++ + "、需要帮助，输入 '?'.\r\n");
		sb.append(i++ + "、登录，输入 'login loginname password'.\r\n");
		sb.append(i++ + "、进入群组，输入 'join group1'.\r\n");
		sb.append(i++ + "、群聊，输入 'groupMsg group1 text'.\r\n");
		sb.append(i++ + "、点对点聊天，输入 'p2pMsg loginname text'.\r\n");

		sb.append(i++ + "、退出程序，输入 'exit'.\r\n");

		System.out.println(sb);

		String line = sc.nextLine(); // 这个就是用户输入的数据
		while (true) {
			if ("exit".equalsIgnoreCase(line)) {
				System.out.println("Thanks for using! bye bye.");
				break;
			} else if ("?".equals(line)) {
				System.out.println(sb);
			}

			processCommand(line);

			line = sc.nextLine(); // 这个就是用户输入的数据
		}

		tioClient.stop();
		System.exit(0);
	}

	public static void main(String[] args) throws Exception {
		tioClient = new TioClient(clientGroupContext);
		clientChannelContext = tioClient.connect(serverNode);
		command();
	}

	public static void processCommand(String line) throws Exception {
		if (StringUtils.isBlank(line)) {
			return;
		}

		String[] args = StringUtils.split(line, " ");
		String command = args[0];

		if ("login".equalsIgnoreCase(command)) {
			String loginname = args[1];
			String password = args[2];

			LoginReqBody loginReqBody = new LoginReqBody();
			loginReqBody.setLoginname(loginname);
			loginReqBody.setPassword(password);

			ShowcasePacket reqPacket = new ShowcasePacket();
			reqPacket.setType(Type.LOGIN_REQ);
			reqPacket.setBody(Json.toJson(loginReqBody).getBytes(ShowcasePacket.CHARSET));

			Tio.send(clientChannelContext, reqPacket);

		} else if ("join".equals(command)) {
			String group = args[1];

			JoinGroupReqBody joinGroupReqBody = new JoinGroupReqBody();
			joinGroupReqBody.setGroup(group);

			ShowcasePacket reqPacket = new ShowcasePacket();
			reqPacket.setType(Type.JOIN_GROUP_REQ);
			reqPacket.setBody(Json.toJson(joinGroupReqBody).getBytes(ShowcasePacket.CHARSET));

			Tio.send(clientChannelContext, reqPacket);
		} else if ("groupMsg".equals(command)) {
			String group = args[1];
			String text = args[2];

			GroupMsgReqBody groupMsgReqBody = new GroupMsgReqBody();
			groupMsgReqBody.setToGroup(group);
			groupMsgReqBody.setText(text);

			ShowcasePacket reqPacket = new ShowcasePacket();
			reqPacket.setType(Type.GROUP_MSG_REQ);
			reqPacket.setBody(Json.toJson(groupMsgReqBody).getBytes(ShowcasePacket.CHARSET));

			Tio.send(clientChannelContext, reqPacket);
		} else if ("p2pMsg".equals(command)) {
			String toUserid = args[1];
			String text = args[2];

			P2PReqBody p2pReqBody = new P2PReqBody();
			p2pReqBody.setToUserid(toUserid);
			p2pReqBody.setText(text);

			ShowcasePacket reqPacket = new ShowcasePacket();
			reqPacket.setType(Type.P2P_REQ);
			reqPacket.setBody(Json.toJson(p2pReqBody).getBytes(ShowcasePacket.CHARSET));

			Tio.send(clientChannelContext, reqPacket);
		}

	}
}
