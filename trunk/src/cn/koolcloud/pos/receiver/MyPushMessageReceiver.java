package cn.koolcloud.pos.receiver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import cn.koolcloud.pos.controller.dialogs.PushMessageController;
import cn.koolcloud.pos.util.PushUtils;
import cn.koolcloud.pos.wd.R;

import com.baidu.frontia.api.FrontiaPushMessageReceiver;

/**
 * Push message handle receiver. please overwrite your needed callback methods,
 * generally:
 * onBind must, to deal with startWork callback result;
 * onMessage, receive messages;
 * onSetTags, onDelTags, onListTags for tag operation callback;
 * onNotificationClicked when notification clicking callback;
 * onUnbind is stopWork interface callback;
 * 
 * return errorCode description:
 * 0 - Success
 * 10001 - Network Problem
 * 30600 - Internal Server Error
 * 30601 - Method Not Allowed
 * 30602 - Request Params Not Valid
 * 30603 - Authentication Failed
 * 30604 - Quota Use Up Payment Required
 * 30605 - Data Required Not Found
 * 30606 - Request Time Expires Timeout
 * 30607 - Channel Token Timeout
 * 30608 - Bind Relation Not Found
 * 30609 - Bind Number Too Many
 * 
 */
public class MyPushMessageReceiver extends FrontiaPushMessageReceiver {
    /** TAG to Log */
    public static final String TAG = MyPushMessageReceiver.class.getSimpleName();

	/**
	 * after calling PushManager.startWork,sdk will start binding request to push server, it is sync, and return binding result in onBind.
	 * if you need unicast push, you need to upload channel id and user id to server, then call server interface with channel id and user id to the phone or client.
	 * 
	 * @param context: BroadcastReceiver running Context
	 * @param errorCode: binding interface callback value, 0 - success
     * @param appid: app id. it is null when errorCode is not 0
	 * @param userId: app user id, it is null when errorCode is not 0.
	 * @param channelId: app channel id, it is null when errorCode is not 0
	 * @param requestId: request id to server. for tracking problems.
	 * @return none
	 */
	@Override
	public void onBind(Context context, int errorCode, String appid, 
				String userId, String channelId, String requestId) {
		String responseString = "onBind errorCode=" + errorCode + " appid="
				+ appid + " userId=" + userId + " channelId=" + channelId
				+ " requestId=" + requestId;
		Log.d(TAG, responseString);
		
		// binding success, set binded flag, decrease unnecessary binding request.
		if (errorCode == 0) {
			PushUtils.setBind(context, true);
		}
		// update UI for Demo program.
		updateContent(context, responseString);
	}

