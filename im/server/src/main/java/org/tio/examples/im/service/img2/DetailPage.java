package org.tio.examples.im.service.img2;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tanyaowu
 * 2017年5月15日 下午5:54:51
 */
public class DetailPage {
	private static Logger log = LoggerFactory.getLogger(DetailPage.class);

	private static String page_regex = "\\u5171(\\d+)+\\u9875";

	private static Pattern page_pattern = Pattern.compile("\\u5171(\\d+)+\\u9875");
	String title;

	private int pages = 1;
	LinkedList<String> srcs = new LinkedList<>();
	String pre_main;
	String regex1;// 所要下载的文件资源的正则表达式
	String regex2;
	Pattern pattern1, pattern2;

	/**
	 *
	 * @author tanyaowu
	 */
	public DetailPage() {
	}

	public DetailPage(String main, String title, String[] regex) throws IOException {
		this.title = title;
		this.pre_main = main;
		this.regex1 = regex[0];
		this.regex2 = regex[1];
		pattern1 = Pattern.compile(regex1);
		pattern2 = Pattern.compile(regex2);
		initPages();
	}

	public void downloadAll(File dir) throws IOException {
		if (title == null) {
			return;
		}
		File dir2 = new File(dir, title);
		if (!dir2.exists()) {
			dir2.mkdirs();
		}
		int num = 1;

		System.out.println(dir2 + ":创建成功");

		Iterator<String> it = srcs.iterator();
		while (it.hasNext()) {
			try {
				String src = it.next();
				File file = new File(dir2, num++ + ".jpg");
				if (file.exists()) {
					System.out.println(file + "已存在");
					continue;
				}
				URL url = new URL(src);
				BufferedInputStream biStream = new BufferedInputStream(url.openStream());
				BufferedOutputStream boStream = new BufferedOutputStream(new FileOutputStream(file));

				System.out.println(title + ":" + src + "开始下载...");

				byte[] buf = new byte[1024];
				int len;
				while ((len = biStream.read(buf)) != -1) {
					boStream.write(buf, 0, len);
				}
				boStream.close();
				biStream.close();
				System.out.println(title + ":" + src + "下载完毕");
			} catch (Exception e) {
				System.out.println("连接失败，跳过当前文件");
				num--;
				continue;
			}
		}

	}

	private void initPages() throws IOException {
		try {
			URL url = new URL(pre_main + ".html");
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;
			while ((line = in.readLine()) != null) {
				Matcher matcher = page_pattern.matcher(line);
				if (matcher.find()) {
					pages = Integer.parseInt(matcher.group().replaceAll(page_regex, "$1"));
					return;
				}
			}
		} catch (Exception e) {
			pages = 0;
			return;
		}
	}

	public void initSrcs() throws IOException {
		URL url = null;
		for (int i = 1; i <= pages; i++) {
			try {
				String url_str = pre_main;
				if (i != 1) {
					url_str = url_str + "_" + i;
				}
				url = new URL(url_str + ".html");
				BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
				String line;
				while ((line = in.readLine()) != null) {
					Matcher matcher = pattern1.matcher(line);
					if (matcher.find()) {
						Matcher matcher2 = pattern2.matcher(matcher.group());
						if (matcher2.find()) {
							String src_str = matcher2.group();
							srcs.add(src_str);
							// System.out.println( src_str + "添加成功" );
						}
					}
				}
			} catch (Exception e) {
				System.out.println("已跳过" + url);
				continue;
			}
		}
	}
}
