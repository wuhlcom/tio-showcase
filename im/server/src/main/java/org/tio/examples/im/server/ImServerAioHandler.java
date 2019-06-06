package org.tio.examples.im.server;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Tio;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.intf.Packet;
import org.tio.core.stat.ChannelStat;
import org.tio.examples.im.common.CommandStat;
import org.tio.examples.im.common.ImPacket;
import org.tio.examples.im.common.ImSessionContext;
import org.tio.examples.im.common.http.HttpRequestDecoder;
import org.tio.examples.im.common.http.HttpRequestPacket;
import org.tio.examples.im.common.http.HttpResponseEncoder;
import org.tio.examples.im.common.http.HttpResponsePacket;
import org.tio.examples.im.common.http.websocket.WebsocketDecoder;
import org.tio.examples.im.common.http.websocket.WebsocketEncoder;
import org.tio.examples.im.common.http.websocket.WebsocketPacket;
import org.tio.examples.im.common.http.websocket.WebsocketPacket.Opcode;
import org.tio.examples.im.common.packets.ChatRespBody;
import org.tio.examples.im.common.packets.ChatType;
import org.tio.examples.im.common.packets.Client;
import org.tio.examples.im.common.packets.Command;
import org.tio.examples.im.common.utils.ImUtils;
import org.tio.examples.im.server.handler.AuthReqHandler;
import org.tio.examples.im.server.handler.ChatReqHandler;
import org.tio.examples.im.server.handler.ClientPageReqHandler;
import org.tio.examples.im.server.handler.CloseReqHandler;
import org.tio.examples.im.server.handler.HandshakeReqHandler;
import org.tio.examples.im.server.handler.HeartbeatReqHandler;
import org.tio.examples.im.server.handler.ImBsHandlerIntf;
import org.tio.examples.im.server.handler.JoinReqHandler;
import org.tio.examples.im.server.handler.LoginReqHandler;
import org.tio.monitor.RateLimiterWrap;
import org.tio.server.intf.ServerAioHandler;
import org.tio.utils.SystemTimer;

import cn.hutool.core.util.ZipUtil;

/**
 *
 * @author tanyaowu
 *
 */
public class ImServerAioHandler implements ServerAioHandler {
	private static Logger log = LoggerFactory.getLogger(ImServerAioHandler.class);

	private static Map<Command, ImBsHandlerIntf> handlerMap = new HashMap<>();
	static {
		handlerMap.put(Command.COMMAND_HANDSHAKE_REQ, new HandshakeReqHandler());
		handlerMap.put(Command.COMMAND_AUTH_REQ, new AuthReqHandler());
		handlerMap.put(Command.COMMAND_CHAT_REQ, new ChatReqHandler());
		handlerMap.put(Command.COMMAND_JOIN_GROUP_REQ, new JoinReqHandler());
		handlerMap.put(Command.COMMAND_HEARTBEAT_REQ, new HeartbeatReqHandler());
		handlerMap.put(Command.COMMAND_CLOSE_REQ, new CloseReqHandler());

		handlerMap.put(Command.COMMAND_LOGIN_REQ, new LoginReqHandler());
		handlerMap.put(Command.COMMAND_CLIENT_PAGE_REQ, new ClientPageReqHandler());

	}

	/**
	 * 心跳
	 */
	private static WebsocketPacket heartbeatPacket = new WebsocketPacket(Command.COMMAND_HEARTBEAT_REQ);

	/**
	 * 握手
	 */
	private static WebsocketPacket handshakePacket = new WebsocketPacket(Command.COMMAND_HANDSHAKE_REQ);

	/**
	 * @param args
	 *
	 * @author tanyaowu
	 * 2016年11月18日 上午9:13:15
	 *
	 */
	public static void main(String[] args) {
	}

	/**
	 *
	 *
	 * @author tanyaowu
	 * 2016年11月18日 上午9:13:15
	 *
	 */
	public ImServerAioHandler() {
	}

