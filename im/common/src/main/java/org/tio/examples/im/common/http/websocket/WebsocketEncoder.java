package org.tio.examples.im.common.http.websocket;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.utils.ByteBufferUtils;
import org.tio.examples.im.common.packets.Command;

/**
 * 参考了baseio: https://gitee.com/generallycloud/baseio
 * com.generallycloud.nio.codec.http11.WebSocketProtocolEncoder
 * @author tanyaowu
 *
 */
public class WebsocketEncoder {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(WebsocketEncoder.class);

	public static final int MAX_HEADER_LENGTH = 20480;

	private static void checkLength(byte[] bytes, int length, int offset) {
		if (bytes == null) {
			throw new IllegalArgumentException("null");
		}

		if (offset < 0) {
			throw new IllegalArgumentException("invalidate offset " + offset);
		}

		if (bytes.length - offset < length) {
			throw new IllegalArgumentException("invalidate length " + bytes.length);
		}
	}

	public static ByteBuffer encode(WebsocketPacket websocketPacket, GroupContext groupContext, ChannelContext channelContext) {
		//		byte[] websocketHeader;
		byte[] imBody = websocketPacket.getBody();
		int wsBodyLength = 1; //固定有一个命令码，占一位
		Command command = websocketPacket.getCommand();
		if (command == null || Command.COMMAND_HEARTBEAT_RESP == command) {
			wsBodyLength = 0;
		}
		if (imBody != null) {
			wsBodyLength += imBody.length;
		}

		byte header0 = (byte) (0x8f & (websocketPacket.getWsOpcode().getCode() | 0xf0));
		ByteBuffer buf = null;
		if (wsBodyLength < 126) {
			buf = ByteBuffer.allocate(2 + wsBodyLength);
			buf.put(header0);
			buf.put((byte) wsBodyLength);
		} else if (wsBodyLength < (1 << 16) - 1) {
			buf = ByteBuffer.allocate(4 + wsBodyLength);
			buf.put(header0);
			buf.put((byte) 126);
			ByteBufferUtils.writeUB2WithBigEdian(buf, wsBodyLength);
		} else {
			buf = ByteBuffer.allocate(10 + wsBodyLength);
			buf.put(header0);
			buf.put((byte) 127);
			buf.put(new byte[] { 0, 0, 0, 0 });
			ByteBufferUtils.writeUB4WithBigEdian(buf, wsBodyLength);
		}

		/**
		 * ws的心跳包只要在有一个pong标识就可以了
		 */
		if (command != null && Command.COMMAND_HEARTBEAT_RESP != command) {
			buf.put((byte) command.getNumber());
		}

		if (imBody != null && imBody.length > 0) {
			buf.put(imBody);
		}

		return buf;
	}

	public static void int2Byte(byte[] bytes, int value, int offset) {
		checkLength(bytes, 4, offset);

		bytes[offset + 3] = (byte) (value & 0xff);
		bytes[offset + 2] = (byte) (value >> 8 * 1 & 0xff);
		bytes[offset + 1] = (byte) (value >> 8 * 2 & 0xff);
		bytes[offset + 0] = (byte) (value >> 8 * 3);
	}

	/**
	 * @param args
	 *
	 * @author tanyaowu
	 * 2017年2月22日 下午4:06:42
	 *
	 */
	public static void main(String[] args) {

	}

	/**
	 *
	 * @author tanyaowu
	 * 2017年2月22日 下午4:06:42
	 *
	 */
	public WebsocketEncoder() {

	}

}
