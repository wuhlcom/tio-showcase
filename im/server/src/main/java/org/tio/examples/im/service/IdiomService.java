package org.tio.examples.im.service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.examples.im.vo.IdiomVo;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;

/**
 * @author tanyaowu
 * 2017年6月8日 上午9:52:26
 */
public class IdiomService {
	private static Logger log = LoggerFactory.getLogger(IdiomService.class);

	/**
	 * key :  首字母
	 * value :
	 */
	private static Map<String, Map<String, IdiomVo>> firstMap;

	private static final String key_pinyin = "拼音：";

	private static final String key_paraphrase = "释义：";

	public static final IdiomService instance = new IdiomService();

	/**
	 *
	 * @param idiom
	 * @return
	 * @author tanyaowu
	 */
	public static IdiomVo get(String idiom) {
		if (StringUtils.isBlank(idiom)) {
			return null;
		}
		String first = idiom.substring(0, 1);
		Map<String, IdiomVo> idiomMap = firstMap.get(first);
		if (idiomMap == null) {
			return null;
		}
		IdiomVo idiomVo = idiomMap.get(idiom);
		return idiomVo;
	}

	/**
	 *
	 * @param first
	 * @return
	 * @author tanyaowu
	 */
	public static Map<String, IdiomVo> getByFirst(String first) {
		if (StringUtils.isBlank(first)) {
			return null;
		}
		Map<String, IdiomVo> idiomMap = firstMap.get(first);
		return idiomMap;
	}

	private static void init() {
		firstMap = new HashMap<>();
		String rootDirStr = FileUtil.getAbsolutePath("classpath:config/idiom/");
		File rootDir = new File(rootDirStr);
		File[] files = rootDir.listFiles();
		int count = 0;
		if (files != null) {

			//			List<String> alllines = new ArrayList<>();
			for (File file : files) {
				List<String> lines = FileUtil.readLines(file, "utf-8");
				for (String line : lines) {
					try {
						IdiomVo idiomVo = parseLine(line);
						if (idiomVo != null) {
							Map<String, IdiomVo> idiomMap = firstMap.get(idiomVo.getFirst());
							if (idiomMap == null) {
								idiomMap = new HashMap<>();
								firstMap.put(idiomVo.getFirst(), idiomMap);

							}
							idiomMap.put(idiomVo.getIdiom(), idiomVo);
							//							alllines.add(line);
							count++;
						}
					} catch (Exception e) {
						log.error(line, e);
					}
				}
			}
			//			String xx = new File(rootDir, "all.txt").getAbsolutePath();
			//			FileUtil.writeLines(alllines, xx, "utf-8");
		}
		log.error("一共{}个成语", count);
	}

	/**
	 * @param args
	 * @author tanyaowu
	 */
	public static void main(String[] args) {
		//new IdiomService();
	}

	/**
	 * 成语接龙
	 * @param firstIdiom 成语，譬如：张冠李戴
	 * @return
	 * @author tanyaowu
	 */
	public static IdiomVo next(String firstIdiom) {
		if (StringUtils.isBlank(firstIdiom)) {
			return null;
		}

		String end = StringUtils.substring(firstIdiom, firstIdiom.length() - 1);
		Map<String, IdiomVo> idiomMap = getByFirst(end);

		if (idiomMap == null) {
			return null;
		}

		int index = 0;
		if (idiomMap.size() > 1) {
			index = RandomUtil.randomInt(0, idiomMap.size() - 1);
		}

		Set<Entry<String, IdiomVo>> set = idiomMap.entrySet();
		int i = 0;
		for (Entry<String, IdiomVo> entry : set) {
			if (index == i++) {
				return entry.getValue();
			}
		}

		return null;
	}

	private static IdiomVo parseLine(String line) {
		line = StringUtils.trim(line);
		if (StringUtils.isBlank(line)) {
			return null;
		}

		int pinyinIndex = line.indexOf(key_pinyin);
		if (pinyinIndex <= 0) {
			return null;
		}

		int paraphraseIndex = line.indexOf(key_paraphrase);
		if (paraphraseIndex <= 0) {
			return null;
		}

		if (paraphraseIndex < pinyinIndex) {
			return null;
		}

		String idiom = StringUtils.substring(line, 0, pinyinIndex).trim();
		if (idiom.length() < 4) {
			return null;
		}
		String first = idiom.substring(0, 1);
		String pinyin = StringUtils.substring(line, pinyinIndex + key_pinyin.length(), paraphraseIndex).trim();
		String paraphrase = StringUtils.substring(line, paraphraseIndex + key_paraphrase.length()).trim();

		IdiomVo idiomVo = new IdiomVo(idiom, first, pinyin, paraphrase);
		return idiomVo;
	}

	/**
	 * 从库中随机取一个成语
	 * @return
	 * @author tanyaowu
	 */
	public static IdiomVo random() {
		int index = RandomUtil.randomInt(0, Math.min(1000, firstMap.size() - 1));

		Set<Entry<String, Map<String, IdiomVo>>> set = firstMap.entrySet();
		int i = 0;
		for (Entry<String, Map<String, IdiomVo>> entry : set) {
			if (index == i++) {
				return random(entry.getKey());
			}
		}

		return null;
	}

	/**
	 * 根据首字，从库中随机取一个成语
	 * @param firstWord
	 * @return
	 * @author tanyaowu
	 */
	public static IdiomVo random(String firstWord) {
		if (StringUtils.isBlank(firstWord)) {
			return null;
		}

		Map<String, IdiomVo> idiomMap = getByFirst(firstWord.substring(0, 1));

		if (idiomMap == null) {
			return null;
		}

		int index = 0;
		if (idiomMap.size() > 1) {
			index = RandomUtil.randomInt(0, idiomMap.size() - 1);
		}

		Set<Entry<String, IdiomVo>> set = idiomMap.entrySet();
		int i = 0;
		for (Entry<String, IdiomVo> entry : set) {
			if (index == i++) {
				return entry.getValue();
			}
		}

		return null;
	}

	/**
	 *
	 * @author tanyaowu
	 */
	private IdiomService() {
		init();
	}
}
