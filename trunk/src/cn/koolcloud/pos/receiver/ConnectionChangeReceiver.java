package cn.koolcloud.pos.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import cn.koolcloud.pos.service.MerchService;
import cn.koolcloud.pos.service.SecureService;
import cn.koolcloud.pos.util.NetUtil;

/**
 * <p>Title: ConnectionChangeReceiver.java </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2014</p>
 * <p>Company: All In Pay</p>
 * @author 		Teddy
 * @date 		2014-3-27
 * @version 	
 */
public class ConnectionChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent merchService = new Intent(context, MerchService.class);
        context.startService(merchService);
        
        Intent secureService = new Intent(context, SecureService.class);
        context.startService(secureService);
	}
}
