package su.rss.net;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

public class MyHttpResponse {
	/**
	 * HTTPͷ
	 */
	Header[] mHeaders;
	
	/**
	 * HTTP BODY
	 */
	HttpEntity mEntity;
	
	/**
	 * ������
	 */
	int mResponseCode;
	
	/**
	 * ����ͷ
	 * @param header
	 */
	public void setHeaders(Header[] header) {
		this.mHeaders = header;
	}
	
	/**
	 * ��ȡͷ
	 * @return
	 */
	public Header[] getHeaders() {
		return this.mHeaders;
	}
	
	/**
	 * ����body
	 * @param body
	 */
	public void setBody(HttpEntity entity) {
		this.mEntity = entity;
	}
	
	/**
	 * ��ȡbody
	 * @return
	 */
	public HttpEntity getBody() {
		return this.mEntity;
	}
	
	/**
	 * ���÷����� 
	 * @param code
	 */
	public void setResponseCode(int code) {
		this.mResponseCode = code;
	}
	
	/**
	 * ��ȡ������
	 * @return
	 */
	public int getResponseCode() {
		return this.mResponseCode;
	}
}
