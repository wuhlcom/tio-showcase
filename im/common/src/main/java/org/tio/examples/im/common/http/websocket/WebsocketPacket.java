package org.tio.examples.im.common.http.websocket;

import java.util.HashMap;
import java.util.Map;

import org.tio.examples.im.common.ImPacket;
import org.tio.examples.im.common.packets.Command;

/**
 * 参考了baseio: https://gitee.com/generallycloud/baseio
 * com.generallycloud.nio.codec.http11.future.WebSocketReadFutureImpl
 * @author tanyaowu
 *
 */
public class WebsocketPacket extends ImPacket {
	public static enum Opcode {
		TEXT((byte) 1), BINARY((byte) 2), CLOSE((byte) 8), PING((byte) 9), PONG((byte) 10);

		private static final Map<Byte, Opcode> map = new HashMap<>();

		static {
			for (Opcode command : values()) {
				map.put(command.getCode(), command);
			}
		}

		public static Opcode valueOf(byte code) {
			return map.get(code);
		}

		private final byte code;

		private Opcode(byte code) {
			this.code = code;
		}

		public byte getCode() {
			return code;
		}
	}

	private static final long serialVersionUID = 7643589534289815719L;

	public static final int MINIMUM_HEADER_LENGTH = 2;

	public static final int MAX_BODY_LENGTH = 1024 * 512; //最多接受的1024 * 512(半M)数据

	public static final String CHARSET_NAME = "utf-8";

	private boolean wsEof;
	private Opcode wsOpcode = Opcode.BINARY;
	private boolean wsHasMask;
	private long wsBodyLength;
	private byte[] wsMask;
	private byte[] wsBody;
	private String wsBodyText; //当为文本时才有此字段

	/**
	 *
	 * @author tanyaowu
	 */
	public WebsocketPacket() {
		super();
	}

	/**
	 * @param commandHandshakeResp
	 * @author tanyaowu
	 */
	public WebsocketPacket(Command command) {
		super(command);
	}

	/**
	 *
	 *
	 * @author tanyaowu
	 * 2017年2月22日 下午4:14:40
	 * @param bs
	 * @param commandChatResp
	 *
	 */
	public WebsocketPacket(Command command, byte[] bs) {
		super(command, bs);
	}

	/**
	 * @return the wsBody
	 */
	public byte[] getWsBody() {
		return wsBody;
	}

	/**
	 * @return the wsBodyLength
	 */
	public long getWsBodyLength() {
		return wsBodyLength;
	}

	/**
	 * @return the wsBodyText
	 */
	public String getWsBodyText() {
		return wsBodyText;
	}

	/**
	 * @return the wsMask
	 */
	public byte[] getWsMask() {
		return wsMask;
	}

	/**
	 * @return the wsOpcode
	 */
	public Opcode getWsOpcode() {
		return wsOpcode;
	}

	/**
	 * @return the wsEof
	 */
	public boolean isWsEof() {
		return wsEof;
	}

	/**
	 * @return the wsHasMask
	 */
	public boolean isWsHasMask() {
		return wsHasMask;
	}

	/**
	 * @param wsBody the wsBody to set
	 */
	public void setWsBody(byte[] wsBody) {
		this.wsBody = wsBody;
	}

	/**
	 * @param wsBodyLength the wsBodyLength to set
	 */
	public void setWsBodyLength(long wsBodyLength) {
		this.wsBodyLength = wsBodyLength;
	}

	/**
	 * @param wsBodyText the wsBodyText to set
	 */
	public void setWsBodyText(String wsBodyText) {
		this.wsBodyText = wsBodyText;
	}

	/**
	 * @param wsEof the wsEof to set
	 */
	public void setWsEof(boolean wsEof) {
		this.wsEof = wsEof;
	}

	/**
	 * @param wsHasMask the wsHasMask to set
	 */
	public void setWsHasMask(boolean wsHasMask) {
		this.wsHasMask = wsHasMask;
	}

	/**
	 * @param wsMask the wsMask to set
	 */
	public void setWsMask(byte[] wsMask) {
		this.wsMask = wsMask;
	}

	/**
	 * @param wsOpcode the wsOpcode to set
	 */
	public void setWsOpcode(Opcode wsOpcode) {
		this.wsOpcode = wsOpcode;
	}

}
