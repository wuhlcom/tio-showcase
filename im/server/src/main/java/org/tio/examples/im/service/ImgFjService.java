package org.tio.examples.im.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.utils.lock.ListWithLock;

import cn.hutool.core.io.FileUtil;

/**
 * 风景图
 * @author tanyaowu
 * 2017年5月14日 上午9:48:03
 */
public class ImgFjService {
	private static Logger log = LoggerFactory.getLogger(ImgFjService.class);

	public static final ListWithLock<String> imgListWithLock = new ListWithLock<>(new ArrayList<String>());

	public static final String dftimg = "http://images.rednet.cn/articleimage/2013/01/23/1403536948.jpg";

	public static final String filepath = FileUtil.getAbsolutePath("classpath:config/imgs/fj.txt");

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
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					work();
					try {
						Thread.sleep(1000 * 60 * 60 * 24); //2小时爬一次
					} catch (Exception e) {
						log.error(e.toString(), e);
					}
				}
			}
		}, "get img url").start();

	}

	public static void work() {
		File file = new File(filepath);
		if (file.exists()) {
			List<String> list = FileUtil.readLines(file, "utf-8");
			if (list.size() >= 100) {
				imgListWithLock.getObj().addAll(list);
				return;
			}
		}

		List<String> list = new ArrayList<>();
		list.addAll(imgListWithLock.getObj());

		long sleeptime = 500;
		boolean isfirst = false;
		if (list.size() == 0) {
			isfirst = true;
			sleeptime = 1;
		}

		L1: for (String pag : pags) {
			try {
				Document doc = null;
				//创建页面对象
				doc = Jsoup.connect(pag).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.95 Safari/537.36")
						.timeout(10000).get();
				//根据标签和class id获取元素
				Elements div = doc.select("div.ABox");
				//根据标签获取元素
				Elements pages = div.select("a");

				int count = 0;
				int invalidCount = 0;
				L2: for (Element e : pages) {
					try {
						String page = e.attr("href");
						Document imgdoc = Jsoup.connect(page)
								.userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.95 Safari/537.36").timeout(10000)
								.get();
						Elements div22 = imgdoc.select(".totalpage");
						String totalpageStr = div22.get(0).text();
						int totalpage = Integer.parseInt(totalpageStr);
						if (totalpage > 0) {
							for (int i = 1; i <= totalpage; i++) {

								try {
									Document imgpage = Jsoup.connect(page.substring(0, page.length() - 5) + "_" + i + ".html")
											.userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.95 Safari/537.36")
											.timeout(10000).get();
									Elements div2 = imgpage.select("#big-pic");
									Elements p = div2.select("p");
									Elements a = p.select("a");
									Elements img = p.select("img");
									String src = img.attr("src");

									if (StringUtils.isBlank(src) || !StringUtils.startsWith(src, "http")) {
										continue;
									}

									if (isfirst) {
										boolean f = savefile(src);
										if (!f) {
											break L1;
										}
									} else {
										list.add(src);
										log.error("{}、【{}】", list.size(), src);
										while (list.size() > maxSize) {
											break L1;
											//											list.remove(0);
											//											log.error("删除一个元素后:{}", list.size());
										}
									}
									invalidCount++;

									count++;
									i++;
									Thread.sleep(sleeptime);
								} catch (Exception e1) {
									log.error(e1.toString(), e1);
								}
							}
						}
					} catch (IOException e1) {
						log.error(e1.toString(), e1);
					}
				}
				if (!isfirst) {
					savefile(list);
				}

				log.error("抓取图片地址，打完收工，本次共找到:{}, 其中有效数据:{}，", count, invalidCount);

			} catch (Exception e) {
				log.error(e.toString(), e);
			}

		}

		List<String> list1 = imgListWithLock.getObj();
		FileUtil.writeLines(list1, filepath, "utf-8");
	}

	/**
	 *
	 * @author tanyaowu
	 */
	public ImgFjService() {

	}
}