	/**
	 * @see org.tio.core.intf.AioHandler#decode(java.nio.ByteBuffer)
	 *
	 * @param buffer
	 * @return
	 * @throws AioDecodeException
	 * @author tanyaowu
	 * 2016年11月18日 上午9:37:44
	 *
	 */
	@Override
	public ImPacket decode(ByteBuffer buffer, int limit, int position, int readableLength, ChannelContext channelContext) throws AioDecodeException {
		ImSessionContext imSessionContext = (ImSessionContext) channelContext.getAttribute();
		int initPosition = position;
		byte firstbyte = buffer.get(initPosition);

		if (!imSessionContext.isHandshaked()) {
			if (ImPacket.HANDSHAKE_BYTE == firstbyte) {
				buffer.position(1 + initPosition);
				return handshakePacket;
			} else {
				HttpRequestPacket requestPacket = HttpRequestDecoder.decode(buffer, channelContext);
				if (requestPacket == null) {
					return null;
				}
				imSessionContext.setHttpHandshakePacket(requestPacket);
				requestPacket.setCommand(Command.COMMAND_HANDSHAKE_REQ);
				imSessionContext.setWebsocket(true);
				return requestPacket;
			}
		}

		boolean isWebsocket = imSessionContext.isWebsocket();

		if (isWebsocket) {
			WebsocketPacket websocketPacket = WebsocketDecoder.decode(buffer, channelContext);
			if (websocketPacket == null) {
				return null;
			}

			if (!websocketPacket.isWsEof()) {
				log.error("{} websocket包还没有传完", channelContext);
				return null;
			}

			Opcode opcode = websocketPacket.getWsOpcode();
			if (opcode == Opcode.BINARY) {
				byte[] wsBody = websocketPacket.getWsBody();
				if (wsBody == null || wsBody.length == 0) {
					throw new AioDecodeException("错误的websocket包，body为空");
				}

				Command command = Command.forNumber(wsBody[0]);
				WebsocketPacket imPacket = new WebsocketPacket(command);

				if (wsBody.length > 1) {
					byte[] dst = new byte[wsBody.length - 1];
					System.arraycopy(wsBody, 1, dst, 0, dst.length);
					imPacket.setBody(dst);
				}
				return imPacket;
			} else if (opcode == Opcode.PING || opcode == Opcode.PONG) {
				return heartbeatPacket;
			} else if (opcode == Opcode.CLOSE) {
				WebsocketPacket imPacket = new WebsocketPacket(Command.COMMAND_CLOSE_REQ);
				return imPacket;
			} else if (opcode == Opcode.TEXT) {
				throw new AioDecodeException("错误的websocket包，不支持TEXT类型的数据");
			} else {
				throw new AioDecodeException("错误的websocket包，错误的Opcode");
			}

		} else {
			if (ImPacket.HEARTBEAT_BYTE == firstbyte) {
				buffer.position(1 + initPosition);
				return heartbeatPacket;
			}
		}

//		int readableLength = buffer.limit() - initPosition;

		int headerLength = ImPacket.LEAST_HEADER_LENGHT;
		WebsocketPacket imPacket = null;
		firstbyte = buffer.get();
		byte version = ImPacket.decodeVersion(firstbyte);
		boolean isCompress = ImPacket.decodeCompress(firstbyte);
		boolean hasSynSeq = ImPacket.decodeHasSynSeq(firstbyte);
		boolean is4ByteLength = ImPacket.decode4ByteLength(firstbyte);
		if (hasSynSeq) {
			headerLength += 4;
		}
		if (is4ByteLength) {
			headerLength += 2;
		}
		if (readableLength < headerLength) {
			return null;
		}
		Byte code = buffer.get();
		Command command = Command.forNumber(code);
		int bodyLength = 0;
		if (is4ByteLength) {
			bodyLength = buffer.getInt();
		} else {
			bodyLength = buffer.getShort();
		}

		if (bodyLength > ImPacket.MAX_LENGTH_OF_BODY || bodyLength < 0) {
			throw new AioDecodeException("bodyLength [" + bodyLength + "] is not right, remote:" + channelContext.getClientNode());
		}

		int seq = 0;
		if (hasSynSeq) {
			seq = buffer.getInt();
		}

		//		@SuppressWarnings("unused")
		//		int reserve = buffer.getInt();//保留字段

		//		PacketMeta<ImPacket> packetMeta = new PacketMeta<>();
		int neededLength = headerLength + bodyLength;
		int test = readableLength - neededLength;
		if (test < 0) // 不够消息体长度(剩下的buffe组不了消息体)
		{
			//			packetMeta.setNeededLength(neededLength);
			return null;
		} else {
			imPacket = new WebsocketPacket();
			imPacket.setCommand(command);

			if (seq != 0) {
				imPacket.setSynSeq(seq);
			}

			if (bodyLength > 0) {
				byte[] dst = new byte[bodyLength];
				buffer.get(dst);
				if (isCompress) {
					try {
						byte[] unGzippedBytes = ZipUtil.unGzip(dst);//.unGZip(dst);
						imPacket.setBody(unGzippedBytes);
						//						imPacket.setBodyLen(unGzippedBytes.length);
					} catch (Exception e) {
						throw new AioDecodeException(e);
					}
				} else {
					imPacket.setBody(dst);
					//					imPacket.setBodyLen(dst.length);
				}
			}

			//			packetMeta.setPacket(imPacket);
			return imPacket;

		}

	}

