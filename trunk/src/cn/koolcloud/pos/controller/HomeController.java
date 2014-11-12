package cn.koolcloud.pos.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.Toast;
import cn.koolcloud.constant.ConstantUtils;
import cn.koolcloud.pos.ClientEngine;
import cn.koolcloud.pos.JavaScriptEngine;
import cn.koolcloud.pos.MyApplication;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.adapter.HomePagerAdapter;
import cn.koolcloud.pos.controller.dialogs.AboutDialog;
import cn.koolcloud.pos.controller.dialogs.CheckingUpdateDialog;
import cn.koolcloud.pos.controller.dialogs.DevicesCheckingDialog;
import cn.koolcloud.pos.database.ConsumptionRecordDB;
import cn.koolcloud.pos.service.MerchInfo;
import cn.koolcloud.pos.service.local.LocalService;
import cn.koolcloud.pos.util.UtilForDataStorage;
import cn.koolcloud.pos.widget.ViewPagerIndicator;

public class HomeController extends BaseHomeController implements
		View.OnClickListener {

	public final static int REQUEST_CODE = 10;

	private LinearLayout settingsIndexController;
	private LinearLayout transactionManageIndexController;
	private LinearLayout currentLayout;

	private View navSelectedButton = null;
	private Button homeButton = null;
	private Button aboutButton; // about button
	private boolean removeJSTag = true;

	private MyApplication application;

	private LayoutInflater inflater;
	private View exitDialogView;
	private long exitTime = 0;
	protected ViewPager settingViewPager;
	protected ViewPagerIndicator settingPageIndicator;
	private static final int EXIT_LAST_TIME = 2000;

	private JSONObject data;
	private boolean isExternalOrder = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activityList.add(this);
		if (null != formData) {
			data = formData
					.optJSONObject(getString(R.string.formData_key_data));
			if (data != null) {
				isExternalOrder = data.optBoolean("isExternalOrder");
			}
		}

		/*
		 * three layout which will changed each other
		 */
		settingsIndexController = (LinearLayout) findViewById(R.id.home_setting_viewpages_layout);
		transactionManageIndexController = (LinearLayout) findViewById(R.id.transactionmanageindexcontroller);
		currentLayout = home_layout;

		/*
		 * navigation button,setting selected state.
		 */
		homeButton = (Button) findViewById(R.id.home_button);
		navSelectedButton = homeButton;

		homeButton.setSelected(true);

		aboutButton = (Button) findViewById(R.id.abountBtn);
		aboutButton.setOnClickListener(this);

		settingViewPager = (ViewPager) findViewById(R.id.setting_viewpager);
		settingPageIndicator = (ViewPagerIndicator) findViewById(R.id.setting_indicator);

		// start checking devices
		application = (MyApplication) getApplication();
		boolean isFirstStart = application.isFirstStart();
		/*
		 * if (!isFirstStart) { startDeviceChecking();
		 * application.setFirstStart(true); } else {
		 * onCall("Home.updateTransInfo", null); }
		 */
		if (!isFirstStart) {
			/*
			 * if (Env.checkApkExist(HomeController.this,
			 * ConstantUtils.APP_STORE_PACKAGE_NAME)) {
			 * startAppVersionChecking(); } else { startDeviceChecking(); }
			 */
			// application.setFirstStart(true);

			// add tag for starting service to check app version --start mod by
			// Teddy on 9th October
			if (!isExternalOrder) {

				Map<String, ?> map = UtilForDataStorage
						.readPropertyBySharedPreferences(
								MyApplication.getContext(), "merchSettings");
				Intent bindIntent = new Intent(this, LocalService.class);
				if (map != null && map.containsKey("settingString")) {
					bindIntent.putExtra(ConstantUtils.LOCAl_SERVICE_TAG, true);
				} else {
					bindIntent.putExtra(ConstantUtils.LOCAl_SERVICE_TAG, false);
				}
				startService(bindIntent);
			}
			// add tag for starting service to check app version --end mod by
			// Teddy on 9th October

		} else {
			onCall("Home.updateTransInfo", null);
		}

		// exit dialog
		inflater = LayoutInflater.from(this);
		exitDialogView = inflater.inflate(R.layout.dialog_exit_layout, null);

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (application.isFirstStart()) {
			onCall("Home.onShow", null);
		} else {
			application.setFirstStart(true);
		}
		// FIXME: init merchant nickname and login name
		setRightButtonVisible();
		setTitleVisible();
		// get merchant name

		/*
		 * Map<String, ?> merchMap = UtilForDataStorage
		 * .readPropertyBySharedPreferences(HomeController.this, "merchant");
		 * String merchName = (String) merchMap.get("merchName"); String
		 * operatorName = (String) merchMap.get("operator");
		 */
		try {
			String merchName = "";
			String operatorName = "";
			String userInfo = ClientEngine.engineInstance().getSecureService()
					.getUserInfo();
			MerchInfo merchInfo = ClientEngine.engineInstance()
					.getMerchService().getMerchInfo();
			if (merchInfo != null) {

				merchName = merchInfo.getMerchName();
			}

			if (!TextUtils.isEmpty(userInfo)) {
				try {
					JSONObject userInfoObj = new JSONObject(userInfo);
					operatorName = userInfoObj.optString("userName");

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				merchName = getResources().getString(R.string.app_name);
				operatorName = "";
			}

			setTitle(merchName);
			setRightButtonTitle(operatorName);

		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void willShow() {
		onCall("Home.onShow", null);
		super.willShow();

		// start service after download system parameters --start mod by Teddy
		// 10th October
		Map<String, ?> map = UtilForDataStorage
				.readPropertyBySharedPreferences(MyApplication.getContext(),
						"merchant");
		if (map != null) {
			boolean isParamDownloadedChecked = map
					.containsKey("isSysParamEmpty");

			if (!isParamDownloadedChecked) {

				Map<String, ?> settingMap = UtilForDataStorage
						.readPropertyBySharedPreferences(
								MyApplication.getContext(), "merchSettings");
				if (settingMap != null
						&& settingMap.containsKey("settingString")) {
					// save data to preference, that tagged first download
					// system params whether or not
					Map<String, Object> newMerchantMap = new HashMap<String, Object>();
					newMerchantMap.put("isSysParamEmpty", false);
					UtilForDataStorage.savePropertyBySharedPreferences(
							MyApplication.getContext(), "merchant",
							newMerchantMap);
				}

				// start service
				if (map.containsKey("operator")) {
					if (!isExternalOrder) {

						Intent bindIntent = new Intent(this, LocalService.class);
						bindIntent.putExtra(ConstantUtils.LOCAl_SERVICE_TAG,
								true);
						startService(bindIntent);
					}
				}
			}
		}
		// start service after download system parameters --end mod by Teddy
		// 10th October
	}

	@Override
	protected void loadRelatedJS() {
		if (getRemoveJSTag()) {
			JavaScriptEngine js = ClientEngine.engineInstance()
					.javaScriptEngine();
			js.loadJs(getString(R.string.controllerJSName_TransactionManageIndex));
			js.loadJs(getString(R.string.controllerJSName_SettingsIndex));
		}
		super.loadRelatedJS();
		setRemoveJSTag(false);
	}

	private void startDeviceChecking() {
		Intent mIntent = new Intent(HomeController.this,
				DevicesCheckingDialog.class);
		startActivity(mIntent);
	}

	private void startAppVersionChecking() {
		Intent mIntent = new Intent(HomeController.this,
				CheckingUpdateDialog.class);
		startActivity(mIntent);
	}

	@Override
	protected String getTitlebarTitle() {
		return getString(R.string.title_activity_home_controller);
	}

	@Override
	protected String getControllerName() {
		return getString(R.string.controllerName_Home);
	}

	@Override
	protected String getControllerJSName() {
		return getString(R.string.controllerJSName_Home);
	}

	@Override
	protected void setControllerContentView() {
		setContentView(R.layout.activity_home_controller);
	}

	public void onClickHome(View view) {
		changeSelectedButton(view);
		if (currentLayout == home_layout) {
			return;
		} else {
			currentLayout.setVisibility(View.GONE);
			home_layout.setVisibility(View.VISIBLE);
			currentLayout = home_layout;
		}
	}

	public void onClickSetting(View view) {
		int index = 0;
		int pages = 0;
		/*
		 * setting button selected state.
		 */
		changeSelectedButton(view);
		/*
		 * change layout.
		 */
		if (currentLayout == settingsIndexController) {
			return;
		} else {
			currentLayout.setVisibility(View.GONE);
			ArrayList<View> viewList = new ArrayList<View>();
			LayoutInflater lIf = getLayoutInflater().from(this);
			TableLayout tableView_1st = (TableLayout) lIf.inflate(
					R.layout.home_setting_layout_first, null);
			TableLayout tableView_2nd = (TableLayout) lIf.inflate(
					R.layout.home_setting_layout_second, null);
			viewList.add(tableView_1st);
			viewList.add(tableView_2nd);
			pages = 2;
			settingPageIndicator.setViewPager(settingViewPager);
			settingPageIndicator.setPageSize(pages);
			settingPageIndicator.setCurrentPage(0);
			if (pages == 1) {
				settingPageIndicator.setVisibility(View.INVISIBLE);
			} else {
				settingPageIndicator.setVisibility(View.VISIBLE);
			}
			settingViewPager.setAdapter(new HomePagerAdapter(viewList));
			settingsIndexController.setVisibility(View.VISIBLE);
			currentLayout = settingsIndexController;
		}
	}

	public void onClickTransactionInquiries(View view) {
		/*
		 * setting button selected state.
		 */
		changeSelectedButton(view);
		/*
		 * change layout.
		 */
		if (currentLayout == transactionManageIndexController) {
			return;
		} else {
			currentLayout.setVisibility(View.GONE);
			transactionManageIndexController.setVisibility(View.VISIBLE);
			currentLayout = transactionManageIndexController;
		}

	}

	public void onClickMultiPay(View view) {
		// changeSelectedButton(view);
		onCall("Home.onClickMultiPay", null);
	}

	private void changeSelectedButton(View view) {
		if (navSelectedButton == view) {
			return;
		}
		if (navSelectedButton != null) {
			navSelectedButton.setSelected(false);
			navSelectedButton = view;
			navSelectedButton.setSelected(true);
		}
	}

	/*
	 * 交易查询界面
	 */
	public void gotoConsumptionRecord(View view) {
		onCall("TransactionManageIndex.onConsumptionRecord", null);
	}

	public void gotoConsumptionRecordSearch(View view) {
		onCall("TransactionManageIndex.onConsumptionRecordSearch", null);
	}

	public void gotoSingleRecordSearch(View view) {
		onCall("TransactionManageIndex.onSingleRecordSearch", null);
	}

	public void gotoDelVoucherRecordSearch(View view) {
		onCall("TransactionManageIndex.onDelVoucherRecordSearch", null);
	}

	public void gotoConsumptionSummary(View view) {
		onCall("TransactionManageIndex.gotoConsumptionSummary", null);
	}

	/*
	 * 设置界面
	 */
	public void gotoLogin(View view) {
		onCall("SettingsIndex.gotoLogin", null);
	}

	public void gotoLogout(View view) {
		/*
		 * boolean existMispos = CacheDB.getInstance(HomeController.this)
		 * .isMisposConfiged(); JSONObject jsObj = new JSONObject(); try {
		 * jsObj.put("existMispos", existMispos); } catch (JSONException e) {
		 * e.printStackTrace(); } onCall("SettingsIndex.gotoLogout", jsObj);
		 */
		onCall("SettingsIndex.gotoLogout", null);
	}

	public void gotoCreateUser(View view) {
		onCall("SettingsIndex.gotoCreateUser", null);
	}

	public void gotoModifyPwd(View view) {
		onCall("SettingsIndex.gotoModifyPwd", null);
	}

	public void gotoMerchantInfo(View view) {
		onCall("SettingsIndex.gotoMerchantInfo", null);
	}

	public void clearReverseData(View view) {
		// onCall("SettingsIndex.clearReverseData", null);
		onCall("window.util.clearReverseDataWithLoginChecked", null);

		// clear consumption record table data
		ConsumptionRecordDB cacheDB = ConsumptionRecordDB
				.getInstance(HomeController.this);
		cacheDB.clearRecordTableData();
	}

	public void downloadMerchData(View view) {
		onCall("SettingsIndex.downloadMerchData", null);
	}

	public void gotoSetTransId(View view) {
		onCall("SettingsIndex.gotoSetTransId", null);
	}

	public void gotoTransBatch(View view) {
		/*
		 * boolean existMispos = CacheDB.getInstance(HomeController.this)
		 * .isMisposConfiged(); JSONObject jsObj = new JSONObject(); try {
		 * jsObj.put("existMispos", existMispos); } catch (JSONException e) {
		 * e.printStackTrace(); } onCall("SettingsIndex.gotoTransBatch", jsObj);
		 */
		onCall("SettingsIndex.gotoTransBatch", null);
	}

	public void gotoSetMerchId(View view) {
		onCall("SettingsIndex.gotoSetMerchId", null);
	}

	public void gotoListUsers(View view) {
		onCall("SettingsIndex.gotoListUsers", null);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.abountBtn:
			Intent mIntent = new Intent(HomeController.this, AboutDialog.class);
			startActivity(mIntent);

			break;

		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (isExternalOrder) {
			return super.onKeyDown(keyCode, event);
		} else {

			if (keyCode == KeyEvent.KEYCODE_BACK
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				// TODO:show exit dialog
				if ((System.currentTimeMillis() - exitTime) > EXIT_LAST_TIME) {
					Toast.makeText(HomeController.this,
							R.string.msg_exist_toast, Toast.LENGTH_SHORT)
							.show();
					exitTime = System.currentTimeMillis();
				} else {
					moveTaskToBack(true);
					// exit();
				}
				return true;
			}
			return true;
		}
	}

	@Override
	protected void setRemoveJSTag(boolean tag) {
		removeJSTag = tag;

	}

	@Override
	protected boolean getRemoveJSTag() {
		// TODO Auto-generated method stub
		return removeJSTag;
	}
}
