package cn.koolcloud.pos.adapter;

import org.json.JSONObject;

import cn.koolcloud.pos.wd.R;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ConsumptionRecordAdapter  extends LoadMoreAdapter {

	public ConsumptionRecordAdapter(Context context) {
		super(context);
	}

	@Override
	protected View getNormalView(int position, View convertView,
			ViewGroup parent) {
		RecordViewHolder recordViewHolder = null;
		if (null == convertView) {
			convertView = inflater.inflate(R.layout.lv_consumption_record_row, parent, false);
			recordViewHolder = new RecordViewHolder();
			recordViewHolder.tv_transType = (TextView) convertView.findViewById(R.id.lv_consumption_record_tv_transType);
			recordViewHolder.tv_payType = (TextView) convertView.findViewById(R.id.lv_consumption_record_tv_payType);
			recordViewHolder.tv_rrn = (TextView) convertView.findViewById(R.id.lv_consumption_record_tv_rrn);
			recordViewHolder.tv_transDate = (TextView) convertView.findViewById(R.id.lv_consumption_record_tv_transDate);
			recordViewHolder.tv_transTime = (TextView) convertView.findViewById(R.id.lv_consumption_record_tv_transTime);
			recordViewHolder.tv_transAmount = (TextView) convertView.findViewById(R.id.lv_consumption_record_tv_transAmount);
			recordViewHolder.tv_orderStatus = (TextView) convertView.findViewById(R.id.lv_consumption_record_tv_orderStatus);
			convertView.setTag(recordViewHolder);
		} else {
			recordViewHolder = (RecordViewHolder) convertView.getTag();
		}
		
		JSONObject recordData = this.list.get(position);
		recordViewHolder.tv_transType.setText(recordData.optString("transTypeDesc"));
		recordViewHolder.tv_payType.setText(recordData.optString("payTypeDesc"));
		recordViewHolder.tv_rrn.setText(recordData.optString("refNo"));
		recordViewHolder.tv_transDate.setText(recordData.optString("tDate"));
		recordViewHolder.tv_transTime.setText(recordData.optString("tTime"));
		recordViewHolder.tv_transAmount.setText(recordData.optString("transAmount"));
		recordViewHolder.tv_orderStatus.setText(recordData.optString("orderStateDesc"));
		
		return convertView;
	}

	private class RecordViewHolder {
		public TextView tv_rrn;
		public TextView tv_transType;
		public TextView tv_payType;
		public TextView tv_transDate;
		public TextView tv_transTime;
		public TextView tv_transAmount;
		public TextView tv_orderStatus;
	}
	
}
