package cn.koolcloud.pos.external;

import cn.koolcloud.pos.R;

import com.google.zxing.Result;
import com.google.zxing.client.android.IScanEvent;
import com.google.zxing.client.android.ScannerRelativeLayout;
import com.google.zxing.client.android.camera.CameraSettings;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

public class CodeScanner {
	private Context context;
	private CodeScannerListener listener;

	private final String TAG = "CodeScanner";

	private ScannerRelativeLayout scanner;
	private IScanEvent iScanSuccessListener;

	public void onCreate(Context c, CodeScannerListener listener) {
		context = c;
		this.listener = listener;

		CameraSettings.setCAMERA_FACING(CameraSettings.FACING_FRONT);
		CameraSettings.setAUTO_FOCUS(true);
		CameraSettings.setBEEP(true);
		//true：可以连续识别 ，false：识别成功后要重新 startScan
		CameraSettings.setBULKMODE(true);
		
		scanner = (ScannerRelativeLayout) ((Activity) context)
				.findViewById(R.id.scanner);
		iScanSuccessListener = new ScanSuccesListener();
		scanner.setScanSuccessListener(iScanSuccessListener);
		scanner.startScan();
	}

	private class ScanSuccesListener extends IScanEvent {

		@Override
		public void scanCompleted(Result scannerResult) {
			if (listener != null) {
				listener.onRecvData(scannerResult.getText().getBytes());
			}
		}
	}

	public void onStart() {
		scanner.startScan();
	}

	public void onPause() {
		scanner.pauseScan();
	}

	public void onResume() {
		Log.d(TAG, "NearFieldController onResume");
		scanner.startScan();
	}

	public void onDestroy() {
		scanner.pauseScan();
		scanner.stopScan();
	}

	public interface CodeScannerListener {
		public void onRecvData(byte[] receivedBytes);
	}
}
