package cn.koolcloud.pos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.koolcloud.pos.R;
import cn.koolcloud.pos.entity.AcquireInstituteBean;
import cn.koolcloud.pos.util.UtilForMoney;

public class SummaryListAdapter extends BaseAdapter {

	private final String APMP_TRAN_CONSUME = "1021";
	private final String APMP_TRAN_CONSUMECANCE = "3021";
	private final String APMP_TRAN_PREAUTH = "1011";
	private final String APMP_TRAN_PRAUTHCOMPLETE = "1031";
	private final String APMP_TRAN_PRAUTHSETTLEMENT = "1091";
	private final String APMP_TRAN_PRAUTHCANCEL = "3011";
	private final String APMP_TRAN_PREAUTHCOMPLETECANCEL = "3031";
	private List<JSONObject> dataSource;
	private Context context;

	public SummaryListAdapter(Context context) {
		this.context = context;
	}

	public void setList(List<JSONObject> dataSource){
		this.dataSource = dataSource;
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
			convertView = LayoutInflater.from(context).inflate(R.layout.activity_summary_info_controller_list_item, null); // init convertView
			holder = new ViewHolder();
			holder.transTypeTextView = (TextView) convertView.findViewById(R.id.transTypeTextView);
			holder.transCountTextView = (TextView) convertView.findViewById(R.id.transCountTextView);
			holder.transAmountTextView = (TextView) convertView.findViewById(R.id.transAmountTextView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		JSONObject contentSrc = dataSource.get(position);
		String transType = contentSrc.optString("transType");
		String count = contentSrc.optString("totalSize");
		String amount = UtilForMoney.fen2yuan(contentSrc
				.optString("totalAmount"));
		if (transType.equals(APMP_TRAN_CONSUME)) {
			holder.transTypeTextView.setText(R.string.consumption_summary_consume);
		} else if (transType.equals(APMP_TRAN_CONSUMECANCE)) {
			holder.transTypeTextView.setText(R.string.consumption_summary_consumeCancel);
		} else if (transType.equals(APMP_TRAN_PRAUTHCANCEL)) {
			holder.transTypeTextView.setText(R.string.consumption_summary_preAuthCancel);
		} else if (transType.equals(APMP_TRAN_PRAUTHCOMPLETE)) {
			holder.transTypeTextView.setText(R.string.consumption_summary_preAuthComplete);
		} else if (transType.equals(APMP_TRAN_PRAUTHSETTLEMENT)) {
			holder.transTypeTextView.setText(R.string.consumption_summary_preAuthCompleteOffline);
		} else if (transType.equals(APMP_TRAN_PREAUTH)) {
			holder.transTypeTextView.setText(R.string.consumption_summary_preAuth);
		} else if (transType.equals(APMP_TRAN_PREAUTHCOMPLETECANCEL)) {
			holder.transTypeTextView.setText(R.string.consumption_summary_preAuthCompleteCancel);
		}
		holder.transCountTextView.setText(count);
		holder.transAmountTextView.setText(amount);
		return convertView;
	}

	static class ViewHolder {
		TextView transTypeTextView;
		TextView transCountTextView;
		TextView transAmountTextView;
	}
}