	/**
	 * @see org.tio.core.intf.AioHandler#encode(org.tio.core.intf.Packet)
	 *
	 * @param packet
	 * @return
	 * @author tanyaowu
	 * 2016年11月18日 上午9:37:44
	 *
	 */
	@Override
	public ByteBuffer encode(Packet packet, GroupContext groupContext, ChannelContext channelContext) {
		ImPacket imPacket = (ImPacket) packet;
		ImSessionContext imSessionContext = (ImSessionContext) channelContext.getAttribute();
		boolean isWebsocket = imSessionContext.isWebsocket();

		if (imPacket.getCommand() == Command.COMMAND_HANDSHAKE_RESP) {
			if (isWebsocket) {
				return HttpResponseEncoder.encode((HttpResponsePacket) packet, groupContext, channelContext);
			} else {
				ByteBuffer buffer = ByteBuffer.allocate(1);
				buffer.put(ImPacket.HANDSHAKE_BYTE);
				return buffer;
			}
		}

		if (isWebsocket) {
			WebsocketPacket websocketPacket = (WebsocketPacket) packet;
			return WebsocketEncoder.encode(websocketPacket, groupContext, channelContext);
		}

		byte[] body = imPacket.getBody();
		int bodyLen = 0;
		boolean isCompress = false;
		boolean is4ByteLength = false;
		if (body != null) {
			bodyLen = body.length;
			if (bodyLen > 300) {
				try {
					byte[] gzipedbody = ZipUtil.gzip(body);
					if (gzipedbody.length < body.length) {
						log.info("压缩前:{}, 压缩后:{}", body.length, gzipedbody.length);
						body = gzipedbody;
						bodyLen = gzipedbody.length;
						isCompress = true;
					}
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			}

			if (bodyLen > Short.MAX_VALUE) {
				is4ByteLength = true;
			}
		}

		int allLen = imPacket.calcHeaderLength(is4ByteLength) + bodyLen;

		ByteBuffer buffer = ByteBuffer.allocate(allLen);
		buffer.order(groupContext.getByteOrder());

		byte firstbyte = ImPacket.encodeCompress(ImPacket.VERSION, isCompress);
		firstbyte = ImPacket.encodeHasSynSeq(firstbyte, packet.getSynSeq() > 0);
		firstbyte = ImPacket.encode4ByteLength(firstbyte, is4ByteLength);
		//		String bstr = Integer.toBinaryString(firstbyte);
		//		log.error("二进制:{}",bstr);

		buffer.put(firstbyte);
		buffer.put((byte) imPacket.getCommand().getNumber());

		//GzipUtils

		if (is4ByteLength) {
			buffer.putInt(bodyLen);
		} else {
			buffer.putShort((short) bodyLen);
		}

		if (packet.getSynSeq() != null && packet.getSynSeq() > 0) {
			buffer.putInt(packet.getSynSeq());
		}
		//		else
		//		{
		//			buffer.putInt(0);
		//		}
		//
		//		buffer.putInt(0);

		if (body != null) {
			buffer.put(body);
		}
		return buffer;
	}

	/**
	 * @see org.tio.core.intf.AioHandler#handler(org.tio.core.intf.Packet)
	 *
	 * @param packet
	 * @return
	 * @throws Exception
	 * @author tanyaowu
	 * 2016年11月18日 上午9:37:44
	 *
	 */
	@Override
	public void handler(Packet packet, ChannelContext channelContext) throws Exception {
		ImPacket imPacket = (ImPacket) packet;
		Command command = imPacket.getCommand();

		ChannelStat channelStat = channelContext.stat;
		if (channelStat.receivedPackets.get() > ImServerStarter.conf.getInt("skip.warn.count")) { //前面几条命令不计入令牌桶
			GroupContext groupContext = channelContext.groupContext;
			ImSessionContext imSessionContext = (ImSessionContext) channelContext.getAttribute();
			RateLimiterWrap rateLimiterWrap = imSessionContext.getRequestRateLimiter();
			boolean[] ss = rateLimiterWrap.tryAcquire();
			String group = "g";
			if (ss[0] == false && ss[1] == false) {

				log.error("{} 访问过频繁，本次命令:{}， 将拉黑其IP", channelContext.toString(), command);
				//			String imgsrc = UserService.nextImg();
				String text = "<span style='font-size:16px;color:red'>对不起大家，由于我发消息太频繁，已经被服务器拉黑了，大家珍重，管理员心情好，可能会把我从黑名单中清除。</span>";
				ChatRespBody.Builder builder = ChatRespBody.newBuilder();
				builder.setType(ChatType.CHAT_TYPE_PUBLIC);
				builder.setText(text);
				builder.setFromClient(imSessionContext.getClient());

				builder.setGroup(group);
				builder.setTime(SystemTimer.currTime);
				ChatRespBody chatRespBody = builder.build();
				WebsocketPacket respPacket1 = new WebsocketPacket(Command.COMMAND_CHAT_RESP, chatRespBody.toByteArray());
				Tio.sendToGroup(groupContext, group, respPacket1);

				Tio.IpBlacklist.add(groupContext, channelContext.getClientNode().getIp());
				return;
			} else if (ss[0] == false && ss[1] == true) {
				log.error("{} 访问过频繁，本次命令:{}，将警告一次", channelContext.toString(), command);

				Client client = imSessionContext.getClient();
				String nick = client.getUser().getNick();
				String region = client.getRegion();
				String ip = client.getIp();

				int warnCount = rateLimiterWrap.getWarnCount().get();
				int maxWarnCount = rateLimiterWrap.getMaxWarnCount();
				int xx = maxWarnCount - warnCount;

				String formatedUserAgent = ImUtils.formatUserAgent(channelContext);

				String text = "<div>第" + warnCount + "次警告【" + nick + "】【" + region + "】【" + ip + "】【" + formatedUserAgent + "】，还剩" + xx + "次警告机会" + "</div>";
				//				text += "<div style='font-size:14px;color:#ff0033'><a href='http://t-io.org:9292/ecosphere.html?v=4514545454' target='_blank'>如果被拉黑请联系作者, 欢迎对t-io生态圈进行投资建设，谢谢！</a></div>";
				ChatRespBody.Builder builder = ChatRespBody.newBuilder();
				builder.setType(ChatType.CHAT_TYPE_PUBLIC);
				builder.setText(text);
				builder.setFromClient(org.tio.examples.im.service.UserService.sysClient);

				builder.setGroup(group);
				builder.setTime(SystemTimer.currTime);
				ChatRespBody chatRespBody = builder.build();
				WebsocketPacket respPacket1 = new WebsocketPacket(Command.COMMAND_CHAT_RESP, chatRespBody.toByteArray());
				Tio.sendToGroup(groupContext, group, respPacket1);
				return;
			}
		}

		ImBsHandlerIntf handler = handlerMap.get(command);
		if (handler != null) {
			Object obj = handler.handler(imPacket, channelContext);
			CommandStat.getCount(command).handled.incrementAndGet();
			return;
		} else {
			log.warn("命令码[{}]没有对应的处理类", command);
			CommandStat.getCount(command).handled.incrementAndGet();
			return;
		}

	}

}
