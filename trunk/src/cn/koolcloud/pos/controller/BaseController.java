package cn.koolcloud.pos.controller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cn.koolcloud.pos.ClientEngine;
import cn.koolcloud.pos.JavaScriptEngine;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.controller.others.settings.LoginController;
import cn.koolcloud.pos.controller.others.settings.SetMachineIdController;
import cn.koolcloud.pos.controller.others.settings.SetMerchIdController;

public abstract class BaseController extends Activity {

	protected JSONObject formData;
	protected Handler mainHandler;
	public final static int RESULT_QUIT = 50;
	public final static int RESULT_ORDER_END = 51;
	public final static int RESULT_START_ACTIVITY = 52;

	private final int TIME_FOR_CONTROLLER_LOADED = 50;
	private boolean isContollerVisible;
	private boolean hasControllerResumed;
	private Button titlebar_btn_right;
	private TextView titlebar_btn_title;
	private Button titlebar_btn_left;
	protected final String TAG = "AllinpayController";
	public boolean willRestart;

	protected StringBuilder numberInputString;
	private EditText currentNumberEditText;
	private Typeface faceType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		numberInputString = new StringBuilder();

		mainHandler = new Handler();
		faceType = Typeface.createFromAsset(getAssets(), "font/digital-7.ttf");
		ClientEngine clientEngine = ClientEngine.engineInstance();
		if (clientEngine.isContextNull()) {
			Log.d(TAG, "clientEngine.isContextNull");
			willRestart = true;
			Intent intent = getBaseContext().getPackageManager()
					.getLaunchIntentForPackage(
							getBaseContext().getPackageName());
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();

		} else {
			clientEngine.setMainHandler(mainHandler);
			clientEngine.setCurrentController(this);
			clientEngine.addController(getControllerName(), this);
			setFormData();
			loadRelatedJS();
		}

