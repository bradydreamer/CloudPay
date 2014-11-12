package cn.koolcloud.pos.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.koolcloud.pos.entity.AcquireInstituteBean;
import cn.koolcloud.pos.wd.R;

public class AcquireListAdapter extends BaseAdapter {

	private List<AcquireInstituteBean> dataSource;
	private Context context;

	public AcquireListAdapter(List<AcquireInstituteBean> list, Context context) {
		this.dataSource = list;
		this.context = context;
	}

	@Override
	public int getCount() {
		return dataSource.size();
	}

	@Override
	public Object getItem(int position) {
		return dataSource.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.activity_merchant_info_controller_list_item, null); // init convertView
			holder = new ViewHolder();
			holder.acquireInstituteTextView = (TextView) convertView.findViewById(R.id.acquireInstituteTextView);
			holder.merchNumTextView = (TextView) convertView.findViewById(R.id.merchNumTextView);
			holder.deviceTextView = (TextView) convertView.findViewById(R.id.deviceTextView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		AcquireInstituteBean acquireInstitute = dataSource.get(position);
		holder.acquireInstituteTextView.setText(acquireInstitute.getInstituteName());
		holder.merchNumTextView.setText(acquireInstitute.getBrhMchtId());
		holder.deviceTextView.setText(acquireInstitute.getBrhTermId());
		return convertView;
	}

	static class ViewHolder {
		TextView acquireInstituteTextView;
		TextView merchNumTextView;
		TextView deviceTextView;
	}
}
