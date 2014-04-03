package cn.koolcloud.pos.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

/**
 * <p>Title: NetUtil.java</p>
 * <p>Description: Use for network checking</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: All In Pay</p>
 * @author 		Teddy
 * @date 		2013-11-4
 * @version 	
 */
public class NetUtil {
	
	public static final String WIFI = "wifi";
	public static final String MOBILE = "wifi";
	public static final String ALL = "all";
	
    public static int getActiveType(Context ctx) {
        ConnectivityManager conMan = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMan == null)
            return -1;
        NetworkInfo info = conMan.getActiveNetworkInfo();
        if (info == null)
            return -1;
        return info.getType();
    }

    /**
    * @Title: isWifiActive
    * @Description: TODO
    * @param @param ctx
    * @param @return
    * @return boolean 
    * @throws
    */
    public static boolean isWifiActive(Context ctx) {
        ConnectivityManager conMan = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMan == null)
            return false;
        NetworkInfo info = conMan.getActiveNetworkInfo();
        if (info == null)
            return false;
        return info.getType() == ConnectivityManager.TYPE_WIFI;
    }

    public static boolean isMobileActive(Context ctx) {
        ConnectivityManager conMan = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMan == null)
            return false;
        NetworkInfo info = conMan.getActiveNetworkInfo();
        if (info == null)
            return false;
        return info.getType() == ConnectivityManager.TYPE_MOBILE;
    }

    public static boolean isCurrentNetworkAllowed(Context ctx, String allow) {
        if ("mobile".equalsIgnoreCase(allow))
            return isMobileActive(ctx);
        if ("wifi".equalsIgnoreCase(allow))
            return isWifiActive(ctx);
        if ("all".equalsIgnoreCase(allow))
            return true;
        return false;
    }

    /**
    * @Title: isWifiConnected
    * @Description: TODO check if wifi is connected
    * @param @param ctx
    * @param @return
    * @return boolean 
    * @throws
    */
    public static boolean isWifiConnected(Context ctx) {
        ConnectivityManager conMan = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMan == null)
            return false;
        NetworkInfo info = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (info == null)
            return false;
        State wifiState = info.getState();
        return (wifiState == State.CONNECTED);
    }

    /**
    * @Title: isMobileConnected
    * @Description: TODO check GPRS network
    * @param @param ctx
    * @param @return
    * @return boolean 
    * @throws
    */
    public static boolean isMobileConnected(Context ctx) {
        ConnectivityManager conMan = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMan == null)
            return false;
        NetworkInfo info = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (info == null)
            return false;
        State wifiState = info.getState();
        return (wifiState == State.CONNECTED);
    }

    public static boolean isMobileGPRS(Context ctx) {
        ConnectivityManager conMan = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMan == null)
            return false;
        NetworkInfo info = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (info == null)
            return false;
        return (info.getSubtypeName().equalsIgnoreCase("GPRS") || info.getSubtypeName().equalsIgnoreCase("EDGE"));
    }

    public static boolean isAvailable(Context ctx) {
        return (isWifiConnected(ctx) || isMobileConnected(ctx) || isEthernetDataEnable(ctx));
    }

    public static NetworkInfo getWifiInfo(Context ctx) {
        ConnectivityManager conMan = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMan == null)
            return null;

        return conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    }

    public static NetworkInfo getMobileInfo(Context ctx) {
        ConnectivityManager conMan = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMan == null)
            return null;

        return conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
    }

    public static boolean startMobileFeature(Context ctx, String feature) {
        ConnectivityManager conMan = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMan == null)
            return false;

        int ret = conMan.startUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, feature);
        return (ret != -1);
    }

    public static boolean stopMobileFeature(Context ctx, String feature) {
        ConnectivityManager conMan = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMan == null)
            return false;

        int ret = conMan.stopUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, feature);
        return (ret != -1);
    }
    
    public static boolean isEthernetDataEnable(Context paramContext){
        
        return ((ConnectivityManager)paramContext.getSystemService("connectivity"))
                .getNetworkInfo(ConnectivityManager.TYPE_ETHERNET).isConnectedOrConnecting();
    }
    
    public static boolean isNetworkAvailable(Context paramContext) {
        ConnectivityManager connectivity = (ConnectivityManager)paramContext.getSystemService("connectivity");
        if (connectivity != null) {
        	NetworkInfo[] info = connectivity.getAllNetworkInfo();
        	if (info != null) {
        		for (int i = 0; i < info.length; i++) {
        			if (info[i].getState() == NetworkInfo.State.CONNECTED) {
        				return true;
        			}
        		}
        	}
        }
        return false;
    }
}
