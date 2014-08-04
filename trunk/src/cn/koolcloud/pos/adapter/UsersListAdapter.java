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
		holder.userName.setText(userInfoItem.optString("operator"));
		holder.gradeId.setText(userInfoItem.optString("gradeId"));
		holder.aliasName.setText(userInfoItem.optString("aliasName"));
		holder.lastLoginTime.setText(userInfoItem.optString("lastLoginTime"));
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
