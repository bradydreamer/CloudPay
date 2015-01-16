package cn.koolcloud.pos.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.nostra13.universalimageloader.core.assist.FlushedInputStream;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cn.koolcloud.pos.R;
import cn.koolcloud.pos.net.NetEngine;

/**
 * Created by Teddy on 2015/1/14.
 */
public class CustomImageDownloader extends BaseImageDownloader {
    public static final String TAG = CustomImageDownloader.class.getName();
    private Context ctx;


    public CustomImageDownloader(Context context, int connectTimeout, int readTimeout) {
        super(context, connectTimeout, readTimeout);
        ctx = context;
    }

    /*@Override
    protected HttpURLConnection createConnection(String url, Object extra) throws IOException {
        HttpURLConnection conn = super.createConnection(url, extra);
        Map<String, String> headers = (Map<String, String>) extra;
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                conn.setRequestProperty(header.getKey(), header.getValue());
            }
        }
        return conn;
    }*/

    @Override
    protected HttpURLConnection createConnection(String url, Object extra) throws IOException {
        HttpURLConnection conn = super.createConnection(url, extra);
        Map<String, String> headers = (Map<String, String>) extra;
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                conn.setRequestProperty(header.getKey(), header.getValue());
            }
        }
        return conn;
    }

    @Override
    protected InputStream getStreamFromNetwork(String imageUri, Object extra) throws IOException {

        /*URL url = null;
        try {
            url = new URL(imageUri);
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        HttpURLConnection http = null;

        if (Scheme.ofUri(imageUri) == Scheme.HTTPS) {
            trustAllHosts();
            HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
            https.setHostnameVerifier(DO_NOT_VERIFY);
            http = https;
            http.connect();
        } else {
            http = (HttpURLConnection) url.openConnection();
        }

        http.setConnectTimeout(connectTimeout);
        http.setReadTimeout(readTimeout);*/

        InputStream inputStream = null;
        JSONArray jsonArray = new JSONArray();
        JSONObject data = new JSONObject();
        Map<String, String> headerMap = new HashMap<String, String>();

        try {
            if (!TextUtils.isEmpty(imageUri)) {
                data.put("action", "msc/file/download");
                data.put("fileId", imageUri.substring(imageUri.lastIndexOf("/") + 1));
                jsonArray.put(data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        inputStream = NetEngine.postForStream(ctx, jsonArray.toString(), headerMap);
        if (null != inputStream) {

            return new FlushedInputStream(new BufferedInputStream(inputStream));
        }

//        return getStreamFromDrawable("drawable://" + R.drawable.logo_default, extra);
        return null;
    }

    @Override
    protected InputStream getStreamFromDrawable(String imageUri, Object extra) {
        return super.getStreamFromDrawable(imageUri, extra);
    }

    // always verify the host - dont check for certificate
    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    /**
     * Trust every server - dont check for any certificate
     */
    private static void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] x509Certificates,
                    String s) throws java.security.cert.CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] x509Certificates,
                    String s) throws java.security.cert.CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }
        }};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
