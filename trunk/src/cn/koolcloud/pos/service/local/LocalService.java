package cn.koolcloud.pos.service.local;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import cn.koolcloud.constant.ConstantUtils;
import cn.koolcloud.interfaces.MisposEventInterface;
import cn.koolcloud.ipos.appstore.service.aidl.IMSCService;
import cn.koolcloud.ipos.appstore.service.aidl.ParcelableApp;
import cn.koolcloud.jni.MsrInterface;
import cn.koolcloud.jni.PinPadInterface;
import cn.koolcloud.jni.PrinterInterface;
import cn.koolcloud.pos.ClientEngine;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.controller.dialogs.ServiceCheckingDevicesDialog;
import cn.koolcloud.pos.controller.dialogs.ServiceCheckingUpdateDialog;
import cn.koolcloud.pos.util.Env;
import cn.koolcloud.pos.util.HttpUtil;
import cn.koolcloud.pos.util.Logger;
import cn.koolcloud.pos.util.MisposCheckingThread;

import com.loopj.android.http.AsyncHttpResponseHandler;

public class LocalService extends Service implements MisposEventInterface {
	public static final String TAG = "LocalService";
	
	private final static int HANDLE_CHECKING_APP_VERSION = 0;
	private final static int HANDLE_CHECKING_DEVICES = 1;
	
	private static final int HANDLE_PINPAD_STATUS = 0;
	private static final int HANDLE_NETWORK_STATUS = 1;
	private static final int HANDLE_PRINTER_STATUS = 2;
	private static final int HANDLE_TITLE_STATUS = 3;
	private static final int HANDLE_MISPOS_STATUS = 4;
	
	private final static int SEND_MESSAGE_DELAYED_TIME = 50;
	private final static int NETWORK_CHECKING_WAITING_TIME = 5000;

	private final static int START_CHECKING_THREAD_TIME = 14400000; //start checking every four hour (4h*60m*60s*1000ms=14400000ms)
	
	private Context context;
	
	//devices checking params
	private boolean devicesStatusTag = true;						//Whether all devices ready or not
	private HashSet<String> devicesSet = new HashSet<String>();		//the collection of execute checking devices
	private Stack<Thread> threadStack = new Stack<Thread>();		//the collection which is waiting for checking devices
	private final int DEVICES_COUNTS = 4;
	private Map<String, Boolean> deviceStatusMap = new HashMap<String, Boolean>();
	
	private MisposCheckingThread misposCheckingThread;
	
	private boolean isStartedFromExtenal = true;
	
	//Time for checking app version
	private final Timer timer = new Timer();
	private TimerTask task;

    private boolean networkCheckingMsgSendTag = false; //use to control send message to message queue or not
	
	//AppStore components
	protected ParcelableApp localParcelableApp;
	protected IMSCService mIService;
	
	protected ServiceConnection connection = new ServiceConnection() {
		  
        public void onServiceConnected(ComponentName name, IBinder service) {
            // get AIDL instance from remote service.  
            mIService = IMSCService.Stub.asInterface(service);
            Logger.i("Bind Checking App Version Service Successfull");
        }
  
        public void onServiceDisconnected(ComponentName name) {
            mIService = null;
            Logger.i("Checking App Version Service Disconnected");
        }
    };

	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate() executed");
		super.onCreate();
		context = this;
		
