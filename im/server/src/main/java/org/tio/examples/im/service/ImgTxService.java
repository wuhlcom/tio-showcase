package org.tio.examples.im.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.utils.lock.ListWithLock;

import cn.hutool.core.io.FileUtil;

/**
 * 头像
 * @author tanyaowu
 * 2017年5月14日 上午9:48:03
 */
public class ImgTxService {
	private static Logger log = LoggerFactory.getLogger(ImgTxService.class);

	public static final ListWithLock<String> imgListWithLock = new ListWithLock<>(new ArrayList<String>());

	public static final String dftimg = "http://images.rednet.cn/articleimage/2013/01/23/1403536948.jpg";

	public static final String filepath = FileUtil.getAbsolutePath("classpath:config/imgs/tx.txt");

	public static final int maxSize = 100000;

	static AtomicInteger imgIndex = new AtomicInteger();

	//风景
	static String[] pags = new String[] { "http://www.mmonly.cc/wmtp/fjtp/list_21_1.html", "http://www.mmonly.cc/wmtp/fjtp/list_21_2.html",
			"http://www.mmonly.cc/wmtp/fjtp/list_21_3.html", "http://www.mmonly.cc/wmtp/fjtp/list_21_4.html", "http://www.mmonly.cc/wmtp/fjtp/list_21_5.html",
			"http://www.mmonly.cc/wmtp/fjtp/list_21_6.html"

	};

	/**
	 * @param args
	 * @author tanyaowu
	 */
	public static void main(String[] args) {
		start();
		String xx = nextImg();
		System.out.println(xx);
	}

	public static String nextImg() {

		Lock lock = imgListWithLock.getLock().readLock();
		try {
			lock.lock();
			List<String> list = imgListWithLock.getObj();

			if (list.size() == 0) {
				return dftimg;
			}

			int index = imgIndex.incrementAndGet() % list.size();// RandomUtil.randomInt(0, list.size() - 1);
			log.error("图片index:" + index);
			String imgsrc = list.get(index);
			if (StringUtils.isNotBlank(imgsrc)) {
				return imgsrc;
			}
			return nextImg();

		} catch (Exception e1) {
			log.error(e1.toString(), e1);
			return dftimg;
		} finally {
			lock.unlock();
		}

	}

	public static void savefile(List<String> srcs) {
		WriteLock lock = imgListWithLock.getLock().writeLock();
		try {
			lock.lock();
			List<String> list = imgListWithLock.getObj();
			list.addAll(srcs);
		} catch (Exception e1) {
			log.error(e1.toString(), e1);
		} finally {
			lock.unlock();
		}
	}

	public static boolean savefile(String src) {
		WriteLock lock = imgListWithLock.getLock().writeLock();
		try {
			lock.lock();
			List<String> list = imgListWithLock.getObj();
			list.add(src);
			log.error("{}、【{}】", list.size(), src);
			while (list.size() > maxSize) {
				return false;
			}

		} catch (Exception e1) {
			log.error(e1.toString(), e1);
		} finally {
			lock.unlock();
		}
		return true;
	}

	public static void start() {
		//https://gitee.com/tywo45/t-io/raw/master/docs/tchat/tx/B010.jpg
		//		List<String> dd = new ArrayList<>();
		//		for(int i = 0; i < 43; i++){
		//			dd.add("https://gitee.com/tywo45/t-io/raw/master/docs/tchat/tx/B"+StringUtils.leftPad((i+1)+"", 3, "0")+".jpg");
		//		}
		//		for(int i = 0; i < 80; i++){
		//			dd.add("https://gitee.com/tywo45/t-io/raw/master/docs/tchat/tx/G"+StringUtils.leftPad((i+1)+"", 3, "0")+".jpg");
		//		}
		//		FileUtil.writeLines(dd, filepath, "utf-8");

		//		File root = new File("D:/work/t-io/docs/tchat/tx");
		//		File[] fs = root.listFiles();
		//		List<String> dd = new ArrayList<>();
		//		for(File fi : fs){
		//			dd.add("https://gitee.com/tywo45/t-io/raw/master/docs/tchat/tx/"+fi.getName());
		//		}
		//		FileUtil.writeLines(dd, filepath, "utf-8");

		File file = new File(filepath);
		if (file.exists()) {
			List<String> list = FileUtil.readLines(file, "utf-8");
			imgListWithLock.getObj().addAll(list);
		}

	}

	public static void work() {
	}

	/**
	 *
	 * @author tanyaowu
	 */
	public ImgTxService() {

	}
}
