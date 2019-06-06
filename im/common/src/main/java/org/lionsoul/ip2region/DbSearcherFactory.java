package org.lionsoul.ip2region;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tanyaowu
 * 2017年5月16日 下午10:15:04
 */
public class DbSearcherFactory {
	private static Logger log = LoggerFactory.getLogger(DbSearcherFactory.class);

	private static DbSearcher dbSearcher = null;

	public static DbSearcher getDbSearcher(String dbpath) {

		try {
			if (dbSearcher == null) {
				synchronized (log) {
					if (dbSearcher == null) {
						DbConfig config = new DbConfig();
						dbSearcher = new DbSearcher(config, dbpath);
					}
				}
			}
			return dbSearcher;
		} catch (Exception e) {
			log.error(e.toString(), e);
			return null;
		}
	}

	/**
	 * @param args
	 * @author tanyaowu
	 */
	public static void main(String[] args) {

	}

	/**
	 *
	 * @author tanyaowu
	 */
	public DbSearcherFactory() {
	}
}
