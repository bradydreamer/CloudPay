package cn.koolcloud.pos.controller.dialogs;

import java.util.HashSet;
import java.util.Stack;

import org.json.JSONObject;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.koolcloud.jni.MisPosInterface;
import cn.koolcloud.jni.MsrInterface;
import cn.koolcloud.jni.PinPadInterface;
import cn.koolcloud.jni.PrinterInterface;
import cn.koolcloud.pos.ClientEngine;
import cn.koolcloud.pos.JavaScriptEngine;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.util.Env;
import cn.koolcloud.pos.util.Logger;
import cn.koolcloud.pos.util.NetUtil;

public class DevicesCheckingDialog extends Activity implements View.OnClickListener {

	private final String TAG = "DevicesCheckingDialog";
	
	public static final int HANDLE_PINPAD_STATUS = 0;
	public static final int HANDLE_NETWORK_STATUS = 1;
	public static final int HANDLE_PRINTER_STATUS = 2;
	public static final int HANDLE_TITLE_STATUS = 3;
	public static final int HANDLE_MISPOS_STATUS = 4;
	
	private TextView titleTextView;
	private TextView pinpadTextView;
	private TextView printerTextView;
	private TextView networkTextView;
	private Button exitButton;
	private Button selfCheckButton;
	private LinearLayout progressBarLayout;
	
	private LinearLayout summaryLayout;
	private ImageView summaryImageView;
	
	
	private Drawable checkPassDrawable;
	private Drawable checkingDrawable;
	private Drawable checkFailDrawable;
//	private Drawable titleCheckPassDrawable;
//	private Drawable titleCheckFailDrawable;
	
