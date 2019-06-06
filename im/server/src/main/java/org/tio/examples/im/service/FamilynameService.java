package org.tio.examples.im.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;

/**
 * @author tanyaowu
 * 2017年5月29日 下午2:50:41
 */
public class FamilynameService {
	private static Logger log = LoggerFactory.getLogger(FamilynameService.class);

	private static final List<String> familynames = new ArrayList<>();

	private static java.util.concurrent.atomic.AtomicInteger curIndex;

	static {
		try {
			init();
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
	}

	public static void init() {
		String rootDirStr = FileUtil.getAbsolutePath("classpath:config/familyname/");
		File rootDir = new File(rootDirStr);
		File[] files = rootDir.listFiles();
		int count = 0;
		if (files != null) {

			for (File file : files) {
				List<String> lines = FileUtil.readLines(file, "utf-8");
				for (String line : lines) {
					if (StringUtils.isNotBlank(line)) {
						String[] ss = StringUtils.split(line, " ");
						if (ss != null && ss.length > 0) {
							for (String familyname : ss) {
								familynames.add(familyname);
								count++;
							}
						}
					}
				}
			}
		}

		curIndex = new AtomicInteger(RandomUtil.randomInt(0, familynames.size()));
		log.error("一共{}个姓氏", count);
	}

	/**
	 * @param args
	 * @author tanyaowu
	 */
	public static void main(String[] args) {
		init();
	}

	public static String next() {
		int index = curIndex.getAndIncrement() % familynames.size();
		return familynames.get(index);
	}

	/**
	 *
	 * @author tanyaowu
	 */
	public FamilynameService() {
	}
}
