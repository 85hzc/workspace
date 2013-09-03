package su.rss.net;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

public class HttpBase {

	public static int get(String url, Map<String, String> headers,
			MyHttpResponse result) {
		try {
			HttpGet request = new HttpGet(url);
			setHeader(request, headers);
			HttpResponse response = HttpManager.execute(request);
			int code = response.getStatusLine().getStatusCode();
			if (code == HttpStatus.SC_OK) {
				if (result != null) {
					result.setBody(response.getEntity());
					Header[] h = response.getAllHeaders();
					result.setHeaders(h);
				}
			} else {
				result.setHeaders(null);
				result.setBody(null);
			}

			result.setResponseCode(code);
			return code;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}

		return -1;
	}

	public static int post(String url, String params,
			Map<String, String> headers, MyHttpResponse result) {
		HttpPost request = new HttpPost(url);

		setHeader(request, headers);

		try {
			if (params != null) {
				request.setEntity(new StringEntity(params, HTTP.UTF_8));
			}
			HttpResponse response = HttpManager.execute(request);
			int code = response.getStatusLine().getStatusCode();
			if (code == HttpStatus.SC_OK) {
				if (result != null) {
					result.setBody(response.getEntity());
					Header[] h = response.getAllHeaders();
					result.setHeaders(h);
				}
			} else if (result != null) {
				result.setHeaders(null);
				result.setBody(null);
			}

			if (result != null) {
				result.setResponseCode(code);
			}

			return code;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}

		return -1;
	}

	private static void setHeader(HttpRequest request,
			Map<String, String> headers) {
		if (headers == null)
			return;

		Set<String> setHead = headers.keySet();
		Iterator<String> iteratorHead = setHead.iterator();
		while (iteratorHead.hasNext()) {
			String headName = iteratorHead.next();
			String headValue = headers.get(headName);
			request.setHeader(headName, headValue);
		}
	}
}
