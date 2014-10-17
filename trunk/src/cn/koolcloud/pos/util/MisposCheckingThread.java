package cn.koolcloud.pos.util;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import cn.koolcloud.interfaces.MisposEventInterface;
import cn.koolcloud.jni.MisPosEvent;
import cn.koolcloud.jni.MisPosInterface;

public class MisposCheckingThread extends Thread {
	private final static String LOG_TAG = "MisposCheckingThread";
	
	private static final int MISPOS_CONN_FAILED = 0;
	
	MisposEventInterface misposEvent;
	EventThread eventThread;
	public int commFlag = 0;
	
	private boolean isEventThreadRunning = true;
	public MisposCheckingThread(MisposEventInterface misposEvent) {
		this.misposEvent = misposEvent;
	}

	public boolean isEventThreadRunning() {
		return isEventThreadRunning;
	}

	public void setEventThreadRunning(boolean isEventThreadRunning) {
		this.isEventThreadRunning = isEventThreadRunning;
		MisPosInterface.communicationClose();
	}

	@Override
	public void run() {
		eventThread = new EventThread();
		eventThread.start();
		
		MisPosInterface.communicationOpen();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		commFlag = 0xAA;
		MisPosInterface.communicationTest();
	}
	
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			case MISPOS_CONN_FAILED:
				
				
				if (commFlag == 0xAA) {
					byte[] returnCode = new byte[2]; 
					MisPosInterface.getTagValue(0x9F14, returnCode);
					Log.i(LOG_TAG, "returnCode: " + returnCode[0] + " " + returnCode[1]);
					if (returnCode[0] != 0x30 && returnCode[1] != 0x30) {
						Log.w(LOG_TAG, "Serialport Communication Failed");
						misposEvent.misposConnectStatus(false);
						return;
					} else { 
						Log.w(LOG_TAG, "Serialport Communication Succ");
						misposEvent.misposConnectStatus(true); 
					}
				}
				break;

			default:
				break;
			}
		}
		
	};

	class EventThread extends Thread {
		@Override
		public void run() {
			while (isEventThreadRunning) {
				if (MisPosEvent.getMisposEvent() == 0) {
					
					/*byte[] returnCode = new byte[2]; 
					MisPosInterface.getTagValue(0x9F14, returnCode);
					Log.i(LOG_TAG, "returnCode: " + returnCode[0] + " " + returnCode[1]);
					if (returnCode[0] != 0x30 && returnCode[1] != 0x30) {
						Log.w(LOG_TAG, "Serialport Communication Failed");
						misposEvent.misposConnectStatus(false);
						return;
					} else { 
						Log.w(LOG_TAG, "Serialport Communication Succ"); 
						misposEvent.misposConnectStatus(true);
					}
					
					MisPosEvent.setMisposEvent(-1);*/
					
					Log.i(LOG_TAG, "Receive Message Timeout");
					
					//receive message timeout
					Message msg = mHandler.obtainMessage();
					msg.what = MISPOS_CONN_FAILED;
					msg.sendToTarget();
					
					MisPosEvent.setMisposEvent(-1);
				}
			}
		}
	}
	
	
}
