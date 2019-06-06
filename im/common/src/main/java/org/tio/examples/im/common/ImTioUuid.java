package org.tio.examples.im.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.intf.TioUuid;

import cn.hutool.core.lang.Snowflake;

/**
 * @author tanyaowu
 * 2017年6月5日 上午10:44:26
 */
public class ImTioUuid implements TioUuid {
	private static Logger log = LoggerFactory.getLogger(ImTioUuid.class);

	/**
	 * @param args
	 * @author tanyaowu
	 */
	public static void main(String[] args) {

	}

	//	private long workerId;
	//	private long datacenterId;
	//
	private Snowflake snowflake;

	/**
	 *
	 * @author tanyaowu
	 */
	public ImTioUuid(long workerId, long datacenterId) {
		snowflake = new Snowflake(workerId, datacenterId);
	}

	/**
	 * @return
	 * @author tanyaowu
	 */
	@Override
	public String uuid() {
		return snowflake.nextId() + "";
	}
}
