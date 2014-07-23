package cn.koolcloud.pos.external;

import java.util.Hashtable;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import cn.koolcloud.jni.MsrInterface;

public class CardSwiper {
	private Context context;
	private CardSwiperListener listener;

	private final String TAG = "CardSwiper";

	private Looper waitDataLooper;
	private Boolean isPollCanceled;

	public void onCreate(Context c, CardSwiperListener listener) {
		this.context = c;
		this.listener = listener;

		isPollCanceled = false;
		if (waitDataLooper == null) {
			HandlerThread waitDataThread = new HandlerThread(
					"waitSwipeCardData");
			waitDataThread.start();
			waitDataLooper = waitDataThread.getLooper();
		}
	}

	public void onStart() {
		if (MsrInterface.open() < 0) {
			MsrInterface.close();
			MsrInterface.open();
		}

		Handler handler = new Handler(waitDataLooper);
		handler.post(new Runnable() {
			@Override
			public void run() {
				int timeout = 300000;
				gotoPoll(timeout);
			}
		});
	}

	public void onPause() {
		onDestroy();
	}

	public void onDestroy() {
		MsrInterface.cancelPoll();
		isPollCanceled = true;
		MsrInterface.close();
		if (null != waitDataLooper) {
			waitDataLooper.quit();
		}
	}

	private void gotoPoll(int timeout) {
		Log.d(TAG, this + " gotoPoll");

		int pollResult = MsrInterface.poll(timeout);
		if (0 != pollResult && !isPollCanceled) {
			Log.d(TAG, this + " gotoPoll");
			gotoPoll(timeout);
		}
		if (0 == pollResult) {
			if (isPollCanceled) {
				this.onDestroy();
			}

			String track1 = msrGetTrackData(0);
			String track2 = msrGetTrackData(1);
			String track3 = msrGetTrackData(2);
			Log.d(TAG, "SwipeCard track1 : " + track1 + " track2 : " + track2
					+ " track3 : " + track3);
			if (null == track2) {
				gotoPoll(timeout);
			} else {
				Hashtable<String, String> trackData = new Hashtable<String, String>();
				if (track1 != null) {
					trackData.put("track1", track1);
				}
				trackData.put("track2", track2);
				if (track3 != null) {
					trackData.put("track3", track3);
				} else {
					trackData.put("track3", "");
				}
				trackData.put("cardID", getCardID(track2));
				trackData.put("validTime", getCardValidTime(track2));
				this.listener.onRecvTrackData(trackData);
			}
		}
	}

	private String getCardID(String msg) {
		String[] strs = msg.split("=");
		return strs[0];
	}

	private String getCardValidTime(String msg) {
		String[] strs = msg.split("=");
		String str = strs[1].substring(0, 4);
		return str;
	}

	private String msrGetTrackData(int trackIndex) {
		int ret = MsrInterface.getTrackError(trackIndex);
		if (ret < 0) {
			Log.i(TAG, "msr track" + trackIndex + " error is = " + ret);
			return null;
		}

		byte[] byteArry = new byte[255];
		int result = MsrInterface.getTrackData(trackIndex, byteArry,
				MsrInterface.getTrackDataLength(trackIndex));
		if (result > 0) {
			return new String(byteArry, 0, result);
		} else {
			return null;
		}
	}

	public interface CardSwiperListener {
		public void onRecvTrackData(Hashtable<String, String> trackData);
	}
}
