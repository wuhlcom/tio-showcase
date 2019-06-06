package org.tio.examples.im.common.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.examples.im.common.ImPacket;
import org.tio.examples.im.common.http.HttpConst.RequestBodyFormat;

//import nl.basjes.parse.useragent.UserAgent;

/**
 *
 * @author tanyaowu
 *
 */
public class HttpRequestPacket extends ImPacket {

	public static class RequestLine {
		private String method;
		private String requestUrl;
		private String queryStr; //譬如http://www.163.com?name=tan&id=789，那些此值就是name=tan&id=789
		private String version;
		private String initStr;

		/**
		 * @return the initStr
		 */
		public String getInitStr() {
			return initStr;
		}

		/**
		 * @return the method
		 */
		public String getMethod() {
			return method;
		}

		/**
		 * @return the queryStr
		 */
		public String getQueryStr() {
			return queryStr;
		}

		/**
		 * @return the requestUrl
		 */
		public String getRequestUrl() {
			return requestUrl;
		}

		/**
		 * @return the version
		 */
		public String getVersion() {
			return version;
		}

		/**
		 * @param initStr the initStr to set
		 */
		public void setInitStr(String initStr) {
			this.initStr = initStr;
		}

		/**
		 * @param method the method to set
		 */
		public void setMethod(String method) {
			this.method = method;
		}

		/**
		 * @param queryStr the queryStr to set
		 */
		public void setQueryStr(String queryStr) {
			this.queryStr = queryStr;
		}

		/**
		 * @param requestUrl the requestUrl to set
		 */
		public void setRequestUrl(String requestUrl) {
			this.requestUrl = requestUrl;
		}

		/**
		 * @param version the version to set
		 */
		public void setVersion(String version) {
			this.version = version;
		}
	}

	private static final long serialVersionUID = -8824360905964520811L;

	private static Logger log = LoggerFactory.getLogger(HttpRequestPacket.class);

	/**
	 * @param args
	 *
	 * @author tanyaowu
	 * 2017年2月22日 下午4:14:40
	 *
	 */
	public static void main(String[] args) {
	}

	private RequestLine requestLine = null;
	private Map<String, String> headers = null;
	/**
	 * 请求参数
	 */
	private Map<String, List<String>> params = null;
	private List<Cookie> cookies = null;
	private Map<String, Cookie> cookieMap = null;
	private int contentLength;
	private byte[] bodyBytes;
	private String bodyString;

	//	private UserAgent userAgent;
	private RequestBodyFormat bodyFormat;

	private String charset = HttpConst.CHARSET_NAME;

	/**
	 *
	 *
	 * @author tanyaowu
	 * 2017年2月22日 下午4:14:40
	 *
	 */
	public HttpRequestPacket() {
	}

	/**
	 * @return the bodyBytes
	 */
	public byte[] getBodyBytes() {
		return bodyBytes;
	}

	/**
	 * @return the bodyFormat
	 */
	public RequestBodyFormat getBodyFormat() {
		return bodyFormat;
	}

	/**
	 * @return the bodyString
	 */
	public String getBodyString() {
		return bodyString;
	}

	/**
	 * @return the charset
	 */
	public String getCharset() {
		return charset;
	}

	/**
	 * @return the bodyLength
	 */
	public int getContentLength() {
		return contentLength;
	}

	public Cookie getCookieByName(String cooiename) {
		if (cookieMap == null) {
			return null;
		}
		return cookieMap.get(cooiename);
	}

	/**
	 * @return the cookieMap
	 */
	public Map<String, Cookie> getCookieMap() {
		return cookieMap;
	}

	/**
	 * @return the cookies
	 */
	public List<Cookie> getCookies() {
		return cookies;
	}

	/**
	 * @return the headers
	 */
	public Map<String, String> getHeaders() {
		return headers;
	}

	/**
	 * @return the params
	 */
	public Map<String, List<String>> getParams() {
		return params;
	}

	//	/**
	//	 * @return the userAgent
	//	 */
	//	public UserAgent getUserAgent() {
	//		return userAgent;
	//	}
	//
	//	/**
	//	 * @param userAgent the userAgent to set
	//	 */
	//	public void setUserAgent(UserAgent userAgent) {
	//		this.userAgent = userAgent;
	//	}

	/**
	 * @return the firstLine
	 */
	public RequestLine getRequestLine() {
		return requestLine;
	}

	public void parseCookie(ChannelContext channelContext) {
		String cookieline = headers.get(HttpConst.RequestHeaderKey.Cookie);
		if (StringUtils.isNotBlank(cookieline)) {
			cookies = new ArrayList<>();
			cookieMap = new HashMap<>();
			Map<String, String> _cookiemap = Cookie.getEqualMap(cookieline);
			List<Map<String, String>> cookieListMap = new ArrayList<>();
			for (Entry<String, String> cookieMapEntry : _cookiemap.entrySet()) {
				HashMap<String, String> cookieOneMap = new HashMap<>();
				cookieOneMap.put(cookieMapEntry.getKey(), cookieMapEntry.getValue());
				cookieListMap.add(cookieOneMap);

				Cookie cookie = Cookie.buildCookie(cookieOneMap);
				cookies.add(cookie);
				cookieMap.put(cookie.getName(), cookie);
				log.error("{}, 收到cookie:{}", channelContext, cookie.toString());
			}
		}
	}

	/**
	 * @param bodyBytes the bodyBytes to set
	 */
	public void setBodyBytes(byte[] bodyBytes) {
		this.bodyBytes = bodyBytes;
	}

	/**
	 * @param bodyFormat the bodyFormat to set
	 */
	public void setBodyFormat(RequestBodyFormat bodyFormat) {
		this.bodyFormat = bodyFormat;
	}

	/**
	 * @param bodyString the bodyString to set
	 */
	public void setBodyString(String bodyString) {
		this.bodyString = bodyString;
	}

	/**
	 * @param charset the charset to set
	 */
	public void setCharset(String charset) {
		this.charset = charset;
	}

	/**
	 * @param bodyLength the bodyLength to set
	 */
	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}

	/**
	 * @param cookieMap the cookieMap to set
	 */
	public void setCookieMap(Map<String, Cookie> cookieMap) {
		this.cookieMap = cookieMap;
	}

	/**
	 * @param cookies the cookies to set
	 */
	public void setCookies(List<Cookie> cookies) {
		this.cookies = cookies;
	}

	/**
	 * 设置好header后，会把cookie等头部信息也设置好
	 * @param headers the headers to set
	 * @param channelContext
	 */
	public void setHeaders(Map<String, String> headers, ChannelContext channelContext) {
		this.headers = headers;
		if (headers != null) {
			parseCookie(channelContext);
		}
	}

	/**
	 * @param params the params to set
	 */
	public void setParams(Map<String, List<String>> params) {
		this.params = params;
	}

	/**
	 * @param requestLine the requestLine to set
	 */
	public void setRequestLine(RequestLine requestLine) {
		this.requestLine = requestLine;
	}

}
