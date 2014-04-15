package cn.koolcloud.pos.adapter;

import org.json.JSONObject;

import cn.koolcloud.pos.R;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MultiPayRecordAdapter extends LoadMoreAdapter {

	public MultiPayRecordAdapter(Context context) {
		super(context);
	}

	@Override
	protected View getNormalView(int position, View convertView,
			ViewGroup parent) {
		RecordViewHolder recordViewHolder = null;
//		var order = {
//				"ref" : ConsumptionData.dataForPayment.rrn,
//				"result" : ConsumptionData.dataForPayment.result,
//				"orderStateDesc" : ConsumptionData.dataForPayment.result == 0 ? "完成" : "失败",
//				"payTypeDesc" : "" + ConsumptionData.dataForPayment.paymentName,
//				"transAmount" : ConsumptionData.dataForPayment.transAmount,
//				"showAmount" : util.formatAmountStr(ConsumptionData.dataForPayment.transAmount),
//			};

		if (null == convertView) {
			convertView = inflater.inflate(R.layout.lv_multipay_record_row, parent, false);
			recordViewHolder = new RecordViewHolder();
			recordViewHolder.tv_no = (TextView) convertView.findViewById(R.id.lv_multipay_record_tv_no);
			recordViewHolder.tv_transType = (TextView) convertView.findViewById(R.id.lv_multipay_record_tv_transType);
			recordViewHolder.tv_rrn = (TextView) convertView.findViewById(R.id.lv_multipay_record_tv_rrn);
			recordViewHolder.tv_transAmount = (TextView) convertView.findViewById(R.id.lv_multipay_record_tv_transAmount);
			recordViewHolder.tv_orderStatus = (TextView) convertView.findViewById(R.id.lv_multipay_record_tv_orderStatus);
			convertView.setTag(recordViewHolder);
		} else {
			recordViewHolder = (RecordViewHolder) convertView.getTag();
		}
		
		JSONObject recordData = this.list.get(position);
		recordViewHolder.tv_no.setText("" + (position + 1));
		recordViewHolder.tv_transType.setText(recordData.optString("payTypeDesc"));
		recordViewHolder.tv_rrn.setText(recordData.optString("ref"));
		recordViewHolder.tv_transAmount.setText(recordData.optString("showAmount"));
		recordViewHolder.tv_orderStatus.setText(recordData.optString("orderStateDesc"));
		
		convertView.setBackgroundResource(R.drawable.lv_row_bg_white);
		
		return convertView;
	}

	private class RecordViewHolder {
		public TextView tv_no;
		public TextView tv_rrn;
		public TextView tv_transType;
		public TextView tv_transAmount;
		public TextView tv_orderStatus;
	}
	
}
