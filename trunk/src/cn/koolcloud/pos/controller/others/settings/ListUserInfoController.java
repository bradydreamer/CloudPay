package cn.koolcloud.pos.controller.others.settings;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.adapter.UsersListAdapter;
import cn.koolcloud.pos.controller.BaseController;
import cn.koolcloud.pos.util.UtilForJSON;

public class ListUserInfoController extends BaseController {

	private List<JSONObject> usersDataList;
	private ListView lv_userInfo;
	private UsersListAdapter userListAdapter;
	private boolean removeJSTag = true;
	private boolean hasMore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (null == formData) {
			finish();
			return;
		}
		JSONObject data = formData
				.optJSONObject(getString(R.string.formData_key_data));
		JSONArray userList = data.optJSONArray("userList");
		usersDataList = UtilForJSON
				.JSONArrayOfJSONObjects2ListOfJSONObjects(userList);
		userListAdapter = new UsersListAdapter(this);
		userListAdapter.setList(usersDataList);
		hasMore = data.optBoolean("hasMore");
		userListAdapter.setHasMore(hasMore);
		lv_userInfo = (ListView) findViewById(R.id.list_users_lv_info);
		lv_userInfo.setAdapter(userListAdapter);

		lv_userInfo
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						setRemoveJSTag(true);
						loadRelatedJS();
						if (position == usersDataList.size()) {
							onCall("ListUserInfo.reqMoreInfo", null);
						} else {
							// JSONObject recordData =
							// usersDataList.get(position);
							// onCall("ConsumptionRecord.getRecordDetail",
							// recordData);
						}
					}
				});
	}

	@Override
	protected void onStart() {
		userListAdapter.notifyDataSetChanged();
		super.onStart();
	}

	@Override
	protected View viewForIdentifier(String name) {
		if ("lv_userInfo".equals(name)) {
			return lv_userInfo;
		}
		return super.viewForIdentifier(name);
	}

	@Override
	protected void setView(View view, String key, Object value) {
		if (null == view || null == key) {
			return;
		}
		if ("addList".equals(key)) {
			JSONObject data = (JSONObject) value;
			JSONArray dataArray = data.optJSONArray("userList");

			for (int i = 0; i < dataArray.length(); i++) {
				usersDataList.add(dataArray.optJSONObject(i));
			}
			hasMore = data.optBoolean("hasMore");
			userListAdapter.setHasMore(hasMore);
			userListAdapter.notifyDataSetChanged();
		}
		super.setView(view, key, value);
	}

	@Override
	protected void setControllerContentView() {
		setContentView(R.layout.activity_list_user_info_controller);
	}

	@Override
	protected String getTitlebarTitle() {
		return getString(R.string.title_activity_list_users_controller);
	}

	@Override
	protected String getControllerName() {
		return getString(R.string.controllerName_ListUserInfo);
	}

	@Override
	protected String getControllerJSName() {
		// TODO Auto-generated method stub
		return getString(R.string.controllerJSName_ListUserInfo);
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
