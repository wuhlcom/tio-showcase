package org.tio.examples.im.service;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.examples.im.server.ImServerStarter;

import cn.hutool.core.io.FileUtil;
import cn.hutool.dfa.WordTree;

/**
 * @author tanyaowu
 * 2017年5月29日 下午2:50:41
 */
public class BadWordService {
	public static interface BadWordHandler {
		/**
		 *
		 * @param initText
		 * @param badWord
		 * @return
		 * @author tanyaowu
		 */
		String replace(String initText, String badWord);
	}

	public static class BaiduBadWordHandler implements BadWordHandler {

		public static BaiduBadWordHandler instance = new BaiduBadWordHandler();

		private BaiduBadWordHandler() {
		}

		/**
		 * @param initText
		 * @param badWord
		 * @return
		 * @author tanyaowu
		 */
		@Override
		public String replace(String initText, String badWord) {
			try {
				//<span style='color:#ee3344;padding:4px;border:1px solid #ee3344;border-radius:5px;margin:4px 4px;'><a href='http://www.gov.cn' target='_blank'>此处为敏感词</a></span>
				String baidu = "https://www.baidu.com/s?word=" + URLEncoder.encode(badWord, "utf-8");//<a href='http://www.gov.cn' target='_blank'>此处为敏感词</a>
				String repStr = "<a style='padding:4px;border-bottom:1px solid #ee3344;border-radius:0px;margin:0px 4px;' href='" + baidu + "' target='_blank'>此处为敏感词</a>";
				return StringUtils.replaceAll(initText, badWord, repStr);
			} catch (UnsupportedEncodingException e) {
				log.error(e.toString(), e);
				return initText;
			}
		}
	}

	private static Logger log = LoggerFactory.getLogger(BadWordService.class);

	public static final WordTree wordTree = new WordTree();

	public static void init() {
		String rootDirStr = FileUtil.getAbsolutePath("classpath:config/dict/");
		File rootDir = new File(rootDirStr);
		File[] files = rootDir.listFiles();
		int count = 0;
		if (files != null) {

			for (File file : files) {
				List<String> lines = FileUtil.readLines(file, "utf-8");
				for (String line : lines) {
					wordTree.addWord(line);
					count++;
					//log.error(line);
				}
			}
		}
		log.error("一共{}个敏感词", count);
	}

	/**
	 * @param args
	 * @author tanyaowu
	 */
	public static void main(String[] args) {
		init();
		replaceWithDefaultReplace("习近平dddd|sss");
	}

	public static String replaceBadWord(String initText, BadWordHandler badWordHandler, Object logstr) {
		List<String> list = wordTree.matchAll(initText);
		if (list != null && list.size() > 0) {
			String ret = initText;
			for (String word : list) {
				ret = badWordHandler.replace(ret, word);//StringUtils.replaceAll(ret, word, replaceText);
			}
			if (logstr != null) {
				log.error("{}, 找到敏感词，原文:【{}】，替换后的:【{}】", logstr, initText, ret);
			} else {
				log.error("找到敏感词，原文:【{}】，替换后的:【{}】", initText, ret);
			}
			return ret;
		}
		return null;
	}

	public static String replaceBadWord(String initText, String replaceText) {
		return replaceBadWord(initText, replaceText, null);
	}

	/**
	 * 如果没匹配到就返回null，否则返回替换后的string
	 * @param initText
	 * @param replaceText
	 * @param logstr
	 * @return
	 * @author tanyaowu
	 */
	public static String replaceBadWord(String initText, String replaceText, Object logstr) {
		List<String> list = wordTree.matchAll(initText);
		if (list != null && list.size() > 0) {
			String ret = initText;
			for (String word : list) {
				ret = StringUtils.replaceAll(ret, word, replaceText);
			}
			if (logstr != null) {
				log.error("{}, 找到敏感词，原文:【{}】，替换后的:【{}】", logstr, initText, ret);
			} else {
				log.error("找到敏感词，原文:【{}】，替换后的:【{}】", initText, ret);
			}
			return ret;
		}
		return null;
	}

	public static String replaceWithDefaultReplace(String initText) {
		return replaceBadWord(initText, ImServerStarter.conf.getString("dft.badword.replaceText"));
	}

	public static String replaceWithDefaultReplace(String initText, String logstr) {
		return replaceBadWord(initText, ImServerStarter.conf.getString("dft.badword.replaceText"), logstr);
	}

	/**
	 *
	 * @author tanyaowu
	 */
	public BadWordService() {
	}
}
