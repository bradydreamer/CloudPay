package cn.koolcloud.pos.util;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class HttpUtil {
	private static AsyncHttpClient client = new AsyncHttpClient();
	static {
		client.setTimeout(3000); // set timeout default 5s
		client.setMaxRetriesAndTimeout(0, 3000);
	}

	/**
	 * full url to get string obj
	 * 
	 * @Title: get
	 * @Description: TODO
	 * @param urlString
	 * @param res
	 * @return: void
	 */
	public static void get(String urlString, AsyncHttpResponseHandler res) {
		client.get(urlString, res);
	}

	/**
	 * with param url
	 * 
	 * @Title: get
	 * @Description: TODO
	 * @param urlString
	 * @param params
	 * @param res
	 * @return: void
	 */
	public static void get(String urlString, RequestParams params, AsyncHttpResponseHandler res) {
		client.get(urlString, params, res);
	}

	/**
	 * no param to get json obj or array
	 * 
	 * @Title: get
	 * @Description: TODO
	 * @param urlString
	 * @param res
	 * @return: void
	 */
	public static void get(String urlString, JsonHttpResponseHandler res) {
		client.get(urlString, res);
	}

	/**
	 * To get json or array with param
	 * 
	 * @Title: get
	 * @Description: TODO
	 * @param urlString
	 * @param params
	 * @param res
	 * @return: void
	 */
	public static void get(String urlString, RequestParams params, JsonHttpResponseHandler res) {
		client.get(urlString, params, res);
	}

	/**
	 * download data and return byte data
	 * 
	 * @Title: get
	 * @Description: TODO
	 * @param uString
	 * @param bHandler
	 * @return: void
	 */
	public static void get(String uString, BinaryHttpResponseHandler bHandler) {
		client.get(uString, bHandler);
	}

	public static AsyncHttpClient getClient() {
		return client;
	}
}