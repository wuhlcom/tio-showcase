package org.tio.examples.im.service.img2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tanyaowu
 * 2017年5月15日 下午5:54:10
 */
public class WebsitList {
	private static Logger log = LoggerFactory.getLogger(WebsitList.class);

	// http://www.169bb.com/xingganmeinv/list_1_1.html
	// ^[u4E00-u9FA5a-zA-Z]{2,}$
	private static String title_regex2 = "[\u4e00-\u9fa5\\w\\-]*[\u4e00-\u9fa5][\u4e00-\u9fa5\\w\\-]*";
	private static Pattern title_pattern2 = Pattern.compile(title_regex2);

	private String pre_url;
	int begin, end;
	int num;
	Pattern pattern1, pattern2, title_pattern1;
	LinkedHashMap<String, String> urls = new LinkedHashMap<>();

	public WebsitList(String url, int num, int begin, int end, String regex1, String regex2, String title_regex1) {
		// 当url="http://www.169bb.com/wangyouzipai/",num=2,total=351
		this.begin = begin;
		this.end = end;
		this.num = num;
		pre_url = url;// http://www.169bb.com/wangyouzipai/list_2_
		pattern1 = Pattern.compile(regex1);
		pattern2 = Pattern.compile(regex2);
		title_pattern1 = Pattern.compile(title_regex1);
	}

	public int getTotal() {
		return end;
	}

	public void initFirstUrls() throws IOException {
		URL url = new URL(pre_url + "list_" + num + "_1.html");
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;
			while ((line = in.readLine()) != null) {
				matchAll(line);
			}
		} catch (Exception e) {
			return;
		}
	}

	public void initUrls() throws IOException {
		// initFirstUrls();
		URL url = null;
		for (int i = begin; i <= end; i++) {
			try {
				if (i != 1) {
					url = new URL(pre_url + "list_" + num + "_" + i + ".html");
				} else {
					url = new URL(pre_url);
				}
				BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
				String line;
				while ((line = in.readLine()) != null) {
					matchAll(line);
				}
			} catch (Exception e) {
				System.out.println("已跳过" + url);
				continue;
			}
		}

	}

	private void matchAll(String line) {
		String url_str, title;
		Matcher matcher1 = pattern1.matcher(line);
		Matcher title_matcher1 = title_pattern1.matcher(line);
		String match, title_match;
		while (matcher1.find()) {
			match = matcher1.group();
			Matcher matcher2 = pattern2.matcher(match);
			if (matcher2.find()) {
				if (title_matcher1.find()) {
					title_match = title_matcher1.group();
					Matcher title_matcher2 = title_pattern2.matcher(title_match);
					if (title_matcher2.find()) {
						url_str = matcher2.group();
						title = title_matcher2.group();
						urls.put(url_str, title);
						System.out.println("添加成功：" + title + url_str);
					}
				}
			}
		}
	}

	public void setTotal(int total) {
		this.end = total;
	}
}
