package cn.koolcloud.jni;

import android.util.Log;

import java.util.LinkedList;
import java.util.Queue;


public class EmvL2Event {
	public static final String TAG = "EmvL2Event";
	public static Queue<Integer> queue = new LinkedList<Integer>();
	public static int iCardEvent = -1;
	
	public static void setCardEvent(int cardEvent) {
		queue.offer(cardEvent);
	}
	
	public static int getCardEvent() {
		if(queue.size() == 0){
			return -1;
		}else {
			iCardEvent = queue.poll();
			return iCardEvent;
		}

	}
	
}