	private boolean devicesStatusTag = true;						//Whether all devices ready or not
	private HashSet<String> devicesSet = new HashSet<String>();		//the collection of execute checking devices
	private Stack<Thread> threadStack = new Stack<Thread>();		//the collection which is waiting for checking devices
	private final int DEVICES_COUNTS = 3;
	private boolean deviceCheckingLock = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.dialog_device_checking);
		initViews();
		// communication open
		MisPosInterface.communicationOpen();
		checkDevices();
	}

	private void initViews() {
		titleTextView = (TextView) findViewById(R.id.titleTextView);
		pinpadTextView = (TextView) findViewById(R.id.pinpadTextView);
		printerTextView = (TextView) findViewById(R.id.printerTextView);
		networkTextView = (TextView) findViewById(R.id.networkTextView);
		progressBarLayout = (LinearLayout) findViewById(R.id.progressBarLayout);
		
		summaryLayout = (LinearLayout) findViewById(R.id.summaryLayout);
		summaryImageView = (ImageView) findViewById(R.id.summaryImageView);
		
		exitButton = (Button) findViewById(R.id.exitButton);
		exitButton.setOnClickListener(this);
		
		selfCheckButton = (Button) findViewById(R.id.selfCheckButton);
		selfCheckButton.setOnClickListener(this);
		
		checkPassDrawable = getResources().getDrawable(R.drawable.dialog_device_checking_pass);
		checkingDrawable = getResources().getDrawable(R.drawable.dialog_device_checking);
		checkFailDrawable = getResources().getDrawable(R.drawable.dialog_device_checking_fail);
//		titleCheckPassDrawable = getResources().getDrawable(R.drawable.dialog_device_checking_title_usual);
//		titleCheckFailDrawable = getResources().getDrawable(R.drawable.dialog_device_checking_title_unusual);
		
		//set drawable bounds
		checkPassDrawable.setBounds(0, 0,
				checkPassDrawable.getMinimumWidth(), checkPassDrawable.getMinimumHeight());
		checkingDrawable.setBounds(0, 0,
				checkingDrawable.getMinimumWidth(), checkingDrawable.getMinimumHeight());
		checkFailDrawable.setBounds(0, 0,
				checkPassDrawable.getMinimumWidth(), checkPassDrawable.getMinimumHeight());
//		titleCheckPassDrawable.setBounds(0, 0,
//				titleCheckPassDrawable.getMinimumWidth(), titleCheckPassDrawable.getMinimumHeight());
//		titleCheckFailDrawable.setBounds(0, 0,
//				titleCheckFailDrawable.getMinimumWidth(), titleCheckFailDrawable.getMinimumHeight());
	}
	
	//devices checking handler
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			boolean status = true;
			//check and set title status
			if (null != msg.obj) {
				status = (Boolean) msg.obj;
				if (!status) {
					devicesStatusTag = false;
				}
			}
			//start next thread in order
			if (threadStack != null && threadStack.size() > 0) {
				threadStack.pop().start();
			}
			
			//check devices whether are all ready or not
			if (devicesSet != null && devicesSet.size() == DEVICES_COUNTS) {
				mHandler.sendEmptyMessage(HANDLE_TITLE_STATUS);
				devicesSet.clear();
				deviceCheckingLock = false;
			}
			
			switch (msg.what) {
			case HANDLE_PINPAD_STATUS:
				
				if (status) {
					pinpadTextView.setText(Env.getResourceString(DevicesCheckingDialog.this,
							R.string.dialog_device_check_pinpad_usual));
					
					pinpadTextView.setCompoundDrawables(checkPassDrawable, null, null, null);
				} else {
					pinpadTextView.setText(Env.getResourceString(DevicesCheckingDialog.this,
							R.string.dialog_device_check_pinpad_unusual));
					pinpadTextView.setCompoundDrawables(checkFailDrawable, null, null, null);
				}
				break;
			case HANDLE_PRINTER_STATUS:
				
				if (status) {
					printerTextView.setText(Env.getResourceString(DevicesCheckingDialog.this, 
							R.string.dialog_device_check_printer_usual));
					printerTextView.setCompoundDrawables(checkPassDrawable, null, null, null);
				} else {
					printerTextView.setText(Env.getResourceString(DevicesCheckingDialog.this, 
							R.string.dialog_device_check_printer_unusual));
					printerTextView.setCompoundDrawables(checkFailDrawable, null, null, null);
				}
				break;
			case HANDLE_NETWORK_STATUS:
				
				if (status) {
					networkTextView.setText(Env.getResourceString(DevicesCheckingDialog.this, 
							R.string.dialog_device_check_network_usual));
					networkTextView.setCompoundDrawables(checkPassDrawable, null, null, null);
				} else {
					networkTextView.setText(Env.getResourceString(DevicesCheckingDialog.this, 
							R.string.dialog_device_check_network_unusual));
					networkTextView.setCompoundDrawables(checkFailDrawable, null, null, null);
				}
				break;
			case HANDLE_TITLE_STATUS:
//				titleTextView.setVisibility(View.VISIBLE);
				summaryLayout.setVisibility(View.VISIBLE);
				progressBarLayout.setVisibility(View.GONE);
				if (devicesStatusTag) {
					titleTextView.setText(Env.getResourceString(DevicesCheckingDialog.this, 
							R.string.dialog_device_check_device_ok_msg));
//					titleTextView.setCompoundDrawables(titleCheckPassDrawable, null, null, null);
					summaryImageView.setImageResource(R.drawable.dialog_device_checking_title_usual);
					exitButton.setText(getResources().getString(R.string.dialog_device_check_start_to_use));
					exitButton.setBackgroundResource(R.drawable.button_green_background);
				} else {
					titleTextView.setText(Env.getResourceString(DevicesCheckingDialog.this, 
							R.string.dialog_device_check_devices_unusual));
//					titleTextView.setCompoundDrawables(titleCheckFailDrawable, null, null, null);
					summaryImageView.setImageResource(R.drawable.dialog_device_checking_title_unusual);
				}
				break;
			case HANDLE_MISPOS_STATUS:
				if (status) {
					pinpadTextView.setText(Env.getResourceString(DevicesCheckingDialog.this,
							R.string.dialog_device_check_mispos_usual));
					
					pinpadTextView.setCompoundDrawables(checkPassDrawable, null, null, null);
				} else {
					pinpadTextView.setText(Env.getResourceString(DevicesCheckingDialog.this,
							R.string.dialog_device_check_mispos_unusual));
					pinpadTextView.setCompoundDrawables(checkFailDrawable, null, null, null);
				}
				break;
			default:
				break;
			}
		}
	};
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void onClick(View view) {
		
		switch (view.getId()) {
		case R.id.exitButton:
			finish();
			onCall("Home.updateTransInfo", null);
			break;
		case R.id.selfCheckButton:
			if (deviceCheckingLock) {
				Toast.makeText(DevicesCheckingDialog.this,
						Env.getResourceString(DevicesCheckingDialog.this,
								R.string.dialog_device_checking_now), Toast.LENGTH_SHORT).show();
			} else {
				checkDevices();
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * @Title: checkDevices
	 * @Description: TODO start to check all the devices and initialize the data status
	 * @return: void
	 */
	private void checkDevices() {
		devicesStatusTag = true;
		devicesSet.clear();
		setDevicesCheckingStatus();
		summaryLayout.setVisibility(View.GONE);
		progressBarLayout.setVisibility(View.VISIBLE);
		
		threadStack.removeAllElements();
		pushCheckingThreadToStack();
		//thread checking devices
		threadStack.pop().start();
		//checking is locking
		deviceCheckingLock = true;
	}
	
	private void setDevicesCheckingStatus() {
		pinpadTextView.setCompoundDrawables(checkingDrawable, null, null, null);
		pinpadTextView.setText(getResources().getString(R.string.dialog_device_checking));
		printerTextView.setCompoundDrawables(checkingDrawable, null, null, null);
		printerTextView.setText(getResources().getString(R.string.dialog_device_checking));
		networkTextView.setCompoundDrawables(checkingDrawable, null, null, null);
		networkTextView.setText(getResources().getString(R.string.dialog_device_checking));
	}
	
	//push all the devices thread to stack then pop to execute checking
	private void pushCheckingThreadToStack() {
		threadStack.push(new CheckPinPadThread());
		threadStack.push(new CheckPrinterThread());
		threadStack.push(new CheckNetworkThread(Env.getResourceString(this, R.string.ping_host_url)));
//		threadStack.push(new CheckMisposThread());
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
			Message msg = mHandler.obtainMessage();
			msg.what = HANDLE_PINPAD_STATUS;
			if (pinPadOpenStatus == 0 && pinPadCloseStatus == 0) {
				msg.obj = true;
			} else {
				msg.obj = false;
			}
			devicesSet.add("pinpad");
			mHandler.sendMessageDelayed(msg, 800);
		}		
	}
	
	class CheckNetworkThread extends Thread {
		
		String str;
		
		public CheckNetworkThread(String str) {
			this.str = str;
		}

		@Override
		public void run() {
			boolean networkStatus = NetUtil.pingHost(str);
			Message msg = mHandler.obtainMessage();
			msg.what = HANDLE_NETWORK_STATUS;
			msg.obj = networkStatus;
			devicesSet.add("network");
			mHandler.sendMessageDelayed(msg, 800);
		}		
	}
	
	class CheckPrinterThread extends Thread {
		
		@Override
		public void run() {
			int printerOpenStatus = PrinterInterface.open();
			int printerCloseStatus = PrinterInterface.close();
			Message msg = mHandler.obtainMessage();
			msg.what = HANDLE_PRINTER_STATUS;
			if (printerOpenStatus >= 0 && printerCloseStatus == 0) {
				msg.obj = true;
			} else {
				msg.obj = false;
			}
			devicesSet.add("printer");
			mHandler.sendMessageDelayed(msg, 800);
		}		
	}
	
	class CheckMisposThread extends Thread {
		
		@Override
		public void run() {
			MisPosInterface.communicationTest();
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			byte[] returnCode = new byte[2];
			MisPosInterface.getTagValue(0x9F14, returnCode);
			Log.i(TAG, "returnCode: " + returnCode[0] + " " + returnCode[1]);
			
			Message msg = mHandler.obtainMessage();
			
			
			msg.what = HANDLE_MISPOS_STATUS;
			
			if (returnCode[0] != 0x30 && returnCode[1] != 0x30) {
				Log.i(TAG, "Serialport Communication Failed");
				msg.obj = false;
			} else {
				msg.obj = true;
			}
			
			devicesSet.add("mispos");
			mHandler.sendMessageDelayed(msg, 800);
		}		
	}
	
	private void onCall(String jsHandler, JSONObject msg) {
		JavaScriptEngine js = ClientEngine.engineInstance().javaScriptEngine();
		if (js != null) {
			js.callJsHandler(jsHandler, msg);
		} else {
			Logger.d("ClientEngine.engineInstance(): "	+ ClientEngine.engineInstance());
			Logger.d("javaScriptEngine(): "	+ ClientEngine.engineInstance().javaScriptEngine());
		}
	}

	/**
	 * deal with not responding on clicking out side of dialog
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return false;  
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		MisPosInterface.communicationClose();
	}
}
