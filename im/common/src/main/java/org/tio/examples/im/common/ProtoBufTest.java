package org.tio.examples.im.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.examples.im.common.packets.Client;

/**
 * @author tanyaowu
 * 2017年5月9日 上午11:21:54
 */
public class ProtoBufTest {
	private static Logger log = LoggerFactory.getLogger(ProtoBufTest.class);

	/**
	 * @param args
	 * @author tanyaowu
	 */
	public static void main(String[] args) {
		Client.Builder clientBuilder = null;
		Client client = null;
		clientBuilder = Client.newBuilder();

		clientBuilder.setId("ddddd");
		clientBuilder.setIp("127.0.0.1");
		clientBuilder.setPort(9876);

		client = clientBuilder.build();

		System.out.println(client);
		System.out.println(clientBuilder);

		Client.Builder clientBuilder1 = Client.newBuilder(client);

		clientBuilder1.setId("4444");
		Client client1 = clientBuilder1.build();

		System.out.println(client1);
		System.out.println(clientBuilder1);

		System.out.println(client1 == client);
		System.out.println(clientBuilder1 == clientBuilder);

	}

	/**
	 *
	 * @author tanyaowu
	 */
	public ProtoBufTest() {
	}
}
