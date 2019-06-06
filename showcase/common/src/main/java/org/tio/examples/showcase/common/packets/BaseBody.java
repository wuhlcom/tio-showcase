package org.tio.examples.showcase.common.packets;

/**
 *
 * @author tanyaowu
 * 2017年3月27日 上午12:12:17
 */
public class BaseBody {
	/**
	 * @param args
	 *
	 * @author tanyaowu
	 * 2017年3月25日 上午8:09:34
	 *
	 */
	public static void main(String[] args) {
	}

	/**
	 * 消息发送时间
	 */
	private Long time = System.currentTimeMillis();

	/**
	 *
	 *
	 * @author tanyaowu
	 * 2017年3月25日 上午8:09:34
	 *
	 */
	public BaseBody() {
	}

	/**
	 * @return the currTime
	 */
	public Long getTime() {
		return time;
	}

	/**
	 * @param currTime the currTime to set
	 */
	public void setTime(Long time) {
		this.time = time;
	}
}
