package cn.koolcloud.pos.net;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import cn.koolcloud.APDefine;
import cn.koolcloud.constant.ConstantUtils;
import cn.koolcloud.pos.ClientEngine;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.secure.SecureEngine;
import cn.koolcloud.pos.service.SecureInfo;
import cn.koolcloud.pos.util.InputStreamUtils;
import cn.koolcloud.pos.util.UtilForDataStorage;

public class NetEngine {

	private static final String TAG = "NetEngine";

	private final static String PREFERENCES_NAME_HTTPCONNECT = "httpconnet";
	private final static String PREFERENCES_KEY_TERMINAL_ID = "terminalId";

	private static final String HEADER_SESSION_ID = "X-APSessionID"
			.toLowerCase(); // sessionid
	private static final String HEADER_VERSION = "X-APVersion".toLowerCase(); // version,
																				// the
																				// new
																				// version
																				// is
																				// 1.0
	private static final String HEADER_SIGNATURE = "X-APSignature"
			.toLowerCase(); // The data checksum
	private static final String HEADER_TERMINAL_ID = "X-APTerminalID"
			.toLowerCase(); // client user ID
	private static final String HEADER_CRYPT = "X-APCrypt".toLowerCase(); // ""
																			// or
																			// 0:No
																			// Encryption
																			// 1:Encryption
	private static final String HEADER_KEY_EXCHANGE = "X-APKeyExchange"
			.toLowerCase(); // The key exchange data
	private static final String HEADER_KEY_APChannel = "X-APChannel"
			.toLowerCase();// channel id
	private static final String HEADER_KEY_APPKey = "X-APPKey".toLowerCase();// channel
																				// id

    private static final String HEADER_KEY_LANGUAGE = "Accept-Language";
    private static final String HEADER_KEY_ACCEPT = "Accept";
    private static final String HEADER_KEY_CONTENT_DISPOSITION = "Content-Disposition";

	/***
	 * 用于卡通惠的联网
	 */
	private static final String HEADER_KEY_CONTENTTYPE = "Content-Type";
	private static final String HEADER_KEY_APSIGNVERION = "X-APSignV";
	private static final String HEADER_KEY_APFORMAT = "X-APFormat";
	private static final String SPECIAL_KEY = APDefine.SPECIAL_KEY;//"9B42A9661BF9BE92975C8A07CF4E7410";//"E8988E9CE30954C72CF20BF771DBAD22";

    private static final int PARAM_ENCRYPT = 0;

	private static final int PARAM_DECRYPT = 1;
    private static final int CONNECT_RESULT_SUCCESS = 0;
	// private static final int CONNECT_RESULT_FAIL = 1;
	private static final int CONNECT_RESULT_NO_CONNECTION = -1;

	private static final String APPSERVER = APDefine.APPSERVER;

	private static final String BODY_ACTION = "action";
	private static final String BODY_TRANS_ACTION_TAG ="txn/";
	private static JSONArray reqBody;

