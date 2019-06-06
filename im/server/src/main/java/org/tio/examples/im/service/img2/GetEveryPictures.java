package org.tio.examples.im.service.img2;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tanyaowu
 * 2017年5月15日 下午5:52:51
 */
public class GetEveryPictures {
	private static Logger log = LoggerFactory.getLogger(GetEveryPictures.class);

	public static void main(String[] args) throws InterruptedException {
		//此处可修改图片存放位置，默认为d盘下的pictures文件夹
		File dir = new File("d:\\pictures\\");

		/**************************************************/
		// http://www.mmonly.cc/mmtp/xgmn/ : 10 : 169
		// http://www.mmonly.cc/mmtp/swmn/ : 11 : 53
		// http://www.mmonly.cc/mmtp/hgmn/ : 12 : 23
		// http://www.mmonly.cc/mmtp/wgmv/ 51
		// http://www.mmonly.cc/mmtp/bjnmn/ 33
		// http://www.mmonly.cc/mmtp/nymn/ 59
		// http://www.mmonly.cc/mmtp/qcmn/ 80
		// http://www.mmonly.cc/mmtp/ctmn/ 28
		// http://www.mmonly.cc/mmtp/mnmx/ 90
		// http://www.mmonly.cc/mmtp/jpmn/ 30

		int[] pages = { 169, 53, 23, 51, 33, 59, 80, 28, 90, 30 };
		String url_str = "http://www.mmonly.cc/mmtp/";
		String[] indexname = { "xgmn", "swmn", "hgmn", "wgmv", "bjnmn", "nymn", "qcmn", "ctmn", "mnmx", "jpmn", };
		int no;
		String[] regex = { "http://www\\.mmonly\\.cc/mmtp/[a-zA-Z]+/\\d+\\.html\"><img", "http://www\\.mmonly\\.cc/mmtp/[a-z]+/\\d+" };
		String title_regex = "alt=\"[\\u4E00-\\u9FA5\\w\\s\\-]+\"\\ssrc=\"";
		String[] picture_regex = { "src=\"http://t1\\.mmonly\\.cc/uploads/.+\\.jpg\" /></a></p>", "http://t1\\.mmonly\\.cc/uploads/.+\\.jpg" };

		for (int i = 0; i < indexname.length; i++) {
			String index = indexname[i];
			String url = url_str + index + "/";
			no = 10 + i;
			File dir_file = new File(dir, index);
			int page = pages[i];
			for (int j = 1; j <= page; j++) {
				Task task = new Task(dir_file, url, no, regex, title_regex, picture_regex, j, j);
				new Thread(task).start();
				if (j % 10 == 0) {
					Thread.sleep(200);
				}
			}
			// Thread.sleep( 60000 );
		}
	}
}
