package cn.koolcloud.pos.external;

import com.wsn.sscl.SSCstudioApi;
import com.wsn.sscl.SSCstudioApi.ReceiveListener;
import com.wsn.sscl.SSCstudioApi.UsbDetachListener;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

public class SoundWave{
	private Context context;
	private SoundWaveListener listener;
	private SSCstudioApi sscStudioApi;
	private final String TAG = "SoundWave";
	private Handler soundWaveSendHandler;
	private Looper soundWaveSendLooper;
		
	private boolean isSoundWaveLegal;
	
	public void onCreate(Context c, SoundWaveListener listener) {
		context = c;
		this.listener = listener;
		initSoundWave();
	}
	
	private void initSoundWave(){
		try {
			sscStudioApi = new SSCstudioApi(context);
			sscStudioApi.setReceiveEnable(true);
			sscStudioApi.setUsbDetachNotifyCallback(new UsbDetachListener() {
				
				@Override
				public void usbDetachNotify() {
					
				}
			});
			sscStudioApi.setDataReceiveCallback(new ReceiveListener() {
				
				@Override
				public void receiveData(byte[] data) {
					if(listener != null){
						listener.onRecvData(data);
					}
				}
			});
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	private class soundWaveSendRunnable implements Runnable {

		@Override
		public void run() {
			((Activity) context).runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					String data = "CHANNEL_IPOS";
					Log.d(TAG, "sendData: " + data);
					try {
						if (isSoundWaveLegal) {
							sscStudioApi.sendData(data.getBytes());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			soundWaveSendHandler.postDelayed(this, 3000);
		}
		
	}	
	
	public void onStart(){
		isSoundWaveLegal = sscStudioApi.start();
		if(isSoundWaveLegal) {
			HandlerThread soundWaveSendThread = new HandlerThread("soundWaveSendThread");
			soundWaveSendThread.start();
			soundWaveSendLooper = soundWaveSendThread.getLooper();
			soundWaveSendHandler = new Handler(soundWaveSendLooper);
			soundWaveSendHandler.post(new soundWaveSendRunnable());
		}
	}
	
	public void onPause() {
		try {
			if (null != soundWaveSendLooper) {
				soundWaveSendLooper.quit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (isSoundWaveLegal) {
			sscStudioApi.stop();
		}
	}

	public void onDestroy() {
		try {
			if (null != soundWaveSendLooper) {
				soundWaveSendLooper.quit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (isSoundWaveLegal) {
			sscStudioApi.stop();
			sscStudioApi.destroyResource();
		}
	}

	public interface SoundWaveListener{
		public void onRecvData(byte[] receivedBytes);
	}
}