	public static HashMap<String, String> getRequestHeader(Context context,
			Map<String, String> headerMap) {
		HashMap<String, String> requestHeaders = new HashMap<String, String>();
		requestHeaders.put(HEADER_VERSION, "1.0");
		requestHeaders.put(HEADER_TERMINAL_ID, "");
		requestHeaders.put(HEADER_SIGNATURE, "");
		requestHeaders.put(HEADER_CRYPT, "1");
		requestHeaders.put(HEADER_KEY_EXCHANGE, "");
		requestHeaders.put(HEADER_SESSION_ID, "");
		requestHeaders.put(HEADER_KEY_APChannel, "AP03");
		requestHeaders.put(HEADER_KEY_APPKey, "kc-ips01");

        //add accept application/json
        requestHeaders.put(HEADER_KEY_ACCEPT, "application/json");
//        requestHeaders.put(HEADER_KEY_ACCEPT, "application/octet-stream");

		
        //add language in header mod by Teddy --start on 4th December
        String language = Locale.getDefault().getLanguage();
        if (!TextUtils.isEmpty(language) && language.equals(ConstantUtils.LANGUAGE_CHINESE)) {
            requestHeaders.put(HEADER_KEY_LANGUAGE, "zh-CN");
        } else {
            requestHeaders.put(HEADER_KEY_LANGUAGE, "en");
        }
		//add language in header mod by Teddy --end on 4th December

		SecureEngine se = ClientEngine.engineInstance().secureEngine();
		boolean isWorktimeValid = se.isValid();
		if (!isWorktimeValid || null != headerMap.get("keyExchange")) {
			Log.d(TAG, "se.isValid() : " + isWorktimeValid);
			Log.d(TAG,
					"headerMap.get(\"keyExchange\") : "
							+ headerMap.get("keyExchange"));
			se.resetWorkKey();
			requestHeaders.put(HEADER_KEY_EXCHANGE, se.getKeyExchange());
		}

		Map<String, ?> map = UtilForDataStorage
				.readPropertyBySharedPreferences(context,
						PREFERENCES_NAME_HTTPCONNECT);
		if (null != map && map.containsKey(PREFERENCES_KEY_TERMINAL_ID)
				&& null != map.get(PREFERENCES_KEY_TERMINAL_ID)) {
			requestHeaders.put(HEADER_TERMINAL_ID,
					(String) map.get(PREFERENCES_KEY_TERMINAL_ID));
		}

		if (null != headerMap) {
			Iterator<Map.Entry<String, String>> iterator = headerMap.entrySet()
					.iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator
						.next();
				if (entry.getKey().equals("session")) {
					requestHeaders.put(HEADER_SESSION_ID, entry.getValue());
				} else if (entry.getKey().equals("crypt")) {
					requestHeaders.put(HEADER_CRYPT, entry.getValue());
				}
			}

			String session = requestHeaders.get(HEADER_SESSION_ID);
			if (session.isEmpty()) {
				requestHeaders.put(HEADER_SESSION_ID, ClientEngine
						.engineInstance().getSecureInfo().getSession());
			} else if (session.equalsIgnoreCase("-1")) {
				updateSession("");
				requestHeaders.put(HEADER_SESSION_ID, "");
			}
		}

