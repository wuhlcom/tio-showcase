package org.tio.examples.im.service.img2;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * @author tanyaowu
 * 2017年5月15日 下午5:53:27
 */
public class Task implements Runnable {
	File dir;
	String url_str;
	int no, begin, end;
	String regex1;
	String regex2;
	String title_regex;
	String[] picture_regex = new String[2];

	public Task(File dir, String url_str, int no, String[] regex, String title_regex, String[] picture_regex, int end) {
		this(dir, url_str, no, regex, title_regex, picture_regex, 1, end);
	}

	public Task(File dir, String url_str, int no, String[] regex, String title_regex, String[] picture_regex, int begin, int end) {
		this.dir = dir;
		this.url_str = url_str;
		this.no = no;
		this.begin = begin;
		this.end = end;
		regex1 = regex[0];
		regex2 = regex[1];
		this.picture_regex[0] = picture_regex[0];
		this.picture_regex[1] = picture_regex[1];
		this.title_regex = title_regex;
	}

	@Override
	public void run() {
		WebsitList websitList = new WebsitList(url_str, no, begin, end, regex1, regex2, title_regex);
		try {
			websitList.initUrls();
		} catch (IOException e1) {
			e1.printStackTrace();
			System.out.println(url_str + "已跳过");
		}
		Iterator<String> iterator = websitList.urls.keySet().iterator();
		int i = 0;
		while (iterator.hasNext()) {
			i++;
			try {
				String main = iterator.next();
				String title = websitList.urls.get(main);
				System.out.println(main + ":" + title);

				DetailPage detailPage = new DetailPage(main, title, picture_regex);
				detailPage.initSrcs();
				detailPage.downloadAll(dir);
			} catch (Exception e) {
				continue;
			}

			// 每下载完6个页面的图片休眠10秒，防止过于频繁访问断开连接
			if (i % 6 == 0) {
				System.out.println("休息10秒");
				for (int j = 0; j < 10; j++) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				System.out.println();
			}
		}
	}

}
