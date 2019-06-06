package org.tio.examples.im.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.examples.im.common.packets.Client;
import org.tio.examples.im.common.packets.User;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;

/**
 * 这个类是mock类，用户实际项目中需要用自己的代码替换现有的代码
 * @author tanyaowu
 * 2017年5月8日 下午6:12:47
 */
public class UserService {
	private static Logger log = LoggerFactory.getLogger(UserService.class);

	public static final Map<String, User> tokenMap = new HashMap<>();

	private static java.util.concurrent.atomic.AtomicLong idAtomicLong = new AtomicLong();
	//模拟头像
	//	private static String[] avatars = new String[] { "http://wx.qlogo.cn/mmopen/O8EdbgD8IxibvOhZ6ib912pLkeAfHfmWRkvr4POl2OF98JDlrSQxtmsCUO9ZqicVZJUjOWicTIEOrvjqZFjhfZ9Ma5c3kF855Yde/0",
	//			"http://wx.qlogo.cn/mmopen/FiaIW3HsXzg7cYbAlYlibdicbw2xwj1QucyIoWQZ2HzvVXDh5AwwewXwsOO7891Rx5OH8PQS2DjGL1EXw3nFajRYw/0",
	//			"http://wx.qlogo.cn/mmopen/Q3auHgzwzM5PzRt6X82qk7cp1n5OxLUBBD1JXCXyVE7rbPKl8KrMIteGfBqZKYAdGxVScu4HCccP9HMyJ4fp1Q/0",
	//			"http://wx.qlogo.cn/mmopen/ajNVdqHZLLCNwrWvNcCNoKadyGbJibEDiak2aqftjrtNvuIFmibiciaicpRDwD5kkkdILDOzWHr8tlKFISuLs0Y9HGtA/0",
	//			"http://wx.qlogo.cn/mmopen/snImia2EJEZ90wAn06WPypdUqrGbFMNW9WTNKSsa55gN1XAic7XPHtZsBtTGP4DnAQoPwIgRaBn6NnpsRLBt4aq7pdVymqHtY3/0",
	//			"http://wx.qlogo.cn/mmopen/PiajxSqBRaEI3icyOv9fpULldSNibfgDsYJqAZM1sTs5ibJzCiaxDptiahLCEnCicibOYbZcmpgjMdqTRib34uyKDEFDNNw/0" };

	//	private static String[] familyName = new String[] { "谭", "刘", "张", "李", "胡", "沈", "朱", "钱", "王", "伍", "赵", "孙", "吕", "马", "秦", "毛", "成", "梅", "黄", "郭", "杨", "季", "童", "习", "郑",
	//			"吴", "周", "蒋", "卫", "尤", "何", "魏", "章", "郎", " 唐", "汤", "苗", "孔", "鲁", "韦", "任", "袁", "贺", "狄朱" };

	private static String[] secondName = new String[] { "艺昕", "红薯", "明远", "天蓬", "三丰", "德华", "歌", "佳", "乐", "天", "燕子", "子牛", "海", "燕", "花", "娟", "冰冰", "丽娅", "大为", "无为", "渔民", "大赋",
			"明", "远平", "克弱", "亦菲", "靓颖", "富城", "岳", "先觉", "牛", "阿狗", "阿猫", "辰", "蝴蝶", "文化", "冲之", "悟空", "行者", "悟净", "悟能", "观", "音", "乐天", "耀扬", "伊健", "炅", "娜", "春花", "秋香", "春香",
			"大为", "如来", "佛祖", "科比", "罗斯", "詹姆屎", "科神", "科蜜", "库里", "卡特", "麦迪", "乔丹", "魔术师", "加索尔", "法码尔", "南斯", "伊哥", "杜兰特", "保罗", "杭州", "爱湘", "湘湘", "昕", "函", "鬼谷子", "膑", "荡",
			"子家", "德利优视", "五方会谈", "来电话了", "t-io", "talent", "夏夏", "晶晶", "婷婷", "玉婷", "步轩", "风", "峰", "飞", "羽", "备", "操", "惇", "良", "远", "玺文", "文琴", "子文", "涛", "紫", "薇", "心如", "有朋",
			"巨基", "学森", "青天", "昭", "静文", "怡", "月", "魔", "立平", "昱", "坤琳" };

	private static java.util.concurrent.atomic.AtomicInteger secondNameCurIndex = new AtomicInteger(RandomUtil.randomInt(0, secondName.length));

	/**
	 * 系统管理员的Client对象，用来发送系统消息时用
	 */
	public static final Client sysClient = getSysClient();

	/**
	 * 获取系统管理员的Client对象
	 * @return
	 * @author tanyaowu
	 */
	private static Client getSysClient() {
		Client.Builder clientBuilder = null;
		Client client = null;

		clientBuilder = Client.newBuilder();
		clientBuilder.setId("x");
		clientBuilder.setIp("0.0.0.0");
		clientBuilder.setPort(1234);
		clientBuilder.setRegion("保密");

		User.Builder userBuilder = User.newBuilder();
		userBuilder.setNick("系统管理员");
		userBuilder.setId(-1);
		userBuilder.setAvatar("https://gitee.com/tywo45/t-io/raw/master/docs/logo/logo.jpg");
		User user = userBuilder.build();

		clientBuilder.setUser(user);

		client = clientBuilder.build();
		return client;
	}

	/**
	 * 根据token获取用户信息
	 * @param token
	 * @return
	 * @author tanyaowu
	 */
	public static User getUser(String token) {
		//demo中用map，生产环境需要用cache
		User user = tokenMap.get(token);
		if (user == null) {
			User.Builder userBuilder = User.newBuilder();
			userBuilder.setNick(FamilynameService.next() + nextSecondName());
			userBuilder.setId(idAtomicLong.incrementAndGet());
			userBuilder.setAvatar(UserService.nextImg());

			user = userBuilder.build();

			if (tokenMap.size() > 10000) {
				tokenMap.clear();
			}

			tokenMap.put(token, user);
		}

		return user;
	}

	/**
	 * 根据用户名和密码获取用户
	 * @param loginname
	 * @param password
	 * @return
	 * @author tanyaowu
	 */
	public static User getUser(String loginname, String password) {
		String token = null;
		return getUser(token);
	}

	/**
	 * @param args
	 * @author tanyaowu
	 */
	public static void main(String[] args) {

	}

	public static String newToken() {
		return UUID.randomUUID().toString();
	}

	public static String nextImg() {
		Date date = new Date();
		int hour = DateUtil.hour(date, true);

		return ImgTxService.nextImg();

		//		if (hour >= 22 || hour <= 5) {
		//			return ImgMnService.nextImg();
		//		} else {
		//			return ImgFjService.nextImg();
		//		}
	}

	public static String nextSecondName() {
		int index = secondNameCurIndex.getAndIncrement() % secondName.length;
		log.info("{}, {}, {}", secondName.length, secondNameCurIndex.get(), index);
		return secondName[index];
	}

	/**
	 *
	 * @author tanyaowu
	 */
	public UserService() {
		//com.google.common.util.concurrent.RateLimiter
	}
}