	/**
	 * receive passthrough message.
	 * 
	 * @param context 
	 * @param message pushed message
	 * @param customContentString custom content, null or json string
	 */
	@Override
	public void onMessage(Context context, String message, String customContentString) {
		String messageString = "Passthrough message=\"" + message + "\" customContentString="
				+ customContentString;
		Log.d(TAG, messageString);
		
		// get custom content,mykey and myvalue face to the kay and value in custom content.
		if (customContentString != null & TextUtils.isEmpty(customContentString)) {
            JSONObject customJson = null;
            try {
                customJson = new JSONObject(customContentString);
                String myvalue = null;
                if (customJson.isNull("mykey")) {
                    myvalue = customJson.getString("mykey");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        updateContent(context, messageString);
    }

    /**
	 * Method for receiving notification clicking. Warn:app can't get notification content before clicking notification.
	 * 
	 * @param context 
	 * @param title The title of push notification
	 * @param description The description of push notification.
	 * @param customContentString Custom content string, null or json string.
	 */
    @Override
    public void onNotificationClicked(Context context, String title,
            String description, String customContentString) {
        String notifyString = "Clicking title=\"" + title + "\" description=\""
                + description + "\" customContent=" + customContentString;
        Log.d(TAG, notifyString);

		// get custom content
        /*if (!TextUtils.isEmpty(customContentString)) {
            JSONObject customJson = null;
            try {
                customJson = new JSONObject(customContentString);
                
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }*/
        
        
        Intent intent = new Intent();
        intent.setClass(context.getApplicationContext(), PushMessageController.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle mBundle = new Bundle();
		mBundle.putString("title", title);
		mBundle.putString("description", description);
		intent.putExtras(mBundle);
        context.startActivity(intent);
    }

	/**
	 * setTags() callback method.
	 * 
	 * @param context 
	 * @param errorCode Error Code. 0 some tag set success; tags' setting are all failure.
	 * @param successTags Setting success tags.
	 * @param failTags Setting failure tags.
	 * @param requestId Assign to push service request id.
	 */
    
    @Override
    public void onSetTags(Context context, int errorCode,
            List<String> sucessTags, List<String> failTags, String requestId) {
        String responseString = "onSetTags errorCode=" + errorCode
                + " sucessTags=" + sucessTags + " failTags=" + failTags
                + " requestId=" + requestId;
        Log.d(TAG, responseString);

        updateContent(context, responseString);
    }

	/**
	 * delTags() callback method.
	 * 
	 * @param context 
	 * @param errorCode Error Code. 0 some tag delete success; !0 All the tags are deleting failure.
	 * @param successTags The Tags deleted are successfull.
	 * @param failTags The tags deleting failure.
	 * @param requestId Assign to push service request id.
	 */
    @Override
    public void onDelTags(Context context, int errorCode,
            List<String> sucessTags, List<String> failTags, String requestId) {
        String responseString = "onDelTags errorCode=" + errorCode
                + " sucessTags=" + sucessTags + " failTags=" + failTags
                + " requestId=" + requestId;
        Log.d(TAG, responseString);

        updateContent(context, responseString);
    }

	/**
	 * listTags() callback method.
	 * 
	 * @param context 
	 * @param errorCode  Error Code. 0 list tags success. !0 failure.
	 * @param tags Current app setted all the tags.
	 * @param requestId Assign to push service request id.
	 */
    @Override
    public void onListTags(Context context, int errorCode, List<String> tags,
            String requestId) {
        String responseString = "onListTags errorCode=" + errorCode + " tags="
                + tags;
        Log.d(TAG, responseString);

        updateContent(context, responseString);
    }

	/**
	 * PushManager.stopWork() call back method.
	 * 
	 * @param context 
	 * @param errorCode Error code. 0 unbind from cloud push service. !0 failure.
	 * @param requestId Assign to push service request id.
	 */
    @Override
    public void onUnbind(Context context, int errorCode, String requestId) {
        String responseString = "onUnbind errorCode=" + errorCode
                + " requestId = " + requestId;
        Log.d(TAG, responseString);

		// unbind success, and set bind flag.
        if (errorCode == 0) {
            PushUtils.setBind(context, false);
        }
        updateContent(context, responseString);
    }

    private void updateContent(Context context, String content) {
        Log.d(TAG, "updateContent");
        String logText = "" + PushUtils.logStringCache;

        if (!logText.equals("")) {
            logText += "\n";
        }

        SimpleDateFormat sDateFormat = new SimpleDateFormat("HH-mm-ss");
        logText += sDateFormat.format(new Date()) + ": ";
        logText += content;

        PushUtils.logStringCache = logText;

        /*Intent intent = new Intent();
        intent.setClass(context.getApplicationContext(), PushDemoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.getApplicationContext().startActivity(intent);*/
    }
    
    private void showCustomToast(Context ctx) {
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);;
        View layout = inflater.inflate(R.layout.dialog_push_notification_layout, null/*(ViewGroup) findViewById(R.id.rootLayout)*/);
//        TextView textView = (TextView) layout.findViewById(R.id.tvForToast);
//        textView.setText("This is Toast but cannot be eaten and hello world");
        Toast toast = new Toast(ctx);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setView(layout);
        toast.show();
    }

}