		//bind MSC service
		Intent service = new Intent(IMSCService.class.getName());
        bindService(service, connection, BIND_AUTO_CREATE);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onBind() executed"); 
		return null;
	}
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLE_CHECKING_APP_VERSION:
				StringBuffer strBuffer = new StringBuffer();
				if (null != localParcelableApp) {
//					strBuffer.append(getResources().getString(R.string.app_name) + "\n");
					strBuffer.append(getResources().getString(R.string.str_current_version_name) + Env.getVersionName(context) + "\n");
					strBuffer.append(getResources().getString(R.string.str_new_version) + localParcelableApp.getVersion() + "\n");
					strBuffer.append(getResources().getString(R.string.about_info) + "\n");
					
					Intent mIntent = new Intent(getApplicationContext(), ServiceCheckingUpdateDialog.class);
	        		mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    			Bundle mBundle = new Bundle();
	    			mBundle.putParcelable(ConstantUtils.SER_KEY, localParcelableApp);
	    			mBundle.putString(ConstantUtils.UPDATE_INFO_KEY, strBuffer.toString());
	    			mIntent.putExtras(mBundle);
	    			startActivity(mIntent);
				} else {
//					strBuffer.append(getResources().getString(R.string.app_name) + "\n");
					strBuffer.append(getResources().getString(R.string.str_current_version_name) + Env.getVersionName(context) + "\n");
					strBuffer.append(getResources().getString(R.string.str_new_version) + Env.getVersionName(context) + "\n");
					strBuffer.append(getResources().getString(R.string.about_info) + "\n");
				}
				
				Log.i(TAG, "app version:" + strBuffer.toString());
				break;
			case HANDLE_CHECKING_DEVICES:
				startCheckingDevices();
				break;
			default:
				break;
			}
		}		
	};

	@Deprecated
	public void onStart(Intent intent, int startId) {
		Log.d(TAG, "onStart() executed");
		super.onStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand() executed");
		//this tag from activity's startService parameter which indicate start checking whether or not.
		boolean startTag = false;
		if (intent != null && intent.getExtras() != null) {
			startTag = intent.getExtras().getBoolean(ConstantUtils.LOCAl_SERVICE_TAG, false);
			isStartedFromExtenal = intent.getExtras().getBoolean(ConstantUtils.START_SERVICE_EXTERNAL_TAG, true);
		}
		if (startTag) {
			//appstore is installed then start service to check new version
			if ((Env.checkApkExist(context, ConstantUtils.APP_STORE_PACKAGE_NAME))) {
				new CheckUpdateThread().start();
			} else {//checking devices starting
				startCheckingDevices();
			}
		}
		
		task = new TimerTask() {
		    @Override 
		    public void run() {
		    	isStartedFromExtenal = false;
		    	new CheckUpdateThread().start();
		    }
		};
		
		timer.schedule(task, START_CHECKING_THREAD_TIME, START_CHECKING_THREAD_TIME);
		
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy() executed");
		//cancel cycled task
		timer.cancel();
		unbindService(connection);
		stopSelf();
		super.onDestroy();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(TAG, "onUnbind() executed");
		unbindService(connection);
		return super.onUnbind(intent);
	}
	
	//devices checking handler
	Handler mDeviceHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			boolean status = true;
			
			if (null != msg.obj) {
				status = (Boolean) msg.obj;
				if (!status) {
					if (msg.what != HANDLE_PINPAD_STATUS && msg.what != HANDLE_MISPOS_STATUS) {
						devicesStatusTag = false;
					}
				}
			}
			
			//check devices are all ready whether or not
			if (devicesSet != null && devicesSet.size() == DEVICES_COUNTS) {
				mDeviceHandler.sendEmptyMessage(HANDLE_TITLE_STATUS);
				devicesSet.clear();
			}
			
			switch (msg.what) {
			case HANDLE_PINPAD_STATUS:
				Log.i(TAG, "HANDLE_PINPAD_STATUS");
				deviceStatusMap.put(ConstantUtils.DEVICE_PINPAD_KEY, status);
				
				break;
			case HANDLE_PRINTER_STATUS:
				Log.i(TAG, "HANDLE_PRINTER_STATUS");
				deviceStatusMap.put(ConstantUtils.DEVICE_PRINTER_KEY, status);
				
				break;
			case HANDLE_NETWORK_STATUS:
				Log.i(TAG, "HANDLE_NETWORK_STATUS");
				deviceStatusMap.put(ConstantUtils.DEVICE_NETWORK_KEY, status);
				break;
			case HANDLE_TITLE_STATUS:
				Log.i(TAG, "HANDLE_TITLE_STATUS");
				
				boolean misposTag = false;
				if (deviceStatusMap.containsKey(ConstantUtils.DEVICE_MISPOS_KEY)) {
					misposTag = deviceStatusMap.get(ConstantUtils.DEVICE_MISPOS_KEY);
				}
				boolean pinPadTag = false;
				if (deviceStatusMap.containsKey(ConstantUtils.DEVICE_PINPAD_KEY)) {
					pinPadTag = deviceStatusMap.get(ConstantUtils.DEVICE_PINPAD_KEY);
				}
				
				//mispos and pinpad are all false, set devices are n't ready
				if (!(misposTag || pinPadTag)) {
					devicesStatusTag = false;
				}
				
				deviceStatusMap.put(ConstantUtils.DEVICE_ALL_KEY, devicesStatusTag);
				//TODO:send all the params to service checking dialog and show.
				if (!devicesStatusTag && Env.isAppInForeground(context)) {
					ClientEngine.engineInstance().javaScriptEngine().loadJs("Common/home");
					Intent mIntent = new Intent(getApplicationContext(), ServiceCheckingDevicesDialog.class);
					mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					Bundle mBundle = new Bundle();
					mBundle.putSerializable(ConstantUtils.SER_KEY, (Serializable) deviceStatusMap);
					mIntent.putExtras(mBundle);
					startActivity(mIntent);
				}
				
				break;
			case HANDLE_MISPOS_STATUS:
				Log.w(TAG, "HANDLE_MISPOS_STATUS");
				deviceStatusMap.put(ConstantUtils.DEVICE_MISPOS_KEY, status);
				break;
			default:
				break;
			}
			
			//start next thread in order
			if (threadStack != null && threadStack.size() > 0) {
				threadStack.pop().start();
			}
		}
	};
	
	/**
	 * @Title: checkDevices
	 * @Description: start to check all the devices and initialize the data status
	 * @return: void
	 */
	private void startCheckingDevices() {
        networkCheckingMsgSendTag = false;
		devicesStatusTag = true;
		devicesSet.clear();
		
		threadStack.removeAllElements();
		pushCheckingThreadToStack();

        //start network checking time
        new NetWorkTimerThread().start();

//        new CheckNetworkThread(Env.getResourceString(this, R.string.ping_host_url)).start();
        startCheckNetwork(Env.getResourceString(this, R.string.ping_host_url));


		//thread checking devices
//		threadStack.pop().start();
		//checking is locking
	}
	
	/**
	 * push all the devices thread to stack then pop to execute checking
	 * @Title: pushCheckingThreadToStack
	 * @Description: 
	 * @return: void
	 */
	private void pushCheckingThreadToStack() {
		misposCheckingThread = new MisposCheckingThread(this);
		threadStack.push(new CheckPinPadThread());
		threadStack.push(new CheckPrinterThread());
//		threadStack.push(new CheckNetworkThread(Env.getResourceString(this, R.string.ping_host_url)));
		threadStack.push(misposCheckingThread);
	}
	
	/**
	 * <p>Description: checking update</p>
	 * <p>Copyright: Copyright (c) 2014</p>
	 * <p>Company: All In Pay</p>
	 * @author 		Teddy
	 * @date 		2014-6-3
	 * @version 	
	 */
	class CheckUpdateThread extends Thread {

		@Override
		public void run() {
			try {
				if (null != mIService) {
					Message msg = mHandler.obtainMessage();
					localParcelableApp = mIService.checkUpdate(Env.getPackageName(context), Env.getVersionCode(context));
					if (localParcelableApp != null) {
						Log.i(TAG, "appId:" + localParcelableApp.getId()
								+ " appName:" + localParcelableApp.getName()
								+ " appVersion:" + localParcelableApp.getVersion());
						msg.what = HANDLE_CHECKING_APP_VERSION;
					} else {//no new version start checking devices.
						Log.i(TAG, "no need to update");
						if (isStartedFromExtenal) {
							msg.what = HANDLE_CHECKING_DEVICES;
						}
					}
					
					msg.obj = localParcelableApp;
					
					mHandler.sendMessage(msg);
				}
			} catch (RemoteException localRemoteException) {
				Logger.e(localRemoteException.getMessage());
			} catch (Exception e) {
				Message msg = mHandler.obtainMessage();
				msg.obj = localParcelableApp;
				msg.what = HANDLE_CHECKING_DEVICES;
				mHandler.sendMessage(msg);
			}
		}
	}
	
	/**
	 * <p>Title: DevicesCheckingDialog.java </p>
	 * <p>Description: check pinpad status</p>
	 * <p>Copyright: Copyright (c) 2014</p>
	 * <p>Company: All In Pay</p>
	 * @author 		Teddy
	 * @date 		2014-5-22
	 * @version 	
	 */
	class CheckPinPadThread extends Thread {

		@Override
		public void run() {
			//close msr anyway
			MsrInterface.close();
			int pinPadOpenStatus = PinPadInterface.open();
			int pinPadCloseStatus = PinPadInterface.close();
			Message msg = mDeviceHandler.obtainMessage();
			msg.what = HANDLE_PINPAD_STATUS;
			if (pinPadOpenStatus == 0 && pinPadCloseStatus == 0) {
				msg.obj = true;
			} else {
				msg.obj = false;
			}
			devicesSet.add("pinpad");
			mDeviceHandler.sendMessageDelayed(msg, SEND_MESSAGE_DELAYED_TIME);
		}		
	}
	
	/*class CheckNetworkThread extends Thread {
		
		String str;

		public CheckNetworkThread(String str) {
			this.str = str;
		}

		@Override
		public void run() {
//            httpClientGet(str);
		}
	}*/

    private void httpClientGet(String url) {
        HttpGet httpRequest = new HttpGet(url);
        Message msg = mDeviceHandler.obtainMessage();
        msg.what = HANDLE_NETWORK_STATUS;
        devicesSet.add("network");

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse = httpClient.execute(httpRequest);

            // success
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                msg.obj = true;
            } else {
                msg.obj = false;
            }
        } catch (ClientProtocolException e) {
            msg.obj = false;
        } catch (IOException e) {
            msg.obj = false;
        } catch (Exception e) {
            msg.obj = false;
        } finally {
            if (!networkCheckingMsgSendTag) {
                networkCheckingMsgSendTag = true;
                mDeviceHandler.sendMessageDelayed(msg, SEND_MESSAGE_DELAYED_TIME);
            }
        }
    }
	
	private void startCheckNetwork(String url) {
		devicesSet.add("network");
		
		HttpUtil.get(url, new AsyncHttpResponseHandler() {
	           
            public void onFinish() {
            	
            }
            
			@Override
			public void onFailure(int status, Header[] header, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)

				Message msg = mDeviceHandler.obtainMessage();
                msg.what = HANDLE_NETWORK_STATUS;
                msg.obj = false;

                if (!networkCheckingMsgSendTag) {
                    networkCheckingMsgSendTag = true;
                    mDeviceHandler.sendMessageDelayed(msg, SEND_MESSAGE_DELAYED_TIME);
                }
			}
			
			@Override
			public void onSuccess(int status, Header[] header, byte[] arg2) {
				// TODO Auto-generated method stub
				// called when response HTTP status is "200 OK"
				
				if (status == 200) {
					Message msg = mDeviceHandler.obtainMessage();
					msg.what = HANDLE_NETWORK_STATUS;
					msg.obj = true;
                    if (!networkCheckingMsgSendTag) {
                        networkCheckingMsgSendTag = true;
                        mDeviceHandler.sendMessageDelayed(msg, SEND_MESSAGE_DELAYED_TIME);
                    }
				}
			};
        });
	}
	
	class CheckPrinterThread extends Thread {
		
		@Override
		public void run() {
			int printerOpenStatus = PrinterInterface.open();
			int printerCloseStatus = PrinterInterface.close();
			Message msg = mDeviceHandler.obtainMessage();
			msg.what = HANDLE_PRINTER_STATUS;
			if (printerOpenStatus >= 0 && printerCloseStatus == 0) {
				msg.obj = true;
			} else {
				msg.obj = false;
			}
			devicesSet.add("printer");
			mDeviceHandler.sendMessageDelayed(msg, SEND_MESSAGE_DELAYED_TIME);
		}		
	}

    class NetWorkTimerThread extends Thread {

        @Override
        public void run() {
            long startedTime = System.currentTimeMillis();
            while (true) {
                if ((System.currentTimeMillis() - startedTime) == NETWORK_CHECKING_WAITING_TIME ) {
                    if (!networkCheckingMsgSendTag) {
                        Message msg = mDeviceHandler.obtainMessage();
                        msg.what = HANDLE_NETWORK_STATUS;
                        msg.obj = false;

                        networkCheckingMsgSendTag = true;
                        mDeviceHandler.sendMessage(msg);
                    }
                    break;
                }
            }
        }
    }

	@Override
	public void misposConnectStatus(boolean isConnected) {
		// TODO Auto-generated method stub
		devicesSet.add("mispos");
		misposCheckingThread.setEventThreadRunning(false);
		Message msg = mDeviceHandler.obtainMessage();
		msg.what = HANDLE_MISPOS_STATUS;
		msg.obj = isConnected;
		mDeviceHandler.sendMessageDelayed(msg, SEND_MESSAGE_DELAYED_TIME);
	}

}
