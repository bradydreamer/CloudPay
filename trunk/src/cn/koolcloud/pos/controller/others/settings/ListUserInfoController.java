package cn.koolcloud.pos.controller.others.settings;

import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import cn.koolcloud.pos.adapter.UsersListAdapter;
import cn.koolcloud.pos.controller.BaseController;
import cn.koolcloud.pos.util.UserInfoComparator;
import cn.koolcloud.pos.util.UtilForJSON;
import cn.koolcloud.pos.wd.R;
import cn.koolcloud.pos.widget.ScrowDeleteListView;
import cn.koolcloud.pos.widget.ScrowDeleteListView.RemoveDirection;
import cn.koolcloud.pos.widget.ScrowDeleteListView.RemoveListener;

public class ListUserInfoController extends BaseController implements
		RemoveListener {

	private List<JSONObject> usersDataList;
	private ScrowDeleteListView lv_userInfo;
	private UsersListAdapter userListAdapter;
	private JSONArray userList;
	private UserInfoComparator userInfoComp;
	private Boolean userName_sortType = false, gradeId_sortType = false,
			aliasName_sortType = false, lastLoginTime_sortType = false,
			LoginCount_sortType = false;
	private ImageView userNameImage, userGradeIdImage, userAliasNameImage,
			userLastLoginTimeImage, userLoginCountImage, currentImageView;
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
		userList = data.optJSONArray("userList");
		usersDataList = UtilForJSON
				.JSONArrayOfJSONObjects2ListOfJSONObjects(userList);
		userListAdapter = new UsersListAdapter(this);
		userListAdapter.setList(usersDataList);
		hasMore = data.optBoolean("hasMore");
		userListAdapter.setHasMore(hasMore);
		lv_userInfo = (ScrowDeleteListView) findViewById(R.id.list_users_lv_info);
		lv_userInfo.setAdapter(userListAdapter);
		lv_userInfo.setRemoveListener(this);
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
		userNameImage = (ImageView) findViewById(R.id.userNameSortImage);
		userGradeIdImage = (ImageView) findViewById(R.id.userGradeIDImage);
		userAliasNameImage = (ImageView) findViewById(R.id.userAliasNameSortImage);
		userLastLoginTimeImage = (ImageView) findViewById(R.id.userLastLoginTimeSortImage);
		userLoginCountImage = (ImageView) findViewById(R.id.userLoginCountSortImage);
		currentImageView = userNameImage;
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
		} else if ("recoverList".equals(key)) {
			JSONObject data = (JSONObject) value;
			JSONObject userInfoItem = data.optJSONObject("userItem");
			try {
				int index = data.getInt("position");
				usersDataList.add(index, userInfoItem);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			userListAdapter.notifyDataSetChanged();
		}

		super.setView(view, key, value);
	}

	@Override
	public void removeItem(RemoveDirection direction, int position) {
		// userListAdapter.(userListAdapter.getItem(position));
		JSONObject msg = new JSONObject();
		try {
			JSONArray userInfoList = new JSONArray(usersDataList.toString());
			msg.put("userList", userInfoList);
			msg.put("position", position);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		onCall("ListUserInfo.deleteAlert", msg);
		// user
		usersDataList.remove(position);
		userListAdapter.notifyDataSetChanged();
	}

	public void userInfoSort(Boolean compareType, int compareContent) {
		userInfoComp = new UserInfoComparator();
		userInfoComp.setCompareType(compareType);
		userInfoComp.setCompareContent(compareContent);
		Collections.sort(usersDataList, userInfoComp);
		userListAdapter.notifyDataSetChanged();
	}

	public void sortUserName(View v) {
		displayArrowLogo(userNameImage, userName_sortType);
		userInfoSort(userName_sortType, UserInfoComparator.COMP_USER_NAME);
		userName_sortType = !userName_sortType;
	}

	public void sortUserGradeID(View v) {
		displayArrowLogo(userGradeIdImage, gradeId_sortType);
		userInfoSort(gradeId_sortType, UserInfoComparator.COMP_USER_GRADEID);
		gradeId_sortType = !gradeId_sortType;
	}

	public void sortUserAliasName(View v) {
		displayArrowLogo(userAliasNameImage, aliasName_sortType);
		userInfoSort(aliasName_sortType, UserInfoComparator.COMP_USER_ALIASNAME);
		aliasName_sortType = !aliasName_sortType;
	}

	public void sortUserLastLoginTime(View v) {
		displayArrowLogo(userLastLoginTimeImage, lastLoginTime_sortType);
		userInfoSort(lastLoginTime_sortType,
				UserInfoComparator.COMP_USER_LASTLOGINTIME);
		lastLoginTime_sortType = !lastLoginTime_sortType;
	}

	public void sortUserLoginCount(View v) {
		displayArrowLogo(userLoginCountImage, LoginCount_sortType);
		userInfoSort(LoginCount_sortType,
				UserInfoComparator.COMP_USER_LOGINCOUNT);
		LoginCount_sortType = !LoginCount_sortType;
	}

	private void displayArrowLogo(ImageView imgView, Boolean arrowTag) {
		if (currentImageView != imgView) {
			currentImageView.setBackgroundResource(0);
			currentImageView = imgView;
		}
		if (!arrowTag) {
			currentImageView
					.setBackgroundResource(R.drawable.form_navigation_bar_arrow_down);
		} else {
			currentImageView
					.setBackgroundResource(R.drawable.form_navigation_bar_arrow_up);
		}
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