		setWindowFeature();
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		setControllerContentView();
		if (getWindow().hasFeature(Window.FEATURE_CUSTOM_TITLE)) {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
					R.layout.titlebar);
			initTitlebar();
		}
		notifyWillshow();
	}

	protected void setWindowFeature() {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
	}

	@Override
	protected void onPause() {
		hasControllerResumed = false;
		setRemoveJSTag(true);
		super.onPause();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	protected void setFormData() {
		Intent intent = getIntent();
		String strFormData = intent
				.getStringExtra(getString(R.string.intent_extra_name_formData));
		if (null == strFormData) {
			return;
		}
		try {
			formData = new JSONObject(strFormData);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	abstract protected void setControllerContentView();

	abstract protected String getTitlebarTitle();

	abstract protected String getControllerName();

	abstract protected String getControllerJSName();

	abstract protected void setRemoveJSTag(boolean tag);

	abstract protected boolean getRemoveJSTag();

	// abstract protected String otherContollerJsName();

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_QUIT) {
			setResult(RESULT_QUIT);
			finish();
			return;
		} else if (resultCode == RESULT_ORDER_END) {
			setResult(RESULT_ORDER_END, data);
			finish();
			return;
		} else if (resultCode == RESULT_START_ACTIVITY) {
			startActivityForResult(data,
					ClientEngine.engineInstance().mRequestCode);
			return;
		} else {
			notifyWillshow();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	protected void showController(final Class<?> cls) {
		ClientEngine clientEngine = ClientEngine.engineInstance();
		clientEngine.showController(cls);
	}

	protected void loadRelatedJS() {
		if (null == getControllerJSName()) {
			return;
		}
		if (getRemoveJSTag()) {
			JavaScriptEngine js = ClientEngine.engineInstance()
					.javaScriptEngine();
			js.loadJs(getControllerJSName());
			setRemoveJSTag(false);
		}
	}

	protected void willShow() {

	}

	public void onClickLeftButton(View view) {
		onBackPressed();
	}

	private void initTitlebar() {
		titlebar_btn_left = (Button) findViewById(R.id.titlebar_btn_left);
		titlebar_btn_right = (Button) findViewById(R.id.titlebar_btn_right);
		titlebar_btn_title = (Button) findViewById(R.id.titlebar_btn_title);
		String title = getTitlebarTitle();
		titlebar_btn_title.setText(title);
	}

	protected void setLeftButton(int resourceId) {
		titlebar_btn_left.setBackgroundResource(resourceId);
		titlebar_btn_left.setVisibility(View.VISIBLE);
	}

	protected void setRightButton(int resourceId) {
		titlebar_btn_right.setBackgroundResource(resourceId);
		titlebar_btn_right.setVisibility(View.VISIBLE);
	}

	protected void setOnClickRightButton(OnClickListener listener) {
		titlebar_btn_right.setOnClickListener(listener);
	}

	protected void setOnClickLeftButton(OnClickListener listener) {
		titlebar_btn_left.setOnClickListener(listener);
	}

	protected void setLeftButtonHidden() {
		titlebar_btn_left.setVisibility(View.INVISIBLE);
	}

	protected void setRightButtonHidden() {
		titlebar_btn_right.setVisibility(View.INVISIBLE);
	}

	protected void setRightButtonEnabled(boolean enabled) {
		titlebar_btn_right.setEnabled(enabled);
	}

	@Override
	public void setTitle(CharSequence title) {
		titlebar_btn_title.setText(title);
	}

	@Override
	public void setTitle(int titleId) {
		setTitle(getString(titleId));
	}

	@Override
	protected void onResume() {
		if (!willRestart) {
			ClientEngine clientEngine = ClientEngine.engineInstance();
			clientEngine.setCurrentController(this);
		}
		loadRelatedJS();
		super.onResume();
		hasControllerResumed = true;
	}

	public void notifyWillshow() {
		if (!willRestart) {
			isContollerVisible = false;
			Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {
					while (!isContollerVisible || !hasControllerResumed) {
						try {
							Thread.sleep(TIME_FOR_CONTROLLER_LOADED);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					mainHandler.post(new Runnable() {

						@Override
						public void run() {
							willShow();
						}
					});
				}
			});
			thread.setName("ThreadForWillShow");
			thread.start();
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if (!willRestart) {
			if (hasFocus) {
				isContollerVisible = true;
			}
		}
		super.onWindowFocusChanged(hasFocus);
	}

	@Override
	protected void onStop() {
		setRemoveJSTag(true);
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		if (!willRestart) {
			ClientEngine clientEngine = ClientEngine.engineInstance();
			clientEngine.removeController(getControllerName());
			if (null != getControllerJSName()
					&& !(this instanceof LoginController)
					&& !(this instanceof SetMachineIdController)
					&& !(this instanceof SetMerchIdController)) {
				JavaScriptEngine js = ClientEngine.engineInstance()
						.javaScriptEngine();
				Log.i(TAG, "Warning:--------RemoveJS:onDestroy:"
						+ getControllerJSName());
				js.removeJs(getControllerJSName());
				setRemoveJSTag(true);
			}
		}
		super.onDestroy();
	}

	public void setProperty(JSONArray data) {
		for (int i = 0; i < data.length(); i++) {
			JSONObject item = data.optJSONObject(i);
			View view = viewForIdentifier(item.optString("name"));
			if (null != view) {
				setView(view, item.optString("key"), item.opt("value"));
			}
		}
	}

	protected View viewForIdentifier(String name) {
		return null;
	}

	protected void setView(View view, String key, Object value) {
		if (null == view || null == key) {
			return;
		}
		if (key.equals("text")) {
			if (view instanceof TextView) {
				((TextView) view).setText((String) value);
			} else if (view instanceof EditText) {
				((EditText) view).setText((String) value);
			}
		} else if (key.equals("placeholder")) {
			if (view instanceof EditText) {
				((EditText) view).setHint((String) value);
			}
		} else if (key.equals("hidden")) {
			if ((Boolean) value) {
				view.setVisibility(View.GONE);
			} else {
				view.setVisibility(View.VISIBLE);
			}
		} else if (key.equals("enable")) {
			view.setEnabled((Boolean) value);
		}
	}

	public void onCall(String jsHandler, JSONObject msg) {
		JavaScriptEngine js = ClientEngine.engineInstance().javaScriptEngine();
		if (js != null) {
			js.callJsHandler(jsHandler, msg);
		} else {
			Log.d(TAG,
					"ClientEngine.engineInstance(): "
							+ ClientEngine.engineInstance());
			Log.d(TAG, "javaScriptEngine(): "
					+ ClientEngine.engineInstance().javaScriptEngine());
		}
	}

	protected void showInputNumber() {
		if (null != currentNumberEditText) {
			currentNumberEditText.setText(numberInputString);
		}
	}

	protected StringBuilder getNumberInputString() {
		return numberInputString;
	}

	protected void addInputNumber(String text) {
		numberInputString.append(text);
	}

	protected void delInputNumber() {
		int index = numberInputString.length() - 1;
		if (index >= 0) {
			numberInputString.deleteCharAt(index);
		}
	}

	public void onClickBtnNumber0(View view) {
		addInputNumber("0");
		showInputNumber();
	}

	public void onClickBtnNumber1(View view) {
		addInputNumber("1");
		showInputNumber();
	}

	public void onClickBtnNumber2(View view) {
		addInputNumber("2");
		showInputNumber();
	}

	public void onClickBtnNumber3(View view) {
		addInputNumber("3");
		showInputNumber();
	}

	public void onClickBtnNumber4(View view) {
		addInputNumber("4");
		showInputNumber();
	}

	public void onClickBtnNumber5(View view) {
		addInputNumber("5");
		showInputNumber();
	}

	public void onClickBtnNumber6(View view) {
		addInputNumber("6");
		showInputNumber();
	}

	public void onClickBtnNumber7(View view) {
		addInputNumber("7");
		showInputNumber();
	}

	public void onClickBtnNumber8(View view) {
		addInputNumber("8");
		showInputNumber();
	}

	public void onClickBtnNumber9(View view) {
		addInputNumber("9");
		showInputNumber();
	}

	public void onClickBtnNumberBack(View view) {
		delInputNumber();
		showInputNumber();
	}

	public void onClickBtnC(View view) {
		numberInputString.delete(0, numberInputString.length());
		showInputNumber();
	}

	public void onClickBtnOK(View view) {

	}

	public EditText getCurrentNumberEditText() {
		return currentNumberEditText;
	}

	public void setCurrentNumberEditText(EditText currentNumberEditText) {
		if (currentNumberEditText != null) {
			currentNumberEditText.setTypeface(faceType);
		}
		if (currentNumberEditText != this.currentNumberEditText) {
			numberInputString.delete(0, numberInputString.length());
			Editable currentEditable = currentNumberEditText.getText();
			if (null != currentEditable) {
				numberInputString.append(currentEditable.toString().replace(
						".", ""));
			}
		}
		this.currentNumberEditText = currentNumberEditText;
	}

	protected void initETWithKBHiddenListener(final EditText editText) {
		editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					getWindow().setSoftInputMode(
							WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
					setOnFocusChangeExtraAction(editText);
				}
			}
		});
		editText.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.requestFocus();
				return true;
			}
		});
	}

	protected void setOnFocusChangeExtraAction(EditText editText) {

	}

}
