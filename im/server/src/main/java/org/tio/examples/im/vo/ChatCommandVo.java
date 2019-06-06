package org.tio.examples.im.vo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.examples.im.common.packets.Client;

/**
 * @author tanyaowu
 * 2017年6月23日 上午11:46:21
 */
public class ChatCommandVo {
	private static Logger log = LoggerFactory.getLogger(ChatCommandVo.class);

	/**
	 * @param args
	 * @author tanyaowu
	 */
	public static void main(String[] args) {

	}

	private String initText;
	private String afterText;
	private boolean privateChat = true; //是否要私发给请求用户（true: 则不会发给其它人）
	private boolean isCommand = true; //是否是命令

	private Client fromClient = null;

	/**
	 *
	 * @author tanyaowu
	 */
	public ChatCommandVo() {
	}

	/**
	 * @return the afterText
	 */
	public String getAfterText() {
		return afterText;
	}

	/**
	 * @return the fromClient
	 */
	public Client getFromClient() {
		return fromClient;
	}

	/**
	 * @return the initText
	 */
	public String getInitText() {
		return initText;
	}

	/**
	 * @return the isCommand
	 */
	public boolean isCommand() {
		return isCommand;
	}

	/**
	 * @return the privateChat
	 */
	public boolean isPrivateChat() {
		return privateChat;
	}

	/**
	 * @param afterText the afterText to set
	 */
	public void setAfterText(String afterText) {
		this.afterText = afterText;
	}

	/**
	 * @param isCommand the isCommand to set
	 */
	public void setCommand(boolean isCommand) {
		this.isCommand = isCommand;
	}

	/**
	 * @param fromClient the fromClient to set
	 */
	public void setFromClient(Client fromClient) {
		this.fromClient = fromClient;
	}

	/**
	 * @param initText the initText to set
	 */
	public void setInitText(String initText) {
		this.initText = initText;
	}

	/**
	 * @param privateChat the privateChat to set
	 */
	public void setPrivateChat(boolean privateChat) {
		this.privateChat = privateChat;
	}
}
