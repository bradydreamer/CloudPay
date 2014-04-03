package cn.koolcloud.pos.receiver;

import cn.koolcloud.pos.service.MerchService;
import cn.koolcloud.pos.service.SecureService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class LanchReceiver extends BroadcastReceiver {
	protected final String TAG = "LanchReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "onReceive: " + intent.getAction());

		context.startService(new Intent(context, MerchService.class));
		context.startService(new Intent(context, SecureService.class));
	}
}
