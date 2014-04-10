package cn.koolcloud.pos.net;


import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.util.Log;

/**
 * connect handler
 * 
 * @author ttlu
 * 
 */
public final class HttpConn {

	public static final String TYPE_WIFI = "WIFI";
	private static final String HTTPS_URL_HEADER = "https://";
	private static final String DEFAULT_HTTPS_PORT = "443";

	private static final int CONNECTION_TIMEOUT = 10000;
	private static final int WAIT_DATA_TIMEOUT = 60000;
	public static final int CACHE_BUFFER_SIZE = 1024;

	private static HttpClient httpClient;
	private static ClientConnectionManager connManager;
	private static Context context;
	public static final byte CMNET_TYPE = 1;
	public static final byte CMWAP_TYPE = 2;
	public static final byte WIFI_TYPE = 3;

	private static byte connType = CMNET_TYPE;

	private boolean is_connect;
	private boolean uploadFile;
	private ConnParams parameters;

	private int responseCode;
	private byte[] responseData;
	private Header[] responseHeaders;
	
	// custom code,which is not used in httpStatus
	static final int RESPONSECODE_EXCEPTION_TIMEOUT = 149;
	static final int RESPONSECODE_EXCEPTION_DEFAULT = 150;
	
	private static final String TAG = "HttpConn";
	
	public static void setContext(Context context) {
		HttpConn.context = context;
	}

