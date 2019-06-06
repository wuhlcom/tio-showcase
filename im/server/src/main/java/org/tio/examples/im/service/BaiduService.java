package org.tio.examples.im.service;

/**
 * @author tanyaowu
 * 2017年6月2日 上午10:54:57
 */
public class BaiduService {
//	private static Logger log = LoggerFactory.getLogger(BaiduService.class);
//
//	private static Cache<String, Object> cache = com.xiaoleilu.hutool.cache.CacheUtil.newLRUCache(5000);
//
//	/**
//	 * @param args
//	 * @author tanyaowu
//	 */
//	public static void main(String[] args) {
//		Searcher searcher = new JSoupBaiduSearcher();
//		SearchResult searchResult = searcher.search("t-io", 1);
//		List<Webpage> webpages = searchResult.getWebpages();
//		if (webpages != null) {
//			int i = 1;
//			log.info("搜索结果 当前第 " + searchResult.getPage() + " 页，页面大小为：" + searchResult.getPageSize() + " 共有结果数：" + searchResult.getTotal());
//			for (Webpage webpage : webpages) {
//				log.info("搜索结果 " + i++ + " ：");
//				log.info("标题：" + webpage.getTitle());
//				log.info("URL：" + webpage.getUrl());
//				log.info("摘要：" + webpage.getSummary());
//				log.info("正文：" + webpage.getContent());
//				log.info("");
//			}
//		} else {
//			log.error("没有搜索到结果");
//		}
//	}
//
//	/**
//	 * 返回null表示没有搜索到结果
//	 * @param initText
//	 * @return
//	 * @author tanyaowu
//	 */
//	public static String replaceToSearchHtml(String initText) {
//
//		try {
//			String lowercaseText = StringUtils.lowerCase(initText);
//			Object obj = cache.get(lowercaseText);
//			if (obj != null) {
//				return obj.toString();
//			}
//
//			String ret = null;
//			Searcher searcher = new JSoupBaiduSearcher();
//			SearchResult searchResult = searcher.search(initText, 1);
//			List<Webpage> webpages = searchResult.getWebpages();
//			if (webpages != null) {
//				ret = "";
//				int i = 1;
//				log.info("搜索结果 当前第 " + searchResult.getPage() + " 页，页面大小为：" + searchResult.getPageSize() + " 共有结果数：" + searchResult.getTotal());
//				//			ret += "<div style='color:#077d11;padding:4px;border:1px solid #077d11;border-radius:5px;margin:4px 0px;'>";
//
//				for (Webpage webpage : webpages) {
//					//				log.info("搜索结果 " + (i++) + " ：");
//					//				log.info("标题：" + webpage.getTitle());
//					//				log.info("URL：" + webpage.getUrl());
//					//				log.info("摘要：" + webpage.getSummary());
//					//				log.info("正文：" + webpage.getContent());
//					//				log.info("");
//
//					String title = webpage.getTitle();
//					Pattern p = Pattern.compile(initText, Pattern.CASE_INSENSITIVE);//不区分大小写
//					title = ReUtil.replaceAll(title, p, "<span style='color:red'>" + initText + "</span>");
//
//					ret += "<div style='padding:4px 0px;margin:4px 0px'>";
//					ret += "<a href='" + webpage.getUrl() + "' target='_blank'>" + title + "</a>";
//					ret += "</div>";
//				}
//				//			ret += "</div>";
//
//				cache.put(lowercaseText, ret);
//				return ret;
//			} else {
//				log.error("【{}】没有搜索到结果", initText);
//				return null;
//			}
//		} catch (Exception e) {
//			log.error(e.toString(), e);
//			return null;
//		}
//
//	}
//
//	/**
//	 *
//	 * @author tanyaowu
//	 */
//	public BaiduService() {
//	}
}
