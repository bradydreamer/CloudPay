package cn.koolcloud.pos.net;

import java.util.HashMap;

/**
 * params for httpconnect
 * 
 * @author ttlu
 * 
 */
public final class ConnParams {
	private String url; 
	private HashMap<String, String> headers;
	private String postData;

	public ConnParams(String url,HashMap<String, String> headers, String postData) {
		this.url = url;
		this.headers = headers;
		this.postData = postData;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String urlStr) {
		this.url = urlStr;
	}

	public HashMap<String, String> getHeaders() {
		return headers;
	}

	public String getPostData() {
		return postData;
	}
}
