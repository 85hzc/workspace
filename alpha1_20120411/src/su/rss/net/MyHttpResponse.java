package su.rss.net;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

public class MyHttpResponse {
	/**
	 * HTTP头
	 */
	Header[] mHeaders;
	
	/**
	 * HTTP BODY
	 */
	HttpEntity mEntity;
	
	/**
	 * 返回码
	 */
	int mResponseCode;
	
	/**
	 * 设置头
	 * @param header
	 */
	public void setHeaders(Header[] header) {
		this.mHeaders = header;
	}
	
	/**
	 * 获取头
	 * @return
	 */
	public Header[] getHeaders() {
		return this.mHeaders;
	}
	
	/**
	 * 设置body
	 * @param body
	 */
	public void setBody(HttpEntity entity) {
		this.mEntity = entity;
	}
	
	/**
	 * 获取body
	 * @return
	 */
	public HttpEntity getBody() {
		return this.mEntity;
	}
	
	/**
	 * 设置返回码 
	 * @param code
	 */
	public void setResponseCode(int code) {
		this.mResponseCode = code;
	}
	
	/**
	 * 获取返回码
	 * @return
	 */
	public int getResponseCode() {
		return this.mResponseCode;
	}
}