		return requestHeaders;
	}

	private static HashMap<String, String> getSpecialRequestHeader(Context context,
	                                                        Map<String, String> headerMap) {
		HashMap<String, String> requestHeaders = new HashMap<String, String>();
		requestHeaders.put(HEADER_KEY_CONTENTTYPE, "application/x-www-form-urlencoded;charset=utf-8");
		requestHeaders.put(HEADER_KEY_APPKey, "mscbank");
		requestHeaders.put(HEADER_KEY_APChannel, "AP03");
		requestHeaders.put(HEADER_TERMINAL_ID, "0000000000");
		requestHeaders.put(HEADER_SESSION_ID, "");
		requestHeaders.put(HEADER_KEY_EXCHANGE, "");
		requestHeaders.put(HEADER_SIGNATURE, "");
		requestHeaders.put(HEADER_KEY_APSIGNVERION, "1.0");
		requestHeaders.put(HEADER_KEY_APFORMAT, "json");
		requestHeaders.put(HEADER_KEY_ACCEPT, "application/json");

		return requestHeaders;
	}

	private static JSONObject responseHeaderWithRequest(Context context,
			Map<String, String> headerMap) {
		String terminal = headerMap.get(HEADER_TERMINAL_ID);
		if (null != terminal && terminal.length() > 0) {
			Map<String, Object> preferencesDataMap = new HashMap<String, Object>();
			preferencesDataMap.put(PREFERENCES_KEY_TERMINAL_ID, terminal);
			UtilForDataStorage.savePropertyBySharedPreferences(context,
					PREFERENCES_NAME_HTTPCONNECT, preferencesDataMap);
		}

		boolean isKeyExchangeSucc = false;
		String keyExchange = headerMap.get(HEADER_KEY_EXCHANGE);
		if (null != keyExchange && keyExchange.length() > 0) {
			SecureEngine se = ClientEngine.engineInstance().secureEngine();

			String kcvStr = keyExchange.substring(0, 64);
			isKeyExchangeSucc = se.keyCheckValue(kcvStr);
			if (isKeyExchangeSucc && keyExchange.length() > 64) {
				String snStr = keyExchange.substring(64);
				se.setSn(snStr);
			}
		}
		JSONObject responseHeader = new JSONObject();
		if (!isKeyExchangeSucc) {
			try {
				responseHeader.put("keyExchange", "1");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		String session = headerMap.get(HEADER_SESSION_ID);
		if (null != session && session.length() > 0) {
			try {
				updateSession(session);
				responseHeader.put("session", session);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return responseHeader;
	}

	private static void updateSession(String session) {
		SecureInfo si = ClientEngine.engineInstance().getSecureInfo();
		si.setSession(session);
		ClientEngine.engineInstance().setSecureInfo(si);
	}

	public static JSONObject post(Context context, String body,
	                              Map<String, String> headerMap,Boolean isSpecial) {
		HashMap<String, String> requestHeaders = getSpecialRequestHeader(context,
				headerMap);
		Log.d(TAG, "request url : " + APPSERVER);
		String postStr = String.format("%s", body);

		String url = APPSERVER;//PREFERNTIAL_APPSERVER;
		HttpConn.setContext(context);
		/*************************************************
		 * 对body和key进行MD5操作
		 *************************************************/
		String tempStr = postStr + SPECIAL_KEY;
		String signature = stringToMD5(tempStr);
		requestHeaders.put(HEADER_SIGNATURE, signature);

		postStr = String.format("params=%s", Uri.encode(postStr));

		Log.d(TAG,
				"request header : " + new JSONObject(requestHeaders).toString());
		Log.d(TAG, "request body : " + postStr);
		HttpConn conn = null;
		JSONObject resJsonObject = null;
		for(int hc = 0; hc < 2; hc ++) {
			if(hc == 1) {
				url = APDefine.APPSERVER_IP;
			}
			//Log.d("NetEngine","--------url:" + url);
			conn = new HttpConn(new ConnParams(url, requestHeaders,
					postStr));
			int result = conn.excute();
			resJsonObject = new JSONObject();
			if (CONNECT_RESULT_SUCCESS == result) {
				Header[] responseHeaders = conn.getResponseHeaders();
				Map<String, String> resHeaderMap = new HashMap<String, String>();
				for (Header header : responseHeaders) {
					resHeaderMap.put(header.getName(), header.getValue());
				}

				Log.d(TAG,
						"response header : "
								+ new JSONObject(resHeaderMap).toString());

				JSONArray resBodyJsonArray = null;
				byte[] bytesResponseData = conn.getResponseData();
				String strResponseData = "";
				if (null != bytesResponseData) {
					try {
						strResponseData = new String(bytesResponseData, "UTF-8");
						strResponseData = Uri.decode(strResponseData);
					} catch (UnsupportedEncodingException e1) {
						e1.printStackTrace();
					}
					if ("1".equals(resHeaderMap.get(HEADER_CRYPT))) {
						strResponseData = cryptPamrams(strResponseData,
								PARAM_DECRYPT);
					}
					try {
						resBodyJsonArray = new JSONArray(strResponseData);
					} catch (JSONException e) {
						resBodyJsonArray = null;
					}
				}
				JSONObject resHeaderJsonObject = responseHeaderWithRequest(context,
						resHeaderMap);
				try {
					if (null != resHeaderJsonObject) {
						String reex = requestHeaders.get(HEADER_KEY_EXCHANGE);
						if ("".equals(reex)) {
							resHeaderJsonObject.remove("keyExchange");
						} else {
							// if (ClientEngine.engineInstance().secureEngine()
							// .isOriginSn()) {
							// String session = "";
							// resHeaderJsonObject.put("session", session);
							// updateSession(session);
							// }
						}
						resJsonObject.put("header", resHeaderJsonObject);
					}

					resJsonObject.put("body", resBodyJsonArray);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			} else {
				if(hc == 0){
					continue;
				}
				String keyExchange = "0";
				if (!("".equals(requestHeaders.get(HEADER_KEY_EXCHANGE)))) {
					keyExchange = "1";
				}
				int responseCode = conn.getResponseCode();
				try {
					resJsonObject.put("errCode", String.valueOf(responseCode));
					if (CONNECT_RESULT_NO_CONNECTION == result) {
						resJsonObject
								.put("errorMsg",
										context.getString(R.string.netconnect_result_no_connection));
					} else {
						resJsonObject.put("errorMsg",
								connectErrorDescript(context, responseCode));
					}
					resJsonObject.put("keyExchange", keyExchange);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		Log.d(TAG, "response : " + resJsonObject.toString());

		//invoke gc
		conn = null;
		return resJsonObject;
	}

    public static JSONObject post(Context context, String body,
                                  Map<String, String> headerMap) {
        JSONObject reqBodyOj;
        String actionStr = null;
        Long startTime = Long.valueOf(0);
        Long endTime = Long.valueOf(0);
        HashMap<String, String> requestHeaders = getRequestHeader(context,
                headerMap);
        Log.d(TAG, "request url : " + APPSERVER);
        String postStr = String.format("%s", body);
        try {
            reqBody = new JSONArray(body);
            reqBodyOj = reqBody.optJSONObject(0);
            actionStr = reqBodyOj.optString(BODY_ACTION);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = APPSERVER;
        HttpConn.setContext(context);
        if ("1".equals(requestHeaders.get(HEADER_CRYPT))) {
            postStr = cryptPamrams(postStr, PARAM_ENCRYPT);
        }

        String signature = ClientEngine.engineInstance().secureEngine()   //body签名
                .signature(postStr);
        requestHeaders.put(HEADER_SIGNATURE, signature);

        postStr = String.format("params=%s", Uri.encode(postStr));

        Log.d(TAG,
                "request header : " + new JSONObject(requestHeaders).toString());
        Log.d(TAG, "request body : " + postStr);
        startTime = System.currentTimeMillis();
	    HttpConn conn = null;
	    JSONObject resJsonObject = null;
	    for(int hc = 0; hc < 2; hc ++) {
		    if(hc == 1) {
			    url = APDefine.APPSERVER_IP;
		    }
		    //Log.d("NetEngine","--------url:" + url);
		    conn = new HttpConn(new ConnParams(url, requestHeaders,
				    postStr));
		    int result = conn.excute();
		    resJsonObject = new JSONObject();
		    if (CONNECT_RESULT_SUCCESS == result) {
			    Header[] responseHeaders = conn.getResponseHeaders();
			    Map<String, String> resHeaderMap = new HashMap<String, String>();
			    for (Header header : responseHeaders) {
				    resHeaderMap.put(header.getName(), header.getValue());
			    }

			    Log.d(TAG,
					    "response header : "
							    + new JSONObject(resHeaderMap).toString());

			    JSONArray resBodyJsonArray = null;
			    byte[] bytesResponseData = conn.getResponseData();
			    String strResponseData = "";
			    if (null != bytesResponseData) {
				    try {
					    strResponseData = new String(bytesResponseData, "UTF-8");
					    strResponseData = Uri.decode(strResponseData);
				    } catch (UnsupportedEncodingException e1) {
					    e1.printStackTrace();
				    }
				    if ("1".equals(resHeaderMap.get(HEADER_CRYPT))) {
					    strResponseData = cryptPamrams(strResponseData,
							    PARAM_DECRYPT);
				    }
				    try {
					    resBodyJsonArray = new JSONArray(strResponseData);
				    } catch (JSONException e) {
					    resBodyJsonArray = null;
				    }
			    }
			    JSONObject resHeaderJsonObject = responseHeaderWithRequest(context,
					    resHeaderMap);
			    try {
				    if (null != resHeaderJsonObject) {
					    String reex = requestHeaders.get(HEADER_KEY_EXCHANGE);
					    if ("".equals(reex)) {
						    resHeaderJsonObject.remove("keyExchange");
					    } else {
						    // if (ClientEngine.engineInstance().secureEngine()
						    // .isOriginSn()) {
						    // String session = "";
						    // resHeaderJsonObject.put("session", session);
						    // updateSession(session);
						    // }
					    }
					    resJsonObject.put("header", resHeaderJsonObject);
				    }

				    resJsonObject.put("body", resBodyJsonArray);
			    } catch (JSONException e) {
				    e.printStackTrace();
			    }
			    break;
		    } else {
			    if(hc == 0) {
				    continue;
			    }
			    String keyExchange = "0";
			    endTime = System.currentTimeMillis();
			    if (!("".equals(requestHeaders.get(HEADER_KEY_EXCHANGE)))) {
				    keyExchange = "1";
			    }
			    int responseCode = conn.getResponseCode();
			    try {
				    resJsonObject.put("errCode", String.valueOf(responseCode));
				    if (CONNECT_RESULT_NO_CONNECTION == result) {
					    resJsonObject
							    .put("errorMsg",
									    context.getString(R.string.netconnect_result_no_connection));
				    } else {
					    resJsonObject.put("errorMsg",
							    connectErrorDescript(context, responseCode));
				    }
				    resJsonObject.put("keyExchange", keyExchange);
			    } catch (JSONException e) {
				    e.printStackTrace();
			    }
			    if (actionStr.startsWith(BODY_TRANS_ACTION_TAG)) {
				    int RemainingTime = 62000 - (int) (endTime - startTime);
				    while (RemainingTime > 0) {
					    try {
						    Thread.sleep(1000);
						    RemainingTime = RemainingTime - 1000;
					    } catch (InterruptedException e) {
						    e.printStackTrace();
					    }
				    }
			    }
		    }
	    }
	    Log.d(TAG, "response : " + resJsonObject.toString());

	    //invoke gc
	    conn = null;
	    return resJsonObject;
    }

    public static InputStream postForStream(Context context, String body, Map<String, String> headerMap) {
        InputStream inputStream = null;
        JSONObject reqBodyOj;
        String actionStr = null;
        Long startTime = Long.valueOf(0);
        Long endTime = Long.valueOf(0);
        HashMap<String, String> requestHeaders = getRequestHeader(context, headerMap);
        Log.d(TAG, "request url : " + APPSERVER);
        String postStr = String.format("%s", body);
        try {
            reqBody = new JSONArray(body);
            reqBodyOj = reqBody.optJSONObject(0);
            actionStr = reqBodyOj.optString(BODY_ACTION);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = APPSERVER;
        HttpConn.setContext(context);

        //set accept header for stream
        requestHeaders.put(HEADER_KEY_ACCEPT, "application/octet-stream");

        if ("1".equals(requestHeaders.get(HEADER_CRYPT))) {
            postStr = cryptPamrams(postStr, PARAM_ENCRYPT);
        }

        String signature = ClientEngine.engineInstance().secureEngine().signature(postStr);   //body签名
        requestHeaders.put(HEADER_SIGNATURE, signature);

        postStr = String.format("params=%s", Uri.encode(postStr));

        Log.d(TAG, "request header : " + new JSONObject(requestHeaders).toString());
        Log.d(TAG, "request body : " + postStr);
        startTime = System.currentTimeMillis();
        HttpConn conn = new HttpConn(new ConnParams(url, requestHeaders, postStr));
        int result = conn.excute();
        JSONObject resJsonObject = new JSONObject();
        if (CONNECT_RESULT_SUCCESS == result) {
            Header[] responseHeaders = conn.getResponseHeaders();
            Map<String, String> resHeaderMap = new HashMap<String, String>();
            for (Header header : responseHeaders) {
                resHeaderMap.put(header.getName(), header.getValue());
            }

            Log.d(TAG, "response header : " + new JSONObject(resHeaderMap).toString());

            JSONArray resBodyJsonArray = null;
            byte[] bytesResponseData = conn.getResponseData();
            String strResponseData = "";
            if (null != bytesResponseData && resHeaderMap.containsKey(HEADER_KEY_CONTENT_DISPOSITION)) {
                try {
                    inputStream = InputStreamUtils.byteTOInputStream(bytesResponseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else {
            String keyExchange = "0";
            endTime = System.currentTimeMillis();
            if (!("".equals(requestHeaders.get(HEADER_KEY_EXCHANGE)))) {
                keyExchange = "1";
            }
            int responseCode = conn.getResponseCode();
            try {
                resJsonObject.put("errCode", String.valueOf(responseCode));
                if (CONNECT_RESULT_NO_CONNECTION == result) {
                    resJsonObject.put("errorMsg", context.getString(R.string.netconnect_result_no_connection));
                } else {
                    resJsonObject.put("errorMsg", connectErrorDescript(context, responseCode));
                }
                resJsonObject.put("keyExchange", keyExchange);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(actionStr.startsWith(BODY_TRANS_ACTION_TAG)) {
                int RemainingTime = 62000 - (int) (endTime - startTime);
                while (RemainingTime > 0) {
                    try {
                        Thread.sleep(1000);
                        RemainingTime = RemainingTime - 1000;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        Log.d(TAG, "response : " + resJsonObject.toString());

        //invoke gc
        conn = null;
        return inputStream;
    }

	private static String stringToMD5(String string) {
		byte[] hash;

		try {
			hash = MessageDigest.getInstance("MD5").digest(
					string.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}

		StringBuilder hex = new StringBuilder(hash.length * 2);
		for (byte b : hash) {
			if ((b & 0xFF) < 0x10)
				hex.append("0");
			hex.append(Integer.toHexString(b & 0xFF));
		}

		return hex.toString();
	}

	private static String cryptPamrams(String params, int cryptType) {
		SecureEngine se = ClientEngine.engineInstance().secureEngine();
		final String prefix = "_TDS_";
		String regexPattern = String.format("\"%s([^,]+)\"", prefix);
		Pattern patten = Pattern
				.compile(regexPattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = patten.matcher(params);
		String newParams = params;
		while (matcher.find()) {
			String strMatch = matcher.group();
			String param = strMatch.substring(1, strMatch.length() - 1);
			String encParam;
			switch (cryptType) {
			case PARAM_ENCRYPT:
				encParam = String.format("%s%s", prefix,
						se.fieldEncrypt(param.substring(prefix.length())));
				break;

			case PARAM_DECRYPT:
				encParam = String.format("%s",
						se.fieldDecrypt(param.substring(prefix.length())));
				break;

			default:
				encParam = param;
				break;
			}

			newParams = newParams.replace(param, encParam);
		}

		return newParams;
	}

	private static String connectErrorDescript(Context context, int errorCode) {
		String errorDesc = null;
		switch (errorCode / 100) {
		case 3:
			errorDesc = errorCode
					+ context.getString(R.string.netconnect_result_redirection);
			break;
		case 4:
			switch (errorCode) {
			case HttpStatus.SC_BAD_REQUEST:
				errorDesc = errorCode
						+ context
								.getString(R.string.netconnect_result_SC_BAD_REQUEST);
				break;
			case HttpStatus.SC_UNAUTHORIZED:
				errorDesc = errorCode
						+ context
								.getString(R.string.netconnect_result_SC_UNAUTHORIZED);
				break;
			case HttpStatus.SC_FORBIDDEN:
				errorDesc = errorCode
						+ context
								.getString(R.string.netconnect_result_SC_FORBIDDEN);
				break;
			case HttpStatus.SC_NOT_FOUND:
				errorDesc = errorCode
						+ context
								.getString(R.string.netconnect_result_SC_NOT_FOUND);
				break;
			default:
				errorDesc = errorCode
						+ context
								.getString(R.string.netconnect_result_request_error);
				break;
			}
			break;
		case 5:
			switch (errorCode) {
			case HttpStatus.SC_BAD_GATEWAY:
				errorDesc = errorCode
						+ context
								.getString(R.string.netconnect_result_SC_BAD_GATEWAY);
				break;
			case HttpStatus.SC_SERVICE_UNAVAILABLE:
				errorDesc = errorCode
						+ context
								.getString(R.string.netconnect_result_SC_SERVICE_UNAVAILABLE);
				break;
			case HttpStatus.SC_GATEWAY_TIMEOUT:
				errorDesc = errorCode
						+ context
								.getString(R.string.netconnect_result_SC_GATEWAY_TIMEOUT);
				break;
			case HttpStatus.SC_HTTP_VERSION_NOT_SUPPORTED:
				errorDesc = errorCode
						+ context
								.getString(R.string.netconnect_result_SC_HTTP_VERSION_NOT_SUPPORTED);
				break;
			default:
				errorDesc = errorCode
						+ context
								.getString(R.string.netconnect_result_server_error);
				break;
			}
			break;
		default:
			switch (errorCode) {
			case HttpConn.RESPONSECODE_EXCEPTION_TIMEOUT:
				errorDesc = context
						.getString(R.string.netconnect_result_RESPONSECODE_EXCEPTION_TIMEOUT);
				break;
			default:
				errorDesc = errorCode
						+ context.getString(R.string.netconnect_result_default);
				break;
			}
			break;
		}

		return errorDesc;
	}

}
