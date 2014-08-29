package cn.koolcloud.pos.util;

import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.baidu.android.pushservice.CustomPushNotificationBuilder;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;

public class PushUtils {
	public static final String TAG = "PushDemoActivity";
	public static final String RESPONSE_METHOD = "method";
	public static final String RESPONSE_CONTENT = "content";
	public static final String RESPONSE_ERRCODE = "errcode";
	public static final String ACTION_LOGIN = "com.baidu.pushdemo.action.LOGIN";
	public static final String ACTION_MESSAGE = "com.baiud.pushdemo.action.MESSAGE";
	public static final String ACTION_RESPONSE = "bccsclient.action.RESPONSE";
	public static final String ACTION_SHOW_MESSAGE = "bccsclient.action.SHOW_MESSAGE";
	public static final String EXTRA_ACCESS_TOKEN = "access_token";
	public static final String EXTRA_MESSAGE = "message";
	
	public static String logStringCache = "";
	
	// Get ApiKey
    public static String getMetaValue(Context context, String metaKey) {
        Bundle metaData = null;
        String apiKey = null;
        if (context == null || metaKey == null) {
        	return null;
        }
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
            	apiKey = metaData.getString(metaKey);
            }
        } catch (NameNotFoundException e) {

        }
        return apiKey;
    }

    // set share preference as a switch, set true when onBind success, set false when unBind success.
    public static boolean hasBind(Context context) {
    	SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		String flag = sp.getString("bind_flag", "");
		if ("ok".equalsIgnoreCase(flag)) {
			return true;
		}
		return false;
    }
    
    public static void setBind(Context context, boolean flag) {
    	String flagStr = "not";
    	if (flag) {
    		flagStr = "ok";
    	}
    	SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = sp.edit();
		editor.putString("bind_flag", flagStr);
		editor.commit();
    }
    
	public static List<String> getTagsList(String originalText) {
		if (originalText == null || originalText.equals("")) {
			return null;
		}
		List<String> tags = new ArrayList<String>();
		int indexOfComma = originalText.indexOf(',');
		String tag;
		while (indexOfComma != -1) {
			tag = originalText.substring(0, indexOfComma);
			tags.add(tag);

			originalText = originalText.substring(indexOfComma + 1);
			indexOfComma = originalText.indexOf(',');
		}

		tags.add(originalText);
		return tags;
	}

	public static String getLogText(Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString("log_text", "");
	}
	
	public static void setLogText(Context context, String text) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = sp.edit();
		editor.putString("log_text", text);
		editor.commit();
	}
	
	public static void loginBaiduCloud(Context ctx) {
		Resources resource = ctx.getResources();
		String pkgName = ctx.getPackageName();
		
		// Push: Login with apikey, generally in onCreate of Activity.
		// apikey stored in manifest.
		if (!PushUtils.hasBind(ctx)) {
			PushManager.startWork(ctx,
					PushConstants.LOGIN_TYPE_API_KEY, 
					PushUtils.getMetaValue(ctx, "api_key"));
			PushConstants.startPushService(ctx);
			// Push: push message based on Geolocation, you can open this switch.
			PushManager.enableLbs(ctx);
		}
		
		// Push: customized notification style, follow api of the user guide, if you want the default you can remove the following code.
		// In Baidu cloud push service page, advanced settings -> notification style ->custom style, selected and input :1,
		// face to PushManager.setNotificationBuilder(this, 1, cBuilder) the second argument.
        CustomPushNotificationBuilder cBuilder = new CustomPushNotificationBuilder(
        		ctx,
        		resource.getIdentifier("notification_custom_builder", "layout", pkgName), 
        		resource.getIdentifier("notification_icon", "id", pkgName), 
        		resource.getIdentifier("notification_title", "id", pkgName), 
        		resource.getIdentifier("notification_text", "id", pkgName));
        cBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
        cBuilder.setNotificationDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        cBuilder.setStatusbarIcon(ctx.getApplicationInfo().icon);
        cBuilder.setLayoutDrawable(resource.getIdentifier("notify_icon", "drawable", pkgName));
		PushManager.setNotificationBuilder(ctx, 1, cBuilder);
	}
	
}
