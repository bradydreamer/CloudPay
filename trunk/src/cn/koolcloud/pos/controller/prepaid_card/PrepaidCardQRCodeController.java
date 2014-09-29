package cn.koolcloud.pos.controller.prepaid_card;

import java.io.IOException;

import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.AssetFileDescriptor;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.FrameLayout;
import cn.koolcloud.constant.ConstantUtils;
import cn.koolcloud.parameter.UtilFor8583;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.controller.BaseController;
import cn.koolcloud.pos.external.CodeScanner;
import cn.koolcloud.pos.external.CodeScanner.CodeScannerListener;
import cn.koolcloud.pos.external.SoundWave.SoundWaveListener;
import cn.koolcloud.pos.external.scanner.ZBarScanner;

public class PrepaidCardQRCodeController extends BaseController implements
		SoundWaveListener, CodeScannerListener, Camera.PreviewCallback {

	private CodeScanner codeScanner;
	private boolean isPause = false;
	private boolean removeJSTag = true;

	private String func_nearfieldAccount;

	private JSONObject data;

	private ZBarScanner scanner;
	private ImageScanner imageScanner;
	private FrameLayout preview;
	private MediaPlayer mediaPlayer;
	private boolean playBeep = true;
	private static final float BEEP_VOLUME = 0.10f;
	private static final long VIBRATE_DURATION = 200L;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (null == formData) {
			finish();
			return;
		}

		data = formData.optJSONObject(getString(R.string.formData_key_data));

		func_nearfieldAccount = data.optString("nearfieldAccount");

		UtilFor8583.getInstance().trans
				.setEntryMode(ConstantUtils.ENTRY_QRCODE_MODE);
		codeScanner = new CodeScanner();
		codeScanner.onCreate(PrepaidCardQRCodeController.this,
				PrepaidCardQRCodeController.this);
		onStartQRScanner();
		// preview = (FrameLayout) findViewById(R.id.scanner);
		// scanner = new ZBarScanner(this);
		// preview.addView(scanner.getMpreview());
		// imageScanner = scanner.getMscanner();
		// initBeepSound();
		// scanner.startScanner();
	}

	/**
	 * 初始化声音
	 */
	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);
			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	/**
	 * 播放声音和震动
	 */
	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
	}

	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		// TODO Auto-generated method stub
		Camera.Parameters parameters = camera.getParameters();
		Camera.Size size = parameters.getPreviewSize();

		Image barcode = new Image(size.width, size.height, "Y800");
		barcode.setData(data);

		int result = imageScanner.scanImage(barcode);

		if (result != 0) {
			playBeepSoundAndVibrate();// 播放声音代表成功获取二维码
			SymbolSet syms = imageScanner.getResults();
			for (Symbol sym : syms) {
				String symData = sym.getData();
				if (!TextUtils.isEmpty(symData)) {
					JSONObject transData = new JSONObject();
					try {
						transData
								.put(getString(R.string.formData_key_payData_field0),
										symData);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					scanner.stopScanner();
					Log.d(TAG, "processReceivedData : " + transData.toString());
					onCall(func_nearfieldAccount, transData);
					break;
				}
			}
		}
		// textView.setText("" + data.toString());
	}

	@Override
	protected void onPause() {
		isPause = true;
		if (codeScanner != null) {
			codeScanner.onPause();
		}
		// if (scanner != null) {
		// scanner.stopScanner();
		// }
		super.onPause();
	}

	@Override
	protected void onResume() {
		if (!isPause) {
			super.onResume();
			return;
		} else {
			isPause = false;
		}
		if (codeScanner != null) {
			codeScanner.onResume();
		}
		// if (scanner != null) {
		// scanner.startScanner();
		// }
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, this + "onDestroy");

		if (codeScanner != null) {
			codeScanner.onDestroy();
			codeScanner = null;
		}
		// if (scanner != null) {
		// scanner.stopScanner();
		// }
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	private void onStartQRScanner() {
		if (codeScanner == null) {
			codeScanner = new CodeScanner();
			codeScanner.onCreate(PrepaidCardQRCodeController.this,
					PrepaidCardQRCodeController.this);

			mainHandler.post(new Runnable() {

				@Override
				public void run() {
				}
			});
		} else {
			codeScanner.onResume();
		}
	}

	private void onStopQRScanner() {
		if (codeScanner != null) {
			codeScanner.onPause();
		}
	}

	@Override
	protected void setControllerContentView() {
		setContentView(R.layout.activity_prepaid_card_qrcode_controller);
	}

	@Override
	protected String getTitlebarTitle() {
		return getResources().getString(
				R.string.title_activity_qrcode_controller);
	}

	@Override
	protected String getControllerName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getControllerJSName() {
		// TODO Auto-generated method stub
		return getString(R.string.controllerJSName_PayAccount);
	}

	@Override
	protected void setRemoveJSTag(boolean tag) {
		// TODO Auto-generated method stub
		removeJSTag = tag;
	}

	@Override
	protected boolean getRemoveJSTag() {
		// TODO Auto-generated method stub
		return removeJSTag;
	}

	@Override
	public void onRecvData(byte[] receivedBytes) {
		Log.d(TAG, "receivedBytes length : " + receivedBytes.length);
		int analyzingIndex = 0;
		String receivedData = "";
		receivedData = new String(receivedBytes);
		Log.d(TAG, "NearFieldController receivedData : " + receivedData);
		JSONObject transData = new JSONObject();

		if (!receivedData.isEmpty()) {
			try {
				transData.put(getString(R.string.formData_key_payData_field0),
						receivedData);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			onStopQRScanner();
			Log.d(TAG, "processReceivedData : " + transData.toString());
			// Toast.makeText(PrepaidCardQRCodeController.this,
			// transData.toString(), Toast.LENGTH_SHORT).show();
			onCall(func_nearfieldAccount, transData);
		}

	}

}