	/**
	 * Initialize the network connection
	 * 
	 * @param parameters for connection
	 */
	public HttpConn(ConnParams parameters) {
		this.parameters = parameters;
		if (httpClient == null) {
			HttpParams params = new BasicHttpParams();
			ConnManagerParams.setMaxTotalConnections(params, 20);
			HttpConnectionParams.setConnectionTimeout(params,
					CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(params, WAIT_DATA_TIMEOUT);
			HttpConnectionParams.setSocketBufferSize(params, 8192);
			HttpClientParams.setRedirecting(params, true);

			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 8080));
			schemeRegistry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			
			schemeRegistry.register(new Scheme("https",
					new EasySSLSocketFactory(), 9025));
			schemeRegistry.register(new Scheme("https",
					new EasySSLSocketFactory(), 8443));
			schemeRegistry.register(new Scheme("https",
					new EasySSLSocketFactory(), 10080));
			schemeRegistry.register(new Scheme("https",
					new EasySSLSocketFactory(), 443));
			connManager = new ThreadSafeClientConnManager(params,
					schemeRegistry);
			httpClient = new DefaultHttpClient(connManager, params);
		}
		addPortNumberForHttps();
	}

	/**
	 * excute connecting if cmnet fails,switch to cmwap,and vice versa
	 * 
	 * @return 1:fail; 0:success; -1:no connection
	 */
	public int excute() {
		int result = this.connect();
		if (!this.is_connect && result != -1 && connType != WIFI_TYPE) {
			if (connType == CMNET_TYPE) {
				connType = CMWAP_TYPE;
			} else {
				connType = CMNET_TYPE;
			}
			result = this.connect();
			if (!this.is_connect && result != -1){
				resetConnType();
			}
		}
		return result;
	}

	/**
	 * @return 1:fail; 0:success; -1:no connection
	 */
	private int connect() {

		int result = 1;
		
		if (checkConnProxy() != 0) {

			return -1;
		}

		DataInputStream dis = null;
		ByteArrayOutputStream baos = null;

		try {
			HttpPost request = new HttpPost(parameters.getUrl());

			request.setHeader("Connection", "Keep-Alive");

			HashMap<String, String> headers = parameters.getHeaders();
			if (headers != null) {
				Iterator<String> keys = headers.keySet().iterator();
				while (keys.hasNext()) {
					String key = keys.next();
					request.addHeader(key, headers.get(key));
				}
			}
			

			request.setHeader("Content-Type",
					"application/x-www-form-urlencoded; charset=utf-8");
			request.setEntity(new StringEntity(parameters.getPostData(),"UTF-8"));
		
			Log.d(TAG, "before connect connType : " + connType + "(CMNET_TYPE is 1, CMWAP_TYPE is 2, WIFI_TYPE is 3)");
			
			HttpResponse httpResponse = null;
			try {
				httpResponse = httpClient.execute(request);
			} catch (NullPointerException e) {
				/*
				 * In the ctwap network,if the first connection after long time no connect is in "post" network 
				 * NullPointerException will be thrown.Here are second reconnection, which can successfully
				 */
				httpResponse = httpClient.execute(request);
			}
			this.is_connect = true;
			// get responsecode
			this.responseCode = httpResponse.getStatusLine()
					.getStatusCode();

			if (responseCode != HttpStatus.SC_OK
					&& responseCode != HttpStatus.SC_PARTIAL_CONTENT) {
				Log.i("uppay", "http response status code: " + responseCode);
				return 1;
			}

			responseHeaders = httpResponse.getAllHeaders();
			
			// read response data
			dis = new DataInputStream(httpResponse.getEntity().getContent());
			
			baos = new ByteArrayOutputStream();

			int len = -1;

			byte[] cache = new byte[CACHE_BUFFER_SIZE];
			int curSize = 0;
			while (true) {
				try {
					len = dis.read(cache);
//					Log.d("UP", "len=" + len);
					// Samsung mobile phone may receive 2 bytes Redundant data 0x0D0A
					if (len < 0
							|| (len == 2 && cache[0] == 0x0D && cache[1] == 0x0A)) {
						break;
					}
				} catch (IOException ie) {
					break;
				}

				baos.write(cache, 0, len);

				curSize += len;
				cache = new byte[CACHE_BUFFER_SIZE];
			}

			byte[] responseData = baos.toByteArray();
			if (responseData != null) {
				this.responseData = responseData;
			}
			result = 0;
			
		} catch (ConnectTimeoutException e) {
			e.printStackTrace();
			this.responseCode = RESPONSECODE_EXCEPTION_TIMEOUT;
		} catch (Exception e) {
			e.printStackTrace();
			if(!is_connect){
				this.responseCode = RESPONSECODE_EXCEPTION_DEFAULT;
			}
		} finally {
			try {
				if (baos != null) {
					baos.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (dis != null) {
					dis.close();
				}
			} catch (Exception e) {
			}
		}
		
		Log.d(TAG, "after connect connType : " + connType + "(CMNET_TYPE is 1, CMWAP_TYPE is 2, WIFI_TYPE is 3)");
		return result;
	}

	/**
	 * add default port number for https
	 */
	private void addPortNumberForHttps() {
		String url = parameters.getUrl().toLowerCase();
		if (!url.startsWith(HTTPS_URL_HEADER)
				|| url.matches("https://.*:\\d+.*")) {
			return;
		}

		int secondSlashIndex = url.indexOf("/", HTTPS_URL_HEADER.length());
		if (secondSlashIndex == -1) {
			parameters.setUrl(url + DEFAULT_HTTPS_PORT);
		} else {
			parameters.setUrl(url.substring(0, secondSlashIndex) + ":"
					+ DEFAULT_HTTPS_PORT + url.substring(secondSlashIndex));
		}
	}

	private void resetConnType(){
		connType = CMNET_TYPE;
	}
	/**
	 * Test connection cmnet/cmwap, set proxy
	 * 
	 * @return 0:Network available;  -1:Network not available
	 */
	private int checkConnProxy() {
		NetworkInfo mobNetInfo = ((ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();
		if (mobNetInfo == null) {
			return -1;
		} else if (TYPE_WIFI.equals(mobNetInfo.getTypeName())) {
			connType = WIFI_TYPE;
			httpClient.getParams().removeParameter(
					ConnRoutePNames.DEFAULT_PROXY);
			return 0;
		} else if (connType == WIFI_TYPE){
			resetConnType();
		}
		
		httpClient.getParams().removeParameter(
				ConnRoutePNames.DEFAULT_PROXY);
		
		if (connType == CMWAP_TYPE) {
			HttpHost proxy = new HttpHost("10.0.0.172", 80);
			httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
					proxy);
		} else {
			if (mobNetInfo.getSubtypeName().toLowerCase().contains("cdma")
					|| (mobNetInfo.getExtraInfo() != null && mobNetInfo.getExtraInfo().contains("wap"))) {
				String proxyHost = Proxy.getDefaultHost();
				int proxyPort = Proxy.getDefaultPort();
				if (proxyHost != null && proxyPort != -1) {
					HttpHost proxy = new HttpHost(proxyHost, proxyPort);
					httpClient.getParams().setParameter(
							ConnRoutePNames.DEFAULT_PROXY, proxy);
				}
			} else {
				httpClient.getParams().removeParameter(
						ConnRoutePNames.DEFAULT_PROXY);
			}
		}
		return 0;
	}

	public Header[] getResponseHeaders() {
		return responseHeaders;
	}

	public byte[] getResponseData() {
		return responseData;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setUploadFile(boolean uploadFile) {
		this.uploadFile = uploadFile;
	}

	public boolean isUploadFile() {
		return uploadFile;
	}
}