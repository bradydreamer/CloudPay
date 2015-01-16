package cn.koolcloud.pos.adapter;

import org.json.JSONObject;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cn.koolcloud.pos.R;

public class UsersListAdapter extends LoadMoreAdapter {

	public UsersListAdapter(Context context) {
		super(context);
	}

	@Override
	protected View getNormalView(int position, View convertView,
			ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = this.inflater.inflate(R.layout.lv_users_info_row,
					parent, false);
			holder = new ViewHolder();
			holder.userName = (TextView) convertView
					.findViewById(R.id.lv_user_info_userName);
			holder.gradeId = (TextView) convertView
					.findViewById(R.id.lv_user_info_gradeId);
			holder.aliasName = (TextView) convertView
					.findViewById(R.id.lv_user_info_aliasName);
			holder.lastLoginTime = (TextView) convertView
					.findViewById(R.id.lv_user_info_lastLoginTime);
			holder.loginCount = (TextView) convertView
					.findViewById(R.id.lv_user_info_loginCount);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		JSONObject userInfoItem = this.list.get(position);
		String grade = "无";
		if (userInfoItem.optString("gradeId").equals("1")) {
			grade = context.getString(R.string.user_manager);
		} else if (userInfoItem.optString("gradeId").equals("2")) {
			grade = context.getString(R.string.common_operator);
		} else if (userInfoItem.optString("gradeId").equals("3")) {
			grade = context.getString(R.string.common_non_operator);
		} else if (userInfoItem.optString("gradeId").equals("4")) {
			grade = context.getString(R.string.common_void_operator);
		}
		holder.gradeId.setText(grade);
		holder.userName.setText(userInfoItem.optString("operator"));
		holder.aliasName.setText(userInfoItem.optString("aliasName"));
		String time = userInfoItem.optString("lastLoginTime");
		String lastLoginTime = "";
		if (time.equals("") || time == null) {
			lastLoginTime = time;
		} else {
			lastLoginTime = time.substring(0, 4) + "-" + time.substring(4, 6)
					+ "-" + time.substring(6, 8) + " " + time.substring(8, 10)
					+ ":" + time.substring(10, 12) + ":"
					+ time.substring(12, 14);
		}
		holder.lastLoginTime.setText(lastLoginTime);
		holder.loginCount.setText(userInfoItem.optString("loginCount"));

		return convertView;
	}

	static class ViewHolder {
		TextView userName;
		TextView gradeId;
		TextView aliasName;
		TextView lastLoginTime;
		TextView loginCount;
	}

}
