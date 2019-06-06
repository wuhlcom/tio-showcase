package org.tio.examples.im.service.img2;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tanyaowu
 * 2017年5月15日 下午5:55:33
 */
public class ClearEmptyDirs {
	private static Logger log = LoggerFactory.getLogger(ClearEmptyDirs.class);

	static int i = 0;

	public static void clear(File dir) {
		File[] dir2 = dir.listFiles();
		for (File element : dir2) {
			if (element.isDirectory()) {
				clear(element);
			}
		}
		if (dir.isDirectory() && dir.delete()) {
			i++;
		}
		System.out.println(dir + "删除成功");

	}

	public static void main(String[] args) {
		// 文件夹清理的开始位置，默认为d:\pictures
		String dir_str = "d:\\pictures";
		File dir = new File(dir_str);
		clear(dir);
		System.out.println("清理完毕。");
		System.out.println("共删除了" + i + "个空文件夹");
	}
}
