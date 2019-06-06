package org.tio.examples.im.common.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.tio.examples.im.common.ImPacket;

/**
 *
 * @author tanyaowu
 *
 */
public class HttpResponsePacket extends ImPacket {
	private static final long serialVersionUID = 28425995820953085L;

	/**
	 * @param args
	 *
	 * @author tanyaowu
	 * 2017年2月22日 下午4:14:40
	 *
	 */
	public static void main(String[] args) {
	}

	private HttpResponseStatus httpResponseStatus = null;
	//不包含cookie的头部
	private Map<String, String> headers = null;
	private List<Cookie> cookies = null;
	private int contentLength;

	private byte[] httpResponseBody;

	/**
	 *
	 *
	 * @author tanyaowu
	 * 2017年2月22日 下午4:14:40
	 *
	 */
	public HttpResponsePacket() {
	}

	public boolean addCookie(Cookie cookie) {
		if (cookies == null) {
			synchronized (this) {
				if (cookies == null) {
					cookies = new ArrayList<>();
				}
			}
		}
		return cookies.add(cookie);
	}

	/**
	 * @return the bodyLength
	 */
	public int getContentLength() {
		return contentLength;
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
	 * @return the httpResponseBody
	 */
	public byte[] getHttpResponseBody() {
		return httpResponseBody;
	}

	/**
	 * @return the httpResponseStatus
	 */
	public HttpResponseStatus getHttpResponseStatus() {
		return httpResponseStatus;
	}

	/**
	 * @param bodyLength the bodyLength to set
	 */
	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}

	/**
	 * @param cookies the cookies to set
	 */
	public void setCookies(List<Cookie> cookies) {
		this.cookies = cookies;
	}

	/**
	 * @param headers the headers to set
	 */
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	/**
	 * @param httpResponseBody the httpResponseBody to set
	 */
	public void setHttpResponseBody(byte[] httpResponseBody) {
		this.httpResponseBody = httpResponseBody;
	}

	/**
	 * @param httpResponseStatus the httpResponseStatus to set
	 */
	public void setHttpResponseStatus(HttpResponseStatus httpResponseStatus) {
		this.httpResponseStatus = httpResponseStatus;
	}

}
