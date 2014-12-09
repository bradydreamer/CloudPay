package cn.koolcloud.pos.external.scanner;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.TextView;

public class ZBarScanner implements ZBarConstants {

	private static final String TAG = "ZBarScanner";
	private CameraPreview mPreview;
	private Camera mCamera;
	private ImageScanner mScanner;
	private Handler mAutoFocusHandler;
	private boolean mPreviewing = true;
	private TextView scanText;
	private Context context;
	private Looper waitDataLooper;
	private Boolean cameraOpenStatus = false;

	static {
		System.loadLibrary("iconv");
	}

	public ZBarScanner(Context context) {
		super();
		this.context = context;
		if (!isCameraAvailable()) {
			// Cancel request if there is no rear-facing camera.
			cancelRequest();
			return;
		}
		setupScanner();
		mPreview = new CameraPreview(context, (PreviewCallback) context,
				autoFocusCB);

		if (waitDataLooper == null) {
			HandlerThread waitDataThread = new HandlerThread(
					"waitSwipeCardData");
			waitDataThread.start();
			waitDataLooper = waitDataThread.getLooper();
		}
		mAutoFocusHandler = new Handler(waitDataLooper);

	}

	public CameraPreview getMpreview() {

		return mPreview;
	}

	public void setupScanner() {
		mScanner = new ImageScanner();
		mScanner.setConfig(0, Config.X_DENSITY, 3);
		mScanner.setConfig(0, Config.Y_DENSITY, 3);

		int[] symbols = ((Activity) context).getIntent().getIntArrayExtra(
				SCAN_MODES);
		if (symbols != null) {
			mScanner.setConfig(Symbol.NONE, Config.ENABLE, 0);
			for (int symbol : symbols) {
				mScanner.setConfig(symbol, Config.ENABLE, 1);
			}
		}
	}

	public ImageScanner getMscanner() {
		return mScanner;
	}

	public void startScanner() {
		// Open the default i.e. the first rear facing camera.
		if(cameraOpenStatus){
			return;
		}
		mCamera = Camera.open();
		if (mCamera == null) {
			// Cancel request if mCamera is null.
			cancelRequest();
			return;
		}

		mPreview.setCamera(mCamera);
		mPreview.showSurfaceView();

		mPreviewing = true;
		cameraOpenStatus = true;
	}

	public void stopScanner() {
		// Because the Camera object is a shared resource, it's very
		// important to release it when the activity is paused.
		if(!cameraOpenStatus){
			return;
		}
		if (mCamera != null) {
			mPreview.setCamera(null);
			mCamera.cancelAutoFocus();
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			mCamera.release();

			// According to Jason Kuang on
			// http://stackoverflow.com/questions/6519120/how-to-recover-camera-preview-from-sleep,
			// there might be surface recreation problems when the device goes
			// to sleep. So lets just hide it and
			// recreate on resume
			// mPreview.hideSurfaceView();

			mPreviewing = false;
			mCamera = null;
			cameraOpenStatus = false;
		}
	}

	public void destroyedScanner() {
		// Because the Camera object is a shared resource, it's very
		// important to release it when the activity is paused.
		if(!cameraOpenStatus){
			return;
		}
		if (mCamera != null) {
			mPreview.setCamera(null);
			mCamera.cancelAutoFocus();
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			mCamera.release();

			// According to Jason Kuang on
			// http://stackoverflow.com/questions/6519120/how-to-recover-camera-preview-from-sleep,
			// there might be surface recreation problems when the device goes
			// to sleep. So lets just hide it and
			// recreate on resume
			mPreview.hideSurfaceView();

			mPreviewing = false;
			mCamera = null;
			cameraOpenStatus = false;
		}
	}

	public boolean isCameraAvailable() {
		PackageManager pm = context.getPackageManager();
		return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
	}

	public void cancelRequest() {

	}

	public void onPreviewFrame(byte[] data, Camera camera) {
		Camera.Parameters parameters = camera.getParameters();
		Camera.Size size = parameters.getPreviewSize();

		Image barcode = new Image(size.width, size.height, "Y800");
		barcode.setData(data);

		int result = mScanner.scanImage(barcode);

		if (result != 0) {
			mCamera.cancelAutoFocus();
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			mPreviewing = false;
			SymbolSet syms = mScanner.getResults();
			for (Symbol sym : syms) {
				String symData = sym.getData();
				if (!TextUtils.isEmpty(symData)) {
					// Intent dataIntent = new Intent();
					// dataIntent.putExtra(SCAN_RESULT, symData);
					// dataIntent.putExtra(SCAN_RESULT_TYPE, sym.getType());
					// setResult(Activity.RESULT_OK, dataIntent);
					// finish();
					scanText.setText(symData);
					break;
				}
			}
		}
	}

	public Runnable doAutoFocus = new Runnable() {
		@Override
		public void run() {
			if (mCamera != null && mPreviewing) {
				mCamera.autoFocus(autoFocusCB);
			}
		}
	};

	// Mimic continuous auto-focusing
	public Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			mAutoFocusHandler.postDelayed(doAutoFocus, 1000);
		}
	};

}
